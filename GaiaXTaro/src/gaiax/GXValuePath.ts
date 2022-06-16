export default function computeValuePath(valuePath: string, targetObject: any): any {
    try {
        const keyIndex = valuePath.indexOf('.')
        if (keyIndex <= 0) {
            return targetObject[valuePath];
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