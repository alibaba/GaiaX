package com.alibaba.gaiax.js.support

import com.alibaba.gaiax.js.utils.TimeUtils

/**
 * 该类用于生成可被执行的JS源码内容
 */
object GXScriptBuilder {

    fun buildImportScript(): String {
        return """
import * as GaiaXBridge from "GaiaXBridge";

        """.trimIndent()
    }

    fun buildGlobalContext(contextId: Long, engineType: Int): String {
        return """
var __globalThis = globalThis; 
__globalThis.__CONTEXT_ID__ = ${contextId};
__globalThis.__ENGINE_TYPE__ = ${engineType};

        """.trimIndent()
    }

    fun buildExtendAndAssignScript(): String {
        return """
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
      extendStatics =
        Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array &&
          function (d, b) {
            d.__proto__ = b;
          }) ||
        function (d, b) {
          for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
        };
      return extendStatics(d, b);
    };
    return function (d, b) {
      extendStatics(d, b);
      function __() {
        this.constructor = d;
      }
      d.prototype =
        b === null
          ? Object.create(b)
          : ((__.prototype = b.prototype), new __());
    };
  })();
var __assign = (this && this.__assign) || function () {
    __assign =
      Object.assign ||
      function (t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
          s = arguments[i];
          for (var p in s)
            if (Object.prototype.hasOwnProperty.call(s, p)) t[p] = s[p];
        }
        return t;
      };
    return __assign.apply(this, arguments);
  };
  
        """.trimIndent()
    }

    fun buildModuleGlobalDeclareScript(moduleName: String): String {
        return if (moduleName == "BuildIn") {
            """
__globalThis.gaiax = new GaiaX${moduleName}Module();

                """.trimIndent()
        } else {
            """
__globalThis.${moduleName} = new GaiaX${moduleName}Module();

                """.trimIndent()
        }
    }

    fun buildModuleDeclareScript(moduleName: String): String? {
        if (moduleName.isEmpty()) {
            return null
        }
        return """
var GaiaX${moduleName}Module = (function (_super) {
  __extends(GaiaX${moduleName}Module, _super);
  function GaiaX${moduleName}Module() {
    return _super.call(this) || this;
  }
  return GaiaX${moduleName}Module;
})(Bridge);

        """.trimIndent()
    }

    fun buildModuleDeclareScriptForDebug(moduleName: String): String? {
        if (moduleName.isEmpty()) {
            return null
        }
        return """
class GaiaX${moduleName}Module extends Bridge {}; 
        """.trimIndent()
    }

    fun buildSyncMethodDeclareScript(moduleName: String, methodName: String, moduleId: Long, methodId: Long): String? {
        if (moduleName.isEmpty() || methodName.isEmpty() || moduleId < 0 || methodId < 0) {
            return null
        }
        return """
GaiaX${moduleName}Module.prototype.${methodName} = function () {
  var args = [];
  for (var _i = 0; _i < arguments.length; _i++) {
    args[_i] = arguments[_i];
  }
  return GaiaX${moduleName}Module.callSync({ moduleId: ${moduleId}, methodId: ${methodId}, timestamp: ${TimeUtils.elapsedRealtime()}, args });
};

        """.trimIndent()
    }

    fun buildAsyncMethodDeclareScript(moduleName: String, methodName: String, moduleId: Long, methodId: Long): String? {
        if (moduleName.isEmpty() || methodName.isEmpty() || moduleId < 0 || methodId < 0) {
            return null
        }
        return """
GaiaX${moduleName}Module.prototype.${methodName} = function () {
  var args = [];
  for (var _i = 0; _i < arguments.length; _i++) {
    args[_i] = arguments[_i];
  }
  GaiaX${moduleName}Module.callAsync(
    { moduleId: ${moduleId}, methodId: ${methodId}, timestamp: ${TimeUtils.elapsedRealtime()}, args: (typeof args[args.length-1] == 'function') ? args.slice(0, args.length-1) : args },
    function (result) {
      let callback = args[args.length - 1];
      callback && (typeof callback == 'function') && callback(result);
    }
  );
};

        """.trimIndent()
    }

    fun buildPromiseMethodDeclareScript(moduleName: String, methodName: String, moduleId: Long, methodId: Long): String? {
        if (moduleName.isEmpty() || methodName.isEmpty() || moduleId < 0 || methodId < 0) {
            return null
        }
        return """
GaiaX${moduleName}Module.prototype.${methodName} = function () {
  var args = [];
  for (var _i = 0; _i < arguments.length; _i++) {
    args[_i] = arguments[_i];
  }
  return new Promise(function (resolve, reject) {
    GaiaX${moduleName}Module.callPromise({ moduleId: ${moduleId}, methodId: ${methodId}, timestamp: ${TimeUtils.elapsedRealtime()}, args })
      .then(function (result) {
        resolve(result);
      })
      .catch(function (error) {
        reject(error);
      });
  });
};

        """.trimIndent()
    }

    fun buildInitComponentScript(componentId: Long, bizId: String, templateId: String, templateVersion: String, script: String): String {
        var newScript = script.trimIndent()
        val indexOf = script.indexOf("//# ")
        if (indexOf != -1) {
            newScript = newScript.substring(0, indexOf).trimIndent()
        }
        val extend = "{ bizId: \"${bizId}\", templateId: \"${templateId}\", instanceId: $componentId, templateVersion: $templateVersion }"
        val prefix = newScript.substring(0, newScript.length - 2).trimIndent()
        val suffix = newScript.substring(newScript.length - 2).trimIndent()
        return """
$prefix, $extend
$suffix
        """.trimIndent()
    }

    fun buildComponentReadyScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId);
    if (instance) { 
        instance.onShow && instance.onShow(); 
        instance.onReady && instance.onReady(); 
    }
})()
        """.trimIndent()
    }

    fun buildComponentShowScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onShow && instance.onShow(); 
    }    
})()
        """.trimIndent()
    }

    fun buildComponentHideScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onHide && instance.onHide(); 
    }
})()
        """.trimIndent()
    }

    fun buildComponentDestroyScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onDestroy && instance.onDestroy(); 
    }
})()
        """.trimIndent()
    }

    fun buildDestroyComponentScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) {
        IMs.removeComponent($componentId);
    }
})()
        """.trimIndent()
    }

    fun buildComponentReuseScript(componentId: Long): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onReuse && instance.onReuse(); 
    }
})()
        """.trimIndent()
    }

    fun buildComponentLoadMoreScript(componentId: Long, msg: String): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId); 
    if (instance) { 
        instance.onLoadMore && instance.onLoadMore($msg); 
    }
})()
        """.trimIndent()
    }

    fun buildComponentDataInitScript(componentId: Long, msg: String): String {
        return """
(function () {
    var instance = IMs.getComponent($componentId);
    if (instance) {
        return instance.onDataInit && instance.onDataInit($msg);
    }
})()
        """.trimIndent()
    }

    fun buildPostMessage(msg: String): String {
        return """
window.postMessage($msg)
        """.trimIndent()
    }

    fun buildPostNativeMessage(msg: String): String {
        return """
window.postNativeMessage($msg)
        """.trimIndent()
    }

    fun buildStyle(): String {
        return """
var Style = (function () {
    function Style(data) {
        this.__data__ = __assign({}, data);
    }
    Object.defineProperty(Style.prototype, "targetData", {
        get: function () {
            return this.__data__;
        },
        enumerable: true,
        configurable: true
    });
    return Style;
}());

var Props = (function () {
    function Props() {
    }
    return Props;
}());
        """.trimIndent()
    }

    fun buildDebugStyle(): String {
        return """
            class Props {}; 
            class Style { targetData:any }; 
            """.trimIndent()
    }

}