//
//  GXEgnineTest.h
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

#import <XCTest/XCTest.h>
#import <GaiaXiOS/GaiaXiOS.h>

@interface GXEgnineTest : XCTestCase <GXDataProtocal>

@property (nonatomic, copy) NSString *bizId;

@end

@implementation GXEgnineTest

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
    self.bizId = @"Test";
    [TheGXRegisterCenter registerTemplateServiceWithBizId:self.bizId templateBundle:@"GaiaXiOSTests.bundle"];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [TheGXRegisterCenter unRegisterTemplateServiceWithBizId:self.bizId];
    self.bizId = nil;
}


#pragma mark - Base

- (void)test_template_normal {
    // 测试视图创建
    
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_normal";
    item.bizId = self.bizId;
        
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 2);

    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == (1080 - 100));
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
    
    XCTAssertTrue(view.subviews[1].subviews.count == 2);
    XCTAssertTrue([view.subviews[1].subviews[0] isKindOfClass:NSClassFromString(@"GXText")]);
}


- (void)test_template_normal_binary {
    // 测试视图创建
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_normal_binary";
    item.bizId = self.bizId;
        
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}


#pragma mark - 测试模板嵌套
- (void)test_template_nest {
    // 测试databinding动态属性
    
    CGSize size = CGSizeMake(1080, NAN);
        
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+100));
    XCTAssertTrue(view.subviews.count == 2);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
    
    XCTAssertTrue(view.subviews[0].subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].subviews[0].frame.size.height == 100);
    
    XCTAssertTrue(view.subviews[0].subviews[1].frame.size.width == (1080-100));
    XCTAssertTrue(view.subviews[0].subviews[1].frame.size.height == 100);

    XCTAssertTrue(view.subviews[0].subviews[1].subviews.count == 2);
    XCTAssertTrue(view.subviews[0].subviews[1].subviews[0].frame.size.width == (1080-100));
    XCTAssertTrue(view.subviews[0].subviews[1].subviews[0].frame.size.height == 20);
    XCTAssertTrue(view.subviews[0].subviews[1].subviews[1].frame.size.width == (1080-100));
    XCTAssertTrue(view.subviews[0].subviews[1].subviews[1].frame.size.height == 20);
}

- (void)test_template_nest_child_databinding_update_property_height {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_child_databinding_update_property_height";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"height":@"200px"};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}

- (void)test_template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_width {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_width";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"height":@"200px", @"width":@"300px" };
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}

- (void)test_template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_height {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_height";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"height":@"200px", @"data":@{ @"height":@"300px" } };
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}

- (void)test_template_nest_databinding_override_width_height {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_databinding_override_width_height";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"":@""};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+150));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 150);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}



- (void)test_template_nest_databinding_override_both {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_databinding_override_both";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"":@""};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}

- (void)test_template_nest_databinding_override_only_child {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_databinding_override_only_child";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"":@""};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}

- (void)test_template_nest_databinding_override_both_value {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_databinding_override_both_value";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"height":@"200px", @"data":@{ @"width":@"300px" } };
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}

- (void)test_template_nest_scroll_nodes_self {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_scroll_nodes_self";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}


- (void)test_template_nest_scroll_self_nodes {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_scroll_self_nodes";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}


- (void)test_template_nest_css_override_width_height {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_css_override_width_height";
    item.bizId = self.bizId;
        
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"height":@"200px", @"data":@{ @"width":@"300px" } };
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.subviews.count == 2);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+150));
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 150);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}


// 测试节点拍平
- (void)test_template_merge_empty_nodes {
    
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_merge_empty_nodes";
    item.bizId = self.bizId;
        
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 3);
}

// 测试动态Height
- (void)test_template_nest_databinding_update_property_only_child {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_databinding_update_property_only_child";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"height": @"200px"};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    XCTAssertTrue(view.subviews.count == 2);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 300);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}


// 测试动态size2
- (void)template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_height {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_nest_parent_databinding_update_property_height_and_child_databinding_update_property_height";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"height": @"200px", @"": @{@"height": @"300px"}};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == (100+200));
    XCTAssertTrue(view.subviews.count == 2);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[0].frame.size.height == 200);
    
    XCTAssertTrue(view.subviews[1].frame.size.width == 1080);
    XCTAssertTrue(view.subviews[1].frame.size.height == 100);
}


// 测试动态ratio1，以宽定高
- (void)test_template_aspect_ratio_width_to_height {

    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_aspect_ratio_width_to_height";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 1);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
}
    

// 测试动态ratio2， 以高定宽
- (void)test_template_aspect_ratio_height_to_width {

    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_aspect_ratio_height_to_width";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 1);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
}
    

// 测试maxSize，width
- (void)test_template_max_size_width {
    
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_max_size_width";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 1);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
}

// 测试maxSize，height
- (void)test_template_max_size_height {

    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_max_size_height";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 1);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
}

// 测试minSize，width
- (void)test_template_min_size_width {

    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_min_size_width";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 1);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
}

// 测试minSize，height
- (void)test_template_min_size_height {
    
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_min_size_height";
    item.bizId = self.bizId;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
    XCTAssertTrue(view.subviews.count == 1);
    
    XCTAssertTrue(view.subviews[0].frame.size.width == 100);
    XCTAssertTrue(view.subviews[0].frame.size.height == 100);
}


#pragma mark - Text

- (void)test_template_text_fitcontent_case_1_lines_1_width_100pt_height_100px {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_fitcontent_case_1_lines_1_width_100pt_height_100px";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    NSString *string = @"HelloWorld";
    data.data = @{@"text": string};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    XCTAssertNotNil(view);

    CGFloat textWidth = view.subviews[0].frame.size.width;
    CGFloat nWidth = ceil([string sizeWithAttributes:@{NSFontAttributeName : [UIFont systemFontOfSize:20]}].width);
    XCTAssertTrue(textWidth == nWidth);
}

- (void)test_template_text_fitcontent_case_1_lines_1_width_null_height_100px {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_fitcontent_case_1_lines_1_width_null_height_100px";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    NSString *string = @"HelloWorld";
    data.data = @{@"text": string};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    XCTAssertNotNil(view);

    CGFloat textWidth = view.subviews[0].frame.size.width;
    CGFloat nWidth = ceil([string sizeWithAttributes:@{NSFontAttributeName : [UIFont systemFontOfSize:20]}].width);
    XCTAssertTrue(textWidth == nWidth);
}

- (void)test_template_text_fitcontent_case_1_lines_1_width_null_height_null {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_fitcontent_case_1_lines_1_width_null_height_null";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    NSString *string = @"HelloWorld";
    data.data = @{@"text": string};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    XCTAssertNotNil(view);

    CGFloat textWidth = view.subviews[0].frame.size.width;
    CGFloat textHeight = view.subviews[0].frame.size.height;
    CGSize nSize = [string sizeWithAttributes:@{NSFontAttributeName : [UIFont systemFontOfSize:20]}];
    CGFloat nWidth = ceil(nSize.width);
    CGFloat nHeight = ceil(nSize.height);
    XCTAssertTrue(textWidth == nWidth);
    XCTAssertTrue(textHeight == nHeight);
}

- (void)test_template_text_fitcontent_case_2_lines_0_width_100pt_height_100px {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_fitcontent_case_2_lines_0_width_100pt_height_100px";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    NSString *string = @"HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld";
    data.data = @{@"text": string};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    XCTAssertNotNil(view);

    CGFloat textHeight = view.subviews[0].frame.size.height;
    CGFloat nHeight = ceil([string boundingRectWithSize:CGSizeMake(1080, CGFLOAT_MAX)
                                                options:NSStringDrawingUsesLineFragmentOrigin
                                             attributes:@{NSFontAttributeName: [UIFont systemFontOfSize:20]}
                                                context:nil].size.height);
    XCTAssertTrue(textHeight == nHeight);
}

- (void)test_template_text_fitcontent_case_2_lines_0_width_100px_height_100px {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_fitcontent_case_2_lines_0_width_100px_height_100px";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    NSString *string = @"安静安静安静阿基啊奥术大师大所大所多奥术大师大所多奥术大师大所";
    data.data = @{@"text": string};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);

    CGFloat textHeight = view.subviews[0].frame.size.height;
    CGFloat nHeight = ceil([string boundingRectWithSize:CGSizeMake(1080, CGFLOAT_MAX)
                                                options:NSStringDrawingUsesLineFragmentOrigin
                                             attributes:@{NSFontAttributeName: [UIFont systemFontOfSize:20]}
                                                context:nil].size.height);
    XCTAssertTrue(textHeight == nHeight);
}


- (void)test_template_text_fitcontent_case_2_lines_0_width_null_height_100px {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_fitcontent_case_2_lines_0_width_null_height_100px";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    NSString *string = @"HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld";
    data.data = @{@"text": string};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
   
    XCTAssertThrows( [TheGXTemplateEngine bindData:data onView:view]);
}



//测试文本自定义
- (void)test_template_text_text_process {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_text_text_process";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"text":@"安静安静安静阿基啊"};
    data.dataListener = self;
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
}

#pragma mark - GXDataProtocal

- (id)gx_onTextProcess:(GXTextData *)data{
    NSString *tmp = @"爱啥啥宽度阿圣诞贺卡坚实的框架安徽省科技的哈就开始对科技爱仕达";
    return tmp;
}


#pragma mark - Image

//测试图片的动态size
- (void)test_template_image_resize {
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_image_resize";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"image":@"https://r1.ykimg.com/053500005B9B8DB4ADB185F820065AD7"};

    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:CGSizeMake(100, NAN)];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);
}

//测试图片的动态size
- (void)test_template_image_cut {
    
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_image_cut";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"image":@"https://r1.ykimg.com/053500005B9B8DB4ADB185F820065AD7"};

    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);
}


#pragma mark - SCroll

//测试单个坑位
- (void)test_template_scroll_multi_type_item_one {
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_multi_type_item_one";
    item.bizId = self.bizId;
    
    //绑定数据
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"nodes":@[@{@"title":@"aaaa"}, @{@"title":@"aaaa"}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}


//测试多个个坑位
- (void)test_template_scroll_multi_type_item_two {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_multi_type_item_two";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"nodes":@[@{@"title":@"aaaa"}, @{@"title":@"aaaa"}]};

    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertNotNil(view);
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}

//自适应坑位宽度
- (void)test_template_scroll_width_auto {
    CGSize size = CGSizeMake(1080, NAN);
//
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_width_auto";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{@"nodes":@[@{@"title":@"aaaa"}, @{@"title":@"aaaa"}]};

    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}


- (void)test_template_scroll_vertical_height_auto{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_vertical_height_auto";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{},@{},@{},@{},@{}  ]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 0);
}

- (void)test_template_scroll_item_spacing{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_item_spacing";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}

- (void)test_template_scroll_edge_item_spacing{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_edge_item_spacing";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}

- (void)test_template_scroll_modify_item{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_modify_item";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 200);
}

- (void)test_template_scroll_height_auto{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_height_auto";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}

- (void)test_template_scroll_height_percent_100_limit_height{
    CGSize size = CGSizeMake(1080, 100);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_height_percent_100";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}

- (void)test_template_scroll_height_percent_100{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_height_percent_100";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 0);
}

- (void)test_template_scroll_height_fixed{
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_scroll_height_fixed";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UICollectionView *view = (UICollectionView *)[TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 200);
}


#pragma mark - Grid

- (void)test_template_grid_height_100pt_scroll_enable_true{
    CGSize size = CGSizeMake(1080, 200);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_height_100pt_scroll_enable_true";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 200);
}

- (void)test_template_grid_height_100px_scroll_enable_true{
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_height_100px_scroll_enable_true";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}


- (void)test_template_grid_height_flow{
    CGSize size = CGSizeMake(1080, 200);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_height_flow";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 200);
}

- (void)test_template_grid_normal{
    CGSize size = CGSizeMake(1080, NAN);
    
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_normal";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100*3 + 9*2);
}

- (void)test_template_grid_extend_column {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_extend_column";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100*2 + 9);
}

- (void)test_template_grid_height_100px {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_height_100px";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100);
}

- (void)test_template_grid_height_auto {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_height_auto";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100*3+9*2);
}


- (void)test_template_grid_item_spacing_raw_spacing {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_item_spacing_raw_spacing";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100*3+20*2);
}

- (void)test_template_grid_item_spacing_raw_spacing_edge {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_item_spacing_raw_spacing_edge";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100*3+20*2+30*2);
}

- (void)test_template_grid_item_spacing_raw_spacing_edge_column_3 {
    CGSize size = CGSizeMake(1080, NAN);

    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_grid_item_spacing_raw_spacing_edge_column_3";
    item.bizId = self.bizId;
    
    GXTemplateData *data = [[GXTemplateData alloc] init];
    data.data = @{ @"nodes": @[ @{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""},@{@"":@""}]};
    
    UIView *view = [TheGXTemplateEngine creatViewByTemplateItem:item measureSize:size];
    [TheGXTemplateEngine bindData:data onView:view];
    
    XCTAssertTrue(view.frame.size.width == 1080);
    XCTAssertTrue(view.frame.size.height == 100*2+20+30*2);
}




@end
