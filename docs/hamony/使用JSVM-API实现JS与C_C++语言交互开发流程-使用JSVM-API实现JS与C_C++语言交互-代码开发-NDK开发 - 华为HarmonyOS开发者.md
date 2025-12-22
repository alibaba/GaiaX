# 使用JSVM-API实现JS与C/C++语言交互开发流程-使用JSVM-API实现JS与C/C++语言交互-代码开发-NDK开发 - 华为HarmonyOS开发者

**源地址**: https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-process

---



使用JSVM-API实现跨语言交互，首先需按其机制注册和加载模块。

  * ArkTS/JS侧：实现C++方法的调用。代码比较简单，import一个对应的so库后，即可调用C++方法。

  * Native侧：.cpp文件，实现模块的注册。需要提供注册lib库的名称，并在注册回调方法中定义接口的映射关系，即Native方法及对应的JS/ArkTS接口名称等。




此处以在ArkTS/JS侧和Native侧实现RunJsVm()接口实现跨语言交互为例，展示使用JSVM-API进行跨语言交互的流程。

## 创建Native C++工程 __

具体见[创建NDK工程](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/create-with-ndk)

## Native侧方法的实现 __

参考[使用Node-API实现跨语言交互开发流程](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-napi-process#native侧方法的实现)，以下代码提供了“使用JSVM-API实现JS与C/C++语言交互开发流程”Native侧方法实现的一个demo。

  * 在index.d.ts文件中，提供JS侧的接口方法。

收起

自动换行

深色代码主题

复制
        
                    1. // entry/src/main/cpp/types/libentry/index.d.ts
            2. export const runTest: () => void;

  * 在oh-package.json5文件中将index.d.ts与cpp文件关联起来。

收起

自动换行

深色代码主题

复制
        
                    1. {
            2.   "name": "libentry.so",
            3.   "types": "./index.d.ts",
            4.   "version": "",
            5.   "description": "Please describe the basic information."
            6. }

  * 在CMakeLists.txt文件中配置CMake打包参数。

收起

自动换行

深色代码主题

复制
        
                    1. # entry/src/main/cpp/CMakeLists.txt
            2. cmake_minimum_required(VERSION 3.4.1)
            3. project(JSVMDemo)
            4. 
        
            5. set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})
            6. # 日志打印配置
            7. add_definitions( "-DLOG_DOMAIN=0xd0d0" )
            8. add_definitions( "-DLOG_TAG=\"testTag\"" )
            9. include_directories(${NATIVERENDER_ROOT_PATH}
            10.                     ${NATIVERENDER_ROOT_PATH}/include)
            11. 
        
            12. # 添加名为entry的库
            13. add_library(entry SHARED hello.cpp)
            14. # 构建此可执行文件需要链接的库
            15. target_link_libraries(entry PUBLIC libace_napi.z.so libjsvm.so libhilog_ndk.z.so)

  * 新建entry/src/main/cpp/hello.cpp，实现Native侧的runTest接口。具体代码如下：

收起

自动换行

深色代码主题

复制
        
                    1. // entry/src/main/cpp/hello.cpp
            2. #include "napi/native_api.h"
            3. #include "hilog/log.h"
            4. #include "ark_runtime/jsvm.h"
            5. 
        
            6. #define LOG_DOMAIN 0x3200
            7. #define LOG_TAG "APP"
            8. 
        
            9. static int g_aa = 0;
            10. 
        
            11. #define CHECK_RET(theCall)                                                                                             \
            12.     do {                                                                                                               \
            13.         JSVM_Status cond = theCall;                                                                                    \
            14.         if ((cond) != JSVM_OK) {                                                                                       \
            15.             const JSVM_ExtendedErrorInfo *info;                                                                        \
            16.             OH_JSVM_GetLastErrorInfo(env, &info);                                                                      \
            17.             OH_LOG_ERROR(LOG_APP, "jsvm fail file: %{public}s line: %{public}d ret = %{public}d message = %{public}s", \
            18.                          __FILE__, __LINE__, cond, info != nullptr ? info->errorMessage : "");                         \
            19.             return -1;                                                                                                 \
            20.         }                                                                                                              \
            21.     } while (0)
            22. 
        
            23. #define CHECK(theCall)                                                                                                 \
            24.     do {                                                                                                               \
            25.         JSVM_Status cond = theCall;                                                                                    \
            26.         if ((cond) != JSVM_OK) {                                                                                       \
            27.             OH_LOG_ERROR(LOG_APP, "jsvm fail file: %{public}s line: %{public}d ret = %{public}d", __FILE__, __LINE__,  \
            28.                          cond);                                                                                        \
            29.             return -1;                                                                                                 \
            30.         }                                                                                                              \
            31.     } while (0)
            32. 
        
            33. // 用于调用theCall并检查其返回值是否为JSVM_OK。
            34. // 如果不是，则调用OH_JSVM_GetLastErrorInfo处理错误并返回retVal。
            35. #define JSVM_CALL_BASE(env, theCall, retVal)                                                                           \
            36.     do {                                                                                                               \
            37.         JSVM_Status cond = theCall;                                                                                    \
            38.         if (cond != JSVM_OK) {                                                                                         \
            39.             const JSVM_ExtendedErrorInfo *info;                                                                        \
            40.             OH_JSVM_GetLastErrorInfo(env, &info);                                                                      \
            41.             OH_LOG_ERROR(LOG_APP, "jsvm fail file: %{public}s line: %{public}d ret = %{public}d message = %{public}s", \
            42.                          __FILE__, __LINE__, cond, info != nullptr ? info->errorMessage : "");                         \
            43.             return retVal;                                                                                             \
            44.         }                                                                                                              \
            45.     } while (0)
            46. 
        
            47. // JSVM_CALL_BASE的简化版本，返回nullptr
            48. #define JSVM_CALL(theCall) JSVM_CALL_BASE(env, theCall, nullptr)
            49. 
        
            50. // OH_JSVM_StrictEquals的样例方法
            51. static JSVM_Value IsStrictEquals(JSVM_Env env, JSVM_CallbackInfo info) {
            52.     // 接受两个入参
            53.     size_t argc = 2;
            54.     JSVM_Value args[2] = {nullptr};
            55.     JSVM_CALL(OH_JSVM_GetCbInfo(env, info, &argc, args, nullptr, nullptr));
            56.     // 调用OH_JSVM_StrictEquals接口判断给定的两个JavaScript value是否严格相等
            57.     bool result = false;
            58.     JSVM_Status status = OH_JSVM_StrictEquals(env, args[0], args[1], &result);
            59.     if (status != JSVM_OK) {
            60.         OH_LOG_ERROR(LOG_APP, "JSVM OH_JSVM_StrictEquals: failed");
            61.     } else {
            62.         OH_LOG_INFO(LOG_APP, "JSVM OH_JSVM_StrictEquals: success: %{public}d", result);
            63.     }
            64.     JSVM_Value isStrictEqual;
            65.     JSVM_CALL(OH_JSVM_GetBoolean(env, result, &isStrictEqual));
            66.     return isStrictEqual;
            67. }
            68. // IsStrictEquals注册回调
            69. static JSVM_CallbackStruct param[] = {
            70.     {.data = nullptr, .callback = IsStrictEquals},
            71. };
            72. static JSVM_CallbackStruct *method = param;
            73. // IsStrictEquals方法别名，供JS调用
            74. static JSVM_PropertyDescriptor descriptor[] = {
            75.     {"isStrictEquals", nullptr, method++, nullptr, nullptr, nullptr, JSVM_DEFAULT},
            76. };
            77. // 样例测试js
            78. const char *srcCallNative = R"JS(    let data = '123';
            79.     let value = 123;
            80.     isStrictEquals(data,value);)JS";
            81. 
        
            82. static int32_t TestJSVM() {
            83.     JSVM_InitOptions initOptions = {0};
            84.     JSVM_VM vm;
            85.     JSVM_Env env = nullptr;
            86.     JSVM_VMScope vmScope;
            87.     JSVM_EnvScope envScope;
            88.     JSVM_HandleScope handleScope;
            89.     JSVM_Value result;
            90.     // 初始化JavaScript引擎实例
            91.     if (g_aa == 0) {
            92.         g_aa++;
            93.        CHECK(OH_JSVM_Init(&initOptions));
            94.     }
            95.     // 创建JSVM环境
            96.     CHECK(OH_JSVM_CreateVM(nullptr, &vm));
            97.     CHECK(OH_JSVM_CreateEnv(vm, sizeof(descriptor) / sizeof(descriptor[0]), descriptor, &env));
            98.     CHECK(OH_JSVM_OpenVMScope(vm, &vmScope));
            99.     CHECK_RET(OH_JSVM_OpenEnvScope(env, &envScope));
            100.     CHECK_RET(OH_JSVM_OpenHandleScope(env, &handleScope));
            101. 
        
            102.     // 通过script调用测试函数
            103.     JSVM_Script script;
            104.     JSVM_Value jsSrc;
            105.     CHECK_RET(OH_JSVM_CreateStringUtf8(env, srcCallNative, JSVM_AUTO_LENGTH, &jsSrc));
            106.     CHECK_RET(OH_JSVM_CompileScript(env, jsSrc, nullptr, 0, true, nullptr, &script));
            107.     CHECK_RET(OH_JSVM_RunScript(env, script, &result));
            108. 
        
            109.     // 销毁JSVM环境
            110.     CHECK_RET(OH_JSVM_CloseHandleScope(env, handleScope));
            111.     CHECK_RET(OH_JSVM_CloseEnvScope(env, envScope));
            112.     CHECK(OH_JSVM_CloseVMScope(vm, vmScope));
            113.     CHECK(OH_JSVM_DestroyEnv(env));
            114.     CHECK(OH_JSVM_DestroyVM(vm));
            115.     return 0;
            116. }
            117. 
        
            118. static napi_value RunTest(napi_env env, napi_callback_info info)
            119. {
            120.     TestJSVM();
            121.     return nullptr;
            122. }
            123. 
        
            124. // 模块初始化
            125. EXTERN_C_START
            126. static napi_value Init(napi_env env, napi_value exports) {
            127.     // 实现ArkTS接口与C++接口的绑定和映射
            128.     napi_property_descriptor desc[] = {
            129.       {"runTest", nullptr, RunTest, nullptr, nullptr, nullptr, napi_default, nullptr}
            130.     };
            131.     // 在exports对象上挂载RunJsVm的Native方法
            132.     napi_define_properties(env, exports, sizeof(desc) / sizeof(desc[0]), desc);
            133.     return exports;
            134. }
            135. EXTERN_C_END
            136. 
        
            137. static napi_module demoModule = {
            138.     .nm_version = 1,
            139.     .nm_flags = 0,
            140.     .nm_filename = nullptr,
            141.     .nm_register_func = Init,
            142.     .nm_modname = "entry",
            143.     .nm_priv = ((void *)0),
            144.     .reserved = {0},
            145. };
            146. 
        
            147. extern "C" __attribute__((constructor)) void RegisterEntryModule(void) { napi_module_register(&demoModule); }




## ArkTS侧调用C/C++方法实现 __

收起

自动换行

深色代码主题

复制
    
          1. // 通过import的方式，引入Native能力。
      2. import napitest from 'libentry.so';
      3. 
    
      4. @Entry
      5. @Component
      6. struct Index {
      7.   @State message: string = 'Hello World';
      8. 
    
      9.   build() {
      10.     Row() {
      11.       Column() {
      12.         Text(this.message)
      13.           .fontSize(50)
      14.           .fontWeight(FontWeight.Bold)
      15.           .onClick(() => {
      16.             // runtest
      17.             napitest.runTest();
      18.           })
      19.       }
      20.       .width('100%')
      21.     }
      22.     .height('100%')
      23.   }
      24. }
    
    

预期输出结果

收起

自动换行

深色代码主题

复制
    
          1. JSVM OH_JSVM_StrictEquals: success: 0
    
    
