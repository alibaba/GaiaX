//
//  GXFileHandler.m
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

#import "GXFileHandler.h"
#import <UIKit/UIKit.h>
#import <sys/xattr.h>
#import <sys/stat.h>


@implementation GXFileHandler

#pragma mark - 基础目录

//Cache目录
+ (NSString *)pathForCachesDirectory{
    static NSString *path = nil;
    static dispatch_once_t token;
    
    dispatch_once(&token, ^{
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
        path = [paths lastObject];
    });
    
    return path;
}

+ (NSString *)pathForCachesDirectoryWithPath:(NSString *)path{
    return [[self pathForCachesDirectory] stringByAppendingPathComponent:path];
}


//Library目录
+ (NSString *)pathForLibraryDirectory{
    static NSString *path = nil;
    static dispatch_once_t token;
    
    dispatch_once(&token, ^{
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES);
        path = [paths lastObject];
    });
    
    return path;
}

+ (NSString *)pathForLibraryDirectoryWithPath:(NSString *)path{
    return [[self pathForLibraryDirectory] stringByAppendingPathComponent:path];
}


//Document目录
+ (NSString *)pathForDocumentsDirectory{
    static NSString *path = nil;
    static dispatch_once_t token;
    
    dispatch_once(&token, ^{
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
        path = [paths lastObject];
    });
    
    return path;
}

+ (NSString *)pathForDocumentsDirectoryWithPath:(NSString *)path{
    return [[self pathForDocumentsDirectory] stringByAppendingPathComponent:path];
}


//Application目录
+ (NSString *)pathForApplicationSupportDirectory{
    static NSString *path = nil;
    static dispatch_once_t token;
    
    dispatch_once(&token, ^{
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES);
        path = [paths lastObject];
    });
    
    return path;
}

+ (NSString *)pathForApplicationSupportDirectoryWithPath:(NSString *)path{
    return [[self pathForApplicationSupportDirectory] stringByAppendingPathComponent:path];
}



@end


@implementation GXFileHandler (extension)

+ (BOOL)isFileExistAtPath:(NSString *)path{
    if (!path) {
        return NO;
    }
    
    struct stat st;
    if(lstat([path fileSystemRepresentation], &st) == 0){
        return YES;
    }
    return NO;
}


+ (BOOL)isDirectoryItemAtPath:(NSString *)path{
    if (!path) {
        return NO;
    }
    
    struct stat st;

    //文件是否存在
    if (lstat([path fileSystemRepresentation], &st) != 0) {
        return NO;
    }
    
    //文件是否为文件夹
    if (S_ISDIR(st.st_mode)) {
        return YES;
    }
    
    return NO;
}

+ (NSString *)creatTimeForItemAtPath:(NSString *)path{
    struct stat st;
    if(lstat([path fileSystemRepresentation], &st) == 0){
        return [NSString stringWithFormat:@"%ld", st.st_birthtimespec.tv_sec];
    }
    return 0;
}

@end
