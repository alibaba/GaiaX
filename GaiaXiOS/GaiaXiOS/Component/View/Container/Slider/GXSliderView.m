//
//  GXSliderView.m
//  GaiaXCore
//
//  Created by zhangjc on 2024/10/23.
//  Copyright © 2024 zhangjc. All rights reserved.
//

#import "GXSliderView.h"
#import "UIView+GX.h"
#import "GXNode.h"

@implementation GXSliderView
- (void)onAppear{
    if (self.gxNode) {
        [self.gxNode onShow];
    }
}

- (void)onDisappear{
    if (self.gxNode) {
        [self.gxNode onHide];
    }
}

@end


@implementation GXSliderViewCell

@end
