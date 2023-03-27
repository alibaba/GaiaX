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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
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
import com.alibaba.gaiax.studio.GXClientToStudio
import com.lzf.easyfloat.permission.PermissionUtils
import com.youku.gaiax.js.GaiaXJSManager


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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        GXClientToStudio.instance.destroy()
        super.onDestroy()
    }

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
        GaiaXJSManager.instance.init(this.baseContext)
        //GaiaXJS引擎启动
        GaiaXJSManager.instance.startEngine { }
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
        }
        findViewById<AppCompatButton>(R.id.js)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, JavascriptTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<SwitchCompat>(R.id.dev_tools)?.setOnCheckedChangeListener { p0, result ->
            if (result) {
                if (PermissionUtils.checkPermission(applicationContext)) {
                    DevTools.instance.createDevToolsFloatWindow(applicationContext)
                } else {
                    DevTools.instance.createDevToolsFloatWindow(applicationContext)
                }
            } else {
                DevTools.instance.dismissDevTools()
            }
        }
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

    override fun onDestroy() {
        GXClientToStudio.instance.destroy()
        super.onDestroy()
    }

}