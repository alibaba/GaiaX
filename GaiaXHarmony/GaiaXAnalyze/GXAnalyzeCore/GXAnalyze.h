/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#ifndef _GXANALYZE_CORE_H_
#define _GXANALYZE_CORE_H_

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

    long getValue(string expression, void *source);

    //获取数据 $
    virtual long getSourceValue(string valuePath, void *source) = 0;

    //获取方法 Function
    virtual long
    getFunctionValue(string funName, long *paramPointers, int paramsSize, string source) = 0;

    virtual void throwError(string message) = 0;

private:

    //获取两个数值计算的结果
    GXATSNode doubleCalculate(GXATSNode value1, GXATSNode value2, string op);

    //获取单个数值计算的结果
    GXATSNode singleCalculate(GXATSNode value1, string op);

    long check(string s, vector<GXATSNode> array, void *p_analyze, void* source,string expression);

    long calculateCache(string cache,vector<GXATSNode> array,void *p_analyze, void* source);
};

#endif /*include _GXAnalyze__H_*/
