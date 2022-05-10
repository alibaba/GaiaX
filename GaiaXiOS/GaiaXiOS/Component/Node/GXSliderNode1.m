//
//  GXSliderNode.m
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

#import "GXSliderNode.h"
#import "GXTemplateEngine.h"
#import "GXGradientHelper.h"
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "GXFunctionDef.h"
#import "GXCommonDef.h"
#import "GXRootView.h"
#import "NSArray+GX.h"
#import "UIColor+GX.h"
#import "GXUIHelper.h"
#import "GXScrollView.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXSliderNode () <UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>{
    //是否初始化
    BOOL _hasInit;
    //是否正在展示
    BOOL _isOnShow;
    //坑位尺寸
    CGSize _itemSize;
    //滚动事件
    GXEvent *_scrollEvent;
}

// 数据源
@property (nonatomic, strong) NSArray *dataArray;
@property (nonatomic, strong) NSMutableArray *items;
// 坑位模板的item信息
@property (nonatomic, strong) GXTemplateItem *subTemplateItem;
//定时器
@property (nonatomic, weak) NSTimer *timer;
//指示器
@property (nonatomic , strong) UIPageControl *pageControl;
//是否自动轮播
@property (nonatomic, assign) bool autoScroll;
//是否开始无限轮播
@property (nonatomic, assign) bool infiniteLoop;
//总数
@property (nonatomic, assign) NSInteger totalItems;
//坑位宽度
@property (nonatomic, assign) NSInteger itemWidth;

@end


@implementation GXSliderNode

//创建视图
- (GXScrollView *)creatView{
    GXScrollView *view = (GXScrollView *)self.associatedView;
    if (!view) {
        //创建layout
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        //创建view
        view = [[GXScrollView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
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
- (void)renderView:(GXScrollView *)view{
    if (!_hasInit) {
        _hasInit = YES;
        // 渲染视图
        UICollectionViewFlowLayout *flowLayout = (UICollectionViewFlowLayout *)view.collectionViewLayout;
        // scrollDirection
        flowLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        // 滚动方向垂直的间距
        flowLayout.minimumInteritemSpacing = self.itemSpacing;
        // 滚动方向的间距
        flowLayout.minimumLineSpacing = self.rowSpacing;
        // 是否预估宽高
        flowLayout.estimatedItemSize = CGSizeZero;

        // 滚动条
        view.showsHorizontalScrollIndicator = NO;
        view.showsVerticalScrollIndicator = NO;

        // clickToBounds
        view.clipsToBounds = self.clipsToBounds;
        //scrollEnable
        view.scrollEnabled = self.scrollEnable;
        // 内容inset
        view.contentInset = self.contentInset;

        // delegate && dataSource
        view.dataSource = self;
        view.delegate = self;

        //cell注册
        [self registerItemCell];
        
        //指示器创建
        UIPageControl *pageControl = [[UIPageControl alloc] init];
        pageControl.frame = CGRectZero;
        pageControl.pageIndicatorTintColor = [UIColor colorWithRed:255/255.0 green:255/255.0 blue:255/255.0 alpha:0.3];
        pageControl.currentPageIndicatorTintColor = [UIColor colorWithRed:255/255.0 green:255/255.0 blue:255/255.0 alpha:1];
        pageControl.enabled = NO;
        pageControl.numberOfPages = self.items.count;
        
        [view addSubview:pageControl];
        _pageControl = pageControl;
    }
    
    //更新frame
    if ([self isRootNode]) {
        CGRect frame = view.frame;
        frame.size.width = self.frame.size.width;
        frame.size.height = self.frame.size.height;
        self.frame = frame;
    }
    
    //判断是否相等，更新frame
    if (!CGRectEqualToRect(view.frame, self.frame)) {
        view.frame = self.frame;
    }
    
    //是否可以越界显示
    view.clipsToBounds = self.clipsToBounds;
    //设置背景色
    [self setupNormalBackground:view];
    //设置圆角属性
    [self setupCornerRadius:view];
}


#pragma mark - 绑定数据

- (void)bindData:(NSDictionary *)data{
    //处理数据
    NSArray *dataArray = nil;
    NSDictionary *extend = nil;
    if ([GXUtils isValidDictionary:data]) {
        //获取为{"value":[]}类型
        dataArray = [data gx_arrayForKey:@"value"];
        //处理extend
        extend = [data gx_dictionaryForKey:@"extend"];
    }
    
    //赋值
    [self processListData:dataArray];
    
    //处理extend
    [self handleExtend:extend isCalculate:NO];
    
    //获取横滑容器 & 设置属性
    GXScrollView *sliderView = (GXScrollView *)self.associatedView;
    [sliderView reloadData];

}

// 处理extend
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{

    //更新布局属性 & 标记
    BOOL isMark = [self updateLayoutStyle:extend];
    
    //更新普通属性
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

- (BOOL)updateLayoutStyle:(NSDictionary *)styleInfo{
    BOOL isMark = [super updateLayoutStyle:styleInfo];
    
    //动态获取column
    NSInteger column = [styleInfo gx_intForKey:@"column"];
    if (column > 0) {
        self.column = column;
    }
    
    //计算ItemSize
    [self calculateItemSize];
    
    //计算sliderView的高度 (只有为auto时，才进行内部覆盖)
    StretchStyleSize recordSize = self.style.styleModel.recordSize;
    if (!self.scrollEnable && (recordSize.height.dimen_type == DIM_TYPE_AUTO) && self.frame.size.width > 0) {
        //默认高度
        CGFloat height = 0.f;
        
        //padding
        CGFloat topPadding = self.contentInset.top;
        CGFloat bottomPadding = self.contentInset.bottom;
        //行间距
        CGFloat rowSpace = self.rowSpacing;
        //itemSize
        CGSize itemSize = _itemSize;
        
        //计算容器高度
        NSInteger itemCount = self.items.count;
        int totalRow = ceil(((CGFloat)itemCount) / self.column); //行
        int centerHeiht = totalRow > 0 ? (totalRow * itemSize.height + (totalRow - 1) * rowSpace) : 0;
        height = topPadding + bottomPadding + centerHeiht;

        //更新style
        StretchStyleDimension tmpHeight = self.style.styleModel.size.height;
        if (height != tmpHeight.dimen_value) {
            tmpHeight.dimen_value = height;
            tmpHeight.dimen_type = DIM_TYPE_POINTS;
            
            StretchStyleDimension tmpWidth = self.style.styleModel.size.width;
            StretchStyleSize newSize = (StretchStyleSize){
                .width = tmpWidth,
                .height = tmpHeight
            };
            self.style.styleModel.size = newSize;
            
            //更改style + rustPtr
            [self.style updateRustPtr];
            [self setStyle:self.style];
            
            //标记dirty
            [self markDirty];
            
            isMark = YES;
        }
    }
    
    return isMark;
}


#pragma mark - 处理数据源

//将原始数据转化GXTemplateData
- (void)processListData:(NSArray *)dataArray{
    if (dataArray == self.dataArray) {
        return;
    }
    self.dataArray = dataArray;
    
    //清空数据源
    [self.items removeAllObjects];
    //生成数据源
    for (int i = 0; i < dataArray.count; i++) {
        GXTemplateData *templateData = [[GXTemplateData alloc] init];
        NSDictionary *data = [dataArray gx_objectAtIndex:i];
        templateData.data = data;
        templateData.eventListener = self.templateContext.templateData.eventListener;
        templateData.trackListener = self.templateContext.templateData.trackListener;
        templateData.dataListener = self.templateContext.templateData.dataListener;
        [self.items gx_addObject:templateData];
    }
}


//计算坑位size
- (void)calculateItemSize{
    // 获取宽度
    CGFloat extraWidth = self.contentInset.left + self.contentInset.right + (self.column - 1) * self.itemSpacing;
    CGFloat measureWidth = floor((self.frame.size.width - extraWidth) / self.column);
    CGFloat measureHeight = NAN;
    //计算
    _itemSize = [TheGXTemplateEngine sizeWithTemplateItem:self.subTemplateItem measureSize:CGSizeMake(measureWidth, measureHeight)];
}

#pragma mark - 计算高度

- (void)calculateWithData:(NSDictionary *)data{
    //处理数据
    NSArray *dataArray = nil;
    NSDictionary *extend = nil;

    if ([GXUtils isValidDictionary:data]) {
        //获取为{"value":[]}类型
        dataArray = [data gx_arrayForKey:@"value"];
        //处理extend
        extend = [data gx_dictionaryForKey:@"extend"];
    }
    
    //赋值
    [self processListData:dataArray];

    //处理extend属性
    [self handleExtend:extend isCalculate:YES];
}


#pragma mark - 属性设置

- (void)configureViewInfo:(NSDictionary *)viewInfo{
    [super configureViewInfo:viewInfo];
    
    //获取复用标志 & 子模板id
    NSArray *identifiers = [viewInfo gx_arrayForKey:@"layers"];
    [self setupItemIdentifier:identifiers];
    
    //scroll-enable
    self.scrollEnable = [viewInfo gx_boolForKey:@"scroll-enable"];
    
    //row-spacing
    NSString *rowSpacing = [viewInfo gx_stringForKey:@"row-spacing"];
    self.rowSpacing = [GXStyleHelper converSimpletValue:rowSpacing];
    
    //item-spacing
    NSString *itemSpacing = [viewInfo gx_stringForKey:@"item-spacing"];
    self.itemSpacing = [GXStyleHelper converSimpletValue:itemSpacing];

    //column 只有竖滑生效
    NSInteger column = [viewInfo gx_integerForKey:@"column"];
    self.column = column > 0 ? column : 1;
    
    //edge-insets
    //self.contentInset = UIEdgeInsetsFromString(edgeInsets);
    NSString *edgeInsets = [viewInfo gx_stringForKey:@"edge-insets"];
    edgeInsets = [edgeInsets stringByReplacingOccurrencesOfString:@" " withString:@""];
    if (edgeInsets.length > 3) {
        edgeInsets = [edgeInsets substringWithRange:NSMakeRange(1, edgeInsets.length-2)];
        NSArray *tmpInsets = [edgeInsets componentsSeparatedByString:@","];
        if (tmpInsets.count == 4) {
            CGFloat top = [GXStyleHelper converSimpletValue:tmpInsets[0]];
            CGFloat left = [GXStyleHelper converSimpletValue:tmpInsets[1]];
            CGFloat right = [GXStyleHelper converSimpletValue:tmpInsets[3]];
            CGFloat bottom = [GXStyleHelper converSimpletValue:tmpInsets[2]];
            self.contentInset = UIEdgeInsetsMake(top, left, bottom, right);
        }
    }
}

//获取坑位类型
- (void)setupItemIdentifier:(NSArray *)identifiers{
    //读取子templateId
    NSDictionary *identifierDict = [identifiers gx_objectAtIndex:0];
    NSString *identifier = [identifierDict gx_stringForKey:@"id"];
    //templateItem
    GXTemplateItem *templateItem = [[GXTemplateItem alloc] init];
    templateItem.isLocal = self.templateItem.isLocal;
    templateItem.bizId = self.templateItem.bizId;
    templateItem.templateId = identifier;
    //rootStyle
    templateItem.rootStyleInfo = [self.fullStyleJson gx_dictionaryForKey:identifier];
    self.subTemplateItem = templateItem;
}


#pragma mark -

//注册Cell
- (void)registerItemCell{
    NSString *identifier = self.subTemplateItem.templateId;
    GXScrollView *sliderView = (GXScrollView *)self.associatedView;
    [sliderView registerClass:[GXScrollViewCell class] forCellWithReuseIdentifier:identifier];
}


#pragma mark - UICollectionViewDelegate & UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.items.count;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    return _itemSize;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(nonnull NSIndexPath *)indexPath {
    //重用标识
    NSString *identifier = self.subTemplateItem.templateId;
    GXScrollViewCell *cell = (GXScrollViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
        
    //获取视图
    CGSize itemSize = _itemSize;
    GXRootView *rootView = cell.rootView;
    if (!rootView) {
        rootView = (GXRootView *)[TheGXTemplateEngine creatViewByTemplateItem:_subTemplateItem measureSize:itemSize];
        cell.rootView = rootView;
        [cell.contentView addSubview:rootView];
    }
    
    //index属性
    NSInteger index = indexPath.item;
    rootView.gxNode.index = index;
    
    //数据绑定
    GXTemplateData *data = [self.items objectAtIndex:index];
    [TheGXTemplateEngine bindData:data onView:rootView];
    
    return cell;
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView{
    id <GXEventProtocal> eventListener = self.templateContext.templateData.eventListener;
    if (eventListener && [eventListener respondsToSelector:@selector(gx_onScrollEvent:)]) {
        if (nil == _scrollEvent) {
            _scrollEvent = [[GXEvent alloc] init];
            _scrollEvent.view = self.associatedView;
            _scrollEvent.nodeId = self.nodeId;
        }
        _scrollEvent.contentOffset = scrollView.contentOffset;
        [eventListener gx_onScrollEvent:_scrollEvent];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView{
    id <GXEventProtocal> eventListener = self.templateContext.templateData.eventListener;
    if (eventListener && [eventListener respondsToSelector:@selector(gx_onScrollEndEvent:)]) {
        if (nil == _scrollEvent) {
            _scrollEvent = [[GXEvent alloc] init];
            _scrollEvent.view = self.associatedView;
            _scrollEvent.nodeId = self.nodeId;
        }
        _scrollEvent.contentOffset = scrollView.contentOffset;
        [eventListener gx_onScrollEndEvent:_scrollEvent];
    }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if (!decelerate) {
        id <GXEventProtocal> eventListener = self.templateContext.templateData.eventListener;
        if (eventListener && [eventListener respondsToSelector:@selector(gx_onScrollEndEvent:)]) {
            if (nil == _scrollEvent) {
                _scrollEvent = [[GXEvent alloc] init];
                _scrollEvent.view = self.associatedView;
                _scrollEvent.nodeId = self.nodeId;
            }
            _scrollEvent.contentOffset = scrollView.contentOffset;
            [eventListener gx_onScrollEndEvent:_scrollEvent];
        }
    }
}

#pragma mark - 轮播
- (void)handleSlider {
    self.autoScroll = YES;
    self.infiniteLoop = YES;
    _pageControl.numberOfPages = self.items.count;
    self.totalItems = self.infiniteLoop ? self.items.count * 100 : self.items.count;
    GXScrollView *view = (GXScrollView *)self.associatedView;
    if (self.totalItems > 1) {
        view.scrollEnabled = YES;
        //处理是否自动滑动，定时器问题
        [self setAutoScroll:self.autoScroll];
    }else{
        view.scrollEnabled = NO;
        [self setAutoScroll:NO];
    }
    
    if (view.contentOffset.x == 0 && _totalItems) {
        int targetIndex = 0;
        if (self.infiniteLoop) {
            targetIndex = _totalItems * 0.5;
        }else{
            targetIndex = 0;
        }
        [view scrollToItemAtIndexPath:[NSIndexPath indexPathForItem:targetIndex inSection:0] atScrollPosition:UICollectionViewScrollPositionNone animated:NO];
    }
}

- (void)setAutoScroll:(BOOL)autoScroll {
    
    _autoScroll = autoScroll;
    
    //创建之前，停止定时器
    [self invalidateTimer];
    
    if (_autoScroll) {
        [self setupTimer];
    }
}

- (void)automaticScroll {
    if (0 == _totalItems) {
        return;
    }
    NSInteger currentIndex = [self currentIndex];
    NSInteger targetIndex = currentIndex + 1;
    [self scrollToIndex:targetIndex];
}

- (void)scrollToIndex:(NSInteger)targetIndex{
    GXScrollView *view = (GXScrollView *)self.associatedView;
    if (targetIndex >= _totalItems) {
        if (self.infiniteLoop) {//无限循环
            targetIndex = _totalItems * 0.5;
            [view scrollToItemAtIndexPath:[NSIndexPath indexPathForRow:targetIndex inSection:0] atScrollPosition:UICollectionViewScrollPositionNone animated:NO];
        }
        return;
    }
    //滚动到指定位置，打开系统默认滚动动画，看到过渡效果
    [view scrollToItemAtIndexPath:[NSIndexPath indexPathForRow:targetIndex inSection:0] atScrollPosition:UICollectionViewScrollPositionNone animated:YES];
}

- (NSInteger)currentIndex {
    GXScrollView *view = (GXScrollView *)self.associatedView;
    if (view.frame.size.width == 0 || view.frame.size
        .height == 0) {
        return 0;
    }
    UICollectionViewFlowLayout *flowLayout = (UICollectionViewFlowLayout *)view.collectionViewLayout;
    NSInteger index = 0;
    if (flowLayout.scrollDirection == UICollectionViewScrollDirectionHorizontal) {//水平滑动
        index = (view.contentOffset.x + self.itemWidth * 0.5) / self.itemWidth;
    }else{
        index = (view.contentOffset.y + _containerHeight* 0.5)/ _containerHeight;
    }
    //返回两个数中的最大值
    return MAX(0,index);
}

- (int)pageControlIndexWithCurrentCellIndex:(NSInteger)index {
    return (int)index % self.items.count;
}

- (void)positionPageControl:(UIScrollView *)scrollView{
    if (!self.items.count) return; // 解决清除timer时偶尔会出现的问题
    int itemIndex = (int)[self currentIndex];
    int indexOnPageControl = [self pageControlIndexWithCurrentCellIndex:itemIndex];
    
    UIPageControl *pageControl = (UIPageControl *)_pageControl;
    CGRect frame = pageControl.frame;
    frame.size.width = 150;
    frame.origin.x = scrollView.contentOffset.x + _itemWidth - 120;
    frame.origin.y = _containerHeight - 20;
    pageControl.frame = frame;
    pageControl.currentPage = indexOnPageControl;
}
#pragma mark 定时器
- (void)setupTimer{
    [self invalidateTimer];
    NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(automaticScroll) userInfo:nil repeats:YES];
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    self.timer = timer ;
    
}

- (void)invalidateTimer{
    [self.timer invalidate];
    self.timer = nil;
}

#pragma mark - 懒加载

- (NSMutableArray *)items{
    if (!_items) {
        _items = [NSMutableArray array];
    }
    return _items;
}

- (GXTemplateItem *)subTemplateItem{
    if (!_subTemplateItem) {
        _subTemplateItem = [[GXTemplateItem alloc] init];
    }
    return _subTemplateItem;
}


@end
