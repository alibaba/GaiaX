import GXTemplateContext from './GXTemplateContext';
import { GXNode } from './GXNode';
import GXTemplateNode from './GXTemplateNode';
import GXTemplateInfo from './GXTemplateInfo';
import { GXJSONArray, GXJSONObject } from './GXJson';
import hilog from '@ohos.hilog';
import GXTemplateItem from './GXTemplateItem';

export class GXViewTreeCreator {
  createRootNode(gxTemplateContext: GXTemplateContext): GXNode {
    const gxTemplateData = gxTemplateContext.gxTemplateData.templateData as GXJSONObject;

    const gxTemplateInfo = gxTemplateContext.gxTemplateInfo;

    const gxLayer = gxTemplateInfo.layer;

    const gxParentNode = null;

    const gxVisualTemplateNode = gxTemplateContext.gxVisualTemplateNode;

    const gxVisualTemplateNodeData = null

    const gxRootNode = this.createNode(
      gxTemplateContext,
      gxTemplateInfo,
      gxLayer,
      gxTemplateData,
      gxParentNode,
      gxVisualTemplateNode,
      gxVisualTemplateNodeData
    );

    hilog.debug(0x0000, 'gaiaxTag', 'createRootNode %{public}s', JSON.stringify(gxRootNode) ?? 'null');

    gxTemplateContext.gxRootNode = gxRootNode;

    return gxRootNode;
  }

  private createNode(
    gxTemplateContext: GXTemplateContext,
    gxTemplateInfo: GXTemplateInfo,
    gxLayer: GXJSONObject,
    gxTemplateData: GXJSONObject,
    gxParentNode?: GXNode,
    gxVisualTemplateNode?: GXTemplateNode,
    gxVisualTemplateNodeData?: GXJSONObject,

  ): GXNode {

    const gxNode = GXNode.create();

    gxNode.setIdPath(gxTemplateContext, gxLayer, gxParentNode);

    gxNode.gxTemplateNode = GXTemplateNode.create(gxLayer, gxTemplateInfo, gxVisualTemplateNode);


    if (gxNode.gxTemplateNode.isViewType() || gxNode.gxTemplateNode.isGaiaTemplate()) {
      this.createViewOrTemplateNode(
        gxTemplateContext,
        gxNode,
        gxTemplateData,
        gxLayer,
        gxTemplateInfo,
        gxParentNode,
        gxVisualTemplateNodeData
      );
    } else if (gxNode.gxTemplateNode.isTextType()) {
      this.createTextNode(
        gxTemplateContext,
        gxNode,
        gxTemplateData,
        gxParentNode
      );
    }


    return gxNode;
  }

  private createViewOrTemplateNode(
    gxTemplateContext: GXTemplateContext,
    gxNode: GXNode,
    gxTemplateData: GXJSONObject,
    gxLayer: GXJSONObject,
    gxTemplateInfo: GXTemplateInfo,
    gxParentNode: GXNode,
    gxVisualTemplateNodeData?: GXJSONObject,
  ) {
    gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, gxVisualTemplateNodeData, gxParentNode);

    const layers = gxLayer['layers'] as GXJSONArray;

    if (layers != null) {

      if (gxNode != null && gxNode.gxChildren == null) {
        gxNode.gxChildren = new Array<GXNode>();
      }

      for (const target of layers) {

        const childLayer = target as GXJSONObject;

        // 嵌套子模板类型，是个虚拟节点
        if (GXTemplateNode.isNestChildTemplateType(childLayer)) {

          // 获取子模板信息
          const gxChildTemplateInfo = gxTemplateInfo.getChildTemplate(childLayer.id);

          // 创建一个虚拟节点
          const gxChildVisualTemplateNode = GXTemplateNode.create(childLayer, gxTemplateInfo, null);

          // 获取子模板的根节点
          const gxChildLayer = gxChildTemplateInfo.layer;

          // 容器模板下的子模板
          if (gxNode.gxTemplateNode.isContainerType()) {

            // 初始化
            if (gxNode != null && gxNode.gxChildTemplateItems == null) {
              gxNode.gxChildTemplateItems = new Map<GXTemplateItem, GXTemplateNode>();
            }

            // 容器下的子模板
            const childTemplateItem = GXTemplateItem.create(
              gxTemplateContext.gxTemplateItem.templateBiz,
              gxChildLayer.id
            );

            gxNode?.gxChildTemplateItems?.set(childTemplateItem, gxChildVisualTemplateNode);
          }
          // 普通模板嵌套的子模板根节点，可能是普通模板也可能是容器模板
          else {

            // 获取嵌套子模板的数据，会传给下一级模板当做数据源
            const gxChildTemplateData = gxChildVisualTemplateNode.getDataValue(gxTemplateData);

            // 使用原有数据源作为虚拟节点的数据源
            const gxChildVisualTemplateNodeData = gxTemplateData;

            const childNode = this.createNode(
              gxTemplateContext,
              gxChildTemplateInfo,
              gxChildLayer,
              gxChildTemplateData,
              gxNode,
              gxChildVisualTemplateNode,
              gxChildVisualTemplateNodeData
            );

            gxNode?.gxChildren?.push(childNode);

          }
        }
        // 普通子节点
        else {
          const childNode = this.createNode(
            gxTemplateContext,
            gxTemplateInfo,
            childLayer,
            gxTemplateData,
            gxNode,
            null
          );

          gxNode?.gxChildren?.push(childNode);
        }
      }
    }
  }


  private createTextNode(
    gxTemplateContext: GXTemplateContext,
    gxNode: GXNode,
    gxTemplateData: GXJSONObject,
    gxParentNode?: GXNode
  ) {
    gxNode.gxTemplateNode.initFinal(gxTemplateContext, gxTemplateData, null, gxParentNode);

    const data = gxNode.gxTemplateNode.getData(gxTemplateData);
  }
}

let gxViewTreeCreator = new GXViewTreeCreator();

export default gxViewTreeCreator as GXViewTreeCreator;