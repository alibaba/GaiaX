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

#import "GaiaXJSDebuggerContext.h"
#import "GaiaXJSDRuntime.h"
#import "GaiaXJSModuleManager.h"
#import "GaiaXJSUIManager.h"

@interface GaiaXJSDebuggerContext ()

@property(nonatomic, weak) GaiaXJSDRuntime *runtime;
@property(nonatomic, strong) NSString *boostraptring;

@end

@implementation GaiaXJSDebuggerContext

- (instancetype)initWithRuntime:(GaiaXJSRuntime *)runtime {
    if (self = [super initWithRuntime:runtime]) {
        self.runtime = (GaiaXJSDRuntime *) runtime;
        self.runtime.wsWrapper.jsBridge = self.bridge;
        [self setUpContext];
    }
    return self;
}

- (void)setUpContext {
    [self.bridge setupContext:^{
        NSMutableString *bootstrapString = [NSMutableString string];
        [bootstrapString appendFormat:@"\r\nvar __globalThis = this; \r\n__globalThis.__CONTEXT_ID__ = %ld;\r\n __globalThis.__ENGINE_TYPE__ = 10;\r\n", (long) self.contextId];
        NSDictionary *env = [[NSProcessInfo processInfo] environment];
        if ([env[@"GAIAX_JS_DEV"] boolValue]) {
            [bootstrapString appendFormat:@"__globalThis.__DEV__ = true; \r\n"];
        }
        [bootstrapString appendString:@"/**bridge.ts**/\r\n"];
        [bootstrapString appendString:@"/**bootstrap.ts**/\r\n"];
        [bootstrapString appendFormat:@"%@", [[GaiaXJSModuleManager debuggerManager] rdInjectConfigs]];
        [bootstrapString appendFormat:@"%@", [[GaiaXJSUIManager debuggerManager] rdInjectProps]];
        self.boostraptring = bootstrapString;
        [self.bridge executeJSLibrary:bootstrapString fileName:@"debugger.js"];
    }];
}


- (void)evalScript:(NSString *)jsString fileName:(NSString *)fileName {
    if (jsString != nil && jsString.length > 0) {
        if ([fileName isEqualToString:@"debugger.js"]) {
            [self.runtime.wsWrapper evalInitEnvJSScript:jsString];
        } else {
            [self.runtime.wsWrapper sendJSScript:jsString];
        }
    }
}

- (void)executeIndexJS:(NSString *)jsString fileName:(NSString *)fileName args:(NSDictionary *)args {
    if (jsString != nil && jsString.length > 0) {
        [self.runtime.wsWrapper createJSComponent:args];
    }
}

@end
