//
//  GXNodeTreeCreator.m
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

#import "GXNodeTreeCreator.h"
#import "GXTemplateManager.h"
#import "GXTemplateContext.h"
#import "NSDictionary+GX.h"
#import "GXNodeHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "UIView+GX.h"
#import "GXUtils.h"
#import "GXStyle.h"
#import "GXNode.h"

@implementation GXNodeTreeCreator

//创建节点树，并获取根节点
- (GXNode *)creatNodeTreeWithTemplateItem:(GXTemplateItem *)templateItem
                                  context:(GXTemplateContext *)ctx{
    //读取模板信息
    NSDictionary *templateInfo = [GXTemplateManager loadTemplateContentWithTemplateItem:templateItem];
    //创建节点树
    GXNode *rootNode = [self creatNodeTreeWithTemplateItem:templateItem
                                              templateInfo:templateInfo
                                                   context:ctx];
    return rootNode;
}

//创建节点树，并获取根节点
- (GXNode *)creatNodeTreeWithTemplateItem:(GXTemplateItem *)templateItem
                             templateInfo:(NSDictionary *)templateInfo
                                  context:(GXTemplateContext *)ctx{
    //基础信息
    GXNode *rootNode = nil;
    NSString *bizId = templateItem.bizId;
    NSString *templateId = templateItem.templateId;
    
    //模板信息/节点构建
    if ([GXUtils isValidDictionary:templateInfo]) {
        //获取层级关系 & 根节点类型
        NSDictionary *layerInfo = [templateInfo gx_dictionaryForKey:kGXComDef_KW_VH];
        NSString *templateType = [layerInfo gx_stringForKey:kGXComDef_KW_Type];
        BOOL isValid = layerInfo && [templateType isEqualToString:kGXComDef_KW_GaiaTemplate];
        //创建节点树
        if (isValid) {
            //进行合并样式
            NSDictionary *styleInfo = nil;
            NSDictionary *rootStyleInfo = templateItem.rootStyleInfo;
            if ([GXUtils isValidDictionary:rootStyleInfo]) {
                //获取全部的Css样式
                NSMutableDictionary *tmpStyleInfo = [[templateInfo gx_dictionaryForKey:kGXComDef_KW_SY] mutableCopy];
                //读取根节点的Css样式，将外部Merge到内部
                NSMutableDictionary *tmpRootStyleInfo = [[tmpStyleInfo gx_dictionaryForKey:templateId] mutableCopy];
                if (tmpRootStyleInfo) {
                    [tmpRootStyleInfo addEntriesFromDictionary:rootStyleInfo];
                } else {
                    tmpRootStyleInfo = (NSMutableDictionary *)rootStyleInfo;
                }
                //回写全部样式
                [tmpStyleInfo gx_setObject:tmpRootStyleInfo forKey:templateId];
                styleInfo = tmpStyleInfo;
            } else {
                //直接获取样式map
                styleInfo = [templateInfo gx_dictionaryForKey:kGXComDef_KW_SY];
            }
            
            //创建节点树 & 获取databinding
            NSDictionary *dbInfo = [templateInfo gx_dictionaryForKey:kGXComDef_KW_DB];
            NSMapTable *flatNodes = [NSMapTable strongToWeakObjectsMapTable];
            NSArray<GXNode *> *nodes = [self creatNodeTreeWithTemplateItem:templateItem
                                                                   context:ctx
                                                                 flatNodes:flatNodes
                                                                 styleInfo:styleInfo
                                                                    dbInfo:dbInfo
                                                                    layers:@[layerInfo]];
            //获取rootNode
            rootNode = [nodes firstObject];
            if (rootNode) {
                //layout & flatNodes
                rootNode.flatNodes = flatNodes;
            }
            
        } else {
            GXLog(@"[GaiaX] 构建节点树失败：(%@/%@)模板index.json文件异常", bizId, templateId);
//            GXAssert(isValid, @"构建节点树失败：(%@/%@)index.json文件异常", bizId, templateId);
        }
        
    } else {
        GXLog(@"[GaiaX] 构建节点树失败：(%@/%@)模板文件为空", bizId, templateId);
//        GXAssert([GXUtils isValidDictionary:templateInfo], @"构建节点树失败：(%@/%@)模板文件为空", bizId, templateId);
    }
    
    //赋值ctx
    ctx.rootNode = rootNode;
    ctx.templateInfo = templateInfo;
    
    //返回节点树
    return rootNode;
}


#pragma mark - 节点树构建

//构建节点树
- (NSArray *)creatNodeTreeWithTemplateItem:(GXTemplateItem *)templateItem
                                   context:(GXTemplateContext *)ctx
                                 flatNodes:(NSMapTable *)flatNodes
                                 styleInfo:(NSDictionary *)styleInfo
                                    dbInfo:(NSDictionary *)dbInfo
                                    layers:(NSArray *)layers{
    NSMutableArray *nodes = nil;
    if ([GXUtils isValidArray:layers]) {
        //创建nodes
        nodes = [NSMutableArray array];
        NSDictionary *dataDict = [dbInfo gx_dictionaryForKey:@"data"];
        NSDictionary *eventDict = [dbInfo gx_dictionaryForKey:@"event"];
        NSDictionary *trackDict = [dbInfo gx_dictionaryForKey:@"track"];
        NSDictionary *animationDict = [dbInfo gx_dictionaryForKey:@"animation"];
        
        //获取view层级
        for (NSUInteger i = 0; i < layers.count; i++) {
            //获取对应的json
            NSDictionary *layerInfo = layers[i];
            NSString *nodeId = [layerInfo gx_stringForKey:kGXComDef_KW_ID];
            
            //判断视图Node类型
            GXNode *node = nil;
            NSString *layerType = [layerInfo gx_stringForKey:kGXComDef_KW_Type];
            NSString *layerSubType = [layerInfo gx_stringForKey:kGXComDef_KW_SubType];
            
            if ([GXNodeHelper isCustom:layerType subType:layerSubType]){
                //嵌套模板，外部属性覆盖内部属性
                GXTemplateItem *subItem = [[GXTemplateItem alloc] init];
                subItem.rootStyleInfo = [styleInfo gx_dictionaryForKey:nodeId];
                subItem.isLocal = templateItem.isLocal;
                subItem.bizId = templateItem.bizId;
                subItem.templateId = nodeId;
                
                //创建嵌套节点
                node = [self creatNodeTreeWithTemplateItem:subItem context:ctx];
                node.virtualData = [dataDict objectForKey:nodeId];
                
            } else {
                //普通/Scroll/Grid节点处理
                Class nodeClass = [GXNodeHelper nodeClassWithLayerType:layerType subType:layerSubType];
                //是否为容器类型
                BOOL isContainer = [GXNodeHelper isContainter:layerType subType:layerSubType];
                
                
                //生成GXStyle
                GXStyle *style = [self creatStyleWithNodeId:nodeId styleInfo:styleInfo];
                // 兼容 edge-insets
                [style updateEdgeInsets:[layerInfo objectForKey:@"edge-insets"]];
                //创建节点实例
                node = [[nodeClass alloc] initWithStyle:style children:nil];
                
                //节点模板属性设置
                node.isTemplateType = [GXNodeHelper isTemplateType:layerType];
                node.templateItem = templateItem;
                node.templateContext = ctx;
                node.nodeId = nodeId;
                
                //节点databinding属性设置
                node.data = [dataDict objectForKey:nodeId];
                node.event = [eventDict objectForKey:nodeId];
                node.track = [trackDict objectForKey:nodeId];
                node.animation = [animationDict gx_dictionaryForKey:nodeId];
                
                //容器中所有属性
                if (isContainer) {
                    node.fullStyleJson = styleInfo;
                }
                
                //设置样式属性
                [node configureStyleInfo:style.styleInfo];
                
                //设置层级属性
                [node configureViewInfo:layerInfo];
                
                //非容器类，递归创建子节点
                if (!isContainer) {
                    NSArray *subLayers = [layerInfo gx_arrayForKey:kGXComDef_KW_Layers];
                    if (subLayers.count) {
                        NSArray *subNodes = [self creatNodeTreeWithTemplateItem:templateItem
                                                                        context:ctx
                                                                      flatNodes:flatNodes
                                                                      styleInfo:styleInfo
                                                                         dbInfo:dbInfo
                                                                         layers:subLayers];
                        for (NSUInteger j = 0; j < subNodes.count; j++) {
                            //将子节点添加到节点树下
                            GXNode *childNode = subNodes[j];
                            if (childNode) {
                                [node addChild:childNode];
                            }
                            
                        }
                        
                    }
                    
                }
                
            }
            
            //添加节点数组
            if (node) {
                [node updateTextNodes:ctx.textNodes];
                //添加数组
                [nodes addObject:node];
                [flatNodes setObject:node forKey:node.nodeId];
            }
        }
    }
    
    //返回node节点数组
    return nodes;
}


#pragma mark - 处理style样式

//处理style样式
- (GXStyle *)creatStyleWithNodeId:(NSString *)nodeId styleInfo:(NSDictionary *)styleInfo {
    //获取CSS样式
    NSDictionary *styleDict = nil;
    
    //获取id对应的属性
    if (nodeId) {
        styleDict = [styleInfo gx_dictionaryForKey:nodeId];
    }
    
    //根据样式->style
    GXStyle *style = [[GXStyle alloc] init];
    [style setupStyleInfo:styleDict];
    
    return style;
}


@end
