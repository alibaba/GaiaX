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


#import "GaiaXCommandCenter.h"
#import <GaiaXSocket/GaiaXSocket.h>
#import "GaiaXSocketToastView.h"
#import <GaiaXJS/GaiaXJS.h>
#import "GaiaXScanViewController.h"

@implementation GaiaXCommandCenter


+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    static GaiaXCommandCenter *commandCenter = nil;
    dispatch_once(&onceToken, ^{
        commandCenter = [[GaiaXCommandCenter alloc] init];
        [[NSNotificationCenter defaultCenter] addObserver:commandCenter selector:@selector(openSaoYiSao:) name:@"GAIAX_SOCKET_COMMAND_OPEN_SAOYISAO" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:commandCenter selector:@selector(closeSocketConnect:) name:@"GAIAX_SOCKET_COMMAND_CLOSE_SOCKET_CONNECT" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:commandCenter selector:@selector(socketConnectStateConnected:) name:@"GAIAX_SOCKET_CONNECT_STATE_CONNECTED" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:commandCenter selector:@selector(commandAction:) name:@"GAIAX_SOCKET_COMMAND" object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:commandCenter selector:@selector(updatedAction:) name:@"GAIAX_TEMPLATES_UPDATED_LIST" object:nil];
    });
    return commandCenter;
}

- (void)commandAction:(NSNotification *)notify {
    NSDictionary *userInfo = notify.userInfo;
    if (userInfo) {
        if ([userInfo[@"type"] isEqualToString:@"preview"]) {
          
        } else if ([userInfo[@"type"] isEqualToString:@"js"]) {
            if ([userInfo[@"action"] isEqualToString:@"open"]) {
                [GaiaXJSConfig setBreakPointDebuggingEnabled:YES];
                GaiaXSocketToastView *toastView = [[GaiaXSocketToastView alloc] init];
                [toastView showToastWithTitle:@"GaiaX DevTools【GaiaXJS 断点调试】模式已打开" messages:nil];
                [self setLastJSDebuggerMode:@"breakpoint"];
            } else if ([userInfo[@"action"] isEqualToString:@"close"]) {
                [GaiaXJSConfig setBreakPointDebuggingEnabled:NO];
                GaiaXSocketToastView *toastView = [[GaiaXSocketToastView alloc] init];
                [toastView showToastWithTitle:@"GaiaX DevTools【GaiaXJS 断点调试】模式已关闭" messages:nil];
                [self setLastJSDebuggerMode:nil];
            }
        }
    }
}

- (void)updatedAction:(NSNotification *)notify {
    NSDictionary *userInfo = notify.userInfo;
    if (userInfo) {
        NSArray *list = userInfo[@"ids"];
        if (list && list.count > 0) {
            GaiaXSocketToastView *toastView = [[GaiaXSocketToastView alloc] init];
            [toastView showToastWithTitle:@"推送成功（ GaiaX DevTools ）" messages:list];
        }
    }
}

- (void)openSaoYiSao:(NSNotification *)notify {
    UIViewController *vc = [[GaiaXScanViewController alloc] init];
    vc.title = NSLocalizedString(@"fastpreview", nil);
    [(UINavigationController *)[UIApplication sharedApplication].keyWindow.rootViewController pushViewController:vc animated:YES];
}

- (void)closeSocketConnect:(NSNotification *)notify {
    GaiaXSocketManager *manager = [GaiaXSocketManager sharedInstance];
    GaiaXSocketClient *socketClient = [manager socketClient];
    [socketClient sendeNotification:[GaiaXSocketModel notificationWithMethod:@"close" params:nil]];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [manager disconnect];
    });
}

- (void)socketConnectStateConnected:(NSNotification *)notify {
    GaiaXSocketClient *socketClient = [[GaiaXSocketManager sharedInstance] socketClient];
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    dict[@"version"] = @"2.0";
    dict[@"deviceName"] = [UIDevice currentDevice].name;
    dict[@"deviceMode"] = [UIDevice currentDevice].model;
    dict[@"systemName"] = [UIDevice currentDevice].systemName;
    dict[@"systemVersion"] = [UIDevice currentDevice].systemVersion;
    [socketClient sendRequest:[GaiaXSocketModel requestWithMethod:@"initialized" params:dict] callback:^(GaiaXSocketModel * _Nonnull response) {
        GaiaXSocketToastView *toastView = [[GaiaXSocketToastView alloc] init];
        [toastView showToastWithTitle:@"GaiaX DevTools 已成功连接到 Gaia Studio" messages:nil];
    }];
}


- (NSString *)getLastPreviewMode {
    NSString *result = @"none";
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if ([defaults objectForKey:@"GAIAX_LAST_PREVIEW_MODE"] != nil) {
        result = [defaults stringForKey:@"GAIAX_LAST_PREVIEW_MODE"];
    }
    return result;
}


- (void)setLastPreviewMode:(NSString *)mode {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if(mode == nil) {
        [defaults removeObjectForKey:@"GAIAX_LAST_PREVIEW_MODE"];
    } else {
        [defaults setObject:mode forKey:@"GAIAX_LAST_PREVIEW_MODE"];
    }
}

- (NSString *)getLastJSDebuggerMode {
    NSString *result = @"default";
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if ([defaults objectForKey:@"GAIAX_JS_LAST_DEBUGGER_MODE"] != nil) {
        result = [defaults stringForKey:@"GAIAX_JS_LAST_DEBUGGER_MODE"];
    }
    return result;
}

- (void)setLastJSDebuggerMode:(NSString *)mode {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    if(mode == nil) {
        [defaults removeObjectForKey:@"GAIAX_JS_LAST_DEBUGGER_MODE"];
    } else {
        [defaults setObject:mode forKey:@"GAIAX_JS_LAST_DEBUGGER_MODE"];
    }
}

@end
