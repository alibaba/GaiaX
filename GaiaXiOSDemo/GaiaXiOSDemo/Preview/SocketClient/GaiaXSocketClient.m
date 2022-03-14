//
//  GaiaXSocketClient.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GaiaXSocketClient.h"
#import <SocketRocket/SRWebSocket.h>
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

@interface GaiaXSocketClient ()<SRWebSocketDelegate>

@property (nonatomic, strong) NSURL *url;
@property (nonatomic, strong) SRWebSocket *webScoket;

@property (nonatomic, strong) NSTimer *headerBeatTimer; //心跳定时器
@property (nonatomic, strong) NSTimer *networkTestingTimer; //没有网络的时候检测定时器
@property (nonatomic, assign) NSTimeInterval reConnectTime; //重连时间
@property (nonatomic, strong) NSMutableArray *sendDataArray; //存储要发送给服务器的数据

@property (nonatomic, assign) BOOL isActiveClose; //用于判断是否主动关闭长连接，如果是主动断开连接，连接失败的代理中，就不用执行 重新连接方法
@property (nonatomic, strong) dispatch_queue_t processingQueue;

@end


@implementation GaiaXSocketClient

- (instancetype)initWithURL:(NSURL *)url delegate:(id<GaiaXSocketClientDelegate>)delegate {
    self = [self init];
    if (self) {
        _url = url;
        _delegate = delegate;
        _processingQueue = dispatch_queue_create("eu.nubomedia.websocket.processing", DISPATCH_QUEUE_SERIAL);
    }
    return self;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        self.reConnectTime = 0;
        self.isActiveClose = NO;
        self.sendDataArray = [NSMutableArray array];
    }
    return self;
}

//建立长连接
- (void)connectServer{
    if(self.webScoket){
        return;
    }
    
    self.webScoket = [[SRWebSocket alloc] initWithURL:_url];
    [self.webScoket setDelegateDispatchQueue:self.processingQueue];
    self.webScoket.delegate = self;
    [self.webScoket open];
}

//发送心跳
- (void)sendPing:(id)sender{
    NSData *heartData = [[NSData alloc] initWithBase64EncodedString:@"heart" options:NSUTF8StringEncoding];
    [self.webScoket sendPing:heartData];
}

//关闭长连接
- (void)webSocketClose{
    self.isConnect = NO;
    self.isActiveClose = YES;
    self.socketStatus = GaiaXWebSocketStatusDefault;
    
    //关闭socket
    if (self.webScoket) {
        [self.webScoket close];
        self.webScoket = nil;
    }
    
    //关闭心跳定时器
    [self destoryHeartBeat];
    //关闭网络检测定时器
    [self destoryNetWorkStartTesting];
}

#pragma mark - socket delegate

//已经连接
- (void)webSocketDidOpen:(SRWebSocket *)webSocket{
    self.isConnect = YES;
    self.socketStatus = GaiaXWebSocketStatusConnect;
    
    //开始心跳
    [self initHeartBeat];
    
    //代理方法
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        if (self.delegate && [self.delegate respondsToSelector:@selector(gaiaXSocketClientDidConnect:)]) {
            [self.delegate gaiaXSocketClientDidConnect:self];
        }
    })
}

//连接失败
- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error{
    self.isConnect = NO;
    self.socketStatus = GaiaXWebSocketStatusDisConnect;
    //    NSLog(@"连接失败，这里可以实现掉线自动重连，要注意以下几点");
    //    NSLog(@"1.判断当前网络环境，如果断网了就不要连了，等待网络到来，在发起重连");
    //    NSLog(@"2.判断调用层是否需要连接，不需要的时候不k连接，浪费流量");
    //    NSLog(@"3.连接次数限制，如果连接失败了，重试10次左右就可以了");
    
    //判断网络环境
    if (![GaiaXHelper isNetworkReachable]) {
        //没有网络,开启网络监测定时器
        [self noNetWorkStartTesting];
    } else {
        //连接失败，重新连接
        [self reConnectServer];
    }
    
    //代理方法
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        if (self.delegate && [self.delegate respondsToSelector:@selector(gaiaXSocketClient:didFailWithError:)]) {
            [self.delegate gaiaXSocketClient:self didFailWithError:error];
        }
    })
}

//接收消息
- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)messageData{
    //NSLog(@"接收消息 ---- %@", messageData);
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        if (![GXUtils isValidString:messageData] && ![GXUtils isValidDictionary:messageData]) {
            return;
        }
        GXStrongSelf(self)

        //获取messageData的内容
        NSDictionary *messageDictionary = nil;
        if ([messageData isKindOfClass:[NSString class]]) {
            messageDictionary = [NSDictionary gx_dictionaryFromJSONString:messageData];
        } else {
            messageDictionary = (NSDictionary *)messageData;
        }

        //获取消息ID
        NSInteger messageId = [messageDictionary gx_integerForKey:@"id"];

        switch (messageId) {
            case 1:{//初始化消息
                if (self.delegate && [self.delegate respondsToSelector:@selector(gaiaXSocketClientDidInitialize:)]) {
                    [self.delegate gaiaXSocketClientDidInitialize:self];
                }
            }
                break;
            case 666:{//获取当前模板消息
                NSDictionary *result = [messageDictionary gx_dictionaryForKey:@"result"];
                NSDictionary *data = [result gx_dictionaryForKey:@"data"];
                if (data) {
                    //获取templateId
                    NSMutableDictionary *templateInfo = [NSMutableDictionary dictionaryWithDictionary:data];
                    [templateInfo gx_setObject:[result gx_stringForKey:@"templateId"] forKey:@"templateId"];
                    //代理方法
                    if ([self.delegate respondsToSelector:@selector(gaiaXSocketClient:didReceiveTemplateInfo:)]) {
                        [self.delegate gaiaXSocketClient:self didReceiveTemplateInfo:templateInfo];
                    }
                }
            }
                break;
            case 888:{//获取嵌套模板消息
                NSDictionary *result = [messageDictionary gx_dictionaryForKey:@"result"];
                NSDictionary *data = [result gx_dictionaryForKey:@"data"];
                if (data) {
                    //获取templateId
                    NSMutableDictionary *templateInfo = [NSMutableDictionary dictionaryWithDictionary:data];
                    [templateInfo gx_setObject:[result gx_stringForKey:@"templateId"] forKey:@"templateId"];
                    //代理方法
                    if (self.delegate && [self.delegate respondsToSelector:@selector(gaiaXSocketClient:didReceiveNestedTemplateInfo:)]) {
                        [self.delegate gaiaXSocketClient:self didReceiveNestedTemplateInfo:templateInfo];
                    }
                }
            }
                break;

            default:{//获取通知
                NSString *method = [messageDictionary gx_stringForKey:@"method"];
                if ([method isEqualToString:@"template/didChangedNotification"] ||
                    [method isEqualToString:@"template/didManualChangedNotification"]) {
                    NSDictionary *result = [messageDictionary gx_dictionaryForKey:@"params"];
                    NSDictionary *data = [result gx_dictionaryForKey:@"data"];
                    if (data) {
                        //获取templateId
                        NSMutableDictionary *templateInfo = [NSMutableDictionary dictionaryWithDictionary:data];
                        [templateInfo gx_setObject:[result gx_stringForKey:@"templateId"] forKey:@"templateId"];
                        //代理方法
                        if (self.delegate && [self.delegate respondsToSelector:@selector(gaiaXSocketClient:didReceiveTemplateInfo:)]) {
                            [self.delegate gaiaXSocketClient:self didReceiveTemplateInfo:templateInfo];
                        }
                    }
                }
            }
                break;
        }

    });

}

//关闭连接
- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean{
    self.isConnect = NO;
    
    if (self.isActiveClose) {
        self.socketStatus = GaiaXWebSocketStatusDefault;
        return;
    }
    
    //更新status
    self.socketStatus = GaiaXWebSocketStatusDisConnect;
    
    //断开时销毁心跳
    [self destoryHeartBeat];
    
    //判断网络
    if (![GaiaXHelper isNetworkReachable]) {
        //没有网络,开启网络监测定时器
        [self noNetWorkStartTesting];
    } else {
        //重连
        self.webScoket = nil;
        [self reConnectServer];
    }
    
    //代理
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        if (self.delegate && [self.delegate respondsToSelector:@selector(gaiaXSocketClientDidDisconnect:)]) {
            [self.delegate gaiaXSocketClientDidDisconnect:self];
        }
    });
}


/**
 接受服务端发生Pong消息，我们在建立长连接之后会建立与服务器端的心跳包
 心跳包是我们用来告诉服务端：客户端还在线，心跳包是ping消息，于此同时服务端也会返回给我们一个pong消息
 */
- (void)webSocket:(SRWebSocket *)webSocket didReceivePong:(NSData *)pongData{
    //do nothing
}

#pragma mark - NSTimer

//初始化心跳
- (void)initHeartBeat{
    if (self.headerBeatTimer) {
        return;
    }
    
    [self destoryHeartBeat];
    
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        self.headerBeatTimer = [NSTimer timerWithTimeInterval:10 target:self selector:@selector(senderheartBeat) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:self.headerBeatTimer forMode:NSRunLoopCommonModes];
    });
}

//重新连接
- (void)reConnectServer{
    //关闭之前的连接
    [self webSocketClose];
    
    //重连10次 2^10 = 1024
    if (self.reConnectTime > 1024) {
        self.reConnectTime = 0;
        return;
    }
    
    GXWeakSelf(self)
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(self.reConnectTime * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        GXStrongSelf(self)
        if (self.webScoket.readyState == SR_OPEN && self.webScoket.readyState == SR_CONNECTING) {
            return ;
        }
        
        //链接服务器
        [self connectServer];
        
        //设置事件
        if (self.reConnectTime == 0) {//重连时间2的指数级增长
            self.reConnectTime = 2;
        } else {
            self.reConnectTime *= 2;
        }
    });
}

//发送心跳
- (void)senderheartBeat{
    //和服务端约定好发送什么作为心跳标识，尽可能的减小心跳包大小
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        if (self.webScoket.readyState == SR_OPEN) {
            //NSLog(@"发送心跳");
            [self sendPing:nil];
            
        }else if (self.webScoket.readyState == SR_CONNECTING){
            //NSLog(@"正在连接中");
            [self reConnectServer];
            
        }else if (self.webScoket.readyState == SR_CLOSED || self.webScoket.readyState == SR_CLOSING){
            //NSLog(@"断开，重连");
            [self reConnectServer];
            
        }else{
            //NSLog(@"没网络，发送失败，一旦断网 socket 会被我设置 nil 的");
        }
    });
}

//取消心跳
- (void)destoryHeartBeat{
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        if (self.headerBeatTimer) {
            [self.headerBeatTimer invalidate];
            self.headerBeatTimer = nil;
        }
    });
}

//没有网络的时候开始定时 -- 用于网络检测
- (void)noNetWorkStartTestingTimer{
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        self.networkTestingTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(noNetWorkStartTesting) userInfo:nil repeats:YES];
        [[NSRunLoop currentRunLoop] addTimer:self.networkTestingTimer forMode:NSDefaultRunLoopMode];
    });
}

//定时检测网络
- (void)noNetWorkStartTesting{
    if (![GaiaXHelper isNetworkReachable]) {
        //关闭网络检测定时器
        [self destoryNetWorkStartTesting];
        //重新连接
        [self reConnectServer];
    }
}

//取消网络检测
- (void)destoryNetWorkStartTesting{
    GXWeakSelf(self)
    GX_DISPATCH_MAIN_THREAD(^{
        GXStrongSelf(self)
        if (self.networkTestingTimer) {
            [self.networkTestingTimer invalidate];
            self.networkTestingTimer = nil;
        }
    });
}

- (void)sendRequestToServer:(GaiaXSocketRequest *)request{
    //处理消息
    NSString *msg = [request toJSONString];
    [self.sendDataArray addObject:msg];
    
    //没有网络
    if(![GaiaXHelper isNetworkReachable]){
        //开启网络检测定时器
        [self noNetWorkStartTesting];
        
    } else {
        
        if (self.webScoket != nil) {
            //只有长连接OPEN开启状态才能调用send方法
            if (self.webScoket.readyState == SR_OPEN) {
                //发送消息
                [self.webScoket send:msg];
                
            } else if (self.webScoket.readyState == SR_CONNECTING){
                //正在连接，donothing
                
            } else if (self.webScoket.readyState == SR_CLOSING || self.webScoket.readyState == SR_CLOSED){
                //调用 reConnectServer 方法重连,连接成功后 继续发送数据
                [self reConnectServer];
            }
            
        } else {
            [self connectServer];//连接服务器
        }
    }
}


@end
