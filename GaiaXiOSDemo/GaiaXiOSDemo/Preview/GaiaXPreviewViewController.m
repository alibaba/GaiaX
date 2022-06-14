//
//  GaiaXPreviewViewController.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
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
#import "GaiaXSocketClient.h"
#import <GaiaXiOS/GaiaXiOS.h>
#import "GaiaXHelper.h"

#define kScreenWidth [[UIScreen mainScreen] bounds].size.width
#define kScreenHeight [[UIScreen mainScreen] bounds].size.height
#define kStatusBarHeight [[UIApplication sharedApplication] statusBarFrame].size.height

@interface GaiaXPreviewViewController ()<GaiaXSocketClientDelegate, UIGestureRecognizerDelegate> {
    UIView *_rootView;
    NSDictionary *_mockData;
    GXTemplateItem *_templateItem;
    GXTemplateData *_templateData;
    GaiaXPreviewTemplateSource *_previewTemplateSource;
}

@property (nonatomic) NSUInteger requestId;
@property (nonatomic, strong) NSString *url;
@property (nonatomic, strong) NSString *templateId;
@property (nonatomic, strong) GaiaXSocketClient *client;

@property (nonatomic, strong) UIView *preview;
@property (nonatomic, strong) UIButton *statusBtn;

@property (nonatomic, strong) NSDictionary *templateInfo;
@property (nonatomic, strong) NSMutableDictionary *dependenciesTemplateInfo;

@end

@implementation GaiaXPreviewViewController

- (instancetype)initWithUrl:(NSString *)url {
    if (self = [super init]) {
        //decodeUrl
        NSString *resultString  = [GaiaXHelper URLDecodedString:url];
        //解析url的参数
        NSDictionary *paramters =  [GaiaXHelper parameterFromUrl:resultString];
        //参数赋值
        _url = [paramters gx_stringForKey:@"url"];
        _templateId = [paramters gx_stringForKey:@"id"];
        //初始化source
        [self registerPreviewSource];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"GaiaX预览";
    [self.view addSubview:self.preview];
    self.view.backgroundColor = [UIColor whiteColor];
    //设置button按钮
    [self setupRightButtonItem];
    //创建socket链接
    [self setupScoket];
}

- (void)setupScoket{
    if (_url && [_url isKindOfClass:[NSString class]] && _url.length) {
        // 1.创建socket对象
        _client = [[GaiaXSocketClient alloc] initWithURL:[NSURL URLWithString:_url] delegate:self];
        // 2.开启socket连接
        [_client connectServer];
    }
}

- (void)registerPreviewSource{
    _previewTemplateSource = [[GaiaXPreviewTemplateSource alloc] init];
    [TheGXRegisterCenter registerTemplateSource:_previewTemplateSource];
}

- (void)setupRightButtonItem{
    _statusBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    _statusBtn.frame = CGRectMake(0, 0, 80, 44);
    _statusBtn.titleLabel.textAlignment = NSTextAlignmentRight;
    _statusBtn.titleLabel.font = [UIFont systemFontOfSize:18.f];
    UIBarButtonItem *rightButtonItem = [[UIBarButtonItem alloc] initWithCustomView:_statusBtn];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
}

- (UIView *)preview {
    if (_preview == nil) {
        CGRect frame = CGRectMake(0, kStatusBarHeight + 44.f, kScreenWidth, kScreenHeight- (kStatusBarHeight + 44.f));
        _preview = [[UIView alloc] initWithFrame:frame];
    }
    return _preview;
}

- (NSMutableDictionary *)dependenciesTemplateInfo{
    if (_dependenciesTemplateInfo == nil) {
        _dependenciesTemplateInfo = [NSMutableDictionary dictionary];
    }
    return _dependenciesTemplateInfo;
}

- (void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    //关闭socket链接
    [_client webSocketClose];
    //清除预览数据源
    [TheGXRegisterCenter unregisterTemplateSource:_previewTemplateSource];
    //清除相关依赖模板
    _dependenciesTemplateInfo = nil;
    _previewTemplateSource = nil;
}


#pragma mark -GaiaXSocketClientDelegate

//socket初始化
- (void)gaiaXSocketClientDidInitialize:(GaiaXSocketClient *)client {
    //发起请求
    GaiaXSocketRequest *request = [GaiaXSocketRequest requestWithRequestId:@(666) Method:@"template/getTemplateData" parameters:@{@"id":_templateId}];
    [_client sendRequestToServer:request];
}

//socket链接成功
- (void)gaiaXSocketClientDidConnect:(GaiaXSocketClient *)client {
    //更新状态
    [_statusBtn setTitle:@"已连接" forState:UIControlStateNormal];
    [_statusBtn setTitleColor:[UIColor greenColor] forState:UIControlStateNormal];

    // 向服务器发送初始化消息
    if (_requestId == 0) {
        _requestId++;
        GaiaXSocketRequest *request = [GaiaXSocketRequest requestWithRequestId:@(_requestId) Method:@"initialize" parameters:nil];
        [_client sendRequestToServer:request];
    }
}

//socket连接失败
- (void)gaiaXSocketClient:(GaiaXSocketClient *)client didFailWithError:(NSError *)error {
    //更新状态
    [_statusBtn setTitle:@"连接失败" forState:UIControlStateNormal];
    [_statusBtn setTitleColor:[UIColor redColor] forState:UIControlStateNormal];
}

//获取到模板信息
- (void)gaiaXSocketClient:(GaiaXSocketClient *)client didReceiveTemplateInfo:(NSDictionary *)templateInfo {
    CGFloat width = kScreenWidth;
    CGFloat measureWidth = width;
    CGFloat measureHeight = NAN;
    
    //赋值模板信息
    _templateInfo = templateInfo;
    _templateId = [templateInfo gx_stringForKey:@"templateId"];
    
    //移除原有视图
    [self.preview.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    
    //读取package信息 & 嵌套关系
    NSString *indexJsonStr = [templateInfo gx_stringForKey:@"index.json"];
    NSDictionary *indexJsonDict = [NSDictionary gx_dictionaryFromJSONString:indexJsonStr];
    if ([GaiaXHelper isValidDictionary:indexJsonDict]) {
        //处理模板数据
        [self handleTemlateInfo:templateInfo];
        //读取package, 处理size和嵌套关系
        NSDictionary *package = [indexJsonDict gx_dictionaryForKey:@"package"];
        if (package) {
            NSDictionary *constraintSizeDict = [package gx_dictionaryForKey:@"constraint-size"];
            if (constraintSizeDict) {
                measureWidth = [constraintSizeDict gx_floatForKey:@"width"] ?: measureWidth;
                measureHeight = [constraintSizeDict gx_floatForKey:@"height"] ?: measureHeight;
            }
            // 获取嵌套模板
            [self getNestedTemplate:package];
        }
    }
    
    //入参item
    _templateItem = [[GXTemplateItem alloc] init];
    _templateItem.templateId = _templateId;
    
    //入参data
    _templateData = [[GXTemplateData alloc] init];
    _templateData.data = _mockData;
    
    //创建视图
    CGSize measureSize = CGSizeMake(measureWidth, measureHeight);
    _rootView = [TheGXTemplateEngine creatViewByTemplateItem:_templateItem measureSize:measureSize];
    [self.preview addSubview:_rootView];
    
    //绑定数据
    [TheGXTemplateEngine bindData:_templateData onView:_rootView];
    
    //更新位置
    CGFloat tmpWidth = _rootView.frame.size.width;
    CGFloat x = tmpWidth > 0 ? (width - tmpWidth) / 2.f : 0;
    CGRect frame = _rootView.frame;
    frame.origin.y = 50;
    frame.origin.x = x;
    _rootView.frame = frame;
}

//获取到嵌套模板
- (void)gaiaXSocketClient:(GaiaXSocketClient *)client didReceiveNestedTemplateInfo:(NSDictionary *)templateInfo {
    // 获取嵌套模板
    NSDictionary *package = nil;
    NSString *templateId = [templateInfo gx_stringForKey:@"templateId"];
    NSString *indexJsonStr = [templateInfo gx_stringForKey:@"index.json"];
    NSDictionary *indexJsonDict = [NSDictionary gx_dictionaryFromJSONString:indexJsonStr];
    
    //读取package信息 & 嵌套关系
    if ([GaiaXHelper isValidDictionary:indexJsonDict]) {
        //处理模板数据
        [self handleTemlateInfo:templateInfo];
        //获取依赖关系
        package = [indexJsonDict gx_dictionaryForKey:@"package"];
        if (package) {
            [self getNestedTemplate:package];
        }
    }
    
    //加入依赖关系，用于请求嵌套模板
    [self.dependenciesTemplateInfo gx_setObject:templateInfo forKey:templateId];
    
    // 嵌套模板获取完毕，刷新预览View
    [self gaiaXSocketClient:_client didReceiveTemplateInfo:_templateInfo];
}

//断开连接
- (void)gaiaXSocketClientDidDisconnect:(GaiaXSocketClient *)client {
    //清理缓存模板信息 & 预览模板
    [_previewTemplateSource clearPreviewTemplates];
    _dependenciesTemplateInfo = nil;
    _previewTemplateSource = nil;
}

#pragma mark - 嵌套模板

- (void)getNestedTemplate:(NSDictionary *)package{
    NSDictionary *dependencies = package[@"dependencies"];
    if (dependencies && [dependencies isKindOfClass:[NSDictionary class]] && dependencies.count > 0) {
        [dependencies enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
            [self sendNestedTemplateToStudio:key];
        }];
    }
}

- (void)sendNestedTemplateToStudio:(NSString *)templateId{
    if (![self.dependenciesTemplateInfo.allKeys containsObject:templateId]) {
        GaiaXSocketRequest *request = [GaiaXSocketRequest requestWithRequestId:@(888)
                                                                        Method:@"template/getTemplateData"
                                                                    parameters:@{@"id":templateId}];
        [_client sendRequestToServer:request];
    }
}



#pragma mark - 添加模板缓存，并处理mock数据

- (void)handleTemlateInfo:(NSDictionary *)templateInfo{
    if ([GXUtils isValidDictionary:templateInfo]) {
        //获取模板id
        NSString *templateId = [templateInfo gx_stringForKey:@"templateId"];
        if (templateId.length <= 0) {
            return;
        }
        
        //是否为跟模板
        BOOL isRoot = [templateId isEqualToString:_templateId];
        
        //获取模板文件
        NSString *indexCssStr = [templateInfo gx_stringForKey:@"index.css"];
        NSString *indexJsonStr = [templateInfo gx_stringForKey:@"index.json"];
        NSString *indexMockStr = [templateInfo gx_stringForKey:@"index.mock"];
        
        NSMutableDictionary *result = [NSMutableDictionary dictionary];
        // CSS样式解析
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
                    [nodes addObject:@{@"data":@"'mock'"}];
                }
                [dbDict gx_setObject:@{@"value":nodes} forKey:templateId];
                
            } else {
                //普通模板
                NSDictionary *dependencies = [[indexJsonDict gx_dictionaryForKey:@"package"] gx_dictionaryForKey:@"dependencies"];
                [dependencies enumerateKeysAndObjectsUsingBlock:^(id  _Nonnull key, id  _Nonnull obj, BOOL * _Nonnull stop) {
                    [dbDict gx_setObject:@{@"data":@"'mock'"} forKey:key];
                }];
            }
            
            if (indexDataDict) {
                result[kGXComDef_KW_DB] = @{@"data": dbDict};
            }
            
            //获取数据源
            if (isRoot) {
                _mockData = @{@"data":@"'mock'"};
            }
            
        }
        
        //添加到预览缓存池
        [_previewTemplateSource addPreviewTemplate:result forTemplateId:templateId];
    }
    
}

@end
