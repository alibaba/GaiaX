1. 已支持的能力
    1. 基础能力
        1. SDK架构的搭建。
        2. 基础组件的视觉还原能力（text、view、image组件的绘制）
        3. 嵌套模板的视觉还原能力（父子模板嵌套）。
        4. 表达式处理能力，用于取值和计算。
        5. 样式的映射与处理。
    2. 协作能力
        1. FastPreview能力。
2. 待做的事项
    1. 事件能力。
    2. 基础组件（scroll、grid、richtext、iconfont等组件的绘制）。
    3. DesignToken能力。
    4. 动态组件下发。
    5. 更多case的处理。
3. 存在的风险
    1. 目前原生浏览器FlexBox计算逻辑和GaiaX在双端使用的stretch库的FlexBox计算逻辑有细微差异，会导致计算结果的不同，从而产生很多兼容处理逻辑。
        1. flexShrink和flexGrow和浏览器计算逻辑不一致。
            目前默认grow和shrink都是0的时候，H5无法正确的计算出来，带有内部padding的view。
            例如: rootView width:375  height 100 paddingLeft:10 paddingRight:10，secondView width100% height100%
            计算不出来secondView=335的结果。
            必须让flex-shrink=1，才能计算出来正确的结果。
        2. 可能存在其他未发现的细微差一点，导致UI还原效果不同。
    2. fit-content=true支持不健全。
        1. 无法直接像客户端一样获取文字的宽度，仅能依赖width=auto达成类似的效果，但可能存在兼容问题。
    3. aspect-ratio无法支持。
        1. 小程序都不支持该属性，该属性的计算需要提前获取组件的宽度或者高度，然后计算出高度或者宽度。依赖目前的逻辑，无法计算。
4. DEMO演示（两个）
    1. 普通模板的展示
    2. 嵌套模板的展示
