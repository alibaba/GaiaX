Component({
  state: {},
  onShow: function () {
    //每次模板显示时回调
  },
  onReady: function () {
    //模板渲染完成后回调，生命周期内只回调一次
    let view = this.getElementById("button-title");
    if (view) {
      view.addEventListener("click", (event) => {
        console.log("add Event success");
        window.postMessage("CustomNotificationNameForTemplate", {
          Key1: "messageFromDemo Value1",
        });
        // //@ts-ignore
        // Example.log("invoke Example log method");
        // //@ts-ignore
        // SwiftExample.log("invoke Example log method");
      });
    }
  },
  onHide: function () {
    //每次模板隐藏时回调
  },
  onReuse: function () {
    //模板复用时回调
  },
  onDestroy: function () {
    //模板实例释放时回调
  },
});
