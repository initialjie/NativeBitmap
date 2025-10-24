# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.
project("nativebitmap")

# 公共配置
find_package(shadowhook REQUIRED CONFIG)
find_library(log-lib log)

include_directories(
        "log/"
        "hook/")

# 第一个库：NativeBitmap
add_library(nativebitmap SHARED
        native_bitmap.cpp
        hook/bitmap_hook.cpp
)

target_link_libraries(nativebitmap
        shadowhook::shadowhook
        ${log-lib}
)

# 第二个库：ThreadHook
add_library(threadhook SHARED
        thread_hook_jni.cpp
        hook/thread_hook.cpp
)

target_link_libraries(threadhook
        shadowhook::shadowhook
        ${log-lib}
)