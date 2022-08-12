

export default class GXUtils {

    static convertWidthToNumber(gxWidth: string) {
        if (gxWidth != null) {
            if (gxWidth.endsWith("px")) {
                return Number.parseInt(gxWidth.replace("px", ""));
            } else if (gxWidth.endsWith("pt")) {
                return Number.parseInt(gxWidth.replace("px", ""));
            } else if (gxWidth.endsWith("%")) {
                return 375 * (Number.parseFloat(gxWidth.replace("%", "")) / 100);
            }
        }
        return 375;
    }

    static convertHeightToNumber(gxHeight: string) {
        if (gxHeight != null) {
            if (gxHeight.endsWith("px")) {
                return Number.parseInt(gxHeight.replace("px", ""));
            } else if (gxHeight.endsWith("pt")) {
                return Number.parseInt(gxHeight.replace("px", ""));
            } else if (gxHeight.endsWith("%")) {
                return -1;
            }
        }
        return -1;
    }

}