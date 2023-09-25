//
//  GXDataManager.m
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

#import "GXDataManager.h"
#import "GXRenderManager.h"
#import "NSDictionary+GX.h"
#import "GXRootViewNode.h"
#import "GXTemplateData.h"
#import "GXDataParser.h"
#import "GXScrollNode.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXGridNode.h"
#import "GXSliderNode.h"
#import "UIView+GX.h"
#import "GXTextNode.h"
#import "GXUtils.h"
#import "GXNode.h"

@implementation GXDataManager

//绑定数据到根视图
+ (void)bindData:(GXTemplateData *)data onRootView:(UIView *)view{
    GXNode *node = view.gxNode;
    [self bindData:data onRootNode:node];
}

//绑定数据到根节点
+ (void)bindData:(GXTemplateData *)data onRootNode:(GXNode *)node{
    if (node == nil || ![data isAvailable]) {
        return;
    }
    //绑定数据
    [self bindData:data onRootNode:node fromJS:NO];
}


/// 绑定数据 -（区分js和native调用)
+ (void)bindData:(GXTemplateData *)data onRootNode:(GXNode *)node fromJS:(BOOL)fromJS {
    node.orignalData = data;
    NSDictionary *dataDict = data.data;
    [self gx_bindData:dataDict onNode:node fromJS:fromJS];
    if (!fromJS) {
        [node onReady];
    }
}

//递归绑定操作
+ (void)gx_bindData:(NSDictionary *)data onNode:(GXNode *)node fromJS:(BOOL)fromJS {
    //获取拍平节点
    NSMapTable *flatNodes = node.flatNodes;
    if (flatNodes.count > 0) {
        NSEnumerator *keyEnumerator = flatNodes.keyEnumerator;
        for (NSString *key in keyEnumerator) {
            //获取节点
            GXNode *tmpNode = [flatNodes objectForKey:key];
            if ([tmpNode shouldBind]) {
                //判断是否为嵌套模板
                if ((tmpNode != node) && tmpNode.isTemplateType) {
                    //嵌套容器模板的root & 先解析数据
                    id virtualData = tmpNode.virtualData;
                    NSDictionary *resultData = [GXDataParser parseData:virtualData withSource:data];
                    //绑定数据
                    tmpNode.virtualExtend = nil;
                    NSDictionary *valueDict = nil;
                    if (resultData && [resultData isKindOfClass:[NSDictionary class]] ) {
                        //①获取外部extend
                        tmpNode.virtualExtend = [resultData gx_dictionaryForKey:@"extend"];

                        //②获取外部数据源，进行绑定操作
                        valueDict = [resultData gx_dictionaryForKey:@"value"];
                    }
                    
                    //③绑定数据到子模板
                    [self gx_bindData:valueDict onNode:tmpNode fromJS:fromJS];
                    if (!fromJS) {
                        [tmpNode onReady];
                    }
                    
                } else {
                    //绑定数据到节点
                    GXBindType type = GXBindTypeData;
                    [tmpNode applyData:data type:type];
                }
                
            }
            
        }
        
    }
    
    //绑定数据，重新计算布局
    if (node.isRootNode) {
        GXTemplateContext *context = node.templateContext;
        if (context.isNeedLayout) {
            GXRenderManager *renderManager = context.renderManager;
            //需要文本计算时候
            if (context.textNodes.count > 0) {
                [renderManager computeAndApplyLayout:context];
                //遍历text，更新文字布局信息
                for (GXTextNode *textNode in context.textNodes) {
                    [textNode updateFitContentLayout];
                }
            }
            //最终计算
            [renderManager setNeedLayout:context];
        }
        
    }
    
}


#pragma mark - 计算

//递归计算操作
+ (void)calculateData:(GXTemplateData *)data onRootNode:(GXNode *)node{
    if (node == nil || ![data isAvailable]) {
        return;
    }
    
    //触发计算
    NSDictionary *dataDict = data.data;
    [self gx_calculateData:dataDict onNode:node];
}

//具体计算逻辑
+ (void)gx_calculateData:(NSDictionary *)data onNode:(GXNode *)node{
    //获取拍平节点
    NSMapTable *flatNodes = node.flatNodes;
    if (flatNodes.count > 0) {
        NSEnumerator *keyEnumerator = flatNodes.keyEnumerator;
        for (NSString *key in keyEnumerator) {
            //获取节点
            GXNode *tmpNode = [flatNodes objectForKey:key];
            if (tmpNode.data || tmpNode.virtualData) {
                if ((tmpNode != node) && tmpNode.isTemplateType) {
                    //嵌套子模板类型为GaiaRootViewNode / GaiaScrollNode / GaiaGridNode
                    id virtualData = tmpNode.virtualData;
                    NSDictionary *resultData = [GXDataParser parseData:virtualData withSource:data];
                    
                    tmpNode.virtualExtend = nil;
                    NSDictionary *valueDict = nil;
                    if (resultData && [resultData isKindOfClass:[NSDictionary class]]) {
                        //①获取外部extend
                        node.virtualExtend = [resultData gx_dictionaryForKey:@"extend"];
                        
                        //②获取外部数据源，进行绑定操作
                        valueDict = [resultData gx_dictionaryForKey:@"value"];
                    }
                    
                    //③根据数据计算
                    [self gx_calculateData:valueDict onNode:tmpNode];
                    
                } else {
                    //绑定数据
                    GXBindType type = GXBindTypeCalculate;
                    [tmpNode applyData:data type:type];
                }
                
            }
            
        }
        
    }
    
}

@end
