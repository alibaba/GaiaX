import GXRegisterCenter from './gaiax/GXRegisterCenter';
import GXTemplateEngine from './gaiax/GXTemplateEngine';

// 导出
export { default as GXGesture } from './gaiax/GXGesture';
export { default as GXTemplateKey } from './gaiax/GXTemplateKey';
export { default as GXTemplateComponent } from './components/GXTemplateComponent';
export { default as GXTemplateEngine, GXTemplateData, GXMeasureSize, GXTemplateItem, GXIEventListener } from './gaiax/GXTemplateEngine';
export { default as GXTemplateInfo } from './gaiax/GXTemplateInfo';
export { GXIExtensionTemplateInfoSource, GXIExtensionTemplateSource } from './gaiax/GXRegisterCenter'
export { GXJSONValue, GXJSONArray, GXJSONObject } from './gaiax/GXJson'
export { default as GXTemplate } from './gaiax/GXTemplate'

// 变量
export const GXEngineInstance = new GXTemplateEngine();
export const GXRegisterCenterInstance = new GXRegisterCenter();