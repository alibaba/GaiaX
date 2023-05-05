//
//  GXImageView.m
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

#import "GXImageView.h"
#import <SDWebImage/UIImageView+WebCache.h>
#import <SDWebImage/UIImage+Metadata.h>
#import "GXFunctionDef.h"
#import "GXCommonDef.h"

@interface GXImageView ()

@property(nonatomic,strong) UIImage *defaultImage;

@end

@implementation GXImageView

#pragma mark - 图片加载

//本地图片
- (void)gx_setLocalImage:(NSString *)name{
    UIImage *image = [UIImage imageNamed:name];
    if (nil == image) {
        image = self.defaultImage;
    }
    self.image = image;
}

//网络图片
- (void)gx_setImageWithURLString:(NSString *)urlString
                placeholderImage:(UIImage *)placeholder
                       completed:(GXImageCompletionBlock)completedBlock{
    GXWeakSelf(self);
    //urlstring是否需要encode
    [self sd_setImageWithURL:[NSURL URLWithString:urlString]
            placeholderImage:placeholder
                   completed:^(UIImage * _Nullable image, NSError * _Nullable error, SDImageCacheType cacheType, NSURL * _Nullable imageURL) {
        if (image.images.count > 0 && image) { // image count > 0 means is animated image
            GXStrongSelf(self);
            self.animationImages = image.images;
            self.animationDuration = image.duration;
            self.animationRepeatCount = image.sd_imageLoopCount;
            self.image = image.images.lastObject;
            [self startAnimating];
        }
        //结果回调
        if (completedBlock) {
            completedBlock(image, error, imageURL);
        }
    }];
}

//重置
- (void)gx_resetForReuse{
    //to do
}

#pragma mark - 其他协议

//角标信息
- (void)gx_setMarkInfo:(NSDictionary *)markInfo{
    
}

//腰封/评分
- (void)gx_setSummaryInfo:(NSDictionary *)summaryInfo{
    
}


@end
