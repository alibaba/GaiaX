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
    fn flex_grow_and_flex_shrink2() {
        let mut stretch = Stretch::new();

        let root = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(375.0), height: Dimension::Points(100.0) },
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let group0 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(40.0), height: Dimension::Points(40.0) },
                    flex_shrink: 0.0,
                    flex_grow: 0.0,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let group2 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(35.0), height: Dimension::Points(35.0) },
                    flex_shrink: 0.0,
                    flex_grow: 0.0,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let group1 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Auto, height: Dimension::Percent(1.0) },
                    flex_grow: 1.0,
                    flex_shrink: 1.0,
                    flex_basis: Dimension::Auto,
                    flex_direction: FlexDirection::Column,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let group1_group0 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Auto, height: Dimension::Points(20.0) },
                    flex_grow: 0.0,
                    flex_shrink: 1.0,
                    flex_basis: Dimension::Auto,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        let group1_group0_group0 = stretch
            .new_node(
                Style {
                    size: Size { width: Dimension::Points(500.0), height: Dimension::Points(20.0) },
                    flex_grow: 0.0,
                    flex_shrink: 1.0,
                    flex_basis: Dimension::Auto,
                    flex_direction: FlexDirection::Row,
                    ..Default::default()
                },
                &[],
            )
            .unwrap();

        stretch.add_child(root, group0).unwrap();
        stretch.add_child(root, group1).unwrap();
        stretch.add_child(group1, group1_group0).unwrap();
        stretch.add_child(group1_group0, group1_group0_group0).unwrap();
        stretch.add_child(root, group2).unwrap();

        stretch.compute_layout(root, Size { width: Number::Defined(375.0), height: Number::Defined(100.0) }).unwrap();

        assert_eq!(stretch.layout(root).unwrap().size.width, 375.0);
        assert_eq!(stretch.layout(group0).unwrap().size.width, 40.0);
        assert_eq!(stretch.layout(group1).unwrap().size.width, 300.0);
        assert_eq!(stretch.layout(group1_group0).unwrap().size.width, 300.0);
        assert_eq!(stretch.layout(group1_group0_group0).unwrap().size.width, 300.0);
        assert_eq!(stretch.layout(group2).unwrap().size.width, 35.0);
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
