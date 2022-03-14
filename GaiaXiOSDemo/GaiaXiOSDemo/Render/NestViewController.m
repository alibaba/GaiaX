//
//  NestViewController.m
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

#import "NestViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

@interface NestViewController (){
    UIView *_view1;
    UIView *_view2;
    UIView *_view3;
}

@end

@implementation NestViewController

- (void)loadView{
    UIView *view = [[UIScrollView alloc] initWithFrame:[UIScreen mainScreen].bounds];
    self.view = view;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    [self renderTemplate1];
    [self renderTemplate2];
    [self renderTemplate3];
    
    //更新contentSize
    UIScrollView *scrollView = (UIScrollView *)self.view;
    scrollView.contentSize = CGSizeMake(self.view.bounds.size.width, CGRectGetMaxY(_view3.frame) + 30);
}

- (void)renderTemplate1{
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(10, 10, self.view.frame.size.width - 30, 40)];
    label1.textColor = [UIColor blackColor];
    label1.text = NSLocalizedString(@"normal_template", nil);
    [self.view addSubview:label1];

    //templateItem
    GXTemplateItem *templateItem1 = [[GXTemplateItem alloc] init];
    templateItem1.templateId = @"gx-content-uper-top";
    templateItem1.bizId = [GaiaXHelper bizId];
    templateItem1.isLocal = YES;

    //渲染view
    _view1 = [TheGXTemplateEngine creatViewByTemplateItem:templateItem1 measureSize:CGSizeMake(self.view.frame.size.width, NAN)];
    CGRect frame1 = _view1.frame;
    frame1.origin.y = CGRectGetMaxY(label1.frame);
    _view1.frame = frame1;
    [self.view addSubview:_view1];

    //绑定数据
    GXTemplateData *data1 = [[GXTemplateData alloc] init];
    data1.data = [GaiaXHelper jsonWithFileName:@"uper-top"];
    [TheGXTemplateEngine bindData:data1 onView:_view1];
}


- (void)renderTemplate2{

    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_view1.frame) + 30, self.view.frame.size.width, 40)];
    label2.textColor = [UIColor blackColor];
    label2.text = NSLocalizedString(@"container_template", nil);
    [self.view addSubview:label2];

    //templateItem
    GXTemplateItem *templateItem2 = [[GXTemplateItem alloc] init];
    templateItem2.templateId = @"gx-content-uper-scroll";
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
    data2.data = [GaiaXHelper jsonWithFileName:@"uper"];
    [TheGXTemplateEngine bindData:data2 onView:_view2];
}


- (void)renderTemplate3{
    
    UILabel *label3 = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_view2.frame) + 30, self.view.frame.size.width, 40)];
    label3.textColor = [UIColor blackColor];
    label3.text = NSLocalizedString(@"nest_template", nil);
    [self.view addSubview:label3];

    //templateItem
    GXTemplateItem *templateItem3 = [[GXTemplateItem alloc] init];
    templateItem3.templateId = @"gx-content-uper";
    templateItem3.bizId = [GaiaXHelper bizId];
    templateItem3.isLocal = YES;

    //渲染view
    _view3 = [TheGXTemplateEngine creatViewByTemplateItem:templateItem3 measureSize:CGSizeMake(self.view.frame.size.width, NAN)];
    CGRect frame3 = _view3.frame;
    frame3.origin.y = CGRectGetMaxY(label3.frame);
    _view3.frame = frame3;
    [self.view addSubview:_view3];

    //绑定数据
    GXTemplateData *data3 = [[GXTemplateData alloc] init];
    data3.data = [GaiaXHelper jsonWithFileName:@"uper"];
    [TheGXTemplateEngine bindData:data3 onView:_view3];
}


@end
