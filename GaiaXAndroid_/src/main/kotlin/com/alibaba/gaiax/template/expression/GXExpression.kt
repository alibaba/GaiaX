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
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.utils.getAnyExt
import com.alibaba.gaiax.utils.getIntExt
import com.alibaba.gaiax.utils.setValueExt
import java.util.regex.Pattern

/**
 * 模板中的表达式
 * @suppress
 */
sealed class GXExpression : GXIExpression {

    /**
     * 根据表达式，获取期望的数据
     */
    abstract override fun value(templateData: JSON?): Any?

    companion object {

        // 完全匹配 ${}
        @Suppress("RegExpRedundantEscape")
        val valueFullRegex = Pattern.compile("^\\$\\{(.*?)\\}$")

        // 匹配 ${}
        @Suppress("RegExpRedundantEscape")
        val valueRegex = Pattern.compile("\\$\\{(.*?)\\}")

        fun isFitContentCondition(condition: String): Boolean {
            return if (condition == "true" || condition == "1" || condition == "1.0") {
                true
            } else if (condition == "false" || condition == "0" || condition == "0.0") {
                false
            } else {
                false
            }
        }

        fun isCondition(condition: Any?): Boolean {
            return if (condition is Boolean && condition == true) {
                true
            } else if (condition is Number && condition.toFloat() != 0.0F) {
                true
            } else if (condition == "0" || condition == "false" || condition == false || condition == 0 || condition == 0.0F) {
                false
            } else if (condition == "1" || condition == "true" || (condition is String && condition.isNotBlank())) {
                true
            } else if (condition is String && condition.isBlank()) {
                return false
            } else {
                condition != null
            }
        }

        fun create(expression: Any?): GXExpression? {
            if (expression == null) {
                return null
            }
            return when (expression) {
                is JSON -> when {
                    GXJsonObj.isExpression(expression) -> GXJsonObj.create(expression as JSONObject)
                    GXJsonArrayObj.isExpression(expression) -> GXJsonArrayObj.create(expression as JSONArray)
                    else -> null
                }
                is String -> {
                    val exp = expression.trim()
                    when {
                        GXSelf.isExpression(exp) -> GXSelf
                        GXNull.isExpression(exp) -> GXNull.create()
                        GXBool.isExpression(exp) -> GXBool.create(exp)
                        GXInt.isExpression(exp) -> GXInt.create(exp)
                        GXFloat.isExpression(exp) -> GXFloat.create(exp)
                        GXString.isExpression(exp) -> GXString.create(exp)
                        GXEval.isExpression(exp) -> GXEval.create(exp)
                        GXEnv.isExpression(exp) -> GXEnv.create(exp)
                        GXScroll.isExpression(exp) -> GXScroll.create(exp)
                        GXSize.isExpression(exp) -> GXSize.create(exp)
                        GXTextValue.isExpression(exp) -> GXTextValue.create(exp)
                        GXValue.isExpression(exp) -> GXValue.create(exp)
                        GXTernaryValue3.isExpression(exp) -> GXTernaryValue3.create(exp)
                        GXTernaryValue1.isExpression(exp) -> GXTernaryValue1.create(exp)
                        GXTernaryValue2.isExpression(exp) -> GXTernaryValue2.create(exp)
                        GXText.isExpression(exp) -> GXText.create(exp)
                        expression == "\n" -> GXText.create("\n")
                        else -> null
                    }
                }
                is Boolean -> GXBool(expression)
                is Int -> GXInt(expression)
                is Float -> GXFloat(expression)
                else -> null
            }
        }
    }

    /**
     * 自我表达式：$$
     */
    object GXSelf : GXExpression() {

        override fun toString(): String {
            return "Self()"
        }

        override fun value(rawJson: JSON?): Any? {
            return rawJson
        }

        fun isExpression(expression: String): Boolean {
            return "$$" == expression
        }
    }

    /**
     * 用于逻辑运算 == != >= <= > < && || %
     */
    data class GXEval(val operate: String, val leftValue: GXExpression?, val rightValue: GXExpression?) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            val left = leftValue?.value(rawJson)
            val right = rightValue?.value(rawJson)
            return when (operate) {
                "==" -> equal(left, right)
                "!=" -> notEqual(left, right)
                ">=" -> greaterThanOrEqual(left, right)
                ">" -> greaterThan(left, right)
                "<=" -> lessThanOrEqual(left, right)
                "<" -> lessThan(left, right)
                "&&" -> doubleAnd(left, right)
                "||" -> doubleOr(left, right)
                "%" -> mod(left, right)
                else -> false
            }
        }

        private fun mod(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> {
                    if (left is Int && right is Int) left.toInt() % right.toInt()
                    else if (left is Float && right is Float) left.toFloat() % right.toFloat()
                    else left.toFloat() % right.toFloat()
                }
                else -> false
            }
        }

        private fun doubleOr(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> (left.toFloat() != 0.0F) || (right.toFloat() != 0.0F)
                left is Number && right is Boolean -> (left.toFloat() != 0.0F) || right
                left is Number && right is String -> true
                left is Boolean && right is Boolean -> left || right
                left is Boolean && right is Number -> left || (right.toFloat() != 0.0F)
                left is Boolean && right is String -> true
                left is String && right is String -> true
                left is String && right is Boolean -> true
                left is String && right is Number -> true
                left == null -> {
                    when (right) {
                        is Boolean -> right
                        is Number -> right.toFloat() != 0.0F
                        is String -> true
                        else -> false
                    }
                }
                right == null -> {
                    when (left) {
                        is Boolean -> left
                        is Number -> left.toFloat() != 0.0F
                        is String -> true
                        else -> false
                    }
                }
                else -> false
            }
        }

        private fun doubleAnd(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> (left.toFloat() != 0.0F) && (right.toFloat() != 0.0F)
                left is Number && right is Boolean -> (left.toFloat() != 0.0F) && right
                left is Number && right is String -> left.toFloat() != 0.0F
                left is Boolean && right is Boolean -> left && right
                left is Boolean && right is Number -> left && (right.toFloat() != 0.0F)
                left is Boolean && right is String -> left
                left is String && right is String -> true
                left is String && right is Boolean -> right
                left is String && right is Number -> right.toFloat() != 0.0F
                left == null -> false
                right == null -> false
                else -> false
            }
        }

        private fun lessThan(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> left.toFloat() < right.toFloat()
                else -> false
            }
        }

        private fun lessThanOrEqual(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> left.toFloat() <= right.toFloat()
                else -> false
            }
        }

        private fun greaterThanOrEqual(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> left.toFloat() >= right.toFloat()
                else -> false
            }
        }

        private fun greaterThan(left: Any?, right: Any?): Any? {
            return when {
                left is Number && right is Number -> left.toFloat() > right.toFloat()
                else -> false
            }
        }

        private fun notEqual(left: Any?, right: Any?): Any? {
            return when {
                left is String && right is String -> left != right
                left is Number && right is Number -> left.toFloat() != right.toFloat()
                left is Boolean && right is Boolean -> left != right
                left is Number && right is Boolean -> right == true && (left.toFloat() == 0.0F) || right == false && (left.toFloat() != 0.0F)
                left is Boolean && right is Number -> left == true && (right.toFloat() == 0.0F) || left == false && (right.toFloat() != 0.0F)
                left == null && right == null -> false
                else -> false
            }
        }

        private fun equal(left: Any?, right: Any?): Any? {
            return when {
                left is String && right is String -> left == right
                left is Number && right is Number -> left.toFloat() == right.toFloat()
                left is Boolean && right is Boolean -> left == right
                left is Number && right is Boolean -> right == true && (left.toFloat() != 0.0F) || right == false && (left.toFloat() == 0.0F)
                left is Boolean && right is Number -> left == true && (right.toFloat() != 0.0F) || left == false && (right.toFloat() == 0.0F)
                left == null && right == null -> true
                else -> false
            }
        }

        override fun toString(): String {
            return "GEval(operate='$operate', leftValue=$leftValue, rightValue=$rightValue)"
        }

        companion object {

            fun isExpression(value: String): Boolean {
                return value.startsWith("eval(") && value.endsWith(")")
            }

            fun create(value: String): GXEval {
                val realValue = value.substring("eval(".length, value.length - 1)
                createEval("==", realValue)?.let { return it }
                createEval(">=", realValue)?.let { return it }
                createEval(">", realValue)?.let { return it }
                createEval("<=", realValue)?.let { return it }
                createEval("<", realValue)?.let { return it }
                createEval("!=", realValue)?.let { return it }
                createEval("||", realValue)?.let { return it }
                createEval("&&", realValue)?.let { return it }
                createEval("%", realValue)?.let { return it }
                return GXEval("", null, null)
            }

            private fun createEval(operate: String, realValue: String): GXEval? {
                realValue.split(operate).let {
                    if (it.size == 2) {
                        val leftValue = it[0]
                        val rightValue = it[1]
                        return GXEval(operate, GXExpression.create(leftValue), GXExpression.create(rightValue))
                    }
                }
                return null
            }
        }
    }

    /**
     * 用于取环境变量
     */
    data class GXEnv(val value: String) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
//            return GaiaXProviderImpl.instance.features?.getEnvExpressionResult(value)
            return false
        }

        override fun toString(): String {
            return "GEnv(value='$value')"
        }

        companion object {

            fun isExpression(value: String): Boolean {
                return value.startsWith("env(") && value.endsWith(")")
            }

            fun create(value: String): GXEnv {
                val realValue = value.substring("env(".length, value.length - 1)
                return GXEnv(realValue)
            }
        }
    }

    /**
     * 用于取容器变量
     */
    data class GXScroll(val value: String) : GXExpression() {

        override fun value(rawJson: JSON?): Any {
            return rawJson?.getIntExt(GAIAX_SCROLL_POSITION) ?: -1
        }

        override fun toString(): String {
            return "GScroll(value='$value')"
        }

        companion object {

            const val GAIAX_SCROLL_POSITION = "gaiax_scroll_position"

            fun isExpression(value: String): Boolean {
                return value.startsWith("scroll(") && value.endsWith(")")
            }

            fun create(value: String): GXScroll {
                val realValue = value.substring("scroll(".length, value.length - 1)
                return GXScroll(realValue)
            }
        }
    }

    /**
     * 用于计算字符串、JSONArray和JSONObject的size
     */
    data class GXSize(val value: GXExpression?) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return when (val result = value?.value(rawJson)) {
                is String -> result.length
                is JSONArray -> result.size
                is JSONObject -> result.size
                else -> null
            }
        }

        override fun toString(): String {
            return "GSize(value=$value)"
        }

        companion object {

            fun isExpression(value: String): Boolean {
                return value.startsWith("size(") && value.endsWith(")")
            }

            fun create(value: String): GXSize {
                val realValue = value.substring("size(".length, value.length - 1)
                return GXSize(GXExpression.create(realValue))
            }
        }
    }

    /**
     * JSON Obj
     */
    data class GXJsonObj(val value: JSONObject) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            val result = JSONObject()
            if (value.isNotEmpty()) {
                value.forEach {
                    if (it.key != null && it.value != null) {
                        if (it.value is GXExpression) {
                            result[it.key] = (it.value as GXExpression).value(rawJson)
                        } else {
                            result[it.key] = it.value
                        }
                    }
                }
            }
            return result
        }

        override fun toString(): String {
            return "GJsonObj(value=$value)"
        }

        companion object {
            fun create(value: JSONObject): GXJsonObj {
                val result = JSONObject()
                value.forEach {
                    if (it.key != null && it.value != null) {
                        if (it.value is Int || it.value is Boolean) {
                            result[it.key] = it.value
                        } else {
                            result[it.key] = create(it.value)
                        }
                    }
                }
                return GXJsonObj(result)
            }

            fun isExpression(expression: Any): Boolean {
                return expression is JSONObject
            }
        }
    }

    /**
     * JSON Array
     */
    data class GXJsonArrayObj(val value: JSONArray) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            val result = JSONArray()
            if (value.isNotEmpty()) {
                value.forEach {
                    if (it != null && it is GXExpression) {
                        result.add(it.value(rawJson))
                    }
                }
            }
            return result
        }

        override fun toString(): String {
            return "GJsonArrayObj(value=$value)"
        }

        companion object {
            fun create(value: JSONArray): GXJsonArrayObj {
                val result = JSONArray()
                value.forEach {
                    if (it != null) {
                        result.add(create(it))
                    }
                }
                return GXJsonArrayObj(result)
            }

            fun isExpression(expression: Any): Boolean {
                return expression is JSONArray
            }
        }
    }

    /**
     * 布尔值
     */
    data class GXBool(val value: Boolean) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GBool(value=$value)"
        }

        companion object {
            fun create(value: String): GXBool {
                return GXBool(value.toBoolean())
            }

            fun isExpression(expression: String): Boolean {
                return expression == "true" || expression == "false"
            }
        }
    }

    /**
     * 字符串值
     */
    data class GXString(val value: String) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return value
        }

        companion object {
            fun create(value: String): GXString {
                return GXString(value.substring(1, value.length - 1))
            }

            fun isExpression(expression: String): Boolean {
                return expression.startsWith("'") && expression.endsWith("'")
            }
        }
    }

    /**
     * 数字值: xxx
     */
    data class GXFloat(val value: Float) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GFloat(value=$value)"
        }

        companion object {
            fun create(value: String): GXFloat {
                return GXFloat(value.toFloat())
            }

            fun isExpression(expression: String): Boolean {
                return try {
                    expression.toFloat().toString() == expression
                } catch (e: Exception) {
                    false
                }
            }
        }
    }

    /**
     * 数字值: xxx
     */
    data class GXInt(val value: Int) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GInt(value=$value)"
        }

        companion object {
            fun create(value: String): GXInt {
                return GXInt(value.toInt())
            }

            fun isExpression(expression: String): Boolean {
                return try {
                    expression.toInt().toString() == expression
                } catch (e: Exception) {
                    false
                }
            }
        }
    }

    class GXNull : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return null
        }

        override fun toString(): String {
            return "GNull()"
        }

        companion object {
            fun create(): GXNull {
                return GXNull()
            }

            fun isExpression(expression: String): Boolean {
                return expression == "null"
            }
        }
    }

    /**
     * 常量值: xxx
     */
    data class GXText(val value: String) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            // 处理null字符串
            if (value == "null") {
                return null
            }
            return value
        }

        override fun toString(): String {
            return "GText(value='$value')"
        }

        companion object {
            fun create(value: String): GXText {
                return GXText(value)
            }

            fun isExpression(expression: String): Boolean {
                return expression.isNotEmpty()
            }
        }
    }

    /**
     * 取值表达式: ${data}
     */
    data class GXValue(val value: String) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            return rawJson?.getAnyExt(value)
        }

        fun setData(rawJson: JSON?, target: Any) {
            rawJson?.setValueExt(value, target)
        }

        override fun toString(): String {
            return "GValue(value='$value')"
        }

        companion object {

            fun create(expression: String): GXValue {
                val matcher = valueRegex.matcher(expression)
                if (matcher.find()) {
                    return GXValue(matcher.group(1))
                }
                return GXValue("")
            }

            fun isExpression(expression: String): Boolean {
                return if (valueFullRegex.matcher(expression).find()) {
                    val matcher = valueRegex.matcher(expression)
                    var count = 0
                    while (matcher.find()) {
                        count++
                        if (count >= 2) {
                            break
                        }
                    }
                    count == 1
                } else {
                    false
                }
            }
        }
    }

    /**
     * 三元组合表达式: @{ ${data} ? b : c }
     */
    data class GXTernaryValue1(
        val condition: GXExpression?,
        val trueBranch: GXExpression?,
        val falseBranch: GXExpression?,
    ) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            val result = condition?.value(rawJson)
            return if (isCondition(result)) {
                trueBranch?.value(rawJson)
            } else {
                falseBranch?.value(rawJson)
            }
        }

        override fun toString(): String {
            return "GTernaryValue1(condition=$condition, trueBranch=$trueBranch, falseBranch=$falseBranch)"
        }

        companion object {

            private fun isExp(expression: String) =
                expression.contains("\${") &&
                        expression.startsWith("@{") &&
                        expression.endsWith("}") &&
                        expression.contains(" ? ") &&
                        expression.contains(" : ") &&
                        !expression.contains(" ?: ")

            private fun isExp2(expression: String) =
                expression.startsWith("@{") &&
                        expression.endsWith("}") &&
                        expression.contains(" ? ") &&
                        expression.contains(" : ") &&
                        !expression.contains(" ?: ")

            private fun getExpressionValue(expression: String): String {
                val startIndex = expression.indexOf("{")
                val lastIndexOf = expression.lastIndexOf("}")
                return expression.substring(startIndex + 1, lastIndexOf)
            }

            /**
             * a ? b : c => a
             */
            private fun conditionValue(expression: String): String {
                val split = expression.split(" ? ")
                if (split.isNotEmpty()) {
                    return split[0]
                }
                return ""
            }

            /**
             * a ? b : c => b
             */
            private fun trueValue(expression: String): String {
                val q = expression.split(" ? ")
                if (q.size == 2) {
                    val s = q[1].split(" : ")
                    if (s.size == 2) {
                        return s[0]
                    }
                    if (s.size > 2) {
                        val s1 = q[1].split(" : ")
                        if (s1.size == 2) {
                            return s1[0]
                        }
                    }
                }
                return ""
            }

            /**
             * a ? b : c => c
             */
            private fun falseValue(expression: String): String {
                val q = expression.split(" ? ")
                if (q.size >= 2) {
                    val s = q[1].split(" : ")
                    if (s.size == 2) {
                        return s[1]
                    }
                    if (s.size > 2) {
                        val s1 = q[1].split(" : ")
                        if (s1.size == 2) {
                            return s1[1]
                        }
                    }
                }
                return ""
            }

            fun create(expression: String): GXTernaryValue1 {
                val value = getExpressionValue(expression)
                val condition = GXExpression.create(conditionValue(value))
                val trueBranch = GXExpression.create(trueValue(value))
                val falseBranch = GXExpression.create(falseValue(value))
                return GXTernaryValue1(condition, trueBranch, falseBranch)
            }

            fun isExpression(expression: String): Boolean {
                return isExp(expression) || isExp2(expression)
            }

        }
    }

    /**
     * 三元表达式: @{ ${data} ?: b }
     */
    data class GXTernaryValue2(
        val conditionAndTrueBranch: GXExpression?,
        val falseBranch: GXExpression?,
    ) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            val condition = conditionAndTrueBranch?.value(rawJson)
            return if (isCondition(condition)) {
                condition
            } else {
                falseBranch?.value(rawJson)
            }
        }

        override fun toString(): String {
            return "GTernaryValue2(conditionAndTrueBranch=$conditionAndTrueBranch, falseBranch=$falseBranch)"
        }

        companion object {

            /**
             * @{ a ?: c } => a ?: c
             */
            private fun getExpressionValue(expression: String): String {
                val startIndex = expression.indexOf("{")
                val lastIndexOf = expression.lastIndexOf("}")
                return expression.substring(startIndex + 1, lastIndexOf)
            }

            /**
             * a ?: c => a
             */
            private fun trueValue(expression: String): String {
                val r = expression.split(" ?: ")
                if (r.size == 2) {
                    return r[0]
                }
                return ""
            }

            /**
             * a ?: c => c
             */
            private fun falseValue(expression: String): String {
                val r = expression.split(" ?: ")
                if (r.size == 2) {
                    return r[1]
                }
                return ""
            }

            fun create(expression: String): GXTernaryValue2 {
                val value = getExpressionValue(expression)
                val conditionAndTrueBranch = GXExpression.create(trueValue(value))
                val falseBranch = GXExpression.create(falseValue(value))
                return GXTernaryValue2(conditionAndTrueBranch, falseBranch)
            }

            private fun isExp(expression: String) =
                expression.contains("\${") &&
                        expression.startsWith("@{") &&
                        expression.endsWith("}") &&
                        expression.contains(" ?: ")

            private fun isExp2(expression: String) =
                expression.startsWith("@{") &&
                        expression.endsWith("}") &&
                        expression.contains(" ?: ")

            fun isExpression(expression: String): Boolean {
                return isExp(expression) || isExp2(expression)
            }
        }
    }

    /**
     * 嵌套三元表达式
     */
    data class GXTernaryValue3(
        val value: GXExpression?,
        val trueBranch: GXExpression?,
        val falseBranch: GXExpression?,

        ) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {

            if (falseBranch == null || trueBranch == null) {
                return value?.value(rawJson)
            }

            val condition = value?.value(rawJson)
            if (isCondition(condition)) {
                return trueBranch.value(rawJson)
            }

            return falseBranch.value(rawJson)
        }

        override fun toString(): String {
            return "GTernaryValue3(value=$value, trueBranch=$trueBranch, falseBranch=$falseBranch)"
        }

        companion object {

            @Suppress("RegExpRedundantEscape")
            private val regex = Regex("@\\{(.*)\\}")

            private fun splitBranch(expressionSpace: String): Pair<String?, String?>? {
                val expression = expressionSpace.trim { it <= ' ' }
                val start = expression.indexOf("@{")
                if (start == -1) {
                    val split = expression.split(" : ").toTypedArray()
                    return Pair(split[0], split[1])
                }
                val firstComma = expression.indexOf(" : ")
                return if (firstComma < start) {
                    Pair(expression.substring(0, firstComma), expression.substring(firstComma + 2))
                } else {
                    val subExpression = expression.substring(start)
                    var atCount = 0
                    var targetIndex = -1
                    var i = 0
                    while (i < subExpression.length) {
                        if ('@' == subExpression[i] || '$' == subExpression[i]) {
                            atCount++
                            i++
                        }
                        if ('}' == subExpression[i]) {
                            atCount--
                        }
                        if (atCount == 0) {
                            targetIndex = i
                            break
                        }
                        i++
                    }
                    if (targetIndex != -1) {
                        val commaIndex = subExpression.indexOf(" : ", targetIndex + 1)
                        if (commaIndex != -1) {
                            return Pair(subExpression.substring(0, targetIndex + 1), subExpression.substring(commaIndex + 2, subExpression.length))
                        }
                    }
                    null
                }
            }

            fun create(expression: String): GXExpression? {
                val ms = regex.findAll(expression)
                if (ms.count() > 0 && ms.first().groupValues.count() > 1) {
                    val target = ms.first().groupValues[1]
                    val askPos: Int = target.indexOf(" ? ")
                    return if (askPos != -1) {
                        val conditionStr = target.substring(0, askPos)
                        val conditionExp = GXExpression.create(conditionStr)
                        val quotePos: Int = target.indexOf(" : ")
                        if (quotePos != -1) {
                            val branches = target.substring(askPos + 3)
                            val splitBranch = splitBranch(branches)
                            val trueBranchExp: GXExpression? = GXExpression.create(splitBranch?.first ?: "")
                            val falseBranchExp: GXExpression? = GXExpression.create(splitBranch?.second ?: "")
                            GXTernaryValue3(conditionExp, trueBranchExp, falseBranchExp)
                        } else {
                            conditionExp?.let { create(it) }
                        }
                    } else {
                        GXExpression.create(target)
                    }
                }
                return GXExpression.create(expression)
            }

            private fun isExp(expression: String) =
                expression.contains("\${") &&
                        expression.startsWith("@{") &&
                        expression.endsWith("}") &&
                        expression.contains(" ? ") &&
                        expression.contains(" : ") &&
                        !expression.contains(" ?: ")

            private fun isExp2(expression: String) =
                expression.startsWith("@{") &&
                        expression.endsWith("}") &&
                        expression.contains(" ? ") &&
                        expression.contains(" : ") &&
                        !expression.contains(" ?: ")


            private fun isExp3(expression: String): Boolean {
                val split = expression.split("@{")
                if (split.size > 2) {
                    return true
                }
                return false
            }

            fun isExpression(expression: String): Boolean {
                return (isExp(expression) || isExp2(expression)) && isExp3(expression)
            }
        }
    }

    /**
     * 文本表达式：
     *
     * text + ${data}
     *
     * ${data} + text
     *
     * @{ xxx } + text
     * text + @{}
     */
    data class GXTextValue(val values: MutableList<GXExpression> = mutableListOf()) : GXExpression() {

        override fun value(rawJson: JSON?): Any? {
            val result = StringBuilder()
            this.values.forEach {
                val desireData = it.value(rawJson)
                if (desireData != null) {
                    result.append(desireData)
                }
            }
            return result.toString()
        }

        override fun toString(): String {
            return "GTextValue(values=$values)"
        }


        companion object {
            fun create(expression: String): GXTextValue {
                val textValue = GXTextValue()
                val contents = expression.split(" + ")
                contents.forEach { content ->
                    GXExpression.create(content)?.let { textValue.values.add(it) }
                }
                return textValue
            }

            fun isExpression(expression: String): Boolean {
                return !expression.startsWith("@") && expression.contains(" + ")
            }
        }
    }
}