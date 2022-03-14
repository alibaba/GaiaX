//
//  GXData.h
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
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXData : NSObject

//target view
@property (nonatomic, weak) UIView *view;
//node id
@property (nonatomic, copy) NSString *nodeId;
//view index
@property (nonatomic, assign) NSInteger index;
//tempalte id
@property (nonatomic, copy) NSString *templateId;

@end


@interface GXTextData : GXData

//text content
@property (nonatomic, strong) id value;
//composition rich text attributes
@property (nonatomic, weak) NSDictionary *attributes;
//style in css
@property (nonatomic, strong) NSDictionary *styleParams;
//style in databinding/extend
@property (nonatomic, strong) NSDictionary *extendParams;

@end


NS_ASSUME_NONNULL_END
