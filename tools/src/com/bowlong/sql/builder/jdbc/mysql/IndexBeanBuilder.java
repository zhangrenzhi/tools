package com.bowlong.sql.builder.jdbc.mysql;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.bowlong.Abstract;
import com.bowlong.lang.StrEx;
import com.bowlong.sql.SqlEx;
import com.bowlong.util.StrBuilder;

@SuppressWarnings("unchecked")
public class IndexBeanBuilder extends Abstract {
	public static void main(String[] args) throws Exception {
		String host = "192.168.2.223";
		String db = "sea";
		String table = "用户";
		String pkg = "fych.db";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		String ret = build(conn, pkg, db, table);
		println(ret);
	}
	public static String build(Connection conn, String pkg, String db, String table) throws Exception{
		StrBuilder sb = StrEx.builder();

		Map<String, Map<String, Object>> mapConluns = SqlEx.mapColumns(conn, db, table);
		List<String> indexs = SqlEx.indexColumns(conn, table);
		
		String tEn = getShortPinYin(table);
		String tUEn = upperFirst(tEn);
		String beanName = tUEn + "Index";
		
		sb.pn("package ${1}.bean;", pkg);
		sb.pn("");
		sb.pn("import java.util.*;");
		sb.pn("import java.sql.*;");
		sb.pn("import com.bowlong.sql.*;");
		sb.pn("import com.bowlong.sql.mysql.*;");
		sb.pn("");
		sb.pn("// ${1} - ${2}", db, table);
		sb.pn("@SuppressWarnings({\"rawtypes\",  \"unchecked\"})");
		sb.pn("public class ${1} implements ResultSetHandler {", beanName);
		
		// 字段列表
		for (String s : indexs) {
			Map<String, Object> c = getMap(mapConluns, s);
			String cn = getString(c, "COLUMN_NAME");
			String jtype = SqlEx.toJavaType(getInt(c, "DATA_TYPE"));
			String btype = JavaType.getBasicType(jtype);
			sb.pn("    public ${1} ${2};", btype, cn);
		}
		sb.pn("");
		// Set / Get 
//		for (String s : indexs) {
//			Map<String, Object> c = getMap(mapConluns, s);
//			String cn = getString(c, "COLUMN_NAME");
//			String en = getShortPinYin(cn);
//			String Uen = upperFirst(en);
//			String jtype = SqlEx.toJavaType(getInt(c, "DATA_TYPE"));
//			String btype = JavaType.getBasicType(jtype);
//			sb.pn("    public void set${1}(${2} v){", cn, btype);
//			sb.pn("        this.${1} = v;", cn);
//			sb.pn("    }");
//			sb.pn("    public ${1} get${2}(){", btype, cn);
//			sb.pn("        return ${1};", cn);
//			sb.pn("    }");
//			sb.pn("    public void set${1}(${2} v){", Uen, btype);
//			sb.pn("        this.${1} = v;", cn);
//			sb.pn("    }");
//			sb.pn("    public ${1} get${2}(){", btype, Uen);
//			sb.pn("        return ${1};", cn);
//			sb.pn("    }");
//			sb.pn("");
//		}
		

		// handle
		sb.pn("    public ${1} handle(ResultSet rs) throws SQLException {", beanName);
		sb.pn("        return createFor(rs);");
		sb.pn("    }");
		
		// createFor
		sb.pn("    public static ${1} createFor(ResultSet rs) throws SQLException {", beanName);
		sb.pn("        Map e = SqlEx.toMap(rs);");
		sb.pn("        return originalTo(e);");
		sb.pn("    }");

		// originalTo
		sb.pn("    public static final ${1} originalTo(Map e) {", beanName);
		sb.pn("        ${1} ret = new ${1}();", beanName);
		for (String s : indexs) {
			Map<String, Object> c = getMap(mapConluns, s);
			String cn = getString(c, "COLUMN_NAME");
			//String en = getShortPinYin(cn);
			//String Uen = upperFirst(en);
			String jtype = SqlEx.toJavaType(getInt(c, "DATA_TYPE"));
			String def = SqlEx.getDefaultValue(jtype);
			sb.pn("        ret.${2} = e.containsKey($[2]) ? (${1}) e.get($[2]) : ${3};", jtype, cn, def);
		}
		sb.pn("        return ret;");
		sb.pn("    }");
		sb.pn("");
		
		// toMap
//		sb.pn("    public Map toMap(){");
//		sb.pn("        Map ret = new HashMap()");
//		for (String s : indexs) {
//			Map<String, Object> c = getMap(mapConluns, s);
//			String cn = getString(c, "COLUMN_NAME");
//			sb.pn("        ret.put($[1], ${1});", cn);
//		}
//		sb.pn("        return ret;");
//		sb.pn("    }");
		
		// toString
//		sb.pn("    public String toString(){");
//		sb.pn("        return toMap().toString();");
//		sb.pn("    }");
				
		sb.pn("}");

		return sb.toString();
	}

}
