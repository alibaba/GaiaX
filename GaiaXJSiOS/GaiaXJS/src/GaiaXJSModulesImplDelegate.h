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

NS_ASSUME_NONNULL_BEGIN

@protocol GaiaXJSModulesImplDelegate <NSObject>

/**
 * get element by extendInfo
 * @param extendInfo
 * example:
 * {
    instanceId = 1;
    targetId = "label-a";
    templateId = "template-demo";
 * }
 * @return
 * example
 * {
    targetId = "label-a";
    targetType = "view";
 * } or else nil
 */
- (NSDictionary *)getElement:(NSDictionary *)extendInfo;

/**
 *
 * @param extendInfo
 * @return
 */
- (BOOL)addEventListener:(NSDictionary *)extendInfo;

/**
 */
- (BOOL)removeEventListener:(NSDictionary *)extendInfo;

/**

 */
- (NSDictionary *)getBindingData:(NSDictionary *)extendInfo;

/**

 */

- (void)setBindingData:(NSDictionary *)data extendInfo:(NSDictionary *)extendInfo;

/**
 if not found, return -1; else return index
 */
- (NSNumber *)getIndex:(NSDictionary *)extendInfo;

/**
 refresh component ui
 */
- (void)refresh:(NSDictionary *)extendInfo;

- (void)scrollTo:(NSDictionary *)extendInfo;

@end

NS_ASSUME_NONNULL_END
