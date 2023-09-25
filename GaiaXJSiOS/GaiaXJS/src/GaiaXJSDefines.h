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

#ifndef GaiaXJSDefines_h
#define GaiaXJSDefines_h

typedef struct GaiaXJSExportedMethod {
  const char *const objcName;
  const BOOL isSync;
} GaiaXJSExportedMethod;

typedef NS_ENUM(NSUInteger, GaiaXJSEngineType) {
    GaiaXJSEngineTypeJSC,
    GaiaXJSEngineTypeQuickJS,
    GaiaXJSEngineTypeDebugger
};


typedef NS_ENUM(NSUInteger, GaiaXJSMethodType) {
    GaiaXJSMethodTypeSync,
    GaiaXJSMethodTypeAsync,
    GaiaXJSMethodTypePromise,
};

typedef NS_ENUM(NSUInteger, GaiaXJSEventType) {
    GaiaXJSEventTypeClick,
    GaiaXJSEventTypeLongPress,
    GaiaXJSEventTypeDoubleClick,
    GaiaXJSEventTypeSwipe,
    GaiaXJSEventTypePinch
};


typedef NS_ENUM(NSInteger, GaiaXJSExecPhase)  {
    //JS NativeModules
    GaiaXJSExecPhaseWillStartLoadNativeModules,
    GaiaXJSExecPhaseDidEndLoadNativeModules,
    
    //JS Context
    GaiaXJSExecPhaseWillStartCreateContext,
    GaiaXJSExecPhaseDidEndCreateContext,
    
    //JS Library
    GaiaXJSExecPhaseWillStartLoadJSLibrary,
    GaiaXJSExecPhaseDidEndLoadJSLibrary,
    
    //IndexJS
    GaiaXJSExecPhaseWillStartLoadIndexJS,
    GaiaXJSExecPhaseDidEndLoadIndexJS,
    
    //JSAPI
    GaiaXJSExecPhaseWillStartInvokeSyncMethod,
    GaiaXJSExecPhaseDidEndInvokeSyncMethod,
    GaiaXJSExecPhaseWillStartInvokeAsyncMethod,
    GaiaXJSExecPhaseDidEndInvokeAsyncMethod,
    GaiaXJSExecPhaseWillStartInvokePromiseMethod,
    GaiaXJSExecPhaseDidEndInvokePromiseMethod,
};

typedef NS_ENUM(NSInteger, GaiaXJSInvokeMethodSubPhase) {
    GaiaXJSInvokeMethodSubPhaseJSToContext,
    GaiaXJSInvokeMethodSubPhaseContextToReturn,
    GaiaXJSInvokeMethodSubPhaseReturnToContext
};


#define GaiaXJSQueuePrefix @"com.gaiaxjs.queue"
#define GaiaXJSDebugQueuePrefix @"com.gaiaxjs.debugger.queue"

#define GAIAXJS_DEBUGGER_BIZ_ID @"GAIAXJS_DEBUGGER"

typedef void (^GaiaXJSCallbackBlock)(id result);

typedef void (^GaiaXJSPromiseResolveBlock)(id result);

typedef void (^GaiaXJSPromiseRejectBlock)(NSString *code, NSString *message);

typedef void (^GaiaXJSPropBlock)(id view, id json);

typedef void (^GaiaXJSStyleBlock)(id view, id json);

#define GaiaXJSSafeString(value) ([value length] == 0 ? @"" : value)

#define GaiaXJSNullIfNil(value) ((value) ?: (id)kCFNull)
#define GaiaXJSNilIfNull(value)                           \
  ({                                                  \
    __typeof__(value) t = (value);                    \
    (id) t == (id)kCFNull ? (__typeof(value))nil : t; \
  })


#if !defined GAIAXJS_DYNAMIC
#if __has_attribute(objc_dynamic)
#define GAIAXJS_DYNAMIC __attribute__((objc_dynamic))
#else
#define GAIAXJS_DYNAMIC
#endif
#endif

#define GAIAXJS_CONCAT2(A, B) A##B
#define GAIAXJS_CONCAT(A, B) GAIAXJS_CONCAT2(A, B)

#define GAIAXJS_EXPORT_MODULE(name) \
char * gaiaxjs_##name##_module __attribute((used, section("__DATA, __gaiaxjs"))) = ""#name"";

#define GAIAXJS_EXPORT_UI_MODULE(name) \
char * gaiaxjs_##name##_ui_module __attribute((used, section("__DATA, __gaiaxjs"))) = ""#name"";

#define GAIAXJS_EXPORT_UI_PROPERTY(name, type)                \
  +(NSArray<NSString *> *)gaiaxjs_propConfig_##name GAIAXJS_DYNAMIC \
  {                                                           \
    return @[ @ #type ];                                      \
  }

#define GAIAXJS_EXPORT_UI_STYLE(name, type)                \
  +(NSArray<NSString *> *)gaiaxjs_styleConfig_##name GAIAXJS_DYNAMIC \
  {                                                           \
    return @[ @ #type ];                                      \
  }


#define _GAIAXJS_EXTERN_REMAP_METHOD(method, is_synchronous_method)                            \
+(const GaiaXJSExportedMethod *)GAIAXJS_CONCAT(__gaiaxjs_export__,GAIAXJS_CONCAT(__LINE__,__COUNTER__)) \
{ \
    static GaiaXJSExportedMethod config = {#method, is_synchronous_method};                       \
    return &config;                                                                           \
}

#define GAIAXJS_EXPORT_SYNC_METHOD(returnType, method) \
  _GAIAXJS_EXTERN_REMAP_METHOD(method, YES) \
  -(returnType)method GAIAXJS_DYNAMIC

#define GAIAXJS_EXPORT_ASYNC_METHOD(method) \
  _GAIAXJS_EXTERN_REMAP_METHOD(method, NO) \
  -(void)method GAIAXJS_DYNAMIC

#define GAIAXJS_EXPORT_EXTERN_MODULE(name, objc_name, objc_supername) \
objc_name:                                                        \
objc_supername @                                                  \
end @interface objc_name(GaiaXJSExternModule)                         \
@end                                                              \
@implementation objc_name (GaiaXJSExternModule)                       \
char * gaiaxjs_##name##_module __attribute((used, section("__DATA, __gaiaxjs"))) = ""#name""; \

#define GAIAXJS_EXPORT_EXTERN_SYNC_METHOD(returnType, method) \
  _GAIAXJS_EXTERN_REMAP_METHOD(method, YES) \

#define GAIAXJS_EXPORT_EXTERN_ASYNC_METHOD(method) \
  _GAIAXJS_EXTERN_REMAP_METHOD(method, NO) \


#define GAIAXJS_CONVERTER(type, name, getter) GAIAXJS_CUSTOM_CONVERTER(type, name, [json getter])

#define GAIAXJS_CUSTOM_CONVERTER(type, name, code) \
  +(type)name : (id)json GAIAXJS_DYNAMIC           \
  {                                            \
    return code;                             \
  } \

#define GAIAXJS_NUMBER_CONVERTER(type, getter) \
    GAIAXJS_CUSTOM_CONVERTER(type, type, [json getter])

typedef NS_ENUM(NSUInteger, GaiaXJSNullability) {
    GaiaXJSNullabilityUnspecified,
    GaiaXJSNullable,
    GaiaXJSNonnullable,
};


#define __GAIAXJS_PRIMITIVE_CASE(_type, _nullable)                                                \
  {                                                                                       \
    isNullableType = _nullable;                                                           \
    _type (*convert)(id, SEL, id) = (__typeof__(convert))objc_msgSend;                    \
    [argumentBlocks addObject:^(NSUInteger index, id json) { \
      _type value = convert([GaiaXJSConvert class], selector, json);                          \
      [invocation setArgument:&value atIndex:(index) + 2];                                \
      return YES;                                                                         \
    }];                                                                                   \
    break;                                                                                \
  }

#define GAIAXJS_PRIMITIVE_CASE(_type) __GAIAXJS_PRIMITIVE_CASE(_type, NO)


// Convert nil values to NSNull, and vice-versa
#define GAIAXJSNullIfNil(value) ((value) ?: (id)kCFNull)
#define GAIAXJSNilIfNull(value)                           \
  ({                                                  \
    __typeof__(value) t = (value);                    \
    (id) t == (id)kCFNull ? (__typeof(value))nil : t; \
  })



// Explicitly copy the block
#define __COPY_BLOCK(block...)         \
  id value = [block copy];             \
  if (value) {                         \
    [self.retainedObjects addObject:value]; \
  }

#define GAIAXJS_RETAINED_ARG_BLOCK(_logic)                                                         \
  [argumentBlocks addObject:^( NSUInteger index, id json) { \
    _logic [invocation setArgument:&value atIndex:(index) + 2];                                \
    if (value) {                                                                               \
      [self.retainedObjects addObject:value];                                                       \
    }                                                                                          \
    return YES;                                                                                \
  }]

#define GAIAXJS_BLOCK_CASE(_block_args, _block)             \
  GAIAXJS_RETAINED_ARG_BLOCK(__COPY_BLOCK(^_block_args{ \
      _block});)

#endif /* GaiaXJSDefines_h */
