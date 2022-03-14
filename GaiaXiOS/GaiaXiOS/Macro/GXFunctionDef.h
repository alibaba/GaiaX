//
//  GXFunctionDef.h
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

#ifndef GXFunctionDef_h
#define GXFunctionDef_h

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


//Define corner type
typedef struct {
    CGFloat topLeft;
    CGFloat topRight;
    CGFloat bottomLeft;
    CGFloat bottomRight;
} GXCornerRadius;


//Define template file type
typedef NS_ENUM(NSUInteger, GXTemplateFileType) {
    GXTemplateFileTypeNone = 0,
    GXTemplateFileTypeCSS,         //CSS
    GXTemplateFileTypeHierarchy,   //层级
    GXTemplateFileTypeDataBinding, //数据绑定
};

//template load callback
typedef void (^GXTemplateLoadCompletion)(NSDictionary * _Nonnull);


//quote
#define GXWeakSelf(type)    __weak typeof(type) weak##type = type;
#define GXStrongSelf(type)  __strong typeof(type) type = weak##type;

//RGB Color
#define GX_RGB(R,G,B)       [UIColor colorWithRed:R/255.0f green:G/255.0f blue:B/255.0f alpha:1.0f]
#define GX_RGBA(R,G,B,A)    [UIColor colorWithRed:R/255.0f green:G/255.0f blue:B/255.0f alpha:A]

//Log
#if DEBUG == 1
#define GXLog(...) NSLog(__VA_ARGS__)
#else
#define GXLog(...) {}
#endif

//Assert
#if DEBUG == 1
#define GXAssert(...)  NSAssert(__VA_ARGS__)
#else
#define GXAssert(...)  {}
#endif

//is iPad
#define GX_IS_IPAD (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad)

//run in main thread
#define GX_DISPATCH_MAIN_THREAD(block) {                        \
    if (block) {                                                \
        if ([NSThread isMainThread]) {                          \
            block();                                            \
        } else {                                                \
            dispatch_async(dispatch_get_main_queue(), block);   \
        }                                                       \
    }                                                           \
}


#endif /* GXFunctionDef_h */
