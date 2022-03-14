//
//  GXStretch.m
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

#import "GXStretch.h"
#import "libstretch.h"
#import <pthread/pthread.h>

GXLayout *create_layout(const float *floats) {
    GXLayout *layout = [GXLayout fromFloats:floats];
    return layout;
}

@interface GXStretch (){
    //rust指针
    void *_stretchptr;
    //信号量
    dispatch_semaphore_t _semaphore;
}

@end


@implementation GXStretch

+ (instancetype)stretch{
    static GXStretch *stretch = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (nil == stretch) {
            stretch = [[GXStretch alloc] init];
        }
    });
    return stretch;
}

- (instancetype)init{
    if (self = [super init]) {
        //初始化信号量
        _semaphore = dispatch_semaphore_create(1);
        //初始化rust指针
        _stretchptr = stretch_init();
    }
    return self;
}

//创建节点
- (void *)creatNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    void *rustPtr = stretch_node_create(_stretchptr, node);
    dispatch_semaphore_signal(_semaphore);
    return rustPtr;
}

//释放节点
- (void)freeNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_free(_stretchptr, node);
    dispatch_semaphore_signal(_semaphore);
}

//为节点更新style
- (void)setStyle:(void *)style forNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_set_style(_stretchptr, node, style);
    dispatch_semaphore_signal(_semaphore);
}

//标记节点dirty
- (void)markDirty:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_mark_dirty(_stretchptr, node);
    dispatch_semaphore_signal(_semaphore);
}

//获取节点是否dirty
- (BOOL)isDirty:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    BOOL isDirty = stretch_node_dirty(_stretchptr, node);
    dispatch_semaphore_signal(_semaphore);
    return isDirty;
}

//添加child
- (void)addChild:(void *)child forNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_add_child(_stretchptr, node, child);
    dispatch_semaphore_signal(_semaphore);
}

//移除child
- (void)removeChild:(void *)child forNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_remove_child(_stretchptr, node, child);
    dispatch_semaphore_signal(_semaphore);
}

//通过index移除child
- (void)removeChildAtIndex:(NSInteger)index forNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_remove_child_at_index(_stretchptr, node, index);
    dispatch_semaphore_signal(_semaphore);
}

//通过index替换child
- (void)replaceChild:(void *)child atIndex:(NSInteger)index forNode:(void *)node{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    stretch_node_replace_child_at_index(_stretchptr, node, index, child);
    dispatch_semaphore_signal(_semaphore);
}

//计算布局
- (GXLayout *)computeLayout:(void *)node witSize:(StretchSize)size{
    dispatch_semaphore_wait(_semaphore, DISPATCH_TIME_FOREVER);
    float width = size.width ?: NAN;
    float height = size.height ?: NAN;
    GXLayout *layout = (__bridge GXLayout *)stretch_node_compute_layout(_stretchptr, node, width, height, (void *)create_layout);
    dispatch_semaphore_signal(_semaphore);
    return layout;
}


@end
