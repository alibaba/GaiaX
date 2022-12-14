package com.alibaba.gaiax.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXIExpression
import java.util.regex.Pattern

/**
 * 模板中的表达式
 */
sealed class GXTestYKExpression : GXIExpression {

    var expression: Any? = null

    override fun expression(): Any {
        return expression ?: ""
    }

    override fun value(templateData: JSON?): Any? {
        return this.desireData(templateData)
    }

    /**
     * 根据表达式，获取期望的数据
     */
    abstract fun desireData(rawJson: JSON? = null): Any?

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

        fun create(expression: Any): GXTestYKExpression {
            return when (expression) {
                is JSON -> when {
                    GJsonObj.isExpression(expression) -> GJsonObj.create(expression as JSONObject)
                    GJsonArrayObj.isExpression(expression) -> GJsonArrayObj.create(expression as JSONArray)
                    else -> Undefined
                }
                is String -> {
                    val exp = expression.trim()
                    when {
                        Self.isExpression(exp) -> Self
                        GNull.isExpression(exp) -> GNull.create()
                        GBool.isExpression(exp) -> GBool.create(exp)
                        GInt.isExpression(exp) -> GInt.create(exp)
                        GFloat.isExpression(exp) -> GFloat.create(exp)
                        GString.isExpression(exp) -> GString.create(exp)
                        GEval.isExpression(exp) -> GEval.create(exp)
                        GEnv.isExpression(exp) -> GEnv.create(exp)
                        GScroll.isExpression(exp) -> GScroll.create(exp)
                        GSize.isExpression(exp) -> GSize.create(exp)
                        GTextValue.isExpression(exp) -> GTextValue.create(exp)
                        GValue.isExpression(exp) -> GValue.create(exp)
                        GTernaryValue3.isExpression(exp) -> GTernaryValue3.create(exp)
                        GTernaryValue1.isExpression(exp) -> GTernaryValue1.create(exp)
                        GTernaryValue2.isExpression(exp) -> GTernaryValue2.create(exp)
                        GText.isExpression(exp) -> GText.create(exp)
                        expression == "\n" -> GText.create("\n")
                        else -> Undefined
                    }
                }
                is Boolean -> GBool(expression)
                is Int -> GInt(expression)
                is Float -> GFloat(expression)
                else -> Undefined
            }
        }
    }

    /**
     * 未定义表达式
     */
    object Undefined : GXTestYKExpression() {
        override fun desireData(rawJson: JSON?): Any? {
            return null
        }

        override fun toString(): String {
            return "Undefined()"
        }
    }

    /**
     * 自我表达式：$$
     */
    object Self : GXTestYKExpression() {

        override fun toString(): String {
            return "Self()"
        }

        override fun desireData(rawJson: JSON?): Any? {
            return rawJson
        }

        fun isExpression(expression: String): Boolean {
            return "$$" == expression
        }
    }

    /**
     * 用于逻辑运算 == != >= <= > < && || %
     */
    data class GEval(
        val operate: String,
        val leftValue: GXTestYKExpression,
        val rightValue: GXTestYKExpression
    ) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            val left = leftValue.desireData(rawJson)
            val right = rightValue.desireData(rawJson)
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

            fun create(value: String): GEval {
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
                return GEval("", Undefined, Undefined)
            }

            private fun createEval(operate: String, realValue: String): GEval? {
                realValue.split(operate).let {
                    if (it.size == 2) {
                        val leftValue = it[0]
                        val rightValue = it[1]
                        return GEval(
                            operate,
                            GXTestYKExpression.create(leftValue),
                            GXTestYKExpression.create(rightValue)
                        )
                    }
                }
                return null
            }
        }
    }

    /**
     * 用于取环境变量
     */
    data class GEnv(val value: String) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return null
        }

        override fun toString(): String {
            return "GEnv(value='$value')"
        }

        companion object {

            fun isExpression(value: String): Boolean {
                return value.startsWith("env(") && value.endsWith(")")
            }

            fun create(value: String): GEnv {
                val realValue = value.substring("env(".length, value.length - 1)
                return GEnv(realValue)
            }
        }
    }

    /**
     * 用于取容器变量
     */
    data class GScroll(val value: String) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any {
            return -1
        }

        override fun toString(): String {
            return "GScroll(value='$value')"
        }

        companion object {

            fun isExpression(value: String): Boolean {
                return value.startsWith("scroll(") && value.endsWith(")")
            }

            fun create(value: String): GScroll {
                val realValue = value.substring("scroll(".length, value.length - 1)
                return GScroll(realValue)
            }
        }
    }

    /**
     * 用于计算字符串、JSONArray和JSONObject的size
     */
    data class GSize(val value: GXTestYKExpression) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return when (val result = value.desireData(rawJson)) {
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

            fun create(value: String): GSize {
                val realValue = value.substring("size(".length, value.length - 1)
                return GSize(GXTestYKExpression.create(realValue))
            }
        }
    }

    /**
     * JSON Obj
     */
    data class GJsonObj(val value: JSONObject) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            val result = JSONObject()
            if (value.isNotEmpty()) {
                value.forEach {
                    if (it.key != null && it.value != null) {
                        if (it.value is GXTestYKExpression) {
                            result[it.key] = (it.value as GXTestYKExpression).desireData(rawJson)
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
            fun create(value: JSONObject): GJsonObj {
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
                return GJsonObj(result)
            }

            fun isExpression(expression: Any): Boolean {
                return expression is JSONObject
            }
        }
    }

    /**
     * JSON Array
     */
    data class GJsonArrayObj(val value: JSONArray) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            val result = JSONArray()
            if (value.isNotEmpty()) {
                value.forEach {
                    if (it != null && it is GXTestYKExpression) {
                        result.add(it.desireData(rawJson))
                    }
                }
            }
            return result
        }

        override fun toString(): String {
            return "GJsonArrayObj(value=$value)"
        }

        companion object {
            fun create(value: JSONArray): GJsonArrayObj {
                val result = JSONArray()
                value.forEach {
                    if (it != null) {
                        result.add(create(it))
                    }
                }
                return GJsonArrayObj(result)
            }

            fun isExpression(expression: Any): Boolean {
                return expression is JSONArray
            }
        }
    }

    /**
     * 布尔值
     */
    data class GBool(val value: Boolean) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GBool(value=$value)"
        }

        companion object {
            fun create(value: String): GBool {
                return GBool(value.toBoolean())
            }

            fun isExpression(expression: String): Boolean {
                return expression == "true" || expression == "false"
            }
        }
    }

    /**
     * 字符串值
     */
    data class GString(val value: String) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GString(value='$value')"
        }

        companion object {
            fun create(value: String): GString {
                return GString(value.substring(1, value.length - 1))
            }

            fun isExpression(expression: String): Boolean {
                return expression.startsWith("'") && expression.endsWith("'")
            }
        }
    }

    /**
     * 数字值: xxx
     */
    data class GFloat(val value: Float) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GFloat(value=$value)"
        }

        companion object {
            fun create(value: String): GFloat {
                return GFloat(value.toFloat())
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
    data class GInt(val value: Int) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return value
        }

        override fun toString(): String {
            return "GInt(value=$value)"
        }

        companion object {
            fun create(value: String): GInt {
                return GInt(value.toInt())
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

    class GNull : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return null
        }

        override fun toString(): String {
            return "GNull()"
        }

        companion object {
            fun create(): GNull {
                return GNull()
            }

            fun isExpression(expression: String): Boolean {
                return expression == "null"
            }
        }
    }

    /**
     * 常量值: xxx
     */
    data class GText(val value: String) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
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
            fun create(value: String): GText {
                return GText(value)
            }

            fun isExpression(expression: String): Boolean {
                return expression.isNotEmpty()
            }
        }
    }

    /**
     * 取值表达式: ${data}
     */
    data class GValue(val value: String) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            return rawJson?.getAnyExt(value)
        }

        override fun expression(): Any {
            return value
        }

        fun setData(rawJson: JSON?, target: Any) {
            rawJson?.setValueExt(value, target)
        }

        override fun toString(): String {
            return "GValue(value='$value')"
        }

        companion object {

            fun create(expression: String): GValue {
                val matcher = valueRegex.matcher(expression)
                if (matcher.find()) {
                    return GValue(matcher.group(1))
                }
                return GValue("")
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
    data class GTernaryValue1(
        val condition: GXTestYKExpression,
        val trueBranch: GXTestYKExpression,
        val falseBranch: GXTestYKExpression
    ) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            val result = condition.desireData(rawJson)
            return if (isCondition(result)) {
                trueBranch.desireData(rawJson)
            } else {
                falseBranch.desireData(rawJson)
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

            fun create(expression: String): GTernaryValue1 {
                val value = getExpressionValue(expression)
                val condition = GXTestYKExpression.create(conditionValue(value))
                val trueBranch = GXTestYKExpression.create(trueValue(value))
                val falseBranch = GXTestYKExpression.create(falseValue(value))
                return GTernaryValue1(condition, trueBranch, falseBranch)
            }

            fun isExpression(expression: String): Boolean {
                return isExp(expression) || isExp2(expression)
            }

        }
    }

    /**
     * 三元表达式: @{ ${data} ?: b }
     */
    data class GTernaryValue2(
        val conditionAndTrueBranch: GXTestYKExpression,
        val falseBranch: GXTestYKExpression
    ) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            val condition = conditionAndTrueBranch.desireData(rawJson)
            return if (isCondition(condition)) {
                condition
            } else {
                falseBranch.desireData(rawJson)
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

            fun create(expression: String): GTernaryValue2 {
                val value = getExpressionValue(expression)
                val conditionAndTrueBranch = GXTestYKExpression.create(trueValue(value))
                val falseBranch = GXTestYKExpression.create(falseValue(value))
                return GTernaryValue2(conditionAndTrueBranch, falseBranch)
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
    data class GTernaryValue3(
        val value: GXTestYKExpression,
        val trueBranch: GXTestYKExpression?,
        val falseBranch: GXTestYKExpression?

    ) : GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {

            if (falseBranch == null || trueBranch == null) {
                return value.desireData(rawJson)
            }

            val condition = value.desireData(rawJson)
            if (isCondition(condition)) {
                return trueBranch.desireData(rawJson)
            }

            return falseBranch.desireData(rawJson)
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
                            return Pair(
                                subExpression.substring(0, targetIndex + 1),
                                subExpression.substring(commaIndex + 2, subExpression.length)
                            )
                        }
                    }
                    null
                }
            }

            fun create(expression: String): GXTestYKExpression {
                val ms = regex.findAll(expression)
                if (ms.count() > 0 && ms.first().groupValues.count() > 1) {
                    val target = ms.first().groupValues[1]
                    val askPos: Int = target.indexOf(" ? ")
                    return if (askPos != -1) {
                        val conditionStr = target.substring(0, askPos)
                        val conditionExp = GXTestYKExpression.create(conditionStr)
                        val quotePos: Int = target.indexOf(" : ")
                        if (quotePos != -1) {
                            val branches = target.substring(askPos + 3)
                            val splitBranch = splitBranch(branches)
                            val trueBranchExp: GXTestYKExpression =
                                GXTestYKExpression.create(splitBranch?.first ?: "")
                            val falseBranchExp: GXTestYKExpression =
                                GXTestYKExpression.create(splitBranch?.second ?: "")
                            GTernaryValue3(conditionExp, trueBranchExp, falseBranchExp)
                        } else {
                            create(conditionExp)
                        }
                    } else {
                        GXTestYKExpression.create(target)
                    }
                }
                return GXTestYKExpression.create(expression)
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
    data class GTextValue(val values: MutableList<GXTestYKExpression> = mutableListOf()) :
        GXTestYKExpression() {

        override fun desireData(rawJson: JSON?): Any? {
            val result = StringBuilder()
            this.values.forEach {
                val desireData = it.desireData(rawJson)
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
            fun create(expression: String): GTextValue {
                val textValue = GTextValue()
                val contents = expression.split(" + ")
                contents.forEach { content ->
                    textValue.values.add(GXTestYKExpression.create(content))
                }
                return textValue
            }

            fun isExpression(expression: String): Boolean {
                return !expression.startsWith("@") && expression.contains(" + ")
            }
        }
    }
}