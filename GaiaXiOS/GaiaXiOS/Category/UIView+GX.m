//
//  UIView+GX.m
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

#import "UIView+GX.h"
#import "GXNode.h"
#import "GXEvent.h"
#import "NSArray+GX.h"
#import <objc/runtime.h>
#import "GXEventManager.h"
#import "GXGradientHelper.h"
#import "GXTemplateContext.h"
#import "GXCornerRadiusHelper.h"

//基础属性
static const void *kGXNodeKey = &kGXNodeKey;
static const void *kGXEventTapKey = &kGXEventTapKey;
static const void *kGXEventLongpressKey = &kGXEventLongpressKey;
static const void *kGXTrackKey = &kGXTrackKey;
static const void *kGXBizIdKey = &kGXBizIdKey;
static const void *kGXNodeIdKey = &kGXNodeIdKey;
static const void *kGXTemplateIdKey = &kGXTemplateIdKey;
static const void *kGXTemplateVersionKey = &kGXTemplateVersionKey;
//圆角
static const void *kGXBorderLayerKey = &kGXBorderLayerKey;
static const void *kGXCornerRadiusKey = &kGXCornerRadiusKey;

//渐变View
static const void *kGXGradientViewKey = &kGXGradientViewKey;
//渐变Image
static const void *kGXGradientImageKey = &kGXGradientImageKey;
//渐变Layer
static const void *kGXGradientLayerKey = &kGXGradientLayerKey;
//渐变string
static const void *kGXLinearGradientKey = &kGXLinearGradientKey;
//阴影
static const void *kGXShadowLayerKey = &kGXShadowLayerKey;
//毛玻璃
static const void *kGXEffectViewKey = &kGXEffectViewKey;


@implementation UIView (GX)

//gxNode
- (GXNode *)gxNode{
    return objc_getAssociatedObject(self, kGXNodeKey);
}

- (void)setGxNode:(GXNode *)gxNode{
    objc_setAssociatedObject(self, &kGXNodeKey, gxNode, OBJC_ASSOCIATION_RETAIN);
}

//gxTrack
- (GXTrack *)gxTrack{
    return objc_getAssociatedObject(self, kGXTrackKey);
}

- (void)setGxTrack:(GXTrack *)gxTrack{
    objc_setAssociatedObject(self, &kGXTrackKey, gxTrack, OBJC_ASSOCIATION_RETAIN);
}

//bizId
- (NSString *)gxBizId{
    return objc_getAssociatedObject(self, &kGXBizIdKey);
}

- (void)setGxBizId:(NSString *)gxBizId{
    objc_setAssociatedObject(self, &kGXBizIdKey, gxBizId, OBJC_ASSOCIATION_RETAIN);
}

//gxNodeId
- (NSString *)gxNodeId{
    return objc_getAssociatedObject(self, &kGXNodeIdKey);
}

- (void)setGxNodeId:(NSString *)gxNodeId{
    objc_setAssociatedObject(self, &kGXNodeIdKey, gxNodeId, OBJC_ASSOCIATION_RETAIN);
}

//template_id
- (NSString *)gxTemplateId{
    return objc_getAssociatedObject(self, &kGXTemplateIdKey);
}

- (void)setGxTemplateId:(NSString *)gxTemplateId{
    objc_setAssociatedObject(self, &kGXTemplateIdKey, gxTemplateId, OBJC_ASSOCIATION_RETAIN);
}

//template_version
- (NSString *)gxTemplateVersion{
    return objc_getAssociatedObject(self, &kGXTemplateVersionKey);
}

- (void)setGxTemplateVersion:(NSString *)gxTemplateVersion{
    objc_setAssociatedObject(self, &kGXTemplateVersionKey, gxTemplateVersion, OBJC_ASSOCIATION_RETAIN);
}

// gxEvents
- (GXEvent *)gxEvent {
    return objc_getAssociatedObject(self, &kGXEventTapKey);
}

- (void)setGxEvent:(GXEvent *)gxEvent {
    objc_setAssociatedObject(self, &kGXEventTapKey, gxEvent, OBJC_ASSOCIATION_RETAIN);
}

- (GXEvent *)gxLpEvent{
    return objc_getAssociatedObject(self, &kGXEventLongpressKey);
}

- (void)setGxLpEvent:(GXEvent *)gxLpEvent {
    objc_setAssociatedObject(self, &kGXEventLongpressKey, gxLpEvent, OBJC_ASSOCIATION_RETAIN);
}

// 设置事件数据
- (GXEvent *)gx_eventWithType:(GXEventType)eventType {
    switch (eventType) {
        case GXEventTypeTap:
            return self.gxEvent;
        case GXEventTypeLongPress:
            return self.gxLpEvent;
        default:
            return nil;
    }
}

- (void)gx_setEvent:(GXEvent *)gxEvent withType:(GXEventType)eventType {
    switch (eventType) {
        case GXEventTypeTap:
            self.gxEvent = gxEvent;
            break;
        case GXEventTypeLongPress:
            self.gxLpEvent = gxEvent;
            break;
        default:
            break;
    }
}


#pragma mark - method

- (UIView *)gx_rootView{
    GXNode *node = [self.gxNode rootNode];
    return node.associatedView;
}

- (void)gx_removeAllSubviews{
    [self.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
}

- (UIImage *)gx_snapshotImage{
    CGSize size = self.bounds.size;
    UIGraphicsImageRenderer *render = [[UIGraphicsImageRenderer alloc] initWithSize:size];
    UIImage *image = [render imageWithActions:^(UIGraphicsImageRendererContext * _Nonnull rendererContext) {
        [self.layer renderInContext:rendererContext.CGContext];
    }];
    return image;
}

- (UIViewController *)gx_viewController{
    for (UIView *view = self; view; view = view.superview) {
        UIResponder *nextResponder = [view nextResponder];
        if ([nextResponder isKindOfClass:[UIViewController class]]) {
            return (UIViewController *)nextResponder;
        }
    }
    return nil;
}

//处理手势
- (void)gx_handleGesture:(UIGestureRecognizer *)gesture{
    //基础信息
    GXNode *node = self.gxNode;
    //手势类型判断
    if ([gesture isKindOfClass:[UILongPressGestureRecognizer class]]) {
        //长按事件
        if (gesture.state == UIGestureRecognizerStateBegan) {
            GXEvent *event = self.gxLpEvent;
            [TheGXEventManager fireEvent:event toNode:node];
        }
    } else {
        //点击事件
        GXEvent *event = self.gxEvent;
        [TheGXEventManager fireEvent:event toNode:node];
    }

    //点击埋点分发
    [node manualClickTrackEvent];
}


@end


@implementation UIView (GXGradient)

//linearGradient
- (NSString *)gxLinearGradient{
    return objc_getAssociatedObject(self, &kGXLinearGradientKey);
}

- (void)setGxLinearGradient:(NSString *)gxLinearGradient{
    objc_setAssociatedObject(self, &kGXLinearGradientKey, gxLinearGradient, OBJC_ASSOCIATION_RETAIN);
}


//gradientView
- (UIView *)gxGradientView{
    return objc_getAssociatedObject(self, &kGXGradientViewKey);
}

- (void)setGxGradientView:(UIView *)gxGradientView{
    objc_setAssociatedObject(self, &kGXGradientViewKey, gxGradientView, OBJC_ASSOCIATION_RETAIN);
}

//gradientImage
- (UIImage *)gxGradientImage{
    return objc_getAssociatedObject(self, &kGXGradientImageKey);
}

- (void)setGxGradientImage:(UIImage *)gxGradientImage {
    objc_setAssociatedObject(self, &kGXGradientImageKey, gxGradientImage, OBJC_ASSOCIATION_RETAIN);
}

//gradientLayer
- (CAGradientLayer *)gxGradientLayer{
    return objc_getAssociatedObject(self, &kGXGradientLayerKey);
}

- (void)setGxGradientLayer:(CAGradientLayer *)gxGradientLayer{
    objc_setAssociatedObject(self, &kGXGradientLayerKey, gxGradientLayer, OBJC_ASSOCIATION_RETAIN);
}

//设置渐变背景色
- (void)gx_setBackgroundGradient:(NSDictionary *)backgroundGradient{
    //重置渐变背景色
    [self gx_clearGradientBackground];
    
    //设置渐变layer
    CGRect bounds = self.bounds;
//    CAGradientLayer *layer = [GXGradientHelper creatGradientLayerWithParams:backgroundGradient bounds:bounds];
//    if (layer) {
//        self.gxGradientLayer = layer;
//        [self.layer insertSublayer:layer atIndex:0];
//    }
    
    UIView *view = [GXGradientHelper creatGradientViewWithParams:backgroundGradient bounds:bounds];
    if (view) {
        self.gxGradientView = view;
        //添加背景
        CAGradientLayer *layer = (CAGradientLayer *)view.layer;
        self.gxGradientLayer = layer;
        [self.layer insertSublayer:layer atIndex:0];
    }
}

//重置渐变背景色
- (void)gx_clearGradientBackground{
    if (self.gxGradientLayer) {
        [self.gxGradientLayer removeFromSuperlayer];
        self.gxGradientLayer = nil;
    }
}


@end



@implementation UIView (GXCornerRadius)

//CornerRadii
- (GXCornerRadius)gxCornerRadius{
    NSValue *value = objc_getAssociatedObject(self, &kGXCornerRadiusKey);
    GXCornerRadius radius = GXCornerRadiusMake(0, 0, 0, 0);
    if (value) {
        if (@available(iOS 11, *)) {
            [value getValue:&radius size:sizeof(radius)];
        } else {
            [value getValue:&radius];
        }
    }
    return radius;
}

- (void)setGxCornerRadius:(GXCornerRadius)gxCornerRadius{
    NSValue *value = [NSValue value:&gxCornerRadius withObjCType:@encode(GXCornerRadius)];
    objc_setAssociatedObject(self, &kGXCornerRadiusKey, value, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

//BorderLayer
- (CAShapeLayer *)gxBorderLayer{
    CAShapeLayer *layer = objc_getAssociatedObject(self, &kGXBorderLayerKey);
    return layer;
}

- (void)setGxBorderLayer:(CAShapeLayer *)gxBorderLayer{
    objc_setAssociatedObject(self, &kGXBorderLayerKey, gxBorderLayer, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

//绘制圆角边框
- (void)gx_setCornerRadius:(GXCornerRadius)radius borderWidth:(CGFloat)borderWidth borderColor:(UIColor *)borderColor{
    //设置圆角layer
    [GXCornerRadiusHelper setCornerRadius:radius borderWidth:borderWidth borderColor:borderColor targetView:self];
}

@end


#pragma mark - GaiaXShadow

@implementation UIView (GXShadow)

//阴影
- (void)setShadowLayer:(CAShapeLayer *)shadowLayer{
    objc_setAssociatedObject(self, &kGXShadowLayerKey, shadowLayer, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (CAShapeLayer *)shadowLayer{
    CAShapeLayer *layer = objc_getAssociatedObject(self, &kGXShadowLayerKey);
    return layer;
}

//简单设置阴影
- (void)gx_setShadow:(UIColor*)color offset:(CGSize)offset radius:(CGFloat)radius{
    //内部阴影
    if (self.superview != nil) {
        //shadowLayer
        CAShapeLayer *shadowLayer = self.shadowLayer;
        if (shadowLayer == nil) {
            shadowLayer = [CAShapeLayer layer];
            shadowLayer.fillRule = kCAFillRuleEvenOdd;
            shadowLayer.shadowOpacity = 1;
            self.shadowLayer = shadowLayer;
        }
        
        //获取path
        CGPathRef path = nil;
        if (self.layer.mask) {
            path = [((CAShapeLayer *)self.layer.mask) path];
        } else {
            path = [UIBezierPath bezierPathWithRoundedRect:self.bounds cornerRadius:self.layer.cornerRadius].CGPath;
        }
        shadowLayer.fillColor = color.CGColor;
        shadowLayer.frame = self.frame;
        shadowLayer.path = path;
        shadowLayer.shadowRadius = radius;
        shadowLayer.shadowColor = color.CGColor;
        shadowLayer.shadowOffset = offset;
        
        //添加阴影
        [self.layer.superlayer insertSublayer:shadowLayer below:self.layer];
    }
}

@end


#pragma mark - BlurEffect

@implementation UIView (BlurEffect)

- (UIVisualEffectView *)blurView{
    return objc_getAssociatedObject(self, &kGXEffectViewKey);
}

- (void)setBlurView:(UIVisualEffectView *)blurView{
    objc_setAssociatedObject(self, &kGXEffectViewKey, blurView, OBJC_ASSOCIATION_RETAIN);
}

@end

