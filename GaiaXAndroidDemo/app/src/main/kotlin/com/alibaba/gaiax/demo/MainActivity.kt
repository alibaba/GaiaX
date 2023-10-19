package com.alibaba.gaiax.demo

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.devtools.DevTools
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
import com.alibaba.gaiax.demo.source.GXManualPushSource
import com.alibaba.gaiax.demo.utils.GXExtensionMultiVersionExpression
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.adapter.GXJSEngineProxy
import com.alibaba.gaiax.studio.GXStudioClient
import com.alibaba.gaiax.studio.GX_CONNECT_URL
import com.alibaba.gaiax.studio.loadInLocal
import com.lzf.easyfloat.permission.PermissionUtils


class MainActivity : AppCompatActivity() {

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val scanResult =
                it.data?.getStringExtra("SCAN_RESULT") ?: return@registerForActivityResult
            Log.d("MainActivity", "StartActivityForResult() called scanResult = $scanResult")
            val intent = Intent(MainActivity@ this, GXFastPreviewActivity::class.java)
            intent.putExtra(GXFastPreviewActivity.GAIA_STUDIO_URL, scanResult)
            startActivity(intent)
        }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.fastpreview -> {
                if (Build.MODEL.contains("Android SDK") || Build.MODEL.contains("sdk_gphone64_x86_64")) {
                    val intent = Intent(MainActivity@ this, GXFastPreviewActivity::class.java)
                    // 9001
                    // 9292
                    intent.putExtra(
                        "GAIA_STUDIO_URL",
                        "gaiax://gaiax/preview?url=ws://30.78.146.57:9292&id=test-template&type=auto"
                    )
                    launcher.launch(intent)
                } else {
                    val intent = Intent(MainActivity@ this, GXQRCodeActivity::class.java)
                    launcher.launch(intent)
                }
                true
            }

            R.id.devToolButton -> {
                DevTools.instance.createDevToolsFloatWindow(
                    this
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        initGXTemplateEngine()
        initGXJSEngine()
        initGXStudio()
        initGXDevTools()
        initRouters()
    }

    private fun initRouters() {
        findViewById<AppCompatButton>(R.id.normal_template)?.setOnClickListener {
            DevTools.instance.createDevToolsFloatWindow(
                this
            )
            val intent = Intent(MainActivity@ this, NormalTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.nest_template)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, NestTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.container_template)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, ContainerTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.databinding)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, DataTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.event)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, EventTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.track)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, TrackTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.style)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, StyleTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.js)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, JSTemplateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initGXDevTools() {
        if (PermissionUtils.checkPermission(applicationContext)) {
            DevTools.instance.createDevToolsFloatWindow(
                applicationContext
            )
        }
    }

    private fun initGXStudio() {
        // GXStudioClient
        GXStudioClient.instance.init(this)

        // 自动重连时，提前初始化
        val result = loadInLocal(this, GX_CONNECT_URL)
        JSONObject.parseObject(result)?.let {
            DevTools.instance.connectReally(this, it)
        }

        // 设置与JS模块的通信逻辑
        // 注册Socket消息发送者
        GXJSEngineProxy.instance.setSocketSender(object : GXJSEngine.ISocketSender {
            override fun onSendMsg(data: JSONObject) {
                GXStudioClient.instance.sendMsg(data)
            }
        })

        // 设置与JS模块的通信逻辑
        // 注册消息接受者
        GXStudioClient.instance.setSocketReceiver(object : GXStudioClient.ISocketReceiver {
            override fun onReceiveCallSync(socketId: Int, params: JSONObject) {
                GXJSEngineProxy.instance.getSocketBridge()?.callSync(socketId, params)
            }

            override fun onReceiveCallAsync(socketId: Int, params: JSONObject) {
                GXJSEngineProxy.instance.getSocketBridge()?.callAsync(socketId, params)
            }

            override fun onReceiveCallPromise(socketId: Int, params: JSONObject) {
                GXJSEngineProxy.instance.getSocketBridge()?.callPromise(socketId, params)
            }

            override fun onReceiveCallGetLibrary(socketId: Int, methodName: String) {
                GXJSEngineProxy.instance.getSocketBridge()?.callGetLibrary(socketId, methodName)
            }

        })
    }

    private fun initGXJSEngine() {
        GXJSEngineProxy.instance.init(this)
        GXJSEngineProxy.instance.startDefaultEngine()
    }

    private fun initGXTemplateEngine() {
        GXTemplateEngine.instance.init(this)

        GXRegisterCenter.instance.registerExtensionExpression(GXExtensionMultiVersionExpression())

        GXRegisterCenter.instance.registerExtensionException(object :
            GXRegisterCenter.GXIExtensionException {
            override fun exception(exception: Exception) {
                exception.printStackTrace()
            }
        })

        GXRegisterCenter.instance.registerExtensionTemplateSource(GXManualPushSource.instance, 101)
            .registerExtensionTemplateSource(GXFastPreviewSource.instance, 102)

        GXRegisterCenter.instance.registerExtensionFontFamily(object :
            GXRegisterCenter.GXIExtensionFontFamily {
            override fun fontFamily(fontFamilyName: String): Typeface? {
                if (fontFamilyName == "iconfont") {
                    return Typeface.createFromAsset(assets, "$fontFamilyName.ttf")
                }
                return null
            }
        })
    }

    override fun onDestroy() {
        destroyGXStudio()
        destroyGXJSEngine()
        destroyGXTemplateEngine()
        super.onDestroy()
    }

    private fun destroyGXTemplateEngine() {
    }

    private fun destroyGXJSEngine() {
        GXJSEngineProxy.instance.stopDefaultEngine()
    }

    private fun destroyGXStudio() {
        GXStudioClient.instance.destroy()
    }
}
