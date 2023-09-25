//  Copyright (c) 2023, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.


#import "GaiaXSocketToastView.h"

@interface GaiaXSocketToastView ()

@property(nonatomic, strong) UIVisualEffectView *blurView;
@property(nonatomic, strong) UIView *seperatorView;
@property(nonatomic, strong) UILabel *titleLabel;
@property(nonatomic, strong) UILabel *timeLabel;
@property(nonatomic, strong) NSMutableArray *contents;
@property(nonatomic, assign) CGFloat maxWidth;

@end

@implementation GaiaXSocketToastView

- (void)showToastWithTitle:(NSString *)title messages:(NSArray *)messages {
    self.tag = 10201;
    self.clipsToBounds = YES;
    self.layer.cornerRadius = 10;
    CGFloat maxWidth = 0;
    
    self.contents = [NSMutableArray array];
    UIBlurEffect *blurEffect =
    [UIBlurEffect effectWithStyle:UIBlurEffectStyleLight];
    self.blurView = [[UIVisualEffectView alloc] initWithEffect:blurEffect];
    self.blurView.backgroundColor = [UIColor colorWithRed:41 / 255.0
                                                    green:142 / 255.0
                                                     blue:70 / 255.0
                                                    alpha:1];
    [self addSubview:self.blurView];
    
    self.titleLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.titleLabel.font = [UIFont systemFontOfSize:14 weight:UIFontWeightMedium];
    self.titleLabel.textAlignment = NSTextAlignmentLeft;
    self.titleLabel.textColor = [UIColor whiteColor];
    self.titleLabel.text = title;
    NSDictionary *fontWithAttributes =
    @{NSFontAttributeName : self.titleLabel.font};
    CGSize size = [self.titleLabel.text sizeWithAttributes:fontWithAttributes];
    maxWidth = MAX(size.width, maxWidth);
    [self addSubview:self.titleLabel];
    
    self.timeLabel = [[UILabel alloc] initWithFrame:CGRectZero];
    self.timeLabel.font = [UIFont systemFontOfSize:12 weight:UIFontWeightMedium];
    self.timeLabel.textAlignment = NSTextAlignmentLeft;
    self.timeLabel.textColor = [[UIColor whiteColor] colorWithAlphaComponent:0.6];
    NSDate *now = [NSDate date];
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy/MM/dd HH:mm:ss"];
    self.timeLabel.text =
    [NSString stringWithFormat:@"%@", [formatter stringFromDate:now]];
    NSDictionary *fontWithAttributes2 =
    @{NSFontAttributeName : self.timeLabel.font};
    CGSize size2 = [self.timeLabel.text sizeWithAttributes:fontWithAttributes2];
    maxWidth = MAX(size2.width, maxWidth);
    [self addSubview:self.timeLabel];
    
    for (NSInteger i = 0; i < messages.count; i++) {
        if (i == 0) {
            self.seperatorView = [[UIView alloc] initWithFrame:CGRectZero];
            self.seperatorView.backgroundColor =
            [[UIColor whiteColor] colorWithAlphaComponent:0.5];
            [self addSubview:self.seperatorView];
        }
        UILabel *label = [[UILabel alloc] initWithFrame:CGRectZero];
        label.font = [UIFont systemFontOfSize:13 weight:UIFontWeightMedium];
        label.textAlignment = NSTextAlignmentLeft;
        label.textColor = [UIColor whiteColor];
        label.text = messages[i];
        [self.contents addObject:label];
        [self addSubview:label];
        NSDictionary *fontWithAttributes = @{NSFontAttributeName : label.font};
        CGSize size = [label.text sizeWithAttributes:fontWithAttributes];
        maxWidth = MAX(size.width, maxWidth);
    }
    self.maxWidth = maxWidth;
    [self computeLayout];
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    if ([window viewWithTag:10201] != nil) {
        [[window viewWithTag:10201] removeFromSuperview];
    }
    [window addSubview:self];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)),
                   dispatch_get_main_queue(), ^{
        [self removeFromSuperview];
    });
}

- (void)computeLayout {
    CGFloat leftRightMargin = 12;
    CGFloat width = self.maxWidth + 2 * leftRightMargin;
    CGFloat height = 0;
    CGFloat originY = 12;
    CGFloat gap = 3;
    self.titleLabel.frame =
    CGRectMake(leftRightMargin, originY, self.maxWidth, 15);
    originY += 15 + gap;
    self.timeLabel.frame =
    CGRectMake(leftRightMargin, originY, self.maxWidth, 13);
    originY += 13 + gap;
    self.seperatorView.frame =
    CGRectMake(leftRightMargin, originY, self.maxWidth, 1);
    originY += 1 + gap;
    for (NSInteger i = 0; i < self.contents.count; i++) {
        UILabel *label = self.contents[i];
        label.frame = CGRectMake(12, originY, self.maxWidth, 15);
        originY += 15 + gap;
    }
    height = originY + 4;
    
    UIWindow *window = [[UIApplication sharedApplication] keyWindow];
    self.frame =
    CGRectMake((window.bounds.size.width - width) / 2,
               window.bounds.size.height - height - 100, width, height);
    self.blurView.frame = self.bounds;
}

@end
