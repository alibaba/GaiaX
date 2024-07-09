//
//  GXTemplateTest.h
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

@interface GXTemplateTest : XCTestCase

@end

@implementation GXTemplateTest

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
    [TheGXRegisterCenter registerTemplateServiceWithBizId:@"Test" templateBundle:@"GaiaXiOSTests.bundle"];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
}

// test register business
- (void)testRegisterBiz {
    BOOL result = [TheGXRegisterCenter registerTemplateServiceWithBizId:@"Test" templateBundle:@"GaiaXiOSTests.bundle"];
    XCTAssertTrue(result, @"业务注册失败");
}

// test load template info
- (void)testLoadNormalTemplateInfo {
    //读取明文模板信息
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_normal";
    item.bizId = @"Test";
    item.isLocal = YES;
    
    NSDictionary *templateInfo = [TheGXTemplateEngine loadTemplateContentWithTemplateItem:item];
    XCTAssertNotNil(templateInfo, @"明文模板信息异常");
    
    NSDictionary *cssInfo = [templateInfo objectForKey:@"sy"];
    XCTAssertTrue((cssInfo && [cssInfo isKindOfClass:[NSDictionary class]]), @"index.css文件异常");

    NSDictionary *jsonInfo = [templateInfo objectForKey:@"vh"];
    XCTAssertTrue((jsonInfo && [jsonInfo isKindOfClass:[NSDictionary class]]), @"index.json文件异常");
    
    NSDictionary *databindingInfo = [templateInfo objectForKey:@"db"];
    XCTAssertTrue((databindingInfo && [databindingInfo isKindOfClass:[NSDictionary class]]), @"index,databinding文件异常");
    
//    NSDictionary *jsInfo = [templateInfo objectForKey:@"js"];
//    XCTAssertTrue((jsInfo && [jsInfo isKindOfClass:[NSString class]]), @"index,js文件异常");
}


// test load binary template info
- (void)testLoadBinaryTemplateInfo {
    GXTemplateItem *item = [[GXTemplateItem alloc] init];
    item.templateId = @"template_normal_binary";
    item.bizId = @"Test";
    item.isLocal = YES;
    
    NSDictionary *templateInfo = [TheGXTemplateEngine loadTemplateContentWithTemplateItem:item];
    XCTAssertNotNil(templateInfo, @"二进制模板信息异常");
}


//- (void)testPerformanceExample {
//    // This is an example of a performance test case.
//    [self measureBlock:^{
//        // Put the code you want to measure the time of here.
//    }];
//}

@end
