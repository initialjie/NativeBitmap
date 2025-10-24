package com.mico.libx.hook.nativebitmap

import android.os.Build
import android.util.Log
import com.mico.libx.hook.shadowhook.ShadowHookManager
import java.util.concurrent.atomic.AtomicBoolean

object NativeBitmap {
    const val TAG = "nativebitmap"
    private val isInitialized = AtomicBoolean(false)

    fun init(isDebug: Boolean) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        ) {
            Log.e(TAG, "init failed! unSupport os version!")
            return
        }

        if (isInitialized.compareAndSet(false, true)) {
            // 首先初始化 ShadowHook
            if (!ShadowHookManager.init(isDebug)) {
                Log.e(TAG, "ShadowHook initialization failed, aborting NativeBitmap init")
                return
            }

            try {
                System.loadLibrary(TAG)
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load nativebitmap library", e)
                return
            }

            nInit(isDebug)
            Log.i(TAG, "NativeBitmap init done!")
        } else {
            Log.e(TAG, "NativeBitmap has been initialized!")
        }
    }

    private external fun nInit(isDebug: Boolean)
}