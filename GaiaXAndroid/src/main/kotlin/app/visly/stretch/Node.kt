package app.visly.stretch

import androidx.annotation.Keep
import java.lang.ref.WeakReference

/**
 * @suppress
 */
interface MeasureFunc {
    fun measure(constraints: Size<Float?>): Size<Float>
}

/**
 * @suppress
 */
private class MeasureFuncImpl(val measureFunc: WeakReference<MeasureFunc>) {

    fun measure(width: Float, height: Float): FloatArray {
        val result = measureFunc.get()!!
            .measure(Size(if (width.isNaN()) null else width, if (height.isNaN()) null else height))
        return floatArrayOf(result.width, result.height)
    }
}

/**
 * @suppress
 */
@Keep
open class Node {
    companion object {

        init {
            Stretch.init()
        }
    }

    val id: String
    private var rustptr: Long
    private var style: Style
    private var children: MutableList<Node>
    private var measure: MeasureFunc? = null

    constructor(id: String, style: Style, measure: MeasureFunc) {
        synchronized(Stretch::class.java) {
            this.id = id
            this.rustptr =
                nConstructLeaf(Stretch.ptr, style.rustptr, MeasureFuncImpl(WeakReference(measure)))
            this.style = style
            this.children = mutableListOf()
            this.measure = measure
        }
    }

    constructor(id: String, style: Style, children: List<Node>) {
        synchronized(Stretch::class.java) {
            this.id = id
            this.rustptr = nConstruct(
                Stretch.ptr,
                style.rustptr,
                LongArray(children.size) { children[it].rustptr })
            this.style = style
            this.children = children.toMutableList()
        }
    }

    constructor(id: String, style: Style) {
        synchronized(Stretch::class.java) {
            val children: List<Node> = mutableListOf()
            this.id = id
            this.rustptr = nConstruct(
                Stretch.ptr,
                style.rustptr,
                LongArray(children.size) { children[it].rustptr })
            this.style = style
            this.children = children.toMutableList()
        }
    }

    fun safeFree() {
        synchronized(Stretch::class.java) {
            style.free()
            free()
        }
    }

    private fun free() {
        if (rustptr != -1L) {
            nFree(Stretch.ptr, rustptr)
            rustptr = -1
        }
    }

    fun getChildren(): List<Node> {
        return this.children
    }

    fun safeAddChild(child: Node) {
        synchronized(Stretch::class.java) {
            if (rustptr != -1L && child.rustptr != -1L) {
                nAddChild(Stretch.ptr, rustptr, child.rustptr)
                children.add(child)
            }
        }
    }

    fun getStyle(): Style {
        return this.style
    }

    fun safeSetStyle(style: Style): Boolean {
        synchronized(Stretch::class.java) {
            if (rustptr != -1L && style.rustptr != -1L) {
                nSetStyle(Stretch.ptr, rustptr, style.rustptr)
                this.style = style
                return true
            } else {
                return false
            }
        }
    }


    fun safeSetStyle(style: Style, locked: (() -> Unit)? = null): Boolean {
        synchronized(Stretch::class.java) {
            if (rustptr != -1L && style.rustptr != -1L) {
                locked?.invoke()
                if (style.rustptr == -1L) {
                    return false
                }
                nSetStyle(Stretch.ptr, rustptr, style.rustptr)
                this.style = style
                return true
            } else {
                return false
            }
        }
    }


    fun safeMarkDirty() {
        synchronized(Stretch::class.java) {
            if (rustptr != -1L) {
                nMarkDirty(Stretch.ptr, rustptr)
            }
        }
    }

    fun safeComputeLayout(size: Size<Float?>): Layout {
        synchronized(Stretch::class.java) {
            if (rustptr != -1L) {
                val args = nComputeLayout(
                    Stretch.ptr,
                    rustptr,
                    // FIX: 修复一个奇怪的BUG，整数传入，会导致一些层级节点计算为空
                    (size.width?.minus(0.01F)) ?: Float.NaN,
                    (size.height?.minus(0.01F)) ?: Float.NaN
                )
                val result = Layout.fromFloatArray(args, 0)
                return result.second
            } else {
                throw IllegalArgumentException("rustptr is null")
            }
        }
    }

    private external fun nConstruct(stretch: Long, style: Long, children: LongArray): Long
    private external fun nConstructLeaf(stretch: Long, style: Long, measure: MeasureFuncImpl): Long
    private external fun nFree(stretch: Long, ptr: Long)
    private external fun nSetMeasure(stretch: Long, ptr: Long, measure: MeasureFuncImpl)
    private external fun nSetChildren(stretch: Long, ptr: Long, children: LongArray)
    private external fun nAddChild(stretch: Long, ptr: Long, child: Long)
    private external fun nReplaceChildAtIndex(
        stretch: Long,
        ptr: Long,
        index: Int,
        child: Long
    ): Long

    private external fun nRemoveChild(stretch: Long, ptr: Long, child: Long): Long
    private external fun nRemoveChildAtIndex(stretch: Long, ptr: Long, index: Int): Long
    private external fun nSetStyle(stretch: Long, ptr: Long, args: Long): Boolean
    private external fun nIsDirty(stretch: Long, ptr: Long): Boolean
    private external fun nMarkDirty(stretch: Long, ptr: Long)
    private external fun nComputeLayout(
        stretch: Long,
        ptr: Long,
        width: Float,
        height: Float
    ): FloatArray

    fun markDirtyAll() {
        safeMarkDirty()
        children.forEach {
            it.markDirtyAll()
        }
    }

    override fun toString(): String {
        return "Node(id='$id', style=$style, children=$children)"
    }

}