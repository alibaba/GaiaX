//  Copyright (c) 2023, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.


#import "GaiaXPreviewViewController.h"
#import "GaiaXPreviewTemplateSource.h"
#import <GaiaXSocket/GaiaXSocket.h>
#import <GaiaXiOS/GaiaXiOS.h>
#import <GaiaXiOS/UIColor+GX.h>
#import "GaiaXHelper.h"

#define kScreenWidth [UIScreen mainScreen].bounds.size.width
#define kScreenHeight [UIScreen mainScreen].bounds.size.height
#define kStatusBarHeight [[UIApplication sharedApplication] statusBarFrame].size.height

@interface GaiaXPreviewViewController ()<GaiaXSocketProtocol, UIGestureRecognizerDelegate>{
    UIView *_rootView;
    NSDictionary *_mockData;
    GXTemplateItem *_templateItem;
    GXTemplateData *_templateData;
    GaiaXPreviewTemplateSource *_previewTemplateSource;
}

@property (nonatomic, strong) UIView *containerView;
@property (nonatomic, strong) UIView *preview;
@property (nonatomic) NSUInteger requestId;
@property (nonatomic, weak) GaiaXSocketClient *client;
@property (nonatomic, strong) NSDictionary *templateInfo;
@property (nonatomic, assign) CGSize artboardSize;
@property (nonatomic, strong) UIView *artboardView;

@end

@implementation GaiaXPreviewViewController
@synthesize isActive;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.navigationController.interactivePopGestureRecognizer.delegate = self;
    self.navigationController.navigationBarHidden = YES;
    self.view.backgroundColor = [UIColor whiteColor];
    
    [self setTitle:@"GaiaX预览"];
    [self.view addSubview:self.containerView];
    [self.containerView addSubview:self.preview];
    [self.preview addSubview:self.artboardView];
    self.artboardSize = self.artboardView.frame.size;
    _previewTemplateSource = [[GaiaXPreviewTemplateSource alloc] init];
    [TheGXRegisterCenter registerTemplateSource:_previewTemplateSource];
}


- (UIView *)preview{
    if (_preview == nil) {
        _preview = [[UIView alloc] initWithFrame:self.containerView.bounds];
        _preview.clipsToBounds = NO;
        _preview.layer.borderWidth = 1;
        _preview.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.05];
        _preview.layer.borderColor = [UIColor gx_colorWithHexString:@"#97979750"].CGColor;
        _preview.layer.shadowColor = [[UIColor colorWithRed:0 green:0 blue:0 alpha:1] CGColor];
        _preview.layer.shadowRadius = 4;
        _preview.clipsToBounds = NO;
        _preview.layer.cornerRadius = 20;
    }
    return _preview;
}

- (UIView *)artboardView {
    if (_artboardView == nil) {
        _artboardView = [[UIView alloc] initWithFrame:CGRectMake(0, 20, self.preview.bounds.size.width, self.preview.bounds.size.height-2*20)];
        _artboardView.clipsToBounds = NO;
        _artboardView.backgroundColor = [UIColor whiteColor];
    }
    return _artboardView;
}


- (UIView *)containerView{
    if (_containerView == nil) {
        CGRect frame = CGRectMake(0, kStatusBarHeight + 44.f + 10, kScreenWidth, kScreenHeight - (kStatusBarHeight + 44.f) - 2*10);
        if (@available(iOS 11.0, *)) {
             UIWindow *window = UIApplication.sharedApplication.keyWindow;
             frame.size.height -= window.safeAreaInsets.bottom;
        }
        _containerView.backgroundColor = [UIColor whiteColor];
        _containerView.clipsToBounds = NO;
        _containerView = [[UIView alloc] initWithFrame:frame];
    }
    return _containerView;
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [[GaiaXSocketManager sharedInstance] registerListener:self];
    self.isActive = YES;
    _client = [GaiaXSocketManager sharedInstance].socketClient;
    GaiaXSocketModel *request = [GaiaXSocketModel requestWithMethod:@"template/get" params:nil];
    [_client sendRequest:request callback:^(GaiaXSocketModel * _Nonnull response) {
        NSDictionary *result = response.result;
        NSDictionary *data = [result gx_dictionaryForKey:@"templateData"];
        if (data) {
            //获取templateId
            [self didReceiveTemplateId:[result gx_stringForKey:@"templateId"] data:data subTemplates:[result gx_arrayForKey:@"subTemplates"]];
        }
    }];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [[GaiaXSocketManager sharedInstance] unRegisterListener:self];
    
    [TheGXRegisterCenter unregisterTemplateSource:_previewTemplateSource];
    _previewTemplateSource = nil;
    self.isActive = NO;
}

#pragma mark - socket协议

- (NSString *)gxMessageId{
    return @"GAIAX_AUTO_PREVIEW";
}

- (void)gxSocketClientDidConnect:(GaiaXSocketClient *)client{

    //获取client
    self.client = client;
}

- (void)gxSocketClientDidDisConnect:(GaiaXSocketClient *)client{

}

- (void)gxSocketClient:(GaiaXSocketClient *)client didFailWithError:(NSError *)error{
    //连接出错
    
}

- (void)gxSocketClient:(GaiaXSocketClient *)client didReceiveMessage:(GaiaXSocketModel *)message{
    NSDictionary *result = message.params;
    if ([message.method isEqualToString:@"template/didChangedNotification"]) {
        NSDictionary *data = [result gx_dictionaryForKey:@"templateData"];
        //获取templateId
        if (data != nil) {
            [self didReceiveTemplateId:[result gx_stringForKey:@"templateId"] data:data subTemplates:[result gx_arrayForKey:@"subTemplates"]];
        }
    }
}

//获取到模板信息
- (void)didReceiveTemplateId:(NSString *)templateId data:(NSDictionary *)templateInfo subTemplates:(NSArray *)subTemplates{
        
    CGFloat measureWidth = _artboardSize.width;
    CGFloat measureHeight = _artboardSize.height;
    
    //赋值模板信息
    _templateInfo = templateInfo;
    _templateId = templateId;
    
    //移除原有视图
    [self.artboardView.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    NSMutableArray *updatedTemplates = [NSMutableArray array];
    [self handleTemlateId:templateId data:templateInfo updatedTemplates:updatedTemplates];
    
    NSString *indexJsonStr = [templateInfo gx_stringForKey:@"index.json"];
    NSDictionary *indexJsonDict = [NSDictionary gx_dictionaryFromJSONString:indexJsonStr];
    if ([GaiaXHelper isValidDictionary:indexJsonDict]) {
        //读取package, 处理size和嵌套关系
        NSDictionary *package = [indexJsonDict gx_dictionaryForKey:@"package"];
        if (package) {
            NSDictionary *constraintSizeDict = [package gx_dictionaryForKey:@"constraint-size"];
            if (constraintSizeDict) {
                measureWidth = [constraintSizeDict gx_floatForKey:@"width"] ?: measureWidth;
                measureHeight = [constraintSizeDict gx_floatForKey:@"height"] ?: measureHeight;
            }
        }
    }
    
    if (subTemplates) {
        for (NSDictionary *subTemplate in subTemplates) {
            [self handleTemlateId:subTemplate[@"templateId"] data:subTemplate[@"templateData"] updatedTemplates:updatedTemplates];
        }
    }
    
    //入参item
    _templateItem = [[GXTemplateItem alloc] init];
    _templateItem.templateId = _templateId;
    
    //入参data
    _templateData = [[GXTemplateData alloc] init];
    _templateData.data = _mockData;
    
    //创建视图
    measureWidth = MIN(measureWidth, _artboardSize.width);
    measureHeight = MIN(measureHeight, _artboardSize.height);
    CGSize measureSize = CGSizeMake(measureWidth, measureHeight);
    _rootView = [TheGXTemplateEngine creatViewByTemplateItem:_templateItem measureSize:measureSize];
    
    //绑定数据
    [TheGXTemplateEngine bindData:_templateData onView:_rootView];
    measureWidth = _rootView.bounds.size.width;
    measureHeight = _rootView.bounds.size.height;
    //添加视图
    [self.artboardView addSubview:_rootView];
    
    CGRect frame = self.preview.frame;
    frame.size.width = measureWidth;
    frame.size.height = measureHeight+2*20;
    frame.origin.x = (_artboardSize.width-measureWidth)/2;
    frame.origin.y = (MAX(0, _artboardSize.height-measureHeight-2*20)) /5;
    self.preview.frame = frame;
    self.artboardView.frame  = CGRectMake(0, 20, frame.size.width, frame.size.height-2*20);
    if (updatedTemplates.count > 0) {
        [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_TEMPLATES_UPDATED_LIST" object:nil userInfo:@{@"ids": updatedTemplates}];
    }
}


#pragma mark - 请求嵌套模板



#pragma mark - 添加模板缓存，并处理mock数据

- (void)handleTemlateId:(NSString *)templateId data:(NSDictionary *)templateInfo updatedTemplates:(NSMutableArray *)updatedTemplates{
    if ([GaiaXHelper isValidDictionary:templateInfo]) {
        //获取模板id
        if (templateId.length <= 0) {
            return;
        }
        
        //是否为跟模板
        BOOL isRoot = [templateId isEqualToString:_templateId];
        
        //获取模板文件
        NSString *indexJSStr = [templateInfo gx_stringForKey:@"index.js"];
        NSString *indexCssStr = [templateInfo gx_stringForKey:@"index.css"];
        NSString *indexJsonStr = [templateInfo gx_stringForKey:@"index.json"];
        NSString *indexMockStr = [templateInfo gx_stringForKey:@"index.mock"];
        
        NSMutableDictionary *result = [NSMutableDictionary dictionary];
        //JS解析
        result[kGXComDef_KW_JS] = indexJSStr;
        //CSS样式解析
        result[kGXComDef_KW_SY] = [GXUtils parserStyleString:indexCssStr];
        //视图层级解析
        NSDictionary *indexJsonDict = [NSDictionary gx_dictionaryFromJSONString:indexJsonStr];
        result[kGXComDef_KW_VH] = indexJsonDict;
        
        //获取事件绑定关系
        if (indexMockStr.length > 0) {
            //如有index.mock，则使用index.mock做数据源，配置index.databinding使用
            NSString *indexDatabindingStr = [templateInfo gx_stringForKey:@"index.databinding"];
            result[kGXComDef_KW_DB] = [NSDictionary gx_dictionaryFromJSONString:indexDatabindingStr];
            //获取数据源
            if (isRoot) {
                _mockData = [NSDictionary gx_dictionaryFromJSONString:indexMockStr];
            }
            
        } else {
            //如没有index.mock，则使用index.data替换为index.databinding内容
            NSString *indexDataStr = [templateInfo gx_stringForKey:@"index.data"];
            NSDictionary *indexDataDict = [NSDictionary gx_dictionaryFromJSONString:indexDataStr];
            NSMutableDictionary *dbDict = [NSMutableDictionary dictionaryWithDictionary:indexDataDict];
            
            //遍历处理子模板
            NSString *subType = [indexJsonDict gx_stringForKey:@"sub-type"];
            if (subType.length > 0) {
                //容器模板
                NSMutableArray *nodes = [NSMutableArray array];
                for (int i = 0; i < 10; i++) {
                    [nodes addObject:@{@"data":@"mock"}];
                }
                [dbDict gx_setObject:@{@"value":nodes} forKey:templateId];
                
            } else {
                //普通模板
                NSDictionary *dependencies = [[indexJsonDict gx_dictionaryForKey:@"package"] gx_dictionaryForKey:@"dependencies"];
                [dependencies enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
                    [dbDict gx_setObject:@{@"data":@"mock"} forKey:key];
                }];
            }
            
            if (indexDataDict) {
                result[kGXComDef_KW_DB] = @{@"data": dbDict};
            }
            
            //获取数据源
            if (isRoot) {
                _mockData = @{@"data":@"mock"};
            }
            
        }
        
        //添加到预览缓存池
        [_previewTemplateSource addPreviewTemplate:result forTemplateId:templateId];
        [updatedTemplates addObject:templateId];
    }
    
}

#pragma mark - 侧滑

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldBeRequiredToFailByGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer{
    return [gestureRecognizer isKindOfClass:UIScreenEdgePanGestureRecognizer.class];
}



@end
