package com.bowlong.sql.builder.jdbc.oracle;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bowlong.Abstract;
import com.bowlong.lang.StrEx;
import com.bowlong.pinyin.PinYin;
import com.bowlong.sql.SqlEx;
import com.bowlong.util.MapEx;

@SuppressWarnings("unused")
public class BeanBuilder extends Abstract {

	public static void main(String[] args) throws Exception {
		String TABLENAME = "COPYRIGHT";
		String sql = "SELECT * FROM " + TABLENAME;
		String host = "192.168.1.121";
		int port = 1521;
		String db = "XE"; 
		String user = "sa";
		String password = "12345670";
		Connection conn = SqlEx.newOracleConnection(host, port, db, user, password);

		String bpackage = "fych.db";

		ResultSet rs = SqlEx.executeQuery(conn, sql);

		String xml = build(conn, rs, bpackage, db, TABLENAME);
		System.out.println(xml);

	}

	@SuppressWarnings({ "rawtypes" })
	public static String build(Connection conn, ResultSet rs, String pkg, String DB, String TABLENAME)
			throws Exception {
		TABLENAME = TABLENAME.replace("\"", "");
		ResultSetMetaData rsmd = rs.getMetaData();
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		String catalogName = DB; //(String) columns.get(0).get("catalogName");
		String table = TABLENAME;// (String) columns.get(0).get("tableName");

		boolean iscd = columnsDuplicate(columns);
		if(iscd)
			throw new IOException("columns pinyin short is duplicate.");
		
		String dbIndexs = dbIndexs(conn, table);
		String tableEn = PinYin.getShortPinYin(table);
		String tableUEn = Ss(tableEn);
		String primaryKey = BeanBuilder.primaryKey(conn, table, columns);
		String primaryKeyType = JavaType.getType(rsmd, primaryKey);
		String pkBType = JavaType.getBasicType(primaryKeyType);
		String javaTypes = javaTypes(rsmd, columns);
		String dataTypes = dataTypes(rsmd, columns);
		String columns1 = columns1(rsmd, columns);
		String columns2 = columns2(rsmd, columns);
		String columns10 = columns10(rsmd, columns);
		List<Map> iks = SqlEx.getImportedKeys(conn, table);
		List<Map> eks = SqlEx.getExportedKeys(conn, table);

		// log(catalogName, tableName, tableNameEn, tableNameUEn);
		// log(columns);

		StringBuffer sb = new StringBuffer();
		sn(sb, "package %s.bean;", pkg);
		sn(sb, "");
		//sn(sb, "import java.io.*;");
		sn(sb, "import java.util.*;");
		sn(sb, "import java.sql.*;");
		sn(sb, "import com.bowlong.sql.*;");
		sn(sb, "import com.bowlong.pinyin.*;");
		sn(sb, "import %s.entity.*;", pkg);
		sn(sb, "");
		sn(sb, "//%s - %s", catalogName, table);
		sn(sb, "@SuppressWarnings({\"rawtypes\",  \"unchecked\", \"serial\" })");
		sn(sb, "public class %s extends com.bowlong.sql.oracle.BeanSupport {", tableUEn);
		sn(sb, "");
		sn(sb, "    private static final %s _me = new %s();", tableUEn, tableUEn);
		sn(sb, "");
		sn(sb, "    public static String TABLENAME = \"%s\";", table);
		sn(sb, "");
		sn(sb, "    public static String[] carrays ={%s};", columns1);
		sn(sb, "");
		sn(sb, "    public static String[] javaTypes ={%s};", javaTypes);
		sn(sb, "");
		sn(sb, "    public static String[] dbTypes ={%s};", dataTypes);
		sn(sb, "");
		sn(sb, "    public static String[][] dbIndexs ={%s};", dbIndexs);
		sn(sb, "");
//		sn(sb, "    public static ModifyScheduler _scheduler;");
//		sn(sb, "    public static void newModifyScheduler() {");
//		sn(sb, "        setModifyScheduler(new ModifyScheduler());");
//		sn(sb, "    }");
//		sn(sb, "    public static void setModifyScheduler(ModifyScheduler scheduler) {");
//		sn(sb, "        long initialDelay = 60 * 1000;");
//		sn(sb, "        long delay = 10 * 1000;");
//		sn(sb, "        setModifyScheduler(scheduler, initialDelay, delay);");
//		sn(sb, "    }");
//		sn(sb, "    public static void setModifyScheduler(ModifyScheduler scheduler, long initialDelay, long delay){");
//		sn(sb, "        _scheduler = scheduler;");
//		sn(sb, "        if( _scheduler == null ) return;");
//		sn(sb, "        _scheduler.schedule(initialDelay, delay);");
//		sn(sb, "    }");
//		sn(sb, "");
		sn(sb, "    public static BeanEvent _event;");
		sn(sb, "    public static void setEvent(BeanEvent evt){");
		sn(sb, "        _event = evt;");
		sn(sb, "    }");
		sn(sb, "    public void doEvent(String key, Object oldValue, Object newValue){");
		sn(sb, "        if(_event == null) return;");
		if (pkBType.contains("int") || pkBType.contains("long")) {
			sn(sb, "        if(%s <= 0) return;", primaryKey);
		} else if (pkBType.contains("String")) {
			sn(sb, "        if(%s == null || %s.isEmpty()) return;", primaryKey, primaryKey);
		}
		sn(sb, "        _event.doEvent(this, key, oldValue, newValue);");
		sn(sb, "    }");
		sn(sb, "");

		// // 关联其他表的主键
		// List iksNon = newList();
		// for (Map<String, Object> m : iks) {
		// String t = MapEx.get(m, "PKTABLE_NAME");
		// String c = MapEx.get(m, "FKCOLUMN_NAME");
		// iksNon.add(ListBuilder.builder().add(t).add(c).toList());
		// }
		// println("iksNon:" + iksNon);
		// // 被其他表关联的主键
		// List myEks = newList();
		// for (Map m : eks) {
		// String t = MapEx.get(m, "FKTABLE_NAME");
		// String c = MapEx.get(m, "FKCOLUMN_NAME");
		// Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn,
		// t);
		// myEks.add(ListBuilder.builder().add(t).add(c).add(isNonUnique(indexs,
		// c)).toList());
		// }
		// println("myEks[TABLE, COLUMN, UNIQUE]:" + myEks);

		for (Map<String, Object> m : columns) {
			String columnName = MapEx.get(m, "columnName");
			String javaType = JavaType.getType(rsmd, columnName);
			String bType = JavaType.getBasicType(javaType);
			sn(sb, "    public %s %s;", bType, columnName);
		}
		sn(sb, "");
		sn(sb, "    public Map extension = new HashMap();");
		sn(sb, "");

//		sn(sb, "    public String _tableId() {");
//		sn(sb, "        return TABLENAME;");
//		sn(sb, "    }");

		
		// 被其他表关联的主键
		for (Map m : eks) {
			String t = MapEx.get(m, "FKTABLE_NAME");
			String tUn = PinYin.getShortPinYin(t);
			String tUen = StrEx.upperFirst(tUn);
			String c = MapEx.get(m, "FKCOLUMN_NAME");
			String cUn = PinYin.getShortPinYin(c);
			String cUen = StrEx.upperFirst(cUn);
			Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(
					conn, t);
			if (isNonUnique(indexs, c)) {
				sn(sb, "    // public %s linked%sFk%s = null; // %s", tUen,
						tUen, cUen, t);
			} else {
			}
		}
		for (Map m : eks) {
			String t = MapEx.get(m, "FKTABLE_NAME");
			String tUn = PinYin.getShortPinYin(t);
			String tUen = StrEx.upperFirst(tUn);
			String c = MapEx.get(m, "FKCOLUMN_NAME");
			String cUn = PinYin.getShortPinYin(c);
			String cUen = StrEx.upperFirst(cUn);
			Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(
					conn, t);
			if (isNonUnique(indexs, c)) {
			} else {
				sn(sb,
						"    // public List<%s> linked%ssFK%s = new Vector<%s>(); // %s",
						tUen, tUen, cUen, tUen, t);
			}
		}
		sn(sb, "");

		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String bType = JavaType.getBasicType(javaType);
			
//			if (column.equals(primaryKey)) {
//				sn(sb, "    public Object _primaryKey() {");
//				sn(sb, "        return %s;", column);
//				sn(sb, "    }");
//				sn(sb, "");
//			} 
			
			sn(sb, "    public %s get%s(){", bType, columnU);
			sn(sb, "        return %s;", column);
			sn(sb, "    }");
			sn(sb, "");
			sn(sb, "    public %s set%s(%s %s){", tableUEn, columnU, bType, columnEn);
			if (!column.equals(primaryKey)) {
				sn(sb, "        %s _old = %s;", bType, column);
				sn(sb, "        this.%s = %s;", column, columnEn);
				sn(sb, "        changeIt(\"%s\", _old, %s);", column, columnEn);
			} else {
				sn(sb, "        this.%s = %s;", column, columnEn);
			}
			sn(sb, "        return this;");
			sn(sb, "    }");
			sn(sb, "");

			if (!column.equals(primaryKey)) {
				if (bType.contains("short") || bType.contains("int")
						|| bType.contains("long") || bType.contains("float")
						|| bType.contains("double")) {
					sn(sb, "    public %s change%s(%s %s){", tableUEn, columnUEn, bType, columnEn);
					sn(sb, "        return set%s(%s + %s);", columnUEn, column, columnEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public %s change%sWithMin(%s %s, %s _min){", tableUEn, columnUEn, bType, columnEn, bType);
					sn(sb, "        %s _v2 = this.%s + %s;", bType, column, columnEn);
					sn(sb, "        return set%s(_v2 < _min  ? _min : _v2);", columnUEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public %s change%sWithMax(%s %s, %s _max){", tableUEn, columnUEn, bType, columnEn, bType);
					sn(sb, "        %s _v2 = this.%s + %s;", bType, column, columnEn);
					sn(sb, "        return set%s(_v2 > _max  ? _max : _v2);", columnUEn);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb,
							"    public %s change%sWithMinMax(%s %s, %s _min, %s _max){", tableUEn, columnUEn, bType, columnEn, bType, bType);
					sn(sb, "        %s _v2 = this.%s + %s;", bType, column, columnEn);
					sn(sb, "        _v2 = _v2 > _max  ? _max : _v2;");
					sn(sb, "        return set%s(_v2 < _min  ? _min : _v2);", columnUEn);
					sn(sb, "    }");
					sn(sb, "");

				}
			}

			if (column.equals(columnEn))
				continue;

			sn(sb, "    public %s get%s(){", bType, columnUEn);
			sn(sb, "        return %s;", column);
			sn(sb, "    }");
			sn(sb, "");
			sn(sb, "    public %s set%s(%s %s){", tableUEn, columnUEn, bType, columnEn);
			sn(sb, "        return set%s(%s);", column, columnEn);
			sn(sb, "    }");
			sn(sb, "");

			// sn(sb, "    public Map put%s(Map map){", columnUEn);
			// sn(sb, "        return put%s(map, \"%s\");", columnUEn,
			// columnEn);
			// sn(sb, "    }");
			// sn(sb, "");
			//
			// sn(sb, "    public Map put%s(Map map, String key){", columnUEn);
			// sn(sb, "        map.put(key, %s);", column);
			// sn(sb, "        return map;");
			// sn(sb, "    }");
			// sn(sb, "");
			//
			// sn(sb, "    public Map put%s(Map map, int key){", columnUEn);
			// sn(sb, "        map.put(key, %s);", column);
			// sn(sb, "        return map;");
			// sn(sb, "    }");
			// sn(sb, "");

		}

		sn(sb, "    public static %s new%s(%s) {", tableUEn, tableUEn, columns2);
		sn(sb, "        %s result = new %s();", tableUEn, tableUEn);
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.%s = %s;", column, columnEn);
		}
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static %s new%s(%s %s) {", tableUEn, tableUEn, tableUEn, tableEn);
		sn(sb, "        %s result = new %s();", tableUEn, tableUEn);
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.%s = %s.%s;", column, tableEn, column);
		}
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s createFor(ResultSet rs) throws SQLException {",
				tableUEn);
		sn(sb, "        Map e = SqlEx.toMap(rs);");
		sn(sb, "        return originalTo(e);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    /*");
		sn(sb, "    @SuppressWarnings(\"unused\")");
		sn(sb, "    public static void get%s(){", tableUEn, tableUEn);
		sn(sb, "        %s %s = null; // %s", tableUEn, tableEn, table);
		sn(sb, "        { // new and insert %s (%s)", tableUEn, table);
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.contains("Date"))
				sn(sb, "            Date %s = new Date(); \t// %s", columnEn,
						column);
			else if (basicType.contains("String"))
				sn(sb, "            %s %s = \"\"; \t// %s", basicType,
						columnEn, column);
			else if (basicType.contains("boolean")
					|| basicType.contains("Boolean"))
				sn(sb, "            %s %s = true; \t// %s", basicType,
						columnEn, column);
			else if (basicType.contains("short") || basicType.contains("Short")
					|| basicType.contains("int") || basicType.contains("int")
					|| basicType.contains("long") || basicType.contains("long"))
				sn(sb, "            %s %s = 0; \t// %s", basicType, columnEn,
						column);
			else if (basicType.contains("float") || basicType.contains("Float")
					|| basicType.contains("double")
					|| basicType.contains("Double"))
				sn(sb, "            %s %s = 0.0; \t// %s", basicType, columnEn,
						column);
			else if (basicType.contains("byte[]"))
				sn(sb, "            %s %s = new byte[0]; \t// %s", basicType, columnEn, column);
			else if (basicType.contains("BigDecimal"))
				sn(sb, "            java.math.BigDecimal %s = new java.math.BigDecimal(0); \t// %s", columnEn, column);
			else
				sn(sb, "            %s %s = ; \t// %s", basicType, columnEn, column);
		}
		sn(sb, "            %s = %s.new%s(%s);", tableEn, tableUEn, tableUEn,
				columns10);
		sn(sb, "        }");
		sn(sb, "        %s = %s.insert();", tableEn, tableEn);
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String bType = JavaType.getBasicType(javaType);
			if (bType.contains("Date"))
				sn(sb, "        Date %s = %s.get%s(); \t// %s", columnEn,
						tableEn, columnUEn, column);
			else
				sn(sb, "        %s %s = %s.get%s(); \t// %s", bType, columnEn,
						tableEn, columnUEn, column);
		}
		sn(sb, "    }");
		sn(sb, "    */");
		sn(sb, "");

		/*
		 * sn(sb, "    public List toList(){"); sn(sb,
		 * "        List result = new Vector();"); sn(sb,
		 * "        result.add(TABLENAME);"); sn(sb,
		 * "        result.add(\"%s.bean.\");", pkg, tableUEn); for (Map<String,
		 * Object> m : columns) { String column = MapEx.get(m, "columnName"); //
		 * String columnU = StrEx.upperFirst(column); // String columnEn =
		 * PinYin.getShortPinYin(column); // String columnUEn =
		 * StrEx.upperFirst(columnEn); // String javaType =
		 * JavaType.getType(rsmd, column); sn(sb,
		 * "        result.add(%s);", column); } sn(sb,
		 * "        return result;"); sn(sb, "    }"); sn(sb, "");
		 * 
		 * sn(sb, "    public static %s listTo(List list){", tableUEn); sn(sb,
		 * "        %s result = new %s();", tableUEn, tableUEn, tableEn); int x
		 * = 0; for (Map<String, Object> m : columns) { String column =
		 * MapEx.get(m, "columnName"); String javaType = JavaType.getType(rsmd,
		 * column); sn(sb, "        result.%s = (%s)list.get(%d);", column,
		 * javaType, x++); } x = 0; sn(sb, "        return result;"); sn(sb,
		 * "    }"); sn(sb, "");
		 */
		sn(sb, "    public int valueZhInt(String fieldZh){");
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return valueInt(fieldEn);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int valueInt(String fieldEn){");
		sn(sb, "        switch(fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("int") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return %s;", column);
			}
		}
		sn(sb, "        }");
		sn(sb, "        return 0;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setZhInt(String fieldZh, int value2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return setInt(fieldEn, value2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setInt(String fieldEn, int value2) {", tableUEn);
		sn(sb, "        switch(fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("int") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return set%s(value2);", columnU);
			}
		}
		sn(sb, "        }");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s changeZhInt(String fieldZh, int value2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return changeInt(fieldEn, value2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s changeInt(String fieldEn, int value2) {", tableUEn);
		sn(sb, "        switch(fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (column.equals(primaryKey))
				continue;

			if (basicType.indexOf("int") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return change%s(value2);", columnUEn);
			}
		}
		sn(sb, "        }");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s changeZhIntWithMin(String fieldZh, int value2, int _min) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return changeIntWithMin(fieldEn, value2, _min);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s changeIntWithMin(String fieldEn, int value2, int _min) {", tableUEn);
		sn(sb, "        switch(fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (column.equals(primaryKey))
				continue;

			if (basicType.indexOf("int") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return change%sWithMin(value2, _min);", columnUEn);
			}
		}
		sn(sb, "        }");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public double valueZhDouble(String fieldZh){");
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return valueDouble(fieldEn);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public double valueDouble(String fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("double") > -1) {
				sn(sb, "        if(\"%s\".equals(fieldEn)) return %s;",
						columnEn, column);
			}
		}
		sn(sb, "        return 0;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setZhDouble(String fieldZh, double value2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return setDouble(fieldEn, value2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setDouble(String fieldEn, double value2) {", tableUEn);
		sn(sb, "        switch(fieldEn) {");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("double") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return set%s(value2);", columnUEn);
			}
		}
		sn(sb, "        }");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public String valueZhStr(String fieldZh){");
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return valueStr(fieldEn);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public String valueStr(String fieldEn){");
		sn(sb, "        switch(fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("String") > -1) {
				sn(sb, "        case \"%s\": ", columnEn);
				sn(sb, "            return %s;", column);
			}
		}
		sn(sb, "        }");
		sn(sb, "        return \"\";");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public <T> T vZh(String fieldZh){");
		sn(sb, "        return (T) valueZh(fieldZh);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public Object valueZh(String fieldZh){");
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return value(fieldEn);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public <T> T v(String fieldEn){");
		sn(sb, "        return (T) value(fieldEn);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public Object value(String fieldEn){");
		sn(sb, "        switch(fieldEn){");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			// String basicType = JavaType.getBasicType(javaType);
			sn(sb, "        case \"%s\":", columnEn);
			sn(sb, "            return %s;", column);
		}
		sn(sb, "        }");
		sn(sb, "        return null;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setZhStr(String fieldZh, String value2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return setStr(fieldEn, value2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setStr(String fieldEn, String value2) {", tableUEn);
		sn(sb, "        switch(fieldEn) {");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("String") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return set%s(value2);", columnUEn);
			}
		}
		sn(sb, "        }");
		sn(sb, "        // throw new IOException(\"fieldEn:\" + fieldEn + \" Not Found.\");");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setZhBigDecimal(String fieldZh, java.math.BigDecimal value2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return setBigDecimal(fieldEn, value2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setBigDecimal(String fieldEn, java.math.BigDecimal value2) {", tableUEn);
		sn(sb, "        switch(fieldEn) {");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("BigDecimal") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return set%s(value2);", columnUEn);
			}
		}
		sn(sb, "        }");
		sn(sb, "        // throw new IOException(\"fieldEn:\" + fieldEn + \" Not Found.\");");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public %s setZhTimestamp(String fieldZh, java.sql.Timestamp value2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return setTimestamp(fieldEn, value2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setTimestamp(String fieldEn, java.sql.Timestamp value2) {", tableUEn);
		sn(sb, "        switch(fieldEn) {");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			String basicType = JavaType.getBasicType(javaType);
			if (basicType.indexOf("Timestamp") > -1) {
				sn(sb, "        case \"%s\":", columnEn);
				sn(sb, "            return set%s(value2);", columnUEn);
			}
		}
		sn(sb, "        }");
		sn(sb, "        // throw new IOException(\"fieldEn:\" + fieldEn + \" Not Found.\");");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public %s setZhJson(String fieldZh, Object o2) {", tableUEn);
		sn(sb, "        String fieldEn = PinYin.getShortPinYin(fieldZh);");
		sn(sb, "        return setJson(fieldEn, o2);");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s setJson(String fieldEn, Object o2) {", tableUEn);
		sn(sb, "        try {");
		sn(sb, "            String value2 = com.bowlong.json.JSON.toJSONString(o2);");
		sn(sb, "            return setStr(fieldEn, value2);");
		sn(sb, "        } catch (Exception e) {");
		sn(sb, "            e.printStackTrace();");
		sn(sb, "        }");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public Map toMap(){");
		sn(sb, "        Map result = new HashMap();");
		sn(sb, "        result.put(\"_TABLENAME\", TABLENAME);");
		sn(sb, "        result.put(\"_CLASSNAME\", \"%s.bean.%s\");", pkg,
				tableUEn);
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.put(\"%s\", %s);", columnEn, column);
		}
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public Map toBasicMap(){");
		sn(sb, "        Map result = new HashMap();");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.put(\"%s\", %s);", column, column);
		}
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public Map toOriginalMap(){");
		sn(sb, "        Map result = new HashMap();");
		sn(sb, "        result.put(\"_TABLENAME\", TABLENAME);");
		sn(sb, "        result.put(\"_CLASSNAME\", \"%s.bean.%s\");", pkg,
				tableUEn);
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.put(\"%s\", %s);", column, column);
		}
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		/*
		 * sn(sb, "    public Map toMapCN(){"); sn(sb,
		 * "        Map result = new HashMap();"); sn(sb,
		 * "        result.put(\"_TABLENAME\", TABLENAME);"); for (Map<String,
		 * Object> m : columns) { String column = MapEx.get(m, "columnName"); //
		 * String columnU = StrEx.upperFirst(column); // String columnEn =
		 * PinYin.getShortPinYin(column); // String columnUEn =
		 * StrEx.upperFirst(columnEn); // String javaType =
		 * JavaType.getType(rsmd, column); sn(sb,
		 * "        result.put(\"%s\", %s);", column, column); } sn(sb,
		 * "        return result;"); sn(sb, "    }"); sn(sb, "");
		 */

		sn(sb, "    public %s mapToObject(Map e){", tableUEn);
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			JavaType.getBasicType(javaType);
			sn(sb, "        %s %s = (%s)e.get(\"%s\");", javaType, column, javaType, columnEn);
		}
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			JavaType.getBasicType(javaType);
			sn(sb, "        if(%s == null) %s = %s;", column, column, SqlEx.getDefaultValue(javaType));
		}
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        set%s(%s);", columnU, column);
		}
		sn(sb, "");
		sn(sb, "        return this;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static final %s mapTo(Map e){", tableUEn, tableUEn);
		sn(sb, "        %s result = new %s();", tableUEn, tableUEn);
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			JavaType.getBasicType(javaType);
			sn(sb, "        %s %s = (%s)e.get(\"%s\");", javaType, column,
					javaType, columnEn);
		}
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			JavaType.getBasicType(javaType);
			sn(sb, "        if(%s == null) %s = %s;", column, column,
					SqlEx.getDefaultValue(javaType));
		}
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.%s = %s;", column, column);
		}
		sn(sb, "");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static final %s originalTo(Map e){", tableUEn, tableUEn);
		sn(sb, "        %s result = new %s();", tableUEn, tableUEn);
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			//JavaType.getBasicType(javaType);
			sn(sb, "        %s %s = (%s)e.get(\"%s\");", javaType, column, javaType, column);
		}
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);
			JavaType.getBasicType(javaType);
			sn(sb, "        if(%s == null) %s = %s;", column, column, SqlEx.getDefaultValue(javaType));
		}
		sn(sb, "");
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			sn(sb, "        result.%s = %s;", column, column);
		}
		sn(sb, "");
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public String toString(){");
		sn(sb, "        return toOriginalMap().toString();");
		sn(sb, "    }");
		sn(sb, "");

		// 关联其他表的主键
		for (Map<String, Object> m : iks) {
			String t = MapEx.get(m, "PKTABLE_NAME");
			String tUn = PinYin.getShortPinYin(t);
			String tUen = StrEx.upperFirst(tUn);
			String c = MapEx.get(m, "FKCOLUMN_NAME");
			String cUn = PinYin.getShortPinYin(c);
			String cUen = StrEx.upperFirst(cUn);
			sn(sb, "    public final %s get%sFk%s() { // %s - %s", Ss(tUen), Ss(tUen),
					cUen, t, c);
			sn(sb, "        return %sEntity.getByKey(%s);", Ss(tUen), c);
			sn(sb, "    }");
			sn(sb, "");

			sn(sb, "    public final int count%sFk%s() { // %s - %s", Ss(tUen), Ss(cUen), t, c);
			sn(sb, "        return get%sFk%s() == null ? 0 : 1;", Ss(tUen), cUen);
			sn(sb, "    }");
			sn(sb, "");

		}

		// 被其他表关联的主键
		for (Map m : eks) {
			String t = MapEx.get(m, "FKTABLE_NAME");
			String tUn = PinYin.getShortPinYin(t);
			String tUen = StrEx.upperFirst(tUn);
			String c = MapEx.get(m, "FKCOLUMN_NAME");
			String cUn = PinYin.getShortPinYin(c);
			String cUen = StrEx.upperFirst(cUn);
			String p = MapEx.get(m, "PKCOLUMN_NAME");
			Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(
					conn, t);

			if (isNonUnique(indexs, c)) {
				sn(sb, "    public final %s get%sFk%s() { // %s - %s", Ss (tUen), Ss(tUen), cUen, t, c);
				sn(sb, "        return %sEntity.getBy%s(%s);", Ss(tUen), cUen, p);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public final int count%sFk%s() { // %s - %s ",
						Ss(tUen), Ss(cUen), t, c);
				sn(sb, "        return get%sFk%s() == null ? 0 : 1;", Ss(tUen),
						cUen);
				sn(sb, "    }");
				sn(sb, "");

			} else {
				sn(sb, "    public final List<%s> get%ssFk%s() { // %s - %s",
						Ss(tUen), Ss(tUen), cUen, t, c);
				sn(sb, "        return %sEntity.getBy%s(%s);", Ss(tUen), cUen, p);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public final int count%ssFk%s() { // %s - %s",
						Ss(tUen), Ss(cUen), t, c);
				sn(sb, "        return %sEntity.countBy%s(%s);", Ss(tUen), cUen, p);
				sn(sb, "    }");
				sn(sb, "");

			}
		}
		sn(sb, "    public static final %s loadByKey(%s %s) {", Ss(tableUEn),
				pkBType, primaryKey);
		sn(sb, "        return %sEntity.getByKey(%s);", Ss(tableUEn), primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public final %s insert() {", tableUEn);
		sn(sb, "        %s result = %sEntity.insert(this);", tableUEn, tableUEn);
		sn(sb, "        if(result == null) return null;");
		sn(sb, "        %s = result.%s;", primaryKey, primaryKey);
		sn(sb, "        return result;");
		sn(sb, "    }");
		sn(sb, "");

//		sn(sb, "    public final %s insert2() {", tableUEn);
//		sn(sb, "        return %sEntity.insert2(this);", tableUEn);
//		sn(sb, "    }");
//		sn(sb, "");

		sn(sb, "    public final %s update() {", tableUEn);
		sn(sb, "        return %sEntity.update(this);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

//		sn(sb, "    public boolean asyncUpdate() {");
//		sn(sb, "        if( _scheduler == null ) return false;");
//		sn(sb, "        if(%s <= 0) return false;", primaryKey);
//		sn(sb, "        return _scheduler.putTo( this );");
//		sn(sb, "    }");
//		sn(sb, "");

		// sn(sb, "    public final %s update2() {", tableUEn);
		// sn(sb, "        return %sEntity.update2(this);", tableUEn);
		// sn(sb, "    }");
		// sn(sb, "");

		sn(sb, "    public final %s insertOrUpdate() {", tableUEn);
		if(primaryKeyType.contains("BigDecimal"))
			sn(sb, "        if(%s.intValue() <= 0)", primaryKey);
		else
			sn(sb, "        if(%s <= 0)", primaryKey);
		sn(sb, "            return insert();");
		sn(sb, "        else");
		sn(sb, "            return update();");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public final int delete() {", tableUEn);
		sn(sb, "        return %sEntity.delete(%s);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		// sn(sb, "    public final int delete2() {", tableUEn);
		// sn(sb, "        return %sEntity.delete2(%s);", tableUEn, primaryKey);
		// sn(sb, "    }");
		// sn(sb, "");

		sn(sb, "    public Object clone() throws CloneNotSupportedException {");
		sn(sb, "        return super.clone();");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s clone2() {", tableUEn);
		sn(sb, "        try{");
		sn(sb, "            return (%s) clone();", tableUEn);
		sn(sb, "        } catch (Exception e) {");
		sn(sb, "            e.printStackTrace();");
		sn(sb, "        }");
		sn(sb, "        return null;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "}");

		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String primaryKey(Connection conn, String table,
			List<Map<String, Object>> columns) throws Exception {
		String pk;
		List<Map> idxs = SqlEx.getIndexInfo(conn, table, true);
		Map<String, Integer> midx = newMap();
		for (Map map : idxs) {
			String c = MapEx.getString(map, "COLUMN_NAME");
			BigDecimal t = MapEx.getBigDecimal(map, "TYPE");
			midx.put(c, t.intValue());
		}
		
		for (Map<String, Object> map : columns) {
			String c = MapEx.getString(map, "columnName");
			Integer t = midx.get(c);
			if(c != null && t.intValue() == 1)
				return c;
		}
		
		return "";
		
//		for (Map<String, Object> m : columns) {
//			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
//			String column = MapEx.get(m, "columnName");
//			// String columnU = StrEx.upperFirst(column);
//			// String columnEn = PinYin.getShortPinYin(column);
//			// String columnUEn = StrEx.upperFirst(columnEn);
//			// String javaType = JavaType.getType(rsmd, column);
//			if (isAutoIncrement)
//				return column;
//		}
//		return "";
	}

	public static String javaTypes(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			String columnClassName = MapEx.get(m, "columnClassName");
			
//			if(columnClassName.equals("java.math.BigDecimal"))
//				columnClassName = "java.lang.Integer";
			s(sb, "\"%s\"", columnClassName);
			p++;
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	public static String dbIndexs(Connection conn, String table)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		Map<String, List<Map<String, Object>>> idxs = SqlEx.getIndexs(conn,
				table);
		Iterator<String> it = idxs.keySet().iterator();
		int count = idxs.size();
		int p = 0;
		while (it.hasNext()) {
			p++;
			String key = it.next();
			List<Map<String, Object>> l = idxs.get(key);
			// if(p > 1){
			// s(sb, "                                         ");
			// }
			// s(sb, "{");
			int i = 0;
			for (Map<String, Object> map : l) {
				i++;
				String COLUMN_NAME = (String) map.get("COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(map.get("NON_UNIQUE"));
				
				s(sb, "{\"" + COLUMN_NAME + "\", \"%s\"}",
						NON_UNIQUE.equals("true") ? "NON_UNIQUE" : "UNIQUE");
				if (i < l.size()) {
					s(sb, ", ");
				}
			}
			if (p < count)
				s(sb, ",");
			// s(sb, "},\n");
		}
		return sb.toString();
	}

	public static String dataTypes(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			String columnTypeName = MapEx.get(m, "columnTypeName");
			int precision = MapEx.get(m, "precision");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			if (columnTypeName.equals("VARCHAR") && precision >= 715827882) {
				columnTypeName = ("LONGTEXT");
			} else if (columnTypeName.equals("VARCHAR") && precision >= 5592405) {
				columnTypeName = ("MEDIUMTEXT");
			} else if (columnTypeName.equals("VARCHAR") && precision >= 21845) {
				columnTypeName = ("TEXT");
			} else if (columnTypeName.equals("VARCHAR") && precision >= 255) {
				columnTypeName = ("TINYTEXT");
			}

			s(sb, "\"%s\"", columnTypeName);
			p++;
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// "id","城市id","名字"
	public static String columns1(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);

			s(sb, "\"%s\"", column);
			p++;
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// Integer id, Integer csid,
	public static String columns2(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			String javaType = JavaType.getType(rsmd, column);

			s(sb, "%s %s", javaType, columnEn);
			p++;
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// id, csid,
	public static String columns3(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);

			s(sb, "%s", column.toUpperCase());
			p++;
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// csid, ....
	public static String columns4(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			p++;
			if (isAutoIncrement)
				continue;
			s(sb, "%s", column.toUpperCase());
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// :csid, ....
	public static String columns5(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			p++;
			if (isAutoIncrement)
				continue;
			s(sb, ":%s", column);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	public static String columnsForInsert(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			 String javaType = JavaType.getType(rsmd, column);
			p++;
			if (isAutoIncrement)
				continue;
			if(javaType.toLowerCase().contains("blob"))
				s(sb, "EMPTY_BLOB()");
			else if(javaType.toLowerCase().contains("clob"))
				s(sb, "EMPTY_CLOB()");
			else
				s(sb, ":%s", column);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// :id, :csid, ....
	public static String columns6(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			// boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			p++;
			// if(isAutoIncrement)
			// continue;
			s(sb, ":%s", column);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// ?, ....
	public static String columns7(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			// String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			p++;
			if (isAutoIncrement)
				continue;
			s(sb, "?");
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// csid = :csid, ....
	public static String columns8(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			p++;
			if (isAutoIncrement)
				continue;
			s(sb, "%s=:%s", column.toUpperCase(), column);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// csid = ?, ....
	public static String columns9(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			boolean isAutoIncrement = MapEx.get(m, "isAutoIncrement");
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			// String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);
			p++;
			if (isAutoIncrement)
				continue;
			s(sb, "%s=?", column);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// id, csid,
	public static String columns10(ResultSetMetaData rsmd,
			List<Map<String, Object>> columns) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = columns.size();
		int p = 0;
		for (Map<String, Object> m : columns) {
			String column = MapEx.get(m, "columnName");
			// String columnU = StrEx.upperFirst(column);
			String columnEn = PinYin.getShortPinYin(column);
			// String columnUEn = StrEx.upperFirst(columnEn);
			// String javaType = JavaType.getType(rsmd, column);

			s(sb, "%s", columnEn);
			p++;
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static String indexCoulmns(List<Map<String, Object>> columns,
			Map<String, List<Map<String, Object>>> indexs) throws Exception {
		StringBuffer result = new StringBuffer();
		Map<String, String> m = newMap();

		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if (idx_size == 1) { // 单索引
				Map<String, Object> index = idx.get(0);
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				m.put(COLUMN_NAME, COLUMN_NAME);
			} else { // 多索引
				for (Map<String, Object> e : idx) {
					String COLUMN_NAME = MapEx.get(e, "COLUMN_NAME");
					m.put(COLUMN_NAME, COLUMN_NAME);
				}
			}
		}

		for (Map<String, Object> map : columns) {
			String columnName = (String) map.get("columnName");
			if (!m.containsKey(columnName))
				continue;

			result.append(columnName).append(", ");
		}
		if (result.length() < 2)
			return "";
		return result.substring(0, result.length() - 2);
	}

	// XYCsid..
	public static String index1(ResultSetMetaData rsmd,
			List<Map<String, Object>> index) throws Exception {
		StringBuffer sb = new StringBuffer();
		// int size = index.size();
		int p = 0;

		for (Map<String, Object> m : index) {
			// String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
			String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
			// String NON_UNIQUE = String.valueOf(m.get("NON_UNIQUE"));
			String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
			String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
			// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
			p++;
			s(sb, "%s", COLUMN_NAME_UEN);
			// if(p < size)
			// s(sb, "And");
		}
		return sb.toString();
	}

	// Integer x, Integer y, ...
	public static String index2(ResultSetMetaData rsmd,
			List<Map<String, Object>> index) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = index.size();
		int p = 0;

		for (Map<String, Object> m : index) {
			// String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
			String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
			// String NON_UNIQUE = String.valueOf(m.get("NON_UNIQUE"));
			String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
			// String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
			String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
			p++;
			s(sb, "%s %s", COLUMN_NAME_TYPE, COLUMN_NAME_EN);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// x, y, ...
	public static String index3(ResultSetMetaData rsmd,
			List<Map<String, Object>> index) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = index.size();
		int p = 0;

		for (Map<String, Object> m : index) {
			// String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
			String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
			// String NON_UNIQUE = String.valueOf(m.get("NON_UNIQUE"));
			String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
			// String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
			// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
			p++;
			s(sb, "%s", COLUMN_NAME_EN);
			if (p < size)
				s(sb, ", ");
		}
		return sb.toString();
	}

	// x=:x AND y=:y, ...
	public static String index4(ResultSetMetaData rsmd,
			List<Map<String, Object>> index) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = index.size();
		int p = 0;
		for (Map<String, Object> m : index) {
			// String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
			String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
			// String NON_UNIQUE = String.valueOf(m.get("NON_UNIQUE"));
			// String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
			// String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
			// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
			p++;
			s(sb, "%s=:%s", COLUMN_NAME, COLUMN_NAME);
			if (p < size)
				s(sb, " AND ");
		}
		return sb.toString();
	}

	// x-y-...
	public static String index5(ResultSetMetaData rsmd,
			List<Map<String, Object>> index) throws Exception {
		StringBuffer sb = new StringBuffer();
		int size = index.size();
		int p = 0;
		for (Map<String, Object> m : index) {
			// String INDEX_NAME = MapEx.get(m, "INDEX_NAME");
			String COLUMN_NAME = MapEx.get(m, "COLUMN_NAME");
			// String NON_UNIQUE = String.valueOf(m.get("NON_UNIQUE"));
			String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
			// String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
			// String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
			p++;
			s(sb, "%s", COLUMN_NAME_EN);
			if (p < size)
				s(sb, "+\"-\"+");
		}
		return sb.toString();
	}

	public static boolean isNonUnique(
			Map<String, List<Map<String, Object>>> indexs, String columnName) {
		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if (idx_size == 1) { // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				if (INDEX_NAME.equals("PRIMARY"))
					continue;
				if (columnName.equals(COLUMN_NAME)) {
					return (NON_UNIQUE.equals("0"));
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static boolean columnsDuplicate(List<Map<String, Object>> columns){
		List<String> szColumns = newList();
		for (Map<String, Object> map : columns) {
			szColumns.add((String) map.get("columnName"));
		}
		return isDuplicate(szColumns);
	}
	
	// 中文名字是否重复
	public static boolean isDuplicate(List<String> strings) {
		Map<String, String> m = new HashMap<String, String>();
		for (String s : strings) {
			String s2 = PinYin.shortPinYin(s);
			String t = m.get(s2);
			if (t != null) {
				System.out.println(s + " duplicate with:" + t);
				return true;
			}
			m.put(s2, s);
		}
		return false;
	}

	public static String Ss (String s){
		return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
	}
}
