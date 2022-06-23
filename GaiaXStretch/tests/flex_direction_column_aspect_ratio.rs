#[cfg(test)]
mod aspect_ratio {
    use stretch::Stretch;
    use stretch::style::*;
    use stretch::geometry::*;
    use stretch::number::Number;

    #[test]
    fn aspect_ratio_percent_width_undefine_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Undefined,
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size { width: Number::Defined(1000.0), height: Number::Defined(500.0) }).unwrap();

        assert_eq!(stretch.layout(root_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(root_node).unwrap().size.height, 500.0);

        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.height, 200.0);
    }

    #[test]
    fn aspect_ratio_percent_width_auto_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        assert_eq!(stretch.layout(root_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(root_node).unwrap().size.height, 500.0);

        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.height, 200.0);
    }

    #[test]
    fn aspect_ratio_percent_width_percent_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Percent(1.0),
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        assert_eq!(stretch.layout(root_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(root_node).unwrap().size.height, 500.0);

        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.height, 200.0);
    }

    #[test]
    fn aspect_ratio_percent_width_point_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Points(2000.0),
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        assert_eq!(stretch.layout(root_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(root_node).unwrap().size.height, 500.0);

        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.width, 1000.0);
        assert_eq!(stretch.layout(aspect_ratio_node).unwrap().size.height, 200.0);
    }

    #[test]
    fn aspect_ratio_point_width_point_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Points(500.0),
                height: Dimension::Points(500.0),
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 500.0);
        assert_eq!(child_node_size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_auto_width_point_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Auto,
                height: Dimension::Points(100.0),
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 500.0);
        assert_eq!(child_node_size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_undefine_width_point_height() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Undefined,
                height: Dimension::Points(100.0),
            },
            aspect_ratio: Number::Defined(5.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 500.0);
        assert_eq!(child_node_size.height, 100.0);
    }

    #[test]
    fn aspect_ratio_nest_node() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Percent(1.0),
            },
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);


        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 1000.0);
        assert_eq!(nest_node_size.height, 500.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_point_percent() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Points(500.0),
                height: Dimension::Percent(1.0),
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 500.0);
        assert_eq!(nest_node_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_point_point() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Points(500.0),
                height: Dimension::Points(250.0),
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 500.0);
        assert_eq!(nest_node_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_point_undefine() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Points(500.0),
                height: Dimension::Undefined,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 500.0);
        assert_eq!(nest_node_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_percent_percent() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Percent(1.0),
            },
            aspect_ratio: Number::Defined(4.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 1000.0);
        assert_eq!(nest_node_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_percent_point() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Points(100.0),
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Percent(1.0),
            },
            aspect_ratio: Number::Defined(4.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 1000.0);
        assert_eq!(nest_node_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_percent_auto() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(4.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 1000.0);
        assert_eq!(nest_node_size.height, 250.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_auto_percent() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Auto,
                height: Dimension::Percent(1.0),
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 1000.0);
        assert_eq!(nest_node_size.height, 500.0);
    }

    #[test]
    fn aspect_ratio_nest_aspect_ratio_node_auto_point() {
        let mut stretch = Stretch::new();

        let root_node = stretch.new_node(
            Style {
                flex_direction: FlexDirection::Column,
                size: Size {
                    width: Dimension::Percent(1.0),
                    height: Dimension::Percent(1.0),
                    ..Default::default()
                },
                ..Default::default()
            }, &[],
        ).unwrap();

        let aspect_ratio_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Percent(1.0),
                height: Dimension::Auto,
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(root_node, aspect_ratio_node).unwrap();

        let nest_node = stretch.new_node(Style {
            size: Size {
                width: Dimension::Auto,
                height: Dimension::Points(500.0),
            },
            aspect_ratio: Number::Defined(2.0),
            ..Default::default()
        }, &[]).unwrap();

        stretch.add_child(aspect_ratio_node, nest_node).unwrap();

        stretch.compute_layout(root_node, Size {
            width: Number::Defined(1000.0),
            height: Number::Defined(500.0),
        }).unwrap();

        let node_size = stretch.layout(root_node).unwrap().size;
        assert_eq!(node_size.width, 1000.0);
        assert_eq!(node_size.height, 500.0);

        let child_node_size = stretch.layout(aspect_ratio_node).unwrap().size;
        assert_eq!(child_node_size.width, 1000.0);
        assert_eq!(child_node_size.height, 500.0);

        let nest_node_size = stretch.layout(nest_node).unwrap().size;
        assert_eq!(nest_node_size.width, 1000.0);
        assert_eq!(nest_node_size.height, 500.0);
    }
}

