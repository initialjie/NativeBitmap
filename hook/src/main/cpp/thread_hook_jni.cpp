//
// ThreadHook JNI implementation
//

#include <jni.h>
#include "logger.h"
#include "thread_hook.h"

extern "C" JNIEXPORT jboolean JNICALL
Java_com_mico_libx_hook_thread_ThreadHook_nInitThreadHook(JNIEnv *env, jobject thiz, jlong stackSizeBytes) {
    if (stackSizeBytes < 0) {
        LOGE("Invalid stack size: %lld", (long long)stackSizeBytes);
        return JNI_FALSE;
    }
    
    ThreadHook::setDesiredStackSize((size_t)stackSizeBytes);
    
    if (ThreadHook::install(env)) {
        LOGI("thread hook installed successfully with stack size: %zu", (size_t)stackSizeBytes);
        return JNI_TRUE;
    } else {
        LOGE("thread hook installation failed");
        return JNI_FALSE;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_mico_libx_hook_thread_ThreadHook_nSetThreadStackSize(JNIEnv *env, jobject thiz, jlong bytes) {
    if (bytes < 0) {
        return;
    }
    ThreadHook::setDesiredStackSize((size_t)bytes);
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_mico_libx_hook_thread_ThreadHook_nGetThreadStackSize(JNIEnv *env, jobject thiz) {
    return (jlong)ThreadHook::getDesiredStackSize();
}
