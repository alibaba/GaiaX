# the minimum version of CMake.
cmake_minimum_required(VERSION 3.4.1)
project(harmony_stretch)

set(NATIVERENDER_ROOT_PATH ${CMAKE_CURRENT_SOURCE_DIR})

include_directories(${NATIVERENDER_ROOT_PATH}
                    ${NATIVERENDER_ROOT_PATH}/include)

add_library(gxstretch SHARED stretch.cpp)

add_library(stretch SHARED IMPORTED)
set_target_properties(stretch
    PROPERTIES
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../../../libs/${OHOS_ARCH}/libstretch.so)

target_link_libraries(gxstretch PUBLIC libace_napi.z.so stretch)