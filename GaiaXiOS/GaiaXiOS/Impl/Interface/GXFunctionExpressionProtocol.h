//
//  GXExpressionFunctionProtocol.h
//  GaiaXiOS
//
//  Created by biezhihua on 2023/4/18.
//

NS_ASSUME_NONNULL_BEGIN

@protocol GXFunctionExpressionProtocol <NSObject>

- (id _Nullable)execute: (NSString *)funName params:(NSArray *)funParams;

@end

NS_ASSUME_NONNULL_END
