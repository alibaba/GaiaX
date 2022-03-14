//
//  GXCornerRadiusHelper.m
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

#import "GXCornerRadiusHelper.h"
#import "UIView+GX.h"

//Radius设置
GXCornerRadius GXCornerRadiusMake(CGFloat topLeft, CGFloat topRight, CGFloat bottomLeft, CGFloat bottomRight){
    return (GXCornerRadius){
        topLeft,
        topRight,
        bottomLeft,
        bottomRight,
    };
}

//Equal方法
NS_INLINE BOOL GXCornerRadiusEqualToRadius(GXCornerRadius radius1, GXCornerRadius radius2) {
    return (radius1.topLeft == radius2.topLeft) &&
           (radius1.topRight == radius2.topRight) &&
           (radius1.bottomLeft == radius2.bottomLeft) &&
           (radius1.bottomRight == radius2.bottomRight);
}

//绘制path路径
CGPathRef GXCreatePath(CGRect bounds, GXCornerRadius cornerRadius){
    //属性配置
    const CGFloat minX = CGRectGetMinX(bounds);
    const CGFloat minY = CGRectGetMinY(bounds);
    const CGFloat maxX = CGRectGetMaxX(bounds);
    const CGFloat maxY = CGRectGetMaxY(bounds);
    
    const CGFloat topLeftCenterX = minX + cornerRadius.topLeft;
    const CGFloat topLeftCenterY = minY + cornerRadius.topLeft;
    
    const CGFloat topRightCenterX = maxX - cornerRadius.topRight;
    const CGFloat topRightCenterY = minY + cornerRadius.topRight;
    
    const CGFloat bottomLeftCenterX = minX + cornerRadius.bottomLeft;
    const CGFloat bottomLeftCenterY = maxY - cornerRadius.bottomLeft;
    
    const CGFloat bottomRightCenterX = maxX - cornerRadius.bottomRight;
    const CGFloat bottomRightCenterY = maxY - cornerRadius.bottomRight;
    
    //开始绘制
    CGMutablePathRef path = CGPathCreateMutable();
    //topLeft
    CGPathAddArc(path, NULL, topLeftCenterX, topLeftCenterY,cornerRadius.topLeft, M_PI, 3 * M_PI_2, NO);
    //topRight
    CGPathAddArc(path, NULL, topRightCenterX , topRightCenterY, cornerRadius.topRight, 3 * M_PI_2, 0, NO);
    //bottomRight
    CGPathAddArc(path, NULL, bottomRightCenterX, bottomRightCenterY, cornerRadius.bottomRight, 0, M_PI_2, NO);
    //bottomLeft
    CGPathAddArc(path, NULL, bottomLeftCenterX, bottomLeftCenterY, cornerRadius.bottomLeft, M_PI_2, M_PI, NO);
    //结束绘制
    CGPathCloseSubpath(path);
    
    //返回path
    return path;
}


@implementation GXCornerRadiusHelper

//绘制圆角
+ (void)setCornerRadius:(GXCornerRadius)radii
            borderWidth:(CGFloat)borderWidth
            borderColor:(UIColor *)borderColor
             targetView:(UIView *)view {
    if (view == nil) {
        return;
    }
    
    //获取不同圆角
    CALayer *layer = view.layer;
    CGRect newBounds = layer.bounds;
    CGRect oldBounds = layer.mask.frame;
    //是否需要重新绘制
    GXCornerRadius oldRadii = view.gxCornerRadius;
    BOOL isRectCornerEqual = CGRectEqualToRect(oldBounds, newBounds) && GXCornerRadiusEqualToRadius(radii, oldRadii);
    if (!isRectCornerEqual) {
        BOOL iscornerRadiusZero = GXCornerRadiusEqualToRadius(radii, GXCornerRadiusMake(0, 0, 0, 0));
        if (iscornerRadiusZero) {
            //圆角为0进行移除
            if (layer.mask) {
                layer.mask = nil;
                view.gxCornerRadius = radii;
            }
        } else {
            //创建圆角
            CGPathRef path = GXCreatePath(newBounds, radii);
            CAShapeLayer *maskLayer = [CAShapeLayer layer];
            maskLayer.path = path;
            layer.mask = maskLayer;
            view.gxCornerRadius = radii;
            CGPathRelease(path);
        }
    }
    
    //边框 & 颜色
    CAShapeLayer *borderLayer = view.gxBorderLayer;
    if (borderWidth && borderColor) {
        //判断
        if (borderLayer &&
            isRectCornerEqual &&
            fabs(borderLayer.lineWidth - borderWidth) &&
            CGColorEqualToColor(borderLayer.strokeColor, borderColor.CGColor)) {
            return;
        }
        
        //移除旧的BorderLayer
        if (borderLayer) {
            [borderLayer removeFromSuperlayer];
        }
        
        //创建新的borderLayer
        CGPathRef path = GXCreatePath(newBounds, radii);
        borderLayer = [CAShapeLayer layer];
        borderLayer.fillColor = [UIColor clearColor].CGColor;
        borderLayer.strokeColor = borderColor.CGColor;
        borderLayer.lineWidth = borderWidth;
        borderLayer.path = path;
        [view.layer addSublayer:borderLayer];
        view.gxBorderLayer = borderLayer;
        CGPathRelease(path);
        
    } else {
        //移除borderLayer
        if (borderLayer) {
            [borderLayer removeFromSuperlayer];
            view.gxBorderLayer = nil;
        }
    }
    
}


@end
