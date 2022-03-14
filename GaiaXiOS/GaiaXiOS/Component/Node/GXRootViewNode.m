//
//  GXRootViewNode.m
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

#import "GXRootViewNode.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXRootView.h"
#import "UIView+GX.h"

@implementation GXRootViewNode

// 创建视图
- (UIView *)creatView{
    UIView *view = self.associatedView;
    if (!view) {
        view = [[GXRootView alloc] initWithFrame:CGRectZero];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        //弱引用view
        self.associatedView = view;
        //支持渐变背景
        self.isSupportGradientBgColor = YES;
    }
    return view;
}


// 渲染视图
- (void)renderView:(UIView *)view{
    // 更新frame
    if ([self isRootNode]) {
        CGRect frame = view.frame;
        frame.size.width = self.frame.size.width;
        frame.size.height = self.frame.size.height;
        self.frame = frame;
    }
    
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


@end
