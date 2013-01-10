package com.bowlong.astar;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:A*Ѱ����������(����Ϊ��ʾ�ã�������ζ���㷨�����ʵ��)
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
public class AStarFinder {
    // ·�����ȵȼ�list(��ʾ����Ϊ�ڲ�����)
    private LevelList _levelList;

    // �����·����list
    private LinkedList<AStarNode> _closedList;

    // ��ͼ����
    private int[][] _map;

    // ������������
    private int[] _limit;

    /**
     * ��ע���ͼ��2ά���鼰���Ƶ�������ʼ������
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
     * A*��ʽѰ��,ע�뿪ʼ���꼰Ŀ�����������,���ؿ���·����List
     * 
     * @param startPos
     * @param objectPos
     * @return
     */
    public List<AStarNode> searchPath(Point startPos, Point objectPos) {
        // ��ʼ����ʼ�ڵ���Ŀ��ڵ�
        AStarNode startNode = new AStarNode(startPos);
        AStarNode objectNode = new AStarNode(objectPos);
        // �趨��ʼ�ڵ����
        startNode._costFromStart = 0;
        startNode._costToObject = startNode.getCost(objectNode);
        startNode._parentNode = null;
        // ��������ȼ�����
        _levelList.add(startNode);
        // ������ȼ������д�������ʱ��ѭ������Ѱ����ֱ��levelListΪ��
        while (!_levelList.isEmpty()) {
            // ȡ����ɾ�������Ԫ��
            AStarNode firstNode = (AStarNode) _levelList.removeFirst();
            // �ж��Ƿ��Ŀ��node�������
            if (firstNode.equals(objectNode)) {
                // �ǵĻ����ɹ�������������·��ͼ���������
                return makePath(firstNode);
            } else {
                // ����
                // ��������֤List
                _closedList.add(firstNode);
                // ���firstNode���ƶ�����
                LinkedList<AStarNode> _limit = firstNode.getLimit();
                // ����
                for (int i = 0; i < _limit.size(); i++) {
                    //������ڽڵ�
                    AStarNode neighborNode = (AStarNode) _limit.get(i);
                    //����Ƿ�����ȼ�����
                    boolean isOpen = _levelList.contains(neighborNode);
                    //����Ƿ�������
                    boolean isClosed = _closedList.contains(neighborNode);
                    //�ж��Ƿ��޷�ͨ��
                    boolean isHit = isHit(neighborNode._pos.x,
                            neighborNode._pos.y);
                    //�������ж��Է�ʱ
                    if (!isOpen && !isClosed && !isHit) {
                        //�趨costFromStart
                        neighborNode._costFromStart = firstNode._costFromStart + 1;
                        //�趨costToObject  
                        neighborNode._costToObject = neighborNode
                                .getCost(objectNode);
                        //�ı�neighborNode���ڵ�
                        neighborNode._parentNode = firstNode;
                        //����level 
                        _levelList.add(neighborNode);
                    }
                }
            }

        }
        //�������
        _levelList.clear();
        _closedList.clear();
        //��while�޷�����ʱ��������null
        return  null;
    }
    
    /**
     * �ж��Ƿ�Ϊ��ͨ������
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
     * ͨ��Node��������·��
     * 
     * @param node
     * @return
     */
    private LinkedList<AStarNode> makePath(AStarNode node) {
        LinkedList<AStarNode> path = new LinkedList<AStarNode>();
        // ���ϼ��ڵ����ʱ
        while (node._parentNode != null) {
            // �ڵ�һ��Ԫ�ش����
            path.addFirst(node);
            // ��node��ֵΪparent node
            node = node._parentNode;
        }
        // �ڵ�һ��Ԫ�ش����
        path.addFirst(node);
        return path;
    }

    private class LevelList extends LinkedList<Object> {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * ����һ��node
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