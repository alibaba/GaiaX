/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "napi/native_api.h"
#include "GXAnalyze.h"
#include <sys/stat.h>
#include "hilog/log.h"
#include <map>

#undef LOG_DOMAIN
#undef LOG_TAG
#define LOG_DOMAIN 0x3200    // 全局domain宏，标识业务领域
#define LOG_TAG "GX_C++_TAG" // 全局tag宏，标识模块日志tag

static void callJSLog(const char *str) { OH_LOG_INFO(LOG_APP, "[C++ 日志打印] : %{public}s.", str); }

static void callJSErrorLog(const char *str) { OH_LOG_ERROR(LOG_APP, "[C++ 错误打印] : %{public}s.", str); }


static char *transStringFormNapiValue(napi_env env, napi_value jsStr) {
    size_t len = 0;
    napi_get_value_string_utf8(env, jsStr, nullptr, 0, &len); // 获取字符串长度到len
    char *buf = new char[len + 1]; // 分配合适大小的char数组(长度+1，以便为字符串添加终止符'\0')
    napi_get_value_string_utf8(env, jsStr, buf, len + 1, &len); // 获取字符串
    return buf;
}

static float transFloatFormNapiValue(napi_env env, napi_value jsNumber) {
    double doubleResult = 0;
    napi_get_value_double(env, jsNumber, &doubleResult);
    float floatResult = static_cast<float>(doubleResult);
    return floatResult;
}

static float transBoolFormNapiValue(napi_env env, napi_value jsBool) {
    bool result = false;
    napi_get_value_bool(env, jsBool, &result);
    return result;
}

/**
 * 将JS指针转为C++指针，方便进行后续的C++逻辑处理
 * @param env
 * @param JS的对象（napi_value 类型）
 * @return C++指针
 * @description
 * JS对象指针就是napi_ref，在使用时，需要确保JS对象不被释放，refcount设置为1就可以保障。
 * 在进行C++与napi_ref转化时，使用 static_cast<void *> 与 static_cast<napi_ref> 进行转化。
 */
static void *transPTRFormNapiValue(napi_env env, napi_value jsValue) {
    napi_ref jsValueRef = nullptr;
    napi_create_reference(env, jsValue, 1, &jsValueRef);
    void *jsValueRefToVoidPtr = static_cast<void *>(jsValueRef);
    ;
    // 将napi_ref转换为void*
    long address4 = reinterpret_cast<long>(jsValueRefToVoidPtr);
    callJSLog(("address6:" + std::to_string(address4)).c_str());
    return jsValueRefToVoidPtr;
}

static napi_value getPropertyFormJSObject(napi_env env, napi_value jsObject, std::string propertyKeyStr) {
    napi_value propertyKey;
    napi_value result = nullptr;
    napi_create_string_utf8(env, propertyKeyStr.c_str(), propertyKeyStr.size(), &propertyKey);
    napi_status status = napi_get_property(env, jsObject, propertyKey, &result);
    if (status != napi_ok) {
        callJSErrorLog((propertyKeyStr + "取值失败").c_str());
    }
    return result;
}

/**
 * jsGXValue 在 JS层的定义如下
 export class JSGXValue {
  objectValue:object
  numberValue:number
  boolValue:boolean
  stringValue:string
  arrayValue:Array<any>
  valueType:string = JSGXValueType.nil
}
* 类型枚举如下
enum JSGXValueType {
  nil = 'nil',
  object = 'object',
  boolean = 'boolean',
  number = 'number',
  string = 'string',
  array = 'array'
}
 * */
static GXValue *transToGXValue(napi_env env, napi_value jsGXValue) {
    napi_status status;
    napi_value valueTypeKey;
    napi_value jsValueType;
    std::string valueTypeStr = "valueType";
    napi_create_string_utf8(env, valueTypeStr.c_str(), valueTypeStr.size(), &valueTypeKey);
    status = napi_get_property(env, jsGXValue, valueTypeKey, &jsValueType);
    if (status == napi_ok) {
        char *valueType = transStringFormNapiValue(env, jsValueType);
        callJSLog(("valueType" + string(valueType)).c_str());
        if (strcmp(valueType, "string") == 0) {
            callJSLog("log string");
            napi_value propertyValue = getPropertyFormJSObject(env, jsGXValue, "stringValue");
            char *strValue = transStringFormNapiValue(env, propertyValue);
            return GX_NewGXString(strValue);
        }
        if (strcmp(valueType, "number") == 0) {
            callJSLog("log number");
            napi_value propertyValue = getPropertyFormJSObject(env, jsGXValue, "numberValue");
            float floatValue = transFloatFormNapiValue(env, propertyValue);
            return GX_NewFloat64(floatValue);
        }
        if (strcmp(valueType, "boolean") == 0) {
            callJSLog("log boolean");
            napi_value propertyValue = getPropertyFormJSObject(env, jsGXValue, "boolValue");
            bool boolValue = transBoolFormNapiValue(env, propertyValue);
            return GX_NewBool(boolValue);
        }
        if (strcmp(valueType, "object") == 0 || strcmp(valueType, "array") == 0) {
            const char *keyStr = strcmp(valueType, "object") == 0 ? "objectValue" : "arrayValue";
            napi_value propertyValue = getPropertyFormJSObject(env, jsGXValue, keyStr);
            void *valuePtr = transPTRFormNapiValue(env, propertyValue);
            GXValue *result = strcmp(valueType, "object") == 0 ? GX_NewMap(valuePtr) : GX_NewArray(valuePtr);
            return result;
        }
    }
    callJSErrorLog("valueType：取值失败");
    return GX_NewNull(0);
}

//====================TOOL - getSourceValue start ==========
struct GetSourceValueInjectedData {
    napi_env callbackEnv = nullptr;
    napi_ref callbackRef = nullptr;
};

static GetSourceValueInjectedData *getSourceValueInjectedData = nullptr;

static long CallJSGetSourceValue(const char *valuePath, void *source) {
    if (getSourceValueInjectedData == nullptr) {
        return 0;
    }
    napi_value jsGetSourceValue = nullptr;
    napi_get_reference_value(getSourceValueInjectedData->callbackEnv, getSourceValueInjectedData->callbackRef,
                             &jsGetSourceValue);
    // 创建GetSourceValue的js方法参数
    // 1.valuePath字符串
    napi_value funcArgs[2] = {nullptr};
    napi_create_string_utf8(getSourceValueInjectedData->callbackEnv, valuePath, NAPI_AUTO_LENGTH, &funcArgs[0]);
    // 2.将void*转换将napi_ref
    napi_ref *jsSourceRef = static_cast<napi_ref *>(source);
    napi_status getRefValueStatus =
        napi_get_reference_value(getSourceValueInjectedData->callbackEnv, *jsSourceRef, &funcArgs[1]);
    // 执行回调函数
    napi_value result;
    napi_value undefined;
    napi_get_undefined(getSourceValueInjectedData->callbackEnv, &undefined);
    napi_call_function(getSourceValueInjectedData->callbackEnv, undefined, jsGetSourceValue, 2, funcArgs, &result);
    // 获取回调函数的返回值，返回值为通用的 JSGXValue
    GXValue *gXValue = transToGXValue(getSourceValueInjectedData->callbackEnv, result);
    // 华为遵循 LP64 模型（Long和Pointer为64位），这意味着 long 类型和指针都是 64 位宽的
    long address = reinterpret_cast<long>(gXValue);
    return address;
}

napi_value InjectGetSourceValue(napi_env env, napi_callback_info info) {
    if (getSourceValueInjectedData != nullptr) {
        return nullptr;
    }
    size_t argc = 1;
    napi_value args[1];
    napi_get_cb_info(env, info, &argc, args, nullptr, nullptr);
    getSourceValueInjectedData = new GetSourceValueInjectedData();
    // 将传入的callback转换为napi_ref延长其生命周期，防止被GC掉
    getSourceValueInjectedData->callbackEnv = env;
    napi_create_reference(env, args[0], 1, &getSourceValueInjectedData->callbackRef);
    return nullptr;
}
//====================TOOL - getSourceValue end ============


//====================TOOL - getFunctionValue start ========
struct GetFunctionValueInjectedData {
    napi_env callbackEnv = nullptr;
    napi_ref callbackRef = nullptr;
};

static GetFunctionValueInjectedData *getFunctionValueInjectedData = nullptr;

static long CallJSGetFunctionValue(const char *funName, long *paramPointers, int paramsSize) {
    if (getFunctionValueInjectedData == nullptr) {
        return 0;
    }
    napi_value jsLogFunc = nullptr;
    napi_get_reference_value(getFunctionValueInjectedData->callbackEnv, getFunctionValueInjectedData->callbackRef,
                             &jsLogFunc);
    // 创建GetSourceValue的js方法参数
    // 1.valuePath字符串
    napi_value funcArgs[3] = {nullptr};
    napi_create_string_utf8(getFunctionValueInjectedData->callbackEnv, funName, NAPI_AUTO_LENGTH, &funcArgs[0]);
    // 2.paramPointers（GXValue的数组）
    napi_create_array(getFunctionValueInjectedData->callbackEnv, &funcArgs[1]);
    for (int index = 0; index < paramsSize; index++) {
        napi_value element = nullptr;
        GXValue *gXValue = reinterpret_cast<GXValue *>(paramPointers[index]);
        switch (gXValue->tag) {
        case GX_TAG_BOOL: {
            napi_get_boolean(getFunctionValueInjectedData->callbackEnv, gXValue->int32, &element);
            callJSLog("CallJSGetFunctionValue 方法 GX_TAG_BOOL");
        } break;
        case GX_TAG_FLOAT: {
            napi_create_double(getFunctionValueInjectedData->callbackEnv, gXValue->float64, &element);
            callJSLog("CallJSGetFunctionValue 方法 GX_TAG_FLOAT");
        } break;
        case GX_TAG_STRING: {
            napi_create_string_utf8(getFunctionValueInjectedData->callbackEnv, gXValue->str, NAPI_AUTO_LENGTH,
                                    &element);
            callJSLog("CallJSGetFunctionValue 方法 GX_TAG_STRING");
        } break;
        case GX_TAG_ARRAY: {
            napi_ref jsResultRef = static_cast<napi_ref>(gXValue->ptr);
            napi_status status =
                napi_get_reference_value(getFunctionValueInjectedData->callbackEnv, jsResultRef, &element);
            if (status != napi_ok) {
                callJSErrorLog("CallJSGetFunctionValue 方法 napi_get_reference_value 取值失败");
            }
        } break;
        case GX_TAG_OBJECT: {
            napi_ref resultRef = static_cast<napi_ref>(gXValue->ptr);
            napi_status status =
                napi_get_reference_value(getFunctionValueInjectedData->callbackEnv, resultRef, &element);
            if (status != napi_ok) {
                callJSErrorLog("CallJSGetFunctionValue 方法 napi_get_reference_value 取值失败");
            }
        } break;
        default:
            callJSErrorLog("CallJSGetFunctionValue 方法 解析参数异常");
            break;
        }
        napi_set_element(getFunctionValueInjectedData->callbackEnv, funcArgs[1], index, element);
    }
    // 3.paramsSize (GXValue的数组个数)
    napi_create_double(getFunctionValueInjectedData->callbackEnv, paramsSize, &funcArgs[2]);
    // 调用JS方法透传数据
    napi_value jsResult;
    napi_value undefined;
    napi_get_undefined(getFunctionValueInjectedData->callbackEnv, &undefined);
    napi_call_function(getFunctionValueInjectedData->callbackEnv, undefined, jsLogFunc, 3, funcArgs, &jsResult);
    // 获取回调函数的返回值，返回值为通用的 JSGXValue
    GXValue *gXValue = transToGXValue(getSourceValueInjectedData->callbackEnv, jsResult);
    // 华为遵循 LP64 模型（Long和Pointer为64位），这意味着 long 类型和指针都是 64 位宽的
    long address = reinterpret_cast<long>(gXValue);
    return address;
}

napi_value InjectGetFunctionValue(napi_env env, napi_callback_info info) {
    if (getFunctionValueInjectedData != nullptr) {
        return nullptr;
    }
    size_t argc = 1;
    napi_value args[1];
    napi_get_cb_info(env, info, &argc, args, nullptr, nullptr);
    getFunctionValueInjectedData = new GetFunctionValueInjectedData();
    // 将传入的callback转换为napi_ref延长其生命周期，防止被GC掉
    getFunctionValueInjectedData->callbackEnv = env;
    napi_create_reference(env, args[0], 1, &getFunctionValueInjectedData->callbackRef);
    return nullptr;
}
//====================TOOL - getFunctionValue end ==========

//===============GXAnalyzeImpl start===============

class GXAnalyzeImpl : public GXAnalyze {

public:
    // 设置self
    static GXAnalyzeImpl *sharedInstance(void);

    // 解析取值
    long getSourceValue(string valuePath, void *source);

    // 解析方法
    long getFunctionValue(string funName, long *paramPointers, int paramsSize, string source);

    // 异常抛出
    void throwError(string message);

private:
    GXAnalyzeImpl *self;
    bool isInit = false;
};


static GXAnalyzeImpl *staticInstance;
static bool isImplInit = false;

GXAnalyzeImpl *GXAnalyzeImpl::sharedInstance(void) {
    if (isImplInit == false) {
        staticInstance = new GXAnalyzeImpl();
    }
    return staticInstance;
}

long GXAnalyzeImpl::getFunctionValue(string funName, long *paramPointers, int paramsSize, string source) {
    return CallJSGetFunctionValue(funName.c_str(), paramPointers, paramsSize);
    return 0;
}

long GXAnalyzeImpl::getSourceValue(string valuePath, void *source) {
    std::string str1 = "getSourceValue(),";
    std::string str2 = valuePath;
    return CallJSGetSourceValue(valuePath.c_str(), source);
    return 0;
}

void GXAnalyzeImpl::throwError(string message) {}

//===============GXAnalyzeImpl end ================


//===============GXUse start ======================
static napi_value GXGetValue(napi_env env, napi_callback_info info) {
    // 三个参数
    size_t argc = 3;
    napi_value args[3] = {nullptr};
    napi_get_cb_info(env, info, &argc, args, nullptr, nullptr);

    napi_value expression = args[0];
    napi_value source = args[1];

    size_t len = 0;
    napi_get_value_string_utf8(env, expression, nullptr, 0, &len); // 获取字符串长度到len
    char *buf = new char[len + 1]; // 分配合适大小的char数组(长度+1，以便为字符串添加终止符'\0')
    napi_get_value_string_utf8(env, expression, buf, len + 1, &len); // 获取字符串
    std::string token = std::string(buf);
    callJSLog(("取值表达式：取值路径→ " + token).c_str());
    napi_ref jsSourceRef = nullptr;
    napi_status toRefStatus = napi_create_reference(env, source, 1, &jsSourceRef);

    // 将napi_ref转换为void*
    void *jsSourceRefToVoidPtr = static_cast<void *>(&jsSourceRef);
    long cppResult = GXAnalyzeImpl::sharedInstance()->getValue(token, jsSourceRefToVoidPtr);
    napi_value result;
    GXValue *gXValue = new GXValue(GX_TAG_NULL, 0);
    if (cppResult == 0) {
        // 返回的是无效的地址，后续无法再进行地址反解析，当前取值失败
        callJSLog("返回的是无效的地址，后续无法再进行地址反解析,直接返回token");
        napi_create_string_utf8(env, token.c_str(), NAPI_AUTO_LENGTH, &result);
        callJSLog("GX_TAG_STRING");
        return result;
    } else {
        gXValue = reinterpret_cast<GXValue *>(cppResult);
        callJSLog(("cppResult 获取成功, tag:" + std::to_string(gXValue->tag)).c_str());
    }
    switch (gXValue->tag) {
    case GX_TAG_BOOL: {
        napi_get_boolean(env, gXValue->int32, &result);
        callJSLog("GX_TAG_BOOL");
        return result;
    } break;
    case GX_TAG_FLOAT: {
        napi_create_double(env, gXValue->float64, &result);
        callJSLog("GX_TAG_FLOAT");
        return result;
    } break;
    case GX_TAG_STRING: {
        napi_create_string_utf8(env, gXValue->str, NAPI_AUTO_LENGTH, &result);
        callJSLog("GX_TAG_STRING");
        return result;
    } break;
    case GX_TAG_ARRAY: {
        napi_ref jsResultRef = static_cast<napi_ref>(gXValue->ptr);
        napi_status status = napi_get_reference_value(env, jsResultRef, &result);
        if (status != napi_ok) {
            callJSErrorLog("napi_get_reference_value 取值失败");
        }
        return result;
    } break;
    case GX_TAG_OBJECT: {
        napi_ref resultRef = static_cast<napi_ref>(gXValue->ptr);
        napi_status status = napi_get_reference_value(env, resultRef, &result);
        if (status != napi_ok) {
            callJSErrorLog("napi_get_reference_value 取值失败");
        }
        return result;
    } break;
    case GX_TAG_MAP: {
        napi_ref resultRef = static_cast<napi_ref>(gXValue->ptr);
        napi_status status = napi_get_reference_value(env, resultRef, &result);
        if (status != napi_ok) {
            callJSErrorLog("napi_get_reference_value 取值失败");
        }
        return result;
    }
    default:
        break;
    }
    callJSErrorLog("类型异常，取值失败");
    napi_create_int64(env, 0, &result);
    return result;
}
//===============GXUse end ========================

EXTERN_C_START
static napi_value Init(napi_env env, napi_value exports) {
    napi_property_descriptor desc[] = {
        {"gxAnalyzeGetValue", nullptr, GXGetValue, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"gxInjectGetSourceValue", nullptr, InjectGetSourceValue, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"gxInjectGetFunctionValue", nullptr, InjectGetFunctionValue, nullptr, nullptr, nullptr, napi_default,
         nullptr}};
    napi_define_properties(env, exports, sizeof(desc) / sizeof(desc[0]), desc);
    return exports;
}
EXTERN_C_END

static napi_module analysiscoreModule = {
    .nm_version = 1,
    .nm_flags = 0,
    .nm_filename = nullptr,
    .nm_register_func = Init,
    .nm_modname = "GXAnalyze",
    .nm_priv = ((void *)0),
    .reserved = {0},
};

extern "C" __attribute__((constructor)) void RegisterEntryModule(void) { napi_module_register(&analysiscoreModule); }
