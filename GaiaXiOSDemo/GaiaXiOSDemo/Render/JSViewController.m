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

#import "JSViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

@interface JSViewController ()

@property (nonatomic, strong) UIView *view1;

@end

@implementation JSViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];
    //templateItem
    GXTemplateItem *templateItem1 = [[GXTemplateItem alloc] init];
    templateItem1.templateId = @"gx-with-js";
    templateItem1.bizId = [GaiaXHelper bizId];
    templateItem1.isLocal = YES;

    //渲染view
    _view1 = [TheGXTemplateEngine creatViewByTemplateItem:templateItem1 measureSize:CGSizeMake(self.view.frame.size.width - 20, NAN)];
    CGRect frame1 = _view1.frame;
    frame1.origin.x = 10;
    frame1.origin.y = 100;
    _view1.frame = frame1;
    [self.view addSubview:_view1];

    //绑定数据
    GXTemplateData *data1 = [[GXTemplateData alloc] init];
    data1.data = [GaiaXHelper jsonWithFileName:@"gx-with-js"];
    [TheGXTemplateEngine bindData:data1 onView:_view1];
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
