//
//  GXNode.m
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

#import "GXNode.h"
#import "GXEvent.h"
#import "GXUtils.h"
#import "UIView+GX.h"
#import "NSArray+GX.h"
#import "GXStretch.h"
#import "GXBaseNode.h"
#import "GXCommonDef.h"
#import "GXDataParser.h"
#import "GXFunctionDef.h"
#import "GXTemplateItem.h"
#import "NSDictionary+GX.h"
#import "GXTemplateEngine.h"
#import "GXTemplateContext.h"
#import "GXGridNode.h"
#import "GXScrollNode.h"
#import "GXRootViewNode.h"

@interface GXNode ()<UIGestureRecognizerDelegate>{
    //js是否ready
    BOOL _isReady;
    //Stretch
    GXStretch *_stretch;
    //当前根节点
    __weak GXNode *_rooNode;
    //手势
    UITapGestureRecognizer *_tap;
    UILongPressGestureRecognizer *_longPress;
}

//节点id
@property(nonatomic, strong) NSString *nodeId;
//节点类型
@property(nonatomic, strong) NSString *type;
@property(nonatomic, strong) NSString *subType;
//是否模板类型
@property(nonatomic, assign) BOOL isTemplateType;
//模板信息
@property(nonatomic, strong) GXTemplateItem *templateItem;
//模板信息
@property(nonatomic, strong) GXTemplateContext *templateContext;

//描述节点层级关系
@property(nonatomic, strong) NSDictionary *viewJson;
//描述节点的layout信息
@property(nonatomic, strong) NSDictionary *styleJson;
@property(nonatomic, strong) NSDictionary *fullStyleJson;

@property (nonatomic, strong) id data;
@property (nonatomic, strong) id event;
@property (nonatomic, strong) id track;
@property (nonatomic, strong) NSDictionary *animation;

@property (nonatomic, strong) id virtualData; //嵌套模板中的数据
@property (nonatomic, strong) NSDictionary *virtualExtend;

//是否需要拍平 - 针对view
@property (nonatomic, assign) BOOL isFlat;
//文本是否自适应（text有效的）
@property (nonatomic, assign) BOOL fitContent;

//当前的index(scroll/grid）,仅在expression中使用
@property (nonatomic, assign) NSInteger index;

//扁平化节点键值对, 只有根节点会赋值
@property (nonatomic, strong) NSMapTable *flatNodes;

//节点是否正在展示
@property (nonatomic, assign) BOOL isAppear;

//JS原始数据
@property (nonatomic, strong) GXTemplateData *orignalData;
//jsComponent
@property (nonatomic, strong) GaiaXJSComponent *jsComponent;

@end


@implementation GXNode

- (instancetype)initWithStyle:(GXStyle *)style children:(NSArray *)children {
    if (self = [super init]) {
        //stretch管理类
        _stretch = [GXStretch stretch];
        //节点指针创建
        _rustptr = [_stretch creatNode:style.rustptr];
        //样式
        _style = style;
        //children
        if (children && [children isKindOfClass:[NSArray class]] && children.count > 0) {
            [self setChildren:[NSMutableArray arrayWithArray:children]];
        } else{
            _children = [NSMutableArray array];
        }
        //index
        _index = -1;
    }
    return self;
}

//替换子node
- (void)replaceChild:(GXNode *)child atIndex:(NSUInteger)index {
    [_stretch replaceChild:child.rustptr atIndex:index forNode:_rustptr];
    child.parent = self;
    _children[index] = child;
}

//根据nodeId获取节点树上的某个节点
- (GXNode *)queryNodeByNodeId:(NSString *)nodeId{
    //判断nodeId有效性
    if (!nodeId || ![nodeId isKindOfClass:[NSString class]] || nodeId.length <= 0) {
        return nil;
    }
    
    //判断是否为当前节点
    if ([self.nodeId isEqualToString:nodeId]) {
        return self;
    }
    
    //遍历子节点
    GXNode *node = nil;
    for (GXNode *child in self.children) {
        node = [child queryNodeByNodeId:nodeId];
        if (node) {
            break;
        }
    }
    
    //返回对应节点
    return node;
}

//添加子node
- (void)setChildren:(NSMutableArray *)children {
    //倒序移除老节点
    NSUInteger oldChildrenLength = [_children count];
    for (NSUInteger i = 0; i < oldChildrenLength; i++) {
        [self removeChildAtIndex:oldChildrenLength - 1 - i];
    }
    
    //正序添加新节点
    NSUInteger newChildrenLength = [children count];
    for (NSUInteger i = 0; i < newChildrenLength; i++) {
        GXNode *child = children[i];
        [self addChild:child];
    }
}

- (void)addChild:(GXNode *)child {
    [_stretch addChild:child.rustptr forNode:_rustptr];
    child.parent = self;
    [_children addObject:child];
}


//移除子node
- (void)removeChildAtIndex:(NSUInteger)index {
    [_stretch removeChildAtIndex:index forNode:_rustptr];
    [_children removeObjectAtIndex:index];
}

- (void)removeChild:(GXNode *)child {
    [_stretch removeChild:child.rustptr forNode:_rustptr];
    [_children removeObject:child];
}


//计算layout
-(GXLayout *)computeLayout:(StretchSize)size {
    GXLayout *layout = [_stretch computeLayout:_rustptr witSize:size];
    return layout;
}

//设置style
- (void)setStyle:(GXStyle *)style {
    //更新当前的sytle
    [_stretch setStyle:style.rustptr forNode:_rustptr];
    _style = style;
    //释放之前的style
    [style freePrevRustptr];
}

//标记node
- (void)markDirty {
    [_stretch markDirty:_rustptr];
}

- (BOOL)dirty {
    return [_stretch isDirty:_rustptr];
}

//获取根节点
- (GXNode *)rootNode{
    if (!_rooNode) {
        GXNode *node = self;
        while (node.parent) {
            node = node.parent;
        }
        _rooNode = node;
    }
    return _rooNode;
}


//是否根节点
- (BOOL)isRootNode{
    GXNode *parentNode = self.parent;
    if (parentNode) {
        return NO;
    }
    return YES;
}

//dealloc
- (void)dealloc {
    if (_rustptr != NULL) {
        [_stretch freeNode:_rustptr];
        _rustptr = NULL;
    }
    GXLog(@"[GaiaX] 节点node释放 - %@", self);
}


@end


@implementation GXNode(Template)

- (NSString *)type{
    if (!_type) {
        _type = [self.viewJson gx_stringForKey:@"type"];
    }
    return _type;
}

- (NSString *)subType{
    if (!_subType) {
        _subType = [self.viewJson gx_stringForKey:@"sub-type"];
    }
    return _subType;
}

@end


@implementation GXNode(Render)

//创建当前view和子views
- (UIView *)applyView{
    //默认为nil
    UIView *view = nil;
    //扁平化处理
    if (TheGXTemplateEngine.isNeedFlat && self.isFlat) {
        //如果当前节点被拍平，则获取父节点的view
        view = [self superView];
    } else {
        //如果当前节点未被拍平，则创建
        view = [self creatView];
        [self renderView:view];
    }
    
    //添加子视图
    NSUInteger length = self.children.count;
    for (int i = 0; i < length; i++) {
        GXNode *childNode = [self.children objectAtIndex:i];
        UIView *childView = [childNode applyView];
        if (childView && ![childView isDescendantOfView:view]) {
            [view addSubview:childView];
        }
        //阴影处理
        if ([(GXBaseNode *)childNode isSupportShadow]) {
            [(GXBaseNode *)childNode setupShadow:childView];
        }
    }
    
    //返回视图
    return view;
}

//view层级上的super，非node层级的super
- (UIView *)superView{
    GXNode *node = self;
    UIView *view = node.associatedView;
    while (node && !view) {
        node = node.parent;
        view = node.associatedView;
    }
    return view;
}

//更新layout属性到node节点上
- (void)applyLayout:(GXLayout *)layout{
    if (layout) {
        //支持宽度度0.x设置
        CGFloat width = layout.width;
        if ((width == 0 || width == 1) &&
            self.style.styleModel.size.width.dimen_value < 1 &&
            self.style.styleModel.size.width.dimen_type == DIM_TYPE_POINTS) {
            width = self.style.styleModel.size.width.dimen_value;
        }
        
        // 支持高度0.x设置
        CGFloat height = layout.height;
        if ((height == 0 || height == 1) &&
            self.style.styleModel.size.height.dimen_value < 1 &&
            self.style.styleModel.size.height.dimen_type == DIM_TYPE_POINTS) {
            height = self.style.styleModel.size.height.dimen_value;
        }
        
        if (TheGXTemplateEngine.isNeedFlat && self.parent.isFlat) {
            //如果有父节点 || 父节点被拍平
            CGFloat x = layout.x + self.parent.frame.origin.x;
            CGFloat y = layout.y + self.parent.frame.origin.y;
            self.frame = CGRectMake(x, y, width, height);
        } else {
            //非拍平的情况
            self.frame = CGRectMake(layout.x, layout.y, width, height);
        }
        
        //获取子node
        NSUInteger count = self.children.count;
        for (int i = 0; i < count; i++) {
            //获取childNode和childLayout
            GXLayout *childLayout = layout.children[i];
            GXNode *childNode = self.children[i];
            if (childNode && childLayout) {
                [childNode applyLayout:childLayout];
            }
        }
    }
}

//更新数据(databinding + source)
- (void)applyData:(NSDictionary *)data type:(GXBindType)type{
    if (![GXUtils isValidDictionary:data]) {
        return;
    }
    
    //数据绑定
    switch (type) {
        case GXBindTypeData:{
            //绑定数据
            if (self.data || self.virtualExtend) {
                NSMutableDictionary *resultData = [GXDataParser parseData:self.data withSource:data];
                resultData = [self mergeExtendWithResult:resultData];
                [self bindData:resultData];
            }
            //绑定事件
            if (self.event) {
                NSMutableArray *resultEventArray = [NSMutableArray array];
                
                // 兼容之前单事件数据格式
                if ([self.event isKindOfClass:[NSDictionary class]]) {
                    NSDictionary *resultEvent = [GXDataParser parseData:self.event withSource:data];
                    [resultEventArray gx_addObject:resultEvent];
                } else if ([self.event isKindOfClass:[NSArray class]]) {
                    NSArray *eventArray = (NSArray *) self.event;
                    for (int i = 0; i < eventArray.count; i++) {
                        NSDictionary *value = [eventArray gx_objectAtIndex:i];
                        NSDictionary *resultEvent = [GXDataParser parseData:value withSource:data];
                        [resultEventArray gx_addObject:resultEvent];
                    }
                }
                [self bindEvents:resultEventArray];
            }
            //绑定埋点
            if (self.track) {
                NSDictionary *trackEvent = [GXDataParser parseData:self.track withSource:data];
                [self bindTrack:trackEvent];
            }
            //绑定动画
            if (self.animation) {
                NSDictionary *resultAnimation = [GXDataParser parseData:self.animation withSource:data];
                [self bindAnimation:resultAnimation];
            }
            
        }
            break;
        case GXBindTypeCalculate:{
            //数据绑定
            if (self.data || self.virtualExtend) {
                NSMutableDictionary *resultData = [GXDataParser parseData:self.data withSource:data];
                resultData = [self mergeExtendWithResult:resultData];
                [self calculateWithData:resultData];
            }
        
        }
            break;
            
        default:
            break;
    }
    
}

#pragma mark - 合并Extend

- (NSMutableDictionary *)mergeExtendWithResult:(NSMutableDictionary *)resultData {
    NSMutableDictionary *dataDict = nil;
    NSDictionary *extendDict = self.virtualExtend;
    if (extendDict) {
        if (!resultData){
            //为空的话直接赋值
            dataDict = [NSMutableDictionary dictionary];
            [dataDict gx_setObject:extendDict forKey:@"extend"];
            
        } else if (resultData && [GXUtils isMutableDictionary:resultData]){
            //赋值
            dataDict = resultData;
            //获取extend
            NSMutableDictionary *tmpExtend = [resultData gx_mutableDictionaryForKey:@"extend"];
            if (tmpExtend) {
                [tmpExtend addEntriesFromDictionary:extendDict];
            } else {
                tmpExtend = (NSMutableDictionary *)extendDict;
            }
            
            //设置extend
            [dataDict gx_setObject:tmpExtend forKey:@"extend"];
        }
    } else {
        dataDict = resultData;
    }
    
    return dataDict;
}


#pragma mark - 绑定（数据，事件，动画）

//节点创建视图
- (UIView *)creatView{
    return nil;
}

//节点渲染视图
- (void)renderView:(UIView *)view{
    
}

//是否需要绑定
- (BOOL)shouldBind{
    BOOL should = self.data || self.virtualData || self.event || self.track || self.animation;
    return should;
}

//数据绑定
- (void)bindData:(NSDictionary *)data{
    
}

//动画绑定
- (void)bindAnimation:(NSDictionary *)animation{
    
}

//事件绑定
- (void)bindEvents:(NSArray *)events{
    UIView *view = self.associatedView;
    
    // 绑定event
    for (int i = 0; i < events.count; i++) {
        NSDictionary *eventInfo = [events gx_objectAtIndex:i];
        GXEventType eventType = [GXEvent eventTypeWithEventInfo:eventInfo];
        
        // 获取event
        GXEvent *gxEvent = [view gx_eventWithType:eventType];
        if (nil == gxEvent) {
            gxEvent = [[GXEvent alloc] init];
            gxEvent.templateItem = self.templateItem;
            gxEvent.eventType = eventType;
            gxEvent.nodeId = self.nodeId;
            gxEvent.view = view;
            //绑定到view
            [view gx_setEvent:gxEvent withType:eventType];
        }
        // 更新数据
        [gxEvent setupEventInfo:eventInfo];
        
        // 绑定事件
        [self bindEvent:gxEvent];
    }
}

- (void)bindEvent:(GXEvent *)event{
    //设置userInterface
    UIView *view = self.associatedView;
    if (!view.userInteractionEnabled) {
        view.userInteractionEnabled = YES;
    }
    
    //添加手势类型
    if (event.eventType == GXEventTypeLongPress) {
        //长按手势
        if (!_longPress) {
            _longPress = [[UILongPressGestureRecognizer alloc] initWithTarget:view action:@selector(gx_handleGesture:)];
            [view addGestureRecognizer:_longPress];
        }
    } else {
        //tap手势
        if (!_tap) {
            _tap = [[UITapGestureRecognizer alloc] initWithTarget:view action:@selector(gx_handleGesture:)];
            [view addGestureRecognizer:_tap];
        }
    }
    
}

//埋点绑定
-(void)bindTrack:(NSDictionary *)trackInfo{
    UIView *view = self.associatedView;
    //获取track信息
    GXTrack *track = view.gxTrack;
    if (nil == track) {
        track = [[GXTrack alloc] init];
        track.templateId = self.templateItem.templateId;
        track.nodeId = self.nodeId;
        track.view = view;
        //赋值
        view.gxTrack = track;
    }
    //更新数据
    [track setupTrackInfo:trackInfo];
    //埋点分发
    id <GXTrackProtocal> trackListener = self.templateContext.templateData.trackListener;
    if (trackListener && [trackListener respondsToSelector:@selector(gx_onTrackEvent:)] ) {
        [trackListener gx_onTrackEvent:track];
    }
}

//处理扩展
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate{
    
}


#pragma mark - 计算相关

//通过属性计算
- (void)calculateWithData:(NSDictionary *)data{
    
}

- (void)updateTextNodes:(NSPointerArray *)textNodes{
    //子类文字节点实现
}


#pragma mark - 属性相关

//读取json中的属性
- (void)configureViewInfo:(NSDictionary *)viewInfo;{
    self.viewJson = viewInfo;
    
}

//读取css中的属性
- (void)configureStyleInfo:(NSDictionary *)styleInfo{
    self.styleJson = styleInfo;
}




#pragma mark - 埋点

- (void)manualClickTrackEvent{
    GXTrack *track = self.associatedView.gxTrack;
    if (track) {
        id <GXTrackProtocal> trackListener = self.templateContext.templateData.trackListener;
        if (trackListener && [trackListener respondsToSelector:@selector(gx_onManualClickTrackEvent:)] ) {
            [trackListener gx_onManualClickTrackEvent:track];
        }
    }
}

- (void)manualExposureTrackEvent{
    GXTrack *track = self.associatedView.gxTrack;
    if (track) {
        id <GXTrackProtocal> trackListener = self.templateContext.templateData.trackListener;
        if (trackListener && [trackListener respondsToSelector:@selector(gx_onManualExposureTrackEvent:)] ) {
            [trackListener gx_onManualExposureTrackEvent:track];
        }
    }
}


@end

@implementation GXNode(JS)

- (void)onReady{
    //调用js事件
    if (_jsComponent) {
        //区分首次
        if (!_isReady) {
            _isReady = YES;
            [_jsComponent onReady];
        } else {
            [_jsComponent onReuse];
        }
    }
}

- (void)onShow{
    //处理JS
    if (_jsComponent) {
        [_jsComponent onShow];
    }
    
    //分发子节点(透传子节点，处理曝光)
    self.isAppear = YES;
    [self manualExposureTrackEvent];
    for (GXNode *node in self.children) {
        [node onShow];
    }
}

- (void)onHide{
    //处理JS
    if (_jsComponent) {
        [_jsComponent onHide];
    }
    
    //分发子节点
    self.isAppear = NO;
    for (GXNode *node in self.children) {
        [node onHide];
    }
}

- (void)onDestroy{
    if (_jsComponent) {
        [_jsComponent onDestroy];
    }
}

@end
