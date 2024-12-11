//
//  UIView+GX.h
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
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXEvent.h"

@class  GXNode;
@class  GXEvent;
@class  GXTrack;

NS_ASSUME_NONNULL_BEGIN

@interface UIView (GX)

//view对应node
@property(nonatomic, strong)GXNode *gxNode;
//view对应的track
@property(nonatomic, strong)GXTrack *gxTrack;
//view对应的 tap event
@property(nonatomic, strong)GXEvent *gxEvent;
//view对应的 long press event
@property(nonatomic, strong)GXEvent *gxLpEvent;
//业务id
@property(nonatomic, strong) NSString *gxBizId;
//节点id
@property(nonatomic, strong) NSString *gxNodeId;
//模板id
@property(nonatomic, strong) NSString *gxTemplateId;
//模板version
@property(nonatomic, strong) NSString *gxTemplateVersion;

//获取当前view对应的rootView
- (UIView *)gx_rootView;

//移除所有子视图
- (void)gx_removeAllSubviews;

//截取view视图
- (UIImage *)gx_snapshotImage;

//获取view对应的vc
- (UIViewController *)gx_viewController;

//处理点击手势（tap、longpress）
- (void)gx_handleGesture:(UIGestureRecognizer *)gesture;

// 获取事件数据
- (GXEvent * _Nullable)gx_eventWithType:(GXEventType)eventType;

// 设置事件数据
- (void)gx_setEvent:(GXEvent * _Nullable)gxEvent withType:(GXEventType)eventType;


@end


@interface UIView (GXGradient)

//渐变原始值
@property(nonatomic, strong, nullable) NSString *gxLinearGradient;

//渐变的view
@property(nonatomic, strong, nullable) UIView *gxGradientView;
//渐变的图片 + text
@property(nonatomic, strong, nullable) UIImage *gxGradientImage;
//渐变的layer + image/view
@property(nonatomic, strong, nullable) CAGradientLayer *gxGradientLayer;


//设置渐变背景色
- (void)gx_setBackgroundGradient:(NSDictionary *)backgroundGradient;

//重置渐变背景色
- (void)gx_clearGradientBackground;


@end


@interface UIView (GXCornerRadius)

//圆角属性
@property(nonatomic) GXCornerRadius gxCornerRadius;
//边框layer
@property(nonatomic, strong, nullable) CAShapeLayer *gxBorderLayer;

//设置圆角 & 边框
- (void)gx_setCornerRadius:(GXCornerRadius)radius borderWidth:(CGFloat)borderWidth borderColor:(UIColor *)borderColor;


@end


@interface UIView (GXShadow)

//阴影
@property(nonatomic, strong, nullable) CAShapeLayer *shadowLayer;

//简单设置阴影
- (void)gx_setShadow:(UIColor*)color offset:(CGSize)offset radius:(CGFloat)radius;

@end


@interface UIView (BlurEffect)

//阴影
@property(nonatomic, strong, nullable) UIVisualEffectView *blurView;

@end


@interface UIView (GXLayout)

@property (nonatomic) CGFloat gx_left;        ///< Shortcut for frame.origin.x.
@property (nonatomic) CGFloat gx_top;         ///< Shortcut for frame.origin.y
@property (nonatomic) CGFloat gx_right;       ///< Shortcut for frame.origin.x + frame.size.width
@property (nonatomic) CGFloat gx_bottom;      ///< Shortcut for frame.origin.y + frame.size.height
@property (nonatomic) CGFloat gx_width;       ///< Shortcut for frame.size.width.
@property (nonatomic) CGFloat gx_height;      ///< Shortcut for frame.size.height.
@property (nonatomic) CGFloat gx_centerX;     ///< Shortcut for center.x
@property (nonatomic) CGFloat gx_centerY;     ///< Shortcut for center.y
@property (nonatomic) CGPoint gx_origin;      ///< Shortcut for frame.origin.
@property (nonatomic) CGSize  gx_size;        ///< Shortcut for frame.size.

@end



NS_ASSUME_NONNULL_END
