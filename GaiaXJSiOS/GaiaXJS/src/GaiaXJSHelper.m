/*
 * Copyright (c) 2022, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


#import "GaiaXJSHelper.h"
#import "GaiaXJSMethodInfo.h"
#import "GaiaXJSModuleInfo.h"
#import "GaiaXJSMethodArgument.h"

@implementation GaiaXJSHelper

+ (NSUserDefaults *)getUserDefaults {
    static NSUserDefaults *defaults = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        defaults = [[NSUserDefaults alloc] initWithSuiteName:@"GAIAXJS"];
    });
    return defaults;
}

+ (NSString *)toJSName:(NSString *)input {
    NSRange range = [input rangeOfString:@":"];
    NSString *jsName;
    if (range.location != NSNotFound) {
        jsName = [input substringToIndex:range.location];
    }
    if (jsName != NULL) {
        jsName = [jsName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    } else {
        jsName = input;
    }
    return jsName;
}

+ (NSString *)getHelperJSMethods {
    return [NSString stringWithFormat:@"var __extends =  (this && this.__extends) || (function () { \r\n \
            var extendStatics = function (d, b) { \r\n \
                extendStatics = Object.setPrototypeOf || \
                    ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) || \
                    function (d, b) { \r\n for (var p in b) if (b.hasOwnProperty(p)) \r\n d[p] = b[p]; }; \
                return extendStatics(d, b); \r\n \
            } \r\n \
            return function (d, b) { \r\n \
                extendStatics(d, b); \
                function __() { \r\n this.constructor = d; } \
                d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __()); \
            }; \
        })(); \
        var __assign =  (this && this.__assign) || function () { \r\n \
            __assign = Object.assign || function(t) { \r\n \
                for (var s, i = 1, n = arguments.length; i < n; i++) { \r\n \
                    s = arguments[i]; \r\n \
                    for (var p in s)  if (Object.prototype.hasOwnProperty.call(s, p)) \
                        t[p] = s[p]; \
                } \r\n \
                return t; \r\n \
            }; \r\n \
            return __assign.apply(this, arguments); \r\n \
        }; \r\n"];
}

+ (NSString *)generateJSPropsStringWithClass:(NSString *)className key:(NSString *)keyString {
    return [NSString stringWithFormat:@"Object.defineProperty(%@Props.prototype, \"%@\", {\
                                  get: function () {\
                                    return this.__%@__;\
                                  },\
                                  set: function (value) {\
                                    this.__%@__ = value;\
                                    gaiax.setProps(\"%@\", value, this.targetData);\
                                  },\
                                });", className, keyString, keyString, keyString, keyString];
}

+ (NSString *)generateJSStyleStringWithClass:(NSString *)className key:(NSString *)keyString {
    return [NSString stringWithFormat:@"Object.defineProperty(%@Style.prototype, \"%@\", {\
                                  get: function () {\
                                    return this.__%@__;\
                                  },\
                                  set: function (value) {\
                                    this.__%@__ = value;\
                                    gaiax.setStyle(\"%@\", value, this.targetData);\
                                  },\
                                });", className, keyString, keyString, keyString, keyString];
}


+ (NSString *)generateJSMethodString:(GaiaXJSMethodInfo *)methodInfo
                         moduleIndex:(NSInteger)moduleIndex
                         methodIndex:(NSInteger)methodIndex {
    NSMutableString *result = [NSMutableString string];
    NSString *objcMethodString = methodInfo.methodName;
    NSString *jsName = [GaiaXJSHelper toJSName:objcMethodString];
    NSString *className = methodInfo.moduleInfo.moduleName;
    NSString *jsClassName = className;
    [result appendFormat:@"%@.prototype.%@ = function() {", jsClassName, jsName];
    [result appendFormat:@"var args = [];\
     for (var _i = 0; _i < arguments.length; _i++) {\
         args[_i] = arguments[_i];\
     }"];
    if (methodInfo.isSync) {
        [result appendFormat:@"return %@.callSync({moduleId:%ld, methodId:%ld, timestamp:Date.now(), args});", jsClassName, (long) moduleIndex, (long) methodIndex];
    } else {
        if (methodInfo.methodType == GaiaXJSMethodTypePromise) {
            [result appendFormat:@"return new Promise(function (resolve, reject) {\
             %@.callPromise({moduleId:%ld, methodId:%ld, timestamp:Date.now(), args}).then(function(result) {\
             resolve(result)\
             }).catch(function(error) {\
             reject(error);}) }) ", jsClassName, (long) moduleIndex, (long) methodIndex];
        } else if (methodInfo.methodType == GaiaXJSMethodTypeAsync) {
            [result appendFormat:@"%@.callAsync({moduleId:%ld, methodId:%ld, timestamp:Date.now(), args: (typeof args[args.length-1] == 'function') ?  args.slice(0, args.length-1) : args}, function(result) {\
             let callback = args[args.length-1];\
             callback && (typeof callback == 'function') && callback(result);  });",
                                 jsClassName, (long) moduleIndex, (long) methodIndex];
        }
    }
    [result appendString:@"}"];
    return result;
}

+ (NSString *)removeGaiaXPrefix:(NSString *)moduleName {
    NSString *result = moduleName;
    if ([result hasPrefix:@"GaiaX"]) {
        result = [result substringFromIndex:5];
    } else if ([result hasPrefix:@"GaiaXJS"]) {
        result = [result substringFromIndex:7];
    }
    if ([result hasSuffix:@"Module"]) {
        result = [result substringToIndex:result.length - 6];
    }
    return result;
}

NSString *GaiaXJSParseMethodSignature(const char *input, NSArray<GaiaXJSMethodArgument *> **arguments) {
    GaiaXJSSkipWhitespace(&input);
    NSMutableArray *args;
    NSMutableString *selector = [NSMutableString new];
    while (GaiaXJSParseSelectorPart(&input, selector)) {
        if (!args) {
            args = [NSMutableArray new];
        }
        if (GaiaXJSReadChar(&input, '(')) {
            GaiaXJSSkipWhitespace(&input);
            GaiaXJSNullability nullability = GaiaXJSParseNullability(&input);
            GaiaXJSSkipWhitespace(&input);
            BOOL unused = GaiaXJSParseUnused(&input);
            GaiaXJSSkipWhitespace(&input);
            NSString *type = GaiaXJSParseType(&input);
            GaiaXJSSkipWhitespace(&input);
            if (nullability == GaiaXJSNullabilityUnspecified) {
                nullability = GaiaXJSParseNullabilityPostfix(&input);
                GaiaXJSSkipWhitespace(&input);
                if (!unused) {
                    unused = GaiaXJSParseUnused(&input);
                    GaiaXJSSkipWhitespace(&input);
                    if (unused && nullability == GaiaXJSNullabilityUnspecified) {
                        nullability = GaiaXJSParseNullabilityPostfix(&input);
                        GaiaXJSSkipWhitespace(&input);
                    }
                }
            } else if (!unused) {
                unused = GaiaXJSParseUnused(&input);
                GaiaXJSSkipWhitespace(&input);
            }
            [args addObject:[[GaiaXJSMethodArgument alloc] initWithType:type nullability:nullability unused:unused]];
            GaiaXJSSkipWhitespace(&input);
            GaiaXJSReadChar(&input, ')');
            GaiaXJSSkipWhitespace(&input);
        } else {
            [args addObject:[[GaiaXJSMethodArgument alloc] initWithType:@"id" nullability:GaiaXJSNullable unused:NO]];
        }
        GaiaXJSParseArgumentIdentifier(&input, NULL);
        GaiaXJSSkipWhitespace(&input);
    }
    *arguments = [args copy];
    return selector;
}


void GaiaXJSSkipWhitespace(const char **input) {
    while (isspace(**input)) {
        (*input)++;
    }
}

BOOL GaiaXJSReadChar(const char **input, char c) {
    if (**input == c) {
        (*input)++;
        return YES;
    }
    return NO;
}

static BOOL GaiaXJSParseUnused(const char **input) {
    return GaiaXJSReadString(input, "__attribute__((unused))") || GaiaXJSReadString(input, "__attribute__((__unused__))") ||
            GaiaXJSReadString(input, "__unused");
}

BOOL GaiaXJSReadString(const char **input, const char *string) {
    int i;
    for (i = 0; string[i] != 0; i++) {
        if (string[i] != (*input)[i]) {
            return NO;
        }
    }
    *input += i;
    return YES;
}

static BOOL GaiaXJSParseSelectorPart(const char **input, NSMutableString *selector) {
    NSString *selectorPart;
    if (GaiaXJSParseSelectorIdentifier(input, &selectorPart)) {
        [selector appendString:selectorPart];
    }
    GaiaXJSSkipWhitespace(input);
    if (GaiaXJSReadChar(input, ':')) {
        [selector appendString:@":"];
        GaiaXJSSkipWhitespace(input);
        return YES;
    }
    return NO;
}


BOOL GaiaXJSParseSelectorIdentifier(const char **input, NSString **string) {
    const char *start = *input;
    if (!GaiaXJSIsIdentifierHead(**input)) {
        return NO;
    }
    (*input)++;
    while (GaiaXJSIsIdentifierTail(**input)) {
        (*input)++;
    }
    if (string) {
        *string = [[NSString alloc] initWithBytes:start length:(NSInteger) (*input - start) encoding:NSASCIIStringEncoding];
    }
    return YES;
}

static BOOL GaiaXJSIsIdentifierHead(const char c) {
    return isalpha(c) || c == '_';
}

static BOOL GaiaXJSIsIdentifierTail(const char c) {
    return isalnum(c) || c == '_';
}


NSString *GaiaXJSParseType(const char **input) {
    NSString *type;
    GaiaXJSParseArgumentIdentifier(input, &type);
    GaiaXJSSkipWhitespace(input);
    if (GaiaXJSReadChar(input, '<')) {
        GaiaXJSSkipWhitespace(input);
        NSString *subtype = GaiaXJSParseType(input);
        if (GaiaXJSIsCollectionType(type)) {
            if ([type isEqualToString:@"NSDictionary"]) {
                GaiaXJSSkipWhitespace(input);
                GaiaXJSReadChar(input, ',');
                GaiaXJSSkipWhitespace(input);
                subtype = GaiaXJSParseType(input);
            }
            if (![subtype isEqualToString:@"id"]) {
                type = [type stringByReplacingCharactersInRange:(NSRange) {0, 2} withString:subtype];
            }
        }
        GaiaXJSSkipWhitespace(input);
        GaiaXJSReadChar(input, '>');
    }
    GaiaXJSSkipWhitespace(input);
    if (!GaiaXJSReadChar(input, '*')) {
        GaiaXJSReadChar(input, '&');
    }
    return type;
}

BOOL GaiaXJSParseArgumentIdentifier(const char **input, NSString **string) {
    const char *start = *input;
    do {
        if (!GaiaXJSIsIdentifierHead(**input)) {
            return NO;
        }
        (*input)++;
        while (GaiaXJSIsIdentifierTail(**input)) {
            (*input)++;
        }
    } while (GaiaXJSReadString(input, "::"));
    if (string) {
        *string = [[NSString alloc] initWithBytes:start length:(NSInteger) (*input - start) encoding:NSASCIIStringEncoding];
    }
    return YES;
}

static BOOL GaiaXJSIsCollectionType(NSString *type) {
    static NSSet *collectionTypes;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        collectionTypes = [[NSSet alloc] initWithObjects:@"NSArray", @"NSSet", @"NSDictionary", nil];
    });
    return [collectionTypes containsObject:type];
}

static GaiaXJSNullability GaiaXJSParseNullability(const char **input) {
    if (GaiaXJSReadString(input, "nullable")) {
        return GaiaXJSNullable;
    } else if (GaiaXJSReadString(input, "nonnull")) {
        return GaiaXJSNonnullable;
    }
    return GaiaXJSNullabilityUnspecified;
}

static GaiaXJSNullability GaiaXJSParseNullabilityPostfix(const char **input) {
    if (GaiaXJSReadString(input, "_Nullable") || GaiaXJSReadString(input, "__nullable")) {
        return GaiaXJSNullable;
    } else if (GaiaXJSReadString(input, "_Nonnull") || GaiaXJSReadString(input, "__nonnull")) {
        return GaiaXJSNonnullable;
    }
    return GaiaXJSNullabilityUnspecified;
}


@end
