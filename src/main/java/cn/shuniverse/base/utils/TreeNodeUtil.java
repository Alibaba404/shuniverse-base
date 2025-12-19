package cn.shuniverse.base.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by 蛮小满Sama at 2025-12-12 15:38
 *
 * @author 蛮小满Sama
 * @description 树节点工具类
 */
public class TreeNodeUtil {
    /**
     * 构建无限级树形结构（通用版）
     *
     * @param nodeList          扁平的节点列表
     * @param idExtractor       节点ID提取器（String类型）
     * @param parentIdExtractor 父ID提取器（String类型）
     * @param childrenGetter    子节点获取器
     * @param childrenSetter    子节点设置器
     * @param rootParentId      根节点的父ID值（通常为""、null、"0"、"-1"）
     * @param <T>               节点类型
     * @return 树形结构的根节点列表
     */
    public static <T> List<T> buildTree(
            List<T> nodeList,
            Function<T, String> idExtractor,
            Function<T, String> parentIdExtractor,
            Function<T, List<T>> childrenGetter,
            BiConsumer<T, List<T>> childrenSetter,
            String rootParentId) {

        // 空列表直接返回
        if (CollectionUtils.isEmpty(nodeList)) {
            return new ArrayList<>();
        }
        // 1. 构建ID到节点的映射（处理null键）
        Map<String, T> idNodeMap = new HashMap<>();
        for (T node : nodeList) {
            String id = idExtractor.apply(node);
            if (id != null) {
                idNodeMap.put(id, node);
            }
        }

        // 2. 存储根节点
        List<T> rootNodes = new ArrayList<>();
        // 3. 遍历所有节点，构建父子关系
        for (T node : nodeList) {
            String parentId = parentIdExtractor.apply(node);
            // 判断是否为根节点（兼容null和空字符串）
            boolean isRoot;
            if (rootParentId == null) {
                isRoot = (parentId == null || parentId.isEmpty());
            } else {
                isRoot = rootParentId.equals(parentId);
            }

            if (isRoot) {
                rootNodes.add(node);
            } else {
                // 查找父节点（兼容null）
                T parentNode = (parentId != null) ? idNodeMap.get(parentId) : null;
                if (parentNode != null) {
                    // 获取父节点的子节点列表
                    List<T> children = childrenGetter.apply(parentNode);
                    // 初始化子节点列表（避免空指针）
                    if (children == null) {
                        children = new ArrayList<>();
                    }
                    // 添加当前节点到父节点的子节点列表
                    children.add(node);
                    // 设置子节点列表到父节点
                    childrenSetter.accept(parentNode, children);
                } else {
                    // 父节点不存在，作为根节点处理（容错）
                    rootNodes.add(node);
                }
            }
        }

        return rootNodes;
    }

    /**
     * 重载方法1：默认根节点父ID为null
     */
    public static <T> List<T> buildTree(
            List<T> nodeList,
            Function<T, String> idExtractor,
            Function<T, String> parentIdExtractor,
            Function<T, List<T>> childrenGetter,
            BiConsumer<T, List<T>> childrenSetter) {
        return buildTree(nodeList, idExtractor, parentIdExtractor, childrenGetter, childrenSetter, null);
    }

    /**
     * 重载方法2：自定义根节点父ID，简化参数
     */
    public static <T> List<T> buildTree(
            List<T> nodeList,
            Function<T, String> idExtractor,
            Function<T, String> parentIdExtractor,
            BiConsumer<T, List<T>> childrenSetter,
            String rootParentId) {
        // 默认的子节点获取器（通过反射）
        return buildTree(
                nodeList,
                idExtractor,
                parentIdExtractor,
                TreeNodeUtil::getChildrenByReflection,
                childrenSetter,
                rootParentId
        );
    }

    /**
     * 重载方法3：最简调用（默认根节点父ID为null）
     */
    public static <T> List<T> buildTree(
            List<T> nodeList,
            Function<T, String> idExtractor,
            Function<T, String> parentIdExtractor,
            BiConsumer<T, List<T>> childrenSetter) {
        return buildTree(nodeList, idExtractor, parentIdExtractor, childrenSetter, null);
    }

    /**
     * 带排序的树形构建方法
     */
    public static <T> List<T> buildTreeWithSort(
            List<T> nodeList,
            Function<T, String> idExtractor,
            Function<T, String> parentIdExtractor,
            Function<T, List<T>> childrenGetter,
            BiConsumer<T, List<T>> childrenSetter,
            String rootParentId,
            Comparator<T> comparator) {

        List<T> tree = buildTree(nodeList, idExtractor, parentIdExtractor, childrenGetter, childrenSetter, rootParentId);
        // 对所有层级的子节点进行排序
        sortTree(tree, childrenGetter, comparator);
        return tree;
    }

    /**
     * 递归排序树形结构的所有子节点
     */
    private static <T> void sortTree(
            List<T> nodeList,
            Function<T, List<T>> childrenGetter,
            Comparator<T> comparator) {

        if (CollectionUtils.isEmpty(nodeList)) {
            return;
        }

        // 排序当前层级节点
        nodeList.sort(comparator);

        // 递归排序子节点
        for (T node : nodeList) {
            List<T> children = childrenGetter.apply(node);
            sortTree(children, childrenGetter, comparator);
        }
    }

    /**
     * 通过反射获取子节点列表（兼容方案）
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> getChildrenByReflection(T node) {
        if (node == null) {
            return new ArrayList<>();
        }

        try {
            // 查找List类型的字段（优先children）
            for (java.lang.reflect.Field field : node.getClass().getDeclaredFields()) {
                if (List.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    List<T> children = (List<T>) field.get(node);
                    return children != null ? children : new ArrayList<>();
                }
            }
        } catch (Exception e) {
            // 反射失败返回空列表
        }
        return new ArrayList<>();
    }
}
