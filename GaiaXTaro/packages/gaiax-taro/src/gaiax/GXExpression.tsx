import computeValuePath from './GXValuePath';

class GXExpression {

    desireData(expression: any, rawJson: any): any {
        if (typeof expression === 'boolean') {
            return expression;
        }
        else if (typeof expression === 'number') {
            return expression;
        }
        else if (expression == null) {
            return null;
        }
        else if (typeof expression === 'object') {
            const targetObj = expression;
            const result = {};
            Object.keys(targetObj).forEach(key => {
                result[key] = instance.desireData(targetObj[key], rawJson);
            });
            return result;
        }
        else if (typeof expression === 'string') {
            const exp = expression.trim()
            // GXSelf
            if (this.isSelf(exp)) {
                return rawJson;
            }
            // GXNull
            else if (this.isNull(exp)) {
                return null;
            }
            // GXBool
            else if (this.isBool(exp)) {
                if (exp == 'true') {
                    return true;
                } else {
                    return false;
                }
            }
            // GXNumber
            else if (this.isNumber(exp)) {
                return parseFloat(exp);
            }
            // GXString
            else if (this.isString(exp)) {
                return exp.substring(1, exp.length - 1);
            }
            // GXEval
            else if (this.isEval(exp)) {
                const newExp = exp.substring("eval(".length, exp.length - 1).trim();
                if (newExp.includes("==")) {
                    const result = newExp.split("==");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left == right;
                } else if (newExp.includes(">=")) {
                    const result = newExp.split(">=")
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left >= right;
                } else if (newExp.includes(">")) {
                    const result = newExp.split(">");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left > right;
                } else if (newExp.includes("<=")) {
                    const result = newExp.split("<=");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left <= right;
                } else if (newExp.includes("<")) {
                    const result = newExp.split("<");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left < right;
                } else if (newExp.includes("!=")) {
                    const result = newExp.split("!=");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left != right;
                } else if (newExp.includes("||")) {
                    const result = newExp.split("||");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left || right;
                } else if (newExp.includes("&&")) {
                    const result = newExp.split("&&");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left && right;
                } else if (newExp.includes("%")) {
                    const result = newExp.split("%");
                    const left = instance.desireData(result[0], rawJson)
                    const right = instance.desireData(result[1], rawJson)
                    return left % right;
                }
                return false;
            }
            // GXEnv
            else if (this.isEnv(exp)) {
                return NaN;
            }
            // GXScroll
            else if (this.isScroll(exp)) {
                return NaN;
            }
            // GXSize
            else if (this.isSize(exp)) {
                return NaN;
            }
            // GXTextValue
            else if (this.isTextValue(exp)) {
                let result = '';
                const splits = exp.split(" + ");
                splits.forEach(element => {
                    let value = instance.desireData(element, rawJson)
                    if (value != null) {
                        result += value;
                    }
                });
                return result;
            }
            // GXValue
            else if (this.isValue(exp)) {
                const result = exp.substring(2, exp.length - 1)
                if (typeof rawJson == 'object') {
                    return computeValuePath(result, rawJson);
                }
                return '';
            }
            // GXTernaryValue3
            else if (this.isTernaryValue3(exp)) {
                // @{ a ? b : @{ c ? d : e } }
                // @{ a ?: @{ c ?: e } }

                const newExp = exp.substring(2, exp.length - 1).trim();

                let ternaryIndexOf = newExp.indexOf('@{')
                let ternary11IndexOf = newExp.indexOf(' ? ')
                let ternary12IndexOf = newExp.indexOf(' : ')
                let ternary2IndexOf = newExp.indexOf(' ?: ')

                if (ternary11IndexOf < ternaryIndexOf && ternary12IndexOf < ternaryIndexOf) {
                    const condition = newExp.substring(0, ternary11IndexOf).trim()
                    const trueResult = newExp.substring(ternary11IndexOf + 3, ternary12IndexOf).trim()
                    const falseResult = newExp.substring(ternary12IndexOf + 3, newExp.length).trim()

                    const cond = instance.desireData(condition, rawJson)
                    if (this.isCondition(cond)) {
                        return instance.desireData(trueResult, rawJson)
                    } else {
                        return instance.desireData(falseResult, rawJson)
                    }

                } else if (ternary2IndexOf < ternaryIndexOf) {
                    const conditionAndTrue = newExp.substring(0, ternary2IndexOf).trim()
                    const falseResult = newExp.substring(ternary2IndexOf + 4, newExp.length).trim()

                    const cond = instance.desireData(conditionAndTrue, rawJson)
                    if (this.isCondition(cond)) {
                        return cond
                    } else {
                        return instance.desireData(falseResult, rawJson)
                    }
                }
                return NaN;
            }
            // GXTernaryValue1
            else if (this.isTernaryValue1(exp)) {

                const newExp = exp.substring(2, exp.length - 1);
                const splits1 = newExp.split("?");
                const condition = splits1[0].trim()
                const splits2 = splits1[1].split(":");
                const trueResult = splits2[0].trim()
                const falseResult = splits2[1].trim()

                const cond = instance.desireData(condition, rawJson)
                if (this.isCondition(cond)) {
                    return instance.desireData(trueResult, rawJson)
                } else {
                    return instance.desireData(falseResult, rawJson)
                }
            }
            // GXTernaryValue2
            else if (this.isTernaryValue2(exp)) {

                const newExp = exp.substring(2, exp.length - 1);
                const split = newExp.split("?:");
                const conditionAndTrue = split[0].trim()
                const falseResult = split[1].trim()

                const cond = instance.desireData(conditionAndTrue, rawJson)
                if (this.isCondition(cond)) {
                    return cond
                } else {
                    return instance.desireData(falseResult, rawJson)
                }
            }
            // GXText
            else if (this.isText(exp)) {
                return exp;
            }
        }
        return null;
    }

    isCondition(condition: any): boolean {
        if (typeof condition == 'boolean' && condition == true) {
            return true
        } else if (typeof condition == 'number' && condition != 0.0) {
            return true
        } else if (condition == "0" || condition == "false" || condition == false || condition == 0 || condition == 0.0) {
            return false
        } else if (condition == "1" || condition == "true" || (typeof condition == 'string' && condition.length != 0)) {
            return true
        } else if (typeof condition == 'string' && condition.length == 0) {
            return false
        } else {
            return condition != null
        }
    }

    private isText(expression: string) {
        return expression.length != 0;
    }

    // @{ a ? b : @{ c ?: d} }
    private isTernaryValue3(expression: string) {
        let result = expression.match(this.ternaryValueRegex)
        if (result != null && result.length >= 2) {
            return true;
        }
        return false;
    }

    // @{ ${data} ? b : c }
    private isTernaryValue1(expression: string) {
        let result = expression.match(this.ternaryValueRegex)
        if (result != null && result.length == 1) {
            return /\@\{.+\?.+\:.+\}/g.test(expression);
        }
        return false;
    }

    // @{ ${data} ?: b }
    private isTernaryValue2(expression: string) {
        let result = expression.match(this.ternaryValueRegex)
        if (result != null && result.length == 1) {
            return /\@\{.+\?\:.+\}/g.test(expression)
        }
        return false;
    }

    private valueRegex = /\$\{[a-zA-Z.\[\]0-9]+\}/g;
    private ternaryValueRegex = /\@\{/g;

    private isValue(expression: string) {
        let result = expression.match(this.valueRegex)
        if (result != null && result.length == 1) {
            return true;
        }
        return false;
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
    private isTextValue(expression: string) {
        return !expression.startsWith("@") && expression.indexOf(" + ") != -1;
    }

    private isSize(expression: string) {
        return expression.startsWith("size(") && expression.endsWith(")");
    }

    private isScroll(expression: string) {
        return expression.startsWith("scroll(") && expression.endsWith(")");
    }

    private isEnv(expression: string) {
        return expression.startsWith("env(") && expression.endsWith(")");
    }

    private isEval(expression: string) {
        return expression.startsWith("eval(") && expression.endsWith(")");
    }

    private isString(expression: string) {
        return expression.startsWith("'") && expression.endsWith("'");
    }

    private isNumber(expression: string) {
        // eslint-disable-next-line no-restricted-globals
        return !isNaN(parseFloat(expression)) // ...and ensure strings of whitespace fail
    }

    private isBool(expression: string) {
        return expression == "true" || expression == "false";
    }

    private isNull(expression: string) {
        return expression == "null";
    }

    private isSelf(expression: string) {
        return expression == "$$";
    }
}

const instance = new GXExpression();

export default instance