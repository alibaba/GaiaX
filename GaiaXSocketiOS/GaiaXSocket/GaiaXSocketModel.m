//  Copyright (c) 2023, Alibaba Group Holding Limited.
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

#import "GaiaXSocketModel.h"

static NSInteger GAIAX_SOCKET_REQUEST_ID = 1;

@implementation GaiaXSocketModel

- (GaiaXSocketModel *)initWithMessageString:(NSString *)message {
    if (self = [super init]) {
        NSDictionary *result = nil;
        @try {
            result = [NSJSONSerialization JSONObjectWithData:[message dataUsingEncoding:NSUTF8StringEncoding] options:0 error:NULL];
        } @catch (NSException *exception) {
            
        } @finally {
            if (result != nil) {
                self.jsonRPCVersion = result[@"jsonrpc"];
                if (result[@"id"] != nil) {
                    self.messageId = [NSNumber numberWithInteger: [result[@"id"] integerValue]];
                }
                if (result[@"error"] != nil) {
                    self.error  = result[@"error"];
                } else if (result[@"result"] != nil) {
                    self.result = result[@"result"];
                }
                if (result[@"params"] != nil) {
                    self.params = result[@"params"];
                }
                if (result[@"method"] != nil) {
                    self.method = result[@"method"];
                }
            }
        }
    }
    return self;
}

- (NSString *)stringifyModel {
    NSString *result = nil;
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    [dict setObject:self.jsonRPCVersion forKey:@"jsonrpc"];
    if (self.messageId != nil) {
        [dict setObject:self.messageId forKey:@"id"];
    }
    if (self.method != nil) {
        [dict setObject:self.method forKey:@"method"];
    }
    if (self.params != nil) {
        [dict setObject:self.params forKey:@"params"];
    }
    if (self.result != nil) {
        [dict setObject:self.result forKey:@"result"];
    }
    if (self.error != nil) {
        [dict setObject:self.error forKey:@"error"];
    }
    @try {
        NSData *data = [NSJSONSerialization dataWithJSONObject:dict options:0 error:NULL];
        result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    } @catch (NSException *exception) {
        
    } @finally {
        return result;
    }
}

+ (GaiaXSocketModel *)requestWithMethod:(NSString *)method
                                 params:(NSDictionary *)params {
    GaiaXSocketModel *model = [[GaiaXSocketModel alloc] init];
    model.jsonRPCVersion = @"2.0";
    model.messageId = [NSNumber numberWithInteger:GAIAX_SOCKET_REQUEST_ID++];
    model.method = method;
    model.params = params;
    return model;
}

+ (GaiaXSocketModel *)responseWithMessageId:(NSInteger )messageId
                                     result:(NSDictionary *)result {
    GaiaXSocketModel *model = [[GaiaXSocketModel alloc] init];
    model.jsonRPCVersion = @"2.0";
    model.messageId = [NSNumber numberWithInteger:messageId];
    model.result = result;
    return model;
}

+ (GaiaXSocketModel *)responseWithMessageId:(NSInteger )messageId
                                      error:(NSDictionary *)error {
    GaiaXSocketModel *model = [[GaiaXSocketModel alloc] init];
    model.jsonRPCVersion = @"2.0";
    model.messageId = [NSNumber numberWithInteger:messageId];
    model.error = error;
    return model;
}

+ (GaiaXSocketModel *)notificationWithMethod:(NSString *)method
                                      params:(NSDictionary *)params {
    GaiaXSocketModel *model = [[GaiaXSocketModel alloc] init];
    model.jsonRPCVersion = @"2.0";
    model.method = method;
    model.params = params;
    return model;
}

@end
