package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.youku.gaiax.js.GaiaXJS

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */
class JavascriptTemplateActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_template)

        renderTemplate1(this)
    }

    private fun renderTemplate1(activity: JavascriptTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        var delegate = IJSDelegate { Log.d("lms-13", "hehehe") }



        GaiaXJS.instance.init(activity).initListener(object : GaiaXJS.Listener{
            override fun errorLog(data: JSONObject) {

            }

            override fun monitor(
                scene: String,
                biz: String,
                id: String,
                type: String,
                state: String,
                value: Long,
                jsModuleName: String,
                jsApiName: String,
                jsApiType: String
            ) {
//                GaiaXProxy.instance.monitor?.monitor(scene, biz, id, type, state, value)
            }

        })

        GaiaXJS.instance.startEngine {  }

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-vertical-item")

        GaiaXJS.instance.registerComponent("templates","gx-vertical-item","111","//------ts start------\n" +
                "//UEsDBBQAAAgIAKGbcVXUHUeILAEAAP4BAAAIAAAAaW5kZXguanNzzs8tyM9LzSvRqOZSUCguSSxJtVKortUBcvLzgjPyy60U0krzkksy8/MUNDQVQIoUFPT1n63vf7Zm4bMVC5/N3f9sxr7nS3a9bN77ZM+sp7PnvdjQDFQENSEoNTGlEocREM07Nj2bP/npup5nHROeTuh7PqsFYsT7PT3Pp8x/OnHv04krns2Z/7St9Wn/KojUkx0NQLuhpjiUFOtmpuflF6WCBUJLMnOK9YqB7g7JTywGekqhJLMkB+glpaxihfTMkozSJCWFWk1rhAs9MlNSCfvx5awJL6b3o/lRQSE5P684PydVLyc/XQNkA8Q4JWTzg1JLi3FZADb66ZLe51NWPJu+DT3sXFKLS4ry8Ybe0w1tL5Y1vmzvejZlHzzocLgMZFx+USXccaBQAABQSwECFAMUAAAICAChm3FV1B1HiCwBAAD+AQAACAAAAAAAAAAAAAAApIEAAAAAaW5kZXguanNQSwUGAAAAAAEAAQA2AAAAUgEAAAAA\n" +
                "//------ts end------\n" +
                "\n" +
                "Component({state:{},onShow:function(){},onReady:function(){VIP.showToast({title:\"js github\"});},onHide:function(){console.log(\"js onHide\")},onReuse:function(){},onDestroy:function(){console.log(\"js onDestory\")}});")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(100F.dpToPx(), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "assets_data_source/data/vertical-item.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)

        GaiaXJS.instance.onReadyComponent(6)
    }

    override fun onPause() {
        super.onPause()
        GaiaXJS.instance.onHiddenComponent(6)
    }

    override fun onDestroy() {
        super.onDestroy()
        GaiaXJS.instance.onDestroyComponent(6)
    }
}