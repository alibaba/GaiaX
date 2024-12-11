//
//  GXSliderView.h
//  GaiaXCore
//
//  Created by zhangjc on 2024/10/23.
//  Copyright © 2024 zhangjc. All rights reserved.
//

#import "GXPagerView.h"
#import "GXRootViewProtocal.h"

@class GXRootView;

NS_ASSUME_NONNULL_BEGIN

@interface GXSliderView : GXPagerView<GXRootViewProtocal>

@end


@interface GXSliderViewCell : UICollectionViewCell

@property (nonatomic, strong) GXRootView *rootView;

@end

NS_ASSUME_NONNULL_END
