//
//  GXTextNode.m
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

#import "GXTextNode.h"
#import <CoreText/CoreText.h>
#import "GXGradientHelper.h"
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "NSArray+GX.h"
#import "UIColor+GX.h"
#import "GXUIHelper.h"
#import "UIView+GX.h"
#import "GXUtils.h"
#import "GXText.h"

//最大行数限制
const NSUInteger GXTextMaxLines = 200;
//最大宽度限制
const NSUInteger GXTextMaxWidth = 1080;

@interface GXTextNode (){
    //是否为富文本
    BOOL _isAttribute;
    //当前颜色
    UIColor *_currentColor;
}

// 文字行高
@property (nonatomic, assign) NSInteger lineHeight;
// 文字渐变色背景图
@property(nonatomic, strong) UIColor *gradientColor;

@end

@implementation GXTextNode

// 创建视图
- (UIView *)creatView{
    GXText *view = (GXText *)self.associatedView;
    if (!view) {
        view = [[GXText alloc] initWithFrame:CGRectZero];
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
    GXText *label = (GXText *)view;
    if (!CGRectEqualToRect(label.frame, self.frame)) {
        label.frame = self.frame;
    }
    
    //设置布局属性
    label.alpha = self.opacity;
    label.numberOfLines = self.numberOfLines;
    label.clipsToBounds = self.clipsToBounds;
    
    //边距
    label.gxPadding = self.gxPadding;
    
    //设置圆角
    [self setupCornerRadius:label];
    
    //背景颜色
    [self setupNormalBackground:label];
    
    //文字渐变颜色
    if (self.linearGradient) {
        [self setupTextGradientColor:label];
    }
    
}


#pragma mark - 绑定数据

- (void)bindData:(NSDictionary *)data{
    NSString *text = nil;
    NSDictionary *extend = nil;
    // 读取属性赋值
    if ([GXUtils isDictionary:data]) {
        //获取text
        text = [data gx_stringForKey:@"value"];
        //获取扩展属性
        extend = [data gx_dictionaryForKey:@"extend"];
        //设置无障碍
        [self setupAccessibilityInfo:data];
        
    } else {
        text = nil;
    }
    
    //处理扩展属性 & 计算
    if (extend || self.fitContent) {
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
        _text = ((NSAttributedString *)result).string;
        _isAttribute = YES;
    } else {
        _text = result;
        _isAttribute = NO;
    }
    
    //赋值
    GXText *label = (GXText *)self.associatedView;
    if (_isAttribute) {
        //对于业务传进来的参数，先保留默认数据，在让attribute进行覆盖
        NSAttributedString *attributedString = (NSAttributedString *)result;
        self.attributedText = attributedString;
        
        label.font = self.font;
        label.textColor  = _currentColor;
        label.textAlignment = self.textAlignment;
        label.attributedText = attributedString;
        label.lineBreakMode = self.lineBreakMode;
        
    } else {
        //对于自身生成的富文本，直接覆盖
        if (_text) {
            NSAttributedString *attributedString = [[NSAttributedString alloc] initWithString:_text attributes:self.attributes];
            label.attributedText = attributedString;
            self.attributedText = attributedString;
            //更新渐变色
            if (self.gradientColor) {
                label.textColor = self.gradientColor;
            }
            
        } else {
            label.attributedText = nil;
            self.attributedText = nil;
        }
    }
    
}


#pragma mark - 计算Size

- (void)calculateWithData:(NSDictionary *)data{
    NSString *text = nil;
    NSDictionary *extend = nil;
    
    // 读取属性赋值
    if ([GXUtils isValidDictionary:data]) {
        //读取text
        text = [data gx_stringForKey:@"value"];
        //读取扩展属性
        extend = [data gx_dictionaryForKey:@"extend"];
        //处理扩展属性
        if (extend || self.fitContent) {
            [self handleExtend:extend isCalculate:YES];
        }
    } else {
        text = nil;
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
        _text = ((NSAttributedString *)result).string;
        _isAttribute = YES;
    } else {
        _text = result;
        _isAttribute = NO;
    }
    
    //赋值
    if (_isAttribute) {
        //对于业务传进来的参数，先保留默认数据，在让attribute进行覆盖
        NSAttributedString *attributedString = (NSAttributedString *)result;
        self.attributedText = attributedString;
        
    } else {
        //对于自身生成的富文本，直接覆盖
        if (_text) {
            NSAttributedString *attributedString = [[NSAttributedString alloc] initWithString:_text attributes:self.attributes];
            self.attributedText = attributedString;
        } else {
            self.attributedText = nil;
        }
    }
}


#pragma mark - 处理扩展属性

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
    
    //获取label & paragraphStyle
    GXText *label = (GXText *)self.associatedView;
    NSMutableParagraphStyle *paragraphStyle = [self.attributes objectForKey:NSParagraphStyleAttributeName];
    
    //更新text-align
    NSString *textAlign = [styleInfo gx_stringForKey:@"text-align"];
    if (textAlign.length) {
        self.textAlignment = [GXUIHelper convertTextAlignment:textAlign];
        paragraphStyle.alignment = self.textAlignment;
    }
        
    //更新text-color
    NSString *color = [styleInfo gx_stringForKey:@"background-image"] ?: [styleInfo gx_stringForKey:@"color"];
    if (color.length) {
        color = [color stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if ([color hasPrefix:@"linear-gradient("] && [color hasSuffix:@")"]) {
            self.linearGradient = color;
            if (!isMark) {
                //不需要刷新布局，直接渲染
                [self setupTextGradientColor:label];
            }
        } else {
            self.linearGradient = nil;
            label.gxLinearGradient = nil;
            _currentColor = [color isEqualToString:@"null"] ? self.textColor : [UIColor gx_colorWithString:color];
            [self.attributes setObject:_currentColor forKey:NSForegroundColorAttributeName];
        }
    }
    
}

//重写更新布局方法
- (BOOL)updateLayoutStyle:(NSDictionary *)styleInfo{
    //是否刷新布局标志位 & fitContent属性
    BOOL isMark = [super updateLayoutStyle:styleInfo];
        
    //动态更新内部padding
    if (styleInfo[@"padding"]) {
        //设置四周边距
        CGFloat padding = [GXStyleHelper converSimpletValue:styleInfo[@"padding"]];
        self.gxPadding = UIEdgeInsetsMake(padding, padding, padding, padding);
    } else if (styleInfo[@"padding-top"] ||
               styleInfo[@"padding-left"] ||
               styleInfo[@"padding-right"] ||
               styleInfo[@"padding-bottom"]) {
        //有哪个设置哪个边距
        CGFloat top = [GXStyleHelper converSimpletValue:styleInfo[@"padding-top"]];
        CGFloat left = [GXStyleHelper converSimpletValue:styleInfo[@"padding-left"]];
        CGFloat right= [GXStyleHelper converSimpletValue:styleInfo[@"padding-right"]];
        CGFloat bottom = [GXStyleHelper converSimpletValue:styleInfo[@"padding-bottom"]];
        self.gxPadding = UIEdgeInsetsMake(top, left, bottom, right);
    }
    
    //文字相关行高，字体等影响布局属性的更新
    BOOL shouldUpdate = [self updateTextLayoutIfNeed:styleInfo];
    if (!isMark) {
        isMark = shouldUpdate;
    }
    
    //fit-content
    if (self.fitContent && (self.style.styleModel.display == DisplayFlex)) {
        self.style.styleModel.size = self.style.styleModel.recordSize;
        self.style.styleModel.minSize = self.style.styleModel.defaultSize;
        self.style.styleModel.flexGrow = self.style.styleModel.recordFlexGrow;
        isMark = YES;
    }
    
    return isMark;
}


- (BOOL)updateTextLayoutIfNeed:(NSDictionary *)styleInfo{
    //是否更新lineHeight标志位
    BOOL shouldUpdate = NO;
    
    //更新font
    NSString *fontSize = [styleInfo gx_stringForKey:@"font-size"];
    NSString *fontWeight = [styleInfo gx_stringForKey:@"font-weight"];
    NSString *fontfamily = [styleInfo gx_stringForKey:@"font-family"];
    if (fontSize.length || fontWeight.length || fontfamily.length) {
        NSMutableDictionary *styleJson = [NSMutableDictionary dictionaryWithDictionary:self.styleJson];
        [styleJson gx_setObject:fontSize forKey:@"font-size"];
        [styleJson gx_setObject:fontWeight forKey:@"font-weight"];
        [styleJson gx_setObject:fontfamily forKey:@"font-family"];
        self.font = [GXUIHelper fontFromStyle:styleJson];
        [self.attributes setObject:self.font forKey:NSFontAttributeName];
        
        //更新line-height
        self.lineHeight = MAX(self.lineHeight, (self.font.lineHeight + 1));
        shouldUpdate = YES;
    }
    
    //更新lineHeight
    NSString *lineHeight = [styleInfo gx_stringForKey:@"line-height"];
    if (lineHeight.length) {
        CGFloat fontLineHeight = self.font.lineHeight + 1;
        CGFloat tmpLineHeight = [GXStyleHelper converSimpletValue:lineHeight];
        self.lineHeight = MAX(tmpLineHeight, fontLineHeight);
        shouldUpdate = YES;
    }
    
    //更新段落信息
    if (shouldUpdate) {
        NSMutableParagraphStyle *paragraphStyle = [self.attributes objectForKey:NSParagraphStyleAttributeName];
        if (paragraphStyle && self.numberOfLines != 1) {
            paragraphStyle.minimumLineHeight = self.lineHeight;
            paragraphStyle.maximumLineHeight = self.lineHeight;
            //baseline
            CGFloat baselineOffset = (self.lineHeight - self.font.lineHeight) / 4;
            [self.attributes setObject:@(baselineOffset) forKey:NSBaselineOffsetAttributeName];
        }
    }
    
    //lines
    NSString *lines = [styleInfo gx_stringForKey:@"lines"];
    if (lines.length) {
        self.numberOfLines = [lines integerValue];
        shouldUpdate = YES;
    }
    
    return shouldUpdate;
}

//更新fit-content方法
- (void)updateFitContentLayout{
    if (self.fitContent && (self.style.styleModel.display == DisplayFlex)) {
        //基于上一次计算的宽度
        CGFloat maxWidth = self.frame.size.width;
        
        //文本自适应计算
        CGSize size = [self labelSizeForFont:self.font maxWidth:maxWidth lineBreakMode:self.lineBreakMode];
        
        //设置size
        StretchStyleDimension width = {
            .dimen_type = DIM_TYPE_POINTS,
            .dimen_value = size.width
        };
        StretchStyleDimension height = {
            .dimen_type = DIM_TYPE_POINTS,
            .dimen_value = size.height
        };
        StretchStyleSize newSize = (StretchStyleSize){
            .width = width,
            .height = height
        };
        self.style.styleModel.size = newSize;
        
        //设置minSize
        StretchStyleSize newMinSize = self.style.styleModel.recordMinSize;
        self.style.styleModel.minSize = newMinSize;
        
        //设置flexGrow
        self.style.styleModel.flexGrow = 0.f;
        
        //更改style + rustPtr
        [self.style updateRustPtr];
        [self setStyle:self.style];
        
        //标记dirty
        [self markDirty];
    }
    
}

- (void)updateTextNodes:(NSPointerArray *)textNodes{
    if (self.fitContent) {
        [textNodes addPointer:(__bridge void *)(self)];
    }
}


#pragma mark - Size计算

//计算size
- (CGSize)labelSizeForFont:(UIFont *)font
                  maxWidth:(CGFloat)maxWidth
             lineBreakMode:(NSLineBreakMode)lineBreakMode {
    
    // 空字符串返回大小为0
    NSString *text = self.text;
    if (!text.length) {
        return CGSizeZero;
    }
    
    //maxLines
    NSUInteger maxLines = self.numberOfLines;
    if (maxLines == 0) {
        maxLines = GXTextMaxLines;
    } else {
        maxLines = MIN(maxLines, GXTextMaxLines);
    }
    
    // 设置inset之后的高度
    maxWidth = (maxWidth - self.gxPadding.left - self.gxPadding.right);
    if (maxWidth <= 0) {
        GXAssert(maxLines == 1, @"当lines > 1时，文本宽度设置必须要大于0");
        maxWidth = GXTextMaxWidth;
    }
        
    // CTLineRef Array & lineNum
    NSArray *linesRef = GXLinesRefArray(font, text, maxWidth, maxLines, self.attributedText);
    CFIndex linesCount = linesRef.count;
    NSInteger lineNum = MIN(maxLines, linesCount);
    
    // Width
    CGFloat width = 0.f;
    if (lineNum == 0) {
        width = 0;
        
    } else if (lineNum == 1) {
        // 等于1行时，动态计算宽度
        CTLineRef lineRef = (__bridge CTLineRef)[linesRef firstObject];
        // 通过kCTLineBoundsIncludeLanguageExtents来确保不同的语言都有足够的空间
        CGRect rect = CTLineGetBoundsWithOptions(lineRef,
                                                 kCTLineBoundsExcludeTypographicLeading|
                                                 kCTLineBoundsExcludeTypographicShifts);
         width = rect.size.width;
    } else {
        // 大于1行时,行宽直接用最大宽度
        width = maxWidth;
        //特殊处理，获取所有line中的最大宽度
//        for (int i = 0; i < lineNum; i++) {
//           CTLineRef lineRef = (__bridge CTLineRef)[linesRef objectAtIndex:i];
//           CGFloat tmpWidth = ceil(CTLineGetTypographicBounds(lineRef, NULL, NULL, NULL));
//           if (width < tmpWidth) {
//               width = tmpWidth;
//           }
//        }
    }
    
    //Size
    CGFloat textWidth = ceil(MIN(width, maxWidth) + (self.gxPadding.left + self.gxPadding.right));
    CGFloat textHeight = 0;
    // Height （font.lineHeight 替换 lineHeight）
    if (self.numberOfLines == 1) {
        textHeight = ceil(self.lineHeight + (self.gxPadding.top + self.gxPadding.bottom));
        if (textHeight < self.frame.size.height) {
            textHeight = self.frame.size.height;
        }
    } else {
        textHeight = ceil(lineNum * self.lineHeight + (self.gxPadding.top + self.gxPadding.bottom));
        if (textHeight < self.lineHeight) {
            textHeight = self.lineHeight;
        }
    }
    
    CGSize textSize = CGSizeMake(textWidth, textHeight);
    return textSize;
}

//计算行数
static NSArray *GXLinesRefArray(UIFont *font,
                                NSString *text,
                                CGFloat maxWidth,
                                NSUInteger maxLines,
                                NSAttributedString *attributeText) {
    // 生成AttributedString
    NSMutableAttributedString *attrStr = nil;
    if (attributeText) {
        attrStr = [[NSMutableAttributedString alloc] initWithAttributedString:attributeText];
    } else {
        attrStr = [[NSMutableAttributedString alloc] initWithString:text];
    }
    
    //增加段落 & font属性
    NSMutableParagraphStyle *paraStyle = [[NSMutableParagraphStyle alloc] init];
    paraStyle.lineBreakMode = NSLineBreakByCharWrapping;
    [attrStr addAttribute:NSParagraphStyleAttributeName value:paraStyle range:NSMakeRange(0, text.length)];    
    // 计算FrameRef
    CTFramesetterRef frameSetterRef = CTFramesetterCreateWithAttributedString((__bridge CFAttributedStringRef)attrStr);
    CGMutablePathRef pathRef = CGPathCreateMutable();
    CGPathAddRect(pathRef, NULL, CGRectMake(0, 0, maxWidth, GXTextMaxLines * font.lineHeight));
    CTFrameRef frameRef = CTFramesetterCreateFrame(frameSetterRef, CFRangeMake(0, 0), pathRef, NULL);
    
    // LinesRefArray
    NSArray * linesArray = [(__bridge NSArray *)CTFrameGetLines(frameRef) copy];
    
    //release
    CFRelease(frameSetterRef);
    CFRelease(frameRef);
    CGPathRelease(pathRef);
    
    return linesArray;
}


#pragma mark - 文字渐变

- (void)setupTextGradientColor:(UILabel *)view{
    //判断
    if (view == nil ||
        self.linearGradient == nil ||
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
        NSDictionary *params = [GXGradientHelper parserLinearGradient:self.linearGradient];
        //设置渐变图片
        UIImage *image = [GXGradientHelper creatGradientImageWithParams:params bounds:view.bounds];
        if (image) {
            self.gradientColor = [UIColor colorWithPatternImage:image];
            view.textColor = self.gradientColor;
            view.gxGradientImage = image;
        }
        
    }
    
}


#pragma mark - 无障碍

- (void)setupAccessibilityInfo:(NSDictionary *)info{
    //无障碍内容
    NSString *accessibilityLabel = [info gx_stringForKey:@"accessibilityDesc"];
    self.associatedView.accessibilityLabel = accessibilityLabel;
    self.associatedView.isAccessibilityElement = YES;
    
    //是否开启无障碍
    NSString *accessibilityEnableStr = [info gx_stringForKey:@"accessibilityEnable"];
    if (accessibilityEnableStr.length) {
        BOOL accessibilityEnable = [accessibilityEnableStr boolValue];
        self.associatedView.isAccessibilityElement = accessibilityEnable;
    }
}


#pragma mark - 解析属性

- (void)configureStyleInfo:(NSDictionary *)styleJson{
    [super configureStyleInfo:styleJson];
    
    //颜色属性，默认黑色
    NSString *color = [styleJson gx_stringForKey:@"color"] ?: @"#000000";
    self.textColor = [UIColor gx_colorWithString:color];
    _currentColor = self.textColor;
    
    //字体
    self.font = [GXUIHelper fontFromStyle:styleJson];
    
    //渐变色
    if (!self.linearGradient && [color hasPrefix:@"linear-gradient"]) {
        self.linearGradient = color;
    }
    
    //居中展示
    NSString *textAlign = [styleJson gx_stringForKey:@"text-align"];
    self.textAlignment = [GXUIHelper convertTextAlignment:textAlign];
    
    //行数
    NSString *lines = [styleJson gx_stringForKey:@"lines"];
    if (lines.length) {
        self.numberOfLines = [lines integerValue];
    } else {
        self.numberOfLines = 1;
    }
    
    //文字行高
    NSString *lineHeight = [styleJson gx_stringForKey:@"line-height"];
    if (lineHeight.length) {
        CGFloat fontLineHeight = self.font.lineHeight + 1;
        CGFloat tmpLineHeight = [GXStyleHelper converSimpletValue:lineHeight];
        self.lineHeight = MAX(tmpLineHeight, fontLineHeight);
    } else {
        self.lineHeight = self.font.lineHeight + 1;
    }
    
    //lineBreakMode  (ellipsis, clip)
    NSString *textOverflow = [styleJson gx_stringForKey:@"text-overflow"];
    if ([textOverflow isEqualToString:@"clip"]) {
        self.lineBreakMode = NSLineBreakByClipping;
    } else  if ([textOverflow isEqualToString:@"middle"]) {
        self.lineBreakMode = NSLineBreakByTruncatingMiddle;
    } else {
        self.lineBreakMode = NSLineBreakByTruncatingTail;
    }
    
    //删除线属性
    NSString *textDecoration = [styleJson gx_stringForKey:@"text-decoration"];
    if ([textDecoration isEqualToString:@"line-through"]) {
        self.textDecoration = NSStrikethroughStyleAttributeName;
    } else if ([textDecoration isEqualToString:@"underline"]) {
        self.textDecoration = NSUnderlineStyleAttributeName;
    }
    
    //富文本信息
    {
        //段落信息
        NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
        paragraphStyle.alignment = self.textAlignment;
        paragraphStyle.lineBreakMode = self.lineBreakMode;
        //设置文字行高
        if (self.lineHeight && (self.numberOfLines != 1)) {
            paragraphStyle.minimumLineHeight = self.lineHeight;
            paragraphStyle.maximumLineHeight = self.lineHeight;
            //baseline
            CGFloat baselineOffset = (self.lineHeight - self.font.lineHeight) / 4;
            [self.attributes setObject:@(baselineOffset) forKey:NSBaselineOffsetAttributeName];
        }
        [self.attributes setObject:paragraphStyle forKey:NSParagraphStyleAttributeName];
        //删除线 / 下划线
        if (self.textDecoration.length) {
            [self.attributes setObject:@(NSUnderlineStyleSingle) forKey:self.textDecoration];
        }
        //字色
        [self.attributes setObject:_currentColor forKey:NSForegroundColorAttributeName];
        //字体
        [self.attributes setObject:self.font forKey:NSFontAttributeName];
    }
    
    //fit-content（仅支持CSS设置）
    BOOL fitContent = self.style.styleModel.fitContent;
    self.fitContent = fitContent;
    
    //gxadding
    [self setupTextPadding:styleJson];
}


//设置文字绘制的内边距
- (void)setupTextPadding:(NSDictionary *)styleJSON{
    if (styleJSON[@"padding"]) {
        //设置四周边距
        CGFloat padding = [GXStyleHelper converSimpletValue:styleJSON[@"padding"]];
        self.gxPadding = UIEdgeInsetsMake(padding, padding, padding, padding);
        
    } else if (styleJSON[@"padding-top"] ||
               styleJSON[@"padding-left"] ||
               styleJSON[@"padding-right"] ||
               styleJSON[@"padding-bottom"]) {
        //有哪个设置哪个边距
        CGFloat top = [GXStyleHelper converSimpletValue:styleJSON[@"padding-top"]];
        CGFloat left = [GXStyleHelper converSimpletValue:styleJSON[@"padding-left"]];
        CGFloat right= [GXStyleHelper converSimpletValue:styleJSON[@"padding-right"]];
        CGFloat bottom = [GXStyleHelper converSimpletValue:styleJSON[@"padding-bottom"]];
        self.gxPadding = UIEdgeInsetsMake(top, left, bottom, right);
        
    } else {
        //边距设置为0
        self.gxPadding = UIEdgeInsetsZero;
    }
}


#pragma mark - 设置属性

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

- (void)setNumberOfLines:(NSInteger)numberOfLines{
    if (_numberOfLines != numberOfLines) {
        _numberOfLines = numberOfLines;
    }
}

- (void)setTextDecoration:(NSString *)textDecoration{
    if (_textDecoration != textDecoration) {
        _textDecoration = textDecoration;
    }
}

- (void)setTextAlignment:(NSTextAlignment)textAlignment{
    if (_textAlignment != textAlignment) {
        _textAlignment = textAlignment;
    }
}

- (void)setLineBreakMode:(NSLineBreakMode)lineBreakMode{
    if (_lineBreakMode != lineBreakMode) {
        _lineBreakMode = lineBreakMode;
    }
}


#pragma mark - lazy load
//富文本属性
- (NSMutableDictionary *)attributes {
    if (!_attributes) {
        _attributes = [NSMutableDictionary dictionary];
    }
    return _attributes;
}


@end
