export default function computeValuePath(valuePath: string, targetObject: any): any {
    try {
        const keyIndex = valuePath.indexOf('.')
        if (keyIndex <= 0) {
            if (targetObject != null && targetObject != undefined) {
                return targetObject[valuePath];
            } else {
                console.error(`computeValuePath: ${valuePath} is not exist`);
                return '';
            }

        }
        const firstKey = valuePath.substring(0, keyIndex).trim();
        const restKey = valuePath.substring(keyIndex + 1, valuePath.length).trim();
        if (firstKey.includes('[') && firstKey.includes(']')) {
            const indexLeft = firstKey.indexOf('[')
            const indexRight = firstKey.indexOf(']')
            const arrayName = firstKey.substring(0, indexLeft).trim()
            const arrayIndex = firstKey.substring(indexLeft + 1, indexRight).trim()
            return computeValuePath(restKey, targetObject[arrayName][arrayIndex]);
        } else {
            return computeValuePath(restKey, targetObject[firstKey]);
        }
    } catch (error) {
        console.error(error);
        return '';
    }
};