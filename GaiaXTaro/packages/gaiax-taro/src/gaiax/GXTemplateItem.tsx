
export default class GXTemplateItem {
    static create(templateBiz: string, templateId: string) {
        const gxTemplateItem = new GXTemplateItem();
        gxTemplateItem.templateBiz = templateBiz;
        gxTemplateItem.templateId = templateId;
        return gxTemplateItem;
    }
    templateBiz: string;
    templateId: string;
}
