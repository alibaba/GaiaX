import { ArrayList } from '@kit.ArkTS';
import { hilog } from '@kit.PerformanceAnalysisKit';

class Tmp {
  offset: number
  layout: Layout

  constructor(offset, layout) {
    this.offset = offset;
    this.layout = layout;

  }
}

export class Layout {
  x: number
  y: number
  width: number
  height: number
  children: ArrayList<Layout>

  constructor(x: number, y: number, width: number, height: number, children: ArrayList<Layout>) {
    this.x = x
    this.y = y
    this.width = width
    this.height = height
    this.children = children
  }

  static fromFloatArray(args: number[], off: number) {
    let offset = off;
    let x = args[offset++];
    let y = args[offset++];
    let width = args[offset++];
    let height = args[offset++];
    let childCount = args[offset++];

    hilog.info(0x0000, 'GXStretch', `Layout fromFloatArray offset=${offset} x=${x} y=${y} width=${width} height=${height} childCount=${childCount}`);

    let children = new ArrayList<Layout>()
    for (let i = 0; i < childCount; i++) {
      let child = Layout.fromFloatArray(args, offset)
      offset = child.offset;
      children.add(child.layout);
    }
    return new Tmp(offset, new Layout(x, y, width, height, children));
  }

  toString() {
    return `Layout(width=${this.width} height=${this.height} x=${this.x} y=${this.y} children.length=${this.children.length})`
  }
}