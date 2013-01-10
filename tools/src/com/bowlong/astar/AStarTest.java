package com.bowlong.astar;

import java.awt.Point;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:Java��A*Ѱ��ʵ��
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
public class AStarTest {

    // ��ʼ����1,1
    private static Point START_POS = new Point(10, 1);

    // Ŀ������10,13
    private static Point OBJECT_POS = new Point(7, 13);


	// ��ͼ����
	final static public int[][] MAP = {
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };

	// �޷��ƶ�����
	final static public int[] HIT = { 1 };

    public static void main(String[] args) throws IOException {
        // ע���ͼ�������ϰ�������
        AStarFinder astar = new AStarFinder(MAP, HIT);
        // searchPath�����������ƶ�·�������List����
        // ��ʵ��Ӧ���У�����Thread�ֲ�����List�����꼴��ʵ���Զ�����
        List<AStarNode> path = astar.searchPath(START_POS, OBJECT_POS);
        System.out.println(path.size());
        System.out.println(path);
    }
}