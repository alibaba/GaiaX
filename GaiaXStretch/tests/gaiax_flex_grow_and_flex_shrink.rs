mod gaiax_flex_grow_and_flex_shrink {
    use stretch::geometry::*;
    use stretch::number::Number;
    use stretch::style::*;
    use stretch::Stretch;

    ///
    /// Input hierarchy:
    ///    - root 400
    ///         - group0 100
    ///         - group1 flex_grow:1 flex_shrink:1
    ///             - group1_group0 width:200 flex_shrink:1
    ///             - group1_group1 width:200 flex_shrink:1
    ///
    /// Output hierarchy:
    ///     - root 400
    ///         - group0 100
    ///         - group1 300
    ///             - group1_group0 width:150
    ///             - group1_group1 width:150
    #[test]
    fn flex_grow_and_flex_shrink() {
        let mut stretch = Stretch::new();

        let root = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(400.0), height: Dimension::Points(100.0) },
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(100.0), ..Default::default() },
                    flex_shrink: 0.0,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2 = stretch
            .new_node(
                Style { flex_grow: 1.0, flex_shrink: 1.0, flex_direction: FlexDirection::Column, ..Default::default() },
                &[],
            )
            .unwrap();

        let container_2_1 = stretch
            .new_node(
                Style { flex_grow: 1.0, flex_shrink: 1.0, flex_direction: FlexDirection::Row, ..Default::default() },
                &[],
            )
            .unwrap();

        let container_2_1_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2_1_2 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        stretch.add_child(root, container_1).unwrap();
        stretch.add_child(root, container_2).unwrap();

        stretch.add_child(container_2, container_2_1).unwrap();
        stretch.add_child(container_2_1, container_2_1_1).unwrap();
        stretch.add_child(container_2_1, container_2_1_2).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_2_1_size = stretch.layout(container_2_1).unwrap().size;
        let container_2_1_1_size = stretch.layout(container_2_1_1).unwrap().size;
        let container_2_1_2_size = stretch.layout(container_2_1_2).unwrap().size;

        assert_eq!(root_size.width, 400.0);
        assert_eq!(root_size.height, 100.0);

        assert_eq!(container_1_size.width, 100.0);
        assert_eq!(container_1_size.height, 100.0);

        assert_eq!(container_2_size.width, 300.0);
        assert_eq!(container_2_size.height, 100.0);

        assert_eq!(container_2_1_size.width, 300.0);
        assert_eq!(container_2_1_size.height, 100.0);

        assert_eq!(container_2_1_1_size.width, 150.0);
        assert_eq!(container_2_1_2_size.width, 150.0);

        assert_eq!(container_2_1_1_size.height, 100.0);
        assert_eq!(container_2_1_2_size.height, 100.0);
    }

    ///
    /// Input hierarchy:
    ///    - root 400
    ///         - group0 100
    ///         - group1 flex_grow:1 flex_shrink:0
    ///             - group1_group0 width:200 flex_shrink:1
    ///             - group1_group1 width:200 flex_shrink:1
    ///
    /// Output hierarchy:
    ///     - root 400
    ///         - group0 100
    ///         - group1 400
    ///             - group1_group0 width:200
    ///             - group1_group1 width:200
    #[test]
    fn flex_grow_and_flex_shrink1() {
        let mut stretch = Stretch::new();

        let root = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(400.0), height: Dimension::Points(100.0) },
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
                    flex_shrink: 0.0,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2 = stretch
            .new_node(
                Style { flex_grow: 1.0, flex_shrink: 0.0, flex_direction: FlexDirection::Column, ..Default::default() },
                &[],
            )
            .unwrap();

        let container_2_1 = stretch
            .new_node(Style { flex_grow: 1.0, flex_direction: FlexDirection::Row, ..Default::default() }, &[])
            .unwrap();

        let container_2_1_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(200.0), ..Default::default() },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2_1_2 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(200.0), ..Default::default() },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        stretch.add_child(root, container_1).unwrap();
        stretch.add_child(root, container_2).unwrap();

        stretch.add_child(container_2, container_2_1).unwrap();
        stretch.add_child(container_2_1, container_2_1_1).unwrap();
        stretch.add_child(container_2_1, container_2_1_2).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_2_1_size = stretch.layout(container_2_1).unwrap().size;
        let container_2_1_1_size = stretch.layout(container_2_1_1).unwrap().size;
        let container_2_1_2_size = stretch.layout(container_2_1_2).unwrap().size;

        assert_eq!(root_size.width, 400.0);
        assert_eq!(root_size.height, 100.0);

        assert_eq!(container_1_size.width, 100.0);
        assert_eq!(container_1_size.height, 100.0);

        assert_eq!(container_2_size.width, 400.0);
        assert_eq!(container_2_size.height, 100.0);

        assert_eq!(container_2_1_size.width, 400.0);
        assert_eq!(container_2_1_size.height, 100.0);

        assert_eq!(container_2_1_1_size.width, 200.0);
        assert_eq!(container_2_1_2_size.width, 200.0);

        assert_eq!(container_2_1_1_size.height, 100.0);
        assert_eq!(container_2_1_2_size.height, 100.0);
    }

    ///
    /// Input hierarchy:
    ///    - root 375
    ///         - group0 40 40 flex_shrink:0
    ///         - group1 height:100% flex_grow:1 flex_shrink:1 flex_direction:column
    ///             - group1_group0 height:20 flex_shrink:1 flex_direction:row
    ///                 - group1_group0_group0 width width:400 height:20 flex_shrink:1 flex_direction:row
    ///         - group2 35 35 flex_shrink:0
    ///
    /// Output hierarchy:
    ///     - root 400
    ///         - group0 100
    ///         - group1 400
    ///             - group1_group0 width:200
    ///             - group1_group1 width:200
    #[test]
    fn flex_grow_and_flex_shrink_yk_vip_channel_identity_area() {
        let mut stretch = Stretch::new();

        /*
        style = Style(
            display=Flex,
            positionType=Relative,
            direction=Inherit,
            flexDirection=Row,
            flexWrap=NoWrap,
            overflow=Hidden,
            alignItems=Center,
            alignSelf=Auto,
            alignContent=FlexStart,
            justifyContent=FlexStart,
            position=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            margin=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            padding=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            border=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            flexGrow=0.0,
            flexShrink=0.0,
            flexBasis=Dimension.Auto,
            size=Size(width=Points(points=1313.0),
            height=Points(points=231.0)),
            minSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            maxSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            aspectRatio=null,
            rustptr=3857449456)
         */
        let yk_vip_channel_identity_area = stretch
            .new_node(
                Style {
                    display: Display::Flex,
                    position_type: PositionType::Relative,
                    direction: Direction::Inherit,
                    flex_direction: FlexDirection::Row,
                    flex_wrap: FlexWrap::NoWrap,
                    overflow: Overflow::Hidden,
                    align_items: AlignItems::Center,
                    align_self: AlignSelf::Auto,
                    align_content: AlignContent::FlexStart,
                    justify_content: JustifyContent::FlexStart,
                    position: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    margin: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    padding: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    border: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    flex_grow: 0.0,
                    flex_shrink: 0.0,
                    flex_basis: Dimension::Auto,
                    size: Size { width: Dimension::Points(1313.0), height: Dimension::Points(231.0) },
                    min_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    max_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    aspect_ratio: Number::Undefined,
                },
                &[],
            )
            .unwrap();

        /*
        style = Style(
            display=Flex,
            positionType=Relative,
            direction=Inherit,
            flexDirection=Row,
            flexWrap=NoWrap,
            overflow=Hidden,
            alignItems=Stretch,
            alignSelf=Center,
            alignContent=FlexStart,
            justifyContent=FlexStart,
            position=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            margin=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            padding=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            border=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            flexGrow=0.0,
            flexShrink=0.0,
            flexBasis=Dimension.Auto,
            size=Size(width=Points(points=140.0), height=Points(points=140.0)),
            minSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            maxSize=Size(width=Dimension.Auto, height=Dimension.Auto),
             aspectRatio=null, rustptr=3857470096)
         */
        let avatar = stretch
            .new_node(
                Style {
                    display: Display::Flex,
                    position_type: PositionType::Relative,
                    direction: Direction::Inherit,
                    flex_direction: FlexDirection::Row,
                    flex_wrap: FlexWrap::NoWrap,
                    overflow: Overflow::Hidden,
                    align_items: AlignItems::Stretch,
                    align_self: AlignSelf::Center,
                    align_content: AlignContent::FlexStart,
                    justify_content: JustifyContent::FlexStart,
                    position: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    margin: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    padding: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    border: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    flex_grow: 0.0,
                    flex_shrink: 0.0,
                    flex_basis: Dimension::Auto,
                    size: Size { width: Dimension::Points(140.0), height: Dimension::Points(140.0) },
                    min_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    max_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    aspect_ratio: Number::Undefined,
                },
                &[],
            )
            .unwrap();

        /*
        style = Style(
            display=Flex, 
            positionType=Relative, 
            direction=Inherit, 
            flexDirection=Row, 
            flexWrap=NoWrap, 
            overflow=Hidden, 
            alignItems=Center, 
            alignSelf=Auto, 
            alignContent=FlexStart, 
            justifyContent=FlexStart, 
            position=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined), 
            margin=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined), 
            padding=Rect(start=Points(points=42.0), end=Points(points=42.0), top=Dimension.Undefined, bottom=Dimension.Undefined), 
            border=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined), 
            flexGrow=0.0, flexShrink=0.0, flexBasis=Dimension.Auto, 
            size=Size(width=Points(points=123.0), height=Points(points=123.0)), 
            minSize=Size(width=Dimension.Auto, height=Dimension.Auto), 
            maxSize=Size(width=Dimension.Auto, height=Dimension.Auto), 
            aspectRatio=null, rustptr=3857474416)
         */
        let button = stretch
            .new_node(
                Style {
                    display: Display::Flex,
                    position_type: PositionType::Relative,
                    direction: Direction::Inherit,
                    flex_direction: FlexDirection::Row,
                    flex_wrap: FlexWrap::NoWrap,
                    overflow: Overflow::Hidden,
                    align_items: AlignItems::Center,
                    align_self: AlignSelf::Auto,
                    align_content: AlignContent::FlexStart,
                    justify_content: JustifyContent::FlexStart,
                    position: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    margin: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    padding: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    border: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    flex_grow: 0.0,
                    flex_shrink: 0.0,
                    flex_basis: Dimension::Auto,
                    size: Size { width: Dimension::Points(123.0), height: Dimension::Points(123.0) },
                    min_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    max_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    aspect_ratio: Number::Undefined,
                },
                &[],
            )
            .unwrap();

        /*
        style = Style(
            display=Flex,
            positionType=Relative,
            direction=Inherit,
            flexDirection=Column,
            flexWrap=NoWrap,
            overflow=Hidden,
            alignItems=Stretch,
            alignSelf=Auto,
            alignContent=FlexStart,
            justifyContent=Center,
            position=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            margin=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            padding=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            border=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            flexGrow=1.0,
            flexShrink=1.0,
            flexBasis=Dimension.Auto,
            size=Size(width=Dimension.Auto, height=Percent(percentage=1.0)),
            minSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            maxSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            aspectRatio=null, rustptr=3857466976)
        */
        let title_view = stretch
            .new_node(
                Style {
                    display: Display::Flex,
                    position_type: PositionType::Relative,
                    direction: Direction::Inherit,
                    flex_direction: FlexDirection::Column,
                    flex_wrap: FlexWrap::NoWrap,
                    overflow: Overflow::Hidden,
                    align_items: AlignItems::Stretch,
                    align_self: AlignSelf::Auto,
                    align_content: AlignContent::FlexStart,
                    justify_content: JustifyContent::FlexStart,
                    position: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    margin: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    padding: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    border: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    flex_grow: 1.0,
                    flex_shrink: 1.0,
                    flex_basis: Dimension::Auto,
                    size: Size { width: Dimension::Auto, height: Dimension::Percent(1.0) },
                    min_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    max_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    aspect_ratio: Number::Undefined,
                },
                &[],
            )
            .unwrap();

        /*
        style = Style(
            display=Flex,
            positionType=Relative,
            direction=Inherit,
            flexDirection=Row,
            flexWrap=NoWrap,
            overflow=Hidden,
            alignItems=Center,
            alignSelf=Auto,
            alignContent=FlexStart,
            justifyContent=FlexStart,
            position=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            margin=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            padding=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            border=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            flexGrow=0.0,
            flexShrink=1.0,
            flexBasis=Dimension.Auto,
            size=Size(width=Dimension.Auto, height=Points(points=70.0)),
            minSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            maxSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            aspectRatio=null, rustptr=3857462656)
        */
        let name_view = stretch
            .new_node(
                Style {
                    display: Display::Flex,
                    position_type: PositionType::Relative,
                    direction: Direction::Inherit,
                    flex_direction: FlexDirection::Row,
                    flex_wrap: FlexWrap::NoWrap,
                    overflow: Overflow::Hidden,
                    align_items: AlignItems::Center,
                    align_self: AlignSelf::Auto,
                    align_content: AlignContent::FlexStart,
                    justify_content: JustifyContent::FlexStart,
                    position: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    margin: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    padding: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    border: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    flex_grow: 0.0,
                    flex_shrink: 1.0,
                    flex_basis: Dimension::Auto,
                    size: Size { width: Dimension::Auto, height: Dimension::Points(70.0) },
                    min_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    max_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    aspect_ratio: Number::Undefined
                },
                &[],
            )
            .unwrap();

        /*
        style = Style(
            display=Flex,
            positionType=Relative,
            direction=Inherit,
            flexDirection=Row,
            flexWrap=NoWrap,
            overflow=Hidden,
            alignItems=Stretch,
            alignSelf=Auto,
            alignContent=FlexStart,
            justifyContent=FlexStart,
            position=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            margin=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            padding=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            border=Rect(start=Dimension.Undefined, end=Dimension.Undefined, top=Dimension.Undefined, bottom=Dimension.Undefined),
            flexGrow=0.0,
            flexShrink=1.0,
            flexBasis=Dimension.Auto,
            size=Size(width=Points(points=1370.0), height=Points(points=70.0)),
            minSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            maxSize=Size(width=Dimension.Auto, height=Dimension.Auto),
            aspectRatio=null, rustptr=3857473936)
         */
        let title = stretch
            .new_node(
                Style {
                    display: Display::Flex,
                    position_type: PositionType::Relative,
                    direction: Direction::Inherit,
                    flex_direction: FlexDirection::Row,
                    flex_wrap: FlexWrap::NoWrap,
                    overflow: Overflow::Hidden,
                    align_items: AlignItems::Center,
                    align_self: AlignSelf::Auto,
                    align_content: AlignContent::FlexStart,
                    justify_content: JustifyContent::FlexStart,
                    position: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    margin: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    padding: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    border: Rect {
                        start: Dimension::Undefined,
                        top: Dimension::Undefined,
                        end: Dimension::Undefined,
                        bottom: Dimension::Undefined,
                    },
                    flex_basis: Dimension::Auto,
                    flex_grow: 0.0,
                    flex_shrink: 1.0,
                    size: Size { width: Dimension::Points(1370.0), height: Dimension::Points(70.0) },
                    min_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    max_size: Size { width: Dimension::Auto, height: Dimension::Auto },
                    aspect_ratio: Number::Undefined
                },
                &[],
            )
            .unwrap();

        stretch.add_child(yk_vip_channel_identity_area, avatar).unwrap();
        stretch.add_child(yk_vip_channel_identity_area, title_view).unwrap();
        stretch.add_child(title_view, name_view).unwrap();
        stretch.add_child(name_view, title).unwrap();
        stretch.add_child(yk_vip_channel_identity_area, button).unwrap();

        stretch
            .compute_layout(
                yk_vip_channel_identity_area,
                Size { width: Number::Defined(1313.0), height: Number::Undefined },
            )
            .unwrap();

        assert_eq!(stretch.layout(yk_vip_channel_identity_area).unwrap().size.width, 1313.0);
        assert_eq!(stretch.layout(avatar).unwrap().size.width, 140.0);
        assert_eq!(stretch.layout(title_view).unwrap().size.width, 1313.0 - 140.0 - 123.0);
        assert_eq!(stretch.layout(name_view).unwrap().size.width, 1313.0 - 140.0 - 123.0);
        assert_eq!(stretch.layout(title).unwrap().size.width, 1313.0 - 140.0 - 123.0);
        assert_eq!(stretch.layout(button).unwrap().size.width, 123.0);
    }

    // #[test]
    fn flex_grow_and_flex_shrink_99() {
        let mut stretch = Stretch::new();

        let root = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(400.0), height: Dimension::Points(100.0) },
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(100.0), ..Default::default() },
                    flex_shrink: 0.0,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2 = stretch
            .new_node(
                Style { flex_grow: 1.0, flex_shrink: 1.0, flex_direction: FlexDirection::Column, ..Default::default() },
                &[],
            )
            .unwrap();

        let container_2_1 = stretch
            .new_node(
                Style { flex_grow: 1.0, flex_shrink: 1.0, flex_direction: FlexDirection::Row, ..Default::default() },
                &[],
            )
            .unwrap();

        let container_2_1_1 = stretch
            .new_node(
                Style {
                    size: Size { height: Dimension::Points(100.0), ..Default::default() },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2_1_2 = stretch
            .new_node(
                Style {
                    size: Size { height: Dimension::Points(100.0), ..Default::default() },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2_1_1_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let container_2_1_2_1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
                    flex_shrink: 1.0,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        stretch.add_child(root, container_1).unwrap();
        stretch.add_child(root, container_2).unwrap();

        stretch.add_child(container_2, container_2_1).unwrap();
        stretch.add_child(container_2_1, container_2_1_1).unwrap();
        stretch.add_child(container_2_1, container_2_1_2).unwrap();

        stretch.add_child(container_2_1_1, container_2_1_1_1).unwrap();
        stretch.add_child(container_2_1_2, container_2_1_2_1).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_2_1_size = stretch.layout(container_2_1).unwrap().size;
        let container_2_1_1_size = stretch.layout(container_2_1_1).unwrap().size;
        let container_2_1_2_size = stretch.layout(container_2_1_2).unwrap().size;

        let container_2_1_1_1_size = stretch.layout(container_2_1_1_1).unwrap().size;
        let container_2_1_2_1_size = stretch.layout(container_2_1_2_1).unwrap().size;

        assert_eq!(root_size.width, 400.0);
        assert_eq!(root_size.height, 100.0);

        assert_eq!(container_1_size.width, 100.0);
        assert_eq!(container_1_size.height, 100.0);

        assert_eq!(container_2_size.width, 300.0);
        assert_eq!(container_2_size.height, 100.0);

        assert_eq!(container_2_1_size.width, 300.0);
        assert_eq!(container_2_1_size.height, 100.0);

        assert_eq!(container_2_1_1_size.width, 150.0);
        assert_eq!(container_2_1_2_size.width, 150.0);
        assert_eq!(container_2_1_1_1_size.width, 150.0);
        assert_eq!(container_2_1_2_1_size.width, 150.0);

        assert_eq!(container_2_1_1_size.height, 100.0);
        assert_eq!(container_2_1_2_size.height, 100.0);
    }
}
