//
//  GXProgressNode.m
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

#import "GXProgressNode.h"
#import "GXProgressView.h"
#import "NSDictionary+GX.h"
#import "UIColor+GX.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXProgressNode (){
    CGFloat _progress;
    BOOL _isNeedReload;
}

//进度颜色
@property (nonatomic, strong) UIColor *strokeColor;
//未完成的颜色
@property (nonatomic, strong) UIColor *trailColor;
//类型
@property (nonatomic, strong) NSString *progressType;
//动画
@property (nonatomic, assign) BOOL animated;

@end


@implementation GXProgressNode

// 创建视图
- (UIView *)creatView{
    GXProgressView *view = (GXProgressView *)self.associatedView;
    if (!view) {
        view = [[GXProgressView alloc] initWithFrame:CGRectZero];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        //弱引用view
        self.associatedView = view;
    }
    return view;
}

// 渲染视图
- (void)renderView:(UIView *)view{
    //渲染视图
    GXProgressView *progressView = (GXProgressView *)view;
    //判断是否相等，更新frame
    if (!CGRectEqualToRect(view.frame, self.frame)) {
        progressView.frame = self.frame;
    }
    
    //设置属性
    progressView.trailColor = self.trailColor;
    progressView.strokeColor = self.strokeColor;

    //是否设置
    if (_isNeedReload) {
        [progressView updateProgress:_progress animated:self.animated];
    }
}

#pragma mark - 赋值

- (void)bindData:(id)data{
    _progress = 0.f;
    _isNeedReload = NO;
    
    // 读取属性赋值
    if ([GXUtils isValidDictionary:data]) {
        NSDictionary *dataDict = (NSDictionary *)data;
        
        //处理extend
        NSDictionary *extend = [dataDict gx_dictionaryForKey:@"extend"];
        if (extend) {
            [self handleExtend:extend isCalculate:NO];
        }

        //重新刷新布局标识
        _isNeedReload = self.templateContext.isNeedLayout;

        //获取text
        NSString *value = [dataDict gx_stringForKey:@"value"];
        _progress = [value floatValue];

        //设置无障碍
        [self setupAccessibilityInfo:dataDict];
        
    } else if ([GXUtils isValidString:data]){
        //获取text
        NSString *value = (NSString *)data;
        _progress = [value floatValue];
        
    } else {
        //重置为0.f
        _progress = 0.f;
    }
    
    //更新progress
    GXProgressView *progressView = (GXProgressView *)self.associatedView;
    [progressView updateProgress:_progress animated:self.animated];
}

- (void)updateNormalStyle:(NSDictionary *)styleInfo isMark:(BOOL)isMark{
    [super updateNormalStyle:styleInfo isMark:isMark];
    
    // 进度条颜色
    NSString *strokeColor = [styleInfo gx_stringForKey:@"stroke-color"];
    if (strokeColor.length) {
        self.strokeColor = [UIColor gx_colorWithString:strokeColor];
    }

    // 未完成部分颜色
    NSString *trailColor = [styleInfo gx_stringForKey:@"trail-color"];
    if (trailColor) {
        self.trailColor = [UIColor gx_colorWithString:trailColor];
    }

    // 是否支持动画
    NSString *animatedStr = [styleInfo gx_stringForKey:@"animated"];
    if (animatedStr.length) {
        self.animated = [animatedStr boolValue];
    }
    
}


#pragma mark -
//设置普通色
- (void)setupNormalBackground:(UIView *)view{

}


#pragma mark - 读取属性

- (void)configureViewInfo:(NSDictionary *)viewInfo{
    [super configureViewInfo:viewInfo];
    
    // 进度条颜色
    NSString *strokeColor = [viewInfo gx_stringForKey:@"stroke-color"] ?: @"#ff0000";
    self.strokeColor = [UIColor gx_colorWithString:strokeColor];

    // 未完成部分颜色
    NSString *trailColor = [viewInfo gx_stringForKey:@"trail-color"] ?: @"#e4e4e4";
    self.trailColor = [UIColor gx_colorWithString:trailColor];

    // 组件类型
    self.progressType = [viewInfo gx_stringForKey:@"progress-type"];
    
    // 是否支持动画
    self.animated = [viewInfo gx_boolForKey:@"animated"];
}


#pragma mark - 属性设置



@end
