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

#import "GaiaXJSUIManager.h"
#import <mach-o/getsect.h>
#import <dlfcn.h>
#import <objc/runtime.h>
#import "GaiaXJSModuleInfo.h"
#import "GaiaXJSHelper.h"
#import <objc/message.h>
#import "GaiaXJSConvert.h"

@implementation GaiaXJSUIManager

+ (instancetype)defaultManager {
    static dispatch_once_t onceToken;
    static GaiaXJSUIManager *defaultM;
    dispatch_once(&onceToken, ^{
        defaultM = [[GaiaXJSUIManager alloc] init];
        [defaultM initMaps];
        [defaultM registerModules];
    });
    return defaultM;
}

+ (instancetype)debuggerManager {
    static dispatch_once_t onceDebuggerToken;
    static GaiaXJSUIManager *debuggerM;
    dispatch_once(&onceDebuggerToken, ^{
        debuggerM = [[GaiaXJSUIManager alloc] init];
        [debuggerM initMaps];
        [debuggerM registerModulesWithEngineType:GaiaXJSEngineTypeDebugger];
    });
    return debuggerM;
}


- (void)initMaps {
    self.injectProps = [NSMutableString string];
    self.rdInjectProps = [NSMutableString string];
    self.modulesMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
    self.idsMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
}

- (void)registerModulesWithEngineType:(GaiaXJSEngineType)engineType {
    BOOL isDebugging = engineType == GaiaXJSEngineTypeDebugger;
    static NSString *configuration = @"";
    Dl_info info;
    dladdr((__bridge const void *) (configuration), &info);
#ifndef __LP64__
    const struct mach_header *mhp = (struct mach_header*)info.dli_fbase;
    unsigned long size = 0;
    uint32_t *data = (uint32_t*)getsectiondata(mhp, "__DATA", "__gaiaxjs", &size);
#else
    const struct mach_header_64 *mhp = (struct mach_header_64 *) info.dli_fbase;
    unsigned long size = 0;
    uint64_t *data = (uint64_t *) getsectiondata(mhp, "__DATA", "__gaiaxjs", &size);
#endif
    NSUInteger counter = size / sizeof(void *);
    for (NSUInteger idx = 0; idx < counter; ++idx) {
        char *string = (char *) data[idx];
        NSString *str = [NSString stringWithFormat:@"GaiaXJSUI%sModule", string];
        Class cls = NSClassFromString(str);
        if (cls != nil) {
            GaiaXJSModuleInfo *moduleInfo = [[GaiaXJSModuleInfo alloc] init];
            moduleInfo.moduleName = str;
            [self.modulesMap setObject:moduleInfo forKey:@(idx)];
            [self.idsMap setObject:@(idx) forKey:moduleInfo.moduleName];
            NSString *jsName = [GaiaXJSHelper removeGaiaXPrefix:str];
            if (![str isEqualToString:@"GaiaXJSUIViewModule"]) {
                [self.injectProps appendFormat:@"var %@Props = /** @class */ (function (_super) {\
                 __extends(%@Props, _super);\
                 function %@Props() {\
                     return _super.call(this) || this;\
                 }\
                 return %@Props;\
             }(Props));", jsName, jsName, jsName, jsName];
                [self.injectProps appendFormat:@"var %@Style = /** @class */ (function (_super) {\
                 __extends(%@Style, _super);\
                 function %@Style() {\
                     return _super.call(this) || this;\
                 }\
                 return %@Style;\
             }(Style));", jsName, jsName, jsName, jsName];
                if (isDebugging) {
                    [self.rdInjectProps appendFormat:@"class %@Props extends Props {}; ", jsName];
                    [self.rdInjectProps appendFormat:@"class %@Style extends Style {}; ", jsName];
                }
            } else {
                [self.injectProps appendFormat:@"var Style = /** @class */ (function () {\
                 function Style(data) {\
                     this.__data__ = __assign({}, data);\
                 }\
                 Object.defineProperty(Style.prototype, \"targetData\", {\
                     get: function () {\
                         return this.__data__;\
                     },\
                     enumerable: true,\
                     configurable: true\
                 });\
                 return Style;\
             }());\
             var Props = /** @class */ (function () {\
                 function Props() {\
                 }\
                 return Props;\
                 }());"];
                if (isDebugging) {
                    [self.rdInjectProps appendFormat:@"class Props {}; "];
                    [self.rdInjectProps appendFormat:@"class Style { targetData:any }; "];
                }
            }
            unsigned int methodCount;
            Method *methods = class_copyMethodList(object_getClass(cls), &methodCount);
            moduleInfo.methodsMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
            moduleInfo.idsMap = [[NSMapTable alloc] initWithKeyOptions:NSPointerFunctionsStrongMemory valueOptions:NSPointerFunctionsStrongMemory capacity:20];
            for (NSUInteger i = 0; i < methodCount; i++) {
                Method method = methods[i];
                SEL selector = method_getName(method);
                NSString *selectorString = NSStringFromSelector(selector);
                if ([selectorString hasPrefix:@"gaiaxjs_propConfig_"]) {
                    [self createPropBlock:[selectorString substringFromIndex:@"gaiaxjs_propConfig_".length] class:cls selector:selector];
                    [self.injectProps appendFormat:@"%@\r\n", [GaiaXJSHelper generateJSPropsStringWithClass:[jsName isEqualToString:@"View"] ? @"" : jsName key:[selectorString substringFromIndex:@"gaiaxjs_propConfig_".length]]];
                    if (isDebugging) {
                        [self.rdInjectProps appendFormat:@"%@\r\n", [GaiaXJSHelper generateJSPropsStringWithClass:[jsName isEqualToString:@"View"] ? @"" : jsName key:[selectorString substringFromIndex:@"gaiaxjs_propConfig_".length]]];
                    }
                } else if ([selectorString hasPrefix:@"gaiaxjs_styleConfig_"]) {
                    [self createStyleBlock:[selectorString substringFromIndex:@"gaiaxjs_styleConfig_".length] class:cls selector:selector];
                    [self.injectProps appendFormat:@"%@\r\n", [GaiaXJSHelper generateJSStyleStringWithClass:[jsName isEqualToString:@"View"] ? @"" : jsName key:[selectorString substringFromIndex:@"gaiaxjs_styleConfig_".length]]];
                    if (isDebugging) {
                        [self.rdInjectProps appendFormat:@"%@\r\n", [GaiaXJSHelper generateJSStyleStringWithClass:[jsName isEqualToString:@"View"] ? @"" : jsName key:[selectorString substringFromIndex:@"gaiaxjs_styleConfig_".length]]];
                    }
                }
            }
            free(methods);
        }
    }
}

- (void)registerModules {
    [self registerModulesWithEngineType:GaiaXJSEngineTypeJSC];
}

- (GaiaXJSPropBlock)createPropBlock:(NSString *)name class:(Class)class selector:(SEL)selector {
    return nil;
}

- (GaiaXJSStyleBlock)createStyleBlock:(NSString *)name class:(Class)class selector:(SEL)selector {
    SEL type = NULL;
    NSString *keyPath = nil;
    NSArray<NSString *> *typeAndKeyPath = ((NSArray<NSString *> *(*)(id, SEL)) objc_msgSend)(class, selector);
    type = selectorForType(typeAndKeyPath[0]);
    keyPath = typeAndKeyPath.count > 1 ? typeAndKeyPath[1] : nil;
    NSString *key = name;
    NSArray<NSString *> *parts = [keyPath componentsSeparatedByString:@"."];
    if (parts) {
        key = parts.lastObject;
        parts = [parts subarrayWithRange:(NSRange) {0, parts.count - 1}];
    }

    // Get property getter
    SEL getter = NSSelectorFromString(key);

    // Get property setter
    SEL setter = NSSelectorFromString(
            [NSString stringWithFormat:@"set%@%@:", [key substringToIndex:1].uppercaseString, [key substringFromIndex:1]]);

    // Build setter block
    void (^setterBlock)(id target, id json) = nil;

    NSMethodSignature *typeSignature = [[GaiaXJSConvert class] methodSignatureForSelector:type];
    if (!typeSignature) {
        return ^(__unused id view, __unused id json) {
        };
    }
    switch (typeSignature.methodReturnType[0]) {
#define GAIAX_CASE(_value, _type)                                       \
case _value: {                                                      \
__block BOOL setDefaultValue = NO;                                \
__block _type defaultValue;                                       \
_type (*convert)(id, SEL, id) = (typeof(convert))objc_msgSend;    \
_type (*get)(id, SEL) = (typeof(get))objc_msgSend;                \
void (*set)(id, SEL, _type) = (typeof(set))objc_msgSend;          \
setterBlock = ^(id target, id json) {                             \
if (json) {                                                     \
if (!setDefaultValue && target) {                             \
if ([target respondsToSelector:getter]) {                   \
defaultValue = get(target, getter);                       \
}                                                           \
setDefaultValue = YES;                                      \
}                                                             \
set(target, setter, convert([GaiaXJSConvert class], type, json)); \
} else if (setDefaultValue) {                                   \
set(target, setter, defaultValue);                            \
}                                                               \
};                                                                \
break;                                                            \
}
        GAIAX_CASE(_C_SEL, SEL)
        GAIAX_CASE(_C_CHARPTR, const char *)
        GAIAX_CASE(_C_CHR, char)
        GAIAX_CASE(_C_UCHR, unsigned char)
        GAIAX_CASE(_C_SHT, short)
        GAIAX_CASE(_C_USHT, unsigned short)
        GAIAX_CASE(_C_INT, int)
        GAIAX_CASE(_C_UINT, unsigned int)
        GAIAX_CASE(_C_LNG, long)
        GAIAX_CASE(_C_ULNG, unsigned long)
        GAIAX_CASE(_C_LNG_LNG, long long)
        GAIAX_CASE(_C_ULNG_LNG, unsigned long long)
        GAIAX_CASE(_C_FLT, float)
        GAIAX_CASE(_C_DBL, double)
        GAIAX_CASE(_C_BOOL, BOOL)
        GAIAX_CASE(_C_PTR, void *)
        GAIAX_CASE(_C_ID, id)

        case _C_STRUCT_B:
        default: {
            setterBlock = createNSInvocationSetter(typeSignature, type, getter, setter);
            break;
        }
    }
    return ^(__unused id view, __unused id json) {
        // Follow keypath
        id target = view;
        for (NSString *part in parts) {
            target = [target valueForKey:part];
        }

        // Set property with json
        setterBlock(target, GaiaXJSNilIfNull(json));
    };
}

static GaiaXJSPropBlock createNSInvocationSetter(NSMethodSignature *typeSignature, SEL type, SEL getter, SEL setter) {
    return nil;
}

static SEL selectorForType(NSString *type) {
    const char *input = type.UTF8String;
    return NSSelectorFromString([GaiaXJSParseType(&input) stringByAppendingString:@":"]);
}


- (void)setStyleWithContextId:(NSInteger)contextId
                     moduleId:(NSInteger)moduleId
                     methodId:(NSInteger)methodId
                         args:(NSArray *)args {

}

- (void)setPropWithContextId:(NSInteger)contextId
                    moduleId:(NSInteger)moduleId
                    methodId:(NSInteger)methodId
                        args:(NSArray *)args {

}

@end
