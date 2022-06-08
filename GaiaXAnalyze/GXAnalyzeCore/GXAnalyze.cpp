#include "GXAnalyze.h"
#include <time.h>

using namespace std;
static vector<vector<char> > G;              //文法G[S]产生式 ，~为空字
static unordered_map<char, set<char> > ts;   //终结符(char)terminal symbol,及它的first集合(set<char>)
static unordered_map<char, set<char> > nts;  //非终结符(char)non-terminal symbol，及它的first集合(set<char>)
static unordered_map<string, string> tableT;
static bool isInit = false;
static string tString[] = {"true", "false", "null", "value", "num", "string", "data", "id", ",",
                           "(", ")", "!", "-", "+", "%", "/", "*", ">", "<", ">=", "<=", "==", "!=",
                           "&&", "||", "?", ":", "?:", "error", "#", "~"};
static char tSymbol[] = {'t', 'f', 'n', 'v', 'u', 's', 'd', 'i', ',', '(', ')', '!', '-', '+', '%',
                         '/', '*', '>', '<', 'l', 'b', '=', 'p', '&', '@', '?', ':', 'y', 'e', '#',
                         '~'};
static string ntString[] = {"S", "Ten", "L", "Nin", "Eig", "Sev", "Six", "Fiv", "Fou", "Thr", "Two",
                            "P", "One"};
static char ntSymbol[] = {'S', 'T', 'L', 'N', 'E', 'D', 'F', 'G', 'H', 'U', 'Y', 'P', 'O'};
static string grammar[] = {"S->T", "T->TyN|L:N|N", "L->N?N", "N->N@E|E", "E->E&D|D", "D->DpF|D=F|F",
                           "F->F>G|F<G|FlG|FbG|G", "G->G+H|G-H|H", "H->H*U|H/U|H%U|U",
                           "U->+Y|-Y|!Y|Y", "Y->(T)|i(P)|O|~", "P->O,P|O", "O->t|f|n|v|u|s|d"};
static unordered_map<string, char> Vt;     //终结符集合
static unordered_map<string, char> Vn;     //非终结符集合
static vector<string> productions;
struct CLOSURE {                                  //闭包CLOSURE
    vector<vector<char> > project; //项目集
    vector<set<char> > outlook;    //展望串
    unordered_map<char, int> go;   // GO函数
};
static vector<CLOSURE> cloArray;
static unordered_map<char, string> CtoS;     //Char to String
static unordered_map<string, char> StoC;     //String to Char

//初始化终结符和非终结符
void init_SAndC() {
    int sizeC = sizeof(tString) / sizeof(tString[0]);
    int sizeU = sizeof(ntString) / sizeof(ntString[0]);
    set<char> m;
    //终结符
    for (int i = 0; i < sizeC; i++) {
        CtoS.insert(pair<char, string>(tSymbol[i], tString[i]));
        StoC.insert(pair<string, char>(tString[i], tSymbol[i]));

    }
    for (int i = 0; i < sizeU; i++) {
        CtoS.insert(pair<char, string>(ntSymbol[i], ntString[i]));
        StoC.insert(pair<string, char>(ntString[i], ntSymbol[i]));
    }
}

void read_G() {
    int sizeG = sizeof(grammar) / sizeof(grammar[0]);
    for (int i = 0; i < sizeG; i++) {
        productions.push_back(grammar[i]);
    }
    char symbol;
    int i = 0;
    vector<char> value;
    char chX;
    set<char> m;
    nts['M'] = m;
    for (int x = 0; x < productions.size(); x++) {
        string temp = productions[x];
        for (int y = 0; y < temp.length(); y++) {
            symbol = temp[y];
            if (symbol != ' ' || symbol != '\t') {
                if (symbol == '|') {
                    G.push_back(value);
                    value.clear();
                    i = 3;
                    value.push_back(chX);
                    continue;
                }
                i++;
                if (i == 1) {
                    chX = symbol;
                    nts[symbol] = m;
                } else if (i != 2 && i != 3 && symbol != '~')
                    ts[symbol] = m;
                if (i != 2 && i != 3)
                    value.push_back(symbol);
            }
            if (y == temp.length() - 1) {
                if (!value.empty()) {
                    G.push_back(value);
                }
                value.clear();
                i = 0;
                continue;
            }
        }
    }
    if (G.empty()) {
        exit(0);
    }
    value.clear();
    value.push_back('M');
    value.push_back(G[0][0]);
    G.insert(G.begin(), value);

    //去掉ts中的非终结符
    for (unordered_map<char, set<char> >::iterator it = nts.begin(); it != nts.end(); it++) {
        unordered_map<char, set<char> >::iterator iter;
        iter = ts.find(it->first);
        if (iter != ts.end())
            ts.erase(iter);
    }
}

bool is_terminal_char(const string &s) {
    //在map中判断
    if (Vt.count(s) > 0) {
        return true;
    }
    if (s == "M") {
        return true;
    }
    return false;
}

void get_First() { //得到First集合
    for (auto &it : ts)
        it.second.insert(it.first);

    //求非终结符的First集合
    int r = 0;
    int change = 1;
    while (change) {
        if (r == 20)
            break;
        r++;
        change = 0;
        for (auto &it : nts) {
            for (unsigned int i = 0; i < G.size(); i++) {
                if (G[i][0] == it.first) {
                    unsigned int size = it.second.size();
                    unordered_map<char, set<char> >::iterator iter = ts.find(G[i][1]);
                    if (ts.find(G[i][1]) != ts.end() ||
                        G[i][1] == '~') {
                        it.second.insert(G[i][1]);
                        if (it.second.size() > size)
                            change = 1;
                    } else {
                        unsigned int col = 1;
                        while (1) {
                            int flag = 0;
                            unordered_map<char, set<char> >::iterator itt = nts.find(G[i][col]);
                            for (auto &iter : itt->second) {
                                if (iter == '~')
                                    flag = 1;
                                else
                                    it.second.insert(iter);
                            }
                            if (flag) {
                                col++;
                                if (G[i].size() <= col) {
                                    it.second.insert('~');
                                    break;
                                } else if (ts.find(G[i][col]) !=
                                           ts.end()) {
                                    it.second.insert(G[i][col]);
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

void get_Closure() {
    int i = 0;
    CLOSURE clo;
    cloArray.push_back(clo);
    while (1) {
        if (i == cloArray.size())
            break;
        if (i == 0) {
            vector<char> vec(G[0]);
            vec.insert(vec.begin() + 1, ' ');
            cloArray[i].project.push_back(vec);
            set<char> m;
            m.insert('#');
            cloArray[i].outlook.push_back(m);
        }
        for (unsigned int j = 0; j < cloArray[i].project.size(); j++) {
            for (unsigned int k = 0; k < cloArray[i].project[j].size(); k++) {
                if (cloArray[i].project[j][k] == ' ') {
                    if (k == cloArray[i].project[j].size() - 1)
                        break;
                    for (unsigned int x = 0;
                         x < G.size(); x++) {
                        if (G[x][0] ==
                            cloArray[i].project[j][k + 1]) {
                            vector<char> vec(G[x]);
                            vec.insert(vec.begin() + 1, ' ');
                            int exist = 0;
                            for (unsigned int y = 0;
                                 y < cloArray[i].project.size(); y++) {
                                if (cloArray[i].project[y] == vec) {
                                    exist = y;
                                    break;
                                }
                            }
                            if (exist == 0) {
                                cloArray[i].project.push_back(vec);
                            }
                            set<char> m;
                            bool emp = true;    //判空
                            int t = 0;
                            while (emp) {
                                emp = false;
                                if (k + t + 1 == cloArray[i].project[j].size() - 1) { //情况一
                                    for (auto it : cloArray[i].outlook[j])
                                        m.insert(it);
                                } else if (ts.find(cloArray[i].project[j][k + t + 2]) !=
                                           ts.end()) { //情况二
                                    m.insert(cloArray[i].project[j][k + 2 + t]);
                                } else {
                                    set<char> m1(
                                            (nts.find(cloArray[i].project[j][k + 2 + t]))->second);
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
                                    cloArray[i].outlook[exist].insert(it);
                                }
                            } else
                                cloArray[i].outlook.push_back(m);
                        }
                    }
                    break;
                }
            }
        }
        for (unsigned int j = 0; j < cloArray[i].project.size(); j++) {
            for (unsigned int k = 0; k < cloArray[i].project[j].size(); k++) {
                if (cloArray[i].project[j][k] == ' ') {
                    if (k == cloArray[i].project[j].size() - 1)
                        break;
                    vector<char> new_closure_pro(cloArray[i].project[j]);
                    new_closure_pro[k] = new_closure_pro[k + 1];
                    new_closure_pro[k + 1] = ' ';
                    set<char> new_closure_search(cloArray[i].outlook[j]);
                    bool dif = false;
                    for (unsigned int x = 0; x < cloArray.size(); x++) {
                        // dif = false;
                        for (unsigned int y = 0; y <
                                                 cloArray[x].project.size(); y++) {
                            dif = false;
                            if (new_closure_pro == cloArray[x].project[y]) {
                                if (cloArray[x].outlook[0].size() != new_closure_search.size()) {
                                    dif = true;
                                    continue;
                                }
                                auto iter = cloArray[x].outlook[0].begin();
                                for (auto it : new_closure_search) {
                                    if (it != *iter) {
                                        dif = true;
                                        break;
                                    }
                                    iter++;
                                }
                                if (dif == false) {
                                    cloArray[i].go[new_closure_pro[k]] = x;
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
                    if (cloArray[i].go.count(new_closure_pro[k]) != 0 &&
                        dif) {
                        cloArray[cloArray[i].go[new_closure_pro[k]]].project.push_back(
                                new_closure_pro);
                        cloArray[cloArray[i].go[new_closure_pro[k]]].outlook.push_back(
                                new_closure_search);
                        break;
                    }
                    if (dif) {
                        CLOSURE new_closure;
                        new_closure.project.push_back(new_closure_pro);
                        new_closure.outlook.push_back(new_closure_search);
                        cloArray.push_back(new_closure);
                        cloArray[i].go[new_closure_pro[k]] = cloArray.size() - 1;
                    }
                }
            }
        }
        i++;
    }
}

int get_Table() {
    for (unsigned int i = 0; i < cloArray.size(); i++) {
        for (unsigned int j = 0; j < cloArray[i].project.size(); j++) {
            for (unsigned int k = 0; k < cloArray[i].project[j].size(); k++) {
                if (cloArray[i].project[j][k] == ' ') {
                    if (k == cloArray[i].project[j].size() - 1) {
                        if (cloArray[i].project[j][0] == 'M') {
                            string m = to_string(i) + '#';
                            if (tableT.find(m) != tableT.end() && tableT[m] != "acc") {
                                return 0;
                            } else
                                tableT[m] = "acc";
                        } else {
                            int id;
                            for (unsigned int x = 0; x < G.size(); x++) {
                                vector<char> vec(cloArray[i].project[j]);
                                vec.pop_back();
                                if (G[x] == vec) {
                                    id = x;
                                    break;
                                }
                            }
                            for (auto it : cloArray[i].outlook[j]) {
                                string m = to_string(i) + it;
                                if (tableT.find(m) != tableT.end() &&
                                    tableT[m] != (string) "r" + to_string(id)) {
                                    return 0;
                                } else
                                    tableT[m] = (string) "r" + to_string(id);
                            }
                        }
                    } else {
                        char next = cloArray[i].project[j][k + 1];
                        if (ts.find(next) != ts.end()) {
                            string m = to_string(i) + next;
                            if (tableT.find(m) != tableT.end() &&
                                tableT[m] != (string) "s" + to_string(cloArray[i].go[next])) {
                                return 0;
                            } else
                                tableT[m] = (string) "s" + to_string(cloArray[i].go[next]);
                        } else {
                            string m = to_string(i) + next;
                            if (tableT.find(m) != tableT.end() &&
                                tableT[m] != to_string(cloArray[i].go[next])) {
                                return 0;
                            } else
                                tableT[m] = to_string(cloArray[i].go[next]);
                        }
                    }
                    break;
                }
            }
        }
    }
    return 1;
}

//初始化终结符和非终结符
void init_Terminal() {
    int sizeC = sizeof(tString) / sizeof(tString[0]);
    int sizeU = sizeof(ntString) / sizeof(ntString[0]);
    set<char> m;
    //终结符
    for (int i = 0; i < sizeC; i++) {
        Vt.insert(pair<string, char>(tString[i], tSymbol[i]));
        ts[tSymbol[i]] = m;
    }
    for (int i = 0; i < sizeU; i++) {
        Vn.insert(pair<string, char>(ntString[i], ntSymbol[i]));
        nts[ntSymbol[i]] = m;
    }
}

void init() {
    if (isInit == false) {
        isInit = true;
        read_G();
        get_First();
        get_Closure();
        init_Terminal();
        get_Table();
        init_SAndC();
    }
}

GXAnalyze::GXAnalyze() {
    init();
}

GXAnalyze::~GXAnalyze() {
}

//获取两个数值计算的结果
GXATSNode GXAnalyze::doubleCalculate(GXATSNode left, GXATSNode right, string op) {
    GXATSNode result = GXATSNode(left.name, left.token, left.token);
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
            if (left.token == "num" && right.token == "string") {
                if(left.name.find('.')){
                    regex e("0+?$");
                    regex e2("[.]$");
                    left.name = regex_replace(left.name, e, ""); // 除了捕捉到的组以外，其他的东西均舍弃
                    left.name = regex_replace(left.name, e2, ""); // 除了捕捉到的组以外，其他的东西均舍弃
                }
                result.name = left.name + right.name;
                result.token = "string";
            } else if (right.token == "num" && left.token == "string") {
                if(right.name.find('.')){
                    regex e("0+?$");
                    regex e2("[.]$");
                    right.name = regex_replace(right.name, e, ""); // 除了捕捉到的组以外，其他的东西均舍弃
                    right.name = regex_replace(right.name, e2, ""); // 除了捕捉到的组以外，其他的东西均舍弃
                }
                result.name = left.name + right.name;
                result.token = "string";
            } else {
                result.token = "error";
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
    GXATSNode result = GXATSNode(left.name, left.token, left.token);
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

long GXAnalyze::getValue(string expression, void *source) {
    char *input;
    int inputLength = expression.length();
    input = new char[inputLength + 2];
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
            if (is_terminal_char(token.detail)) {
                result = result + Vt[token.detail];
            } else {
                result = result + Vn[token.detail];
            }
            array.push_back(token);
        }
    }
    //释放s的内存空间
    delete[]input;
    result = result + "#";
    long Res = check(result, array, this, source);
    array.clear();
    return Res;
}

long GXAnalyze::check(string s, vector<GXATSNode> array, void *p_analyze, void *source) {
    static GXValue pointer;
    GXAnalyze *analyze = (GXAnalyze *) p_analyze;
    string temp = "\0"; //需要分析的语句
    string sentence = s + temp;
    string *statusStack = new string[sentence.size() + 2];    //状态栈
    int statusSize = 0;
    char *symbolStack = new char[sentence.size() + 2];        //符号栈
    int symbolSize = 0;
    GXATSNode *valueStack = new GXATSNode[sentence.size() + 2];
    int valueSize = 0;
    GXValue *paramsStack = new GXValue[sentence.size() + 2];
    int paramsSize = 0;
    int valueStep = 0; //数值数
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
        new_status = tableT[m];
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
                if(valueStack[0].name.find('.')){
                    regex e("0+?$");
                    regex e2("[.]$");
                    valueStack[0].name = regex_replace(valueStack[0].name, e, ""); // 除了捕捉到的组以外，其他的东西均舍弃
                    valueStack[0].name = regex_replace(valueStack[0].name, e2, ""); // 除了捕捉到的组以外，其他的东西均舍弃
                }
                pointer = GX_NewFloat64(atof(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "map") {
                pointer = GX_NewMap((void *) atol(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "array") {
                pointer = GX_NewArray((void *) atol(valueStack[0].name.c_str()));
            } else if (valueStack[0].token == "null") {
                pointer = GX_NewNull(1);
            }
            delete[] statusStack;
            delete[] symbolStack;
            delete[] valueStack;
            delete[] paramsStack;
            return (long) (&pointer);
        } else if (new_status[0] ==
                   's') {
            statusStack[statusSize] = new_status.substr(1);
            ++statusSize;
            // 1
            symbolStack[symbolSize] = cur_symbol; //读入一个字符
            ++symbolSize;
            string temp = CtoS[cur_symbol];
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
                    valueStack[valueSize] = t1;
                    ++valueSize;
                    if (gxv->tag == GX_TAG_STRING && gxv->u.str != NULL) {
                        delete[] gxv->u.str;
                        gxv->u.str = NULL;
                    }
                    free(gxv);
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
            int len = G[gid].size() - 1;
            if (len == 1) {
                char reduced_symbol = G[gid][0];
                string m = statusStack[statusSize - 2] + reduced_symbol;
                new_status = tableT[m];
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
                char reduced_symbol = G[gid][0];
                for (int i = 0; i < len; i++) {
                    action[i] = CtoS[symbolStack[symbolSize - 1]];
                    --statusSize;
                    --symbolSize;
                }
                for (int i = 0; i < len; i++) {
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
                delete[] action;
                if (len > 1) {
                    if (changedT2) {
                        if (valueSize < 2) {
                            delete[] statusStack;
                            delete[] symbolStack;
                            delete[] valueStack;
                            delete[] paramsStack;
                            analyze->throwError("expression error");
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
                                        delete[] params;
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
                                        --valueSize;
                                        isFunction = false;
                                        if (fun->tag == GX_TAG_STRING && fun->u.str != NULL) {
                                            delete[] fun->u.str;
                                            fun->u.str = NULL;
                                        }
                                        free(fun);
                                        break;
                                    } else {
                                        //往vector<GXValue>逐个扔进去参数，然后通过id调用
                                        if (valueStack[i].token == "num") {
                                            paramsStack[paramsSize] =
                                                    GX_NewFloat64(
                                                            atof(valueStack[i].name.c_str()));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "string") {
                                            paramsStack[paramsSize] =
                                                    GX_NewGXString(valueStack[i].name.c_str());
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "bool") {
                                            if (valueStack[i].name == "true") {
                                                paramsStack[paramsSize] = GX_NewBool(1);
                                                ++paramsSize;
                                            } else {
                                                paramsStack[paramsSize] = GX_NewBool(0);
                                                ++paramsSize;
                                            }
                                        } else if (valueStack[i].token == "map") {
                                            paramsStack[paramsSize] = GX_NewMap(
                                                    (void *) atol(valueStack[i].name.c_str()));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "array") {
                                            paramsStack[paramsSize] = GX_NewArray(
                                                    (void *) atol(valueStack[i].name.c_str()));
                                            ++paramsSize;
                                        } else if (valueStack[i].token == "null") {
                                            paramsStack[paramsSize] = GX_NewNull(1);
                                            ++paramsSize;
                                        }
                                        --valueSize;
                                    }
                                }
                            } else {
                                delete[] statusStack;
                                delete[] symbolStack;
                                delete[] valueStack;
                                delete[] paramsStack;
                                analyze->throwError("expression error");
                                return 0L;
                            }
                        } else {
                            valueSize = valueSize - 2;
                            tempR = doubleCalculate(t1, t2, op);
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
                            analyze->throwError("expression error");
                            return 0L;
                        }
                        t1 = valueStack[valueSize - 1];
                        --valueSize;
                        tempR = singleCalculate(t1, op);
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
                new_status = tableT[m];
                statusStack[statusSize] = new_status;
                ++statusSize;
                symbolStack[symbolSize] = reduced_symbol;
                ++symbolSize;
            }
        } else {
            delete[] statusStack;
            delete[] symbolStack;
            delete[] valueStack;
            delete[] paramsStack;
            analyze->throwError("expression error");
            return 0L;
        }
    }
}
