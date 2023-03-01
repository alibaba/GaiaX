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


#import "GaiaXJSCContext.h"
#import "GaiaXJSCRuntime.h"
#import "GaiaXJSModuleManager.h"
#import "GaiaXJSUIManager.h"
#import "GaiaXJSCModule.h"
#import "GaiaXJSHelper.h"
#import "GaiaXJSHandler.h"

@interface GaiaXJSCContext ()

@property(nonatomic, weak) GaiaXJSCRuntime *runtime;


@end

@implementation GaiaXJSCContext

- (instancetype)initWithRuntime:(GaiaXJSRuntime *)runtime {
    if (self = [super initWithRuntime:runtime]) {
        self.runtime = (GaiaXJSCRuntime *) runtime;
        self.ctx = [[JSContext alloc] initWithVirtualMachine:self.runtime.vm];
        [self setUpContext:self.ctx];
    }
    return self;
}

- (void)dealloc {
    NSLog(@"GaiaXJSCContext dealloc");
}

- (void)setUpContext:(JSContext *)ctx {
    [self.bridge setupContext:^{
        init_jsc_module(self);

        [ctx setExceptionHandler:^(JSContext *context, JSValue *exception) {
            [[NSNotificationCenter defaultCenter] postNotificationName:@"GaiaXJSDidChangedNotification" object:nil userInfo:@{@"level": @"error", @"data": GaiaXJSSafeString([exception toString])}];
            
            NSDictionary *errorInfo = @{@"errorCode": GaiaXJSSafeString([exception toString]), @"errorMessage": GaiaXJSSafeString([exception toString]), @"errorStack": GaiaXJSSafeString([[exception objectForKeyedSubscript:@"stack"] toString])};
            [GaiaXJSHandler throwJSError:errorInfo];
   
        }];

        NSTimeInterval startTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseWillStartLoadJSLibrary extendInfo:@{@"timestamp": @(startTime)}];

        NSString *bootstrapPath = [[NSBundle mainBundle] pathForResource:@"GaiaXJS.bundle/bootstrap.min" ofType:@"js"];
        NSMutableString *bootstrapString = [NSMutableString string];
        [bootstrapString appendFormat:@"\r\nvar __globalThis = this; \r\n__globalThis.__CONTEXT_ID__ = %ld;\r\n __globalThis.__ENGINE_TYPE__ = 1;\r\n", (long) self.contextId];
        NSDictionary *env = [[NSProcessInfo processInfo] environment];
        if ([env[@"GAIAX_JS_DEV"] boolValue]) {
            [bootstrapString appendFormat:@"__globalThis.__DEV__ = true; \r\n"];
        }
        [bootstrapString appendString:[GaiaXJSHelper getHelperJSMethods]];
        NSString *fileString = [[NSString alloc] initWithContentsOfFile:bootstrapPath encoding:NSUTF8StringEncoding error:NULL];
        [bootstrapString appendString:fileString];
        [bootstrapString appendFormat:@"%@", [[GaiaXJSModuleManager defaultManager] injectConfigs]];
        [bootstrapString appendFormat:@"%@", [[GaiaXJSUIManager defaultManager] injectProps]];

        [self.bridge executeJSLibrary:bootstrapString fileName:@"bootstrap.min.js"];
        
        NSTimeInterval endTime = [[NSDate date] timeIntervalSince1970] * 1000;
        [GaiaXJSHandler dispatchExecPhase:GaiaXJSExecPhaseDidEndLoadJSLibrary extendInfo:@{@"timestamp": @(endTime),
                                                                                           @"cost": @(endTime-startTime)
                                                                                         }];
    }];

}

- (void)executeIndexJS:(NSString *)jsString fileName:(NSString *)fileName args:(NSDictionary *)args {
    NSString *str = jsString;
    if (args != nil) {
        NSArray *jsArray = [str componentsSeparatedByString:@"\r\n"];
        NSMutableArray *newContentArray = [[NSMutableArray alloc] init];
        for (NSInteger i = 0; i < jsArray.count; i++) {
            if ([jsArray[i] hasPrefix:@"//"]) {
                continue;
            }
            [newContentArray addObject:jsArray[i]];
        }
        if (newContentArray.count > 0) {
            str = [newContentArray componentsJoinedByString:@"\r\n"];
        }
        str = [str
                stringByTrimmingCharactersInSet:[NSCharacterSet
                        whitespaceAndNewlineCharacterSet]];
        NSRange range = [str rangeOfString:@"})" options:NSBackwardsSearch];
        str = [str
                stringByReplacingCharactersInRange:range
                                        withString:
                                                [NSString
                                                        stringWithFormat:
                                                                @"},{ 'bizId':'%@', 'templateId': "
                                                                @"'%@', 'instanceId': %@, 'templateVersion': '%@' } )",
                                                                args[@"bizId"], args[@"templateId"],
                                                                args[@"instanceId"], args[@"templateVersion"]]];
    }

    if (str.length > 0) {
        if (fileName != nil && fileName.length > 0) {
            [self.ctx evaluateScript:str withSourceURL:[NSURL URLWithString:fileName]];

        } else {
            [self.ctx evaluateScript:str];
        }
    }
}

- (void)evalScript:(NSString *)jsString fileName:(NSString *)fileName {
    if (jsString.length > 0) {
        if (fileName != nil && fileName.length > 0) {
            [self.ctx evaluateScript:jsString withSourceURL:[NSURL URLWithString:fileName]];

        } else {
            [self.ctx evaluateScript:jsString];
        }
    }
}

@end
