package com.alibaba.gaiax.analyze

class GXParams {
    private val params: LongArray

    constructor(params: LongArray){
        this.params = params
        //获取返回的参数列表结果
    }

    fun Get(index: Int): GXValue? {
        return GXContext.wrapAsGXValue(params[index])
    }

    fun getParamsLength():Int{
        return params.count()
    }
}