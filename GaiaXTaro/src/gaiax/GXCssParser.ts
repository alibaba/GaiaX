export const selX = /([^\s\;\{\}][^\;\{\}]*)\{/g;
export const endX = /\}/g;
export const lineX = /([^\;\{\}]*)\;/g;
export const commentX = /\/\*[\s\S]*?\*\//g;
export const lineAttrX = /([^\:]+):([^\;]*);/;

// This is used, a concatenation of all above. We use alternation to
// capture.
export const altX = /(\/\*[\s\S]*?\*\/)|([^\s\;\{\}][^\;\{\}]*(?=\{))|(\})|([^\;\{\}]+\;(?!\s*\*\/))/gim;

export const isEmpty = function (x: Record<any, any>): boolean {
    return typeof x == 'undefined' || x.length == 0 || x == null;
};

const capComment = 1;
const capSelector = 2;
const capEnd = 3;
const capAttr = 4;

export const toJSON = function (cssString: string): any {
    const node = {};
    let match: any = null;
    let count = 0;

    while ((match = altX.exec(cssString)) != null) {
        if (!isEmpty(match[capSelector])) {
            // New node, we recurse
            const name = match[capSelector].trim();
            // This will return when we encounter a closing brace
            const newNode = toJSON(cssString);
            const bits = [name];
            for (const i in bits) {
                const sel = bits[i].trim();
                if (sel in node) {
                    for (const att in newNode) {
                        node[sel][att] = newNode[att];
                    }
                } else {
                    node[sel] = newNode;
                }
            }
        } else if (!isEmpty(match[capEnd])) {
            // Node has finished
            return node;
        } else if (!isEmpty(match[capAttr])) {
            const line = match[capAttr].trim();
            const attr = lineAttrX.exec(line);
            if (attr) {
                // Attribute
                const name = attr[1].trim();
                const value = attr[2].trim();

                if (name in node) {
                    const currVal = node[name];
                    if (!(currVal instanceof Array)) {
                        node[name] = [currVal];
                    }
                    node[name].push(value);
                } else {
                    node[name] = value;
                }
            } else {
                // Semicolon terminated line
                node[count++] = line;
            }
        }
    }

    return node;
};