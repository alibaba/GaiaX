//
//  ListViewController.m
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

#import "ListViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"
#import "TemplateListCell.h"

@interface ListViewController ()<UITableViewDelegate, UITableViewDataSource>{
    CGFloat _itemWidth;
}

@property (nonatomic, strong) NSMutableArray *dataArray;
@property (nonatomic, strong) UITableView *tableView;

@end

@implementation ListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    _itemWidth = self.view.frame.size.width;
    [self.tableView registerClass:[TemplateListCell class] forCellReuseIdentifier:@"cell"];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        //处理数据
        [self processTemplateInfo];
        dispatch_async(dispatch_get_main_queue(), ^{
            //刷新UI
            [self.tableView reloadData];
        });
    });
}


-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return self.dataArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    CGFloat height = [[self.dataArray[indexPath.row] objectForKey:@"height"] floatValue];
    return height + 55;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    TemplateListCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell" forIndexPath:indexPath];
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    NSDictionary *itemInfo = self.dataArray[indexPath.row];
    [cell setupData:itemInfo];
    
    return cell;
}



#pragma mark - data

- (void)processTemplateInfo{
    NSArray *tmpData = @[@"vertical-item", @"horizontal-item", @"subscribe-item",@"multi-scroll",  @"recommend", @"uper", @"uper"];
    NSArray *tmpTemplate = @[@"gx-vertical-item", @"gx-horizontal-item", @"gx-subscribe-item", @"gx-mutable-scroll", @"gx-recommend-scroll", @"gx-content-uper", @"gx-content-uper-grid"];
    
    for (int i = 0; i < tmpTemplate.count; i++) {
        //item
        GXTemplateItem *item = [[GXTemplateItem alloc] init];
        item.templateId = tmpTemplate[i];
        item.bizId = [GaiaXHelper bizId];
        item.isLocal = YES;
        
        //data
        GXTemplateData *data = [[GXTemplateData alloc] init];
        data.data = [GaiaXHelper jsonWithFileName:tmpData[i]];
        
        //高度
        CGFloat height = [TheGXTemplateEngine sizeWithTemplateItem:item measureSize:CGSizeMake(_itemWidth, NAN) data:data].height;
        [self.dataArray addObject:@{@"item":item, @"data": data, @"height": @(height)}];
    }
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

- (NSMutableArray *)dataArray{
    if (!_dataArray) {
        _dataArray = [NSMutableArray array];
    }
    return _dataArray;
}


@end
