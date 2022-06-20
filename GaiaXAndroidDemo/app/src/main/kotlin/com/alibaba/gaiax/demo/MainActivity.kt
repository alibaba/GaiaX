package com.alibaba.gaiax.demo

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.fastpreview.GaiaXFastPreviewActivity
import com.alibaba.gaiax.fastpreview.GaiaXQRCodeActivity

class MainActivity : AppCompatActivity() {

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val scanResult =
                it.data?.getStringExtra("SCAN_RESULT") ?: return@registerForActivityResult
            Log.d("MainActivity", "StartActivityForResult() called scanResult = $scanResult")
            val intent = Intent(MainActivity@ this, GaiaXFastPreviewActivity::class.java)
            intent.putExtra(GaiaXFastPreviewActivity.GAIA_STUDIO_URL, scanResult)
            startActivity(intent)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        GXTemplateEngine.instance.init(this)

        // 注册IconFont字体的加载逻辑
        GXRegisterCenter.instance.registerExtensionFontFamily(object :
            GXRegisterCenter.GXIExtensionFontFamily {
            override fun fontFamily(fontFamilyName: String): Typeface? {
                if (fontFamilyName == "iconfont") {
                    return Typeface.createFromAsset(assets, "$fontFamilyName.ttf")
                }
                return null
            }
        })

        findViewById<AppCompatButton>(R.id.fastpreview)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, GaiaXQRCodeActivity::class.java)
            launcher.launch(intent)
        }
        findViewById<AppCompatButton>(R.id.normal_template)?.setOnClickListener {
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

        findViewById<AppCompatButton>(R.id.api)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, ApiTemplateActivity::class.java)
            startActivity(intent)
        }
    }
}
