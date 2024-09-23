package com.alibaba.gaiax.js.support.script

enum class PageLifecycle : ILifecycle {
    ON_LOAD,
    ON_SHOW,
    ON_READY,
    ON_HIDE,
    ON_UNLOAD,
    ON_PAGE_SCROLL,
    ON_REACH_BOTTOM
}