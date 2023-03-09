package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.visly.stretch.Node
import app.visly.stretch.Stretch
import app.visly.stretch.Style
import com.alibaba.gaiax.utils.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXStretchTest : GXBaseTest() {

    @Test
    fun node_set_style_null_pointer() {
        Stretch.init()
        val style1 = Style()
        style1.safeInit()
        val node = Node("node", style1)
        val style2 = Style()
        style2.safeInit()
        style2.safeFree()
        val safeSetStyle = node.safeSetStyle(style2)

        Assert.assertEquals(false, safeSetStyle)
    }
}