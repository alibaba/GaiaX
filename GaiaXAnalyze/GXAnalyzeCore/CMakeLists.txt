
# 设置cmake最低版本
cmake_minimum_required(VERSION 3.4.1)

# 设置库名称
project(GXAnalyzeCore)

# 指定C++版本
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Oz -flto -fdata-sections -ffunction-sections")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Oz -flto -fdata-sections -ffunction-sections  -s")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -O3 -flto  -Wl,--gc-sections -Wl,-s")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-rtti")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")

# 设置根目录
get_filename_component(CORE_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../ ABSOLUTE)

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

# 链接模块
if (${CMAKE_SYSTEM_NAME} MATCHES "Android")

    target_link_libraries(${PROJECT_NAME}
            -landroid
            -llog
            )
endif ()