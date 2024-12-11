//
//  GaiaPagerView.h
//  GaiaXCore
//
//  Created by zhangjc on 2024/10/22.
//  Copyright © 2024 zhangjc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GXPagerLayout.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, GXPagerScrollDirection) {
    GXPagerScrollDirectionLeft,
    GXPagerScrollDirectionRight,
};

typedef struct {
    NSInteger index;
    NSInteger section;
}GXIndexSection;


@class GXPagerView;

@protocol GXPagerViewDataSource <NSObject>

// 元素的数量
- (NSInteger)numberOfItemsInPagerView:(GXPagerView *)pageView;
// 布局样式
- (GXPagerLayoutConfig *)layoutForPagerView:(GXPagerView *)pageView;
// 元素item（UICollectionCell）
- (__kindof UICollectionViewCell *)pagerView:(GXPagerView *)pagerView cellForItemAtIndex:(NSInteger)index;

@end

@protocol GXPagerViewDelegate <NSObject>

@optional

// pagerView 选中某个cell
- (void)pagerView:(GXPagerView *)pageView didSelectedItemCell:(__kindof UICollectionViewCell *)cell atIndex:(NSInteger)index;

// pagerView 滚动到新的index
- (void)pagerView:(GXPagerView *)pageView didScrollFromIndex:(NSInteger)fromIndex toIndex:(NSInteger)toIndex;
- (void)pagerView:(GXPagerView *)pageView didSelectedItemCell:(__kindof UICollectionViewCell *)cell atIndexSection:(GXIndexSection)indexSection;

// layout布局
- (void)pagerView:(GXPagerView *)pageView initializeTransformAttributes:(UICollectionViewLayoutAttributes *)attributes;

- (void)pagerView:(GXPagerView *)pageView applyTransformToAttributes:(UICollectionViewLayoutAttributes *)attributes;

// scrollViewDelegate
- (void)pagerViewDidScroll:(GXPagerView *)pageView;

- (void)pagerViewWillBeginDragging:(GXPagerView *)pageView;

- (void)pagerViewDidEndDragging:(GXPagerView *)pageView willDecelerate:(BOOL)decelerate;

- (void)pagerViewWillBeginDecelerating:(GXPagerView *)pageView;

- (void)pagerViewDidEndDecelerating:(GXPagerView *)pageView;

- (void)pagerViewWillBeginScrollingAnimation:(GXPagerView *)pageView;

- (void)pagerViewDidEndScrollingAnimation:(GXPagerView *)pageView;

@end


@interface GXPagerView : UIView

// 背景（自动等大小）
@property (nonatomic, strong, nullable) UIView *backgroundView;
// pagerView基于的UICollectionView
@property (nonatomic, weak, readonly) UICollectionView *collectionView;

//代理和数据源
@property (nonatomic, weak, nullable) id<GXPagerViewDataSource> dataSource;
@property (nonatomic, weak, nullable) id<GXPagerViewDelegate> delegate;

// pagerView的布局
@property (nonatomic, strong, readonly) GXPagerLayoutConfig *layout;

// 设置是否可以循环滚动
@property (nonatomic, assign) BOOL isInfiniteLoop;

// 自动滚动的时间间隔，isInfiniteLoop为YES时才有效
@property (nonatomic, assign) CGFloat autoScrollInterval;
@property (nonatomic, assign) BOOL reloadDataNeedResetIndex;

// 当前的index
@property (nonatomic, assign, readonly) NSInteger curIndex;
@property (nonatomic, assign, readonly) GXIndexSection indexSection;

// scrollview属性
@property (nonatomic, assign, readonly) CGPoint contentOffset;
@property (nonatomic, assign, readonly) BOOL decelerating;
@property (nonatomic, assign, readonly) BOOL tracking;
@property (nonatomic, assign, readonly) BOOL dragging;

// 控制倒数第二个不再整个Item滑动，嘻哈会员专用
@property (nonatomic, assign) BOOL isSecondToLast;

// 刷新数据, 注意: 会清空布局 & 重新调用代理方法
- (void)reloadData;

// 更新数据，不清布局
- (void)updateData;

// 只用来刷新layout，不涉及数据
- (void)setNeedUpdateLayout;

// 清空布局 & 重新获取新布局
- (void)setNeedClearLayout;

// 当前居中的cell
- (__kindof UICollectionViewCell * _Nullable)curIndexCell;

// 可见的cell
- (NSArray<__kindof UICollectionViewCell *> *_Nullable)visibleCells;


// 可见的index
- (NSArray *)visibleIndexs;

// 滚动到指定的index
- (void)scrollToItemAtIndex:(NSInteger)index animate:(BOOL)animate;
- (void)scrollToItemAtIndexSection:(GXIndexSection)indexSection animate:(BOOL)animate;

// 滚动到附近的cell
- (void)scrollToNearlyIndexAtDirection:(GXPagerScrollDirection)direction animate:(BOOL)animate;

// 获取某个滚动方向的index
- (GXIndexSection)nearlyIndexPathAtDirection:(GXPagerScrollDirection)direction;

// 注册cell
- (void)registerClass:(Class)Class forCellWithReuseIdentifier:(NSString *)identifier;

// 重用方法
- (__kindof UICollectionViewCell *)dequeueReusableCellWithReuseIdentifier:(NSString *)identifier forIndex:(NSInteger)index;

@end

NS_ASSUME_NONNULL_END
