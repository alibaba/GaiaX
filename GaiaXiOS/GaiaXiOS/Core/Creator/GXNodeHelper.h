//
//  GXNodeHelper.h
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

NS_ASSUME_NONNULL_BEGIN


typedef NS_ENUM(NSInteger, GXContainerType) {
    GXContainerTypeNone = 0,//非容器类型
    GXContainerTypeGrid,//Grid类型
    GXContainerTypeScroll,//Scroll类型
    GXContainerTypeCustom//自定义类型
};

@interface GXNodeHelper : NSObject

// 获取对应的容器node类型
+ (GXContainerType)loadContainerType:(NSString *)type subType:(NSString *)subType;

// 获取节点的类型Class
+ (Class)nodeClassWithLayerType:(NSString *)type subType:(NSString *)subType;

// 判断是否容器node类型
+ (BOOL)isContainter:(NSString *)type subType:(NSString *)subType;

// 判断是否是自定义的node类型
+ (BOOL)isCustom:(NSString *)type subType:(NSString *)subType;

// 是否为模板类型节点（根模板，嵌套模板）
+ (BOOL)isTemplateType:(NSString *)type;

@end

NS_ASSUME_NONNULL_END
