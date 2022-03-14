//
//  GXTemplateItem.h
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

NS_ASSUME_NONNULL_BEGIN

@interface GXTemplateItem : NSObject

//whether to use local templates
@property (nonatomic, assign) BOOL isLocal;

//biz id
@property (nonatomic, copy, nullable) NSString *bizId;
//template id
@property (nonatomic, copy, nonnull) NSString *templateId;
//template version
@property (nonatomic, copy, nullable) NSString *templateVersion;
       
//Used to override nested template root node style
@property (nonatomic, strong, nullable) NSDictionary *rootStyleInfo;

//template identifier
@property (nonatomic, copy, nullable) NSString *identifier;

//Is valid template information
- (BOOL)isAvailable;

@end

NS_ASSUME_NONNULL_END
