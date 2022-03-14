//
//  GXTemplateSourceInterface.h
//  GaiaXiOS
//
//  Created by 张敬成 on 2022/1/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/*
 * 模板数据源
 */
@protocol GXITemplateSource <NSObject>

@required
//add template
- (void)addTemplate:(NSDictionary *)aTemplate forTemplateId:(NSString *)templateId;

//get template
- (NSDictionary *)getTemplateWithTemplateId:(NSString *)templateId;

//remoview all templates
- (void)clearAllTemplates;

@end

NS_ASSUME_NONNULL_END
