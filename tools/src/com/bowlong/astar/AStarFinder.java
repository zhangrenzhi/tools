package com.bowlong.astar;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:A*寻径处理用类(此类为演示用，并不意味着算法是最佳实现)
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: LoonFramework
 * </p>
 * <p>
 * License: http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * 
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class AStarFinder {
    // 路径优先等级list(此示例中为内部方法)
    private LevelList _levelList;

    // 已完成路径的list
    private LinkedList<AStarNode> _closedList;

    // 地图描述
    private int[][] _map;

    // 行走区域限制
    private int[] _limit;

    /**
     * 以注入地图的2维数组及限制点描述初始化此类
     * 
     * @param _map
     */
    public AStarFinder(int[][] map, int[] limit) {
        _map = map;
        _limit = limit;
        _levelList = new LevelList();
        _closedList = new LinkedList<AStarNode>();
    }

    /**
     * A*方式寻径,注入开始坐标及目标坐标后运算,返回可行路径的List
     * 
     * @param startPos
     * @param objectPos
     * @return
     */
    public List<AStarNode> searchPath(Point startPos, Point objectPos) {
        // 初始化起始节点与目标节点
        AStarNode startNode = new AStarNode(startPos);
        AStarNode objectNode = new AStarNode(objectPos);
        // 设定起始节点参数
        startNode._costFromStart = 0;
        startNode._costToObject = startNode.getCost(objectNode);
        startNode._parentNode = null;
        // 加入运算等级序列
        _levelList.add(startNode);
        // 当运算等级序列中存在数据时，循环处理寻径，直到levelList为空
        while (!_levelList.isEmpty()) {
            // 取出并删除最初的元素
            AStarNode firstNode = (AStarNode) _levelList.removeFirst();
            // 判定是否和目标node坐标相等
            if (firstNode.equals(objectNode)) {
                // 是的话即可构建出整个行走路线图，运算完毕
                return makePath(firstNode);
            } else {
                // 否则
                // 加入已验证List
                _closedList.add(firstNode);
                // 获得firstNode的移动区域
                LinkedList<AStarNode> _limit = firstNode.getLimit();
                // 遍历
                for (int i = 0; i < _limit.size(); i++) {
                    //获得相邻节点
                    AStarNode neighborNode = (AStarNode) _limit.get(i);
                    //获得是否满足等级条件
                    boolean isOpen = _levelList.contains(neighborNode);
                    //获得是否已行走
                    boolean isClosed = _closedList.contains(neighborNode);
                    //判断是否无法通行
                    boolean isHit = isHit(neighborNode._pos.x,
                            neighborNode._pos.y);
                    //当三则判定皆非时
                    if (!isOpen && !isClosed && !isHit) {
                        //设定costFromStart
                        neighborNode._costFromStart = firstNode._costFromStart + 1;
                        //设定costToObject  
                        neighborNode._costToObject = neighborNode
                                .getCost(objectNode);
                        //改变neighborNode父节点
                        neighborNode._parentNode = firstNode;
                        //加入level 
                        _levelList.add(neighborNode);
                    }
                }
            }

        }
        //清空数据
        _levelList.clear();
        _closedList.clear();
        //当while无法运行时，将返回null
        return  null;
    }
    
    /**
     * 判定是否为可通行区域
     * 
     * @param x
     * @param y
     * @return
     */
    private boolean isHit(int x, int y) {
        for (int i = 0; i < _limit.length; i++) {
            if (_map[y][x] == _limit[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过Node制造行走路径
     * 
     * @param node
     * @return
     */
    private LinkedList<AStarNode> makePath(AStarNode node) {
        LinkedList<AStarNode> path = new LinkedList<AStarNode>();
        // 当上级节点存在时
        while (node._parentNode != null) {
            // 在第一个元素处添加
            path.addFirst(node);
            // 将node赋值为parent node
            node = node._parentNode;
        }
        // 在第一个元素处添加
        path.addFirst(node);
        return path;
    }

    private class LevelList extends LinkedList<Object> {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * 增加一个node
         * 
         * @param node
         */
        public void add(AStarNode node) {
            for (int i = 0; i < size(); i++) {
                if (node.compareTo(get(i)) <= 0) {
                    add(i, node);
                    return;
                }
            }
            addLast(node);
        }
    }
}