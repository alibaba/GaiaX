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

//get template
- (NSDictionary *)getTemplateInfoWithTemplateItem:(GXTemplateItem *)templateItem;

@optional


@end

NS_ASSUME_NONNULL_END
