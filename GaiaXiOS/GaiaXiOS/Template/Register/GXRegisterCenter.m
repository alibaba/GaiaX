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
#import "NSDictionary+GX.h"
#import "GXUtils.h"

@interface GXRegisterCenter ()

//对应的ImageView的class
@property (nonatomic, strong) Class imageViewClass;
//业务模板注册关系
@property (nonatomic, strong) NSMutableDictionary *bizRegisterMap;

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
    }
    return self;
}


- (NSMutableDictionary *)bizRegisterMap{
    if (!_bizRegisterMap) {
        _bizRegisterMap = [NSMutableDictionary dictionary];
    }
    return _bizRegisterMap;
}


// 注册Lottie动画的class
- (void)registerLottieViewClass:(Class <GXLottieAniamtionProtocal>)aClass{
    if (aClass && [aClass isKindOfClass:[UIView class]] && [aClass conformsToProtocol:@protocol(GXLottieAniamtionProtocal)]) {
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
        case GXNodeTypeImage:
        {
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

//注册预览的数据源
- (void)registerPreviewTemplateSource:(id <GXITemplatePreviewSource>)source{
    if (source && [source conformsToProtocol:@protocol(GXITemplatePreviewSource)]) {
        _previewSource = source;
    }
}

//移除预览的数据源
- (void)unregisterPreviewTemplateSource:(id <GXITemplatePreviewSource>)source{
    if (source == _previewSource) {
        _previewSource = nil;
    }
}

@end



@implementation GXRegisterCenter (Biz)

/// 注册业务模板
- (BOOL)registerTemplateServiceWithBizId:(NSString *)bizId
                          templateBundle:(NSString *)templateBundle{
    //获取前置条件
    if (bizId == nil || templateBundle == nil) {
        return NO;
    }
    
    //获取UserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    //获取模板路径
    NSString *currentBundlePath = [GXTemplatePathHelper loadBizBundlePathWithBundleName:templateBundle];
    
    //获取bundle的name
    NSString *currentBundle = [defaults stringForKey:bizId];
    if (currentBundle.length > 0 && [currentBundle isEqualToString:templateBundle]) {
        [self.bizRegisterMap gx_setObject:currentBundlePath forKey:bizId];
        //已经注册，则直接返回
        return YES;
    }
    
    //重新写入
    [self.bizRegisterMap gx_setObject:currentBundlePath forKey:bizId];
    [defaults setObject:templateBundle forKey:bizId];
    
    return YES;    
}

/// 注销业务离散化服务
- (BOOL)unRegisterTemplateServiceWithBizId:(NSString *)bizId{
    if ([GXUtils isValidString:bizId]) {
        //移除本地存储
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults removeObjectForKey:bizId];
        //移除缓存
        [self.bizRegisterMap removeObjectForKey:bizId];
        return YES;
    } else {
        return NO;
    }
}

/// 通过bizId来获取已经注册过的bundle
- (NSString *)loadTemplateBundlePathForBizId:(NSString *)bizId{
    NSString *bizBundlePath = [self.bizRegisterMap gx_stringForKey:bizId];
    if (!bizBundlePath.length) {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        //获取bundleName
        NSString *bizBundleName = [defaults stringForKey:bizId];
        //获取bundlePath
        bizBundlePath = [GXTemplatePathHelper loadBizBundlePathWithBundleName:bizBundleName];
        //写入缓存
        [self.bizRegisterMap gx_setObject:bizBundlePath forKey:bizId];
    }
    return bizBundlePath;
}

@end
