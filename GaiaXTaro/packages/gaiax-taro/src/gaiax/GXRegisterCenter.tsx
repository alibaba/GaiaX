import { GXEngineInstance } from './GXEngineInstance';
import GXIExtensionTemplateSource from './GXIExtensionTemplateSource';
import GXIExtensionCustomComponent from './GXIExtensionCustomComponent';

export default class GXRegisterCenter {

    gxExtensionCustomComponent: GXIExtensionCustomComponent

    registerExtensionTemplateSource(gxTemplateSource: GXIExtensionTemplateSource) {
        GXEngineInstance.gxData.gxTemplateSource.register(gxTemplateSource);
    }

    registerExtensionCustomComponent(gxExtensionComponent: GXIExtensionCustomComponent) {
        this.gxExtensionCustomComponent = gxExtensionComponent
    }

}