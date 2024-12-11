//
//  GaiaPagerLayout.h
//  GaiaXCore
//
//  Created by zhangjc on 2024/10/22.
//  Copyright © 2024 zhangjc. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, GXPagerLayoutType) {
    GXPagerLayoutTypeNormal,
    GXPagerLayoutTypeLinear,
    GXPagerLayoutTypeCoverflow,
};

@class GXPagerLayout;
@protocol GXPagerLayoutDelegate <NSObject>

// 初始化 layout attributes
- (void)pagerViewTransformLayout:(GXPagerLayout *)pagerViewTransformLayout initializeTransformAttributes:(UICollectionViewLayoutAttributes *)attributes;
// 使用 layout attributes
- (void)pagerViewTransformLayout:(GXPagerLayout *)pagerViewTransformLayout applyTransformToAttributes:(UICollectionViewLayoutAttributes *)attributes;

@end


@interface GXPagerLayoutConfig : NSObject

@property (nonatomic, assign) CGSize itemSize;
@property (nonatomic, assign) CGFloat itemSpacing;
@property (nonatomic, assign) UIEdgeInsets sectionInset;

@property (nonatomic, assign) GXPagerLayoutType layoutType;

@property (nonatomic, assign) CGFloat minimumScale; // sacle 默认 0.8
@property (nonatomic, assign) CGFloat minimumAlpha; // alpha 默认 1.0
@property (nonatomic, assign) CGFloat maximumAngle; // 默认 0.2

@property (nonatomic, assign) BOOL isInfiniteLoop;  // 是否循环滚动
@property (nonatomic, assign) CGFloat rateOfChange; // 缩放比例
@property (nonatomic, assign) BOOL adjustSpacingWhenScroling;

// item cell 是否垂直居中
@property (nonatomic, assign) BOOL itemVerticalCenter;

// item cell 横向居中
@property (nonatomic, assign) BOOL itemHorizontalCenter;

//item cell 横向居左
@property (nonatomic, assign) BOOL itemHorizontalLeft;

@property (nonatomic, assign, readonly) UIEdgeInsets onlyOneSectionInset;
@property (nonatomic, assign, readonly) UIEdgeInsets firstSectionInset;
@property (nonatomic, assign, readonly) UIEdgeInsets lastSectionInset;
@property (nonatomic, assign, readonly) UIEdgeInsets middleSectionInset;

@end


@interface GXPagerLayout : UICollectionViewFlowLayout

@property (nonatomic, strong) GXPagerLayoutConfig *layout;

@property (nonatomic, weak, nullable) id<GXPagerLayoutDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
