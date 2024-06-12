# the minimum version of CMake.
cmake_minimum_required(VERSION 3.4.1)
# 设置库名称
project(GXAnalyze)

# 配置 ANALYZE_CORE_ROOT_DIR 目录，关联GXAnalyzeCore文件
get_filename_component(ANALYZE_CORE_ROOT_DIR ${CMAKE_SOURCE_DIR}/../../../../../GXAnalyzeCore ABSOLUTE)
add_subdirectory(${ANALYZE_CORE_ROOT_DIR} GXAnalyzeCore)

if(DEFINED PACKAGE_FIND_FILE)
    include(${PACKAGE_FIND_FILE})
endif()

# 优化编译选项
if (${CMAKE_BUILD_TYPE} EQUAL "Release")
    #编译包大小优化选项 如表达式需要debug，请把以下三个编译选项注释掉
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Oz -flto -fdata-sections -ffunction-sections")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Oz -flto -fdata-sections -ffunction-sections")
    set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -O3 -flto  -Wl,--gc-sections -Wl")
else ()
    # nothing
endif ()

# 头文件处理
## 1.设置头文件读取路径
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/include
)


# 配置并链接库
##1. 配置库
add_library(
        # 设置库名称
        GXAnalyze
        # 设置库类型为动态库
        SHARED
        # 添加napi关联文件
        GXNapiAnalyze.cpp
        ${ANALYZE_CORE_ROOT_DIR}/GXAnalyze.cpp
        ${ANALYZE_CORE_ROOT_DIR}/GXATSNode.cpp
        ${ANALYZE_CORE_ROOT_DIR}/GXWordAnalyze.cpp
        )
# 2.关联GXAnalyze头文件（暴露头文件，添加NAPI桥接）
target_include_directories(GXAnalyze PUBLIC ${ANALYZE_CORE_ROOT_DIR} ${CMAKE_CURRENT_SOURCE_DIR})

##2. 链接库
target_link_libraries(GXAnalyze PUBLIC libace_napi.z.so libhilog_ndk.z.so)