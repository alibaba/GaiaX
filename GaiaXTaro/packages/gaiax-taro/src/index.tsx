import { GXRegisterCenter } from './gaiax/GXRegisterCenter';
import GXTemplateEngine from './gaiax/GXTemplateEngine';

export { default as GXTemplateComponent } from './gaiax/components/root/GXTemplateComponent';
export { default as GXTemplateEngine, GXTemplateData, GXMeasureSize, GXTemplateItem } from './gaiax/GXTemplateEngine';
export { default as GXTemplateInfo } from './gaiax/GXTemplateInfo';
export { GXIExtensionTemplateInfoSource, GXIExtensionTemplateSource } from './gaiax/GXRegisterCenter'
export { default as GXTemplate } from './gaiax/GXTemplate'
export const GXEngineInstance = new GXTemplateEngine()
export const GXRegisterCenterInstance = new GXRegisterCenter();