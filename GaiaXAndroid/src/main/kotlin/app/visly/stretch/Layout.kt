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

        fun equals(leftLayout: Layout?, rightLayout: Layout?): Boolean {
            rightLayout as Layout
            if (leftLayout?.x != rightLayout.x) {
                return false
            }
            if (leftLayout.y != rightLayout.y) {
                return false
            }
            if (leftLayout.width != rightLayout.width) {
                return false
            }
            if (leftLayout.height != rightLayout.height) {
                return false
            }
            if (leftLayout.children.size != rightLayout.children.size) {
                return false
            }
            leftLayout.children.forEachIndexed { index, nextLeftLayout ->
                val nextRightLayout = rightLayout.children[index]
                if (!equals(nextLeftLayout, nextRightLayout)) {
                    return false
                }
            }
            return true
        }
    }

    override fun toString(): String {
        return "Layout(x=$x, y=$y, width=$width, height=$height, id='$id')"
    }

}