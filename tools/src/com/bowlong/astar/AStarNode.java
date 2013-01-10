package com.bowlong.astar;

import java.awt.Point;
import java.util.LinkedList;

import com.bowlong.lang.PStr;

/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:描述路径节点用类
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
@SuppressWarnings({ "rawtypes", "unused" })
public class AStarNode implements Comparable {

	// 坐标
	public Point _pos;

	// 开始地点数值
	public int _costFromStart;

	// 目标地点数值
	public int _costToObject;

	// 父节点
	public AStarNode _parentNode;

	private AStarNode() {
	}

	/**
	 * 以注入坐标点方式初始化Node
	 * 
	 * @param _pos
	 */
	public AStarNode(Point _pos) {
		this._pos = _pos;
	}

	/**
	 * 返回路径成本
	 * 
	 * @param node
	 * @return
	 */
	public int getCost(AStarNode node) {
		// 获得坐标点间差值 公式：(x1, y1)-(x2, y2)
		int m = node._pos.x - _pos.x;
		int n = node._pos.y - _pos.y;
		// 取两节点间欧几理德距离（直线距离）做为估价值，用以获得成本
		return (int) Math.sqrt(m * m + n * n);
	}

	/**
	 * 检查node对象是否和验证对象一致
	 */
	public boolean equals(Object node) {
		// 验证坐标为判断依据
		if (_pos.x == ((AStarNode) node)._pos.x && _pos.y == ((AStarNode) node)._pos.y) {
			return true;
		}
		return false;
	}

	/**
	 * 比较两点以获得最小成本对象
	 */
	public int compareTo(Object node) {
		int a1 = _costFromStart + _costToObject;
		int a2 = ((AStarNode) node)._costFromStart + ((AStarNode) node)._costToObject;
		if (a1 < a2) {
			return -1;
		} else if (a1 == a2) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 获得上下左右各点间移动限制区域
	 * 
	 * @return
	 */
	public LinkedList<AStarNode> getLimit() {
		LinkedList<AStarNode> limit = new LinkedList<AStarNode>();
		int x = _pos.x;
		int y = _pos.y;
		// 上下左右各点间移动区域(对于斜视地图，可以开启注释的偏移部分，此时将评估8个方位)
		// 上
		limit.add(new AStarNode(new Point(x, y - 1)));
		// 右上
		// limit.add(new Node(new Point(x+1, y-1)));
		// 右
		limit.add(new AStarNode(new Point(x + 1, y)));
		// 右下
		// limit.add(new Node(new Point(x+1, y+1)));
		// 下
		limit.add(new AStarNode(new Point(x, y + 1)));
		// 左下
		// limit.add(new Node(new Point(x-1, y+1)));
		// 左
		limit.add(new AStarNode(new Point(x - 1, y)));
		// 左上
		// limit.add(new Node(new Point(x-1, y-1)));

		return limit;
	}

	@Override
	public String toString() {
		return PStr.begin("[").a(_pos.x).a(",").a(_pos.y).end("]");
	}

}