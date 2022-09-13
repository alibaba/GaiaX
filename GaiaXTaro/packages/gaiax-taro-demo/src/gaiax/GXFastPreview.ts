import Taro, { SocketTask } from "@tarojs/taro";

// gaiax://gaiax/preview?url=ws://30.77.74.137:9296&id=phone-demand&type=auto
class GXFastPreview {


    private studioIpAddress = "192.168.10.29"
    private listener: IGXFastPreviewListener;
    private socketTask: SocketTask;
    private lastTemplateId: string = "";
    private mainTemplateId: string = "";
    private subTemplateCount = 0;
    private timeoutId: NodeJS.Timeout;

    startFastPreview() {

        Taro.connectSocket({
            // gaiax-opensource://preview?url=ws://30.78.147.247:9001&id=test2&type=auto
            // port = 9296 or 9001
            url: 'ws://' + this.studioIpAddress + ':9001',
            success: () => {
                // console.log('GXFastPreview connect success')
            }
        }).then(task => {
            this.socketTask = task
            task.onOpen(() => {
                // console.log('GXFastPreview onOpen')

                // 发生初始化
                task.send({
                    data: JSON.stringify({
                        jsonrpc: '2.0',
                        method: 'initialize',
                        id: 102
                    }),
                })
            })
            task.onMessage((msg) => {
                // console.log(`GXFastPreview onMessage: lastTemplateId = ${this.lastTemplateId}`)

                const data = JSON.parse(msg.data);
                // console.log(data);

                // 手动切换通知消息
                // "{"jsonrpc":"2.0","method":"template/didChangedNotification","params":{"templateId":"phone-demand","id":"phone-demand","data":{".metaInfo":"{\"name\":\"phone-demand\",\"desc\":\"点播剧场\",\"version\":25,\"thumbnailUrl\":\"http://dl-oss-wanju.youku.com/phone-demand/36819d752f1f183a6282a40c11323bd5/index.png\",\"templateUrl\":null,\"templateFileData\":\"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\",\"status\":\"PRE_OFFLINE\",\"sign\":\"2a9dca2da3b56485955eb13b47e0c30424c136ca\",\"appKey\":\"youku\",\"appName\":\"优酷App\",\"biz\":\"yk-vip\",\"bizId\":\"优酷会员\",\"templateType\":\"NORMAL\",\"metaInfo\":{\"templateId\":\"phone-demand\",\"templateVersion\":25},\"creator\":{\"nick\":\"庭舟\",\"eid\":\"231041\",\"timestamp\":1632822148000},\"modifier\":{\"nick\":\"庭舟\",\"eid\":\"231041\",\"timestamp\":1632904358000},\"commitMessage\":\"点播剧场\",\"templateFileType\":\"BINARY\",\"platforms\":[{\"minVersion\":\"MIN_VALUE\",\"maxVersion\":\"MAX_VALUE\",\"platform\":\"IPHONE\"},{\"minVersion\":\"MIN_VALUE\",\"maxVersion\":\"0.0.0\",\"platform\":\"IPAD\"},{\"minVersion\":\"MIN_VALUE\",\"maxVersion\":\"0.0.0\",\"platform\":\"ANDROID\"},{\"minVersion\":\"MIN_VALUE\",\"maxVersion\":\"0.0.0\",\"platform\":\"UIPAD\"},{\"minVersion\":\"MIN_VALUE\",\"maxVersion\":\"0.0.0\",\"platform\":\"APAD\"}],\"rateStatus\":\"CANCEL\",\"ratePercent\":0}\n","index.css":" #phone-demand{width:100%;height:100%;padding-right:youku_margin_left;padding-left:youku_margin_left;}  #cover-img{width:84pt;height:112pt;border-radius:7px;background-size:cover;background-color:primaryFillColor;}  #text-area{padding-right:0px;padding-bottom:0px;flex-grow:1;flex-direction:column;padding-left:youku_column_spacing;}  #title-area{width:100%;height:20px;}  #title{flex-grow:1;height:20px;line-height:20px;font-size:14px;color:primaryInfo;font-family:PingFangSC-Semibold;font-weight:700;}  #year{margin-left:6px;height:20px;line-height:20px;width:40px;font-size:11px;color:tertiaryInfo;}  #top-text{margin-top:3px;width:100%;height:16px;font-size:11px;color:tertiaryInfo;}  #bottom-text{margin-top:3px;width:100%;height:16px;min-height:16px;font-size:11px;text-overflow:ellipsis;color:tertiaryInfo;lines:2;}  #reason-view{background-color:secondaryBackground;border-radius:7px;height:28px;left:9px;right:0px;bottom:0px;position:absolute;}  #reason-icon{left:4px;top:0px;width:18px;height:18px;position:absolute;}  #reason{margin-left:6px;margin-right:6px;flex-grow:1;height:28px;line-height:28px;font-size:11px;color:tertiaryInfo;} ","index.data":"{}","index.databinding":"{\"event\":{\"phone-demand\":\"${nodes[0].data.action}\"},\"data\":{\"cover-img\":{\"url\":\"${nodes[0].data.img}\",\"mark\":{\"type\":\"${nodes[0].data.mark.type}\",\"img\":\"${nodes[0].data.mark.data.img}\",\"text\":\"${nodes[0].data.mark.data.text}\",\"color\":\"${nodes[0].data.mark.data.color}\"},\"summary\":\"${nodes[0].data.summary}\",\"summary-type\":\"${nodes[0].data.summaryType}\",\"summary-color\":\"${nodes[0].gaiaxscene.sceneScoreColor}\"},\"title\":{\"value\":\"${nodes[0].data.title}\",\"extend\":{\"fit-content\":\"true\"}},\"year\":{\"value\":\"${nodes[0].data.year}\",\"extend\":{\"fit-content\":\"true\"}},\"top-text\":\"${nodes[0].data.formatInfo[0]}\",\"bottom-text\":{\"value\":\"${nodes[0].data.formatInfo[1]}\",\"extend\":{\"fit-content\":\"true\"}},\"reason-icon\":\"https://gw.alicdn.com/tfs/TB1PRxNEHY1gK0jSZTEXXXDQVXa-54-54.png\",\"reason\":\"${nodes[0].data.reason.text.title}\"}}","index.js":"//------ts start------\r\n//UEsDBBQAAAgIAOZ9Z1RZfqz6vAAAAFABAAAIAAAAaW5kZXguanNzzs8tyM9LzSvRqOZSUCguSSxJtVKortUBcvLzgjPyy60U0krzkksy8/MUNDQVQIoUFPT1n63vf7Zm4bMVC5/N3f9sxr7nS3a9bN77ZM+sp7PnvdjQDFZUkpFZrFeUmlaUWpzhnFiUoqFpDRSHmhyUmphSiWY0VMojMyWVsKUvZ014Mb0fzVK44aXFuIwAa366pPf5lBXPpm9D1+mSWlxSlI/uMFS9G9peLGt82d71bMq+57NakE2oBfoQAFBLAQIUAxQAAAgIAOZ9Z1RZfqz6vAAAAFABAAAIAAAAAAAAAAAAAACkgQAAAABpbmRleC5qc1BLBQYAAAAAAQABADYAAADiAAAAAAA=\r\n//------ts end------\r\n\r\nComponent({state:{},onShow:function(){this.refreshCard()},onReady:function(){},onHide:function(){},onReuse:function(){},onDestroy:function(){}});","index.json":"{\"id\":\"phone-demand\",\"uid\":\"b2da84f6c820b344eaeb2581bf70c916\",\"type\":\"gaia-template\",\"package\":{\"engines\":{\"gaiax\":\">=0.0.1\"},\"id\":\"phone-demand\",\"modify-timestamp\":\"Mon Mar 07 2022 15:47:13 GMT+0800 (China Standard Time)\",\"priority\":\"0\",\"version\":\"0\",\"constraint-size\":{\"width\":375,\"height\":112,\"zoom\":1},\"dependencies\":{}},\"layers\":[{\"id\":\"cover-img\",\"uid\":\"44026cff984aca5270d43822ebbf23f8\",\"class\":\"cover-img\",\"type\":\"image\"},{\"id\":\"text-area\",\"uid\":\"d18d28c46f3a91bc80b912e27a5a46a7\",\"class\":\"text-area\",\"type\":\"view\",\"layers\":[{\"id\":\"title-area\",\"uid\":\"ea700e093948e4ac139c10b86128686f\",\"class\":\"title-area\",\"type\":\"view\",\"layers\":[{\"id\":\"title\",\"uid\":\"b546164c1a8b10d31c71cffc2c4b9bcf\",\"class\":\"title\",\"type\":\"text\"},{\"id\":\"year\",\"uid\":\"459627ed02b3d24bf25d7aba02243942\",\"class\":\"year\",\"type\":\"text\"}]},{\"id\":\"top-text\",\"uid\":\"ab88f01896f486c9122719811fa0f3e7\",\"class\":\"top-text\",\"type\":\"text\"},{\"id\":\"bottom-text\",\"uid\":\"120dfdefc78a88af7d7182171d7524d4\",\"class\":\"bottom-text\",\"type\":\"text\"},{\"id\":\"reason-view\",\"uid\":\"8379fb2160351b511fcbe41cb37a3189\",\"class\":\"reason-view\",\"type\":\"view\",\"layers\":[{\"id\":\"reason-icon\",\"uid\":\"e3f25c53e0abcdd5d921a6570d39f6bc\",\"class\":\"reason-icon\",\"type\":\"image\"},{\"id\":\"reason\",\"uid\":\"9c4ab23618d9c96ded5efc0c6ae2d713\",\"class\":\"reason\",\"type\":\"text\"}]}]}]}"}}}"
                if (data.method == 'template/didChangedNotification') {
                    const params = data.params;

                    const templateId = params.templateId;
                    const templateJson = this.createTemplateData(params);

                    this.listener.onAddData(templateId, templateJson);

                    this.lastTemplateId = templateId;

                    clearTimeout(this.timeoutId);

                    this.timeoutId = setTimeout(() => {
                        this.listener.onUpdate(templateId);
                    }, 150);
                }
                // 手动拉去单个模板
                // {
                //     "jsonrpc": "2.0",
                //     "id": 103,
                //     "result": {
                //         "templateId": "scroll_test",
                //         "id": "scroll_test",
                //         "data": {
                //             "index.css": " #scroll_test{width:100%;height:100px;background-color:#ffffff;}   ",
                //             "index.data": "{}",
                //             "index.databinding": "{\"data\":{\"scroll_test\":{\"value\":\"${nodes}\"}}}",
                //             "index.json": "{\"id\":\"scroll_test\",\"uid\":\"e279ed9a9c723616f12e77f8874a52ea\",\"type\":\"gaia-template\",\"sub-type\":\"scroll\",\"row-spacing\":9,\"item-spacing\":12,\"direction\":\"horizontal\",\"edge-insets\":\"{0,0,0,0}\",\"package\":{\"id\":\"scroll_test\",\"version\":\"0.0.1\",\"modify-timestamp\":\"Mon Aug 15 2022 10:33:31 GMT+0800 (China Standard Time)\",\"constraint-size\":{\"width\":375,\"height\":100,\"zoom\":1},\"dependencies\":{\"scroll_test_item\":\"0.0.0\"}},\"layers\":[{\"id\":\"scroll_test_item\",\"uid\":\"6bed510c5599346befc4f5096a942900\",\"type\":\"gaia-template\",\"sub-type\":\"custom\"}]}",
                //             "index.mock": "{\n  \"nodes\":[\n    {},\n    {},\n    {},\n    {},\n    {}\n  ]\n}"
                //         }
                //     }
                // }
                else if (data.id == 103) {
                    const params = data.result;

                    const templateId = params.templateId;
                    const templateJson = this.createTemplateData(params);

                    this.listener.onAddData(templateId, templateJson);

                    // {
                    //     "id": "scroll_test",
                    //     "uid": "e279ed9a9c723616f12e77f8874a52ea",
                    //     "type": "gaia-template",
                    //     "sub-type": "scroll",
                    //     "row-spacing": 9,
                    //     "item-spacing": 12,
                    //     "direction": "horizontal",
                    //     "edge-insets": "{0,0,0,0}",
                    //     "package": {
                    //         "id": "scroll_test",
                    //         "version": "0.0.1",
                    //         "modify-timestamp": "Mon Aug 15 2022 10:43:48 GMT+0800 (China Standard Time)",
                    //         "constraint-size": {
                    //             "width": 375,
                    //             "height": 100,
                    //             "zoom": 1
                    //         },
                    //         "dependencies": {
                    //             "scroll_test_item": "0.0.0"
                    //         }
                    //     },
                    //     "layers": [
                    //         {
                    //             "id": "scroll_test_item",
                    //             "uid": "6bed510c5599346befc4f5096a942900",
                    //             "type": "gaia-template",
                    //             "sub-type": "custom"
                    //         }
                    //     ]
                    // }
                    const index_json = JSON.parse(templateJson['index.json']);

                    const dependencies = index_json['package']['dependencies']
                    if (dependencies != null) {
                        Object.keys(dependencies).forEach((key) => {
                            this.subTemplateCount += 1;
                            // console.log('Key : ' + key + ', Value : ' + dependencies[key])
                            if (key != null) {
                                task.send({
                                    data: JSON.stringify({
                                        jsonrpc: '2.0',
                                        method: 'template/getTemplateData',
                                        params: {
                                            id: key
                                        },
                                        id: 104
                                    }),
                                })
                            }
                        })
                    }

                    this.mainTemplateId = templateId;

                    if (this.subTemplateCount == 0) {
                        this.listener.onUpdate(this.mainTemplateId);
                        this.mainTemplateId = "";
                    }

                }
                else if (data.id == 104) {
                    this.subTemplateCount -= 1;

                    const params = data.result;

                    const templateId = params.templateId;
                    const templateJson = this.createTemplateData(params);

                    this.listener.onAddData(templateId, templateJson);

                    const index_json = JSON.parse(templateJson['index.json']);

                    const dependencies = index_json['package']['dependencies']
                    if (dependencies != null) {
                        Object.keys(dependencies).forEach((key) => {
                            this.subTemplateCount += 1;
                            // console.log('Key : ' + key + ', Value : ' + dependencies[key])
                            if (key != null) {
                                task.send({
                                    data: JSON.stringify({
                                        jsonrpc: '2.0',
                                        method: 'template/getTemplateData',
                                        params: {
                                            id: key
                                        },
                                        id: 104
                                    }),
                                })
                            }
                        })
                    }

                    if (this.subTemplateCount == 0) {
                        this.listener.onUpdate(this.mainTemplateId);
                        this.mainTemplateId = "";
                    }
                }
                else {
                    if (this.lastTemplateId != "") {
                        task.send({
                            data: JSON.stringify({
                                jsonrpc: '2.0',
                                method: 'template/getTemplateData',
                                params: {
                                    id: this.lastTemplateId
                                },
                                id: 103
                            }),
                        })
                    }
                }
            })
            task.onError(() => {
                // console.log('GXFastPreview onError')
            })
            task.onClose((e) => {
                // console.log('GXFastPreview onClose: ', e)
            })
        })
    }

    // /**
    // * 嵌套模板需要获取依赖模板，手动输入需要ID
    // */
    // sendObtainChildrenTemplateDataMsg(templateId: String) {
    //     JSONObject data = new JSONObject();
    //     data.put("jsonrpc", "2.0");
    //     data.put("method", "template/getTemplateData");
    //     JSONObject params = new JSONObject();
    //     params.put("id", templateId);
    //     data.put("params", params);
    //     data.put("id", JSON_RPC_ID_OBTAIN_CHILDREN_TEMPLATE_DATA);

    //     WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString());
    // }

    stopFastPreview() {
        this.socketTask.close({
            success: () => {
                // console.log('GXFastPreview disconnect success')
            }
        });
    }

    setListener(listener: IGXFastPreviewListener) {
        this.listener = listener;
    }

    createTemplateData(params: any): any {
        const templateJson = {};

        const index_json = params.data['index.json']?.trim();
        const index_css = params.data['index.css']?.trim();
        const index_js = params.data['index.js']?.trim();
        const index_data_binding = params.data['index.databinding']?.trim();
        const index_mock = params.data['index.mock']?.trim();
        const index_data = params.data['index.data']?.trim();

        if ((index_mock == undefined || index_mock == null) && index_data != undefined && index_data != '{}') {
            templateJson['index.databinding'] = JSON.stringify({
                data: JSON.parse(index_data)
            });
        } else {
            templateJson['index.databinding'] = index_data_binding;
        }

        if (index_mock != undefined) {
            templateJson['index.mock'] = JSON.parse(index_mock);
        }

        templateJson['index.json'] = index_json;
        templateJson['index.css'] = index_css;
        templateJson['index.js'] = index_js;
        return templateJson;
    }
}


export interface IGXFastPreviewListener {
    onUpdate(templateId: string)
    onAddData(templateId: string, template: any)
}

export const GXFastPreviewInstance = new GXFastPreview();