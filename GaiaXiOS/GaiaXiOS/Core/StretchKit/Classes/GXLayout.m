//
//  GXLayout.m
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

#import "GXLayout.h"

@interface GXLayout ()

@property(nonatomic,assign)const float *pointer;

@end


@implementation GXLayout

- (instancetype)initWithX:(CGFloat)x
                        y:(CGFloat)y
                    width:(CGFloat)width
                   height:(CGFloat)height
                 children:(NSArray *)children
                  pointer:(const float *)pointer {
    if (self = [super init]) {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _children = children;
        _pointer = pointer;
    }
    return self;
}

+ (instancetype)fromFloats:(const float *)floats {
    const float * ptr = floats;
    float x = ptr[0];
    float y = ptr[1];
    float width = ptr[2];
    float height = ptr[3];
    
    int childCount = (int)(ptr[4]);
    NSMutableArray *children = [NSMutableArray array];
    for (int i = 0; i < childCount; i++) {
        GXLayout *child = [GXLayout fromFloats:ptr+5];
        ptr = child.pointer;
        [children addObject:child];
    }
    
    return [[GXLayout alloc] initWithX:x
                                     y:y
                                 width:width
                                height:height
                              children:children
                               pointer:ptr];
}

@end
