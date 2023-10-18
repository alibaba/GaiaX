    #include <iostream>
    #include "sum.h"

    int main(int argc,const char **argv)
    {
        std::cout<< "hello world!" <<std::endl;
        int total = sum(1, 100);
        std::cout<< "Sum 1 + 100=" << total << std::endl;
        return 0;
    }