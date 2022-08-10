//
//  GXScrollView.h
//  GaiaXiOS
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import <UIKit/UIKit.h>
#import "GXRootView.h"
#import "GXRootViewProtocal.h"

NS_ASSUME_NONNULL_BEGIN

@interface GXScrollView : UICollectionView<GXRootViewProtocal>

@end



@interface GXScrollViewCell : UICollectionViewCell

@property (nonatomic, strong) GXRootView *rootView;

@end



@protocol GXFlowLayoutDelegate <UICollectionViewDelegateFlowLayout>
@end

@interface GXFlowLayout : UICollectionViewFlowLayout

@property (nonatomic, strong) NSString *gravity;

@property (nonatomic, assign) CGFloat containerHeight;

@property (nonatomic, weak) id <GXFlowLayoutDelegate> delegate;

@end


NS_ASSUME_NONNULL_END
