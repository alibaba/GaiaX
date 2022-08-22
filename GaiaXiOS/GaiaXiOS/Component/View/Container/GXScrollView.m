//
//  GXScrollView.m
//  GaiaXiOS
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GXScrollView.h"
#import "UIView+GX.h"
#import "GXNode.h"

@implementation GXScrollView

- (void)onAppear{
    if (self.gxNode) {
        [self.gxNode onAppear];
    }
}

- (void)onDisappear{
    if (self.gxNode) {
        [self.gxNode onDisappear];
    }
}

@end



#pragma mark - GXScrollViewCell

@implementation GXScrollViewCell


@end



#pragma mark - GXFlowLayout

@interface GXFlowLayout ()

@property (nonatomic, strong) NSMutableArray<UICollectionViewLayoutAttributes *> *itemAttributes;//存储各个cell的属性
@property (nonatomic) CGFloat offsetX;
@property (nonatomic) CGFloat offsetY;

@end


@implementation GXFlowLayout

- (void)prepareLayout{
    [super prepareLayout];
    
    //初始化array
    self.itemAttributes = [NSMutableArray array];
    
    //获取初始offset
    UIEdgeInsets sectionInset = self.sectionInset;
    CGFloat tmpOffsetX = sectionInset.left;
    CGFloat tmpOffsetY = sectionInset.top;
    
    //处理每个cell布局
    NSInteger count = [self.collectionView numberOfItemsInSection:0];
    for (int i = 0; i < count; i++) {
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:i inSection:0];
        //获取对应的indexPath的cell的属性
        UICollectionViewLayoutAttributes *attributes = [UICollectionViewLayoutAttributes layoutAttributesForCellWithIndexPath:indexPath];
        
        //获取对应的cell的大小
        CGSize itemSize = [self.delegate collectionView:self.collectionView layout:self sizeForItemAtIndexPath:indexPath];
        
        if (self.scrollDirection == UICollectionViewScrollDirectionHorizontal) {
            //给cell赋值新的frame
            CGFloat originalY = tmpOffsetY;
            CGFloat itemHeight = itemSize.height;
            if ([self.gravity isEqualToString:@"center"]) {
                originalY = ((self.containerHeight - (tmpOffsetY + sectionInset.bottom)) - itemHeight) / 2.0 + tmpOffsetY;
            } else if ([self.gravity isEqualToString:@"bottom"]){
                originalY = self.containerHeight - (itemHeight + sectionInset.bottom);
            }
            attributes.frame = CGRectMake(tmpOffsetX + self.minimumInteritemSpacing * i, originalY, itemSize.width, itemHeight);
            tmpOffsetX += itemSize.width;
            //计算offsetX
            if (i == count - 1) {
                self.offsetX = CGRectGetMaxX(attributes.frame) + sectionInset.right;
            }
            
        } else {
            //给cell赋值新的frame
            attributes.frame = CGRectMake(tmpOffsetX, tmpOffsetY + self.minimumInteritemSpacing * i, itemSize.width, itemSize.height);
            tmpOffsetY += itemSize.height;
            //计算offsetY
            if (i == count - 1) {
                self.offsetY = CGRectGetMaxY(attributes.frame) + sectionInset.bottom;
            }
        }
        
        [self.itemAttributes addObject:attributes];
    }
}

//返回所有的attributes
- (NSArray<UICollectionViewLayoutAttributes *> *)layoutAttributesForElementsInRect:(CGRect)rect{
    return self.itemAttributes;
}


// 在滚动的时候是否允许刷新布局
- (BOOL)shouldInvalidateLayoutForBoundsChange:(CGRect)newBounds{
    return YES;
}

// 计算collectionView滚动范围
- (CGSize)collectionViewContentSize{
    if (self.scrollDirection == UICollectionViewScrollDirectionHorizontal) {
        return CGSizeMake(self.offsetX, self.collectionView.bounds.size.height);
    } else {
        return CGSizeMake(self.collectionView.bounds.size.width, self.offsetY);
    }
}


@end
