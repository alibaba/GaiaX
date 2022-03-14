//
//  GXConstants.h
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

#ifndef GXConstants_h
#define GXConstants_h

#import "libstretch.h"

typedef NS_ENUM(NSInteger, DIM_TYPE) {
    DIM_TYPE_POINTS,
    DIM_TYPE_PERCENT,
    DIM_TYPE_AUTO,
    DIM_TYPE_UNDEFINED
};

typedef NS_ENUM(NSInteger, AlignItems) {
    AlignItemsFlexStart,
    AlignItemsFlexEnd,
    AlignItemsCenter,
    AlignItemsBaseline,
    AlignItemsStretch
};

typedef NS_ENUM(NSInteger, AlignSelf) {
    AlignSelfAuto,
    AlignSelfFlexStart,
    AlignSelfFlexEnd,
    AlignSelfCenter,
    AlignSelfBaseline,
    AlignSelfStretch
};

typedef NS_ENUM(NSInteger, AlignContent) {
    AlignContentFlexStart,
    AlignContentFlexEnd,
    AlignContentCenter,
    AlignContentStretch,
    AlignContentSpaceBetween,
    AlignContentSpaceAround
};

typedef NS_ENUM(NSInteger, Direction) {
    DirectionInherit,
    DirectionLtr,
    DirectionRtl
};

typedef NS_ENUM(NSInteger, Display) {
    DisplayFlex,
    DisplayNone
};

typedef NS_ENUM(NSInteger, FlexDirection) {
    FlexDirectionRow,
    FlexDirectionColumn,
    FlexDirectionRowReverse,
    FlexDirectionColumnReverse
};

typedef NS_ENUM(NSInteger, JustifyContent) {
    JustifyContentFlexStart,
    JustifyContentFlexEnd,
    JustifyContentCenter,
    JustifyContentSpaceBetween,
    JustifyContentSpaceAround,
    JustifyContentSpaceEvenly
};

typedef NS_ENUM(NSInteger, Overflow) {
    OverflowVisible,
    OverflowHidden,
    OverflowScroll
};

typedef NS_ENUM(NSInteger, PositionType) {
    PositionTypeRelative,
    PositionTypeAbsolute
};

typedef NS_ENUM(NSInteger, FlexWrap) {
    FlexWrapNoWrap,
    FlexWrapWrap,
    FlexWrapWrapReverse
};

extern StretchStyleSize GXMakeSize(StretchStyleDimension width,
                                   StretchStyleDimension height);

extern StretchStyleRect GXMakeRect(StretchStyleDimension start,
                                   StretchStyleDimension end,
                                   StretchStyleDimension top,
                                   StretchStyleDimension bottom);

#endif /* GXConstants_h */
