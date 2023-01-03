//
//  GXStyle.m
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

#import "GXStyle.h"
#import "GXUtils.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXStyleHelper.h"

extern StretchStyleSize GXMakeSize(StretchStyleDimension width,
                                   StretchStyleDimension height)
{
    StretchStyleSize size;
    size.width = width;
    size.height = height;
    return size;
}

extern StretchStyleRect GXMakeRect(StretchStyleDimension left,
                                   StretchStyleDimension right,
                                   StretchStyleDimension top,
                                   StretchStyleDimension bottom)
{
    StretchStyleRect rect;
    rect.left = left;
    rect.right = right;
    rect.top = top;
    rect.bottom = bottom;
    return rect;
}


@implementation GXStyle


- (void)setupStyleInfo:(NSDictionary *)styleInfo{
    //更新属性
    GXStyleModel *styleModel = [GXStyleModel defaultStyleModel];
    [GXStyleHelper configStyleModel:styleModel style:styleInfo];
    
    //赋值
    self.styleModel = styleModel;
    self.styleInfo = styleInfo;

    //更新rust内容
    [self updateRustPtr];
}

//更新rust内容
- (void)updateRustPtr{
    //先释放原有指针
    if ([self isValidPtr:_rustptr]) {
        _prevRustptr = _rustptr;
    }
    
    //再生成新的指针
    _rustptr = stretch_style_create(
                                    (int32_t)_styleModel.display,
                                    (int32_t)_styleModel.positionType,
                                    (int32_t)_styleModel.direction,
                                    (int32_t)_styleModel.flexDirection,
                                    (int32_t)_styleModel.flexWrap,
                                    (int32_t)_styleModel.overflow,
                                    (int32_t)_styleModel.alignItems,
                                    (int32_t)_styleModel.alignSelf,
                                    (int32_t)_styleModel.alignContent,
                                    (int32_t)_styleModel.justifyContent,
                                    _styleModel.position,
                                    _styleModel.margin,
                                    _styleModel.padding,
                                    _styleModel.border,
                                    _styleModel.flexGrow,
                                    _styleModel.flexShrink,
                                    _styleModel.flexBasis,
                                    _styleModel.size,
                                    _styleModel.minSize,
                                    _styleModel.maxSize,
                                    _styleModel.aspectRatio
                                    );
    
}

- (BOOL)isValidPtr:(void *)ptr{
    return ptr != NULL;
}

//释放上次的rust指针
- (void)freePrevRustptr{
    if ([self isValidPtr:_prevRustptr]) {
        stretch_style_free(_prevRustptr);
        _prevRustptr = NULL;
    }
}

- (void)dealloc {
    //样式释放
    if ([self isValidPtr:_rustptr]) {
        stretch_style_free(_rustptr);
        _rustptr = NULL;
    }
    //临时样式释放
    if ([self isValidPtr:_prevRustptr]) {
        stretch_style_free(_prevRustptr);
        _prevRustptr = NULL;
    }
    GXLog(@"[GaiaX] 样式style释放 - %@", self);
}

// 兼容 edge-insets
- (void)updateEdgeInsets:(NSString *)edgeInsetsStr {
    if (edgeInsetsStr == nil) {
        return;
    }
    edgeInsetsStr = [edgeInsetsStr stringByReplacingOccurrencesOfString:@" " withString:@""];
    if (edgeInsetsStr != nil && edgeInsetsStr.length > 3) {
        edgeInsetsStr = [edgeInsetsStr substringWithRange:NSMakeRange(1, edgeInsetsStr.length-2)];
        NSArray *tmpInsets = [edgeInsetsStr componentsSeparatedByString:@","];
        if (tmpInsets.count == 4) {
            StretchStyleDimension top = [GXStyleHelper convertValue:tmpInsets[0]];
            StretchStyleDimension left = [GXStyleHelper convertValue:tmpInsets[1]];
            StretchStyleDimension right = [GXStyleHelper convertValue:tmpInsets[3]];
            StretchStyleDimension bottom = [GXStyleHelper convertValue:tmpInsets[2]];
            
            StretchStyleRect padding = self.styleModel.padding;
            if (left.dimen_type == DIM_TYPE_POINTS) {
                padding.left = left;
            }
            if (right.dimen_type == DIM_TYPE_POINTS) {
                padding.right = right;
            }
            if (top.dimen_type == DIM_TYPE_POINTS) {
                padding.top = top;
            }
            if (bottom.dimen_type == DIM_TYPE_POINTS) {
                padding.bottom = bottom;
            }
            self.styleModel.padding = padding;
        }
    }
}


@end


@implementation GXStyleModel

//创建defaultModel
+ (GXStyleModel *)defaultStyleModel{
    GXStyleModel *styleModel = [[GXStyleModel alloc] init];
    //基础属性
    styleModel.display = DisplayFlex;
    styleModel.alignSelf = AlignSelfAuto;
    styleModel.flexWrap = FlexWrapNoWrap;
    styleModel.overflow = OverflowHidden;
    styleModel.direction = DirectionInherit;
    styleModel.alignItems = AlignItemsStretch;
    styleModel.flexDirection = FlexDirectionRow;
    styleModel.positionType = PositionTypeRelative;
    styleModel.alignContent = AlignContentFlexStart;
    styleModel.justifyContent = JustifyContentFlexStart;
    
    //GaiaRect
    StretchStyleDimension rectValue = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleRect defaultRect = GXMakeRect(rectValue, rectValue, rectValue, rectValue);
    styleModel.position = defaultRect;
    styleModel.margin = defaultRect;
    styleModel.padding = defaultRect;
    styleModel.border = defaultRect;
    
    //GaiaSize
    StretchStyleDimension sizeValue = {.dimen_type = DIM_TYPE_AUTO, .dimen_value = 0.0};
    StretchStyleSize defaultSize = GXMakeSize(sizeValue, sizeValue);
    styleModel.size = defaultSize;
    styleModel.minSize = defaultSize;
    styleModel.maxSize = defaultSize;
    styleModel.defaultSize = defaultSize;
    
    //纵横比
    styleModel.aspectRatio = NAN;
    
    //flex-grow，flex-shrink，flex-basis
    styleModel.flexGrow = 0.0f;//默认不撑满父视图
    styleModel.flexShrink = 0.0f;//默认不压缩其他视图的空间
    styleModel.flexBasis = sizeValue;//默认auto
    
    //fitContent
    styleModel.fitContent = NO;
    
    //记录的属性
    styleModel.recordSize = styleModel.size;
    styleModel.recordMinSize = styleModel.minSize;
    styleModel.recordFlexGrow = styleModel.flexGrow;
    
    return styleModel;
}


@end

