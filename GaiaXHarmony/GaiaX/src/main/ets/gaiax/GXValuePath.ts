export default function computeValuePath(valuePath: string, targetObject: any): any {
  try {

    // 表达式
    const keyIndex = valuePath.indexOf('.')

    // 取数组
    if (keyIndex <= 0 && valuePath.includes('[') && valuePath.includes(']')) {
      const indexLeft = valuePath.indexOf('[')
      const indexRight = valuePath.indexOf(']')
      const arrayName = valuePath.substring(0, indexLeft).trim()
      const arrayIndex = valuePath.substring(indexLeft + 1, indexRight).trim()
      if (targetObject != null && targetObject != undefined) {
        return targetObject[arrayName][arrayIndex];
      } else {
        console.error(`computeValuePath: ${valuePath} is not exist`);
        return '';
      }
    }

    // 表达式
    if (keyIndex <= 0) {
      if (targetObject != null && targetObject != undefined) {
        return targetObject[valuePath];
      } else {
        console.error(`computeValuePath: ${valuePath} is not exist`);
        return '';
      }

    }

    if (keyIndex > 0) {
      // 取值
      const firstKey = valuePath.substring(0, keyIndex).trim();
      const restKey = valuePath.substring(keyIndex + 1, valuePath.length).trim();
      return computeValuePath(restKey, targetObject[firstKey]);
    }

  } catch (error) {
    console.error(error);
    return '';
  }
};