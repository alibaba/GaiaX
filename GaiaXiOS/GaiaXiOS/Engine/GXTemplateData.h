//
//  GXTemplateData.h
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
#import <GaiaXiOS/GXEventProtocal.h>
#import <GaiaXiOS/GXTrackProtocal.h>
#import <GaiaXiOS/GXDataProtocal.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXTemplateData : NSObject

//Data listener
@property (nonatomic, weak) id <GXDataProtocal> dataListener;
//Event listener
@property (nonatomic, weak) id <GXEventProtocal> eventListener;
//Track listener
@property (nonatomic, weak) id <GXTrackProtocal> trackListener;

//Raw data
@property (nonatomic, strong) NSDictionary *data;
//Result data
@property (nonatomic, strong) NSMutableDictionary *resultData;

//Is valid data
- (BOOL)isAvailable;

@end

NS_ASSUME_NONNULL_END

