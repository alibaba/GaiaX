//
//  DataViewController.m
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

#import "DataViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

@interface DataViewController ()<GXDataProtocal>{
    UIView *_view1;
    UIView *_view2;
}

@end

@implementation DataViewController

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
    label1.text = NSLocalizedString(@"normal_data_binding", nil);
    [self.view addSubview:label1];

    //templateItem
    GXTemplateItem *templateItem1 = [[GXTemplateItem alloc] init];
    templateItem1.templateId = @"gx-subscribe-item";
    templateItem1.bizId = [GaiaXHelper bizId];
    templateItem1.isLocal = YES;

    //渲染view
    _view1 = [TheGXTemplateEngine creatViewByTemplateItem:templateItem1 measureSize:CGSizeMake(self.view.frame.size.width, NAN)];
    CGRect frame1 = _view1.frame;
    frame1.origin.y = CGRectGetMaxY(label1.frame);
    _view1.frame = frame1;
    [self.view addSubview:_view1];
    
    UIView *lineView = [[UIView alloc] initWithFrame:CGRectMake(0, frame1.origin.y + frame1.size.height + 20, frame1.size.width, 0.5)];
    lineView.backgroundColor = [UIColor lightGrayColor];
    [self.view addSubview:lineView];

    //绑定数据
    GXTemplateData *data1 = [[GXTemplateData alloc] init];
    data1.data = [GaiaXHelper jsonWithFileName:@"subscribe-item"];
    [TheGXTemplateEngine bindData:data1 onView:_view1];
}


- (void)renderTemplate2{
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(10, CGRectGetMaxY(_view1.frame) + 30, self.view.frame.size.width, 40)];
    label2.textColor = [UIColor blackColor];
    label2.text = NSLocalizedString(@"custom_data_binding", nil);
    [self.view addSubview:label2];

    //templateItem
    GXTemplateItem *templateItem2 = [[GXTemplateItem alloc] init];
    templateItem2.templateId = @"gx-subscribe-item";
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
    data2.data = [GaiaXHelper jsonWithFileName:@"subscribe-item"];
    
    //设置代理/监听
    data2.dataListener = self;
    
    [TheGXTemplateEngine bindData:data2 onView:_view2];
}



#pragma mark - 数据
- (id)gx_onTextProcess:(GXTextData *)data{
    if ([data.templateId isEqualToString:@"gx-subscribe-item"] && [data.nodeId isEqualToString:@"title"]) {
        //基础信息
        NSString *text = data.value;
        NSDictionary *attributeDict = data.attributes;
        NSLog(@"文本:%@, 属性:%@", text, attributeDict);
        
        //生成新的文本，修改文本
        NSString *newText = @"富文本样式预览";
        NSMutableAttributedString *attributedString = [[NSMutableAttributedString  alloc] initWithString:newText];
        [attributedString  addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:20] range:NSMakeRange(0, newText.length)];
        [attributedString addAttribute: NSForegroundColorAttributeName value: [UIColor blueColor] range: NSMakeRange(0, 2)];
        [attributedString addAttribute: NSForegroundColorAttributeName value: [UIColor redColor] range: NSMakeRange(3, 1)];

        return attributedString;
    }
    
    return nil;
}


@end
