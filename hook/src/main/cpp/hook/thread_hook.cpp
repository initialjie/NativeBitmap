//
// Created by assistant on 2025/10/24.
//

#include "thread_hook.h"
#include "logger.h"
#include "shadowhook.h"

#include <pthread.h>
#include <atomic>
#include <unistd.h>
#include <linux/prctl.h>
#include <sys/prctl.h>

#define TARGET_ART_LIB "libart.so"
#define THREAD_NAME_SIZE 16

#if defined(__arm__) // ARMv7 32-bit
#define TARGET_CREATE_NATIVE_THREAD "_ZN3art6Thread18CreateNativeThreadEP7_JNIEnvP8_jobjectjb"
#elif defined(__aarch64__) // ARMv8 64-bit
#define TARGET_CREATE_NATIVE_THREAD "_ZN3art6Thread18CreateNativeThreadEP7_JNIEnvP8_jobjectmb"
#endif

#define SIZE_1M_BYTE (1 * 1024 * 1024)

namespace ThreadHook {
    void *originalFunction = nullptr;
    void *stubFunction = nullptr;
    static std::atomic<size_t> gDesiredStackSize {0};
    static std::atomic<bool> gHookInstalled {false};

    typedef void (*ThreadCreateFunc)(JNIEnv *env, jobject java_peer, size_t stack_size, bool is_daemon);

    static bool currentThreadName(char *name) {
        return prctl(PR_GET_NAME, (unsigned long) name, 0, 0, 0) == 0;
    }

    static void createNativeThreadProxy(JNIEnv *env, jobject java_peer, size_t stack_size, bool is_daemon) {
        char threadName[THREAD_NAME_SIZE];
        size_t desiredSize = gDesiredStackSize.load();
        
        if (desiredSize > 0) {
            // 如果设置了期望的栈大小，使用它
            LOGI("Creating thread with custom stack size: %zu, thread name: %s", 
                 desiredSize, currentThreadName(threadName) ? threadName : "N/A");
            ((ThreadCreateFunc) originalFunction)(env, java_peer, desiredSize, is_daemon);
        } else if (stack_size == 0) {
            LOGI("Creating thread with default stack size, thread name: %s",
                 currentThreadName(threadName) ? threadName : "N/A");
            ((ThreadCreateFunc) originalFunction)(env, java_peer, stack_size, is_daemon);
        } else {
            // 使用原始栈大小
            ((ThreadCreateFunc) originalFunction)(env, java_peer, stack_size, is_daemon);
        }
    }

    bool install(JNIEnv *env) {
        if (gHookInstalled.load()) {
            LOGI("thread hook already installed");
            return true;
        }

#if defined(__arm__) || defined(__aarch64__)
        stubFunction = shadowhook_hook_sym_name(TARGET_ART_LIB, TARGET_CREATE_NATIVE_THREAD,
                                                (void *) createNativeThreadProxy,
                                                (void **) &originalFunction);
        if (stubFunction == nullptr) {
            const int err_num = shadowhook_get_errno();
            const char *errMsg = shadowhook_to_errmsg(err_num);
            LOGE("thread hook failed: %s", errMsg ? errMsg : "unknown error");
            if (errMsg) {
                delete errMsg;
            }
            return false;
        }
        
        gHookInstalled.store(true);
        LOGI("thread hook installed successfully from %s", TARGET_ART_LIB);
        return true;
#else
        LOGE("thread hook failed: unsupported architecture");
        return false;
#endif
    }

    void setDesiredStackSize(size_t bytes) {
        size_t value = bytes;
#ifdef PTHREAD_STACK_MIN
        if (value > 0 && value < (size_t)PTHREAD_STACK_MIN) {
            value = (size_t)PTHREAD_STACK_MIN;
        }
#endif
        if (value > 0) {
            long page = sysconf(_SC_PAGESIZE);
            if (page > 0) {
                size_t p = (size_t)page;
                value = (value + p - 1) / p * p; // ceil to page size
            }
        }
        gDesiredStackSize.store(value);
        LOGI("desired thread stack size set: %zu", value);
    }
    
    size_t getDesiredStackSize() {
        return gDesiredStackSize.load();
    }
}
