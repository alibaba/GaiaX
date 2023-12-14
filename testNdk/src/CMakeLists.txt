    SET(LIBHELLO_SRC hello.cpp)

    # 设置编译参数
    SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O0")   
 
    # 设置链接参数，具体参数可以忽略，纯粹为了举例
    SET(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,--emit-relocs --verbose")    

    # 添加一个libsum动态库目标，编译成功会生成一个libsum.so
    ADD_LIBRARY(sum SHARED sum.cpp)

    # 生成可执行程序，添加一个Hello的可执行程序目标，编译成功会生成一个Hello可执行程序
    ADD_EXECUTABLE(Hello ${LIBHELLO_SRC})

    # 指定Hello目标include目录路径
    TARGET_INCLUDE_DIRECTORIES(Hello PUBLIC ../include)

    # 指定Hello目标需要链接的库名字
    TARGET_LINK_LIBRARIES(Hello PUBLIC sum)