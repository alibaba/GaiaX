import { Size, Style } from '../stretch/Style';
import gxstretch from 'libgxstretch.so';
import { Stretch } from './Stretch';
import { Layout } from './Layout';
import hilog from '@ohos.hilog';

export class Node {
  ptr?: number | null = null;
  // 经过处理后的样式，可能在复用过程中被改写。
  style?: Style | null = null;
  // 保存css中的样式
  cssStyle?: Style | null = null;
  // 父节点
  parent: Node | null = null;
  children: Node[];

  static {
    Stretch.init()
  }

  constructor(style: Style, children: Node[] = []) {
    this.cssStyle = style.clone();
    if (!style.isInit) {
      style.init()
    }
    if (Stretch.ptr == null) {
      new Error('Stretch ptr is null');
    }
    if (style.ptr == null) {
      throw new Error('Style ptr is null');
    }
    this.ptr = gxstretch.napi_stretch_node_create(Stretch.ptr, style.ptr);
    this.children = children;
    this.style = style;
  }

  addChild(child: Node) {
    gxstretch.napi_stretch_node_add_child(Stretch.ptr, this.ptr, child.ptr);
    child.parent = this;
    this.children.push(child);
  }

  removeChild(child: Node): Node {
    let target_index = -1;
    for (let i = 0; i < this.children.length; i++) {
      let tmp = this.children[i];
      if (tmp.ptr == child.ptr) {
        target_index = i;
        break;
      }
    }
    if (target_index != -1 && target_index >= 0) {
      gxstretch.napi_stretch_node_remove_child(Stretch.ptr, this.ptr, child.ptr);
      this.children.splice(target_index, 1);
    } else {
      throw new Error(`remove child index error ${target_index}`);
    }
    return child
  }

  removeChildAtIndex(index: number): Node {
    if (index >= 0 && index < this.getChildren().length) {
      let target = this.children.splice(index, 1)[0];
      gxstretch.napi_stretch_node_remove_child(Stretch.ptr, this.ptr, target.ptr);
      return target;
    } else {
      throw new Error(`remove child index error ${index}`);
    }
  }

  getChildren(): Node[] {
    return this.children;
  }

  getStyle(): Style {
    return this.style
  }

  free() {
    if (this.ptr != null) {
      gxstretch.napi_stretch_node_free(Stretch.ptr, this.ptr);
      this.ptr = null;
    }
  }

  markDirty() {
    gxstretch.napi_stretch_node_mark_dirty(Stretch.ptr, this.ptr);
  }

  isDirty(): boolean {
    return gxstretch.napi_stretch_node_dirty(Stretch.ptr, this.ptr) == 1;
  }

  setStyle(style: Style) {
    if (!style.isInit) {
      style.init()
    }
    gxstretch.napi_stretch_node_set_style(Stretch.ptr, this.ptr, style.ptr);
    this.style = style
  }

  computeLayout(size: Size<number>): Layout | null {
    let layouts: number[] = gxstretch.napi_stretch_node_compute_layout(
      Stretch.ptr,
      this.ptr,
      size.width ?? Number.NaN,
      size.height ?? Number.NaN,
    );
    if (layouts != undefined) {
      let layout = Layout.fromFloatArray(layouts, 0).layout;
      hilog.info(0x0000, 'GXStretch', 'Node computeLayout layout=%{public}s', layout.toString());
      return layout;
    }
    return null;
  }
}