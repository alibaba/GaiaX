//
//  GXAnalyzeiOSTests.m
//  GXAnalyzeiOSTests
//
//  Created by 张敬成 on 2022/3/10.
//

#import <XCTest/XCTest.h>
#import <GXAnalyzeiOS/GXAnalyzeiOS.h>

@interface GXAnalyzeiOSTests : XCTestCase

@property (nonatomic, strong) GXNativeAnalyze *analyze;

@end

@implementation GXAnalyzeiOSTests

- (void)setUp {
    // Put setup code here. This method is called before the invocation of each test method in the class.
    self.analyze = [[GXNativeAnalyze alloc] init];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    self.analyze = nil;
}

- (void)testExample {
    // This is an example of a functional test case.
    // Use XCTAssert and related functions to verify your tests produce the correct results.
    
    //数字相等
    id result1 = [self.analyze valueWithExpression:@"3 == 3" Source:@{}];
    XCTAssertEqual([result1 boolValue], true);

    //数字比较
    id result2 = [self.analyze valueWithExpression:@"2 > 3" Source:@{}];
    XCTAssertEqual([result2 boolValue], false);

    //bool测试
    id result3 = [self.analyze valueWithExpression:@"true == false" Source:@{}];
    XCTAssertEqual([result3 boolValue], false);
    
    //取值表达式逻辑
    id result4 = [self.analyze valueWithExpression:@"$a.b == 3" Source:@{@"a": @{@"b": @3}}];
    XCTAssertEqual([result4 boolValue], true);
    
    id result5 = [self.analyze valueWithExpression:@"$a.b == $c" Source:@{@"a": @{@"b": @3}, @"c": @3}];
    XCTAssertEqual([result5 boolValue], true);
    
    id result6 = [self.analyze valueWithExpression:@"$$" Source:@{@"a":@{@"b": @3}, @"c":@3}];
    XCTAssertNotNil(result6);
    
    id result7 = [self.analyze valueWithExpression:@"size($d)" Source:@{@"a": @{@"b": @3}, @"c": @3, @"d": @[@"1", @2, @3]}];
    XCTAssertEqual([result7 intValue], 3);
    
    //取值表达式逻辑
    id result8 = [self.analyze valueWithExpression:@"$a.b + 3 == 6" Source:@{@"a": @{@"b": @3}}];
    XCTAssertEqual([result8 boolValue], true);
    
    //取值表达式逻辑 & 特殊
//    id result9 = [self.analyze valueWithExpression:@"$[0]" Source:@[@"1", @"2", @"3"]];
//    XCTAssertEqual([result9 boolValue], true);
    
    //取值表达式逻辑  
    id result10 = [self.analyze valueWithExpression:@"true ?: 1" Source:@{@"a": @{@"b": @3}}];
    XCTAssertEqual([result10 boolValue], true);
    
    id result11 = [self.analyze valueWithExpression:@"$a.c == null" Source:@{@"a": @{@"b": @3}}];
    NSLog(@"%@", result11);
}

- (void)testPerformanceExample {
    // This is an example of a performance test case.
    [self measureBlock:^{
        // Put the code you want to measure the time of here.
        for (int i = 0; i < 20; i ++) {
            //取值表达式逻辑
            id result8 = [self.analyze valueWithExpression:@"$a.b + 3 == 6" Source:@{@"a": @{@"b": @3}}];
            NSLog(@"%@", result8);
        }
    }];
}

@end
