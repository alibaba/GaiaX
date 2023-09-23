//package com.alibaba.gaiax.js.adapter.view
//
//import android.app.Dialog
//import android.content.Context
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.view.View
//import android.view.Window
//import com.alibaba.fastjson.JSONObject
//import com.alibaba.gaiax.GXTemplateEngine
//
///**
// *  @author: shisan.lms
// *  @date: 2023-03-27
// *  Description:
// */
//class GaiaXCustomDialogView : Dialog {
//    private val params: JSONObject = JSONObject()
//    private var mContainer: View? = null
//
//    constructor(context: Context, params: JSONObject?) : super(context) {
//        if (params != null) {
//            this.params.putAll(params)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        if (params.isEmpty()) {
//            return
//        }
//        initDialogFeature()
//        val builder: GaiaX.Params.Builder? = initGaiaXBuilder()
//        val params: GaiaX.Params = builder.build()
//        GaiaX.Companion.getInstance().bindView(params)
//    }
//
//    fun getEventDelegate(): GaiaX.IEventDelegate? {
//        return object : IEventDelegate() {
//            fun onEvent(@NotNull eventParams: EventParams) {
//                Log.d("GaiaXCustomDialogView", "onEvent: ")
//                doEvent(eventParams)
//            }
//        }
//    }
//
//    protected fun doEvent(@NonNull eventParams: EventParams) {
//        //目前只做点击事件
//        if (eventParams.getData() != null) {
//            val action: Action? = safeToAction(eventParams.getData())
//            ActionWrapper.doAction(AppInfoProviderProxy.getAppContext(), action)
//        }
//    }
//
//    /**
//     * 安全转换ACTION
//     *
//     * @return 如果失败返回null
//     */
//    fun safeToAction(targetData: JSONObject): Action? {
//        try {
//            return targetData.toJavaObject(Action::class.java)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return null
//    }
//
//    fun getTrackDelegate(): GaiaX.ITrackDelegate3? {
//        return object : ITrackDelegate3() {
//            fun onTrack(@NotNull trackParams: TrackParams) {
//                doTrack(trackParams)
//            }
//        }
//    }
//
//    protected fun doTrack(@NonNull trackParams: TrackParams) {
//        if (trackParams.getView() != null && trackParams.getData() != null) {
//            val args = getTrackParams(trackParams.getData())
//            if (args != null && TextUtils.isEmpty(args["arg1"])) {
//                args.put("arg1", args["spm"] + "")
//            }
//            YoukuAnalyticsProviderProxy.setTrackerTagParam(trackParams.getView(), args, IContract.ALL_TRACKER)
//        }
//    }
//
//    /**
//     * 获取埋点参数
//     */
//    fun getTrackParams(@NonNull targetData: JSONObject): Map<String, String>? {
//        val action: Action? = safeToAction(targetData)
//        return if (action != null) {
//            ReportDelegate.generateTrackerMap(action.getReportExtend(), null)
//        } else HashMap()
//    }
//
//
//    private fun initDialogFeature() {
//        val dismissWhenTap = if (params.getBoolean("dismissWhenTap") != null) params.getBoolean("dismissWhenTap") else true
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        setCanceledOnTouchOutside(dismissWhenTap)
//        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    }
//
//    private fun getWidth(): Int {
//        val width: Int = params.getIntValue("width")
//        return if (width != 0) {
//            width
//        } else {
//            DeviceInfoProviderProxy.getWindowWidth()
//        }
//    }
//
//    private fun getHeightBuilder(builder: GaiaX.Params.Builder): GaiaX.Params.Builder {
//        val height: Float = if (params.getFloat("height") == null) 0 else params.getFloat("height")
//        return if (height != 0f) {
//            builder.height(height)
//        } else {
//            builder
//        }
//    }
//
//    private fun getTemplateData(): JSONObject? {
//        val templateData: JSONObject = params.getJSONObject("templateData")
//        return if (templateData != null) {
//            templateData
//        } else {
//            JSONObject()
//        }
//    }
//
//    private fun initGaiaXBuilder(): GXTemplateEngine.GXTemplateItem {
//        val templateId: String = params.getString("templateId")
//        val templateBiz: String = params.getString("bizId")
//        if (templateBiz == null || templateId == null) {
//            return null
//        }
//        mContainer = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog_layout, null, false)
//        setContentView(mContainer)
//        var builder: GaiaX.Params.Builder = Builder()
//            .templateId(templateId)
//            .templateBiz(templateBiz)
//            .container(mContainer)
//            .width(getWidth())
//            .data(getTemplateData())
//            .mode(LoadType.SYNC_NORMAL)
//        builder = getHeightBuilder(builder)
//        return builder
//    }
//}