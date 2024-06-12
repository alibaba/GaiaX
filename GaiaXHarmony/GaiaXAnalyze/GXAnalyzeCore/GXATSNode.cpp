/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "GXATSNode.h"

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
