//
//  GXRichTextNode.m
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

#import "GXRichTextNode.h"
#import "NSDictionary+GX.h"
#import <YYText/YYText.h>
#import "GXCacheCenter.h"
#import "GXRichText.h"
#import "UIColor+GX.h"
#import "GXUIHelper.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXRichTextNode (){
    //是否为富文本
    BOOL _isAttribute;
    //高亮属性
    NSString *_highlightTag;//标签（#,@,$等）
    NSString *_highlightColor;//高亮颜色
    //字体属性
    NSDictionary *_highlightFontInfo;
}

//ranges
@property (nonatomic, strong) NSMutableArray *ranges;

@end


@implementation GXRichTextNode

- (UIView *)creatView{
    GXRichText *view = (GXRichText *)self.associatedView;
    if (!view) {
        view = [[GXRichText alloc] initWithFrame:CGRectZero];
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

- (void)renderView:(UIView *)view{
    GXRichText *label = (GXRichText *)view;
    if (!CGRectEqualToRect(label.frame, self.frame)) {
        label.frame = self.frame;
    }
    
    //设置布局属性
    label.alpha = self.opacity;
    label.numberOfLines = self.numberOfLines;
    label.clipsToBounds = self.clipsToBounds;
    
    //内边距
    label.textContainerInset = self.gxPadding;
    
    //设置圆角
    [self setupCornerRadius:label];
    
    //背景颜色
    [self setupNormalBackground:label];
    
    //文字渐变颜色
    if (self.linearGradient) {
        [self setupTextGradientColor:(UILabel *)label];
    }
    
}

#pragma mark - 数据绑定

- (void)bindData:(id)data{
    //取值
    NSString *text = nil;
    NSDictionary *extend = nil;
    // 读取属性赋值
    if ([GXUtils isDictionary:data]) {
        NSDictionary *dict = (NSDictionary *)data;
        //获取text
        text = [dict gx_stringForKey:@"value"];
        //获取扩展属性
        extend = [dict gx_dictionaryForKey:@"extend"];
        //设置无障碍
        [self setupAccessibilityInfo:dict];
    } else {
        //重置为nil
        text = nil;
    }
    
    //处理扩展属性 & 计算
    _highlightTag = nil;
    _highlightColor = nil;
    _highlightFontInfo = nil;
    if (extend.count || self.fitContent) {
        [self handleExtend:extend isCalculate:NO];
    }
    
    //文本处理
    NSString *result = text;
    id <GXDataProtocal> dataList = self.templateContext.templateData.dataListener;
    if (dataList && [dataList respondsToSelector:@selector(gx_onTextProcess:)]) {
        //更新textData
        GXTextData *textData = self.textData;
        if (textData == nil) {
            textData = [[GXTextData alloc] init];
            textData.nodeId = self.nodeId;
            textData.view = self.associatedView;
            textData.styleParams = self.styleJson;
            textData.templateId = self.templateItem.templateId;
        }
        textData.attributes = self.attributes;
        textData.extendParams = extend;
        textData.value = text;
        //回调方法
        NSString *tmpResult = [dataList gx_onTextProcess:textData];
        if (tmpResult) {
            result = tmpResult;
        }
    }
    
    // 获取string
    if (result && [result isKindOfClass:[NSAttributedString class]]) {
        _isAttribute = YES;
    } else {
        _isAttribute = NO;
    }
    
    //处理string
    if (_isAttribute) {
        NSAttributedString *attributedText = (NSAttributedString *)result;
        self.attributedText = attributedText;
        self.text = attributedText.string;
        
        //对于业务传进来的参数，先保留默认数据，在让attribute进行覆盖
        GXRichText *label = (GXRichText *)self.associatedView;
        label.textAlignment = self.textAlignment;
        label.attributedText = attributedText;
    } else {
        //创建富文本 & 高亮
        [self processText:text];
        [self processRichText];
    }
}


//处理扩展属性
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{
    //是否刷新布局标志位 & fitContent属性
    BOOL isMark = [self updateLayoutStyle:extend];
    
    //非计算情况下才更新style
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

//重写样式更新方法
- (void)updateNormalStyle:(NSDictionary *)styleInfo isMark:(BOOL)isMark{
    [super updateNormalStyle:styleInfo isMark:isMark];
    //高亮颜色
    _highlightColor = [styleInfo gx_stringForKey:@"highlight-color"];
    
    //高亮字体
    NSString *highlightFontSize = [styleInfo gx_stringForKey:@"highlight-font-size"];
    NSString *highlightFontWeight = [styleInfo gx_stringForKey:@"highlight-font-weight"];
    NSString *highlightFontFamily = [styleInfo gx_stringForKey:@"highlight-font-family"];
    if (highlightFontSize.length || highlightFontWeight.length || highlightFontFamily.length) {
        NSMutableDictionary *tmpFontInfo = [NSMutableDictionary dictionaryWithCapacity:3];
        [tmpFontInfo gx_setObject:highlightFontSize forKey:@"font-size"];
        [tmpFontInfo gx_setObject:highlightFontWeight forKey:@"font-weight"];
        [tmpFontInfo gx_setObject:highlightFontFamily forKey:@"font-family"];
        _highlightFontInfo = tmpFontInfo;
    }
    
    //tag
    NSDictionary *map = [GXRichTextNode regexMap];
    NSString *highlightTag = [styleInfo gx_stringForKey:@"highlight-tag"];
    _highlightTag = [map gx_stringForKey:highlightTag] ?: highlightTag;
}


#pragma mark - 加工文本 & 富文本

//处理文本信息
- (void)processText:(NSString *)text{
    NSString *resultStr = text;
    NSMutableArray *resultRanges = nil;
    if (resultStr && _highlightTag && (_highlightColor || _highlightFontInfo)) {
        //获取标签内容
        NSString *regexStr = [NSString stringWithFormat:@"%@(.*?)%@", _highlightTag, _highlightTag];
        NSRegularExpression *regex = [self regularExpressionWithValue:regexStr];
        if (regex) {
            //获取基础数据
            NSRange resultRange = NSMakeRange(0, resultStr.length);
            NSArray *matches = [regex matchesInString:resultStr options:NSMatchingReportProgress range:resultRange];
            
            if (matches.count > 0) {
                resultRanges = [NSMutableArray array];
                //遍历处理string
                for (int i = 0; i < matches.count; i++) {
                    NSTextCheckingResult *result = matches[i];
                    NSRange matchRange = NSMakeRange(result.range.location - 2 * i, result.range.length);
                    
                    //获取去标识的string
                    if (matchRange.length > 2) {
                        NSRange subMatchRange = NSMakeRange(matchRange.location + 1, matchRange.length - 2);
                        NSString *subStr = [resultStr substringWithRange:subMatchRange];
                        resultStr = [resultStr stringByReplacingCharactersInRange:matchRange withString:subStr];
                        
                        //校正之后的range
                        NSRange resultMatchRange = NSMakeRange(matchRange.location, matchRange.length - 2);
                        [resultRanges addObject:[NSValue valueWithRange:resultMatchRange]];
                    }
                }
            }
        }
    }
    
    self.text = resultStr;
    self.ranges = resultRanges;
}

//处理富文本
- (void)processRichText{
    //基础属性设置
    GXRichText *label = (GXRichText *)self.associatedView;
    
    //内容为空
    if (!self.text.length) {
        self.attributedText = nil;
        label.attributedText = nil;
        return;
    }
    
    //内容不为空
    NSString *resultStr = self.text;
    NSMutableArray *resultRanges = self.ranges;
    NSMutableAttributedString *mAttributedText = nil;
    if (self.ranges.count) {
        //生成富文本 & 高亮处理
        UIColor *highLightColor = nil;
        if (_highlightColor) {
            highLightColor = [UIColor gx_colorWithString:_highlightColor];
        }
        UIFont *highLightFont = nil;
        if (_highlightFontInfo) {
            highLightFont = [GXUIHelper fontFromStyle:_highlightFontInfo];
        }
        
        mAttributedText = [[NSMutableAttributedString alloc] initWithString:resultStr attributes:self.attributes];
        for (int i = 0; i < resultRanges.count; i++) {
            //range
            NSValue *resultRangeValue = resultRanges[i];
            NSRange resultRange = [resultRangeValue rangeValue];
            
            //tap
            YYTextHighlight *highlight = [YYTextHighlight highlightWithBackgroundColor:nil];
            highlight.tapAction = ^(UIView * _Nonnull containerView, NSAttributedString * _Nonnull text, NSRange range, CGRect rect) {
                GXLog(@"[GaiaX] 点击内容：%@", text);
            };
            [mAttributedText yy_setTextHighlight:highlight range:resultRange];
            
            //color
            if (highLightColor) {
                [mAttributedText yy_setColor:highLightColor range:resultRange];
            }
            
            //font
            if (highLightFont) {
                [mAttributedText yy_setFont:highLightFont range:resultRange];
            }
            
        }
        
    } else {
        //不符合高亮规范
        mAttributedText = [[NSMutableAttributedString alloc] initWithString:resultStr attributes:self.attributes];
    }
    
    //赋值text
    self.attributedText = mAttributedText;
    label.attributedText = mAttributedText;
}


- (NSRegularExpression *)regularExpressionWithValue:(NSString *)regexStr{
    GXCache *cahche = [GXCacheCenter defaulCenter].regularCahche;
    NSRegularExpression *regular = [cahche objectForKey:regexStr];
    //缓存为空创建表达式
    if (regular == nil) {
        NSError *error = NULL;
        regular = [NSRegularExpression regularExpressionWithPattern:regexStr
                                                            options:NSRegularExpressionCaseInsensitive
                                                              error:&error];
        [cahche setObject:regular forKey:regexStr];
    }
    //返回
    return regular;
}

+ (NSDictionary *)regexMap
{
    static NSDictionary *_regexMap;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _regexMap = @{@"^": @"\\^",
                      @"$": @"\\$",
                      @"*": @"\\*",
                      @"+": @"\\+",
                      @"?": @"\\?",
                      @".": @"\\."};
    });
    return _regexMap;
}


#pragma mark - 计算Size

- (void)calculateWithData:(id)data{
    NSString *text = nil;
    NSDictionary *extend = nil;
    
    // 读取属性赋值
    if ([GXUtils isValidDictionary:data]) {
        NSDictionary *dict = (NSDictionary *)data;
        //读取text
        text = [dict gx_stringForKey:@"value"];
        //读取扩展属性
        extend = [dict gx_dictionaryForKey:@"extend"];
        //处理扩展属性
        if (extend.count) {
            [self handleExtend:extend isCalculate:YES];
        }
    } else {
        text = nil;
    }
    
    //生成对应的文本
    [self processText:text];
}


@end
