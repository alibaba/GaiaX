//
//  GXAnalyzeBridge.h
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXAnalyzeBridge : NSObject

+ (instancetype)sharedInstance;

- (long)getFunctionValue:(NSString *)funName paramPointers:(long *)paramPointers paramsSize:(int)paramsSize;

- (long)getSourceValue:(NSString *)valuePath source:(id)source;

- (void)throwError:(NSString *)message;

@end

NS_ASSUME_NONNULL_END
