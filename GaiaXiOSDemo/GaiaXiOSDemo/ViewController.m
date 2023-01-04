//
//  ViewController.m
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

#import "ViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXScanViewController.h"
#import "GaiaXPreviewViewController.h"
#import "GaiaXHelper.h"


@interface ViewController ()<UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong) NSArray *dataArray;
@property (nonatomic, strong) UITableView *tableView;


@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    //注册业务模板
    [self registerBizService];

    //页面设置
    self.title = @"GaiaXDemo";
    self.view.backgroundColor = [UIColor whiteColor];

    //数据处理 & reload
    self.dataArray = [GaiaXHelper loadGaiaXFounctionList];
    [self.tableView reloadData];
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    return 50.f;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    cell.textLabel.text = [[self.dataArray objectAtIndex:indexPath.item] objectForKey:@"title"];
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    NSDictionary *dict = [self.dataArray objectAtIndex:indexPath.item];
    NSString *class = [dict objectForKey:@"class"];
    if (class) {
        UIViewController *vc = [[NSClassFromString(class) alloc] init];
        NSString *title =  [dict objectForKey:@"title"];
        vc.title = title;
        [self.navigationController pushViewController:vc animated:YES];
    }
}


#pragma mark -

- (IBAction)previewAction:(id)sender{
    if (self.isSimuLator) {
        NSString *content = @"gaiax://gaiax/preview?url=ws://30.78.146.17:9001&id=test-template&type=auto";
        GaiaXPreviewViewController *previewController = [[GaiaXPreviewViewController alloc] initWithUrl:content];
        [self.navigationController pushViewController:previewController animated:YES];
    }else {
        UIViewController *vc = [[GaiaXScanViewController alloc] init];
        vc.title = NSLocalizedString(@"fastpreview", nil);
        [self.navigationController pushViewController:vc animated:YES];
    }
}


-(BOOL)isSimuLator
{
    if (TARGET_IPHONE_SIMULATOR == 1 && TARGET_OS_IPHONE == 1) {
        return YES;
    }else{
        return NO;
    }
}

#pragma mark -

- (void)registerBizService{
    //图层合并
    TheGXTemplateEngine.isNeedFlat = YES;
    //业务注册
    NSString *bizId = [GaiaXHelper bizId];
    [TheGXRegisterCenter registerTemplateServiceWithBizId:bizId templateBundle:@"GaiaXTemplate.bundle"];
}


#pragma mark - lazy load

- (UITableView *)tableView{
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        [_tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"cell"];
        [self.view addSubview:_tableView];
    }
    return _tableView;
}


@end
