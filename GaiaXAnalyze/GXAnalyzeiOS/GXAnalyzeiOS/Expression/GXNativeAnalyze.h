//
//  GXNativeAnalyze.h
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXNativeAnalyze : NSObject

- (id)valueWithExpression:(NSString *)expression Source:(NSDictionary *)source;

@end

NS_ASSUME_NONNULL_END
