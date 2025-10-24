package com.mico.libx.hook.thread

import android.util.Log
import com.mico.libx.hook.shadowhook.ShadowHookManager
import java.util.concurrent.atomic.AtomicBoolean

object ThreadHook {
    const val TAG = "ThreadHook"
    private val isInitialized = AtomicBoolean(false)

    @JvmStatic
    fun init(isDebug: Boolean, stackSizeBytes: Long): Boolean {
        if (isInitialized.compareAndSet(false, true)) {
            // 首先初始化 ShadowHook
            if (!ShadowHookManager.init(isDebug)) {
                Log.e(
                    TAG,
                    "ShadowHook initialization failed, aborting NativeBitmap init"
                )
                return false
            }

            try {
                System.loadLibrary("threadhook")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load threadhook library", e)
                return false
            }

            val result = nInitThreadHook(stackSizeBytes)
            if (result) {
                Log.i(TAG, "ThreadHook initialized successfully with stack size: $stackSizeBytes")
            } else {
                Log.e(TAG, "ThreadHook initialization failed")
            }
            return result
        } else {
            Log.w(TAG, "ThreadHook has been initialized!")
            return true
        }
    }

    @JvmStatic
    fun setStackSize(bytes: Long) {
        nSetThreadStackSize(bytes)
    }

    @JvmStatic
    fun getStackSize(): Long {
        return nGetThreadStackSize()
    }

    @JvmStatic
    fun isInitialized(): Boolean {
        return isInitialized.get()
    }

    private external fun nInitThreadHook(stackSizeBytes: Long): Boolean
    private external fun nSetThreadStackSize(bytes: Long)
    private external fun nGetThreadStackSize(): Long
}