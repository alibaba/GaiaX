#include "JniHelp.h"

/**
 * Equivalent to ScopedLocalRef, but for C_JNIEnv instead. (And slightly more powerful.)
 */
template<typename T>
class scoped_local_ref {
public:
    explicit scoped_local_ref(C_JNIEnv *env, T localRef = nullptr)
            : mEnv(env), mLocalRef(localRef) {
    }

    ~scoped_local_ref() {
        reset();
    }

    void reset(T localRef = nullptr) {
        if (mLocalRef != nullptr) {
            (*mEnv)->DeleteLocalRef(reinterpret_cast<JNIEnv *>(mEnv), mLocalRef);
            mLocalRef = localRef;
        }
    }

    T get() const {
        return mLocalRef;
    }

private:
    C_JNIEnv *const mEnv;
    T mLocalRef;

    DISALLOW_COPY_AND_ASSIGN(scoped_local_ref);
};

static jclass findClass(C_JNIEnv *env, const char *className) {
    auto *e = reinterpret_cast<JNIEnv *>(env);
    return (*env)->FindClass(e, className);
}

extern "C" int jniRegisterNativeMethods(C_JNIEnv *env, const char *className,
                                        const JNINativeMethod *gMethods, int numMethods) {
    auto *e = reinterpret_cast<JNIEnv *>(env);

    scoped_local_ref<jclass> c(env, findClass(env, className));
    if (c.get() == nullptr) {
        char *tmp;
        const char *msg;
        if (asprintf(&tmp,
                     "Native registration unable to find class '%s'; aborting...",
                     className) == -1) {
            // Allocation failed, print default warning.
            msg = "Native registration unable to find class; aborting...";
        } else {
            msg = tmp;
        }
        e->FatalError(msg);
    }

    if ((*env)->RegisterNatives(e, c.get(), gMethods, numMethods) < 0) {
        char *tmp;
        const char *msg;
        if (asprintf(&tmp, "RegisterNatives failed for '%s'; aborting...", className) == -1) {
            // Allocation failed, print default warning.
            msg = "RegisterNatives failed; aborting...";
        } else {
            msg = tmp;
        }
        e->FatalError(msg);
    }

    return 0;
}

/*
 * Returns a human-readable summary of an exception object.  The buffer will
 * be populated with the "binary" class name and, if present, the
 * exception message.
 */
static bool getExceptionSummary(C_JNIEnv *env, jthrowable exception, std::string &result) {
    auto *e = reinterpret_cast<JNIEnv *>(env);

    /* get the name of the exception's class */
    scoped_local_ref<jclass> exceptionClass(env,
                                            (*env)->GetObjectClass(e, exception)); // can't fail
    scoped_local_ref<jclass> classClass(env,
                                        (*env)->GetObjectClass(e,
                                                               exceptionClass.get())); // java.lang.Class, can't fail
    jmethodID classGetNameMethod =
            (*env)->GetMethodID(e, classClass.get(), "getName", "()Ljava/lang/String;");
    scoped_local_ref<jstring> classNameStr(env,
                                           (jstring) (*env)->CallObjectMethod(e,
                                                                              exceptionClass.get(),
                                                                              classGetNameMethod));
    if (classNameStr.get() == nullptr) {
        (*env)->ExceptionClear(e);
        result = "<error getting class name>";
        return false;
    }
    const char *classNameChars = (*env)->GetStringUTFChars(e, classNameStr.get(), nullptr);
    if (classNameChars == nullptr) {
        (*env)->ExceptionClear(e);
        result = "<error getting class name UTF-8>";
        return false;
    }
    result += classNameChars;
    (*env)->ReleaseStringUTFChars(e, classNameStr.get(), classNameChars);

    /* if the exception has a detail message, get that */
    jmethodID getMessage =
            (*env)->GetMethodID(e, exceptionClass.get(), "getMessage", "()Ljava/lang/String;");
    scoped_local_ref<jstring> messageStr(env,
                                         (jstring) (*env)->CallObjectMethod(e, exception,
                                                                            getMessage));
    if (messageStr.get() == nullptr) {
        return true;
    }

    result += ": ";

    const char *messageChars = (*env)->GetStringUTFChars(e, messageStr.get(), nullptr);
    if (messageChars != nullptr) {
        result += messageChars;
        (*env)->ReleaseStringUTFChars(e, messageStr.get(), messageChars);
    } else {
        result += "<error getting message>";
        (*env)->ExceptionClear(e); // clear OOM
    }

    return true;
}

/*
 * Returns an exception (with stack trace) as a string.
 */
static bool getStackTrace(C_JNIEnv *env, jthrowable exception, std::string &result) {
    auto *e = reinterpret_cast<JNIEnv *>(env);

    scoped_local_ref<jclass> stringWriterClass(env, findClass(env, "java/io/StringWriter"));
    if (stringWriterClass.get() == nullptr) {
        return false;
    }

    jmethodID stringWriterCtor = (*env)->GetMethodID(e, stringWriterClass.get(), "<init>", "()V");
    jmethodID stringWriterToStringMethod =
            (*env)->GetMethodID(e, stringWriterClass.get(), "toString", "()Ljava/lang/String;");

    scoped_local_ref<jclass> printWriterClass(env, findClass(env, "java/io/PrintWriter"));
    if (printWriterClass.get() == nullptr) {
        return false;
    }

    jmethodID printWriterCtor =
            (*env)->GetMethodID(e, printWriterClass.get(), "<init>", "(Ljava/io/Writer;)V");

    scoped_local_ref<jobject> stringWriter(env,
                                           (*env)->NewObject(e, stringWriterClass.get(),
                                                             stringWriterCtor));
    if (stringWriter.get() == nullptr) {
        return false;
    }

    scoped_local_ref<jobject> printWriter(env,
                                          (*env)->NewObject(e, printWriterClass.get(),
                                                            printWriterCtor, stringWriter.get()));
    if (printWriter.get() == nullptr) {
        return false;
    }

    scoped_local_ref<jclass> exceptionClass(env,
                                            (*env)->GetObjectClass(e, exception)); // can't fail
    jmethodID printStackTraceMethod =
            (*env)->GetMethodID(e, exceptionClass.get(), "printStackTrace",
                                "(Ljava/io/PrintWriter;)V");
    (*env)->CallVoidMethod(e, exception, printStackTraceMethod, printWriter.get());

    if ((*env)->ExceptionCheck(e)) {
        return false;
    }

    scoped_local_ref<jstring> messageStr(env,
                                         (jstring) (*env)->CallObjectMethod(e, stringWriter.get(),
                                                                            stringWriterToStringMethod));
    if (messageStr.get() == nullptr) {
        return false;
    }

    const char *utfChars = (*env)->GetStringUTFChars(e, messageStr.get(), nullptr);
    if (utfChars == nullptr) {
        return false;
    }

    result = utfChars;

    (*env)->ReleaseStringUTFChars(e, messageStr.get(), utfChars);
    return true;
}

extern "C" int jniThrowException(C_JNIEnv *env, const char *className, const char *msg) {
    auto *e = reinterpret_cast<JNIEnv *>(env);

    if ((*env)->ExceptionCheck(e)) {
        /* TODO: consider creating the new exception with this as "cause" */
        scoped_local_ref<jthrowable> exception(env, (*env)->ExceptionOccurred(e));
        (*env)->ExceptionClear(e);

        if (exception.get() != nullptr) {
            std::string text;
            getExceptionSummary(env, exception.get(), text);
            __android_log_print(ANDROID_LOG_ERROR, "[GaiaX]",
                                "Discarding pending exception (%s) to throw %s", text.c_str(),
                                className);
        }
    }

    scoped_local_ref<jclass> exceptionClass(env, findClass(env, className));
    if (exceptionClass.get() == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, "[GaiaX]", "Unable to find exception class %s",
                            className);
        /* ClassNotFoundException now pending */
        return -1;
    }

    if ((*env)->ThrowNew(e, exceptionClass.get(), msg) != JNI_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "[GaiaX]", "Failed throwing '%s' '%s'",
                            className, msg);
        /* an exception, most likely OOM, will now be pending */
        return -1;
    }

    return 0;
}

int jniThrowExceptionFmt(C_JNIEnv *env, const char *className, const char *fmt, va_list args) {
    char msgBuf[512];
    vsnprintf(msgBuf, sizeof(msgBuf), fmt, args);
    return jniThrowException(env, className, msgBuf);
}

int jniThrowNullPointerException(C_JNIEnv *env, const char *msg) {
    return jniThrowException(env, "java/lang/NullPointerException", msg);
}

int jniThrowRuntimeException(C_JNIEnv *env, const char *msg) {
    return jniThrowException(env, "java/lang/RuntimeException", msg);
}

int jniThrowIOException(C_JNIEnv *env, int errnum) {
    char buffer[80];
    const char *message = jniStrError(errnum, buffer, sizeof(buffer));
    return jniThrowException(env, "java/io/IOException", message);
}

int jniGetFDFromFileDescriptor(C_JNIEnv *env, jobject fileDescriptor) {
    auto *e = reinterpret_cast<JNIEnv *>(env);
    jint fd = -1;
    jclass fdClass = (*env)->FindClass(e, "java/io/FileDescriptor");

    if (fdClass != nullptr) {
        jfieldID fdClassDescriptorFieldID = (*env)->GetFieldID(e, fdClass, "descriptor", "I");
        if (fdClassDescriptorFieldID != nullptr && fileDescriptor != nullptr) {
            fd = (*env)->GetIntField(e, fileDescriptor, fdClassDescriptorFieldID);
        }
    }

    return fd;
}


static std::string jniGetStackTrace(C_JNIEnv *env, jthrowable exception) {
    auto *e = reinterpret_cast<JNIEnv *>(env);

    scoped_local_ref<jthrowable> currentException(env, (*env)->ExceptionOccurred(e));
    if (exception == nullptr) {
        exception = currentException.get();
        if (exception == nullptr) {
            return "<no pending exception>";
        }
    }

    if (currentException.get() != nullptr) {
        (*env)->ExceptionClear(e);
    }

    std::string trace;
    if (!getStackTrace(env, exception, trace)) {
        (*env)->ExceptionClear(e);
        getExceptionSummary(env, exception, trace);
    }

    if (currentException.get() != nullptr) {
        (*env)->Throw(e, currentException.get()); // rethrow
    }

    return trace;
}

void jniLogException(C_JNIEnv *env, int priority, const char *tag, jthrowable exception) {
    std::string trace(jniGetStackTrace(env, exception));
    __android_log_write(priority, tag, trace.c_str());
}

const char *jniStrError(int errnum, char *buf, size_t buflen) {
#if __GLIBC__
    // Note: glibc has a nonstandard strerror_r that returns char* rather than POSIX's int.
    // char *strerror_r(int errnum, char *buf, size_t n);
    return strerror_r(errnum, buf, buflen);
#else
    int rc = strerror_r(errnum, buf, buflen);
    if (rc != 0) {
        // (POSIX only guarantees a value other than 0. The safest
        // way to implement this function is to use C++ and overload on the
        // type of strerror_r to accurately distinguish GNU from POSIX.)
        snprintf(buf, buflen, "errno %d", errnum);
    }
    return buf;
#endif
}

jstring jniCreateString(C_JNIEnv *env, const jchar *unicodeChars, jsize len) {
    auto *e = reinterpret_cast<JNIEnv *>(env);
    return (*env)->NewString(e, unicodeChars, len);
}
