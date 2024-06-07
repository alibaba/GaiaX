import hilog from '@ohos.hilog';
import { Size } from 'gxstretch';
import GXTemplateData from '../../../../main/ets/context/GXTemplateData';
import GXTemplateItem from '../../../../main/ets/context/GXTemplateItem';
import GXTemplateEngine from '../../../../main/ets/GXTemplateEngine';
import GXContext from '../../../../main/ets/utils/GXContext';
import resourceManager from '@ohos.resourceManager';
import { util } from '@kit.ArkTS';
import { GXComponent } from '../../../../main/ets/components/GXComponent';

@Entry
@Component
struct GXIndex {
  @State message: string = 'Hello World'
  templateItem = new GXTemplateItem('', '', '')
  templateData = new GXTemplateData();
  measureSize: Size<number> = new Size(NaN, NaN);
  cWidth = 0;

  onPageShow(): void {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXIndex onPageShow');
  }

  onPageHide(): void {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXIndex onPageHide');
  }

  async getJson(resourceManager: resourceManager.ResourceManager, jsonPath: string) {
    let rawFile: Uint8Array;
    rawFile = resourceManager.getRawFileContentSync(jsonPath);
    let jsonStr: string = util.TextDecoder.create("utf-8", { ignoreBOM: true }).decodeWithStream(rawFile);
    let jsonObj: Record<string, Object> = JSON.parse(jsonStr);
    return jsonObj;
  }

  aboutToReuse(params: Record<string, ESObject>) {
    hilog.info(0x0000, 'GXTest', `GXIndex aboutToReuse`);
  }

  aboutToRecycle(): void {
    hilog.info(0x0000, 'GXTest', `GXIndex aboutToRecycle`);
  }

  async aboutToAppear() {
    const bizId = AppStorage.get<string>("bizId") ?? "";
    const templateId = AppStorage.get<string>("templateId") ?? "";
    const dataPath = AppStorage.get<string>("dataPath") ?? "";
    hilog.info(0x0000, 'GXTest', `GXIndex aboutToDisappear bizId=${bizId} templateId=${templateId} dataPath=${dataPath}`);

    GXTemplateEngine.instance.init()

    let sWidth = GXContext.shared().screenWidth;
    let sScale = GXContext.shared().screenScale;
    this.message = 'info.width = ' + sWidth;
    const scale = sScale ?? 1;

    this.templateItem = new GXTemplateItem(bizId, templateId, '')
    if (dataPath.length > 0) {
      this.templateData.jsonData = await this.getJson(getContext().resourceManager, `data/${dataPath}`)
    } else {
      this.templateData.jsonData = {};
    }

    // size
    this.measureSize = new Size(sWidth / scale, NaN);
    this.cWidth = sWidth / scale;
  }

  aboutToDisappear(): void {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'GXIndex aboutToDisappear');
  }

  build() {
    Column() {
      GXComponent({
        templateItem: this.templateItem,
        templateData: this.templateData,
        measureSize: this.measureSize
      })
    }
    .alignItems(HorizontalAlign.Start)
    .width('100%')
  }
}