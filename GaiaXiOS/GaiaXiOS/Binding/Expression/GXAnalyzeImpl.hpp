//
//  GXAnalyzeImpl.hpp
//  GXAnalyzeiOS
//
//  Created by 张敬成 on 2022/3/18.
//

#ifndef GXAnalyzeImpl_hpp
#define GXAnalyzeImpl_hpp

#include <stdio.h>
#include <string>
#include <iostream>
#include "GXAnalyze.h"

using namespace std;

class GXAnalyzeImpl: public GXAnalyze {
    
public:
    //设置self
    void setup(void);
    
    //解析取值
    long getSourceValue(string valuePath, void* source);

    //解析方法
    long getFunctionValue(string funName, long *paramPointers, int paramsSize, string source);

    //异常抛出
    void throwError(string message);
    
private:
    void * self;
    
};

#endif /* GXAnalyzeImpl_hpp */
