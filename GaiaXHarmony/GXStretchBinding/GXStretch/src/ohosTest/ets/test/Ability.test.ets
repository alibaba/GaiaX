import { hilog } from '@kit.PerformanceAnalysisKit';
import { afterAll, afterEach, beforeAll, beforeEach, describe, expect, it } from '@ohos/hypium';
import { Node } from '../../../main/ets/stretch/Node';
import { Stretch } from '../../../main/ets/stretch/Stretch';
import { DimPercent, DimPoints, Size, Style } from '../../../main/ets/stretch/Style';

export default function abilityTest() {
  describe('ActsAbilityTest', () => {
    // Defines a test suite. Two parameters are supported: test suite name and test suite function.
    beforeAll(() => {
      // Presets an action, which is performed only once before all test cases of the test suite start.
      // This API supports only one parameter: preset action function.
    });
    beforeEach(() => {
      // Presets an action, which is performed before each unit test case starts.
      // The number of execution times is the same as the number of test cases defined by **it**.
      // This API supports only one parameter: preset action function.
    });
    afterEach(() => {
      // Presets a clear action, which is performed after each unit test case ends.
      // The number of execution times is the same as the number of test cases defined by **it**.
      // This API supports only one parameter: clear action function.
    });
    afterAll(() => {
      // Presets a clear action, which is performed after all test cases of the test suite end.
      // This API supports only one parameter: clear action function.
    });
    it('assertContain', 0, () => {
      // Defines a test case. This API supports three parameters: test case name, filter parameter, and test case function.
      hilog.info(0x0000, 'testTag', '%{public}s', 'it begin');
      let a = 'abc';
      let b = 'b';
      // Defines a variety of assertion methods, which are used to declare expected boolean conditions.
      expect(a).assertContain(b);
      expect(a).assertEqual(a);
    });
    it('Stretch_init_free', 0, () => {
      Stretch.free();
      hilog.info(0x0000, 'testTag', '%{public}s', 'it begin');
      expect(Stretch.ptr).assertNull();
      Stretch.init()
      expect(Stretch.ptr != null).assertTrue();
      Stretch.free();
      expect(Stretch.ptr).assertNull();
    });
    it('Stretch_style_create_free', 0, () => {
      Stretch.free();
      hilog.info(0x0000, 'testTag', '%{public}s', 'it begin');
      let style = new Style();
      if (!style.isInit) {
        style.init()
      }
      expect(style.ptr != null).assertTrue();
      style.free();
      expect(style.ptr).assertNull();
    });
    it('Stretch_node_create_free', 0, () => {
      Stretch.free();

      Stretch.init()
      let style = new Style();

      let node = new Node(style);

      expect(node.ptr != null).assertTrue();

      node.free();

      expect(node.ptr).assertNull();

      style.free();

      Stretch.free();
    });
    it('Stretch_node_add_remove', 0, () => {
      Stretch.free();
      Stretch.init()
      let style = new Style();
      let node = new Node(style);

      let child_node = new Node(style);
      node.addChild(child_node);
      expect(node.getChildren().length == 1).assertTrue();

      let removed_node = node.removeChild(child_node);
      expect(node.getChildren().length == 0).assertTrue();
      expect(child_node.ptr == removed_node.ptr).assertTrue();

      child_node.free();
      node.free();
      style.free();
      Stretch.free();
    });
    it('Stretch_node_dirty', 0, () => {
      Stretch.free();
      Stretch.init()
      let style = new Style();
      let node = new Node(style);

      node.markDirty();
      expect(node.isDirty()).assertTrue();

      node.free();
      style.free();
      Stretch.free();
    });

    it('Stretch_node_set_style', 0, () => {
      Stretch.free();
      Stretch.init()
      let style = new Style();
      let node = new Node(style);
      let style1 = new Style();

      node.setStyle(style1);
      expect(style.ptr != node.getStyle().ptr).assertTrue();

      node.free();
      style1.free();
      style.free();
      Stretch.free();
    });

    it('Stretch_node_compute_layout', 0, () => {
      Stretch.free();
      Stretch.init()
      let style = new Style();
      style.size = new Size(new DimPoints(50), new DimPoints(50))
      let node = new Node(style);
      let size = new Size(100, 100);
      let layout = node.computeLayout(size)!!
      expect(layout.x).assertEqual(0);
      expect(layout.y).assertEqual(0);
      expect(layout.width).assertEqual(50);
      expect(layout.height).assertEqual(50);

      node.free();
      style.free();
      Stretch.free();
    });

    it('Stretch_node_compute_layout_width_100_height_100', 0, () => {
      Stretch.free();
      Stretch.init()
      let style = new Style();
      style.size = new Size(new DimPercent(1), new DimPercent(1))
      let node = new Node(style);

      let childStyle = new Style();
      childStyle.size = new Size(new DimPoints(50), new DimPoints(50))
      let childNode = new Node(childStyle)
      node.addChild(childNode);

      let size = new Size(100, 100);
      let layout = node.computeLayout(size)!!

      expect(layout.x).assertEqual(0);
      expect(layout.y).assertEqual(0);
      expect(layout.width).assertEqual(100);
      expect(layout.height).assertEqual(100);

      expect(layout.children[0].x).assertEqual(0);
      expect(layout.children[0].y).assertEqual(0);
      expect(layout.children[0].width).assertEqual(50);
      expect(layout.children[0].height).assertEqual(50);

      childNode.free;
      childStyle.free();
      node.free();
      style.free();
      Stretch.free();
    });

    it('Stretch_node_compute_layout_width_int_height_null', 0, () => {
      Stretch.free();
      Stretch.init()
      let style = new Style();
      style.size = new Size(new DimPercent(1), new DimPoints(100))
      let node = new Node(style);

      let childStyle = new Style();
      childStyle.size = new Size(new DimPoints(50), new DimPoints(50))
      let childNode = new Node(childStyle)
      node.addChild(childNode);

      let size = new Size(100, null);
      let layout = node.computeLayout(size)!!

      expect(layout.x).assertEqual(0);
      expect(layout.y).assertEqual(0);
      expect(layout.width).assertEqual(100);
      expect(layout.height).assertEqual(100);

      expect(layout.children[0].x).assertEqual(0);
      expect(layout.children[0].y).assertEqual(0);
      expect(layout.children[0].width).assertEqual(50);
      expect(layout.children[0].height).assertEqual(50);

      childNode.free;
      childStyle.free();
      node.free();
      style.free();
      Stretch.free();
    });


  })
}