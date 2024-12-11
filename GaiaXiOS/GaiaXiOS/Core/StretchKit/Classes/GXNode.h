//
//  GXNode.h
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

#import <Foundation/Foundation.h>
#import "GXTemplateItem.h"
#import "GXLayout.h"
#import "GXStyle.h"
#import "GXTemplateData.h"
#import <GaiaXJS/GaiaXJS.h>

@class GXEvent;
@class GXTemplateContext;

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, GXBindType) {
    GXBindTypeData = 0,
    GXBindTypeCalculate,
};

@interface GXNode : NSObject

//子节点
@property(nonatomic, strong) NSMutableArray *children;
//对应的view
@property(nonatomic, weak) UIView *associatedView;
//样式Style
@property(nonatomic, strong) GXStyle *style;
//对应的rust指针
@property(nonatomic, assign) void *rustptr;
//父节点
@property(nonatomic, weak) GXNode *parent;
//dirty标记
@property(nonatomic, assign) BOOL dirty;
//根据计算之后拍平的frame
@property(nonatomic) CGRect frame;


//初始化方法 + children
- (instancetype)initWithStyle:(GXStyle *)style children:(NSArray * _Nullable)children;

//替换子节点
- (void)replaceChild:(GXNode *)child atIndex:(NSUInteger)index;

//根据nodeId获取节点树上的某个节点
- (GXNode *)queryNodeByNodeId:(NSString *)nodeId;

//添加子节点
- (void)setChildren:(NSMutableArray *)children;
- (void)addChild:(GXNode *)child;

//移除节点
- (void)removeChildAtIndex:(NSUInteger)index;
- (void)removeChild:(GXNode *)child;

//计算布局 -> 返回布局信息
-(GXLayout *)computeLayout:(StretchSize)size;

//重新设置样式
- (void)setStyle:(GXStyle *)style;

//标记dirty
- (void)markDirty;
- (BOOL)dirty;

//获取根节点
- (GXNode *)rootNode;
//是否是根节点
- (BOOL)isRootNode;

@end


@interface GXNode(Template)

//节点id
@property(nonatomic, strong) NSString *nodeId;
//节点类型
@property(nonatomic, strong) NSString *type;
@property(nonatomic, strong) NSString *subType;
//是否为模板类型（根模板/嵌套子模板）
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

@end


@interface GXNode(Render)

//databinding中的内容
@property (nonatomic, strong) id data;
@property (nonatomic, strong) id event;
@property (nonatomic, strong) id track;
@property (nonatomic, strong) NSDictionary *animation;

//嵌套模板中的数据
@property (nonatomic, strong) id virtualData;
@property (nonatomic, strong, nullable) NSDictionary *virtualExtend;


//是否需要拍平（仅对view有效）
@property (nonatomic, assign) BOOL isFlat;
//文本是否自适应（仅对text有效）
@property (nonatomic, assign) BOOL fitContent;

//当前的index(scroll/grid）,仅在expression中使用
@property (nonatomic, assign) NSInteger index;

//扁平化节点键值对, 只有根节点会赋值
@property (nonatomic, strong) NSMapTable *flatNodes;

//递归创建view
- (UIView *)applyView;
//递归更新layout属性
- (void)applyLayout:(GXLayout *)layout;
//更新数据(databinding + source)
- (void)applyData:(NSDictionary *)data type:(GXBindType)type;

//创建视图
- (UIView *)creatView;
//渲染视图，属性设置
- (void)renderView:(UIView *)view;

//是否需要绑定数据
- (BOOL)shouldBind; 
//绑定事件
- (void)bindEvent:(GXEvent *)event;
//数据绑定
- (void)bindData:(NSDictionary *)data;
//动画绑定
- (void)bindAnimation:(NSDictionary *)animation;
//处理扩展
- (void)handleExtend:(NSDictionary *)extend isCalculate:(BOOL)isCalculate;

//通过属性计算
- (void)calculateWithData:(NSDictionary *)data;
- (void)updateTextNodes:(NSPointerArray *)textNodes;

//读取json中的属性
- (void)configureViewInfo:(NSDictionary *)viewInfo;
//读取css中的属性
- (void)configureStyleInfo:(NSDictionary *)styleInfo;


//是否正在显示
@property (nonatomic, assign) BOOL isAppear;

//埋点处理
- (void)manualClickTrackEvent;
- (void)manualExposureTrackEvent;

@end


@interface GXNode(JS)

//原始数据
@property (nonatomic, weak) GXTemplateData *orignalData;
//jsComponent
@property(nonatomic, strong) GaiaXJSComponent *jsComponent;

//生命周期
- (void)onReady;

- (void)onShow;

- (void)onHide;

- (void)onDestroy;

@end


NS_ASSUME_NONNULL_END
