#include "GXAnalyze.h"
#include <mutex>          // std::mutex

using namespace std;

static mutex mtx;                                                   //锁，避免多线程读写导致的线程不安全问题发生
static vector<vector<char> > grammarProduct;                        //文法grammarProduct[S]产生式 ，~为空字
static unordered_map<char, set<char> > terminalSymbolMap;           //终结符(char)terminal symbol,及它的first集合(set<char>)
static unordered_map<char, set<char> > nonTerminalSymbolMap;        //非终结符(char)non-terminal symbol，及它的first集合(set<char>)
static unordered_map<string, string> gotoTable;                     //初始化最终产物，可根据该表格找到规约或递进的结果
static bool isInit = false;                                         //判断是否已经进行过初始化
static unordered_map<string, char> terminal;                        //终结符集合
static unordered_map<string, char> nonTerminal;                     //非终结符集合
static vector<string> grammarFormula;                               //每层文法式的集合
struct Closure {                                                    //闭包CLOSURE
    vector<vector<char> > project;                                  //项目集
    vector<set<char> > outlook;                                     //展望串
    unordered_map<char, int> go;                                    // GO函数
};
static vector<Closure> closureArray;                                //闭包集合
static unordered_map<char, string> wordToSymbol;                    //key:word value:symbol
static unordered_map<string, string> cache;                         //表达式缓存
/*
 * 存放终结符完整词汇的string集合
 */
static string terminalWord[] = {"true", "false", "null", "value", "num", "string", "data", "id",
                                ",", "function", "long",
                                "(", ")", "!", "-", "+", "%", "/", "*", ">", "<", ">=", "<=", "==",
                                "!=",
                                "&&", "||", "?", ":", "?:", "error", "#", "~"};
/*
 * 存放终结符标识符的char集合
 */
static char terminalSymbol[] = {'t', 'f', 'n', 'v', 'u', 's', 'd', 'i', ',', 'a', 'o', '(', ')',
                                '!', '-',
                                '+', '%',
                                '/', '*', '>', '<', 'l', 'b', '=', 'p', '&', '@', '?', ':', 'y',
                                'e', '#',
                                '~'};
/*
 * 存放非终结符完整词汇的string集合
 */
static string nonTerminalWord[] = {"S", "Ten", "L", "Nin", "Eig", "Sev", "Six", "Fiv", "Fou", "Thr",
                                   "Two",
                                   "P", "One"};
/*
 * 存放非终结符标识符的char集合
 */
static char nonTerminalSymbol[] = {'S', 'T', 'L', 'N', 'E', 'D', 'F', 'G', 'H', 'U', 'Y', 'P', 'O'};
/*
 * 表达式语法集合
 */
static string grammar[] = {"S->T", "T->TyN|L:N|N", "L->N?N", "N->N@E|E", "E->E&D|D", "D->DpF|D=F|F",
                           "F->F>G|F<G|FlG|FbG|G", "G->G+H|G-H|H", "H->H*U|H/U|H%U|U",
                           "U->+Y|-Y|!Y|Y", "Y->(T)|i(P)|O|~", "P->O,P|O|~",
                           "O->t|f|n|v|u|s|d|a|o"};

/*
 * 判断word是否为终结符
 */
bool isTerminalWord(const string &s) {
    //在map中判断
    if (terminal.count(s) > 0) {
        return true;
    }
    if (s == "M") {
        return true;
    }
    return false;
}

/*
 * 判断字符是否是数字类型
 */
bool isNumber(char ch) {
    if (ch >= '0' && ch <= '9')
        return true;
    else
        return false;
}

/*
 * 初始化：获取文法grammarProduct
 */
void getGrammarProduct() {
    int sizeG = sizeof(grammar) / sizeof(grammar[0]);
    for (int i = 0; i < sizeG; i++) {
        grammarFormula.push_back(grammar[i]);
    }
    char symbol;
    int i = 0;
    vector<char> value;
    char chX;
    set<char> m;
    nonTerminalSymbolMap['M'] = m;
    for (auto temp : grammarFormula) {
        for (int y = 0; y < temp.length(); y++) {
            symbol = temp[y];
            if (symbol == '|') {
                grammarProduct.push_back(value);
                value.clear();
                i = 3;
                value.push_back(chX);
                continue;
            }
            i++;
            if (i == 1) {
                chX = symbol;
                nonTerminalSymbolMap[symbol] = m;
            } else if (i != 2 && i != 3 && symbol != '~')
                terminalSymbolMap[symbol] = m;
            if (i != 2 && i != 3)
                value.push_back(symbol);
            if (y == temp.length() - 1) {
                if (!value.empty()) {
                    grammarProduct.push_back(value);
                }
                value.clear();
                i = 0;
                continue;
            }
        }
    }
    if (grammarProduct.empty()) {
        exit(0);
    }
    value.clear();
    value.push_back('M');
    value.push_back(grammarProduct[0][0]);
    grammarProduct.insert(grammarProduct.begin(), value);

    //去掉ts中的非终结符
    for (auto &it : nonTerminalSymbolMap) {
        unordered_map<char, set<char> >::iterator iter;
        iter = terminalSymbolMap.find(it.first);
        if (iter != terminalSymbolMap.end())
            terminalSymbolMap.erase(iter);
    }
}

/*
 * 初始化：获取First集
 */
void getFirst() { //得到First集合
    for (auto &it : terminalSymbolMap)
        it.second.insert(it.first);

    //求非终结符的First集合
    int r = 0;
    int change = 1;
    while (change) {
        if (r == 20)
            break;
        r++;
        change = 0;
        for (auto &it : nonTerminalSymbolMap) {
            for (unsigned int i = 0; i < grammarProduct.size(); i++) {
                if (grammarProduct[i][0] == it.first) {
                    unsigned int size = it.second.size();
                    unordered_map<char, set<char> >::iterator iter = terminalSymbolMap.find(
                            grammarProduct[i][1]);
                    if (terminalSymbolMap.find(grammarProduct[i][1]) != terminalSymbolMap.end() ||
                        grammarProduct[i][1] == '~') {
                        it.second.insert(grammarProduct[i][1]);
                        if (it.second.size() > size)
                            change = 1;
                    } else {
                        unsigned int col = 1;
                        while (1) {
                            int flag = 0;
                            unordered_map<char, set<char> >::iterator itt = nonTerminalSymbolMap.find(
                                    grammarProduct[i][col]);
                            for (auto &iter : itt->second) {
                                if (iter == '~')
                                    flag = 1;
                                else
                                    it.second.insert(iter);
                            }
                            if (flag) {
                                col++;
                                if (grammarProduct[i].size() <= col) {
                                    it.second.insert('~');
                                    break;
                                } else if (terminalSymbolMap.find(grammarProduct[i][col]) !=
                                           terminalSymbolMap.end()) {
                                    it.second.insert(grammarProduct[i][col]);
                                    break;
                                } else {
                                }
                            } else
                                break;
                        }
                        if (it.second.size() > size)
                            change = 1;
                    }
                }
            }
        }
    }
}

/*
 * 初始化：生成闭包集
 */
void getClosure() {
    int i = 0;
    Closure clo;
    closureArray.push_back(clo);
    while (1) {
        if (i == closureArray.size())
            break;
        if (i == 0) {
            vector<char> vec(grammarProduct[0]);
            vec.insert(vec.begin() + 1, ' ');
            closureArray[i].project.push_back(vec);
            set<char> m;
            m.insert('#');
            closureArray[i].outlook.push_back(m);
        }
        for (unsigned int j = 0; j < closureArray[i].project.size(); j++) {
            for (unsigned int k = 0; k < closureArray[i].project[j].size(); k++) {
                if (closureArray[i].project[j][k] == ' ') {
                    if (k == closureArray[i].project[j].size() - 1)
                        break;
                    for (unsigned int x = 0;
                         x < grammarProduct.size(); x++) {
                        if (grammarProduct[x][0] ==
                            closureArray[i].project[j][k + 1]) {
                            vector<char> vec(grammarProduct[x]);
                            vec.insert(vec.begin() + 1, ' ');
                            int exist = 0;
                            for (unsigned int y = 0;
                                 y < closureArray[i].project.size(); y++) {
                                if (closureArray[i].project[y] == vec) {
                                    exist = y;
                                    break;
                                }
                            }
                            if (exist == 0) {
                                closureArray[i].project.push_back(vec);
                            }
                            set<char> m;
                            bool emp = true;    //判空
                            int t = 0;
                            while (emp) {
                                emp = false;
                                if (k + t + 1 == closureArray[i].project[j].size() - 1) { //情况一
                                    for (auto it : closureArray[i].outlook[j])
                                        m.insert(it);
                                } else if (
                                        terminalSymbolMap.find(
                                                closureArray[i].project[j][k + t + 2]) !=
                                        terminalSymbolMap.end()) { //情况二
                                    m.insert(closureArray[i].project[j][k + 2 + t]);
                                } else {
                                    set<char> m1(
                                            (nonTerminalSymbolMap.find(
                                                    closureArray[i].project[j][k + 2 +
                                                                               t]))->second);
                                    for (auto it : m1) {
                                        if (it == '~') {
                                            emp = true;
                                            t++;
                                        } else {
                                            m.insert(it);
                                        }
                                    }
                                }
                            }
                            if (exist) {
                                for (auto it : m) {
                                    closureArray[i].outlook[exist].insert(it);
                                }
                            } else
                                closureArray[i].outlook.push_back(m);
                        }
                    }
                    break;
                }
            }
        }
        for (unsigned int j = 0; j < closureArray[i].project.size(); j++) {
            for (unsigned int k = 0; k < closureArray[i].project[j].size(); k++) {
                if (closureArray[i].project[j][k] == ' ') {
                    if (k == closureArray[i].project[j].size() - 1)
                        break;
                    vector<char> new_closure_pro(closureArray[i].project[j]);
                    new_closure_pro[k] = new_closure_pro[k + 1];
                    new_closure_pro[k + 1] = ' ';
                    set<char> new_closure_search(closureArray[i].outlook[j]);
                    bool dif = false;
                    for (unsigned int x = 0; x < closureArray.size(); x++) {
                        // dif = false;
                        for (unsigned int y = 0; y <
                                                 closureArray[x].project.size(); y++) {
                            dif = false;
                            if (new_closure_pro == closureArray[x].project[y]) {
                                if (closureArray[x].outlook[0].size() !=
                                    new_closure_search.size()) {
                                    dif = true;
                                    continue;
                                }
                                auto iter = closureArray[x].outlook[0].begin();
                                for (auto it : new_closure_search) {
                                    if (it != *iter) {
                                        dif = true;
                                        break;
                                    }
                                    iter++;
                                }
                                if (dif == false) {
                                    closureArray[i].go[new_closure_pro[k]] = x;
                                    break;
                                }
                            } else
                                dif = true;
                            if (dif == false)
                                break;
                        }
                        if (dif == false)
                            break;
                    }
                    if (closureArray[i].go.count(new_closure_pro[k]) != 0 &&
                        dif) {
                        closureArray[closureArray[i].go[new_closure_pro[k]]].project.push_back(
                                new_closure_pro);
                        closureArray[closureArray[i].go[new_closure_pro[k]]].outlook.push_back(
                                new_closure_search);
                        break;
                    }
                    if (dif) {
                        Closure new_closure;
                        new_closure.project.push_back(new_closure_pro);
                        new_closure.outlook.push_back(new_closure_search);
                        closureArray.push_back(new_closure);
                        closureArray[i].go[new_closure_pro[k]] = closureArray.size() - 1;
                    }
                }
            }
        }
        i++;
    }
}

/*
 * 初始化：生成gotoTable
 */
int getGotoTable() {
    for (unsigned int i = 0; i < closureArray.size(); i++) {
        for (unsigned int j = 0; j < closureArray[i].project.size(); j++) {
            for (unsigned int k = 0; k < closureArray[i].project[j].size(); k++) {
                if (closureArray[i].project[j][k] == ' ') {
                    if (k == closureArray[i].project[j].size() - 1) {
                        if (closureArray[i].project[j][0] == 'M') {
                            string m = to_string(i) + '#';
                            if (gotoTable.find(m) != gotoTable.end() && gotoTable[m] != "acc") {
                                return 0;
                            } else
                                gotoTable[m] = "acc";
                        } else {
                            int id;
                            for (unsigned int x = 0; x < grammarProduct.size(); x++) {
                                vector<char> vec(closureArray[i].project[j]);
                                vec.pop_back();
                                if (grammarProduct[x] == vec) {
                                    id = x;
                                    break;
                                }
                            }
                            for (auto it : closureArray[i].outlook[j]) {
                                string m = to_string(i) + it;
                                if (gotoTable.find(m) != gotoTable.end() &&
                                    gotoTable[m] != (string) "r" + to_string(id)) {
                                    return 0;
                                } else
                                    gotoTable[m] = (string) "r" + to_string(id);
                            }
                        }
                    } else {
                        char next = closureArray[i].project[j][k + 1];
                        if (terminalSymbolMap.find(next) != terminalSymbolMap.end()) {
                            string m = to_string(i) + next;
                            if (gotoTable.find(m) != gotoTable.end() &&
                                gotoTable[m] !=
                                (string) "s" + to_string(closureArray[i].go[next])) {
                                return 0;
                            } else
                                gotoTable[m] = (string) "s" + to_string(closureArray[i].go[next]);
                        } else {
                            string m = to_string(i) + next;
                            if (gotoTable.find(m) != gotoTable.end() &&
                                gotoTable[m] != to_string(closureArray[i].go[next])) {
                                return 0;
                            } else
                                gotoTable[m] = to_string(closureArray[i].go[next]);
                        }
                    }
                    break;
                }
            }
        }
    }
    return 1;
}

/*
 * 初始化：获取终结符和非终结符相关集合
 */
void initTerminal() {
    int sizeTerminal = sizeof(terminalWord) / sizeof(terminalWord[0]);
    int sizeNonTerminal = sizeof(nonTerminalWord) / sizeof(nonTerminalWord[0]);
    set<char> m;
    for (int i = 0; i < sizeTerminal; i++) {
        wordToSymbol.insert(pair<char, string>(terminalSymbol[i], terminalWord[i]));
        terminal.insert(pair<string, char>(terminalWord[i], terminalSymbol[i]));
        terminalSymbolMap[terminalSymbol[i]] = m;
    }
    for (int i = 0; i < sizeNonTerminal; i++) {
        wordToSymbol.insert(pair<char, string>(nonTerminalSymbol[i], nonTerminalWord[i]));
        nonTerminal.insert(pair<string, char>(nonTerminalWord[i], nonTerminalSymbol[i]));
        nonTerminalSymbolMap[nonTerminalSymbol[i]] = m;
    }
}

/*
 * 调用所有初始化函数
 */
void init() {
    if (!isInit) {
        cache.reserve(1024);
        isInit = true;
        getGrammarProduct();
        getFirst();
        getClosure();
        initTerminal();
        getGotoTable();
    }
}

/*
 * Analyze对象初始化
 */
GXAnalyze::GXAnalyze() {
    init();
}

/*
 * Analyze对象析构函数
 */
GXAnalyze::~GXAnalyze() {
}

/*
 * 获取两个数值计算的结果
 */
GXATSNode GXAnalyze::doubleCalculate(GXATSNode left, GXATSNode right, string op) {
    GXATSNode result = GXATSNode(left.name, left.token, left.token);
    string name;
    if ((op == "?") || (op == ":") || (op == "?:") || (op == "==") || (op == "!=") || (op == "||") || (op == "&&")) {
        //可以返回map和array
    } else if (left.token == "map" || left.token == "array") {
        result.name = "expressionError: illegal operator '" + op + "',left operand has type of '" +
                      left.token + "'";
        result.token = "error";
        return result;
    } else if (op != "?:" && (right.token == "map" || right.token == "array")) {
        result.name = "expressionError: illegal operator '" + op + "',right operand has type of '" +
                      right.token + "'";
        result.token = "error";
        return result;
    }
    //返回值都为bool
    if (op == ">") {
        result.token = "bool";
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
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
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
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
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
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
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
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
        } else if (left.token == "bool" && (right.token != "null")) {
            if (left.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token != "null" && right.token == "bool") {
            if (right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token != "null" && right.token != "null") {
            result.name = "true";
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
        } else if (left.token == "bool") {
            if (left.name == "true" || right.token != "null") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (right.token == "bool") {
            if (left.token != "null" || right.name == "true") {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (left.token != "null" || right.token != "null") {
            result.name = "true";
        } else {
            result.name = "false";
        }
    }
        //返回值都为bool
    else if (op == "!=") {
        result.token = "bool";
        if (left.token == "bool" && (right.token == "num" || right.token == "long")) {
            if ((left.name == "true" && (stof(right.name) == 0.0F)) ||
                (left.name == "false" && (stof(right.name) != 0.0F))) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "num" || left.token == "long") && right.token == "bool") {
            if (((stof(left.name) == 0.0F) && right.name == "true") ||
                ((stof(left.name) != 0.0F) && right.name == "false")) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (((left.token == "num" || left.token == "long") &&
                    (right.token == "num" || right.token == "long"))) {
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
        if (left.token == "bool" && (right.token == "num" || right.token == "long")) {
            if ((left.name == "true" && (stof(right.name) != 0.0F)) ||
                (left.name == "false" && (stof(right.name) == 0.0F))) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if ((left.token == "num" || left.token == "long") && right.token == "bool") {
            if (((stof(left.name) != 0.0F) && right.name == "true") ||
                ((stof(left.name) == 0.0F) && right.name == "false")) {
                result.name = "true";
            } else {
                result.name = "false";
            }
        } else if (((left.token == "num" || left.token == "long") &&
                    (right.token == "num" || right.token == "long")) &&
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
        if (left.name == "true" || ((left.name != "false" && left.token != "null"))) {
            result.name = right.name;
            result.token = right.token;
        } else if ((left.token == "num" || left.token == "long") && stof(left.name) != 0.0F) {
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
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
            if (left.token == "num" || right.token == "num") {
                float temp = stof(left.name) + stof(right.name);
                result.name = to_string(temp);
                result.token = "num";
            } else {
                long temp = stol(left.name) + stol(right.name);
                result.name = to_string(temp);
                result.token = "long";
            }
        } else if (left.token == "string" && right.token == "string") {
            result.token = "string";
            result.name = left.name + right.name;
        } else if (left.token == "null" || right.token == "null") {
            result.name = "null";
            result.token = "null";
        } else {
            if ((left.token == "num" || left.token == "long") && right.token == "string") {
                result.name = left.name + right.name;
                result.token = "string";
            } else if ((right.token == "num" || right.token == "long") && left.token == "string") {
                result.name = left.name + right.name;
                result.token = "string";
            } else {
                result.token = "error";
                if (left.token == "string" || (left.token == "num" || left.token == "long")) {
                    result.name = "expressionError: '" + right.name +
                                  "' expected num or string value,not '" + right.token + "'";
                } else if (right.token == "string" ||
                           (right.token == "num" || right.token == "long")) {
                    result.name = "expressionError: '" + left.name +
                                  "' expected num or string value,not '" + left.token + "'";
                } else {
                    result.name = "expressionError: '" + left.name +
                                  "' expected num or string value,not '" + left.token + "'";
                }
            }
        }
    } else if (op == "-") {
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {

            if (left.token == "num" || right.token == "num") {
                float temp = stof(left.name) - stof(right.name);
                result.name = to_string(temp);
                result.token = "num";
            } else {
                long temp = stol(left.name) - stol(right.name);
                result.name = to_string(temp);
                result.token = "long";
            }
        } else if (left.token == "null" || right.token == "null") {
            result.name = "null";
            result.token = "null";
        } else {
            result.token = "error";
            if ((left.token == "num" || left.token == "long")) {
                result.name =
                        "expressionError: '" + right.name + "'" + ": expected num value,not: " +
                        right.token;
            } else if (left.token == "null" || right.token == "null") {
                result.name = "null";
                result.token = "null";
            } else if ((right.token == "num" || right.token == "long")) {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            } else {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            }
        }
    } else if (op == "*") {
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
            if (left.token == "num" || right.token == "num") {
                float temp = stof(left.name) * stof(right.name);
                result.name = to_string(temp);
                result.token = "num";
            } else {
                long temp = stol(left.name) * stol(right.name);
                result.name = to_string(temp);
                result.token = "long";
            }
        } else if (left.token == "null" || right.token == "null") {
            result.name = "null";
            result.token = "null";
        } else {
            result.token = "error";
            if ((left.token == "num" || left.token == "long")) {
                result.name =
                        "expressionError: '" + right.name + "'" + ": expected num value,not: " +
                        right.token;
            } else if (left.token == "null" || right.token == "null") {
                result.name = "null";
                result.token = "null";
            } else if ((right.token == "num" || right.token == "long")) {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            } else {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            }
        }
    } else if (op == "/") {
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
            if (stof(right.name) == 0) {
                result.token = "error";
                result.name = "expressionError: divide or mod by zero";
            } else {
                if (left.token == "num" || right.token == "num") {
                    float temp = stof(left.name) / stof(right.name);
                    result.name = to_string(temp);
                    result.token = "num";
                } else {
                    long temp = stol(left.name) / stol(right.name);
                    long double tempF = stold(left.name) / stold(right.name);
                    if (temp != tempF && (left.token == "num" || right.token == "num")) {
                        result.name = to_string(tempF);
                        result.token = "num";
                    } else {
                        result.name = to_string(temp);
                        result.token = "long";
                    }
                }
            }
        } else if (left.token == "null" || right.token == "null") {
            result.name = "null";
            result.token = "null";
        } else {
            result.token = "error";
            if ((left.token == "num" || left.token == "long")) {
                result.name =
                        "expressionError: '" + right.name + "'" + ": expected num value,not: " +
                        right.token;
            } else if (left.token == "null" || right.token == "null") {
                result.name = "null";
                result.token = "null";
            } else if ((right.token == "num" || right.token == "long")) {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            } else {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            }
        }
    } else if (op == "%") {
        if ((left.token == "num" || left.token == "long") &&
            (right.token == "num" || right.token == "long")) {
            if (stof(right.name) == 0) {
                result.token = "error";
                result.name = "expressionError: divide or mod by zero";
            } else {
                long temp = stol(left.name) % stol(right.name);
                result.name = to_string(temp);
                result.token = "long";
            }
        } else if (left.token == "null" || right.token == "null") {
            result.name = "null";
            result.token = "null";
        } else {
            result.token = "error";
            if ((left.token == "num" || left.token == "long")) {
                result.name =
                        "expressionError: '" + right.name + "'" + ": expected num value,not: " +
                        right.token;
            } else if (left.token == "null" || right.token == "null") {
                result.name = "null";
                result.token = "null";
            } else if ((right.token == "num" || right.token == "long")) {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            } else {
                result.name =
                        "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                        left.token;
            }
        }
    }
    return result;
}

/*
 * 获取单个数值计算的结果
 */
GXATSNode GXAnalyze::singleCalculate(GXATSNode left, string op) {
    GXATSNode result = GXATSNode(left.name, left.token, left.token);
    if (left.token == "map" || left.token == "array") {
        return result;
    }
    if (op == "-") {
        if ((left.token == "num" || left.token == "long")) {
            float temp = -stof(left.name);
            result.name = to_string(temp);
            result.token = left.token;
        } else {
            result.token = "error";
            result.name =
                    "expressionError: '" + left.name + "'" + ": expected num value,not: " +
                    left.token;
        }
    } else if (op == "+") {
        if ((left.token == "num" || left.token == "long")) {
            result.token = left.token;
            result.name = left.name;
        } else {
            result.token = "error";
            result.name =
                    "expressionError: '" + left.name + "'" + ": expected num value,not: " +
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
                        "expressionError: unknown identifier '" + left.name + "'";
            }
        } else {
            result.token = "error";
            result.name =
                    "expressionError: '" + left.name + "' expected bool value,not '" +
                    left.token + "'";
        }
    }
    return result;
}

/*
 * @expression：需要计算的表达式
 * @source：数据源
 * 计算表达式结果函数
 */
long GXAnalyze::getValue(string expression, void *source) {
    char *input;
    int inputLength = expression.length();
    input = new char[inputLength + 2];
    vector<GXATSNode> array;
    vector<GXATSNode> arrayNum;
    array.reserve(128);
    arrayNum.reserve(128);
    string result = "#";
    strcpy(input, expression.c_str());
    int p = 0;
    int synCode;
    int countNode = 0;
    while (p < strlen(input)) {
        if (input[p] == ' ') {
            p++;
        } else {
            GXATSNode token = scanner(synCode, p, input, this);
            if (isTerminalWord(token.detail)) {
                result = result + terminal[token.detail];
            } else {
                result = result + nonTerminal[token.detail];
            }
            GXATSNode tokenNum;
            if (token.token == "value" || token.token == "data") {
                long res = this->getSourceValue(token.name, source);
                if (res == 0) {
                    tokenNum.name = "null";
                    tokenNum.token = "null";
                } else {
                    GXValue *gxv = (GXValue *) res;
                    if (gxv->tag == GX_TAG_FLOAT) {
                        tokenNum.name = to_string(gxv->float64);
                        if (tokenNum.name.find('.') != -1) {
                            tokenNum.name = regex_replace(tokenNum.name, regex("0+?$"),
                                                          ""); // 除了捕捉到的组以外，其他的东西均舍弃
                            tokenNum.name = regex_replace(tokenNum.name, regex("[.]$"),
                                                          ""); // 除了捕捉到的组以外，其他的东西均舍弃
                        }
                        tokenNum.token = "num";
                    } else if (gxv->tag == GX_TAG_LONG) {
                        tokenNum.name = to_string(gxv->intNum);
                        tokenNum.token = "long";
                    } else if (gxv->tag == GX_TAG_STRING) {
                        tokenNum.name = gxv->str;
                        tokenNum.token = "string";
                    } else if (gxv->tag == GX_TAG_BOOL) {
                        if (gxv->int32 == 1) {
                            tokenNum.name = "true";
                        } else {
                            tokenNum.name = "false";
                        }
                        tokenNum.token = "bool";
                    } else if (gxv->tag == GX_TAG_ARRAY) {
                        tokenNum.name = to_string((long) (gxv->ptr));
                        tokenNum.token = "array";
                    } else if (gxv->tag == GX_TAG_MAP) {
                        tokenNum.name = to_string((long) (gxv->ptr));
                        tokenNum.token = "map";
                    } else if (gxv->tag == GX_TAG_NULL) {
                        tokenNum.name = "null";
                        tokenNum.token = "null";
                    }
                    if (gxv->tag == GX_TAG_STRING && gxv->str != NULL) {
                        delete[] gxv->str;
                        gxv->str = NULL;
                    }
                    delete gxv;
                }
            } else if (token.token == "function") {
                long res = this->getFunctionValue(token.name, nullptr, 0, "");
                if (res == 0) {
                    tokenNum.name = "null";
                    tokenNum.token = "null";
                } else {
                    GXValue *gxv = (GXValue *) res;
                    if (gxv->tag == GX_TAG_FLOAT) {
                        tokenNum.name = to_string(gxv->float64);
                        if (tokenNum.name.find('.') != -1) {
                            tokenNum.name = regex_replace(tokenNum.name, regex("0+?$"),
                                                          ""); // 除了捕捉到的组以外，其他的东西均舍弃
                            tokenNum.name = regex_replace(tokenNum.name, regex("[.]$"),
                                                          ""); // 除了捕捉到的组以外，其他的东西均舍弃
                        }
                        tokenNum.token = "num";
                    } else if (gxv->tag == GX_TAG_LONG) {
                        tokenNum.name = to_string(gxv->intNum);
                        tokenNum.token = "long";
                    } else if (gxv->tag == GX_TAG_STRING) {
                        tokenNum.name = gxv->str;
                        tokenNum.token = "string";
                    } else if (gxv->tag == GX_TAG_BOOL) {
                        if (gxv->int32 == 1) {
                            tokenNum.name = "true";
                        } else {
                            tokenNum.name = "false";
                        }
                        tokenNum.token = "bool";
                    } else if (gxv->tag == GX_TAG_ARRAY) {
                        tokenNum.name = to_string((long) (gxv->ptr));
                        tokenNum.token = "array";
                    } else if (gxv->tag == GX_TAG_MAP) {
                        tokenNum.name = to_string((long) (gxv->ptr));
                        tokenNum.token = "map";
                    } else if (gxv->tag == GX_TAG_NULL) {
                        tokenNum.name = "null";
                        tokenNum.token = "null";
                    }
                    if (gxv->tag == GX_TAG_STRING && gxv->str != NULL) {
                        delete[] gxv->str;
                        gxv->str = NULL;
                    }
                    delete gxv;
                }
            } else {
                tokenNum = token;
            }
            if (token.token != "op") {
                token.count = countNode;
                tokenNum.count = countNode;
                countNode++;
                arrayNum.push_back(tokenNum);
            }
            array.push_back(token);
        }
    }
    //释放s的内存空间
    delete[]input;
    result = result + "#";
    long Res;
    mtx.lock();
    unordered_map<string, string>::iterator iter = cache.find(result);
    mtx.unlock();
    if (iter != cache.end()) {
        if (iter->second == "(0)") {
            GXATSNode res = arrayNum[0];
            GXValue *pointer;
            if (res.token == "string") {
                pointer = new GXValue(GX_TAG_STRING, res.name);
            } else if (res.token == "bool") {
                if (res.name == "true") {
                    pointer = new GXValue(GX_TAG_BOOL, 1);
                } else {
                    pointer = new GXValue(GX_TAG_BOOL, 0);
                }
            } else if (res.token == "num") {
//                if (res.name.find('.') != -1) {
//                    regex e("0+?$");
//                    regex e2("[.]$");
//                    res.name = regex_replace(res.name, e, "");
//                    res.name = regex_replace(res.name, e2, "");
//                }
                pointer = new GXValue(GX_TAG_FLOAT, (float) atof(res.name.c_str()));
            } else if (res.token == "long") {
                pointer = new GXValue(GX_TAG_LONG, (int64_t) atoll(res.name.c_str()));
            } else if (res.token == "map") {
                pointer = new GXValue(GX_TAG_MAP, (void *) atol(res.name.c_str()));
            } else if (res.token == "array") {
                pointer = new GXValue(GX_TAG_ARRAY, (void *) atol(res.name.c_str()));
            } else if (res.token == "null") {
                pointer = new GXValue(GX_TAG_NULL, 1);
            }
            Res = (long) pointer;
        } else {
            Res = calculateCache(iter->second, arrayNum, this, source);
        }
    } else {
        Res = check(result, array, this, source, expression);
    }
    arrayNum.clear();
    array.clear();
    return Res;
}

/*
 * 计算缓存格式的表达式的方法
 */
long
GXAnalyze::calculateCache(string cacheString, vector<GXATSNode> array, void *p_analyze,
                          void *source) {
    long *paramsStack;
    int paramsSize = 0;
    bool isFunction = false;
    vector<long> paramsTempArray;
    bool hasNum2 = false;
    bool isNum1 = true;
    int num1 = -1;
    int num2 = -1;
    GXATSNode res;
    string op = "";
    for (int i = 0; i < cacheString.length(); i++) {
        if (cacheString[i] == '(') {
            continue;
        }
        if (cacheString[i] == ')') {
            if (op == "") {
                res = array[num1];
            } else if (hasNum2) {
                array[num2] = doubleCalculate(array[num1], array[num2], op);
                res = array[num2];
            } else {
                array[num1] = singleCalculate(array[num1], op);
                res = array[num1];
            }
            num1 = -1;
            num2 = -1;
            hasNum2 = false;
            isNum1 = true;
            op = "";
            continue;
        }
        if (isNumber(cacheString[i])) {
            if (isNum1) {
                if (num1 == -1) {
                    num1 = cacheString[i] - '0';
                } else {
                    num1 = num1 * 10 + (cacheString[i] - '0');
                }
            } else {
                hasNum2 = true;
                if (num2 == -1) {
                    num2 = cacheString[i] - '0';
                } else {
                    num2 = num2 * 10 + (cacheString[i] - '0');
                }
            }
        } else {
            if (cacheString[i] == ',') {
                //函数参数
                if (!isFunction) {
                    paramsStack = new long[array.size() + 2];
                    isFunction = true;
                }
                GXATSNode node = array[num1];
                if (node.token == "num") {
                    GXValue *par = new GXValue(GX_TAG_FLOAT, (float) atof(
                            node.name.c_str()));
                    paramsTempArray.push_back((long) par);
                } else if (node.token == "long") {
                    GXValue *par = new GXValue(GX_TAG_LONG, (int64_t) atoll(
                            node.name.c_str()));
                    paramsTempArray.push_back((long) par);
                } else if (node.token == "string") {
                    GXValue *par = new GXValue(GX_TAG_STRING,
                                               node.name.c_str());
                    paramsTempArray.push_back((long) par);
                } else if (node.token == "bool") {
                    if (node.name == "true") {
                        GXValue *par = new GXValue(GX_TAG_BOOL, 1);
                        paramsTempArray.push_back((long) par);
                    } else {
                        GXValue *par = new GXValue(GX_TAG_BOOL, 0);
                        paramsTempArray.push_back((long) par);
                    }
                } else if (node.token == "map") {
                    GXValue *par = new GXValue(GX_TAG_MAP, (void *) atol(
                            node.name.c_str()));
                    paramsTempArray.push_back((long) par);
                } else if (node.token == "array") {
                    GXValue *par = new GXValue(GX_TAG_ARRAY, (void *) atol(
                            node.name.c_str()));
                    paramsTempArray.push_back((long) par);
                } else if (node.token == "null") {
                    GXValue *par = new GXValue(GX_TAG_NULL, 1);
                    paramsTempArray.push_back((long) par);
                }
                num1 = -1;
            } else if (cacheString[i] == 'g') {
                //函数名
                if (cacheString[i - 1] != '(') {
                    if (!isFunction) {
                        paramsStack = new long[array.size() + 2];
                        isFunction = true;
                    }
                    GXATSNode node = array[num1];
                    num1 = -1;
                    if (node.token == "num") {
                        GXValue *par = new GXValue(GX_TAG_FLOAT, (float) atof(
                                node.name.c_str()));
                        paramsTempArray.push_back((long) par);
                    } else if (node.token == "long") {
                        GXValue *par = new GXValue(GX_TAG_LONG, (int64_t) atoll(
                                node.name.c_str()));
                        paramsTempArray.push_back((long) par);
                    } else if (node.token == "string") {
                        GXValue *par = new GXValue(GX_TAG_STRING,
                                                   node.name.c_str());
                        paramsTempArray.push_back((long) par);
                    } else if (node.token == "bool") {
                        if (node.name == "true") {
                            GXValue *par = new GXValue(GX_TAG_BOOL, 1);
                            paramsTempArray.push_back((long) par);
                        } else {
                            GXValue *par = new GXValue(GX_TAG_BOOL, 0);
                            paramsTempArray.push_back((long) par);
                        }
                    } else if (node.token == "map") {
                        GXValue *par = new GXValue(GX_TAG_MAP, (void *) atol(
                                node.name.c_str()));
                        paramsTempArray.push_back((long) par);
                    } else if (node.token == "array") {
                        GXValue *par = new GXValue(GX_TAG_ARRAY, (void *) atol(
                                node.name.c_str()));
                        paramsTempArray.push_back((long) par);
                    } else if (node.token == "null") {
                        GXValue *par = new GXValue(GX_TAG_NULL, 1);
                        paramsTempArray.push_back((long) par);
                    }
                }
                int numFunction = -1;
                for (int x = i + 1; x < cacheString.length(); x++) {
                    if (isNumber(cacheString[x])) {
                        if (numFunction == -1) {
                            numFunction = cacheString[x] - '0';
                        } else {
                            numFunction = numFunction * 10 + (cacheString[x] - '0');
                        }
                        i++;
                    } else {
                        i++;
                        break;
                    }
                }
                for (int paramsIndex = paramsTempArray.size() - 1;
                     paramsIndex >= 0; paramsIndex--) {
                    paramsStack[paramsSize] = paramsTempArray[paramsIndex];
                    ++paramsSize;
                }
                paramsTempArray.clear();
                GXATSNode node = array[numFunction];
                //有参数
                long funVal = this->getFunctionValue(array[numFunction].name,
                                                     paramsStack,
                                                     paramsSize, "");
                if (funVal == 0) {
                    node.name = "null";
                    node.token = "null";
                    array[numFunction] = node;
                    res = array[numFunction];
                } else {
                    GXValue *fun = (GXValue *) funVal;
                    //取出结果
                    if (fun->tag == GX_TAG_FLOAT) {
                        node.name = to_string(fun->float64);
                        if (node.name.find('.') != -1) {
                            node.name = regex_replace(node.name, regex("0+?$"),
                                                      ""); // 除了捕捉到的组以外，其他的东西均舍弃
                            node.name = regex_replace(node.name, regex("[.]$"),
                                                      ""); // 除了捕捉到的组以外，其他的东西均舍弃
                        }
                        node.token = "num";
                    } else if (fun->tag == GX_TAG_LONG) {
                        node.name = to_string(fun->intNum);
                        node.token = "long";
                    } else if (fun->tag == GX_TAG_BOOL) {
                        if (fun->int32 == 1) {
                            node.name = "true";
                            node.token = "bool";
                        } else {
                            node.name = "false";
                            node.token = "bool";
                        }
                    } else if (fun->tag == GX_TAG_STRING) {
                        node.name = fun->str;
                        node.token = "string";
                    } else if (fun->tag == GX_TAG_MAP) {
                        node.name = to_string((long) fun->ptr);
                        node.token = "map";
                    } else if (fun->tag == GX_TAG_ARRAY) {
                        node.name = to_string((long) fun->ptr);
                        node.token = "array";
                    } else if (fun->tag == GX_TAG_NULL) {
                        node.name = "null";
                        node.token = "null";
                    }
                    array[numFunction] = node;
                    res = array[numFunction];
                    if (fun != NULL && fun->tag == GX_TAG_STRING && fun->str != NULL) {
                        delete[] fun->str;
                        fun->str = NULL;
                    }
                    if (fun != NULL) {
                        delete fun;
                    }
                }
                if (isFunction) {
                    isFunction = false;
                    delete[] paramsStack;
                }
                paramsSize = 0;
            } else {
                //操作符
                isNum1 = false;
                op = op + cacheString[i];
                if (!isNumber(cacheString[i + 1]) && cacheString[i + 1] != ')') {
                    op = op + cacheString[i + 1];
                    i++;
                }
            }
        }
    }
    GXValue *pointer;
    if (res.token == "string") {
        pointer = new GXValue(GX_TAG_STRING, res.name);
    } else if (res.token == "bool") {
        if (res.name == "true") {
            pointer = new GXValue(GX_TAG_BOOL, 1);
        } else {
            pointer = new GXValue(GX_TAG_BOOL, 0);
        }
    } else if (res.token == "num") {
//        if (res.name.find('.') != -1) {
//            regex e("0+?$");
//            regex e2("[.]$");
//            res.name = regex_replace(res.name, e, "");
//            res.name = regex_replace(res.name, e2, "");
//        }
        pointer = new GXValue(GX_TAG_FLOAT, (float) atof(res.name.c_str()));
    } else if (res.token == "long") {
        pointer = new GXValue(GX_TAG_LONG, (int64_t) atoll(res.name.c_str()));
    } else if (res.token == "map") {
        pointer = new GXValue(GX_TAG_MAP, (void *) atol(res.name.c_str()));
    } else if (res.token == "array") {
        pointer = new GXValue(GX_TAG_ARRAY, (void *) atol(res.name.c_str()));
    } else if (res.token == "null") {
        pointer = new GXValue(GX_TAG_NULL, 1);
    }

    return (long) pointer;
}

/*
 * 语法分析并生成缓存表达式的过程
 */
long GXAnalyze::check(string s, vector<GXATSNode> array, void *p_analyze, void *source,
                      string expression) {
    string tree;
    if (array.size() == 1) {
        tree = "(0)";
    }
    GXValue *pointer;
    GXAnalyze *analyze = (GXAnalyze *) p_analyze;
    string temp = "\0"; //需要分析的语句
    string sentence = s + temp;
    string *statusStack = new string[sentence.size() + 2];    //状态栈
    int statusSize = 0;
    char *symbolStack = new char[sentence.size() + 2];        //符号栈
    int symbolSize = 0;
    GXATSNode *valueStack = new GXATSNode[sentence.size() + 2];
    int valueSize = 0;
    long *paramsStack = new long[sentence.size() + 2];
    int paramsSize = 0;
    int valueStep = 0; //数值数
    vector<long> paramsTempArray;
    bool isFunction = false;
    string valueType;
    symbolStack[symbolSize] = '#';
    ++symbolSize;
    sentence = sentence.substr(1);
    statusStack[statusSize] = "0";
    ++statusSize;
    string new_status;                             //下一入栈的新状态
    while (true) {
        string cur_status;//当前状态
        char cur_symbol;//当前“展望”字符
        cur_status = statusStack[statusSize - 1];
        cur_symbol = sentence[0];
        string m = cur_status + cur_symbol;
        //当前new_status,下一入栈的新状态
        new_status = gotoTable[m];
        if (new_status == "acc") {
            if (valueStack[0].token == "string") {
                pointer = new GXValue(GX_TAG_STRING, valueStack[0].name);
            } else if (valueStack[0].token == "bool") {
                if (valueStack[0].name == "true") {
                    pointer = new GXValue(GX_TAG_BOOL, 1);
                } else {
                    pointer = new GXValue(GX_TAG_BOOL, 0);
                }
            } else if (valueStack[0].token == "num") {
//                if (valueStack[0].name.find('.') != -1) {
//                    regex e("0+?$");
//                    regex e2("[.]$");
//                    valueStack[0].name = regex_replace(valueStack[0].name, e, "");
//                    valueStack[0].name = regex_replace(valueStack[0].name, e2, "");
//                }
                pointer = new GXValue(GX_TAG_FLOAT, (float) atof(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "long") {
                pointer = new GXValue(GX_TAG_LONG, (int64_t) atoll(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "map") {
                pointer = new GXValue(GX_TAG_MAP, (void *) atol(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "array") {
                pointer = new GXValue(GX_TAG_ARRAY, (void *) atol(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "null") {
                pointer = new GXValue(GX_TAG_NULL, 1);
            }
            delete[] statusStack;
            delete[] symbolStack;
            delete[] valueStack;
            delete[] paramsStack;
            mtx.lock();
            auto iterEnd = cache.find(s);
            if (iterEnd == cache.end()) {
                cache.insert(pair<string, string>{s, tree});
            }
            mtx.unlock();
            return (long) pointer;
        } else if (new_status[0] ==
                   's') {
            statusStack[statusSize] = new_status.substr(1);
            ++statusSize;
            // 1
            symbolStack[symbolSize] = cur_symbol; //读入一个字符
            ++symbolSize;
            string temp;
            auto ite = wordToSymbol.find(cur_symbol);
            if (ite != wordToSymbol.end()) {
                temp = ite->second;
            }
            if ((isTerminalWord(temp) &&
                 (temp == "true" || temp == "false" || temp == "null" || temp == "value" ||
                  temp == "num" || temp == "string" || temp == "data" || temp == "id" ||
                  temp == "function" || temp == "long")) ||
                (temp == "map" || temp == "array")) {
                // push value
                if (isTerminalWord(temp) && temp == "id") {
                    //接下来读入参数，(和)变为运算符
                    isFunction = true;
                }
                GXATSNode t1;
                if (temp == "value" || temp == "data") {
                    long res = analyze->getSourceValue(array[valueStep].name, source);
                    if (res == 0) {
                        t1.name = "null";
                        t1.token = "null";
                        t1.count = array[valueStep].count;
                        valueStack[valueSize] = t1;
                        ++valueSize;
                    } else {
                        GXValue *gxv = (GXValue *) res;
                        t1.count = array[valueStep].count;
                        if (gxv->tag == GX_TAG_FLOAT) {
                            t1.name = to_string(gxv->float64);
                            if (t1.name.find('.') != -1) {
                                t1.name = regex_replace(t1.name, regex("0+?$"),
                                                        ""); // 除了捕捉到的组以外，其他的东西均舍弃
                                t1.name = regex_replace(t1.name, regex("[.]$"),
                                                        ""); // 除了捕捉到的组以外，其他的东西均舍弃
                            }
                            t1.token = "num";
                        } else if (gxv->tag == GX_TAG_LONG) {
                            t1.name = to_string(gxv->intNum);
                            t1.token = "long";
                        } else if (gxv->tag == GX_TAG_STRING) {
                            t1.name = gxv->str;
                            t1.token = "string";
                        } else if (gxv->tag == GX_TAG_BOOL) {
                            if (gxv->int32 == 1) {
                                t1.name = "true";
                            } else {
                                t1.name = "false";
                            }
                            t1.token = "bool";
                        } else if (gxv->tag == GX_TAG_ARRAY) {
                            t1.name = to_string((long) (gxv->ptr));
                            t1.token = "array";
                        } else if (gxv->tag == GX_TAG_MAP) {
                            t1.name = to_string((long) (gxv->ptr));
                            t1.token = "map";
                        } else if (gxv->tag == GX_TAG_NULL) {
                            t1.name = "null";
                            t1.token = "null";
                        }
                        valueStack[valueSize] = t1;
                        ++valueSize;
                        if (gxv->tag == GX_TAG_STRING && gxv->str != NULL) {
                            delete[] gxv->str;
                            gxv->str = NULL;
                        }
                        delete gxv;
                    }
                } else if (temp == "function") {
                    long res = analyze->getFunctionValue(array[valueStep].name, nullptr, 0, "");
                    if (res == 0) {
                        t1.name = "null";
                        t1.token = "null";
                        t1.count = array[valueStep].count;
                        valueStack[valueSize] = t1;
                        ++valueSize;
                    } else {
                        GXValue *gxv = (GXValue *) res;
                        t1.count = array[valueStep].count;
                        if (gxv->tag == GX_TAG_FLOAT) {
                            t1.name = to_string(gxv->float64);
                            if (t1.name.find('.') != -1) {
                                t1.name = regex_replace(t1.name, regex("0+?$"),
                                                        ""); // 除了捕捉到的组以外，其他的东西均舍弃
                                t1.name = regex_replace(t1.name, regex("[.]$"),
                                                        ""); // 除了捕捉到的组以外，其他的东西均舍弃
                            }
                            t1.token = "num";
                        } else if (gxv->tag == GX_TAG_LONG) {
                            t1.name = to_string(gxv->intNum);
                            t1.token = "long";
                        } else if (gxv->tag == GX_TAG_STRING) {
                            t1.name = gxv->str;
                            t1.token = "string";
                        } else if (gxv->tag == GX_TAG_BOOL) {
                            if (gxv->int32 == 1) {
                                t1.name = "true";
                            } else {
                                t1.name = "false";
                            }
                            t1.token = "bool";
                        } else if (gxv->tag == GX_TAG_ARRAY) {
                            t1.name = to_string((long) (gxv->ptr));
                            t1.token = "array";
                        } else if (gxv->tag == GX_TAG_MAP) {
                            t1.name = to_string((long) (gxv->ptr));
                            t1.token = "map";
                        } else if (gxv->tag == GX_TAG_NULL) {
                            t1.name = "null";
                            t1.token = "null";
                        }
                        valueStack[valueSize] = t1;
                        ++valueSize;
                        if (gxv->tag == GX_TAG_STRING && gxv->str != NULL) {
                            delete[] gxv->str;
                            gxv->str = NULL;
                        }
                        delete gxv;
                    }
                } else {
                    valueStack[valueSize] = array[valueStep];
                    ++valueSize;
                }
            }
            valueStep = valueStep + 1;
            sentence = sentence.substr(1);
        } else if (new_status[0] ==
                   'r') {
            new_status = new_status.substr(1);
            int gid = atoi(new_status.c_str());
            int len = grammarProduct[gid].size() - 1;
            if (len == 1) {
                char reduced_symbol = grammarProduct[gid][0];
                string m = statusStack[statusSize - 2] + reduced_symbol;
                new_status = gotoTable[m];
                statusStack[statusSize - 1] = (new_status);
                symbolStack[symbolSize - 1] = (reduced_symbol);
            } else {
                string *action = new string[len];
                GXATSNode t1;
                GXATSNode t2;
                string op;
                bool changedT1 = false;
                bool changedT2 = false;
                GXATSNode tempR;
                GXATSNode tempR2;
                bool isChangedOp = false;
                char reduced_symbol = grammarProduct[gid][0];
                unordered_map<char, string>::iterator iterAction;
                for (int i = 0; i < len; i++) {
                    iterAction = wordToSymbol.find(symbolStack[symbolSize - 1]);
                    if (iterAction != wordToSymbol.end()) {
                        action[i] = iterAction->second;
                    }
//                    action[i] = wordToSymbol[symbolStack[symbolSize - 1]];
                    --statusSize;
                    --symbolSize;
                }
                for (int i = 0; i < len; i++) {
                    if ((isTerminalWord(action[i]) &&
                         !((action[i] == "true" || action[i] == "false" || action[i] == "null" ||
                            action[i] == "value" || action[i] == "num" || action[i] == "string" ||
                            action[i] == "long" ||
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
                delete[] action;
                if (len > 1) {
                    if (changedT2) {
                        if (valueSize < 2) {
                            delete[] statusStack;
                            delete[] symbolStack;
                            delete[] valueStack;
                            delete[] paramsStack;
                            analyze->throwError("expressionError: expression '" + expression +
                                                "' missing calculation element");
                            return 0L;
                        }
                        t2 = valueStack[valueSize - 1];
                        t1 = valueStack[valueSize - 2];
                        if (isFunction) {
                            if (op == "(") {
                                tempR = t1;
                                --valueSize;
                            } else if (op == ",") {
                                tempR = t1;
                                tempR2 = t2;
                                valueSize = valueSize - 2;
                            } else if (op == ")") {
                                int ParamsSize = valueSize - 1;
                                for (int i = valueSize - 1; i >= 0; i--) {
                                    if (i == ParamsSize) {
                                        tree = tree + '(';
                                    }
                                    if (valueStack[i].token == "id") {
                                        //需要翻转一遍，否则取到的参数顺序是反过来的
                                        for (int paramsIndex = paramsTempArray.size() - 1;
                                             paramsIndex >= 0; paramsIndex--) {
                                            paramsStack[paramsSize] = paramsTempArray[paramsIndex];
                                            ++paramsSize;
                                        }
                                        paramsTempArray.clear();
                                        //这里特别注意，g是函数的识别符，需要和运算符做区分
                                        tree = tree + "g" + to_string(valueStack[i].count) + ")";
                                        //在这里调用获取函数结果方法
                                        long funVal = analyze->getFunctionValue(valueStack[i].name,
                                                                                paramsStack,
                                                                                paramsSize, "");
                                        if (funVal == 0) {
                                            tempR.name = "null";
                                            tempR.token = "null";
                                            tempR.count = valueStack[i].count;
                                            --valueSize;
                                            isFunction = false;
                                            paramsSize = 0;
                                            break;
                                        }
                                        GXValue *fun = (GXValue *) funVal;
                                        tempR.count = valueStack[i].count;
                                        //取出结果
                                        if (fun->tag == GX_TAG_FLOAT) {
                                            tempR.name = to_string(fun->float64);
                                            if (tempR.name.find('.') != -1) {
                                                tempR.name = regex_replace(tempR.name,
                                                                           regex("0+?$"),
                                                                           ""); // 除了捕捉到的组以外，其他的东西均舍弃
                                                tempR.name = regex_replace(tempR.name,
                                                                           regex("[.]$"),
                                                                           ""); // 除了捕捉到的组以外，其他的东西均舍弃
                                            }
                                            tempR.token = "num";
                                        } else if (fun->tag == GX_TAG_LONG) {
                                            tempR.name = to_string(fun->intNum);
                                            tempR.token = "long";
                                        } else if (fun->tag == GX_TAG_BOOL) {
                                            if (fun->int32 == 1) {
                                                tempR.name = "true";
                                                tempR.token = "bool";
                                            } else {
                                                tempR.name = "false";
                                                tempR.token = "bool";
                                            }
                                        } else if (fun->tag == GX_TAG_STRING) {
                                            tempR.name = fun->str;
                                            tempR.token = "string";
                                        } else if (fun->tag == GX_TAG_MAP) {
                                            tempR.name = to_string((long) fun->ptr);
                                            tempR.token = "map";
                                        } else if (fun->tag == GX_TAG_ARRAY) {
                                            tempR.name = to_string((long) fun->ptr);
                                            tempR.token = "array";
                                        } else if (fun->tag == GX_TAG_NULL) {
                                            tempR.name = "null";
                                            tempR.token = "null";
                                        }
                                        --valueSize;
                                        isFunction = false;
                                        paramsSize = 0;
                                        if (fun->tag == GX_TAG_STRING && fun->str != NULL) {
                                            delete[] fun->str;
                                            fun->str = NULL;
                                        }
                                        delete fun;
                                        break;
                                    } else {
                                        if (valueStack[i - 1].token != "id") {
                                            tree = tree + to_string(valueStack[i].count) + ",";
                                        } else {
                                            tree = tree + to_string(valueStack[i].count);
                                        }
                                        //往vector<GXValue>逐个扔进去参数，然后通过id调用
                                        if (valueStack[i].token == "num") {
                                            GXValue *par = new GXValue(GX_TAG_FLOAT, (float) atof(
                                                    valueStack[i].name.c_str()));
                                            paramsTempArray.push_back((long) par);
                                        } else if (valueStack[i].token == "long") {
                                            GXValue *par = new GXValue(GX_TAG_LONG,
                                                                       (int64_t) atoll(
                                                                               valueStack[i].name.c_str()));
                                            paramsTempArray.push_back((long) par);
                                        } else if (valueStack[i].token == "string") {
                                            GXValue *par = new GXValue(GX_TAG_STRING,
                                                                       valueStack[i].name.c_str());
                                            paramsTempArray.push_back((long) par);
                                        } else if (valueStack[i].token == "bool") {
                                            if (valueStack[i].name == "true") {
                                                GXValue *par = new GXValue(GX_TAG_BOOL, 1);
                                                paramsTempArray.push_back((long) par);
                                            } else {
                                                GXValue *par = new GXValue(GX_TAG_BOOL, 0);
                                                paramsTempArray.push_back((long) par);
                                            }
                                        } else if (valueStack[i].token == "map") {
                                            GXValue *par = new GXValue(GX_TAG_MAP, (void *) atol(
                                                    valueStack[i].name.c_str()));
                                            paramsTempArray.push_back((long) par);
                                        } else if (valueStack[i].token == "array") {
                                            GXValue *par = new GXValue(GX_TAG_ARRAY, (void *) atol(
                                                    valueStack[i].name.c_str()));
                                            paramsTempArray.push_back((long) par);
                                        } else if (valueStack[i].token == "null") {
                                            GXValue *par = new GXValue(GX_TAG_NULL, 1);
                                            paramsTempArray.push_back((long) par);
                                        }
                                        --valueSize;
                                    }
                                }
                            } else {
                                delete[] statusStack;
                                delete[] symbolStack;
                                delete[] valueStack;
                                delete[] paramsStack;
                                analyze->throwError("expressionError: wrong function expression");
                                return 0L;
                            }
                        } else {
                            valueSize = valueSize - 2;
                            tempR = doubleCalculate(t1, t2, op);
                            tempR.count = t2.count;
                            tree = tree + '(' + to_string(t1.count) + op + to_string(t2.count) +
                                   ')';
                            if (tempR.token == "error") {
                                delete[] statusStack;
                                delete[] symbolStack;
                                delete[] valueStack;
                                delete[] paramsStack;
                                analyze->throwError(tempR.name);
                                return 0L;
                            }
                        }
                    } else {
                        if (valueSize < 1) {
                            delete[] statusStack;
                            delete[] symbolStack;
                            delete[] valueStack;
                            delete[] paramsStack;
                            analyze->throwError(
                                    "expressionError: expression has 0 value after operator, but must have 1 value");
                            return 0L;
                        }
                        t1 = valueStack[valueSize - 1];
                        --valueSize;
                        tempR = singleCalculate(t1, op);
                        tempR.count = t1.count;
                        if (op != ")" && op != ",") {
                            tree = tree + '(' + to_string(t1.count) + op + ')';
                        }
                        if (tempR.token == "error") {
                            analyze->throwError(tempR.name);
                            return 0L;
                        }
                    }
                    if (isFunction && op == ",") {
                        valueStack[valueSize] = tempR;
                        valueStack[valueSize + 1] = tempR2;
                        valueSize = valueSize + 2;
                    } else {
                        valueStack[valueSize] = tempR;
                        ++valueSize;
                    }
                }
                string m = statusStack[statusSize - 1] + reduced_symbol;
                new_status = gotoTable[m];
                statusStack[statusSize] = new_status;
                ++statusSize;
                symbolStack[symbolSize] = reduced_symbol;
                ++symbolSize;
            }
        } else {
            if (valueStep <= 0) {
                valueStep = 1;
            }
            string value = array[valueStep - 1].name;
            delete[] statusStack;
            delete[] symbolStack;
            delete[] valueStack;
            delete[] paramsStack;
            analyze->throwError(
                    "expressionError: unexpected identifier '" + value + "' in expression '" +
                    expression + "'");
            return 0L;
        }
    }
}


