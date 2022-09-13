//
//  GXCustomNode.m
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

#import "GXCustomNode.h"
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXCustomNode ()

//自定义view的class
@property(nonatomic, strong) Class viewClazz;

@end

@implementation GXCustomNode

//创建view
- (UIView *)creatView{
    UIView *view = self.associatedView;
    if (!view) {
        view = [[self.viewClazz alloc] init];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        //弱引用view
        self.associatedView = view;
        self.isSupportShadow = YES;
    }
    return view;
}


//渲染
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
//    [self setupShadow:view];
}


#pragma mark - 绑定数据

- (void)bindData:(NSDictionary *)data{
    //赋值
    if ([GXUtils isDictionary:data]) {
        //处理绑定数据
        UIView *view = self.associatedView;
        SEL selector = NSSelectorFromString(@"gx_bindData:");
        if ([view respondsToSelector:selector]) {
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Warc-performSelector-leaks"
            [view performSelector:selector withObject:data];
#pragma GCC diagnostic pop
        }
        
        //处理extend
        NSDictionary *extend = [data gx_dictionaryForKey:@"extend"];
        if (extend) {
            [self handleExtend:extend isCalculate:NO];
        }
        
        //无障碍
        [self setupAccessibilityInfo:data];
    }
    
}


#pragma mark - 计算

//计算布局
- (void)calculateWithData:(NSDictionary *)data{
    //赋值
    if ([GXUtils isDictionary:data]) {
        //处理extend
        NSDictionary *extend = [data gx_dictionaryForKey:@"extend"];
        if (extend) {
            [self handleExtend:extend isCalculate:YES];
        }
    }
}


#pragma mark - 处理extend属性

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


#pragma mark - 属性解析

//处理json中的属性
- (void)configureViewInfo:(NSDictionary *)viewInfo{
    [super configureViewInfo:viewInfo];
    
    //获取自定义view的class
    NSString *clazzStr = [viewInfo gx_stringForKey:@"view-class-ios"];
    Class clazz = NSClassFromString(clazzStr) ?: UIView.class;
    self.viewClazz = clazz;
}

//处理css中的属性
- (void)configureStyleInfo:(NSDictionary *)styleInfo{
    [super configureStyleInfo:styleInfo];
    
    //opacity
    NSString *opacity = [styleInfo gx_stringForKey:@"opacity"];
    self.opacity = opacity.length ? [opacity floatValue] : 1.f;
}


@end
