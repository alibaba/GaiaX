package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.visly.stretch.Node
import app.visly.stretch.Stretch
import app.visly.stretch.Style
import com.alibaba.gaiax.utils.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


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

    @Test
    fun multi_thread_node_set_style_null_pointer() {
        Stretch.init()

        val c1 = CountDownLatch(1)
        val c2 = CountDownLatch(1)
        val c3 = CountDownLatch(1)
        var safeSetStyle = false
        val style2 = Style()

        Thread {
            c2.await()
            Thread {
                style2.safeFree()
            }.start()
            Thread {
                c1.countDown()
            }.start()
        }.start()

        Thread {
            val style1 = Style()
            style1.safeInit()
            val node = Node("node", style1)
            style2.safeInit()
            safeSetStyle = node.safeSetStyle(style2) {
                c2.countDown()
                c1.await()
                c3.countDown()
            }
        }.start()

        c3.await()

        Assert.assertEquals(true, safeSetStyle)
    }

}