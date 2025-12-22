# JSVM-API 支持的数据类型和接口-使用JSVM-API实现JS与C/C++语言交互-代码开发-NDK开发 - 华为HarmonyOS开发者

**源地址**: https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/jsvm-data-types-interfaces

---



## JSVM-API 的数据类型 __

### JSVM_Status __

这是一个枚举数据类型，用来表示JSVM-API接口返回的状态信息。

每调用一次JSVM-API函数，都会返回一个值，用来表示操作成功与否的相关信息。

收起

自动换行

深色代码主题

复制
    
          1.     typedef enum {
      2.         JSVM_OK,                              /* 成功状态 */
      3.         JSVM_INVALID_ARG,                     /* 无效的状态 */
      4.         JSVM_OBJECT_EXPECTED,                 /* 期待传入对象类型 */
      5.         JSVM_STRING_EXPECTED,                 /* 期待传入字符串类型 */
      6.         JSVM_NAME_EXPECTED,                   /* 期待传入名字 */
      7.         JSVM_FUNCTION_EXPECTED,               /* 期待传入函数类型 */
      8.         JSVM_NUMBER_EXPECTED,                 /* 期待传入数字类型 */
      9.         JSVM_BOOL_EXPECTED,                   /* 期待传入布尔类型 */
      10.         JSVM_ARRAY_EXPECTED,                  /* 期待传入数组类型 */
      11.         JSVM_GENERIC_FAILURE,                 /* 泛型失败状态 */
      12.         JSVM_PENDING_EXCEPTION,               /* 挂起异常状态 */
      13.         JSVM_CANCELLED,                       /* 取消状态 */
      14.         JSVM_ESCAPE_CALLED_TWICE,             /* 转义调用了2次 */
      15.         JSVM_HANDLE_SCOPE_MISMATCH,           /* 句柄作用域不匹配 */
      16.         JSVM_CALLBACK_SCOPE_MISMATCH,         /* 回调作用域不匹配 */
      17.         JSVM_QUEUE_FULL,                      /* 队列满 */
      18.         JSVM_CLOSING,                         /* 关闭中 */
      19.         JSVM_BIGINT_EXPECTED,                 /* 期望传入Bigint类型 */
      20.         JSVM_DATE_EXPECTED,                   /* 期望传入日期类型 */
      21.         JSVM_ARRAYBUFFER_EXPECTED,            /* 期望传入ArrayBuffer类型 */
      22.         JSVM_DETACHABLE_ARRAYBUFFER_EXPECTED, /* 可分离的数组缓冲区预期状态 */
      23.         JSVM_WOULD_DEADLOCK,                  /* 将死锁状态 */
      24.         JSVM_NO_EXTERNAL_BUFFERS_ALLOWED,     /* 不允许外部缓冲区 */
      25.         JSVM_CANNOT_RUN_JS,                   /* 不能执行JS */
      26.         JSVM_INVALID_TYPE,                    /* 非法类型 */
      27.         JSVM_JIT_MODE_EXPECTED,                /* 期望在JIT模式下执行 */
      28.     } JSVM_Status;
    
    

### JSVM_ExtendedErrorInfo __

一个结构体，在调用函数不成功时存储了较为详细的错误信息。

收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.     const char* errorMessage;
      3.     void* engineReserved;
      4.     uint32_t engineErrorCode;
      5.     JSVM_Status errorCode;
      6. } JSVM_ExtendedErrorInfo;
    
    

### JSVM_Value __

在C++代码中，用于表示JavaScript值。

### JSVM_Env __

  * 表示JSVM-API执行时的上下文，作为Native函数的参数传递给JSVM-API接口。

  * 退出Native侧插件时，JSVM_Env将失效，该事件通过回调传递给OH_JSVM_SetInstanceData接口。

  * 禁止缓存JSVM_Env，并禁止在不同Worker中传递JSVM_Env。

  * 在不同线程间共享JSVM_Env时，要保证在线程切换时在前一个线程中关闭env scope并在新的线程中打开新的env scope，以保证threadlocal变量的线程隔离。




### JSVM_ValueType __

JSVM_Value的类型。包含了ECMAScript语言规范中定义的类型，其中JSVM_EXTERNAL表示外部数据类型。

收起

自动换行

深色代码主题

复制
    
          1. typedef enum {
      2.     JSVM_UNDEFINED,
      3.     JSVM_NULL,
      4.     JSVM_BOOLEAN,
      5.     JSVM_NUMBER,
      6.     JSVM_STRING,
      7.     JSVM_SYMBOL,
      8.     JSVM_OBJECT,
      9.     JSVM_FUNCTION,
      10.     JSVM_EXTERNAL,
      11.     JSVM_BIGINT,
      12. } JSVM_ValueType;
    
    

### JSVM_TypedarrayType __

TypedArray 的基本二进制标量数据类型。

收起

自动换行

深色代码主题

复制
    
          1. typedef enum {
      2.     JSVM_INT8_ARRAY,
      3.     JSVM_UINT8_ARRAY,
      4.     JSVM_UINT8_CLAMPED_ARRAY,
      5.     JSVM_INT16_ARRAY,
      6.     JSVM_UINT16_ARRAY,
      7.     JSVM_INT32_ARRAY,
      8.     JSVM_UINT32_ARRAY,
      9.     JSVM_FLOAT32_ARRAY,
      10.     JSVM_FLOAT64_ARRAY,
      11.     JSVM_BIGINT64_ARRAY,
      12.     JSVM_BIGUINT64_ARRAY,
      13. } JSVM_TypedarrayType;
    
    

### JSVM_RegExpFlags __

正则表达式标志位。

收起

自动换行

深色代码主题

复制
    
          1. typedef enum {
      2.     JSVM_REGEXP_NONE = 0,
      3.     JSVM_REGEXP_GLOBAL = 1 << 0,
      4.     JSVM_REGEXP_IGNORE_CASE = 1 << 1,
      5.     JSVM_REGEXP_MULTILINE = 1 << 2,
      6.     JSVM_REGEXP_STICKY = 1 << 3,
      7.     JSVM_REGEXP_UNICODE = 1 << 4,
      8.     JSVM_REGEXP_DOT_ALL = 1 << 5,
      9.     JSVM_REGEXP_LINEAR = 1 << 6,
      10.     JSVM_REGEXP_HAS_INDICES = 1 << 7,
      11.     JSVM_REGEXP_UNICODE_SETS = 1 << 8,
      12. } JSVM_RegExpFlags;
    
    

### 编译选项相关类型 __

 **JSVM_CompileOptions**

配合 OH_JSVM_CompileScriptWithOptions 接口使用，是其参数中 options 数组的元素类型。

其中：

  * id 代表这个编译选项的类型。
  * content 代表编译选项的内容。



id 的值和 content 的类型需对应使用，具体对应关系请参见各选项类型的介绍。

收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.     /** compile option id. */
      3.     JSVM_CompileOptionId id;
      4.     /** option content. */
      5.     union {
      6.       /** ptr type. */
      7.       void *ptr;
      8.       /** int type. */
      9.       int num;
      10.       /** bool type. */
      11.       _Bool boolean;
      12.     } content;
      13. } JSVM_CompileOptions;
    
    

**JSVM_CompileOptionId**

JSVM_CompileOptions 中的 id 对应类型，每个值有对应的 content 类型。JSVM_COMPILE_ENABLE_SOURCE_MAP 的类型为 bool，当 JSVM_ScriptOrigin 中的 sourceMapUrl 不为空时生效。

收起

自动换行

深色代码主题

复制
    
          1. typedef enum {
      2.     /** compile mode. */
      3.     JSVM_COMPILE_MODE,
      4.     /** code cache content. */
      5.     JSVM_COMPILE_CODE_CACHE,
      6.     /** script origin. */
      7.     JSVM_COMPILE_SCRIPT_ORIGIN,
      8.     /** compile profile content. */
      9.     JSVM_COMPILE_COMPILE_PROFILE,
      10.     /** switch for source map support. */
      11.     JSVM_COMPILE_ENABLE_SOURCE_MAP,
      12. } JSVM_CompileOptionId;
    
    

**JSVM_CompileMode**

当 id 为 JSVM_COMPILE_MODE 时，content 类型的每个值代表一种编译模式。

  * JSVM_COMPILE_MODE_DEFAULT : 默认的编译选项。
  * JSVM_COMPILE_MODE_CONSUME_CODE_CACHE : 消耗 codecache 进行编译。
  * JSVM_COMPILE_MODE_EAGER_COMPILE : 进行全量编译，不再进行 lazy compile。
  * JSVM_COMPILE_MODE_PRODUCE_COMPILE_PROFILE/JSVM_COMPILE_MODE_CONSUME_COMPILE_PROFILE : 当前暂无效果，请等待后续更新。



收起

自动换行

深色代码主题

复制
    
          1. typedef enum {
      2.     /** default mode. */
      3.     JSVM_COMPILE_MODE_DEFAULT,
      4.     /** consume code cache. */
      5.     JSVM_COMPILE_MODE_CONSUME_CODE_CACHE,
      6.     /** apply eager compile. */
      7.     JSVM_COMPILE_MODE_EAGER_COMPILE,
      8.     /** preset for compile profile. */
      9.     JSVM_COMPILE_MODE_PRODUCE_COMPILE_PROFILE,
      10.     /** consume compile profile. */
      11.     JSVM_COMPILE_MODE_CONSUME_COMPILE_PROFILE,
      12. } JSVM_CompileMode;
    
    

**JSVM_CodeCache**

当 id 为 JSVM_COMPILE_CODE_CACHE 时，content 的类型为：

  * cache : 指向 code cache 的指针。
  * length : 代表 code cache 的大小。



收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.     /** cache pointer. */
      3.     uint8_t *cache;
      4.     /** length. */
      5.     size_t length;
      6. } JSVM_CodeCache;
    
    

**JSVM_ScriptOrigin**

当 id 为 JSVM_COMPILE_SCRIPT_ORIGIN 时，content 存放待编译脚本的源码信息。

  * sourceMapUrl : sourceMap 的路径，当前仅支持运行设备上的本地路径，可以为空。
  * resourceName : 待编译的 js script 的名字。



收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.     /** Sourcemap url. */
      3.     const char* sourceMapUrl;
      4.     /** Resource name. */
      5.     const char* resourceName;
      6.     /** Resource line offset. */
      7.     size_t resourceLineOffset;
      8.     /** Resource column offset. */
      9.     size_t resourceColumnOffset;
      10. } JSVM_ScriptOrigin;
    
    

### JSVM __

### 内存管理类型 __

JSVM-API 包含以下内存管理类型：

**JSVM_HandleScope**

JSVM_HandleScope 数据类型用于管理 JavaScript 对象的生命周期。它确保在指定范围内创建的 JavaScript 对象保持活动状态，直到该范围结束。这样可以防止使用已释放的对象，提高代码的可靠性和性能。

**JSVM_EscapableHandleScope**

  * 由 OH_JSVM_OpenEscapableHandleScope 接口创建，由 OH_JSVM_CloseEscapableHandleScope 接口关闭。

  * 表示一种特殊类型的句柄范围，用于将在JSVM_EscapableHandleScope范围内创建的值返回给父scope。

  * 用于 OH_JSVM_EscapeHandle 接口，将 JSVM_EscapableHandleScope 范围内的值提升为 JavaScript 对象，以便在外部作用域使用。




**JSVM_Ref**

指向JSVM_Value，允许开发者管理JavaScript值的生命周期。

**JSVM_TypeTag**

该结构体定义了一个包含两个无符号64位整数的类型标签，用于标识一个JSVM-API值的类型信息。

收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.     uint64_t lower;
      3.     uint64_t upper;
      4. } JSVM_TypeTag;
    
    

  * 存储了两个无符号64位整数的128位值，用它来标识JavaScript对象，确保它们属于某种类型。

  * 比OH_JSVM_Instanceof更强的类型检查，如果对象的原型被操纵，OH_JSVM_Instanceof可能会报告误报。

  * JSVM_TypeTag 在与 OH_JSVM_Wrap 结合使用时最有用，因为它确保从包装对象检索的指针可以安全地转换为与先前应用于JavaScript对象的类型标签相对应的Native类型。




### 回调类型 __

JSVM-API包含以下回调类型：

**JSVM_CallbackStruct**

用户提供的 Native callback 的回调函数指针和数据，JSVM_CallbackStruct 将通过 JSVM-API 暴露给 JavaScript。例如，可以使用 OH_JSVM_CreateFunction 接口创建绑定到 Native callback 的 JS 函数，其中 Native callback 就是通过 JSVM_CallbackStruct 结构定义。除非在对象生命周期管理中有特殊要求，一般不在此 callback 中创建 handle 或者 callback scope。

收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.   JSVM_Value(*callback)(JSVM_Env env, JSVM_CallbackInfo info);
      3.   void* data;
      4. } JSVM_CallbackStruct;
    
    

**JSVM_Callback**

JSVM_CallbackStruct 指针类型的别名。

定义如下:

收起

自动换行

深色代码主题

复制
    
          1. typedef JSVM_CallbackStruct* JSVM_Callback;
    
    

**JSVM_CallbackInfo**

用户定义的 Native callback，第一个参数类型是 JSVM_Env，第二个参数类型是 JSVM_CallbackInfo。JSVM_CallbackInfo 表示从 JS 侧调用到 Native 侧时携带的调用信息，如参数列表。在实现 Native callback 时，一般使用 OH_JSVM_GetCbInfo 接口从 JSVM_CallbackInfo 中获取调用信息。

**JSVM_Finalize**

函数指针，用于传入OH_JSVM_SetInstanceData、OH_JSVM_CreateExternal、OH_JSVM_Wrap等接口。JSVM_Finalize在对象被回收后会被调用，可用于在JavaScript对象被垃圾回收时释放Native对象。JSVM 不保证是否执行该回调函数，也不保证执行该回调函数的时机， **开发者不应依赖于该回调的执行时机** 。

写法如下：

收起

自动换行

深色代码主题

复制
    
          1. typedef void (*JSVM_Finalize)(JSVM_Env env, void* finalizeData, void* finalizeHint);
    
    

**JSVM_PropertyHandlerConfigurationStruct**

当执行对象的getter、setter、deleter和enumerator操作时，对应的回调将会触发。

收起

自动换行

深色代码主题

复制
    
          1. typedef struct {
      2.     JSVM_Value(JSVM_CDECL* genericNamedPropertyGetterCallback)(JSVM_Env env,
      3.                                                                JSVM_Value name,
      4.                                                                JSVM_Value thisArg,
      5.                                                                JSVM_Value namedPropertyData);
      6.     JSVM_Value(JSVM_CDECL* genericNamedPropertySetterCallback)(JSVM_Env env,
      7.                                                                JSVM_Value name,
      8.                                                                JSVM_Value property,
      9.                                                                JSVM_Value thisArg,
      10.                                                                JSVM_Value namedPropertyData);
      11.     JSVM_Value(JSVM_CDECL* genericNamedPropertyDeleterCallback)(JSVM_Env env,
      12.                                                                 JSVM_Value name,
      13.                                                                 JSVM_Value thisArg,
      14.                                                                 JSVM_Value namedPropertyData);
      15.     JSVM_Value(JSVM_CDECL* genericNamedPropertyEnumeratorCallback)(JSVM_Env env,
      16.                                                                    JSVM_Value thisArg,
      17.                                                                    JSVM_Value namedPropertyData);
      18.     JSVM_Value(JSVM_CDECL* genericIndexedPropertyGetterCallback)(JSVM_Env env,
      19.                                                                 JSVM_Value index,
      20.                                                                 JSVM_Value thisArg,
      21.                                                                 JSVM_Value indexedPropertyData);
      22.     JSVM_Value(JSVM_CDECL* genericIndexedPropertySetterCallback)(JSVM_Env env,
      23.                                                                  JSVM_Value index,
      24.                                                                  JSVM_Value property,
      25.                                                                  JSVM_Value thisArg,
      26.                                                                  JSVM_Value indexedPropertyData);
      27.     JSVM_Value(JSVM_CDECL* genericIndexedPropertyDeleterCallback)(JSVM_Env env,
      28.                                                                   JSVM_Value index,
      29.                                                                   JSVM_Value thisArg,
      30.                                                                   JSVM_Value indexedPropertyData);
      31.     JSVM_Value(JSVM_CDECL* genericIndexedPropertyEnumeratorCallback)(JSVM_Env env,
      32.                                                                      JSVM_Value thisArg,
      33.                                                                      JSVM_Value indexedPropertyData);
      34.     JSVM_Value namedPropertyData;
      35.     JSVM_Value indexedPropertyData;
      36. } JSVM_PropertyHandlerConfigurationStruct;
    
    

**JSVM_PropertyHandlerCfg**

包含属性监听回调的结构指针。

基本用法如下:

收起

自动换行

深色代码主题

复制
    
          1. typedef JSVM_PropertyHandlerConfigurationStruct* JSVM_PropertyHandlerCfg;
    
    

## 支持的JSVM-API接口 __

标准JS引擎的能力通过JSVM-API提供。JSVM-API支持动态链接到不同版本的JS引擎库，从而为开发者屏蔽掉不同引擎接口的差异。JSVM-API提供引擎生命周期管理、JS context管理、JS代码执行、JS/C++互操作、执行环境快照、codecache等能力，具体可见下文。

### 使用 JSVM-API 接口创建引擎实例及 JS 执行上下文环境 __

 **场景介绍**

执行JS代码需要先创建JavaScript VM，创建JS执行的上下文环境。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_Init | 初始化JavaScript引擎实例  
OH_JSVM_CreateVM | 创建JavaScript引擎实例  
OH_JSVM_DestroyVM | 销毁JavaScript引擎实例  
OH_JSVM_OpenVMScope | 打开一个新的VM scope，引擎实例只能在scope范围内使用，可以保证引擎实例不被销毁  
OH_JSVM_CloseVMScope | 关闭VM scope  
OH_JSVM_CreateEnv | 创建一个新的JS执行上下文环境，并注册指定的Native函数  
OH_JSVM_DestroyEnv | 销毁一个JS执行上下文环境  
OH_JSVM_OpenEnvScope | 打开一个新的Env scope，Env只能在scope范围内使用  
OH_JSVM_CloseEnvScope | 关闭Env scope  
OH_JSVM_OpenHandleScope | 打开一个Handle scope，确保scope范围内的JSVM_Value不被GC回收  
OH_JSVM_CloseHandleScope | 关闭Handle scope  
  
**JSVM_InitOptions 的使用描述**

JSVM 提供了多种配置选项，允许开发者在执行 OH_JSVM_Init 时灵活配置其行为。可以通过 OH_JSVM_GetVMInfo 接口获取当前 JSVM 版本所对应的 V8 引擎版本。JSVM 中可支持的选项范围与对应的 V8 引擎版本可支持的选项范围保持一致。OH_JSVM_GetVMInfo 接口的使用参考[使用JSVM-API接口获取JSVM API的版本号](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-about-version)。

注意

  * 建议开发者在没有特殊需求的情况下，仅使用JSVM内部的默认配置选项。



场景示例：

常规模式下初始化 VM 平台。

收起

自动换行

深色代码主题

复制
    
          1. static void NormalInit(bool &vmInit) {
      2.     if (!vmInit) {
      3.         // JSVM only need init once
      4.         JSVM_InitOptions initOptions;
      5.         memset(&initOptions, 0, sizeof(initOptions));
      6.         JSVM_Status cond = OH_JSVM_Init(&initOptions);
      7.         if(cond == JSVM_OK) {
      8.             vmInit = true;
      9.         } else {
      10.             vmInit = false;
      11.         }
      12.     }
      13. }
    
    

场景示例：

初始化低内存占用的 VM 平台。

收起

自动换行

深色代码主题

复制
    
          1. static void LowMemoryInit(bool &vmInit) {
      2.     if (!vmInit) {
      3.         // JSVM only need init once
      4.         JSVM_InitOptions initOptions;
      5.         int argc = 4;
      6.         initOptions.argc = &argc;
      7.         const char* argv[4];
      8.         argv[1] = "--incremental-marking-hard-trigger=40";
      9.         argv[2] = "--min-semi-space-size=1";
      10.         argv[3] = "--max-semi-space-size=4";
      11.         initOptions.argv = const_cast<char**>(argv);
      12.         OH_JSVM_Init(&initOptions);
      13.         vmInit = true;
      14.     }
      15. }
    
    

场景示例：

初始化低GC触发频次的 VM 平台。

收起

自动换行

深色代码主题

复制
    
          1. static void LowGCFrequencyInit(bool &vmInit) {
      2.     if (!vmInit) {
      3.         // JSVM only need init once
      4.         JSVM_InitOptions initOptions;
      5.         int argc = 4;
      6.         initOptions.argc = &argc;
      7.         const char* argv[4];
      8.         argv[1] = "--incremental-marking-hard-trigger=80";
      9.         argv[2] = "--min-semi-space-size=16";
      10.         argv[3] = "--max-semi-space-size=16";
      11.         initOptions.argv = const_cast<char**>(argv);
      12.         OH_JSVM_Init(&initOptions);
      13.         vmInit = true;
      14.     }
      15. }
    
    

执行结果：

使用以上三个接口可以分别初始化具备不同特性的 VM 平台。初始化之后，可以创建 VM 实例，并执行 JavaScript 脚本。

相比 NormalInit 接口，LowGCFrequencyInit 接口初始化的VM平台 GC 触发频次更低。

相比 NormalInit 接口，LowMemoryInit 接口初始化的VM平台内存占用更少。

**创建 VM 实例**

场景示例:

创建及销毁 JavaScript 引擎实例，包含创建及销毁 JS 执行上下文环境

收起

自动换行

深色代码主题

复制
    
          1. bool VM_INIT = false;
      2. 
    
      3. static JSVM_Value ConsoleInfo(JSVM_Env env, JSVM_CallbackInfo info) {
      4.     size_t argc = 1;
      5.     JSVM_Value args[1];
      6.     char log[256] = "";
      7.     size_t logLength = 0;
      8.     OH_JSVM_GetCbInfo(env, info, &argc, args, NULL, NULL);
      9. 
    
      10.     OH_JSVM_GetValueStringUtf8(env, args[0], log, 255, &logLength);
      11.     log[255] = 0;
      12.     OH_LOG_INFO(LOG_APP, "JSVM API TEST: %{public}s", log);
      13.     return nullptr;
      14. }
      15. 
    
      16. static JSVM_Value Add(JSVM_Env env, JSVM_CallbackInfo info) {
      17.     size_t argc = 2;
      18.     JSVM_Value args[2];
      19.     OH_JSVM_GetCbInfo(env, info, &argc, args, NULL, NULL);
      20.     double num1 = 0;
      21.     double num2 = 0;
      22.     OH_JSVM_GetValueDouble(env, args[0], &num1);
      23.     OH_JSVM_GetValueDouble(env, args[1], &num2);
      24.     JSVM_Value sum = nullptr;
      25.     OH_JSVM_CreateDouble(env, num1 + num2, &sum);
      26.     return sum;
      27. }
      28. 
    
      29. static napi_value MyJSVMDemo([[maybe_unused]] napi_env _env, [[maybe_unused]] napi_callback_info _info) {
      30.     std::thread t([]() {
      31.         // 可以根据不同的业务需求初始化具备不同能力的 VM 平台：
      32.         // 1. 初始化默认的 VM 平台：调用'NormalInit'接口。
      33.         // 2. 初始化低内存占用的 VM 平台：调用'LowMemoryInit'接口。
      34.         // 3. 初始化低 GC 触发频次的 VM 平台：调用'LowGCFrequencyInit'接口。
      35.         NormalInit(VM_INIT);
      36.         // create vm, and open vm scope
      37.         JSVM_VM vm;
      38.         JSVM_CreateVMOptions options;
      39.         memset(&options, 0, sizeof(options));
      40.         OH_JSVM_CreateVM(&options, &vm);
      41.   42.         JSVM_VMScope vmScope;
      43.         OH_JSVM_OpenVMScope(vm, &vmScope);
      44.   45.         JSVM_CallbackStruct param[] = {
      46.             {.data = nullptr, .callback = ConsoleInfo},
      47.             {.data = nullptr, .callback = Add},
      48.         };
      49.         JSVM_PropertyDescriptor descriptor[] = {
      50.             {"consoleinfo", NULL, &param[0], NULL, NULL, NULL, JSVM_DEFAULT},
      51.             {"add", NULL, &param[1], NULL, NULL, NULL, JSVM_DEFAULT},
      52.         };
      53.         // create env, register native method, and open env scope
      54.         JSVM_Env env;
      55.         OH_JSVM_CreateEnv(vm, sizeof(descriptor) / sizeof(descriptor[0]), descriptor, &env);
      56.   57.         JSVM_EnvScope envScope;
      58.         OH_JSVM_OpenEnvScope(env, &envScope);
      59.   60.         // open handle scope
      61.         JSVM_HandleScope handleScope;
      62.         OH_JSVM_OpenHandleScope(env, &handleScope);
      63.   64.         std::string sourceCodeStr = "\
      65.         {\
      66.             let value = add(4.96, 5.28);\
      67.             consoleinfo('Result is:' + value);\
      68.         }";
      69.         // compile js script
      70.         JSVM_Value sourceCodeValue;
      71.         OH_JSVM_CreateStringUtf8(env, sourceCodeStr.c_str(), sourceCodeStr.size(), &sourceCodeValue);
      72.         JSVM_Script script;
      73.         OH_JSVM_CompileScript(env, sourceCodeValue, nullptr, 0, true, nullptr, &script);
      74.         JSVM_Value result;
      75.         // run js script
      76.         OH_JSVM_RunScript(env, script, &result);
      77.         JSVM_ValueType type;
      78.         OH_JSVM_Typeof(env, result, &type);
      79.         OH_LOG_INFO(LOG_APP, "JSVM API TEST type: %{public}d", type);
      80.   81.         // exit vm and clean memory
      82.         OH_JSVM_CloseHandleScope(env, handleScope);
      83.   84.         OH_JSVM_CloseEnvScope(env, envScope);
      85.         OH_JSVM_DestroyEnv(env);
      86.   87.         OH_JSVM_CloseVMScope(vm, vmScope);
      88.         OH_JSVM_DestroyVM(vm);
      89.     });
      90. 
    
      91.     t.detach();
      92. 
    
      93.     return nullptr;
      94. }
    
    

### 使用 JSVM-API 接口编译及执行 JS 代码 __

 **场景介绍**

编译及执行JS代码。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CompileScript | 编译JavaScript代码并返回绑定到当前环境的编译脚本  
OH_JSVM_CompileScriptWithOrigin | 编译JavaScript代码并返回绑定到当前环境的编译脚本，同时传入包括 sourceMapUrl 和源文件名在内的源代码信息，用于处理 source map 信息  
OH_JSVM_CompileScriptWithOptions | 通用的编译接口，通过传入 option 数组完成前面的 compile 接口全部功能，同时支持后续选项扩展  
OH_JSVM_CreateCodeCache | 为编译脚本创建code cache  
OH_JSVM_RunScript | 执行编译脚本，如果没有 JIT 权限支持，执行含wasm的脚本会失败，在特定场景下存在性能差异，并打印一行日志提示开发者  
  
场景示例：

编译及执行 JS 代码（创建 VM 实例，注册函数，执行 JS，销毁 VM 实例）

cpp 部分代码

收起

自动换行

深色代码主题

复制
    
          1. #include <cstring>
      2. #include <fstream>
      3. #include <string>
      4. #include <vector>
      5. 
    
      6. // 依赖libjsvm.so
      7. #include "ark_runtime/jsvm.h"
      8. 
    
      9. using namespace std;
      10. 
    
      11. static JSVM_Value Hello(JSVM_Env env, JSVM_CallbackInfo info) {
      12.     JSVM_Value output;
      13.     void* data = nullptr;
      14.     OH_JSVM_GetCbInfo(env, info, nullptr, nullptr, nullptr, &data);
      15.     OH_JSVM_CreateStringUtf8(env, (char*)data, strlen((char*)data), &output);
      16.     return output;
      17. }
      18. 
    
      19. static JSVM_CallbackStruct hello_cb = { Hello, (void*)"Hello" };
      20. 
    
      21. static string srcGlobal = R"JS(
      22. const concat = (...args) => args.reduce((a, b) => a + b);
      23. )JS";
      24. 
    
      25. static void RunScriptWithOption(JSVM_Env env, string& src,
      26.                                 uint8_t** dataPtr = nullptr,
      27.                                 size_t* lengthPtr = nullptr) {
      28.     JSVM_HandleScope handleScope;
      29.     OH_JSVM_OpenHandleScope(env, &handleScope);
      30. 
    
      31.     JSVM_Value jsSrc;
      32.     OH_JSVM_CreateStringUtf8(env, src.c_str(), src.size(), &jsSrc);
      33. 
    
      34.     uint8_t* data = dataPtr ? *dataPtr : nullptr;
      35.     auto compilMode = data ? JSVM_COMPILE_MODE_CONSUME_CODE_CACHE :  JSVM_COMPILE_MODE_DEFAULT;
      36.     size_t length = lengthPtr ? *lengthPtr : 0;
      37.     JSVM_Script script;
      38.     // 编译js代码
      39.     JSVM_ScriptOrigin origin {
      40.         // 以包名 helloworld 为例, 假如存在对应的 sourcemap, sourcemap 的路径可以是 /data/app/el2/100/base/com.example.helloworld/files/index.js.map
      41.         .sourceMapUrl = "/data/app/el2/100/base/com.example.helloworld/files/index.js.map",
      42.         // 源文件名字
      43.         .resourceName = "index.js",
      44.         // script 在源文件中的起始行列号
      45.         .resourceLineOffset = 0,
      46.         .resourceColumnOffset = 0,
      47.     };
      48.     JSVM_CompileOptions option[3];
      49.     option[0] = {
      50.         .id = JSVM_COMPILE_MODE,
      51.         .content = { .num = compilMode }
      52.     };
      53.     JSVM_CodeCache codeCache = {
      54.         .cache = data,
      55.         .length = length
      56.     };
      57.     option[1] = {
      58.         .id = JSVM_COMPILE_CODE_CACHE,
      59.         .content = { .ptr = &codeCache }
      60.     };
      61.     // JSVM_COMPILE_ENABLE_SOURCE_MAP 选项默认值为 false，若为 true 那么对应的 sourceMapUrl 必须不为空
      62.     option[2] = {
      63.         .id = JSVM_COMPILE_ENABLE_SOURCE_MAP,
      64.         .content = { .boolean = true }
      65.     };
      66.     OH_JSVM_CompileScriptWithOptions(env, jsSrc, 3, option, &script);
      67. 
    
      68.     JSVM_Value result;
      69.     // 执行js代码
      70.     OH_JSVM_RunScript(env, script, &result);
      71. 
    
      72.     char resultStr[128];
      73.     size_t size = 0;
      74.     OH_JSVM_GetValueStringUtf8(env, result, resultStr, 128, &size);
      75. 
    
      76.     OH_JSVM_CloseHandleScope(env, handleScope);
      77. }
      78. 
    
      79. static void RunScript(JSVM_Env env, string& src,
      80.                        bool withOrigin = false,
      81.                        uint8_t** dataPtr = nullptr,
      82.                        size_t* lengthPtr = nullptr) {
      83.     JSVM_HandleScope handleScope;
      84.     OH_JSVM_OpenHandleScope(env, &handleScope);
      85. 
    
      86.     JSVM_Value jsSrc;
      87.     OH_JSVM_CreateStringUtf8(env, src.c_str(), src.size(), &jsSrc);
      88. 
    
      89.     uint8_t* data = dataPtr ? *dataPtr : nullptr;
      90.     size_t length = lengthPtr ? *lengthPtr : 0;
      91.     bool cacheRejected = true;
      92.     JSVM_Script script;
      93.     // 编译js代码
      94.     if (withOrigin) {
      95.         JSVM_ScriptOrigin origin {
      96.             // 以包名 helloworld 为例, 假如存在对应的 sourcemap, sourcemap 的路径可以是 /data/app/el2/100/base/com.example.helloworld/files/index.js.map
      97.             .sourceMapUrl = "/data/app/el2/100/base/com.example.helloworld/files/index.js.map",
      98.             // 源文件名字
      99.             .resourceName = "index.js",
      100.             // script 在源文件中的起始行列号
      101.             .resourceLineOffset = 0,
      102.             .resourceColumnOffset = 0,
      103.         };
      104.         OH_JSVM_CompileScriptWithOrigin(env, jsSrc, data, length, true, &cacheRejected, &origin, &script);
      105.     } else {
      106.         OH_JSVM_CompileScript(env, jsSrc, data, length, true, &cacheRejected, &script);
      107.     }
      108.     printf("Code cache is %s\n", cacheRejected ? "rejected" : "used");
      109. 
    
      110.     JSVM_Value result;
      111.     // 执行js代码
      112.     OH_JSVM_RunScript(env, script, &result);
      113. 
    
      114.     char resultStr[128];
      115.     size_t size = 0;
      116.     OH_JSVM_GetValueStringUtf8(env, result, resultStr, 128, &size);
      117. 
    
      118.     OH_JSVM_CloseHandleScope(env, handleScope);
      119. }
      120. 
    
      121. void RunWithOption(uint8_t** dataPtr, size_t* lengthPtr) {
      122.     // 创建虚拟机实例
      123.     JSVM_VM vm;
      124.     OH_JSVM_CreateVM(nullptr, &vm);
      125.     JSVM_VMScope vmScope;
      126.     OH_JSVM_OpenVMScope(vm, &vmScope);
      127. 
    
      128.     JSVM_Env env;
      129.     // 将native函数注册成js可调用的方法，hello_cb中记录该native方法的指针和参数等信息
      130.     JSVM_PropertyDescriptor descriptors[] = {
      131.         { "hello", NULL, &hello_cb, NULL, NULL, NULL, JSVM_DEFAULT }
      132.     };
      133.     OH_JSVM_CreateEnv(vm, 1, descriptors, &env);
      134.     JSVM_EnvScope envScope;
      135.     OH_JSVM_OpenEnvScope(env, &envScope);
      136.     // 执行js源码src，src中可以包含任何js语法。也可以调用已注册的native方法。
      137.     auto src = srcGlobal + "concat(hello(), ', ', 'World', ' from RunWithOption!')";
      138.     // 其中使用新增接口，可以覆盖原有 Compile 系列接口的功能且具有拓展性
      139.     RunScriptWithOption(env, src, dataPtr, lengthPtr);
      140. 
    
      141.     OH_JSVM_CloseEnvScope(env, envScope);
      142.     OH_JSVM_DestroyEnv(env);
      143.     OH_JSVM_CloseVMScope(vm, vmScope);
      144.     OH_JSVM_DestroyVM(vm);
      145. 
    
      146.     bool result = true;
      147.     OH_LOG_INFO(LOG_APP, "RunWithOption: success: %{public}d", result);
      148. }
      149. 
    
      150. void RunWithOrigin(uint8_t **dataPtr, size_t *lengthPtr) {
      151.     // 创建虚拟机实例
      152.     JSVM_VM vm;
      153.     JSVM_CreateVMOptions options;
      154.     memset(&options, 0, sizeof(options));
      155.     options.isForSnapshotting = true;
      156.     OH_JSVM_CreateVM(&options, &vm);
      157.     JSVM_VMScope vmScope;
      158.     OH_JSVM_OpenVMScope(vm, &vmScope);
      159. 
    
      160.     // 从快照中创建env
      161.     JSVM_Env env;
      162.     // 将native函数注册成js可调用的方法，hello_cb中记录该native方法的指针和参数等信息
      163.     JSVM_PropertyDescriptor descriptors[] = {
      164.         { "hello", NULL, &hello_cb, NULL, NULL, NULL, JSVM_DEFAULT }
      165.     };
      166.     OH_JSVM_CreateEnv(vm, 1, descriptors, &env);
      167.     JSVM_EnvScope envScope;
      168.     OH_JSVM_OpenEnvScope(env, &envScope);
      169.     // 执行js脚本，因为快照记录的env中定义了hello()，所以无需重新定义。dataPtr中如果保存了编译后的js脚本，就能直接执行js脚本，避免从源码重复编译。
      170.     string src = "concat(hello(), ', ', 'World', ' from RunWithOrigin!')";
      171.     RunScript(env, src, true, dataPtr, lengthPtr);
      172. 
    
      173.     OH_JSVM_CloseEnvScope(env, envScope);
      174.     OH_JSVM_DestroyEnv(env);
      175.     OH_JSVM_CloseVMScope(vm, vmScope);
      176.     OH_JSVM_DestroyVM(vm);
      177. 
    
      178.     bool result = true;
      179.     OH_LOG_INFO(LOG_APP, "RunWithOrigin: success: %{public}d", result);
      180. }
      181. 
    
      182. static JSVM_Value RunDemo(JSVM_Env env, JSVM_CallbackInfo info) {
      183.     size_t argc = 1;
      184.     JSVM_Value args[1] = {nullptr};
      185.     OH_JSVM_GetCbInfo(env, info, &argc, args, nullptr, nullptr);
      186. 
    
      187.     char* str = "WithOrigin";
      188.     size_t len = strlen(str);
      189.     JSVM_Value result = nullptr;
      190.     OH_JSVM_CreateStringUtf8(env, str, len, &result);
      191. 
    
      192.     uint8_t* data = nullptr;
      193.     size_t length = 0;
      194.     bool equal = false;
      195.     OH_JSVM_StrictEquals(env, args[0], result, &equal);
      196.     const auto run = equal ? RunWithOrigin : RunWithOption;
      197.     run(&data, &length);
      198.     delete[] data;
      199. 
    
      200.     return nullptr;
      201. }
      202. 
    
      203. // RunDemo注册回调
      204. static JSVM_CallbackStruct param[] = {
      205.     {.data = nullptr, .callback = RunDemo},
      206. };
      207. static JSVM_CallbackStruct *method = param;
      208. // RunDemo方法别名，供JS调用
      209. static JSVM_PropertyDescriptor descriptor[] = {
      210.     {"RunDemo", nullptr, method++, nullptr, nullptr, nullptr, JSVM_DEFAULT},
      211. };
      212. 
    
      213. // 样例测试js
      214. const char *srcCallNative = R"JS(RunDemo("WithOrigin"); RunDemo("WithOption"))JS";
    
    

预期输出结果

收起

自动换行

深色代码主题

复制
    
          1. RunWithOption: success: 1
      2. RunWithOrigin: success: 1
    
    

OH_JSVM_CreateCodeCache接口用法可参考[使用code cache加速编译](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-about-code-cache)。

### 使用 JSVM-API WebAssembly 接口编译 wasm module __

 **场景介绍**

JSVM-API WebAssembly 接口提供了 WebAssembly 字节码编译、WebAssembly 函数优化、WebAssembly cache 序列化和反序列化的能力。

详见[使用 JSVM-API WebAssembly 接口](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-about-wasm)。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CompileWasmModule | 将 wasm 字节码同步编译为 wasm module。如果提供了 cache 参数，先尝试将 cache 反序列为 wasm module，反序列化失败时再执行编译。如果没有 JIT 权限支持，则打印一行日志提示开发者。  
OH_JSVM_CompileWasmFunction | 将 wasm module 中指定编号的函数编译为优化后的机器码，目前只使能了最高的优化等级，函数编号的合法性由接口调用者保证。如果没有 JIT 权限支持，则打印一行日志提示开发者。  
OH_JSVM_IsWasmModuleObject | 判断传入的值是否是一个 wasm module。  
OH_JSVM_CreateWasmCache | 将 wasm module 中的机器码序列化为 wasm cache，如果 wasm module 不包含机器码，则会序列化失败。如果没有 JIT 权限支持，则打印一行日志提示开发者。  
OH_JSVM_ReleaseCache | 释放由 JSVM 接口生成的 cache。传入的 cacheType 和 cacheData 必须匹配，否则会产生未定义行为。  
  
**场景示例**

详见[使用 JSVM-API WebAssembly 接口](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-about-wasm)。

### 异常处理 __

 **场景介绍**

获取、抛出、清理JS异常。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_Throw | 抛出一个JS值  
OH_JSVM_ThrowTypeError | 抛出一个JS TypeError  
OH_JSVM_ThrowRangeError | 抛出一个JS RangeError  
OH_JSVM_IsError | 判断JS值是否为JS异常  
OH_JSVM_CreateError | 创建一个JS异常  
OH_JSVM_CreateTypeError | 创建一个JS TypeError并返回  
OH_JSVM_CreateRangeError | 创建一个JS RangeError并返回  
OH_JSVM_ThrowError | 抛出一个JS异常  
OH_JSVM_GetAndClearLastException | 清理并返回最后一个JS异常  
OH_JSVM_IsExceptionPending | 判断当前是否有异常  
OH_JSVM_GetLastErrorInfo | 获取最后一个异常的信息  
OH_JSVM_ThrowSyntaxError | 抛出一个JS SyntaxError  
OH_JSVM_CreateSyntaxError | 创建一个JS SyntaxError并返回  
  
场景示例：

以TypeError为例。创建、判断，并抛出JS TypeError。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value code = nullptr;
      2. JSVM_Value message = nullptr;
      3. OH_JSVM_CreateStringUtf8(env, "500", JSVM_AUTO_LENGTH, &code);
      4. OH_JSVM_CreateStringUtf8(env, "type error 500", JSVM_AUTO_LENGTH, &message);
      5. JSVM_Value error = nullptr;
      6. OH_JSVM_CreateTypeError(env, code, message, &error);
      7. bool isError = false;
      8. OH_JSVM_IsError(env, error, &isError);
      9. OH_JSVM_ThrowTypeError(env, nullptr, "type error1");
    
    

使用OH_JSVM_GetAndClearLastException后将异常信息以字符串形式打印

收起

自动换行

深色代码主题

复制
    
          1. if (status != JSVM_OK) // 当执行失败出现异常时
      2. {
      3.     bool isPending = false;
      4.     if (JSVM_OK == OH_JSVM_IsExceptionPending((env), &isPending) && isPending)
      5.     {
      6.         JSVM_Value error;
      7.         if (JSVM_OK == OH_JSVM_GetAndClearLastException((env), &error))
      8.         {
      9.             // 获取异常堆栈
      10.             JSVM_Value stack;
      11.             OH_JSVM_GetNamedProperty((env), error, "stack", &stack);
      12. 
    
      13.             JSVM_Value message;
      14.             OH_JSVM_GetNamedProperty((env), error, "message", &message);
      15. 
    
      16.             char stackstr[256];
      17.             OH_JSVM_GetValueStringUtf8(env, stack, stackstr, 256, nullptr);
      18.             OH_LOG_INFO(LOG_APP, "JSVM error stack: %{public}s", stackstr);
      19. 
    
      20.             char messagestr[256];
      21.             OH_JSVM_GetValueStringUtf8(env, message, messagestr, 256, nullptr);
      22.             OH_LOG_INFO(LOG_APP, "JSVM error message: %{public}s", messagestr);
      23.         }
      24.     }
      25. }
    
    

### 对象生命周期管理 __

在调用JSVM-API接口时，底层VM堆中的对象可能会作为JSVM_Values返回句柄。这些句柄必须在Native方法销毁或主动释放掉前，使其关联的对象处于“活动”状态，防止被引擎回收掉。

当对象句柄被返回时，它们与一个“scope”相关联。默认作用域的生命周期与Native方法调用的生命周期相关联，这些句柄及关联的对象将在Native方法的生命周期内保持活动状态。

然而，在许多情况下，句柄必须保持有效的时间范围并不与Native方法的生命周期相同。下面将介绍可用于更改句柄的生命周期的JSVM-API方法。

**对象生命周期管理接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_OpenHandleScope | 打开一个新的scope，在关闭该scope之前创建的对象在scope范围内不会被GC回收  
OH_JSVM_CloseHandleScope | 关闭一个scope，在此scope范围内创建的对象在关闭scope后可以被GC回收  
OH_JSVM_OpenEscapableHandleScope | 打开一个新的scope逃逸handle scope，在关闭该scope之前创建的对象与父作用域有相同的生命周期  
OH_JSVM_CloseEscapableHandleScope | 关闭一个scope，在此scope范围外创建的对象不受父作用域保护  
OH_JSVM_EscapeHandle | 将 JavaScript 对象的句柄提升到外部作用域，确保在外部作用域中可以持续地使用该对象  
OH_JSVM_CreateReference | 以指定的引用计数为JavaScript对象创建一个新的引用，该引用将指向传入的对象，引用允许在不同的上下文中使用和共享对象，并且可以有效地监测对象的生命周期  
OH_JSVM_DeleteReference | 释放由 OH_JSVM_CreateReference 创建的引用，确保对象在不再被使用时能够被正确地释放和回收，避免内存泄漏  
OH_JSVM_ReferenceRef | 增加由OH_JSVM_CreateReference 创建的引用的引用计数，以确保对象在有引用时不会被提前释放  
OH_JSVM_ReferenceUnref | 减少由OH_JSVM_CreateReference 创建的引用的引用计数，以确保没有任何引用指向该对象时能正确地释放和回收  
OH_JSVM_GetReferenceValue | 返回由 OH_JSVM_CreateReference 创建的引用的对象  
OH_JSVM_RetainScript | 持久化保存一个 JSVM_Script, 使其能够跨过当前 scope 使用  
OH_JSVM_ReleaseScript | 释放持久化保存过的 JSVM_Script，释放之后 JSVM_Script 不再可用，应当置为空  
  
场景示例：

通过handle scope保护在scope范围内创建的对象在该范围内不被回收。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_HandleScope scope;
      2. OH_JSVM_OpenHandleScope(env, &scope);
      3. JSVM_Value obj = nullptr;
      4. OH_JSVM_CreateObject(env, &obj);
      5. OH_JSVM_CloseHandleScope(env, scope);
    
    

通过escapable handle scope保护在scope范围内创建的对象在父作用域范围内不被回收

收起

自动换行

深色代码主题

复制
    
          1. JSVM_EscapableHandleScope scope;
      2. JSVM_CALL(OH_JSVM_OpenEscapableHandleScope(env, &scope));
      3. JSVM_Value output = NULL;
      4. JSVM_Value escapee = NULL;
      5. JSVM_CALL(OH_JSVM_CreateObject(env, &output));
      6. JSVM_CALL(OH_JSVM_EscapeHandle(env, scope, output, &escapee));
      7. JSVM_CALL(OH_JSVM_CloseEscapableHandleScope(env, scope));
      8. return escapee;
    
    

通过CreateReference创建对象引用和释放

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value obj = nullptr;
      2. OH_JSVM_CreateObject(env, &obj);
      3. // 创建引用
      4. JSVM_Ref reference;
      5. OH_JSVM_CreateReference(env, obj, 1, &reference);
      6. 
    
      7. // 使用引用
      8. JSVM_Value result;
      9. OH_JSVM_GetReferenceValue(env, reference, &result);
      10. 
    
      11. // 释放引用
      12. OH_JSVM_DeleteReference(env, reference);
    
    

通过 RetainScript 持久化保存 JSVM_Script 并使用

收起

自动换行

深色代码主题

复制
    
          1. JSVM_HandleScope scope;
      2. JSVM_CALL(OH_JSVM_OpenHandleScope(env, &scope));
      3. JSVM_Script script;
      4. JSVM_Value jsSrc;
      5. std::string src(R"JS(
      6. let a = 37;
      7. a = a * 9;
      8. )JS");
      9. JSVM_CALL(OH_JSVM_CreateStringUtf8(env, src.c_str(), src.size(), &jsSrc));
      10. JSVM_CALL(OH_JSVM_CompileScriptWithOptions(env, jsSrc, 0, nullptr, &script));
      11. JSVM_CALL(OH_JSVM_RetainScript(env, script));
      12. JSVM_CALL(OH_JSVM_CloseHandleScope(env, scope));
      13. 
    
      14. // 使用JSVM_Script
      15. JSVM_CALL(OH_JSVM_OpenHandleScope(env, &scope));
      16. JSVM_Value result;
      17. JSVM_CALL(OH_JSVM_RunScript(env, script, &result));
      18. 
    
      19. // 释放JSVM_Script，并置空
      20. JSVM_CALL(OH_JSVM_ReleaseScript(env, script));
      21. script = nullptr;
      22. JSVM_CALL(OH_JSVM_CloseHandleScope(env, scope));
    
    

### 创建JS对象类型和基本类型 __

 **场景介绍**

创建JS对象类型和基本类型。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CreateArray | 创建一个新的 JavaScript 数组对象  
OH_JSVM_CreateArrayWithLength | 创建一个指定长度的 JavaScript 数组对象  
OH_JSVM_CreateArraybuffer | 创建一个指定大小的 ArrayBuffer 对象  
OH_JSVM_CreateDate | 创建了一个表示给定毫秒数的 Date 对象  
OH_JSVM_CreateExternal | 创建一个包装了外部指针的 JavaScript 对象  
OH_JSVM_CreateObject | 创建一个默认的JavaScript Object对象  
OH_JSVM_CreateSymbol | 根据给定的描述符创建一个 Symbol 对象  
OH_JSVM_SymbolFor | 在全局注册表中搜索具有给定描述的现有Symbol，如果该Symbol已经存在，它将被返回，否则将在注册表中创建一个新Symbol  
OH_JSVM_CreateTypedarray | 在现有的 ArrayBuffer 上创建一个 JavaScript TypedArray 对象，TypedArray 对象在底层数据缓冲区上提供类似数组的视图，其中每个元素都具有相同的底层二进制标量数据类型  
OH_JSVM_CreateDataview | 在现有的 ArrayBuffer 上创建一个 JavaScript DataView 对象，DataView 对象在底层数据缓冲区上提供类似数组的视图  
OH_JSVM_CreateInt32 | 根据 Int32_t 类型对象创建 JavaScript number 对象  
OH_JSVM_CreateUint32 | 根据 Uint32_t 类型对象创建 JavaScript number 对象  
OH_JSVM_CreateInt64 | 根据 Int64_t 类型对象创建 JavaScript number 对象  
OH_JSVM_CreateDouble | 根据 Double 类型对象创建 JavaScript number 对象  
OH_JSVM_CreateBigintInt64 | 根据 Int64 类型对象创建 JavaScript Bigint 对象  
OH_JSVM_CreateBigintUint64 | 根据 Uint64 类型对象创建 JavaScript Bigint 对象  
OH_JSVM_CreateBigintWords | 根据给定的 Uint64_t 数组创建一个 JavaScript BigInt 对象  
OH_JSVM_CreateStringLatin1 | 根据 Latin-1 编码的字符串创建一个 JavaScript string 对象  
OH_JSVM_CreateStringUtf16 | 根据 Utf16 编码的字符串创建一个 JavaScript string 对象  
OH_JSVM_CreateStringUtf8 | 根据 Utf8 编码的字符串创建一个 JavaScript string 对象  
OH_JSVM_CreateMap | 创建一个新的 JavaScript Map对象  
OH_JSVM_CreateRegExp | 根据输入的字符串创建一个JavaScript 正则对象  
OH_JSVM_CreateSet | 创建一个新的 JavaScript Set对象  
  
场景示例:

创建指定长度的JavaScript数组。

收起

自动换行

深色代码主题

复制
    
          1. size_t arrayLength = 2;
      2. JSVM_Value arr;
      3. 
    
      4. OH_JSVM_CreateArrayWithLength(env, arrayLength, &arr);
      5. for (uint32_t i = 0; i < arrayLength; i++)
      6. {
      7.     JSVM_Value element;
      8.     OH_JSVM_CreateUint32(env, i * 2, &element);
      9.     OH_JSVM_SetElement(env, arr, i, element);
      10. }
    
    

创建TypedArray，以Int32Array为例：

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value arrayBuffer = nullptr;
      2. void *arrayBufferPtr = nullptr;
      3. size_t arrayBufferSize = 16;
      4. size_t typedArrayLength = 4;
      5. OH_JSVM_CreateArraybuffer(env, arrayBufferSize, &arrayBufferPtr, &arrayBuffer);
      6. 
    
      7. void *tmpArrayBufferPtr = nullptr;
      8. size_t arrayBufferLength = 0;
      9. OH_JSVM_GetArraybufferInfo(env, arrayBuffer, &tmpArrayBufferPtr, &arrayBufferLength);
      10. 
    
      11. JSVM_Value result;
      12. OH_JSVM_CreateTypedarray(env, JSVM_TypedarrayType::JSVM_INT32_ARRAY, typedArrayLength, arrayBuffer, 0, &result);
      13. return result;
    
    

创建number和string：

收起

自动换行

深色代码主题

复制
    
          1. const char *testStringStr = "test";
      2. JSVM_Value testString = nullptr;
      3. OH_JSVM_CreateStringUtf8(env, testStringStr, strlen(testStringStr), &testString);
      4. 
    
      5. JSVM_Value testNumber1 = nullptr;
      6. JSVM_Value testNumber2 = nullptr;
      7. OH_JSVM_CreateDouble(env, 10.1, &testNumber1);
      8. OH_JSVM_CreateInt32(env, 10, &testNumber2);
    
    

创建Map：

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value = nullptr;
      2. OH_JSVM_CreateMap(env, &value);
    
    

创建RegExp：

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value = nullptr;
      2. const char testStr[] = "ab+c";
      3. OH_JSVM_CreateStringUtf8(env, testStr, strlen(testStr), &value);
      4. JSVM_Value result = nullptr;
      5. OH_JSVM_CreateRegExp(env, value, JSVM_RegExpFlags::JSVM_REGEXP_GLOBAL, &result);
    
    

创建Set：

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value = nullptr;
      2. OH_JSVM_CreateSet(env, &value);
    
    

### 从JS类型获取C类型&JS类型信息 __

 **场景介绍**

从JS类型获取C类型&JS类型信息。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_GetArrayLength | 返回 Array 对象的长度  
OH_JSVM_GetArraybufferInfo | 检索 ArrayBuffer 的底层数据缓冲区及其长度  
OH_JSVM_GetPrototype | 获取给定 JavaScript 对象的原型  
OH_JSVM_GetTypedarrayInfo | 获取 TypedArray（类型化数组）对象的信息  
OH_JSVM_GetDataviewInfo | 获取 Dataview 对象的信息  
OH_JSVM_GetDateValue | 获取给定 JavaScript Date 的时间值的 Double 基础类型值  
OH_JSVM_GetValueBool | 获取给定 JavaScript Boolean 的 C 布尔基础类型值  
OH_JSVM_GetValueDouble | 获取给定 JavaScript number 的 Double 基础类型值  
OH_JSVM_GetValueBigintInt64 | 获取给定 JavaScript BigInt 的 Int64_t 基础类型值  
OH_JSVM_GetValueBigintUint64 | 获取给定 JavaScript BigInt 的 Uint64_t 基础类型值  
OH_JSVM_GetValueBigintWords | 获取给定 JavaScript BigInt 对象的底层数据，即 BigInt 数据的字词表示  
OH_JSVM_GetValueExternal | 获取先前传递给 OH_JSVM_CreateExternal 的外部数据指针  
OH_JSVM_GetValueInt32 | 获取给定 JavaScript number 的 Int32 基础类型值  
OH_JSVM_GetValueInt64 | 获取给定 JavaScript number 的 Int64 基础类型值  
OH_JSVM_GetValueStringLatin1 | 获取给定 JavaScript string 对象的 Latin1 编码字符串  
OH_JSVM_GetValueStringUtf8 | 获取给定 JavaScript string 对象的 Utf8 编码字符串  
OH_JSVM_GetValueStringUtf16 | 获取给定 JavaScript string 对象的 Utf16 编码字符串  
OH_JSVM_GetValueUint32 | 获取给定 JavaScript number 的 Uint32 基础类型值  
OH_JSVM_GetBoolean | 返回用于表示给定布尔值的 JavaScript 单例对象  
OH_JSVM_GetGlobal | 返回当前环境中的全局 global 对象  
OH_JSVM_GetNull | 返回 JavaScript null 对象  
OH_JSVM_GetUndefined | 返回 JavaScript Undefined 对象  
  
场景示例：

创建64位的 BigInt，并获取64位 Int 值。

收起

自动换行

深色代码主题

复制
    
          1. int64_t testValue = INT64_MAX;
      2. JSVM_Value result = nullptr;
      3. OH_JSVM_CreateBigintInt64(env, testValue, &result);
      4. int64_t resultValue = 0;
      5. bool flag = false;
      6. OH_JSVM_GetValueBigintInt64(env, result, &resultValue, &flag);
    
    

创建一个 Int32Array，并获取其长度、byteOffset 等信息。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value arrayBuffer = nullptr;
      2. void *arrayBufferPtr = nullptr;
      3. size_t arrayBufferSize = 16;
      4. size_t typedArrayLength = 4;
      5. OH_JSVM_CreateArraybuffer(env, arrayBufferSize, &arrayBufferPtr, &arrayBuffer);
      6. 
    
      7. bool isArrayBuffer = false;
      8. OH_JSVM_IsArraybuffer(env, arrayBuffer, &isArrayBuffer);
      9. 
    
      10. JSVM_Value result;
      11. OH_JSVM_CreateTypedarray(env, JSVM_TypedarrayType::JSVM_INT32_ARRAY, typedArrayLength, arrayBuffer, 0, &result);
      12. 
    
      13. bool isTypedArray = false;
      14. OH_JSVM_IsTypedarray(env, result, &isTypedArray);
      15. 
    
      16. 
    
      17. JSVM_TypedarrayType type;
      18. size_t length = 0;
      19. void *data = nullptr;
      20. JSVM_Value retArrayBuffer;
      21. size_t byteOffset = -1;
      22. OH_JSVM_GetTypedarrayInfo(env, result, &type, &length, &data, &retArrayBuffer, &byteOffset);
      23. 
    
      24. 
    
      25. bool retIsArrayBuffer = false;
      26. OH_JSVM_IsArraybuffer(env, retArrayBuffer, &retIsArrayBuffer);
      27. void *tmpArrayBufferPtr = nullptr;
      28. size_t arrayBufferLength = 0;
      29. OH_JSVM_GetArraybufferInfo(env, retArrayBuffer, &tmpArrayBufferPtr, &arrayBufferLength);
    
    

根据 UTF-8 编码的 C 字符串创建一个 JavaScript 字符串，以及获取给定 JavaScript 字符串的 UTF-8 编码 C 字符串。

收起

自动换行

深色代码主题

复制
    
          1. const char *testStringStr = "testString";
      2. JSVM_Value testString = nullptr;
      3. JSVM_CALL(OH_JSVM_CreateStringUtf8(env, testStringStr, strlen(testStringStr), &testString));
      4. 
    
      5. char buffer[128];
      6. size_t bufferSize = 128;
      7. size_t copied = 0;
      8. 
    
      9. JSVM_CALL(OH_JSVM_GetValueStringUtf8(env, testString, buffer, bufferSize, &copied));
    
    

### JS值操作和抽象操作 __

 **场景介绍**

JS值操作和抽象操作。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CoerceToBool | 将目标值转换为 Boolean 类型对象  
OH_JSVM_CoerceToNumber | 将目标值转换为 Number 类型对象  
OH_JSVM_CoerceToObject | 将目标值转换为 Object 类型对象  
OH_JSVM_CoerceToString | 将目标值转换为 String 类型对象  
OH_JSVM_CoerceToBigInt | 将目标值转换为 BigInt 类型对象  
OH_JSVM_Typeof | 返回 JavaScript 对象的类型  
OH_JSVM_Instanceof | 判断一个对象是否是某个构造函数的实例  
OH_JSVM_IsArray | 判断一个 JavaScript 对象是否为 Array 类型对象  
OH_JSVM_IsArraybuffer | 判断一个 JavaScript 对象是否为 ArrayBuffer 类型对象  
OH_JSVM_IsDate | 判断一个 JavaScript 对象是否为 Date 类型对象  
OH_JSVM_IsTypedarray | 判断一个 JavaScript 对象是否为 TypedArray 类型对象  
OH_JSVM_IsDataview | 判断一个 JavaScript 对象是否为 DataView 类型对象  
OH_JSVM_IsUndefined | 此API检查传入的值是否为Undefined。这相当于JS中的value === undefined。  
OH_JSVM_IsNull | 此API检查传入的值是否为Null对象。这相当于JS中的value === null。  
OH_JSVM_IsNullOrUndefined | 此API检查传入的值是否为Null或Undefined。这相当于JS中的value == null。  
OH_JSVM_IsBoolean | 此API检查传入的值是否为Boolean。这相当于JS中的typeof value === 'boolean'。  
OH_JSVM_IsNumber | 此API检查传入的值是否为Number。这相当于JS中的typeof value === 'number'。  
OH_JSVM_IsString | 此API检查传入的值是否为String。这相当于JS中的typeof value === 'string'。  
OH_JSVM_IsSymbol | 此API检查传入的值是否为Symbol。这相当于JS中的typeof value === 'symbol'。  
OH_JSVM_IsFunction | 此API检查传入的值是否为Function。这相当于JS中的typeof value === 'function'。  
OH_JSVM_IsObject | 此API检查传入的值是否为Object。  
OH_JSVM_IsBigInt | 此API检查传入的值是否为BigInt。这相当于JS中的typeof value === 'bigint'。  
OH_JSVM_IsConstructor | 此API检查传入的值是否为构造函数。  
OH_JSVM_IsMap | 此API检查传入的值是否为Map。  
OH_JSVM_IsSet | 此API检查传入的值是否为Set。  
OH_JSVM_IsRegExp | 此API检查传入的值是否为RegExp。  
OH_JSVM_StrictEquals | 判断两个 JSVM_Value 对象是否严格相等  
OH_JSVM_Equals | 判断两个 JSVM_Value 对象是否宽松相等  
OH_JSVM_DetachArraybuffer | 调用 ArrayBuffer 对象的Detach操作  
OH_JSVM_IsDetachedArraybuffer | 检查给定的 ArrayBuffer 是否已被分离(detached)  
  
场景示例:

判断JS值是否为Array类型

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value array = nullptr;
      2. OH_JSVM_CreateArray(env, &array);
      3. bool isArray = false;
      4. OH_JSVM_IsArray(env, array, &isArray);
    
    

将int32类型的目标值转换为string类型

收起

自动换行

深色代码主题

复制
    
          1. int32_t num = 123;
      2. JSVM_Value intValue;
      3. OH_JSVM_CreateInt32(env, num, &intValue);
      4. JSVM_Value stringValue;
      5. OH_JSVM_CoerceToString(env, intValue, &stringValue);
      6. 
    
      7. char buffer[128];
      8. size_t bufferSize = 128;
      9. size_t copied = 0;
      10. 
    
      11. OH_JSVM_GetValueStringUtf8(env, stringValue, buffer, bufferSize, &copied);
      12. // buffer:"123";
    
    

将boolean类型的目标值转换为bigint类型

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value boolValue;
      2. OH_JSVM_GetBoolean(env, false, &boolValue);
      3. JSVM_Value bigIntValue;
      4. OH_JSVM_CoerceToBigInt(env, boolValue, &bigIntValue);
    
    

判断两个JSVM_Value对象类型是否严格相同：先比较操作数类型，操作数类型不同就是不相等，操作数类型相同时，比较值是否相等，相等才返回true。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value = nullptr;
      2. JSVM_Value value1 = nullptr;
      3. OH_JSVM_CreateArray(env, &value);
      4. 
    
      5. OH_JSVM_CreateInt32(env, 10, &value1);
      6. bool isArray = true;
      7. OH_JSVM_StrictEquals(env, value, value, &isArray);
    
    

判断两个JSVM_Value对象类型是否宽松相同：判断两个操作数的类型是否相同，若不相同，且可以转换为相同的数据类型，转换为相同的数据类型后，值做严格相等比较，其他的都返回false。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_HandleScope handleScope;
      2. OH_JSVM_OpenHandleScope(env, &handleScope);
      3. const char testStr[] = "1";
      4. JSVM_Value lhs = nullptr;
      5. OH_JSVM_CreateStringUtf8(env, testStr, strlen(testStr), &lhs);
      6. JSVM_Value rhs;
      7. OH_JSVM_CreateInt32(env, 1, &rhs);
      8. bool isEquals = false;
      9. OH_JSVM_Equals(env, lhs, rhs, &isEquals); // 这里isEquals的值是true
      10. OH_JSVM_CloseHandleScope(env, handleScope);
    
    

判断传入的JS值是否为构造函数

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value SayHello(JSVM_Env env, JSVM_CallbackInfo info)
      2. {
      3.     return nullptr;
      4. }
      5. JSVM_Value value = nullptr;
      6. JSVM_CallbackStruct param;
      7. param.data = nullptr;
      8. param.callback = SayHello;
      9. OH_JSVM_CreateFunction(env, "func", JSVM_AUTO_LENGTH, &param, &value);
      10. bool isConstructor = false;
      11. OH_JSVM_IsConstructor(env, value, &isConstructor); // 这里isConstructor的值是true
    
    

判断传入的JS值是否为Map类型

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value = nullptr;
      2. OH_JSVM_CreateMap(env, &value);
      3. bool isMap = false;
      4. OH_JSVM_IsMap(env, value, &isMap); // 这里isMap的值是true
    
    

判断传入的JS值是否为Set类型

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value;
      2. OH_JSVM_CreateSet(env, &value);
      3. bool isSet = false;
      4. OH_JSVM_IsSet(env, value, &isSet); // 这里isSet的值是true
    
    

判断JS值是否为RegExp类型

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value value = nullptr;
      2. const char testStr[] = "ab+c";
      3. OH_JSVM_CreateStringUtf8(env, testStr, strlen(testStr), &value);
      4. JSVM_Value result = nullptr;
      5. OH_JSVM_CreateRegExp(env, value, JSVM_RegExpFlags::JSVM_REGEXP_GLOBAL, &result);
      6. bool isRegExp = false;
      7. OH_JSVM_IsRegExp(env, result, &isRegExp);
    
    

### JS属性操作 __

 **场景介绍**

JS对象属性的增加、删除、获取和判断。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_GetPropertyNames | 获取给定对象的所有可枚举属性名称, 结果变量将存储一个包含所有可枚举属性名称的JavaScript数组。  
OH_JSVM_GetAllPropertyNames | 获取给定对象的所有可用属性名称, 结果变量将存储一个包含所有可枚举属性名称的JavaScript数组。  
OH_JSVM_SetProperty | 为给定对象设置一个属性。  
OH_JSVM_GetProperty | 用给定的属性的名称，检索目标对象的属性。  
OH_JSVM_HasProperty | 用给定的属性的名称，查询目标对象是否有此属性。  
OH_JSVM_DeleteProperty | 用给定的属性的名称，删除目标对象属性。  
OH_JSVM_HasOwnProperty | 检查目标对象是否具有指定的自有属性。  
OH_JSVM_SetNamedProperty | 用给定的属性的名称为目标对象设置属性，此方法等效于使用从作为 utf8Name 传入的字符串创建的 JSVM_Value 调用 OH_JSVM_SetProperty。  
OH_JSVM_GetNamedProperty | 用给定的属性的名称，检索目标对象的属性，此方法等效于使用从作为 utf8Name 传入的字符串创建的 JSVM_Value 调用 OH_JSVM_GetProperty。  
OH_JSVM_HasNamedProperty | 用给定的属性的名称，查询目标对象是否有此属性，此方法等效于使用从作为 utf8Name 传入的字符串创建的 JSVM_Value 调用 OH_JSVM_HasProperty。  
OH_JSVM_SetElement | 在给定对象的指定索引处设置元素。  
OH_JSVM_GetElement | 获取给定对象指定索引处的元素。  
OH_JSVM_HasElement | 若给定对象的指定索引处拥有属性，获取该元素。  
OH_JSVM_DeleteElement | 尝试删除给定对象的指定索引处的元素。  
OH_JSVM_DefineProperties | 批量的向给定对象中定义属性。  
OH_JSVM_ObjectFreeze | 冻结给定的对象,防止向其添加新属性，删除现有属性，防止更改现有属性的可枚举性、可配置性或可写性，并防止更改现有属性的值。  
OH_JSVM_ObjectSeal | 密封给定的对象。这可以防止向其添加新属性，以及将所有现有属性标记为不可配置。  
OH_JSVM_ObjectSetPrototypeOf | 为给定对象设置一个原型。  
OH_JSVM_ObjectGetPrototypeOf | 获取给定JavaScript对象的原型。  
  
场景示例:

JS对象属性的增加、删除、获取和判断。

收起

自动换行

深色代码主题

复制
    
          1. // 创建一个空对象
      2. JSVM_Value myObject = nullptr;
      3. OH_JSVM_CreateObject(env, &myObject);
      4. 
    
      5. // 设置属性
      6. const char *testNameStr = "John Doe";
      7. JSVM_Value propValue = nullptr;
      8. JSVM_Value key = nullptr;
      9. OH_JSVM_CreateStringUtf8(env, "name", JSVM_AUTO_LENGTH, &key);
      10. OH_JSVM_CreateStringUtf8(env, testNameStr, strlen(testNameStr), &propValue);
      11. OH_JSVM_SetProperty(env, myObject, key, propValue);
      12. 
    
      13. // 获取属性
      14. JSVM_Value propResult = nullptr;
      15. OH_JSVM_GetProperty(env, myObject, key, &propResult);
      16. 
    
      17. // 检查属性是否存在
      18. bool hasProperty = false;
      19. OH_JSVM_HasNamedProperty(env, myObject, "name", &hasProperty);
      20.     // 属性存在，做相应处理...
      21.     if (hasProperty)
      22.     {
      23.         // 获取对象的所有属性名
      24.         JSVM_Value propNames = nullptr;
      25.         OH_JSVM_GetPropertyNames(env, myObject, &propNames);
      26. 
    
      27.         bool isArray = false;
      28.         OH_JSVM_IsArray(env, propNames, &isArray);
      29. 
    
      30.         uint32_t arrayLength = 0;
      31.         OH_JSVM_GetArrayLength(env, propNames, &arrayLength);
      32.         // 遍历属性元素
      33.         for (uint32_t i = 0; i < arrayLength; i++)
      34.         {
      35.             bool hasElement = false;
      36.             OH_JSVM_HasElement(env, propNames, i, &hasElement);
      37. 
    
      38.             JSVM_Value propName = nullptr;
      39.             OH_JSVM_GetElement(env, propNames, i, &propName);
      40. 
    
      41.             bool hasProp = false;
      42.             OH_JSVM_HasProperty(env, myObject, propName, &hasProp);
      43. 
    
      44.             JSVM_Value propValue = nullptr;
      45.             OH_JSVM_GetProperty(env, myObject, propName, &propValue);
      46.         }
      47.     }
      48. 
    
      49. // 删除属性
      50. OH_JSVM_DeleteProperty(env, myObject, key, &hasProperty);
      51. 
    
      52. // 设置对象原型
      53. JSVM_Value value;
      54. OH_JSVM_CreateSet(env, &value);
      55. OH_JSVM_ObjectSetPrototypeOf(env, myObject, value);
      56. 
    
      57. // 获取对象原型
      58. JSVM_Value proto;
      59. OH_JSVM_ObjectGetPrototypeOf(env, myObject, &proto);
    
    

### JS函数操作 __

 **场景介绍**

JS函数操作。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CallFunction | 在C/C++侧调用JS方法  
OH_JSVM_CreateFunction | 用于创建JavaScript函数,用于从JavaScript环境中调用C/C++代码中的函数  
OH_JSVM_GetCbInfo | 从给定的callback info中获取有关调用的详细信息，如参数和this指针  
OH_JSVM_GetNewTarget | 获取构造函数调用的new.target  
OH_JSVM_NewInstance | 通过给定的构造函数，构建一个实例  
OH_JSVM_CreateFunctionWithScript | 根据传入的函数体和函数参数列表，创建一个新的 JavaScript Function对象  
  
场景示例:

创建JavaScript函数操作。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value SayHello(JSVM_Env env, JSVM_CallbackInfo info)
      2. {
      3.     printf("Hello\n");
      4.     JSVM_Value ret;
      5.     OH_JSVM_CreateInt32(env, 2, &ret);
      6.     return ret;
      7. }
      8. 
    
      9. static JSVM_Value JsvmCreateFunction(JSVM_Env env, JSVM_CallbackInfo info)
      10. {
      11.     JSVM_CallbackStruct param;
      12.     param.data = nullptr;
      13.     param.callback = SayHello;
      14. 
    
      15.     JSVM_Value funcValue = nullptr;
      16.     JSVM_Status status = OH_JSVM_CreateFunction(env, "func", JSVM_AUTO_LENGTH, &param, &funcValue);
      17.     return funcValue;
      18. }
    
    

在C/C++侧调用JS方法。

收起

自动换行

深色代码主题

复制
    
          1. static JSVM_Value CallFunction(JSVM_Env env, JSVM_CallbackInfo info)
      2. {
      3.     size_t argc = 1;
      4.     JSVM_Value args[1];
      5.     JSVM_CALL(OH_JSVM_GetCbInfo(env, info, &argc, args, NULL, NULL));
      6.     if (argc < 1) {
      7.         OH_LOG_ERROR(LOG_APP, "Wrong number of arguments");
      8.         return nullptr;
      9.     }
      10. 
    
      11.     JSVM_ValueType valuetype;
      12.     JSVM_CALL(OH_JSVM_Typeof(env, args[0], &valuetype));
      13.     if (valuetype != JSVM_ValueType::JSVM_FUNCTION) {
      14.         OH_LOG_ERROR(LOG_APP, "Wrong type of argment. Expects a function.");
      15.         return nullptr;
      16.     }
      17. 
    
      18.     JSVM_Value global;
      19.     JSVM_CALL(OH_JSVM_GetGlobal(env, &global));
      20. 
    
      21.     JSVM_Value ret;
      22.     JSVM_CALL(OH_JSVM_CallFunction(env, global, args[0], 0, nullptr, &ret));
      23.     return ret;
      24. }
    
    

创建JavaScript函数:

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Value script;
      2. OH_JSVM_CreateStringUtf8(env, "return a + b;", JSVM_AUTO_LENGTH, &script);
      3. JSVM_Value param1;
      4. JSVM_Value param2;
      5. OH_JSVM_CreateStringUtf8(env, "a", JSVM_AUTO_LENGTH, &param1);
      6. OH_JSVM_CreateStringUtf8(env, "b", JSVM_AUTO_LENGTH, &param2);
      7. JSVM_Value argus[] = {param1, param2};
      8. JSVM_Value func;
      9. OH_JSVM_CreateFunctionWithScript(env, "add", JSVM_AUTO_LENGTH, 2, argus, script, &func);
    
    

### 对象绑定操作 __

 **场景介绍**

对象绑定操作。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_DefineClass | 用于在JavaScript中定义一个类，并与对应的C类进行封装和交互。它提供了创建类的构造函数、定义属性和方法的能力，以及在C和JavaScript之间进行数据交互的支持。  
OH_JSVM_Wrap | 在 JavaScript 对象中封装原生实例。稍后可以使用 OH_JSVM_Unwrap() 检索原生实例。  
OH_JSVM_Unwrap | 使用 OH_JSVM_Wrap() 检索先前封装在 JavaScript 对象中的原生实例。  
OH_JSVM_RemoveWrap | 检索先前封装在 JavaScript 对象中的原生实例并移除封装。  
OH_JSVM_TypeTagObject | 将 type_tag 指针的值与 JavaScript 对象或外部对象相关联。  
OH_JSVM_CheckObjectTypeTag | 检查给定的类型标签是否与对象上的类型标签匹配。  
OH_JSVM_AddFinalizer | 为对象添加 JSVM_Finalize 回调，以便在 JavaScript 对象被垃圾回收时调用来释放原生对象。  
OH_JSVM_DefineClassWithPropertyHandler | 定义一个具有给定类名、构造函数、属性和回调处理程序的JavaScript类，并作为函数回调进行调用。属性操作包括getter、setter、deleter、enumerator等。  
OH_JSVM_DefineClassWithOptions | 定义一个具有给定类名、构造函数、属性和回调处理程序、父类的JavaScript类，并根据传入了DefineClassOptions来决定是否需要为所定义的Class设置属性代理、预留internal-field槽位、为class作为函数进行调用时设置函数回调。  
  
场景示例：

对象绑定操作。

收起

自动换行

深色代码主题

复制
    
          1. static int aa = 0;
      2. 
    
      3. static JSVM_Value AssertEqual(JSVM_Env env, JSVM_CallbackInfo info)
      4. {
      5.     size_t argc = 2;
      6.     JSVM_Value args[2];
      7.     JSVM_CALL(OH_JSVM_GetCbInfo(env, info, &argc, args, NULL, NULL));
      8. 
    
      9.     bool isStrictEquals = false;
      10.     OH_JSVM_StrictEquals(env, args[0], args[1], &isStrictEquals);
      11.     return nullptr;
      12. }
      13. 
    
      14. static napi_value TestWrap(napi_env env1, napi_callback_info info)
      15. {
      16.     OH_LOG_ERROR(LOG_APP, "testWrap start");
      17.     JSVM_InitOptions init_options;
      18.     memset(&init_options, 0, sizeof(init_options));
      19.     if (aa == 0) {
      20.         OH_JSVM_Init(&init_options);
      21.         aa++;
      22.     }
      23.     JSVM_VM vm;
      24.     JSVM_CreateVMOptions options;
      25.     memset(&options, 0, sizeof(options));
      26.     OH_JSVM_CreateVM(&options, &vm);
      27.     JSVM_VMScope vm_scope;
      28.     OH_JSVM_OpenVMScope(vm, &vm_scope);
      29.     JSVM_Env env;
      30.     JSVM_CallbackStruct param[1];
      31.     param[0].data = nullptr;
      32.     param[0].callback = AssertEqual;
      33.     JSVM_PropertyDescriptor descriptor[] = {
      34.         {"assertEqual", NULL, &param[0], NULL, NULL, NULL, JSVM_DEFAULT},
      35.     };
      36.     OH_JSVM_CreateEnv(vm, sizeof(descriptor) / sizeof(descriptor[0]), descriptor, &env);
      37.     JSVM_EnvScope envScope;
      38.     OH_JSVM_OpenEnvScope(env, &envScope);
      39.     JSVM_HandleScope handlescope;
      40.     OH_JSVM_OpenHandleScope(env, &handlescope);
      41.     JSVM_Value testClass = nullptr;
      42.     JSVM_CallbackStruct param1;
      43.     param1.data = nullptr;
      44.     param1.callback = [](JSVM_Env env, JSVM_CallbackInfo info) -> JSVM_Value {
      45.         JSVM_Value thisVar = nullptr;
      46.         OH_JSVM_GetCbInfo(env, info, nullptr, nullptr, &thisVar, nullptr);
      47. 
    
      48.         return thisVar;
      49.     };
      50.     OH_JSVM_DefineClass(env, "TestClass", JSVM_AUTO_LENGTH, &param1, 0, nullptr, &testClass);
      51. 
    
      52.     JSVM_Value instanceValue = nullptr;
      53.     OH_JSVM_NewInstance(env, testClass, 0, nullptr, &instanceValue);
      54. 
    
      55.     const char *testStr = "test";
      56.     OH_JSVM_Wrap(
      57.         env, instanceValue, (void *)testStr, [](JSVM_Env env, void *data, void *hint) {}, nullptr, nullptr);
      58.     const char *tmpTestStr = nullptr;
      59.     OH_JSVM_Unwrap(env, instanceValue, (void **)&tmpTestStr);
      60.     const char *tmpTestStr1 = nullptr;
      61.     OH_JSVM_RemoveWrap(env, instanceValue, (void **)&tmpTestStr1);
      62.     OH_JSVM_Unwrap(env, instanceValue, (void **)&tmpTestStr1);
      63.     OH_JSVM_CloseHandleScope(env, handlescope);
      64.     OH_JSVM_CloseEnvScope(env, envScope);
      65.     OH_JSVM_DestroyEnv(env);
      66.     OH_JSVM_CloseVMScope(vm, vm_scope);
      67.     OH_JSVM_DestroyVM(vm);
      68.     OH_LOG_ERROR(LOG_APP, "testWrap pass");
      69.     return nullptr;
      70. }
    
    

场景示例：

对象绑定及监听拦截属性操作。

收起

自动换行

深色代码主题

复制
    
          1. static int aa = 0;
      2. static JSVM_Value hello(JSVM_Env env, JSVM_CallbackInfo info) {
      3.     JSVM_Value output;
      4.     void *data = nullptr;
      5.     OH_JSVM_GetCbInfo(env, info, nullptr, nullptr, nullptr, &data);
      6.     OH_JSVM_CreateStringUtf8(env, (char *)data, strlen((char *)data), &output);
      7.     return output;
      8. }
      9. 
    
      10. static JSVM_CallbackStruct hello_cb = {hello, (void *)"Hello"};
      11. static intptr_t externals[] = {
      12.     (intptr_t)&hello_cb,
      13.     0,
      14. };
      15. 
    
      16. static void test1() { OH_LOG_INFO(LOG_APP, "test1 called"); }
      17. 
    
      18. struct Test {
      19.     void *ptr1;
      20.     void *ptr2;
      21. };
      22. 
    
      23. static JSVM_Value assertEqual(JSVM_Env env, JSVM_CallbackInfo info) {
      24.     size_t argc = 2;
      25.     JSVM_Value args[2];
      26.     JSVM_CALL(OH_JSVM_GetCbInfo(env, info, &argc, args, NULL, NULL));
      27. 
    
      28.     bool isStrictEquals = false;
      29.     OH_JSVM_StrictEquals(env, args[0], args[1], &isStrictEquals);
      30.     return nullptr;
      31. }
      32. 
    
      33. static JSVM_Value GetPropertyCbInfo(JSVM_Env env, JSVM_Value name, JSVM_Value thisArg, JSVM_Value data) {
      34.     // 该回调是由对象上的获取请求触发的
      35.     char strValue[100];
      36.     size_t size = 0;
      37.     OH_JSVM_GetValueStringUtf8(env, name, strValue, 300, &size);
      38.     JSVM_Value newResult = nullptr;
      39.     char newStr[] = "new return value hahaha from name listening";
      40.     OH_JSVM_CreateStringUtf8(env, newStr, strlen(newStr), &newResult);
      41.     int signBit = 0;
      42.     size_t wordCount = 2;
      43.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      44.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      45.     if (status == JSVM_OK) {
      46.         OH_LOG_INFO(LOG_APP, "GetPropertyCbInfo wordCount is %{public}zu", wordCount);
      47.         auto test = reinterpret_cast<Test *>(wordsOut);
      48.         typedef void (*callTest1)();
      49.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      50.         callTe();
      51.     }
      52.     return nullptr;
      53. }
      54. 
    
      55. static JSVM_Value SetPropertyCbInfo(JSVM_Env env, JSVM_Value name, JSVM_Value property, JSVM_Value thisArg, JSVM_Value data) {
      56.     // 该回调是由对象上的设置请求触发的
      57.     char strValue[100];
      58.     size_t size = 0;
      59.     OH_JSVM_GetValueStringUtf8(env, name, strValue, 300, &size);
      60.     JSVM_Value newResult = nullptr;
      61.     char newStr[] = "new return value hahaha from name listening";
      62.     OH_JSVM_CreateStringUtf8(env, newStr, strlen(newStr), &newResult);
      63.     int signBit = 0;
      64.     size_t wordCount = 2;
      65.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      66.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      67.     if (status == JSVM_OK) {
      68.         OH_LOG_INFO(LOG_APP, "SetPropertyCbInfo wordCount is %{public}zu", wordCount);
      69.         auto test = reinterpret_cast<Test *>(wordsOut);
      70.         typedef void (*callTest1)();
      71.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      72.         callTe();
      73.     }
      74.     return nullptr;
      75. }
      76. 
    
      77. static JSVM_Value DeleterPropertyCbInfo(JSVM_Env env, JSVM_Value name, JSVM_Value thisArg, JSVM_Value data) {
      78.     // 该回调是由对象上的删除请求触发的
      79.     char strValue[100];
      80.     size_t size = 0;
      81.     OH_JSVM_GetValueStringUtf8(env, name, strValue, 300, &size);
      82.     JSVM_Value newResult = nullptr;
      83.     bool returnValue = false;
      84.     OH_JSVM_GetBoolean(env, returnValue, &newResult);
      85.     int signBit = 0;
      86.     size_t wordCount = 2;
      87.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      88.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      89.     if (status == JSVM_OK) {
      90.         OH_LOG_INFO(LOG_APP, "DeleterPropertyCbInfo wordCount is %{public}zu", wordCount);
      91.         auto test = reinterpret_cast<Test *>(wordsOut);
      92.         typedef void (*callTest1)();
      93.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      94.         callTe();
      95.     }
      96.     return nullptr;
      97. }
      98. 
    
      99. static JSVM_Value EnumeratorPropertyCbInfo(JSVM_Env env, JSVM_Value thisArg, JSVM_Value data) {
      100.     // 该回调是由获取对象上的所有属性请求触发的
      101.     JSVM_Value testArray = nullptr;
      102.     OH_JSVM_CreateArrayWithLength(env, 2, &testArray);
      103.     JSVM_Value name1 = nullptr;
      104.     char newStr1[] = "hahaha";
      105.     OH_JSVM_CreateStringUtf8(env, newStr1, strlen(newStr1), &name1);
      106.     JSVM_Value name2 = nullptr;
      107.     char newStr2[] = "heheheh";
      108.     OH_JSVM_CreateStringUtf8(env, newStr2, strlen(newStr2), &name2);
      109. 
    
      110.     OH_JSVM_SetElement(env, testArray, 0, name1);
      111.     OH_JSVM_SetElement(env, testArray, 1, name2);
      112.     int signBit = 0;
      113.     size_t wordCount = 2;
      114.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      115.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      116.     if (status == JSVM_OK) {
      117.         OH_LOG_INFO(LOG_APP, "EnumeratorPropertyCbInfo wordCount is %{public}zu", wordCount);
      118.         auto test = reinterpret_cast<Test *>(wordsOut);
      119.         typedef void (*callTest1)();
      120.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      121.         callTe();
      122.     }
      123.     return nullptr;
      124. }
      125. 
    
      126. static JSVM_Value IndexedPropertyGet(JSVM_Env env, JSVM_Value index, JSVM_Value thisArg, JSVM_Value data) {
      127.     // 该回调是由获取实例对象的索引属性触发的
      128.     uint32_t value = 0;
      129.     OH_JSVM_GetValueUint32(env, index, &value);
      130. 
    
      131.     JSVM_Value newResult = nullptr;
      132.     char newStr[] = "new return value hahaha from index listening";
      133.     OH_JSVM_CreateStringUtf8(env, newStr, strlen(newStr), &newResult);
      134.     int signBit = 0;
      135.     size_t wordCount = 2;
      136.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      137.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      138.     if (status == JSVM_OK) {
      139.         OH_LOG_INFO(LOG_APP, "IndexedPropertyGet wordCount is %{public}zu", wordCount);
      140.         auto test = reinterpret_cast<Test *>(wordsOut);
      141.         typedef void (*callTest1)();
      142.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      143.         callTe();
      144.     }
      145.     return nullptr;
      146. }
      147. 
    
      148. static JSVM_Value IndexedPropertySet(JSVM_Env env, JSVM_Value index, JSVM_Value property, JSVM_Value thisArg, JSVM_Value data) {
      149.     // 该回调是由设置实例对象的索引属性触发的
      150.     uint32_t value = 0;
      151.     OH_JSVM_GetValueUint32(env, index, &value);
      152.     char str[100];
      153.     size_t size = 0;
      154.     OH_JSVM_GetValueStringUtf8(env, property, str, 100, &size);
      155.     JSVM_Value newResult = nullptr;
      156.     char newStr[] = "new return value hahaha from name listening";
      157.     OH_JSVM_CreateStringUtf8(env, newStr, strlen(newStr), &newResult);
      158.     int signBit = 0;
      159.     size_t wordCount = 2;
      160.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      161.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      162.     if (status == JSVM_OK) {
      163.         OH_LOG_INFO(LOG_APP, "IndexedPropertySet wordCount is %{public}zu", wordCount);
      164.         auto test = reinterpret_cast<Test *>(wordsOut);
      165.         typedef void (*callTest1)();
      166.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      167.         callTe();
      168.     }
      169.     return nullptr;
      170. }
      171. 
    
      172. static JSVM_Value IndexedPropertyDeleter(JSVM_Env env, JSVM_Value index, JSVM_Value thisArg, JSVM_Value data) {
      173.     // 该回调是由删除实例对象的索引属性触发的
      174.     uint32_t value = 0;
      175.     OH_JSVM_GetValueUint32(env, index, &value);
      176.     JSVM_Value newResult = nullptr;
      177.     bool returnValue = false;
      178.     OH_JSVM_GetBoolean(env, returnValue, &newResult);
      179.     int signBit = 0;
      180.     size_t wordCount = 2;
      181.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      182.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      183.     if (status == JSVM_OK) {
      184.         OH_LOG_INFO(LOG_APP, "IndexedPropertyDeleter wordCount is %{public}zu", wordCount);
      185.         auto test = reinterpret_cast<Test *>(wordsOut);
      186.         typedef void (*callTest1)();
      187.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      188.         callTe();
      189.     }
      190.     return nullptr;
      191. }
      192. 
    
      193. static JSVM_Value IndexedPropertyEnumerator(JSVM_Env env, JSVM_Value thisArg, JSVM_Value data) {
      194.     // 该回调是由获取对象上的所有索引属性请求触发的
      195.     JSVM_Value testArray = nullptr;
      196.     OH_JSVM_CreateArrayWithLength(env, 2, &testArray);
      197.     JSVM_Value index1 = nullptr;
      198.     OH_JSVM_CreateUint32(env, 1, &index1);
      199.     JSVM_Value index2 = nullptr;
      200.     OH_JSVM_CreateUint32(env, 2, &index2);
      201.     OH_JSVM_SetElement(env, testArray, 0, index1);
      202.     OH_JSVM_SetElement(env, testArray, 1, index2);
      203.     int signBit = 0;
      204.     size_t wordCount = 2;
      205.     uint64_t wordsOut[2] = {0ULL, 0ULL};
      206.     JSVM_Status status = OH_JSVM_GetValueBigintWords(env, data, &signBit, &wordCount, wordsOut);
      207.     if (status == JSVM_OK) {
      208.         OH_LOG_INFO(LOG_APP, "IndexedPropertyDeleter wordCount is %{public}zu", wordCount);
      209.         auto test = reinterpret_cast<Test *>(wordsOut);
      210.         typedef void (*callTest1)();
      211.         callTest1 callTe = reinterpret_cast<callTest1>(test->ptr1);
      212.         callTe();
      213.     }
      214.     return nullptr;
      215. }
      216. 
    
      217. static napi_value TestDefineClassWithProperty(napi_env env1, napi_callback_info info) {
      218.     OH_LOG_ERROR(LOG_APP, "TestDefineClassWithProperty start");
      219.     JSVM_InitOptions init_options;
      220.     memset(&init_options, 0, sizeof(init_options));
      221.     init_options.externalReferences = externals;
      222.     if (aa == 0) {
      223.         OH_JSVM_Init(&init_options);
      224.         aa++;
      225.     }
      226.     JSVM_VM vm;
      227.     JSVM_CreateVMOptions options;
      228.     memset(&options, 0, sizeof(options));
      229.     OH_JSVM_CreateVM(&options, &vm);
      230.     JSVM_VMScope vm_scope;
      231.     OH_JSVM_OpenVMScope(vm, &vm_scope);
      232.     JSVM_Env env;
      233.     JSVM_CallbackStruct param[1];
      234.     param[0].data = nullptr;
      235.     param[0].callback = assertEqual;
      236.     JSVM_PropertyDescriptor descriptor[] = {
      237.         {"assertEqual", NULL, &param[0], NULL, NULL, NULL, JSVM_DEFAULT},
      238.     };
      239.     OH_JSVM_CreateEnv(vm, sizeof(descriptor) / sizeof(descriptor[0]), descriptor, &env);
      240.     JSVM_EnvScope envScope;
      241.     OH_JSVM_OpenEnvScope(env, &envScope);
      242.     JSVM_HandleScope handlescope;
      243.     OH_JSVM_OpenHandleScope(env, &handlescope);
      244. 
    
      245. 
    
      246.     JSVM_CallbackStruct param1;
      247.     param1.callback = [](JSVM_Env env, JSVM_CallbackInfo info) -> JSVM_Value {
      248.         JSVM_Value thisVar = nullptr;
      249.         OH_JSVM_GetCbInfo(env, info, nullptr, nullptr, &thisVar, nullptr);
      250.         return thisVar;
      251.     };
      252.     param1.data = nullptr;
      253. 
    
      254.     JSVM_Value res = nullptr;
      255.     Test *test = new Test();
      256.     test->ptr1 = (void *)test1;
      257.     test->ptr2 = (void *)test1;
      258.     OH_LOG_INFO(LOG_APP, "OH_JSVM_CreateBigintWords 111 word count %{public}d",
      259.                 sizeof(*test) / sizeof(uint64_t));
      260.     JSVM_Status status = OH_JSVM_CreateBigintWords(env, 1, 2, reinterpret_cast<const uint64_t *>(test), &res);
      261. 
    
      262.     // 初始化propertyCfg
      263.     JSVM_PropertyHandlerConfigurationStruct propertyCfg;
      264.     propertyCfg.genericNamedPropertyGetterCallback = GetPropertyCbInfo;
      265.     propertyCfg.genericNamedPropertySetterCallback = SetPropertyCbInfo;
      266.     propertyCfg.genericNamedPropertyDeleterCallback = DeleterPropertyCbInfo;
      267.     propertyCfg.genericNamedPropertyEnumeratorCallback = EnumeratorPropertyCbInfo;
      268.     propertyCfg.genericIndexedPropertyGetterCallback = IndexedPropertyGet;
      269.     propertyCfg.genericIndexedPropertySetterCallback = IndexedPropertySet;
      270.     propertyCfg.genericIndexedPropertyDeleterCallback = IndexedPropertyDeleter;
      271.     propertyCfg.genericIndexedPropertyEnumeratorCallback = IndexedPropertyEnumerator;
      272.     propertyCfg.namedPropertyData = res;
      273.     propertyCfg.indexedPropertyData = res;
      274. 
    
      275.     JSVM_CallbackStruct callbackStruct;
      276.     callbackStruct.callback = [](JSVM_Env env, JSVM_CallbackInfo info) -> JSVM_Value {
      277.         OH_LOG_INFO(LOG_APP, "call as a function called");
      278.         JSVM_Value thisVar = nullptr;
      279.         void *innerData;
      280.         size_t argc = 1;
      281.         JSVM_Value args[1];
      282.         OH_JSVM_GetCbInfo(env, info, &argc, args, &thisVar, &innerData);
      283.         OH_LOG_INFO(LOG_APP, "function call as function result is %{public}s", reinterpret_cast<char *>(innerData));
      284.         uint32_t ret = 0;
      285.         OH_JSVM_GetValueUint32(env, args[0], &ret);
      286.         const char testStr[] = "hello world 111111";
      287.         JSVM_Value setvalueName = nullptr;
      288.         JSVM_CALL(OH_JSVM_CreateStringUtf8(env, testStr, strlen(testStr), &setvalueName));
      289.         return setvalueName;
      290.     };
      291.     char data[100] = "1111 hello world";
      292.     callbackStruct.data = data;
      293.     JSVM_Value testWrapClass = nullptr;
      294. 
    
      295.     // 将属性的访问监听注册在propertyCfg中
      296.     OH_JSVM_DefineClassWithPropertyHandler(env, "TestWrapClass", NAPI_AUTO_LENGTH, &param1, 0, nullptr, &propertyCfg,
      297.                                            &callbackStruct, &testWrapClass);
      298.     JSVM_Value instanceValue = nullptr;
      299.     OH_JSVM_NewInstance(env, testWrapClass, 0, nullptr, &instanceValue);
      300.     const char testStr[] = "hello world";
      301.     JSVM_Value setvalueName = nullptr;
      302.     OH_JSVM_CreateStringUtf8(env, testStr, strlen(testStr), &setvalueName);
      303. 
    
      304.     // 1. 名称属性回调
      305.     // 设置属性
      306.     OH_JSVM_SetNamedProperty(env, instanceValue, "str11", setvalueName);
      307.     OH_JSVM_SetNamedProperty(env, instanceValue, "str123", setvalueName);
      308. 
    
      309.     // 获取属性
      310.     JSVM_Value valueName = nullptr;
      311.     OH_JSVM_GetNamedProperty(env, instanceValue, "str11", &valueName);
      312.     char str[100];
      313.     size_t size = 0;
      314.     OH_JSVM_GetValueStringUtf8(env, valueName, str, 100, &size);
      315. 
    
      316.     // 获取所有属性的名称
      317.     JSVM_Value allPropertyNames = nullptr;
      318.     OH_JSVM_GetAllPropertyNames(env, instanceValue, JSVM_KEY_OWN_ONLY,
      319.                                 static_cast<JSVM_KeyFilter>(JSVM_KEY_ENUMERABLE | JSVM_KEY_SKIP_SYMBOLS),
      320.                                 JSVM_KEY_NUMBERS_TO_STRINGS, &allPropertyNames);
      321.     uint32_t nameSize = 0;
      322.     OH_JSVM_GetArrayLength(env, allPropertyNames, &nameSize);
      323.     JSVM_Value propertyName = nullptr;
      324.     for (uint32_t i = 0; i < nameSize; ++i) {
      325.         OH_JSVM_GetElement(env, allPropertyNames, i, &propertyName);
      326.         char str[100];
      327.         size_t size = 0;
      328.         OH_JSVM_GetValueStringUtf8(env, propertyName, str, 100, &size);
      329.     }
      330. 
    
      331.     // 删除属性
      332.     bool result = false;
      333.     propertyName = nullptr;
      334.     char propertyChar[] = "str11";
      335.     OH_JSVM_CreateStringUtf8(env, propertyChar, strlen(propertyChar), &propertyName);
      336.     OH_JSVM_DeleteProperty(env, instanceValue, propertyName, &result);
      337. 
    
      338.     // 2. 索引属性回调
      339.     // 设置属性
      340.     JSVM_Value jsIndex = nullptr;
      341.     uint32_t index = 0;
      342.     OH_JSVM_CreateUint32(env, index, &jsIndex);
      343.     OH_JSVM_SetProperty(env, instanceValue, jsIndex, setvalueName);
      344.     JSVM_Value jsIndex1 = nullptr;
      345.     index = 1;
      346.     OH_JSVM_CreateUint32(env, index, &jsIndex1);
      347.     OH_JSVM_SetProperty(env, instanceValue, jsIndex1, setvalueName);
      348. 
    
      349.     // 获取属性
      350.     JSVM_Value valueName1 = nullptr;
      351.     OH_JSVM_GetProperty(env, instanceValue, jsIndex, &valueName1);
      352.     char str1[100];
      353.     size_t size1 = 0;
      354.     OH_JSVM_GetValueStringUtf8(env, valueName1, str1, 100, &size1);
      355. 
    
      356.     // 获取所有属性的名称
      357.     JSVM_Value allPropertyNames1 = nullptr;
      358.     OH_JSVM_GetAllPropertyNames(env, instanceValue, JSVM_KEY_OWN_ONLY,
      359.                                 static_cast<JSVM_KeyFilter>(JSVM_KEY_ENUMERABLE | JSVM_KEY_SKIP_SYMBOLS),
      360.                                 JSVM_KEY_NUMBERS_TO_STRINGS, &allPropertyNames1);
      361.     uint32_t nameSize1 = 0;
      362.     OH_JSVM_GetArrayLength(env, allPropertyNames1, &nameSize1);
      363.     JSVM_Value propertyName1 = nullptr;
      364.     for (uint32_t i = 0; i < nameSize1; ++i) {
      365.         OH_JSVM_GetElement(env, allPropertyNames1, i, &propertyName1);
      366.         char str[100];
      367.         size_t size = 0;
      368.         OH_JSVM_GetValueStringUtf8(env, propertyName1, str, 100, &size);
      369.     }
      370. 
    
      371.     // 删除属性
      372.     bool result1 = false;
      373.     OH_JSVM_DeleteProperty(env, instanceValue, jsIndex, &result1);
      374. 
    
      375.     // 3. 作为函数的回调
      376.     JSVM_Value gloablObj = nullptr;
      377.     OH_JSVM_GetGlobal(env, &gloablObj);
      378.     OH_JSVM_SetNamedProperty(env, gloablObj, "myTestInstance", instanceValue);
      379.     OH_LOG_INFO(LOG_APP, "set property on global object");
      380.     std::string innerSourcecodestr = R"(
      381.     {
      382.         let res = myTestInstance(12);
      383.     })";
      384.     JSVM_Value innerSourcecodevalue;
      385.     OH_JSVM_CreateStringUtf8(env, innerSourcecodestr.c_str(), innerSourcecodestr.size(), &innerSourcecodevalue);
      386.     JSVM_Script innerscript;
      387.     OH_JSVM_CompileScript(env, innerSourcecodevalue, nullptr, 0, true, nullptr, &innerscript);
      388.     JSVM_Value innerResult;
      389.     OH_JSVM_RunScript(env, innerscript, &innerResult);
      390. 
    
      391.     OH_JSVM_CloseHandleScope(env, handlescope);
      392.     OH_JSVM_CloseEnvScope(env, envScope);
      393.     OH_JSVM_DestroyEnv(env);
      394.     OH_JSVM_CloseVMScope(vm, vm_scope);
      395.     OH_JSVM_DestroyVM(vm);
      396.     OH_LOG_ERROR(LOG_APP, "TestDefineClassWithProperty pass");
      397.     return nullptr;
      398. }
    
    

场景示例：设置父类并通过DefineClassOptions设置监听拦截属性操作

具体示例参考[使用JSVM-API接口进行class相关开发](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-about-class)。

### 版本管理 __

 **场景介绍**

获取当前版本信息。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_GetVersion | 返回JSVM运行时支持的最高JSVM API版本  
OH_JSVM_GetVMInfo | 返回虚拟机的信息  
  
场景示例：

获取当前版本信息。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_VMInfo result;
      2. OH_JSVM_GetVMInfo(&result);
      3. uint32_t versionId = 0;
      4. OH_JSVM_GetVersion(env, &versionId);
    
    

### 内存管理 __

 **场景介绍**

内存管理。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_AdjustExternalMemory | 将因JavaScript对象而保持活跃的外部分配的内存大小及时通知给底层虚拟机，虚拟机后续触发GC时，就会综合内外内存状态来判断是否进行全局GC。即增大外部内存分配，则会增大触发全局GC的概率；反之减少。  
OH_JSVM_MemoryPressureNotification | 通知虚拟机系统内存压力层级，并有选择地触发垃圾回收。  
OH_JSVM_AllocateArrayBufferBackingStoreData | 申请一块 BackingStore 内存。  
OH_JSVM_FreeArrayBufferBackingStoreData | 释放 BackingStore 内存。  
OH_JSVM_CreateArrayBufferFromBackingStoreData | 基于申请的 BackingStore 内存创建 array buffer。  
  
使用 BackingStore 属于高危操作，使用者需确保内存使用正确。请参考下方正确示例，谨慎操作。

场景示例：

内存管理。

收起

自动换行

深色代码主题

复制
    
          1. // 分别在调用OH_JSVM_AdjustExternalMemory前后来查看底层虚拟机视角下外部分配的内存大小
      2. int64_t result = 0;
      3. OH_JSVM_AdjustExternalMemory(env, 0, &result); // 假设外部分配内存的变化不变
      4. OH_LOG_INFO(LOG_APP, "Before AdjustExternalMemory: %{public}lld\n", result); // 得到调整前的数值
      5. // 调整外部分配的内存大小通知给底层虚拟机（此示例假设内存使用量增加）
      6. int64_t memoryIncrease = 1024 * 1024; // 增加 1 MB
      7. OH_JSVM_AdjustExternalMemory(env, memoryIncrease, &result);
      8. OH_LOG_INFO(LOG_APP, "After AdjustExternalMemory: %{public}lld\n", result); // 得到调整后的数值
    
    

收起

自动换行

深色代码主题

复制
    
          1. // 打开一个Handle scope，在scope范围内申请大量内存来测试函数功能；
      2. // 分别在“完成申请后”、“关闭scope后”和“调用OH_JSVM_MemoryPressureNotification后”三个节点查看内存状态
      3. JSVM_HandleScope tmpscope = nullptr;
      4. OH_JSVM_OpenHandleScope(env, &tmpscope);
      5. for (int i = 0; i < 1000000; ++i) {
      6.     JSVM_Value obj;
      7.     OH_JSVM_CreateObject(env, &obj);
      8. }
      9. JSVM_HeapStatistics mem;
      10. OH_JSVM_GetHeapStatistics(vm, &mem); // 获取虚拟机堆的统计数据
      11. OH_LOG_INFO(LOG_APP, "%{public}zu\n", mem.usedHeapSize); // 申请完成后，内存处于最大状态
      12. OH_JSVM_CloseHandleScope(env, tmpscope); // 关闭Handle scope
      13. 
    
      14. OH_JSVM_GetHeapStatistics(vm, &mem);
      15. OH_LOG_INFO(LOG_APP, "%{public}zu\n", mem.usedHeapSize); // 关闭scope后，GC并没有立即回收
      16. 
    
      17. // 通知虚拟机系统内存压力层级，并有选择地触发垃圾回收
      18. OH_JSVM_MemoryPressureNotification(env, JSVM_MEMORY_PRESSURE_LEVEL_CRITICAL); // 假设内存压力处于临界状态
      19. 
    
      20. OH_JSVM_GetHeapStatistics(vm, &mem);
      21. OH_LOG_INFO(LOG_APP, "%{public}zu\n", mem.usedHeapSize); // 触发垃圾回收后
    
    

BackingStore 正确使用示例

收起

自动换行

深色代码主题

复制
    
          1. void *backingStore;
      2. JSVM_Value arrayBuffer;
      3. 
    
      4. // 申请一块大小为 100 字节的 BackingStore 内存
      5. OH_JSVM_AllocateArrayBufferBackingStoreData(100, JSVM_ZERO_INITIALIZED, &backingStore);
      6. 
    
      7. // 在之前申请的 BackingStore 上创建一个 ArrayBuffer，位置为距离 BackingStore 起始地址加 30 字节处，大小为 20 字节
      8. OH_JSVM_CreateArrayBufferFromBackingStoreData(env, backingStore, 100, 30, 20, &arrayBuffer);
      9. 
    
      10. // 在 JS 中使用创建的 ArrayBuffer
      11. JSVM_Value js_global;
      12. JSVM_Value name;
      13. OH_JSVM_GetGlobal(env, &js_global);
      14. OH_JSVM_CreateStringUtf8(env, "buffer", JSVM_AUTO_LENGTH, &name);
      15. OH_JSVM_SetProperty(env, js_global, name, arrayBuffer);
      16. 
    
      17. JSVM_Script script;
      18. JSVM_Value scriptString;
      19. JSVM_Value result;
      20. const char *src = R"JS(
      21. function writeBuffer(data) {
      22.   let view = new Uint8Array(data);
      23.   // Write some values to the ArrayBuffer
      24.   for (let i = 0; i < view.length; i++) {
      25.     view[i] = i % 256;
      26.   }
      27. }
      28. writeBuffer(buffer)
      29. )JS";
      30. OH_JSVM_CreateStringUtf8(env, src, JSVM_AUTO_LENGTH, &scriptString);
      31. OH_JSVM_CompileScriptWithOptions(env, scriptString, 0, nullptr, &script);
      32. OH_JSVM_RunScript(env, script, &result);
      33. 
    
      34. // 检查 ArrayBuffer 的内容
      35. uint8_t *array = static_cast<uint8_t*>(backingStore);
      36. for (auto i = 0; i < 100; ++i) {
      37.   if (array[i] != i % 256) {
      38.     return false;
      39.   }
      40. }
      41. 
    
      42. // 释放 array buffer. 注意对于这种方式创建的 ArrayBuffer, 在释放对应的 BackingStore 之前,
      43. // 务必使用 OH_JSVM_DetachArraybuffer 将所有使用当前的 BackingStore 创建的 ArrayBuffer 释放
      44. // 否则可能产生不可预测的内存问题，请谨慎使用
      45. OH_JSVM_DetachArraybuffer(env, arrayBuffer);
      46. 
    
      47. // 释放申请的 backing store 内存
      48. OH_JSVM_FreeArrayBufferBackingStoreData(backingStore);
    
    

### Promise操作 __

 **场景介绍**

Promise相关操作。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CreatePromise | 创建一个延迟对象和一个JavaScript promise  
OH_JSVM_ResolveDeferred | 通过与之关联的延迟对象来解析JavaScript promise  
OH_JSVM_RejectDeferred | 通过与之关联的延迟对象来拒绝JavaScript Promise  
OH_JSVM_IsPromise | 查询Promise是否为原生Promise对象  
  
场景示例：

Promise相关操作。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_Deferred deferred;
      2. JSVM_Value promise;
      3. OH_JSVM_CreatePromise(env, &deferred, &promise);
      4. 
    
      5. // 模拟异步操作
      6. int result = 42;
      7. bool success = true;
      8. if (success)
      9. {
      10.     // 解析Promise，并传递结果
      11.     JSVM_Value value;
      12.     OH_JSVM_CreateInt32(env, result, &value);
      13.     OH_JSVM_ResolveDeferred(env, deferred, value);
      14. } else {
      15.     // 拒绝Promise，并传递错误信息
      16.     JSVM_Value code = nullptr;
      17.     JSVM_Value message = nullptr;
      18.     OH_JSVM_CreateStringUtf8(env, "600", JSVM_AUTO_LENGTH, &code);
      19.     OH_JSVM_CreateStringUtf8(env, "Async operation failed", JSVM_AUTO_LENGTH, &message);
      20.     JSVM_Value error = nullptr;
      21.     OH_JSVM_CreateError(env, code, message, &error);
      22.     OH_JSVM_RejectDeferred(env, deferred, error);
      23. }
    
    

### JSON操作 __

 **场景介绍**

JSON操作。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_JsonParse | 解析JSON字符串，并返回成功解析的值  
OH_JSVM_JsonStringify | 将对象字符串化，并返回成功转换后的字符串  
  
场景示例：

解析JSON操作。

收起

自动换行

深色代码主题

复制
    
          1. std::string sourcecodestr = "{\"name\": \"John\", \"age\": 30, \"city\": \"New York\"}" ;
      2. JSVM_Value jsonString;
      3. OH_JSVM_CreateStringUtf8(env, sourcecodestr.c_str(), sourcecodestr.size(), &jsonString);
      4. JSVM_Value result;
      5. OH_JSVM_JsonParse(env, jsonString, &result);
    
    

### 创建和使用虚拟机的启动快照 __

 **场景介绍**

创建和使用虚拟机的启动快照。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_CreateSnapshot | 用于创建虚拟机的启动快照  
OH_JSVM_CreateEnvFromSnapshot | 基于启动快照创建jsvm环境  
  
场景示例：

[创建和使用虚拟机的启动快照。](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-create-snapshot)

### 检查传入的值是否可调用 __

 **场景介绍**

检查传入的值是否可调用。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_IsCallable | 检查传入的值是否可调用  
  
场景示例：

检查传入的值是否可调用。

收起

自动换行

深色代码主题

复制
    
          1. static JSVM_Value NapiIsCallable(JSVM_Env env, JSVM_CallbackInfo info) {
      2.     JSVM_Value value, rst;
      3.     size_t argc = 1;
      4.     bool isCallable = false;
      5.     JSVM_CALL(OH_JSVM_GetCbInfo(env, info, &argc, &value, NULL, NULL));
      6.     JSVM_CALL(OH_JSVM_IsCallable(env, value, &isCallable));
      7.     OH_JSVM_GetBoolean(env, isCallable, &rst);
      8.     return rst;
      9. }
      10. 
    
      11. static napi_value MyJSVMDemo([[maybe_unused]] napi_env _env, [[maybe_unused]] napi_callback_info _info) {
      12.     std::thread t([]() {
      13.         // create vm, and open vm scope
      14.         JSVM_VM vm;
      15.         JSVM_CreateVMOptions options;
      16.         memset(&options, 0, sizeof(options));
      17.         OH_JSVM_CreateVM(&options, &vm);
      18.         JSVM_VMScope vmScope;
      19.         OH_JSVM_OpenVMScope(vm, &vmScope);
      20.         JSVM_CallbackStruct param[] = {
      21.             {.data = nullptr, .callback = NapiIsCallable},
      22.         };
      23.         JSVM_PropertyDescriptor descriptor[] = {
      24.             {"napiIsCallable", NULL, &param[0], NULL, NULL, NULL, JSVM_DEFAULT},
      25.         };
      26.         // create env, register native method, and open env scope
      27.         JSVM_Env env;
      28.         OH_JSVM_CreateEnv(vm, sizeof(descriptor) / sizeof(descriptor[0]), descriptor, &env);
      29.         JSVM_EnvScope envScope;
      30.         OH_JSVM_OpenEnvScope(env, &envScope);
      31.         // open handle scope
      32.         JSVM_HandleScope handleScope;
      33.         OH_JSVM_OpenHandleScope(env, &handleScope);
      34.         std::string sourceCodeStr = R"JS(
      35.         function addNumbers(num1, num2)
      36.         {
      37.             var rst= num1 + num2;
      38.             return rst;
      39.         }
      40.         let rst = napiIsCallable(addNumbers);
      41.         )JS";
      42.         // compile js script
      43.         JSVM_Value sourceCodeValue;
      44.         OH_JSVM_CreateStringUtf8(env, sourceCodeStr.c_str(), sourceCodeStr.size(), &sourceCodeValue);
      45.         JSVM_Script script;
      46.         OH_JSVM_CompileScript(env, sourceCodeValue, nullptr, 0, true, nullptr, &script);
      47.         JSVM_Value result;
      48.         // run js script
      49.         OH_JSVM_RunScript(env, script, &result);
      50.         JSVM_ValueType type;
      51.         OH_JSVM_Typeof(env, result, &type);
      52.         OH_LOG_INFO(LOG_APP, "JSVM API TEST type: %{public}d", type);
      53.         // exit vm and clean memory
      54.         OH_JSVM_CloseHandleScope(env, handleScope);
      55.         OH_JSVM_CloseEnvScope(env, envScope);
      56.         OH_JSVM_DestroyEnv(env);
      57.         OH_JSVM_CloseVMScope(vm, vmScope);
      58.         OH_JSVM_DestroyVM(vm);
      59.     });
      60.     t.detach();
      61.     return nullptr;
      62. }
    
    

### Lock操作 __

 **场景介绍**

Lock操作。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_IsLocked | 判断当前线程是否持有指定环境的锁  
OH_JSVM_AcquireLock | 获取指定环境的锁  
OH_JSVM_ReleaseLock | 释放指定环境的锁  
  
场景示例：

加锁解锁操作。

收起

自动换行

深色代码主题

复制
    
          1. class LockWrapper {
      2.  public:
      3.   LockWrapper(JSVM_Env env) : env(env) {
      4.     OH_JSVM_IsLocked(env, &isLocked);
      5.     if (!isLocked) {
      6.       OH_JSVM_AcquireLock(env);
      7.       OH_JSVM_GetVM(env, &vm);
      8.       OH_JSVM_OpenVMScope(vm, &vmScope);
      9.       OH_JSVM_OpenEnvScope(env, &envScope);
      10.     }
      11.   }
      12. 
    
      13.   ~LockWrapper() {
      14.     if (!isLocked) {
      15.       OH_JSVM_CloseEnvScope(env, envScope);
      16.       OH_JSVM_CloseVMScope(vm, vmScope);
      17.       OH_JSVM_ReleaseLock(env);
      18.     }
      19.   }
      20. 
    
      21.   LockWrapper(const LockWrapper&) = delete;
      22.   LockWrapper& operator=(const LockWrapper&) = delete;
      23.   LockWrapper(LockWrapper&&) = delete;
      24.   void* operator new(size_t) = delete;
      25.   void* operator new[](size_t) = delete;
      26. 
    
      27.  private:
      28.   JSVM_Env env;
      29.   JSVM_EnvScope envScope;
      30.   JSVM_VMScope vmScope;
      31.   JSVM_VM vm;
      32.   bool isLocked;
      33. };
      34. 
    
      35. static int aa = 0;
      36. 
    
      37. static napi_value Add([[maybe_unused]] napi_env _env, [[maybe_unused]] napi_callback_info _info) {
      38.     static JSVM_VM vm;
      39.     static JSVM_Env env;
      40.     if (aa == 0) {
      41.         OH_JSVM_Init(nullptr);
      42.         aa++;
      43.         // create vm
      44.         JSVM_CreateVMOptions options;
      45.         memset(&options, 0, sizeof(options));
      46.         OH_JSVM_CreateVM(&options, &vm);
      47.         // create env
      48.         OH_JSVM_CreateEnv(vm, 0, nullptr, &env);
      49.     }
      50. 
    
      51.     std::thread t1([]() {
      52.         LockWrapper lock(env);
      53.         JSVM_HandleScope handleScope;
      54.         OH_JSVM_OpenHandleScope(env, &handleScope);
      55.         JSVM_Value value;
      56.         JSVM_Status rst = OH_JSVM_CreateInt32(env, 32, &value); // 32: numerical value
      57.         if (rst == JSVM_OK) {
      58.             OH_LOG_INFO(LOG_APP, "JSVM:t1 OH_JSVM_CreateInt32 suc");
      59.         } else {
      60.             OH_LOG_ERROR(LOG_APP, "JSVM:t1 OH_JSVM_CreateInt32 fail");
      61.         }
      62.         int32_t num1 = 0;
      63.         OH_JSVM_GetValueInt32(env, value, &num1);
      64.         OH_LOG_INFO(LOG_APP, "JSVM:t1 num1 = %{public}d", num1);
      65.         OH_JSVM_CloseHandleScope(env, handleScope);
      66.     });
      67.     std::thread t2([]() {
      68.         LockWrapper lock(env);
      69.         JSVM_HandleScope handleScope;
      70.         OH_JSVM_OpenHandleScope(env, &handleScope);
      71.         JSVM_Value value;
      72.         JSVM_Status rst = OH_JSVM_CreateInt32(env, 32, &value); // 32: numerical value
      73.         if (rst == JSVM_OK) {
      74.             OH_LOG_INFO(LOG_APP, "JSVM:t2 OH_JSVM_CreateInt32 suc");
      75.         } else {
      76.             OH_LOG_ERROR(LOG_APP, "JSVM:t2 OH_JSVM_CreateInt32 fail");
      77.         }
      78.         int32_t num1 = 0;
      79.         OH_JSVM_GetValueInt32(env, value, &num1);
      80.         OH_LOG_INFO(LOG_APP, "JSVM:t2 num1 = %{public}d", num1);
      81.         OH_JSVM_CloseHandleScope(env, handleScope);
      82.     });
      83.     t1.detach();
      84.     t2.detach();
      85.     return nullptr;
      86. }
    
    

### 设置与获取和当前运行的JSVM环境相关联的数据 __

 **场景介绍**

调用OH_JSVM_SetInstanceData接口，设置与当前运行的JSVM环境相关联的数据。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_SetInstanceData | 设置与当前运行的JSVM环境相关联的数据  
OH_JSVM_GetInstanceData | 获取与当前运行的JSVM环境相关联的数据  
  
场景示例：

设置并获取与当前运行的JSVM环境相关联的数据。

收起

自动换行

深色代码主题

复制
    
          1. JSVM_VM vm;
      2. JSVM_CreateVMOptions options;
      3. JSVM_VMScope vm_scope;
      4. JSVM_Env env;
      5. JSVM_EnvScope envScope;
      6. JSVM_HandleScope handlescope;
      7. 
    
      8. static int aa = 0;
      9. struct InstanceData {
      10.     int32_t value;
      11. };
      12. 
    
      13. // 初始化虚拟机，创建JSVM运行环境
      14. void init_JSVM_environment(){
      15.     JSVM_InitOptions init_options;
      16.     memset(&init_options, 0, sizeof(init_options));
      17.     if (aa == 0) {
      18.         OH_JSVM_Init(&init_options);
      19.         aa++;
      20.     }
      21.     memset(&options, 0, sizeof(options));
      22.     OH_JSVM_CreateVM(&options, &vm);
      23.     OH_JSVM_OpenVMScope(vm, &vm_scope);
      24.     OH_JSVM_CreateEnv(vm, 0, nullptr, &env);
      25.     OH_JSVM_OpenEnvScope(env, &envScope);
      26.     OH_JSVM_OpenHandleScope(env, &handlescope);
      27. }
      28. 
    
      29. // 退出虚拟机，释放对应的环境
      30. napi_value close_JSVM_environment(napi_env env1, napi_callback_info info)
      31. {
      32.     OH_JSVM_CloseHandleScope(env, handlescope);
      33.     OH_JSVM_CloseEnvScope(env, envScope);
      34.     OH_JSVM_DestroyEnv(env);
      35.     OH_JSVM_CloseVMScope(vm, vm_scope);
      36.     OH_JSVM_DestroyVM(vm);
      37.     napi_value result;
      38.     char* s = "ok";
      39.     napi_create_string_latin1(env1, s, strlen(s), &result);
      40.     return result;
      41. }
      42. 
    
      43. //清除和释放与实例相关联的内存资源
      44. void InstanceFinalizeCallback(JSVM_Env env, void *finalizeData, void *finalizeHint)
      45. {
      46.     if (finalizeData) {
      47.         InstanceData *data = reinterpret_cast<InstanceData *>(finalizeData);
      48.         free(data);
      49.         *(InstanceData **)finalizeData = nullptr;
      50.     }
      51. }
      52. 
    
      53. static napi_value GetInstanceData(napi_env env1, napi_callback_info info)
      54. {
      55.     InstanceData *instanceData = reinterpret_cast<InstanceData *>(malloc(sizeof(InstanceData)));
      56.     if (instanceData == nullptr) {
      57.         printf("Memory allocation failed!\n");
      58.         return nullptr;
      59.     }
      60.     size_t argc = 1;
      61.     napi_value args[1] = {nullptr};
      62.     //用于获取回调函数参数
      63.     napi_get_cb_info(env1, info, &argc, args , nullptr, nullptr);
      64.     napi_valuetype valuetype0;
      65.     napi_typeof(env1, args[0], &valuetype0);
      66.     int32_t tmp = 0;
      67.     napi_get_value_int32(env1, args[0], &tmp);
      68.     instanceData->value = tmp;
      69.     //将获得的参数与当前运行的JSVM环境关联起来
      70.     OH_JSVM_SetInstanceData(env, instanceData, InstanceFinalizeCallback, nullptr);
      71.     InstanceData *resData = nullptr;
      72.     //获取与当前运行的JSVM环境相关联的数据
      73.     OH_JSVM_GetInstanceData(env, (void **)&resData);
      74.     napi_value result;
      75.     napi_create_uint32(env1, resData->value, &result);
      76.     return result;
      77. }
    
    

### 任务队列 __

 **场景介绍**

在虚拟机内部启动任务队列的运行，检查队列中是否有待处理的微任务。任务队列可由外部事件循环执行。

**接口说明**

接口 | 功能说明  
---|---  
OH_JSVM_PumpMessageLoop | 启动任务队列的运行  
OH_JSVM_PerformMicrotaskCheckpoint | 执行任务队列里的微任务  
  
场景示例：

[使用JSVM-API接口进行任务队列相关开发](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/use-jsvm-execute_tasks)
