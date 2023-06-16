// js Version https://github.com/alibaba/GaiaX/blob/main/GaiaXAndroid/src/main/kotlin/com/alibaba/gaiax/data/assets/GXBinParser.kt
//          ┌◄───────────────────────── Multple ────────────────────────────►┐
//          │                                                                │
// ┌────────┼────────────────┬───────────┬───────────────────┬───────────────┤
// │ Header │ File name size │ File name │ File Content size │ File content  │
// │  100   │       4        │           │         4         │               │
// └────────┴────────────────┴───────────┴───────────────────┴───────────────┘
import * as base64ArrayBuffer from 'base64-arraybuffer';
const HEADER_SIZE = 100;
const FILE_NAME_SIZE = 4;
const FILE_CONTENT_SIZE = 4;

function convertFourUnSignInt(byteArray) {
  return (byteArray[1] & 0xFF) << 8 | (byteArray[0] & 0xFF);
}
export const parse = (base64data: string) => {
  const arrayBuffer = base64ArrayBuffer.decode(base64data);
  let offset = HEADER_SIZE;
  const resObj: Record<string, string> = {};
  while(offset < arrayBuffer.byteLength) {
    const fileNameSizeView = new Uint8Array(arrayBuffer, offset, FILE_NAME_SIZE);
    const fileNameSize = convertFourUnSignInt(fileNameSizeView);
    offset += FILE_NAME_SIZE;

    const fileNameView = new Uint8Array(arrayBuffer, offset, fileNameSize);
    const fileName = new TextDecoder("utf-8").decode(fileNameView);

    offset += fileNameSize

    const fileContentSizeView = new Uint8Array(arrayBuffer, offset, FILE_CONTENT_SIZE);
    const fileContentSize = convertFourUnSignInt(fileContentSizeView);

    offset+= FILE_CONTENT_SIZE

    const fileContentView = new Uint8Array(arrayBuffer, offset, fileContentSize);
    const fileContent = new TextDecoder("utf-8").decode(fileContentView);
    resObj[fileName] = fileContent;
    offset += fileContentSize
  }
  return resObj;
}
