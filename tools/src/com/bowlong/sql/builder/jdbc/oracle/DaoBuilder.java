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

public class DaoBuilder extends Abstract {
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
		boolean batch = true;
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		String xml = build(conn, rs, bpackage, batch, db, TABLENAME);
		System.out.println(xml);

	}

	public static String build(Connection conn, ResultSet rs, String pkg, boolean batch, String db, String TABLENAME) throws Exception {
		TABLENAME = TABLENAME.replace("\"", "");
		StringBuffer sb = new StringBuffer();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		String catalogName = db; //(String) columns.get(0).get("catalogName");
		String table = TABLENAME.toUpperCase(); //(String) columns.get(0).get("tableName");
		String tableEn = PinYin.getShortPinYin(table);
		tableEn = lower(tableEn);
		String tableUEn = Ss(tableEn);
		Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn, table);
		String primaryKey = BeanBuilder.primaryKey(conn, table, columns);
		String primaryKeyType = JavaType.getType(rsmd, primaryKey);
		String pkBasicType = JavaType.getBasicType(primaryKeyType);
		String columns1 = BeanBuilder.columns1(rsmd, columns);
//		String columns2 = BeanBuilder.columns2(rsmd, columns);
		String columns3 = BeanBuilder.columns3(rsmd, columns);
		String columns4 = BeanBuilder.columns4(rsmd, columns);
		String columns5 = BeanBuilder.columns5(rsmd, columns);
		String columnsForInsert = BeanBuilder.columnsForInsert(rsmd, columns);
		//String columns6 = BeanBuilder.columns6(rsmd, columns);
//		String columns7 = BeanBuilder.columns7(rsmd, columns);
		String columns8 = BeanBuilder.columns8(rsmd, columns);
//		String columns9 = BeanBuilder.columns9(rsmd, columns);
		
//		String createSql = SqlEx.createMysqlTable(conn, rs, table);
//		String createNoUniqueSql = SqlEx.createMysqlNoUniqueTable(conn, rs, table);
		sn(sb, "package %s.dao;", pkg);
		sn(sb, "");
		sn(sb, "import org.apache.commons.logging.*;");
		sn(sb, "");
		sn(sb, "import java.util.*;");
		//if(batch){ // 批处理
		//sn(sb, "import java.sql.*;");
		//}
		sn(sb, "import %s.bean.*;", pkg);
		sn(sb, "");
		sn(sb, "//%s - %s", catalogName, table);
		sn(sb, "@SuppressWarnings({\"rawtypes\", \"unchecked\"})");
		sn(sb, "public class %sDAO extends com.bowlong.sql.oracle.JdbcTemplate {", tableUEn);
		sn(sb, "    static Log log = LogFactory.getLog(%sDAO.class);", tableUEn);
		sn(sb, "");
		sn(sb, "    public static final String TABLE = \"%s\";", table);
		sn(sb, "    public static String TABLENAME = \"%s\";", table);
		sn(sb, "");
		sn(sb, "    public static String TABLEYY() {");
		sn(sb, "        return TABLE + sdfYy.format(new Date());");
		sn(sb, "    }");
		sn(sb, "");
		sn(sb, "    public static String TABLEMM() {");
		sn(sb, "        return TABLE + sdfMm.format(new Date());");
		sn(sb, "    }");
		sn(sb, "");
		sn(sb, "    public static String TABLEDD() {");
		sn(sb, "        return TABLE + sdfDd.format(new Date());");
		sn(sb, "    }");
		sn(sb, "");
		sn(sb, "    public static String[] carrays ={%s};", columns1);
		sn(sb, "    public static String coulmns = \"%s\";", columns3);
		sn(sb, "    public static String coulmns2 = \"%s\";", columns4);
		sn(sb, "");

		sn(sb, "    public %sDAO(java.sql.Connection conn) {", tableUEn);
		sn(sb, "        super(conn);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %sDAO(javax.sql.DataSource ds) {", tableUEn);
		sn(sb, "        super(ds);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %sDAO(javax.sql.DataSource ds_r, javax.sql.DataSource ds_w) {", tableUEn);
		sn(sb, "        super(ds_r, ds_w);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int insert(final %s %s) {", tableUEn, tableEn);
		sn(sb, "        return insert(%s, TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int insert(final %s %s, final String TABLENAME2) {", tableUEn, tableEn);
		sn(sb, "        try {");
		sn(sb, "            String sql = \"INSERT INTO \" + TABLENAME2 + \" (%s) VALUES (%s)\";", columns4, columnsForInsert);
		sn(sb, "            String sqlId = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE ROWID=?\";", primaryKey);
		sn(sb, "            Map map = super.insert(sql, sqlId, %s);", tableEn);
		sn(sb, "            return getInt(map, \"%s\");", primaryKey);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return 0;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

//		sn(sb, "    public int insert2(final %s %s) {", tableUEn, tableEn);
//		sn(sb, "        return insert2(%s, TABLENAME);", tableEn);
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public int insert2(final %s %s, final String TABLENAME2) {", tableUEn, tableEn);
//		sn(sb, "        try{");
//		sn(sb, "            String sql = \"INSERT INTO \" + TABLENAME2 + \" (%s) VALUES (%s)\";", columns3, columns6);
//		sn(sb, "            String sqlId = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE ROWID=?\";", primaryKey);
//		sn(sb, "            Map map = super.insert(sql, sqlId,  %s);", tableEn);
//		sn(sb, "            return getInt(map, \"%s\");", primaryKey);
//		sn(sb, "        } catch(Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "            return 0;");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "");
		
		if(batch){ // 批处理
		sn(sb, "    public int[] insert(final List<%s> %ss) {", tableUEn, tableEn);
		sn(sb, "        return insert(%ss, TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int[] insert(final List<%s> %ss, final String TABLENAME2) {", tableUEn, tableEn);
		sn(sb, "        try {");
		sn(sb, "            if(%ss == null || %ss.isEmpty()) return new int[0];", tableEn, tableEn);
		sn(sb, "            String sql = \"INSERT INTO \" + TABLENAME2 + \" (%s) VALUES (%s)\";", columns4, columns5);
		sn(sb, "            return super.batchInsert(sql, %ss);", tableEn);
		sn(sb, "         } catch (Exception e) {");
		sn(sb, "             log.info(e2s(e));");
		sn(sb, "             return new int[0];");
		sn(sb, "         }");
		sn(sb, "    }");
		sn(sb, "");
		}
		
		sn(sb, "    public int deleteByKey(final %s %s) {", pkBasicType, primaryKey);
		sn(sb, "        return deleteByKey(%s, TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int deleteByKey(final %s %s, final String TABLENAME2) {", pkBasicType, primaryKey);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"DELETE FROM \" + TABLENAME2 + \" WHERE %s=:%s\";", primaryKey, primaryKey);
		sn(sb, "            Map params = newMap();");
		sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
		sn(sb, "            return super.update(sql, params);");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return 0;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		if(batch){ // 批处理
		sn(sb, "    public int[] deleteByKey(final %s[] %ss) {", pkBasicType, primaryKey);
		sn(sb, "        return deleteByKey(%ss, TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int[] deleteByKey(final %s[] keys, final String TABLENAME2) {", pkBasicType);
		sn(sb, "        try{");
		sn(sb, "            if(keys == null || keys.length <= 0) return new int[0];");
		sn(sb, "            String sql = \"DELETE FROM \" + TABLENAME2 + \" WHERE %s=:%s\";", primaryKey, primaryKey);
		sn(sb, "            List list = newList();");
		sn(sb, "            for (%s %s : keys) {", pkBasicType, primaryKey);
		sn(sb, "                Map params = newMap();");
		sn(sb, "                params.put(\"%s\", %s);", primaryKey, primaryKey);
		sn(sb, "                list.add(params);");
		sn(sb, "            }");
		sn(sb, "            return super.batchUpdate(sql, list);");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return new int[0];");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		}

		if(batch){
		sn(sb, "    public int deleteInKeys(final List<%s> keys) {", primaryKeyType);
		sn(sb, "        return deleteInKeys(keys, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int deleteInKeys(final List<%s> keys, final String TABLENAME2) {", primaryKeyType);
		sn(sb, "        try{");
		sn(sb, "            if(keys == null || keys.isEmpty()) return 0;");
		sn(sb, "            StringBuffer sb = new StringBuffer();");
		sn(sb, "            int size = keys.size();");
		sn(sb, "            for (int i = 0; i < size; i ++) {");
		sn(sb, "                sb.append(keys.get(i));");
		sn(sb, "                if(i < size - 1)");
		sn(sb, "                    sb.append(\", \");");
		sn(sb, "            }");
		sn(sb, "            String str = sb.toString();");
		sn(sb, "            String sql = \"DELETE FROM \" + TABLENAME2 + \" WHERE %s in (\" + str + \" ) \";", primaryKey);
		sn(sb, "            return super.update(sql);");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return 0;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		}
		
		sn(sb, "    public List<%s> selectAll() {", tableUEn);
		sn(sb, "        return selectAll(TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectAll(final String TABLENAME2) {", tableUEn);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" ORDER BY %s\";", columns3, primaryKey);
		sn(sb, "            return super.queryForList(sql, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectPKs() {", primaryKeyType);
		sn(sb, "        return selectPKs(TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectPKs(final String TABLENAME2) {", primaryKeyType);
		sn(sb, "        try{");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" ORDER BY %s\";", primaryKey , primaryKey);
		sn(sb, "            List<Map> dbresult = super.queryForList(sql);");
		sn(sb, "            for(Map map : dbresult){");
		sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		String ics = BeanBuilder.indexCoulmns(columns, indexs);
		if(ics != null && ics.length() >= 1){
			sn(sb, "    public List<%s> selectInIndex() {", tableUEn);
			sn(sb, "        return selectInIndex(TABLENAME);");
			sn(sb, "    }");
			sn(sb, "");

			sn(sb, "    public List<%s> selectInIndex(final String TABLENAME2) {", tableUEn);
			sn(sb, "        try{");
			sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" ORDER BY %s\";", ics, primaryKey);
			sn(sb, "            return super.queryForList(sql, %s.class);", tableUEn);
			sn(sb, "        } catch(Exception e) {");
			sn(sb, "            log.info(e2s(e));");
			sn(sb, "            return newList();");
			sn(sb, "        }");
			sn(sb, "    }");
			sn(sb, "");

		}
		
		if(batch){
		sn(sb, "    public List<%s> selectIn(final List<%s> keys) {", tableUEn, primaryKeyType);
		sn(sb, "        return selectIn(keys, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectIn(final List<%s> keys, final String TABLENAME2) {", tableUEn, primaryKeyType);
		sn(sb, "        try{");
		sn(sb, "            if(keys == null || keys.isEmpty()) return newList();");
		sn(sb, "            StringBuffer sb = new StringBuffer();");
		sn(sb, "            int size = keys.size();");
		sn(sb, "            for (int i = 0; i < size; i ++) {");
		sn(sb, "                sb.append(keys.get(i));");
		sn(sb, "                if(i < size - 1)");
		sn(sb, "                    sb.append(\", \");");
		sn(sb, "            }");
		sn(sb, "            String str = sb.toString();");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s in (\" + str + \" ) ORDER BY %s\";", columns3, primaryKey, primaryKey);
		sn(sb, "            return super.queryForList(sql, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		}

		if(batch){
		sn(sb, "    public List<%s> selectInPKs(final List<%s> keys) {", primaryKeyType, primaryKeyType);
		sn(sb, "        return selectInPKs(keys, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectInPKs(final List<%s> keys, final String TABLENAME2) {", primaryKeyType, primaryKeyType);
		sn(sb, "        try{");
		sn(sb, "            if(keys == null || keys.isEmpty()) return newList();");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            StringBuffer sb = new StringBuffer();");
		sn(sb, "            int size = keys.size();");
		sn(sb, "            for (int i = 0; i < size; i ++) {");
		sn(sb, "                sb.append(keys.get(i));");
		sn(sb, "                if(i < size - 1)");
		sn(sb, "                    sb.append(\", \");");
		sn(sb, "            }");
		sn(sb, "            String str = sb.toString();");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s in (\" + str + \" ) ORDER BY %s\";", primaryKey, primaryKey, primaryKey);
		sn(sb, "            List<Map> dbresult = super.queryForList(sql);");
		sn(sb, "            for(Map map : dbresult){");
		sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		}
		
		sn(sb, "    public List<%s> selectLast(final int num) {", tableUEn);
		sn(sb, "        return selectLast(num, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectLast(final int num, final String TABLENAME2) {", tableUEn);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE ROWNUM < \" + num + \" ORDER BY %s DESC \";", columns3, primaryKey);
		sn(sb, "            return super.queryForList(sql, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectLastPKs(final int num) {", primaryKeyType);
		sn(sb, "        return selectLastPKs(num, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectLastPKs(final int num, final String TABLENAME2) {", primaryKeyType);
		sn(sb, "        try{");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" ROWNUM < \" + num + \" ORDER BY %s DESC \";", primaryKey, primaryKey);
		sn(sb, "            List<Map> dbresult = super.queryForList(sql);");
		sn(sb, "            for(Map map : dbresult){");
		sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public %s last() {", tableUEn);
		sn(sb, "        return last(TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s last(final String TABLENAME2) {", tableUEn);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE ROWNUM < 1 ORDER BY %s DESC \";", columns3, primaryKey);
		sn(sb, "            return super.queryForObject(sql, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            // log.info(e2s(e));");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectGtKeyNum(final %s %s, final int _num) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return selectGtKeyNum(%s, _num, TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectGtKeyNum(final %s %s, final int _num, final String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s > :%s AND ROWNUM < \" + _num + \"ORDER BY %s \";", columns3, primaryKey, primaryKey, primaryKey);
		sn(sb, "            Map params = newMap();");
		sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
		sn(sb, "            return super.queryForList(sql, params, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public List<%s> selectGtKey(final %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return selectGtKey(%s, TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectGtKey(final %s %s, final String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s > :%s ORDER BY %s\";", columns3, primaryKey, primaryKey, primaryKey);
		sn(sb, "            Map params = newMap();");
		sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
		sn(sb, "            return super.queryForList(sql, params, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectGtKeyPKs(final %s %s) {", primaryKeyType, pkBasicType, primaryKey);
		sn(sb, "        return selectGtKeyPKs(%s, TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectGtKeyPKs(final %s %s, final String TABLENAME2) {", primaryKeyType, pkBasicType, primaryKey);
		sn(sb, "        try{");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s > :%s ORDER BY %s\";", primaryKey, primaryKey, primaryKey, primaryKey);
		sn(sb, "            Map params = newMap();");
		sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
		sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
		sn(sb, "            for(Map map : dbresult){");
		sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public %s selectByKey(final %s %s) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return selectByKey(%s, TABLENAME);", primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s selectByKey(final %s %s, final String TABLENAME2) {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s = :%s\";", columns3, primaryKey, primaryKey);
		sn(sb, "            Map params = newMap();");
		sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
		sn(sb, "            return super.queryForObject(sql, params, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            // log.info(e2s(e));");
		sn(sb, "            return null;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				//String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				if(COLUMN_NAME == null) continue;
				
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
//				if(INDEX_NAME.equals("PRIMARY"))
//					continue;
				if(NON_UNIQUE.equals("0")){
					sn(sb, "    public %s selectBy%s(final %s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return selectBy%s(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public %s selectBy%s(final %s %s, final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s = :%s\";", columns3, COLUMN_NAME, COLUMN_NAME);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					sn(sb, "            return super.queryForObject(sql, params, %s.class);", tableUEn);
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            // log.info(e2s(e));");
					sn(sb, "            return null;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

				}else{
					sn(sb, "    public int countBy%s(final %s %s) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return countBy%s(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int countBy%s(final %s %s, final String TABLENAME2) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT COUNT(*) FROM \" + TABLENAME2 + \" WHERE %s = :%s \";", COLUMN_NAME, COLUMN_NAME);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					sn(sb, "            return super.queryForInt(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					
					sn(sb, "    public List<%s> selectBy%s(final %s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return selectBy%s(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> selectBy%s(final %s %s, final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s = :%s ORDER BY %s \";", columns3, COLUMN_NAME, COLUMN_NAME, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					sn(sb, "            return super.queryForList(sql, params, %s.class);", tableUEn);
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> selectBy%sPKs(final %s %s) {", primaryKeyType, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return selectBy%sPKs(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> selectBy%sPKs(final %s %s, final String TABLENAME2) {", primaryKeyType, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        try{");
					sn(sb, "            List<%s> result = newList();", primaryKeyType);
					sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s = :%s ORDER BY %s \";", primaryKey, COLUMN_NAME, COLUMN_NAME, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
					sn(sb, "            for(Map map : dbresult){");
					sn(sb, "                result.add((%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
					sn(sb, "            }");
					sn(sb, "            return result;");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
				
					sn(sb, "    public List<%s> selectPageBy%s(final %s %s, final int begin, final int num) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return selectPageBy%s(%s, begin, num, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					// SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a WHERE ROMNUM < m + n AND 条件 ORDER BY id ) b WHERE b.r > m
					sn(sb, "    public List<%s> selectPageBy%s(final %s %s, final int begin, final int num, final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM ( SELECT ROWNUM r, a.* FROM \" + TABLENAME2 + \" a WHERE %s = :%s ORDER BY %s) b WHERE b.r >= \" + begin + \" AND b.r < \" + (begin + num) + \"\";", columns3, COLUMN_NAME, COLUMN_NAME, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					sn(sb, "            return super.queryForList(sql, params, %s.class);", tableUEn);
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					
					sn(sb, "    public List<%s> selectPageBy%sPKs(final %s %s, final int begin, final int num) {", primaryKeyType, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return selectPageBy%sPKs(%s, begin, num, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					// SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a WHERE ROMNUM < m + n AND 条件 ORDER BY id ) b WHERE b.r > m
					sn(sb, "    public List<%s> selectPageBy%sPKs(final %s %s, final int begin, final int num, final String TABLENAME2) {", primaryKeyType, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        try{");
					sn(sb, "            List<%s> result = newList();", primaryKeyType);
					sn(sb, "            String sql=\"SELECT %s FROM ( SELECT ROWNUM r, a.* \" + TABLENAME2 + \" a WHERE ROWNUM < \" + (begin + num) + \" AND %s = :%s ORDER BY %s) b WHERE b.r > \" + begin + \"\";", primaryKey, COLUMN_NAME, COLUMN_NAME, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
					sn(sb, "            for(Map map : dbresult){");
					sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
					sn(sb, "            }");
					sn(sb, "            return result;");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
				}
				
				// 如果类型是字符串则参与LIKE查询
				if(COLUMN_NAME_TYPE.equals("String")){
				sn(sb, "    public int countLike%s(final %s %s) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return countLike%s(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public int countLike%s(final %s %s, final String TABLENAME2) {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        try{");
				sn(sb, "            String sql=\"SELECT COUNT(*) FROM \" + TABLENAME2 + \" WHERE %s LIKE '%%\" + "+COLUMN_NAME_EN+" + \"%%' \";", COLUMN_NAME);
				sn(sb, "            return super.queryForInt(sql);");
				sn(sb, "        } catch(Exception e) {");
				sn(sb, "            log.info(e2s(e));");
				sn(sb, "            return 0;");
				sn(sb, "        }");
				sn(sb, "    }");
				sn(sb, "");
					
				sn(sb, "    public List<%s> selectLike%s(final %s %s) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return selectLike%s(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public List<%s> selectLike%s(final %s %s, final String TABLENAME2) {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        try{");
				sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s LIKE '%%\" + "+COLUMN_NAME_EN+" + \"%%' ORDER BY %s \";", columns3, COLUMN_NAME, primaryKey);
				sn(sb, "            return super.queryForList(sql, %s.class);", tableUEn);
				sn(sb, "        } catch(Exception e) {");
				sn(sb, "            log.info(e2s(e));");
				sn(sb, "            return newList();");
				sn(sb, "        }");
				sn(sb, "    }");
				sn(sb, "");
				
				sn(sb, "    public List<%s> selectLike%sPKs(final %s %s) {", primaryKeyType, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return selectLike%sPKs(%s, TABLENAME);", COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public List<%s> selectLike%sPKs(final %s %s, final String TABLENAME2) {", primaryKeyType, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        try{");
				sn(sb, "            List<%s> result = newList();", primaryKeyType);
				sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s LIKE '%%\" + "+COLUMN_NAME_EN+" + \"%%' ORDER BY %s \";", primaryKey, COLUMN_NAME, primaryKey);
				sn(sb, "            Map params = newMap();");
				sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
				sn(sb, "            for(Map map : dbresult){");
				sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
				sn(sb, "            }");
				sn(sb, "            return result;");
				sn(sb, "        } catch(Exception e) {");
				sn(sb, "            log.info(e2s(e));");
				sn(sb, "            return newList();");
				sn(sb, "        }");
				sn(sb, "    }");
				sn(sb, "");

				}
				///////////////////////////////
			}else { // 多键索引
				Map<String, Object> index = idx.get(0);
				String index1 = BeanBuilder.index1(rsmd, idx);
				String index2 = BeanBuilder.index2(rsmd, idx);
				String index3 = BeanBuilder.index3(rsmd, idx);
				String index4 = BeanBuilder.index4(rsmd, idx);
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				if(NON_UNIQUE.equals("0")){ // 唯一数据
					sn(sb, "    public %s selectBy%s(final %s) {", tableUEn, index1, index2);
					sn(sb, "        return selectBy%s(%s, TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public %s selectBy%s(final %s, final String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s\";", columns3, index4);
					sn(sb, "            Map params = newMap();");
					for (Map<String, Object> m : idx) {
						//String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
						String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						// String COLUMN_NAME_UEN =
						// StrEx.upperFirst(COLUMN_NAME_EN);
						// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					}
					sn(sb, "            return super.queryForObject(sql, params, %s.class);", tableUEn);
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            // log.info(e2s(e));");
					sn(sb, "            return null;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
				}else{ // 非唯一数据
					sn(sb, "    public int countBy%s(final %s) {", index1, index2);
					sn(sb, "        return  countBy%s(%s, TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int countBy%s(final %s, final String TABLENAME2) {", index1, index2);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT COUNT(*) FROM \" + TABLENAME2 + \" WHERE %s \";", index4);
					sn(sb, "            Map params = newMap();");
					for (Map<String, Object> m : idx) {
						//String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
						String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						// String COLUMN_NAME_UEN =
						// StrEx.upperFirst(COLUMN_NAME_EN);
						// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					}
					sn(sb, "            return super.queryForInt(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					
					
					sn(sb, "    public List<%s> selectBy%s(final %s) {", tableUEn, index1, index2);
					sn(sb, "        return selectBy%s(%s, TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> selectBy%s(final %s, final String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s ORDER BY %s \";", columns3, index4, primaryKey);
					sn(sb, "            Map params = newMap();");
					for (Map<String, Object> m : idx) {
						//String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
						String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						// String COLUMN_NAME_UEN =
						// StrEx.upperFirst(COLUMN_NAME_EN);
						// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					}
					sn(sb, "            return super.queryForList(sql, params, %s.class);", tableUEn);
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					
					
					sn(sb, "    public List<%s> selectBy%sPKs(final %s) {", primaryKeyType, index1, index2);
					sn(sb, "        return selectBy%sPKs(%s, TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> selectBy%sPKs(final %s, final String TABLENAME2) {", primaryKeyType, index1, index2);
					sn(sb, "        List<%s> result = newList();", primaryKeyType);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM \" + TABLENAME2 + \" WHERE %s ORDER BY %s \";", primaryKey, index4, primaryKey);
					sn(sb, "            Map params = newMap();");
					for (Map<String, Object> m : idx) {
						//String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
						String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						// String COLUMN_NAME_UEN =
						// StrEx.upperFirst(COLUMN_NAME_EN);
						// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					}
					sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
					sn(sb, "            for(Map map : dbresult){");
					sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
					sn(sb, "            }");
					sn(sb, "            return result;");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> selectPageBy%s(final %s, final int begin, final int num) {", tableUEn, index1, index2);
					sn(sb, "        return selectPageBy%s(%s, begin, num, TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					// SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a WHERE ROMNUM < m + n AND 条件 ORDER BY id ) b WHERE b.r > m
					sn(sb, "    public List<%s> selectPageBy%s(final %s, final int begin, final int num, final String TABLENAME2) {", tableUEn, index1, index2);
					sn(sb, "        try{");
					sn(sb, "            String sql=\"SELECT %s FROM ( SELECT ROWNUM r, a.* FROM \" + TABLENAME2 + \" a WHERE ROWNUM < \" + ( begin + num )+ \" AND %s ORDER BY %s ) b WHERE b.r > \" + begin + \"\";", columns3, index4, primaryKey);
					sn(sb, "            Map params = newMap();");
					for (Map<String, Object> m : idx) {
						//String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
						String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						// String COLUMN_NAME_UEN =
						// StrEx.upperFirst(COLUMN_NAME_EN);
						// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					}
					sn(sb, "            return super.queryForList(sql, params, %s.class);", tableUEn);
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					
					sn(sb, "    public List<%s> selectPageBy%sPKs(final %s, final int begin, final int num) {", primaryKeyType, index1, index2);
					sn(sb, "        return selectPageBy%sPKs(%s, begin, num, TABLENAME);", index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					// SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a WHERE ROMNUM < m + n AND 条件 ORDER BY id ) b WHERE b.r > m
					sn(sb, "    public List<%s> selectPageBy%sPKs(final %s, final int begin, final int num, final String TABLENAME2) {", primaryKeyType, index1, index2);
					sn(sb, "        try{");
					sn(sb, "            List<%s> result = newList();", primaryKeyType);
					sn(sb, "            String sql=\"SELECT %s FROM ( SELECT ROWNUM r, a.* FROM \" + TABLENAME2 + \" a WHERE ROWNUM < \" + ( begin + num ) + \"%s ORDER BY %s ) WHERE b.r >  \" + begin + \"\";", primaryKey, index4, primaryKey);
					sn(sb, "            Map params = newMap();");
					for (Map<String, Object> m : idx) {
						//String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
						String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
						String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
						// String COLUMN_NAME_UEN =
						// StrEx.upperFirst(COLUMN_NAME_EN);
						// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
						sn(sb, "            params.put(\"%s\", %s);", COLUMN_NAME, COLUMN_NAME_EN);
					}
					sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
					sn(sb, "            for(Map map : dbresult){");
					sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
					sn(sb, "            }");
					sn(sb, "            return result;");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return newList();");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
				}
			}
			
		}
		
		sn(sb, "    public int count() {");
		sn(sb, "        return count(TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int count(final String TABLENAME2) {");
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT COUNT(*) FROM \" + TABLENAME2 + \"\";");
		sn(sb, "            return super.queryForInt(sql);");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return 0;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public List<%s> selectByPage(final int begin, final int num) {", tableUEn);
		sn(sb, "        return selectByPage(begin, num, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		// SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a WHERE ROMNUM < m + n AND 条件 ORDER BY id ) b WHERE b.r > m
		sn(sb, "    public List<%s> selectByPage(final int begin, final int num, final String TABLENAME2) {", tableUEn);
		sn(sb, "        try{");
		sn(sb, "            String sql = \"SELECT %s FROM ( SELECT ROWNUM r, a.* FROM \" + TABLENAME2 + \" a ORDER BY %s ) b WHERE b.r >= \" + begin + \" AND b.r < \" + ( begin + num )+ \"\";", columns3, primaryKey);
		sn(sb, "            return super.queryForList(sql, %s.class);", tableUEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> selectByPagePKs(final int begin, final int num) {", primaryKeyType);
		sn(sb, "        return selectByPagePKs(begin, num, TABLENAME);");
		sn(sb, "    }");
		sn(sb, "");

		// SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a WHERE ROMNUM < m + n AND 条件 ORDER BY id ) b WHERE b.r > m
		sn(sb, "    public List<%s> selectByPagePKs(final int begin, final int num, final String TABLENAME2) {", primaryKeyType);
		sn(sb, "        try{");
		sn(sb, "            List<%s> result = newList();", primaryKeyType);
		sn(sb, "            String sql = \"SELECT %s FROM ( SELECT ROWNUM r, a.* FROM \" + TABLENAME2 + \" a ORDER BY %s ) b WHERE b.r >= \" + begin + \" AND b.r < \" + ( begin + num )+ \"\";", primaryKey, primaryKey);
		sn(sb, "            Map params = new Hashtable();");
		sn(sb, "            List<Map> dbresult = super.queryForList(sql, params);");
		sn(sb, "            for(Map map : dbresult){");
		sn(sb, "                result.add( (%s)map.get(\"%s\") );", primaryKeyType, primaryKey);
		sn(sb, "            }");
		sn(sb, "            return result;");
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return newList();");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int updateByKey(final %s %s) {", tableUEn, tableEn);
		sn(sb, "        return updateByKey(%s, TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int updateByKey(final %s %s, final String TABLENAME2) {", tableUEn, tableEn);
		sn(sb, "        try{");
		sn(sb, "            String _ustr = %s.ustr();", tableEn);
		sn(sb, "            if( _ustr.length() <= 0 )");
		sn(sb, "                return -1;");
		sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET \" + _ustr + \" WHERE %s=:%s\";", primaryKey, primaryKey);
		sn(sb, "            return super.update(sql, %s);", tableEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return 0;");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");

		
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			//String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String bType = JavaType.getBasicType(javaType);
			if(!column.equals(primaryKey)){
			if( bType.contains("short") ||
					bType.contains("int") ||
					bType.contains("long") || 
					bType.contains("float") ||
					bType.contains("double")){
					sn(sb, "    public int update%sByKey(final %s %s, final %s %s){", columnUEn, bType, columnEn, pkBasicType, primaryKey);
					sn(sb, "        return update%sByKey(%s, %s, TABLENAME);", columnUEn, columnEn, primaryKey);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sByKey(final %s %s, final %s %s, final String TABLENAME2) {", columnUEn, bType, columnEn, pkBasicType,  primaryKey);
					sn(sb, "        try{");
					sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET %s=%s+:%s WHERE %s=:%s\";", column, column, column, primaryKey, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
					sn(sb, "            params.put(\"%s\", %s);", column, columnEn);
					sn(sb, "            return super.update(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMinByKey(final %s %s, final %s %s, final %s _min){", columnUEn, pkBasicType, primaryKey, bType, columnEn, bType);
					sn(sb, "        return update%sWithMinByKey(%s, %s, _min, TABLENAME);", columnUEn, primaryKey, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMinByKey(final %s %s, final %s %s, final %s _min, final String TABLENAME2) {", columnUEn, pkBasicType,  primaryKey, bType, columnEn, bType);
					sn(sb, "        try{");
					sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET %s = (select case when %s+:%s<=:_min then :_min else %s+:%s end) WHERE %s=:%s\";", column, column, column, column, column, primaryKey, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
					sn(sb, "            params.put(\"_min\", _min);");
					sn(sb, "            params.put(\"%s\", %s);", column, columnEn);
					sn(sb, "            return super.update(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					if(batch){
					sn(sb, "    public int update%sWithMinInKeys(final List<%s> keys, final %s %s, final %s _min){", columnUEn, primaryKeyType, bType, columnEn, bType);
					sn(sb, "        return update%sWithMinInKeys(keys, %s, _min, TABLENAME);", columnUEn, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMinInKeys(final List<%s> keys, final %s %s, final %s _min, final String TABLENAME2) {", columnUEn, primaryKeyType,  bType, columnEn, bType);
					sn(sb, "        try{");
					sn(sb, "            if(keys == null || keys.isEmpty()) return 0;");
					sn(sb, "            StringBuffer sb = new StringBuffer();");
					sn(sb, "            int size = keys.size();");
					sn(sb, "            for (int i = 0; i < size; i ++) {");
					sn(sb, "                sb.append(keys.get(i));");
					sn(sb, "                if(i < size - 1)");
					sn(sb, "                    sb.append(\", \");");
					sn(sb, "            }");
					sn(sb, "            String str = sb.toString();");
					sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET %s = (select case when %s+:%s<=:_min then :_min else %s+:%s end) WHERE %s in (\" + str + \")\";", column, column, column, column, column, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"_min\", _min);");
					sn(sb, "            params.put(\"%s\", %s);", column, columnEn);
					sn(sb, "            return super.update(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					}
					
					sn(sb, "    public int update%sWithMaxByKey(final %s %s, final %s %s, final %s _max){", columnUEn, pkBasicType, primaryKey, bType, columnEn, bType);
					sn(sb, "        return update%sWithMaxByKey(%s, %s, _max, TABLENAME);", columnUEn, primaryKey, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMaxByKey(final %s %s, final %s %s, final %s _max, final String TABLENAME2) {", columnUEn, pkBasicType,  primaryKey, bType, columnEn, bType);
					sn(sb, "        try{");
					sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET %s = (select case when %s+:%s>=:_max then :_max else %s+:%s end) WHERE %s=:%s\";", column, column, column, column, column, primaryKey, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"%s\", %s);", primaryKey, primaryKey);
					sn(sb, "            params.put(\"_max\", _max);");
					sn(sb, "            params.put(\"%s\", %s);", column, columnEn);
					sn(sb, "            return super.update(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					if(batch){
					sn(sb, "    public int update%sWithMaxInKeys(final List<%s> keys, final %s %s, final %s _max){", columnUEn, primaryKeyType, bType, columnEn, bType);
					sn(sb, "        return update%sWithMaxInKeys(keys, %s, _max, TABLENAME);", columnUEn, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMaxInKeys(final List<%s> keys, final %s %s, final %s _max, final String TABLENAME2) {", columnUEn, primaryKeyType,  bType, columnEn, bType);
					sn(sb, "        try{");
					sn(sb, "            if(keys == null || keys.isEmpty()) return 0;");
					sn(sb, "            StringBuffer sb = new StringBuffer();");
					sn(sb, "            int size = keys.size();");
					sn(sb, "            for (int i = 0; i < size; i ++) {");
					sn(sb, "                sb.append(keys.get(i));");
					sn(sb, "                if(i < size - 1)");
					sn(sb, "                    sb.append(\", \");");
					sn(sb, "            }");
					sn(sb, "            String str = sb.toString();");
					sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET %s = (select case when %s+:%s>=:_max then :_max else %s+:%s end) WHERE %s in (\" + str + \")\";", column, column, column, column, column, primaryKey);
					sn(sb, "            Map params = newMap();");
					sn(sb, "            params.put(\"_max\", _max);");
					sn(sb, "            params.put(\"%s\", %s);", column, columnEn);
					sn(sb, "            return super.update(sql, params);");
					sn(sb, "        } catch(Exception e) {");
					sn(sb, "            log.info(e2s(e));");
					sn(sb, "            return 0;");
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					}
					
					
					sn(sb, "    public int update%sWithMinMaxByKey(final %s %s, final %s %s, final %s _min, final %s _max){", columnUEn, pkBasicType, primaryKey, bType, columnEn, bType, bType);
					sn(sb, "        return update%sWithMinMaxByKey(%s, %s, _min, _max, TABLENAME);", columnUEn, primaryKey, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMinMaxByKey(final %s %s, final %s %s, final %s _min, final %s _max, final String TABLENAME2){", columnUEn, pkBasicType, primaryKey, bType, columnEn, bType, bType);
					sn(sb, "        if( %s < 0 ) {", columnEn);
					sn(sb, "            return update%sWithMinByKey(%s, %s, _min, TABLENAME2);", columnUEn, primaryKey, columnEn);
					sn(sb, "        } else {");
					sn(sb, "            return update%sWithMaxByKey(%s, %s, _max, TABLENAME2);", columnUEn, primaryKey, columnEn);
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");

					if(batch){
					sn(sb, "    public int update%sWithMinMaxInKeys(final List<%s> keys, final %s %s, final %s _min, final %s _max){", columnUEn, primaryKeyType, bType, columnEn, bType, bType);
					sn(sb, "        return update%sWithMinMaxInKeys(keys, %s, _min, _max, TABLENAME);", columnUEn, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int update%sWithMinMaxInKeys(final List<%s> keys, final %s %s, final %s _min, final %s _max, final String TABLENAME2){", columnUEn, primaryKeyType, bType, columnEn, bType, bType);
					sn(sb, "        if( %s < 0 ) {", columnEn);
					sn(sb, "            return update%sWithMinInKeys(keys, %s, _min, TABLENAME2);", columnUEn, columnEn);
					sn(sb, "        } else {");
					sn(sb, "            return update%sWithMaxInKeys(keys, %s, _max, TABLENAME2);", columnUEn, columnEn);
					sn(sb, "        }");
					sn(sb, "    }");
					sn(sb, "");
					}

			}
			}

		}
		
		if(batch){ // 批处理
		sn(sb, "    public int[] updateByKey (final List<%s> %ss) {", tableUEn, tableEn);
		sn(sb, "        return updateByKey(%ss, TABLENAME);", tableEn);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public int[] updateByKey (final List<%s> %ss, final String TABLENAME2) {", tableUEn, tableEn);
		sn(sb, "        try{");
		sn(sb, "            if(%ss == null || %ss.isEmpty()) return new int[0];", tableEn, tableEn);
		sn(sb, "            String sql = \"UPDATE \" + TABLENAME2 + \" SET %s WHERE %s=:%s\";", columns8, primaryKey, primaryKey);
		sn(sb, "            return super.batchUpdate2(sql, %ss);", tableEn);
		sn(sb, "        } catch(Exception e) {");
		sn(sb, "            log.info(e2s(e));");
		sn(sb, "            return new int[0];");
		sn(sb, "        }");
		sn(sb, "    }");
		sn(sb, "");
		}
		
//		String[] ss = createSql.split("\n");
//		StringBuffer createSql2 = new StringBuffer();
//		p = 0;
//		for (String s : ss) {
//			if(p > 0)
//				s(createSql2, "                \"%s\"", s);
//			else
//				s(createSql2, "\"%s\"", s);
//			p ++;
//			if(p < ss.length){
//				sn(createSql2, " +");
//			}
//		}

//		String[] ss2 = createNoUniqueSql.split("\n");
//		StringBuffer createNoUniqueSql2 = new StringBuffer();
//		p = 0;
//		for (String s : ss2) {
//			if(p > 0)
//				s(createNoUniqueSql2, "                \"%s\"", s);
//			else
//				s(createNoUniqueSql2, "\"%s\"", s);
//			p ++;
//			if(p < ss.length){
//				sn(createNoUniqueSql2, " +");
//			}
//		}

//		sn(sb, "    public void createTable(final String TABLENAME2){");
//		sn(sb, "        try{");
//		sn(sb, "            String sql = %s;\r\n", createSql2.toString());
//		sn(sb, "            Map params = newMap();");
//		sn(sb, "            params.put(\"TABLENAME\", TABLENAME2);");
//		sn(sb, "            sql  = EasyTemplate.make(sql, params);");
//		sn(sb, "            super.update(sql);");
//		sn(sb, "        } catch(Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		
//		sn(sb, "    public void createNoUniqueTable(final String TABLENAME2){");
//		sn(sb, "        try{");
//		sn(sb, "            String sql = %s;\r\n", createNoUniqueSql2.toString());
//		sn(sb, "            Map params = newMap();");
//		sn(sb, "            params.put(\"TABLENAME\", TABLENAME2);");
//		sn(sb, "            sql  = EasyTemplate.make(sql, params);");
//		sn(sb, "            super.update(sql);");
//		sn(sb, "        } catch(Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "");

		
//		sn(sb, "    public void truncate(){");
//		sn(sb, "        try {");
//		sn(sb, "            super.truncate(TABLENAME);");
//		sn(sb, "        } catch (Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public void repair(){");
//		sn(sb, "        try {");
//		sn(sb, "            super.repair(TABLENAME);");
//		sn(sb, "        } catch (Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "");
//
//		sn(sb, "    public void optimize(){");
//		sn(sb, "        try {");
//		sn(sb, "            super.optimize(TABLENAME);");
//		sn(sb, "        } catch (Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "");

		sn(sb, "}");

		
		return sb.toString();
	}
	
	public static String Ss (String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
	}
	
	public static String lower( String s ){
		return s.toLowerCase();
	}
}
