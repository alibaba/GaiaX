import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { window } from '@kit.ArkUI';
import hilog from '@ohos.hilog';

export default class GXAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam) {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility onCreate');
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility want param:' + JSON.stringify(want) ?? '');
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility launchParam:' + JSON.stringify(launchParam) ?? '');
    let params: Record<string, Object> = want.parameters ?? {};
    AppStorage.setOrCreate("bizId", params["bizId"])
    AppStorage.setOrCreate("templateId", params["templateId"])
    AppStorage.setOrCreate("dataPath", params["dataPath"])
  }

  onDestroy() {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage) {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility onWindowStageCreate');

    windowStage.loadContent('testability/pages/GXIndex', LocalStorage.getShared(), (err) => {
      if (err.code) {
        hilog.error(0x0000, 'GXTest', 'GXAbility Failed to load the content. Cause: %{public}s', JSON.stringify(err) ?? '');
        return;
      }
      hilog.info(0x0000, 'GXTest', 'GXAbility Succeeded in loading the content.');
    });
  }

  onWindowStageDestroy() {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility onWindowStageDestroy');
  }

  onForeground() {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility onForeground');
  }

  onBackground() {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXAbility onBackground');
  }
}