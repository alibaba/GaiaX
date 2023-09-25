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

#import "GaiaXSocketManager.h"
#import "GaiaXSocketProtocol.h"
#import <UIKit/UIKit.h>

@interface GaiaXSocketManager ()<GaiaXSocketClientDelegate>{
    Class <GaiaXOpenPreviewProtocol> _openPreviewClazz;
}

@property (nonatomic, strong) NSMutableDictionary *listenerMap;

@end

@implementation GaiaXSocketManager

+ (instancetype)sharedInstance{
    static GaiaXSocketManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[GaiaXSocketManager alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        self.listenerMap = [NSMutableDictionary dictionary];
        self.socketClient = [[GaiaXSocketClient alloc] init];
        self.socketClient.delegate = self;
    }
    return self;
}


- (void)connet:(NSString *)url{
    if (self.socketClient.isConnect) {
        [self.socketClient disConnectServer];
    }
    
    self.socketClient.url = url;
    [self.socketClient connectServer];

}

- (void)disconnect{
    [self.socketClient disConnectServer];
}


- (id <GaiaXSocketProtocol>)listenerForKey:(NSString *)key {
    if (key.length > 0) {
        return self.listenerMap[key];
    }
    return nil;
}

- (void)registerListener:(id <GaiaXSocketProtocol>)listener {
    if (listener && [listener conformsToProtocol:@protocol(GaiaXSocketProtocol)]) {
        NSString *key = [listener gxMessageId];
        if (key.length > 0) {
            self.listenerMap[key] = listener;
        }
    }
}

- (void)unRegisterListener:(id <GaiaXSocketProtocol>)listener {
    if (listener && [listener conformsToProtocol:@protocol(GaiaXSocketProtocol)]) {
        NSString *key = [listener gxMessageId];
        if (key.length > 0) {
            [self.listenerMap removeObjectForKey:key];
        }
    }
}


- (void)clearSocketUrl {
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"gx_socket_url"];
}

#pragma mark - GaiaXSocketClientDelegate

- (void)socketClientDidConnect:(GaiaXSocketClient *)client{
    
    [NSUserDefaults.standardUserDefaults setValue:client.url forKey:@"gx_socket_url"];
    [NSUserDefaults.standardUserDefaults synchronize];
    
    for (id <GaiaXSocketProtocol> listener in self.listenerMap.allValues) {
        if ([listener respondsToSelector:@selector(gxSocketClientDidConnect:)]) {
            [listener gxSocketClientDidConnect:client];
        }
    }
}

- (void)socketClientDidDisConnect:(GaiaXSocketClient *)client{
    for (id <GaiaXSocketProtocol> listener in self.listenerMap.allValues) {
        if ([listener respondsToSelector:@selector(gxSocketClientDidDisConnect:)]) {
            [listener gxSocketClientDidDisConnect:client];
        }
    }
}

- (void)socketClient:(GaiaXSocketClient *)client didFailWithError:(NSError *)error{
    for (id <GaiaXSocketProtocol> listener in self.listenerMap.allValues) {
        if ([listener respondsToSelector:@selector(gxSocketClient:didFailWithError:)]) {
            [listener gxSocketClient:client didFailWithError:error];
        }
    }
}

- (void)socketClient:(GaiaXSocketClient *)client didReceiveMessage:(GaiaXSocketModel *)message{
    
    [self.listenerMap enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull key, id <GaiaXSocketProtocol> _Nonnull obj, BOOL * _Nonnull stop) {
        if ([obj respondsToSelector:@selector(gxSocketClient:didReceiveMessage:)]) {
            [obj gxSocketClient:client didReceiveMessage:message];
        }
    }];
    
}

@end
