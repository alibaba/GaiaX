//
//  UIImage+GX.m
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

#import "UIImage+GX.h"

@implementation UIImage (GX)

-(UIImage *)gx_resizeWithFitSize:(CGSize)newSize{
    if (self.size.width == 0 || self.size.height == 0) {
        return self;
    }
    
    //默认宽高
    CGFloat imgWidth = 0;
    CGFloat imgHeight = 0;
    
    //获取比例
    CGFloat widthRatio = newSize.width / self.size.width;
    CGFloat heightRatio = newSize.height / self.size.height;

    //获取正确的宽高
    if (self.size.width > self.size.height){
        imgWidth = newSize.width;
        imgHeight = self.size.height * widthRatio;
    } else {
        imgHeight = newSize.height;
        imgWidth = self.size.width * heightRatio;
    }
    
    if (imgWidth > newSize.width){
        imgWidth = newSize.width;
        imgHeight = self.size.height * widthRatio;
    }
    
    if (imgHeight > newSize.height) {
        imgHeight = newSize.height;
        imgWidth = self.size.width * heightRatio;
    }
    
    return [self gx_resizeWithSize:CGSizeMake(imgWidth, imgHeight)];
}

-(UIImage *)gx_resizeWithCoverSize:(CGSize)newSize{
    if (self.size.width == 0 || self.size.height == 0) {
        return self;
    }

    //默认宽高
    CGFloat imgWidth = 0;
    CGFloat imgHeight = 0;
    
    //获取比例
    CGFloat widthRatio = newSize.width / self.size.width;
    CGFloat heightRatio = newSize.height / self.size.height;
    
    //获取正确的宽高
    if (heightRatio > widthRatio) {
        imgHeight = newSize.height;
        imgWidth = self.size.width * heightRatio;
    } else  {
        imgWidth = newSize.width;
        imgHeight = self.size.height * widthRatio;
    }
    
    return [self gx_resizeWithSize:CGSizeMake(imgWidth, imgHeight)];
}


-(UIImage *)gx_resizeWithFillSize:(CGSize)newSize{
    if (self.size.width == 0 || self.size.height == 0) {
        return self;
    }

    return [self gx_resizeWithSize:newSize];
}


-(UIImage *)gx_resizeWithSize:(CGSize)newSize{
    //获取图片的scale
    CGFloat scale = self.scale;
    if (scale == 1) {
        scale = [[UIScreen mainScreen] scale];
    }
    
    //获取最终size
    CGFloat imgWidth = newSize.width * scale;
    CGFloat imgHeight = newSize.height * scale;

    @try {
        //结果image
        UIImage *newImage = nil;
        
        //创建context
        CGContextRef context = CGBitmapContextCreate(NULL, imgWidth, imgHeight,
                                                   CGImageGetBitsPerComponent(self.CGImage), 0,
                                                   CGImageGetColorSpace(self.CGImage),
                                                   CGImageGetBitmapInfo(self.CGImage));
        if (context){
            //设置image质量
            CGContextSetShouldAntialias(context, true);
            CGContextSetAllowsAntialiasing(context, true);
            CGContextSetInterpolationQuality(context, kCGInterpolationHigh);
            
            //congtext中绘图
            UIGraphicsPushContext(context);
            CGContextDrawImage(context, CGRectMake(0.f, 0.f, imgWidth, imgHeight), self.CGImage);
            UIGraphicsPopContext();
            
            //从context中创建image
            CGImageRef newImageRef = CGBitmapContextCreateImage(context);
            newImage = [UIImage imageWithCGImage:newImageRef scale:scale orientation:self.imageOrientation];
            
            //释放
            CGImageRelease(newImageRef);
            CGContextRelease(context);
        }
        
        return newImage;
    } @catch (NSException *exception) {
        return self;
    }
}

@end
