import GXMeasureSize from '../gaiax/GXMeasureSize';
import { gxRawFileTemplateSource } from '../gaiax/GXRegisterCenter';
import GXTemplateData from '../gaiax/GXTemplateData';
import GXTemplateItem from '../gaiax/GXTemplateItem';
import createViewForEngine from '../gaiax/GXViewBuilder';

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

@Component
export struct GXComponent {
  gxTemplateItem: GXTemplateItem;
  gxTemplateData: GXTemplateData;
  gxMeasureSize: GXMeasureSize;
  @State reload: boolean = false

  aboutToAppear() {
    try {
      getContext()
        .resourceManager
        .getRawFileContent(`${this.gxTemplateItem.templateBiz}/${this.gxTemplateItem.templateId}/index.json`, (error, value) => {
          if (error != null) {
            console.log("error is " + error);
          } else {
            let indexJSON = Uint8ArrayToString(value);
            getContext()
              .resourceManager
              .getRawFileContent(`${this.gxTemplateItem.templateBiz}/${this.gxTemplateItem.templateId}/index.css`, (error, value) => {
                if (error != null) {
                  console.log("error is " + error);
                } else {
                  let indexCSS = Uint8ArrayToString(value);
                  getContext()
                    .resourceManager
                    .getRawFileContent(`${this.gxTemplateItem.templateBiz}/${this.gxTemplateItem.templateId}/index.databinding`, (error, value) => {
                      if (error != null) {
                        console.log("error is " + error);
                      } else {
                        let indexDataBinding = Uint8ArrayToString(value);

                        let template = {
                          "index.json": indexJSON,
                          "index.css": indexCSS,
                          "index.databinding": indexDataBinding,
                        }

                        gxRawFileTemplateSource.addData(this.gxTemplateItem.templateId, template);

                        // this.reload = true;
                      }
                    });
                }
              });
          }
        });
    } catch (error) {
      console.error(`callback getRawFileContent failed, error code: ${error.code}, message: ${error.message}.`)
    }
  }

  build() {
    if (this.reload) {
      createViewForEngine(this.gxTemplateItem, this.gxTemplateData, this.gxMeasureSize)
    } else {
      Text("loading template")
    }
  }
}

