# the minimum version of CMake.
cmake_minimum_required(VERSION 3.4.1)
project(GXStretchBinding)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

if(DEFINED PACKAGE_FIND_FILE)
    include(${PACKAGE_FIND_FILE})
endif()

# gxstretch module 配置
include_directories(${NATIVERENDER_ROOT_PATH}
                    ${NATIVERENDER_ROOT_PATH}/include)
add_library(gxstretch SHARED
    napi_init.cpp
    hilog.cpp
)


# https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/build-with-ndk-prebuilts-0000001821000405-V5
# 目前会崩溃，先不要使用
add_library(stretch SHARED IMPORTED)
set_target_properties(stretch
    PROPERTIES
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${OHOS_ARCH}/libstretch.so)

# https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/use-napi-process-0000001774120794-V5
target_link_libraries(gxstretch PUBLIC libace_napi.z.so
        # hilog
        libhilog_ndk.z.so
        # stretch.so 配置
        ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${OHOS_ARCH}/libstretch.so)