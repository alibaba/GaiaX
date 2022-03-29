#include "GXBeforeTree.h"

using namespace std;
static vector<vector<char> > G;              //文法G[S]产生式 ，~为空字
static unordered_map<char, set<char> > ts;   //终结符(char)terminal symbol,及它的first集合(set<char>)
static unordered_map<char, set<char> > nts;  //非终结符(char)non-terminal symbol，及它的first集合(set<char>)
static unordered_map<string, string> tableT;
static bool isInit = false;
static string tString[] = {"true", "false", "null", "value", "num", "string", "data", "id", ",",
                           "(",
                           ")",
                           "!", "-", "+", "%", "/", "*", ">", "<", ">=", "<=", "==", "!=", "&&",
                           "||",
                           "?",
                           ":", "?:", "error", "#", "~"};
static char tSymbol[] = {'t', 'f', 'n', 'v', 'u', 's', 'd', 'i', ',', '(', ')', '!', '-', '+', '%',
                         '/',
                         '*', '>', '<', 'l', 'b', '=', 'p', '&', '@', '?', ':', 'y', 'e', '#', '~'};
static string ntString[] = {"S", "Ten", "Len", "Nin", "Eig", "Sev", "Six", "Fiv", "Fou", "Thr",
                            "Two",
                            "Parms",
                            "One"};
static char ntSymbol[] = {'S', 'T', 'L', 'N', 'E', 'D', 'F', 'G', 'H', 'U', 'Y', 'P', 'O'};
static string grammar[] = {"S->T",
                           "T->TyN|L:N|N",
                           "L->N?N",
                           "N->N@E|E",
                           "E->E&D|D",
                           "D->DpF|D=F|F",
                           "F->F>G|F<G|FlG|FbG|G",
                           "G->G+H|G-H|H",
                           "H->H*U|H/U|H%U|U",
                           "U->+Y|-Y|!Y|Y",
                           "Y->(T)|i(P)|O|~",
                           "P->O,P|O",
                           "O->t|f|n|v|u|s|d"};
static unordered_map<string, char> Vt;     //终结符集合
static unordered_map<string, char> Vn;     //非终结符集合
static vector<string> productions;
struct CLOSURE {                                  //闭包CLOSURE
    vector<vector<char> > project; //项目集
    vector<set<char> > outlook;    //展望串
    unordered_map<char, int> go;   // GO函数
};
static vector<CLOSURE> cloArray;

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

char change_Word(string s) {
    if (is_terminal_char(s)) {
        return Vt[s];
    } else {
        return Vn[s];
    }
}

vector<char> get_G_Vector(int gid) {
    return G[gid];
}

char get_G_Char(int gid, int num) {
    return G[gid][num];
}

string get_Table_By_String(string param) {
    return tableT[param];
}

void init() {
    if (isInit == false) {
        read_G();
        get_First();
        get_Closure();
        init_Terminal();
        get_Table();
        init_All();
        isInit = true;
    }
};