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

#import "GaiaXSocketUtils.h"

@implementation GaiaXSocketUtils

+ (BOOL)isNetworkReachable{
    return YES;
}

+ (NSString *)jsonStringFromDictionary:(NSDictionary *)dictionary{
    if (dictionary && [dictionary isKindOfClass:[NSDictionary class]]) {
        NSData *data = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:nil];
        if (data) {
            NSString *jsonString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
            return jsonString;
        }
    }
    return nil;
}

+ (NSDictionary *)dictionaryFromJsonString:(NSString *)jsonString{
    if (jsonString && [jsonString isKindOfClass:[NSString class]]) {
        NSData *data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
        if (data) {
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
            return dict;
        }
    }
    return nil;
}


@end
