//
//  GaiaJSEventManager.m
//  GaiaXCore
//
//  Created by zjc on 2021/7/2.
//  Copyright © 2021 zhangjc. All rights reserved.
//

#import "GXJSDelegateImplManager.h"
#import "GXRootViewProtocal.h"
#import "GXTemplateContext.h"
#import "NSDictionary+GX.h"
#import "GXEvent_Private.h"
#import "GXEventManager.h"
#import "GXTemplateData.h"
#import "GXDataManager.h"
#import "GXCommonDef.h"
#import "NSArray+GX.h"
#import "UIView+GX.h"
#import "GXUtils.h"
#import "GXNode.h"
#import "GXRootView.h"
#import "GXEvent.h"

@implementation GXJSDelegateImplManager

+ (instancetype)defaultManager {
    static dispatch_once_t onceToken;
    static GXJSDelegateImplManager *manager = nil;
    dispatch_once(&onceToken, ^{
        manager = [[GXJSDelegateImplManager alloc] init];
        [GaiaXJSFactory defaultFactory].modulesImplDelegate = manager;
    });
    return manager;
}


#pragma mark - 实现代理

- (NSDictionary *)getElement:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:extendInfo]) {
        return nil;
    }
    
    //获取js透传的内容
    NSString *targetId = [extendInfo gx_stringForKey:@"targetId"];
    NSString *templateId = [extendInfo gx_stringForKey:@"templateId"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //获取对应的节点
    NSMutableDictionary *resultDict = nil;
    if (targetId.length && templateId.length && instanceIdStr.length) {
        //通过instanceId获取节点
        GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
        if (node) {
            GXNode *targetNode = [node queryNodeByNodeId:targetId];
            if (targetNode) {
                resultDict = [NSMutableDictionary dictionary];
                //节点id
                [resultDict gx_setObject:targetId forKey:@"targetId"];
                //添加type
                [resultDict gx_setObject:targetNode.type forKey:@"targetType"];
                [resultDict gx_setObject:targetNode.subType forKey:@"targetSubType"];
            }
        }
    }
    
    return resultDict;
}


- (BOOL)addEventListener:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:extendInfo]) {
        return NO;
    }
    
    //节点&模板相关属性
    NSString *targetId = [extendInfo gx_stringForKey:@"targetId"];
    NSString *templateId = [extendInfo gx_stringForKey:@"templateId"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //开始注册事件
    if (targetId.length && templateId.length && instanceIdStr.length) {
        dispatch_async(dispatch_get_main_queue(), ^{
            //通过instanceId获取模板信息
            GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
            if (node) {
                //获取target节点 & view
                GXNode *targetNode = [node queryNodeByNodeId:targetId];
                UIView *targetView = targetNode.associatedView;
                if (targetView == nil) {
                    targetView = [targetNode creatView];
                }
                
                //获取event
                GXEventType eventType = [GXEvent eventTypeWithEventInfo:extendInfo];
                GXEvent *event = [targetView gx_eventWithType:eventType];
                if (!event) {
                    event = [[GXEvent alloc] init];
                    event.view = (GXRootView *)(node.rootNode.associatedView);
                    event.templateItem = node.templateItem;
                    event.eventType = eventType;
                    event.nodeId = node.nodeId;
                    event.view = targetView;
                    //赋值
                    [targetView gx_setEvent:event withType:eventType];
                }
                //jsComponent
                event.jsComponent = node.jsComponent;
                [event creatJsEvent:extendInfo];
                
                //注册事件
                [TheGXEventManager registerEvent:event forNode:targetNode];
            }
        });
    }
    
    return YES;
}


- (BOOL)removeEventListener:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:extendInfo]) {
        return NO;
    }
    
    //节点&模板相关属性
    NSString *targetId = [extendInfo gx_stringForKey:@"targetId"];
    NSString *eventType = [extendInfo gx_stringForKey:@"eventType"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //获取对应的节点
    if (targetId.length && instanceIdStr.length && eventType.length) {
        //通过instanceId获取节点
        dispatch_async(dispatch_get_main_queue(), ^{
            GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
            if (node) {
                GXNode *targetNode = [node queryNodeByNodeId:targetId];
                UIView *targetView = targetNode.associatedView;
                //取消事件
                if ([eventType isEqualToString:@"click"]) {
                    targetView.gxEvent.jsEvent = nil;
                } else if ([eventType isEqualToString:@"longpress"]){
                    targetView.gxLpEvent.jsEvent = nil;
                }
            }
        });
    }
    
    return YES;
}


- (NSDictionary *)getBindingData:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:extendInfo]) {
        return nil;
    }
    
    //获取js透传的内容
    NSString *templateId = [extendInfo gx_stringForKey:@"templateId"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //获取对应的节点
    NSDictionary *resultDict = nil;
    if (templateId.length && instanceIdStr.length) {
        //通过instanceId获取节点
        GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
        if (node) {
            //获取根节点/数据
            GXNode *rootNode = node.rootNode;
            resultDict = rootNode.orignalData.data;
        }
    }
    
    return resultDict;
}


- (void)setBindingData:(NSDictionary *)data extendInfo:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:data] || ![GXUtils isValidDictionary:extendInfo]) {
        return;
    }
    
    //获取js透传的内容
    NSString *templateId = [extendInfo gx_stringForKey:@"templateId"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //绑定数据
    if (templateId.length && instanceIdStr.length) {
        //通过instanceId获取节点
        GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
        if (node) {
            //数据相同，直接return
            GXTemplateData *templateData = node.orignalData;
            if (![data isEqualToDictionary:templateData.data]) {
                //重新绑定数据
                GXNode *rootNode = node.rootNode;
                if (rootNode) {
                    //更新数据
                    if (templateData) {
                        node.orignalData.data = data;
                    }
                    
                    //绑定数据
                    [GXDataManager bindData:templateData onRootNode:rootNode fromJS:YES];
                }
            }
        }
    }
}


- (NSNumber *)getIndex:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:extendInfo]) {
        return nil;
    }
    
    //获取js透传的内容
    NSString *templateId = [extendInfo gx_stringForKey:@"templateId"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //获取对应的节点
    NSNumber *result = nil;
    if (templateId.length && instanceIdStr.length) {
        //通过instanceId获取节点
        GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
        if (node && node.index != -1) {
            //返回index
            result = @(node.index);
        }
    }
    
    return result;
}


- (void)refresh:(NSDictionary *)extendInfo {
    if (![GXUtils isValidDictionary:extendInfo]) {
        return;
    }
    
    //获取js透传的内容
    NSString *templateId = [extendInfo gx_stringForKey:@"templateId"];
    NSString *instanceIdStr = [extendInfo gx_stringForKey:@"instanceId"];
    
    //获取对应的节点
    if (templateId.length && instanceIdStr.length) {
        //通过instanceId获取节点
        GXNode *node = [TheGXEventManager templateForkey:instanceIdStr];
        if (node) {
            //向native发送js消息
            id <GXEventProtocal> eventListener = node.templateContext.templateData.eventListener;
            if (eventListener && [eventListener respondsToSelector:@selector(gx_onJSEvent:)]) {
                [eventListener gx_onJSEvent:extendInfo];
            }
        }
    }
}


@end
