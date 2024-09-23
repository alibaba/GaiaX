package com.alibaba.gaiax.js.support.script

enum class ComponentLifecycle : ILifecycle {
    ON_DATA_INIT,
    ON_READY,
    ON_REUSE,
    ON_SHOW,
    ON_HIDE,
    ON_DESTROY,
    ON_LOAD_MORE,
    ON_DESTROY_COMPONENT
}