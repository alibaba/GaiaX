/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef GXANALYZEANDROID_GXVALUE_H
#define GXANALYZEANDROID_GXVALUE_H

#include <stdio.h>
#include <stdint.h>
#include <string>

enum {
    GX_TAG_FLOAT = 0,
    GX_TAG_BOOL = 1,
    GX_TAG_NULL = 2,
    GX_TAG_VALUE = 3,
    GX_TAG_STRING = 4,
    GX_TAG_OBJECT = 5,
    GX_TAG_ARRAY = 6,
    GX_TAG_MAP = 7,
    GX_TAG_LONG = 8,
};

class GXValue {
public:
    int64_t tag;
    int32_t int32;  //Bool 1，0
    float float64;  //Float
    int64_t intNum;    //long
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

    GXValue(int tag, int64_t i) {
        this->tag = tag;
        this->intNum = i;
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

static long GX_VALUE_GET_LONG(GXValue v) {
    return v.intNum;
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
static GXValue *GX_NewNull(int val) {
    GXValue *v = new GXValue(GX_TAG_NULL, val);
    return v;
}

/**
 * 通过该方法NewInt对象
 * @param val long值
 */
static GXValue *GX_NewLong(int64_t val) {
    GXValue *v = new GXValue(GX_TAG_LONG, val);
    return v;
}

/**
 * 通过该方法NewArray对象
 * @param val long值
 */
static GXValue *GX_NewArray(void *val) {
    GXValue *v = new GXValue(GX_TAG_ARRAY, val);
    return v;
}

/**
 * 通过该方法NewMap对象
 * @param val long值
 */
static GXValue *GX_NewMap(void *val) {
    GXValue *v = new GXValue(GX_TAG_MAP, val);
    return v;
}

/**
 * 通过该方法NewBool对象
 * @param val bool对应的int值
 */
static GXValue *GX_NewBool(int val) {
    GXValue *v = new GXValue(GX_TAG_BOOL, val);
    return v;
}

/**
 * 通过该方法newFloat值
 * @param d Value值
 */
static GXValue *GX_NewFloat64(float d) {
    GXValue *v = new GXValue(GX_TAG_FLOAT, d);
    return v;
}

/**
 * 通过该方法NewString对象
 * @param str 字符串的值
 */
static GXValue *GX_NewGXString(const char *str) {
    GXValue *v = new GXValue(GX_TAG_STRING, str);
    return v;
}

#endif //GXANALYZEANDROID_GXVALUE_H
