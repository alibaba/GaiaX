//
//  GXTemplateData.m
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

#import "GXTemplateData.h"
#import "NSDictionary+GX.h"
#import "GxFunctionDef.h"
#import "GXUtils.h"

@implementation GXTemplateData

//set方法
- (void)setData:(NSDictionary *)data{
    if ([data isKindOfClass:[NSDictionary class]]){
        //不可变，先copy在赋值
        _data = [data gx_mutableDeepCopy];
    } else {
        //否则为nil
        _data = nil;
    }
}

//结果数据
- (NSMutableDictionary *)resultData{
    if (!_resultData) {
        _resultData = [NSMutableDictionary dictionary];
    }
    return _resultData;
}

//有效性判断
- (BOOL)isAvailable{
    return [GXUtils isValidDictionary:self.data];
}


- (void)dealloc{
    GXLog(@"[GaiaX] GXTemplateData释放 -- %@", self);
}

@end
