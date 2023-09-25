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
@class GaiaXSocketClient;

@protocol GaiaXSocketProtocol <NSObject>

@required
- (NSString *)gxMessageId;

@optional

@property (nonatomic, assign) BOOL isActive;

- (void)gxSocketClientDidConnect:(GaiaXSocketClient *)client;

- (void)gxSocketClientDidDisConnect:(GaiaXSocketClient *)client;

- (void)gxSocketClient:(GaiaXSocketClient *)client didFailWithError:(NSError *)error;

- (void)gxSocketClient:(GaiaXSocketClient *)client didReceiveMessage:(GaiaXSocketModel *)message;

@end


@protocol GaiaXOpenPreviewProtocol <NSObject>

@required
+ (void)gxOpenPreviewPage;

@end


NS_ASSUME_NONNULL_END
