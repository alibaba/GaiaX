cmake_minimum_required(VERSION 3.4.1)

project(gxquickjs)

file(STRINGS "gxquickjs/VERSION" CONFIG_VERSION)

include_directories(gxquickjs)

set(COMMON_FLAGS -DCONFIG_VERSION=\"${CONFIG_VERSION}\")

set(QUICKJS_LIB_SOURCES
        gxquickjs-patch.c
        gxquickjs/libregexp.c
        gxquickjs/libunicode.c
        gxquickjs/libbf.c
        gxquickjs/cutils.c
        )

add_library(${PROJECT_NAME} SHARED
        ${QUICKJS_LIB_SOURCES}
        )

add_compile_options(-fno-optimize-sibling-calls -fno-omit-frame-pointer)

target_compile_options(${PROJECT_NAME} PRIVATE
        ${COMMON_FLAGS}
        )

target_include_directories(${PROJECT_NAME} PUBLIC
        gxquickjs
        .)
