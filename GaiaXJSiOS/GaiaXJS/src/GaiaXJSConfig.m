/*
 * Copyright (c) 2022, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

#import "GaiaXJSConfig.h"
#import "GaiaXJSFactory.h"

@implementation GaiaXJSConfig


+ (BOOL)isBreakPointDebugging {
    BOOL enabled = NO;
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"GAIAX_JS_DEBUGGER_ENABLED"] != nil) {
        enabled = [[NSUserDefaults standardUserDefaults] boolForKey:@"GAIAX_JS_DEBUGGER_ENABLED"];
    }
    return enabled;
}

+ (void)setBreakPointDebuggingEnabled:(BOOL)enabled {
    if (enabled) {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"GAIAX_JS_DEBUGGER_ENABLED"];
    } else {
        [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"GAIAX_JS_DEBUGGER_ENABLED"];
    }
    NSDictionary *userInfo = nil;
    if (enabled) {
        userInfo = @{@"engineType": @"breakpoint"};
    }
    if (enabled) {
        [GaiaXJSFactory newContextByBizIdIfNeeded:@"common"];
    }
    [[NSNotificationCenter defaultCenter] postNotificationName:@"GAIAX_JS_ENGINE_TYPE_CHANGED" object:nil userInfo:userInfo];
}

@end
