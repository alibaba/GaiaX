//
//  GXTemplateContext.h
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
#import "GXTemplateItem.h"
#import "GXTemplateData.h"
#import <UIKit/UIKit.h>
#import "GXNode.h"

@class GXRenderManager;

NS_ASSUME_NONNULL_BEGIN

@interface GXTemplateContext : NSObject

//measure size
@property (nonatomic) CGSize measureSize;
//template information
@property (nonatomic, strong) GXTemplateItem *templateItem;
//template data
@property (nonatomic, strong) GXTemplateData *templateData;

//the root view
@property (nonatomic, weak) UIView *rootView;
//the root node
@property (nonatomic, weak) GXNode *rootNode;
//template content
@property (nonatomic, strong)  NSDictionary *templateInfo;


//is need to refresh the layout again
@property (nonatomic, assign) BOOL isNeedLayout;
//fit-content textNodes
@property (nonatomic, strong) NSPointerArray *textNodes;
//RenderManager
@property (nonatomic, weak) GXRenderManager *renderManager;

@end

NS_ASSUME_NONNULL_END
