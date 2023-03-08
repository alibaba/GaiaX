cmake_minimum_required(VERSION 3.4.1)

project(gaiaxjs)

if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    option(LEAK_TRIGGER "Add a leak trigger" ON)
else ()
    option(LEAK_TRIGGER "Add a leak trigger" OFF)
endif (CMAKE_BUILD_TYPE STREQUAL "Debug")

add_subdirectory(../../../quickjs quickjs)

#add_library(quickjs
#        SHARED
#        IMPORTED)
#
#set_target_properties( # Specifies the target library.
#        quickjs
#
#        # Specifies the parameter you want to define.
#        PROPERTIES
#
#        # Provides the path to the library you want to import.
#        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../${CMAKE_ANDROID_ARCH_ABI}/libquickjs.so)
#
#include_directories(
#        ../../../quickjs
#        ../../../quickjs/quickjs
#)
#

set(QUICKJS_ANDROID_SOURCES
        quickjs-jni.c
        java-method.c
        java-module-method.c
        java-object.c
        java-common.c
        )

if (LEAK_TRIGGER)
    set(COMMON_FLAGS -DLEAK_TRIGGER)
else ()
    set(COMMON_FLAGS)
endif (LEAK_TRIGGER)

add_library(${PROJECT_NAME} SHARED ${QUICKJS_ANDROID_SOURCES})

target_compile_options(${PROJECT_NAME} PRIVATE ${COMMON_FLAGS})

target_link_libraries(${PROJECT_NAME} PRIVATE
        quickjs
        -llog)
