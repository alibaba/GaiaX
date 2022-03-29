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

package com.alibaba.gaiax.template.expression

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.analyze.GXAnalyze

/**
 * @suppress
 */
object GXExpressionUtils {

    private lateinit var analyze: GXAnalyze

    fun initAnalyze() {
        analyze = GXAnalyze()
        analyze.initComputeExtend(object : GXAnalyze.IComputeExtend {

            /**
             * 用于处理取值逻辑
             */
            override fun computeValueExpression(valuePath: String, source: Any): Long {
                return 0L
            }

            /**
             * 用于处理函数逻辑
             */
            override fun computeFunctionExpression(functionName: String, params: LongArray): Long {
                return 0L
            }
        })
    }

    class GXAnalyzeWrapper(val expression: String) : GXIExpression {

        override fun value(templateData: JSON?): Any? {
            return analyze.getResult(expression, templateData ?: JSONObject())
        }
    }

    fun create(expression: Any?): GXIExpression? {
        return GXExpression.create(expression)
    }

    fun isCondition(condition: Any?): Boolean {
        return GXExpression.isCondition(condition)
    }
}