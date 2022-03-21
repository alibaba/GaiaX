#include "GXDataWarehouse.h"

static string CharToNum_Char[] = {"true", "false", "null", "value", "num", "string", "data", "id",
                                  ",",
                                  "(", ")", "!", "-", "+", "%", "/", "*", ">", "<", ">=", "<=",
                                  "==", "!=",
                                  "&&", "||", "?", ":", "?:", "error", "#", "~"};
static int CharToNum_Code[] = {1, 2, 3, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
                               25, 26,
                               27, 28, 29, 30, 31, 32, 33, 34, 0, 35, 36};
static map<int, string> CharToWord;     //终结符-数值集合   格式：{"true",1}
static int sizeCharToNum;  //终结符-数值集合数组Size

static string StringToChar_S[] = {"true", "false", "null", "value", "num", "string", "data", "id", ",", "(",
                                  ")",
                                  "!", "-", "+", "%", "/", "*", ">", "<", ">=", "<=", "==", "!=", "&&",
                                  "||", "?",
                                  ":", "?:", "error", "#", "~"};
static char StringToChar_C[] = {'t', 'f', 'n', 'v', 'u', 's', 'd', 'i', ',', '(', ')', '!', '-', '+', '%',
                                '/',
                                '*', '>', '<', 'l', 'b', '=', 'p', '&', '@', '?', ':', 'y', 'e', '#',
                                '~'};
static string nt_To_Symbol_S[] = {"S", "Ten", "L", "Nin", "Eig", "Sev", "Six", "Fiv", "Fou", "Thr", "Two", "P",
                                  "One"};
static char nt_To_Symbol_C[] = {'S', 'T', 'L', 'N', 'E', 'D', 'F', 'G', 'H', 'U', 'Y', 'P', 'O'};
static map<char, string> CtoS;     //Char to String
static map<string, char> StoC;     //String to Char

//初始化终结符和非终结符
void init_SAndC() {
    int sizeC = sizeof(StringToChar_S) / sizeof(StringToChar_S[0]);
    int sizeU = sizeof(nt_To_Symbol_S) / sizeof(nt_To_Symbol_S[0]);
    set<char> m;
    //终结符
    for (int i = 0; i < sizeC; i++) {
        CtoS.insert(pair<char, string>(StringToChar_C[i], StringToChar_S[i]));
        StoC.insert(pair<string, char>(StringToChar_S[i], StringToChar_C[i]));

    }
    for (int i = 0; i < sizeU; i++) {
        CtoS.insert(pair<char, string>(nt_To_Symbol_C[i], nt_To_Symbol_S[i]));
        StoC.insert(pair<string, char>(nt_To_Symbol_S[i], nt_To_Symbol_C[i]));
    }
}

string get_S_By_C(char c) {
    return CtoS[c];
}

void init_All() {
    init_Char_To_Num();
    init_SAndC();
}

//初始化终结符-数值集合
void init_Char_To_Num() {
    sizeCharToNum = sizeof(CharToNum_Char) / sizeof(CharToNum_Char[0]);
    //终结符
    for (int i = 0; i < sizeCharToNum; i++) {
        CharToWord.insert(pair<int, string>(CharToNum_Code[i], CharToNum_Char[i]));
    }
}

//通过code获取对应string
string get_Word_By_Code(int code) {
    return CharToWord[code];
}
