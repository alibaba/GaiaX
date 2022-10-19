//
//  GXRegisterCenter.m
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

#import "GXRegisterCenter.h"
#import "GXTemplatePathHelper.h"
#import "GXTemplateSource.h"
#import "NSDictionary+GX.h"
#import "GXFunctionDef.h"
#import "NSArray+GX.h"
#import "GXUtils.h"

@interface GXRegisterCenter (){
    //外部数据源
    BOOL _isNeedSort;
    NSArray *_resultSources;
    NSMutableDictionary *_kvSources;
    //默认内部数据源
    GXTemplateSource *_defaultTemplateSource;
}

//对应的ImageView的class
@property (nonatomic, strong) Class imageViewClass;

@end


@implementation GXRegisterCenter

+ (instancetype)defaultCenter {
    static GXRegisterCenter *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if (nil == instance) {
            instance = [[GXRegisterCenter alloc] init];
        }
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        //默认的imageClass
        self.imageViewClass = NSClassFromString(@"GXImageView");
        //数据源数组
        _kvSources = [NSMutableDictionary dictionary];
        
        //默认内部数据源
        _defaultTemplateSource = [[GXTemplateSource alloc] init];
        [self registerTemplateSource:_defaultTemplateSource];
    }
    return self;
}


- (id <GXTemplateSourceProtocal>)defaultTemplateSource{
    return _defaultTemplateSource;
}

- (NSArray <GXTemplateSourceProtocal> *)templateSources{
    if (_isNeedSort) {
        _isNeedSort = NO;
        // 进行排序处理
        _resultSources = [self sortByPriority];
    }
    
    return (NSArray <GXTemplateSourceProtocal> *)_resultSources;
}

- (NSArray *)sortByPriority{
    NSMutableArray *resultArray = nil;
    if (_kvSources.count > 0) {
        NSMutableDictionary *tmpDict = [_kvSources mutableCopy];
        NSArray *sortedArray = [[tmpDict allKeys] sortedArrayUsingComparator:^NSComparisonResult(id obj1, id obj2){
            //优先级降序排列，由大到小
            NSInteger priority1 = [obj1 integerValue];
            NSInteger priority2 = [obj2 integerValue];
            if (priority1 > priority2){
                return NSOrderedAscending;//降序
            } else if (priority1 < priority2){
                return NSOrderedDescending;//升序
            } else {
                return NSOrderedSame;
            }
        }];
        
        //读取数据源
        resultArray = [NSMutableArray array];
        for (int i = 0; i < sortedArray.count; i++) {
            NSString *key = [sortedArray objectAtIndex:i];
            NSString *value = [tmpDict objectForKey:key];
            [resultArray gx_addObject:value];
        }
    }
    return resultArray;
}

// 注册Lottie动画的class
- (void)registerLottieViewClass:(Class <GXLottieAniamtionProtocal>)aClass{
    if (aClass && [aClass conformsToProtocol:@protocol(GXLottieAniamtionProtocal)]) {//&& [aClass isKindOfClass:[UIView class]] 
        _lottieViewClass = aClass;
    }
}


//注册节点对应的view
- (void)registerClass:(Class)aClass forNodeType:(GXNodeType)type{
    //异常拦截
    if (!aClass || GXNodeTypeImage != type) {
        return;
    }
    
    // 进行注册
    switch (type) {
        case GXNodeTypeImage: {
            if ([aClass isSubclassOfClass:[UIImageView class]] &&
                [aClass conformsToProtocol:@protocol(GXImageViewProtocal)]) {
                //注册
                self.imageViewClass = aClass;
            }
        }
            break;
            
        default:
            break;
    }
    
}


//注册业务能力支持的impl
- (void)registerBizServiceImpl:(Class)impl{
    if (!impl || ![impl conformsToProtocol:@protocol(GXBizServiceProtocol)]) {
        return;
    }
    self.bizServiceImpl = impl;
}


@end


@implementation GXRegisterCenter (Preview)

- (void)registerTemplateSource:(id <GXTemplateSourceProtocal>)source {
    if (source && [source conformsToProtocol:@protocol(GXTemplateSourceProtocal)]) {
        NSInteger priority = [source priority];
        if (priority >= 0 && priority <= 99) {
            _isNeedSort = YES;
            NSString *key = [NSString stringWithFormat:@"%ld", priority];
            GXAssert(![_kvSources objectForKey:key] , @"已经存在相同优先级的数据源，请重新设置数据源优先级");
            [_kvSources gx_setObject:source forKey:key];
        }
    }
}

- (void)unregisterTemplateSource:(id <GXTemplateSourceProtocal>)source {
    if (source) {
        NSInteger priority = [source priority];
        if (priority >= 0 && priority <= 99) {
            _isNeedSort = YES;
            NSString *key = [NSString stringWithFormat:@"%ld", priority];
            [_kvSources removeObjectForKey:key];
        }
    }
}

@end



@implementation GXRegisterCenter (Biz)

/// 注册业务模板
- (BOOL)registerTemplateServiceWithBizId:(NSString *)bizId
                          templateBundle:(NSString *)templateBundle{
    return [_defaultTemplateSource registerTemplateServiceWithBizId:bizId templateBundle:templateBundle];
}

/// 注销业务离散化服务
- (BOOL)unRegisterTemplateServiceWithBizId:(NSString *)bizId{
    return [_defaultTemplateSource unRegisterTemplateServiceWithBizId:bizId];
}

/// 通过bizId来获取已经注册过的bundle
- (NSString *)loadTemplateBundlePathForBizId:(NSString *)bizId{
    return [_defaultTemplateSource loadTemplateBundlePathForBizId:bizId];
}

@end
