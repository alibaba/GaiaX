//
//  GXStretch.h
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
#import "GXLayout.h"
#import "GXStyle.h"

NS_ASSUME_NONNULL_BEGIN

@interface GXStretch : NSObject

/// 获取渲染引擎单例
+ (instancetype)stretch;

//创建节点
- (void *)creatNode:(void *)node;
//释放节点
- (void)freeNode:(void *)node;

//为节点更新style
- (void)setStyle:(void *)style forNode:(void *)node;

//标记节点dirty
- (void)markDirty:(void *)node;
//获取节点是否dirty
- (BOOL)isDirty:(void *)node;

//添加child
- (void)addChild:(void *)child forNode:(void *)node;
//移除child
- (void)removeChild:(void *)child forNode:(void *)node;
//通过index移除child
- (void)removeChildAtIndex:(NSInteger)index forNode:(void *)node;
//通过index替换child
- (void)replaceChild:(void *)child atIndex:(NSInteger)index forNode:(void *)node;

//计算布局
- (GXLayout *)computeLayout:(void *)node witSize:(StretchSize)size;


@end

NS_ASSUME_NONNULL_END
