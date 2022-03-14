//
//  GaiaXSocketRequest.m
//  GaiaXiOSDemo
//
//  Copyright (c) 2021, Alibaba Group Holding Limited.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

#import "GaiaXSocketRequest.h"
#import "GaiaXJSONRPCConstants.h"

@implementation GaiaXSocketRequest

+ (instancetype)requestWithRequestId:(NSNumber *)requestId Method:(NSString *)method parameters:(id)parameters {
    GaiaXSocketRequest *request = [[GaiaXSocketRequest alloc] init];
    request.parameters = parameters;
    request.requestId = requestId;
    request.method = method;
    return request;
}

- (NSDictionary *)toJSONDictionary {
    NSMutableDictionary *json = [NSMutableDictionary dictionary];
    [json setObject:GaiaXJSONRPCVersion forKey:GaiaXJSONRPCKey];
    [json setObject:self.method forKey:GaiaXJSONRPCMethodKey];
    
    if (self.parameters) {
        [json setObject:self.parameters forKey:GaiaXJSONRPCParamsKey];
    }
    
    if (self.requestId) {
        [json setObject:self.requestId forKey:GaiaXJSONRPCIdKey];
    }
    
    return [json copy];
}

- (NSString *)toJSONString {
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:[self toJSONDictionary]
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:nil];
    NSString * str = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return str;
}

@end
