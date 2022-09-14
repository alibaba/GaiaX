//
//  RecycleListViewController.m
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

#import "RecycleListViewController.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"
#import "RecycleTemplateListCell.h"

@interface RecycleListViewController ()<UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>{
    CGFloat _itemWidth;
}

@property (nonatomic, strong) NSMutableArray *dataArray;
@property (nonatomic, strong) NSMutableArray *heights;
@property (nonatomic, strong) UICollectionView *collectionView;

@end

@implementation RecycleListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    [self.view addSubview:self.collectionView];
    _itemWidth = self.view.frame.size.width;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        //处理数据
        [self processTemplateInfo];
    
        dispatch_async(dispatch_get_main_queue(), ^{
            //刷新UI
            [self.collectionView reloadData];
        });
    });
}


- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section{
    return self.dataArray.count;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    CGFloat height = [self.heights[indexPath.item] floatValue];
    return CGSizeMake(_itemWidth, height);
}


- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(nonnull NSIndexPath *)indexPath {
    //重用标识
    RecycleTemplateListCell *cell = (RecycleTemplateListCell *)[collectionView dequeueReusableCellWithReuseIdentifier:@"cell" forIndexPath:indexPath];
    
    //数据绑定
    GXTemplateData *data = [self.dataArray objectAtIndex:indexPath.item];
    [cell setupData:data];
    
    return cell;
}


#pragma mark - data

- (void)processTemplateInfo{
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"gx-subscribe-item";
    item.bizId = [GaiaXHelper bizId];
    
    
    for (int i = 0; i < 10; i++) {
        //data
        GXTemplateData *data = [[GXTemplateData alloc] init];
        data.data = [GaiaXHelper jsonWithFileName:@"subscribe-item"];
        
        //高度
        CGFloat height = [TheGXTemplateEngine sizeWithTemplateItem:item measureSize:CGSizeMake(_itemWidth, NAN) data:data].height;
        [self.heights addObject:@(height)];
        
        //data
        [self.dataArray addObject:data];
    }
}

#pragma mark - lazy load

- (UICollectionView *)collectionView{
    if (!_collectionView) {
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        flowLayout.minimumInteritemSpacing = 10;

        _collectionView = [[UICollectionView alloc] initWithFrame:self.view.bounds collectionViewLayout:flowLayout];
        _collectionView.backgroundColor = [UIColor whiteColor];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        [_collectionView registerClass:RecycleTemplateListCell.class forCellWithReuseIdentifier:@"cell"];
    }
    return _collectionView;
}

- (NSMutableArray *)dataArray{
    if (!_dataArray) {
        _dataArray = [NSMutableArray array];
    }
    return _dataArray;
}

- (NSMutableArray *)heights{
    if (!_heights) {
        _heights = [NSMutableArray array];
    }
    return _heights;
}


@end
