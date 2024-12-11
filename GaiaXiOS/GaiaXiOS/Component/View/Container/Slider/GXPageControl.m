//  GXPageControl.m
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

#import "GXPageControl.h"

@interface GXPageControl () {
    NSArray *_pages;
}

@end


@implementation GXPageControl

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _pageGapWidth = 0.0;
        _selectedPageWidth = 8.0;
        _unSelectedPageWidthHeight = 3.0;
    }
    return self;
}

- (void)setPageCount:(NSInteger)pageCount {
    if (_pageCount != pageCount) {
        _pageCount = pageCount;
        
        // 移除pages内容
        [_pages makeObjectsPerformSelector:@selector(removeFromSuperview)];
        _pages = nil;
        
        if (_pageCount > 1) { //页数小于2不显示页签
            NSMutableArray *pages = [NSMutableArray array];
            for (int i = 0; i < _pageCount; i++) {
                CGRect frame = CGRectMake(0.0, 0.0, _unSelectedPageWidthHeight, _unSelectedPageWidthHeight);
                UIView *page = [[UIView alloc] initWithFrame:frame];
                page.layer.cornerRadius = _hideCornerRadius ? 0 : _unSelectedPageWidthHeight / 2.0;
                [self addSubview:page];
                
                [pages addObject:page];
            }
            _pages = pages;
        }
        
        _currentPage = -1; //初始值
        [self setCurrentPage:0 animated:NO]; //默认选中项
        
        //自适应宽度
        UIView *lastPage = _pages.lastObject;
        
        CGRect frame = self.frame;
        frame.size.width = lastPage ? CGRectGetMaxX(lastPage.frame) : 0.0;
        frame.size.height = _unSelectedPageWidthHeight;
        self.frame = frame;
    }
}

- (void)setCurrentPage:(NSInteger)currentPage {
    //经测试6s在iOS9.2上，滚动出右边界回弹，静止时offset不是bounds的整数倍，导致页码错误。需验证上下限。
    [self setCurrentPage:MAX(MIN(currentPage, _pageCount - 1), 0) animated:YES];
}

- (void)setCurrentPage:(NSInteger)currentPage animated:(BOOL)animated {
    if (_currentPage != currentPage) {
        _currentPage = currentPage;
        if (animated) {
            [UIView animateWithDuration:0.35 animations:^{
                [self updatePagesFrame:currentPage];
            }];
        } else {
            [self updatePagesFrame:currentPage];
        }
    }
}

- (void)updatePagesFrame:(NSInteger)currentPage {
    CGFloat x = 0.0, gap = (_pageGapWidth > 0 ? _pageGapWidth : _unSelectedPageWidthHeight);
    UIColor *selectedPageColor = _selectedPageColor ?: [UIColor whiteColor];
    UIColor *unselectedPageColor = _unselectedPageColor ?: [UIColor colorWithWhite:1.0 alpha:0.6];
    
    for (int i = 0; i < _pages.count; i++) {
        UIView *page = _pages[i];
        //颜色
        page.backgroundColor = (i == currentPage) ? selectedPageColor : unselectedPageColor;
        
        //修正坐标
        CGRect frame = page.frame;
        frame.origin.x = round(x);
        frame.size.width = (i == currentPage) ? _selectedPageWidth : _unSelectedPageWidthHeight;
        page.frame = frame;
        
        x = CGRectGetMaxX(frame) + gap;
    }
}

- (void)setPageGapWidth:(NSInteger)pageGapWidth {
    _pageGapWidth = pageGapWidth;
    [self setCurrentPage:_currentPage animated:NO];
}

- (void)setSelectedPageColor:(UIColor *)selectedPageColor {
    if (!CGColorEqualToColor(_selectedPageColor.CGColor, selectedPageColor.CGColor)) {
        _selectedPageColor = selectedPageColor;
        
        //刷新选中色
        UIView *selectedPage = (_currentPage < _pages.count) ? [_pages objectAtIndex:_currentPage] : nil;
        selectedPage.backgroundColor = selectedPageColor ?: [UIColor whiteColor];
    }
}

- (void)setUnselectedPageColor:(UIColor *)unselectedPageColor {
    if (!CGColorEqualToColor(_unselectedPageColor.CGColor, unselectedPageColor.CGColor)) {
        _unselectedPageColor = unselectedPageColor;
        //刷新未选中色
        for (int i = 0; i < _pages.count; i++) {
            if (i != _currentPage) {
                UIView *page = _pages[i];
                page.backgroundColor = unselectedPageColor ?: [UIColor colorWithWhite:1.0 alpha:0.6];
            }
        }
    }
}

- (void)switchSelectAnimated:(int)fromIndex
                     toIndex:(int)toIndex
                    progress:(CGFloat)progress {
    if (fabs(progress) <= 1) {} else return;
    
    if (fromIndex >= 0 && fromIndex < _pageCount && toIndex >= 0 && toIndex < _pageCount && (abs(toIndex - fromIndex) == 1 || abs(toIndex - fromIndex) == _pageCount - 1)) {} else return;
    
    UIView *fromView = _pages[fromIndex];
    UIView *toView = _pages[toIndex];
    CGFloat toProgress = fabs(progress);
    BOOL isNext = progress > 0;
    
    // 1. frame动画
    CGRect fromBeforeRect = fromView.frame;
    CGRect toBeforeRect = toView.frame;
    CGFloat changeWidth = (_selectedPageWidth - _unSelectedPageWidthHeight) * toProgress;
    
    // 1.1 计算frame
    BOOL shouldChangeOther_pages = false; // 标识是否需要调整中间_pages
    
    CGFloat fromAfterWidth = _selectedPageWidth - changeWidth;
    CGFloat fromAfterOriginX = 0;
    CGFloat toAfterWidth = _unSelectedPageWidthHeight + changeWidth;
    CGFloat toAfterOriginX = 0;
    
    if (isNext) { // 向右
        if (fromIndex != _pageCount - 1) {
            fromAfterOriginX = CGRectGetMinX(fromBeforeRect);
            toAfterOriginX = (fromAfterWidth + fromAfterOriginX + _pageGapWidth);
        } else {
            fromAfterOriginX = self.frame.size.width - fromAfterWidth;
            toAfterOriginX = CGRectGetMinX(toBeforeRect);
            shouldChangeOther_pages = true;
        }
    } else { // 向左
        if (fromIndex != 0) {
            fromAfterOriginX = CGRectGetMaxX(fromBeforeRect) - fromAfterWidth;
            toAfterOriginX = CGRectGetMinX(toBeforeRect);
        } else {
            fromAfterOriginX = CGRectGetMinX(fromBeforeRect);
            toAfterOriginX = self.frame.size.width - toAfterWidth;
            shouldChangeOther_pages = true;
        }
    }
    
    // 1.2 调整frame
    CGRect fromAfterRect = CGRectMake(fromAfterOriginX, CGRectGetMinY(fromBeforeRect), fromAfterWidth, CGRectGetHeight(fromBeforeRect));
    CGRect toAfterRect = CGRectMake(toAfterOriginX, CGRectGetMinY(toBeforeRect), toAfterWidth, CGRectGetHeight(toBeforeRect));
    fromView.frame = fromAfterRect;
    toView.frame = toAfterRect;
    
    // 1.3 处理特殊情况
    if (shouldChangeOther_pages) {
        for (int i = 1 ;i < (_pageCount - 1);i++) {
            UIView *tempPrefixView = _pages[i - 1];
            UIView *tempView = _pages[i];
            
            CGFloat prefixViewMaxX = CGRectGetMinX(tempPrefixView.frame) + tempPrefixView.frame.size.width;
            CGFloat tempViewOriginX = prefixViewMaxX + _pageGapWidth;
            tempView.frame = CGRectMake(tempViewOriginX, tempView.frame.origin.y, tempView.frame.size.width, tempView.frame.size.height);
        }
    }
    
    // 2. 颜色动画
    CGFloat selectRed = 0, selectGreen = 0, selectBlue = 0, selectAlpha = 0;
    CGFloat unSelectRed = 0, unSelectGreen = 0, unSelectBlue = 0, unSelectAlpha = 0;
    [_selectedPageColor getRed:&selectRed green:&selectGreen blue:&selectBlue alpha:&selectAlpha];
    [_unselectedPageColor getRed:&unSelectRed green: &unSelectGreen blue: &unSelectBlue alpha: &unSelectAlpha];

    fromView.backgroundColor = [UIColor colorWithRed: selectRed - (selectRed - unSelectRed) * toProgress green: selectGreen - (selectGreen - unSelectGreen) * toProgress blue: selectBlue - (selectBlue - unSelectBlue) * toProgress alpha: selectAlpha - (selectAlpha - unSelectAlpha) * toProgress];
    
    toView.backgroundColor = [UIColor colorWithRed: unSelectRed + (selectRed - unSelectRed) * toProgress green: unSelectGreen + (selectGreen - unSelectGreen) * toProgress blue: unSelectBlue + (selectBlue - unSelectBlue) * toProgress alpha: unSelectAlpha + (selectAlpha - unSelectAlpha) * toProgress];
}

@end
