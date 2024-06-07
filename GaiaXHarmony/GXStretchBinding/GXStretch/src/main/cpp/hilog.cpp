// logger.cpp
#include "hilog.h"
#include "hilog/log.h"

#undef LOG_DOMAIN

#undef LOG_TAG

#define LOG_DOMAIN 0x3200 // 全局domain宏，标识业务领域

#define LOG_TAG "GXSRust" // 全局tag宏，标识模块日志tag

void hilog_debug(const char *message) { OH_LOG_DEBUG(LOG_APP, "%{public}s", message); }
void hilog_info(const char *message) { OH_LOG_INFO(LOG_APP, "%{public}s", message); }
void hilog_error(const char *message) { OH_LOG_ERROR(LOG_APP, "%{public}s", message); }