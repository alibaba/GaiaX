import { afterAll, afterEach, beforeAll, beforeEach, describe, expect, it } from '@ohos/hypium';
import abilityDelegatorRegistry from '@ohos.app.ability.abilityDelegatorRegistry';
import { Driver, ON, Rect } from '@ohos.UiTest';
import Want from '@ohos.app.ability.Want';
import hilog from '@ohos.hilog';
import GXContext from '../../../main/ets/utils/GXContext';

const delegator: abilityDelegatorRegistry.AbilityDelegator = abilityDelegatorRegistry.getAbilityDelegator()
const bundleName = abilityDelegatorRegistry.getArguments().bundleName;

function sleep(time: number) {
  return new Promise<void>((resolve: Function) => setTimeout(resolve, time));
}

// https://gitee.com/openharmony/testfwk_arkxtest/tree/master/#%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95%E6%A1%86%E6%9E%B6%E5%8A%9F%E8%83%BD%E7%89%B9%E6%80%A7
// https://developer.huawei.com/consumer/cn/doc/harmonyos-guides-V5/arkxtest-guidelines-V5
// https://developer.huawei.com/consumer/cn/doc/harmonyos-references-V5/js-apis-uitest-V5
export default function abilityTest() {
  hilog.info(0x0000, 'GXTest', '%{public}s', 'abilityTest');
  describe('ScrollAbility', () => {
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

    it('template_scroll_different_item_width', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_different_item_width');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_different_item_width";
      let dataPath: string = "template_scroll_different_item_width.json";
      await startGXAbility('GXAbility6', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let views = await driver.findComponents(ON.id('template_scroll_different_item_width_item'));
      expect(Math.ceil(vp2px(100 + 15))).assertEqual(rectWidth(await views[0].getBounds()));
      expect(Math.ceil(vp2px(100 + 12))).assertEqual(rectWidth(await views[1].getBounds()));
      done();
    })

    it('template_scroll_height_auto', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_height_auto');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_height_auto";
      let dataPath: string = "template_scroll_height_auto.json";
      await startGXAbility('GXAbility7', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let view = await driver.findComponent(ON.id('template_scroll_height_auto'));
      expect(vp2px(100)).assertEqual(rectHeight(await view.getBounds()));
      expect(GXContext.shared().screenWidth).assertEqual(rectWidth(await view.getBounds()));
      done();
    })

    it('template_scroll_height_root_200px_item_100px', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_height_root_200px_item_100px');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_height_root_200px_item_100px";
      let dataPath: string = "template_scroll_height_root_200px_item_100px.json";
      await startGXAbility('GXAbility8', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let view = await driver.findComponent(ON.id('template_scroll_height_root_200px_item_100px'));
      expect(vp2px(200)).assertEqual(rectHeight(await view.getBounds()));
      expect(GXContext.shared().screenWidth).assertEqual(rectWidth(await view.getBounds()));
      done();
    })

    it('template_scroll_load_more_hasMore_false', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_load_more_hasMore_false');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_load_more";
      let dataPath: string = "template_scroll_load_more_false.json";
      await startGXAbility('GXAbility9', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let views = await driver.findComponents(ON.id('template_scroll_load_more_item'));
      let load_more_view = await driver.findComponent(ON.id('template_scroll_load_more_item_load_more'));
      expect(1).assertEqual(views.length);
      expect(vp2px(100)).assertEqual(rectWidth(await views[0].getBounds()));
      expect(false).assertEqual(load_more_view != null);
      done();
    })

    it('template_scroll_load_more_hasMore_true', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_load_more_hasMore_true');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_load_more";
      let dataPath: string = "template_scroll_load_more_true.json";
      await startGXAbility('GXAbility10', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let views = await driver.findComponents(ON.id('template_scroll_load_more_item'));
      let load_more_view = await driver.findComponent(ON.id('template_scroll_load_more_item_load_more'));
      expect(1).assertEqual(views.length);
      expect(true).assertEqual(load_more_view != null);
      expect(vp2px(100)).assertEqual(rectWidth(await views[0].getBounds()));
      expect(vp2px(200)).assertEqual(rectWidth(await load_more_view.getBounds()));
      done();
    })

    it('template_scroll_modify_item', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_modify_item');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_modify_item";
      let dataPath: string = "template_scroll_modify_item.json";
      await startGXAbility('GXAbility11', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let views = await driver.findComponents(ON.id('template_scroll_modify_item_item'));
      expect(1).assertEqual(views.length);
      expect(vp2px(200)).assertEqual(rectWidth(await views[0].getBounds()));
      done();
    })

    // TODO 未实现的Case
    it('template_scroll_css_extend_modify_item', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_css_extend_modify_item');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_css_extend_modify_item";
      let dataPath: string = "template_scroll_css_extend_modify_item.json";
      await startGXAbility('GXAbility12', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let views = await driver.findComponents(ON.id('template_scroll_css_extend_modify_item_item'));
      expect(1).assertEqual(views.length);
      expect(vp2px(300)).assertEqual(rectWidth(await views[0].getBounds()));
      done();
    })

    it('template_scroll_horizontal_different_item_height', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_horizontal_different_item_height');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_horizontal_different_item_height";
      let dataPath: string = "template_scroll_horizontal_different_item_height.json";
      await startGXAbility('GXAbility13', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);

      let view = await driver.findComponent(ON.id('template_scroll_horizontal_different_item_height'));
      expect(Math.ceil(vp2px(70))).assertEqual(rectHeight(await view.getBounds()));

      let views = await driver.findComponents(ON.id('template_scroll_horizontal_different_item_height_item'));
      expect(2).assertEqual(views.length);
      expect(Math.ceil(vp2px(50))).assertEqual(rectWidth(await views[0].getBounds()));
      expect(Math.ceil(vp2px(50))).assertEqual(rectHeight(await views[0].getBounds()));

      expect(Math.ceil(vp2px(50))).assertEqual(rectWidth(await views[1].getBounds()));
      expect(Math.ceil(vp2px(70))).assertEqual(rectHeight(await views[1].getBounds()));
      done();
    })

    it('template_scroll_multi_type_two_gravity_top_horizontal', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_multi_type_two_gravity_top_horizontal');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_multi_type_two_gravity_top_horizontal";
      let dataPath: string = "template_scroll_multi_type_two_gravity_top_horizontal.json";
      await startGXAbility('GXAbility14', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let root = await driver.findComponent(ON.id('template_scroll_multi_type_two_gravity_top_horizontal'));
      let root_bound: Rect = await root.getBounds();

      let view0 = await driver.findComponent(ON.id('template_scroll_multi_type_item_0'));
      let view0_bound: Rect = await view0.getBounds();
      expect(Math.ceil(vp2px(100))).assertEqual(rectWidth(view0_bound));
      expect(0).assertEqual(root_bound.top - view0_bound.top);

      done();
    })

    it('template_scroll_multi_type_two_gravity_bottom_horizontal', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_multi_type_two_gravity_bottom_horizontal');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_multi_type_two_gravity_bottom_horizontal";
      let dataPath: string = "template_scroll_multi_type_two_gravity_bottom_horizontal.json";
      await startGXAbility('GXAbility15', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let root = await driver.findComponent(ON.id('template_scroll_multi_type_two_gravity_bottom_horizontal'));
      let root_bound: Rect = await root.getBounds();

      let view0 = await driver.findComponent(ON.id('template_scroll_multi_type_item_0'));
      let view0_bound: Rect = await view0.getBounds();
      expect(Math.ceil(vp2px(100))).assertEqual(rectWidth(view0_bound));
      expect(Math.ceil(vp2px(100))).assertEqual(view0_bound.top - root_bound.top);

      done();
    })

    it('template_scroll_multi_type_two_gravity_center_horizontal', 0, async (done: Function) => {
      hilog.info(0x0000, 'GXTest', 'template_scroll_multi_type_two_gravity_center_horizontal');
      let bizId: string = "scroll";
      let templateId: string = "template_scroll_multi_type_two_gravity_center_horizontal";
      let dataPath: string = "template_scroll_multi_type_two_gravity_center_horizontal.json";
      await startGXAbility('GXAbility16', bizId, templateId, dataPath);
      let driver = Driver.create();
      await driver.delayMs(1000);
      let root = await driver.findComponent(ON.id('template_scroll_multi_type_two_gravity_center_horizontal'));
      let root_bound: Rect = await root.getBounds();

      let view0 = await driver.findComponent(ON.id('template_scroll_multi_type_item_0'));
      let view0_bound: Rect = await view0.getBounds();
      expect(Math.ceil(vp2px(100))).assertEqual(rectWidth(view0_bound));
      expect(Math.ceil(vp2px(100) / 2)).assertEqual(view0_bound.top - root_bound.top);

      done();
    })
  })
}

function rectWidth(bounds: Rect): number {
  return bounds.right - bounds.left;
}

function rectHeight(bounds: Rect): number {
  return bounds.bottom - bounds.top;
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
  await sleep(1000);
}

