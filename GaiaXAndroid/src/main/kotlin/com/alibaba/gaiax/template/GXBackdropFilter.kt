package com.alibaba.gaiax.template

sealed class GXBackdropFilter {

    object None : GXBackdropFilter()
    object Blur : GXBackdropFilter()

    companion object {
        fun create(backdropFilter: String): GXBackdropFilter? {
            if (backdropFilter == "none") {
                return None
            } else if (backdropFilter.contains("blur")) {
                return Blur
            }
            return null
        }
    }
}