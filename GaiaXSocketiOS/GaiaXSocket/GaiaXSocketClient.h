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

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class GaiaXSocketModel;
typedef void(^GaiaXSocketCallback)(GaiaXSocketModel *model);

typedef NS_ENUM(NSUInteger, GaiaXSocketStatus) {
    GaiaXSocketStatusDefault = 0,
    GaiaXSocketStatusConnect,
    GaiaXSocketStatusDisConnect,
};


@protocol GaiaXSocketClientDelegate;

@interface GaiaXSocketClient : NSObject

@property (nonatomic, strong) NSString *url;
@property(nonatomic, assign, readonly) BOOL isConnect;
@property(nonatomic, assign) GaiaXSocketStatus socketStatus;
@property(nonatomic,   weak) id <GaiaXSocketClientDelegate> delegate;

- (instancetype)initWithUrl:(NSString *)url delegate:(id <GaiaXSocketClientDelegate>)delegate;
@property (nonatomic, assign) BOOL closeManually;

- (void)connectServer;
- (void)reConnectServer;
- (void)disConnectServer;

- (void)sendRequest:(GaiaXSocketModel *)request  callback:(GaiaXSocketCallback)callback;

- (void)sendResponse:(GaiaXSocketModel *)model;

- (void)sendeNotification:(GaiaXSocketModel *)model;

@end


@protocol GaiaXSocketClientDelegate <NSObject>

- (void)socketClientDidConnect:(GaiaXSocketClient *)client;

- (void)socketClientDidDisConnect:(GaiaXSocketClient *)client;

- (void)socketClient:(GaiaXSocketClient *)client didFailWithError:(NSError *)error;

- (void)socketClient:(GaiaXSocketClient *)client didReceiveMessage:(GaiaXSocketModel *)message;

@end

NS_ASSUME_NONNULL_END
