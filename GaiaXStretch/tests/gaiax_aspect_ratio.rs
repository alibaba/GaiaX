mod gaiax_aspect_ratio {
    use stretch::Stretch;
    use stretch::style::*;
    use stretch::geometry::*;
    use stretch::number::Number;

    /// 宽高比不能设置在根节点上
    #[test]
    fn aspect_ratio_root() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 0.0);
    }

    #[test]
    fn aspect_ratio_multi_6_flex_grow_aspect_ratio() {
        let mut stretch = Stretch::new();

        let scg_topic_a = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();


        let yk_item_line_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(scg_topic_a, yk_item_line_1).unwrap();

        let yk_item_layout_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_line_1, yk_item_layout_1).unwrap();

        let yk_item_layout_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_line_1, yk_item_layout_2).unwrap();

        let yk_item_img_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), height: Dimension::Percent(1.0) },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_layout_1, yk_item_img_1).unwrap();

        let yk_item_img_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), height: Dimension::Percent(1.0) },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_layout_2, yk_item_img_2).unwrap();

        stretch.compute_layout(scg_topic_a, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let scg_topic_a_size = stretch.layout(scg_topic_a).unwrap().size;
        let yk_item_line_1_size = stretch.layout(yk_item_line_1).unwrap().size;
        let yk_item_layout_1_size = stretch.layout(yk_item_layout_1).unwrap().size;
        let yk_item_layout_2_size = stretch.layout(yk_item_layout_2).unwrap().size;
        let yk_item_img_1_size = stretch.layout(yk_item_img_1).unwrap().size;
        let yk_item_img_2_size = stretch.layout(yk_item_img_2).unwrap().size;

        assert_eq!(scg_topic_a_size.width, 375.0);
        assert_eq!(scg_topic_a_size.height, 250.0);

        assert_eq!(yk_item_line_1_size.width, 375.0);
        assert_eq!(yk_item_line_1_size.height, 250.0);

        assert_eq!(yk_item_layout_1_size.width, 188.0);
        assert_eq!(yk_item_layout_1_size.height, 250.0);

        assert_eq!(yk_item_img_1_size.width, 188.0);
        assert_eq!(yk_item_img_1_size.height, 250.0);

        assert_eq!(yk_item_layout_2_size.width, 187.0);
        assert_eq!(yk_item_layout_2_size.height, 250.0);

        assert_eq!(yk_item_img_2_size.width, 187.0);
        assert_eq!(yk_item_img_2_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_multi_5_raw_aspect_ratio_flex_grow_margin() {
        let mut stretch = Stretch::new();

        let scg_topic_a = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },

            margin: Rect {
                bottom: Dimension::Points(18.0),
                ..Default::default()
            },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let yk_item_top_layout = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(scg_topic_a, yk_item_top_layout).unwrap();

        let yk_item_line_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_top_layout, yk_item_line_1).unwrap();

        let yk_item_layout_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_line_1, yk_item_layout_1).unwrap();

        let yk_item_layout_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_line_1, yk_item_layout_2).unwrap();

        let yk_item_img_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_layout_1, yk_item_img_1).unwrap();

        let yk_item_img_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_layout_2, yk_item_img_2).unwrap();

        stretch.compute_layout(scg_topic_a, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let scg_topic_a_size = stretch.layout(scg_topic_a).unwrap().size;
        let yk_item_top_layout_size = stretch.layout(yk_item_top_layout).unwrap().size;
        let yk_item_line_1_size = stretch.layout(yk_item_line_1).unwrap().size;
        let yk_item_layout_1_size = stretch.layout(yk_item_layout_1).unwrap().size;
        let yk_item_layout_2_size = stretch.layout(yk_item_layout_2).unwrap().size;
        let yk_item_img_1_size = stretch.layout(yk_item_img_1).unwrap().size;
        let yk_item_img_2_size = stretch.layout(yk_item_img_2).unwrap().size;

        assert_eq!(scg_topic_a_size.width, 375.0);
        assert_eq!(scg_topic_a_size.height, 250.0);

        assert_eq!(yk_item_top_layout_size.width, 375.0);
        assert_eq!(yk_item_top_layout_size.height, 250.0);

        assert_eq!(yk_item_line_1_size.width, 375.0);
        assert_eq!(yk_item_line_1_size.height, 250.0);

        assert_eq!(yk_item_layout_1_size.width, 188.0);
        assert_eq!(yk_item_layout_1_size.height, 250.0);

        assert_eq!(yk_item_img_1_size.width, 188.0);
        assert_eq!(yk_item_img_1_size.height, 250.0);

        assert_eq!(yk_item_layout_2_size.width, 187.0);
        assert_eq!(yk_item_layout_2_size.height, 250.0);

        assert_eq!(yk_item_img_2_size.width, 187.0);
        assert_eq!(yk_item_img_2_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_multi_5_raw_aspect_ratio_flex_grow() {
        let mut stretch = Stretch::new();

        let scg_topic_a = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let yk_item_top_layout = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(scg_topic_a, yk_item_top_layout).unwrap();

        let yk_item_line_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_top_layout, yk_item_line_1).unwrap();

        let yk_item_layout_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_line_1, yk_item_layout_1).unwrap();

        let yk_item_layout_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_line_1, yk_item_layout_2).unwrap();

        let yk_item_img_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_layout_1, yk_item_img_1).unwrap();

        let yk_item_img_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(yk_item_layout_2, yk_item_img_2).unwrap();

        stretch.compute_layout(scg_topic_a, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let scg_topic_a_size = stretch.layout(scg_topic_a).unwrap().size;
        let yk_item_top_layout_size = stretch.layout(yk_item_top_layout).unwrap().size;
        let yk_item_line_1_size = stretch.layout(yk_item_line_1).unwrap().size;
        let yk_item_layout_1_size = stretch.layout(yk_item_layout_1).unwrap().size;
        let yk_item_layout_2_size = stretch.layout(yk_item_layout_2).unwrap().size;
        let yk_item_img_1_size = stretch.layout(yk_item_img_1).unwrap().size;
        let yk_item_img_2_size = stretch.layout(yk_item_img_2).unwrap().size;

        assert_eq!(scg_topic_a_size.width, 375.0);
        assert_eq!(scg_topic_a_size.height, 250.0);

        assert_eq!(yk_item_top_layout_size.width, 375.0);
        assert_eq!(yk_item_top_layout_size.height, 250.0);

        assert_eq!(yk_item_line_1_size.width, 375.0);
        assert_eq!(yk_item_line_1_size.height, 250.0);

        assert_eq!(yk_item_layout_1_size.width, 188.0);
        assert_eq!(yk_item_layout_1_size.height, 250.0);

        assert_eq!(yk_item_img_1_size.width, 188.0);
        assert_eq!(yk_item_img_1_size.height, 250.0);

        assert_eq!(yk_item_layout_2_size.width, 187.0);
        assert_eq!(yk_item_layout_2_size.height, 250.0);

        assert_eq!(yk_item_img_2_size.width, 187.0);
        assert_eq!(yk_item_img_2_size.height, 250.0);
    }

    /// flex-grow与aspect-ratio嵌套组合使用时，cross-size受到同级孩子的固定宽度影响，导致flex-grow计算错误的问题。
    #[test]
    fn aspect_ratio_multi_4_column_aspect_ratio_flex_grow_point_width() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_1_child_1 = stretch.new_node(Style {
            aspect_ratio: Number::Defined(1.8),
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_1_child_2 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(20.0), width: Dimension::Points(188.0) },
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_child_1 = stretch.new_node(Style {
            aspect_ratio: Number::Defined(1.8),
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_child_2 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();


        stretch.add_child(root, container_1).unwrap();

        stretch.add_child(container_1, container_1_child_1).unwrap();
        stretch.add_child(container_1, container_1_child_2).unwrap();

        stretch.add_child(root, container_2).unwrap();

        stretch.add_child(container_2, container_2_child_1).unwrap();
        stretch.add_child(container_2, container_2_child_2).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_1_child_1_size = stretch.layout(container_1_child_1).unwrap().size;
        let container_1_child_2_size = stretch.layout(container_1_child_2).unwrap().size;

        let container_2_child_1_size = stretch.layout(container_2_child_1).unwrap().size;
        let container_2_child_2_size = stretch.layout(container_2_child_2).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 154.0);

        assert_eq!(container_1_size.width, 188.0);
        assert_eq!(container_1_size.height, 154.0);

        assert_eq!(container_2_size.width, 187.0);
        assert_eq!(container_2_size.height, 154.0);
    }

    #[test]
    fn aspect_ratio_multi_different_height_flex_grow() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_1_child_1 = stretch.new_node(Style {
            aspect_ratio: Number::Defined(1.8),
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_1_child_2 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(20.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();


        let container_2_child_1 = stretch.new_node(Style {
            aspect_ratio: Number::Defined(1.8),
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_child_2 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();


        stretch.add_child(root, container_1).unwrap();

        stretch.add_child(container_1, container_1_child_1).unwrap();
        stretch.add_child(container_1, container_1_child_2).unwrap();

        stretch.add_child(root, container_2).unwrap();

        stretch.add_child(container_2, container_2_child_1).unwrap();
        stretch.add_child(container_2, container_2_child_2).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_1_child_1_size = stretch.layout(container_1_child_1).unwrap().size;
        let container_1_child_2_size = stretch.layout(container_1_child_2).unwrap().size;

        let container_2_child_1_size = stretch.layout(container_2_child_1).unwrap().size;
        let container_2_child_2_size = stretch.layout(container_2_child_2).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 154.0);

        assert_eq!(container_1_size.width, 188.0);
        // assert_eq!(container_1_size.height, 154.0);
        // assert_eq!(container_1_size.height, 124.0);

        assert_eq!(container_2_size.width, 187.0);
        assert_eq!(container_2_size.height, 154.0);
    }

    #[test]
    fn aspect_ratio_absolute() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), height: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let child = stretch.new_node(Style {
            aspect_ratio: Number::Defined(2f32),
            position_type: stretch::style::PositionType::Absolute,
            position: Rect {
                start: Dimension::Points(0f32),
                end: Dimension::Points(0f32),
                top: Dimension::Points(0f32),
                ..Default::default()
            },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, child).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let child_size = stretch.layout(child).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 375.0);

        assert_eq!(child_size.width, 375.0);
        assert_eq!(child_size.height, 188.0);
    }

    #[test]
    fn aspect_ratio_multi_aspect_3() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let header_root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), height: Dimension::Points(42.0) },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1_img_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.777),
            ..Default::default()
        }, &[]).unwrap();

        let container_1_gap = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_1_img_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.777),
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        let container_2_gap_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        let container_2_gap_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_3 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();


        stretch.add_child(root, header_root).unwrap();
        stretch.add_child(root, container_root).unwrap();

        stretch.add_child(container_root, container_1).unwrap();
        stretch.add_child(container_1, container_1_img_1).unwrap();
        stretch.add_child(container_1, container_1_gap).unwrap();
        stretch.add_child(container_1, container_1_img_2).unwrap();

        stretch.add_child(container_root, container_2).unwrap();
        stretch.add_child(container_2, container_2_img_1).unwrap();
        stretch.add_child(container_2, container_2_gap_1).unwrap();
        stretch.add_child(container_2, container_2_img_2).unwrap();
        stretch.add_child(container_2, container_2_gap_2).unwrap();
        stretch.add_child(container_2, container_2_img_3).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let header_root_size = stretch.layout(header_root).unwrap().size;

        let container_root_size = stretch.layout(container_root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_1_img_1_size = stretch.layout(container_1_img_1).unwrap().size;
        let container_1_gap_size = stretch.layout(container_1_gap).unwrap().size;
        let container_1_img_2_size = stretch.layout(container_1_img_2).unwrap().size;

        let container_2_img_1_size = stretch.layout(container_2_img_1).unwrap().size;
        let container_2_gap_1_size = stretch.layout(container_2_gap_1).unwrap().size;
        let container_2_img_2_size = stretch.layout(container_2_img_2).unwrap().size;
        let container_2_gap_2_size = stretch.layout(container_2_gap_2).unwrap().size;
        let container_2_img_3_size = stretch.layout(container_2_img_3).unwrap().size;

        assert_eq!(header_root_size.width, 375.0);
        assert_eq!(header_root_size.height, 42.0);

        assert_eq!(container_root_size.width, 375.0);
        assert_eq!(container_root_size.height, 269.0);

        assert_eq!(container_1_size.width, 375.0);
        assert_eq!(container_1_size.height, 105.0);

        assert_eq!(container_1_img_1_size.width, 186.0);
        assert_eq!(container_1_img_1_size.height, 105.0);

        assert_eq!(container_1_gap_size.width, 3.0);
        assert_eq!(container_1_gap_size.height, 105.0);

        assert_eq!(container_1_img_2_size.width, 186.0);
        assert_eq!(container_1_img_2_size.height, 105.0);

        assert_eq!(container_2_size.width, 375.0);
        assert_eq!(container_2_size.height, 164.0);

        assert_eq!(container_2_img_1_size.width, 123.0);
        assert_eq!(container_2_img_1_size.height, 164.0);

        assert_eq!(container_2_gap_1_size.width, 3.0);
        assert_eq!(container_2_gap_1_size.height, 164.0);

        assert_eq!(container_2_img_2_size.width, 123.0);
        assert_eq!(container_2_img_2_size.height, 164.0);

        assert_eq!(container_2_gap_2_size.width, 3.0);
        assert_eq!(container_2_gap_2_size.height, 164.0);

        assert_eq!(container_2_img_3_size.width, 123.0);
        assert_eq!(container_2_img_3_size.height, 164.0);
    }

    #[test]
    fn aspect_ratio_multi_aspect_1() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        let container_2_gap_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        let container_2_gap_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_3 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, container_2).unwrap();
        stretch.add_child(container_2, container_2_img_1).unwrap();
        stretch.add_child(container_2, container_2_gap_1).unwrap();
        stretch.add_child(container_2, container_2_img_2).unwrap();
        stretch.add_child(container_2, container_2_gap_2).unwrap();
        stretch.add_child(container_2, container_2_img_3).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_2_size = stretch.layout(container_2).unwrap().size;
        let container_2_img_1_size = stretch.layout(container_2_img_1).unwrap().size;
        let container_2_gap_1_size = stretch.layout(container_2_gap_1).unwrap().size;
        let container_2_img_2_size = stretch.layout(container_2_img_2).unwrap().size;
        let container_2_gap_2_size = stretch.layout(container_2_gap_2).unwrap().size;
        let container_2_img_3_size = stretch.layout(container_2_img_3).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 164.0);

        assert_eq!(container_2_size.width, 375.0);
        assert_eq!(container_2_size.height, 164.0);

        assert_eq!(container_2_img_1_size.width, 123.0);
        assert_eq!(container_2_img_1_size.height, 164.0);

        assert_eq!(container_2_gap_1_size.width, 3.0);
        assert_eq!(container_2_gap_1_size.height, 164.0);

        assert_eq!(container_2_img_2_size.width, 123.0);
        assert_eq!(container_2_img_2_size.height, 164.0);

        assert_eq!(container_2_gap_2_size.width, 3.0);
        assert_eq!(container_2_gap_2_size.height, 164.0);

        assert_eq!(container_2_img_3_size.width, 123.0);
        assert_eq!(container_2_img_3_size.height, 164.0);
    }

    #[test]
    fn aspect_ratio_multi_aspect() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(375.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1_img_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.777),
            ..Default::default()
        }, &[]).unwrap();

        let container_1_gap = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_1_img_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.777),
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        let container_2_gap_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        let container_2_gap_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(3.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let container_2_img_3 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.75),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, container_1).unwrap();
        stretch.add_child(container_1, container_1_img_1).unwrap();
        stretch.add_child(container_1, container_1_gap).unwrap();
        stretch.add_child(container_1, container_1_img_2).unwrap();

        stretch.add_child(root, container_2).unwrap();
        stretch.add_child(container_2, container_2_img_1).unwrap();
        stretch.add_child(container_2, container_2_gap_1).unwrap();
        stretch.add_child(container_2, container_2_img_2).unwrap();
        stretch.add_child(container_2, container_2_gap_2).unwrap();
        stretch.add_child(container_2, container_2_img_3).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;

        let container_1_size = stretch.layout(container_1).unwrap().size;
        let container_2_size = stretch.layout(container_2).unwrap().size;

        let container_1_img_1_size = stretch.layout(container_1_img_1).unwrap().size;
        let container_1_gap_size = stretch.layout(container_1_gap).unwrap().size;
        let container_1_img_2_size = stretch.layout(container_1_img_2).unwrap().size;

        let container_2_img_1_size = stretch.layout(container_2_img_1).unwrap().size;
        let container_2_gap_1_size = stretch.layout(container_2_gap_1).unwrap().size;
        let container_2_img_2_size = stretch.layout(container_2_img_2).unwrap().size;
        let container_2_gap_2_size = stretch.layout(container_2_gap_2).unwrap().size;
        let container_2_img_3_size = stretch.layout(container_2_img_3).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 269.0);

        assert_eq!(container_1_size.width, 375.0);
        assert_eq!(container_1_size.height, 105.0);

        assert_eq!(container_1_img_1_size.width, 186.0);
        assert_eq!(container_1_img_1_size.height, 105.0);

        assert_eq!(container_1_gap_size.width, 3.0);
        assert_eq!(container_1_gap_size.height, 105.0);

        assert_eq!(container_1_img_2_size.width, 186.0);
        assert_eq!(container_1_img_2_size.height, 105.0);

        assert_eq!(container_2_size.width, 375.0);
        assert_eq!(container_2_size.height, 164.0);

        assert_eq!(container_2_img_1_size.width, 123.0);
        assert_eq!(container_2_img_1_size.height, 164.0);

        assert_eq!(container_2_gap_1_size.width, 3.0);
        assert_eq!(container_2_gap_1_size.height, 164.0);

        assert_eq!(container_2_img_2_size.width, 123.0);
        assert_eq!(container_2_img_2_size.height, 164.0);

        assert_eq!(container_2_gap_2_size.width, 3.0);
        assert_eq!(container_2_gap_2_size.height, 164.0);

        assert_eq!(container_2_img_3_size.width, 123.0);
        assert_eq!(container_2_img_3_size.height, 164.0);
    }
}