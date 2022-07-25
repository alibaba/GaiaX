//
//  GXBizServiceProtocol.h
//  GaiaXiOS
//
//  Created by 张敬成 on 2022/1/18.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol GXBizServiceProtocol <NSObject>

#pragma mark - iconfont

//load iconfont
+ (void)loadIconFont;


#pragma mark - DesignToken

//处理image
+ (UIImage * _Nullable)imageFromToken:(NSString *)token;

//处理font-faimly
+ (NSString * _Nullable)fontFamilyFromToken:(NSString *)token;

//get color by DesignToken
+ (UIColor *)colorFromDesignToken:(NSString *)token;

//get font by DesignToken
+ (UIFont *)fontFromDesignToken:(NSString *)token;
+ (UIFont *)fontFromDesignToken:(NSString *)token fontWeight:(UIFontWeight)fontWeight;

//get corner by DesignToken
+ (CGFloat)cornerFromDesignToken:(NSString *)token;

//get dim by DesignToken
+ (CGFloat)dimFromDesignToken:(NSString *)token;


#pragma mark - ResponsiveLayout

//get responsive screen size
+ (CGSize)screenSize;

//Get the width of the responsive view
+ (CGFloat)valueForRule:(NSString *)rule withContainerWidth:(CGFloat)containerWidth gap:(CGFloat)gap margin:(CGFloat)margin;

@end

NS_ASSUME_NONNULL_END
