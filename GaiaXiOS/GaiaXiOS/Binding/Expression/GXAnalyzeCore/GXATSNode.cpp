#include "GXATSNode.h"
#include <iostream>
#include <string>

using namespace std;

GXATSNode::GXATSNode() {
}

GXATSNode::GXATSNode(string name, int syn, string token) {
    GXATSNode::name = name;   //字段
    GXATSNode::syn = syn;     //code
    GXATSNode::token = token; //类型
}

GXATSNode::~GXATSNode() {

}

GXATSNode &GXATSNode::operator=(const GXATSNode &node) {
    GXATSNode::name = node.name;
    GXATSNode::syn = node.syn;
    GXATSNode::token = node.token;
    return *this;
}
