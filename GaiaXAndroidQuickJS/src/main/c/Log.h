#ifndef QUICK_JS_LOG_H
#define QUICK_JS_LOG_H

#ifdef __ANDROID__

#include <android/log.h>

#define _LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "[GaiaX][JS][NATIVE]", __VA_ARGS__))
#define _LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "[GaiaX][JS][NATIVE]", __VA_ARGS__))

#else

#define _LOGD(...) (void)printf(__VA_ARGS__);
#define _LOGE(...) (void)printf(__VA_ARGS__);

#endif

#endif
