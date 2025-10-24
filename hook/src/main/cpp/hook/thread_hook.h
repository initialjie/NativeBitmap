//
// Created by assistant on 2025/10/24.
//

#ifndef NATIVEBITMAP_THREAD_HOOK_H
#define NATIVEBITMAP_THREAD_HOOK_H

#include <jni.h>
#include <stddef.h>

namespace ThreadHook {
    extern void *originalFunction;
    extern void *stubFunction;

    bool install(JNIEnv *env);
    void setDesiredStackSize(size_t bytes);
    size_t getDesiredStackSize();
}

#endif //NATIVEBITMAP_THREAD_HOOK_H
