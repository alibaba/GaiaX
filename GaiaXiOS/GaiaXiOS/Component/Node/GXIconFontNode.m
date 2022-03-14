//
//  GXIconFontNode.m
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

#import "GXIconFontNode.h"
#import "GXGradientHelper.h"
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "GXBizHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "NSArray+GX.h"
#import "UIColor+GX.h"
#import "GXUIHelper.h"
#import "UIView+GX.h"
#import "GXUtils.h"

NSString * const GX_ICON_FONT_NAME = @"iconfont";

@interface GXIconFontNode (){
    NSString *_originalText;
}

// 文字渐变色背景图
@property(nonatomic, strong) UIColor *gradientColor;

@end


@implementation GXIconFontNode

// 创建视图
- (UIView *)creatView{
    UILabel *view = (UILabel *)self.associatedView;
    if (!view) {
        view = [[UILabel alloc] initWithFrame:CGRectZero];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        //弱引用view
        self.associatedView = view;
    }
    return view;
}

// 渲染视图
- (void)renderView:(UIView *)view{
    //渲染视图
    UILabel *label = (UILabel *)view;
    label.frame = self.frame;
    
    //设置属性
    label.clipsToBounds = self.clipsToBounds;
    label.textAlignment = self.textAlignment;
    label.alpha = self.opacity;
    label.font = self.font;
    
    //背景颜色
    [self setupNormalBackground:label];
    
    //文字颜色
    if (self.linearGradient) {
        [self setupTextGradientColor:label];
    } else {
        label.textColor = self.textColor;
    }
    
}

#pragma mark - 绑定数据

- (void)bindData:(id)data{
    NSString *text = nil;
    // 读取属性赋值
    if ([GXUtils isValidDictionary:data]) {
        NSDictionary *dict = (NSDictionary *)data;
        //获取text
        text = [dict gx_stringForKey:@"value"];
        
        //处理扩展属性 & 计算
        NSDictionary *extend = [dict gx_dictionaryForKey:@"extend"];
        if (extend.count) {
            [self handleExtend:extend isCalculate:NO];
        }
        
        //设置无障碍
        [self setupAccessibilityInfo:dict];
        
    } else {
        text = nil;
    }
    
    //赋值
    if (text && ![text isEqualToString:_originalText]) {
        if ([text hasPrefix:@"&#"]) {
            text = [text stringByReplacingOccurrencesOfString:@";" withString:@""];
            text = [text stringByReplacingOccurrencesOfString:@"&#" withString:@""];
            text = [self utf8ToUnicode:text];
        }
        
        //赋值
        _originalText = text;
        
        //获取label
        UILabel *label = (UILabel *)self.associatedView;
        label.text = text;
    }
    
}

#pragma mark - 处理extend

//处理扩展属性
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{
    //更新布局属性
    BOOL isMark = [self updateLayoutStyle:extend];
    
    //更新样式属性
    if (!isCalculate) {
        [self updateNormalStyle:extend isMark:isMark];
    }
    
    //确认属性发生变化，更新布局
    if (isMark) {
        //更改style + rustPtr
        [self.style updateRustPtr];
        [self setStyle:self.style];
        
        //标记dirty
        [self markDirty];
        
        //重新刷新布局标识
        self.templateContext.isNeedLayout = YES;
    }
}

//设置样式属性
- (void)updateNormalStyle:(NSDictionary *)styleInfo isMark:(BOOL)isMark{
    [super updateNormalStyle:styleInfo isMark:isMark];
    
    UILabel *label = (UILabel *)self.associatedView;
    
    //颜色属性，默认黑色
    NSString *color = [styleInfo gx_stringForKey:@"background-image"] ?: [styleInfo gx_stringForKey:@"color"];
    if (color && [color hasPrefix:@"linear-gradient"]) {
        //设置渐变色
        self.linearGradient = [color stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        [self setupTextGradientColor:label];
        
    } else if (color && ![color isEqualToString:@"null"]) {
        //设置纯色
        self.linearGradient = nil;
        label.gxLinearGradient = nil;
        self.textColor = [UIColor gx_colorWithString:color];
        label.textColor  = self.textColor;
        
    } else {
        //不做处理
    }
    
}

//设置渐变
- (void)setupTextGradientColor:(UILabel *)view{
    //判断
    if (!view || !self.linearGradient ||
        CGSizeEqualToSize(view.bounds.size, CGSizeZero) ||
        ([self.linearGradient isEqualToString:view.gxLinearGradient] &&
         CGSizeEqualToSize(view.gxGradientImage.size, view.bounds.size))){
        return;
    }
    
    //设置属性
    view.gxLinearGradient = self.linearGradient;
    
    //创建渐变
    if (self.linearGradient && [self.linearGradient hasPrefix:@"linear-gradient("] && [self.linearGradient hasSuffix:@")"]) {
        //生成渐变map
        NSDictionary *dict = [GXGradientHelper parserLinearGradient:self.linearGradient];
        
        //设置渐变图片
        UIImage *image = [GXGradientHelper creatGradientImageWithParams:dict bounds:view.bounds];
        if (image) {
            self.gradientColor = [UIColor colorWithPatternImage:image];
            view.textColor = self.gradientColor;
            view.gxGradientImage = image;
        }
        
    }
}


//unicode转utf8
- (NSString *)utf8ToUnicode:(NSString *)string {
    //string 转data
    NSString *iconFontStr = nil;
    while (string.length < 6) {
        string = [NSString stringWithFormat:@"0%@", string];
    }
    
    NSScanner *scanner = [NSScanner scannerWithString:string];
    unsigned int code;
    [scanner scanHexInt:&code];
    iconFontStr = [NSString stringWithFormat:@"%C", (unsigned short)code];
    
    //返回数据
    return iconFontStr;
}


#pragma mark - 计算

- (void)calculateWithData:(id)data{
    //用于计算 & 避免走到父类计算
    if ([GXUtils isValidDictionary:data]) {
        NSDictionary *dataDict = (NSDictionary *)data;
        NSDictionary *extend = [dataDict gx_dictionaryForKey:@"extend"];
        if (extend.count) {
            [self handleExtend:extend isCalculate:YES];
        }
    }
}

#pragma mark - 读取属性
//读取属性
- (void)configureStyleInfo:(NSDictionary *)styleJson{
    [super configureStyleInfo:styleJson];
    
    //注册Iconfont
    [GXBizHelper loadIconFont];
    
    //字体
    NSString *fontSize = [styleJson gx_stringForKey:@"font-size"];
    CGFloat size = [GXStyleHelper converSimpletValue:fontSize] ?: 14.f;
    NSString *fontFamily = [styleJson gx_stringForKey:@"font-family"] ?: GX_ICON_FONT_NAME;
    self.font = [UIFont fontWithName:fontFamily size:size];
    
    //textAlign
    NSString *textAlign = [styleJson gx_stringForKey:@"text-align"];
    if ([textAlign isEqualToString:@"left"]) {
        self.textAlignment = NSTextAlignmentLeft;
    } else if ([textAlign isEqualToString:@"right"]) {
        self.textAlignment = NSTextAlignmentRight;
    } else {
        self.textAlignment = NSTextAlignmentCenter;
    }
    
    //颜色属性，默认黑色
    NSString *color = [styleJson gx_stringForKey:@"color"] ?: @"#000000";
    self.textColor = [UIColor gx_colorWithString:color];
    
    //color = linear-gradient()
    if (!self.linearGradient && [color hasPrefix:@"linear-gradient"]) {
        self.linearGradient = color;
    }
    
}


#pragma mark - 属性设置

- (void)setFont:(UIFont *)font{
    if (_font != font) {
        _font = font;
    }
}

- (void)setTextColor:(UIColor *)textColor{
    if (_textColor != textColor) {
        _textColor = textColor;
    }
}

//alignMent
- (void)setTextAlignment:(NSTextAlignment)textAlignment{
    if (_textAlignment != textAlignment) {
        _textAlignment = textAlignment;
    }
}

@end
