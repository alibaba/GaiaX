import GXTemplateItem from '../gaiax/GXTemplateItem';

type Callback = (error: Error | null, data: any | null) => void;

function Uint8ArrayToString(fileData) {
  var dataString = "";
  for (var i = 0; i < fileData.length; i++) {
    dataString += String.fromCharCode(fileData[i]);
  }

  return dataString
}

function stringToUint8Array(str) {
  var arr = [];
  for (var i = 0, j = str.length; i < j; ++i) {
    arr.push(str.charCodeAt(i));
  }

  var tmpUint8Array = new Uint8Array(arr);
  return tmpUint8Array
}

// 从RawFile中加载模板
export function loadTemplate(context: Context, gxTemplateItem: GXTemplateItem, callback: Callback) {
  context
    .resourceManager
    .getRawFileContent(`${gxTemplateItem.templateBiz}/${gxTemplateItem.templateId}/index.json`, (error, value) => {
      if (error != null) {
        console.log("error is " + error);
        callback(error, null)
      } else {
        let indexJSON = Uint8ArrayToString(value);
        context
          .resourceManager
          .getRawFileContent(`${gxTemplateItem.templateBiz}/${gxTemplateItem.templateId}/index.css`, (error, value) => {
            if (error != null) {
              console.log("error is " + error);
              callback(error, null)
            } else {
              let indexCSS = Uint8ArrayToString(value);
              context
                .resourceManager
                .getRawFileContent(`${gxTemplateItem.templateBiz}/${gxTemplateItem.templateId}/index.databinding`, (error, value) => {
                  if (error != null) {
                    console.log("error is " + error);
                    callback(error, null)
                  } else {
                    let indexDataBinding = Uint8ArrayToString(value);

                    let template = {
                      "index.json": indexJSON,
                      "index.css": indexCSS,
                      "index.databinding": indexDataBinding,
                    };
                    callback(null, template);
                  }
                });
            }
          });
      }
    });
}
