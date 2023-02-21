//
//  GXStyleHelper.m
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

#import "GXStyleHelper.h"
#import "NSDictionary+GX.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXUIHelper.h"
#import "GXUTils.h"


@implementation GXStyleHelper


+ (void)configStyleModel:(GXStyleModel *)styleModel style:(NSDictionary *)styleJSON {
    if (styleModel == nil || ![GXUtils isValidDictionary:styleJSON]) {
        return;
    }
    
    // 设置Margin
    if ([styleJSON objectForKey:@"margin"] ||
        [styleJSON objectForKey:@"margin-top"] ||
        [styleJSON objectForKey:@"margin-left"] ||
        [styleJSON objectForKey:@"margin-bottom"] ||
        [styleJSON objectForKey:@"margin-right"]) {
        styleModel.margin = [self convertMargin:styleJSON];
    }
    
    // 设置Padding
    if ([styleJSON objectForKey:@"padding"] ||
        [styleJSON objectForKey:@"padding-top"] ||
        [styleJSON objectForKey:@"padding-left"] ||
        [styleJSON objectForKey:@"padding-bottom"] ||
        [styleJSON objectForKey:@"padding-right"]) {
        styleModel.padding = [self convertPadding:styleJSON];
    }
    
    //设置Size
    if ([styleJSON objectForKey:@"width"] || [styleJSON objectForKey:@"height"]) {
        styleModel.size = [self convertSize:styleJSON];
        styleModel.recordSize = styleModel.size;
    }
    
    // 设置minSize
    if ([styleJSON objectForKey:@"min-width"] || [styleJSON objectForKey:@"min-height"]) {
        styleModel.minSize = [self convertMinSize:styleJSON];
        styleModel.recordMinSize = styleModel.minSize;
    }
    
    // 设置maxSize
    if ([styleJSON objectForKey:@"max-width"] || [styleJSON objectForKey:@"max-height"]) {
        styleModel.maxSize = [self convertMaxSize:styleJSON];
    }
    
    // positionType
    NSString *position = [styleJSON gx_stringForKey:@"position"];
    if (position.length) {
        styleModel.positionType = [self convertPositionType:position];
        //设置Position
        if (styleModel.positionType == PositionTypeAbsolute) {
            styleModel.position = [self convertPosition:styleJSON];
        }
    }
    
    // direction
    NSString *direction = [styleJSON gx_stringForKey:@"direction"];
    if (direction.length) {
        styleModel.direction = [self convertDirection:direction];
    }
    
    // flex-direction
    NSString *flexDirection = [styleJSON gx_stringForKey:@"flex-direction"];
    if (flexDirection.length) {
        styleModel.flexDirection = [self convertFlexDirection:flexDirection];
    }
    
    // display
    NSString *display = [styleJSON gx_stringForKey:@"display"];
    if (display.length) {
        styleModel.display = [self convertDisplay:display];
    }
    
    // align-items
    NSString *alignItems = [styleJSON gx_stringForKey:@"align-items"];
    if (alignItems.length) {
        styleModel.alignItems = [self convertAlignItems:alignItems];
    }
    
    // align-self
    NSString *alignSelf = [styleJSON gx_stringForKey:@"align-self"];
    if (alignSelf.length) {
        styleModel.alignSelf = [self convertAlignSelf:alignSelf];
    }
    
    // align-content
    NSString *alignContent = [styleJSON gx_stringForKey:@"align-content"];
    if (alignContent.length) {
        styleModel.alignContent = [self convertAlignContent:alignContent];
    }
    
    // justify-content
    NSString *justifyContent = [styleJSON gx_stringForKey:@"justify-content"];
    if (justifyContent.length) {
        styleModel.justifyContent = [self convertJustifyContent:justifyContent];
    }
    
    // flex-basis
    NSString *flexBasis = [styleJSON gx_stringForKey:@"flex-basis"];
    if (flexBasis.length) {
        styleModel.flexBasis = [self convertAutoValue:flexBasis];
    }
    
    // flex-shrink
    NSString *flexShrink = [styleJSON gx_stringForKey:@"flex-shrink"];
    if (flexShrink.length) {
        styleModel.flexShrink = [flexShrink floatValue];
    }
    
    // flex-grow
    NSString *flexGrow = [styleJSON gx_stringForKey:@"flex-grow"];
    if (flexGrow.length) {
        styleModel.flexGrow = [flexGrow floatValue];
        styleModel.recordFlexGrow = styleModel.flexGrow;
    }
    
    // aspect-ratio 宽 / 高，
    NSString *aspectRatio = [styleJSON gx_stringForKey:@"aspect-ratio"];
    if (aspectRatio.length) {
        CGFloat value = [aspectRatio floatValue];
        if (value > 0) {
            styleModel.aspectRatio = value;
        }
    }
    
    // fit-content 自适应属性
    BOOL fitContent = [styleJSON gx_boolForKey:@"fit-content"];
    styleModel.fitContent = fitContent;
    
}


//Position
+ (StretchStyleRect)convertPosition:(NSDictionary *)styleJSON{
    //初始化
    StretchStyleDimension top = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension left = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension bottom = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension right = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    
    //获取css属性
    NSString *topStr = [styleJSON gx_stringForKey:@"top"];
    if (topStr) {
        top = [self convertValue:topStr];
    }
    
    NSString *leftStr = [styleJSON gx_stringForKey:@"left"];
    if (leftStr) {
        left = [self convertValue:leftStr];
    }
    
    NSString *bottomStr = [styleJSON gx_stringForKey:@"bottom"];
    if (bottomStr) {
        bottom = [self convertValue:bottomStr];
    }
    
    NSString *rightStr = [styleJSON gx_stringForKey:@"right"];
    if (rightStr) {
        right = [self convertValue:rightStr];
    }
    
    //返回position
    StretchStyleRect position = (StretchStyleRect){
        .top = top,
        .left = left,
        .bottom = bottom,
        .right = right
    };
    return position;
}


// Margin
+ (StretchStyleRect)convertMargin:(NSDictionary *)styleJSON{
    //初始化
    StretchStyleDimension marginTop = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension marginLeft = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension marginBottom = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension marginRight = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    
    //优先设置margin
    NSString *marginStr = [styleJSON gx_stringForKey:@"margin"];
    if (marginStr) {
        NSArray *margins = [marginStr componentsSeparatedByString:@" "];
        if ([margins count] == 1) {
            //margin = 5
            StretchStyleDimension margin = [self convertValue:margins[0]];
            marginTop = margin;
            marginLeft = margin;
            marginBottom = margin;
            marginRight = margin;
        } else if ([margins count] == 4) {
            //margin = 5 5 5 5
            marginTop = [self convertValue:margins[0]];
            marginLeft = [self convertValue:margins[1]];
            marginBottom = [self convertValue:margins[2]];
            marginRight = [self convertValue:margins[3]];
        }
    }
    
    //单独处理margin-xxx
    NSString *marginTopStr = [styleJSON gx_stringForKey:@"margin-top"];
    if (marginTopStr) {
        marginTop = [self convertValue:marginTopStr];
    }
    
    NSString *marginLeftStr = [styleJSON gx_stringForKey:@"margin-left"];
    if (marginLeftStr) {
        marginLeft = [self convertValue:marginLeftStr];
    }
    
    NSString *marginBottomStr = [styleJSON gx_stringForKey:@"margin-bottom"];
    if (marginBottomStr) {
        marginBottom = [self convertValue:marginBottomStr];
    }
    
    NSString *marginRightStr = [styleJSON gx_stringForKey:@"margin-right"];
    if (marginRightStr) {
        marginRight = [self convertValue:marginRightStr];
    }
    
    //返回margin
    StretchStyleRect margin = (StretchStyleRect){
        .top = marginTop,
        .left = marginLeft,
        .bottom = marginBottom,
        .right = marginRight
    };
    return margin;
}


// Padding
+ (StretchStyleRect)convertPadding:(NSDictionary *)styleJSON{
    //初始化
    StretchStyleDimension paddingTop = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension paddingLeft = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension paddingBottom = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    StretchStyleDimension paddingRight = {.dimen_type = DIM_TYPE_UNDEFINED, .dimen_value = 0.0f};
    
    //优先取padding
    NSString *paddingStr = [styleJSON gx_stringForKey:@"padding"];
    if (paddingStr) {
        NSArray *paddings = [paddingStr componentsSeparatedByString:@" "];
        if ([paddings count] == 1) {
            //padding = 5
            StretchStyleDimension padding = [self convertValue:paddings[0]];
            paddingTop = padding;
            paddingLeft = padding;
            paddingBottom = padding;
            paddingRight = padding;
            
        } else if ([paddings count] == 4) {
            //padding = 5 5 5 5
            paddingTop = [self convertValue:paddings[0]];
            paddingLeft = [self convertValue:paddings[1]];
            paddingBottom = [self convertValue:paddings[2]];
            paddingRight = [self convertValue:paddings[3]];
        }
    }
    
    //单独处理padding-xxx
    NSString *paddingTopStr = [styleJSON gx_stringForKey:@"padding-top"];
    if (paddingTopStr) {
        paddingTop = [self convertValue:paddingTopStr];
    }
    
    NSString *paddingLeftStr = [styleJSON gx_stringForKey:@"padding-left"];
    if (paddingLeftStr) {
        paddingLeft = [self convertValue:paddingLeftStr];
    }
    
    NSString *paddingBottomStr = [styleJSON gx_stringForKey:@"padding-bottom"];
    if (paddingBottomStr) {
        paddingBottom = [self convertValue:paddingBottomStr];
    }
    
    NSString *paddingRightStr = [styleJSON gx_stringForKey:@"padding-right"];
    if (paddingRightStr) {
        paddingRight = [self convertValue:paddingRightStr];
    }
    
    //返回padding
    StretchStyleRect padding = (StretchStyleRect){
        .top = paddingTop,
        .left = paddingLeft,
        .bottom = paddingBottom,
        .right = paddingRight
    };
    return padding;
}

// Size
+ (StretchStyleSize)convertSize:(NSDictionary *)styleJSON{
    //初始化
    NSString *widthStr = [styleJSON gx_stringForKey:@"width"];
    StretchStyleDimension width = [self convertAutoValue:widthStr];
    
    NSString *heightStr = [styleJSON gx_stringForKey:@"height"];
    StretchStyleDimension height = [self convertAutoValue:heightStr];
    
    //返回size
    StretchStyleSize size = (StretchStyleSize){
        .width = width,
        .height = height
    };
    return size;
}

//minSize
+ (StretchStyleSize)convertMinSize:(NSDictionary *)styleJSON{
    //初始化
    NSString *minWidth = [styleJSON gx_stringForKey:@"min-width"];
    StretchStyleDimension width = [self convertAutoValue:minWidth];
    
    NSString *minHeight = [styleJSON gx_stringForKey:@"min-height"];
    StretchStyleDimension height = [self convertAutoValue:minHeight];
    
    //返回size
    StretchStyleSize minSize = (StretchStyleSize){
        .width = width,
        .height = height
    };
    return minSize;
}

//maxSize
+ (StretchStyleSize)convertMaxSize:(NSDictionary *)styleJSON{
    //初始化
    NSString *maxWidth = [styleJSON gx_stringForKey:@"max-width"];
    StretchStyleDimension width = [self convertAutoValue:maxWidth];
    
    NSString *maxHeight = [styleJSON gx_stringForKey:@"max-height"];
    StretchStyleDimension height = [self convertAutoValue:maxHeight];
    
    //返回size
    StretchStyleSize maxSize = (StretchStyleSize){
        .width = width,
        .height = height
    };
    
    return maxSize;
}

//FlexWrap
+ (FlexWrap)convertFlexWrap:(NSString *)flexWrap {
    //默认no wrap
    FlexWrap fw = FlexWrapNoWrap;
    if ([flexWrap isEqualToString:@"wrap"]) {
        fw = FlexWrapWrap;
        
    } else if ([flexWrap isEqualToString:@"wrap-reverse"]) {
        fw = FlexWrapWrapReverse;
        
    }
    return fw;
}

//Display
+ (Display)convertDisplay:(NSString *)display {
    Display dis = DisplayNone;
    if ([display isEqualToString:@"flex"]) {
        dis = DisplayFlex;
    }
    return dis;
}

//PositionType
+ (PositionType)convertPositionType:(NSString *)positionType {
    //默认相对布局
    PositionType pt = PositionTypeRelative;
    if ([positionType isEqualToString:@"absolute"]) {
        pt = PositionTypeAbsolute;
    }
    return pt;
}

//Direction
+ (Direction)convertDirection:(NSString *)direction {
    //默认Inherit
    Direction di = DirectionInherit;
    if ([direction isEqualToString:@"ltr"]) {
        di = DirectionLtr;
        
    } else if ([direction isEqualToString:@"rtl"]) {
        di = DirectionRtl;
        
    }
    return di;
}

//FlexDirection
+ (FlexDirection)convertFlexDirection:(NSString *)direction {
    //默认row
    FlexDirection  flexDirection = FlexDirectionRow;
    if ([direction isEqualToString:@"column"]) {
        flexDirection = FlexDirectionColumn;
        
    } else if ([direction isEqualToString:@"column-reverse"]) {
        flexDirection = FlexDirectionColumnReverse;
        
    } else if ([direction isEqualToString:@"row-reverse"]) {
        flexDirection = FlexDirectionRowReverse;
        
    }
    return flexDirection;
}

//AlignItems
+ (AlignItems)convertAlignItems:(NSString *)alignItems {
    //默认stretch
    AlignItems ai = AlignItemsStretch;
    if ([alignItems isEqualToString:@"flex-start"]) {
        ai = AlignItemsFlexStart;
        
    } else if ([alignItems isEqualToString:@"flex-end"]) {
        ai = AlignItemsFlexEnd;
        
    } else if ([alignItems isEqualToString:@"baseline"]) {
        ai = AlignItemsBaseline;
        
    } else if ([alignItems isEqualToString:@"center"]) {
        ai = AlignItemsCenter;
        
    }
    return ai;
}

//AlignSelf
+ (AlignSelf)convertAlignSelf:(NSString *)alignSelf {
    //默认auto
    AlignSelf as  = AlignSelfAuto;
    if ([alignSelf isEqualToString:@"flex-start"]) {
        as = AlignSelfFlexStart;
        
    } else if ([alignSelf isEqualToString:@"flex-end"]) {
        as = AlignSelfFlexEnd;
        
    } else if ([alignSelf isEqualToString:@"baseline"]) {
        as = AlignSelfBaseline;
        
    } else if ([alignSelf isEqualToString:@"center"]) {
        as = AlignSelfCenter;
        
    } else if ([alignSelf isEqualToString:@"stretch"]) {
        as = AlignSelfStretch;
        
    }
    return as;
}

//AlignContent
+ (AlignContent)convertAlignContent:(NSString *)alignContent {
    //默认start
    AlignContent ac  = AlignContentFlexStart;
    if ([alignContent isEqualToString:@"flex-end"]) {
        ac = AlignContentFlexEnd;
        
    } else if ([alignContent isEqualToString:@"center"]) {
        ac = AlignContentCenter;
        
    } else if ([alignContent isEqualToString:@"stretch"]) {
        ac = AlignContentStretch;
        
    } else if ([alignContent isEqualToString:@"space-between"]) {
        ac = AlignContentSpaceBetween;
        
    }else if ([alignContent isEqualToString:@"space-around"]) {
        ac = AlignContentSpaceAround;
        
    }
    return ac;
}

//JustifyContent
+ (JustifyContent)convertJustifyContent:(NSString *)justifyContent {
    //默认是start
    JustifyContent jc  = JustifyContentFlexStart;
    if ([justifyContent isEqualToString:@"flex-end"]) {
        jc = JustifyContentFlexEnd;
        
    } else if ([justifyContent isEqualToString:@"center"]) {
        jc = JustifyContentCenter;
        
    } else if ([justifyContent isEqualToString:@"space-between"]) {
        jc = JustifyContentSpaceBetween;
        
    } else if ([justifyContent isEqualToString:@"space-around"]) {
        jc = JustifyContentSpaceAround;
        
    } else if ([justifyContent isEqualToString:@"space-evenly"]) {
        jc = JustifyContentSpaceEvenly;
        
    }
    return jc;
}


@end


@implementation GXStyleHelper (Value)

//获取具体的value值
+ (CGFloat)converSimpletValue:(NSString *)pxValue{
    //默认值
    CGFloat value = 0.f;
    //判断逻辑
    if (pxValue.length) {
        if ([pxValue hasSuffix:@"px"]) {
            //计算px
            value = [[pxValue substringToIndex:(pxValue.length - 2)] floatValue];
            
        } else if ([pxValue hasSuffix:@"pt"]) {
            //计算pt = px * ratio
            value = [[pxValue substringToIndex:(pxValue.length - 2)] floatValue];
            value = value * [GXUIHelper deviceRatio];
            
        } else {
            if ([GXUtils isNumber:pxValue]) {
                //判断是否为纯数字
                value = [pxValue floatValue];
            } else {
                //token
                //                value = [GaiaXBizHelper dimFromDesignToken:pxValue];
            }
            
            //如果为-10086，则设置默认值
            if (value == kGX_INVALID_DIM) {
                value = 0.f;
            }
        }
    }
    
    return value;
}


//获取基础布局属性，默认值为undefined
+ (StretchStyleDimension)convertValue:(NSString *)pxValue {
    //设置默认值
    StretchStyleDimension styleDimension = {
        .dimen_type = DIM_TYPE_UNDEFINED,
        .dimen_value = 0.0f
    };
    
    if (pxValue.length && ![pxValue isEqualToString:@"null"]) {
        CGFloat value = 0.f;
        if ([pxValue hasSuffix:@"px"]) {
            //计算px
            value = [[pxValue substringToIndex:(pxValue.length - 2)] floatValue];
            styleDimension.dimen_value = value;
            styleDimension.dimen_type = DIM_TYPE_POINTS;
            
        } else if ([pxValue hasSuffix:@"pt"]) {
            //计算pt = px * ratio
            value = [[pxValue substringToIndex:(pxValue.length - 2)] floatValue];
            styleDimension.dimen_value = value * [GXUIHelper deviceRatio];
            styleDimension.dimen_type = DIM_TYPE_POINTS;
            
        } else if ([pxValue hasSuffix:@"%"]) {
            //计算%
            value = [[pxValue substringToIndex:(pxValue.length - 1)] floatValue] / 100.0f;
            styleDimension.dimen_value = value;
            styleDimension.dimen_type = DIM_TYPE_PERCENT;
            
        } else if ([pxValue hasSuffix:@"auto"]) {
            //计算auto
            styleDimension.dimen_value = 0.f;
            styleDimension.dimen_type = DIM_TYPE_AUTO;
            
        } else {
            if ([GXUtils isNumber:pxValue]) {
                //判断是否为纯数字
                value = [pxValue floatValue];
            } else {
                //token
                //                value = [GaiaXBizHelper dimFromDesignToken:pxValue];
            }
            
            if (value != kGX_INVALID_DIM) {
                styleDimension.dimen_value = value;
                styleDimension.dimen_type = DIM_TYPE_POINTS;
            }
            
        }
    }
    
    return styleDimension;
}


//获取基础布局属性，默认值为auto
+ (StretchStyleDimension)convertAutoValue:(NSString *)pxValue{
    //设置默认值
    StretchStyleDimension styleDimension = {
        .dimen_type = DIM_TYPE_AUTO,
        .dimen_value = 0.0f
    };
    
    if (pxValue.length && ![pxValue isEqualToString:@"null"]) {
        CGFloat value = 0.f;
        if ([pxValue hasSuffix:@"px"]) {
            //计算px
            value = [[pxValue substringToIndex:(pxValue.length - 2)] floatValue];
            styleDimension.dimen_value = value;
            styleDimension.dimen_type = DIM_TYPE_POINTS;
            
        } else if ([pxValue hasSuffix:@"pt"]) {
            //计算pt = px * ratio
            value = [[pxValue substringToIndex:(pxValue.length - 2)] floatValue];
            styleDimension.dimen_value = value * [GXUIHelper deviceRatio];
            styleDimension.dimen_type = DIM_TYPE_POINTS;
            
        } else if ([pxValue hasSuffix:@"%"]) {
            //计算%
            value = [[pxValue substringToIndex:(pxValue.length - 1)] floatValue] / 100.0f;
            styleDimension.dimen_value = value;
            styleDimension.dimen_type = DIM_TYPE_PERCENT;
            
        } else if ([pxValue hasSuffix:@"auto"]) {
            //计算auto
            styleDimension.dimen_value = 0.f;
            styleDimension.dimen_type = DIM_TYPE_AUTO;
            
        } else {
            if ([GXUtils isNumber:pxValue]) {
                //判断是否为纯数字
                value = [pxValue floatValue];
            } else {
                //token
                //                value = [GaiaXBizHelper dimFromDesignToken:pxValue];
            }
            if (value != kGX_INVALID_DIM) {
                styleDimension.dimen_value = value;
                styleDimension.dimen_type = DIM_TYPE_POINTS;
            }
            
        }
    }
    
    return styleDimension;
}



@end
