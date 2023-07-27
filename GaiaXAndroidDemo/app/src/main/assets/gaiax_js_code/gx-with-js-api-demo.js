Component({
  state: {},
  onShow: function () {
    //每次模板显示都会回调
  },
  onReady: function () {
    //模板渲染完成后的回调
    this.registerStorageAPI();
    this.registerTipsAPI();
    this.registerTimerAPI();
    // this.registerCustomAPI();
    this.registerMessageAPI();
  },
  onHide: function () {
    console.log("js-api-demo onHiden");
    //每次模板隐藏都会回调
  },
  onReuse: function () {
    //模板复用时回调
  },
  onDestroy: function () {
    console.log("js-api-demo onDistory");
    //模板将要释放的回调
  },
  registerMessageAPI: function () {
    let addEventBtn = this.getElementById("addEventListener");
    if (addEventBtn) {
      addEventBtn.addEventListener("click", (event) => {
        console.log("added listener for other templates");
        this.addEventListener("CustomNotificationNameForTemplate", (event) => {
          console.log(event.data);
        });
      });
    }
    let removeEventBtn = this.getElementById("removeEventListener");
    if (removeEventBtn) {
      removeEventBtn.addEventListener("click", (event) => {
        console.log("removed listener for other templates");
        this.removeEventListener("CustomNotificationNameForTemplate");
      });
    }
    let addNativeEventBtn = this.getElementById("addNativeListener");
    if (addNativeEventBtn) {
      addNativeEventBtn.addEventListener("click", (event) => {
        console.log("added listener for native code");
        this.addNativeEventListener(
          "CustomNotificationNameForNative",
          (event) => {
            console.log(event.data);
          }
        );
      });
    }
    let removeNativeEventBtn = this.getElementById("removeNativeListener");
    if (removeNativeEventBtn) {
      removeNativeEventBtn.addEventListener("click", (event) => {
        console.log("removed listener for native code");
        this.removeNativeEventListener("CustomNotificationNameForNative");
      });
    }
  },
  registerStorageAPI: function () {
    let getStorage = this.getElementById("getStorage");
    if (getStorage) {
      getStorage.addEventListener("click", async (event) => {
        let value = await gaiax.getStorage("key1");
        console.log(value);
      });
    }

    let setStorage = this.getElementById("setStorage");
    if (setStorage) {
      setStorage.addEventListener("click", async (event) => {
        await gaiax.setStorage("key1", {
          value1: "value: this is a test method for getStorage()",
        });
      });
    }

    let removeStorage = this.getElementById("removeStorage");
    if (removeStorage) {
      removeStorage.addEventListener("click", async (event) => {
        await gaiax.removeStorage("key1");
      });
    }
  },
  registerTipsAPI: function () {
    let showAlert = this.getElementById("showAlert");
    if (showAlert) {
      showAlert.addEventListener("click", async (event) => {
        gaiax.showAlert(
          { title: "我是标题", message: "我是消息" },
          (result) => {
            console.log(result);
          }
        );
      });
    }

    this.registerCustomAPI("showDialog", 123456);

    let showToast = this.getElementById("showToast");
    if (showToast) {
      showToast.addEventListener("click", async (event) => {
        gaiax.showToast({ title: "我是标题", duration: 5 }, () => {
          console.log("ShowToast");
        });

        gaiax.showToast({ title: "我是标题4" });
      });
    }
  },
  registerDeviceAPI: function () {
    let getSystemInfo = this.getElementById("getSystemInfo");
    if (getSystemInfo) {
      getSystemInfo.addEventListener("click", async (event) => {
        let systemInfo = gaiax.getSystemInfo();
        console.log(systemInfo);
      });
    }
  },
  registerTimerAPI: function () {
    let setInterval1 = this.getElementById("setInterval");
    if (setInterval1) {
      setInterval1.addEventListener("click", async (event) => {
        let count = 0;
        let intervalId = setInterval(() => {
          console.log(count++);
          console.log(intervalId);
          clearInterval(intervalId);
        }, 1000);
        this.setState({ intervalId }, () => {});
      });
    }

    let clearInterval1 = this.getElementById("clearInterval");
    if (clearInterval1) {
      clearInterval1.addEventListener("click", async (event) => {
        let { intervalId } = this.state;
        if (intervalId) {
          clearInterval(intervalId);
          console.log(`定时已清除 = ${intervalId}`);
        }
      });
    }

    let setTimeout1 = this.getElementById("setTimeout");
    if (setTimeout1) {
      setTimeout1.addEventListener("click", async (event) => {
        let timeoutId = setTimeout(() => {
          console.log("延时");
        }, 5000);
        this.setState({ timeoutId });
      });
    }

    let clearTimeout1 = this.getElementById("clearTimeout");
    if (clearTimeout1) {
      clearTimeout1.addEventListener("click", async (event) => {
        let { timeoutId } = this.state;
        if (timeoutId) {
          clearTimeout(timeoutId);
          console.log(`定时已清除 = ${timeoutId}`);
        }
      });
    }
  },
  registerCustomAPI: function (name, dialogIdentifierId) {
    let showCustomDialog = this.getElementById(name);
    if (showCustomDialog) {
      showCustomDialog.addEventListener("click", async (event) => {
        gaiax.showCustomDialog(
          {
            identifierId: dialogIdentifierId + "_",
            templateId: "yk-vip-task-item2",
            bizId: "yk-vip",
            measureSize: {
              width: 300,
            },
            templateData: {
              data: {
                button: {
                  unfinishText: "去完成",
                  finishUnAwardText: "领取",
                  awardText: "继续购买",
                  action: {
                    report: {
                      scmAB: "20140719.manual",
                      trackInfo: {
                        drawerid: "24634",
                        servertime: 1632475740507,
                        object_title: "买会员送成长值",
                        component_id: "GROUTH_TASK",
                        cms_req_id: "21042a3d16324757405043589e9b77",
                        itemid: 581527,
                      },
                      scmC: "24634",
                      spmC: "drawer1",
                      pageName: "page_guidenode_VIP_GROUTH",
                      spmD: "zj1_1",
                      scmD: "url_https://activity.youku.com/app/ykvip_rax/yk-vip-cashier-plato/pages/index?wh_weex=true&hideNavigatorBar=true&sceneType=fullScreen&h5params=%7B%22pageKey%22%3A%22STANDARDRENDER_YOUKU%22%7D",
                      spmAB: "a2h8d.VIP_GROUTH",
                      index: 1,
                    },
                    type: "JUMP_TO_URL",
                    value:
                      "https://activity.youku.com/app/ykvip_rax/yk-vip-cashier-plato/pages/index?wh_weex=true&hideNavigatorBar=true&sceneType=fullScreen&h5params=%7B%22pageKey%22%3A%22STANDARDRENDER_YOUKU%22%7D",
                  },
                },
                actId: 10821,
                taskId: 12893,
                taskIcon:
                  "http://ykimg.alicdn.com/develop/image/2020-11-18/0af00222d1041a936c7f66e87b4a8cdc.png",
                grouthText: "至少+800成长值",
                taskName: "买会员助成长",
                udouMulti: "2.2",
                needReport: 1,
                groupId: 10459,
                "@entryPath": "581527,90289,0",
                taskState: 1,
              },
            },
          },
          () => {
            console.log("showCustomDialog");
          }
        );

        setTimeout(() => {
          gaiax.dismissCustomDialog(dialogIdentifierId);
        }, 5000);
      });
    }
  },
});
