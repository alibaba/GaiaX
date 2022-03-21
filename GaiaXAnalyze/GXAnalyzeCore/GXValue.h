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
#define GX_MKVAL(tag, val) (GXValue){ (GXValueUnion){ .int32 = val }, tag }
#define GX_MKOBJECT(tag, val) (GXValue){ (GXValueUnion){ .ptr = val }, tag }

#define GX_VALUE_GET_TAG(v) ((int32_t)(v).tag)
#define GX_VALUE_GET_BOOL(v) ((v).u.int32)
#define GX_VALUE_GET_FLOAT64(v) ((v).u.float64)
#define GX_VALUE_GET_OBJECT(v) ((v).u.ptr)


typedef struct GXString GXString;

//Value值
typedef union GXValueUnion {
    int32_t int32;  //Bool 1，0
    float float64;  //Float
    void *ptr;      //Array,Map
    char *str;      //String
} GXValueUnion;

//gx变量
typedef struct GXValue {
    GXValueUnion u;
    int64_t tag;
} GXValue;

static inline const char *GX_ToCString(GXValue val) {
    return (const char *) val.u.str;
}

static inline GXValue __GX_NewFloat64(float d) {
    GXValue v;
    v.tag = GX_TAG_FLOAT;
    v.u.float64 = d;
    return v;
}
/**
 * 通过该方法NewNull对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewNull(int val) {
    return GX_MKVAL(GX_TAG_NULL, (val != 0));
}
/**
 * 通过该方法NewArray对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewArray(void* val) {
    return GX_MKOBJECT(GX_TAG_ARRAY, val);
}
/**
 * 通过该方法NewMap对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewMap(void* val) {
    return GX_MKOBJECT(GX_TAG_MAP, val);
}
/**
 * 通过该方法NewBool对象
 * @param val bool对应的int值
 */
static gx_force_inline GXValue GX_NewBool(int val) {
    return GX_MKVAL(GX_TAG_BOOL, (val != 0));
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
    int len = strlen(str);
    gxValue.u.str = new char[len + 1];
    for (int i = 0; i < len; i++) {
        gxValue.u.str[i] = str[i];
    }
    gxValue.u.str[len] = '\0';
    return gxValue;
}

#endif //GXANALYZEANDROID_GXVALUE_H
