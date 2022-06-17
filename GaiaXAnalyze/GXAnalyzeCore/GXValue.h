#ifndef GXANALYZEANDROID_GXVALUE_H
#define GXANALYZEANDROID_GXVALUE_H

#include <stdio.h>
#include <stdint.h>
#include <string>

#define gx_force_inline       inline __attribute__((always_inline))

enum {
    GX_TAG_FLOAT = 0,
    GX_TAG_BOOL = 1,
    GX_TAG_NULL = 2,
    GX_TAG_VALUE = 3,
    GX_TAG_STRING = 4,
    GX_TAG_OBJECT = 5,
    GX_TAG_ARRAY = 6,
    GX_TAG_MAP = 7,
    GX_TAG_INT = 8,
};

#define GX_VALUE_GET_TAG(v) ((int32_t)(v).tag)
#define GX_VALUE_GET_BOOL(v) ((v).u.int32)
#define GX_VALUE_GET_FLOAT64(v) ((v).u.float64)
#define GX_VALUE_GET_OBJECT(v) ((v).u.ptr)

//Value值
typedef struct GXValueUnion {
    int32_t int32 = 0;  //Bool 1，0
    float float64 = 0;  //Float
    void *ptr = nullptr;      //Array,Map
    const char *str;      //String
} GXValueUnion;

//gx变量
typedef struct GXValue {
    GXValueUnion u;
    int64_t tag;
    int count;
    bool hasChanged;
} GXValue;

static inline const char *GX_ToCString(GXValue val) {
    return val.u.str;
}

static inline GXValue __GX_NewFloat64(float d) {
    GXValue v;
    v.tag = GX_TAG_FLOAT;
    v.u.float64 = d;
    v.hasChanged = false;
    v.count = -2;
    v.u.ptr = nullptr;
    return v;
}

/**
 * 通过该方法NewNull对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewNull(int val) {
    GXValue v;
    v.tag = GX_TAG_NULL;
    v.u.ptr = nullptr;
    v.u.int32 = val;
    v.count = -2;
    v.hasChanged = false;
    return v;
}

/**
 * 通过该方法NewArray对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewArray(void *val) {
    GXValue v;
    v.tag = GX_TAG_ARRAY;
    v.u.ptr = val;
    v.count = -2;
    v.hasChanged = false;
    return v;
}

/**
 * 通过该方法NewMap对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewMap(void *val) {
    GXValue v;
    v.tag = GX_TAG_MAP;
    v.u.ptr = val;
    v.count = -2;
    v.hasChanged = false;
    return v;
}


/**
 * 通过该方法NewBool对象
 * @param val bool对应的int值
 */
static gx_force_inline GXValue GX_NewBool(int val) {
    GXValue v;
    v.tag = GX_TAG_BOOL;
    v.u.ptr = nullptr;
    v.u.int32 = val;
    v.count = -2;
    v.hasChanged = false;
    return v;
}

/**
 * 通过该方法newFloat值
 * @param d Value值
 */
static gx_force_inline GXValue GX_NewFloat64(float d) {
    GXValue v;
    v = __GX_NewFloat64(d);
    return v;
}

/**
 * 通过该方法NewString对象
 * @param str 字符串的值
 */
static gx_force_inline GXValue GX_NewGXString(const char *str) {
    GXValue gxValue;
    gxValue.tag = GX_TAG_STRING;
    gxValue.u.ptr = nullptr;
    gxValue.u.str = str;
    gxValue.count = -2;
    gxValue.hasChanged = false;
    return gxValue;
}

#endif //GXANALYZEANDROID_GXVALUE_H