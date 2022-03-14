//
//  TemplateListCell.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
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

#import "TemplateListCell.h"
#import <GaiaXiOS/GaiaXiOS.h>

@interface TemplateListCell ()

@property (nonatomic, strong) UILabel *label;
@property (nonatomic, strong) UIView *templateView;

@end

@implementation TemplateListCell

- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier{
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        _label = [[UILabel alloc] initWithFrame:CGRectMake(10, 0, [UIScreen mainScreen].bounds.size.width - 20, 40)];
        _label.textColor = [UIColor blackColor];
        _label.font = [UIFont systemFontOfSize:15];
        [self.contentView addSubview:_label];
    }
    return self;
}

- (void)setupData:(NSDictionary *)itemInfo{
    GXTemplateItem *item = itemInfo[@"item"];
    GXTemplateData *data = itemInfo[@"data"];
    
    _label.text = item.templateId;
    
    //创建view
    if (_templateView) {
        [_templateView removeFromSuperview];
        _templateView = nil;
    }
    
    _templateView = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:CGSizeMake([UIScreen mainScreen].bounds.size.width, NAN)];
    CGRect frame = _templateView.frame;
    if ([item.templateId isEqualToString:@"gx-vertical-item"]){
        frame.origin.x = 15;
    }
    
    frame.origin.y = CGRectGetMaxY(_label.frame);
    _templateView.frame = frame;
    
    [self.contentView addSubview:_templateView];
    //绑定数据
    [TheGXTemplateEngine bindData:data onView:_templateView];
    
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
