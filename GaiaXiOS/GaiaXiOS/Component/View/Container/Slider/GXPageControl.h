//  GXPageControl.h
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

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXPageControl : UIView

///页数
@property (nonatomic) NSInteger pageCount;

///当前页码
@property (nonatomic) NSInteger currentPage;

///选中页宽度。默认8.0。
@property (nonatomic) NSInteger selectedPageWidth;

///未选中页大小。默认3.0。
@property (nonatomic) NSInteger unSelectedPageWidthHeight;

///间距。默认等于unSelectedPageWidthHeight。
@property (nonatomic) NSInteger pageGapWidth;

///选中页颜色。默认为[UIColor ykBlueColor]。
@property (nonatomic, strong) UIColor *selectedPageColor;

///未选中页颜色。默认为[UIColor colorWithWhite:1.0 alpha:0.6]。
@property (nonatomic, strong) UIColor *unselectedPageColor;

///是否隐藏圆角(需在设置pageCount之前设置该属性)
@property (nonatomic) BOOL hideCornerRadius;

- (void)setCurrentPage:(NSInteger)currentPage
              animated:(BOOL)animated;

- (void)switchSelectAnimated:(int)fromIndex
                     toIndex:(int)toIndex
                    progress:(CGFloat)progress;

@end

NS_ASSUME_NONNULL_END
