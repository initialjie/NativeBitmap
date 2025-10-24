//
// Created by ptrain on 2022/12/27.
//

#ifndef NATIVEHOOK_LOGGER_H
#define NATIVEHOOK_LOGGER_H
#define TAG "nativehook"

#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#endif //NATIVEHOOK_LOGGER_H
