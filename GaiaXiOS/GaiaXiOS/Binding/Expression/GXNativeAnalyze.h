//
//  GXNativeAnalyze.h
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXNativeAnalyze : NSObject

+ (instancetype)sharedInstance;

/// 通过表达式获取值
/// @param expression 表达式内容（number，string类型）
/// @param source 数据源
- (id)valueWithExpression:(id)expression Source:(NSDictionary *)source;

@end

NS_ASSUME_NONNULL_END
