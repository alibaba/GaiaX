//
//  GXTemplateParser.m
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

#include "GXTemplateParser.h"

const int FileHeadNumByte = 100;
const int FileNumByte = 4;

char* getFileName(FILE *fp) {
    char *fileName = NULL;
    uint32_t fileNameLength = 0;
    fread(&fileNameLength, FileNumByte, 1, fp);
    fileName = malloc(fileNameLength + 1);
    // fileName内存空间分配失败保护
    if (fileName == NULL){
        return NULL;
    }
    
    fread(fileName, fileNameLength, 1, fp);
    // 必须添加结尾符,否则C转OC字符串会转失败
    if (fileName != NULL) {
        fileName[fileNameLength] = '\0';
    }
    return fileName;
}

char* getFileContent(FILE *fp) {
    char *fileContent = NULL;
    uint32_t fileContentLength = 0;
    fread(&fileContentLength, FileNumByte, 1, fp);
    fileContent = malloc(fileContentLength + 1);
    fread(fileContent, fileContentLength, 1, fp);
    if (fileContent != NULL) {
        fileContent[fileContentLength] = '\0';
    }
    return fileContent;
}

void parseFile(char *path,File** files,int *fileCount)
{
    char fileHead[FileHeadNumByte] = {0};
    char *fileName = NULL;
    char* fileContent = NULL;
    FILE *fp = fopen(path, "r");
    if (fp == NULL) { // 文件打开失败
        return;
    }
    File** tmp = files;
    
    //获取文件大小
    fseek(fp, 0, SEEK_END);
    long totalLength = ftell(fp);
    rewind (fp);
    
    // 读取文件头
    size_t headLength = fread(fileHead, FileHeadNumByte, 1, fp);
    if (headLength <= 0) {
        // 读取文件头失败
        fclose(fp);
        return;
    };
    
    int i = 0;
    // 不能使用feof(fp) == 0，会多遍历一次
    while(totalLength > ftell(fp)){
        
        File *f = malloc(sizeof(File));
        if (f == NULL) {
            break;
        }
        
        // 读取文件名
        fileName = getFileName(fp);
        if (fileName == NULL) {
            // 读取文件名失败
            break;
        }
        f->fileName = fileName;
        
        // 读取文件内容
        fileContent = getFileContent(fp);
        if (fileContent == NULL) {
            // 读取内容失败
            break;
        }
        f->fileContent = fileContent;
        
        *tmp = f;
        tmp++;
        i++;
    }
    *fileCount = i;
    
    // 关闭IO
    fclose(fp);
}

void freeFiles(File** files,int fileCount)
{
    if (files == NULL || fileCount <= 0) {
        return;
    }
    
    for (int i = 0; i < fileCount; i++) {
        File *file = files[i];
        free(file->fileName);
        file->fileName = NULL;
        free(file->fileContent);
        file->fileContent = NULL;
        free(file);
        file = NULL;
    }
    
    free(files);
    files = NULL;
}
