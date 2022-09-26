//
//  NormalViewController.m
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

#import "NormalViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

@interface NormalViewController (){
    UIView *_view1;
    UIView *_view2;
}

@end

@implementation NormalViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    [self renderTemplate1];
    [self renderTemplate2];
}

- (void)renderTemplate1{

    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(10, 100, self.view.frame.size.width - 20, 40)];
    label1.textColor = [UIColor blackColor];
    NSString *tmpString = NSLocalizedString(@"normal_template", nil);
    label1.text = [NSString stringWithFormat:@"%@ 1",tmpString];
    [self.view addSubview:label1];

    //templateItem
    GXTemplateItem *templateItem1 = [[GXTemplateItem alloc] init];
    templateItem1.templateId = @"gx-style-backdrop-filter";
    templateItem1.bizId = [GaiaXHelper bizId];
    templateItem1.isLocal = YES;

    //渲染view
    _view1 = [TheGXTemplateEngine creatViewByTemplateItem:templateItem1 measureSize:CGSizeMake(self.view.frame.size.width - 20, NAN)];
    CGRect frame1 = _view1.frame;
    frame1.origin.x = 10;
    frame1.origin.y = CGRectGetMaxY(label1.frame);
    _view1.frame = frame1;
    [self.view addSubview:_view1];

    //绑定数据
    GXTemplateData *data1 = [[GXTemplateData alloc] init];
//    data1.data = [GaiaXHelper jsonWithFileName:@"vertical-item"];
    data1.data = @{@"blur_text": @"我是文本我是文本我是文本我是文本我是文本", @"img": @"https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fphoto%2F2011-10-14%2Fenterdesk.com-2E8A38D0891116035E78DD713EED9637.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1666781857&t=595349c20a2e34ceddbd48b130339fbf"};
    
    [TheGXTemplateEngine bindData:data1 onView:_view1];
    
}


- (void)renderTemplate2{
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_view1.frame) + 30, self.view.frame.size.width, 40)];
    label2.textColor = [UIColor blackColor];
    NSString *tmpString = NSLocalizedString(@"normal_template", nil);
    label2.text = [NSString stringWithFormat:@"%@ 2",tmpString];
    [self.view addSubview:label2];

    //templateItem
    GXTemplateItem *templateItem2 = [[GXTemplateItem alloc] init];
    templateItem2.templateId = @"gx-horizontal-item";
    templateItem2.bizId = [GaiaXHelper bizId];
    templateItem2.isLocal = YES;

    //渲染view
    _view2 = [TheGXTemplateEngine creatViewByTemplateItem:templateItem2 measureSize:CGSizeMake(self.view.frame.size.width, NAN)];
    CGRect frame2 = _view2.frame;
    frame2.origin.y = CGRectGetMaxY(label2.frame);
    _view2.frame = frame2;
    [self.view addSubview:_view2];

    //绑定数据
    GXTemplateData *data2 = [[GXTemplateData alloc] init];
    data2.data = [GaiaXHelper jsonWithFileName:@"horizontal-item"];
    [TheGXTemplateEngine bindData:data2 onView:_view2];
}

@end
