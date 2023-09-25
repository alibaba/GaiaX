//
//  GaiaJSEventManager.h
//  GaiaXCore
//
//  Created by zjc on 2021/7/2.
//  Copyright © 2021 zhangjc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <GaiaXJS/GaiaXJS.h>

NS_ASSUME_NONNULL_BEGIN

#define TheGXJSDelegateImplManager  [GXJSDelegateImplManager  defaultManager]

@interface GXJSDelegateImplManager : NSObject<GaiaXJSModulesImplDelegate>

+ (instancetype)defaultManager;

@end

NS_ASSUME_NONNULL_END
