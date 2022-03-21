#ifndef _BEFORETREE__H_
#define _BEFORETREE__H_

#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <map>
#include <set>
#include <cstring>
#include <unordered_map>
#include "GXATSNode.h"
#include "GXDataWarehouse.h"

using namespace std;

void read_G();

void get_First();

void get_Closure();

int get_Table();

void init();

void init_Terminal();

bool is_terminal_char(const string &s);

char change_Word(string s);

string get_Table_By_Map(map<string, char> map);

vector<char> get_G_Char_Array(int num);

char get_G_Symbol(int num1, int num2);

#endif /*include _BEFORETREE__H_*/
