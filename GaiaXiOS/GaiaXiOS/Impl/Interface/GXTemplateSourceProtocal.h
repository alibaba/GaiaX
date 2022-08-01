//
//  GXTemplateSourceProtocal.h
//  GaiaXiOS
//
//  Created by 张敬成 on 2022/5/25.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class GXTemplateItem;
@protocol GXTemplateSourceProtocal <NSObject>

@required

// priority
- (NSInteger)priority;

/// get template
/// @param templateItem 模板相关信息
/// 注意：
/// 1. 如需缓存逻辑，需要在协议方法内自行添加
/// 2. 配合 GXUtils (Template) 中的方法，可以解析文件夹和二进制模板
- (NSDictionary *)getTemplateInfoWithTemplateItem:(GXTemplateItem *)templateItem;

@optional


@end

NS_ASSUME_NONNULL_END
