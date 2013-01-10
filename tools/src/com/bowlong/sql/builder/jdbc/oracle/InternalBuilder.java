package com.bowlong.sql.builder.jdbc.oracle;

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
		String TABLENAME = "BASEACCOUNTPERIOD";
		String sql = "SELECT * FROM " + TABLENAME;
		String host = "192.168.1.114";
		int port = 1521;
		String db = "ORCL"; 
		String user = "DFDBO";
		String password = "Df010203";
		Connection conn = SqlEx.newOracleConnection(host, port, db, user, password);

		String bpackage = "fych.db";
		String appContext = "fych.context.AppContext";
		boolean batch = true;
		
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		String xml = build(conn, rs, bpackage, appContext, batch, db, TABLENAME);
		System.out.println(xml);

	}

	public static String build(Connection conn, ResultSet rs, String pkg, String appContext, boolean batch, String db, String TABLENAME) throws Exception {
		TABLENAME = TABLENAME.replace("\"", "");
		StringBuffer sb = new StringBuffer();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		String catalogName = db;//(String) columns.get(0).get("catalogName");
		String table = TABLENAME; // (String) columns.get(0).get("tableName");
		String tableEn = PinYin.getShortPinYin(table);
		String tableUEn = Ss (tableEn);
		Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn, table);
		String primaryKey = BeanBuilder.primaryKey(conn, table, columns);
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
		sn(sb, "    public static final Map<%s, %s> vars = newMap();", primaryKeyType, tableUEn);
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
				if(INDEX_NAME.equals(primaryKey))
					continue;
				if(NON_UNIQUE.equals("0")){ // 唯一数据
					sn(sb, "    public static final Map<%s, %s> varsBy%s = newMap();", COLUMN_NAME_TYPE, primaryKeyType, COLUMN_NAME_UEN);
				}else{
					if(COLUMN_NAME_TYPE.equals("java.util.Date"))
						continue;
					sn(sb, "    public static final Map<%s, Map<%s, %s>> varsBy%s = newMap();", COLUMN_NAME_TYPE, primaryKeyType, primaryKeyType, COLUMN_NAME_UEN);
				}
				
			}else{ // 多索引
				Map<String, Object> index = idx.get(0);
				String index1 = BeanBuilder.index1(rsmd, idx);
				// String index2 = SqlBeanBuilder.index2(rsmd, idx);
				// String index3 = SqlBeanBuilder.index3(rsmd, idx);
				// String index4 = SqlBeanBuilder.index4(rsmd, idx);
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				if(NON_UNIQUE.equals("0")){ // 唯一数据
					sn(sb, "    public static final Map<String, %s> varsBy%s = newMap();", primaryKeyType, index1);
				}else{
					sn(sb, "    public static final Map<String, Map<%s, %s>> varsBy%s = newMap();", primaryKeyType, primaryKeyType, index1);
				}
			}
		}
		}
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

		sn(sb, "    private static void put(%s %s){", tableUEn, tableEn);
		sn(sb, "        if(%s == null) return ;", tableEn);
		sn(sb, "");
		//sn(sb, "        %s.ustr();", tableEn);
		sn(sb, "        %s %s = %s.%s;", pkBasicType, primaryKey, tableEn, primaryKey);
		//sn(sb, "        refreshFuture(%s);", primaryKey);
		sn(sb, "        vars.put(%s, %s);", primaryKey, tableEn);
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
				
				if(INDEX_NAME.equals("PRIMARY") || COLUMN_NAME.equals(primaryKey))
					continue;
				if(NON_UNIQUE.equals("0")){ // 唯一数据
					sn(sb, "        %s %s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
					sn(sb, "        varsBy%s.put(%s, %s);", COLUMN_NAME_UEN, COLUMN_NAME_EN, primaryKey);
				}else{
					if(basicType.equals("java.util.Date"))
						continue;
					p = p + 1;
					sn(sb, "        %s %s = %s.get%s();", basicType, COLUMN_NAME_EN, tableEn, COLUMN_NAME_UEN);
					sn(sb, "        Map m%d = varsBy%s.get(%s);", p, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        if(m%d == null){", p);
					sn(sb, "            m%d = newMap();", p);
					sn(sb, "            varsBy%s.put(%s, m%d);", COLUMN_NAME_UEN, COLUMN_NAME_EN, p);
					sn(sb, "        }", p);
					sn(sb, "        m%d.put(%s, %s);", p, primaryKey, primaryKey);
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
				if(NON_UNIQUE.equals("0")){ // 唯一数据
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
					sn(sb, "            Map m%d = varsBy%s.get(vkey);", p, index1);
					sn(sb, "            if(m%d == null){", p);
					sn(sb, "                m%d = newMap();", p);
					sn(sb, "                varsBy%s.put(vkey, m%d);", index1, p);
					sn(sb, "            }", p);
					sn(sb, "            m%d.put(%s, %s);", p, primaryKey, primaryKey);
					//sn(sb, "        varsBy%s.put(_key, %s);", index1, primaryKey);
					sn(sb, "        }");
				}
				sn(sb, "");
			}
		}
		}
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
		sn(sb, "        vars.clear();");
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
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return count(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int count(String TABLENAME2){");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
		sn(sb, "        }else{");
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

//		sn(sb, "    public static String createTableYy() {");
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return createTableYy(DAO);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static String createTableYy(%sDAO DAO) {", tableUEn);
//		sn(sb, "        String TABLENAME2 = DAO.TABLEYY();");
//		sn(sb, "        createTable(DAO, TABLENAME2);");
//		sn(sb, "        return TABLENAME2;");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static String createTableMm() {");
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return createTableMm(DAO);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static String createTableMm(%sDAO DAO) {", tableUEn);
//		sn(sb, "        String TABLENAME2 = DAO.TABLEMM();");
//		sn(sb, "        createTable(DAO, TABLENAME2);");
//		sn(sb, "        return TABLENAME2;");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static String createTableDd() {");
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return createTableDd(DAO);");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static String createTableDd(%sDAO DAO) {", tableUEn);
//		sn(sb, "        String TABLENAME2 = DAO.TABLEDD();");
//		sn(sb, "        createTable(DAO, TABLENAME2);");
//		sn(sb, "        return TABLENAME2;");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static void createTable(String TABLENAME2) {");
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        DAO.createTable(TABLENAME2);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static void createTable(%sDAO DAO) {", tableUEn);
//		sn(sb, "        DAO.createTable(DAO.TABLENAME);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static void createTable(%sDAO DAO, String TABLENAME2) {", tableUEn);
//		sn(sb, "        DAO.createTable(TABLENAME2);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static void createNoUniqueTable(String TABLENAME2) {");
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        DAO.createNoUniqueTable(TABLENAME2);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static void createNoUniqueTable(%sDAO DAO) {", tableUEn);
//		sn(sb, "        DAO.createNoUniqueTable(DAO.TABLENAME);");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static void createNoUniqueTable(%sDAO DAO, String TABLENAME2) {", tableUEn);
//		sn(sb, "        DAO.createNoUniqueTable(TABLENAME2);");
//		sn(sb, "    }");
//		sn(sb, "");

		sn(sb, "    public static void loadAll() {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        loadAll(DAO);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static void loadAll(%sDAO DAO) {", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE )");
		sn(sb, "            return;");
		sn(sb, "        clear();");
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
		
		sn(sb, "    public static List<%s> sort(List<%s> %ss){", tableUEn, tableUEn, tableEn);
		sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
		sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
		if(primaryKeyType.contains("int")){
			sn(sb, "               int v1 = (int)o1.%s;", primaryKey);
			sn(sb, "               int v2 = (int)o2.%s;", primaryKey);
		}else{
			sn(sb, "               int v1 = o1.%s.intValue();", primaryKey);
			sn(sb, "               int v2 = o2.%s.intValue();", primaryKey);
		}
		sn(sb, "               return v1 - v2;");
		sn(sb, "            }");
		sn(sb, "        });");
		sn(sb, "        return %ss;", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> sortReverse(List<%s> %ss){", tableUEn, tableUEn, tableEn);
		sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
		sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
		if(primaryKeyType.contains("int")){
			sn(sb, "               int v1 = (int)o1.%s;", primaryKey);
			sn(sb, "               int v2 = (int)o2.%s;", primaryKey);
		}else{
			sn(sb, "               int v1 = o1.%s.intValue();", primaryKey);
			sn(sb, "               int v2 = o2.%s.intValue();", primaryKey);
		}
		sn(sb, "               return v2 - v1;");
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
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
				
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				
				if (!basicType.equals("int") && !basicType.equals("java.util.Date"))
					continue;

				sn(sb, "    public static List<%s> sort%s(List<%s> %ss){", tableUEn, COLUMN_NAME_UEN, tableUEn, tableEn);
				sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
				sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
				if(basicType.equals("int")){
					sn(sb, "                %s v1 = (int)o1.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                %s v2 = (int)o2.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                return v1 - v2;");
				}else if(basicType.contains("BigDecimal")){
					sn(sb, "                %s v1 = o1.get%s().intValue();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                %s v2 = o2.get%s().intValue();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                return v1 - v2;");
				}else if (basicType.equals("java.util.Date")){
					sn(sb, "                %s v1 = o1.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                %s v2 = o2.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                return v2.before(v1) ? 1 : -1;");
				}
				sn(sb, "            }");
				sn(sb, "        });");
				sn(sb, "        return %ss;", tableEn);
				sn(sb, "    }");
				sn(sb, "");
				sn(sb, "    public static List<%s> sort%sRo(List<%s> %ss){", tableUEn, COLUMN_NAME_UEN, tableUEn, tableEn);
				sn(sb, "        Collections.sort(%ss, new Comparator<%s>(){", tableEn, tableUEn);
				sn(sb, "            public int compare(%s o1, %s o2) {", tableUEn, tableUEn);
				if(basicType.equals("int")){
					sn(sb, "                %s v1 = (int)o1.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                %s v2 = (int)o2.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                return v2 - v1;");
				}else if(basicType.contains("BigDecimal")){
					sn(sb, "                %s v1 = o1.get%s().intValue();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                %s v2 = o2.get%s().intValue();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                return v1 - v2;");
				}else if (basicType.equals("java.util.Date")){
					sn(sb, "                %s v1 = o1.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                %s v2 = o2.get%s();", basicType, COLUMN_NAME_UEN);
					sn(sb, "                return v2.before(v1) ? -1 : 1;");
				}
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
		sn(sb, "        int n = 0;");
		sn(sb, "        if(cacheModel != FULL_MEMORY){");
		sn(sb, "            n = DAO.insert(%s, TABLENAME2);", tableEn);
		sn(sb, "            if(n <= 0) return null;");
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        %s.%s = new %s(n);", tableEn, primaryKey, pkBasicType);
		sn(sb, "        if(cacheModel != NO_CACHE) put(%s);", tableEn);
		sn(sb, "");
		sn(sb, "        return %s;", tableEn);
		sn(sb, "    }");
		sn(sb, "");

//		sn(sb, "    public static %s insert2(%s %s) {", tableUEn, tableUEn, tableEn);
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return insert2(DAO, %s, DAO.TABLENAME);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static %s insert2(%sDAO DAO, %s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
//		sn(sb, "        return insert2(DAO, %s, DAO.TABLENAME);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static %s insert2(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return insert2(DAO, %s, TABLENAME2);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static %s insert2(%sDAO DAO, %s %s, String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
//		sn(sb, "        int n = 0;");
//		sn(sb, "        if(cacheModel != FULL_MEMORY){");
//		sn(sb, "            n = DAO.insert2(%s, TABLENAME2);", tableEn);
//		sn(sb, "            if(n <= 0) return null;");
//		sn(sb, "        }");
//		sn(sb, "");
//		sn(sb, "        %s.%s = new %s(n);", tableEn, primaryKey, pkBasicType);
//		sn(sb, "        if(cacheModel != NO_CACHE) put(%s);", tableEn);
//		sn(sb, "");
//		sn(sb, "        return %s;", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
		
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
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.insert(%ss, TABLENAME2);", tableEn);
		sn(sb, "        }");
		sn(sb, "");
		sn(sb, "        int[] ret = new int[%ss.size()];", tableEn);
		sn(sb, "        int n = 0;");
		sn(sb, "        for(%s %s : %ss){", tableUEn, tableEn, tableEn);
		sn(sb, "            %s = insert(DAO, %s, TABLENAME2);", tableEn, tableEn);
		sn(sb, "            ret[n++] = (%s == null) ? 0 : %s.%s.intValue();", tableEn, tableEn, primaryKey);
		sn(sb, "        }");
		sn(sb, "        return ret;");
		sn(sb, "    }");
		sn(sb, "");
		}
		
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
		
//		sn(sb, "    public static int delete2(%s %s) {", pkBasicType, primaryKey);
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return delete2(DAO, %s, DAO.TABLENAME);", primaryKey);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static int delete2(%sDAO DAO, %s %s) {", tableUEn, pkBasicType, primaryKey);
//		sn(sb, "        return delete2(DAO, %s, DAO.TABLENAME);", primaryKey);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static int delete2(%s %s, String TABLENAME2) {", pkBasicType, primaryKey);
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return delete2(DAO, %s, TABLENAME2);", primaryKey);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static int delete2(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
//		sn(sb, "        if(cacheModel != FULL_MEMORY)");
//		sn(sb, "            execute(new Runnable() {");
//		sn(sb, "                public void run() {");
//		sn(sb, "                    DAO.deleteByKey(%s, TABLENAME2);", primaryKey);
//		sn(sb, "                }");
//		sn(sb, "            });");
//		sn(sb, "        if(cacheModel != NO_CACHE) {");
//		sn(sb, "            deleteFromMemory(%s);", primaryKey);
//		sn(sb, "        }");
//		sn(sb, "        return 1;");
//		sn(sb, "    }");
//		sn(sb, "");

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
		}
		
		sn(sb, "    public static List<%s> getAll() {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAll(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return getAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAll(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getAll(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getAll(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.selectAll(TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = getNoSortAll(DAO, TABLENAME2);", tableUEn);
		sn(sb, "            return sort(result);");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortAll() {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getNoSortAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortAll(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return getNoSortAll(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortAll(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getNoSortAll(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getNoSortAll(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        List<%s> result = newList();", tableUEn);
		sn(sb, "        if(cacheModel == NO_CACHE){");
		sn(sb, "            return DAO.selectAll(TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            result.addAll(vars.values());");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static Set<%s> memoryKeys(){", primaryKeyType);
		sn(sb, "        return vars.keySet();");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static Collection<%s> memoryValues(){", tableUEn);
		sn(sb, "        return vars.values();");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks() {", primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getPks(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks(%sDAO DAO) {", primaryKeyType, tableUEn);
		sn(sb, "        return getPks(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks(String TABLENAME2) {", primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getPks(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getPks(final %sDAO DAO, final String TABLENAME2) {", primaryKeyType, tableUEn);
		sn(sb, "        if ( cacheModel == NO_CACHE) { ");
		sn(sb, "            return DAO.selectPKs(TABLENAME2);");
		sn(sb, "        } else {");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            result.addAll(vars.keySet());");
		sn(sb, "            return result;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		///
		
		sn(sb, "    public static List<%s> getInIndex() {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getInIndex(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getInIndex(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return getInIndex(DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getInIndex(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getInIndex(DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getInIndex(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        return DAO.selectInIndex(TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");
		
		if(batch){
		sn(sb, "    public static List<%s> getIn(List<%s> keys) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getIn(List<%s> keys, %sDAO DAO) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        return getIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getIn(List<%s> keys, String TABLENAME2) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getIn(keys, DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getIn(final List<%s> keys, final %sDAO DAO, final String TABLENAME2) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        if(keys == null || keys.isEmpty()) return newList();");
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectIn(keys, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> result = getNoSortIn(keys, DAO, TABLENAME2);", tableUEn);
		sn(sb, "            return sort(result);");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getNoSortIn(List<%s> keys) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getNoSortIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortIn(List<%s> keys, %sDAO DAO) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        return getNoSortIn(keys, DAO, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getNoSortIn(List<%s> keys, String TABLENAME2) {", tableUEn, primaryKeyType);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getNoSortIn(keys, DAO, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getNoSortIn(final List<%s> keys, final %sDAO DAO, final String TABLENAME2) {", tableUEn, primaryKeyType, tableUEn);
		sn(sb, "        if(keys == null || keys.isEmpty()) return newList();");
		sn(sb, "        List<%s> result = newList();", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectIn(keys, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            for (%s key : keys) {", pkBasicType);
		sn(sb, "                %s %s = getByKeyFromMemory(key);", tableUEn, tableEn);
		sn(sb, "                if( %s == null ) continue;", tableEn);
		sn(sb, "                result.add(%s);", tableEn);
		sn(sb, "            }");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		}
		
		sn(sb, "    public static List<%s> getLast(int num) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getLast(DAO, num, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getLast(%sDAO DAO, int num) {", tableUEn, tableUEn);
		sn(sb, "        return getLast(DAO, num, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getLast(int num, String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getLast(DAO, num, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getLast(final %sDAO DAO, final int num, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        List<%s> result = newList();", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectLast(num, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE or FULL_MEMORY");
		sn(sb, "            List<%s> mvars = getAll(DAO, TABLENAME2);", tableUEn);
		sn(sb, "            if( mvars.size() > num ){");
		sn(sb, "                result = mvars.subList(mvars.size() - num, mvars.size() - 1);");
		sn(sb, "            }");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static %s last() {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return last(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s last(%sDAO DAO) {", tableUEn, tableUEn);
		sn(sb, "        return last(DAO, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s last(String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return last(DAO, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s last(final %sDAO DAO, final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        %s result = null;", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.last(TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
		sn(sb, "            // result = getByKey(DAO, LASTID, TABLENAME2);");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");
		

		sn(sb, "    public static List<%s> getGtKey(%s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getGtKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(%sDAO DAO, %s %s) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return getGtKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(%s %s, String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getGtKey(DAO, %s, TABLENAME2);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getGtKey(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        List<%s> result = newList();", tableUEn);
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectGtKey(%s, TABLENAME2);", primaryKey);
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> %ss = getAll();", tableUEn, tableEn);
		sn(sb, "            for (%s %s : %ss) {", tableUEn, tableEn, tableEn);
		sn(sb, "                if(%s.%s.intValue() <= %s.intValue()) continue;", tableEn, primaryKey, primaryKey);
		sn(sb, "                result.add(%s);", tableEn);
		sn(sb, "            }");
		sn(sb, "        }");
		//sn(sb, "        sort(result);");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(%s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getByKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(%sDAO DAO, %s %s) {", tableUEn, tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return getByKey(DAO, %s, DAO.TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s getByKey(%s %s, String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
		sn(sb, "        return vars.get(%s);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s deleteFromMemory(final %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return vars.remove(%s);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public static List<%s> getByPage(int page, int size) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getByPage(DAO, page, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getByPage(%sDAO DAO, int page, int size) {", tableUEn, tableUEn);
		sn(sb, "        return getByPage(DAO, page, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getByPage(int page, int size, String TABLENAME2) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return getByPage(DAO, page, size, TABLENAME2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static List<%s> getByPage(final %sDAO DAO, final int page, final int size,final String TABLENAME2) {", tableUEn, tableUEn);
		sn(sb, "        List<%s> result = newList();", tableUEn);
		sn(sb, "        int begin = page * size;");
		sn(sb, "        int num = size;");
		sn(sb, "        if( cacheModel == NO_CACHE ){");
		sn(sb, "            return DAO.selectByPage(begin, num, TABLENAME2);");
		sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY ");
		sn(sb, "            List<%s> v = getAll(DAO, TABLENAME2);", tableUEn);
		sn(sb, "            result = SqlEx.getPage(v, page, size);");
		sn(sb, "        }");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		////
		sn(sb, "    public static int pageCount(int size) {", tableUEn);
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
		sn(sb, "        return pageCount(DAO, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int pageCount(%sDAO DAO, int size) {", tableUEn);
		sn(sb, "        return pageCount(DAO, size, DAO.TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static int pageCount(int size, String TABLENAME2) {");
		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
				if(COLUMN_NAME.equals(primaryKey))
					continue;
				if(COLUMN_NAME_TYPE.equals("java.util.Date"))
					continue;
				if(NON_UNIQUE.equals("0")){
					sn(sb, "    public static %s getBy%s(%s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%sDAO DAO, %s %s) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%s %s, String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%sDAO DAO, %s %s) {", COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%s %s, String TABLENAME2) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%sDAO DAO, %s %s) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%s %s, String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        List<%s> result = newList();", tableUEn);
					sn(sb, "        if( cacheModel == NO_CACHE ){");
					sn(sb, "            return DAO.selectBy%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "        } else { //FULL_CACHE || FULL_MEMORY {");
					sn(sb, "            Map m1 = varsBy%s.get(%s);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "            if (m1 == null || m1.isEmpty()) return result;");
					sn(sb, "            List<%s> list = newList();", primaryKeyType);
					sn(sb, "            list.addAll(m1.values());");
					sn(sb, "            for (%s key : list) {;", pkBasicType);
					sn(sb, "                %s e = getByKey(DAO, key, TABLENAME2);", tableUEn);
					sn(sb, "                if(e == null){");
					sn(sb, "                    m1.remove(key); ");
					sn(sb, "                    continue;");
					sn(sb, "                }");
					sn(sb, "                %s _%s = e.get%s();", COLUMN_NAME_BTYPE, COLUMN_NAME_EN, COLUMN_NAME_UEN);
					if(COLUMN_NAME_BTYPE.equals("int") || COLUMN_NAME_BTYPE.equals("long") || COLUMN_NAME_BTYPE.equals("boolean")){
						sn(sb, "                if(_%s != %s){ ", COLUMN_NAME_EN, COLUMN_NAME_EN);
					}else if(COLUMN_NAME_BTYPE.contains("BigDecimal")){
						//-1 小于 0 等于 1 大于
						sn(sb, "                if(_%s.compareTo(%s) != 0){ ", COLUMN_NAME_EN, COLUMN_NAME_EN);
					}else if(COLUMN_NAME_BTYPE.equals("String") || COLUMN_NAME_BTYPE.equals("java.util.Date")){
						sn(sb, "                if(!_%s.equals(%s)){ ", COLUMN_NAME_EN, COLUMN_NAME_EN);
					}
					sn(sb, "                    m1.remove(key);");
					sn(sb, "                    continue;");
					sn(sb, "                }");
					sn(sb, "                result.add(e);");
					sn(sb, "            }");
					
					sn(sb, "        }");

					sn(sb, "        sort(result);");
					sn(sb, "        return result;");
					sn(sb, "    }");
					sn(sb, "");
				} 
				
				// like 
				if(COLUMN_NAME_TYPE.equals("String")){
				sn(sb, "    public static int countLike%s(%s %s) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = DAO();", tableUEn);
				sn(sb, "        return countLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static int countLike%s(%sDAO DAO, %s %s) {", COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return countLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static int countLike%s(%s %s, String TABLENAME2) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
				sn(sb, "        %sDAO DAO = DAO();", tableUEn);
				sn(sb, "        return getLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static List<%s> getLike%s(%sDAO DAO, %s %s) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return getLike%s(DAO, %s, DAO.TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static List<%s> getLike%s(%s %s, String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        %sDAO DAO = DAO();", tableUEn);
				sn(sb, "        return getLike%s(DAO, %s, TABLENAME2);", COLUMN_NAME_UEN,  COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public static List<%s> getLike%s(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, tableUEn, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        List<%s> result = newList();", tableUEn);
				sn(sb, "        if(cacheModel == NO_CACHE){");
				sn(sb, "            return DAO.selectLike%s(%s, TABLENAME2);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
				sn(sb, "            List<%s> %ss = getAll(DAO, TABLENAME2);", tableUEn, tableEn);
				sn(sb, "            for(%s e : %ss){", tableUEn, tableEn);
				sn(sb, "                %s _var = e.get%s();", COLUMN_NAME_TYPE, COLUMN_NAME_UEN);
				sn(sb, "                if(_var.indexOf(%s) >= 0)", COLUMN_NAME_EN, primaryKey);
				sn(sb, "                    result.add(e);");
				sn(sb, "            }");
				sn(sb, "        } ");
//				sn(sb, "        sort(result);");
				sn(sb, "        return result;");
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
				if(NON_UNIQUE.equals("0")){ // 唯一数据
					sn(sb, "    public static %s getBy%s(%s) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%sDAO DAO, %s) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static %s getBy%s(%s, String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%sDAO DAO, %s) {", index1, tableUEn, index2);
					sn(sb, "        return countBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static int countBy%s(%s, String TABLENAME2) {", index1, index2);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
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
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%sDAO DAO, %s) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        return getBy%s(DAO, %s, DAO.TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(%s, String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        %sDAO DAO = DAO();", tableUEn);
					sn(sb, "        return getBy%s(DAO, %s, TABLENAME2);", index1,  index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public static List<%s> getBy%s(final %sDAO DAO, %s, final String TABLENAME2) {", tableUEn, index1, tableUEn, index2);
					sn(sb, "        List<%s> result = newList();", tableUEn);
					sn(sb, "        if( cacheModel == NO_CACHE ){");
					sn(sb, "            return DAO.selectBy%s(%s, TABLENAME2);", index1, index3);
					sn(sb, "        } else { // FULL_CACHE || FULL_MEMORY");
					sn(sb, "            String vkey = %s;", index5);
					sn(sb, "            Map m1 = varsBy%s.get(vkey);", index1);
					sn(sb, "            if (m1 == null || m1.isEmpty()) return result;");
					sn(sb, "            List<%s> list = newList();", primaryKeyType);
					sn(sb, "            list.addAll(m1.values());");
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
					sn(sb, "        } ");
					sn(sb, "        sort(result);");
					sn(sb, "        return result;");
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

//		sn(sb, "    public static %s update2(%s %s) {", tableUEn, tableUEn, tableEn);
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return update2(DAO, %s, DAO.TABLENAME);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static %s update2(%sDAO DAO, %s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
//		sn(sb, "        return update2(DAO, %s, DAO.TABLENAME);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static %s update2(%s %s, String TABLENAME2) {", tableUEn, tableUEn, tableEn);
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return update2(DAO, %s, TABLENAME2);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static %s update2(final %sDAO DAO, final %s %s,final String TABLENAME2) {", tableUEn, tableUEn, tableUEn, tableEn);
//		sn(sb, "        if(cacheModel != FULL_MEMORY){");
//		sn(sb, "            execute(new Runnable() {");
//		sn(sb, "                public void run() {");
//		sn(sb, "                    DAO.updateByKey(%s, TABLENAME2);", tableEn);
//		sn(sb, "                }");
//		sn(sb, "            });");
//		sn(sb, "        }");
//		sn(sb, "        if(cacheModel != NO_CACHE){");
//		sn(sb, "            put(%s);", tableEn);
//		sn(sb, "        }");
//		sn(sb, "        return %s;", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
		
//		sn(sb, "    public synchronized static void truncate(){");
//		sn(sb, "        clear();");
//		sn(sb, "        DAO().truncate();");
//		sn(sb, "        DAO().repair();");
//		sn(sb, "        DAO().optimize();");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    static Executor singlePool = null;");
//		sn(sb, "    public synchronized static final void executeSingle(Runnable r) {");
//		sn(sb, "        if( singlePool == null )");
//		sn(sb, "            singlePool = Executors.newSingleThreadExecutor();");
//		sn(sb, "        singlePool.execute(r);");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static int inMemFlush() {");
//		sn(sb, "        %sDAO DAO = DAO();", tableUEn);
//		sn(sb, "        return inMemFlush(DAO, DAO.TABLENAME);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static int inMemFlush(%sDAO DAO){", tableUEn);
//		sn(sb, "        return inMemFlush(DAO, DAO.TABLENAME);");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static int inMemFlush(String TABLENAME2) {", tableUEn);
//		sn(sb, "        return inMemFlush(DAO(), TABLENAME2);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public static int inMemFlush(final %sDAO DAO, final String TABLENAME2) {", tableUEn);
//		sn(sb, "        int result = 0;");
//		sn(sb, "        if(cacheModel != FULL_MEMORY)");
//		sn(sb, "            return result;");
//		sn(sb, "        try {");
//		sn(sb, "            DAO.truncate(TABLENAME2);");
//		sn(sb, "            DAO.repair(TABLENAME2);");
//		sn(sb, "            DAO.optimize(TABLENAME2);");
//		sn(sb, "        } catch (Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "");
//		sn(sb, "        List<%s> %ss = getAll();", tableUEn, tableEn);
//		sn(sb, "        for (%s %s : %ss) {", tableUEn, tableEn, tableEn);
//		sn(sb, "            int n = DAO.insert2(%s, TABLENAME2);", tableEn);
//		sn(sb, "            if (n > 0) result++;");
//		sn(sb, "        }");
//		sn(sb, "        return result;");
//		sn(sb, "    }");
//		sn(sb, "");
		
		sn(sb, "}");
		
		return sb.toString();
	}
	
	public static String Ss (String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
	}

}
