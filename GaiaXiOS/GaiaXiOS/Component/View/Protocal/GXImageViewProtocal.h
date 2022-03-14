//
//  GXImageViewProtocal.h
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

//图片加载回调
typedef void(^GXImageCompletionBlock)(UIImage * _Nullable image, NSError * _Nullable error, NSURL * _Nullable imageURL);


@protocol GXImageViewProtocal <NSObject>

@optional
//reset
- (void)gx_resetForReuse;

//set mark
- (void)gx_setMarkInfo:(NSDictionary *)markInfo;

//set summary
- (void)gx_setSummaryInfo:(NSDictionary *)summaryInfo;


@required
//load local image
- (void)gx_setLocalImage:(NSString *)name;

//Load web image
- (void)gx_setImageWithURL:(NSURL *)url placeholderImage:(nullable UIImage *)placeholder completed:(GXImageCompletionBlock)completedBlock;

@end

NS_ASSUME_NONNULL_END
