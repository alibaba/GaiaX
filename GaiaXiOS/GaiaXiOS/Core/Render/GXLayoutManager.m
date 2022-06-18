//
//  GXLayoutManager.m
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

#import "GXLayoutManager.h"
#import "GXTemplateContext.h"
#import "GXNodeTreeCreator.h"
#import "GXRenderManager.h"
#import "NSDictionary+GX.h"
#import "GXTemplateItem.h"
#import "GXDataManager.h"
#import "GXTextNode.h"
#import "NSArray+GX.h"
#import "GXLayout.h"
#import "GXNode.h"

@interface GXLayoutManager ()

//模板创建
@property (nonatomic, strong) GXNodeTreeCreator *creator;
//模板对应的缓存id
@property (nonatomic, strong) NSMutableDictionary *templateCache;

@end


@implementation GXLayoutManager

//计算模板真实Size
- (CGSize)sizeWithTemplateItem:(GXTemplateItem *)templateItem
                   measureSize:(CGSize)measureSize{
    CGSize size = CGSizeZero;
    
    //模板有效性
    if ([templateItem isAvailable]) {
        
        //获取节点
        NSString *templateKey = [NSString stringWithFormat:@"%@-%f",templateItem.identifier, measureSize.width];
        GXNode *rootNode = (GXNode *)[self.templateCache objectForKey:templateKey];
        if (rootNode == nil) {
            GXTemplateContext *ctx = [self creatTemplateContext:templateItem measureSize:measureSize];
            rootNode = [self.creator creatNodeTreeWithTemplateItem:templateItem context:ctx];
            if (rootNode) {
                [self.templateCache gx_setObject:rootNode forKey:templateKey];
            }
        }
        
        //通过节点计算size
        if (rootNode) {
            GXTemplateContext *ctx = rootNode.templateContext;
            NSString *preMeasureSizeStr = NSStringFromCGSize(ctx.measureSize);
            NSString *measureSizeStr = NSStringFromCGSize(measureSize);
            //判断measureSize是否一致
            if ([preMeasureSizeStr isEqualToString:measureSizeStr]) {
                //获取layout，如果已经存在直接读取，否则计算
                if (CGSizeEqualToSize(rootNode.frame.size, CGSizeZero)) {
                    //计算size
                    GXLayout *layout = [self computeLayout:ctx];
                    rootNode.frame = CGRectMake(layout.x, layout.y, layout.width, layout.height);
                    size = CGSizeMake(layout.width, layout.height);
                    
                } else {
                    //读取size
                    size = rootNode.frame.size;
                }
                
            } else {
                //更新measureSize
                ctx.measureSize = measureSize;
                //计算size
                GXLayout *layout = [self computeLayout:ctx];
                rootNode.frame = CGRectMake(layout.x, layout.y, layout.width, layout.height);
                size = CGSizeMake(layout.width, layout.height);
            }
            
        }
        
    }
    
    return size;
}



//通过data计算模板的真实Size
- (CGSize)sizeWithTemplateItem:(GXTemplateItem *)templateItem
                   measureSize:(CGSize)measureSize
                          data:(GXTemplateData *)data{
    CGSize size = CGSizeZero;
    
    //模板有效性
    if ([templateItem isAvailable]) {
        
        //获取节点
        NSString *templateKey = [NSString stringWithFormat:@"%@-%f",templateItem.identifier, measureSize.width];
        GXNode *rootNode = (GXNode *)[self.templateCache objectForKey:templateKey];
        if (rootNode == nil) {
            GXTemplateContext *ctx = [self creatTemplateContext:templateItem measureSize:measureSize];
            rootNode = [self.creator creatNodeTreeWithTemplateItem:templateItem context:ctx];
            if (rootNode) {
                [self computeAndApplyLayout:ctx];
                [self.templateCache gx_setObject:rootNode forKey:templateKey];
            }
        }
        
        //根据数据计算layout
        if (rootNode && [data isAvailable]) {
            //获取context
            GXTemplateContext *ctx = rootNode.templateContext;
            ctx.measureSize = measureSize;
            ctx.templateData = data;
            
            //数据绑定,更新布局
            [GXDataManager calculateData:data onRootNode:rootNode];
            
            //文字二次更新
            if (ctx.textNodes.count) {
                [ctx.renderManager computeAndApplyLayout:ctx];
                for (GXTextNode *textNode in ctx.textNodes) {
                    [textNode updateFitContentLayout];
                }
            }
            
            //最终计算
            GXLayout *layout = [self computeLayout:ctx];
            if (layout) {
                size = CGSizeMake(layout.width, layout.height);
            }
        }
        
    }
    
    return size;
}


#pragma mark - compute

//计算layout并绑定到节点
- (BOOL)computeAndApplyLayout:(GXTemplateContext *)ctx{
    GXNode *node = ctx.rootNode;
    GXLayout *layout = [self computeLayout:ctx];
    if (layout) {
        [node applyLayout:layout];
        return YES;
    }
    return NO;
}

//计算layout
- (GXLayout *)computeLayout:(GXTemplateContext *)ctx{
    GXNode *node = ctx.rootNode;
    CGSize size = ctx.measureSize;
    GXLayout *layout = [node computeLayout:(StretchSize){.width = size.width, .height = size.height}];
    return layout;
}


#pragma mark - context

//创建context
- (GXTemplateContext *)creatTemplateContext:(GXTemplateItem *)templateItem measureSize:(CGSize)measureSize{
    GXTemplateContext *context = [[GXTemplateContext alloc] init];
    context.templateItem = templateItem;
    context.measureSize = measureSize;
    return context;
}


#pragma mark - lazy load

- (GXNodeTreeCreator *)creator{
    if (!_creator) {
        _creator = [[GXNodeTreeCreator alloc] init];
    }
    return _creator;
}

- (NSMutableDictionary *)templateCache{
    if (!_templateCache) {
        _templateCache = [NSMutableDictionary dictionary];
    }
    return _templateCache;
}


@end
