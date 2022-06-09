#ifndef _GXANALYZE_CORE_H_
#define _GXANALYZE_CORE_H_

#include <iostream>
#include <stdlib.h>
#include "GXATSNode.h"
#include "GXWordAnalyze.h"
#include <vector>
#include "GXValue.h"
#include <set>
#include <unordered_map>
#include <regex>


using namespace std;

class GXAnalyze {
public:

    GXAnalyze();

    ~GXAnalyze();

    long getValue(string expression, void* source);

    //获取数据 $
    virtual long getSourceValue(string valuePath, void* source) = 0;

    //获取方法 Function
    virtual long
    getFunctionValue(string funName, long *paramPointers, int paramsSize, string source) = 0;

    virtual void throwError(string message) = 0;

private:

    //获取两个数值计算的结果
    GXATSNode doubleCalculate(GXATSNode value1, GXATSNode value2, string op);

    //获取单个数值计算的结果
    GXATSNode singleCalculate(GXATSNode value1, string op);


    string grammarScanner(vector<GXATSNode> array);

    long check(string s, vector<GXATSNode> array, void *p_analyze, void* source);
};

#endif /*include _GXAnalyze__H_*/
