import hilog from '@ohos.hilog';
import GXMeasureSize from './GXMeasureSize';
import GXTemplateContext from './GXTemplateContext';
import GXTemplateData from './GXTemplateData';
import GXTemplateEngine from './GXTemplateEngine';
import GXTemplateInfo from './GXTemplateInfo';
import GXTemplateItem from './GXTemplateItem';
import GXTemplateNode from './GXTemplateNode';

class GXGenerator {
  // 创建上下文
  createContext(gxTemplateItem: GXTemplateItem,
                gxTemplateData: GXTemplateData,
                gxMeasureSize: GXMeasureSize,
                gxVisualTemplateNode?: GXTemplateNode) {
    hilog.debug(0x0000, 'gaiaxTag', 'createContext %{public}s %{public}s %{public}s', JSON.stringify(gxTemplateItem) ?? 'null', JSON.stringify(GXTemplateData) ?? 'null', JSON.stringify(gxMeasureSize) ?? 'null');

    let gxTemplateInfo: GXTemplateInfo = GXTemplateEngine.gxData.getTemplateInfo(gxTemplateItem);

    hilog.debug(0x0000, 'gaiaxTag', 'createContext %{public}s ', JSON.stringify(gxTemplateInfo) ?? 'null');

    if (gxTemplateInfo == null) {
      return null;
    }

    // 构建上下文
    return new GXTemplateContext(gxTemplateItem, gxTemplateData, gxMeasureSize, gxTemplateInfo, gxVisualTemplateNode)
  }
}

let gxGenerate = new GXGenerator();

export default gxGenerate as GXGenerator;