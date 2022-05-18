#ifndef _ATSNODE__H_
#define _ATSNODE__H_

#include <string>

using namespace std;

class GXATSNode {
public:
    GXATSNode();                                   //无参构造函数
    GXATSNode(string name, string detail, string token); //有参构造函数
    ~GXATSNode();                                  //析构函数
    GXATSNode &operator=(const GXATSNode &node);

    string name;  //字段
    string token; //类型
    string detail;

private:
};

#endif /*include _ATSNODE__H_*/
