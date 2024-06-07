export const napi_stretch_init: () => number;

export const napi_stretch_free: (ptr: number) => void;

export const napi_stretch_node_create: (stretch_ptr: number, style_ptr: number) => number;

export const napi_stretch_node_free: (stretch_ptr: number, node_ptr: number) => void;

export const napi_stretch_node_mark_dirty: (stretch_ptr: number, node_ptr: number) => void;

export const napi_stretch_node_dirty: (stretch_ptr: number, node_ptr: number) => number;

export const napi_stretch_node_set_style: (stretch_ptr: number, node_ptr: number, style_ptr: number) => void;

export const napi_stretch_node_add_child: (stretch_ptr: number, node_ptr: number, child_node_ptr: number) => void;

export const napi_stretch_node_remove_child: (stretch_ptr: number, node_ptr: number, child_node_ptr: number) => void;

export const napi_stretch_node_compute_layout: (stretch_ptr: number, node_ptr: number, width: number, height:number) => number[];

export const napi_stretch_style_create: (
  display: number,
  positionType: number,
  direction: number,
  flexDirection: number,
  flexWrap: number,
  overflow: number,
  alignItems: number,
  alignSelf: number,
  alignContent: number,
  justifyContent: number,
  positionStartType: number,
  positionStartValue: number,
  positionEndType: number,
  positionEndValue: number,
  positionTopType: number,
  positionTopValue: number,
  positionBottomType: number,
  positionBottomValue: number,
  marginStartType: number,
  marginStartValue: number,
  marginEndType: number,
  marginEndValue: number,
  marginTopType: number,
  marginTopValue: number,
  marginBottomType: number,
  marginBottomValue: number,
  paddingStartType: number,
  paddingStartValue: number,
  paddingEndType: number,
  paddingEndValue: number,
  paddingTopType: number,
  paddingTopValue: number,
  paddingBottomType: number,
  paddingBottomValue: number,
  borderStartType: number,
  borderStartValue: number,
  borderEndType: number,
  borderEndValue: number,
  borderTopType: number,
  borderTopValue: number,
  borderBottomType: number,
  borderBottomValue: number,
  flexGrow: number,
  flexShrink: number,
  flexBasisType: number,
  flexBasisValue: number,
  widthType: number,
  widthValue: number,
  heightType: number,
  heightValue: number,
  minWidthType: number,
  minWidthValue: number,
  minHeightType: number,
  minHeightValue: number,
  maxWidthType: number,
  maxWidthValue: number,
  maxHeightType: number,
  maxHeightValue: number,
  aspectRatio: number,
) => number;

export const napi_stretch_style_free: (ptr: number) => void;
