package com.bowlong.astar;

import java.awt.Point;
import java.util.LinkedList;

import com.bowlong.lang.PStr;

/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:����·���ڵ�����
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
 * @email��ceponline@yahoo.com.cn
 * @version 0.1
 */
@SuppressWarnings({ "rawtypes", "unused" })
public class AStarNode implements Comparable {

	// ����
	public Point _pos;

	// ��ʼ�ص���ֵ
	public int _costFromStart;

	// Ŀ��ص���ֵ
	public int _costToObject;

	// ���ڵ�
	public AStarNode _parentNode;

	private AStarNode() {
	}

	/**
	 * ��ע������㷽ʽ��ʼ��Node
	 * 
	 * @param _pos
	 */
	public AStarNode(Point _pos) {
		this._pos = _pos;
	}

	/**
	 * ����·���ɱ�
	 * 
	 * @param node
	 * @return
	 */
	public int getCost(AStarNode node) {
		// ����������ֵ ��ʽ��(x1, y1)-(x2, y2)
		int m = node._pos.x - _pos.x;
		int n = node._pos.y - _pos.y;
		// ȡ���ڵ��ŷ����¾��루ֱ�߾��룩��Ϊ����ֵ�����Ի�óɱ�
		return (int) Math.sqrt(m * m + n * n);
	}

	/**
	 * ���node�����Ƿ����֤����һ��
	 */
	public boolean equals(Object node) {
		// ��֤����Ϊ�ж�����
		if (_pos.x == ((AStarNode) node)._pos.x && _pos.y == ((AStarNode) node)._pos.y) {
			return true;
		}
		return false;
	}

	/**
	 * �Ƚ������Ի����С�ɱ�����
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
	 * ����������Ҹ�����ƶ���������
	 * 
	 * @return
	 */
	public LinkedList<AStarNode> getLimit() {
		LinkedList<AStarNode> limit = new LinkedList<AStarNode>();
		int x = _pos.x;
		int y = _pos.y;
		// �������Ҹ�����ƶ�����(����б�ӵ�ͼ�����Կ���ע�͵�ƫ�Ʋ��֣���ʱ������8����λ)
		// ��
		limit.add(new AStarNode(new Point(x, y - 1)));
		// ����
		// limit.add(new Node(new Point(x+1, y-1)));
		// ��
		limit.add(new AStarNode(new Point(x + 1, y)));
		// ����
		// limit.add(new Node(new Point(x+1, y+1)));
		// ��
		limit.add(new AStarNode(new Point(x, y + 1)));
		// ����
		// limit.add(new Node(new Point(x-1, y+1)));
		// ��
		limit.add(new AStarNode(new Point(x - 1, y)));
		// ����
		// limit.add(new Node(new Point(x-1, y-1)));

		return limit;
	}

	@Override
	public String toString() {
		return PStr.begin("[").a(_pos.x).a(",").a(_pos.y).end("]");
	}

}