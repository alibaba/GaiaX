#include "napi/native_api.h"
// NAPI开发文档
// Node-API：实现ArkTS与C/C++交互的逻辑。
// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/use-napi-process-0000001774120794-V5
// https://developer.huawei.com/consumer/cn/doc/harmonyos-references/napi-0000001813416940
// 使用HiLog打印日志:
// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/hilog-guidelines-ndk-0000001861966313


#include "hilog/log.h"
#include <string>

#undef LOG_DOMAIN

#undef LOG_TAG

#define LOG_DOMAIN 0x3200 // 全局domain宏，标识业务领域

// GXStretch => GXStretch
#define LOG_TAG "GXStretch" // 全局tag宏，标识模块日志tag

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


// https://crates.io/crates/libc
// https://doc.rust-lang.org/nomicon/ffi.html
// #[no_mangle]
// pub unsafe extern "C" fn stretch_init() -> *mut c_void {
//     let stretch = stretch::node::Stretch::new();
//     Box::into_raw(Box::new(stretch)) as *mut c_void
// }
extern "C" void *stretch_init(void);

extern "C" void stretch_free(void *stretch);

extern "C" void stretch_node_add_child(void *stretch, void *node, void *child);

extern "C" void *stretch_node_compute_layout(void *stretch, void *node, float width, float height, void *(*create_layout)(const float *, int32_t));

extern "C" void *stretch_node_create(void *stretch, void *style);

extern "C" bool stretch_node_dirty(void *stretch, void *node);

extern "C" void stretch_node_free(void *stretch, void *node);

extern "C" void stretch_node_mark_dirty(void *stretch, void *node);

extern "C" void stretch_node_remove_child(void *stretch, void *node, void *child);

extern "C" void stretch_node_remove_child_at_index(void *stretch, void *node, uintptr_t index);

extern "C" void stretch_node_replace_child_at_index(void *stretch, void *node, uintptr_t index, void *child);

extern "C" void stretch_node_set_measure(void *stretch, void *node, void *swift_ptr, StretchSize (*measure)(const void *, float, float));

extern "C" void stretch_node_set_style(void *stretch, void *node, void *style);

extern "C" void *stretch_style_create(int32_t display, int32_t position_type, int32_t direction, int32_t flex_direction, int32_t flex_wrap, int32_t overflow,
                                      int32_t align_items, int32_t align_self, int32_t align_content, int32_t justify_content, StretchStyleRect position,
                                      StretchStyleRect margin, StretchStyleRect padding, StretchStyleRect border, float flex_grow, float flex_shrink,
                                      StretchStyleDimension flex_basis, StretchStyleSize size, StretchStyleSize min_size, StretchStyleSize max_size, float aspect_ratio);

extern "C" void stretch_style_free(void *style);

// class LayoutRet {
//
// public:
//     const float *floats;
//     int32_t len;
// };

struct LayoutRet {
    float *floats;
    int len;
};

static void *create_layout(const float *floats, int32_t len) {

    OH_LOG_DEBUG(LOG_APP, "napi_stretch create_layout len=%{public}d", len);

    for (int i = 0; i < len; i++) {
        float value = floats[i];
        OH_LOG_DEBUG(LOG_APP, "napi_stretch create_layout index=%{public}d ptr=%{public}p value=%{public}f", i, &floats[i], value);
    }

    LayoutRet *ptr = new LayoutRet();
    ptr->len = len;
    // 序列化 floats 为 std::string
    ptr->floats = new float[len * sizeof(float)];
    memcpy(ptr->floats, floats, len * sizeof(float));

    return (void *)ptr;
}

static napi_value convert_to_js_numbers(napi_env env, void *ret) {
    LayoutRet *ptr = (LayoutRet *)ret;

    // 反序列化
    float *floats = ptr->floats;
    int32_t len = ptr->len;

    OH_LOG_DEBUG(LOG_APP, "napi_stretch convert_to_js_numbers floats=%{public}p len=%{public}d", floats, len);

    size_t arrSize = len;
    napi_value jsArr = nullptr;
    napi_create_array(env, &jsArr);
    for (int i = 0; i < arrSize; i++) {
        float value = floats[i];
        napi_value arrValue = nullptr;
        napi_create_double(env, value, &arrValue);
        napi_set_element(env, jsArr, i, arrValue);
        OH_LOG_DEBUG(LOG_APP, "napi_stretch convert_to_js_numbers index=%{public}d ptr=%{public}p value=%{public}f", i, &floats[i], value);
    }

    delete[] ptr->floats;
    ptr->floats = nullptr;

    delete ptr;

    OH_LOG_DEBUG(LOG_APP, "napi_stretch convert_to_js_numbers end");

    return jsArr;
}

static napi_value napi_stretch_node_compute_layout(napi_env env, napi_callback_info info) {

    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 4) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_compute_layout argc != 4 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);

    double width;
    napi_get_value_double(env, argv[2], &width);

    double height;
    napi_get_value_double(env, argv[3], &height);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_compute_layout width=%{public}f height=%{public}f", width, height);

    void *layoutRet = stretch_node_compute_layout((void *)stretch_ptr, (void *)node_ptr, (float)width, (float)height, create_layout);

    return convert_to_js_numbers(env, layoutRet);
}

static napi_value napi_stretch_node_remove_child(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 3) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_remove_child argc != 3 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);

    long child_node_ptr;
    napi_get_value_int64(env, argv[2], &child_node_ptr);

    stretch_node_remove_child((void *)stretch_ptr, (void *)node_ptr, (void *)child_node_ptr);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_remove_child");

    return nullptr;
}

static napi_value napi_stretch_node_add_child(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 3) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_add_child argc != 3 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);

    long child_node_ptr;
    napi_get_value_int64(env, argv[2], &child_node_ptr);

    stretch_node_add_child((void *)stretch_ptr, (void *)node_ptr, (void *)child_node_ptr);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_add_child ");

    return nullptr;
}

static napi_value napi_stretch_node_set_style(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 3) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_set_style argc != 3 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);


    long style_ptr;
    napi_get_value_int64(env, argv[2], &style_ptr);

    stretch_node_set_style((void *)stretch_ptr, (void *)node_ptr, (void *)style_ptr);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_set_style");

    return nullptr;
}

static napi_value napi_stretch_node_dirty(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 2) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_dirty argc != 2 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);

    bool is_dirty = stretch_node_dirty((void *)stretch_ptr, (void *)node_ptr);

    napi_value ret;
    napi_create_int32(env, is_dirty ? 1 : 0, &ret);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_dirty is_dirty=%{public}d", is_dirty);

    return ret;
}


static napi_value napi_stretch_node_mark_dirty(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 2) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_mark_dirty argc != 2 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);

    stretch_node_mark_dirty((void *)stretch_ptr, (void *)node_ptr);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_mark_dirty");

    return nullptr;
}

static napi_value napi_stretch_node_create(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 2) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_create argc != 2 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long style_ptr;
    napi_get_value_int64(env, argv[1], &style_ptr);

    void *node_ptr = stretch_node_create((void *)stretch_ptr, (void *)style_ptr);

    int64_t value = (int64_t)node_ptr;
    napi_value ret;
    napi_create_int64(env, value, &ret);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_create ptr=%{public}p value=%{public}ld", node_ptr, value);

    return ret;
}

static napi_value napi_stretch_node_free(napi_env env, napi_callback_info info) {
    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 2) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_node_free argc != 2 argc=%{public}zu", argc);
        return nullptr;
    }

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long stretch_ptr;
    napi_get_value_int64(env, argv[0], &stretch_ptr);

    long node_ptr;
    napi_get_value_int64(env, argv[1], &node_ptr);

    stretch_node_free((void *)stretch_ptr, (void *)node_ptr);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_node_free");

    return nullptr;
}

static napi_value napi_stretch_style_create(napi_env env, napi_callback_info info) {


    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    if (argc != 59) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_style_create argc != 59 argc=%{public}zu", argc);
        return nullptr;
    }

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_style_create argc=%{public}zu", argc);

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    int32_t display;
    napi_get_value_int32(env, argv[0], &display);

    int32_t position_type;
    napi_get_value_int32(env, argv[1], &position_type);

    int32_t direction;
    napi_get_value_int32(env, argv[2], &direction);

    int32_t flex_direction;
    napi_get_value_int32(env, argv[3], &flex_direction);

    int32_t flex_wrap;
    napi_get_value_int32(env, argv[4], &flex_wrap);

    int32_t overflow;
    napi_get_value_int32(env, argv[5], &overflow);

    int32_t align_items;
    napi_get_value_int32(env, argv[6], &align_items);

    int32_t align_self;
    napi_get_value_int32(env, argv[7], &align_self);

    int32_t align_content;
    napi_get_value_int32(env, argv[8], &align_content);

    int32_t justify_content;
    napi_get_value_int32(env, argv[9], &justify_content);

    // position

    int32_t position_start_type;
    napi_get_value_int32(env, argv[10], &position_start_type);

    double position_start_value;
    napi_get_value_double(env, argv[11], &position_start_value);

    int32_t position_end_type;
    napi_get_value_int32(env, argv[12], &position_end_type);

    double position_end_value;
    napi_get_value_double(env, argv[13], &position_end_value);

    int32_t position_top_type;
    napi_get_value_int32(env, argv[14], &position_top_type);

    double position_top_value;
    napi_get_value_double(env, argv[15], &position_top_value);

    int32_t position_bottom_type;
    napi_get_value_int32(env, argv[16], &position_bottom_type);

    double position_bottom_value;
    napi_get_value_double(env, argv[17], &position_bottom_value);

    StretchStyleRect position = {
        {position_start_type, (float)position_start_value},  // start
        {position_end_type, (float)position_end_value},      // end
        {position_top_type, (float)position_top_value},      // top
        {position_bottom_type, (float)position_bottom_value} // bottom
    };


    // margin

    int32_t margin_start_type;
    napi_get_value_int32(env, argv[18], &margin_start_type);

    double margin_start_value;
    napi_get_value_double(env, argv[19], &margin_start_value);

    int32_t margin_end_type;
    napi_get_value_int32(env, argv[20], &margin_end_type);

    double margin_end_value;
    napi_get_value_double(env, argv[21], &margin_end_value);

    int32_t margin_top_type;
    napi_get_value_int32(env, argv[22], &margin_top_type);

    double margin_top_value;
    napi_get_value_double(env, argv[23], &margin_top_value);

    int32_t margin_bottom_type;
    napi_get_value_int32(env, argv[24], &margin_bottom_type);

    double margin_bottom_value;
    napi_get_value_double(env, argv[25], &margin_bottom_value);

    StretchStyleRect margin = {
        {margin_start_type, (float)margin_start_value},  // start
        {margin_end_type, (float)margin_end_value},      // end
        {margin_top_type, (float)margin_top_value},      // top
        {margin_bottom_type, (float)margin_bottom_value} // bottom
    };


    // padding

    int32_t padding_start_type;
    napi_get_value_int32(env, argv[26], &padding_start_type);

    double padding_start_value;
    napi_get_value_double(env, argv[27], &padding_start_value);

    int32_t padding_end_type;
    napi_get_value_int32(env, argv[28], &padding_end_type);

    double padding_end_value;
    napi_get_value_double(env, argv[29], &padding_end_value);

    int32_t padding_top_type;
    napi_get_value_int32(env, argv[30], &padding_top_type);

    double padding_top_value;
    napi_get_value_double(env, argv[31], &padding_top_value);

    int32_t padding_bottom_type;
    napi_get_value_int32(env, argv[32], &padding_bottom_type);

    double padding_bottom_value;
    napi_get_value_double(env, argv[33], &padding_bottom_value);

    StretchStyleRect padding = {
        {padding_start_type, (float)padding_start_value},  // start
        {padding_end_type, (float)padding_end_value},      // end
        {padding_top_type, (float)padding_top_value},      // top
        {padding_bottom_type, (float)padding_bottom_value} // bottom
    };

    // border


    int32_t border_start_type;
    napi_get_value_int32(env, argv[34], &border_start_type);

    double border_start_value;
    napi_get_value_double(env, argv[35], &border_start_value);

    int32_t border_end_type;
    napi_get_value_int32(env, argv[36], &border_end_type);

    double border_end_value;
    napi_get_value_double(env, argv[37], &border_end_value);

    int32_t border_top_type;
    napi_get_value_int32(env, argv[38], &border_top_type);

    double border_top_value;
    napi_get_value_double(env, argv[39], &border_top_value);

    int32_t border_bottom_type;
    napi_get_value_int32(env, argv[40], &border_bottom_type);

    double border_bottom_value;
    napi_get_value_double(env, argv[41], &border_bottom_value);

    StretchStyleRect border = {
        {border_start_type, (float)border_start_value},  // start
        {border_end_type, (float)border_end_value},      // end
        {border_top_type, (float)border_top_value},      // top
        {border_bottom_type, (float)border_bottom_value} // bottom
    };

    //
    double flex_grow;
    napi_get_value_double(env, argv[42], &flex_grow);

    double flex_shrink;
    napi_get_value_double(env, argv[43], &flex_shrink);

    // flex_basis

    int32_t flex_basis_type;
    napi_get_value_int32(env, argv[44], &flex_basis_type);

    double flex_basis_value;
    napi_get_value_double(env, argv[45], &flex_basis_value);

    StretchStyleDimension flex_basis = {flex_basis_type, (float)flex_basis_value};

    // size
    int32_t width_type;
    napi_get_value_int32(env, argv[46], &width_type);

    double width_value;
    napi_get_value_double(env, argv[47], &width_value);

    int32_t height_type;
    napi_get_value_int32(env, argv[48], &height_type);

    double height_value;
    napi_get_value_double(env, argv[49], &height_value);

    StretchStyleSize size = {
        {width_type, (float)width_value},
        {height_type, (float)height_value},
    };

    // min-size
    int32_t min_width_type;
    napi_get_value_int32(env, argv[50], &min_width_type);

    double min_width_value;
    napi_get_value_double(env, argv[51], &min_width_value);

    int32_t min_height_type;
    napi_get_value_int32(env, argv[52], &min_height_type);

    double min_height_value;
    napi_get_value_double(env, argv[53], &min_height_value);

    StretchStyleSize min_size = {
        {min_width_type, (float)min_width_value},
        {min_height_type, (float)min_height_value},
    };

    // max-size
    int32_t max_width_type;
    napi_get_value_int32(env, argv[54], &max_width_type);

    double max_width_value;
    napi_get_value_double(env, argv[55], &max_width_value);

    int32_t max_height_type;
    napi_get_value_int32(env, argv[56], &max_height_type);

    double max_height_value;
    napi_get_value_double(env, argv[57], &max_height_value);

    StretchStyleSize max_size = {
        {max_width_type, (float)max_width_value},
        {max_height_type, (float)max_height_value},
    };

    //
    double aspect_ratio;
    napi_get_value_double(env, argv[58], &aspect_ratio);

    void *ptr = stretch_style_create(display, position_type, direction, flex_direction, flex_wrap, overflow, align_items, align_self, align_content, justify_content,
                                     position, margin, padding, border, (float)flex_grow, (float)flex_shrink, flex_basis, size, min_size, max_size, (float)aspect_ratio);

    int64_t value = (int64_t)ptr;

    napi_value ret;
    napi_create_int64(env, value, &ret);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_style_create ptr=%{public}p value=%{public}ld", ptr, value);
    return ret;
}

static napi_value napi_stretch_style_free(napi_env env, napi_callback_info info) {

    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    // JS 传入参数为0，不执行后续逻辑
    if (argc == 0) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_style_free argc = 0");
        return nullptr;
    }

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_style_free argc=%{public}zu", argc);

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long value0;
    napi_get_value_int64(env, argv[0], &value0);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_style_free value=%{public}ld", value0);

    stretch_style_free((void *)value0);

    delete argv;

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_style_free complete");

    return nullptr;
}

static napi_value napi_stretch_init(napi_env env, napi_callback_info info) {
    void *ptr = stretch_init();
    int64_t value = (int64_t)ptr;
    napi_value ret;
    napi_create_int64(env, value, &ret);
    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_init ptr=%{public}p value=%{public}ld", ptr, value);
    return ret;
}

static napi_value napi_stretch_free(napi_env env, napi_callback_info info) {

    size_t argc = 0;
    // argv 传入 nullptr 来获取传入参数真实数量
    napi_get_cb_info(env, info, &argc, nullptr, nullptr, nullptr);
    // JS 传入参数为0，不执行后续逻辑
    if (argc == 0) {
        OH_LOG_ERROR(LOG_APP, "napi_stretch napi_stretch_free argc = 0");
        return nullptr;
    }

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_free argc=%{public}zu", argc);

    // 创建数组以获取JS传入的参数
    napi_value *argv = new napi_value[argc];
    napi_get_cb_info(env, info, &argc, argv, nullptr, nullptr);

    long value0;
    napi_get_value_int64(env, argv[0], &value0);

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_free value=%{public}ld", value0);

    stretch_free((void *)value0);

    delete argv;

    OH_LOG_DEBUG(LOG_APP, "napi_stretch napi_stretch_free complete");

    return nullptr;
}


EXTERN_C_START
static napi_value Init(napi_env env, napi_value exports) {
    napi_property_descriptor desc[] = {
        {"napi_stretch_init", nullptr, napi_stretch_init, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_free", nullptr, napi_stretch_free, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_style_create", nullptr, napi_stretch_style_create, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_style_free", nullptr, napi_stretch_style_free, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_create", nullptr, napi_stretch_node_create, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_free", nullptr, napi_stretch_node_free, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_add_child", nullptr, napi_stretch_node_add_child, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_remove_child", nullptr, napi_stretch_node_remove_child, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_mark_dirty", nullptr, napi_stretch_node_mark_dirty, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_dirty", nullptr, napi_stretch_node_dirty, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_set_style", nullptr, napi_stretch_node_set_style, nullptr, nullptr, nullptr, napi_default, nullptr},
        {"napi_stretch_node_compute_layout", nullptr, napi_stretch_node_compute_layout, nullptr, nullptr, nullptr, napi_default, nullptr},
    };
    napi_define_properties(env, exports, sizeof(desc) / sizeof(desc[0]), desc);
    return exports;
}
EXTERN_C_END

static napi_module demoModule = {
    .nm_version = 1,
    .nm_flags = 0,
    .nm_filename = nullptr,
    .nm_register_func = Init,
    .nm_modname = "gxstretch",
    .nm_priv = ((void *)0),
    .reserved = {0},
};

extern "C" __attribute__((constructor)) void RegisterGXStretchModule(void) { napi_module_register(&demoModule); }
