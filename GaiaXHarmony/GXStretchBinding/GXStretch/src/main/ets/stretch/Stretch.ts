import gxstretch from 'libgxstretch.so';
import hilog from '@ohos.hilog';

// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/arkts-get-started-0000001820879561-V5
// ArkTS 语言
// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/introduction-to-arkts-0000001774279590-V5#ZH-CN_TOPIC_0000001813785290__%E7%B1%BB
export class Stretch {
  static ptr?: number | null = null;

  static init() {
    if (Stretch.ptr == null) {
      Stretch.ptr = gxstretch.napi_stretch_init();
      hilog.info(0x0000, 'GXStretch', 'Stretch ptr = %{public}d', Stretch.ptr);
    }
  }

  static free() {
    if (Stretch.ptr != null) {
      gxstretch.napi_stretch_free(Stretch.ptr);
      Stretch.ptr = null;
    }
  }
}
