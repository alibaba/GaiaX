    # 指定CMake的最小版本
    CMAKE_MINIMUM_REQUIRED(VERSION 3.16)

    # 工程名称，这里我们就叫HELLO
    PROJECT(HELLO)

    #添加一个子目录并构建该子目录。
    ADD_SUBDIRECTORY(src bin)