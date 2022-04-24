//
//  GaiaFunctionParser.h
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/16.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXFunction : NSObject

+ (id)function:(NSString *)func params:(NSArray *)params;

@end

NS_ASSUME_NONNULL_END
