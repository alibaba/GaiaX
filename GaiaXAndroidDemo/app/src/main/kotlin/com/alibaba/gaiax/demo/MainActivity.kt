package com.alibaba.gaiax.demo

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
import com.alibaba.gaiax.demo.source.GXManualPushSource
import com.alibaba.gaiax.demo.utils.GXExtensionMultiVersionExpression
import com.alibaba.gaiax.studio.GXClientToStudio


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
                val intent = Intent(MainActivity@ this, GXQRCodeActivity::class.java)
                launcher.launch(intent)
                true
            }
            R.id.fastpreview_emulator -> {
                val intent = Intent(MainActivity@ this, GXFastPreviewActivity::class.java)
                // 9001
                // 9292
                intent.putExtra(
                    "GAIA_STUDIO_URL",
                    "gaiax://gaiax/preview?url=ws://30.78.146.17:9001&id=test-template&type=auto"
                )
                launcher.launch(intent)
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
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

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

        findViewById<AppCompatButton>(R.id.track)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, TrackTemplateActivity::class.java)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.style)?.setOnClickListener {
            val intent = Intent(MainActivity@ this, StyleTemplateActivity::class.java)
            startActivity(intent)
        }
    }
}
