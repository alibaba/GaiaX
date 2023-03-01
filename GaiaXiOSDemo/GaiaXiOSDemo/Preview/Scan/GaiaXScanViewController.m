//
//  GaiaXScanViewController.m
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

#import "GaiaXScanViewController.h"
#import "GaiaXScanView.h"
#import "GaiaXHelper.h"
#import <GaiaXSocket/GaiaXSocket.h>
#import "GaiaXCommandCenter.h"

@interface GaiaXScanViewController ()<GaiaXScanViewDelegate>

@property (nonatomic, assign) BOOL isFinished;
@property (nonatomic, strong) GaiaXScanView *scanView;

@end

@implementation GaiaXScanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
        
    //扫码view
    CGFloat scanWidth = self.view.frame.size.width;
    CGFloat topHeight = [[UIApplication sharedApplication] statusBarFrame].size.height + 44;
    _scanView = [[GaiaXScanView alloc] initWithFrame:CGRectMake(0, topHeight, scanWidth, scanWidth)];
    _scanView.delegate = self;
    [self.view addSubview:_scanView];
}

- (void)didRecievedScanContent:(NSString *)content{
        
    if (self.isFinished) {
        return;
    }
    
    self.isFinished = YES;
    
    NSString *resultString  = [GaiaXHelper URLDecodedString:content];
    //解析url的参数
    NSDictionary *params =  [GaiaXHelper parameterFromUrl:resultString];
    //socketUrl
    NSString *socketUrl = [params valueForKey:@"url"];
    socketUrl = socketUrl.stringByRemovingPercentEncoding;
    //type
    NSString *type = [params valueForKey:@"type"];

    if ([type isEqualToString:@"connect"]){
        //建立socket连接
        [GaiaXSocketManager.sharedInstance connet:socketUrl];
        [self.navigationController popViewControllerAnimated:YES];
    }
}


- (void)viewDidAppear:(BOOL)animated{
    [_scanView startScan];
}

- (void)viewDidDisappear:(BOOL)animated{
    self.isFinished = NO;
    [_scanView stopScan];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
