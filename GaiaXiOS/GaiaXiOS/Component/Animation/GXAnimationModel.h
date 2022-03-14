//
//  GXAnimationModel.h
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

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <GaiaMotionCurve/GaiaMotionCurve.h>

NS_ASSUME_NONNULL_BEGIN

@class GXLottieAnimationModel;
@class GXPropAnimationModel;
@class GXPropAnimatorSet;

@interface GXAnimationBaseModel : NSObject

//动画对应的frame
@property (nonatomic) CGRect frame;

- (void)setupAnimationInfo:(NSDictionary *)info frame:(CGRect)frame;

@end



@interface GXAnimationModel : GXAnimationBaseModel

//trigger = YES时生效
@property (nonatomic, assign) BOOL state;
//是否手动触发
@property (nonatomic, assign) BOOL trigger;
//lottie | prop
@property (nonatomic, copy) NSString *type;
//动画的数据
@property (nonatomic, strong) NSDictionary *animationInfo;
//属性动画 | 属性组合动画
@property (nonatomic, strong) GXPropAnimatorSet *propAnimatorSet;
//lottie单个动画
@property (nonatomic, strong) GXLottieAnimationModel *lottieAnimator;

@end



@interface GXLottieAnimationModel : GXAnimationBaseModel

//是否循环
@property (nonatomic, assign) BOOL loop;
//远程动画url
@property (nonatomic, copy) NSString *url;
//本地bundle和文件名拼接后的value
@property (nonatomic, copy) NSString *value;

@end



@interface GXPropAnimatorSet : GXAnimationBaseModel

// ordering: together 同时执行
// ordering: sequentially 串行执行
// 非必传， 默认together
@property (nonatomic, copy) NSString *ordering;
//组合动画
@property (nonatomic, strong) NSMutableArray <GXPropAnimationModel *>*animators;

@end



@interface GXPropAnimationModel : GXAnimationBaseModel

@property (nonatomic, assign) CFTimeInterval delay;
// 动画是否循环播放（只对自动播放动画生效 trigger = false）
// 默认为0
@property (nonatomic, assign) NSInteger loopCount;
//持续时间
@property (nonatomic, assign) CGFloat duration;
// reset 循环播放重置进度 例如: [0,1] -> [0,1] -> [0,1]
// reverse 循环播放翻转进度 例如：[0,1] -> [1,0] -> [0,1]
// 默认reset
@property (nonatomic, assign) BOOL autoReverse;
//动画值开始点 - 整形 浮点型 十六进制（后两位为Alpha通道）
@property (nonatomic, strong) NSValue *valueFrom;
//动画值结束点 - 整形 浮点型 十六进制（后两位为Alpha通道）
@property (nonatomic, strong) NSValue *valueTo;
// 属性名
// positionX 视图X轴位置偏移
// positionY 视图Y轴位置偏移
// opacity 视图透明度
// scale 视图X轴、Y轴缩放
// ration 视图旋转角度0~360
// renderColor 视图渲染颜色，支持视图背景
@property (nonatomic, strong) NSString *propName;
//插值器
@property (nonatomic, assign) GMCCurveType curveType;


@end


NS_ASSUME_NONNULL_END
