//
//  GXTemplateSource.h
//  GaiaXiOS
//
//  Created by 张敬成 on 2022/5/25.
//

#import <Foundation/Foundation.h>
#import "GXTemplateSourceProtocal.h"

NS_ASSUME_NONNULL_BEGIN

@interface GXTemplateSource : NSObject <GXTemplateSourceProtocal>

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
