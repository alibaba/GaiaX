//
//  GXViewNode.m
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

#import "GXViewNode.h"
#import <GaiaMotionCurve/GaiaMotionCurve.h>
#import "GXLottieAniamtionProtocal.h"
#import "GXRegisterCenter.h"
#import "GXAnimationModel.h"
#import "NSDictionary+GX.h"
#import "GXBizHelper.h"
#import "NSArray+GX.h"
#import "UIView+GX.h"
#import "GXUtils.h"
#import "GXView.h"

@interface GXViewNode () {
    //是否手动触发
    BOOL _isLottie;
    BOOL _isTrigger;
    BOOL _userEnable;
}

//动画属性
@property (nonatomic, copy) NSString *lottieName;
//动画属性
@property (nonatomic, strong) GXAnimationModel *animationModel;
//动画view
@property (nonatomic, strong) UIView<GXLottieAniamtionProtocal> *animationView;

@end

@implementation GXViewNode

- (UIView *)creatView{
    UIView *view = self.associatedView;
    if (!view) {
        view = [[GXView alloc] initWithFrame:CGRectZero];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        self.associatedView = view;
        //支持渐变背景
        self.isSupportGradientBgColor = YES;
        //设置通知
        if (self.animation) {
            view.userInteractionEnabled = _userEnable;
        }
    }
    return view;
}

- (void)renderView:(UIView *)view{
    //判断是否相等，更新frame
    if (!CGRectEqualToRect(view.frame, self.frame)) {
        view.frame = self.frame;
    }
    
    //设置属性
    view.alpha = self.opacity;
    view.clipsToBounds = self.clipsToBounds;
    
    //设置背景色
    if (self.linearGradient) {
        [self setupGradientBackground:view];
    } else {
        [self setupNormalBackground:view];
    }
    
    //设置圆角
    [self setupCornerRadius:view];
    
    //设置阴影
    [self setupShadow:view];
}


#pragma mark - 绑定数据

- (void)bindData:(id)data{
    //赋值
    if ([GXUtils isDictionary:data]) {
        //处理extend
        NSDictionary *dataDict = (NSDictionary *)data;
        NSDictionary *extend = [dataDict gx_dictionaryForKey:@"extend"];
        if (extend) {
            [self handleExtend:extend isCalculate:NO];
        }
        
        //处理无障碍
        [self setupAccessibilityInfo:dataDict];
    }
}

// 处理扩展处理
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{
    //更新布局属性 & 标记
    BOOL isMark = [self updateLayoutStyle:extend];
    
    //更新普通属性
    if (!isCalculate) {
        [self updateNormalStyle:extend isMark:isMark];
    }
    
    //确认属性发生变化，更新布局
    if (isMark) {
        //更改style + rustPtr
        [self.style updateRustPtr];
        [self setStyle:self.style];
        
        //标记dirty
        [self markDirty];
        
        //重新刷新布局标识
        self.templateContext.isNeedLayout = YES;
    }
}


#pragma mark - 计算高度

- (void)calculateWithData:(id)data{
    //赋值
    if ([GXUtils isValidDictionary:data]) {
        NSDictionary *dataDict = (NSDictionary *)data;
        NSDictionary *extend = [dataDict gx_dictionaryForKey:@"extend"];
        if (extend) {
            [self handleExtend:extend isCalculate:YES];
        }
    }
}



#pragma mark - 属性解析

//读取属性
- (void)configureStyleInfo:(NSDictionary *)styleJson{
    [super configureStyleInfo:styleJson];
    //动画
    if (self.animation) {
        //设置当前view是否可以交互
        NSString *type = [self.animation gx_stringForKey:@"type"];
        if ([type isEqualToString:@"lottie"]) {
            _userEnable = NO;
            _isLottie = YES;
        } else {
            _userEnable = YES;
            _isLottie = NO;
        }
        
        //设置是否手动触发
        _isTrigger = [self.animation gx_boolForKey:@"trigger"];
    }
}


#pragma mark - 绑定动画

- (void)bindAnimation:(id)data{
    //获取动画数据 & 动画view
    NSDictionary *animationDict = (NSDictionary *)data;
    //动画处理
    if ([GXUtils isValidDictionary:animationDict] && (self.style.styleModel.display == DisplayFlex)) {
        if ([animationDict objectForKey:@"lottieAnimator"] || [animationDict objectForKey:@"propAnimatorSet"]) {
            //加载动画
            [self setupGaiaXAnimation:animationDict];
        } else {
            //隐藏动画
            if (_animationView) {
                _animationView.hidden = YES;
                [_animationView gx_stop];
            }
        }
        
    } else {
        //隐藏动画
        if (_animationView) {
            _animationView.hidden = YES;
            [_animationView gx_stop];
        }
    }
    
}

#pragma mark - new animation

- (void)setupGaiaXAnimation:(NSDictionary *)animationInfo{
    //更新model
    if (!_animationModel) {
        _animationModel = [[GXAnimationModel alloc] init];
    }
    [_animationModel setupAnimationInfo:animationInfo frame:self.frame];
    
    //判断类型
    NSString *animationType = _animationModel.type;
    if ([animationType isEqualToString:@"lottie"]) {
        //lottie动画
        [self lottieAnimation:_animationModel];
        
    } else if ([animationType isEqualToString:@"prop"]){
        //属性动画
        [self propAnimation:_animationModel];
        
    }
}

//lottie动画
- (void)lottieAnimation:(GXAnimationModel *)animationModel{
    //判断lottie的class是否存在
    Class lottieViewClass = TheGXRegisterCenter.lottieViewClass;
    if (lottieViewClass == nil) {
        return;
    }
    
    //读取属性
    BOOL trigger = animationModel.trigger;
    BOOL state = animationModel.state;
    //手动触发动画，state = false
    if (trigger && !state) {
        return;
    }
    
    BOOL isLocal = NO; //网络=NO
    NSString *lottieUrl = animationModel.lottieAnimator.url;
    if (!lottieUrl.length) {
        isLocal = YES;
        lottieUrl = animationModel.lottieAnimator.value;
    }
    
    BOOL loop = animationModel.lottieAnimator.loop;
    
    //正在播放就直接return
    if (_animationView &&
        !_animationView.hidden &&
        [_animationView gx_isAnimationPlaying] &&
        [_lottieName isEqualToString:lottieUrl]) {
        return;
    }
    
    //动画停止
    _animationView.hidden = YES;
    [_animationView gx_stop];
    
    //动画判断
    _lottieName = lottieUrl;
    if (!_lottieName.length) {
        return;
    }
    
    //创建view
    if (_animationView == nil) {
        _animationView = [[lottieViewClass alloc] initWithFrame:self.associatedView.bounds];
        _animationView.translatesAutoresizingMaskIntoConstraints = YES;
        _animationView.contentMode = UIViewContentModeScaleAspectFit;
        _animationView.userInteractionEnabled = NO;
        [self.associatedView insertSubview:_animationView atIndex:0];
    }
    
    //执行animation
    if (_animationView) {
        //显示动画 & 更新frame
        _animationView.hidden = NO;
        _animationView.frame = self.associatedView.bounds;
        
        //动画参数
        NSMutableDictionary *animationInfo = [NSMutableDictionary dictionary];
        [animationInfo gx_setObject:@"isLocal" forKey:@(isLocal)];
        [animationInfo gx_setObject:@"lottieUrl" forKey:_lottieName];
        [animationInfo gx_setObject:@"loopCount" forKey:@((loop ? -1 : 0))];

        //执行动画
        GXWeakSelf(self);
        [_animationView gx_playAnimation:animationInfo completion:^(BOOL finished) {
            GXStrongSelf(self)
            NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
            [dictionary gx_setObject:animationModel.animationInfo forKey:@"animationInfo"];
            [dictionary gx_setObject:@(finished) forKey:@"animationFinished"];
            [self animationDidFinished:dictionary];
        }];
    }
    
}


//标准曲线动画
- (void)propAnimation:(GXAnimationModel *)animationModel{
    //子动画属性
    NSArray *animators = animationModel.propAnimatorSet.animators;
    NSInteger count = animators.count;
    if (count == 0) {
        return;
    }
    
    //读取属性
    BOOL trigger = animationModel.trigger;
    BOOL state = animationModel.state;
    
    //手动触发动画 & state = false
    if (trigger && !state) {
        return;
    }
    
    //动画执行顺序
    NSString *ordering = animationModel.propAnimatorSet.ordering;
    
    [self.associatedView.layer gmc_cancelAllAnimations];
    
    //执行动画
    if (count == 1 || ![ordering isEqualToString:@"sequentially"]) {
        //动画组
        NSMutableArray *animationModels = [NSMutableArray array];
        for (int i = 0; i < count; i ++) {
            GXPropAnimationModel *propAnimator = [animationModel.propAnimatorSet.animators gx_objectAtIndex:i];
            GMCModel *model = [GMCModel modelWithKeyPath:propAnimator.propName
                                                duration:propAnimator.duration
                                                   delay:propAnimator.delay
                                             repeatCount:propAnimator.loopCount
                                             autoReverse:propAnimator.autoReverse
                                               curveType:propAnimator.curveType
                                               fromValue:propAnimator.valueFrom
                                                 toValue:propAnimator.valueTo];
            [animationModels gx_addObject:model];
        }
        //添加动画
        GXWeakSelf(self)
        [self.associatedView.layer gmc_animateWithGMCModels:animationModels completion:^(BOOL finished) {
            //动画结束的回调
            if (finished) {
                GXStrongSelf(self);
                [self animationDidFinished:animationModel.animationInfo];
            }
        }];
        
    } else {
        //动画组
        NSMutableArray *animationModels = [NSMutableArray array];
        for (int i = 0; i < count; i ++) {
            GXPropAnimationModel *propAnimator = [animationModel.propAnimatorSet.animators gx_objectAtIndex:i];
            GMCModel *model = [GMCModel modelWithKeyPath:propAnimator.propName
                                                duration:propAnimator.duration
                                                   delay:propAnimator.delay
                                             repeatCount:propAnimator.loopCount
                                             autoReverse:propAnimator.autoReverse
                                               curveType:propAnimator.curveType
                                               fromValue:propAnimator.valueFrom
                                                 toValue:propAnimator.valueTo];
            [animationModels gx_addObject:model];
        }
        
        //顺序执行动画
        GXWeakSelf(self)
        [self.associatedView.layer gmc_serialAnimationWithGMCModels:animationModels completion:^(BOOL finished) {
            //动画结束的回调
            if (finished) {
                GXStrongSelf(self);
                [self animationDidFinished:animationModel.animationInfo];
            }
        }];
    }
    
}


//动画结束
- (void)animationDidFinished:(NSDictionary *)animationInfo{
    id <GXEventProtocal> eventListener = self.templateContext.templateData.eventListener;
    if (eventListener && [eventListener respondsToSelector:@selector(gx_animationDidFinished:)]) {
        [eventListener gx_animationDidFinished:animationInfo];
    }
}


@end
