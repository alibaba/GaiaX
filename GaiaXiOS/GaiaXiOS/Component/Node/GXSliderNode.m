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
#import "NSDictionary+GX.h"
#import "GXStyleHelper.h"
#import "GXScrollView.h"
#import "GXEXPression.h"
#import "GXDataParser.h"
#import "NSArray+GX.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXSliderNode ()<UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout, GXFlowLayoutDelegate> {
    //是否初始化过
    BOOL _hasInit;
    //滚动事件
    GXEvent *_scrollEvent;
    //容器高度
    CGFloat _containerHeight;
    //区分坑位的type & config
    NSDictionary *_config;
    NSString *_expression;
}

//数据源
@property (nonatomic, strong) NSArray *dataArray;
@property (nonatomic, strong) NSMutableArray *items;
//坑位Size数组
@property (nonatomic, strong) NSMutableArray *sizeValues;
//坑位的复用标识
@property (nonatomic, strong) NSMutableArray *identifiers;
//子坑位的subItems
@property (nonatomic, strong) NSMutableDictionary *subTemplateItems;
//定时器
@property (nonatomic, weak) NSTimer *timer;
//指示器
@property (nonatomic , strong) UIPageControl *pageControl;
//是否自动轮播
@property (nonatomic, assign) bool autoScroll;
//轮播时长
@property (nonatomic, assign) CGFloat interval;
//是否开始无限轮播
@property (nonatomic, assign) bool infiniteLoop;
//是否展示指示器
@property (nonatomic, assign) bool isShowPageControl;
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
        //创建view
        GXFlowLayout *flowLayout = [[GXFlowLayout alloc] init];
        view = [[GXScrollView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
        view.gxNode = self;
        view.gxNodeId = self.nodeId;
        view.gxBizId = self.templateItem.bizId;
        view.gxTemplateId = self.templateItem.templateId;
        view.gxTemplateVersion = self.templateItem.templateVersion;
        view.pagingEnabled = YES;
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
        GXFlowLayout *flowLayout = (GXFlowLayout *)view.collectionViewLayout;
        flowLayout.delegate = self;
        // 是否预估宽高
        flowLayout.estimatedItemSize = CGSizeZero;
        // scrollDirection
        flowLayout.scrollDirection = self.scrollDirection;
        // lineSpacing 滚动方向的间距
        flowLayout.minimumLineSpacing = 0;
        // interitemSpacing 滚动方向垂直的间距
        flowLayout.minimumInteritemSpacing = self.itemSpacing;
        
        // delegate && dataSource
        view.delegate = self;
        view.dataSource = self;
        // 内容inset
        view.contentInset = self.contentInset;
        // clickToBounds
        view.clipsToBounds = self.clipsToBounds;
        // 滚动条
        view.showsVerticalScrollIndicator = NO;
        view.showsHorizontalScrollIndicator = NO;
        
        //cell注册
        [self registerItemCell];
        
        //指示器创建
        UIPageControl *pageControl = [[UIPageControl alloc] init];
        pageControl.frame = CGRectZero;
//        pageControl.pageIndicatorTintColor = [UIColor colorWithRed:255/255.0 green:255/255.0 blue:255/255.0 alpha:0.3];
//        pageControl.currentPageIndicatorTintColor = [UIColor colorWithRed:255/255.0 green:255/255.0 blue:255/255.0 alpha:1];
        pageControl.pageIndicatorTintColor = [UIColor redColor];
        pageControl.currentPageIndicatorTintColor = [UIColor greenColor];
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
    
    //设置背景色
    [self setupNormalBackground:view];
    //设置圆角属性
    [self setupCornerRadius:view];
}


#pragma mark - 绑定数据

- (void)bindData:(NSDictionary *)data{
    NSArray *dataArray = nil;
    NSDictionary *extend = nil;
    if ([GXUtils isValidDictionary:data]) {
        //获取为{"value":[]}类型 & extend
        dataArray = [data gx_arrayForKey:@"value"];
        extend = [data gx_dictionaryForKey:@"extend"];
    }
    
    //赋值
    [self processListData:dataArray];
    
    //extend处理 & 计算itemSize
    [self handleExtend:extend isCalculate:NO];
    
    //轮播属性初始化
    [self handleSlider];
    
    //刷新数据
    GXScrollView *scrollView = (GXScrollView *)self.associatedView;
    [scrollView reloadData];
}


#pragma mark - 计算高度

- (void)calculateWithData:(NSDictionary *)data{
    //数据
    NSArray *dataArray = nil;
    NSDictionary *extend = nil;
    if ([GXUtils isValidDictionary:data]) {
        //获取为{"value":[]}类型 & extend
        dataArray = [data gx_arrayForKey:@"value"];
        extend = [data gx_dictionaryForKey:@"extend"];
    }
    
    //赋值items
    [self processListData:dataArray];
    
    //计算extend
    [self handleExtend:extend isCalculate:YES];
}


#pragma mark -  处理extend

- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{
    //更新布局属性 & 标记
    BOOL isMark = [self updateLayoutStyle:extend];
    
    //更新普通属性
    if (!isCalculate) {
        [self updateNormalStyle:extend isMark:isMark];
    }
    
    //处理指定滑动
    [self handleScrollToIndex:extend];
    
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

//更新布局属性
- (BOOL)updateLayoutStyle:(NSDictionary *)styleInfo{
    BOOL isMark = [super updateLayoutStyle:styleInfo];
    
    //计算坑位宽度
    [self calculateItemSize:styleInfo];
    
    //横向滚动时，需要比较容器高度和坑位高度，取最大值
    if (self.scrollDirection == UICollectionViewScrollDirectionHorizontal) {
        //如果是pt/px，则不进行计算
        StretchStyleSize recordSize = self.style.styleModel.recordSize;
        if (recordSize.height.dimen_type == DIM_TYPE_AUTO) {
            //获取当前的size
            StretchStyleSize size = self.style.styleModel.size;
            CGFloat scrollHeight = size.height.dimen_value;
            if (scrollHeight == _containerHeight) {
                //如果是pt/px,并且坑位高度=容器高度
                return isMark;
            }
            
            //尺寸不一致时，更新layout
            StretchStyleDimension height = {
                .dimen_type = DIM_TYPE_POINTS,
                .dimen_value = _containerHeight
            };
            size.height = height;
            self.style.styleModel.size = size;
            
            isMark = YES;
        }
        
    }
    
    return isMark;
}


#pragma mark - 坑位相关

//注册Cell
- (void)registerItemCell{
    NSInteger count = self.identifiers.count;
    GXScrollView *scrollView = (GXScrollView *)self.associatedView;
    for (int i = 0; i < count; i++) {
        //获取复用标识
        NSString *identifier = [self.identifiers gx_objectAtIndex:i];
        //注册坑位cell
        [scrollView registerClass:[GXScrollViewCell class] forCellWithReuseIdentifier:identifier];
    }
}

//获取坑位类型
- (void)setupItemIdentifier:(NSArray *)identifiers{
    NSInteger count = identifiers.count;
    for (int i = 0; i < count; i++) {
        //获取identifier
        NSDictionary *identifierDict = [identifiers gx_objectAtIndex:i];
        NSString *identifier = [identifierDict gx_stringForKey:@"id"];
        [self.identifiers gx_addObject:identifier];
        
        //templateItem
        GXTemplateItem *templateItem = [[GXTemplateItem alloc] init];
        templateItem.isLocal = self.templateItem.isLocal;
        templateItem.bizId = self.templateItem.bizId;
        templateItem.templateId = identifier;
        templateItem.templateVersion = @"slider";

        //子坑位样式信息更新
        templateItem.rootStyleInfo = [self.fullStyleJson gx_dictionaryForKey:identifier];
        [self.subTemplateItems gx_setObject:templateItem forKey:identifier];
    }
}

//获取重用标识
- (NSString *)identifierWithIndex:(NSInteger)index{
    NSString *identifier = [self.identifiers gx_objectAtIndex:0];
    if (_expression && _config.count > 0) {
        GXTemplateData *data = [self.items gx_objectAtIndex:index];
        id type = (NSString *)[GXExpression valueWithExpression:_expression Source:data.data];
        //类型处理
        if ([GXUtils isValidString:type]) {//string类型
            NSString *key = (NSString *)type;
            identifier = [_config gx_stringForKey:key] ?: identifier;
            identifier = [identifier stringByReplacingOccurrencesOfString:@"'" withString:@""];
            
        } else if ([type isKindOfClass:[NSNumber class]]){//number类型
            NSString *key = [type stringValue];
            identifier = [_config gx_stringForKey:key] ?: identifier;
        }
        
    }
    return identifier;
}

//通过identifier获取templateItem
- (GXTemplateItem *)templateItemWithIdentifier:(NSString *)identifier{
    GXTemplateItem *templateItem = [self.subTemplateItems objectForKey:identifier];
    return templateItem;
}


//计算itemSize
- (void)calculateItemSize:(NSDictionary *)extend{
    CGFloat measureWidth = NAN;
    CGFloat measureHeight = NAN;
    if (self.scrollDirection == UICollectionViewScrollDirectionVertical) {
        //竖向布局，需要根据列数获取宽度 - 向下取整
        CGFloat extraWidth = self.contentInset.left + self.contentInset.right;
        measureWidth = floor(self.frame.size.width - extraWidth);
    }else{
        measureWidth = [self itemWidthForRule];
        self.itemWidth = measureWidth;
    }
    CGSize itemMeasurSize = CGSizeMake(measureWidth, measureHeight);
    
    //计算itemSize
    _containerHeight = 0.f;
    CGSize itemSize = CGSizeZero;
    [self.sizeValues removeAllObjects];
    for (int i = 0; i < self.items.count; i++) {
        //获取坑位类型
        NSString *identifier = [self identifierWithIndex:i];
        GXTemplateData *data = [self.items gx_objectAtIndex:i];
        GXTemplateItem *templateItem = [self templateItemWithIdentifier:identifier];
        if (self.scrollDirection == UICollectionViewScrollDirectionHorizontal) {
            NSString *widthStr = [templateItem.rootStyleInfo gx_stringForKey:@"width"];
            if (widthStr.length > 0) {
                StretchStyleDimension width = [GXStyleHelper convertAutoValue:widthStr];
                if (width.dimen_type == DIM_TYPE_POINTS) {
                    itemMeasurSize.width = width.dimen_value;
                }
            }
        }
        //计算itemSize
        itemSize = [TheGXTemplateEngine sizeWithTemplateItem:templateItem measureSize:itemMeasurSize data:data];
        //获取高度
        CGFloat itemHeight = itemSize.height;
        if (itemHeight > _containerHeight) {
            _containerHeight = itemHeight;
        }
        //添加到数组中
        [self.sizeValues gx_addObject:[NSValue valueWithCGSize:itemSize]];
    }
    
}

//单屏坑位宽度
- (CGFloat)itemWidthForRule{
    CGFloat width = 0.f;
    CGFloat gap = self.itemSpacing;
    //CGFloat margin = self.contentInset.left;
    CGFloat tmpWidth = self.frame.size.width;
    width = tmpWidth - gap;
    return width;
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


#pragma mark - UICollectionViewDelegate & UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.totalItems;
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath {
    NSValue *sizeValue = [self.sizeValues gx_objectAtIndex:[self pageControlIndexWithCurrentCellIndex:indexPath.item]];
    CGSize itemSize = sizeValue ? [sizeValue CGSizeValue] : CGSizeZero;
    return itemSize;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(nonnull NSIndexPath *)indexPath {
    //重用标识
    NSInteger index = [self pageControlIndexWithCurrentCellIndex:indexPath.item];
    NSString *identifier = [self identifierWithIndex:index];
    GXScrollViewCell *cell = (GXScrollViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
    
    //获取坑位size
    NSValue *value = [self.sizeValues gx_objectAtIndex:[self pageControlIndexWithCurrentCellIndex:indexPath.item]];
    CGSize itemSize = [value CGSizeValue];
    
    //获取视图
    GXRootView *rootView = cell.rootView;
    if (!rootView) {
        GXTemplateItem *templateItem = [self templateItemWithIdentifier:identifier];
        rootView = (GXRootView *)[TheGXTemplateEngine creatViewByTemplateItem:templateItem measureSize:itemSize];
        cell.rootView = rootView;
        [cell.contentView addSubview:rootView];
    }
    
    //index属性
    rootView.gxNode.index = index;
    
    //数据绑定
    GXTemplateData *data = [self.items objectAtIndex:index];
    [TheGXTemplateEngine bindData:data   onView:rootView];
    
    return cell;
}

#pragma mark - UIScrollViewDelegate
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView{
    if (self.autoScroll) {
        [self invalidateTimer];
    }
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
    [self positionPageControl:scrollView];
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
    
    //处理埋点
    [self handleVisibleCells];
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
        
        //处理埋点
        [self handleVisibleCells];
    }
    
    if (self.autoScroll) {
        [self setupTimer];
    }
}


#pragma mark - appear

- (void)onShow{
    [super onShow];
    [self handleVisibleCells];
}

- (void)onHide{
    [super onHide];
}

- (void)handleVisibleCells{
    if (self.isAppear) {
        dispatch_async(dispatch_get_main_queue(), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                UICollectionView *collectionView = (UICollectionView *)self.associatedView;
                NSArray *cells = [collectionView visibleCells];
                for (GXScrollViewCell *cell in cells) {
                    [cell.rootView onAppear];
                }
            });
        });
    }
}


#pragma mark - 轮播

- (void)handleSlider {
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
    [self positionPageControl:view];
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
    GXFlowLayout *flowLayout = (GXFlowLayout *)view.collectionViewLayout;
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
    frame.size.width =  [pageControl sizeForNumberOfPages:self.items.count].width;
    frame.origin.x = scrollView.contentOffset.x + _itemWidth - frame.size.width;
    frame.origin.y = _containerHeight - 20;
    pageControl.frame = frame;
    pageControl.currentPage = indexOnPageControl;
}


#pragma mark 定时器

- (void)setupTimer{
    [self invalidateTimer];
    NSTimer *timer = [NSTimer scheduledTimerWithTimeInterval:self.interval target:self selector:@selector(automaticScroll) userInfo:nil repeats:YES];
    [[NSRunLoop mainRunLoop] addTimer:timer forMode:NSRunLoopCommonModes];
    self.timer = timer ;
    
}

- (void)invalidateTimer{
    [self.timer invalidate];
    self.timer = nil;
}

- (void)handleScrollToIndex:(NSDictionary *) extend{
    NSUInteger index = [extend gx_intForKey:@"scroll-index"];
    if (index > 0 && index < self.items.count) {
        BOOL animated = [extend gx_boolForKey:@"scroll-animated"];
        NSUInteger duration = [extend gx_intForKey:@"scroll-duration"];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(duration * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:index inSection:0];
            [(GXScrollView *)self.associatedView scrollToItemAtIndexPath:indexPath atScrollPosition:UICollectionViewScrollPositionNone animated:animated];
        });
    }
}


#pragma mark - 属性设置

//读取styleInfo
- (void)configureStyleInfo:(NSDictionary *)styleInfo{
    [super configureStyleInfo:styleInfo];
    
    //获取坑位类型 & 表达式
    [self parserItemType];
    
    //获取宽度信息
    NSString *borderRadius = [styleInfo gx_stringForKey:@"border-radius"];
    if (borderRadius.length) {
        self.cornerRadius = [GXStyleHelper converSimpletValue:borderRadius];
    } else {
        self.cornerRadius = 0.f;
    }
}

//读取viewInfo
- (void)configureViewInfo:(NSDictionary *)viewInfo{
    [super configureViewInfo:viewInfo];
    
    //说明1 - v 横向滑动子视图必须有指定宽高
    //说明2 - h 竖向滑动子视图宽度设置100%，高度内部撑开
    //获取复用标志 & 子模板id
    NSArray *identifiers = [viewInfo gx_arrayForKey:@"layers"];
    [self setupItemIdentifier:identifiers];
        
    //item-spacing
    NSString *itemSpacing = [viewInfo gx_stringForKey:@"item-spacing"];
    self.itemSpacing = [GXStyleHelper converSimpletValue:itemSpacing];
    
    //edge-inset
    UIEdgeInsets edgeInsets = UIEdgeInsetsMake(0, 0, 0, 0);
    StretchStyleRect padding = self.style.styleModel.padding;
    if (padding.top.dimen_type == DIM_TYPE_POINTS) {
        edgeInsets.top = (CGFloat) padding.top.dimen_value;
    }
    if (padding.bottom.dimen_type == DIM_TYPE_POINTS) {
        edgeInsets.bottom = (CGFloat) padding.bottom.dimen_value;
    }
    if (padding.left.dimen_type == DIM_TYPE_POINTS) {
        edgeInsets.left = (CGFloat) padding.left.dimen_value;
    }
    if (padding.right.dimen_type == DIM_TYPE_POINTS) {
        edgeInsets.right = (CGFloat) padding.right.dimen_value;
    }
    self.contentInset = edgeInsets;
    
    //direction
    NSString *direction = [viewInfo gx_stringForKey:@"direction"];
    if ([direction isEqualToString:@"vertical"]) {
        //竖向滚动设置 & 容器高度就是指定高度
        self.scrollDirection = UICollectionViewScrollDirectionVertical;
        
    } else {
        //横向滚动设置，容器高度不确定
        self.scrollDirection = UICollectionViewScrollDirectionHorizontal;
    }
    
    //slider属性设置
    [self configSlider:viewInfo];
}

//获取坑位类型 & 表达式
- (void)parserItemType{
    NSDictionary *dataDict = self.data;
    if (dataDict && [dataDict isKindOfClass:[NSDictionary class]]) {
        NSDictionary *itemType = [[dataDict gx_dictionaryForKey:@"extend"] gx_dictionaryForKey:@"item-type"];
        if (itemType) {
            //获取path
            _expression = [itemType gx_stringForKey:@"path"];
            //获取config
            _config = [itemType gx_dictionaryForKey:@"config"];
        }
    }
}

- (void)configSlider:(NSDictionary *)viewInfo{
    CGFloat interval = [viewInfo gx_floatForKey:@"slider-scroll-time-interval"];
    self.interval = interval == 0.0 ?  3 : interval/1000.;
    self.autoScroll = interval == 0.0 ? NO : YES;
    self.infiniteLoop = [viewInfo gx_boolForKey:@"slider-infinity-scroll"];
    self.isShowPageControl = [viewInfo gx_boolForKey:@"lider-has-indicator"];
}

#pragma mark - lazy load
- (NSMutableArray *)items{
    if (!_items) {
        _items = [NSMutableArray array];
    }
    return _items;
}

- (NSMutableArray *)sizeValues{
    if (!_sizeValues) {
        _sizeValues = [NSMutableArray array];
    }
    return _sizeValues;
}

- (NSMutableArray *)identifiers{
    if (!_identifiers) {
        _identifiers = [NSMutableArray array];
    }
    return _identifiers;
}

- (NSMutableDictionary *)subTemplateItems{
    if (!_subTemplateItems) {
        _subTemplateItems = [NSMutableDictionary dictionary];
    }
    return _subTemplateItems;
}


@end
