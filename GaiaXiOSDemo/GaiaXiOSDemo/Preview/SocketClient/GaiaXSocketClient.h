//
//  GaiaXSocketClient.h
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

#import <Foundation/Foundation.h>
#import "GaiaXSocketRequest.h"

NS_ASSUME_NONNULL_BEGIN

@class GaiaXSocketClient;

typedef NS_ENUM(NSInteger, GaiaXWebSocketStatus){
    GaiaXWebSocketStatusDefault = 0, //初始状态，未连接
    GaiaXWebSocketStatusConnect,     //已连接
    GaiaXWebSocketStatusDisConnect,  //断开连接
};

@protocol GaiaXSocketClientDelegate<NSObject>

@optional

/**
 *接受到模板信息
 *  @param client 客户端socket.
 *  @param templateInfo 模板信息.
 */
-(void)gaiaXSocketClient:(GaiaXSocketClient *)client didReceiveTemplateInfo:(NSDictionary *)templateInfo;

/**
 *  接受到嵌套模板信息
 *  @param client 客户端socket.
 *  @param templateInfo 模板信息.
 */
-(void)gaiaXSocketClient:(GaiaXSocketClient *)client didReceiveNestedTemplateInfo:(NSDictionary *)templateInfo;

/**
 *  scoket连接初始化成功
 *  @param client 客户端socket.
 */
-(void)gaiaXSocketClientDidInitialize:(GaiaXSocketClient *)client;

/**
 *scoket连接出错
 *  @param client 客户端socket.
 *  @param error  错误.
 */
-(void)gaiaXSocketClient:(GaiaXSocketClient *)client didFailWithError:(NSError *)error;
/**
 *scoket连接成功
 *  @param client 客户端socket.
 */
-(void)gaiaXSocketClientDidConnect:(GaiaXSocketClient *)client;
/**
 *scoket取消连接
 *  @param client 客户端socket.
 */
-(void)gaiaXSocketClientDidDisconnect:(GaiaXSocketClient *)client;

@end



@interface GaiaXSocketClient : NSObject

//是否连接
@property (nonatomic, assign) BOOL isConnect;
//socket状态
@property (nonatomic, assign) GaiaXWebSocketStatus socketStatus;
//socket代理
@property (nonatomic, weak) id<GaiaXSocketClientDelegate> delegate;

//初始化
- (instancetype)initWithURL:(NSURL *)url delegate:(id<GaiaXSocketClientDelegate>)delegate;

//建立长连接
- (void)connectServer;
//重新连接
- (void)reConnectServer;
//关闭连接
- (void)webSocketClose;
//向服务器发送数据
- (void)sendRequestToServer:(GaiaXSocketRequest *)request;

@end

NS_ASSUME_NONNULL_END
