//
//  GXLRUCache.m
//  GaiaXiOS
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GXLRUCache.h"

const NSInteger kGXLRUCacheDefaultMaxCount = 256;

@implementation GXLRUNode

- (instancetype)initWithKey:(NSString *)key Value:(id)value{
    if (self = [super init]) {
        _key = key;
        _value = value;
    }
    return self;
}

@end


@interface GXLRUCache (){
    //辅助节点
    GXLRUNode *_p;
}

//缓存map信息
@property (nonatomic, strong) NSMutableDictionary<NSString*, GXLRUNode*> *map;

@end

@implementation GXLRUCache

- (instancetype)init {
    return [self initWithMaxCount:kGXLRUCacheDefaultMaxCount];
}

- (instancetype)initWithMaxCount:(NSInteger)maxCount {
    if (self = [super init]) {
        _p = [[GXLRUNode alloc] init];
        _map = [NSMutableDictionary dictionary];
        _maxCount = maxCount > 0 ? maxCount : kGXLRUCacheDefaultMaxCount;
    }
    return self;
}

- (void)setObject:(id)value forKey:(NSString *)key {
    if (!value || !key || ![key isKindOfClass:[NSString class]] || !key.length) {
        return;
    }
    
    //添加node
    GXLRUNode *node = [_map objectForKey:key];
    if (!node) {
        node = [[GXLRUNode alloc] initWithKey:key Value:value];
        [_map setObject:node forKey:key];
        //创建新节点，插入到头部
        [self insertNode:node];
    } else {
        //更新已有节点，移动到头部
        node.value = value;
        [self updateNode:node];
    }
    
    // 数量超限，移除不常用内容
    if (_maxCount > 0 && _map.count > _maxCount) {
        [self updateLimitCount:_maxCount];
    }
    
}

- (void)removeObjectForKey:(NSString *)key {
    if (!key || ![key isKindOfClass:[NSString class]] || !key.length) {
        return;
    }
    
    //移除node
    GXLRUNode *node = [_map objectForKey:key];
    if (node) {
        //更新前后节点
        GXLRUNode *prev = node.prev;
        GXLRUNode *next = node.next;
        if (prev) {
            prev.next = next;
        }
        if (next) {
            next.prev = prev;
        }
        
        //置空前后节点
        node.prev = nil;
        node.next = nil;
        
        //移除节点
        [_map removeObjectForKey:key];
    }
}

- (id)objectForKey:(NSString *)key {
    if (!key || ![key isKindOfClass:[NSString class]] || !key.length) {
        return nil;
    }
    
    //获取node
    GXLRUNode *node = [_map objectForKey:key];
    if (!node) {
        return nil;
    }
    
    // 移动到头部
    [self updateNode:node];
    
    return node.value;
}

- (void)updateLimitCount:(NSInteger)limitCount {
    //获取有效的lastNode
    GXLRUNode *node = _p;
    for (int i = 0; i < limitCount; i++) {
        node = node.next;
    }
    
    //获取invalidNode
    GXLRUNode *tmpNode = node.next;
    while (tmpNode) {
        [_map removeObjectForKey:tmpNode.key];
        tmpNode = tmpNode.next;
    }

    //最后一个node的next置空
    node.next = nil;
}

- (void)removeAllObjects {
    _p.next = nil;
    [_map removeAllObjects];
}

- (NSArray *)allKeys {
    return _map.allKeys;
}

- (NSUInteger)count {
    return _map.count;
}


#pragma mark - Node Operation

//创建新节点
- (void)insertNode:(GXLRUNode *)node {
    GXLRUNode *head = _p.next;
    
    //更新当前节点前后节点
    node.next = head;
    node.prev = _p;
        
    //原head节点后移
    if (head) {
        head.prev = node;
    };
    
    //更新工具节点
    _p.next = node;
}

//更新已存在的节点
- (void)updateNode:(GXLRUNode *)node {
    GXLRUNode *head = _p.next;
    // 已是头节点，直接return
    if (node == head) {
        return;
    }
    
    //获取节点前后节点
    GXLRUNode *prev = node.prev;
    GXLRUNode *next = node.next;
    if (prev) {
        prev.next = next;
    }
    if (next) {
        next.prev = prev;
    }
    
    //更新head节点
    node.prev = _p;
    node.next = head;
    head.prev = node;
    
    //更新工具节点
    _p.next = node;
}


@end
