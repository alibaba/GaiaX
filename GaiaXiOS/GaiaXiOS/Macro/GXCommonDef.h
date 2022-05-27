//
//  GXCommonDef.h
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

#ifndef GXCommonDef_h
#define GXCommonDef_h

//css
#define kGXComDef_KW_Dot           @"."
#define kGXComDef_KW_Sharp         @"#"

//id & class，id > class
#define kGXComDef_KW_ID            @"id"
#define kGXComDef_KW_Class         @"class"

//template type
#define kGXComDef_KW_GaiaTemplate  @"gaia-template"
#define kGXComDef_KW_SubType       @"sub-type"
#define kGXComDef_KW_Layers        @"layers"
#define kGXComDef_KW_Type          @"type"

//container type
#define kGXComDef_KW_Custom        @"custom"//自定义模板
#define kGXComDef_KW_Scroll        @"scroll"//横滑容器
#define kGXComDef_KW_Grid          @"grid"//栅格容器
#define kGXComDef_KW_Slider        @"slider"//栅格容器

//the template content - key
#define kGXComDef_KW_VH            @"vh"//层级
#define kGXComDef_KW_SY            @"sy"//样式内容
#define kGXComDef_KW_DB            @"db"//数据绑定关系
#define kGXComDef_KW_DA            @"da"//mock内容
#define kGXComDef_KW_JS            @"js"//js内容

//GaiaX file types
#define kGXComDef_KW_JS            @"js"//js内容
#define kGXComDef_KW_CSS           @"css"//css样式
#define kGXComDef_KW_Data          @"data"//moke数据
#define kGXComDef_KW_Json          @"json"//层级关系
#define kGXComDef_KW_Gaiax         @"gaiax"//压缩文件后缀
#define kGXComDef_KW_DataBinding   @"databinding"//数据bind对应关系

//template file
#define kGXComDef_KW_Index                       @"index"
#define kGXComDef_KW_IndexJS                     @"index.js"
#define kGXComDef_KW_IndexCSS                    @"index.css"
#define kGXComDef_KW_IndexJson                   @"index.json"
#define kGXComDef_KW_IndexData                   @"index.data"
#define kGXComDef_KW_IndexDataBinding            @"index.databinding"

//file directory/name
#define kGXTemplateDataBaseFile                  @"gaiax_db.sqlite"
#define kGXTemplateResourceStoragePath           @"gaiax_template_center"
#define KGXTemplateResourceDownloadStoragePath   @"gaiax_template_download"

//net image
#define kGXComDef_Prefix_Https                   @"https://"
#define kGXComDef_Prefix_Http                    @"http://"
//local image
#define kGXComDef_Prefix_Local                   @"local:"

//local & cloud
#define kGXComDef_KW_Source_Local                @"local"
#define kGXComDef_KW_Source_Cloud                @"cloud"


#define kGX_INVALID_DIM -10086


#endif /* GXCommonDef_h */
