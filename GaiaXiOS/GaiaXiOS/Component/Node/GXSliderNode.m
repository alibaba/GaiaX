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
#import "GXEXPression.h"
#import "GXDataParser.h"
#import "GXPageControl.h"
#import "GXSliderView.h"
#import "GXRootView.h"
#import "UIColor+GX.h"
#import "NSArray+GX.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXSliderNode ()<GXPagerViewDelegate, GXPagerViewDataSource> {
    BOOL _hasInit;
    // 坑位尺寸
    CGSize _itemSize;
    //布局变化之后是否需要重新刷新数据
    BOOL _isNeedReload;
    // 获取坑位类型
    NSDictionary *_config;
    NSString *_expression;
    //visible
    NSArray *_visibleIndexPaths;
}

@property (nonatomic, strong) UIColor *indicatorUnSelectedColor;
@property (nonatomic, strong) UIColor *indicatorSelectedColor;
@property (nonatomic, assign) BOOL isShowIndicator;

@property (nonatomic, strong) NSString *indicatorPosition;
@property (nonatomic) UIEdgeInsets indicatorMargin;

@property (nonatomic, assign) CGFloat autoScrollInterval; // 自动轮播间隔，默认3s
@property (nonatomic, assign) BOOL isInfiniteLoop; // 是否循环
// 默认选中index
@property (nonatomic, assign) NSInteger selectedIndex;

//数据源
@property (nonatomic, strong) NSArray *items;
@property (nonatomic, strong) NSArray *dataArray;
// item的间距
@property (nonatomic, assign) CGFloat itemSpacing;
//坑位的复用标识
@property (nonatomic, strong) NSMutableArray *identifiers;
//子坑位的subItems
@property (nonatomic, strong) NSMutableDictionary *subTemplateItemMap;

//pageControl
@property (nonatomic, strong) GXPageControl *pageControl;

@end


@implementation GXSliderNode

//创建视图
- (GXSliderView *)creatView{
    GXSliderView *view = (GXSliderView *)self.associatedView;
    if (!view) {
        view = [[GXSliderView alloc] initWithFrame:CGRectZero];
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
- (void)renderView:(GXSliderView *)view{
    if (!_hasInit) {
        //已经初始化过
        _hasInit = YES;
        
        //delegate && dataSource
        view.delegate = self;
        view.dataSource = self;
        
        //cell注册
        [self registerItemCell];
    }

    view.autoScrollInterval = self.autoScrollInterval;
    view.isInfiniteLoop = self.isInfiniteLoop;
    view.clipsToBounds = NO;
    
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
    
    //更新frame之后重新刷新数据
    if (_isNeedReload) {
        _isNeedReload = NO;
        [self reloadSliderData];
    }
}


#pragma mark - 绑定数据

- (void)bindData:(id)data {
    _isNeedReload = NO;
    //更新数据
    NSArray *dataArray = nil;
    NSDictionary *extend = nil;
    if ([GXUtils isValidArray:data]) {
        //数据赋值
        dataArray = (NSArray *)data;
    } else if ([GXUtils isValidDictionary:data]) {
        //获取为{"value":[]}类型 & extend
        dataArray = [(NSDictionary *)data gx_arrayForKey:@"value"];
        extend = [(NSDictionary *)data gx_dictionaryForKey:@"extend"];
    }
    
    //赋值
    [self processListData:dataArray];
    
    //extend处理 & 计算itemSize
    [self handleExtend:extend isCalculate:NO];
    
    //reload
    _isNeedReload = self.templateContext.isNeedLayout;
    if (!_isNeedReload) {
        [self reloadSliderData];
    }
}

- (void)reloadSliderData {
    //计算坑位宽度
    [self calculateItemSize];

    //刷新数据
    GXSliderView *sliderView = (GXSliderView *)self.associatedView;
    [sliderView reloadData];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        // 更新pageControl的状态
        [self reloadPageControl];
        //处理指定滑动
        [self handleScrollIndex];
    });
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

- (void)updateNormalStyle:(NSDictionary *)styleInfo isMark:(BOOL)isMark {
    [super updateNormalStyle:styleInfo isMark:isMark];
    // 解析属性
    [self parseSliderProperty:styleInfo];
}

- (void)handleScrollIndex {
    GXSliderView *view = (GXSliderView *)self.associatedView;
    NSInteger curIndex = view.curIndex;
    NSInteger selIndex = self.selectedIndex;
    if (selIndex != curIndex) {
        [view scrollToItemAtIndex:selIndex animate:NO];
    }
}


#pragma mark - 属性设置

- (void)configureStyleInfo:(NSDictionary *)styleInfo{
    [super configureStyleInfo:styleInfo];
    
    //获取坑位类型 & 表达式
    [self loadItemType];
    
    //获取宽度信息
    NSString *borderRadius = [styleInfo gx_stringForKey:@"border-radius"];
    if (borderRadius.length) {
        self.cornerRadius = [GXStyleHelper converSimpletValue:borderRadius];
    } else {
        self.cornerRadius = 0.f;
    }
}

- (void)configureViewInfo:(NSDictionary *)viewInfo{
    [super configureViewInfo:viewInfo];
    
    // 解析属性
    self.indicatorMargin = UIEdgeInsetsZero;
    self.indicatorPosition = @"bottom-right";
    [self parseSliderProperty:viewInfo];
    
    //获取复用标志 & 子模板id
    NSArray *identifiers = [viewInfo gx_arrayForKey:@"layers"];
    [self setupItemIdentifier:identifiers];
    
    //item-spacing
    NSString *itemSpacing = [viewInfo gx_stringForKey:@"item-spacing"];
    if (itemSpacing.length == 0) {
        itemSpacing = [viewInfo gx_stringForKey:@"line-spacing"];
    }
    self.itemSpacing = [GXStyleHelper converSimpletValue:itemSpacing];
}

//获取坑位类型 & 表达式
- (void)loadItemType{
    //获取坑位类型
    NSDictionary *dataDict = self.data;
    if ([GXUtils isValidDictionary:dataDict]) {
        NSDictionary *extend = [dataDict gx_dictionaryForKey:@"extend"];
        if (extend) {
            //itemType
            NSDictionary *itemType = [extend gx_dictionaryForKey:@"item-type"];
            if (itemType) {
                //获取path
                NSString *path = [itemType gx_stringForKey:@"path"];
                if (path.length) {
                    _expression = path;
                }
                //获取config, 兼容v1和v2
                NSDictionary *tmpConfig = [itemType gx_dictionaryForKey:@"config"];
                if (tmpConfig.count > 0) {
                    NSMutableDictionary *result = [NSMutableDictionary dictionaryWithCapacity:2];
                    [tmpConfig enumerateKeysAndObjectsUsingBlock:^(NSString *  _Nonnull key, NSString *  _Nonnull obj, BOOL * _Nonnull stop) {
                        if ([obj isKindOfClass: [NSString class]]) {
                            NSString *value = [obj stringByReplacingOccurrencesOfString:@"'" withString:@""];
                            [result gx_setObject:value forKey:key];
                        }
                    }];
                    _config = result;
                }
            }
        }
    }
}

- (void)parseSliderProperty:(NSDictionary *)info {
    if ([GXUtils isValidDictionary:info]) {
        NSString *indicatorUnSelectedColor = [info gx_stringForKey:@"slider-indicator-unselected-color"];
        if (indicatorUnSelectedColor.length) {
            self.indicatorUnSelectedColor = [UIColor gx_colorWithString:indicatorUnSelectedColor];
        }
        
        NSString *indicatorSelectedColor = [info gx_stringForKey:@"slider-indicator-selected-color"];
        if (indicatorSelectedColor.length) {
            self.indicatorSelectedColor = [UIColor gx_colorWithString:indicatorSelectedColor];
        }
        
        NSString *isShowIndicator = [info gx_stringForKey:@"slider-has-indicator"];
        if (isShowIndicator.length) {
            self.isShowIndicator = [isShowIndicator boolValue];
        }

        NSString *autoScrollInterval = [info gx_stringForKey:@"slider-scroll-time-interval"];
        if (autoScrollInterval.length) {
            NSInteger tmpInterval = [autoScrollInterval floatValue] / 1000.f;
            self.autoScrollInterval = tmpInterval > 0 ? tmpInterval : 0;
        }
        
        NSString *isInfiniteLoop = [info gx_stringForKey:@"slider-infinity-scroll"];
        if (isInfiniteLoop.length) {
            self.isInfiniteLoop = [isInfiniteLoop boolValue];
        }

        NSString *selectedIndex = [info gx_stringForKey:@"slider-selected-index"];
        if (selectedIndex.length) {
            NSInteger tmpIndex = [selectedIndex integerValue];
            self.selectedIndex = tmpIndex > 0 ? tmpIndex : 0;
        }

        NSString *indicatorMargin = [info gx_stringForKey:@"slider-indicator-margin"];
        if (indicatorMargin.length) {
            self.indicatorMargin = UIEdgeInsetsFromString(indicatorMargin);
        }
        
        NSString *indicatorPosition = [info gx_stringForKey:@"slider-indicator-position"];
        if (indicatorPosition.length) {
            self.indicatorPosition = indicatorPosition;
        }
        
        NSString *viewClass = [info gx_stringForKey:@"slider-indicator-class-ios"];
        if (viewClass.length) {
            
        }
    }
}


#pragma mark - 坑位相关

//注册Cell
- (void)registerItemCell{
    NSInteger count = self.identifiers.count;
    GXPagerView *sliderView = (GXPagerView *)self.associatedView;
    for (int i = 0; i < count; i++) {
        //获取复用标识
        NSString *identifier = [self.identifiers gx_objectAtIndex:i];
        //注册坑位cell
        [sliderView registerClass:[GXSliderViewCell class] forCellWithReuseIdentifier:identifier];
    }
}


//获取坑位类型
- (void)setupItemIdentifier:(NSArray *)identifiers{
    NSInteger count = identifiers.count;
    for (int i = 0; i < count; i++) {
        NSDictionary *identifierDict = [identifiers gx_objectAtIndex:i];
        NSString *identifier = [identifierDict gx_stringForKey:@"id"];
        [self.identifiers gx_addObject:identifier];
        
        //templateItem
        GXTemplateItem *templateItem = [[GXTemplateItem alloc] init];
        templateItem.isLocal = self.templateItem.isLocal;
        templateItem.bizId = self.templateItem.bizId;
        templateItem.templateVersion = @"slider";
        templateItem.templateId = identifier;

        //子坑位样式信息更新
        templateItem.rootStyleInfo = [self.fullStyleJson gx_dictionaryForKey:identifier];
        [self.subTemplateItemMap gx_setObject:templateItem forKey:identifier];
    }
}

//获取重用标识
- (NSString *)identifierWithIndex:(NSInteger)index{
    //获取坑位类型
    NSString *identifier = [self.identifiers gx_objectAtIndex:0];
    
    if (_expression.length > 0 && _config.count > 0) {
        GXTemplateData *data = [self.items gx_objectAtIndex:index];
        id type = (NSString *)[GXExpression valueWithExpression:_expression Source:data.data];
        if ([GXUtils isValidString:type]) {
            //string类型
            NSString *key = (NSString *)type;
            identifier = [_config gx_stringForKey:key] ?: identifier;
        } else if ([type isKindOfClass:[NSNumber class]]) {
            //number类型
            NSString *key = [type stringValue];
            identifier = [_config gx_stringForKey:key] ?: identifier;
        }
    }
    
    return identifier;
}

//通过identifier获取templateItem
- (GXTemplateItem *)templateItemWithIdentifier:(NSString *)identifier{
    GXTemplateItem *templateItem = [self.subTemplateItemMap objectForKey:identifier];
    return templateItem;
}

//计算itemSize
- (void)calculateItemSize{
    //获取MeasureSize
    CGFloat measureWidth = self.frame.size.width;
    CGFloat measureHeight = NAN;
    CGSize itemMeasurSize = CGSizeMake(measureWidth, measureHeight);
    
    NSString *identifier = [self identifierWithIndex:0];
    GXTemplateItem *templateItem = [self templateItemWithIdentifier:identifier];
    
    _itemSize = [TheGXTemplateEngine sizeWithTemplateItem:templateItem measureSize:itemMeasurSize];
}


#pragma mark - 计算高度

- (void)calculateWithData:(id)data{
    //数据
    NSArray *dataArray = nil;
    NSDictionary *extend = nil;
    if ([GXUtils isValidArray:data]) {
        // 数据赋值
        dataArray = (NSArray *)data;
        
    } else if ([GXUtils isValidDictionary:data]) {
        //获取为{"value":[]}类型 & extend
        dataArray = [(NSDictionary *)data gx_arrayForKey:@"value"];
        extend = [(NSDictionary *)data gx_dictionaryForKey:@"extend"];
    }
    
    //赋值items
    [self processListData:dataArray];
    
    //计算extend
    [self handleExtend:extend isCalculate:YES];
}


#pragma mark - 数据源处理

//将原始数据转化GXTemplateData
- (void)processListData:(NSArray *)dataArray{
    if (dataArray == self.dataArray) {
        return;
    }
    self.dataArray = dataArray;
        
    //生成数据源
    NSMutableArray *tmpItems = [NSMutableArray array];
    for (int i = 0; i < dataArray.count; i++) {
        GXTemplateData *templateData = [[GXTemplateData alloc] init];
        templateData.data = [dataArray gx_objectAtIndex:i];
        templateData.dataListener = self.templateContext.templateData.dataListener;
        templateData.eventListener = self.templateContext.templateData.eventListener;
        [tmpItems gx_addObject:templateData];
    }
    
    self.items = tmpItems;
}


#pragma mark - GXPagerViewDelegate & GXPagerViewDataSource

- (GXPagerLayoutConfig *)layoutForPagerView:(GXPagerView *)pageView {
    // layout
    GXPagerLayoutConfig *layout = [[GXPagerLayoutConfig alloc] init];
    layout.sectionInset = UIEdgeInsetsMake(0, 0, 0, 0);
    layout.layoutType = GXPagerLayoutTypeNormal;
    layout.itemHorizontalCenter = YES;
    layout.itemVerticalCenter = YES;
    layout.itemSize = _itemSize;
    layout.itemSpacing = 10.f;
    return layout;
}

- (NSInteger)numberOfItemsInPagerView:(nonnull GXPagerView *)pageView {
    return self.dataArray.count;
}

- (__kindof UICollectionViewCell *)pagerView:(nonnull GXPagerView *)pagerView cellForItemAtIndex:(NSInteger)index {
    //获取坑位size
    CGSize itemSize = _itemSize;
    
    //重用标识
    NSString *identifier = [self identifierWithIndex:index];
    GXSliderViewCell *cell = (GXSliderViewCell *)[pagerView dequeueReusableCellWithReuseIdentifier:identifier forIndex:index];
    
    //获取视图
    GXRootView *rootView = cell.rootView;
    if (!rootView) {
        GXTemplateItem *templateItem = [self templateItemWithIdentifier:identifier];
        rootView = (GXRootView *)[TheGXTemplateEngine creatViewByTemplateItem:templateItem measureSize:itemSize];
        if (rootView) {
            [cell.contentView addSubview:rootView];
            cell.rootView = rootView;
        }
    }
    
    //index属性
    rootView.gxNode.index = index;

    //数据绑定
    GXTemplateData *data = [self.items objectAtIndex:index];
    [TheGXTemplateEngine bindData:data measureSize:itemSize onRootView:rootView];
    
    return cell;
}

- (void)pagerView:(GXPagerView *)pageView didScrollFromIndex:(NSInteger)fromIndex toIndex:(NSInteger)toIndex {
    if (_pageControl) {
        _pageControl.currentPage = toIndex;
    }
    //曝光
    if (self.isAppear) {
        [self getVisibleItems];
    }
}


#pragma mark - onShow & onHidden

- (void)onShow{
    [super onShow];
    //触发曝光
    [self getVisibleItems];
    // 性能优化 - 可见时才轮播
    GXSliderView *sliderView = (GXSliderView *)self.associatedView;
    sliderView.autoScrollInterval = self.autoScrollInterval;
}

- (void)onHide{
    [super onHide];
    //重置曝光
    [self removeVisibleItems];
    // 性能优化 - 不可见时停止
    GXSliderView *sliderView = (GXSliderView *)self.associatedView;
    sliderView.autoScrollInterval = 0;
}

- (void)getVisibleItems{
    UICollectionView *view = (UICollectionView *)self.associatedView;
    NSArray *indexPaths = view.indexPathsForVisibleItems;
    for (NSIndexPath *indexPath in indexPaths) {
        if (![_visibleIndexPaths containsObject:indexPath]) {
            GXSliderViewCell *cell = (GXSliderViewCell *)[view cellForItemAtIndexPath:indexPath];
            GXRootView *rootView = cell.rootView;
            if (rootView && [rootView respondsToSelector:@selector(onAppear)]) {
                [rootView onAppear];
            }
        }
    }
    _visibleIndexPaths = indexPaths;
}

- (void)removeVisibleItems{
    UICollectionView *view = (UICollectionView *)self.associatedView;
    for (NSIndexPath *indexPath in _visibleIndexPaths) {
        GXSliderViewCell *cell = (GXSliderViewCell *)[view cellForItemAtIndexPath:indexPath];
        GXRootView *rootView = cell.rootView;
        if (rootView && [rootView respondsToSelector:@selector(onDisappear)]) {
            [rootView onDisappear];
        }
    }
    _visibleIndexPaths = nil;
}


#pragma mark - lazy load

- (NSMutableArray *)identifiers{
    if (!_identifiers) {
        _identifiers = [NSMutableArray array];
    }
    return _identifiers;
}

- (NSMutableDictionary *)subTemplateItemMap{
    if (!_subTemplateItemMap) {
        _subTemplateItemMap = [NSMutableDictionary dictionary];
    }
    return _subTemplateItemMap;
}

- (GXPageControl *)pageControl{
    if (!_pageControl) {
        _pageControl = [[GXPageControl alloc] initWithFrame:CGRectZero];
        _pageControl.unSelectedPageWidthHeight = 4;
        _pageControl.userInteractionEnabled = NO;
        _pageControl.selectedPageWidth = 9;
        _pageControl.pageGapWidth = 2;
    }
    return _pageControl;
}

- (void)reloadPageControl {
    if (self.dataArray.count > 1 && self.isShowIndicator) {
        self.pageControl.hidden = NO;
        self.pageControl.pageCount = self.dataArray.count;
        self.pageControl.currentPage = self.selectedIndex;
        self.pageControl.selectedPageColor = self.indicatorSelectedColor;
        self.pageControl.unselectedPageColor = self.indicatorUnSelectedColor;
        self.pageControl.center = self.associatedView.center;
        [self.associatedView addSubview:self.pageControl];
        // 更新位置
        [self updatePageControlPosition];
    } else {
        if (_pageControl) {
            _pageControl.hidden = YES;
        }
    }
}

- (void)updatePageControlPosition {
    if (self.indicatorPosition.length > 0 && _pageControl) {
        CGFloat sliderWidth = self.associatedView.gx_width;
        CGFloat sliderHeight = self.associatedView.gx_height;

        if ([self.indicatorPosition isEqualToString:@"bottom-left"]) {
            _pageControl.gx_left = self.indicatorMargin.left;
            _pageControl.gx_bottom = sliderHeight - self.indicatorMargin.bottom;

        } else if ([self.indicatorPosition isEqualToString:@"bottom-center"]) {
            _pageControl.gx_centerX = self.frame.size.width / 2.0;
            _pageControl.gx_bottom = sliderHeight - self.indicatorMargin.bottom;
            
        } else if ([self.indicatorPosition isEqualToString:@"bottom-right"]) {
            _pageControl.gx_right = sliderWidth - self.indicatorMargin.right;
            _pageControl.gx_bottom = sliderHeight - self.indicatorMargin.bottom;

        } else  if ([self.indicatorPosition isEqualToString:@"top-left"]) {
            _pageControl.gx_left = self.indicatorMargin.left;
            _pageControl.gx_top = self.indicatorMargin.top;

        } else if ([self.indicatorPosition isEqualToString:@"top-center"]) {
            _pageControl.gx_centerX = self.frame.size.width / 2.0;
            _pageControl.gx_top = self.indicatorMargin.top;

        } else if ([self.indicatorPosition isEqualToString:@"top-right"]) {
            _pageControl.gx_right = sliderWidth - self.indicatorMargin.right;
            _pageControl.gx_top = self.indicatorMargin.top;

        } else {
            _pageControl.gx_right = self.indicatorMargin.right;
            _pageControl.gx_bottom = self.indicatorMargin.bottom;
        }
    }
}

@end
