package app.visly.stretch

import androidx.annotation.Keep


/**
 * @suppress
 */
enum class AlignItems {
    FlexStart,
    FlexEnd,
    Center,
    Baseline,
    Stretch,
}

/**
 * @suppress
 */
enum class AlignSelf {
    Auto,
    FlexStart,
    FlexEnd,
    Center,
    Baseline,
    Stretch,
}

/**
 * @suppress
 */
enum class AlignContent {
    FlexStart,
    FlexEnd,
    Center,
    Stretch,
    SpaceBetween,
    SpaceAround,
}

/**
 * @suppress
 */
enum class Direction {
    Inherit,
    LTR,
    RTL,
}

/**
 * @suppress
 */
enum class Display {
    Flex,
    None,
}

/**
 * @suppress
 */
enum class FlexDirection {
    Row,
    Column,
    RowReverse,
    ColumnReverse,
}

/**
 * @suppress
 */
enum class JustifyContent {
    FlexStart,
    FlexEnd,
    Center,
    SpaceBetween,
    SpaceAround,
    SpaceEvenly,
}

/**
 * @suppress
 */
enum class Overflow {
    Visible,
    Hidden,
    Scroll,
}

/**
 * @suppress
 */
enum class PositionType {
    Relative,
    Absolute,
}

/**
 * @suppress
 */
enum class FlexWrap {
    NoWrap,
    Wrap,
    WrapReverse,
}

/**
 * @suppress
 */
sealed class Dimension {

    data class Points(val points: Float) : Dimension()

    data class Percent(val percentage: Float) : Dimension()

    object Undefined : Dimension()

    object Auto : Dimension()

    val type: Int
        get() = when (this) {
            is Points -> 0
            is Percent -> 1
            is Undefined -> 2
            is Auto -> 3
        }

    val value: Float
        get() = when (this) {
            is Points -> this.points
            is Percent -> this.percentage
            is Undefined -> 0f
            is Auto -> 0f
        }

    override fun toString(): String {
        return when (this) {
            is Points -> "Dimension.Points"
            is Percent -> "Dimension.Percent(value=$value)"
            is Undefined -> "Dimension.Undefined"
            is Auto -> "Dimension.Auto"
        }
    }

}

/**
 * @suppress
 */
data class Size<T>(var width: T, var height: T) {

    override fun toString(): String {
        return "Size(width=$width, height=$height)"
    }
}

/**
 * @suppress
 */
data class Rect<T>(var start: T, var end: T, var top: T, var bottom: T) {
    override fun toString(): String {
        return "Rect(start=$start, end=$end, top=$top, bottom=$bottom)"
    }
}

/**
 * @suppress
 */
@Keep
data class Style(
    var display: Display = Display.Flex,
    var positionType: PositionType = PositionType.Relative,
    var direction: Direction = Direction.Inherit,
    var flexDirection: FlexDirection = FlexDirection.Row,
    var flexWrap: FlexWrap = FlexWrap.NoWrap,
    var overflow: Overflow = Overflow.Hidden,
    var alignItems: AlignItems = AlignItems.Stretch,
    var alignSelf: AlignSelf = AlignSelf.Auto,
    var alignContent: AlignContent = AlignContent.FlexStart,
    var justifyContent: JustifyContent = JustifyContent.FlexStart,
    var position: Rect<Dimension> = Rect(
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined
    ),
    var margin: Rect<Dimension> = Rect(
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined
    ),
    var padding: Rect<Dimension> = Rect(
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined
    ),
    var border: Rect<Dimension> = Rect(
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined,
        Dimension.Undefined
    ),
    var flexGrow: Float = 0f,
    var flexShrink: Float = 0f,
    var flexBasis: Dimension = Dimension.Auto,
    var size: Size<Dimension> = Size(Dimension.Auto, Dimension.Auto),
    var minSize: Size<Dimension> = Size(Dimension.Auto, Dimension.Auto),
    var maxSize: Size<Dimension> = Size(Dimension.Auto, Dimension.Auto),
    var aspectRatio: Float? = null
) {

    companion object {
        init {
            Stretch.init()
        }
    }

    var rustptr: Long = -1

    private fun init() {
        rustptr = nConstruct(
            display.ordinal,
            positionType.ordinal,
            direction.ordinal,
            flexDirection.ordinal,
            flexWrap.ordinal,
            overflow.ordinal,
            alignItems.ordinal,
            alignSelf.ordinal,
            alignContent.ordinal,
            justifyContent.ordinal,

            position.start.type,
            position.start.value,
            position.end.type,
            position.end.value,
            position.top.type,
            position.top.value,
            position.bottom.type,
            position.bottom.value,

            margin.start.type,
            margin.start.value,
            margin.end.type,
            margin.end.value,
            margin.top.type,
            margin.top.value,
            margin.bottom.type,
            margin.bottom.value,

            padding.start.type,
            padding.start.value,
            padding.end.type,
            padding.end.value,
            padding.top.type,
            padding.top.value,
            padding.bottom.type,
            padding.bottom.value,

            border.start.type,
            border.start.value,
            border.end.type,
            border.end.value,
            border.top.type,
            border.top.value,
            border.bottom.type,
            border.bottom.value,

            flexGrow,
            flexShrink,

            flexBasis.type,
            flexBasis.value,

            size.width.type,
            size.width.value,

            size.height.type,
            size.height.value,

            minSize.width.type,
            minSize.width.value,

            minSize.height.type,
            minSize.height.value,

            maxSize.width.type,
            maxSize.width.value,

            maxSize.height.type,
            maxSize.height.value,

            aspectRatio ?: Float.NaN
        )
    }

    internal fun free() {
        if (rustptr != -1L) {
            nFree(rustptr)
            rustptr = -1
        }
    }

    fun safeFree() {
        synchronized(Stretch::class.java) {
            free()
        }
    }

    fun safeInit() {
        synchronized(Stretch::class.java) {
            init()
        }
    }

    private external fun nFree(ptr: Long)

    private external fun nConstruct(
        display: Int,
        positionType: Int,
        direction: Int,
        flexDirection: Int,
        flexWrap: Int,
        overflow: Int,
        alignItems: Int,
        alignSelf: Int,
        alignContent: Int,
        justifyContent: Int,

        positionStartType: Int,
        positionStartValue: Float,
        positionEndType: Int,
        positionEndValue: Float,
        positionTopType: Int,
        positionTopValue: Float,
        positionBottomType: Int,
        positionBottomValue: Float,

        marginStartType: Int,
        marginStartValue: Float,
        marginEndType: Int,
        marginEndValue: Float,
        marginTopType: Int,
        marginTopValue: Float,
        marginBottomType: Int,
        marginBottomValue: Float,

        paddingStartType: Int,
        paddingStartValue: Float,
        paddingEndType: Int,
        paddingEndValue: Float,
        paddingTopType: Int,
        paddingTopValue: Float,
        paddingBottomType: Int,
        paddingBottomValue: Float,

        borderStartType: Int,
        borderStartValue: Float,
        borderEndType: Int,
        borderEndValue: Float,
        borderTopType: Int,
        borderTopValue: Float,
        borderBottomType: Int,
        borderBottomValue: Float,

        flexGrow: Float,
        flexShrink: Float,

        flexBasisType: Int,
        flexBasisValue: Float,

        widthType: Int,
        widthValue: Float,
        heightType: Int,
        heightValue: Float,

        minWidthType: Int,
        minWidthValue: Float,
        minHeightType: Int,
        minHeightValue: Float,

        maxWidthType: Int,
        maxWidthValue: Float,
        maxHeightType: Int,
        maxHeightValue: Float,

        aspectRatio: Float
    ): Long

    override fun toString(): String {
        return "Style(display=$display, positionType=$positionType, direction=$direction, flexDirection=$flexDirection, flexWrap=$flexWrap, overflow=$overflow, alignItems=$alignItems, alignSelf=$alignSelf, alignContent=$alignContent, justifyContent=$justifyContent, position=$position, margin=$margin, padding=$padding, border=$border, flexGrow=$flexGrow, flexShrink=$flexShrink, flexBasis=$flexBasis, size=$size, minSize=$minSize, maxSize=$maxSize, aspectRatio=$aspectRatio)"
    }
}
