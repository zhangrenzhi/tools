package com.bowlong.sql.builder.jdbc.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bowlong.Abstract;
import com.bowlong.lang.StrEx;
import com.bowlong.pinyin.PinYin;
import com.bowlong.sql.SqlEx;
import com.bowlong.util.MapEx;

public class InternalBuilder extends Abstract {
	public static void main(String[] args) throws Exception {
		String sql = "SELECT * FROM `用户的道具` LIMIT 1";
		String host = "127.0.0.1";
		String db = "test";
		String bpackage = "fych.db";
		String appContext = "fych.context.AppContext";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		boolean batch = true;
		boolean sorted = true;
		
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		String xml = build(conn, rs, bpackage, appContext, batch, sorted);
		System.out.println(xml);

	}

	public static String build(Connection conn, ResultSet rs, String pkg, String appContext, boolean batch, boolean sorted) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		String catalogName = (String) columns.get(0).get("catalogName");
		String table = (String) columns.get(0).get("tableName");
		String tableEn = PinYin.getShortPinYin(table);
		String tableUEn = StrEx.upperFirst(tableEn);
		Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn, table);
		String primaryKey = BeanBuilder.primaryKey(rsmd, columns);
		String primaryKeyType = JavaType.getType(rsmd, primaryKey);
		String pkBasicType = JavaType.getBasicType(primaryKeyType);
//		String columns1 = BeanBuilder.columns1(rsmd, columns);
//		String columns2 = BeanBuilder.columns2(rsmd, columns);
//		String columns3 = BeanBuilder.columns3(rsmd, columns);
//		String columns4 = BeanBuilder.columns4(rsmd, columns);
//		String columns5 = BeanBuilder.columns5(rsmd, columns);
//		String columns6 = BeanBuilder.columns6(rsmd, columns);
//		String columns7 = BeanBuilder.columns7(rsmd, columns);
//		String columns8 = BeanBuilder.columns8(rsmd, columns);
//		String columns9 = BeanBuilder.columns9(rsmd, columns);
		
		sn(sb, "package %s.internal;", pkg);
		sn(sb, "");
		sn(sb, "import org.apache.commons.logging.*;");
		sn(sb, "import java.util.*;");
		sn(sb, "import java.util.concurrent.atomic.*;");
		//sn(sb, "import java.util.concurrent.*;");
		sn(sb, "import com.bowlong.sql.*;");
		sn(sb, "import static com.bowlong.sql.CacheModel.*;");
		sn(sb, "import %s.bean.*;", pkg);
		sn(sb, "import %s.dao.*;", pkg);
		sn(sb, "import %s.entity.*;", pkg);
		//sn(sb, "import %s;", appContext);
		
		sn(sb, "");
		sn(sb, "//%s - %s", catalogName, table);
		sn(sb, "@SuppressWarnings({\"rawtypes\", \"unchecked\", \"static-access\"})");
		sn(sb, "public abstract class %sInternal extends InternalSupport{", tableUEn);
		sn(sb, "    static Log log = LogFactory.getLog(%sDAO.class);", tableUEn);
		//sn(sb, "    // true直接操作数据库, false操作内存缓存");
//		sn(sb, "    // NO_CACHE    // 不缓存");
//		sn(sb, "    // FULL_CACHE  // 全缓存");
//		sn(sb, "    // FULL_MEMORY // 全内存");
		sn(sb, "    public static CacheModel cacheModel = NO_CACHE;");
//		sn(sb, "    // 超时时间(超过时间了则从数据库重新加载数据)");
//		sn(sb, "    public static long LASTTIME = 0;");
//		sn(sb, "    public static long TIMEOUT = %sEntity.TIMEOUT();", tableUEn);
		sn(sb, "");
//		sn(sb, "    // 全内存数据(不写入数据库)");
		//sn(sb, "    public static boolean InMEM = false;");
		sn(sb, "    // public static %s LASTID = 0;", pkBasicType);
		sn(sb, "    private static AtomicInteger LASTID = new AtomicInteger();");
		sn(sb, "");
		sn(sb, "    public %sInternal(){}", tableUEn);
		sn(sb, "");
		sn(sb, "    public static %sDAO DAO(){", tableUEn);
		sn(sb, "        return %sEntity.%sDAO();", tableUEn, tableUEn);
		sn(sb, "    }");
		sn(sb, "");
		//sn(sb, "    public static final %sInternal my = new %sInternal();", tableUEn, tableUEn);
		//sn(sb, "");
		//sn(sb, "    public static final Map<%s, ScheduledFuture> futures = newMap();", primaryKeyType);
		sn(sb, "");
		sn(sb, "    private static int MAX = 0;");
		sn(sb, "    public static void setFixedMAX(int num) {");
		sn(sb, "        MAX = num;");
		sn(sb, "        FIXED = new %s[MAX];", tableUEn);
		sn(sb, "    }");
		sn(sb, "    private static %s[] FIXED = new %s[MAX];", tableUEn, tableUEn);
		sn(sb, "    public static final Map<%s, %s> vars = %s();", primaryKeyType, tableUEn, (sorted ? "newSortedMap" : "newMap"));
		{
		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "    public static final Map<%s, %s> varsBy%s = %s();", COLUMN_NAME_TYPE, primaryKeyType, COLUMN_NAME_UEN, (sorted ? "newSortedMap" : "newMap"));
				}else{
					if(COLUMN_NAME_TYPE.equals("java.util.Date"))
						continue;
					sn(sb, "    public static final Map<%s, Set<%s>> varsBy%s = %s();", COLUMN_NAME_TYPE, primaryKeyType, COLUMN_NAME_UEN, (sorted ? "newSortedMap" : "newMap"));
				}
				
			}else{ // 多索引
				Map<String, Object> index = idx.get(0);
				String index1 = BeanBuilder.index1(rsmd, idx);
				// String index2 = SqlBeanBuilder.index2(rsmd, idx);
				// String index3 = SqlBeanBuilder.index3(rsmd, idx);
				// String index4 = SqlBeanBuilder.index4(rsmd, idx);
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "    public static final Map<String, %s> varsBy%s = %s();", primaryKeyType, index1, (sorted ? "newSortedMap" : "newMap"));
				}else{
					sn(sb, "    public static final Map<String, Set<%s>> varsBy%s = %s();", primaryKeyType, index1, (sorted ? "newSortedMap" : "newMap"));
				}
			}
		}
		}
		sn(sb, "");
		sn(sb, "    private static final List<%s> fixedCache = newList();", tableUEn);
//		sn(sb, "    private static NewList<%s> refreshFixedCache() {", tableUEn);
//		sn(sb, "        if ( cacheModel != STATIC_CACHE ) return fixedCache;");
//		sn(sb, "        if ( !fixedCache.isEmpty() ) return fixedCache;");
//		sn(sb, "        int max = FIXED.length;");
//		sn(sb, "        for (int i = 0; i < max; i++) {");
//		sn(sb, "            %s %s = FIXED[i];", tableUEn, tableEn);
//		sn(sb, "            if (%s != null) fixedCache.add(%s);", tableEn, tableEn);
//		sn(sb, "        }");
//		sn(sb, "        return fixedCache;");
//		sn(sb, "    }");
		sn(sb, "");

//		sn(sb, "    public static long difference(int l1, int l2) {");
//		sn(sb, "        return l2 - l1;");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static long now() {");
//		sn(sb, "        return System.currentTimeMillis();");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static Date time() {");
//		sn(sb, "        return new java.util.Date();");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static Map newMap() {");
//		sn(sb, "        return new Hashtable();");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static List newList() {");
//		sn(sb, "        return new Vector();");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static boolean isTimeout() {");
//		sn(sb, "        return isTimeout(TIMEOUT, LASTTIME);");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static boolean isTimeout(long TIMEOUT, long LASTTIME) {");
//		sn(sb, "        if(TIMEOUT <= 0) return false;");
//		sn(sb, "        long l2 = System.currentTimeMillis();");
//		sn(sb, "        long t = l2 - LASTTIME;");
//		sn(sb, "        return (t > TIMEOUT);");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static boolean isTimeout(long TIMEOUT, Date DLASTTIME) {");
//		sn(sb, "        long LASTTIME = DLASTTIME.getTime();");
//		sn(sb, "        return isTimeout(TIMEOUT, LASTTIME);");
//		sn(sb, "    }");
//		sn(sb, "");

		sn(sb, "    public static void put(%s %s){", tableUEn, tableEn);
		sn(sb, "        if(%s == null || %s.%s <= 0) return ;", tableEn, tableEn, primaryKey);
		sn(sb, "");
		//sn(sb, "        %s.ustr();", tableEn);
		sn(sb, "        %s %s = %s.%s;", pkBasicType, primaryKey, tableEn, primaryKey);
		//sn(sb, "        refreshFuture(%s);", primaryKey);
		sn(sb, "        if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            if (%s > 0 && %s <= FIXED.length) {", primaryKey, primaryKey);
		sn(sb, "                if (FIXED[%s - 1] == null || !FIXED[%s - 1].equals(%s))", primaryKey, primaryKey, tableEn);
		sn(sb, "                	FIXED[%s - 1] = %s;", primaryKey, tableEn);
		sn(sb, "                if (!fixedCache.contains(%s))", tableEn, tableEn);
		sn(sb, "                	fixedCache.add(%s);", tableEn);
		sn(sb, "            }");
		sn(sb, "        } else {");
		sn(sb, "            vars.put(%s, %s);", primaryKey, tableEn);
		sn(sb, "        }");
		sn(sb, "");
		{
		Iterator<String> it = indexs.keySet().iterator();
		int p = 0;
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
				
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "        %s %s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
					sn(sb, "        varsBy%s.put(%s, %s);", COLUMN_NAME_UEN, COLUMN_NAME_EN, primaryKey);
				}else{
					if(basicType.equals("java.util.Date"))
						continue;
					p = p + 1;
					sn(sb, "        %s %s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
					sn(sb, "        Set m%d = varsBy%s.get(%s);", p, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        if(m%d == null){", p);
					sn(sb, "            m%d = %s();", p, (sorted ? "newSortedSet" : "newSet"));
					sn(sb, "            varsBy%s.put(%s, m%d);", COLUMN_NAME_UEN, COLUMN_NAME_EN, p);
					sn(sb, "        }", p);
					sn(sb, "        m%d.add(%s);", p, primaryKey);
				}
				sn(sb, "");
				
			}else{ // 多索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String index1 = BeanBuilder.index1(rsmd, idx);
				// String index2 = SqlBeanBuilder.index2(rsmd, idx);
				// String index3 = SqlBeanBuilder.index3(rsmd, idx);
				// String index4 = SqlBeanBuilder.index4(rsmd, idx);
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String skey = "";
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "        { // %s" , INDEX_NAME);
					for (Map<String, Object> map : idx) {
						String COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
						String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);

						sn(sb, "            %s v%s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
						skey = skey + s("v%s", COLUMN_NAME_EN);
						if(idx.indexOf(map) < idx.size() - 1){
							skey = skey + " + \"-\" + ";
						}
					}
					sn(sb, "            String vkey = %s;", skey);
					sn(sb, "            varsBy%s.put(vkey, %s);", index1, primaryKey);
					sn(sb, "        }");
				}else{
					p = p + 1;
					sn(sb, "        { // %s" , INDEX_NAME);
					for (Map<String, Object> map : idx) {
						String COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
						String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);

						sn(sb, "            %s v%s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
						skey = skey + s("v%s", COLUMN_NAME_EN);
						if(idx.indexOf(map) < idx.size() - 1){
							skey = skey + "+ \"-\" +";
						}
					}
					sn(sb, "            String vkey = %s;", skey);
					sn(sb, "            Set m%d = varsBy%s.get(vkey);", p, index1);
					sn(sb, "            if(m%d == null){", p);
					sn(sb, "                m%d = %s();", p, (sorted ? "newSortedSet" : "newSet"));
					sn(sb, "                varsBy%s.put(vkey, m%d);", index1, p);
					sn(sb, "            }", p);
					sn(sb, "            m%d.add(%s);", p, primaryKey);
					//sn(sb, "        varsBy%s.put(_key, %s);", index1, primaryKey);
					sn(sb, "        }");
				}
				sn(sb, "");
			}
		}
		}
		sn(sb, "        // LASTID = %s > LASTID ? %s : LASTID;", primaryKey, primaryKey);
		sn(sb, "        if (%s > LASTID.get()) LASTID.set(%s);", primaryKey, primaryKey);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static void clear(){");
		{
			Iterator<String> it = indexs.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				List<Map<String, Object>> idx = indexs.get(key);
				int idx_size = idx.size();
				if(idx_size == 1){ // 单索引
					Map<String, Object> index = idx.get(0);
					String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
					String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
//					String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
					String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
					String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
					String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
					String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
					
					if(INDEX_NAME.equals("PRIMARY"))
						continue;
					if(basicType.equals("java.util.Date"))
						continue;

					sn(sb, "        varsBy%s.clear();", COLUMN_NAME_UEN);
					
				}else{ // 多索引
//					Map<String, Object> index = idx.get(0);
					String index1 = BeanBuilder.index1(rsmd, idx);
					// String index2 = SqlBeanBuilder.index2(rsmd, idx);
					// String index3 = SqlBeanBuilder.index3(rsmd, idx);
					// String index4 = SqlBeanBuilder.index4(rsmd, idx);
					sn(sb, "        varsBy%s.clear();", index1);
				}
			}
			}
		
		sn(sb, "        FIXED = new %s[MAX];", tableUEn);
		sn(sb, "        fixedCache.clear();", tableUEn);
		sn(sb, "        vars.clear();");
		sn(sb, "        LASTID.set(0);");
		sn(sb, "        // LASTID = 0;");
		//sn(sb, "        clearFutures();");
		sn(sb, "    }");
		sn(sb, "");
		
//		sn(sb, "    public static void clearIndexs(){");
//		{
//		Iterator<String> it = indexs.keySet().iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			List<Map<String, Object>> idx = indexs.get(key);
//			int idx_size = idx.size();
//			if(idx_size == 1){ // 单索引
//				Map<String, Object> index = idx.get(0);
//				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
//				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
////				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
//				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
//				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
//				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
//				String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
//				
//				if(INDEX_NAME.equals("PRIMARY"))
//					continue;
//				if(basicType.equals("java.util.Date"))
//					continue;
//
//				sn(sb, "        varsBy%s.clear();", COLUMN_NAME_UEN);
//				
//			}else{ // 多索引
////				Map<String, Object> index = idx.get(0);
//				String index1 = BeanBuilder.index1(rsmd, idx);
//				// String index2 = SqlBeanBuilder.index2(rsmd, idx);
//				// String index3 = SqlBeanBuilder.index3(rsmd, idx);
//				// String index4 = SqlBeanBuilder.index4(rsmd, idx);
//				sn(sb, "        varsBy%s.clear();", index1);
//			}
//		}
//		}
//		sn(sb, "    }");
//		sn(sb, "");
		
		sn(sb, "    public static int count(){");
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return count(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int count(String TABLENAME2){");
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return count(DAO, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int count(%sDAO DAO){", tableUEn);
		sn(sb, "        return count(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int count(%sDAO DAO, String TABLENAME2){", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.count(TABLENAME2);");
		sn(sb, "        } else if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            return fixedCache.size();");
		sn(sb, "        } else {  // FULL_CACHE || FULL_MEMORY");
		sn(sb, "            return vars.size();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void relocate(String TABLENAME2) {");
		sn(sb, "        DAO().TABLENAME = TABLENAME2;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void relocate(%sDAO DAO, String TABLENAME2) {", tableUEn);
		sn(sb, "        DAO().TABLENAME = TABLENAME2;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String createTableYy() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return createTableYy(DAO);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String createTableYy(%sDAO DAO) {", tableUEn);
		sn(sb, "        String TABLENAME2 = DAO.TABLEYY();");
		sn(sb, "        createTable(DAO, TABLENAME2);");
		sn(sb, "        return TABLENAME2;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String createTableMm() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return createTableMm(DAO);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String createTableMm(%sDAO DAO) {", tableUEn);
		sn(sb, "        String TABLENAME2 = DAO.TABLEMM();");
		sn(sb, "        createTable(DAO, TABLENAME2);");
		sn(sb, "        return TABLENAME2;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String createTableDd() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return createTableDd(DAO);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String createTableDd(%sDAO DAO) {", tableUEn);
		sn(sb, "        String TABLENAME2 = DAO.TABLEDD();");
		sn(sb, "        createTable(DAO, TABLENAME2);");
		sn(sb, "        return TABLENAME2;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void createTable(String TABLENAME2) {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        DAO.createTable(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void createTable(%sDAO DAO) {", tableUEn);
		sn(sb, "        DAO.createTable(DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void createTable(%sDAO DAO, String TABLENAME2) {", tableUEn);
		sn(sb, "        DAO.createTable(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void createNoUniqueTable(String TABLENAME2) {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        DAO.createNoUniqueTable(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void createNoUniqueTable(%sDAO DAO) {", tableUEn);
		sn(sb, "        DAO.createNoUniqueTable(DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void createNoUniqueTable(%sDAO DAO, String TABLENAME2) {", tableUEn);
		sn(sb, "        DAO.createNoUniqueTable(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void loadAll() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        loadAll(DAO);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void loadAll(%sDAO DAO) {", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE )");
		sn(sb, "            return;");
		sn(sb, "        clear();");
		sn(sb, "        if( cacheModel == STATIC_CACHE )");
		sn(sb, "            if (FIXED == null || MAX <= 0)");
		sn(sb, "                FIXED = new %s[maxId(DAO)];", tableUEn);
		sn(sb, "");
		sn(sb, "        List<%s> %ss = DAO.selectAll();", tableUEn, tableEn);
		sn(sb, "        for (%s %s : %ss) {", tableUEn, tableEn, tableEn);
		sn(sb, "            %s.reset();", tableEn);
		sn(sb, "            put(%s);", tableEn);
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static Map toMap(%s %s){", tableUEn, tableEn);
		sn(sb, "        return %s.toMap();", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<Map> toMap(List<%s> %ss){", tableUEn, tableEn);
		sn(sb, "        List<Map> ret = new Vector<Map>();");
		sn(sb, "        for (%s %s : %ss){", tableUEn, tableEn, tableEn);
		sn(sb, "            Map e = %s.toMap();", tableEn);
		sn(sb, "            ret.add(e);");
		sn(sb, "        }");
		sn(sb, "        return ret;");
		sn(sb, "    }");
		sn(sb, "");

		
		// sortBy
		sn(sb, "    public static List<%s> sortZh(List<%s> %ss, final String key) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        Collections.sort(%ss, new Comparator<%s>() {", tableEn, tableUEn, tableUEn);
		sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
		sn(sb, "                return o1.compareZhTo(o2, key);");
		sn(sb, "            }");
		sn(sb, "        });");
		sn(sb, "        return %ss;", tableEn);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> sort(List<%s> %ss, final String key) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        Collections.sort(%ss, new Comparator<%s>() {", tableEn, tableUEn, tableUEn);
		sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
		sn(sb, "                return o1.compareTo(o2, key);");
		sn(sb, "            }");
		sn(sb, "        });");
		sn(sb, "        return %ss;", tableEn);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> sort(List<%s> %ss){", tableUEn, tableUEn, tableEn);
		sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
		sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
		sn(sb, "                Object v1 = o1.%s;", primaryKey);
		sn(sb, "                Object v2 = o2.%s;", primaryKey);
		sn(sb, "                return compareTo(v1, v2);");
		sn(sb, "            }");
		sn(sb, "        });");
		sn(sb, "        return %ss;", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> sortReverse(List<%s> %ss){", tableUEn, tableUEn, tableEn);
		sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
		sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
		sn(sb, "                Object v1 = o1.%s;", primaryKey);
		sn(sb, "                Object v2 = o2.%s;", primaryKey);
		sn(sb, "                return 0 - compareTo(v1, v2);");
		sn(sb, "            }");
		sn(sb, "        });");
		sn(sb, "        return %ss;", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		{
		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
//				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				//String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				//String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
				
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				sn(sb, "    public static List<%s> sort%s(List<%s> %ss){", tableUEn, COLUMN_NAME_UEN, tableUEn, tableEn);
				sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
				sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
				sn(sb, "                Object v1 = o1.get%s();", COLUMN_NAME_UEN);
				sn(sb, "                Object v2 = o2.get%s();", COLUMN_NAME_UEN);
				sn(sb, "                return compareTo(v1, v2);");
				sn(sb, "            }");
				sn(sb, "        });");
				sn(sb, "        return %ss;", tableEn);
				sn(sb, "    }");
				sn(sb, "");
				sn(sb, "    public static List<%s> sort%sRo(List<%s> %ss){", tableUEn, COLUMN_NAME_UEN, tableUEn, tableEn);
				sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
				sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
				sn(sb, "                Object v1 = o1.get%s();", COLUMN_NAME_UEN);
				sn(sb, "                Object v2 = o2.get%s();", COLUMN_NAME_UEN);
				sn(sb, "                return 0 - compareTo(v1, v2);");
				sn(sb, "            };");
				sn(sb, "        });");
				sn(sb, "        return %ss;", tableEn);
				sn(sb, "    }");
				sn(sb, "");
			}else{ // 多索引
			}
		}
		}
		
		sn(sb, "    public static %s insert(%s %s) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return insert(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s insert(%sDAO DAO, %s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        return insert(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s insert(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return insert(DAO, %s, TABLENAME2);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s insert(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE && LASTID.intValue() >= MAX) {");
		sn(sb, "            log.error(\"The cacheModel = STATIC_CACHE is Full.\");");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        %s n = 0;", pkBasicType);
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            n = DAO.insert(%s, TABLENAME2);", tableEn);
		sn(sb, "            if(n <= 0) return null;");
		sn(sb, "        }else{");
		sn(sb, "            n = LASTID.incrementAndGet();");
		sn(sb, "            // n = LASTID + 1;");
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        %s.%s = n;", tableEn, primaryKey);
		sn(sb, "        if(cacheModel != NO_CACHE) put(%s);", tableEn);
		sn(sb, "");
		sn(sb, "        return %s;", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		
		sn(sb, "    public static %s asynchronousInsert(%s %s) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return asynchronousInsert(DAO, %s, DAO.TABLENAME);",tableEn);
		sn(sb, "    }");

		sn(sb, "    public static %s asynchronousInsert(%sDAO DAO, %s %s) {",tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        return asynchronousInsert(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");

		sn(sb, "    public static %s asynchronousInsert(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return asynchronousInsert(DAO, %s, TABLENAME2);", tableEn);
		sn(sb, "    }");

		sn(sb, "    public static %s asynchronousInsert(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE && LASTID.intValue() >= MAX) {");
		sn(sb, "            log.error(\"The cacheModel = STATIC_CACHE is Full.\");");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "        %s n = 0;", primaryKeyType);
		sn(sb, "        if(cacheModel != FULL_MEMORY) {");
		sn(sb, "            DAO.asynchronousInsert(%s, TABLENAME2);", tableEn);
		sn(sb, "        }");
		sn(sb, "        n = LASTID.incrementAndGet();");
		sn(sb, "        %s.%s = n;", tableEn, primaryKey);
		sn(sb, "        if(cacheModel != NO_CACHE) put(%s);", tableEn);
		sn(sb, "        return %s;",tableEn);
		sn(sb, "    }");
		
		sn(sb, "    public static %s insert2(%s %s) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return insert2(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s insert2(%sDAO DAO, %s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        return insert2(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s insert2(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return insert2(DAO, %s, TABLENAME2);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s insert2(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE && LASTID.intValue() >= MAX) {");
		sn(sb, "            log.error(\"The cacheModel = STATIC_CACHE is Full.\");");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "        %s n = 0;", pkBasicType);
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            n = DAO.insert2(%s, TABLENAME2);", tableEn);
		sn(sb, "            if(n <= 0) return null;");
		sn(sb, "        }else{");
		sn(sb, "            n = LASTID.incrementAndGet();");
		sn(sb, "            // n = LASTID + 1;");
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        %s.%s = n;", tableEn, primaryKey);
		sn(sb, "        if(cacheModel != NO_CACHE) put(%s);", tableEn);
		sn(sb, "");
		sn(sb, "        return %s;", tableEn);
		sn(sb, "    }");
		sn(sb, "");
		
		//
		sn(sb, "    public static %s asynchronousInsert2(%s %s) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return asynchronousInsert2(DAO, %s, DAO.TABLENAME);",tableEn);
		sn(sb, "    }");

		sn(sb, "    public static %s asynchronousInsert2(%sDAO DAO, %s %s) {",tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        return asynchronousInsert2(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");

		sn(sb, "    public static %s asynchronousInsert2(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return asynchronousInsert2(DAO, %s, TABLENAME2);", tableEn);
		sn(sb, "    }");

		sn(sb, "    public static %s asynchronousInsert2(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE && LASTID.intValue() >= MAX) {");
		sn(sb, "            log.error(\"The cacheModel = STATIC_CACHE is Full.\");");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "        int n = LASTID.incrementAndGet();");
		sn(sb, "        %s.%s = n;", tableEn, primaryKey);
		sn(sb, "        if(cacheModel != FULL_MEMORY) {");
		sn(sb, "            DAO.asynchronousInsert2(%s, TABLENAME2);", tableEn);
		sn(sb, "        }");
		sn(sb, "        if(cacheModel != NO_CACHE) put(%s);", tableEn);
		sn(sb, "        return %s;",tableEn);
		sn(sb, "    }");
		//
		
		if(batch){ // 批处理
		sn(sb, "    public static int[] insert(List<%s> %ss) {", tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return insert(DAO, %ss, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int[] insert(%sDAO DAO, List<%s> %ss) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return insert(DAO, %ss, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int[] insert(List<%s> %ss, String TABLENAME2) {", tableUEn, tableEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return insert(DAO, %ss, TABLENAME2);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int[] insert(%sDAO DAO, List<%s> %ss, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE && LASTID.intValue() >= MAX) {");
		sn(sb, "            log.error(\"The cacheModel = STATIC_CACHE is Full.\");");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.insert(%ss, TABLENAME2);", tableEn);
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        int[] ret = new int[%ss.size()];", tableEn);
		sn(sb, "        int n = 0;");
		sn(sb, "        for(%s %s : %ss){", tableUEn, tableEn, tableEn);
		sn(sb, "            %s = insert(DAO, %s, TABLENAME2);", tableEn, tableEn);
		sn(sb, "            ret[n++] = (%s == null) ? 0 : (int)%s.%s;", tableEn, tableEn, primaryKey);
		sn(sb, "        }");
		sn(sb, "        return ret;");
		sn(sb, "    }");
		sn(sb, "");
		}

		sn(sb, "    public static int delete(%s %s) {", tableUEn, tableEn);
		sn(sb, "        %s %s = %s.%s;", pkBasicType, primaryKey, tableEn, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return delete(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int delete(%s %s) {", pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return delete(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int delete(%sDAO DAO, %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return delete(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int delete(%s %s, String TABLENAME2) {", pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return delete(DAO, %s, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int delete(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        int n = 1;");
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            n = DAO.deleteByKey(%s, TABLENAME2);", primaryKey);
		sn(sb, "            //if(n <= 0) return 0;");
		sn(sb, "        }");
		sn(sb, "        if(cacheModel != NO_CACHE) {");
		sn(sb, "            deleteFromMemory(%s);", primaryKey);
		sn(sb, "        }");
		sn(sb, "        return n;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void asynchronousDelete(%s %s) {", tableUEn, tableEn);
		sn(sb, "        %s %s = %s.%s;", pkBasicType, primaryKey, tableEn, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        asynchronousDelete(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void asynchronousDelete(%s %s) {", pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        asynchronousDelete(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void asynchronousDelete(%sDAO DAO, %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        asynchronousDelete(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void asynchronousDelete(%s %s, String TABLENAME2) {", pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        asynchronousDelete(DAO, %s, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void asynchronousDelete(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            DAO.asynchronousDeleteByKey(%s, TABLENAME2);", primaryKey);
		sn(sb, "        }");
		sn(sb, "        if(cacheModel != NO_CACHE) {");
		sn(sb, "            deleteFromMemory(%s);", primaryKey);
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		if(batch){ // 批处理
		sn(sb, "    public static int[] delete(%s[] %ss) {", pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return delete(DAO, %ss, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int[] delete(%sDAO DAO, %s[] %ss) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return delete(DAO, %ss, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int[] delete(%s[] %ss,String TABLENAME2) {", pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return delete(DAO, %ss, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int[] delete(%sDAO DAO, %s[] %ss,String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.deleteByKey(%ss, TABLENAME2);", primaryKey);
		sn(sb, "        }");
		sn(sb, "        int[] ret = new int[%ss.length];", primaryKey);
		sn(sb, "        int n = 0;");
		sn(sb, "        for(%s %s : %ss){", pkBasicType, primaryKey, primaryKey);
		sn(sb, "            ret[n++] = delete(DAO, %s, TABLENAME2);", primaryKey);
		sn(sb, "        }");
		sn(sb, "        return ret;");
		sn(sb, "    }");
		sn(sb, "");
		}

		if(batch){
		sn(sb, "    public static int deleteIn(List<%s> keys) {", primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return deleteIn(keys, DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int deleteIn(List<%s> keys, %sDAO DAO) {", primaryKeyType, tableUEn);
		sn(sb, "        return deleteIn(keys, DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int deleteIn(List<%s> keys, String TABLENAME2) {", primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return deleteIn(keys, DAO, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int deleteIn(final List<%s> keys, final %sDAO DAO, final String TABLENAME2) {", primaryKeyType, tableUEn);
		sn(sb, "        if(keys == null || keys.isEmpty()) return 0;");
		sn(sb, "        int result = DAO.deleteInKeys(keys, TABLENAME2);");
		sn(sb, "        if(cacheModel != NO_CACHE) {");
		sn(sb, "            for(%s %s : keys){", primaryKeyType, primaryKey);
		sn(sb, "                deleteFromMemory(%s);", primaryKey);
		sn(sb, "            }");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static int deleteWith(List<%s> beans) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return deleteWith(beans, DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int deleteWith(List<%s> beans, %sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return deleteWith(beans, DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int deleteWith(List<%s> beans, String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return deleteWith(beans, DAO, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int deleteWith(final List<%s> beans, final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        if(beans == null || beans.isEmpty()) return 0;");
		sn(sb, "        int result = DAO.deleteInBeans(beans, TABLENAME2);");
		sn(sb, "        if(cacheModel != NO_CACHE) {");
		sn(sb, "            for(%s %s : beans){", tableUEn, tableEn);
		sn(sb, "                %s %s = %s.%s;", pkBasicType, primaryKey, tableEn, primaryKey);		
		sn(sb, "                deleteFromMemory(%s);", primaryKey);
		sn(sb, "            }");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		
		}
		
		sn(sb, "    public static List<%s> getAll() {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAll(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return getAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAll(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getAll(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAll(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.selectAll(TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = getNoSortAll(DAO, TABLENAME2);", tableUEn);
		//sn(sb, "            return sort(result);");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortAll() {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getNoSortAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortAll(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return getNoSortAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortAll(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getNoSortAll(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getNoSortAll(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.selectAll(TABLENAME2);");
		sn(sb, "        } else if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            List<%s> result = newList();", tableUEn);
		sn(sb, "            result.addAll(fixedCache);");
		sn(sb, "            return result;");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = newList();", tableUEn);
		sn(sb, "            result.addAll(vars.values());");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static Set<%s> memoryKeys(){", primaryKeyType);
		sn(sb, "        if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            Set<%s> result = newSet();", primaryKeyType);
		sn(sb, "            int max = FIXED.length;");
		sn(sb, "            for (int i = 0; i < max; i++) {");
		sn(sb, "                %s %s = FIXED[i];", tableUEn, tableEn);
		sn(sb, "                if (%s != null) result.add(i + 1);", tableEn);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            return vars.keySet();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static Collection<%s> memoryValues(){", tableUEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            return fixedCache;");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            return vars.values();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAllNotCopy(){", tableUEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            return fixedCache;");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            return new Vector(vars.values());");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks() {", primaryKeyType);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getPks(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks(%sDAO DAO) {", primaryKeyType, tableUEn);
		sn(sb, "        return getPks(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks(String TABLENAME2) {", primaryKeyType);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getPks(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks(final %sDAO DAO, final String TABLENAME2) {", primaryKeyType, tableUEn);
		sn(sb, "        if ( cacheModel == NO_CACHE) { ");
		sn(sb, "            return DAO.selectPKs(TABLENAME2);");
		sn(sb, "        } else if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            result.addAll(memoryKeys());");
		sn(sb, "            return result;");
		sn(sb, "        } else {");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            result.addAll(vars.keySet());");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		///
		
		sn(sb, "    public static List<Map> getInIndex() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getInIndex(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<Map> getInIndex(%sDAO DAO) {", tableUEn);
		sn(sb, "        return getInIndex(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<Map> getInIndex(String TABLENAME2) {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getInIndex(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<Map> getInIndex(final %sDAO DAO, final String TABLENAME2) {", tableUEn);
		sn(sb, "        return DAO.selectInIndex(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");
		
		if(batch){
		sn(sb, "    public static List<%s> getIn(List<%s> keys) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getIn(List<%s> keys, %sDAO DAO) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        return getIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getIn(List<%s> keys, String TABLENAME2) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getIn(keys, DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getIn(final List<%s> keys, final %sDAO DAO, final String TABLENAME2) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        if(keys == null || keys.isEmpty()) return newList();");
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectIn(keys, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = getNoSortIn(keys, DAO, TABLENAME2);", tableUEn);
		//sn(sb, "            return sort(result);");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getNoSortIn(List<%s> keys) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getNoSortIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortIn(List<%s> keys, %sDAO DAO) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        return getNoSortIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortIn(List<%s> keys, String TABLENAME2) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getNoSortIn(keys, DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getNoSortIn(final List<%s> keys, final %sDAO DAO, final String TABLENAME2) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        if(keys == null || keys.isEmpty()) return newList();");
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectIn(keys, TABLENAME2);");
		sn(sb, "        } else { // STATIC_CACHE || FULL_CACHE || FULL_MEMORY");
		sn(sb, "            List<%s> result = newList();", tableUEn);
		sn(sb, "            for (%s key : keys) {", pkBasicType);
		sn(sb, "                %s %s = getByKeyFromMemory(key);", tableUEn, tableEn);
		sn(sb, "                if( %s == null ) continue;", tableEn);
		sn(sb, "                result.add(%s);", tableEn);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		}
		
		sn(sb, "    public static List<%s> getLast(int num) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getLast(DAO, num, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getLast(%sDAO DAO, int num) {", tableUEn, tableUEn);
		sn(sb, "        return getLast(DAO, num, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getLast(int num, String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getLast(DAO, num, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getLast(final %sDAO DAO, final int num, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectLast(num, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE or FULL_MEMORY");
		sn(sb, "            List<%s> result = newList();", tableUEn);
		sn(sb, "            List<%s> mvars = getAll(DAO, TABLENAME2);", tableUEn);
		sn(sb, "            if( mvars.size() > num ){");
		sn(sb, "                result = mvars.subList(mvars.size() - num, mvars.size());");
		sn(sb, "            }else{");
		sn(sb, "                result.addAll(mvars);");
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static %s last() {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return last(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s last(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return last(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s last(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return last(DAO, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s last(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        %s result = null;", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.last(TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
		sn(sb, "            int id = LASTID.get();");
		sn(sb, "            result = getByKey(DAO, id, TABLENAME2);");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static %s maxId() {", pkBasicType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return maxId(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s maxId(%sDAO DAO) {", pkBasicType, tableUEn);
		sn(sb, "        return maxId(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static %s maxId(String TABLENAME2) {", pkBasicType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return maxId(DAO, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s maxId(final %sDAO DAO, final String TABLENAME2) {", pkBasicType, tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.maxId(TABLENAME2);");
		sn(sb, "        }");
		sn(sb, "        // FULL_CACHE || FULL_MEMORY || STATIC_CACHE");
		sn(sb, "        int id = LASTID.get();");
		sn(sb, "        if(id > 0) return id;");
		sn(sb, "        return DAO.maxId(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(%s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getGtKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(%sDAO DAO, %s %s) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return getGtKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(%s %s, String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getGtKey(DAO, %s, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectGtKey(%s, TABLENAME2);", primaryKey);
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = newList();", tableUEn);
		sn(sb, "            List<%s> %ss = getAll();", tableUEn, tableEn);
		sn(sb, "            for (%s %s : %ss) {", tableUEn, tableEn, tableEn);
		sn(sb, "                if(%s.%s <= %s) continue;", tableEn, primaryKey, primaryKey);
		sn(sb, "                result.add(%s);", tableEn);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        }");
		//sn(sb, "        sort(result);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(%s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getByKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(%sDAO DAO, %s %s) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return getByKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(%s %s, String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getByKey(DAO, %s, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.selectByKey(%s, TABLENAME2);", primaryKey);
		sn(sb, "        }");
		sn(sb, "        return getByKeyFromMemory(%s);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKeyFromMemory(final %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            if (%s < 1 || FIXED == null || FIXED.length < %s) return null;", primaryKey, primaryKey);
		sn(sb, "            return FIXED[%s - 1];", primaryKey);
		sn(sb, "        } else if (cacheModel == FULL_CACHE || cacheModel == FULL_MEMORY) {");
		sn(sb, "            return vars.get(%s);", primaryKey);
		sn(sb, "        }");
		sn(sb, "        return null;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s deleteFromMemory(final %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %s %s;", tableUEn, tableEn);
		sn(sb, "        if (cacheModel == STATIC_CACHE) {");
		sn(sb, "            if (%s < 1 || FIXED == null || FIXED.length < %s || FIXED[%s-1]==null) return null;", primaryKey, primaryKey, primaryKey);
		sn(sb, "            %s = FIXED[%s - 1];", tableEn, primaryKey);
		sn(sb, "            FIXED[%s - 1] = null;", primaryKey);
		sn(sb, "            fixedCache.remove(%s);", tableEn);
		sn(sb, "        } else {");
		sn(sb, "            %s = vars.remove(%s);", tableEn, primaryKey);
		sn(sb, "        }");
		sn(sb, "        if(%s == null) return null;", tableEn);
		sn(sb, "");
		{
		Iterator<String> it = indexs.keySet().iterator();
		int p = 0;
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
				
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "        %s %s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
					sn(sb, "        varsBy%s.remove(%s);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				}else{
					if(basicType.equals("java.util.Date"))
						continue;
					p = p + 1;
					sn(sb, "        %s %s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
					sn(sb, "        Set m%d = varsBy%s.get(%s);", p, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        if(m%d != null)", p);
					sn(sb, "            m%d.remove(%s);", p, primaryKey);
				}
				sn(sb, "");
				
			}else{ // 多索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String index1 = BeanBuilder.index1(rsmd, idx);
				// String index2 = SqlBeanBuilder.index2(rsmd, idx);
				// String index3 = SqlBeanBuilder.index3(rsmd, idx);
				// String index4 = SqlBeanBuilder.index4(rsmd, idx);
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String skey = "";
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "        { // %s" , INDEX_NAME);
					for (Map<String, Object> map : idx) {
						String COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
						String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);

						sn(sb, "            %s v%s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
						skey = skey + s("v%s", COLUMN_NAME_EN);
						if(idx.indexOf(map) < idx.size() - 1){
							skey = skey + " + \"-\" + ";
						}
					}
					sn(sb, "            String vkey = %s;", skey);
					sn(sb, "            varsBy%s.remove(vkey);", index1);
					sn(sb, "        }");
				}else{
					p = p + 1;
					sn(sb, "        { // %s" , INDEX_NAME);
					for (Map<String, Object> map : idx) {
						String COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
						String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);

						sn(sb, "            %s v%s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
						skey = skey + s("v%s", COLUMN_NAME_EN);
						if(idx.indexOf(map) < idx.size() - 1){
							skey = skey + "+ \"-\" +";
						}
					}
					sn(sb, "            String vkey = %s;", skey);
					sn(sb, "            Set m%d = varsBy%s.get(vkey);", p, index1);
					sn(sb, "            if(m%d != null)", p);
					sn(sb, "                m%d.remove(%s);", p, primaryKey);
					//sn(sb, "        varsBy%s.put(_key, %s);", index1, primaryKey);
					sn(sb, "        }");
				}
				sn(sb, "");
			}
		}
		}		
		sn(sb, "        return %s;", tableEn);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getByPage(int page, int size) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getByPage(DAO, page, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getByPage(%sDAO DAO, int page, int size) {", tableUEn, tableUEn);
		sn(sb, "        return getByPage(DAO, page, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getByPage(int page, int size, String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return getByPage(DAO, page, size, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getByPage(final %sDAO DAO, final int page, final int size,final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        int begin = page * size;");
		sn(sb, "        int num = size;");
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectByPage(begin, num, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = newList();", tableUEn);
		sn(sb, "            List<%s> v = getAll(DAO, TABLENAME2);", tableUEn);
		sn(sb, "            result = SqlEx.getPage(v, page, size);");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		////
		sn(sb, "    public static int pageCount(int size) {", tableUEn);
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return pageCount(DAO, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int pageCount(%sDAO DAO, int size) {", tableUEn);
		sn(sb, "        return pageCount(DAO, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int pageCount(int size, String TABLENAME2) {");
		sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
		sn(sb, "        return pageCount(DAO, size, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int pageCount(final %sDAO DAO, final int size,final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        int v = 0;");
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            v = DAO.count(TABLENAME2);");
		sn(sb, "        }else{");
		sn(sb, "            v = count(DAO, TABLENAME2);");
		sn(sb, "        }");
		sn(sb, "        return SqlEx.pageCount(v, size);");
		sn(sb, "    }");
		sn(sb, "");
		
		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				String COLUMN_NAME_BTYPE = JavaType.getBasicType(COLUMN_NAME_TYPE);
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				if(COLUMN_NAME_TYPE.equals("java.util.Date"))
					continue;
				if(NON_UNIQUE.equals("false")){
					sn(sb, "    public static %s getBy%s(%s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%sDAO DAO, %s %s) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%s %s, String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_BTYPE, COLUMN_NAME_EN);
					sn(sb, "        if( cacheModel == NO_CACHE ){");
					sn(sb, "            return DAO.selectBy%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
					sn(sb, "            %s %s = varsBy%s.get(%s);", primaryKeyType, primaryKey, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "            if(%s == null) return null;", primaryKey);
					sn(sb, "            %s result = getByKey(DAO, %s, TABLENAME2);", tableUEn, primaryKey);
					sn(sb, "            if(result == null) return null;");
					if(COLUMN_NAME_BTYPE.equals("String")){
						sn(sb, "            if(!result.get%s().equals(%s)){", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					}else{
						sn(sb, "            if(result.get%s() != %s){", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					}
					
					sn(sb, "                varsBy%s.remove(%s);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "                return null;");
					sn(sb, "            }");
					sn(sb, "            return result;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
				}else{
					sn(sb, "    public static int countBy%s(%s %s) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%sDAO DAO, %s %s) {", COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%s %s, String TABLENAME2) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return countBy%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        if(cacheModel == NO_CACHE){");
					sn(sb, "            return DAO.countBy%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        }");
					sn(sb, "        List<%s> %ss = getBy%s(DAO, %s, TABLENAME2);", tableUEn, tableEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        return %ss.size();", tableEn);
					sn(sb, "    }");
					sn(sb, "");
					
					sn(sb, "    public static List<%s> getBy%s(%s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%sDAO DAO, %s %s) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%s %s, String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        if( cacheModel == NO_CACHE ){");
					sn(sb, "            return DAO.selectBy%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        } else { //FULL_CACHE || FULL_MEMORY {");
					sn(sb, "            List<%s> result = newList();", tableUEn);
					sn(sb, "            Set<%s> m1 = varsBy%s.get(%s);", primaryKeyType,  COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "            if (m1 == null || m1.isEmpty()) return result;");
					sn(sb, "            List<%s> list = new ArrayList(m1);", primaryKeyType);
					sn(sb, "            for (%s key : list) {;", pkBasicType);
					sn(sb, "                %s e = getByKey(DAO, key, TABLENAME2);", tableUEn);
					sn(sb, "                if(e == null){");
					sn(sb, "                    m1.remove(key); ");
					sn(sb, "                    continue;");
					sn(sb, "                }");
					sn(sb, "                %s _%s = e.get%s();", COLUMN_NAME_BTYPE, COLUMN_NAME_EN, COLUMN_NAME_UEN);
					if(COLUMN_NAME_BTYPE.equals("int") || COLUMN_NAME_BTYPE.equals("long") || COLUMN_NAME_BTYPE.equals("boolean")){
						sn(sb, "                if(_%s != %s){ ", COLUMN_NAME_EN, COLUMN_NAME_EN);
					}else if(COLUMN_NAME_BTYPE.equals("String") || COLUMN_NAME_BTYPE.equals("java.util.Date")){
						sn(sb, "                if(!_%s.equals(%s)){ ", COLUMN_NAME_EN, COLUMN_NAME_EN);
					}
					sn(sb, "                    m1.remove(key);");
					sn(sb, "                    continue;");
					sn(sb, "                }");
					sn(sb, "                result.add(e);");
					sn(sb, "            }");
					
					//sn(sb, "            sort(result);");
					sn(sb, "            return result;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
				} 
				
				// like 
				if(COLUMN_NAME_TYPE.equals("String")){
				sn(sb, "    public static int countLike%s(%s %s) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
				sn(sb, "        return countLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static int countLike%s(%sDAO DAO, %s %s) {", COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return countLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static int countLike%s(%s %s, String TABLENAME2) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
				sn(sb, "        return countLike%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static int countLike%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        if(cacheModel == NO_CACHE){");
				sn(sb, "            return DAO.countLike%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "        }");
				sn(sb, "        List<%s> %ss = getLike%s(DAO, %s, TABLENAME2);", tableUEn, tableEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "        return %ss.size();", tableEn);
				sn(sb, "    }");
				sn(sb, "");
				
				sn(sb, "    public static List<%s> getLike%s(%s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
				sn(sb, "        return getLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static List<%s> getLike%s(%sDAO DAO, %s %s) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return getLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static List<%s> getLike%s(%s %s, String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
				sn(sb, "        return getLike%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static List<%s> getLike%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        if(cacheModel == NO_CACHE){");
				sn(sb, "            return DAO.selectLike%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
				sn(sb, "            List<%s> result = newList();", tableUEn);
				sn(sb, "            List<%s> %ss = getAll(DAO, TABLENAME2);", tableUEn, tableEn);
				sn(sb, "            for(%s e : %ss){", tableUEn, tableEn);
				sn(sb, "                %s _var = e.get%s();", COLUMN_NAME_TYPE, COLUMN_NAME_UEN);
				sn(sb, "                if(_var.indexOf(%s) >= 0)", COLUMN_NAME_EN, primaryKey);
				sn(sb, "                    result.add(e);");
				sn(sb, "            }");
//				sn(sb, "        sort(result);");
				sn(sb, "            return result;");
				sn(sb, "        }");
				sn(sb, "    }");
				sn(sb, "");
				}
				////////////////
				
			}else { // 多键索引
				Map<String, Object> index = idx.get(0);
//				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
//				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
//				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
//				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
//				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
//				String COLUMN_NAME_BTYPE = JavaType.getBasicType(COLUMN_NAME_TYPE);
				String index1 = BeanBuilder.index1(rsmd, idx);
				String index2 = BeanBuilder.index2(rsmd, idx);
				String index3 = BeanBuilder.index3(rsmd, idx);
//				String index4 = BeanBuilder.index4(rsmd, idx);
				String index5 = BeanBuilder.index5(rsmd, idx);
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "    public static %s getBy%s(%s) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%sDAO DAO, %s) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%s, String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, TABLENAME2);", index1,  index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(final %sDAO DAO, %s,final String TABLENAME2) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        if( cacheModel == NO_CACHE ){");
					sn(sb, "            return DAO.selectBy%s(%s, TABLENAME2);", index1, index3);
					sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
					sn(sb, "            String vkey = %s;", index5);
					sn(sb, "            %s %s = varsBy%s.get(vkey);", primaryKeyType, primaryKey, index1);
					sn(sb, "            if(%s == null) return null;", primaryKey);
					sn(sb, "            %s result = getByKey(DAO, %s, TABLENAME2);", tableUEn, primaryKey);
					sn(sb, "            if(result == null) return null;");
					{
						for (Map<String, Object> m : idx) {
							// String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
							String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
							// String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
							String COLUMN_NAME_EN = PinYin
									.getShortPinYin(COLUMN_NAME);
							String COLUMN_NAME_UEN = StrEx
									.upperFirst(COLUMN_NAME_EN);
							String COLUMN_NAME_TYPE = JavaType.getType(rsmd,
									COLUMN_NAME);
							String COLUMN_NAME_BTYPE = JavaType.getBasicType(COLUMN_NAME_TYPE);
							if(COLUMN_NAME_BTYPE.equals("String")){
								sn(sb, "            if(!result.get%s().equals(%s)){", COLUMN_NAME_UEN, COLUMN_NAME_EN);
							}else{
								sn(sb, "            if(result.get%s() != %s){", COLUMN_NAME_UEN, COLUMN_NAME_EN);
							}
							sn(sb, "                varsBy%s.remove(vkey);", index1);
							sn(sb, "                return null;");
							sn(sb, "            }");
						}
					}
					sn(sb, "            return result;");
					sn(sb, "        }");

					
					sn(sb, "    }");
					sn(sb, "");
				}else{ // 非唯一数据
					{
					sn(sb, "    public static int countBy%s(%s) {", index1, index2);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%sDAO DAO, %s) {", index1, tableUEn, index2);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%s, String TABLENAME2) {", index1, index2);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return countBy%s(DAO, %s, TABLENAME2);", index1,  index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(final %sDAO DAO, %s, final String TABLENAME2) {", index1, tableUEn, index2);
					sn(sb, "        if(cacheModel == NO_CACHE){");
					sn(sb, "            return DAO.countBy%s(%s, TABLENAME2);", index1, index3);
					sn(sb, "        }");
					sn(sb, "        List<%s> %ss = getBy%s(%s, TABLENAME2);", tableUEn , tableEn, index1, index3);
					sn(sb, "        return %ss.size();", tableEn);
					sn(sb, "    }");
					sn(sb, "");
					}
					{
					sn(sb, "    public static List<%s> getBy%s(%s) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%sDAO DAO, %s) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%s, String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = (cacheModel == NO_CACHE) ? DAO() : null;", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, TABLENAME2);", index1,  index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(final %sDAO DAO, %s, final String TABLENAME2) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        if( cacheModel == NO_CACHE ){");
					sn(sb, "            return DAO.selectBy%s(%s, TABLENAME2);", index1, index3);
					sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
					sn(sb, "            List<%s> result = newList();", tableUEn);
					sn(sb, "            String vkey = %s;", index5);
					sn(sb, "            Set<%s> m1 = varsBy%s.get(vkey);", primaryKeyType, index1);
					sn(sb, "            if (m1 == null || m1.isEmpty()) return result;");
					sn(sb, "            List<%s> list = new ArrayList(m1);", primaryKeyType);
					sn(sb, "            for (%s key : list) {", pkBasicType);
					sn(sb, "                %s e = getByKey(DAO, key, TABLENAME2);", tableUEn);
					sn(sb, "                if(e == null){");
					sn(sb, "                    m1.remove(key); ");
					sn(sb, "                    continue;");
					sn(sb, "                }");
					String _skey = "";
					for (Map<String, Object> map : idx) {
						String _COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						String _COLUMN_NAME_EN = PinYin.getShortPinYin(_COLUMN_NAME);
						String _COLUMN_NAME_UEN = StrEx.upperFirst(_COLUMN_NAME_EN);
						String _COLUMN_NAME_TYPE = JavaType.getType(rsmd, _COLUMN_NAME);
						String _basicType = JavaType.getBasicType(_COLUMN_NAME_TYPE);

						sn(sb, "                %s _%s = e.get%s();", _basicType, _COLUMN_NAME_EN, _COLUMN_NAME_UEN);
						_skey = _skey + s("_%s", _COLUMN_NAME_EN);
						if(idx.indexOf(map) < idx.size() - 1){
							_skey = _skey + " + \"-\" + ";
						}
					}
					sn(sb, "                String _key = %s;", _skey);
					sn(sb, "                if(!_key.equals(vkey)){");
					sn(sb, "                    m1.remove(key);");
					sn(sb, "                    continue;");
					sn(sb, "                }");
					sn(sb, "                result.add(e);");
					sn(sb, "            }");
					//sn(sb, "            sort(result);");
					sn(sb, "            return result;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					}

				}
			}
			
		}
		
		sn(sb, "    public static %s update(%s %s) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return update(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s update(%sDAO DAO, %s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        return update(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s update(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return update(DAO, %s, TABLENAME2);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s update(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            int n = DAO.updateByKey(%s, TABLENAME2);", tableEn);
		sn(sb, "            if(n == -1) ");
		sn(sb, "                return %s;", tableEn);
		sn(sb, "            else if(n <= 0) ");
		sn(sb, "                return null;");
		sn(sb, "        }");
		sn(sb, "        if(cacheModel != NO_CACHE){");
		sn(sb, "            put(%s);", tableEn);
		sn(sb, "        }");
		sn(sb, "        return %s;", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		
		////
		sn(sb, "    public static %s asynchronousUpdate(%s %s) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return asynchronousUpdate(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s asynchronousUpdate(%sDAO DAO, %s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        return asynchronousUpdate(DAO, %s, DAO.TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s asynchronousUpdate(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return asynchronousUpdate(DAO, %s, TABLENAME2);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s asynchronousUpdate(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            DAO.asynchronousUpdate(%s, TABLENAME2);", tableEn);
		sn(sb, "        }");
		sn(sb, "        if(cacheModel != NO_CACHE){");
		sn(sb, "            put(%s);", tableEn);
		sn(sb, "        }");
		sn(sb, "        return %s;", tableEn);
		sn(sb, "    }");
		sn(sb, "");
		
		////
		
		sn(sb, "    public static void truncate(){");
		sn(sb, "        clear();");
		sn(sb, "        DAO().truncate();");
		sn(sb, "        DAO().repair();");
		sn(sb, "        DAO().optimize();");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int inMemFlush() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return inMemFlush(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int inMemFlush(%sDAO DAO){", tableUEn);
		sn(sb, "        return inMemFlush(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int inMemFlush(String TABLENAME2) {", tableUEn);
		sn(sb, "        return inMemFlush(DAO(), TABLENAME2);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int inMemFlush(final %sDAO DAO, final String TABLENAME2) {", tableUEn);
		sn(sb, "        int result = 0;");
		sn(sb, "        if(cacheModel != FULL_MEMORY)");
		sn(sb, "            return result;");
		sn(sb, "        try {");
		sn(sb, "            DAO.truncate(TABLENAME2);");
		sn(sb, "            DAO.repair(TABLENAME2);");
		sn(sb, "            DAO.optimize(TABLENAME2);");
		sn(sb, "        } catch (Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        List<%s> %ss = getAll();", tableUEn, tableEn);
		sn(sb, "        for (%s %s : %ss) {", tableUEn, tableEn, tableEn);
		sn(sb, "            int n = DAO.insert2(%s, TABLENAME2);", tableEn);
		sn(sb, "            if (n > 0) result++;");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "}");
		
		return sb.toString();
	}
}
