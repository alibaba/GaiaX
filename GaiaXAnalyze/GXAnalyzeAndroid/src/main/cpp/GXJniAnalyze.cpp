#include <jni.h>
#include <stdio.h>
#include <string>
#include "GXAnalyze.h"
#include <android/log.h>

static JavaVM *javaVM = nullptr;

static jlong getSourceValueFromJava(string valuePath, jobject source, jobject object_in,jobject jfileObject,jclass jAnalyzeJni);

static jlong getFunctionValueFromJava(string valuePath, jlongArray dataPointer, jobject object_in,jobject jfileObject,jclass jAnalyzeJni);

static jlongArray getLongArray(long *paramPointers, int paramsSize);

/**
 * 获取env
 * @return
 */
static JNIEnv *getJNIEnv() {
    JNIEnv *env;
    if (javaVM == nullptr || javaVM->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return nullptr;
    }
    return env;
}


/**
 * GXAnalyze Jni子类，用于获取Java/Kotlin具体实现实例类
 */
class GXJniAnalyze : public GXAnalyze {

public:
    jobject globalSelf = nullptr;    //thiz
    jfieldID jfieldId = nullptr;    //判断context是否为空
    jclass jAnalyzeJni = nullptr;
    jobject jObjectFieldId = nullptr;
    bool isInitJni = false;

    long getSourceValue(string valuePath, void *source) override {
        jobject dataSource = static_cast<jobject>(source);
        if(!isInitJni){
            initJniClass(globalSelf);
            isInitJni = true;
        }
        return getSourceValueFromJava(valuePath, dataSource, globalSelf,jObjectFieldId,jAnalyzeJni);
    }

    long
    getFunctionValue(string funName, long *paramPointers, int paramsSize, string source) override {
        if(!isInitJni){
            initJniClass(globalSelf);
            isInitJni = true;
        }
        return getFunctionValueFromJava(funName, getLongArray(paramPointers, paramsSize),
                                        globalSelf,jObjectFieldId,jAnalyzeJni);
    }

    void throwError(string message) override {
        __android_log_print(ANDROID_LOG_ERROR, "[GaiaX]",
                            "%s", message.c_str());
    }
    void initJniClass(jobject object_in);
};

void GXJniAnalyze::initJniClass(jobject object_in) {
    JNIEnv *env = getJNIEnv();
    if (env != nullptr) {
        jclass clazz;
        jfieldID analyze_fieldID;
        clazz = env->GetObjectClass(object_in);
        analyze_fieldID = env->GetFieldID(clazz, "computeExtend",
                                          "Lcom/alibaba/gaiax/analyze/GXAnalyze$IComputeExtend;");
        jobject jobject1 = env->GetObjectField(object_in, analyze_fieldID);
        jclass analyzeJni = env->GetObjectClass(jobject1);
        jAnalyzeJni = static_cast<jclass>(env->NewWeakGlobalRef(analyzeJni));
        jObjectFieldId = env->NewWeakGlobalRef(jobject1);
        env->DeleteLocalRef(clazz);
    }
}


static jlongArray getLongArray(long *paramPointers, int size) {
    JNIEnv *env = getJNIEnv();
    jlong *params = new jlong[size];
    for (int i = 0; i < size; i++) {
        params[i] = paramPointers[i];
    }
    jlongArray res = env->NewLongArray(size);
    env->SetLongArrayRegion(res, 0, size, params);
    delete[]params;
    return res;
}

/**
 * 调用具体实现方法：取值方法
 * @param valuePath $data.title 里的 data.title
 * @param dataPointer 数据源
 * @param object_in 具体类实例，即GXJniAnalyze的this
 * @return
 */
static jlong getSourceValueFromJava(string valuePath, jobject source, jobject object_in,jobject jfileObject,jclass jAnalyzeJni) {
    JNIEnv *env = getJNIEnv();
    if (env != nullptr) {
        jmethodID jmethodId = env->GetMethodID(jAnalyzeJni, "computeValueExpression",
                                               "(Ljava/lang/String;Ljava/lang/Object;)J");
        jlong res = env->CallLongMethod(jfileObject, jmethodId,
                                        env->NewStringUTF(valuePath.c_str()),
                                        source);
        return res;
    }
    return 0L;
}

/**
 * 调用具体实现方法：获取函数结果
 * @param valuePath $data.title 里的 data.title
 * @param dataPointer 参数列表
 * @param object_in 具体类实例，即GXJniAnalyze的this
 * @return
 */
static jlong getFunctionValueFromJava(string valuePath, jlongArray dataPointer, jobject object_in,jobject jfileObject,jclass jAnalyzeJni) {
    JNIEnv *env = getJNIEnv();
    if (env != nullptr) {
        jmethodID jmethodId = env->GetMethodID(jAnalyzeJni, "computeFunctionExpression",
                                               "(Ljava/lang/String;[J)J");
        jlong res = env->CallLongMethod(jfileObject, jmethodId, env->NewStringUTF(valuePath.c_str()),
                                        dataPointer);
        return res;
    }
    return 0L;
}

/**
 * 获取jfileId
 */
static jfieldID getAnalyzeFieldId(JNIEnv *env, jobject self) {
    if (env != nullptr && self != nullptr) {
        jclass clazz = env->GetObjectClass(self);
        if (clazz != nullptr) {
            jfieldID context = env->GetFieldID(clazz, "pointer", "J");
            env->DeleteLocalRef(clazz);
            return context;
        }
    }
    return nullptr;
}

/**
 * 获取当前存储的JniAnalyze指针
 */
static GXJniAnalyze *getJniAnalyze(JNIEnv *env, jobject self) {
    if (env != nullptr && self != nullptr) {
        jfieldID context = getAnalyzeFieldId(env, self);
        if (context != nullptr) {
            jlong value = env->GetLongField(self, context);
            if (value != 0L) {
                GXJniAnalyze *analyze = (GXJniAnalyze *) value;
                return analyze;
            } else {
                return nullptr;
            }
        }
    }
    return nullptr;
}

/**
 * 设置并存储JniAnalyze
 */
static GXJniAnalyze *setJniAnalyze(JNIEnv *env, jobject self, long analyze) {
    GXJniAnalyze *old = getJniAnalyze(env, self);
    if (env != nullptr && self != nullptr) {
        env->SetLongField(self, getAnalyzeFieldId(env, self), analyze);
    }
    return old;
}
/**
 * 初始化并存储JVM
 */
extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    return JNI_VERSION_1_4;
}
/**
 * Java/Kotlin 调用该方法获取表达式结果
 */
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_getResultNative(JNIEnv *env, jobject thiz, jobject self,
                                                         jstring expression, jobject data) {
    GXJniAnalyze *jAnalyze = getJniAnalyze(env, self);
    long res = jAnalyze->getValue(env->GetStringUTFChars(expression,JNI_FALSE), data);
    return (jlong) (res);
}
/**
 * 初始化方法
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_initNative(JNIEnv *env, jobject thiz, jobject self) {
    //类
    GXJniAnalyze *jAnalyze = getJniAnalyze(env, self);
    //如果目前没有实例，则new一个实例
    if (jAnalyze == nullptr) {
        jAnalyze = new GXJniAnalyze();
        //初始化

        jAnalyze->globalSelf = env->NewWeakGlobalRef(self);
        jAnalyze->jfieldId = getAnalyzeFieldId(env, self);
        if (jAnalyze->jfieldId == nullptr) {
            //throw error
        }
        setJniAnalyze(env, self, (long) jAnalyze);
    } else {
//        jniThrowException()
        //throw error
    }
}
/**
 * 获取Value的Tag，即类型
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueTag(JNIEnv *env, jobject thiz,
                                                                    jlong value) {
    GXValue *val = (GXValue *) value;
    return GX_VALUE_GET_TAG(*val);
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueFloat(JNIEnv *env, jobject thiz,
                                                                      jlong value) {
    GXValue *val = (GXValue *) value;
    return (jfloat) GX_VALUE_GET_FLOAT64(*val);

}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueFloat64(JNIEnv *env,
                                                                           jobject thiz,
                                                                           jfloat value) {
    GXValue *result = new GXValue(GX_TAG_FLOAT, value);
    return (jlong) result;
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueBoolean(JNIEnv *env, jobject thiz,
                                                                        jlong value) {
    GXValue *val = (GXValue *) value;
    int boolVal = GX_VALUE_GET_BOOL(*val);
    if (boolVal) {
        return true;
    } else {
        return false;
    }
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueString(JNIEnv *env, jobject thiz,
                                                                       jlong value) {
    GXValue *val = (GXValue *) value;
    const char *str = (*val).str;
    jstring j_str = env->NewStringUTF(str);
    if (val->tag == GX_TAG_STRING && val->str != NULL) {
        delete[]val->str;
        val->str = NULL;
    }
    return j_str;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueArray(JNIEnv *env, jobject thiz,
                                                                      jlong value) {
    GXValue *val = (GXValue *) value;
    return static_cast<jobject>(GX_VALUE_GET_OBJECT(*val));
}
extern "C"
JNIEXPORT jobject JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueMap(JNIEnv *env, jobject thiz,
                                                                    jlong value) {
    GXValue *val = (GXValue *) value;
    return static_cast<jobject>(GX_VALUE_GET_OBJECT(*val));
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueString(JNIEnv *env, jobject thiz,
                                                                          jstring value) {

    jsize size = env->GetStringUTFLength(value);
    GXValue *result;
    if (size <= 0) {
        result = new GXValue(GX_TAG_STRING, "");
    } else {
        result = new GXValue(GX_TAG_STRING, env->GetStringUTFChars(value,JNI_FALSE));
    }
    return (jlong) result;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueBool(JNIEnv *env, jobject thiz,
                                                                        jboolean value) {
    GXValue *result = new GXValue(GX_TAG_BOOL, value);
    return (jlong) result;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueArray(JNIEnv *env, jobject thiz,
                                                                         jobject value) {
    //必须持有引用，否则会释放掉指针
    jobject ref = env->NewWeakGlobalRef(value);
    GXValue *result = new GXValue(GX_TAG_ARRAY, ref);
    return (jlong) result;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueMap(JNIEnv *env, jobject thiz,
                                                                       jobject value) {
    //必须持有引用，否则会释放掉指针
    jobject ref = env->NewWeakGlobalRef(value);
    GXValue *result = new GXValue(GX_TAG_MAP, ref);
    return (jlong) result;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueNull(JNIEnv *env, jobject thiz) {
    GXValue *result = new GXValue(GX_TAG_NULL, 1);
    return (jlong) result;
}
extern "C"
JNIEXPORT void JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_releaseGXValue(JNIEnv *env, jobject thiz,
                                                                       jlong value) {
    GXValue *val = (GXValue *) value;
    if (val->tag == GX_TAG_ARRAY || val->tag == GX_TAG_MAP) {
        if (val->ptr != NULL) {
            env->DeleteWeakGlobalRef((jweak) val->ptr);
        }
    }
    releaseGXValue(value);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_getValueLong(JNIEnv *env, jobject thiz,
                                                                     jlong value) {
    GXValue *val = (GXValue *) value;
    return (jlong) GX_VALUE_GET_LONG(*val);
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_alibaba_gaiax_analyze_GXAnalyze_00024Companion_createValueLong(JNIEnv *env, jobject thiz,
                                                                        jlong value) {
    GXValue *result = new GXValue(GX_TAG_LONG, (int64_t) value);
    return (jlong) result;
}
