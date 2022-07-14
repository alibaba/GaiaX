mod gaiax_flex_grow_and_flex_shrink {
    use stretch::Stretch;
    use stretch::style::*;
    use stretch::geometry::*;
    use stretch::number::Number;
    use stretch::number::Number::Defined;

    #[test]
    fn flex_grow_and_flex_shrink() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(400.0), height: Dimension::Points(100.0) },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), ..Default::default() },
            flex_shrink: 0.0,
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

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

    #[test]
    fn flex_grow_and_flex_shrink1() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(400.0), height: Dimension::Points(100.0) },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), height: Dimension::Points(100.0) },
            flex_shrink: 0.0,
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_shrink: 0.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), ..Default::default() },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_2 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), ..Default::default() },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

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

    // #[test]
    fn flex_grow_and_flex_shrink2() {
        let mut stretch = Stretch::new();

        let root = stretch.new_node(Style {
            size: Size { width: Dimension::Points(400.0), height: Dimension::Points(100.0) },
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(100.0), ..Default::default() },
            flex_shrink: 0.0,
            ..Default::default()
        }, &[]).unwrap();

        let container_2 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Column,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1 = stretch.new_node(Style {
            flex_grow: 1.0,
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_1 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(100.0), ..Default::default() },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_2 = stretch.new_node(Style {
            size: Size { height: Dimension::Points(100.0), ..Default::default() },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_1_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

        let container_2_1_2_1 = stretch.new_node(Style {
            size: Size { width: Dimension::Points(200.0), height: Dimension::Points(100.0) },
            flex_shrink: 1.0,
            flex_direction: FlexDirection::Row,
            ..Default::default()
        }, &[]).unwrap();

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