//
//  GXExpression.h
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

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface GXExpression : NSObject

//expression rule
@property (nonatomic, copy) NSString *expression;
//data index
@property (nonatomic, assign) NSInteger currentIndex;

/// Get expression by rule
/// @param string rule
+ (nullable GXExpression *)expressionWithString:(NSString *)string;

/// Get the final value by an expression
/// @param object source data（dictionary/array）
- (nullable id)valueWithObject:(nullable id)object;

@end


/**
 * constant expression
*/
@interface GXConstExpression : GXExpression

@end


/**
 * value expression
 * ${data.items[0].title}
*/
@interface GXValueExpression : GXExpression

@end


/**
 * condition expression
 * @{a ?: b}
 * @{condition ? a : b}
*/
@interface GXConditionExpression : GXExpression

@end


/**
 * eval expression, not support nest eval expression
 * eval(a > b)
 * eval('a' == 'b')
*/
@interface GXEvalExpression : GXExpression

@end

/**
 * environment expression
 * env(isVip)
 */
@interface GXEnvExpression : GXExpression

@end


/**
 * count expression
 * size(array / ${data.title})
 **/
@interface GXSizeExpression : GXExpression

@end


/**
 * plus expression
 * text + text
 * text + ${}
 * ${} + text
 * ${} + ${}
 */
@interface GXPlusExpression : GXExpression

@end



NS_ASSUME_NONNULL_END
