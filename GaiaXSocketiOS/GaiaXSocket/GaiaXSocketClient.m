//  Copyright (c) 2023, Alibaba Group Holding Limited.
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
#import "GaiaXSocketProtocol.h"
#import "GaiaXSocketModel.h"
#import "GaiaXSocketUtils.h"
#import "SRWebSocket.h"

@interface GaiaXSocketClient ()<SRWebSocketDelegate>

@property (nonatomic, strong) SRWebSocket *webScoket;

@property (nonatomic, strong) NSTimer *heartBeatTimer;
@property (nonatomic, strong) NSTimer *networkCheckTimer;

@property (nonatomic, strong) dispatch_queue_t processingQueue;

@property (nonatomic, strong) NSTimer *reConnectTimer;

@property (nonatomic, strong) NSMapTable<NSNumber *, GaiaXSocketCallback> *requestIdCallbackTable;

@end

@implementation GaiaXSocketClient

- (instancetype)initWithUrl:(NSString *)url delegate:(id <GaiaXSocketClientDelegate>)delegate{
    self = [self init];
    if (self) {
        _url = url;
        _delegate = delegate;
    }
    return self;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        self.closeManually = NO;
        self.requestIdCallbackTable = [NSMapTable strongToStrongObjectsMapTable];
        self.processingQueue = dispatch_queue_create("eu.nubomedia.websocket.processing", DISPATCH_QUEUE_SERIAL);
    }
    return self;
}

- (BOOL)isConnect {
    return self.webScoket.readyState == SR_OPEN;
}


- (void)connectServer{
    if ([self isConnect]) {
        return;
    }
    
    NSURL *url = [NSURL URLWithString:self.url];
    self.webScoket = [[SRWebSocket alloc] initWithURL:url];
    [self.webScoket setDelegateDispatchQueue:self.processingQueue];
    self.webScoket.delegate = self;
    [self.webScoket open];
}

- (void)reConnectServer{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        if ([self.reConnectTimer isValid]) {
            [self.reConnectTimer invalidate];
            self.reConnectTimer = nil;
        }
        self.reConnectTimer = [NSTimer scheduledTimerWithTimeInterval:2 target:self selector:@selector(doReConnectServer) userInfo:nil repeats:YES];
    });
}

- (void)doReConnectServer {

    if (self.webScoket.readyState == SR_OPEN ) {
        if (self.reConnectTimer != nil && [self.reConnectTimer isValid]) {
            [self.reConnectTimer invalidate];
            self.reConnectTimer = nil;
        }
        return;
    }
    [self disConnectServer:NO];

    [self connectServer];

}

- (void)disConnectServer: (BOOL)manually{
    self.closeManually = manually;
    
    if (self.webScoket != nil) {
        self.webScoket.delegate = nil;
        [self.webScoket close];
        self.webScoket = nil;
    }
    
    [self distroyNetworkCheckTimer];
}

- (void)disConnectServer {
    [self disConnectServer:YES];
}


#pragma mark -



- (void)setupNetworkCheckTimer{
    if (_networkCheckTimer != nil) {
        return;
    }
    
    _networkCheckTimer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(networkStartCheck) userInfo:nil repeats:YES];
    [[NSRunLoop currentRunLoop] addTimer:_networkCheckTimer forMode:NSRunLoopCommonModes];
    [[NSRunLoop currentRunLoop] run];
}

- (void)distroyNetworkCheckTimer{
    if ([_networkCheckTimer isValid]) {
        [_networkCheckTimer invalidate];
        _networkCheckTimer = nil;
    }
}

- (void)networkStartCheck{
    if([GaiaXSocketUtils isNetworkReachable]){
        return;
    }
    
    [self disConnectServer:NO];
    
    [self reConnectServer];
}


#pragma mark - send


- (void)sendResponse:(GaiaXSocketModel *)model {
    if (self.webScoket.readyState != SR_OPEN) {
        return;
    }
    [self.webScoket send:[model stringifyModel]];
}

- (void)sendeNotification:(GaiaXSocketModel *)model {
    if (self.webScoket.readyState != SR_OPEN) {
        return;
    }
    [self.webScoket send:[model stringifyModel]];
}

- (void)sendRequest:(GaiaXSocketModel *)request callback:(GaiaXSocketCallback)callback {
    if (self.webScoket.readyState != SR_OPEN) {
        return;
    }
    
    [_requestIdCallbackTable setObject:callback forKey:request.messageId];

    [self.webScoket send:[request stringifyModel]];

}


#pragma mark - SRWebSocketDelegate




- (void)webSocketDidOpen:(SRWebSocket *)webSocket{
    self.closeManually = NO;
    _socketStatus = GaiaXSocketStatusConnect;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_SOCKET_CONNECT_STATE_CONNECTED" object:nil userInfo:nil];

        if (self.delegate && [self.delegate respondsToSelector:@selector(socketClientDidConnect:)]) {
            [self.delegate socketClientDidConnect:self];
        }
    });
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error{
    _socketStatus = GaiaXSocketStatusDisConnect;
    
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.delegate && [self.delegate respondsToSelector:@selector(socketClient:didFailWithError:)]) {
            [self.delegate socketClient:self didFailWithError:error];
        }
    });

    if(![GaiaXSocketUtils isNetworkReachable]){
        [self networkStartCheck];
    } else {
        [self reConnectServer];
    }

}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)messageData{
    if (messageData == nil || ![messageData isKindOfClass:[NSString class]]) {
        return;
    }

    GaiaXSocketModel *model = [[GaiaXSocketModel alloc] initWithMessageString:messageData];
    GaiaXSocketCallback callback = [_requestIdCallbackTable objectForKey:model.messageId];
    if (callback != nil) {
        __weak typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            callback(model);
            [weakSelf.requestIdCallbackTable removeObjectForKey:model.messageId];
        });
    } else {
        if ([model.method isEqualToString:@"close"]) {
            _socketStatus = GaiaXSocketStatusDisConnect;
            dispatch_async(dispatch_get_main_queue(), ^{
                if (self.delegate && [self.delegate respondsToSelector:@selector(socketClient:didFailWithError:)]) {
                    [self.delegate socketClient:self didFailWithError:nil];
                }
            });
        } else if ([model.method isEqualToString:@"mode/get"]) {
            NSString *previewMode = @"none";
            NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
            if ([defaults objectForKey:@"GAIAX_LAST_PREVIEW_MODE"] != nil) {
                previewMode = [defaults stringForKey:@"GAIAX_LAST_PREVIEW_MODE"];
            }
            NSString *jsMode = @"default";
            if ([defaults objectForKey:@"GAIAX_JS_LAST_DEBUGGER_MODE"] != nil) {
                jsMode = [defaults stringForKey:@"GAIAX_JS_LAST_DEBUGGER_MODE"];
            }
            GaiaXSocketModel *m = [GaiaXSocketModel responseWithMessageId:[model.messageId integerValue]
                                                                       result:@{@"preview":previewMode,@"js":jsMode}];
            [self sendResponse:m];
        } else {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (self.delegate && [self.delegate respondsToSelector:@selector(socketClient:didReceiveMessage:)]) {
                    [self.delegate socketClient:self didReceiveMessage:model];
                }
            });
        }
    }

}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if (self.delegate && [self.delegate respondsToSelector:@selector(socketClientDidDisConnect:)]) {
            [self.delegate socketClientDidDisConnect:self];
        }
    });
        
    if (self.closeManually) {
        self.socketStatus = GaiaXSocketStatusDefault;
        return;
    }
    
    self.socketStatus = GaiaXSocketStatusDisConnect;
    
    if (![GaiaXSocketUtils isNetworkReachable]) {
        [self networkStartCheck];
    } else {
        [self reConnectServer];
    }
}


@end
