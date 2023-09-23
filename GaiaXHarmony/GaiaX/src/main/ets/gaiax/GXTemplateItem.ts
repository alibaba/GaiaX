export class GXTemplateItem {
  static create(templateBiz: string, templateId: string) {
    const gxTemplateItem = new GXTemplateItem();
    gxTemplateItem.templateBiz = templateBiz;
    gxTemplateItem.templateId = templateId;
    return gxTemplateItem;
  }

  templateBiz: string = null;
  templateId: string = null;
  templatePrefixId: string = null;
}

export default GXTemplateItem