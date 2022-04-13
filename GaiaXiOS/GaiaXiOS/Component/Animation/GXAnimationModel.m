//
//  GXAnimationModel.m
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

#import "GXAnimationModel.h"
#import "NSDictionary+GX.h"
#import "NSArray+GX.h"
#import "UIColor+GX.h"

@implementation GXAnimationBaseModel

- (void)setupAnimationInfo:(NSDictionary *)info frame:(CGRect)frame{
    self.frame = frame;
}

@end


@implementation GXAnimationModel

- (void)setupAnimationInfo:(NSDictionary *)info frame:(CGRect)frame{
    [super setupAnimationInfo:info frame:frame];
    
    //基础属性设置
    self.animationInfo = info;
    self.type = [info gx_stringForKey:@"type"];
    self.state = [info gx_boolForKey:@"state"];
    self.trigger = [info gx_boolForKey:@"trigger"];
    
    NSDictionary *lottieAnimation = [info gx_dictionaryForKey:@"lottieAnimator"];
    if (lottieAnimation) {
        //lottie动画
        if (!_lottieAnimator) {
            _lottieAnimator = [[GXLottieAnimationModel alloc] init];
        }
        [_lottieAnimator setupAnimationInfo:lottieAnimation frame:frame];
        
    } else {
        //属性动画
        NSDictionary *propAnimatorSet = [info gx_dictionaryForKey:@"propAnimatorSet"];
        if (propAnimatorSet) {
            if (!_propAnimatorSet) {
                _propAnimatorSet = [[GXPropAnimatorSet alloc] init];
            }
            [_propAnimatorSet setupAnimationInfo:propAnimatorSet frame:frame];
        }
    }
}

@end


@implementation GXLottieAnimationModel

- (void)setupAnimationInfo:(NSDictionary *)info frame:(CGRect)frame{
    [super setupAnimationInfo:info frame:frame];

    //属性配置
    self.loop = [info gx_boolForKey:@"loop"];
    //获取url
    self.url = [info gx_stringForKey:@"url"];
    //获取value
    NSString *value = [info gx_stringForKey:@"value"];
    if (value.length) {
        NSString *bundle = [info gx_stringForKey:@"bundle"];
        if (bundle.length) {
            value = [NSString stringWithFormat:@"%@/%@", bundle,value];
        }
    }
    self.value = value;
}

@end


@implementation GXPropAnimatorSet

- (void)setupAnimationInfo:(NSDictionary *)info frame:(CGRect)frame{
    [super setupAnimationInfo:info frame:frame];

    //顺序
    self.ordering = [info gx_stringForKey:@"ordering"];
    //animators
    NSArray *animators = [info gx_arrayForKey:@"animators"];
    if (animators) {
        if (!_animators) {
            _animators = [NSMutableArray array];
        }
        
        //移除多余内容
        NSInteger count = animators.count;
        if (_animators.count > count) {
            NSInteger location = count;
            NSInteger length = _animators.count - count;
            [_animators removeObjectsInRange:NSMakeRange(location, length)];
        }
        
        //更新动画属性
        for (int i = 0; i < count; i++) {
            GXPropAnimationModel *animator = [_animators gx_objectAtIndex:i];
            if (!animator) {
                animator = [[GXPropAnimationModel alloc] init];
                [_animators gx_addObject:animator];
            }
            NSDictionary *animatorInfo = [animators gx_objectAtIndex:i];
            if ([animatorInfo isKindOfClass:[NSDictionary class]]) {
                [animator setupAnimationInfo:animatorInfo frame:frame];
            }
        }
    }
}

@end


@implementation GXPropAnimationModel

- (void)setupAnimationInfo:(NSDictionary *)info frame:(CGRect)frame{
    [super setupAnimationInfo:info frame:frame];

    self.delay = [info gx_integerForKey:@"delay"] / 1000.f;
    self.loopCount = [info gx_integerForKey:@"loopCount"];
    self.duration = [info gx_integerForKey:@"duration"] / 1000.f;
    
    NSString *loopMode = [info gx_stringForKey:@"loopMode"];
    self.autoReverse = [loopMode isEqualToString:@"reverse"];
    
    //keyPath
    NSString *propName = [info gx_stringForKey:@"propName"];
    if ([propName isEqualToString:@"opacity"]){
        self.propName = @"opacity";
        
    } else if ([propName isEqualToString:@"scale"]){
        self.propName = @"transform.scale";
        
    } else if ([propName isEqualToString:@"rotation"]){
        self.propName = @"transform.rotation";
        
    } else if ([propName isEqualToString:@"renderColor"]){
        self.propName = @"backgroundColor";
        
    } else if ([propName isEqualToString:@"positionX"]) {
        self.propName = @"position.x";
        
    } else if ([propName isEqualToString:@"positionY"]){
        self.propName = @"position.y";
        
    } else {
        self.propName = @"";
    }
    
    //处理valueType
    NSString *valueType = [info gx_stringForKey:@"valueType"];
    if ([valueType isEqualToString:@"floatType"] || [valueType isEqualToString:@"intType"]){
        CGFloat valueFrom = [info gx_floatForKey:@"valueFrom"];
        CGFloat valueTo = [info gx_floatForKey:@"valueTo"];
        //rotation转化
        if ([propName isEqualToString:@"rotation"]) {
            valueFrom = (valueFrom / 180.f) * M_PI;
            valueTo = (valueTo / 180.f) * M_PI;
        } else if ([propName isEqualToString:@"positionX"]){
            CGFloat tmpValue = self.frame.origin.x + self.frame.size.width / 2.f;
            valueFrom += tmpValue;
            valueTo += tmpValue;
            
        } else if ([propName isEqualToString:@"positionY"]){
            CGFloat tmpValue = self.frame.origin.y + self.frame.size.height / 2.f;
            valueFrom += tmpValue;
            valueTo += tmpValue;
        }
        
        self.valueFrom = [NSValue gmc_valueWithCGFloat:valueFrom];
        self.valueTo = [NSValue gmc_valueWithCGFloat:valueTo];
        
    } else if ([valueType isEqual:@"colorType"]){
        NSString *valueFrom = [info gx_stringForKey:@"valueFrom"];
        CGColorRef fromColor = [UIColor gx_colorWithHexString:valueFrom].CGColor;
        self.valueFrom = [NSValue gmc_valueWithCGColor:fromColor];
        
        NSString *valueTo = [info gx_stringForKey:@"valueTo"];
        CGColorRef toColor = [UIColor gx_colorWithHexString:valueTo].CGColor;
        self.valueTo = [NSValue gmc_valueWithCGColor:toColor];
    }
    
    //插值器处理
    NSString *interpolator = [info gx_stringForKey:@"interpolator"];
    if ([interpolator isEqualToString:@"linear"]) {
        self.curveType = GMCCurveTypeLinear;
        
    } else if ([interpolator isEqualToString:@"standard"]){
        self.curveType = GMCCurveTypeStandard;
        
    } else if ([interpolator isEqualToString:@"accelerate"]){
        self.curveType = GMCCurveTypeAccelerate;
        
    } else if ([interpolator isEqualToString:@"decelerate"]){
        self.curveType = GMCCurveTypeDecelerate;
        
    } else if ([interpolator isEqualToString:@"anticipate"]){
        self.curveType = GMCCurveTypeAnticipate;
        
    } else if ([interpolator isEqualToString:@"overshoot"]){
        self.curveType = GMCCurveTypeOvershoot;
        
    } else if ([interpolator isEqualToString:@"spring"]){
        self.curveType = GMCCurveTypeSpring;
        
    } else if ([interpolator isEqualToString:@"bounce"]){
        self.curveType = GMCCurveTypeBounce;
        
    } else if ([interpolator isEqualToString:@"cosine"]){
        self.curveType = GMCCurveTypeCosine;
        
    } else {
        self.curveType = GMCCurveTypeStandard;
    }
    
}

@end

