//
//  GXTemplateSource.m
//  GaiaXiOS
//
//  Created by 张敬成 on 2022/5/25.
//

#import "GXTemplateSource.h"
#import "GXTemplatePathHelper.h"
#import "GXTemplateLoader.h"
#import "GXTemplateItem.h"
#import "NSDictionary+GX.h"
#import "GXUtils.h"

@interface GXTemplateSource ()
//业务模板注册关系
@property (nonatomic, strong) NSMutableDictionary *bizRegisterMap;

@end

@implementation GXTemplateSource

- (NSMutableDictionary *)bizRegisterMap{
    if (!_bizRegisterMap) {
        _bizRegisterMap = [NSMutableDictionary dictionary];
    }
    return _bizRegisterMap;
}


/// 注册业务模板
- (BOOL)registerTemplateServiceWithBizId:(NSString *)bizId
                          templateBundle:(NSString *)templateBundle{
    //获取前置条件
    if (bizId == nil || templateBundle == nil) {
        return NO;
    }
    
    //获取UserDefaults
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    //获取模板路径
    NSString *currentBundlePath = [GXTemplatePathHelper loadBizBundlePathWithBundleName:templateBundle];
    
    //获取bundle的name
    NSString *currentBundle = [defaults stringForKey:bizId];
    if (currentBundle.length > 0 && [currentBundle isEqualToString:templateBundle]) {
        [self.bizRegisterMap gx_setObject:currentBundlePath forKey:bizId];
        //已经注册，则直接返回
        return YES;
    }
    
    //重新写入
    [self.bizRegisterMap gx_setObject:currentBundlePath forKey:bizId];
    [defaults setObject:templateBundle forKey:bizId];
    
    return YES;
}

/// 注销业务离散化服务
- (BOOL)unRegisterTemplateServiceWithBizId:(NSString *)bizId{
    if ([GXUtils isValidString:bizId]) {
        //移除本地存储
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        [defaults removeObjectForKey:bizId];
        //移除缓存
        [self.bizRegisterMap removeObjectForKey:bizId];
        return YES;
    } else {
        return NO;
    }
}

/// 通过bizId来获取已经注册过的bundle
- (NSString *)loadTemplateBundlePathForBizId:(NSString *)bizId{
    NSString *bizBundlePath = [self.bizRegisterMap gx_stringForKey:bizId];
    if (!bizBundlePath.length) {
        NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
        //获取bundleName
        NSString *bizBundleName = [defaults stringForKey:bizId];
        //获取bundlePath
        bizBundlePath = [GXTemplatePathHelper loadBizBundlePathWithBundleName:bizBundleName];
        //写入缓存
        [self.bizRegisterMap gx_setObject:bizBundlePath forKey:bizId];
    }
    return bizBundlePath;
}


#pragma mark - GXTemplateSourceProtocal

// priority
- (NSInteger)priority{
    return 50;
}

//get template
- (NSDictionary *)getTemplateInfoWithTemplateItem:(GXTemplateItem *)templateItem {
    //基础信息
    NSString *bizId = templateItem.bizId;
    NSString *templateId = templateItem.templateId;
    //读取模板信息
    NSDictionary *resultDict = [[GXTemplateLoader defaultLoader] loadTemplateInfoWithBizId:bizId templateId:templateId];
    return resultDict;
}

@end
