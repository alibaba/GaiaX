//
//  GXGridNode.m
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

#import "GXGridNode.h"
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
#import "GXGridView.h"
#import "UIView+GX.h"
#import "GXUtils.h"

@interface GXGridNode () <UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout>{
    //是否初始化
    BOOL _hasInit;
    //是否正在展示
    BOOL _isOnShow;
    //坑位尺寸
    CGSize _itemSize;
    //是否需要reload
    CGFloat _tmpWidth;
    BOOL _isNeedReload;
    //滚动事件
    GXEvent *_scrollEvent;
}

// 数据源
@property (nonatomic, strong) NSArray *dataArray;
@property (nonatomic, strong) NSMutableArray *items;
// 坑位模板的item信息
@property (nonatomic, strong) GXTemplateItem *subTemplateItem;

@end


@implementation GXGridNode

//创建视图
- (GXGridView *)creatView{
    GXGridView *view = (GXGridView *)self.associatedView;
    if (!view) {
        //创建layout
        UICollectionViewFlowLayout *flowLayout = [[UICollectionViewFlowLayout alloc] init];
        //创建view
        view = [[GXGridView alloc] initWithFrame:CGRectZero collectionViewLayout:flowLayout];
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
- (void)renderView:(GXGridView *)view{
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
        [self calculateItemSize];
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
    //重置标识位
    _isNeedReload = NO;
    
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
    
    //重新刷新布局标识
    _isNeedReload = self.templateContext.isNeedLayout;
    if (!_isNeedReload) {
        GXGridView *gridView = (GXGridView *)self.associatedView;
        [gridView reloadData];
    }
    
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


#pragma mark - 处理extend

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
    
    if (!self.templateContext.isNeedLayout && !isMark) {
        isMark = [self shouldReLayout];
        return isMark;
    }
    
    return isMark;
}

- (BOOL)shouldReLayout {
    BOOL isMark = NO;
    
    //计算ItemSize
    [self calculateItemSize];
    
    //计算GridView的高度 (只有为auto时，才进行内部覆盖)
    StretchStyleSize recordSize = self.style.styleModel.recordSize;
    if (!self.scrollEnable && (recordSize.height.dimen_type == DIM_TYPE_AUTO) && [self currentWidth] > 0) {
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

- (void)updateFitContentLayout{
    [self shouldReLayout];

    //重新reload
    GXGridView *gridView = (GXGridView *)self.associatedView;
    if (gridView) {
        [gridView reloadData];
    }
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
    CGFloat measureWidth = floor(([self currentWidth] - extraWidth) / self.column);
    if (measureWidth <= 0) {
        _itemSize = CGSizeZero;
        return;
    }
    //计算
    _itemSize = [TheGXTemplateEngine sizeWithTemplateItem:self.subTemplateItem measureSize:CGSizeMake(measureWidth, NAN)];
}

- (CGFloat)currentWidth{
    CGFloat width = self.frame.size.width > 0 ? self.frame.size.width : _tmpWidth;
    return width;
}

- (void)updateTextNodes:(NSPointerArray *)textNodes{
    [textNodes addPointer:(__bridge void *)(self)];
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
    templateItem.templateVersion = @"grid";

    //rootStyle
    templateItem.rootStyleInfo = [self.fullStyleJson gx_dictionaryForKey:identifier];
    self.subTemplateItem = templateItem;
}


#pragma mark -

//注册Cell
- (void)registerItemCell{
    NSString *identifier = self.subTemplateItem.templateId;
    GXGridView *gridView = (GXGridView *)self.associatedView;
    [gridView registerClass:[GXGridViewCell class] forCellWithReuseIdentifier:identifier];
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
    GXGridViewCell *cell = (GXGridViewCell *)[collectionView dequeueReusableCellWithReuseIdentifier:identifier forIndexPath:indexPath];
        
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
    [TheGXTemplateEngine bindData:data measureSize:itemSize onRootView:rootView];
    
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
    
    //埋点处理
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
        
        //埋点处理
        [self handleVisibleCells];
    }
}


#pragma mark - appear

- (void)onAppear{
    [super onAppear];
    [self handleVisibleCells];
}

- (void)onDisappear{
    [super onDisappear];
}

- (void)handleVisibleCells{
    if (self.isAppear) {
        dispatch_async(dispatch_get_main_queue(), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                UICollectionView *collectionView = (UICollectionView *)self.associatedView;
                NSArray *cells = [collectionView visibleCells];
                for (GXGridViewCell *cell in cells) {
                    [cell.rootView onAppear];
                }
            });
        });
    }
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
