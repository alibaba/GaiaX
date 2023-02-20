cmake_minimum_required(VERSION 3.4.1)
# 设置库名称
project(GXAnalyzeAndroid)

# 设置Root目录
get_filename_component(ANDROID_ROOT_DIR ${CMAKE_SOURCE_DIR}/../../../../ ABSOLUTE)

add_subdirectory(${ANDROID_ROOT_DIR}/GXAnalyzeCore GXAnalyzeCore)

# 设置头文件
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
)

if (${CMAKE_BUILD_TYPE} EQUAL "Release")
    #编译包大小优化选项 如表达式需要debug，请把以下三个编译选项注释掉
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Oz -flto -fdata-sections -ffunction-sections")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Oz -flto -fdata-sections -ffunction-sections")
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -O3 -flto  -Wl,--gc-sections -Wl")
else ()
    # nothing
endif ()


add_library( # Sets the name of the library.
        ${PROJECT_NAME}

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        GXJniAnalyze.cpp
        )

# 链接头文件
target_include_directories(${PROJECT_NAME} PRIVATE

        ${ANDROID_ROOT_DIR}/GXAnalyzeCore

        ${CMAKE_CURRENT_SOURCE_DIR}
        )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries(${PROJECT_NAME}
        GXAnalyzeCore
        -landroid
        -llog)