#include "GXATSNode.h"
#include <iostream>
#include <string>

using namespace std;

GXATSNode::GXATSNode() {
}

GXATSNode::GXATSNode(string name, string detail, string token) {
    GXATSNode::name = name;   //字段
    GXATSNode::detail = detail;     //code
    GXATSNode::token = token; //类型
}

GXATSNode::~GXATSNode() {

}

GXATSNode &GXATSNode::operator=(const GXATSNode &node) {
    GXATSNode::name = node.name;
    GXATSNode::detail = node.detail;
    GXATSNode::token = node.token;
    GXATSNode::count = node.count;
    return *this;
}

GXATSNode::GXATSNode(string name, string detail, string token, int count) {
    GXATSNode::name = name;   //字段
    GXATSNode::detail = detail;     //code
    GXATSNode::token = token; //类型
    GXATSNode::count = count;
}
