import { afterAll, afterEach, beforeAll, beforeEach, describe, expect, it } from '@ohos/hypium';
import abilityDelegatorRegistry from '@ohos.app.ability.abilityDelegatorRegistry';
import { Driver, ON, Rect } from '@ohos.UiTest';
import Want from '@ohos.app.ability.Want';
import hilog from '@ohos.hilog';


const delegator: abilityDelegatorRegistry.AbilityDelegator = abilityDelegatorRegistry.getAbilityDelegator()
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

function sleep(time: number) {
  return new Promise<void>((resolve: Function) => setTimeout(resolve, time));
}



// https://gitee.com/openharmony/testfwk_arkxtest/tree/master/#%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95%E6%A1%86%E6%9E%B6%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7
// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/arkxtest-guidelines-V5
// https://developer.huawei.com/consumer/cn/doc/harmonyos-references-V5/js-apis-uitest-V5
export default function viewAbilityTest() {
  hilog.info(0x0000, 'GXTest', '%{public}s', 'abilityTest');
  describe('ViewAbility', () => {
    hilog.info(0x0000, 'GXTest', '%{public}s', 'describe');
    // Defines a test suite. Two parameters are supported: test suite name and test suite function.
    beforeAll(() => {
      hilog.info(0x0000, 'GXTest', '%{public}s', 'beforeAll');
      // Presets an action, which is performed only once before all test cases of the test suite start.
      // This API supports only one parameter: preset action function.
    });
    beforeEach(() => {
      hilog.info(0x0000, 'GXTest', '%{public}s', 'beforeEach');
      // Presets an action, which is performed before each unit test case starts.
      // The number of execution times is the same as the number of test cases defined by **it**.
      // This API supports only one parameter: preset action function.
    });
    afterEach(() => {
      hilog.info(0x0000, 'GXTest', '%{public}s', 'afterEach');
      // Presets a clear action, which is performed after each unit test case ends.
      // The number of execution times is the same as the number of test cases defined by **it**.
      // This API supports only one parameter: clear action function.
    });
    afterAll(() => {
      hilog.info(0x0000, 'GXTest', '%{public}s', 'afterAll');
      // Presets a clear action, which is performed after all test cases of the test suite end.
      // This API supports only one parameter: clear action function.
    });

    it('template_width_min_size', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_width_min_size');
      let bizId: string = "view";
      let templateId: string = "template_width_min_size";
      let dataPath: string = "";
      await startGXAbility('GXAbility', bizId, templateId, dataPath);
      let driver = Driver.create();
      let view = await driver.findComponent(ON.id('container1'));
      let bounds = await view.getBounds();
      expect(rectWidth(bounds)).assertEqual(100);
      expect(rectHeight(bounds)).assertEqual(100);
      done();
    })

    it('template_width_max_size', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_width_max_size');
      let bizId: string = "view";
      let templateId: string = "template_width_max_size";
      let dataPath: string = "";
      await startGXAbility('GXAbility1', bizId, templateId, dataPath);
      let driver = Driver.create();
      let view = await driver.findComponent(ON.id('container1'));
      let bounds = await view.getBounds();
      expect(rectWidth(bounds)).assertEqual(100);
      expect(rectHeight(bounds)).assertEqual(100);
      done();
    })
    //
    // it('template_aspect_ratio_height_to_width', 0, async (done: Function) => {
    //   hilog.info(0x0000, 'GXTest', 'template_aspect_ratio_height_to_width');
    //   let bizId: string = "view";
    //   let templateId: string = "template_aspect_ratio_height_to_width";
    //   let dataPath: string = "";
    //   await startGXAbility('GXAbility1', bizId, templateId, dataPath);
    //   let driver = Driver.create();
    //   await driver.delayMs(1000);
    //   let view = await driver.findComponent(ON.id('container1'));
    //   let bounds = await view.getBounds();
    //   expect(rectWidth(bounds)).assertEqual(vp2px(100));
    //   expect(rectHeight(bounds)).assertEqual(vp2px(100));
    //   done();
    // })
    //
    // it('template_aspect_ratio_width_to_height', 0, async (done: Function) => {
    //   hilog.info(0x0000, 'GXTest', 'template_aspect_ratio_width_to_height');
    //   let bizId: string = "view";
    //   let templateId: string = "template_aspect_ratio_width_to_height";
    //   let dataPath: string = "";
    //   await startGXAbility('GXAbility2', bizId, templateId, dataPath);
    //   let driver = Driver.create();
    //   await driver.delayMs(1000);
    //   let view = await driver.findComponent(ON.id('container1'));
    //   let bounds = await view.getBounds();
    //   expect(rectWidth(bounds)).assertEqual(vp2px(100));
    //   expect(rectHeight(bounds)).assertEqual(vp2px(100));
    //   done();
    // })
    //
    // it('template_view_property_display_none', 0, async (done: Function) => {
    //   hilog.info(0x0000, 'GXTest', 'template_view_property_display_none');
    //   let bizId: string = "view";
    //   let templateId: string = "template_view_property_display_none";
    //   let dataPath: string = "";
    //   await startGXAbility('GXAbility3', bizId, templateId, dataPath);
    //   let driver = Driver.create();
    //   await driver.delayMs(1000);
    //   let view = await driver.findComponent(ON.id('empty_view'));
    //   let bounds = await view.getBounds();
    //   expect(rectWidth(bounds)).assertEqual(vp2px(0));
    //   expect(rectHeight(bounds)).assertEqual(vp2px(0));
    //   done();
    // })
    //
    // it('template_view_property_display_databinding_flex', 0, async (done: Function) => {
    //   hilog.info(0x0000, 'GXTest', 'template_view_property_display_databinding_flex');
    //   let bizId: string = "view";
    //   let templateId: string = "template_view_property_display_databinding";
    //   let dataPath: string = "template_view_property_display_databinding_flex.json";
    //   await startGXAbility('GXAbility4', bizId, templateId, dataPath);
    //   let driver = Driver.create();
    //   await driver.delayMs(1000);
    //   let view = await driver.findComponent(ON.id('empty_view'));
    //   let bounds = await view.getBounds();
    //   expect(rectWidth(bounds)).assertEqual(vp2px(100));
    //   expect(rectHeight(bounds)).assertEqual(vp2px(100));
    //   done();
    // })
    //
    // it('template_view_property_display_databinding_none', 0, async (done: Function) => {
    //   hilog.info(0x0000, 'GXTest', 'template_view_property_display_databinding_none');
    //   let bizId: string = "view";
    //   let templateId: string = "template_view_property_display_databinding";
    //   let dataPath: string = "template_view_property_display_databinding_none.json";
    //   await startGXAbility('GXAbility5', bizId, templateId, dataPath);
    //   let driver = Driver.create();
    //   await driver.delayMs(1000);
    //   let view = await driver.findComponent(ON.id('empty_view'));
    //   let bounds = await view.getBounds();
    //   expect(rectWidth(bounds)).assertEqual(vp2px(0));
    //   expect(rectHeight(bounds)).assertEqual(vp2px(0));
    //   done();
    // })
    //
    // it('template_merge_empty_node_with_padding', 0, async (done: Function) => {
    //   hilog.info(0x0000, 'GXTest', 'template_merge_empty_node_with_padding');
    //   let bizId: string = "view";
    //   let templateId: string = "template_merge_empty_node_with_padding";
    //   let dataPath: string = "";
    //   await startGXAbility('GXAbility17', bizId, templateId, dataPath);
    //   let driver = Driver.create();
    //   await driver.delayMs(1000);
    //
    //   // 验证根容器尺寸
    //   let rootView = await driver.findComponent(ON.id('template_merge_empty_node_with_padding'));
    //   let rootBounds = await rootView.getBounds();
    //   expect(rectWidth(rootBounds)).assertEqual(vp2px(1080)); // 100% 宽度
    //   expect(rectHeight(rootBounds)).assertEqual(vp2px(100));  // 100px 高度
    //
    //   // 验证容器1（带padding的容器）
    //   let container1 = await driver.findComponent(ON.id('container1'));
    //   let container1Bounds = await container1.getBounds();
    //   expect(rectWidth(container1Bounds)).assertEqual(vp2px(1080 - 10)); // 宽度减去左边距10px
    //   expect(rectHeight(container1Bounds)).assertEqual(vp2px(100));    // 高度100%
    //
    //   // 验证text1组件
    //   let text1 = await driver.findComponent(ON.id('text1'));
    //   let text1Bounds = await text1.getBounds();
    //   expect(rectWidth(text1Bounds)).assertEqual(vp2px(1080 - 10)); // 继承容器宽度
    //   expect(rectHeight(text1Bounds)).assertEqual(vp2px(20));        // 20px高度
    //
    //   done();
    // })
  })
}

function rectWidth(bounds: Rect): number {
  return PX2VP(bounds.right - bounds.left);
}

function rectHeight(bounds: Rect): number {
  return PX2VP(bounds.bottom - bounds.top);
}

function PX2VP(value: number): number {
  return Math.round(px2vp(value));
}

async function startGXAbility(abilityName: string, bizId: Object, templateId: Object, dataPath: Object) {
  hilog.info(0x0000, 'GXTest', '%{public}s', `startGXAbility ${bizId} ${templateId} ${dataPath}`);
  const want: Want = {
    bundleName: bundleName,
    abilityName: abilityName,
    parameters: {
      "bizId": bizId,
      "templateId": templateId,
      "dataPath": dataPath,
    }
  };
  await delegator.startAbility(want);
}

