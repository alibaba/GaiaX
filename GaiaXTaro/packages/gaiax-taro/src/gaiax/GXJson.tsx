export type GXJSONValue =
    | string
    | number
    | boolean
    | any
    | GXJSONObject
    | GXJSONArray
    | null
    | {}

export interface GXJSONObject {
    [k: string]: GXJSONValue;
}

export interface GXJSONArray extends Array<GXJSONValue> { }