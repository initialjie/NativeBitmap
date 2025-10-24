package com.mico.libx.hook.shadowhook

import android.util.Log
import com.bytedance.shadowhook.ShadowHook
import java.util.concurrent.atomic.AtomicBoolean

object ShadowHookManager {
    const val TAG = "ShadowHookManager"
    private val isInitialized = AtomicBoolean(false)
    private val initResult = AtomicBoolean(false)

    /**
     * 初始化 ShadowHook，确保只初始化一次
     * @param isDebug 是否为调试模式
     * @return 初始化是否成功
     */
    @JvmStatic
    fun init(isDebug: Boolean = false): Boolean {
        if (isInitialized.compareAndSet(false, true)) {
            try {
                Log.i(TAG, "Initializing ShadowHook...")
                ShadowHook.init(
                    ShadowHook.ConfigBuilder()
                        .setDebuggable(isDebug)
                        .setMode(ShadowHook.Mode.UNIQUE)
                        .build()
                )
                initResult.set(true)
                Log.i(TAG, "ShadowHook initialized successfully")
                return true
            } catch (e: Exception) {
                Log.e(TAG, "ShadowHook initialization failed", e)
                initResult.set(false)
                return false
            }
        } else {
            Log.w(TAG, "ShadowHook already initialized, result: ${initResult.get()}")
            return initResult.get()
        }
    }

    /**
     * 检查 ShadowHook 是否已初始化成功
     * @return 是否初始化成功
     */
    @JvmStatic
    fun isInitialized(): Boolean {
        return isInitialized.get() && initResult.get()
    }

    /**
     * 获取初始化结果
     * @return 初始化是否成功
     */
    @JvmStatic
    fun getInitResult(): Boolean {
        return initResult.get()
    }
}