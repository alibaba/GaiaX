export class GXTemplate {
  static create(layer: string, css: string, data: string): GXTemplate {
    const template = new GXTemplate();
    template.layer = layer;
    template.css = css;
    template.data = data;
    return template;
  }

  layer: string;

  data: string;

  css: string;
}

export default GXTemplate