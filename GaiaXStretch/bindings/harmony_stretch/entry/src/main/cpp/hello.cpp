#include "napi/native_api.h"

extern "C" void hello_from_rust();
extern "C" int hello2_from_rust(int a, int b);

static napi_value Add(napi_env env, napi_callback_info info) {

    int ret = hello2_from_rust(10, 10);

    napi_value ret2;
    napi_create_int32(env, ret, &ret2);

    return ret2;
}

EXTERN_C_START
static napi_value Init(napi_env env, napi_value exports) {
    napi_property_descriptor desc[] = {{"add", nullptr, Add, nullptr, nullptr, nullptr, napi_default, nullptr}};
    napi_define_properties(env, exports, sizeof(desc) / sizeof(desc[0]), desc);
    return exports;
}
EXTERN_C_END

static napi_module demoModule = {
    .nm_version = 1,
    .nm_flags = 0,
    .nm_filename = nullptr,
    .nm_register_func = Init,
    .nm_modname = "entry",
    .nm_priv = ((void *)0),
    .reserved = {0},
};

extern "C" __attribute__((constructor)) void RegisterEntryModule(void) { napi_module_register(&demoModule); }
