//
//  GXNodeHelper.m
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

#import "GXNodeHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"


@implementation GXNodeHelper

//获取对应的容器类型
+ (GXContainerType)loadContainerType:(NSString *)type subType:(NSString *)subType{
    //默认类型None
    GXContainerType containerType = GXContainerTypeNone;
    //类型判断
    if ([type isEqualToString:kGXComDef_KW_GaiaTemplate]){
        //判断subLayer的类型
        if ([subType isEqualToString:kGXComDef_KW_Grid]){
            containerType = GXContainerTypeGrid;
        } else if ([subType isEqualToString:kGXComDef_KW_Scroll]){
            containerType = GXContainerTypeScroll;
        }else if ([subType isEqualToString:kGXComDef_KW_Slider]){
            containerType = GXContainerTypeSlider;
        }
    }
    
    return containerType;
}


// 获取节点的类型Class
+ (Class)nodeClassWithLayerType:(NSString *)type subType:(NSString *)subType{
    Class nodeClass = nil;
    
    if (subType.length > 0) {
        GXContainerType containerType = [self loadContainerType:type subType:subType];
        switch (containerType) {
            case GXContainerTypeGrid:{
                //容器grid
                nodeClass = NSClassFromString(@"GXGridNode");
            }
                break;
            case GXContainerTypeScroll:{
                //容器scroll
                nodeClass = NSClassFromString(@"GXScrollNode");
            }
                break;
            case GXContainerTypeSlider:{
                //容器scroll
                nodeClass = NSClassFromString(@"GXSliderNode");
            }
                break;
            default:{
                //默认GXViewNode
                nodeClass = NSClassFromString(@"GXViewNode");
            }
                break;
        }
        
    } else {
        
        if ([type isEqualToString:@"view"]) {
            //view类型
            nodeClass = NSClassFromString(@"GXViewNode");
            
        } else if ([type isEqualToString:@"image"]) {
            //image类型
            nodeClass = NSClassFromString(@"GXImageNode");
            
        } else if ([type isEqualToString:@"text"]) {
            //text类型
            nodeClass = NSClassFromString(@"GXTextNode");
            
        } else if ([type isEqualToString:@"richtext"]) {
            //iconFont类型
            nodeClass = NSClassFromString(@"GXRichTextNode");
            
        } else if ([type isEqualToString:@"iconfont"]) {
            //iconFont类型
            nodeClass = NSClassFromString(@"GXIconFontNode");
            
        }  else if ([type isEqualToString:@"custom"]) {
            //custom,自定义节点类型
            nodeClass = NSClassFromString(@"GXCustomNode");
            
        }  else if ([type isEqualToString:@"progress"]) {
            //custom,自定义节点类型
            nodeClass = NSClassFromString(@"GXProgressNode");
            
        } else if ([type isEqualToString:@"gaia-template"]) {
            //根节点类型
            nodeClass = NSClassFromString(@"GXRootViewNode");
            
        } else {
            //默认View类型
            nodeClass = NSClassFromString(@"GXViewNode");
        }
        
    }
    
    return nodeClass;
}


//判断是否容器类型
+ (BOOL)isContainter:(NSString *)type subType:(NSString *)subType {
    //默认类型
    BOOL isContainer = NO;
    //类型判断
    if ([type isEqualToString:kGXComDef_KW_GaiaTemplate] &&
        ([subType isEqualToString:kGXComDef_KW_Grid] || [subType isEqualToString:kGXComDef_KW_Scroll] || [subType isEqualToString:kGXComDef_KW_Slider])) {
        // scroll & grid &slider
        isContainer = YES;
    }
    
    return isContainer;
}


//获取自定义类型
+ (BOOL)isCustom:(NSString *)type subType:(NSString *)subType{
    //默认NO
    BOOL isCustom = NO;
    //类型判断
    if ([type isEqualToString:kGXComDef_KW_GaiaTemplate] && [subType isEqualToString:@"custom"]) {
        isCustom = YES;
    }
    
    return isCustom;
}


//是否为模板类型
+ (BOOL)isTemplateType:(NSString *)type{
    return [type isEqualToString:kGXComDef_KW_GaiaTemplate];
}


@end
