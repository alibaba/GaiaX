//
//  GXContext.h
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXContext : NSObject

/// 通过地址获取对应值
/// @param adress 内存地址
+ (id)getValueWithAdress:(long)adress;


/// 通过对象获取GXValue的地址
/// @param value 对象
+ (long)getAdressWithValue:(id)value;


@end

NS_ASSUME_NONNULL_END
