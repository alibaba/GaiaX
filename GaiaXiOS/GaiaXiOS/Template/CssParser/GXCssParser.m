//
//  GXCssParser.m
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

#import "GXCssParser.h"

typedef enum : NSUInteger {
    GXCssStateComment = 0,//注释
    GXCssStateSelector,//节点信息#
    GXCssStateInsideBraces,//小括号
    GXCssStateSingleQuotes,//单引号
    GXCssStateDoubleQuotes,//双引号
    GXCssStatePropertyName,//key
    GXCssStatePropertyValue//value
} GXCssState;

@interface GXCssParser (){
    NSMutableString *_propertyValue;
    NSMutableString *_propertyName;
    NSMutableString *_quotedString;
    NSMutableString *_selector;
    //item信息
    NSMutableSet *_itemSet;
    //状态
    unichar _prevChar;
    GXCssState _prevState;
    GXCssState _state;
}

@end


@implementation GXCssParser

-(id)init {
    self = [super init];
    if (self) {
        [self configureProperty];
    }
    return self;
}

//初始化基础属性
- (void)configureProperty {
    _prevChar = 0;
    _state = GXCssStateSelector;
    
    _selector = [[NSMutableString alloc] init];
    _quotedString = [[NSMutableString alloc] init];
    _propertyName = [[NSMutableString alloc] init];
    _propertyValue = [[NSMutableString alloc] init];
}

//解析css
-(NSDictionary *)parse:(NSString *)cssStr{
    NSUInteger length = cssStr.length;
    if (cssStr && length == 0) {
        return nil;
    }
    
    //对应的css的map
    NSMutableDictionary *resultDict = [NSMutableDictionary dictionary];

    //解析cssString
    for (NSUInteger i = 0; i < length; ++i) {
        unichar c = [cssStr characterAtIndex:i];
        switch (_state) {
            case GXCssStateSelector:{
                //解析#内容
                [self collectSelector:c];
            }
                break;
            case GXCssStatePropertyName:{
                //解析name
                [self collectPropertyName:c resultDict:resultDict];
            }
                break;
            case GXCssStatePropertyValue:{
                //解析value
                [self collectPropertyValue:c];
            }
                break;
            case GXCssStateSingleQuotes:
            case GXCssStateDoubleQuotes:
            case GXCssStateInsideBraces:{
                //解析特殊格式
                [self collectTextInsideQuotes:c];
            }
                break;
            case GXCssStateComment:{
                //处理注释
                [self handleComment:c];
            }
                break;
        }
    }
    
    return resultDict;
}


//获取处理item的#内容
- (void)collectSelector:(unichar)c {
    //判断是否为space
    if (!isSpace(c)) {
        //判断是否为注释
        if ([self checkCommentState:c]) {
            return;
        }
        
        NSMutableString *selector = _selector;
        
        if (c == '{') {
            //删除后面的空格信息
            if (selector.length > 0 && isSpace([selector characterAtIndex:selector.length - 1])) {
                [selector deleteCharactersInRange:NSMakeRange(selector.length - 1, 1)];
            }
            
            //创建nsset
            _itemSet = [[NSMutableSet alloc] init];
            
            //更新状态
            _prevChar = 0;
            _state = GXCssStatePropertyName;
            
        } else {
            if (isSpace(_prevChar) && selector.length > 0 && !isSpace([selector characterAtIndex:selector.length - 1])) {
                [selector appendFormat:@"%c", ' '];
            }
            [selector appendFormat:@"%c", c];
        }
        
    }
    
    _prevChar = c;
}

//处理属性key
- (void)collectPropertyName:(unichar)c resultDict:(NSMutableDictionary *)dict {
    if (!isSpace(c)) {
        //检查是否为注释
        if([self checkCommentState:c]){
            return;
        }
        
        NSMutableString *pn = _propertyName;
        
        switch (c) {
            case ':':{
                _state = GXCssStatePropertyValue;
            }
                break;
            case ';':{
                [self collectPropertyNameOnly];
                _state = GXCssStatePropertyName;
            }
                break;
            case '}':{
                [self collectPropertyNameOnly];
                _state = GXCssStateSelector;
                
                //生成的kv对的rule添加到set中
                NSMutableString *selector = _selector;
                [dict setObject:_itemSet forKey:selector];
                [selector deleteCharactersInRange:NSMakeRange(0, selector.length)];
            }
                break;
            default:{
                [pn appendFormat:@"%c", c];
            }
                break;
        }
    }
    
    _prevChar = c;
}

//css中允许value为nil，兼容处理
- (void)collectPropertyNameOnly {
    NSMutableString *pn = _propertyName;
    if (pn.length > 0) {
        if (isSpace([pn characterAtIndex:pn.length - 1])) {
            [pn deleteCharactersInRange:NSMakeRange(pn.length - 1, 1)];
        }
        
        //添加到生成对应set中
        [_itemSet addObject:[[GXCssItem alloc] initWithPropertyName:pn propertyValue:nil]];
        [pn deleteCharactersInRange:NSMakeRange(0, pn.length)];
    }
}

//处理属性value
-(void)collectPropertyValue:(unichar)c {
    if (!isSpace(c)) {
        //判断是否为注释
        if([self checkCommentState:c]) return;
        
        NSMutableString *pn = _propertyName;
        NSMutableString *pv = _propertyValue;
        if (c == ';') {
            //删除key & value后面的空格
            if (pn.length > 0 && isSpace([pn characterAtIndex:pn.length - 1])) {
                [pn deleteCharactersInRange:NSMakeRange(pn.length - 1, 1)];
            }
            if (pv.length > 0 && isSpace([pv characterAtIndex:pv.length - 1])) {
                [pv deleteCharactersInRange:NSMakeRange(pv.length - 1, 1)];
            }
            
            //添加到生成对应rule中
            [_itemSet addObject:[[GXCssItem alloc] initWithPropertyName:pn propertyValue:pv]];
            
            [pn deleteCharactersInRange:NSMakeRange(0, pn.length)];
            [pv deleteCharactersInRange:NSMakeRange(0, pv.length)];
            
            _state = GXCssStatePropertyName;
            
        } else {
            switch (c) {
                case '\'':
                    _state = GXCssStateSingleQuotes;
                    break;
                case '"':
                    _state = GXCssStateDoubleQuotes;
                    break;
                case '(':
                    _state = GXCssStateInsideBraces;
                    break;
                default:
                    if (isSpace(_prevChar) && pv.length > 0 && !isSpace([pv characterAtIndex:pv.length - 1])) {
                        [pv appendFormat:@"%c", ' '];
                    }
                    [pv appendFormat:@"%c", c];
                    break;
            }
        }
    }
    
    _prevChar = c;
}

//解析特殊格式（'', "", ()）
- (void)collectTextInsideQuotes:(unichar)c {
    
    NSMutableString *qs = _quotedString;
    
    if ((c == '\'' && _state == GXCssStateSingleQuotes)
        || (c == '"' && _state == GXCssStateDoubleQuotes)
        || (c == ')' && _state == GXCssStateInsideBraces)) {
        if (qs.length > 0) {
            switch (_state) {
                case GXCssStateSingleQuotes:
                    [_propertyValue appendFormat:@"'%@'", qs];
                    break;
                case GXCssStateDoubleQuotes:
                    [_propertyValue appendFormat:@"\"%@\"", qs];
                    break;
                case GXCssStateInsideBraces:
                    [_propertyValue appendFormat:@"(%@)", qs];
                    break;
                default:
                    break;
            }
            
            [qs deleteCharactersInRange:NSMakeRange(0, qs.length)];
            _state = GXCssStatePropertyValue;
        }
    } else {
        [qs appendFormat:@"%c", c];
    }
}

//判断是否为注释（注释开始）
- (BOOL)checkCommentState:(unichar)c {
    if (_prevChar == '/' && c == '*') {
        switch (_state) {
            case GXCssStateSelector:{
                [_selector deleteCharactersInRange:NSMakeRange(_selector.length - 1, 1)];
            }
                break;
            case GXCssStatePropertyName:{
                [_propertyName deleteCharactersInRange:NSMakeRange(_propertyName.length - 1, 1)];
            }
                break;
            case GXCssStatePropertyValue:{
                [_propertyValue deleteCharactersInRange:NSMakeRange(_propertyValue.length - 1, 1)];
            }
                break;
            default:
                break;
        }
        
        //更新状态
        _prevChar = 0;
        _prevState = _state;
        _state = GXCssStateComment;
            
        return YES;
    }
    
    return NO;
}

//处理注释
- (void)handleComment:(unichar)c {
    //判断是否为注释
    if (_prevChar == '*' && c == '/') {
        _state = _prevState;
        _prevChar = 0;
        
    } else {
        _prevChar = c;
    }
}

// 判断是否为空格
BOOL isSpace(unichar c) {
    return (c == ' ' || c == '\n' || c == '\t' || c == '\r');
}



@end
