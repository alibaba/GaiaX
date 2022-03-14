//
//  GXExpression.m
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

#import "GXExpression.h"
#import <UIKit/UIKit.h>
#import "GXBizHelper.h"
#import "GXUtils.h"

@implementation GXExpression

+ (GXExpression *)expressionWithString:(NSString *)string{
    //判断rule
    GXExpression *expression = nil;
    if ([GXUtils isValidString:string]) {
        //处理头尾空格
        string = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        BOOL isPlus = [string rangeOfString:@" + "].location != NSNotFound;
        //基础判断
        if ([string hasPrefix:@"${"] && !isPlus) {
            //获取取值表达式
            expression = [GXValueExpression expressionWithString:string];
            
        } else if ([string hasPrefix:@"@{"]) {
            //获取三元表达式
            expression = [GXConditionExpression expressionWithString:string];
            
        } else if ([string hasPrefix:@"eval("]) {
            //获取运算表达式
            expression = [GXEvalExpression expressionWithString:string];
            
        } else if ([string hasPrefix:@"env("]){
            //获取环境变量
            expression = [GXEnvExpression expressionWithString:string];
            
        } else if ([string hasPrefix:@"size("]){
            //计算array/dictionary/string的count
            expression = [GXSizeExpression expressionWithString:string];
            
        } else  if (isPlus){
            //获取拼接表达式
            expression = [GXPlusExpression expressionWithString:string];
            
        }
        
    }
    
    //常量表达式兜底
    if (!expression) {
        expression = [GXConstExpression expressionWithString:string];
    }
    expression.expression = string;
    
    //返回结果
    return expression;
    
}

- (id)valueWithObject:(id)object{
    
    return nil;
}

- (void)setCurrentIndex:(NSInteger)currentIndex{
    _currentIndex = currentIndex;
}

@end

#pragma mark - 常量表达式

@interface GXConstExpression ()

@property (nonatomic, strong) NSString *value;

@end


@implementation GXConstExpression

+ (nullable GXExpression *)expressionWithString:(nonnull NSString *)rule{
    GXConstExpression *expression = [[GXConstExpression alloc] init];
    expression.value = rule;
    return expression;
}

- (nullable id)valueWithObject:(nullable id)object{
    //默认返回值
    id result = nil;
    if ([self.value hasPrefix:@"'"] && [self.value hasSuffix:@"'"]){
        //获取''中间的内容
        result = [self.value substringWithRange:NSMakeRange(1, self.value.length-2)];
        
    } else if ([self.value isEqualToString:@"scroll(position)"]) {
        //返回number类型
        result = @(self.currentIndex);
        
    } else if ([GXUtils isNumber:self.value]) {
        //返回number类型
        result = @([self.value doubleValue]);
        
    } else {
        //返回原始值
        result = self.value;
    }
    
    return result;
}

@end


#pragma mark - 取值表达式

@interface GXValueExpression ()

@property (nonatomic, copy) NSString *key;
@property (nonatomic, assign) NSInteger index;
@property (nonatomic, strong) GXExpression *nextExpression;

@end

@implementation GXValueExpression

- (instancetype)init{
    if (self = [super init]) {
        _index = -1;
    }
    return self;
}

+ (GXValueExpression *)expressionWithString:(NSString *)string{
    //默认值
    GXValueExpression *expression = nil;
    if ([GXUtils isValidString:string] && [string hasPrefix:@"${"] && [string hasSuffix:@"}"]) {
        //获取内容部分
        string = [string substringWithRange:NSMakeRange(2, string.length - 3)];
        string = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        //内容部分有效
        if (string.length) {
            if (![string hasPrefix:@"["]) {
                string = [@"." stringByAppendingString:string];
            }
            expression = [GXValueExpression handleExpressionWithString:string];
        }
    }
    return expression;
}

//生成表达式
+ (GXValueExpression *)handleExpressionWithString:(NSString *)string
{
    static NSCharacterSet *nextCharacterSet = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        nextCharacterSet = [NSCharacterSet characterSetWithCharactersInString:@"[."];
    });
    
    //默认值
    GXValueExpression *expression = nil;
    //类型判断
    if ([string hasPrefix:@"["]) {
        //数组类型
        NSRange range = [string rangeOfString:@"]"];
        if (range.location != NSNotFound) {
            //获取index
            NSString *indexString = [string substringWithRange:NSMakeRange(1, range.location - 1)];
            expression = [[GXValueExpression alloc] init];
            expression.index = [indexString integerValue];
            string = [string substringFromIndex:range.location + 1];
            
        } else {
            //不符合规则，置nil
            string = nil;
        }
        
    } else if ([string hasPrefix:@"."]) {
        //字典类型
        NSRange range = [string rangeOfCharacterFromSet:nextCharacterSet options:0 range:NSMakeRange(1, string.length - 1)];
        if (range.location != NSNotFound) {
            //获取key
            NSString *key = [string substringWithRange:NSMakeRange(1, range.location - 1)];
            expression = [[GXValueExpression alloc] init];
            expression.key = key;
            string = [string substringFromIndex:range.location];
            
        } else {
            //获取key
            NSString *key = [string substringFromIndex:1];
            expression = [[GXValueExpression alloc] init];
            expression.key = key;
            string = nil;
        }
    }
    
    //递归遍历
    if (expression && string && string.length) {
        expression.nextExpression = [self handleExpressionWithString:string];
    }
    
    //返回表达式
    return expression;
}

//通过解析->解析object->返回value
- (id)valueWithObject:(id)object
{
    if (object) {
        //取值
        id nextObject = nil;
        if (self.key.length && [object isKindOfClass:[NSDictionary class]]) {
            //dictionary
            NSDictionary *dict =(NSDictionary *)object;
            nextObject = [dict objectForKey:self.key];
            
        } else if (self.index >= 0 && [object isKindOfClass:[NSArray class]]) {
            //array
            NSArray *array = (NSArray *)object;
            if (self.index < array.count) {
                nextObject = [array objectAtIndex:self.index];
            }
        }
        
        //获取下一级
        if (self.nextExpression) {
            return [self.nextExpression valueWithObject:nextObject];
        } else {
            return nextObject;
        }
    }
    
    return nil;
}

- (void)setCurrentIndex:(NSInteger)currentIndex{
    [super setCurrentIndex:currentIndex];
    //子类赋值
    self.nextExpression.currentIndex = currentIndex;
}



@end


#pragma mark - 条件表达式

@interface GXConditionExpression ()

@property (nonatomic, strong) GXExpression *conditionExpression;
@property (nonatomic, strong) GXExpression *trueExpression;
@property (nonatomic, strong) GXExpression *falseExpression;

@end


@implementation GXConditionExpression

+ (GXExpression *)expressionWithString:(NSString *)string{
    //默认值
    GXConditionExpression *expression = nil;
    //类型判断
    if ([GXUtils isValidString:string] && [string hasPrefix:@"@{"] && [string hasSuffix:@"}"]) {
        // ① 处理expression
        string = [string substringWithRange:NSMakeRange(2, string.length - 3)];
        // ② 先处理a ?: b;
        expression = [self handleTwoPartExpression:string];
        if (expression == nil) {
            // ③ 在处理 a ? b : c
            expression = [self handleThreePartExpression:string];
        }
    }
    
    return expression;
}

//处理 a ?: b
+ (GXConditionExpression *)handleTwoPartExpression:(NSString *)string{
    //默认值
    GXConditionExpression *expression = nil;
    //生成子表达式
    NSRange range = [string rangeOfString:@" ?: "];
    if (range.location != NSNotFound) {
        NSString *conditionString = [string substringToIndex:range.location];
        NSString *falseString = [string substringFromIndex:range.location + 4];
        GXExpression *conditionExpression = [GXExpression expressionWithString:conditionString];
        GXExpression *falseExpression = [GXExpression expressionWithString:falseString];
        //生成最终表达式
        if (conditionExpression && falseExpression) {
            expression = [[GXConditionExpression alloc] init];
            expression.conditionExpression = conditionExpression;
            expression.trueExpression = conditionExpression;
            expression.falseExpression = falseExpression;
        }
    }
    //返回值
    return expression;
}

//处理 a ? b : c
+ (GXConditionExpression *)handleThreePartExpression:(NSString *)string{
    //默认值
    GXConditionExpression *expression = nil;
    //前后加空格
    NSRange range = [string rangeOfString:@" ? "];
    if (range.location != NSNotFound) {
        //获取条件string
        NSString *conditionStr = [string substringToIndex:range.location];
        //获取结果string
        NSString *resultStr = [string substringFromIndex:range.location + 3];
        //获取true/false
        NSString *trueStr = nil;
        NSString *falseStr = nil;
        if ([resultStr hasPrefix:@"@{"]) {
            NSInteger location = 0;
            NSInteger leftCount = 0;
            NSInteger rightCount = 0;
            for (int i = 0; i < resultStr.length; i++) {
                unichar ch = [resultStr characterAtIndex:i];
                NSString *chStr = [NSString stringWithCharacters:&ch length:1];
                // 获取 { 和 }
                if ([chStr isEqualToString:@"{"]) {
                    leftCount ++;
                } else if ([chStr isEqualToString:@"}"]){
                    rightCount ++;
                }
                // 对比
                if (leftCount != 0 && leftCount == rightCount) {
                    location = i + 1;
                    break;
                }
            }
            //获取成功 & 失败的字符串
            trueStr= [resultStr substringToIndex:location];
            falseStr = [resultStr substringFromIndex:location + 3];
            
        } else {
            //非嵌套
            NSRange range1 = [string rangeOfString:@" : "];
            if (range1.location != NSNotFound) {
                trueStr= [string substringWithRange:NSMakeRange(range.location + 3, range1.location - (range.location + 3))];
                falseStr = [string substringFromIndex:range1.location + 3];
            }
        }
        
        //创建表达式
        GXExpression *conditionExpression = [GXExpression expressionWithString:conditionStr];
        GXExpression *falseExpression = [GXExpression expressionWithString:falseStr];
        GXExpression *trueExpression = [GXExpression expressionWithString:trueStr];
        if (conditionExpression && trueExpression && falseExpression) {
            expression = [[GXConditionExpression alloc] init];
            expression.conditionExpression = conditionExpression;
            expression.trueExpression = trueExpression;
            expression.falseExpression = falseExpression;
        }
        
    }
    
    return expression;
}


- (id)valueWithObject:(id)object{
    if (object && ([object isKindOfClass:[NSDictionary class]] || [object isKindOfClass:[NSArray class]])) {
        //获取条件表达式
        id conditionObject = [self.conditionExpression valueWithObject:object];
        if (conditionObject) {
            // 计算规则
            if ([conditionObject isKindOfClass:[NSString class]] &&
                ([(NSString *)conditionObject isEqualToString:@"false"] ||
                 [(NSString *)conditionObject isEqualToString:@"0"] ||
                 ![(NSString *)conditionObject length])) {
                //false -> @"" / @"false" / @"0"
                return [self.falseExpression valueWithObject:object];
                
            } else if ([conditionObject isKindOfClass:[NSNumber class]] && ![conditionObject boolValue]) {
                //false -> 0 / false
                return [self.falseExpression valueWithObject:object];
                
            } else {
                //true
                if (self.conditionExpression == self.trueExpression) {
                    //返回结果
                    return conditionObject;
                } else {
                    //返回表达式处理结果
                    return [self.trueExpression valueWithObject:object];
                }
            }
            
        } else {
            //false -> nil
            return [self.falseExpression valueWithObject:object];
        }
        
    }
    
    return nil;
}

- (void)setCurrentIndex:(NSInteger)currentIndex{
    [super setCurrentIndex:currentIndex];
    //子类赋值
    self.conditionExpression.currentIndex = currentIndex;
    self.trueExpression.currentIndex = currentIndex;
    self.falseExpression.currentIndex = currentIndex;
}

@end


#pragma mark - eval()表达式

@interface GXEvalExpression ()

@property (nonatomic, strong) GXExpression *leftExpression;
@property (nonatomic, strong) GXExpression *rightExpression;
@property (nonatomic, strong) NSString *operation;

@end

@implementation GXEvalExpression

+ (GXExpression *)expressionWithString:(NSString *)string{
    //默认值
    GXEvalExpression *expression = nil;
    //类型判断
    if ([GXUtils isValidString:string] && [string hasPrefix:@"eval("] && [string hasSuffix:@")"]) {
        //截取表达式内容
        NSInteger location = @"eval(".length;
        NSString *evalValue = [string substringWithRange:NSMakeRange(location, string.length - (location + 1))];
        
        NSString *operation = nil;
        if ([evalValue rangeOfString:@"=="].location != NSNotFound) {
            operation = @"==";
            
        } else if ([evalValue rangeOfString:@"!="].location != NSNotFound){
            operation = @"!=";
            
        } else if ([evalValue rangeOfString:@">="].location != NSNotFound){
            operation = @">=";
            
        }else if ([evalValue rangeOfString:@"<="].location != NSNotFound){
            operation = @"<=";
            
        } else if ([evalValue rangeOfString:@">"].location != NSNotFound){
            operation = @">";
            
        } else if ([evalValue rangeOfString:@"<"].location != NSNotFound){
            operation = @"<";
            
        } else if ([evalValue rangeOfString:@"&&"].location != NSNotFound){
            operation = @"&&";
            
        } else if ([evalValue rangeOfString:@"||"].location != NSNotFound){
            operation = @"||";
            
        } else if ([evalValue rangeOfString:@"!"].location != NSNotFound){
            operation = @"!";
            
        } else if ([evalValue rangeOfString:@"%"].location != NSNotFound){
            operation = @"%";
            
        }  else{
            operation = nil;
        }
        
        //表达式和运算符都存在
        if (evalValue && operation) {
            expression = [self creatEval:evalValue operation:operation];
        }
        
    }
    
    return expression;
}

//创建eval表达式
+ (GXEvalExpression *)creatEval:(NSString *)evalValue operation:(NSString *)operValue{
    
    GXEvalExpression *expression = [[GXEvalExpression alloc] init];
    //分割内容
    NSArray *splitArray = [evalValue componentsSeparatedByString:operValue];
    //count为2处理
    if (splitArray.count == 2) {
        //生成自身表达式
        expression.operation = operValue;
        
        //获取左表达式
        NSString *leftExp = [splitArray objectAtIndex:0];
        expression.leftExpression = [GXExpression expressionWithString:leftExp];
        
        //获取右表达式
        NSString *rightExp = [splitArray objectAtIndex:1];
        expression.rightExpression = [GXExpression expressionWithString:rightExp];
    }
    
    return expression;
}

- (id)valueWithObject:(id)object{
    if (object) {
        //获取值
        id leftValue = [self.leftExpression valueWithObject:object];
        id rightValue = [self.rightExpression valueWithObject:object];
        
        //逻辑运算
        if ([self.operation isEqualToString:@"&&"]){
            return @([GXUtils boolValue:leftValue] && [GXUtils boolValue:rightValue]);
            
        } else if ([self.operation isEqualToString:@"||"]){
            return @([GXUtils boolValue:leftValue] || [GXUtils boolValue:rightValue]);
            
        } else if ([self.operation isEqualToString:@"=="]){
            //默认值
            BOOL isEqual = NO;
            //转换
            leftValue = [leftValue isKindOfClass:[NSNumber class]] ? [leftValue stringValue] : leftValue;
            rightValue = [rightValue isKindOfClass:[NSNumber class]] ? [rightValue stringValue] : rightValue;
            if (leftValue == nil && rightValue == nil) {
                isEqual = YES;
            } else {
                isEqual = [leftValue isEqual:rightValue];
            }
            return @(isEqual);
            
        } else if ([self.operation isEqualToString:@"!="]){
            //默认值
            BOOL isEqual = NO;
            //转换
            leftValue = [leftValue isKindOfClass:[NSNumber class]] ? [leftValue stringValue] : leftValue;
            rightValue = [rightValue isKindOfClass:[NSNumber class]] ? [rightValue stringValue] : rightValue;
            if (leftValue == nil && rightValue == nil) {
                isEqual = YES;
            } else {
                isEqual = [leftValue isEqual:rightValue];
            }
            return @(!isEqual);
            
        } else if ([self.operation isEqualToString:@">"]){
            if ([leftValue respondsToSelector:@selector(doubleValue)] &&
                [rightValue respondsToSelector:@selector(doubleValue)]) {
                //比较结果
                CGFloat leftNum = [leftValue doubleValue];
                CGFloat rightNum = [rightValue doubleValue];
                BOOL isEqual = (leftNum > rightNum);
                return @(isEqual);
            }
            
        } else if ([self.operation isEqualToString:@">="]){
            if ([leftValue respondsToSelector:@selector(doubleValue)] &&
                [rightValue respondsToSelector:@selector(doubleValue)]) {
                //比较结果
                CGFloat leftNum = [leftValue doubleValue];
                CGFloat rightNum = [rightValue doubleValue];
                BOOL isEqual = (leftNum >= rightNum);
                return @(isEqual);
            }
            
        } else if ([self.operation isEqualToString:@"<"]){
            if ([leftValue respondsToSelector:@selector(doubleValue)] &&
                [rightValue respondsToSelector:@selector(doubleValue)]) {
                //比较结果
                CGFloat leftNum = [leftValue doubleValue];
                CGFloat rightNum = [rightValue doubleValue];
                BOOL isEqual = (leftNum < rightNum);
                return @(isEqual);
            }
            
        } else if ([self.operation isEqualToString:@"<="]){
            if ([leftValue respondsToSelector:@selector(doubleValue)] &&
                [rightValue respondsToSelector:@selector(doubleValue)]) {
                //比较结果
                CGFloat leftNum = [leftValue doubleValue];
                CGFloat rightNum = [rightValue doubleValue];
                BOOL isEqual = (leftNum <= rightNum);
                return @(isEqual);
            }
            
        }  else if ([self.operation isEqualToString:@"%"]){
            if ([leftValue respondsToSelector:@selector(doubleValue)] &&
                [rightValue respondsToSelector:@selector(doubleValue)]) {
                //比较结果
                CGFloat leftNum = [leftValue doubleValue];
                CGFloat rightNum = [rightValue doubleValue];
                if (rightNum != 0) {
                    NSInteger result = ((NSInteger)leftNum % (NSInteger)rightNum);
                    return @(result);
                }
            }
            
        } else {
            return @(NO);
        }
        
    }
    
    return @(NO);
}


- (void)setCurrentIndex:(NSInteger)currentIndex{
    [super setCurrentIndex:currentIndex];
    //子类赋值
    self.leftExpression.currentIndex = currentIndex;
    self.rightExpression.currentIndex = currentIndex;
}

@end


#pragma mark - 环境变量表达式

@interface GXEnvExpression ()

//内容表达式
@property (nonatomic, strong) NSString *selector;

@end

@implementation GXEnvExpression

+ (GXExpression *)expressionWithString:(NSString *)string{
    //默认值
    GXEnvExpression *expression = nil;
    if ([GXUtils isValidString:string] && [string hasPrefix:@"env("] && [string hasSuffix:@")"]) {
        // ① 处理expression
        string = [string substringWithRange:NSMakeRange(4, string.length - 5)];
        // ② 删除前后空格
        string = [string stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        // ③ 表达式处理 （isLogin / isVip / isDarkMode / isLiuHaiPing / appVersion / isResponsiveLayout / screenWidth / screenHeight）
        expression = [[GXEnvExpression alloc] init];
        expression.selector = string;
    }
    return expression;
}

- (id)valueWithObject:(id)object{
    //不关注入参，只关注selecter的有效性
    id result = nil;
    
    //判断selector
    if (self.selector.length) {
        //调用表达式实现
        SEL selector = NSSelectorFromString(self.selector);
        if ([GXBizHelper respondsToSelector:selector]) {
            result = [GXBizHelper performSelector:selector];
        }
    }
    
    return result;
}

@end


#pragma mark - size表达式

@interface GXSizeExpression ()

//内容表达式
@property (nonatomic, strong) GXExpression *contentExpression;

@end


@implementation GXSizeExpression

+ (GXExpression *)expressionWithString:(NSString *)string{
    //默认值
    GXSizeExpression *expression = nil;
    if ([GXUtils isValidString:string] && [string hasPrefix:@"size("] && [string hasSuffix:@")"]) {
        // 1.处理expression
        string = [string substringWithRange:NSMakeRange(5, string.length - 6)];
        //生成表达式
        expression = [[GXSizeExpression alloc] init];
        expression.contentExpression = [GXExpression expressionWithString:string];
    }
    return expression;
}

- (id)valueWithObject:(id)object{
    if (object) {
        //获取值
        id result = nil;
        //取值
        id value = [self.contentExpression valueWithObject:object];
        //触发count / length方法
        if ([value isKindOfClass:[NSString class]]) {
            result = @([value length]);
        } else if ([value isKindOfClass:[NSDictionary class]] || [value isKindOfClass:[NSArray class]]){
            result = @([value count]);
        }
        return result;
    }
    return nil;
}

@end



#pragma mark - 拼接表达式

@interface GXPlusExpression ()

//分割的表达式数组
@property (nonatomic, strong) NSArray *expArray;

@end


@implementation GXPlusExpression

+ (GXExpression *)expressionWithString:(NSString *)string{
    //默认值
    GXPlusExpression *expression = nil;
    //类型判断
    if ([GXUtils isValidString:string] && ([string rangeOfString:@" + "].location != NSNotFound)) {
        //截取表达式内容
        NSMutableArray *expArray = [NSMutableArray array];
        NSArray *values = [string componentsSeparatedByString:@" + "];
        for (int i = 0; i < values.count; i++) {
            NSString *value = values[i];
            GXExpression *expression = [GXExpression expressionWithString:value];
            if (expression) {
                [expArray addObject:expression];
            }
        }
        //创建表达式
        expression = [[GXPlusExpression alloc] init];
        expression.expArray = expArray;
    }
    
    return expression;
}

- (id)valueWithObject:(id)object{
    if (object) {
        //获取值
        NSString *result = nil;
        if (self.expArray.count) {
            NSMutableArray *tmpArray = [NSMutableArray array];
            //表达式计算
            for (int i = 0; i < self.expArray.count; i++) {
                GXExpression *expression = [self.expArray objectAtIndex:i];
                id value = [expression valueWithObject:object];
                if (value && [value isKindOfClass:[NSString class]]){
                    [tmpArray addObject:value];
                } else if (value && [value respondsToSelector:@selector(stringValue)]) {
                    [tmpArray addObject:[value stringValue]];
                }
            }
            //拼接结果
            if (tmpArray.count) {
                result = [tmpArray componentsJoinedByString:@""];
            }
        }
        //返回
        return result;
    }
    
    return nil;
}

- (void)setCurrentIndex:(NSInteger)currentIndex{
    [super setCurrentIndex:currentIndex];
    //子类赋值
    for (GXExpression *exp in self.expArray) {
        exp.currentIndex = currentIndex;
    }
}


@end
