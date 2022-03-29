#include "GXAnalyze.h"
#include <time.h>

GXAnalyze::GXAnalyze() {
    init();
}

GXAnalyze::~GXAnalyze() {
}

//获取两个数值计算的结果
GXATSNode GXAnalyze::doubleCalculate(GXATSNode left, GXATSNode right, string op) {
    GXATSNode result = GXATSNode(left.name, left.syn, left.token);
    string name;
    if (((op == "?") && (left.token != "map" && left.token != "array")) ||
        (op == ":") || (op == "?:")) {
        //可以返回map和array
    } else if (left.token == "map" || left.token == "array") {
        result.name =
                "\'" + op + "\'" + ": illegal,left operand has type of \'" + left.token + "\'";
        result.token = "error";
        return result;
    } else if (op != "?:" && (right.token == "map" || right.token == "array")) {
        result.name =
                "\'" + op + "\'" + ": illegal,right operand has type of \'" + left.token + "\'";
        result.token = "error";
        return result;
    }
    //返回值都为bool
    if (op == ">") {
        result.token = "bool";
        if (left.token == "num" && right.token == "num") {
            if (stof(left.name) > stof(right.name)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == ">=") {
        result.token = "bool";
        if (left.token == "num" && right.token == "num") {
            if (stof(left.name) >= stof(right.name)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == "<") {
        result.token = "bool";
        if (left.token == "num" && right.token == "num") {
            if (stof(left.name) < stof(right.name)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == "<=") {
        result.token = "bool";
        if (left.token == "num" && right.token == "num") {
            if (stof(left.name) <= stof(right.name)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == "&&") {
        result.token = "bool";
        if (left.token == "bool" && right.token == "bool") {
            if (left.name == "true" && right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "bool" && right.token == "num") {
            if (left.name == "true" && (stof(right.name) != 0.0F)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "bool" && right.token == "string") {
            if (left.name == "true" && right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "num" && right.token == "num") {
            if ((stof(left.name) != 0.0F) && (stof(right.name) != 0.0F)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "num" && right.token == "bool") {
            if (((stof(left.name) != 0.0F) && right.name == "true") ||
                (left.token == "num" && right.token == "string")) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "string" && right.token == "string") ||
                   (left.token == "string" && right.token == "bool")) {
            if (left.name == "true" && right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "string" && right.token == "num") {
            if (left.name == "true" && (stof(right.name) != 0.0F)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == "||") {
        result.token = "bool";
        if (left.token == "bool" && right.token == "bool") {
            if (left.name == "true" || right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "bool" && right.token == "num") {
            if (left.name == "true" || (stof(right.name) != 0.0F)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "bool" && right.token == "string") {
            if (left.name == "true" || right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "num" && right.token == "num") {
            if ((stof(left.name) != 0.0F) || (stof(right.name) != 0.0F)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "num" && right.token == "bool") {
            if (((stof(left.name) != 0.0F) || right.name == "true") ||
                (left.token == "num" || right.token == "string")) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "string" && right.token == "string") ||
                   (left.token == "string" && right.token == "bool")) {
            if (left.name == "true" || right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "string" && right.token == "num") {
            if (left.name == "true" || (stof(right.name) != 0.0F)) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "null") {
            if (right.token == "num") {
                if (stof(right.name) != 0.0F) {
                    result.name = "true";
                } else {
                    result.name = "false";
                }
            } else if (right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (right.token == "null") {
            if (left.token == "num") {
                if (stof(left.name) != 0.0F) {
                    result.name = "true";
                } else {
                    result.name = "false";
                }
            } else if (left.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else {

        }
    }
        //返回值都为bool
    else if (op == "!=") {
        result.token = "bool";
        if (left.token == "bool" && right.token == "num") {
            if ((left.name == "true" && (stof(right.name) == 0.0F)) ||
                (left.name == "false" && (stof(right.name) != 0.0F))) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "num" && right.token == "bool") {
            if (((stof(left.name) == 0.0F) && right.name == "true") ||
                ((stof(left.name) != 0.0F) && right.name == "false")) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "num" && right.token == "num")) {
            float lef = stof(left.name);
            float rig = stof(right.name);
            if (lef != rig) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "string" && right.token == "bool") ||
                   (left.token == "bool" && right.token == "string")) {
            if (left.name == right.name) {
                result.name = "false";
            }
        } else if ((left.name != right.name) || (left.token != right.token)) {
            result.name = "true";
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == "==") {
        result.token = "bool";
        if (left.token == "bool" && right.token == "num") {
            if ((left.name == "true" && (stof(right.name) != 0.0F)) ||
                (left.name == "false" && (stof(right.name) == 0.0F))) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token == "num" && right.token == "bool") {
            if (((stof(left.name) != 0.0F) && right.name == "true") ||
                ((stof(left.name) == 0.0F) && right.name == "false")) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "num" && right.token == "num") &&
                   (stof(left.name) == stof(right.name))) {
            result.name = "true";
        } else if ((left.name == right.name)) {
            if (left.token == right.token) {
                result.name = "true";
            } else if ((left.token == "string" && right.token == "bool") ||
                       (left.token == "bool" && right.token == "string")) {
                result.name = "true";
            }
        } else {
            result.name = "false";
        }
    } else if (op == "?") {
        if (left.name == "true") {
            result.name = right.name;
            result.token = right.token;
        } else if (left.token == "num" && stof(left.name) != 0.0F) {
            result.name = right.name;
            result.token = right.token;
        } else {
            result.token = "right";
            result.name = "right";
        }
    } else if (op == "?:") {
        if (left.name == "true" || (left.name != "false" && left.token != "null")) {
            result.name = left.name;
            result.token = left.token;
        } else {
            result.name = right.name;
            result.token = right.token;
        }
    } else if (op == ":") {
        if (left.token == "right" && left.name == "right") {
            result.token = right.token;
            result.name = right.name;
        } else {
            result.token = left.token;
            result.name = left.name;
        }
    } else if (op == "+") {
        if (left.token == "num" && right.token == "num") {
            float temp = stof(left.name) + stof(right.name);
            result.name = to_string(temp);
            result.token = "num";
        } else if (left.token == "string" && right.token == "string") {
            result.token = "string";
            result.name = left.name + right.name;
        } else {
            result.token = "error";
            if (left.token == "num" || left.token == "string") {
                result.name =
                        "\'" + right.name + "\'" + ": expected " + left.token + " value,not : " +
                        right.token;
            } else if (right.token == "num" || right.token == "string") {
                result.name =
                        "\'" + left.name + "\'" + ": expected " + right.token + " value,not : " +
                        left.token;
            } else {
                result.name =
                        "\'" + left.name + "\'" + ": expected num value,not : " +
                        left.token;
            }
        }
    } else if (op == "-") {
        if (left.token == "num" && right.token == "num") {
            float temp = stof(left.name) - stof(right.name);
            result.name = to_string(temp);
            result.token = "num";
        } else {
            result.token = "error";
            if (left.token == "num") {
                result.name =
                        "\'" + right.name + "\'" + ": expected " + left.token + " value,not : " +
                        right.token;
            } else if (right.token == "num") {
                result.name =
                        "\'" + left.name + "\'" + ": expected " + right.token + " value,not : " +
                        left.token;
            } else {
                result.name =
                        "\'" + left.name + "\'" + ": expected num value,not : " +
                        left.token;
            }
        }
    } else if (op == "*") {
        if (left.token == "num" && right.token == "num") {
            float temp = stof(left.name) * stof(right.name);
            result.name = to_string(temp);
            result.token = "num";
        } else {
            result.token = "error";
            if (left.token == "num") {
                result.name =
                        "\'" + right.name + "\'" + ": expected " + left.token + " value,not : " +
                        right.token;
            } else if (right.token == "num") {
                result.name =
                        "\'" + left.name + "\'" + ": expected " + right.token + " value,not : " +
                        left.token;
            } else {
                result.name =
                        "\'" + left.name + "\'" + ": expected num value,not : " +
                        left.token;
            }
        }
    } else if (op == "/") {
        if (left.token == "num" && right.token == "num") {
            if (stof(right.name) == 0) {
                result.token = "error";
                result.name = "divide or mod by zero";
            } else {
                float temp = stof(left.name) / stof(right.name);
                result.name = to_string(temp);
                result.token = "num";
            }
        } else {
            result.token = "error";
            if (left.token == "num") {
                result.name =
                        "\'" + right.name + "\'" + ": expected " + left.token + " value,not : " +
                        right.token;
            } else if (right.token == "num") {
                result.name =
                        "\'" + left.name + "\'" + ": expected " + right.token + " value,not : " +
                        left.token;
            } else {
                result.name =
                        "\'" + left.name + "\'" + ": expected num value,not : " +
                        left.token;
            }
        }
    } else if (op == "%") {
        if (left.token == "num" && right.token == "num") {
            if (stof(right.name) == 0) {
                result.token = "error";
                result.name = "divide or mod by zero";
            } else {
                float temp = stoi(left.name) % stoi(right.name);
                result.name = to_string(temp);
                result.token = "num";
            }
        } else {
            result.token = "error";
            if (left.token == "num") {
                result.name =
                        "\'" + right.name + "\'" + ": expected " + left.token + " value,not : " +
                        right.token;
            } else if (right.token == "num") {
                result.name =
                        "\'" + left.name + "\'" + ": expected " + right.token + " value,not : " +
                        left.token;
            } else {
                result.name =
                        "\'" + left.name + "\'" + ": expected num value,not : " +
                        left.token;
            }
        }
    }
    return result;
}

//获取单个数值计算的结果
GXATSNode GXAnalyze::singleCalculate(GXATSNode left, string op) {
    GXATSNode result = GXATSNode(left.name, left.syn, left.token);
    if (left.token == "map" || left.token == "array") {
        return result;
    }
    if (op == "-") {
        if (left.token == "num") {
            float temp = -stof(left.name);
            result.name = to_string(temp);
            result.token = "num";
        } else {
            result.token = "error";
            result.name =
                    "\'" + left.name + "\'" + ": expected num value,not : " +
                    left.token;
        }
    } else if (op == "+") {
        if (left.token == "num") {
            result.token = "num";
            result.name = left.name;
        } else {
            result.token = "error";
            result.name =
                    "\'" + left.name + "\'" + ": expected num value,not : " +
                    left.token;
        }
    } else if (op == "!") {
        if (left.token == "bool") {
            if (left.name == "false") {
                result.name = "true";
            } else if (left.name == "true") {
                result.name = "false";
            } else {
                result.token = "error";
                result.name =
                        "\'" + left.name + "\'" + ": unknown identifier ";
            }
        } else {
            result.token = "error";
            result.name =
                    "\'" + left.name + "\'" + ": expected bool value,not : " +
                    left.token;
        }
    }
    return result;
}

string GXAnalyze::grammarScanner(vector<GXATSNode> array) {
    string temp;
    for (vector<GXATSNode>::iterator i = array.begin(); i != array.end(); i++) {
        temp = temp + change_Word(get_Word_By_Code((*i).syn));
    }
    return temp;
}

static GXValue pointer;

long GXAnalyze::getValue(string expression, void *source) {
    char *input;
    int inputLength = expression.length();
    input = new char[inputLength];
    vector<GXATSNode> array;
    array.reserve(128);
    string result = "#";
    strcpy(input, expression.c_str());
    int p = 0;
    int synCode;
    while (p < strlen(input)) {
        if (input[p] == ' ') {
            p++;
        } else {
            GXATSNode token = scanner(synCode, p, input, this);
            array.push_back(token);
        }
    }
    //释放s的内存空间
    delete[]input;
    result = result + grammarScanner(array);
    result = result + "#";
    long Res = check(result, array, this, source);
    array.clear();
    return Res;
}

long GXAnalyze::check(string s, vector<GXATSNode> array, void *p_analyze, void *source) {
    GXAnalyze *analyze = (GXAnalyze *) p_analyze;
    string temp = "\0"; //需要分析的语句
    string sentence = s + temp;
    vector<string> statusStack; //状态栈
    statusStack.reserve(128);
    int statusSize = 0;
    vector<char> symbolStack;   //符号栈
    symbolStack.reserve(128);
    int symbolSize = 0;
    vector<GXATSNode> valueStack;
    symbolStack.reserve(64);
    int valueSize = 0;
    vector<GXValue> paramsStack;
    paramsStack.reserve(64);
    int paramsSize = 0;
    int valueStep = 0; //数值数
    bool isFunction = false;
    string valueType;
    symbolStack.push_back('#');
    ++symbolSize;
    sentence = sentence.substr(1);
    statusStack.emplace_back("0");
    ++statusSize;
    string new_status;                             //下一入栈的新状态
    while (true) {
        string cur_status;//当前状态
        char cur_symbol;//当前“展望”字符
        cur_status = statusStack[statusSize - 1];
        cur_symbol = sentence[0];
        string m = cur_status + cur_symbol;
        //当前new_status,下一入栈的新状态
        new_status = get_Table_By_String(m);
        if (new_status == "acc") {
            if (valueStack[0].token == "string") {
                const char *tem = valueStack[0].name.c_str();
                pointer = GX_NewGXString(tem);
            } else if (valueStack[0].token == "bool") {
                if (valueStack[0].name == "true") {
                    pointer = GX_NewBool(1);
                } else {
                    pointer = GX_NewBool(0);
                }
            } else if (valueStack[0].token == "num") {
                pointer = GX_NewFloat64(atof(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "map") {
                pointer = GX_NewMap((void *) atol(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "array") {
                pointer = GX_NewArray((void *) atol(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "null") {
                pointer = GX_NewNull(1);
            }
            return (long) (&pointer);
        } else if (new_status[0] ==
                   's') {
            statusStack.push_back(new_status.substr(1));
            ++statusSize;
            // 1
            symbolStack.push_back(cur_symbol); //读入一个字符
            ++symbolSize;
            string temp = get_S_By_C(cur_symbol);
            if ((is_terminal_char(temp) &&
                 (temp == "true" || temp == "false" || temp == "null" || temp == "value" ||
                  temp == "num" || temp == "string" || temp == "data" || temp == "id")) ||
                (temp == "map" || temp == "array")) {
                // push value
                if (is_terminal_char(temp) && temp == "id") {
                    //接下来读入参数，(和)变为运算符
                    isFunction = true;
                }
                GXATSNode t1;
                if (temp == "value" || temp == "data") {
                    long res = analyze->getSourceValue(array[valueStep].name, source);
                    GXValue *gxv = (GXValue *) res;
                    if (gxv->tag == GX_TAG_FLOAT) {
                        t1.name = to_string(gxv->u.float64);
                        t1.token = "num";
                    } else if (gxv->tag == GX_TAG_STRING) {
                        t1.name = gxv->u.str;
                        t1.token = "string";
                    } else if (gxv->tag == GX_TAG_BOOL) {
                        if (gxv->u.int32 == 1) {
                            t1.name = "true";
                        } else {
                            t1.name = "false";
                        }
                        t1.token = "bool";
                    } else if (gxv->tag == GX_TAG_ARRAY) {
                        t1.name = to_string((long) (gxv->u.ptr));
                        t1.token = "array";
                    } else if (gxv->tag == GX_TAG_MAP) {
                        t1.name = to_string((long) (gxv->u.ptr));
                        t1.token = "map";
                    } else if (gxv->tag == GX_TAG_NULL) {
                        t1.name = "null";
                        t1.token = "null";
                    }
                    valueStack.push_back(t1);
                    ++valueSize;
                } else {
                    valueStack.push_back(array[valueStep]);
                    ++valueSize;
                }
            }
            valueStep = valueStep + 1;
            sentence = sentence.substr(1);
        } else if (new_status[0] ==
                   'r') {
            new_status = new_status.substr(1);
            int gid = atoi(new_status.c_str());
            int len = get_G_Vector(gid).size() - 1;
            if (len == 1) {
                char reduced_symbol = get_G_Char(gid, 0);
                string m = statusStack[statusSize - 2] + reduced_symbol;
                new_status = get_Table_By_String(m);
                statusStack[statusSize - 1] = (new_status);
                symbolStack[symbolSize - 1] = (reduced_symbol);
            } else {
                vector<string> action;
                GXATSNode t1;
                GXATSNode t2;
                string op;
                bool changedT1 = false;
                bool changedT2 = false;
                GXATSNode tempR;
                GXATSNode tempR2;
                bool isChangedOp = false;
                char reduced_symbol = get_G_Char(gid, 0);
                for (int i = 0; i < len; i++) {
                    action.push_back(get_S_By_C(symbolStack[symbolSize - 1]));
                    statusStack.pop_back();
                    --statusSize;
                    symbolStack.pop_back();
                    --symbolSize;
                }
                for (int i = 0; i < action.size(); i++) {
                    if ((is_terminal_char(action[i]) &&
                         !((action[i] == "true" || action[i] == "false" || action[i] == "null" ||
                            action[i] == "value" || action[i] == "num" || action[i] == "string" ||
                            action[i] == "data" || action[i] == "id"))) ||
                        (action[i] == "map" || action[i] == "array")) {
                        if (!isChangedOp) {
                            op = action[i];
                            isChangedOp = true;
                        }
                    } else {
                        if (!changedT1) {
                            changedT1 = true;
                        } else {
                            changedT2 = true;
                        }
                    }
                }
                if (len > 1) {
                    if (changedT2) {
                        if (valueSize < 2) {
                            analyze->throwError("expression error");
                            return 0L;
                        }
                        t2 = valueStack[valueSize - 1];
                        t1 = valueStack[valueSize - 2];
                        if (isFunction) {
                            if (op == "(") {
                                tempR = t1;
                                valueStack.pop_back();
                                --valueSize;
                            } else if (op == ",") {
                                tempR = t1;
                                tempR2 = t2;
                                valueStack.pop_back();
                                valueStack.pop_back();
                                valueSize = valueSize - 2;
                            } else if (op == ")") {
                                for (int i = valueSize - 1; i >= 0; i--) {
                                    if (valueStack[i].token == "id") {
                                        long *params = new long[paramsSize];
                                        int j = paramsSize - 1;
                                        for (int i = 0; i < paramsSize; i++) {
                                            params[i] = (long) &paramsStack[j];
                                            j--;
                                        }
                                        //在这里调用获取函数结果方法
                                        long funVal = analyze->getFunctionValue(valueStack[i].name,
                                                                                params,
                                                                                paramsSize, "");
                                        GXValue *fun = (GXValue *) funVal;
                                        //取出结果
                                        if (fun->tag == GX_TAG_FLOAT) {
                                            tempR.name = to_string(fun->u.float64);
                                            tempR.token = "num";
                                        } else if (fun->tag == GX_TAG_BOOL) {
                                            if (fun->u.int32 == 1) {
                                                tempR.name = "true";
                                                tempR.token = "bool";
                                            } else {
                                                tempR.name = "false";
                                                tempR.token = "bool";
                                            }
                                        } else if (fun->tag == GX_TAG_STRING) {
                                            tempR.name = fun->u.str;
                                            tempR.token = "string";
                                        } else if (fun->tag == GX_TAG_MAP) {
                                            tempR.name = to_string((long) fun->u.ptr);
                                            tempR.token = "map";
                                        } else if (fun->tag == GX_TAG_ARRAY) {
                                            tempR.name = to_string((long) fun->u.ptr);
                                            tempR.token = "array";
                                        } else if (fun->tag == GX_TAG_NULL) {
                                            tempR.name = "null";
                                            tempR.token = "null";
                                        }
                                        valueStack.pop_back();
                                        --valueSize;
                                        isFunction = false;
                                        break;
                                    } else {
                                        //往vector<GXValue>逐个扔进去参数，然后通过id调用
                                        if (valueStack[i].token == "num") {
                                            paramsStack.push_back(
                                                    GX_NewFloat64(
                                                            atof(valueStack[i].name.c_str())));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "string") {
                                            paramsStack.push_back(
                                                    GX_NewGXString(valueStack[i].name.c_str()));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "bool") {
                                            if (valueStack[i].name == "true") {
                                                paramsStack.push_back(GX_NewBool(1));
                                                ++paramsSize;
                                            } else {
                                                paramsStack.push_back(GX_NewBool(0));
                                                ++paramsSize;
                                            }
                                        } else if (valueStack[i].token == "map") {
                                            paramsStack.push_back(
                                                    GX_NewMap(
                                                            (void *) atol(
                                                                    valueStack[i].name.c_str())));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "array") {
                                            paramsStack.push_back(
                                                    GX_NewArray(
                                                            (void *) atol(
                                                                    valueStack[i].name.c_str())));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "null") {
                                            paramsStack.push_back(
                                                    GX_NewNull(1));
                                            ++paramsSize;
                                        }
                                        valueStack.pop_back();
                                        --valueSize;
                                    }
                                }
                            } else {
                                analyze->throwError("expression error");
                                return 0L;
                            }
                        } else {
                            valueStack.pop_back();
                            valueStack.pop_back();
                            valueSize = valueSize - 2;
                            tempR = doubleCalculate(t1, t2, op);
                            if (tempR.token == "error") {
                                analyze->throwError(tempR.name);
                                return 0L;
                            }
                        }
                    } else {
                        if (valueSize < 1) {
                            analyze->throwError("expression error");
                            return 0L;
                        }
                        t1 = valueStack[valueSize - 1];
                        valueStack.pop_back();
                        --valueSize;
                        tempR = singleCalculate(t1, op);
                        if (tempR.token == "error") {
                            analyze->throwError(tempR.name);
                            return 0L;
                        }
                    }
                    if (isFunction && op == ",") {
                        valueStack.push_back(tempR);
                        valueStack.push_back(tempR2);
                        valueSize = valueSize + 2;
                    } else {
                        valueStack.push_back(tempR);
                        ++valueSize;
                    }
                }
                string m = statusStack[statusSize - 1] + reduced_symbol;
                new_status = get_Table_By_String(m);
                statusStack.push_back(new_status);
                ++statusSize;
                symbolStack.push_back(reduced_symbol);
                ++symbolSize;
            }
        } else {
            analyze->throwError("expression error");
            return 0L;
        }
    }
}
