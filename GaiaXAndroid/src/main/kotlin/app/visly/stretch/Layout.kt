package app.visly.stretch

/**
 * @suppress
 */
data class Layout(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    val children: MutableList<Layout>,
    var id: String = ""
) {

    companion object {
        fun fromFloatArray(args: FloatArray, offset: Int): Pair<Int, Layout> {
            var offset = offset

            val x = args[offset++]
            val y = args[offset++]
            val width = args[offset++]
            val height = args[offset++]
            val childCount = args[offset++].toInt()
            val children = mutableListOf<Layout>()

            for (i in 0 until childCount) {
                val child = Layout.fromFloatArray(args, offset)
                offset = child.first
                children.add(child.second)
            }

            return Pair(offset, Layout(x, y, width, height, children))
        }
    }

    override fun toString(): String {
        return "Layout(x=$x, y=$y, width=$width, height=$height, children=$children, id='$id')"
    }

}