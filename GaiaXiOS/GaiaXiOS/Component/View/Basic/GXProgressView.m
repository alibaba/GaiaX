//
//  GXProgressView.m
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

#import "GXProgressView.h"
#import "UIColor+GX.h"

@interface GXProgressView ()

@property (nonatomic, strong) UIView *progressView;

@end


@implementation GXProgressView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupUI];
    }
    return self;
}

- (void)setupUI{
    CGFloat height = self.frame.size.height;
    self.layer.cornerRadius = height / 2.f;
    self.layer.masksToBounds = YES;
    
    _progressView = [[UIView alloc] init];
    _progressView.frame = CGRectMake(0, 0, 0, height);
    _progressView.layer.cornerRadius = height / 2.f;
    _progressView.clipsToBounds = YES;
    _progressView.backgroundColor = [UIColor gx_colorWithString:@"#0079fd"];
    [self addSubview:_progressView];
}

//设置背景色
- (void)setTrailColor:(UIColor *)trailColor{
    self.backgroundColor = trailColor;
}

//设置进度颜色
- (void)setStrokeColor:(UIColor *)strokeColor{
    _progressView.backgroundColor = strokeColor;
}

//更新进度条
- (void)updateProgress:(CGFloat)progress animated:(BOOL)animated{
    //更新布局
    CGFloat height = self.frame.size.height;
    CGFloat width = self.frame.size.width;
    self.layer.cornerRadius = height / 2.f;

    //更新进度
    progress = MIN(1.f, fabs(progress));
    CGFloat pWidth = width * progress;
    
    CGRect frame = self.progressView.frame;
    frame.size.height = height;
    self.progressView.frame = frame;
    self.progressView.layer.cornerRadius = height * 0.5;

    frame.size.width = pWidth;
    if (animated) {
        [UIView animateWithDuration:0.25 animations:^{
            self.progressView.frame = frame;
        }];
    } else {
        self.progressView.frame = frame;
    }
    
}


@end
