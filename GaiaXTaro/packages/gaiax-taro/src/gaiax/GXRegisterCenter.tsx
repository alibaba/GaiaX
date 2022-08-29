import { GXEngineInstance } from './GXEngineInstance';
import GXIExtensionTemplateSource from './GXIExtensionTemplateSource';

export default class GXRegisterCenter {

    registerExtensionTemplateSource(gxTemplateSource: GXIExtensionTemplateSource) {
        GXEngineInstance.gxData.gxTemplateSource.register(gxTemplateSource);
    }

}