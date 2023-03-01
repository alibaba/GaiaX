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

#import "GaiaXJSBridge.h"
#import "GaiaXJSModuleManager.h"
#import "GaiaXJSRuntime.h"

static NSInteger queueIndex = 0;

@implementation GaiaXJSBridge

- (instancetype)initWidthJSContext:(GaiaXJSContext *)jsContext {
    if (self = [super init]) {
        NSString *labelString = [NSString stringWithFormat:@"%@.%ld", GaiaXJSQueuePrefix, (long) queueIndex++];
        self.gaiaxJSQueue = dispatch_queue_create(labelString.UTF8String, NULL);
        self.jsContext = jsContext;
    }
    return self;
}

- (void)setupContext:(GaiaXJSSetupContextBlock)block {
    dispatch_async(self.gaiaxJSQueue, ^{
        if (block) {
            block();
        }
    });
}

- (void)executeJSLibrary:(NSString *)jsString fileName:(NSString *)fileName {
    [self.jsContext evalScript:jsString fileName:fileName];
}

- (void)evalScript:(NSString *)jsString fileName:(NSString *)fileName {
    [self evalScript:jsString fileName:fileName callback:nil];
}

- (void)evalScript:(NSString *)jsString
          fileName:(NSString *)fileName
          callback:(GaiaXJSExecutedBlock)callback {
    dispatch_async(self.gaiaxJSQueue, ^{
        [self.jsContext evalScript:jsString fileName:fileName];
        if (callback != nil) {
            callback();
        }
    });
}

- (void)executeIndexJS:(NSString *)jsString
              fileName:(NSString *)fileName
                  args:(NSDictionary *)args
              callback:(GaiaXJSExecutedBlock)callback {
    dispatch_async(self.gaiaxJSQueue, ^{
        [self.jsContext executeIndexJS:jsString fileName:fileName args:args];
        if (callback != nil) {
            callback();
        }
    });
}

@end
