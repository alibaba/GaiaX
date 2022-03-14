//
//  EventViewController.m
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

#import "EventViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

@interface EventViewController ()<GXEventProtocal, GXTrackProtocal>{
    UITextView *_resultView;
    GXTemplateData *_data;
    UILabel *_descLabel;
    UIView *_view;
    BOOL _isFollow;
}

@end

@implementation EventViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    
    UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(10, 100, self.view.frame.size.width, 0)];
    label.textColor = [UIColor blackColor];
    label.text = @"事件测试";
    [self.view addSubview:label];

    //templateItem
    GXTemplateItem *templateItem = [[GXTemplateItem alloc] init];
    templateItem.templateId = @"gx-content-uper";
    templateItem.bizId = [GaiaXHelper bizId];
    templateItem.isLocal = YES;

    //渲染view
    _view = [TheGXTemplateEngine creatViewByTemplateItem:templateItem
                                                    measureSize:CGSizeMake(self.view.frame.size.width, NAN)];
    CGRect frame = _view.frame;
    frame.origin.y = CGRectGetMaxY(label.frame);
    _view.frame = frame;
    [self.view addSubview:_view];

    //生成数据
    _data = [[GXTemplateData alloc] init];
    _data.data = [GaiaXHelper jsonWithFileName:@"uper"];
    
    //设置相关的代理 & 监听
    _data.eventListener = self;
    _data.trackListener = self;
    
    //绑定数据
    [TheGXTemplateEngine bindData:_data onView:_view];
    
    _descLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_view.frame) + 15, self.view.frame.size.width - 20, 30)];
    _descLabel.font = [UIFont boldSystemFontOfSize:14.f];
    _descLabel.textColor = [UIColor redColor];
    _descLabel.text = NSLocalizedString(@"event_log", nil);
    [self.view addSubview:_descLabel];
    
    _resultView = [[UITextView alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_descLabel.frame), self.view.frame.size.width - 20, 200)];
    _resultView.font = [UIFont systemFontOfSize:14];
    _resultView.textColor = [UIColor blackColor];
    _resultView.layer.borderColor = [UIColor lightGrayColor].CGColor;
    _resultView.layer.borderWidth = 1.f;
    _resultView.editable = NO;
    [self.view addSubview:_resultView];

}


#pragma mark - 埋点
- (void)gx_onTrackEvent:(GXTrack *)track{
    NSLog(@"埋点绑定事件 -\n view：%@，\n 节点id：%@, \n 参数：%@", track.view, track.nodeId, track.trackParams);
}

#pragma mark - 事件

- (void)gx_onGestureEvent:(GXEvent *)event{
    NSString *text = [NSString stringWithFormat:@"接收到点击事件 -\n view：%@，\n 节点id：%@, \n 参数：%@", event.view, event.nodeId, event.eventParams];
    NSLog(@"%@", text);

    //UI展示
    _resultView.text = text;
    
    if ([event.nodeId isEqualToString:@"follow"]) {
        _isFollow = !_isFollow;
        
        //更新数据
        NSMutableDictionary *dict = (NSMutableDictionary *)_data.data;
        dict[@"data"][@"follow"][@"isFollow"] = @(_isFollow);
                
        //绑定数据
        [TheGXTemplateEngine bindData:_data onView:_view];
    }
    
}

- (void)gx_onScrollEvent:(GXEvent *)event{
    //UI展示
    NSString *text = [NSString stringWithFormat:@"接收到滚动事件 - offsetX：%f", event.contentOffset.x];
    _resultView.text = text;
    NSLog(@"%@", text);
}

- (void)gx_onScrollEndEvent:(GXEvent *)event{
    NSString *text = [NSString stringWithFormat:@"接收到滚动停止事件 - offsetX：%f", event.contentOffset.x];
    _resultView.text = text;
    NSLog(@"%@", text);
}

@end
