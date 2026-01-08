# JSVM-API常见问题-JSVM-API开发规范-使用JSVM-API实现JS与C/C++语言交互-代码开发-NDK开发 - 华为HarmonyOS开发者

**源地址**: https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-frequently-questions

---



## 定位方法 __

程序崩溃类问题：通过C++崩溃时调用栈查询FAQ的方式定位代码问题

程序执行结果不符合预期类问题：需应用通过JSVM-API调用返回值定位到执行失败或执行结果不符合预期的位置，通过函数名查询FAQ

## 程序崩溃类 __

  1. Q：在OH_JSVM_RunScript或OH_JSVM_CallFunction时crash，调用栈顶层为SetReturnValue

收起

自动换行

深色代码主题

复制
        
                    1. #00 pc 0000000000c68ef0 /system/lib64/ndk/libjsvm.so(v8impl::(anonymous namespace)::FunctionCallbackWrapper::SetReturnValue(JSVM_Value__*)+16)
            2. #01 pc 0000000000c5ad30 /system/lib64/ndk/libjsvm.so(v8impl::(anonymous namespace)::FunctionCallbackWrapper::Invoke(v8::FunctionCallbackInfo<v8::Value> const&)+332)
            3. #02 pc 00000000014a9e58 /system/lib64/ndk/libjsvm.so
            4. #03 pc 00000000014a95d4 /system/lib64/ndk/libjsvm.so(v8::internal::Builtin_HandleApiCall(int, unsigned long*, v8::internal::Isolate*)+176)
            5. #04 pc 0000000000f7dab4 /system/lib64/ndk/libjsvm.so(Builtins_CEntry_Return1_ArgvOnStack_BuiltinExit+84)
            6. #05 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            7. #06 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            8. #07 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            9. #08 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            10. #09 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            11. #10 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            12. #11 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            13. #12 pc 0000000000eece40 /system/lib64/ndk/libjsvm.so(Builtins_InterpreterEntryTrampoline+288)
            14. #13 pc 0000000000fe09f4 /system/lib64/ndk/libjsvm.so(Builtins_PromiseFulfillReactionJob+52)
            15. #14 pc 0000000000f155c0 /system/lib64/ndk/libjsvm.so(Builtins_RunMicrotasks+672)
            16. #15 pc 0000000000eeab54 /system/lib64/ndk/libjsvm.so(Builtins_JSRunMicrotasksEntry+148)
            17. #16 pc 00000000015bed78 /system/lib64/ndk/libjsvm.so(v8::internal::(anonymous namespace)::Invoke(v8::internal::Isolate*, v8::internal::(anonymous namespace)::InvokeParams const&)+2520)
            18. #17 pc 00000000015bf50c /system/lib64/ndk/libjsvm.so(v8::internal::(anonymous namespace)::InvokeWithTryCatch(v8::internal::Isolate*, v8::internal::(anonymous namespace)::InvokeParams const&)+104)
            19. #18 pc 00000000015bf730 /system/lib64/ndk/libjsvm.so(v8::internal::Execution::TryRunMicrotasks(v8::internal::Isolate*, v8::internal::MicrotaskQueue*)+80)
            20. #19 pc 00000000015ecf4c /system/lib64/ndk/libjsvm.so(v8::internal::MicrotaskQueue::RunMicrotasks(v8::internal::Isolate*)+312)
            21. #20 pc 00000000015ecd9c /system/lib64/ndk/libjsvm.so(v8::internal::MicrotaskQueue::PerformCheckpointInternal(v8::Isolate*)+52)
            22. #21 pc 00000000015deaa0 /system/lib64/ndk/libjsvm.so(v8::internal::Isolate::FireCallCompletedCallbackInternal(v8::internal::MicrotaskQueue*)+280)
            23. #22 pc 00000000014334e8 /system/lib64/ndk/libjsvm.so(v8::CallDepthScope<true>::~CallDepthScope()+248)
            24. #23 pc 00000000014330a4 /system/lib64/ndk/libjsvm.so(v8::Script::Run(v8::Local<v8::Context>, v8::Local<v8::Data>)+884)
            25. #24 pc 0000000000c5c2ac /system/lib64/ndk/libjsvm.so(OH_JSVM_RunScript+272)

A：SetReturnValue用于设置js函数的返回值，在js完成注入的native函数调用后触发。需检查native函数的返回值是否正确，如返回值（JSVM_Value）是否未初始化就直接返回。

  2. Q：js执行虚拟机初始化注入的native函数时程序崩溃

A：检查JSVM_CallbackStruct是否为栈上变量。如果跨函数使用，需确保JSVM_CallbackStruct的生命周期长于JSVM_Env的生命周期。

收起

自动换行

深色代码主题

复制
        
                    1. func {
            2.    // ...
            3.     JSVM_CallbackStruct param[] = {
            4.         {.data = nullptr, .callback = ConsoleInfo},
            5.         {.data = nullptr, .callback = Add},
            6.     };
            7.     JSVM_PropertyDescriptor descriptor[] = {
            8.         {"consoleinfo", NULL, &param[0], NULL, NULL, NULL, JSVM_DEFAULT},
            9.         {"add", NULL, &param[1], NULL, NULL, NULL, JSVM_DEFAULT},
            10.     };
            11.     // create env, register native method, and open env scope
            12.     JSVM_Env env;
            13.     OH_JSVM_CreateEnv(vm, sizeof(descriptor) / sizeof(descriptor[0]), descriptor, &env);
            14.    // ...
            15.     OH_JSVM_DestroyEnv(env);
            16.    // ...
            17. }

在上述示例代码中，JS引擎实例在函数结束前被关闭，因此可以直接使用栈上的param。

  3. Q：OH_JSVM_ReferenceRef、OH_JSVM_ReferenceUnRef、OH_JSVM_CreateReference、OH_JSVM_DeleteReference时程序崩溃

A：检查是否同时有多个线程持有和释放JSVM_Ref，见 [多线程共享引擎实例](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-guidelines#多线程共享引擎实例)

  4. Q：在虚拟机引擎实例中创建JS类型实例崩溃（如OH_JSVM_CreateDouble），调用栈如下

收起

自动换行

深色代码主题

复制
        
                    1. #00 pc 0000000001d209e4/system/lib64/ndk/libjsvm.so(v8::base::0S::Abort()+28)
            2. #01 pc 0000000001408480/system/lib64/ndk/libjsvm.so(v8::Utils::ReportApiFailure(char const*,char const*)+124)
            3. #02 pc 00000000015c99b8/system/lib64/ndk/libjsvm.so(v8::internal::HandleScope::Extend(v8::internal::Isolate*+200)

A：检查HandleScope的使用是否正确，参考[生命周期管理](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-guidelines#生命周期管理)




## JSVM-API执行失败类 __

  1. Q：OH_JSVM_GetCbInfo无法获取JS函数参数

A：检查函数传递的参数是否正确，见[获取JS传入参数及其数量](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-guidelines#获取js传入参数及其数量)

  2. Q：OH_JSVM_CreateFunction等函数调用失败，返回值为JSVM_PENDING_EXCEPTION

A：JSVM_PENDING_EXCEPTION表明当前虚拟机环境中存在未处理的异常，可能是由于本次调用产生的JS异常，也可能是之前调用产生的未被清理的异常。可以通过在函数调用前插入OH_JSVM_GetAndClearLastException排查之前是否有未清除的异常。如果为之前的未清理异常，检查是否有JSVM接口调用未处理异常返回值；如果是本次产生的异常，需清理异常，避免影响后续的函数调用。获取并清理异常的函数为OH_JSVM_GetAndClearLastException

  3. Q：JS执行时无法找到 OH_JSVM_DefineClass 定义的类

A：检查是否将定义的类绑定到上下文中，见[上下文绑定对象](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-guidelines#上下文绑定对象)



