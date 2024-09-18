cmake_minimum_required(VERSION 3.4.1)

project(GXAnalyzeDev)

set(CMAKE_CXX_STANDARD 11)

# 添加子模块目录
add_subdirectory(../GXAnalyzeCore ${CMAKE_BINARY_DIR}/GXAnalyzeCore-build)


add_executable(GXAnalyzeDev main.cpp)

target_include_directories(GXAnalyzeDev PRIVATE ../GXAnalyzeCore)
target_link_libraries(GXAnalyzeDev PRIVATE GXAnalyzeCore)
