import gxstretch from 'libgxstretch.so';
import hilog from '@ohos.hilog';


export enum AlignItems {
  FlexStart = 0,
  FlexEnd = 1,
  Center = 2,
  Baseline = 3,
  Stretch = 4,
}

export enum AlignSelf {
  Auto = 0,
  FlexStart = 1,
  FlexEnd = 2,
  Center = 3,
  Baseline = 4,
  Stretch = 5,
}

export enum AlignContent {
  FlexStart = 0,
  FlexEnd = 1,
  Center = 2,
  Stretch = 3,
  SpaceBetween = 4,
  SpaceAround = 5,
}

export enum Direction {
  Inherit = 0,
  LTR = 1,
  RTL = 2,
}


export enum Display {
  Flex = 0,
  None = 1,
}

export enum FlexDirection {
  Row = 0,
  Column = 1,
  RowReverse = 2,
  ColumnReverse = 3,
}

export enum JustifyContent {
  FlexStart = 0,
  FlexEnd = 1,
  Center = 2,
  SpaceBetween = 3,
  SpaceAround = 4,
  SpaceEvenly = 5,
}

export enum Overflow {
  Visible = 0,
  Hidden = 1,
  Scroll = 2,
}

export enum PositionType {
  Relative = 0,
  Absolute = 1,
}

export enum FlexWrap {
  NoWrap = 0,
  Wrap = 1,
  WrapReverse = 2,
}

export class Size<T> {
  width: T;
  height: T;

  constructor(width: T, height: T) {
    this.width = width
    this.height = height
  }

  clone(): Size<T> {
    let size;
    if (this.width instanceof Dim && this.height instanceof Dim) {
      size = new Size(this.width.clone(), this.height.clone())
    } else {
      size = new Size(this.width, this.height)
    }
    return size
  }
}

export class Rect<T> {
  start: T
  end: T
  top: T
  bottom: T

  constructor(start: T, end: T, top: T, bottom: T) {
    this.start = start
    this.end = end
    this.top = top
    this.bottom = bottom
  }


  clone(): Rect<T> {
    let size;
    if (this.start instanceof Dim && this.end instanceof Dim && this.top instanceof Dim && this.bottom instanceof Dim) {
      size = new Rect(this.start.clone(), this.end.clone(), this.top.clone(), this.bottom.clone())
    } else {
      size = new Rect(this.start, this.end, this.top, this.bottom)
    }
    return size
  }
}

export class Dim {
  type: number = 0
  value: number = 0.0

  constructor(type: number, value: number) {
    this.type = type
    this.value = value
  }

  clone(): Dim {
    let dim = new Dim(this.type, this.value)
    return dim
  }
}

export class DimPoints extends Dim {
  constructor(value: number) {
    super(0, value)
  }
}

export class DimPercent extends Dim {
  constructor(value: number) {
    super(1, value)
  }
}

export class DimAuto extends Dim {
  constructor() {
    super(2, 0.0)
  }
}

export class DimUndefined extends Dim {
  constructor() {
    super(3, 0.0)
  }
}

export const Undefined = new DimUndefined();

export const Auto = new DimAuto();

export class Style {
  ptr?: number | null = null;
  display: Display = Display.Flex;
  positionType: PositionType = PositionType.Relative;
  direction: Direction = Direction.Inherit;
  flexDirection: FlexDirection = FlexDirection.Row;
  flexWrap: FlexWrap = FlexWrap.NoWrap;
  overflow: Overflow = Overflow.Hidden;
  alignItems: AlignItems = AlignItems.Stretch;
  alignSelf: AlignSelf = AlignSelf.Auto;
  alignContent: AlignContent = AlignContent.FlexStart;
  justifyContent: JustifyContent = JustifyContent.FlexStart;
  position: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined);
  margin: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined);
  padding: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined);
  border: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined);
  flexGrow: number = 0;
  flexShrink: number = 0;
  flexBasis: Dim = Auto;
  size: Size<Dim> = new Size(Auto, Auto);
  minSize: Size<Dim> = new Size(Auto, Auto);
  maxSize: Size<Dim> = new Size(Auto, Auto);
  aspectRatio?: number | null = null;
  fitContent: boolean = false;

  constructor(display: Display = Display.Flex,
    positionType: PositionType = PositionType.Relative,
    direction: Direction = Direction.Inherit,
    flexDirection: FlexDirection = FlexDirection.Row,
    flexWrap: FlexWrap = FlexWrap.NoWrap,
    overflow: Overflow = Overflow.Hidden,
    alignItems: AlignItems = AlignItems.Stretch,
    alignSelf: AlignSelf = AlignSelf.Auto,
    alignContent: AlignContent = AlignContent.FlexStart,
    justifyContent: JustifyContent = JustifyContent.FlexStart,
    position: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined),
    margin: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined),
    padding: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined),
    border: Rect<Dim> = new Rect(Undefined, Undefined, Undefined, Undefined),
    flexGrow: number = 0,
    flexShrink: number = 0,
    flexBasis: Dim = Auto,
    size: Size<Dim> = new Size(Auto, Auto),
    minSize: Size<Dim> = new Size(Auto, Auto),
    maxSize: Size<Dim> = new Size(Auto, Auto),
    aspectRatio: number | null = null
  ) {
    this.display = display;
    this.positionType = positionType;
    this.direction = direction;
    this.flexDirection = flexDirection;
    this.flexWrap = flexWrap;
    this.overflow = overflow;
    this.alignItems = alignItems;
    this.alignSelf = alignSelf;
    this.alignContent = alignContent;
    this.justifyContent = justifyContent;
    this.position = position;
    this.margin = margin;
    this.padding = padding;
    this.border = border;
    this.flexGrow = flexGrow;
    this.flexShrink = flexShrink;
    this.flexBasis = flexBasis;
    this.size = size;
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.aspectRatio = aspectRatio;
  }

  isInit = false

  public init() {
    if (!this.isInit) {
      this.ptr = gxstretch.napi_stretch_style_create(
        this.display.valueOf(),
        this.positionType.valueOf(),
        this.direction.valueOf(),
        this.flexDirection.valueOf(),
        this.flexWrap.valueOf(),
        this.overflow.valueOf(),
        this.alignItems.valueOf(),
        this.alignSelf.valueOf(),
        this.alignContent.valueOf(),
        this.justifyContent.valueOf(),

        //
        this.position.start.type,
        this.position.start.value,
        this.position.end.type,
        this.position.end.value,
        this.position.top.type,
        this.position.top.value,
        this.position.bottom.type,
        this.position.bottom.value,
        //
        this.margin.start.type,
        this.margin.start.value,
        this.margin.end.type,
        this.margin.end.value,
        this.margin.top.type,
        this.margin.top.value,
        this.margin.bottom.type,
        this.margin.bottom.value,
        //
        this.padding.start.type,
        this.padding.start.value,
        this.padding.end.type,
        this.padding.end.value,
        this.padding.top.type,
        this.padding.top.value,
        this.padding.bottom.type,
        this.padding.bottom.value,
        //
        this.border.start.type,
        this.border.start.value,
        this.border.end.type,
        this.border.end.value,
        this.border.top.type,
        this.border.top.value,
        this.border.bottom.type,
        this.border.bottom.value,
        //
        this.flexGrow.valueOf(),
        this.flexShrink.valueOf(),
        //
        this.flexBasis.type,
        this.flexBasis.value,
        //
        this.size.width.type,
        this.size.width.value,
        this.size.height.type,
        this.size.height.value,
        //
        this.minSize.width.type,
        this.minSize.width.value,
        this.minSize.height.type,
        this.minSize.height.value,
        //
        this.maxSize.width.type,
        this.maxSize.width.value,
        this.maxSize.height.type,
        this.maxSize.height.value,
        //
        this.aspectRatio ?? Number.NaN
      );
      hilog.info(0x0000, 'GXStretch', 'Style ptr = %{public}d', this.ptr);
      this.isInit = true;
    }
  }

  free() {
    if (this.ptr != null) {
      gxstretch.napi_stretch_style_free(this.ptr);
      this.ptr = null;
      this.isInit = false;
    }
  }

  clone(): Style {
    let style = new Style();
    style.display =  this.display
    style.positionType = this.positionType;
    style.direction = this.direction;
    style.flexDirection = this.flexDirection;
    style.flexWrap = this.flexWrap;
    style.overflow = this.overflow;
    style.alignItems = this.alignItems;
    style.alignSelf = this.alignSelf;
    style.alignContent = this.alignContent;
    style.justifyContent = this.justifyContent;
    style.position = this.position.clone();
    style.margin = this.margin.clone();
    style.padding = this.padding.clone();
    style.border = this.border.clone();
    style.flexGrow = this.flexGrow;
    style.flexShrink = this.flexShrink;
    style.flexBasis = this.flexBasis;
    style.size = this.size.clone();
    style.minSize = this.minSize.clone();
    style.maxSize = this.maxSize.clone();
    style.aspectRatio = this.aspectRatio;
    return style;
  }
}