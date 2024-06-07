// logger.h
#ifndef LOGGER_H
#define LOGGER_H

extern "C" {
void hilog_debug(const char *message);
void hilog_info(const char *message);
void hilog_error(const char *message);
}


#endif // LOGGER_H