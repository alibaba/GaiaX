#include "GXWordAnalyze.h"

using namespace std;
static char key[3][20] = {"true", "false", "null"}; //定义一个二维数组存放关键字
//种别码：对应如下--------------
//true      == 1
//false     == 2
//null      == 3
//value     == 10
//num       == 11
//string    == 12
//data      == 13
//id        == 14
//,         == 15
//(         == 16
//)         == 17
//!         == 18
//-         == 19
//+         == 20
//%         == 21
///         == 22
//*         == 23
//>         == 24
//<         == 25
//>=        == 26
//<=        == 27
//==        == 28
//!=        == 29
//&&        == 30
//||        == 31
//?         == 32
//:         == 33
//?:        == 34
//error     == 35
//function  == 36

//判断关键字
int isKey(char s[]) {
    for (int i = 0; i < 3; i++) {
        if (strcmp(s, key[i]) == 0) {
            return i + 1; //关键字的种别码依次为 true=1,false=2,null=3即为 i+1 的值
        }
    }
    return -1;
}

//判断是不是字母
bool isChar(char ch) {
    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_'))
        return true;
    else
        return false;
}

//判断是不是数字
bool isNum(char ch) {
    if (ch >= '0' && ch <= '9')
        return true;
    else
        return false;
}

//核心子程序
GXATSNode scanner(int &syn, int &p, char s[], void *p_analyze) {
    GXAnalyze *analyze = (GXAnalyze *) p_analyze;
    int lengthC = strlen(s);
    char *token = new char[lengthC + 1];
    string sign;
    int count = 0;
    bool err = false;
    if (s[p] == ' ')
        p++;
    //VALUE
    if (s[p] == '$') {
        if (s[p + 1] == '$') {
            //ALL DATA
            token[count++] = s[p];
            p++;
            token[count++] = s[p];
            p++;
            token[count] = '\0'; //'\0'作为结束符 ,将单词分隔开
            syn = 13;
            sign = "data";
        } else {
            //VALUE
            p++;
            while (isNum(s[p]) || isChar(s[p]) || s[p] == '[' || s[p] == ']' || s[p] == '.') {
                token[count++] = s[p];
                p++;
            }
            token[count] = '\0'; //'\0'作为结束符 ,将单词分隔开
            syn = 10;            //value
            sign = "value";
        }
    }
        //ID OR 特定关键字
    else if (isChar(s[p])) {
        while (isNum(s[p]) || isChar(s[p])) {
            token[count++] = s[p];
            p++;
        }
        token[count] = '\0'; //'\0'作为结束符 ,将单词分隔开
        syn = isKey(token);  //特定类型
        //ID
        if (syn == -1) {
            syn = 14; //ID
            sign = "id";
            if (s[p] == '(' && s[p + 1] == ')') {
                sign = "function";
                syn = 36;
                p = p + 2;
            }
        }
            //TOKEN关键字
        else {
            string temp = token;
            if (temp == "true" || temp == "false") {
                sign = "bool";
            } else {
                sign = "null";
            }
        }
    }
        //NUM
    else if (isNum(s[p])) {
        bool hasPoint = false;
        while (isNum(s[p]) || s[p] == '.' || isChar(s[p])) {
            if (isChar(s[p])) {
                //error 数字后面接字母
                err = true;
                token[count++] = s[p];
                p++;
            } else if (s[p] == '.') {
                if (hasPoint == false) {
                    hasPoint = true;
                    token[count++] = s[p];
                    p++;
                    if (!isNum(s[p])) {
                        //error 小数点后不接数值
                        err = true;
                    }
                } else {
                    //error 拥有多于一个.
                    err = true;
                    token[count++] = s[p];
                    p++;
                }
            } else {
                token[count++] = s[p];
                p++;
            }
        }
        syn = 11; //数字digit(digit) *
        sign = "num";
        if (err) {
            syn = 0;
            sign = "error";
            string errorMsg = token;
            analyze->throwError("unknown identifier: " + errorMsg);
        }
        token[count] = '\0'; //结束标识
    }
        //STRING
    else if (s[p] == '\'') {
        p++;
        while (s[p] != '\'' && s[p] != '\0') {
            token[count++] = s[p];
            p++;
        }
        if (s[p] == '\0') {
            err = true;
        }
        syn = 12;
        sign = "string";
        if (err) {
            sign = "error";
            syn = 0;
            string errorMsg = token;
            analyze->throwError("unknown identifier: " + errorMsg);
        }
        token[count] = '\0'; //结束标识
        p++;
    }
        //如果是运算符或者界符
    else {
        //先处理没有争议的字符
        switch (s[p]) {
            case ',':
                syn = 15;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '(':
                syn = 16;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case ')':
                syn = 17;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '-':
                syn = 19;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '+':
                syn = 20;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '%':
                syn = 21;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '/':
                syn = 22;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '*':
                syn = 23;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case ':':
                syn = 33;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "op";
                break;
            case '#':
                syn = 35;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "null";
                break;
            case '~':
                syn = 36;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "null";
                break;
            case '?': {
                token[count++] = s[p];
                if (s[p + 1] == ':') {
                    p++;
                    token[count++] = s[p];
                    syn = 34;
                    sign = "op";
                } else {
                    syn = 32;
                    sign = "op";
                }
                token[count] = '\0';
                break;
            }
                //< <=
            case '<': {
                token[count++] = s[p];
                if (s[p + 1] == '=') {
                    p++;
                    token[count++] = s[p];
                    syn = 27;
                    sign = "op";
                } else {
                    syn = 25;
                    sign = "op";
                }
                token[count] = '\0';
                break;
            }
                //> >=
            case '>': {
                token[count++] = s[p];
                if (s[p + 1] == '=') {
                    p++;
                    token[count++] = s[p];
                    syn = 26;
                    sign = "op";
                } else {
                    syn = 24;
                    sign = "op";
                }
                token[count] = '\0';
                break;
            }
                //&&
            case '&': {
                token[count++] = s[p];
                if (s[p + 1] == '&') {
                    p++;
                    token[count++] = s[p];
                    syn = 30;
                    sign = "op";
                } else {
                    syn = 0;
                    sign = "op";
                }
                token[count] = '\0';
                break;
            }
                //||
            case '|': {
                token[count++] = s[p];
                if (s[p + 1] == '|') {
                    p++;
                    token[count++] = s[p];
                    syn = 31;
                    sign = "op";
                } else {
                    syn = 0;
                    p++;
                    token[count++] = s[p];
                    sign = "error";
                    string errorMsg = token;
                    analyze->throwError("unknown identifier: " + errorMsg);
                }
                token[count] = '\0';
                break;
            }
                //!=
            case '!': {
                token[count++] = s[p];
                if (s[p + 1] == '=') {
                    p++;
                    token[count++] = s[p];
                    syn = 29;
                    sign = "op";
                } else {
                    syn = 18;
                    sign = "op";
                }
                token[count] = '\0';
                break;
            }
                //==
            case '=': {
                token[count++] = s[p];
                if (s[p + 1] == '=') {
                    p++;
                    token[count++] = s[p];
                    syn = 28;
                    sign = "op";
                } else {
                    syn = 0;
                    sign = "error";
                    p++;
                    token[count++] = s[p];
                    string errorMsg = token;
                    analyze->throwError("unknown identifier: " + errorMsg);
                }
                token[count] = '\0';
                break;
            }
            default: {
                syn = 0;
                token[count++] = s[p];
                token[count] = '\0';
                sign = "error";
                string errorMsg = token;
                delete[]token;
                analyze->throwError("unknown identifier: " + errorMsg);
                break;
            }
        }
        //后移
        p++; //判断运算符和界符的这部分由于指针 p 没有向后指，所以需要将指针 p 向后移一位
    }
    string str1 = token;
    GXATSNode temp;
    if (sign == "bool" || sign == "op") {
        temp = GXATSNode(str1, str1, sign);
    } else {
        temp = GXATSNode(str1, sign, sign);
    }
    delete[]token;
    return temp;
}
