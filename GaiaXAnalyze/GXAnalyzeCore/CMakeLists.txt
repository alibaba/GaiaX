
# 设置cmake最低版本
cmake_minimum_required(VERSION 3.4.1)

# 设置库名称
project(GXAnalyzeCore)

# 指定C++版本
set(CMAKE_CXX_STANDARD 11)

# 设置根目录
get_filename_component(CORE_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../ ABSOLUTE)

# 打印消息
message("LOG ENGINE CORE_ROOT_DIR = ${CORE_ROOT_DIR}")
message("LOG ENGINE CORE_DISTRIBUTION_DIR = ${CORE_DISTRIBUTION_DIR}")
message("LOG ENGINE CMAKE_SYSTEM_NAME = ${CMAKE_SYSTEM_NAME}")

# 设置库
add_library(${PROJECT_NAME} SHARED
        GXAnalyze.cpp
        GXATSNode.cpp
        GXWordAnalyze.cpp
        )

# 链接头文件
target_include_directories(${PROJECT_NAME} PRIVATE
        # 引入自有代码头文件
        ${CMAKE_CURRENT_SOURCE_DIR}
        )

# 链接子模块
target_link_libraries(${PROJECT_NAME})

# 链接 libdsm 模块
if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
elseif (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
elseif (${CMAKE_SYSTEM_NAME} MATCHES "Linux")
elseif (${CMAKE_SYSTEM_NAME} MATCHES "Android")

    target_link_libraries(${PROJECT_NAME}
            -landroid
            -llog
            )

elseif (${CMAKE_SYSTEM_NAME} MATCHES "iOS")
endif ()