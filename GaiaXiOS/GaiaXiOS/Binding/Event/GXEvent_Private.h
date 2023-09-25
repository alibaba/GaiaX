//
//  GaiaEvent_Private.h
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

#ifndef GaiaEvent_Private_h
#define GaiaEvent_Private_h

#import "GXEvent.h"
#import <GaiaXJS/GaiaXJS.h>

@interface GXEvent()

//jsComponent
@property (nonatomic, weak, nullable) GaiaXJSComponent *jsComponent;
//js事件
@property (nonatomic, strong, nullable) GXJsEvent *jsEvent;

- (void)creatJsEvent:(NSDictionary *_Nullable)eventInfo;

@end

#endif /* GaiaEvent_Private_h */
