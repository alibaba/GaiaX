//
//  GXImageNode.m
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

#import "GXImageNode.h"
#import "NSDictionary+GX.h"
#import "GXImageView.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXUIhelper.h"
#import "UIImage+GX.h"
#import "UIColor+GX.h"
#import "UIView+GX.h"
#import "GXUTils.h"

@interface GXImageNode (){
    //当前控件的宽高
    CGFloat _imgWidth;
    CGFloat _imgHeight;
    
    //布局变化之后是否需要重新刷新数据
    BOOL _isNeedReload;
    BOOL _sizeDidChanged;
}

//图片数据
@property(nonatomic, strong) NSDictionary *imgData;
//裁剪模式（nil, scale, crop）
@property(nonatomic, strong) NSString *modeType;

@end

@implementation GXImageNode

//创建视图
- (UIView *)creatView{
    UIImageView *view = (UIImageView *)self.associatedView;
    if (!view) {
        view = [[GXImageView alloc] initWithFrame:CGRectZero];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        //弱引用view
        self.associatedView = view;
        //支持渐变背景
        self.isSupportGradientBgColor = YES;
    }
    return view;
}


//渲染视图
- (void)renderView:(UIView *)view{
    //渲染视图
    UIImageView *imageView = (UIImageView *)view;
    if (!CGRectEqualToRect(imageView.frame, self.frame)) {
        imageView.frame = self.frame;
        //宽高比较 & 赋值
        CGFloat width = self.frame.size.width;
        CGFloat height = self.frame.size.height;
        if (_imgWidth != width || _imgHeight != height) {
            _imgWidth = width;
            _imgHeight = height;
            _sizeDidChanged = YES;
        }
    }
    
    //设置属性
    imageView.alpha = self.opacity;
    imageView.contentMode = self.contentMode;
    imageView.clipsToBounds = self.clipsToBounds;
    
    //设置阴影
    [self setupShadow:imageView];
    
    //设置圆角
    [self setupCornerRadius:imageView];
    
    //创建渐变背景
    if (self.linearGradient) {
        [self setupGradientBackground:imageView];
    } else {
        [self setupNormalBackground:imageView];
    }
    
    //更新frame之后设置图片
    if ([self isValidImageData] && (_isNeedReload || _sizeDidChanged)) {
        _isNeedReload = NO;
        //加载图片
        GXImageView *imgView = (GXImageView *)imageView;
        [self setImageView:imgView withImageInfo:_imgData];
    }
    
    _sizeDidChanged = NO;
}

//数据是否有效
- (BOOL)isValidImageData{
    return [GXUtils isValidDictionary:_imgData];
}


#pragma mark - 数据绑定

- (void)bindData:(id)imgData{
    _isNeedReload = NO;
    _imgData = imgData;
    
    //获取view
    GXImageView *imgView = (GXImageView *)self.associatedView;

    //数据类型判断
    if ([GXUtils isDictionary:imgData]) {
        //读取 & 处理扩展属性
        NSDictionary *imgDict = (NSDictionary *)imgData;
        NSDictionary *extend = [imgDict gx_dictionaryForKey:@"extend"];
        if (extend) {
            [self handleExtend:extend isCalculate:NO];
        }
        
        //加载图片
        if (!_isNeedReload) {
            [self setImageView:imgView withImageInfo:imgDict];
        }
        
        //设置无障碍
        [self setupAccessibilityInfo:imgDict];
    } else {
        imgView.image = nil;
    }
    
}

//处理扩展属性
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{
    //更新布局属性
    BOOL isMark = [self updateLayoutStyle:extend];
    
    //更新普通属性
    if (!isCalculate) {
        [self updateNormalStyle:extend isMark:isMark];
    }
    
    //确认属性发生变化，更新布局
    if (isMark) {
        //布局完成之后刷新UI
        _isNeedReload = YES;
        
        //更改style + rustPtr
        [self.style updateRustPtr];
        [self setStyle:self.style];
        
        //标记dirty
        [self markDirty];
        
        //重新刷新布局标识
        self.templateContext.isNeedLayout = YES;
    }
}

//加载图片的方法
- (void)setImageView:(GXImageView *)imgView withImageInfo:(NSDictionary *)imageInfo{
    //尺寸拦截
    if (_imgWidth == 0 || _imgHeight == 0) {
        return;
    }
    
    //重置imageView
    if ([imgView respondsToSelector:@selector(gx_resetForReuse)]) {
        [imgView gx_resetForReuse];
    }
    
    //获取图片value & placeholder
    id imgData = nil;
    NSDictionary *imgDict = nil;
    UIImage *placeholderImage = nil;
    if ([imageInfo isKindOfClass:[NSDictionary class]]) {
        //设置图片map
        imgDict = (NSDictionary *)imageInfo;
        imgData = [imgDict gx_stringForKey:@"value"];
        
        //placeholder
        NSString *placeholder = [imgDict gx_stringForKey:@"placeholder"];
        if (placeholder.length) {
            NSString *tmpImg = placeholder;
            if ([placeholder hasPrefix:kGXComDef_Prefix_Local]) {
                tmpImg = [placeholder stringByReplacingOccurrencesOfString:kGXComDef_Prefix_Local withString:@""];
            }
            placeholderImage = [UIImage imageNamed:tmpImg];
        }
    }
    
    //处理角标，腰封等信息
    if (imgDict) {
        //角标
        NSDictionary *mark = [imgDict gx_dictionaryForKey:@"mark"];
        if (mark && [imgView respondsToSelector:@selector(gx_setMarkInfo:)]) {
            [imgView gx_setMarkInfo:mark];
        }
        
        //腰封
        NSDictionary *summary = [imgDict gx_dictionaryForKey:@"summary"];
        if (summary && [imgView respondsToSelector:@selector(gx_setSummaryInfo:)]) {
            [imgView gx_setSummaryInfo:summary];
        }
    }
    
    //设置图片
    if ([GXUtils isValidString:imgData]) {
        NSString *imageName = (NSString *)imgData;
        if ([imageName hasPrefix:kGXComDef_Prefix_Http] || [imageName hasPrefix:kGXComDef_Prefix_Https]) {
            //图片url
            GXWeakSelf(self)
            GXWeakSelf(imgView)
            [imgView gx_setImageWithURL:[NSURL URLWithString:imgData]
                       placeholderImage:placeholderImage
                              completed:^(UIImage * _Nullable image, NSError * _Nullable error, NSURL * _Nullable imageURL) {
                //加载完成回调
                GXStrongSelf(self)
                if (image && self.modeType.length) {
                    GXStrongSelf(imgView)
                    if ([self.modeType isEqualToString:@"scale"]) {
                        UIImage *newImage = [image gx_resizeWithFitSize:imgView.frame.size];
                        imgView.image = newImage;
                    } else if ([self.modeType isEqualToString:@"crop"]){
                        UIImage *newImage = [image gx_resizeWithCoverSize:imgView.frame.size];
                        imgView.image = newImage;
                    }
                }
            }];
            
        } else {
            //设置图片name
            if ([imgView respondsToSelector:@selector(gx_setLocalImage:)]) {
                NSString *tmpImg = imageName;
                if ([imageName hasPrefix:kGXComDef_Prefix_Local]) {
                    tmpImg = [imageName stringByReplacingOccurrencesOfString:kGXComDef_Prefix_Local withString:@""];
                }
                [imgView gx_setLocalImage:tmpImg];
            }
            
        }
        
    } else {
        //设置image为空
        imgView.image = nil;
        
    }
    
}


#pragma mark - 计算Size

- (void)calculateWithData:(NSDictionary *)data{
    //用于计算 & 避免走到父类计算
    if ([GXUtils isValidDictionary:data]) {
        NSDictionary *extend = [data gx_dictionaryForKey:@"extend"];
        if (extend.count) {
            [self handleExtend:extend isCalculate:YES];
        }
    }
}


#pragma mark - 解析属性

- (void)configureStyleInfo:(NSDictionary *)styleJson{
    [super configureStyleInfo:styleJson];
    
    //获取contentMode
    NSString *mode = [styleJson gx_stringForKey:@"mode"] ?: [styleJson gx_stringForKey:@"background-size"];
    self.contentMode = [GXUIHelper convertContentMode:mode];
    
    //获取裁剪模式
    if (self.contentMode != UIViewContentModeScaleAspectFill &&
        self.contentMode != UIViewContentModeScaleAspectFit &&
        self.contentMode != UIViewContentModeScaleToFill) {
        //获取裁剪模式
        self.modeType = [styleJson gx_stringForKey:@"mode-type"] ?: @"scale";
    }
    
}


#pragma mark - 设置属性

- (void)setContentMode:(UIViewContentMode)contentMode{
    if (_contentMode != contentMode) {
        _contentMode = contentMode;
    }
}


@end
