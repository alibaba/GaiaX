package com.alibaba.gaiax.demo

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.devtools.DevTools
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.alibaba.gaiax.demo.list.clicklatency.NestedRecyclerActivity
import com.alibaba.gaiax.demo.list.util.ClickTrace
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
import com.alibaba.gaiax.demo.source.GXManualPushSource
import com.alibaba.gaiax.demo.utils.GXExtensionMultiVersionExpression
import com.alibaba.gaiax.studio.GXClientToStudioMultiType
import com.alibaba.gaiax.studio.GX_CONNECT_URL
import com.alibaba.gaiax.studio.loadInLocal
import com.lzf.easyfloat.permission.PermissionUtils
import com.youku.gaiax.js.GXJSEngineFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                TopAppBar(title = { Text(text = "GaiaXDemo App") }, actions = {
                    Row {
                        IconButton(
                            modifier = Modifier.padding(12.dp, 12.dp, 8.dp, 12.dp),
                            onClick = {
                                fastPreview()
                            }) {
                            Icon(
                                painter = painterResource(id = R.drawable.qr_code_scanner),
                                contentDescription = null // decorative element
                            )
                        }
                        IconButton(
                            modifier = Modifier.padding(6.dp, 6.dp, 4.dp, 6.dp),
                            onClick = {
                            }) {
                            val status = rememberSaveable() {
                                mutableStateOf(false)
                            }
                            Switch(checked = status.value, enabled = true, onCheckedChange = {
                                status.value = if (it) {
                                    if (PermissionUtils.checkPermission(applicationContext)) {
                                        DevTools.instance.createDevToolsFloatWindow(
                                            applicationContext
                                        )
                                    } else {
                                        DevTools.instance.createDevToolsFloatWindow(
                                            applicationContext
                                        )
                                    }
                                    true
                                } else {
                                    DevTools.instance.dismissDevTools()
                                    false
                                }
                            })
                        }
                    }
                })
                ActivityList()
            }
        }

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

        //GaiaXJS初始化
        GXJSEngineFactory.instance.init(this)
        //GaiaXJS引擎启动
        GXJSEngineFactory.instance.startEngine()

        autoConnect()
    }

    private fun fastPreview() {
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
    }

    @Composable
    fun ActivityList() {
        Column {
            Button(name = "Normal Template") {
                launchActivityWithTrace<NormalTemplateActivity>()
            }
            Button(name = "Container Template") {
                launchActivityWithTrace<ContainerTemplateActivity>()
            }
            Button(name = "Nest Template") {
                launchActivityWithTrace<NestTemplateActivity>()
            }
            Button(name = "Data Binding") {
                launchActivityWithTrace<DataTemplateActivity>()
            }
            Button(name = "Event Binding") {
                launchActivityWithTrace<EventTemplateActivity>()
            }
            Button(name = "Track Binding") {
                launchActivityWithTrace<TrackTemplateActivity>()
            }
            Button(name = "Remote Data Source") {
                launchActivityWithTrace<RemoteDataSourceTemplateActivity>()
            }
            Button(name = "Style") {
                launchActivityWithTrace<StyleTemplateActivity>()
            }
            Button(name = "Nested RecyclerView") {
                launchActivityWithTrace<NestedRecyclerActivity>()
            }

            Button(name = "JS Template") {
                launchActivityWithTrace<JavascriptTemplateActivity>()
            }
        }

//        findViewById<SwitchCompat>(R.id.dev_tools)?.setOnCheckedChangeListener { p0, result ->
//            if (result) {
//                if (PermissionUtils.checkPermission(applicationContext)) {
//                    DevTools.instance.createDevToolsFloatWindow(applicationContext)
//                } else {
//                    DevTools.instance.createDevToolsFloatWindow(applicationContext)
//                }
//            } else {
//                DevTools.instance.dismissDevTools()
//            }
//        }
    }

    @Composable
    fun Button(name: String, onClick: () -> Unit) {
        TextButton(
            modifier = Modifier.padding(8.dp),
            onClick = onClick,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(name)
        }
    }

    private inline fun <reified T : Activity> launchActivityWithTrace(base: Intent? = null) {
        ClickTrace.onClickPerformed()
        val intent = Intent(this, T::class.java)
        if (base != null) {
            intent.putExtras(base)
        }
        startActivity(intent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val scanResult =
                it.data?.getStringExtra("SCAN_RESULT") ?: return@registerForActivityResult
            Log.d("MainActivity", "StartActivityForResult() called scanResult = $scanResult")
            val intent = Intent(MainActivity@ this, GXFastPreviewActivity::class.java)
            intent.putExtra(GXFastPreviewActivity.GAIA_STUDIO_URL, scanResult)
            startActivity(intent)
        }

    private fun autoConnect() {
        GXClientToStudioMultiType.instance.init(this)
        val result = loadInLocal(this, GX_CONNECT_URL)
        JSONObject.parseObject(result)?.let {
            DevTools.instance.connectReally(this, it)
        }
    }

    override fun onDestroy() {
        GXClientToStudioMultiType.instance.destroy()
        super.onDestroy()
    }

}