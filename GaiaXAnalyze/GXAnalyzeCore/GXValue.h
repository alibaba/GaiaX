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

class GXValue {
public:
    int64_t tag;
    int32_t int32;  //Bool 1，0
    float float64;  //Float
    void *ptr;      //Array,Map
    char *str;      //String
    GXValue() {}

    GXValue(int tag, float f) {
        this->tag = tag;
        this->float64 = f;
    }

    GXValue(int tag, int32_t i) {
        this->tag = tag;
        this->int32 = i;
    }

    GXValue(int tag, void *p) {
        this->tag = tag;
        this->ptr = p;
    }

    GXValue(int tag, std::string s) {
        this->tag = tag;
        int len = strlen(s.c_str());
        this->str = new char[len + 1];
        for (int i = 0; i < len; i++) {
            this->str[i] = s[i];
        }
        this->str[len] = '\0';
    }

    ~GXValue() {

    }

private:

};

static void releaseGXValue(long v) {
    GXValue *val = (GXValue *) v;
    delete val;
}

static int GX_VALUE_GET_TAG(GXValue v) {
    return v.tag;
}

static int32_t GX_VALUE_GET_BOOL(GXValue v) {
    return v.int32;
}

static float GX_VALUE_GET_FLOAT64(GXValue v) {
    return v.float64;
}

static void *GX_VALUE_GET_OBJECT(GXValue v) {
    return v.ptr;
}


/**
 * 通过该方法NewNull对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewNull(int val) {
    GXValue v = GXValue(GX_TAG_NULL, val);
    return v;
}

/**
 * 通过该方法NewArray对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewArray(void *val) {
    GXValue v = GXValue(GX_TAG_ARRAY, val);
    return v;
}

/**
 * 通过该方法NewMap对象
 * @param val long值
 */
static gx_force_inline GXValue GX_NewMap(void *val) {
    GXValue v = GXValue(GX_TAG_MAP, val);
    return v;
}

/**
 * 通过该方法NewBool对象
 * @param val bool对应的int值
 */
static gx_force_inline GXValue GX_NewBool(int val) {
    GXValue v = GXValue(GX_TAG_BOOL, val);
    return v;
}

/**
 * 通过该方法newFloat值
 * @param d Value值
 */
static gx_force_inline GXValue GX_NewFloat64(float d) {
    GXValue v = GXValue(GX_TAG_FLOAT, d);
    return v;
}

/**
 * 通过该方法NewString对象
 * @param str 字符串的值
 */
static gx_force_inline GXValue GX_NewGXString(const char *str) {
    GXValue v = GXValue(GX_TAG_STRING, str);
    return v;
}

#endif //GXANALYZEANDROID_GXVALUE_H
