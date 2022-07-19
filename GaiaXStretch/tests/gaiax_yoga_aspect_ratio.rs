#[cfg(test)]
#[allow(dead_code)]
mod gaiax_yoga_aspect_ratio {
    use stretch::Stretch;
    use stretch::style::*;
    use stretch::geometry::*;
    use stretch::number::Number;

    #[test]
    fn aspect_ratio_cross_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 50.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_main_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 50.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_both_dimensions_defined_row() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 100.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_both_dimensions_defined_column() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 100.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_align_stretch() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_flex_grow() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_flex_grow_multi() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size0 = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size0.width, 100.0);
        assert_eq!(size0.height, 20.0);
    }

    #[test]
    fn aspect_ratio_flex_grow_multi_4_child() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        let root_child1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        let root_child2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        let root_child3 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();
        stretch.add_child(root, root_child1).unwrap();
        stretch.add_child(root, root_child2).unwrap();
        stretch.add_child(root, root_child3).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size0 = stretch.layout(root_child0).unwrap().size;
        let size1 = stretch.layout(root_child1).unwrap().size;
        let size2 = stretch.layout(root_child2).unwrap().size;
        let size3 = stretch.layout(root_child3).unwrap().size;

        assert_eq!(size0.width, 25.0);
        assert_eq!(size0.height, 5.0);

        assert_eq!(size1.width, 25.0);
        assert_eq!(size1.height, 5.0);

        assert_eq!(size2.width, 25.0);
        assert_eq!(size2.height, 5.0);

        assert_eq!(size3.width, 25.0);
        assert_eq!(size3.height, 5.0);
    }

    #[test]
    fn aspect_ratio_nest_flex_grow_multi_3_child() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        let root_child0_child0 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        let root_child0_child1 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        let root_child0_child2 = stretch.new_node(Style {
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_child0, root_child0_child0).unwrap();
        stretch.add_child(root_child0, root_child0_child1).unwrap();
        stretch.add_child(root_child0, root_child0_child2).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(375.0), height: Number::Undefined }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;
        let root_child0_size = stretch.layout(root_child0).unwrap().size;
        let root_child0_child0_size = stretch.layout(root_child0_child0).unwrap().size;
        let root_child0_child1_size = stretch.layout(root_child0_child1).unwrap().size;
        let root_child0_child2_size = stretch.layout(root_child0_child2).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 71.0);

        assert_eq!(root_child0_size.width, 375.0);
        assert_eq!(root_child0_size.height, 71.0);

        assert_eq!(root_child0_child0_size.width, 125.0);
        assert_eq!(root_child0_child0_size.height, 71.0);

        assert_eq!(root_child0_child1_size.width, 125.0);
        assert_eq!(root_child0_child1_size.height, 71.0);

        assert_eq!(root_child0_child2_size.width, 125.0);
        assert_eq!(root_child0_child2_size.height, 71.0);
    }

    #[test]
    fn aspect_ratio_nest_flex_grow_flex_direction_column() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        let root_child0_child0 = stretch.new_node(Style {
            flex_direction: FlexDirection::Row,
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_child0, root_child0_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(375.0), height: Number::Undefined }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;
        let root_child0_size = stretch.layout(root_child0).unwrap().size;
        let root_child0_child0_size = stretch.layout(root_child0_child0).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 213.0);

        assert_eq!(root_child0_size.width, 375.0);
        assert_eq!(root_child0_size.height, 213.0);

        assert_eq!(root_child0_child0_size.width, 375.0);
        assert_eq!(root_child0_child0_size.height, 213.0);
    }

    #[test]
    fn aspect_ratio_nest_flex_grow_flex_direction_column_2() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        let root_child0_child0 = stretch.new_node(Style {
            flex_direction: FlexDirection::Column,
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_child0, root_child0_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(375.0), height: Number::Undefined }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;
        let root_child0_size = stretch.layout(root_child0).unwrap().size;
        let root_child0_child0_size = stretch.layout(root_child0_child0).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 213.0);

        assert_eq!(root_child0_size.width, 375.0);
        assert_eq!(root_child0_size.height, 213.0);

        assert_eq!(root_child0_child0_size.width, 375.0);
        assert_eq!(root_child0_child0_size.height, 213.0);
    }

    #[test]
    fn aspect_ratio_nest_flex_grow_flex_direction_column_multi() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let node_content = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, node_content).unwrap();

        let node_top_view = stretch.new_node(Style {
            flex_direction: FlexDirection::Row,
            size: Size { width: Dimension::Percent(1.0), height: Dimension::Points(63.0) },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(node_content, node_top_view).unwrap();

        let node_bottom_view = stretch.new_node(Style {
            flex_direction: FlexDirection::Column,
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(node_content, node_bottom_view).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(375.0), height: Number::Undefined }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;
        let content_size = stretch.layout(node_content).unwrap().size;
        let top_view_size = stretch.layout(node_top_view).unwrap().size;
        let bottom_view_size = stretch.layout(node_bottom_view).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 276.0);

        assert_eq!(content_size.width, 375.0);
        assert_eq!(content_size.height, 276.0);

        assert_eq!(top_view_size.width, 375.0);
        assert_eq!(top_view_size.height, 63.0);

        assert_eq!(bottom_view_size.width, 375.0);
        assert_eq!(bottom_view_size.height, 213.0);
    }

    #[test]
    fn aspect_ratio_nest_flex_grow_flex_direction_column_multi_with_margin() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let node_content = stretch.new_node(Style {
            size: Size { width: Dimension::Percent(1.0), ..Default::default() },
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, node_content).unwrap();

        let node_top_view = stretch.new_node(Style {
            flex_direction: FlexDirection::Row,
            size: Size { width: Dimension::Percent(1.0), height: Dimension::Points(63.0) },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(node_content, node_top_view).unwrap();

        let node_bottom_view = stretch.new_node(Style {
            flex_direction: FlexDirection::Column,
            flex_grow: 1.0,
            margin: Rect { start: Dimension::Points(12.0), end: Dimension::Points(12.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.758),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(node_content, node_bottom_view).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(375.0), height: Number::Undefined }).unwrap();

        let root_size = stretch.layout(root).unwrap().size;
        let content_size = stretch.layout(node_content).unwrap().size;
        let top_view_size = stretch.layout(node_top_view).unwrap().size;
        let bottom_view_size = stretch.layout(node_bottom_view).unwrap().size;

        assert_eq!(root_size.width, 375.0);
        assert_eq!(root_size.height, 263.0);

        assert_eq!(content_size.width, 375.0);
        assert_eq!(content_size.height, 263.0);

        assert_eq!(top_view_size.width, 375.0);
        assert_eq!(top_view_size.height, 63.0);

        assert_eq!(bottom_view_size.width, 351.0);
        assert_eq!(bottom_view_size.height, 200.0);
    }

    #[test]
    fn aspect_ratio_flex_shrink() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(150.0), ..Default::default() },
            flex_shrink: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_flex_shrink_2() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style { size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) }, ..Default::default() }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Percent(1.0), ..Default::default() },
            flex_shrink: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        let root_child1_style = Style {
            size: Size { height: Dimension::Percent(1.0), ..Default::default() },
            flex_shrink: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        };
        let root_child1 = stretch.new_node(root_child1_style, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.add_child(root, root_child1).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let child0_size = stretch.layout(root_child0).unwrap().size;
        let child1_size = stretch.layout(root_child1).unwrap().size;

        assert_eq!(child0_size.width, 50.0);
        assert_eq!(child0_size.height, 50.0);

        assert_eq!(child1_size.width, 50.0);
        assert_eq!(child1_size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_basis() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size {
                width: Dimension::Points(100.0),
                height: Dimension::Points(100.0),
            },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            flex_basis: Dimension::Points(50.0),
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_absolute_layout_width_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0_style = Style {
            position_type: PositionType::Absolute,
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            position: Rect { start: Dimension::Points(0.0), top: Dimension::Points(0.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        };

        let root_child0 = stretch.new_node(root_child0_style, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_absolute_layout_height_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            position_type: PositionType::Absolute,
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            position: Rect { start: Dimension::Points(0.0), top: Dimension::Points(0.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_with_max_cross_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            max_size: Size { width: Dimension::Points(40.0), ..Default::default() },
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 40.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 50.0);
    }

    // #[test]
    fn aspect_ratio_with_max_main_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            max_size: Size { height: Dimension::Points(40.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 40.0);
        assert_eq!(size.height, 40.0);
    }

    #[test]
    fn aspect_ratio_with_min_main_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(30.0), ..Default::default() },
            min_size: Size { width: Dimension::Points(40.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 40.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 30.0);
    }

    // #[test]
    fn aspect_ratio_with_min_cross_defined() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(30.0), ..Default::default() },
            min_size: Size { height: Dimension::Points(40.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 40.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 40.0);
    }

    #[test]
    fn aspect_ratio_double_cross() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 100.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_half_cross() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(100.0), ..Default::default() },
            aspect_ratio: Number::Defined(0.5),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 50.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_double_main() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(0.5),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 50.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_half_main() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), ..Default::default() },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        assert_eq!(stretch.layout(root_child0).unwrap().size.width, 100.0);
        assert_eq!(stretch.layout(root_child0).unwrap().size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_width_height_flex_grow_row() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(200.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), height: Dimension::Points(50.0) },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 100.0);
    }

    // #[test]
    fn aspect_ratio_width_height_flex_grow_column() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), height: Dimension::Points(50.0) },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);

        assert_eq!(size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_height_as_flex_basis() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(200.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        let root_child1 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(100.0), ..Default::default() },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();
        stretch.add_child(root, root_child1).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(200.0), height: Number::Defined(200.0) }).unwrap();

        let child0_size = stretch.layout(root_child0).unwrap().size;
        let child1_size = stretch.layout(root_child1).unwrap().size;

        assert_eq!(child0_size.width, 75.0);
        assert_eq!(child0_size.height, 75.0);

        assert_eq!(child1_size.width, 125.0);
        assert_eq!(child1_size.height, 125.0);
    }

    #[test]
    fn aspect_ratio_width_as_flex_basis() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(200.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        let root_child1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), ..Default::default() },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();
        stretch.add_child(root, root_child1).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(200.0), height: Number::Defined(200.0) }).unwrap();

        let child0_size = stretch.layout(root_child0).unwrap().size;
        let child1_size = stretch.layout(root_child1).unwrap().size;

        assert_eq!(child0_size.width, 75.0);
        assert_eq!(child0_size.height, 75.0);

        assert_eq!(child1_size.width, 125.0);
        assert_eq!(child1_size.height, 125.0);
    }

    #[test]
    fn aspect_ratio_overrides_flex_grow_row() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(0.5),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;

        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 200.0);
    }

    #[test]
    fn aspect_ratio_overrides_flex_grow_column() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            flex_grow: 1.0,
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_left_right_absolute() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            position_type: PositionType::Absolute,
            position: Rect { start: Dimension::Points(10.0), top: Dimension::Points(10.0), end: Dimension::Points(10.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 80.0);
        assert_eq!(size.height, 80.0);
    }

    #[test]
    fn aspect_ratio_top_bottom_absolute() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            position_type: PositionType::Absolute,
            position: Rect { start: Dimension::Points(10.0), top: Dimension::Points(10.0), bottom: Dimension::Points(10.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 80.0);
        assert_eq!(size.height, 80.0);
    }

    #[test]
    fn aspect_ratio_width_overrides_align_stretch_row() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_height_overrides_align_stretch_column() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_not_allow_child_overflow_parent_size() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(4.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 100.0);
        assert_eq!(size.height, 25.0);
    }

    #[test]
    fn aspect_ratio_defined_main_with_margin() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            margin: Rect { start: Dimension::Points(10.0), end: Dimension::Points(10.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_defined_cross_with_margin() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            margin: Rect { start: Dimension::Points(10.0), end: Dimension::Points(10.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }

    #[test]
    fn aspect_ratio_defined_cross_with_main_margin() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            ..Default::default()
        }, &[]).unwrap();

        let root_child0 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(50.0), ..Default::default() },
            aspect_ratio: Number::Defined(1.0),
            margin: Rect { top: Dimension::Points(10.0), bottom: Dimension::Points(10.0), ..Default::default() },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root, root_child0).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(1000.0), height: Number::Defined(1000.0) }).unwrap();

        let size = stretch.layout(root_child0).unwrap().size;
        assert_eq!(size.width, 50.0);
        assert_eq!(size.height, 50.0);
    }
}