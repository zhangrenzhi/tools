package com.bowlong.sql.builder.jdbc.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.bowlong.Abstract;
import com.bowlong.lang.StrEx;
import com.bowlong.pinyin.PinYin;
import com.bowlong.sql.SqlEx;
import com.bowlong.util.MapEx;

public class EntityBuilder extends Abstract {

	public static void main(String[] args) throws Exception {
		String TABLENAME = "用户";
		String sql = "SELECT * FROM " + TABLENAME;
		String host = "192.168.1.120";
		int port = 1521;
		String db = "XE"; 
		String user = "sa";
		String password = "12345670";
		Connection conn = SqlEx.newOracleConnection(host, port, db, user, password);

		String bpackage = "fych.db";
		String appContext = "fych.context.AppContext";

		ResultSet rs = SqlEx.executeQuery(conn, sql);

		String xml = build(conn, rs, bpackage, appContext, db, TABLENAME);
		System.out.println(xml);

	}

	@SuppressWarnings("rawtypes")
	public static String build(Connection conn, ResultSet rs, String pkg, String appContext, String db, String TABLENAME) throws Exception {
		TABLENAME = TABLENAME.replace("\"", "");
		StringBuffer sb = new StringBuffer();
		
		System.out.println("-----------------------");
//		ResultSetMetaData rsmd = rs.getMetaData();
		//List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		String catalogName = db;//(String) columns.get(0).get("catalogName");
		String table = TABLENAME; // (String) columns.get(0).get("tableName");
		String tableEn = PinYin.getShortPinYin(table);
		String tableUEn = Ss(tableEn);
		List<Map> eks = SqlEx.getExportedKeys(conn, table);
		// import
		if (pkg != null && pkg.length() > 0) {
			sb.append("package " + pkg + ".entity;");
			sb.append("\r\n");
			sb.append("\r\n");
		}
//		sn(sb, "import java.util.*;");
//		sn(sb, "import com.bowlong.sql.*;");
//		sn(sb, "import com.bowlong.lang.*;");
		sn(sb, "import org.apache.commons.logging.*;");
//		sn(sb, "import %s.bean.*;", pkg);
		sn(sb, "import %s.dao.*;",pkg);
		sn(sb, "import %s.internal.*;", pkg);
//		sn(sb, "import %s;", appContext);
		sn(sb, "");

		// class
		sn(sb, "//%s - %s", catalogName, table);
		sn(sb, "// @SuppressWarnings({ \"static-access\" })");
		sn(sb, "public class %sEntity extends %sInternal{", tableUEn, tableUEn);
		sn(sb, "    static Log log = LogFactory.getLog(%sEntity.class);", tableUEn);
		sn(sb, "");
		sn(sb, "    public static final %sEntity my = new %sEntity();", tableUEn, tableUEn);
		sn(sb, "");

//		sn(sb, "    public static long TIMEOUT(){");
//		sn(sb, "        return 0;");
//		sn(sb, "    }");
//		sn(sb, "");
		
		sn(sb, "    static %sDAO %sDAO = null;", tableUEn, tableUEn);
		sn(sb, "    public static %sDAO %sDAO() {", tableUEn, tableUEn);
		sn(sb, "        if( %sDAO == null)", tableUEn);
		sn(sb, "            %sDAO = new %sDAO(%s.ds());", tableUEn, tableUEn, appContext);
		sn(sb, "        return %sDAO;", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "//    static %sDAO %sDAO99 = null;", tableUEn, tableUEn);
		sn(sb, "//    public static %sDAO %sDAO99() {", tableUEn, tableUEn);
		sn(sb, "//        if( %sDAO99 == null)", tableUEn);
		sn(sb, "//            %sDAO99 = new %sDAO(%s.ds99());", tableUEn, tableUEn, appContext);
		sn(sb, "//        return %sDAO99;", tableUEn);
		sn(sb, "//    }");
		sn(sb, "");

//		sn(sb, "    public static void insertDdTry(final %s %s) {", tableUEn, tableEn);
//		sn(sb, "        execute(new Runnable() {");
//		sn(sb, "            public void run() {");
//		sn(sb, "                %sDAO DAO = %sDAO();", tableUEn, tableUEn);
//		sn(sb, "                String TABLENAME2 = DAO.TABLEDD();");
//		sn(sb, "                try {");
//		sn(sb, "                    int n = DAO.insert(%s, TABLENAME2);", tableEn);
//		sn(sb, "                    if(n <= 0){");
//		sn(sb, "                        createTable(DAO, TABLENAME2);");
//		sn(sb, "                        log.info(\"new table:\" + TABLENAME2);");
//		sn(sb, "                        DAO.insert(%s, TABLENAME2);", tableEn);
//		sn(sb, "                    }");
//		sn(sb, "                } catch (Exception e) {");
//		sn(sb, "                    log.info(e2s(e));");
//		sn(sb, "                }");
//		sn(sb, "            }");
//		sn(sb, "        });");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static void insertDdTry(final List<%s> %ss) {", tableUEn, tableEn);
//		sn(sb, "        insert(%ss);", tableEn);
//		sn(sb, "        SqlEx.execute4Fixed(new Runnable() {");
//		sn(sb, "            public void run() {");
//		sn(sb, "                %sDAO DAO = %sDAO();", tableUEn, tableUEn);
//		sn(sb, "                String TABLENAME2 = DAO.TABLEDD();");
//		sn(sb, "                boolean b = SqlEx.isTableExist(DAO.ds, TABLENAME2);");
//		sn(sb, "                if(!b) {");
//		sn(sb, "                    log.info(\"new table:\" + TABLENAME2);");
//		sn(sb, "                    DAO.createTable(TABLENAME2);");
//		sn(sb, "                }");
//		sn(sb, "                DAO.insert(%ss, TABLENAME2);", tableEn);
//		sn(sb, "            }");
//		sn(sb, "        });");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static void insertMmTry(final %s %s) {", tableUEn, tableEn);
//		sn(sb, "        execute(new Runnable() {");
//		sn(sb, "            public void run() {");
//		sn(sb, "        %sDAO DAO = %sDAO();", tableUEn, tableUEn);
//		sn(sb, "        String TABLENAME2 = DAO.TABLEMM();");
//		sn(sb, "        try {");
//		sn(sb, "            int n = DAO.insert(%s, TABLENAME2);", tableEn);
//		sn(sb, "            if(n <= 0){");
//		sn(sb, "                createNoUniqueTable(DAO, TABLENAME2);");
//		sn(sb, "                log.info(\"new table:\" + TABLENAME2);");
//		sn(sb, "                DAO.insert(%s, TABLENAME2);", tableEn);
//		sn(sb, "            }");
//		sn(sb, "        } catch (Exception e) {");
//		sn(sb, "            log.info(e2s(e));");
//		sn(sb, "        }");
//		sn(sb, "    }");
//		sn(sb, "        });");
//		sn(sb, "    }");
//		sn(sb, "");

//		sn(sb, "    public static void insertMmTry(final List<%s> %ss) {", tableUEn, tableEn);
//		sn(sb, "        insert(%ss);", tableEn);
//		sn(sb, "        SqlEx.execute4Fixed(new Runnable() {");
//		sn(sb, "            public void run() {");
//		sn(sb, "                %sDAO DAO = %sDAO();", tableUEn, tableUEn);
//		sn(sb, "                String TABLENAME2 = DAO.TABLEMM();");
//		sn(sb, "                boolean b = SqlEx.isTableExist(DAO.ds, TABLENAME2);");
//		sn(sb, "                if(!b) {");
//		sn(sb, "                    log.info(\"new table:\" + TABLENAME2);");
//		sn(sb, "                    DAO.createTable(TABLENAME2);");
//		sn(sb, "                }");
//		sn(sb, "                DAO.insert(%ss, TABLENAME2);", tableEn);
//		sn(sb, "            }");
//		sn(sb, "        });");
//		sn(sb, "    }");
//		sn(sb, "");
		
		
		// 被其他表关联的主键
		if(eks.size() > 0){
		sn(sb, "    // public void loadLinked(final %s %s) {", tableUEn, tableEn);
		sn(sb, "        // if(%s == null) return;", tableEn);
		for (Map m : eks) {
			String t = MapEx.get(m, "FKTABLE_NAME");
			String tUn = PinYin.getShortPinYin(t);
			String tUen = StrEx.upperFirst(tUn);
			String c = MapEx.get(m, "FKCOLUMN_NAME");
			String cUn = PinYin.getShortPinYin(c);
			String cUen = StrEx.upperFirst(cUn);
			Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn, t);
			if(BeanBuilder.isNonUnique(indexs, c)){
				sn(sb, "        // %s %ss = %s.get%sFk%s(); // %s", tUen, tUn, tableEn, tUen, cUen, t);
			}else{
			}
		}
		for (Map m : eks) {
			String t = MapEx.get(m, "FKTABLE_NAME");
			String tUn = PinYin.getShortPinYin(t);
			String tUen = StrEx.upperFirst(tUn);
			String c = MapEx.get(m, "FKCOLUMN_NAME");
			String cUn = PinYin.getShortPinYin(c);
			String cUen = StrEx.upperFirst(cUn);
			Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn, t);
			if(BeanBuilder.isNonUnique(indexs, c)){
			}else{
				sn(sb, "        // List<%s> %ss = %s.get%ssFk%s(); // %s", tUen, tUn, tableEn, tUen, cUen, t);
			}
		}
		sn(sb, "    // }");
		}
		sn(sb, "");
		sn(sb, "    // types begin");
		sn(sb, "    // types end");
		sn(sb, "");
		sn(sb, "}");
		sn(sb, "");

		return sb.toString();
	}
	
	public static String Ss (String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
	}

}
