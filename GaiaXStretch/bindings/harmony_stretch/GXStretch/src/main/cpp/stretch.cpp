#include "napi/native_api.h"

// NAPI开发文档
// Node-API：实现ArkTS与C/C++交互的逻辑。
// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/use-napi-process-0000001774120794-V5

extern "C" void println_hello_from_rusts();
extern "C" int add_two_int_from_rust(int a, int b);

typedef struct {
    float width;
    float height;
} StretchSize;

typedef struct {
    int32_t dimen_type;
    float dimen_value;
} StretchStyleDimension;

typedef struct {
    StretchStyleDimension start;
    StretchStyleDimension end;
    StretchStyleDimension top;
    StretchStyleDimension bottom;
} StretchStyleRect;

typedef struct {
    StretchStyleDimension width;
    StretchStyleDimension height;
} StretchStyleSize;

extern "C" void stretch_free(void *stretch);

extern "C" void *stretch_init(void);

extern "C" void stretch_node_add_child(void *stretch, void *node, void *child);

extern "C" void *stretch_node_compute_layout(void *stretch, void *node, float width, float height,
                                             void *(*create_layout)(const float *));

extern "C" void *stretch_node_create(void *stretch, void *style);

extern "C" bool stretch_node_dirty(void *stretch, void *node);

extern "C" void stretch_node_free(void *stretch, void *node);

extern "C" void stretch_node_mark_dirty(void *stretch, void *node);

extern "C" void stretch_node_remove_child(void *stretch, void *node, void *child);

extern "C" void stretch_node_remove_child_at_index(void *stretch, void *node, uintptr_t index);

extern "C" void stretch_node_replace_child_at_index(void *stretch, void *node, uintptr_t index, void *child);

extern "C" void stretch_node_set_measure(void *stretch, void *node, void *swift_ptr,
                                         StretchSize (*measure)(const void *, float, float));

extern "C" void stretch_node_set_style(void *stretch, void *node, void *style);

extern "C" void *stretch_style_create(int32_t display, int32_t position_type, int32_t direction, int32_t flex_direction,
                                      int32_t flex_wrap, int32_t overflow, int32_t align_items, int32_t align_self,
                                      int32_t align_content, int32_t justify_content, StretchStyleRect position,
                                      StretchStyleRect margin, StretchStyleRect padding, StretchStyleRect border,
                                      float flex_grow, float flex_shrink, StretchStyleDimension flex_basis,
                                      StretchStyleSize size, StretchStyleSize min_size, StretchStyleSize max_size,
                                      float aspect_ratio);

extern "C" void stretch_style_free(void *style);

static napi_value Add(napi_env env, napi_callback_info info) {

    int ret = add_two_int_from_rust(10, 15);

    napi_value ret2;
    napi_create_int32(env, ret, &ret2);

    println_hello_from_rusts();

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
