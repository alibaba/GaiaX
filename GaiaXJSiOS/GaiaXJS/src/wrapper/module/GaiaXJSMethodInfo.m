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

#import "GaiaXJSMethodInfo.h"
#import "GaiaXJSMethodArgument.h"
#import "GaiaXJSHelper.h"
#import "GaiaXJSModuleInfo.h"
#import "GaiaXJSConvert.h"
#import <objc/runtime.h>
#import <objc/message.h>

typedef BOOL (^GAIAXJSArgumentBlock)(NSUInteger, id);

@interface GaiaXJSMethodInfo ()

@property(nonatomic, strong) NSMutableArray *retainedObjects;

@end

@implementation GaiaXJSMethodInfo


static SEL selectorForType(NSString *type) {
    const char *input = type.UTF8String;
    return NSSelectorFromString([GaiaXJSParseType(&input) stringByAppendingString:@":"]);
}

- (instancetype)initWithExportedMethod:(GaiaXJSExportedMethod *)exportMethod
                            moduleInfo:(GaiaXJSModuleInfo *)moduleInfo {
    if (self = [super init]) {
        self.moduleInfo = moduleInfo;
        self.isSync = exportMethod->isSync;
        self.methodName = [NSString stringWithFormat:@"%s", exportMethod->objcName];
        if (self.isSync) {
            self.methodType = GaiaXJSMethodTypeSync;
        } else {
            if ([self.methodName rangeOfString:@"GaiaXJSPromiseResolveBlock"].location != NSNotFound ||
                [self.methodName rangeOfString:@"GaiaXPromiseResolveBlock"].location != NSNotFound ||
                [self.methodName rangeOfString:@"GaiaXJSPromiseRejectBlock"].location != NSNotFound ||
                [self.methodName rangeOfString:@"GaiaXPromiseRejectBlock"].location != NSNotFound) {
                self.methodType = GaiaXJSMethodTypePromise;
            } else {
                self.methodType = GaiaXJSMethodTypeAsync;
            }
        }
        self.retainedObjects = [NSMutableArray array];
    }
    return self;
}

- (id)invokeWithArguments:(NSArray *)args {
    NSObject *object = [[NSClassFromString(self.moduleInfo.moduleName) alloc] init];

    if (object == nil) {
        return nil;
    }

    NSArray <GaiaXJSMethodArgument *> *arguments;
    SEL selector = NSSelectorFromString(GaiaXJSParseMethodSignature([self.methodName UTF8String], &arguments));
    NSMethodSignature *signature = [object methodSignatureForSelector:selector];
    if (!signature) {
        return nil;
    }

    NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
    [invocation setSelector:selector];
    [invocation setTarget:object];

    NSUInteger numberOfArguments = signature.numberOfArguments;
    NSMutableArray *argumentBlocks = [[NSMutableArray alloc] init];
    for (NSUInteger i = 2; i < numberOfArguments; i++) {
        const char *objcType = [signature getArgumentTypeAtIndex:i];
        BOOL isNullableType = NO;
        GaiaXJSMethodArgument *argument = arguments[i - 2];
        NSString *typeName = argument.type;
        SEL selector = selectorForType(typeName);
        if ([GaiaXJSConvert respondsToSelector:selector]) {
            switch (objcType[0]) {
                case _C_CHR: GAIAXJS_PRIMITIVE_CASE(char)
                case _C_UCHR: GAIAXJS_PRIMITIVE_CASE(unsigned char)
                case _C_SHT: GAIAXJS_PRIMITIVE_CASE(short)
                case _C_USHT: GAIAXJS_PRIMITIVE_CASE(unsigned short)
                case _C_INT: GAIAXJS_PRIMITIVE_CASE(int)
                case _C_UINT: GAIAXJS_PRIMITIVE_CASE(unsigned int)
                case _C_LNG: GAIAXJS_PRIMITIVE_CASE(long)
                case _C_ULNG: GAIAXJS_PRIMITIVE_CASE(unsigned long)
                case _C_LNG_LNG: GAIAXJS_PRIMITIVE_CASE(long long)
                case _C_ULNG_LNG: GAIAXJS_PRIMITIVE_CASE(unsigned long long)
                case _C_FLT: GAIAXJS_PRIMITIVE_CASE(float)
                case _C_DBL: GAIAXJS_PRIMITIVE_CASE(double)
                case _C_BOOL: GAIAXJS_PRIMITIVE_CASE(BOOL)
                case _C_SEL: GAIAXJS_PRIMITIVE_CASE(SEL)
                case _C_CHARPTR: GAIAXJS_PRIMITIVE_CASE(const char *)
                case _C_PTR: GAIAXJS_PRIMITIVE_CASE(void *)
                case _C_ID: {
                    isNullableType = YES;
                    id (*convert)(id, SEL, id) = (__typeof__(convert)) objc_msgSend;
                    GAIAXJS_RETAINED_ARG_BLOCK(id value = convert([GaiaXJSConvert class], selector, json););
                    break;
                }
            }
        }
    }

    NSUInteger index = 0;
    for (id json in args) {
        GAIAXJSArgumentBlock block = argumentBlocks[index];
        if (!block(index, GAIAXJSNilIfNull(json))) {
            return nil;
        }
        index++;
    }

    for (; index < [argumentBlocks count]; index++) {
        GAIAXJSArgumentBlock block = argumentBlocks[index];
        if (!block(index, NULL)) {
            return nil;
        }
    }


    [invocation invoke];

    if (*[signature methodReturnType] == '@') {
        void *retValue = nil;
        [invocation getReturnValue:&retValue];
        if (retValue) {
            return (__bridge id) retValue;
        }
    }


    return nil;
}


- (void)invokeWithArguments:(NSArray *)args
                   callback:(GaiaXJSCallbackBlock)callback {
    NSObject *object = [[NSClassFromString(self.moduleInfo.moduleName) alloc] init];
    
    if (object == nil) {
        callback(nil);
        return;
    }

    NSArray <GaiaXJSMethodArgument *> *arguments;
    SEL selector = NSSelectorFromString(GaiaXJSParseMethodSignature([self.methodName UTF8String], &arguments));
    NSMethodSignature *signature = [object methodSignatureForSelector:selector];
    if (!signature) {
        callback(nil);
        return;
    }

    NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
    [invocation setSelector:selector];
    [invocation setTarget:object];

    NSUInteger numberOfArguments = signature.numberOfArguments;
    NSMutableArray *argumentBlocks = [[NSMutableArray alloc] init];

    for (NSUInteger i = 2; i < numberOfArguments; i++) {
        const char *objcType = [signature getArgumentTypeAtIndex:i];
        BOOL isNullableType = NO;
        GaiaXJSMethodArgument *argument = arguments[i - 2];
        NSString *typeName = argument.type;
        SEL selector = selectorForType(typeName);
        if ([GaiaXJSConvert respondsToSelector:selector]) {
            switch (objcType[0]) {
                case _C_CHR: GAIAXJS_PRIMITIVE_CASE(char)
                case _C_UCHR: GAIAXJS_PRIMITIVE_CASE(unsigned char)
                case _C_SHT: GAIAXJS_PRIMITIVE_CASE(short)
                case _C_USHT: GAIAXJS_PRIMITIVE_CASE(unsigned short)
                case _C_INT: GAIAXJS_PRIMITIVE_CASE(int)
                case _C_UINT: GAIAXJS_PRIMITIVE_CASE(unsigned int)
                case _C_LNG: GAIAXJS_PRIMITIVE_CASE(long)
                case _C_ULNG: GAIAXJS_PRIMITIVE_CASE(unsigned long)
                case _C_LNG_LNG: GAIAXJS_PRIMITIVE_CASE(long long)
                case _C_ULNG_LNG: GAIAXJS_PRIMITIVE_CASE(unsigned long long)
                case _C_FLT: GAIAXJS_PRIMITIVE_CASE(float)
                case _C_DBL: GAIAXJS_PRIMITIVE_CASE(double)
                case _C_BOOL: GAIAXJS_PRIMITIVE_CASE(BOOL)
                case _C_SEL: GAIAXJS_PRIMITIVE_CASE(SEL)
                case _C_CHARPTR: GAIAXJS_PRIMITIVE_CASE(const char *)
                case _C_PTR: GAIAXJS_PRIMITIVE_CASE(void *)
                case _C_ID: {
                    isNullableType = YES;
                    id (*convert)(id, SEL, id) = (__typeof__(convert)) objc_msgSend;
                    GAIAXJS_RETAINED_ARG_BLOCK(id value = convert([GaiaXJSConvert class], selector, json););
                    break;
                }
            }
        } else if ([typeName isEqualToString:@"GaiaXJSCallbackBlock"] || [typeName isEqualToString:@"GaiaXCallbackBlock"]) {
            GAIAXJS_BLOCK_CASE((id result), {
                callback(result);
            });
        }
    }

    NSUInteger index = 0;
    for (id json in args) {
        GAIAXJSArgumentBlock block = argumentBlocks[index];
        if (!block(index, GAIAXJSNilIfNull(json))) {
            callback(nil);
            return;
        }
        index++;
    }

    for (; index < [argumentBlocks count]; index++) {
        GAIAXJSArgumentBlock block = argumentBlocks[index];
        if (!block(index, NULL)) {
            callback(nil);
            return;
        }
    }


    [invocation invoke];

    [self.retainedObjects removeAllObjects];

}

- (void)invokeWithArguments:(NSArray *)args
                   resolver:(GaiaXJSPromiseResolveBlock)resolve
                   rejecter:(GaiaXJSPromiseRejectBlock)reject {
    NSObject *object = [[NSClassFromString(self.moduleInfo.moduleName) alloc] init];

    if (object == nil) {
        reject(@"-1", [NSString stringWithFormat:@"无法实例化模块 %@", self.moduleInfo.moduleName]);
        return;
    }

    NSArray <GaiaXJSMethodArgument *> *arguments;
    SEL selector = NSSelectorFromString(GaiaXJSParseMethodSignature([self.methodName UTF8String], &arguments));
    NSMethodSignature *signature = [object methodSignatureForSelector:selector];
    if (!signature) {
        reject(@"-2", [NSString stringWithFormat:@"无法实例化方法 %@", self.methodName]);
        return;
    }

    NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
    [invocation setSelector:selector];
    [invocation setTarget:object];

    NSUInteger numberOfArguments = signature.numberOfArguments;
    NSMutableArray *argumentBlocks = [[NSMutableArray alloc] init];

    for (NSUInteger i = 2; i < numberOfArguments; i++) {
        const char *objcType = [signature getArgumentTypeAtIndex:i];
        BOOL isNullableType = NO;
        GaiaXJSMethodArgument *argument = arguments[i - 2];
        NSString *typeName = argument.type;
        SEL selector = selectorForType(typeName);
        if ([GaiaXJSConvert respondsToSelector:selector]) {
            switch (objcType[0]) {
                case _C_CHR: GAIAXJS_PRIMITIVE_CASE(char)
                case _C_UCHR: GAIAXJS_PRIMITIVE_CASE(unsigned char)
                case _C_SHT: GAIAXJS_PRIMITIVE_CASE(short)
                case _C_USHT: GAIAXJS_PRIMITIVE_CASE(unsigned short)
                case _C_INT: GAIAXJS_PRIMITIVE_CASE(int)
                case _C_UINT: GAIAXJS_PRIMITIVE_CASE(unsigned int)
                case _C_LNG: GAIAXJS_PRIMITIVE_CASE(long)
                case _C_ULNG: GAIAXJS_PRIMITIVE_CASE(unsigned long)
                case _C_LNG_LNG: GAIAXJS_PRIMITIVE_CASE(long long)
                case _C_ULNG_LNG: GAIAXJS_PRIMITIVE_CASE(unsigned long long)
                case _C_FLT: GAIAXJS_PRIMITIVE_CASE(float)
                case _C_DBL: GAIAXJS_PRIMITIVE_CASE(double)
                case _C_BOOL: GAIAXJS_PRIMITIVE_CASE(BOOL)
                case _C_SEL: GAIAXJS_PRIMITIVE_CASE(SEL)
                case _C_CHARPTR: GAIAXJS_PRIMITIVE_CASE(const char *)
                case _C_PTR: GAIAXJS_PRIMITIVE_CASE(void *)
                case _C_ID: {
                    isNullableType = YES;
                    id (*convert)(id, SEL, id) = (__typeof__(convert)) objc_msgSend;
                    GAIAXJS_RETAINED_ARG_BLOCK(id value = convert([GaiaXJSConvert class], selector, json););
                    break;
                }
            }
        } else if ([typeName isEqualToString:@"GaiaXJSPromiseResolveBlock"] || [typeName isEqualToString:@"GaiaXPromiseResolveBlock"]) {
            GAIAXJS_BLOCK_CASE((id result), {
                resolve(result);
            });
        } else if ([typeName isEqualToString:@"GaiaXJSPromiseRejectBlock"] || [typeName isEqualToString:@"GaiaXPromiseRejectBlock"]) {
            GAIAXJS_BLOCK_CASE((NSString * code, NSString * message), {
                reject(code, message);
            });
        }
    }

    NSUInteger index = 0;
    for (id json in args) {
        GAIAXJSArgumentBlock block = argumentBlocks[index];
        if (!block(index, GAIAXJSNilIfNull(json))) {
            reject(@"-1", @"参数错误");
            return;
        }
        index++;
    }

    for (; index < [argumentBlocks count]; index++) {
        GAIAXJSArgumentBlock block = argumentBlocks[index];
        if (!block(index, NULL)) {
            reject(@"-1", @"参数错误");
            return;
        }
    }


    [invocation invoke];

    [self.retainedObjects removeAllObjects];
}

@end
