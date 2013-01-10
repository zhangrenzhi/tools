package com.bowlong.astar;

import java.awt.Point;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * Title: LoonFramework
 * </p>
 * <p>
 * Description:Java的A*寻径实现
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
public class AStarTest {

    // 起始坐标1,1
    private static Point START_POS = new Point(10, 1);

    // 目的坐标10,13
    private static Point OBJECT_POS = new Point(7, 13);


	// 地图描述
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

	// 无法移动区域
	final static public int[] HIT = { 1 };

    public static void main(String[] args) throws IOException {
        // 注入地图描述及障碍物描述
        AStarFinder astar = new AStarFinder(MAP, HIT);
        // searchPath将获得两点间移动路径坐标的List集合
        // 在实际应用中，利用Thread分步处理List中坐标即可实现自动行走
        List<AStarNode> path = astar.searchPath(START_POS, OBJECT_POS);
        System.out.println(path.size());
        System.out.println(path);
    }
}