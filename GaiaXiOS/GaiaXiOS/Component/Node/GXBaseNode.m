//
//  GXBaseNode.m
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

#import "GXBaseNode.h"
#import "GXCornerRadiusHelper.h"
#import "GXGradientHelper.h"
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "NSArray+GX.h"
#import "UIColor+GX.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXBaseNode (){
    //当前颜色
    UIColor *_currentBgColor;
}

//多圆角
@property (nonatomic, assign) CGFloat topLeftRadius;
@property (nonatomic, assign) CGFloat topRightRadius;
@property (nonatomic, assign) CGFloat bottomLeftRadius;
@property (nonatomic, assign) CGFloat bottomRightRadius;

@end


@implementation GXBaseNode


#pragma mark -

//修改样式属性，不影响布局
- (void)updateNormalStyle:(NSDictionary *)styleInfo isMark:(BOOL)isMark{
    UIView *view = self.associatedView;
    //透明度
    NSString *opacity = [styleInfo gx_stringForKey:@"opacity"];
    if (opacity.length) {
        self.opacity = [opacity floatValue];
        view.alpha = self.opacity;
    }
    
    //普通背景色
    NSString *backgroundColor = [styleInfo gx_stringForKey:@"background-color"];
    if (backgroundColor.length) {
        if ([backgroundColor isEqualToString:@"null"]) {
            _currentBgColor = self.backgroundColor;
        } else {
            _currentBgColor = [UIColor gx_colorWithString:backgroundColor];
        }
        //设置颜色
        [self setupNormalBackground:view];
    }
    
    //渐变色背景
    if (self.isSupportGradientBgColor) {
        NSString *backgroundImage = [styleInfo gx_stringForKey:@"background-image"];
        if (backgroundImage.length) {
            backgroundImage = [backgroundImage stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
            if ([backgroundImage hasPrefix:@"linear-gradient("] && [backgroundImage hasSuffix:@")"]) {
                self.linearGradient = backgroundImage;
                [self setupGradientBackground:view];
            } else {
                self.linearGradient = nil;
                view.gxLinearGradient = nil;
                if ([backgroundImage isEqualToString:@"null"]) {
                    _currentBgColor = self.backgroundColor;
                } else {
                    _currentBgColor = [UIColor gx_colorWithString:backgroundColor];
                }
                //清除layer
                [view gx_clearGradientBackground];
                //设置背景色
                [self setupNormalBackground:view];
            }
        }
    }
    
}

//修改布局属性，影响布局
- (BOOL)updateLayoutStyle:(NSDictionary *)styleInfo{
    if (![GXUtils isValidDictionary:styleInfo]) {
        return NO;
    }
    
    //设置布局标识
    BOOL isMark = NO;
    
    //更新display
    NSString *displayStr = [styleInfo gx_stringForKey:@"display"];
    if (displayStr) {
        // display发生变化，才更改布局
        Display display = [displayStr isEqualToString:@"none"] ? DisplayNone : DisplayFlex;
        if (display != self.style.styleModel.display) {
            self.style.styleModel.display = display;
            isMark = YES;
        }
    }
    
    //更新flex-grow
    NSString *flexGrowStr = [styleInfo gx_stringForKey:@"flex-grow"];
    if (flexGrowStr) {
        CGFloat flexGrow = [flexGrowStr floatValue];
        if (flexGrow != self.style.styleModel.flexGrow) {
            self.style.styleModel.flexGrow = flexGrow;
            isMark = YES;
        }
    }
    
    //更新justify-content
    NSString *justifyContent = [styleInfo gx_stringForKey:@"justify-content"];
    if (justifyContent) {
        JustifyContent jc = [GXStyleHelper convertJustifyContent:justifyContent];
        if (self.style.styleModel.justifyContent != jc) {
            self.style.styleModel.justifyContent = jc;
            isMark = YES;
        }
    }
    
    //更新aspect-ratio
    NSString *aspectRatioStr = [styleInfo gx_stringForKey:@"aspect-ratio"];
    if (aspectRatioStr) {
        CGFloat aspectRatio = [aspectRatioStr isEqualToString:@"null"] ? NAN : [aspectRatioStr floatValue];
        if (aspectRatio != self.style.styleModel.aspectRatio) {
            self.style.styleModel.aspectRatio = aspectRatio;
            isMark = YES;
        }
    }
    
    //更新width
    NSString *widthStr = [styleInfo gx_stringForKey:@"width"];
    if (widthStr) {
        StretchStyleDimension width = [GXStyleHelper convertAutoValue:widthStr];
        StretchStyleDimension height = self.style.styleModel.size.height;
        StretchStyleSize newSize = (StretchStyleSize){
            .width = width,
            .height = height
        };
        self.style.styleModel.size = newSize;
        isMark = YES;
    }
    
    //更新height
    NSString *heightStr = [styleInfo gx_stringForKey:@"height"];
    if (heightStr) {
        StretchStyleDimension height = [GXStyleHelper convertAutoValue:heightStr];
        StretchStyleDimension width = self.style.styleModel.size.width;
        StretchStyleSize newSize = (StretchStyleSize){
            .width = width,
            .height = height
        };
        self.style.styleModel.size = newSize;
        isMark = YES;
    }
    
    //更新max-width/max-height
    NSString *maxWidthStr = [styleInfo gx_stringForKey:@"max-width"];
    NSString *maxHeightStr = [styleInfo gx_stringForKey:@"max-height"];
    if (maxWidthStr || maxHeightStr) {
        StretchStyleDimension maxwidth = self.style.styleModel.maxSize.width;
        if (maxWidthStr) {
            maxwidth = [GXStyleHelper convertAutoValue:maxWidthStr];
        }
        StretchStyleDimension maxHeight = self.style.styleModel.maxSize.height;
        if (maxHeightStr) {
            maxHeight = [GXStyleHelper convertAutoValue:maxHeightStr];
        }
        StretchStyleSize newSize = (StretchStyleSize){
            .width = maxwidth,
            .height = maxHeight
        };
        self.style.styleModel.maxSize = newSize;
        isMark = YES;
    }
    
    //更新min-width/min-height
    NSString *minWidthStr = [styleInfo gx_stringForKey:@"min-width"];
    NSString *minHeightStr = [styleInfo gx_stringForKey:@"min-height"];
    if (minWidthStr || minHeightStr) {
        StretchStyleDimension minwidth = self.style.styleModel.minSize.width;
        if (minWidthStr) {
            minwidth = [GXStyleHelper convertAutoValue:minWidthStr];
        }
        StretchStyleDimension minHeight = self.style.styleModel.minSize.height;
        if (minHeightStr) {
            minHeight = [GXStyleHelper convertAutoValue:minHeightStr];
        }
        StretchStyleSize newSize = (StretchStyleSize){
            .width = minwidth,
            .height = minHeight
        };
        self.style.styleModel.minSize = newSize;
        self.style.styleModel.recordMinSize = newSize;
        isMark = YES;
    }
    
    //更新top/left/right/bottom
    NSString *topStr = [styleInfo gx_stringForKey:@"top"];
    NSString *leftStr = [styleInfo gx_stringForKey:@"left"];
    NSString *rightStr = [styleInfo gx_stringForKey:@"right"];
    NSString *bottomStr = [styleInfo gx_stringForKey:@"bottom"];
    if (leftStr || topStr || rightStr || bottomStr) {
        //返回margin
        StretchStyleRect position = self.style.styleModel.position;
        if (topStr){
            StretchStyleDimension top = [GXStyleHelper convertValue:topStr];
            position.top = top;
        }
        if (leftStr){
            StretchStyleDimension left = [GXStyleHelper convertValue:leftStr];
            position.left = left;
        }
        if (rightStr){
            StretchStyleDimension right = [GXStyleHelper convertValue:rightStr];
            position.right = right;
        }
        if (bottomStr){
            StretchStyleDimension bottom = [GXStyleHelper convertValue:bottomStr];
            position.bottom = bottom;
        }
        self.style.styleModel.position = position;
        
        //更新标记
        isMark = YES;
    }
    
    //更新margin-top/margin-left/margin-right/margin-bottom
    NSString *marginTopStr = [styleInfo gx_stringForKey:@"margin-top"];
    NSString *marginLeftStr = [styleInfo gx_stringForKey:@"margin-left"];
    NSString *marginRightStr = [styleInfo gx_stringForKey:@"margin-right"];
    NSString *marginBottomStr = [styleInfo gx_stringForKey:@"margin-bottom"];
    if (marginTopStr || marginLeftStr || marginRightStr || marginBottomStr) {
        //返回margin
        StretchStyleRect margin = self.style.styleModel.margin;
        if (marginTopStr){
            StretchStyleDimension marginTop = [GXStyleHelper convertValue:marginTopStr];
            margin.top = marginTop;
        }
        if (marginLeftStr){
            StretchStyleDimension marginLeft = [GXStyleHelper convertValue:marginLeftStr];
            margin.left = marginLeft;
        }
        if (marginRightStr){
            StretchStyleDimension marginRight = [GXStyleHelper convertValue:marginRightStr];
            margin.right = marginRight;
        }
        if (marginBottomStr){
            StretchStyleDimension marginBottom = [GXStyleHelper convertValue:marginBottomStr];
            margin.bottom = marginBottom;
        }
        self.style.styleModel.margin = margin;
        
        //更新标记
        isMark = YES;
    }
    
    //更新padding-top/padding-left/padding-right/padding-bottom
    NSString *paddingTopStr = [styleInfo gx_stringForKey:@"padding-top"];
    NSString *paddingLeftStr = [styleInfo gx_stringForKey:@"padding-left"];
    NSString *paddingRightStr = [styleInfo gx_stringForKey:@"padding-right"];
    NSString *paddingBottomStr = [styleInfo gx_stringForKey:@"padding-bottom"];
    if (paddingTopStr || paddingLeftStr || paddingRightStr || paddingBottomStr) {
        //返回padding
        StretchStyleRect padding = self.style.styleModel.padding;
        if (paddingTopStr){
            StretchStyleDimension paddingTop = [GXStyleHelper convertValue:paddingTopStr];
            padding.top = paddingTop;
        }
        if (paddingLeftStr){
            StretchStyleDimension paddingLeft = [GXStyleHelper convertValue:paddingLeftStr];
            padding.left = paddingLeft;
        }
        if (paddingRightStr){
            StretchStyleDimension paddingRight = [GXStyleHelper convertValue:paddingRightStr];
            padding.right = paddingRight;
        }
        if (paddingBottomStr){
            StretchStyleDimension paddingBottom = [GXStyleHelper convertValue:paddingBottomStr];
            padding.bottom = paddingBottom;
        }
        self.style.styleModel.padding = padding;
        
        //更新标记
        isMark = YES;
    }
    
    //圆角是否变化
    BOOL isBorderChanged = NO;
    //边框颜色
    NSString *borderColor = [styleInfo gx_stringForKey:@"border-color"];
    if (borderColor) {
        isBorderChanged = YES;
        self.borderColor = [UIColor gx_colorWithString:borderColor];
    }
    //边框宽度
    NSString *borderWidth = [styleInfo gx_stringForKey:@"border-width"];
    if (borderWidth) {
        isBorderChanged = YES;
        self.borderWidth = [GXStyleHelper converSimpletValue:borderWidth];
    }
    
    //获取view
    UIView *view = self.associatedView;
    //全圆角
    NSString *cornerRadius = [styleInfo gx_stringForKey:@"border-radius"];
    //单圆角
    NSString *topLeft = [styleInfo gx_stringForKey:@"border-top-left-radius"];
    NSString *topRight = [styleInfo gx_stringForKey:@"border-top-right-radius"];
    NSString *bottomLeft = [styleInfo gx_stringForKey:@"border-bottom-left-radius"];
    NSString *bottomRight = [styleInfo gx_stringForKey:@"border-bottom-right-radius"];
    if (topLeft || topRight || bottomLeft || bottomRight) {
        if (topLeft) {
            self.topLeftRadius = [GXStyleHelper converSimpletValue:topLeft];
        }
        if (topRight) {
            self.topRightRadius = [GXStyleHelper converSimpletValue:topRight];
        }
        if (bottomLeft) {
            self.bottomLeftRadius = [GXStyleHelper converSimpletValue:bottomLeft];
        }
        if (bottomRight) {
            self.bottomRightRadius = [GXStyleHelper converSimpletValue:bottomRight];
        }
        self.cornerRadius = 0.f;
        
        //更新标记
        if (!isMark) {
            [self setupCornerRadius:view];
        }
        
    } else if (cornerRadius) {
        //设置全圆角属性
        self.cornerRadius = [GXStyleHelper converSimpletValue:cornerRadius];
        self.topLeftRadius = 0.f;
        self.topRightRadius = 0.f;
        self.bottomLeftRadius = 0.f;
        self.bottomRightRadius = 0.f;
        if (!isMark) {
            [self setupCornerRadius:view];
        }
        
    } else if (isBorderChanged && !isMark){
        //只更新表框宽度和颜色
        [self setupCornerRadius:view];
        
    } else {
        //不做处理
    }
    
    return isMark;
}


#pragma mark - 设置基础方法实现

//无障碍色值
- (void)setupAccessibilityInfo:(NSDictionary *)info{
    //无障碍内容
    NSString *accessibilityLabel = [info gx_stringForKey:@"accessibilityDesc"];
    if (accessibilityLabel.length) {
        self.associatedView.isAccessibilityElement = YES;
        self.associatedView.accessibilityLabel = accessibilityLabel;
    } else {
        self.associatedView.isAccessibilityElement = NO;
        self.associatedView.accessibilityLabel = nil;
    }
    
    //accessibilityTraits
    NSString *accessibilityTraits = [info gx_stringForKey:@"accessibilityTraits"];
    if (accessibilityTraits.length) {//none/text/image/button
        if ([accessibilityTraits isEqualToString:@"button"]) {
            self.associatedView.accessibilityTraits = UIAccessibilityTraitButton;
        } else if ([accessibilityTraits isEqualToString:@"image"]){
            self.associatedView.accessibilityTraits = UIAccessibilityTraitImage;
        } else if ([accessibilityTraits isEqualToString:@"none"]){
            self.associatedView.accessibilityTraits = UIAccessibilityTraitNone;
        }
    }
    
    //是否开启无障碍
    NSString *accessibilityEnableStr = [info gx_stringForKey:@"accessibilityEnable"];
    if (accessibilityEnableStr.length) {
        BOOL accessibilityEnable = [accessibilityEnableStr boolValue];
        self.associatedView.isAccessibilityElement = accessibilityEnable;
    }
}

//渐变色背景
- (void)setupGradientBackground:(UIView *)view{
    if (view == nil || self.linearGradient == nil ||
        CGSizeEqualToSize(view.bounds.size, CGSizeZero) ||
        ([self.linearGradient isEqualToString:view.gxLinearGradient] &&
         CGSizeEqualToSize(view.gxGradientLayer.bounds.size, view.bounds.size))) {
        return;
    }
    
    //设置属性
    view.gxLinearGradient = self.linearGradient;
    
    //创建渐变
    if ([self.linearGradient hasPrefix:@"linear-gradient("] && [self.linearGradient hasSuffix:@")"]) {
        //生成渐变的map
        NSDictionary *dict = [GXGradientHelper parserLinearGradient:self.linearGradient];
        //设置渐变背景色
        [view gx_setBackgroundGradient:dict];
    }
}

//普通背景
- (void)setupNormalBackground:(UIView *)view{
    view.backgroundColor = _currentBgColor;
}

//设置圆角属性
- (void)setupCornerRadius:(UIView *)view{
    if (view == nil ) {
        return;
    }
    
    //宽高不能为0
    CGSize size = view.frame.size;
    if (size.width == 0 || size.height == 0) {
        return;
    }
    
    //判断全圆角/单圆角
    if (self.topLeftRadius || self.topRightRadius || self.bottomLeftRadius || self.bottomRightRadius) {
        //重置全圆角
        view.layer.cornerRadius = 0.f;
        view.layer.borderWidth = 0.f;
        view.layer.borderColor = nil;
        
        //获取不同圆角
        CGFloat borderWidth = self.borderWidth;
        UIColor *borderColor = self.borderColor;
        GXCornerRadius radius = GXCornerRadiusMake(self.topLeftRadius, self.topRightRadius, self.bottomLeftRadius, self.bottomRightRadius);
        [view gx_setCornerRadius:radius borderWidth:borderWidth borderColor:borderColor];
        
    } else {
        //移除mask
        CALayer *mask = view.layer.mask;
        if (mask) {
            mask = nil;
        }
        
        //移除borderLayer
        CAShapeLayer *borderLayer = view.gxBorderLayer;
        if (borderLayer) {
            [borderLayer removeFromSuperlayer];
            view.gxBorderLayer = nil;
        }
        
        //设置圆角 & 表框
        view.layer.borderColor = self.borderColor.CGColor;
        view.layer.borderWidth = self.borderWidth;
        view.layer.cornerRadius = self.cornerRadius;
    }
    
}

//设置阴影
- (void)setupShadow:(UIView *)view{
    if (!self.boxShadow.length || view == nil) {
        return;
    }
    
    //10px 10px 5px 5px black;
    NSString *boxShadow = self.boxShadow;
    NSArray *array = [boxShadow componentsSeparatedByString:@" "];
    NSInteger count = array.count;
    if (count == 5) {
        NSMutableArray *tmpArray = [NSMutableArray array];
        //遍历属性
        for (int i = 0; i < count; i++) {
            NSString *obj = [array objectAtIndex:i];
            if (obj.length > 0) {
                [tmpArray addObject:obj];
            }
        }
        //处理引用
        if (tmpArray.count == 5) {
            //x偏移量
            CGFloat x = [GXStyleHelper converSimpletValue:tmpArray[0]];
            //y偏移量
            CGFloat y = [GXStyleHelper converSimpletValue:tmpArray[1]];
            //阴影模糊半径
            CGFloat radius = [GXStyleHelper converSimpletValue:tmpArray[2]];
            //阴影颜色
            UIColor *shadowColor = [UIColor gx_colorWithString:tmpArray[4]];
            //设置阴影
            [view gx_setShadow:shadowColor offset:CGSizeMake(x, y) radius:radius];
        }
        
    }
    
}


#pragma mark - 属性解析

- (void)configureStyleInfo:(NSDictionary *)styleJson{
    [super configureStyleInfo:styleJson];
    
    //是否需要扁平化
    BOOL isNeedFlat = YES;
    
    //clickToBounds
    NSString *clickToBounds = [styleJson gx_stringForKey:@"overflow"];
    if (!clickToBounds || (clickToBounds && [clickToBounds isEqualToString:@"hidden"])) {
        self.clipsToBounds = YES;
    } else {
        self.clipsToBounds = NO;
    }
    
    //opacity
    NSString *opacity = [styleJson gx_stringForKey:@"opacity"];
    if (opacity.length) {
        isNeedFlat = NO;
        self.opacity = [opacity floatValue];
    } else {
        self.opacity = 1.f;
    }
    
    //borderWidth
    NSString *borderWidth = [styleJson gx_stringForKey:@"border-width"];
    if (borderWidth.length) {
        isNeedFlat = NO;
        self.borderWidth = [GXStyleHelper converSimpletValue:borderWidth];
    } else {
        self.borderWidth = 0.f;
    }
    
    //borderColor
    NSString *borderColor = [styleJson gx_stringForKey:@"border-color"];
    if (borderColor.length) {
        isNeedFlat = NO;
        self.borderColor = [UIColor gx_colorWithString:borderColor];
    } else {
        self.borderColor = [UIColor clearColor];
    }
    
    //backgroundColor
    NSString *backgroundColor = [styleJson gx_stringForKey:@"background-color"];
    if (backgroundColor.length) {
        isNeedFlat = NO;
        self.backgroundColor = [UIColor gx_colorWithString:backgroundColor];
    } else {
        self.backgroundColor = [UIColor clearColor];
    }
    //设置当前颜色
    _currentBgColor = self.backgroundColor;
    
    //boxShadow
    NSString *boxShadow = [styleJson gx_stringForKey:@"box-shadow"];
    if (boxShadow.length) {
        self.boxShadow = boxShadow;
    }
    
    // 渐变背景
    NSString *backgroundImage = [styleJson gx_stringForKey:@"background-image"];
    if (backgroundImage.length) {
        isNeedFlat = NO;
        self.linearGradient = [backgroundImage stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    }
    
    //非全圆角 + 高优
    BOOL isSingleRadius = NO;
    NSString *topLeft = [styleJson gx_stringForKey:@"border-top-left-radius"];
    if (topLeft) {
        isNeedFlat = NO;
        isSingleRadius = YES;
        self.topLeftRadius = [GXStyleHelper converSimpletValue:topLeft];
    }
    NSString *topRight = [styleJson gx_stringForKey:@"border-top-right-radius"];
    if (topRight) {
        isNeedFlat = NO;
        isSingleRadius = YES;
        self.topRightRadius = [GXStyleHelper converSimpletValue:topRight];
    }
    NSString *bottomLeft = [styleJson gx_stringForKey:@"border-bottom-left-radius"];
    if (bottomLeft) {
        isNeedFlat = NO;
        isSingleRadius = YES;
        self.bottomLeftRadius = [GXStyleHelper converSimpletValue:bottomLeft];
    }
    NSString *bottomRight = [styleJson gx_stringForKey:@"border-bottom-right-radius"];
    if (bottomRight) {
        isNeedFlat = NO;
        isSingleRadius = YES;
        self.bottomRightRadius = [GXStyleHelper converSimpletValue:bottomRight];
    }
    
    //全圆角 + 低优
    if (!isSingleRadius) {
        NSString *borderRadius = [styleJson gx_stringForKey:@"border-radius"];
        if (borderRadius.length) {
            isNeedFlat = NO;
            self.cornerRadius = [GXStyleHelper converSimpletValue:borderRadius];
        } else {
            self.cornerRadius = 0.f;
        }
    }
    
    //设置isFlat
    NSString *className = NSStringFromClass(self.class);
    if ([className isEqualToString:@"GXViewNode"]) {
        // 无属性图层，并且不需要动态绑定数据，也不需要动画, 则认为是可以优化的图层
        self.isFlat = (isNeedFlat && ![self shouldBind]);
    }
    
}


#pragma mark - 属性设置

- (void)setBackgroundColor:(UIColor *)backgroundColor{
    if (_backgroundColor != backgroundColor) {
        _backgroundColor = backgroundColor;
    }
}

- (void)setLinearGradient:(NSString *)linearGradient{
    if (_linearGradient != linearGradient) {
        _linearGradient = linearGradient;
    }
}

- (void)setBorderColor:(UIColor *)borderColor{
    if (_borderColor != borderColor) {
        _borderColor = borderColor;
    }
}

- (void)setBorderWidth:(CGFloat)borderWidth{
    if (_borderWidth != borderWidth) {
        _borderWidth = borderWidth;
    }
}

- (void)setCornerRadius:(CGFloat)cornerRadius{
    if (_cornerRadius != cornerRadius) {
        _cornerRadius = cornerRadius;
    }
}

- (void)setClipsToBounds:(BOOL)clipsToBounds{
    if (_clipsToBounds != clipsToBounds) {
        _clipsToBounds = clipsToBounds;
    }
}

- (void)setBoxShadow:(NSString *)boxShadow{
    if (_boxShadow != boxShadow) {
        _boxShadow = boxShadow;
    }
}

- (void)setOpacity:(CGFloat)opacity{
    if (_opacity != opacity) {
        _opacity = opacity;
    }
}


@end
