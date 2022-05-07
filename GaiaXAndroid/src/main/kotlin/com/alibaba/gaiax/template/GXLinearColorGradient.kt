package com.alibaba.gaiax.template

import android.graphics.LinearGradient

class GXLinearColorGradient(
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    colors: IntArray,
    positions: FloatArray?,
    tile: TileMode
) : LinearGradient(x0, y0, x1, y1, colors, positions, tile)