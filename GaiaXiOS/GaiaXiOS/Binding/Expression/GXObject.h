//
//  GaiaXValue.h
//  TestExpresssion
//
//  Created by 张敬成 on 2022/3/11.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXObject : NSObject

@end

@interface GXBool : GXObject

+ (NSNumber *)getResultByValue:(BOOL)value;

@end

@interface GXFloat : GXObject

+ (NSNumber *)getResultByValue:(CGFloat)value;

@end

@interface GXLong : GXObject

+ (NSNumber *)getResultByValue:(int64_t)value;

@end


@interface GXStr : GXObject

+ (NSNumber *)getResultByValue:(NSString *)value;

@end

@interface GXArray : GXObject

+ (NSArray *)getResultByValue:(void *)value;

@end

@interface GXMap : GXObject

+ (NSDictionary *)getResultByValue:(void *)value;

@end


NS_ASSUME_NONNULL_END
