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
@protocol GXITemplatePreviewSource <NSObject>

@required
//add template
- (void)addPreviewTemplate:(NSDictionary *)aTemplate forTemplateId:(NSString *)templateId;

//get template
- (NSDictionary *)getPreviewTemplateWithTemplateId:(NSString *)templateId;

//remoview all templates
- (void)clearPreviewTemplates;

@end


@protocol GXITemplatePushSource <NSObject>

@required
//add template
- (void)addPushTemplate:(NSDictionary *)aTemplate forTemplateId:(NSString *)templateId;

//get template
- (NSDictionary *)getPushTemplateWithTemplateId:(NSString *)templateId;

//remoview all templates
- (void)clearPushTemplates;

@end




NS_ASSUME_NONNULL_END
