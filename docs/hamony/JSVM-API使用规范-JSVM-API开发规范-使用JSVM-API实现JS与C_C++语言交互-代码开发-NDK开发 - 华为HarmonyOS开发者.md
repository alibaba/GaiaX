# JSVM-API使用规范-JSVM-API开发规范-使用JSVM-API实现JS与C/C++语言交互-代码开发-NDK开发 - 华为HarmonyOS开发者

**源地址**: https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-guidelines

---



## 生命周期管理 __

 **【规则】** 合理使用OH_JSVM_OpenHandleScope和OH_JSVM_CloseHandleScope管理JSVM_Value的生命周期，做到生命周期最小化，避免发生内存泄漏问题。

每个JSVM_Value属于特定的HandleScope，HandleScope通过OH_JSVM_OpenHandleScope和OH_JSVM_CloseHandleScope来建立和关闭，HandleScope关闭后，所属的JSVM_Value就会自动释放。

**注意事项** ：

  1. JSVM_Value必须在HandleScope打开后才可创建(Node-API无该限制)，否则会造成应用崩溃；
  2. JSVM_Value不能在其对应的HandleScope关闭后使用，如需持久化持有，需调用OH_JSVM_CreateReference转化为JSVM_Ref；
  3. Scope(包括JSVM_VMScope、JSVM_EnvScope、JSVM_HandleScope)需逆序关闭，最先打开的Scope需最后关闭，否则可能造成应用崩溃；



**Scope关闭错误示例** ：

收起

自动换行

深色代码主题

复制
    
          1. // 未逆序关闭JSVM_VMScope，可能造成应用崩溃
      2. JSVM_VM vm;
      3. JSVM_CreateVMOptions options;
      4. OH_JSVM_CreateVM(&options, &vm);
      5. 
    
      6. JSVM_VMScope vmScope1, vmScope2;
      7. OH_JSVM_OpenVMScope(vm, &vmScope1);
      8. OH_JSVM_OpenVMScope(vm, &vmScope2);
      9. 
    
      10. // 正确顺序为先关闭vmScope2，再关闭vmScope1
      11. OH_JSVM_CloseVMScope(vm, vmScope1);
      12. OH_JSVM_CloseVMScope(vm, vmScope2);
      13. OH_JSVM_DestroyVM(vm);
    
    

**C++使用封装** ：

收起

自动换行

深色代码主题

复制
    
          1. class HandleScopeWrapper {
      2.  public:
      3.   HandleScopeWrapper(JSVM_Env env) : env(env) {
      4.     OH_JSVM_OpenHandleScope(env, &handleScope);
      5.   }
      6. 
    
      7.   ~HandleScopeWrapper() {
      8.     OH_JSVM_CloseHandleScope(env, handleScope);
      9.   }
      10. 
    
      11.   HandleScopeWrapper(const HandleScopeWrapper&) = delete;
      12.   HandleScopeWrapper& operator=(const HandleScopeWrapper&) = delete;
      13.   HandleScopeWrapper(HandleScopeWrapper&&) = delete;
      14.   void* operator new(size_t) = delete;
      15.   void* operator new[](size_t) = delete;
      16. 
    
      17.  protected:
      18.   JSVM_Env env;
      19.   JSVM_HandleScope handleScope;
      20. };
    
    

**示例** ：

收起

自动换行

深色代码主题

复制
    
          1. // 在for循环中频繁调用JSVM接口创建js对象时，要加handle_scope及时释放不再使用的资源。
      2. // 下面例子中，每次循环结束局部变量res的生命周期已结束，因此加scope及时释放其持有的js对象，防止内存泄漏
      3. // 每次for循环结束后，触发HandleScopeWrapper的析构函数，释放scope持有的js对象
      4. for (int i = 0; i < 100000; i++)
      5. {
      6.     HandleScopeWrapper scope(env);
      7.     JSVM_Value res;
      8.     OH_JSVM_CreateObject(env, &res);
      9.     if (i == 1000) {
      10.         // break退出循环后会自动调用HandleScopeWrapper析构函数释放资源
      11.         break;
      12.     }
      13. }
    
    

## 多引擎实例上下文敏感 __

 **【规则】** 多引擎实例（VM）场景下，禁止通过JSVM-API跨引擎实例访问JS对象。

引擎实例是一个独立运行环境，JS对象创建访问等操作必须在同一个引擎实例中进行。若在不同引擎实例中操作同一个对象，可能会引发程序崩溃。引擎实例在接口中体现为JSVM_Env。

**错误示例** ：

收起

自动换行

深色代码主题

复制
    
          1. // 线程1执行，在env1创建string对象，值为"value1"
      2. OH_JSVM_CreateStringUtf8(env1, "value1", JSVM_AUTO_LENGTH , &string);
      3. // 线程2执行，在env2创建object对象，并将上述的string对象设置到object对象中
      4. JSVM_Status status = OH_JSVM_CreateObject(env2, &object);
      5. if (status != JSVM_OK)
      6. {
      7.     return;
      8. }
      9. 
    
      10. status = OH_JSVM_SetNamedProperty(env2, object, "string1", string);
      11. if (status != JSVM_OK)
      12. {
      13.     return;
      14. }
    
    

所有的JS对象都隶属于具体的某一JSVM_Env，不可将env1的对象，设置到env2中的对象中。在env2中一旦访问到env1的对象，程序可能会发生崩溃。

## 多线程共享引擎实例 __

 **【规则】** 多线程同时使用同一个引擎实例的场景下，需要加锁使用。保证一个引擎实例在同一时刻只能在一个线程执行。多线程同一时刻同时使用引擎实例可能造成应用崩溃。

**注意事项** ：

  1. OH_JSVM_IsLocked的结果为 **当前线程** 是否持有引擎实例的锁，无需设置循环等待其他线程释放锁；
  2. OH_JSVM_AcquireLock在同一线程中嵌套使用不会造成死锁；
  3. 使用OH_JSVM_ReleaseLock时需判断是否在最外层，避免同一线程中嵌套使用OH_JSVM_AcquireLock的场景下内层释放了整个线程的锁；
  4. OH_JSVM_AcquireLock后需调用OH_JSVM_OpenHandleScope让引擎实例进入线程；OH_JSVM_ReleaseLock前需调用OH_JSVM_CloseHandleScope让引擎实例退出线程；
  5. 不同线程禁止嵌套使用引擎实例，如需临时切换线程使用引擎实例，请确保JSVM_Value已保存为JSVM_Ref，释放锁后对JSVM_Value将不可访问；
  6. 需注意资源获取的顺序为：锁 -> VMScope -> EnvScope -> HandleScope，资源释放的顺序正好相反，错误的顺序可能导致程序崩溃。



**C++使用封装** ：

收起

自动换行

深色代码主题

复制
    
          1. class LockWrapper {
      2.  public:
      3.   // 构造函数，获取锁、VMScope、EnvScope
      4.   LockWrapper(JSVM_Env env) : env(env) {
      5.     OH_JSVM_IsLocked(env, &isLocked);
      6.     if (!isLocked) {
      7.       OH_JSVM_AcquireLock(env);
      8.       OH_JSVM_GetVM(env, &vm);
      9.       OH_JSVM_OpenVMScope(vm, &vmScope);
      10.       OH_JSVM_OpenEnvScope(env, &envScope);
      11.     }
      12.   }
      13. 
    
      14.   // 析构函数，释放EnvScope、VMScope、锁
      15.   ~LockWrapper() {
      16.     if (!isLocked) {
      17.       OH_JSVM_CloseEnvScope(env, envScope);
      18.       OH_JSVM_CloseVMScope(vm, vmScope);
      19.       OH_JSVM_ReleaseLock(env);
      20.     }
      21.   }
      22. 
    
      23.   LockWrapper(const LockWrapper&) = delete;
      24.   LockWrapper& operator=(const LockWrapper&) = delete;
      25.   LockWrapper(LockWrapper&&) = delete;
      26.   void* operator new(size_t) = delete;
      27.   void* operator new[](size_t) = delete;
      28. 
    
      29.  private:
      30.   JSVM_Env env;
      31.   JSVM_EnvScope envScope;
      32.   JSVM_VMScope vmScope;
      33.   JSVM_VM vm;
      34.   bool isLocked;
      35. };
    
    

**正确示例** ：

收起

自动换行

深色代码主题

复制
    
          1. // 该用例演示了多线程中使用vm
      2. // t1线程先获取锁，并继续JSVM-API的调用
      3. // t2线程会在获取锁处阻塞，直到t1线程执行结束释放锁后，t2线程继续执行，调用JSVM-API接口
      4. static napi_value Add([[maybe_unused]] napi_env _env, [[maybe_unused]] napi_callback_info _info) {
      5.     static JSVM_VM vm;
      6.     static JSVM_Env env;
      7.     static int aa = 0;
      8.     if (aa == 0) {
      9.         OH_JSVM_Init(nullptr);
      10.         aa++;
      11.         // create vm
      12.         JSVM_CreateVMOptions options;
      13.         memset(&options, 0, sizeof(options));
      14.         OH_JSVM_CreateVM(&options, &vm);
      15.         // create env
      16.         OH_JSVM_CreateEnv(vm, 0, nullptr, &env);
      17.     }
      18. 
    
      19.     std::thread t1([]() {
      20.         LockWrapper lock(env);
      21.         JSVM_HandleScope handleScope;
      22.         OH_JSVM_OpenHandleScope(env, &handleScope);
      23.         JSVM_Value value;
      24.         JSVM_Status rst = OH_JSVM_CreateInt32(env, 32, &value); // 32: numerical value
      25.         if (rst == JSVM_OK) {
      26.             OH_LOG_INFO(LOG_APP, "JSVM:t1 OH_JSVM_CreateInt32 suc");
      27.         } else {
      28.             OH_LOG_ERROR(LOG_APP, "JSVM:t1 OH_JSVM_CreateInt32 fail");
      29.         }
      30.         int32_t num1 = 0;
      31.         OH_JSVM_GetValueInt32(env, value, &num1);
      32.         OH_LOG_INFO(LOG_APP, "JSVM:t1 num1 = %{public}d", num1);
      33.         OH_JSVM_CloseHandleScope(env, handleScope);
      34.     });
      35.     std::thread t2([]() {
      36.         LockWrapper lock(env);
      37.         JSVM_HandleScope handleScope;
      38.         OH_JSVM_OpenHandleScope(env, &handleScope);
      39.         JSVM_Value value;
      40.         JSVM_Status rst = OH_JSVM_CreateInt32(env, 32, &value); // 32: numerical value
      41.         if (rst == JSVM_OK) {
      42.             OH_LOG_INFO(LOG_APP, "JSVM:t2 OH_JSVM_CreateInt32 suc");
      43.         } else {
      44.             OH_LOG_ERROR(LOG_APP, "JSVM:t2 OH_JSVM_CreateInt32 fail");
      45.         }
      46.         int32_t num1 = 0;
      47.         OH_JSVM_GetValueInt32(env, value, &num1);
      48.         OH_LOG_INFO(LOG_APP, "JSVM:t2 num1 = %{public}d", num1);
      49.         OH_JSVM_CloseHandleScope(env, handleScope);
      50.     });
      51.     t1.detach();
      52.     t2.detach();
      53.     return nullptr;
      54. }
    
    

## 获取JS传入参数及其数量 __

 **【规则】** 当传入OH_JSVM_GetCbInfo的argv不为nullptr时，argv的长度必须大于等于传入argc声明的大小。

当argv不为nullptr时，OH_JSVM_GetCbInfo会根据argc声明的数量将JS实际传入的参数写入argv。如果argc小于等于实际JS传入参数的数量，该接口仅会将声明的argc数量的参数写入argv；而当argc大于实际参数数量时，该接口会在argv的尾部填充undefined。

**错误示例** ：

收起

自动换行

深色代码主题

复制
    
          1. static JSVM_Value IncorrectDemo1(JSVM_Env env, JSVM_CallbackInfo info) {
      2.     // argc 未正确的初始化，其值为不确定的随机值，导致 argv 的长度可能小于 argc 声明的数量，数据越界。
      3.     size_t argc;
      4.     JSVM_Value argv[10] = {nullptr};
      5.     OH_JSVM_GetCbInfo(env, info, &argc, argv, nullptr, nullptr);
      6.     return nullptr;
      7. }
      8. 
    
      9. static JSVM_Value IncorrectDemo2(JSVM_Env env, JSVM_CallbackInfo info) {
      10.     // argc 声明的数量大于 argv 实际初始化的长度，导致 OH_JSVM_GetCbInfo 接口在写入 argv 时数据越界。
      11.     size_t argc = 5;
      12.     JSVM_Value argv[3] = {nullptr};
      13.     OH_JSVM_GetCbInfo(env, info, &argc, argv, nullptr, nullptr);
      14.     return nullptr;
      15. }
    
    

**正确示例** ：

收起

自动换行

深色代码主题

复制
    
          1. static JSVM_Value GetArgvDemo1(napi_env env, JSVM_CallbackInfo info) {
      2.     size_t argc = 0;
      3.     // argv 传入 nullptr 来获取传入参数真实数量
      4.     OH_JSVM_GetCbInfo(env, info, &argc, nullptr, nullptr, nullptr);
      5.     // JS 传入参数为0，不执行后续逻辑
      6.     if (argc == 0) {
      7.         return nullptr;
      8.     }
      9.     // 创建数组用以获取JS传入的参数
      10.     JSVM_Value* argv = new JSVM_Value[argc];
      11.     OH_JSVM_GetCbInfo(env, info, &argc, argv, nullptr, nullptr);
      12.     // 业务代码
      13.     // ... ...
      14.     // argv 为 new 创建的对象，在使用完成后手动释放
      15.     delete[] argv;
      16.     return nullptr;
      17. }
      18. 
    
      19. static JSVM_Value GetArgvDemo2(napi_env env, JSVM_CallbackInfo info) {
      20.     size_t argc = 2;
      21.     JSVM_Value* argv[2] = {nullptr};
      22.     // OH_JSVM_GetCbInfo 会向 argv 中写入 argc 个 JS 传入参数或 undefined
      23.     OH_JSVM_GetCbInfo(env, info, &argc, argv, nullptr, nullptr);
      24.     // 业务代码
      25.     // ... ...
      26.     return nullptr;
      27. }
    
    

## 异常处理 __

 **【建议】** JSVM-API接口调用发生异常需要及时处理，不能遗漏异常到后续逻辑，否则程序可能发生不可预期行为。

根据主从类型，异常处理可以分为两类：

  1. JSVM 执行 C++ 回调函数（JS主，Native从）时发生 C++ 异常，需往 JSVM 中抛出异常，下面用例描述了3种情况下 C++ 回调函数的写法。需要注意的是，回调函数中调用JSVM-API失败，如要向JSVM中抛异常，需保证JSVM中无等待处理的异常，也可以不抛出异常，JS的try-catch块可以捕获回调函数调用API失败产生的JS异常，见NativeFunctionExceptionDemo3。

收起

自动换行

深色代码主题

复制
        
                    1. // JSVM主， Native从
            2. void DoSomething() {
            3.     throw("Do something failed");
            4. }
            5. 
        
            6. // Demo1: C++捕获到异常，抛出异常到JSVM中
            7. JSVM_Value NativeFunctionExceptionDemo1(JSVM_Env env, JSVM_CallbackInfo info) {
            8.     try {
            9.         DoSomething();
            10.     } catch (const char *ex) {
            11.         OH_JSVM_ThrowError(env, nullptr, ex);
            12.         return nullptr;
            13.     }
            14.     return nullptr;
            15. }
            16. 
        
            17. // Demo2: JSVM-API调用失败，抛出异常到JSVM中
            18. JSVM_Value NativeFunctionExceptionDemo2(JSVM_Env env, JSVM_CallbackInfo info) {
            19.     JSVM_Value JSBool = nullptr;
            20.     bool value = false;
            21.     auto status = OH_JSVM_GetValueBool(env, JSBool, &value);
            22.     if (status != JSVM_OK) {
            23.         OH_JSVM_ThrowError(env, nullptr, "Get bool value failed");
            24.         return nullptr;
            25.     }
            26.     return nullptr;
            27. }
            28. 
        
            29. // Demo3：JSVM-API调用失败且在调用过程中已向JSVM中添加等待处理的异常，则无需再向JSVM中抛出异常
            30. JSVM_Value NativeFunctionExceptionDemo3(JSVM_Env env, JSVM_CallbackInfo info) {
            31.     std::string sourcecodestr = R"JS(
            32.         throw Error('Error throw from js');
            33.     )JS";
            34.     JSVM_Value sourcecodevalue = nullptr;
            35.     JSVM_CALL(OH_JSVM_CreateStringUtf8(env, sourcecodestr.c_str(), sourcecodestr.size(), &sourcecodevalue));
            36.     JSVM_Script script;
            37.     auto status = OH_JSVM_CompileScript(env, sourcecodevalue, nullptr, 0, true, nullptr, &script);
            38.     if (status != JSVM_OK) {
            39.         OH_JSVM_ThrowError(env, nullptr, "compile script failed");
            40.         return nullptr;
            41.     }
            42.     JSVM_Value result;
            43.     // 执行JS脚本，执行过程中抛出JS异常
            44.     status = OH_JSVM_RunScript(env, script, &result);
            45.     if (status != JSVM_OK) {
            46.         bool isPending = false;
            47.         // 如果已有异常，则无需再向JSVM中抛出异常；
            48.         // 如需处理并抛出新异常，需先处理JSVM中等待的异常
            49.         if (JSVM_OK == OH_JSVM_IsExceptionPending((env), &isPending) && isPending) {
            50.             return nullptr;
            51.         }
            52.         OH_JSVM_ThrowError(env, nullptr, "Runscript failed");
            53.         return nullptr;
            54.     }
            55.     return nullptr;
            56. }
            57. 
        
            58. // 绑定NativeFunction到JSVM中，省略
            59. std::string sourcecodestr = R"JS(
            60.     // consolelog需用户实现
            61.     try {
            62.         // 调用Native函数
            63.         NativeFunction()
            64.     } catch (e) {
            65.         // 处理Native中产生的异常
            66.         consolelog(e.message)
            67.     }
            68. )JS";
            69. JSVM_Value sourcecodevalue = nullptr;
            70. JSVM_CALL(OH_JSVM_CreateStringUtf8(env, sourcecodestr.c_str(), sourcecodestr.size(), &sourcecodevalue));
            71. JSVM_Script script;
            72. JSVM_CALL(OH_JSVM_CompileScript(env, sourcecodevalue, nullptr, 0, true, nullptr, &script));
            73. JSVM_Value result;
            74. // 执行JS脚本，JS调用Native方法
            75. JSVM_CALL(OH_JSVM_RunScript(env, script, &result));

  2. C++调用JSVM-API（Native主，JS从）失败，需清理JSVM中等待处理的异常，避免影响后续JSVM-API的执行，并设置C++异常处理分支（或抛出C++异常）。

收起

自动换行

深色代码主题

复制
        
                    1. std::string sourcecodestr = R"JS(
            2.     throw Error('Error throw from js');
            3. )JS";
            4. JSVM_Value sourcecodevalue = nullptr;
            5. OH_JSVM_CreateStringUtf8(env, sourcecodestr.c_str(), sourcecodestr.size(), &sourcecodevalue);
            6. JSVM_Script script;
            7. auto status = OH_JSVM_CompileScript(env, sourcecodevalue, nullptr, 0, true, nullptr, &script);
            8. // 异常处理分支
            9. if (status != JSVM_OK) {
            10.     JSVM_Value error = nullptr;
            11.     // 获取并清理异常
            12.     JSVM_CALL(OH_JSVM_GetAndClearLastException((env), &error));
            13.     // 处理异常，如打印信息，省略
            14.     // 抛出 C++ 异常或结束函数执行
            15.     throw "JS Compile Error";
            16. }
            17. JSVM_Value result;
            18. // 执行JS脚本，执行过程中抛出JS异常
            19. status = OH_JSVM_RunScript(env, script, &result);
            20. 
        
            21. // 异常分支处理
            22. if (status != JSVM_OK) {
            23.     JSVM_Value error = nullptr;
            24.     // 获取并清理异常
            25.     JSVM_CALL(OH_JSVM_GetAndClearLastException((env), &error));
            26.     // 处理异常，如打印信息，省略
            27.     // 抛出 C++ 异常或结束函数执行
            28.     throw "JS RunScript Error";
            29. }




## 上下文绑定对象 __

 **【规则】** ：调用JSVM-API生成的JS函数、对象需绑定到上下文中才能从JS侧访问，OH_JSVM_CreateFunction接口中的const char *参数为创建函数的属性name，不代表上下文中指向该函数的名称。调用JSVM-API生成的类、对象同理。

**示例** ：

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value JSFunc = nullptr;
      2. const char *name = "NativeFunction";
      3. JSVM_CallbackStruct cb = {NativeFunction, nullptr};
      4. // 创建JS函数，该函数的属性 "name" 为 "NativeFunction"
      5. OH_JSVM_CreateFunction(env, name, strlen(name), &cb, &JSFunc);
      6. // 绑定函数到上下文
      7. // 获取上下文的global对象
      8. JSVM_Value global = nullptr;
      9. OH_JSVM_GetGlobal(env, &global);
      10. // 创建JS字符串"FunctionNameInJSContext"
      11. JSVM_Value key = nullptr;
      12. OH_JSVM_CreateStringLatin1(env, "FunctionNameInJSContext", JSVM_AUTO_LENGTH, &key);
      13. // 设置global的属性"FunctionNameInJSContext"为JSFunc，将函数绑定到上下文中
      14. OH_JSVM_SetProperty(env, global, key, JSFunc);
      15. // 在JS中调用函数
      16. std::string sourcecodestr = R"JS(
      17.     // consolelog需用户实现
      18.     FunctionNameInJSContext() // 调用成功
      19.     consolelog(FunctionNameInJSContext.name) // 打印 "NativeFunction"
      20.     try {
      21.         NativeFunction() // 无法找到该函数，抛出异常
      22.     } catch (e) {
      23.         consolelog(e.message)
      24.     }
      25. )JS";
    
    
