//
//  GXTemplateParser.h
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

#ifndef GXTemplateParser_h
#define GXTemplateParser_h

#include <stdio.h>
#include <stdlib.h>

#endif /* GXTemplateParser_h */

typedef struct{
    char *fileName;
    char *fileContent;
} File;

//解析
void parseFile(char *path,File** files,int *fileCount);

//释放
void freeFiles(File** files,int fileCount);
