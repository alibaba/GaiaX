//
//  GXLottieAniamtionProtocal.h
//  GaiaXiOS
//
//  Created by 张敬成 on 2022/1/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol GXLottieAniamtionProtocal <NSObject>

@required

/*
 * The business needs to inherit the CompatibleAnimationView implementation，and initialize related properties (compatibleBackgroundBehavior)
 * CompatibleAnimationView *animationView = [[CompatibleAnimationView alloc] initWithFrame:self.associatedView.bounds];
 * animationView.compatibleBackgroundBehavior = 2;
 */

/// lottie animation play
/// @param animationInfo animation parameters {"isLocal": "true", "lottieUrl": "xxx", "loopCount": "-1"}
/// @param completion animation callback
- (void)gx_playAnimation:(NSDictionary *)animationInfo completion:(void (^ __nullable)(BOOL finished))completion;

/// is animation Playing
- (BOOL)gx_isAnimationPlaying;

// stop animation
- (void)gx_stop;


@end

NS_ASSUME_NONNULL_END
