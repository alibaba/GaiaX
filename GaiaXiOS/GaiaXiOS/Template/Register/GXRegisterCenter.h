//
//  GXRegisterCenter.h
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
#import <GaiaXiOS/GXLottieAniamtionProtocal.h>
#import <GaiaXiOS/GXBizServiceProtocol.h>
#import <GaiaXiOS/GXImageViewProtocal.h>
#import <GaiaXiOS/GXITemplateSource.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, GXNodeType) {
    GXNodeTypeNone = 0,
    GXNodeTypeImage
};

#define TheGXRegisterCenter [GXRegisterCenter defaultCenter]

@interface GXRegisterCenter : NSObject

//Support Lottie implementation corresponding to business registration
@property (nonatomic, readonly) Class <GXLottieAniamtionProtocal> lottieViewClass;
//Support ImageView implementation corresponding to business registration
@property (nonatomic, readonly) Class <GXImageViewProtocal> imageViewClass;
//Class impl supported by business capabilities
@property (nonatomic, strong) Class <GXBizServiceProtocol> bizServiceImpl;
//previewed data source
@property (nonatomic, strong) id <GXITemplateSource> previewSource;

/// singleton
+ (instancetype)defaultCenter;


/// Register the class of Lottie animation
/// @param aClass lottieView class
- (void)registerLottieViewClass:(Class <GXLottieAniamtionProtocal>)aClass;


/// Register the view class corresponding to the node
/// @param aClass view class
/// @param type node type
- (void)registerClass:(Class)aClass forNodeType:(GXNodeType)type;


/// Register the impl for business capability support
/// @param impl Impl corresponding to the business
- (void)registerBizServiceImpl:(Class <GXBizServiceProtocol>)impl;

@end


@interface GXRegisterCenter (Preview)

/// Registering a preview data source
/// @param source data source
- (void)registerPreviewTemplateSource:(id <GXITemplateSource>)source;

/// Remove a preview data source
/// @param source data source
- (void)unregisterPreviewTemplateSource:(id <GXITemplateSource>)source;

@end


@interface GXRegisterCenter (Biz)

/// Register Business Template
/// @param bizId biz id
/// @param templateBundle template bundle，eg: GaiaX.bundle
- (BOOL)registerTemplateServiceWithBizId:(NSString *)bizId
                          templateBundle:(NSString *)templateBundle;

/// unRegister Business Template
/// @param bizId biz id
- (BOOL)unRegisterTemplateServiceWithBizId:(NSString *)bizId;

/// Get the registered bundle by bizId
/// @param bizId biz id
- (NSString *)loadTemplateBundlePathForBizId:(NSString *)bizId;


@end


NS_ASSUME_NONNULL_END
