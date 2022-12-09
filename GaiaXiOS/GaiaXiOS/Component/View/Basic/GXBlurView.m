//
//  GXBlurView.m
//  GaiaXiOS
//
//  Created by zhangjc on 2022/12/9.
//

#import "GXBlurView.h"

@implementation GXBlurView

- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    if (@available(iOS 13.0, *)) {
        // trait发生了改变
        if ([self.traitCollection hasDifferentColorAppearanceComparedToTraitCollection:previousTraitCollection]) {
            if (self.traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                self.effect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleDark];
//                self.backgroundColor = [UIColor colorWithWhite:0 alpha:0.4];
            } else {
                self.effect = [UIBlurEffect effectWithStyle:UIBlurEffectStyleExtraLight];
//                self.backgroundColor = [UIColor colorWithWhite:1 alpha:0.4];
            }
        }
    }
}

@end
