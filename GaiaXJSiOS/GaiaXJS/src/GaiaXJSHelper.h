/*
 * Copyright (c) 2022, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class GaiaXJSMethodInfo;
@class GaiaXJSModuleInfo;
@class GaiaXJSMethodArgument;

@interface GaiaXJSHelper : NSObject

+ (NSString *)toJSName:(NSString *)input;

+ (NSString *)generateJSMethodString:(GaiaXJSMethodInfo *)moduleMethod
                         moduleIndex:(NSInteger)moduleIndex
                         methodIndex:(NSInteger)methodIndex;

+ (NSString *)getHelperJSMethods;

+ (NSString *)generateJSPropsStringWithClass:(NSString *)className key:(NSString *)keyString;

+ (NSString *)generateJSStyleStringWithClass:(NSString *)className key:(NSString *)keyString;

+ (NSString *)removeGaiaXPrefix:(NSString *)moduleName;

NSString *GaiaXJSParseType(const char **input);

NSString *GaiaXJSParseMethodSignature(const char *input, NSArray<GaiaXJSMethodArgument *> **arguments);


+ (NSUserDefaults *)getUserDefaults;


@end

NS_ASSUME_NONNULL_END
