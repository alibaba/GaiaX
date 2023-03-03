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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
import com.alibaba.gaiax.demo.source.GXManualPushSource
import com.alibaba.gaiax.demo.utils.GXExtensionMultiVersionExpression
import com.alibaba.gaiax.studio.GXClientToStudio

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
                    }
                })
                ActivityList()
            }
        }

        GXTemplateEngine.instance.init(this)

        GXClientToStudio.instance.init(applicationContext)

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