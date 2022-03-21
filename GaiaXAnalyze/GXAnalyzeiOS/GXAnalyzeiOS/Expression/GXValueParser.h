//
//  GaiaValueParser.h
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXValueParser : NSObject

/// 创建表达式解析
/// @param string 表达式内容
+ (GXValueParser *)parserWithExpString:(NSString *)string;

/// 解析内容
/// @param object 数据源
- (id)valueWithObject:(id)object;

@end

NS_ASSUME_NONNULL_END
