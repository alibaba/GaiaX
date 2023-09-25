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


#import "GaiaXDevTools.h"
#import "GaiaXPreviewViewController.h"
#import <GaiaXSocket/GaiaXSocket.h>
#import <GaiaXJS/GaiaXJS.h>

@interface GaiaXDevTools ()

@property (nonatomic, strong) UILabel *tipLabel;
@property (nonatomic, strong) UIAlertController* alertController;
@property (nonatomic, assign) GaiaXSocketStatus socketStatus;
@property (nonatomic, strong) NSTimer *checkStatusTimer;

@end

@implementation GaiaXDevTools

- (void)setSocketStatus:(GaiaXSocketStatus)socketStatus {
    _socketStatus = socketStatus;
    if (socketStatus == GaiaXSocketStatusDefault) {
        self.tipLabel.text = @"GaiaX DevTools 等待连接到 Gaia Studio...";
        self.tipLabel.textColor = [UIColor colorWithRed:1 green:1 blue:1 alpha:0.5];
        self.backgroundColor = [UIColor colorWithRed:245/255.0 green:168/255.0 blue:35/255.0 alpha:1];
    } else if (socketStatus == GaiaXSocketStatusConnect) {
        self.tipLabel.text = @"GaiaX DevTools 已连接到 Gaia Studio";
        self.backgroundColor = [UIColor colorWithRed:41/255.0 green:142/255.0 blue:70/255.0 alpha:1];
        self.tipLabel.textColor = [UIColor whiteColor];
    } else  if (socketStatus == GaiaXSocketStatusDisConnect) {
        self.tipLabel.text = @"GaiaX DevTools 已与 Gaia Studio 断开";
        self.tipLabel.textColor = [UIColor colorWithRed:1 green:1 blue:1 alpha:0.5];
        self.backgroundColor = [UIColor redColor];
    }
}

+ (instancetype)sharedInstance {
    static GaiaXDevTools *devTool = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        devTool = [[GaiaXDevTools alloc] initWithFrame:CGRectMake(0,0, UIScreen.mainScreen.bounds.size.width, [self statusBarHeight]+20)];
        [devTool addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:devTool action:@selector(tapAction:)]];
        devTool.tipLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, [self statusBarHeight], devTool.bounds.size.width, 20)];
        devTool.tipLabel.textAlignment = NSTextAlignmentCenter;
        devTool.tipLabel.font = [UIFont systemFontOfSize:12 weight:UIFontWeightBold];
        
        devTool.socketStatus = [[GaiaXSocketManager sharedInstance] socketClient].socketStatus;
        [devTool addSubview:devTool.tipLabel];
    });
    return devTool;
}


+ (NSInteger)statusBarHeight {
    if (UIUserInterfaceIdiomPad == UI_USER_INTERFACE_IDIOM()) {
        return 20;
    }
    return [self isLiuHaiPing] ? 44 : 20;
}


+ (BOOL)isLiuHaiPing {
    if (UIUserInterfaceIdiomPhone != UI_USER_INTERFACE_IDIOM()) {
        return NO;
    }
    //iOS11，判断底部的inset
    BOOL result = NO;
    if (@available(iOS 11.0, *)) {
        UIWindow *mainWindow = [[UIApplication sharedApplication] keyWindow];
        if (mainWindow.safeAreaInsets.bottom > 0.0) {
            result = YES;
        }
    }
    return result;
}


- (void)tapAction:(UITapGestureRecognizer *)gesture {
    
    BOOL socketDidConnect = self.socketStatus == GaiaXSocketStatusConnect;
    
    self.alertController = [UIAlertController alertControllerWithTitle:@"GaiaX DevTools"
                                                                             message:@"支持【多合一模式】"
                                                                      preferredStyle:UIAlertControllerStyleActionSheet];
    self.alertController.popoverPresentationController.sourceView = gesture.view;
    self.alertController.popoverPresentationController.sourceRect = gesture.view.bounds;
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        
    }];
    UIAlertAction *closeAction = [UIAlertAction actionWithTitle:@"关闭 GaiaX DevTools" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [GaiaXDevTools dismiss];
    }];
    
    UIAlertAction *connectToAction = nil;
    if (socketDidConnect) {
        connectToAction = [UIAlertAction actionWithTitle:@"断开连接【Gaia Studio】" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_SOCKET_COMMAND_CLOSE_SOCKET_CONNECT" object:nil userInfo:nil];
        }];
    } else {
        connectToAction = [UIAlertAction actionWithTitle:@"扫码连接到【Gaia Studio】" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_SOCKET_COMMAND_OPEN_SAOYISAO" object:nil userInfo:nil];
        }];
    }

    UINavigationController *currentViewController = (UINavigationController *)[UIApplication sharedApplication].keyWindow.rootViewController;
    UIAlertAction *preivewAction = nil;
    if ([currentViewController.topViewController isKindOfClass:[GaiaXPreviewViewController class]]) {
        preivewAction = [UIAlertAction actionWithTitle:@"关闭【实时预览】" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            GaiaXPreviewViewController *controller = [[GaiaXPreviewViewController alloc] initWithNibName:nil bundle:nil];
            controller.hidesBottomBarWhenPushed = YES;
            [currentViewController popViewControllerAnimated:YES];
        }];
    } else {
        preivewAction = [UIAlertAction actionWithTitle:@"打开【实时预览】" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            GaiaXPreviewViewController *controller = [[GaiaXPreviewViewController alloc] initWithNibName:nil bundle:nil];
            [currentViewController pushViewController:controller animated:YES];
        }];
        preivewAction.enabled = socketDidConnect;
    }
  

    UIAlertAction *jsBreakpointAction = nil;
    if ([GaiaXJSConfig isBreakPointDebugging]) {
        jsBreakpointAction = [UIAlertAction actionWithTitle:@"关闭 GaiaXJS【断点调试】模式" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_SOCKET_COMMAND" object:nil userInfo:@{@"action": @"close", @"type" : @"js" , @"subType": @"breakpoint"}];
        }];
    } else {
        jsBreakpointAction = [UIAlertAction actionWithTitle:@"打开 GaiaXJS【断点调试】模式" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_SOCKET_COMMAND" object:nil userInfo:@{@"action": @"open", @"type" : @"js" , @"subType": @"breakpoint"}];
        }];
        jsBreakpointAction.enabled = socketDidConnect;
    }
    

    [self.alertController addAction:cancelAction];
    [self.alertController addAction:connectToAction];
    [self.alertController addAction:preivewAction];
    [self.alertController addAction:jsBreakpointAction];
    [self.alertController addAction:closeAction];
    [[[UIApplication sharedApplication] windows][0].rootViewController presentViewController:self.alertController animated:YES completion:^{
        
    }];
}

+ (void)show {
    GaiaXDevTools *devTool = [GaiaXDevTools sharedInstance];
    if (devTool.superview == nil) {
        [[[UIApplication sharedApplication] windows][0] addSubview:devTool];
    } else {
        [devTool removeFromSuperview];
        [[[UIApplication sharedApplication] windows][0] addSubview:devTool];
    }
    if ([devTool.checkStatusTimer isValid]) {
        [devTool.checkStatusTimer invalidate];
        devTool.checkStatusTimer = nil;
    }
    devTool.checkStatusTimer = [NSTimer scheduledTimerWithTimeInterval:1 target:devTool selector:@selector(checkStatus) userInfo:nil repeats:YES];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_DEVTOOLS_DID_OPENED" object:nil userInfo:nil];
}

- (void)checkStatus {
    if (self.socketStatus != [[GaiaXSocketManager sharedInstance] socketClient].socketStatus) {
        self.socketStatus = [[GaiaXSocketManager sharedInstance] socketClient].socketStatus;
        [self.alertController dismissViewControllerAnimated:YES completion:^{
            
        }];
    }
}

+ (void)dismiss {
    [[NSNotificationCenter defaultCenter] removeObserver:[GaiaXDevTools sharedInstance] name:@"GAIAX_SOCKET_CONNECT_STATE_CONNECTED" object:nil];
    [[GaiaXDevTools sharedInstance] removeFromSuperview];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"GAIAX_DEVTOOLS_SWITCH"];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_DEVTOOLS_DID_CLOSED" object:nil userInfo:nil];
}

+ (UIViewController *)findCurrentViewController {
    UIWindow *window = [[UIApplication sharedApplication].delegate window];
    UIViewController *topViewController = [window rootViewController];
    
    while (true) {
        
        if (topViewController.presentedViewController) {
            
            topViewController = topViewController.presentedViewController;
            
        } else if ([topViewController isKindOfClass:[UINavigationController class]] && [(UINavigationController*)topViewController topViewController]) {
            
            topViewController = [(UINavigationController *)topViewController topViewController];
            
        } else if ([topViewController isKindOfClass:[UITabBarController class]]) {
            
            UITabBarController *tab = (UITabBarController *)topViewController;
            topViewController = tab.selectedViewController;
            
        } else {
            break;
        }
    }
    return topViewController;
}

@end
