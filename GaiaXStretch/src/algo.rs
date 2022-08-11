use core::f32;

use crate::forest::{Forest, NodeData};
use crate::id::NodeId;
use crate::node::MeasureFunc;
use crate::result;
use crate::style::*;
use crate::sys;

use crate::number::Number::*;
use crate::number::*;

use crate::geometry::{Point, Rect, Size};

#[derive(Debug, Clone)]
pub struct ComputeResult {
    pub size: Size<f32>,
}

///
/// §4 Flex Items
///
struct FlexItem {
    /// 节点ID
    node: NodeId,

    /// 宽度和高度
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/width
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/height
    size: Size<Number>,

    /// 最小宽度和高度
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/min-width
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/min-height
    min_size: Size<Number>,

    /// 最大宽度和高度
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/max-width
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/max-height
    max_size: Size<Number>,

    /// 绝对布局偏移量
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/top
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/left
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/right
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/bottom
    position: Rect<Number>,

    /// 外边距
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/margin-left
    margin: Rect<f32>,

    /// 内边距
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/padding-left
    padding: Rect<f32>,

    /// 边框
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/border
    border: Rect<f32>,

    /// 主轴方向上的初始大小
    /// https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-basis
    flex_basis: f32,

    /// 主轴方向上的初始大小减去内边距和边框尺寸
    inner_flex_basis: f32,

    ///
    violation: f32,

    /// 不需要增长（grow）或压缩（shrink）
    frozen: bool,

    /// 猜测的内宽高
    /// flex项目经过最小尺寸和最大尺寸约束过后，就是猜测的内宽高
    hypothetical_inner_size: Size<f32>,

    /// 猜测的外宽高
    /// flex项目的假定内宽高加上flex项目的外边距，就是猜测的外宽高
    hypothetical_outer_size: Size<f32>,

    /// 结果内宽高
    target_size: Size<f32>,

    /// 结果外宽高
    outer_target_size: Size<f32>,

    ///
    baseline: f32,

    // temporary values for holding offset in the main / cross direction.
    // offset is the relative position from the item's natural flow position based on
    // relative position values, alignment, and justification. Does not include margin/padding/border.
    offset_main: f32,
    offset_cross: f32,
}

struct FlexLine<'a> {
    items: &'a mut [FlexItem],
    cross_size: f32,
    offset_cross: f32,
}

impl Forest {
    pub(crate) fn compute(&mut self, root: NodeId, size: Size<Number>) {
        let style: Style = self.nodes[root].style;
        let has_root_min_max = style.min_size.width.is_defined()
            || style.min_size.height.is_defined()
            || style.max_size.width.is_defined()
            || style.max_size.height.is_defined();

        let result = if has_root_min_max {
            let first_pass = self.compute_internal(root, style.size.resolve(size), size, false);

            self.compute_internal(
                root,
                Size {
                    width: first_pass
                        .size
                        .width
                        .maybe_max(style.min_size.width.resolve(size.width))
                        .maybe_min(style.max_size.width.resolve(size.width))
                        .into(),
                    height: first_pass
                        .size
                        .height
                        .maybe_max(style.min_size.height.resolve(size.height))
                        .maybe_min(style.max_size.height.resolve(size.height))
                        .into(),
                },
                size,
                true,
            )
        } else {
            self.compute_internal(root, style.size.resolve(size), size, true)
        };

        self.nodes[root].layout = result::Layout { order: 0, size: result.size, location: Point::zero() };

        Self::round_layout(&mut self.nodes, &self.children, root, 0.0, 0.0);
    }

    fn round_layout(
        nodes: &mut [NodeData],
        children: &[sys::ChildrenVec<NodeId>],
        root: NodeId,
        abs_x: f32,
        abs_y: f32,
    ) {
        let layout = &mut nodes[root].layout;
        let abs_x = abs_x + layout.location.x;
        let abs_y = abs_y + layout.location.y;

        layout.location.x = sys::round(layout.location.x);
        layout.location.y = sys::round(layout.location.y);
        layout.size.width = sys::round(abs_x + layout.size.width) - sys::round(abs_x);
        layout.size.height = sys::round(abs_y + layout.size.height) - sys::round(abs_y);
        for child in &children[root] {
            Self::round_layout(nodes, children, *child, abs_x, abs_y);
        }
    }

    /// node: 节点id
    /// node_size: 当前节点尺寸
    /// parent_size: 父节点尺寸
    #[allow(clippy::cognitive_complexity)]
    fn compute_internal(
        &mut self,
        current_node: NodeId,
        current_node_size: Size<Number>,
        parent_node_size: Size<Number>,
        perform_layout: bool,
    ) -> ComputeResult {
        self.nodes[current_node].is_dirty = false;

        // First we check if we have a result for the given input
        if let Some(ref cache) = self.nodes[current_node].layout_cache {
            if cache.perform_layout || !perform_layout {
                let width_compatible = if let Number::Defined(width) = current_node_size.width {
                    sys::abs(width - cache.result.size.width) < f32::EPSILON
                } else {
                    cache.node_size.width.is_undefined()
                };

                let height_compatible = if let Number::Defined(height) = current_node_size.height {
                    sys::abs(height - cache.result.size.height) < f32::EPSILON
                } else {
                    cache.node_size.height.is_undefined()
                };

                if width_compatible && height_compatible {
                    return cache.result.clone();
                }

                if cache.node_size == current_node_size && cache.parent_size == parent_node_size {
                    return cache.result.clone();
                }
            }
        }

        // Define some general constants we will need for the remainder
        // of the algorithm.
        // 定义一些常用的变量，方便后边算法的使用

        let current_node_style: &Style = &self.nodes[current_node].style;
        let current_node_direction: FlexDirection = current_node_style.flex_direction;
        let current_node_is_row: bool = current_node_direction.is_row();
        let current_node_is_column: bool = current_node_direction.is_column();
        let current_node_is_wrap_reverse: bool = current_node_style.flex_wrap == FlexWrap::WrapReverse;
        let current_node_is_flex_grow: bool = current_node_style.flex_grow != 0.0;

        let current_node_margin: Rect<f32> =
            current_node_style.margin.map(|n: Dimension| n.resolve(parent_node_size.width).or_else(0.0));
        let current_node_padding: Rect<f32> =
            current_node_style.padding.map(|n: Dimension| n.resolve(parent_node_size.width).or_else(0.0));
        let current_node_border: Rect<f32> =
            current_node_style.border.map(|n: Dimension| n.resolve(parent_node_size.width).or_else(0.0));

        // 内边距+边框尺寸
        let current_node_padding_border = Rect {
            start: current_node_padding.start + current_node_border.start,
            end: current_node_padding.end + current_node_border.end,
            top: current_node_padding.top + current_node_border.top,
            bottom: current_node_padding.bottom + current_node_border.bottom,
        };

        // 节点的内部尺寸 = 节点宽高减去内边距和边框
        // 可用于展示内容的区域
        let current_node_inner_size = Size {
            width: current_node_size.width - current_node_padding_border.horizontal(),
            height: current_node_size.height - current_node_padding_border.vertical(),
        };

        // 容器尺寸
        let mut current_node_container_size: Size<f32> = Size::zero();

        // 内部容器尺寸
        let mut current_node_inner_container_size: Size<f32> = Size::zero();

        // If this is a leaf node we can skip a lot of this function in some cases
        // 如果是个叶子节点，可以省略许多逻辑处理
        if self.children[current_node].is_empty() {
            // 如果叶子节点宽高已经定义了，那么直接返回结果
            if current_node_size.width.is_defined() && current_node_size.height.is_defined() {
                return ComputeResult { size: current_node_size.map(|s| s.or_else(0.0)) };
            }

            // 如果叶子节点设置了测量方法，那么使用测量方法返回结果
            if let Some(ref measure) = self.nodes[current_node].measure {
                let result = match measure {
                    MeasureFunc::Raw(measure) => ComputeResult { size: measure(current_node_size) },
                    #[cfg(any(feature = "std", feature = "alloc"))]
                    MeasureFunc::Boxed(measure) => ComputeResult { size: measure(current_node_size) },
                };
                self.nodes[current_node].layout_cache = Some(result::Cache {
                    node_size: current_node_size,
                    parent_size: parent_node_size,
                    perform_layout,
                    result: result.clone(),
                });
                return result;
            }

            // 此处有疑问? 为什么是节点尺寸 + 内边距边框尺寸
            // 按照合理来说，应该就是节点尺寸
            return ComputeResult {
                size: Size {
                    width: current_node_size.width.or_else(0.0) + current_node_padding_border.horizontal(),
                    height: current_node_size.height.or_else(0.0) + current_node_padding_border.vertical(),
                },
            };
        }

        // 9.2. Line Length Determination
        // 9.2  确定行的长度

        // 1. Generate anonymous flex items as described in §4 Flex Items.
        // 1. 根据孩子节点生成匿名的flex项目。

        let mut current_node_child_flex_items: sys::Vec<FlexItem> =
            self.get_current_node_flex_items(current_node, current_node_inner_size);

        // 2. Determine the available main and cross space for the flex items.
        //    For each dimension, if that dimension of the flex container’s content box is a definite size, use that;
        //    if that dimension of the flex container is being sized under a min or max-content constraint, the available space in
        //    that dimension is that constraint; otherwise, subtract the flex container’s
        //    margin, border, and padding from the space available to the flex container
        //    in that dimension and use that value. This might result in an infinite value.
        // 2. 确定flex项目的可用主轴空间和交叉轴空间。
        //    对于每个flex项目的尺寸:
        //    1. 如果flex容器的尺寸是一个确定的大小，则使用它。
        //    2. 如果flex容器的尺寸是在最小或最大约束下的，则可用空间的尺寸就是它的约束。
        //    3. 孩子的flex项目的可用空间，是其父flex容器的尺寸减去外边距、边框和内边距后的值。

        // 当前父节点下所有伸缩项的可用空间
        let current_node_available_space = Size {
            width: current_node_size.width.or_else(parent_node_size.width - current_node_margin.horizontal())
                - current_node_padding_border.horizontal(),
            height: current_node_size.height.or_else(parent_node_size.height - current_node_margin.vertical())
                - current_node_padding_border.vertical(),
        };

        // 当前父节点下是否存在有基线对齐样式的flex项目
        let has_baseline_child = current_node_child_flex_items.iter().any(|child| {
            self.nodes[child.node].style.align_self(&self.nodes[current_node].style) == AlignSelf::Baseline
        });

        // TODO - this does not follow spec. See commented out code below
        // 3. Determine the flex base size and hypothetical main size of each item:

        // flex_basis: 主轴方向上的初始大小 https://developer.mozilla.org/zh-CN/docs/Web/CSS/flex-basis
        // 3.1 确定每个flex项目的flex基准值。
        self.flex_items_flex_basis(
            current_node,
            current_node_direction,
            current_node_is_row,
            current_node_is_column,
            current_node_inner_size,
            &mut current_node_child_flex_items,
            current_node_available_space,
        );

        // The hypothetical main size is the item’s flex base size clamped according to its
        // used min and max main sizes (and flooring the content box size at zero).
        // 假设的主轴尺寸是一个被最小主轴尺寸和最大主轴尺寸裁剪过的。

        // 3.2 确定每个flex项目假设的主轴尺寸。
        self.flex_items_hypothetical_main_size(
            current_node_direction,
            &mut current_node_child_flex_items,
            current_node_available_space,
        );

        // 9.3. Main Size Determination
        // 9.3  确定flex项目的主轴尺寸

        // 5. Collect flex items into flex lines:
        //    - If the flex container is single-line, collect all the flex items into
        //      a single flex line.
        //    - Otherwise, starting from the first uncollected item, collect consecutive
        //      items one by one until the first time that the next collected item would
        //      not fit into the flex container’s inner main size (or until a forced break
        //      is encountered, see §10 Fragmenting Flex Layout). If the very first
        //      uncollected item wouldn’t fit, collect just it into the line.
        //
        //      For this step, the size of a flex item is its outer hypothetical main size. (Note: This can be negative.)
        //      Repeat until all flex items have been collected into flex lines
        //
        //      Note that the "collect as many" line will collect zero-sized flex items onto
        //      the end of the previous line even if the last non-zero item exactly "filled up" the line.
        // 5. 将flex元素收集到flex项目行中:
        //    - 如果flex容器是单行的，则将所有flex项目项到一个flex项目行中。

        let mut flex_lines: sys::Vec<_> = {
            let mut lines = sys::new_vec_with_capacity(1);

            let style: Style = self.nodes[current_node].style;
            if style.flex_wrap == FlexWrap::NoWrap {
                lines.push(FlexLine {
                    items: current_node_child_flex_items.as_mut_slice(),
                    cross_size: 0.0,
                    offset_cross: 0.0,
                });
            } else {
                let mut flex_items = &mut current_node_child_flex_items[..];

                while !flex_items.is_empty() {
                    let mut line_length = 0.0;
                    let index = flex_items
                        .iter()
                        .enumerate()
                        .find(|&(idx, ref child)| {
                            line_length += child.hypothetical_outer_size.main(current_node_direction);
                            if let Defined(main) = current_node_available_space.main(current_node_direction) {
                                line_length > main && idx != 0
                            } else {
                                false
                            }
                        })
                        .map(|(idx, _)| idx)
                        .unwrap_or(flex_items.len());

                    let (items, rest) = flex_items.split_at_mut(index);
                    lines.push(FlexLine { items, cross_size: 0.0, offset_cross: 0.0 });
                    flex_items = rest;
                }
            }

            lines
        };

        // 6. Resolve the flexible lengths of all the flex items to find their used main size.
        //    See §9.7 Resolving Flexible Lengths.
        // 6. 解析所有flex项目的flex长度，以找到它们使用的主轴尺寸。参见§9.7解析flex项目项的长度。

        // 9.7 Resolving Flexible Lengths
        // 9.7 解析确定flex项目的长度

        self.flex_lines_main_size(
            parent_node_size,
            current_node_direction,
            current_node_is_row,
            current_node_is_column,
            current_node_inner_size,
            current_node_available_space,
            &mut flex_lines,
        );

        // Not part of the spec from what i can see but seems correct
        // 设置flex项目行容器的主轴尺寸
        Forest::container_main_size(
            current_node_size,
            current_node_direction,
            current_node_padding_border,
            &mut current_node_container_size,
            &mut current_node_inner_container_size,
            current_node_available_space,
            &mut flex_lines,
        );

        // 9.4. Cross Size Determination
        // 确定交叉轴尺寸

        // 7. Determine the hypothetical cross size of each item by performing layout with the
        //    used main size and the available space, treating auto as fit-content.
        // 执行布局测量结合主轴尺寸和可用空间确定猜测的交叉轴尺寸

        self.flex_items_hypothetical_cross(
            current_node,
            current_node_size,
            current_node_direction,
            current_node_is_row,
            current_node_container_size,
            current_node_available_space,
            &mut flex_lines,
        );

        // TODO - probably should move this somewhere else as it doesn't make a ton of sense here but we need it below
        // TODO - This is expensive and should only be done if we really require a baseline. aka, make it lazy

        fn calc_baseline(db: &Forest, node: NodeId, layout: &result::Layout) -> f32 {
            if db.children[node].is_empty() {
                layout.size.height
            } else {
                let child = db.children[node][0];
                calc_baseline(db, child, &db.nodes[child].layout)
            }
        }

        if has_baseline_child {
            for line in &mut flex_lines {
                for target in line.items.iter_mut() {
                    let child: &mut FlexItem = target;

                    let result = self.compute_internal(
                        child.node,
                        Size {
                            width: if current_node_is_row {
                                child.target_size.width.into()
                            } else {
                                child.hypothetical_inner_size.width.into()
                            },
                            height: if current_node_is_row {
                                child.hypothetical_inner_size.height.into()
                            } else {
                                child.target_size.height.into()
                            },
                        },
                        Size {
                            width: if current_node_is_row {
                                current_node_container_size.width.into()
                            } else {
                                current_node_size.width
                            },
                            height: if current_node_is_row {
                                current_node_size.height
                            } else {
                                current_node_container_size.height.into()
                            },
                        },
                        true,
                    );

                    child.baseline = calc_baseline(
                        self,
                        child.node,
                        &result::Layout {
                            order: self.children[current_node].iter().position(|n| *n == child.node).unwrap() as u32,
                            size: result.size,
                            location: Point::zero(),
                        },
                    );
                }
            }
        }

        // 8. Calculate the cross size of each flex line.
        //    If the flex container is single-line and has a definite cross size, the cross size
        //    of the flex line is the flex container’s inner cross size. Otherwise, for each flex line:
        //
        //    If the flex container is single-line, then clamp the line’s cross-size to be within
        //    the container’s computed min and max cross sizes. Note that if CSS 2.1’s definition
        //    of min/max-width/height applied more generally, this behavior would fall out automatically.
        // 计算flex项目行容器的交叉轴尺寸
        //    如果flex项目行容器是个单行的，并且有确定的交叉轴尺寸，这个尺寸就是flex项目行容器的内部交叉轴尺寸。
        //

        if flex_lines.len() == 1 && current_node_size.cross(current_node_direction).is_defined() {
            let size_cross = current_node_size.cross(current_node_direction);
            let padding_border_cross = current_node_padding_border.cross(current_node_direction);
            flex_lines[0].cross_size = (size_cross - padding_border_cross).or_else(0.0);
        } else {
            for line in &mut flex_lines {
                //    1. Collect all the flex items whose inline-axis is parallel to the main-axis, whose
                //       align-self is baseline, and whose cross-axis margins are both non-auto. Find the
                //       largest of the distances between each item’s baseline and its hypothetical outer
                //       cross-start edge, and the largest of the distances between each item’s baseline
                //       and its hypothetical outer cross-end edge, and sum these two values.

                //    2. Among all the items not collected by the previous step, find the largest
                //       outer hypothetical cross size.

                //    3. The used cross-size of the flex line is the largest of the numbers found in the
                //       previous two steps and zero.

                let max_baseline: f32 = line.items.iter().map(|child| child.baseline).fold(0.0, |acc, x| acc.max(x));
                let mut is_have_aspect_ratio_item: bool = false;
                let mut is_have_point_size_item: bool = false;
                let final_cross_size: f64 = line
                    .items
                    .iter()
                    .map(|child: &FlexItem| {
                        let child_style: &Style = &self.nodes[child.node].style;
                        if child_style.aspect_ratio.is_defined() {
                            is_have_aspect_ratio_item = true;
                        }
                        if child_style.size.width.is_defined() {
                            is_have_point_size_item = true;
                        }
                        if child_style.align_self(&self.nodes[current_node].style) == AlignSelf::Baseline
                            && child_style.cross_margin_start(current_node_direction) != Dimension::Auto
                            && child_style.cross_margin_end(current_node_direction) != Dimension::Auto
                            && child_style.cross_size(current_node_direction) == Dimension::Auto
                        {
                            max_baseline - child.baseline + child.hypothetical_outer_size.cross(current_node_direction)
                        } else {
                            child.hypothetical_outer_size.cross(current_node_direction)
                        }
                    })
                    .fold(0.0, |acc: f64, x: f32| acc.max(x as f64));

                // fix: aspect_ratio_multi_4_aspect_ratio_flex_grow_point_width
                // 修复：flex-grow与aspect-ratio嵌套组合使用时，cross-size受到同级孩子的固定宽度影响，导致flex-grow计算错误的问题。
                if current_node_is_column
                    && current_node_is_flex_grow
                    && is_have_aspect_ratio_item
                    && is_have_point_size_item
                {
                    line.cross_size = 0.0
                } else {
                    line.cross_size = final_cross_size as f32;
                }
            }
        }

        // 9. Handle 'align-content: stretch'. If the flex container has a definite cross size,
        //    align-content is stretch, and the sum of the flex lines' cross sizes is less than
        //    the flex container’s inner cross size, increase the cross size of each flex line
        //    by equal amounts such that the sum of their cross sizes exactly equals the
        //    flex container’s inner cross size.

        //    CSS 的 align-content 属性设置了浏览器如何沿着弹性盒子布局的纵轴和网格布局的主轴在内容项之间和周围分配空间。
        //    处理'align-content:stretch'
        //    align-content: stretch; 均匀分布项目 拉伸‘自动’-大小的项目以充满容器

        if self.nodes[current_node].style.align_content == AlignContent::Stretch
            && current_node_size.cross(current_node_direction).is_defined()
        {
            let total_cross: f32 = flex_lines.iter().map(|line| line.cross_size).sum();
            let inner_cross = (current_node_size.cross(current_node_direction)
                - current_node_padding_border.cross(current_node_direction))
            .or_else(0.0);

            if total_cross < inner_cross {
                let remaining = inner_cross - total_cross;
                let addition = remaining / flex_lines.len() as f32;
                flex_lines.iter_mut().for_each(|line| line.cross_size += addition);
            }
        }

        // 10. Collapse visibility:collapse items. If any flex items have visibility: collapse,
        //     note the cross size of the line they’re in as the item’s strut size, and restart
        //     layout from the beginning.
        //
        //     In this second layout round, when collecting items into lines, treat the collapsed
        //     items as having zero main size. For the rest of the algorithm following that step,
        //     ignore the collapsed items entirely (as if they were display:none) except that after
        //     calculating the cross size of the lines, if any line’s cross size is less than the
        //     largest strut size among all the collapsed items in the line, set its cross size to
        //     that strut size.
        //
        //     Skip this step in the second layout round.

        // TODO implement once (if ever) we support visibility:collapse

        // 11. Determine the used cross size of each flex item. If a flex item has align-self: stretch,
        //     its computed cross size property is auto, and neither of its cross-axis margins are auto,
        //     the used outer cross size is the used cross size of its flex line, clamped according to
        //     the item’s used min and max cross sizes. Otherwise, the used cross size is the item’s
        //     hypothetical cross size.
        //
        //     If the flex item has align-self: stretch, redo layout for its contents, treating this
        //     used size as its definite cross size so that percentage-sized children can be resolved.
        //
        //     Note that this step does not affect the main size of the flex item, even if it has an
        //     intrinsic aspect ratio.

        for line in &mut flex_lines {
            let line_cross_size: f32 = line.cross_size;

            for target in line.items.iter_mut() {
                let child: &mut FlexItem = target;
                let parent_style: &Style = &self.nodes[current_node].style;
                let child_style: &Style = &self.nodes[child.node].style;

                let child_cross_size = Forest::get_cross_size(
                    current_node_size,
                    parent_node_size,
                    current_node_direction,
                    current_node_is_row,
                    line_cross_size,
                    child,
                    parent_style,
                    child_style,
                );

                child.target_size.set_cross(current_node_direction, child_cross_size);

                let outer_size_cross =
                    child.target_size.cross(current_node_direction) + child.margin.cross(current_node_direction);
                child.outer_target_size.set_cross(current_node_direction, outer_size_cross);
            }
        }

        // 9.5. Main-Axis Alignment

        // 12. Distribute any remaining free space. For each flex line:
        //     1. If the remaining free space is positive and at least one main-axis margin on this
        //        line is auto, distribute the free space equally among these margins. Otherwise,
        //        set all auto margins to zero.
        //     2. Align the items along the main-axis per justify-content.

        for line in &mut flex_lines {
            let used_space: f32 =
                line.items.iter().map(|child| child.outer_target_size.main(current_node_direction)).sum();
            let free_space = current_node_inner_container_size.main(current_node_direction) - used_space;
            let mut num_auto_margins = 0;

            for child in line.items.iter_mut() {
                let child_style = &self.nodes[child.node].style;
                if child_style.main_margin_start(current_node_direction) == Dimension::Auto {
                    num_auto_margins += 1;
                }
                if child_style.main_margin_end(current_node_direction) == Dimension::Auto {
                    num_auto_margins += 1;
                }
            }

            if free_space > 0.0 && num_auto_margins > 0 {
                let margin = free_space / num_auto_margins as f32;

                for child in line.items.iter_mut() {
                    let child_style: &Style = &self.nodes[child.node].style;
                    if child_style.main_margin_start(current_node_direction) == Dimension::Auto {
                        if current_node_is_row {
                            child.margin.start = margin;
                        } else {
                            child.margin.top = margin;
                        }
                    }
                    if child_style.main_margin_end(current_node_direction) == Dimension::Auto {
                        if current_node_is_row {
                            child.margin.end = margin;
                        } else {
                            child.margin.bottom = margin;
                        }
                    }
                }
            } else {
                let num_items = line.items.len();
                let layout_reverse = current_node_direction.is_reverse();

                let justify_item = |(i, child): (usize, &mut FlexItem)| {
                    let is_first = i == 0;

                    child.offset_main = match self.nodes[current_node].style.justify_content {
                        JustifyContent::FlexStart => {
                            if layout_reverse && is_first {
                                free_space
                            } else {
                                0.0
                            }
                        }
                        JustifyContent::Center => {
                            if is_first {
                                free_space / 2.0
                            } else {
                                0.0
                            }
                        }
                        JustifyContent::FlexEnd => {
                            if is_first && !layout_reverse {
                                free_space
                            } else {
                                0.0
                            }
                        }
                        JustifyContent::SpaceBetween => {
                            if is_first {
                                0.0
                            } else {
                                free_space / (num_items - 1) as f32
                            }
                        }
                        JustifyContent::SpaceAround => {
                            if is_first {
                                (free_space / num_items as f32) / 2.0
                            } else {
                                free_space / num_items as f32
                            }
                        }
                        JustifyContent::SpaceEvenly => free_space / (num_items + 1) as f32,
                    };
                };

                if layout_reverse {
                    line.items.iter_mut().rev().enumerate().for_each(justify_item);
                } else {
                    line.items.iter_mut().enumerate().for_each(justify_item);
                }
            }
        }

        // 9.6. Cross-Axis Alignment

        // 13. Resolve cross-axis auto margins. If a flex item has auto cross-axis margins:
        //     - If its outer cross size (treating those auto margins as zero) is less than the
        //       cross size of its flex line, distribute the difference in those sizes equally
        //       to the auto margins.
        //     - Otherwise, if the block-start or inline-start margin (whichever is in the cross axis)
        //       is auto, set it to zero. Set the opposite margin so that the outer cross size of the
        //       item equals the cross size of its flex line.

        for line in &mut flex_lines {
            let line_cross_size = line.cross_size;
            let max_baseline: f32 = line.items.iter_mut().map(|child| child.baseline).fold(0.0, |acc, x| acc.max(x));

            for child in line.items.iter_mut() {
                let free_space = line_cross_size - child.outer_target_size.cross(current_node_direction);
                let child_style = &self.nodes[child.node].style;

                if child_style.cross_margin_start(current_node_direction) == Dimension::Auto
                    && child_style.cross_margin_end(current_node_direction) == Dimension::Auto
                {
                    if current_node_is_row {
                        child.margin.top = free_space / 2.0;
                        child.margin.bottom = free_space / 2.0;
                    } else {
                        child.margin.start = free_space / 2.0;
                        child.margin.end = free_space / 2.0;
                    }
                } else if child_style.cross_margin_start(current_node_direction) == Dimension::Auto {
                    if current_node_is_row {
                        child.margin.top = free_space;
                    } else {
                        child.margin.start = free_space;
                    }
                } else if child_style.cross_margin_end(current_node_direction) == Dimension::Auto {
                    if current_node_is_row {
                        child.margin.bottom = free_space;
                    } else {
                        child.margin.end = free_space;
                    }
                } else {
                    // 14. Align all flex items along the cross-axis per align-self, if neither of the item’s
                    //     cross-axis margins are auto.

                    child.offset_cross = match child_style.align_self(&self.nodes[current_node].style) {
                        AlignSelf::Auto => 0.0, // Should never happen
                        AlignSelf::FlexStart => {
                            if current_node_is_wrap_reverse {
                                free_space
                            } else {
                                0.0
                            }
                        }
                        AlignSelf::FlexEnd => {
                            if current_node_is_wrap_reverse {
                                0.0
                            } else {
                                free_space
                            }
                        }
                        AlignSelf::Center => free_space / 2.0,
                        AlignSelf::Baseline => {
                            if current_node_is_row {
                                max_baseline - child.baseline
                            } else {
                                // baseline alignment only makes sense if the direction is row
                                // we treat it as flex-start alignment in columns.
                                if current_node_is_wrap_reverse {
                                    free_space
                                } else {
                                    0.0
                                }
                            }
                        }
                        AlignSelf::Stretch => {
                            if current_node_is_wrap_reverse {
                                free_space
                            } else {
                                0.0
                            }
                        }
                    };
                }
            }
        }

        // 15. Determine the flex container’s used cross size:
        //     - If the cross size property is a definite size, use that, clamped by the used
        //       min and max cross sizes of the flex container.
        //     - Otherwise, use the sum of the flex lines' cross sizes, clamped by the used
        //       min and max cross sizes of the flex container.
        // 15 确定flex容器交叉轴的尺寸
        //     - 如果交叉轴属性是一个确定的值，就使用它，同是该值需要被交叉轴最小值和交叉轴最大值约束。
        //     - 否则，使用flex项目行的交叉轴尺寸，同是该值需要被交叉轴最小值和交叉轴最大值约束。

        let total_cross_size: f32 = flex_lines.iter().map(|line| line.cross_size).sum();
        current_node_container_size.set_cross(
            current_node_direction,
            current_node_size
                .cross(current_node_direction)
                .or_else(total_cross_size + current_node_padding_border.cross(current_node_direction)),
        );
        current_node_inner_container_size.set_cross(
            current_node_direction,
            current_node_container_size.cross(current_node_direction)
                - current_node_padding_border.cross(current_node_direction),
        );

        // We have the container size. If our caller does not care about performing
        // layout we are done now.
        if !perform_layout {
            let result = ComputeResult { size: current_node_container_size };
            self.nodes[current_node].layout_cache = Some(result::Cache {
                node_size: current_node_size,
                parent_size: parent_node_size,
                perform_layout,
                result: result.clone(),
            });
            return result;
        }

        // 16. Align all flex lines per align-content.

        let free_space = current_node_inner_container_size.cross(current_node_direction) - total_cross_size;
        let num_lines = flex_lines.len();

        let align_line = |(i, line): (usize, &mut FlexLine)| {
            let is_first = i == 0;

            line.offset_cross = match self.nodes[current_node].style.align_content {
                AlignContent::FlexStart => {
                    if is_first && current_node_is_wrap_reverse {
                        free_space
                    } else {
                        0.0
                    }
                }
                AlignContent::FlexEnd => {
                    if is_first && !current_node_is_wrap_reverse {
                        free_space
                    } else {
                        0.0
                    }
                }
                AlignContent::Center => {
                    if is_first {
                        free_space / 2.0
                    } else {
                        0.0
                    }
                }
                AlignContent::Stretch => 0.0,
                AlignContent::SpaceBetween => {
                    if is_first {
                        0.0
                    } else {
                        free_space / (num_lines - 1) as f32
                    }
                }
                AlignContent::SpaceAround => {
                    if is_first {
                        (free_space / num_lines as f32) / 2.0
                    } else {
                        free_space / num_lines as f32
                    }
                }
            };
        };

        if current_node_is_wrap_reverse {
            flex_lines.iter_mut().rev().enumerate().for_each(align_line);
        } else {
            flex_lines.iter_mut().enumerate().for_each(align_line);
        }

        // Do a final layout pass and gather the resulting layouts
        {
            let mut total_offset_cross = current_node_padding_border.cross_start(current_node_direction);

            let layout_line = |line: &mut FlexLine| {
                let mut total_offset_main = current_node_padding_border.main_start(current_node_direction);
                let line_offset_cross = line.offset_cross;

                let layout_item = |child: &mut FlexItem| {
                    let result = self.compute_internal(
                        child.node,
                        child.target_size.map(|s| s.into()),
                        current_node_container_size.map(|s| s.into()),
                        true,
                    );

                    let offset_main = total_offset_main
                        + child.offset_main
                        + child.margin.main_start(current_node_direction)
                        + (child.position.main_start(current_node_direction).or_else(0.0)
                            - child.position.main_end(current_node_direction).or_else(0.0));

                    let offset_cross = total_offset_cross
                        + child.offset_cross
                        + line_offset_cross
                        + child.margin.cross_start(current_node_direction)
                        + (child.position.cross_start(current_node_direction).or_else(0.0)
                            - child.position.cross_end(current_node_direction).or_else(0.0));

                    self.nodes[child.node].layout = result::Layout {
                        order: self.children[current_node].iter().position(|n| *n == child.node).unwrap() as u32,
                        size: result.size,
                        location: Point {
                            x: if current_node_is_row { offset_main } else { offset_cross },
                            y: if current_node_is_column { offset_main } else { offset_cross },
                        },
                    };

                    total_offset_main += child.offset_main
                        + child.margin.main(current_node_direction)
                        + result.size.main(current_node_direction);
                };

                if current_node_direction.is_reverse() {
                    line.items.iter_mut().rev().for_each(layout_item);
                } else {
                    line.items.iter_mut().for_each(layout_item);
                }

                total_offset_cross += line_offset_cross + line.cross_size;
            };

            if current_node_is_wrap_reverse {
                flex_lines.iter_mut().rev().for_each(layout_line);
            } else {
                flex_lines.iter_mut().for_each(layout_line);
            }
        }

        // Before returning we perform absolute layout on all absolutely positioned children
        {
            // TODO: remove number of Vec<_> generated
            let candidates = self.children[current_node]
                .iter()
                .cloned()
                .enumerate()
                .filter(|(_, child)| self.nodes[*child].style.position_type == PositionType::Absolute)
                .collect::<sys::Vec<_>>();

            for (order, child) in candidates {
                let container_width = current_node_container_size.width.into();
                let container_height = current_node_container_size.height.into();

                let child_style: Style = self.nodes[child].style;

                let start = child_style.position.start.resolve(container_width)
                    + child_style.margin.start.resolve(container_width);
                let end =
                    child_style.position.end.resolve(container_width) + child_style.margin.end.resolve(container_width);
                let top = child_style.position.top.resolve(container_height)
                    + child_style.margin.top.resolve(container_height);
                let bottom = child_style.position.bottom.resolve(container_height)
                    + child_style.margin.bottom.resolve(container_height);

                let (start_main, end_main) = if current_node_is_row { (start, end) } else { (top, bottom) };
                let (start_cross, end_cross) = if current_node_is_row { (top, bottom) } else { (start, end) };

                let mut width = child_style
                    .size
                    .width
                    .resolve(container_width)
                    .maybe_max(child_style.min_size.width.resolve(container_width))
                    .maybe_min(child_style.max_size.width.resolve(container_width))
                    .or_else(if start.is_defined() && end.is_defined() {
                        container_width - start - end
                    } else {
                        Undefined
                    });

                let mut height = child_style
                    .size
                    .height
                    .resolve(container_height)
                    .maybe_max(child_style.min_size.height.resolve(container_height))
                    .maybe_min(child_style.max_size.height.resolve(container_height))
                    .or_else(if top.is_defined() && bottom.is_defined() {
                        container_height - top - bottom
                    } else {
                        Undefined
                    });

                // fix: aspect_ratio_absolute_layout_width_defined
                if child_style.aspect_ratio.is_defined() && !height.is_defined() && width.is_defined() {
                    height = Number::Defined(width.or_else(0.0) / child_style.aspect_ratio.or_else(0.0));
                }
                // fix: aspect_ratio_absolute_layout_height_defined
                else if child_style.aspect_ratio.is_defined() && height.is_defined() && !width.is_defined() {
                    width = Number::Defined(height.or_else(0.0) * child_style.aspect_ratio.or_else(0.0));
                }

                let result = self.compute_internal(
                    child,
                    Size { width, height },
                    Size { width: container_width, height: container_height },
                    true,
                );

                let free_main_space = current_node_container_size.main(current_node_direction)
                    - result
                        .size
                        .main(current_node_direction)
                        .maybe_max(
                            child_style
                                .min_main_size(current_node_direction)
                                .resolve(current_node_inner_size.main(current_node_direction)),
                        )
                        .maybe_min(
                            child_style
                                .max_main_size(current_node_direction)
                                .resolve(current_node_inner_size.main(current_node_direction)),
                        );

                let free_cross_space = current_node_container_size.cross(current_node_direction)
                    - result
                        .size
                        .cross(current_node_direction)
                        .maybe_max(
                            child_style
                                .min_cross_size(current_node_direction)
                                .resolve(current_node_inner_size.cross(current_node_direction)),
                        )
                        .maybe_min(
                            child_style
                                .max_cross_size(current_node_direction)
                                .resolve(current_node_inner_size.cross(current_node_direction)),
                        );

                let offset_main = if start_main.is_defined() {
                    start_main.or_else(0.0) + current_node_border.main_start(current_node_direction)
                } else if end_main.is_defined() {
                    free_main_space - end_main.or_else(0.0) - current_node_border.main_end(current_node_direction)
                } else {
                    match self.nodes[current_node].style.justify_content {
                        JustifyContent::SpaceBetween | JustifyContent::FlexStart => {
                            current_node_padding_border.main_start(current_node_direction)
                        }
                        JustifyContent::FlexEnd => {
                            free_main_space - current_node_padding_border.main_end(current_node_direction)
                        }
                        JustifyContent::SpaceEvenly | JustifyContent::SpaceAround | JustifyContent::Center => {
                            free_main_space / 2.0
                        }
                    }
                };

                let offset_cross = if start_cross.is_defined() {
                    start_cross.or_else(0.0) + current_node_border.cross_start(current_node_direction)
                } else if end_cross.is_defined() {
                    free_cross_space - end_cross.or_else(0.0) - current_node_border.cross_end(current_node_direction)
                } else {
                    match child_style.align_self(&self.nodes[current_node].style) {
                        AlignSelf::Auto => 0.0, // Should never happen
                        AlignSelf::FlexStart => {
                            if current_node_is_wrap_reverse {
                                free_cross_space - current_node_padding_border.cross_end(current_node_direction)
                            } else {
                                current_node_padding_border.cross_start(current_node_direction)
                            }
                        }
                        AlignSelf::FlexEnd => {
                            if current_node_is_wrap_reverse {
                                current_node_padding_border.cross_start(current_node_direction)
                            } else {
                                free_cross_space - current_node_padding_border.cross_end(current_node_direction)
                            }
                        }
                        AlignSelf::Center => free_cross_space / 2.0,
                        AlignSelf::Baseline => free_cross_space / 2.0, // Treat as center for now until we have baseline support
                        AlignSelf::Stretch => {
                            if current_node_is_wrap_reverse {
                                free_cross_space - current_node_padding_border.cross_end(current_node_direction)
                            } else {
                                current_node_padding_border.cross_start(current_node_direction)
                            }
                        }
                    }
                };

                self.nodes[child].layout = result::Layout {
                    order: order as u32,
                    size: result.size,
                    location: Point {
                        x: if current_node_is_row { offset_main } else { offset_cross },
                        y: if current_node_is_column { offset_main } else { offset_cross },
                    },
                };
            }
        }

        fn hidden_layout(nodes: &mut [NodeData], children: &[sys::ChildrenVec<NodeId>], node: NodeId, order: u32) {
            nodes[node].layout = result::Layout { order, size: Size::zero(), location: Point::zero() };

            for (order, child) in children[node].iter().enumerate() {
                hidden_layout(nodes, children, *child, order as _);
            }
        }

        for (order, child) in self.children[current_node].iter().enumerate() {
            if self.nodes[*child].style.display == Display::None {
                hidden_layout(&mut self.nodes, &self.children, *child, order as _);
            }
        }

        let result = ComputeResult { size: current_node_container_size };
        self.nodes[current_node].layout_cache = Some(result::Cache {
            node_size: current_node_size,
            parent_size: parent_node_size,
            perform_layout,
            result: result.clone(),
        });

        result
    }

    fn flex_items_hypothetical_cross(
        &mut self,
        parent_node: NodeId,
        parent_node_size: Size<Number>,
        parent_node_direction: FlexDirection,
        parent_node_is_row: bool,
        parent_node_container_size: Size<f32>,
        current_node_available_space: Size<Number>,
        flex_lines: &mut sys::Vec<FlexLine>,
    ) {
        for line in &mut flex_lines[..] {
            let line_cross_size = line.cross_size;

            for target in line.items.iter_mut() {
                let child: &mut FlexItem = target;

                // 交叉轴数值
                let child_cross = child
                    .size
                    .cross(parent_node_direction)
                    .maybe_max(child.min_size.cross(parent_node_direction))
                    .maybe_min(child.max_size.cross(parent_node_direction));

                // 孩子节点尺寸
                let child_node_size: Size<Number> = Size {
                    width: if parent_node_is_row { child.target_size.width.into() } else { child_cross },
                    height: if parent_node_is_row { child_cross } else { child.target_size.height.into() },
                };

                // 父节点尺寸
                let parent_node_size = Size {
                    width: if parent_node_is_row {
                        parent_node_container_size.main(parent_node_direction).into()
                    } else {
                        current_node_available_space.width
                    },
                    height: if parent_node_is_row {
                        current_node_available_space.height
                    } else {
                        parent_node_container_size.main(parent_node_direction).into()
                    },
                };

                // 执行布局测量获得孩子节点的交叉轴尺寸
                let mut child_cross_size = self
                    .compute_internal(child.node, child_node_size, parent_node_size, false)
                    .size
                    .cross(parent_node_direction)
                    .maybe_max(child.min_size.cross(parent_node_direction))
                    .maybe_min(child.max_size.cross(parent_node_direction));

                // 如果交叉轴尺寸为0，那么手动尝试计算一下
                if child_cross_size == 0.0 {
                    let parent_style: &Style = &self.nodes[parent_node].style;
                    let child_style: &Style = &self.nodes[child.node].style;
                    child_cross_size = Forest::get_cross_size(
                        parent_node_size,
                        parent_node_size,
                        parent_node_direction,
                        parent_node_is_row,
                        line_cross_size,
                        child,
                        parent_style,
                        child_style,
                    );
                }

                // 设置孩子节点的猜测的内部交叉轴尺寸
                child.hypothetical_inner_size.set_cross(parent_node_direction, child_cross_size);

                // 设置孩子节点的猜测的外部交叉轴尺寸
                let outer_size_cross = child.hypothetical_inner_size.cross(parent_node_direction)
                    + child.margin.cross(parent_node_direction);
                child.hypothetical_outer_size.set_cross(parent_node_direction, outer_size_cross);
            }
        }
    }

    fn container_main_size(
        current_node_size: Size<Number>,
        current_node_dir: FlexDirection,
        current_node_padding_border: Rect<f32>,
        current_node_container_size: &mut Size<f32>,
        current_node_inner_container_size: &mut Size<f32>,
        current_node_available_space: Size<Number>,
        flex_lines: &mut sys::Vec<FlexLine>,
    ) {
        let container_size_value = current_node_size.main(current_node_dir).or_else({
            let longest_line = flex_lines.iter().fold(f32::MIN, |acc, line| {
                let length: f32 = line.items.iter().map(|item| item.outer_target_size.main(current_node_dir)).sum();
                acc.max(length)
            });

            let size = longest_line + current_node_padding_border.main(current_node_dir);
            match current_node_available_space.main(current_node_dir) {
                Defined(val) if flex_lines.len() > 1 && size < val => val,
                _ => size,
            }
        });
        current_node_container_size.set_main(current_node_dir, container_size_value);
        // 设置flex项目行容器的主轴内部尺寸
        current_node_inner_container_size.set_main(
            current_node_dir,
            current_node_container_size.main(current_node_dir) - current_node_padding_border.main(current_node_dir),
        );
    }

    fn flex_lines_main_size(
        &mut self,
        parent_node_size: Size<Number>,
        parent_node_dir: FlexDirection,
        parent_node_is_row: bool,
        parent_node_is_column: bool,
        parent_node_inner_size: Size<Number>,
        parent_node_available_space: Size<Number>,
        flex_lines: &mut sys::Vec<FlexLine>,
    ) {
        for line in &mut flex_lines[..] {
            // 1. Determine the used flex factor. Sum the outer hypothetical main sizes of all
            //    items on the line. If the sum is less than the flex container’s inner main size,
            //    use the flex grow factor for the rest of this algorithm; otherwise, use the
            //    flex shrink factor.
            // 1. 确定使用的flex因子。
            //    将所有当前行的flex项目猜测的外部主轴尺寸相加。
            //    如果之和小于父节点flex容器的内部主轴尺寸大小，使用flex增长因子算法;否则，使用flex收缩因子算法。

            let line_used_flex_factor: f32 =
                line.items.iter().map(|child: &FlexItem| child.hypothetical_outer_size.main(parent_node_dir)).sum();
            let parent_node_inner_size_main = parent_node_inner_size.main(parent_node_dir).or_else(0.0);
            let line_of_growing = line_used_flex_factor < parent_node_inner_size_main;
            let line_of_shrinking = !line_of_growing;

            // 2. Size inflexible items. Freeze, setting its target main size to its hypothetical main size
            //    - Any item that has a flex factor of zero
            //    - If using the flex grow factor: any item that has a flex base size
            //      greater than its hypothetical main size
            //    - If using the flex shrink factor: any item that has a flex base size
            //      smaller than its hypothetical main size
            // 2. 对于无法伸缩的flex项目。冻结住，设置flex项目的主轴尺寸就是其猜测的主轴尺寸。
            //    - flex因子为零的任何项
            //    - 如果使用flex增长因子: 任何具有基准大小的flex项目比其猜测的主轴尺寸更大
            //    - 如果使用flex收缩因子: 任何具有基准大小的flex项目比其猜测的主轴尺寸更小

            for target in line.items.iter_mut() {
                let child: &mut FlexItem = target;
                // TODO - This is not found by reading the spec. Maybe this can be done in some other place
                // instead. This was found by trail and error fixing tests to align with webkit output.
                // 如果父节点是横向排列 && 父节点的主轴尺寸未定义
                // 需要根据孩子节点的尺寸计算的结果当做flex项目的主轴尺寸
                if parent_node_is_row && parent_node_inner_size.main(parent_node_dir).is_undefined() {
                    let child_target_node_size = Size {
                        width: child.size.width.maybe_max(child.min_size.width).maybe_min(child.max_size.width),
                        height: child.size.height.maybe_max(child.min_size.height).maybe_min(child.max_size.height),
                    };
                    let child_target_main_size = self
                        .compute_internal(child.node, child_target_node_size, parent_node_available_space, false)
                        .size
                        .main(parent_node_dir)
                        .maybe_max(child.min_size.main(parent_node_dir))
                        .maybe_min(child.max_size.main(parent_node_dir));
                    child.target_size.set_main(parent_node_dir, child_target_main_size);
                }
                // 使用猜测的内部主轴尺寸作为flex项目的主轴尺寸
                else {
                    let target_size_main = child.hypothetical_inner_size.main(parent_node_dir);
                    child.target_size.set_main(parent_node_dir, target_size_main);
                }

                // TODO this should really only be set inside the if-statement below but
                // that causes the target_main_size to never be set for some items

                // 使用flex项目的主轴尺寸加上外边距，作为flex项目的外部主轴尺寸
                let child_out_target_size_main =
                    child.target_size.main(parent_node_dir) + child.margin.main(parent_node_dir);
                child.outer_target_size.set_main(parent_node_dir, child_out_target_size_main);

                // 判定是否需要冻结孩子节点
                let child_style: &Style = &self.nodes[child.node].style;
                let child_is_no_grow_and_no_shrink = child_style.flex_grow == 0.0 && child_style.flex_shrink == 0.0;
                let child_is_no_grow =
                    line_of_growing && child.flex_basis > child.hypothetical_inner_size.main(parent_node_dir);
                let child_is_no_shrink =
                    line_of_shrinking && child.flex_basis < child.hypothetical_inner_size.main(parent_node_dir);

                if child_is_no_grow_and_no_shrink || child_is_no_grow || child_is_no_shrink {
                    child.frozen = true;
                }
            }

            // 3. Calculate initial free space. Sum the outer sizes of all items on the line,
            //    and subtract this from the flex container’s inner main size. For frozen items,
            //    use their outer target main size; for other items, use their outer flex base size.

            // 3. 计算已用空间，将这一行所有的flex项目外部主轴尺寸相加。
            let line_of_used_space: f32 = line
                .items
                .iter()
                .map(|child: &FlexItem| {
                    child.margin.main(parent_node_dir)
                        + if child.frozen { child.target_size.main(parent_node_dir) } else { child.flex_basis }
                })
                .sum();

            // 计算初始可用空间。将这一行所有的flex项目外部主轴尺寸相加，并从父节点flex容器的内部主轴大小中减去这个值。
            let line_of_initial_free_space =
                (parent_node_inner_size.main(parent_node_dir) - line_of_used_space).or_else(0.0);

            // 4. Loop

            loop {
                // a. Check for flexible items. If all the flex items on the line are frozen,
                //    free space has been distributed; exit this loop.

                // 检查当前flex项目行中的所有元素是否都被冻结，如果都被冻结，代表剩余可用空间都已被分发完。
                // 那么就退出循环
                let all_frozen = line.items.iter().all(|child: &FlexItem| child.frozen);
                if all_frozen {
                    break;
                }

                // b. Calculate the remaining free space as for initial free space, above.
                //    If the sum of the unfrozen flex items’ flex factors is less than one,
                //    multiply the initial free space by this sum. If the magnitude of this
                //    value is less than the magnitude of the remaining free space, use this
                //    as the remaining free space.
                // 使用上面计算初始可用空间去计算的剩余可用空间。
                // 如果未冻结的flex项目的的flex因子之和小于1，将初始剩余空间乘以这个和。如果这个的值小于剩余可用空间的大小，使用此值作为剩余可用空间。

                // 已使用的空间
                let used_space: f32 = line
                    .items
                    .iter()
                    .map(|child: &FlexItem| {
                        child.margin.main(parent_node_dir)
                            + if child.frozen { child.target_size.main(parent_node_dir) } else { child.flex_basis }
                    })
                    .sum();

                // 收集所有未冻结的flex项目
                let mut unfrozen: sys::Vec<&mut FlexItem> =
                    line.items.iter_mut().filter(|child| !child.frozen).collect();

                // 计算所有的增长因子之和和所有收缩因子之和
                let (sum_flex_grow, sum_flex_shrink): (f32, f32) =
                    unfrozen.iter().fold((0.0, 0.0), |(flex_grow, flex_shrink), item| {
                        let style: &Style = &self.nodes[item.node].style;
                        (flex_grow + style.flex_grow, flex_shrink + style.flex_shrink)
                    });

                // 剩余空间
                let free_space: f32 =
                    // 使用增长因子，如果当前行的元素增长只和小于1.0
                    //
                    if line_of_growing && sum_flex_grow < 1.0 {
                        (line_of_initial_free_space * sum_flex_grow).maybe_min(parent_node_inner_size.main(parent_node_dir) - used_space)
                    }
                    // 使用收缩银子，如果当前行的元素收缩只和小于1.0
                    else if line_of_shrinking && sum_flex_shrink < 1.0 {
                        (line_of_initial_free_space * sum_flex_shrink).maybe_max(parent_node_inner_size.main(parent_node_dir) - used_space)
                    }
                    // 使用父节点的内部主轴尺寸减去当前行已使用空间，作为剩余空间
                    else {
                        (parent_node_inner_size.main(parent_node_dir) - used_space).or_else(0.0)
                    };

                // c. Distribute free space proportional to the flex factors.
                //    - If the remaining free space is zero
                //        Do Nothing
                //    - If using the flex grow factor
                //        Find the ratio of the item’s flex grow factor to the sum of the
                //        flex grow factors of all unfrozen items on the line. Set the item’s
                //        target main size to its flex base size plus a fraction of the remaining
                //        free space proportional to the ratio.
                //    - If using the flex shrink factor
                //        For every unfrozen item on the line, multiply its flex shrink factor by
                //        its inner flex base size, and note this as its scaled flex shrink factor.
                //        Find the ratio of the item’s scaled flex shrink factor to the sum of the
                //        scaled flex shrink factors of all unfrozen items on the line. Set the item’s
                //        target main size to its flex base size minus a fraction of the absolute value
                //        of the remaining free space proportional to the ratio. Note this may result
                //        in a negative inner main size; it will be corrected in the next step.
                //    - Otherwise
                //        Do Nothing

                // 根据flex因子分发剩余空间。
                // 1. 如果剩余空间是0，什么也不做。
                // 2. 如果使用flex增长因子
                //      计算flex项目的增长比例系数，并结合剩余空间计算出flex项目的增长值，加上flex项目的基准值，作为flex项目的主轴尺寸。
                //      flex项目增长的长度与flex增长数值成正比关系。
                // 3. 如果使用flex收缩银子
                //      计算flex项目的收缩比例系数，并结合剩余空间计算出flex项目的收缩值，加上flex项目的基准值，作为flex项目的主轴尺寸。
                //      flex项目收缩的长度与flex收缩数值成正比关系。

                if line_of_growing {
                    for target in &mut unfrozen {
                        let child: &mut FlexItem = target;
                        if free_space.is_normal() && sum_flex_grow > 0.0 {
                            let grow_after = child.flex_basis
                                + free_space * (self.nodes[child.node].style.flex_grow / sum_flex_grow);
                            child.target_size.set_main(parent_node_dir, grow_after);
                        }
                    }
                } else if line_of_shrinking && sum_flex_shrink > 0.0 {
                    let sum_scaled_shrink_factor: f32 = unfrozen
                        .iter()
                        .map(|child: &&mut FlexItem| {
                            let child_style: Style = self.nodes[child.node].style;
                            child.inner_flex_basis * child_style.flex_shrink
                        })
                        .sum();

                    for target in &mut unfrozen {
                        let child: &mut FlexItem = target;
                        let scaled_shrink_factor = child.inner_flex_basis * self.nodes[child.node].style.flex_shrink;

                        if free_space.is_normal() && sum_scaled_shrink_factor > 0.0 {
                            let shrink_after =
                                child.flex_basis + free_space * (scaled_shrink_factor / sum_scaled_shrink_factor);
                            child.target_size.set_main(parent_node_dir, shrink_after);
                        } else {
                            let child_style: &Style = &self.nodes[child.node].style;
                            if parent_node_is_column && child_style.aspect_ratio.is_defined() {
                                // fix: aspect_ratio_nest_flex_grow_flex_direction_column
                                // 这种情况下，在纵轴撑满剩余空间，实际上要从宽度的角度计算高度
                                if parent_node_inner_size.cross(parent_node_dir).is_defined()
                                    && !child_style.size.width.is_defined()
                                    && child_style.flex_grow > 0.0
                                {
                                    // fix: aspect_ratio_nest_flex_grow_flex_direction_column_multi_with_margin
                                    // 处理margin情况
                                    let margin: Rect<f32> =
                                        child_style.margin.map(|n| n.resolve(parent_node_size.width).or_else(0.0));

                                    // fix: aspect_ratio_nest_flex_grow_flex_direction_column_multi
                                    // 将整个宽度作为剩余空间计算
                                    let free_space = parent_node_inner_size.cross(parent_node_dir).or_else(0.0)
                                        - margin.horizontal();
                                    let height = free_space / child_style.aspect_ratio.or_else(0.0);
                                    child.target_size.set_main(parent_node_dir, height);
                                }
                            }
                        }
                    }
                }

                // d. Fix min/max violations. Clamp each non-frozen item’s target main size by its
                //    used min and max main sizes and floor its content-box size at zero. If the
                //    item’s target main size was made smaller by this, it’s a max violation.
                //    If the item’s target main size was made larger by this, it’s a min violation.

                let total_violation = unfrozen.iter_mut().fold(0.0, |acc: f32, child: &mut &mut FlexItem| -> f32 {
                    // TODO - not really spec abiding but needs to be done somewhere. probably somewhere else though.
                    // The following logic was developed not from the spec but by trail and error looking into how
                    // webkit handled various scenarios. Can probably be solved better by passing in
                    // min-content max-content constraints from the top. Need to figure out correct thing to do here as
                    // just piling on more conditionals.
                    let child_node = child.node;
                    let child_style: Style = self.nodes[child_node].style;

                    let min_main = if parent_node_is_row && self.nodes[child_node].measure.is_none() {
                        let child_size = self
                            .compute_internal(child_node, Size::undefined(), parent_node_available_space, false)
                            .size;
                        child_size.width.maybe_min(child.size.width).maybe_max(child.min_size.width).into()
                    } else {
                        child.min_size.main(parent_node_dir)
                    };

                    let max_main = child.max_size.main(parent_node_dir);

                    let clamped = child.target_size.main(parent_node_dir)
                            .maybe_min(max_main)
                            .maybe_max(min_main).max(0.0);
                            
                    child.violation = clamped - child.target_size.main(parent_node_dir);

                    if (child_style.flex_direction == FlexDirection::Column
                        || child_style.flex_direction == FlexDirection::ColumnReverse)
                        && !child_style.flex_basis.is_defined()
                        && !child_style.aspect_ratio.is_defined()
                        && child_style.flex_shrink == 1.0
                        && child_style.flex_grow == 1.0
                    {
                        // 在Column和Row相互嵌套且有复杂的自适应和压缩状态时
                        // 需要特殊处理一些情况
                        
                    } else {
                        child.target_size.set_main(parent_node_dir, clamped);
                        let outer_main = child.target_size.main(parent_node_dir) + child.margin.main(parent_node_dir);
                        child.outer_target_size.set_main(parent_node_dir, outer_main);
                    }

                    acc + child.violation
                });

                // e. Freeze over-flexed items. The total violation is the sum of the adjustments
                //    from the previous step ∑(clamped size - unclamped size). If the total violation is:
                //    - Zero
                //        Freeze all items.
                //    - Positive
                //        Freeze all the items with min violations.
                //    - Negative
                //        Freeze all the items with max violations.

                for target in &mut unfrozen {
                    let child: &mut &mut FlexItem = target;
                    match total_violation {
                        v if v > 0.0 => child.frozen = child.violation > 0.0,
                        v if v < 0.0 => child.frozen = child.violation < 0.0,
                        _ => child.frozen = true,
                    }
                }

                // f. Return to the start of this loop.
            }
        }
    }

    fn flex_items_hypothetical_main_size(
        &mut self,
        parent_node_direction: FlexDirection,
        parent_node_child_flex_items: &mut sys::Vec<FlexItem>,
        parent_node_available_space: Size<Number>,
    ) {
        for child in &mut parent_node_child_flex_items[..] {
            //
            let flex_item: &mut FlexItem = child;

            // 计算内部flex项目基准值 = flex项目基准值减去内部边距和边框尺寸
            flex_item.inner_flex_basis = flex_item.flex_basis
                - flex_item.padding.main(parent_node_direction)
                - flex_item.border.main(parent_node_direction);

            // TODO - not really spec abiding but needs to be done somewhere. probably somewhere else though.
            // The following logic was developed not from the spec but by trail and error looking into how
            // webkit handled various scenarios. Can probably be solved better by passing in
            // min-content max-content constraints from the top
            let min_main = self
                .compute_internal(flex_item.node, Size::undefined(), parent_node_available_space, false)
                .size
                .main(parent_node_direction)
                .maybe_max(flex_item.min_size.main(parent_node_direction))
                .maybe_min(flex_item.size.main(parent_node_direction))
                .into();

            let child_inner_main_size =
                flex_item.flex_basis.maybe_max(min_main).maybe_min(flex_item.max_size.main(parent_node_direction));
            // flex项目经过最小尺寸和最大尺寸约束过后，就是猜测的内宽高
            flex_item.hypothetical_inner_size.set_main(parent_node_direction, child_inner_main_size);

            let child_outer_main_size = flex_item.hypothetical_inner_size.main(parent_node_direction)
                + flex_item.margin.main(parent_node_direction);
            // flex项目的假定内宽高加上flex项目的外边距，就是猜测的外宽高
            flex_item.hypothetical_outer_size.set_main(parent_node_direction, child_outer_main_size);
        }
    }

    fn flex_items_flex_basis(
        &mut self,
        current_node: NodeId,
        parent_node_direction: FlexDirection,
        parent_node_is_row: bool,
        parent_node_is_column: bool,
        parent_node_inner_size: Size<Number>,
        parent_node_child_flex_items: &mut sys::Vec<FlexItem>,
        parent_node_available_space: Size<Number>,
    ) {
        for flex_item in &mut parent_node_child_flex_items[..] {
            let child: &mut FlexItem = flex_item;

            let child_style: Style = self.nodes[child.node].style;

            let child_size: Size<Number> = child.size;

            // A. If the item has a definite used flex basis, that’s the flex base size.
            // A. 如果flex项目有一个明确的flex基准值，那么就直接使用这个值。

            let flex_basis: Number = child_style.flex_basis.resolve(parent_node_inner_size.main(parent_node_direction));
            if flex_basis.is_defined() {
                child.flex_basis = flex_basis.or_else(0.0);
                continue;
            };

            // B. If the flex item has an intrinsic aspect ratio,
            //    a used flex basis of content, and a definite cross size,
            //    then the flex base size is calculated from its inner
            //    cross size and the flex item’s intrinsic aspect ratio.

            // 不需要此处的逻辑
            // if child_style.aspect_ratio.is_defined() {
            //     if is_row {
            //         if let Defined(main) = child_size.main(dir) {
            //             if child_style.flex_basis == Dimension::Auto {
            //                 child.flex_basis = main;
            //                 continue;
            //             }
            //         }
            //     } else if is_column {
            //         if let Defined(cross) = child_size.cross(dir) {
            //             if child_style.flex_basis == Dimension::Auto {
            //                 child.flex_basis = cross;
            //                 continue;
            //             }
            //         }
            //     }
            // }

            // C. If the used flex basis is content or depends on its available space,
            //    and the flex container is being sized under a min-content or max-content
            //    constraint (e.g. when performing automatic table layout [CSS21]),
            //    size the item under that constraint. The flex base size is the item’s
            //    resulting main size.

            // TODO - Probably need to cover this case in future

            // D. Otherwise, if the used flex basis is content or depends on its
            //    available space, the available main size is infinite, and the flex item’s
            //    inline axis is parallel to the main axis, lay the item out using the rules
            //    for a box in an orthogonal flow [CSS3-WRITING-MODES]. The flex base size
            //    is the item’s max-content main size.

            // TODO - Probably need to cover this case in future

            // E. Otherwise, size the item into the available space using its used flex basis
            //    in place of its main size, treating a value of content as max-content.
            //    If a cross size is needed to determine the main size (e.g. when the
            //    flex item’s main size is in its block axis) and the flex item’s cross size
            //    is auto and not definite, in this calculation use fit-content as the
            //    flex item’s cross size. The flex base size is the item’s resulting main size.
            // E.

            // https://developer.mozilla.org/zh-CN/docs/Web/CSS/CSS_Flexible_Box_Layout/Basic_Concepts_of_Flexbox
            // align-items 属性可以使元素在交叉轴方向对齐。 这个属性的初始值为stretch，这就是为什么flex项目会默认被拉伸到最高元素的高度。
            // 实际上，它们被拉伸来填满flex容器 —— 最高的元素定义了容器的高度。

            // 根据各种情况，去计算孩子的节点尺寸
            let width: Number =
                // 如果孩子的宽度未定义 && 父布局是纵向 && 孩子的样式alignSelf是Stretch
                // 需要按照规则，直接使用剩余空间宽度，当做节点宽度
                if !child_size.width.is_defined()
                    && parent_node_is_column
                    && child_style.align_self(&self.nodes[current_node].style) == AlignSelf::Stretch {
                    parent_node_available_space.width
                }
                // 如果孩子宽度未定义 && 如果孩子的宽度已经定义 && 父布局是横向 && 孩子的样式alignSelf是Stretch && 孩子定义了比例系数
                // 需要使用高度和比例系数计算出宽度
                // fix: aspect_ratio_multi_aspect_1
                // fix: aspect_ratio_multi_aspect
                else if !child_size.width.is_defined()
                    && child_size.height.is_defined()
                    && parent_node_is_row
                    && child_style.align_self(&self.nodes[current_node].style) == AlignSelf::Stretch
                    && child_style.aspect_ratio.is_defined() {
                    parent_node_available_space.height * child_style.aspect_ratio
                }
                // 如果孩子宽度未定义 && 如果孩子的高度未定义 && 父布局是横向 && 孩子没有设置撑满剩余空间 && 孩子的样式alignSelf是Stretch && 孩子定义了比例系数
                // 直接使用剩余空间宽度当做宽度
                // 这里有疑问？
                // fix: aspect_ratio_align_stretch
                else if !child_size.width.is_defined()
                    && !child_size.height.is_defined()
                    && parent_node_is_row
                    && child_style.flex_grow == 0.0
                    && child_style.align_self(&self.nodes[current_node].style) == AlignSelf::Stretch
                    && child_style.aspect_ratio.is_defined() {
                    parent_node_available_space.width
                }
                // 使用孩子的宽度当做节点宽度
                else {
                    child_size.width
                };

            // 根据各种情况，去计算孩子的节点尺寸
            let height: Number =
                // 如果孩子的高度未定义 && 孩子的样式alignSelf是Stretch && 父布局是横向
                // 直接使用剩余空间高度当做高度
                if !child_size.height.is_defined()
                    && child_style.align_self(&self.nodes[current_node].style) == AlignSelf::Stretch
                    && parent_node_is_row
                {
                    parent_node_available_space.height
                }
                // 使用孩子高度当做节点高度
                else {
                    child_size.height
                };

            // 孩子节点的尺寸需要受到样式最小尺寸和最大尺寸的约束
            let child_node_size = Size {
                width: width.maybe_max(child.min_size.width).maybe_min(child.max_size.width),
                height: height.maybe_max(child.min_size.height).maybe_min(child.max_size.height),
            };

            // 准备好孩子节点的尺寸和孩子的可用空间之后，需要再进行一次计算，以便获取更准确的值。
            let child_size =
                self.compute_internal(child.node, child_node_size, parent_node_available_space, false).size;
            let child_flex_basis_result = child_size
                .main(parent_node_direction)
                .maybe_max(child.min_size.main(parent_node_direction))
                .maybe_min(child.max_size.main(parent_node_direction));

            // 使用计算结果当做flex基准值
            child.flex_basis = child_flex_basis_result;

            // 再次说明flex_basis是指定了flex项目在主轴上的初始大小
        }
    }

    fn get_current_node_flex_items(
        &mut self,
        current_node: NodeId,
        current_node_inner_size: Size<Number>,
    ) -> sys::Vec<FlexItem> {
        let mut flex_items: sys::Vec<FlexItem> = self.children[current_node]
            .iter()
            .map(|child| (child, &self.nodes[*child].style))
            .filter(|(_, style)| style.position_type != PositionType::Absolute)
            .filter(|(_, style)| style.display != Display::None)
            .map(|(child, child_style)| {
                let style: &Style = child_style;

                let target_size = style.size.resolve(current_node_inner_size);

                let target_min_size = style.min_size.resolve(current_node_inner_size);

                let target_max_size = style.max_size.resolve(current_node_inner_size);

                let target_position = style.position.map(|p| p.resolve(current_node_inner_size.width));

                let target_margin = style.margin.map(|m| m.resolve(current_node_inner_size.width).or_else(0.0));

                let target_padding = style.padding.map(|p| p.resolve(current_node_inner_size.width).or_else(0.0));

                let target_border = style.border.map(|b| b.resolve(current_node_inner_size.width).or_else(0.0));

                let target_size = Forest::get_aspect_ratio_size(&style, target_size);

                FlexItem {
                    node: *child,
                    size: target_size,
                    min_size: target_min_size,
                    max_size: target_max_size,

                    position: target_position,
                    margin: target_margin,
                    padding: target_padding,
                    border: target_border,

                    flex_basis: 0.0,
                    inner_flex_basis: 0.0,
                    violation: 0.0,
                    frozen: false,

                    hypothetical_inner_size: Size::zero(),
                    hypothetical_outer_size: Size::zero(),
                    target_size: Size::zero(),
                    outer_target_size: Size::zero(),

                    baseline: 0.0,

                    offset_main: 0.0,
                    offset_cross: 0.0,
                }
            })
            .collect();
        flex_items
    }

    fn get_cross_size(
        node_size: Size<Number>,
        parent_size: Size<Number>,
        dir: FlexDirection,
        is_row: bool,
        line_cross_size: f32,
        child: &mut FlexItem,
        parent_style: &Style,
        child_style: &Style,
    ) -> f32 {
        let is_stretch = child_style.align_self(parent_style) == AlignSelf::Stretch;
        let is_cross_margin_start_no_auto = child_style.cross_margin_start(dir) != Dimension::Auto;
        let is_cross_margin_end_no_auto = child_style.cross_margin_end(dir) != Dimension::Auto;
        let is_cross_size_auto = child_style.cross_size(dir) == Dimension::Auto;

        let child_cross_size: f32 =
            if is_stretch && is_cross_margin_start_no_auto && is_cross_margin_end_no_auto && is_cross_size_auto {
                if is_row {
                    // fix: aspect_ratio_basis
                    if child_style.aspect_ratio.is_defined() && child_style.flex_basis.is_defined() {
                        child_style.flex_basis.resolve(parent_size.width).or_else(0.0)
                            * child_style.aspect_ratio.or_else(0.0)
                    }
                    // fix: aspect_ratio_width_as_flex_basis
                    // fix: aspect_ratio_flex_grow_multi
                    else if child_style.aspect_ratio.is_defined() && child_style.flex_grow > 0.0 {
                        let desire_height = child.target_size.width / child_style.aspect_ratio.or_else(0.0);
                        desire_height
                    }
                    //
                    else if child_style.aspect_ratio.is_defined() {
                        let line_cross = (line_cross_size - child.margin.cross(dir))
                            .maybe_max(child.min_size.cross(dir))
                            .maybe_min(child.max_size.cross(dir));
                        line_cross.maybe_min(child.size.cross(dir))
                    }
                    //
                    else {
                        (line_cross_size - child.margin.cross(dir))
                            .maybe_max(child.min_size.cross(dir))
                            .maybe_min(child.max_size.cross(dir))
                    }
                } else {
                    // fix: aspect_ratio_nest_flex_grow_flex_direction_column
                    if child_style.aspect_ratio.is_defined() && child_style.flex_grow > 0.0 {
                        let desire_width = child.target_size.height * child_style.aspect_ratio.or_else(0.0);
                        desire_width
                    }
                    //
                    else if child_style.aspect_ratio.is_defined() {
                        let line_cross = (line_cross_size - child.margin.cross(dir))
                            .maybe_max(child.min_size.cross(dir))
                            .maybe_min(child.max_size.cross(dir));
                        line_cross.maybe_min(child.size.cross(dir))
                    }
                    //
                    else {
                        (line_cross_size - child.margin.cross(dir))
                            .maybe_max(child.min_size.cross(dir))
                            .maybe_min(child.max_size.cross(dir))
                    }
                }
            } else {
                if is_row
                    && child_style.aspect_ratio.is_defined()
                    && node_size.height.is_defined()
                    && child_style.flex_shrink > 0.0
                {
                    let final_cross = child.hypothetical_inner_size.cross(dir).maybe_min(node_size.height);

                    // fix: aspect_ratio_height_as_flex_basis
                    // fix: aspect_ratio_flex_shrink
                    if !child_style.size.width.is_defined()
                        && !child_style.min_size.width.is_defined()
                        && !child_style.max_size.width.is_defined()
                    {
                        let desire_height = child.target_size.width / child_style.aspect_ratio.or_else(0.0);
                        desire_height
                    }
                    // fix: aspect_ratio_flex_shrink_2
                    else if !child_style.size.width.is_defined()
                        && child_style.min_size.width.is_defined()
                        && !child_style.max_size.width.is_defined()
                    {
                        let desire_height = child.target_size.width / child_style.aspect_ratio.or_else(0.0);
                        final_cross.maybe_min(Number::Defined(desire_height))
                    }
                    // fix: aspect_ratio_width_height_flex_grow_row
                    else if child_style.size.width.is_defined() {
                        let desire_height = child.target_size.width / child_style.aspect_ratio.or_else(0.0);
                        desire_height
                    } else {
                        final_cross
                    }
                } else {
                    let final_cross = child.hypothetical_inner_size.cross(dir);
                    final_cross
                }
            };
        child_cross_size
    }

    // fix: aspect_ratio_both_dimensions_defined_column
    fn get_aspect_ratio_size(child_style: &Style, target_size: Size<Number>) -> Size<Number> {
        return Size {
            width: Forest::get_aspect_ratio_width(child_style, target_size),
            height: Forest::get_aspect_ratio_height(child_style, target_size),
        };
    }

    // fix: aspect_ratio_both_dimensions_defined_column
    fn get_aspect_ratio_height(child_style: &Style, target_size: Size<Number>) -> Number {
        // 若有定义宽度，且存在比例关系，那么使用高度计算宽度
        if target_size.width.is_defined() && child_style.aspect_ratio.is_defined() {
            let width = target_size.width.or_else(0.0);
            let aspect_ratio = child_style.aspect_ratio.or_else(0.0);
            return Number::Defined(width / aspect_ratio);
        }

        return target_size.height;
    }

    fn get_aspect_ratio_width(child_style: &Style, target_size: Size<Number>) -> Number {
        // 若没有定义宽度，并且有定义高度，且存在比例关系，那么使用高度计算宽度
        if !target_size.width.is_defined() && target_size.height.is_defined() && child_style.aspect_ratio.is_defined() {
            let height = target_size.height.or_else(0.0);
            let aspect_ratio = child_style.aspect_ratio.or_else(0.0);
            return Number::Defined(height * aspect_ratio);
        }

        return target_size.width;
    }
}
