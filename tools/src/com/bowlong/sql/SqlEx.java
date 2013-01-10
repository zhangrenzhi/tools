package com.bowlong.sql;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.bowlong.Abstract;
import com.bowlong.objpool.StringBufPool;
import com.bowlong.util.MapEx;
import com.bowlong.util.NewSet;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SqlEx extends Abstract {
	public static enum TableType {
		TABLE, VIEW, SYSTEM_TABLE, GLOBAL_TEMPORARY, LOCAL_TEMPORARY, ALIAS, SYNONYM
	}

	// ///////////////////////////////////////////////////////////////////////
	// Access
	public static final Connection newOdbcMsAccessConnectiion(String filename)
			throws SQLException, ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=%s";
		String url = s(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	public static final Connection newOdbcMsAccessConnectiion(String filename,
			String user, String password) throws SQLException,
			ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=%s";
		String url = s(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Excel
	public static final Connection newOdbcMsExcelConnectiion(String filename)
			throws SQLException, ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls)};DBQ=%s";
		String url = s(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Csv
	public static final Connection newOdbcCsvConnectiion(String filename)
			throws SQLException, ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Access Text Driver (*.txt,*.csv)};DBQ=%s";
		String url = s(s, filename);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Odbc Ms Sql
	public static final Connection newOdbcMsSqlConnectiion(String db)
			throws SQLException, ClassNotFoundException {
		String host = "127.0.0.1";
		return newOdbcMsSqlConnectiion(host, db);
	}

	public static final Connection newOdbcMsSqlConnectiion(String host,
			String db) throws SQLException, ClassNotFoundException {
		String user = "sa";
		String password = "";
		return newOdbcMsSqlConnectiion(host, db, user, password);
	}

	public static final Connection newOdbcMsSqlConnectiion(String host,
			String db, String user, String password) throws SQLException,
			ClassNotFoundException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={SQL Server};Server=%s;Database=%s";
		String url = s(s, host, db);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Jtds
	public static final Connection newJtdsSqlserverConnection(String db)
			throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";
		return newJtdsSqlserverConnection(host, db);
	}

	public static final Connection newJtdsSqlserverConnection(String host,
			String db) throws ClassNotFoundException, SQLException {
		int port = 1433;
		return newJtdsSqlserverConnection(host, port, db);
	}

	public static final Connection newJtdsSqlserverConnection(String host,
			int port, String db) throws ClassNotFoundException, SQLException {
		String user = "sa";
		String password = "";
		return newJtdsSqlserverConnection(host, port, db, user, password);
	}

	public static final Connection newJtdsSqlserverConnection(String host,
			int port, String db, String user, String password)
			throws ClassNotFoundException, SQLException {
		String driver = ("net.sourceforge.jtds.jdbc.Driver");
		String s = "jdbc:jtds:sqlserver://%s:%d/%s";
		String url = s(s, host, port, db);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Mysql
	public static final BasicDataSource newMysqlDataSource(String db)
			throws ClassNotFoundException, SQLException {
		int maxActive = 8;
		return newMysqlDataSource(db, maxActive);
	}

	public static final BasicDataSource newMysqlDataSource(String db,
			int maxActive) throws ClassNotFoundException, SQLException {
		int maxIdle = 1;
		return newMysqlDataSource(db, maxActive, maxIdle);
	}

	public static final BasicDataSource newMysqlDataSource(String db,
			int maxActive, int maxIdle) throws ClassNotFoundException,
			SQLException {
		String host = "127.0.0.1";
		return newMysqlDataSource(host, db, maxActive, maxIdle);
	}

	public static final BasicDataSource newMysqlDataSource(String host,
			String db, int maxActive, int maxIdle)
			throws ClassNotFoundException, SQLException {
		String user = "root";
		String password = "";
		return newMysqlDataSource(host, db, user, password, maxActive, maxIdle);
	}

	public static final BasicDataSource newMysqlDataSource(int port, String db,
			String user, String password, int maxActive, int maxIdle)
			throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";
		return newMysqlDataSource(host, port, db, user, password, maxActive,
				maxIdle);
	}

	public static final BasicDataSource newMysqlDataSource(String host,
			String db, String user, String password, int maxActive, int maxIdle)
			throws ClassNotFoundException, SQLException {
		int port = 3306;
		return newMysqlDataSource(host, port, db, user, password, maxActive,
				maxIdle);
	}

	public static final BasicDataSource newMysqlDataSource(String host,
			int port, String db, String user, String password, int maxActive,
			int maxIdle) throws ClassNotFoundException, SQLException {
		boolean reconnect = true;
		String encoding = "utf-8";
		return newMysqlDataSource(host, port, db, reconnect, encoding, user,
				password, maxActive, maxIdle);
	}

	public static final BasicDataSource newMysqlDataSource(String host,
			int port, String db, boolean reconnect, String encoding,
			String user, String password, int maxActive, int maxIdle)
			throws ClassNotFoundException, SQLException {
		String driver = ("com.mysql.jdbc.Driver");
		String s = "jdbc:mysql://%s:%d/%s?autoReconnect=%s&characterEncoding=%s";
		String url = s(s, host, port, db, String.valueOf(reconnect), encoding);

		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(password);
		ds.setMaxActive(maxActive);
		ds.setMaxIdle(maxIdle);
		// ds.setValidationQuery(validationQuery);
		// ds.setRemoveAbandoned(true);
		// ds.setRemoveAbandonedTimeout(60);
		return ds;
	}

	// ///////////////////////////////////////////////////////////////////////
	// Mysql
	public static final Connection newMysqlConnection(String db)
			throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";
		return newMysqlConnection(host, db);
	}

	public static final Connection newMysqlConnection(String host, String db)
			throws ClassNotFoundException, SQLException {
		String user = "root";
		String password = "";
		return newMysqlConnection(host, db, user, password);
	}

	public static final Connection newMysqlConnection(int port, String db,
			String user, String password) throws ClassNotFoundException,
			SQLException {
		String host = "127.0.0.1";
		return newMysqlConnection(host, port, db, user, password);
	}

	public static final Connection newMysqlConnection(String host, String db,
			String user, String password) throws ClassNotFoundException,
			SQLException {
		int port = 3306;
		return newMysqlConnection(host, port, db, user, password);
	}

	public static final Connection newMysqlConnection(String host, int port,
			String db, String user, String password)
			throws ClassNotFoundException, SQLException {
		boolean reconnect = true;
		String encoding = "utf-8";
		return newMysqlConnection(host, port, db, reconnect, encoding, user,
				password);
	}

	public static final Connection newMysqlConnection(String host, int port,
			String db, boolean reconnect, String encoding, String user,
			String password) throws ClassNotFoundException, SQLException {
		String driver = ("com.mysql.jdbc.Driver");
		String s = "jdbc:mysql://%s:%d/%s?autoReconnect=%s&characterEncoding=%s";
		String url = s(s, host, port, db, String.valueOf(reconnect), encoding);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Oracle
	public static final Connection newOracleConnection(String host, int port,
			String db, String user, String password)
			throws ClassNotFoundException, SQLException {
		String driver = ("oracle.jdbc.driver.OracleDriver");
		String s = "jdbc:oracle:thin:@%s:%d:%s";
		String url = s(s, host, port, db);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	// ///////////////////////////////////////////////////////////////////////
	// VFP
	public Connection newVFPDbcConnection(String file)
			throws ClassNotFoundException, SQLException {
		String type = "dbc";
		return newVFPConnection(type, file);
	}

	public Connection newVFPDbfConnection(String file)
			throws ClassNotFoundException, SQLException {
		String type = "dbf";
		return newVFPConnection(type, file);
	}

	public Connection newVFPConnection(String type, String file)
			throws ClassNotFoundException, SQLException {
		String driver = ("sun.jdbc.odbc.JdbcOdbcDriver");
		String s = "jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=%s;Exclusive=No;SourceDB=%s;";
		String url = s(s, type, file);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// Sybase
	public Connection newJtdsSybaseConnection(String host, int port, String db,
			String charset) throws ClassNotFoundException, SQLException {
		String driver = ("net.sourceforge.jtds.jdbc.Driver");
		// jdbc:jtds:Sybase://192.168.2.200:5000/taxiupload;charset=cp936
		String s = "jdbc:jtds:Sybase://%s:%d/%s;charset=%s";
		String url = s(s, host, port, db, charset);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// ///////////////////////////////////////////////////////////////////////
	// PostgreSQL
	public Connection newPostgreSQLConnection(String db)
			throws ClassNotFoundException, SQLException {
		String host = "127.0.0.1";
		String charset = "UTF-8";
		return newPostgreSQLConnection(host, db, charset);
	}

	public Connection newPostgreSQLConnection(String host, String db,
			String charset) throws ClassNotFoundException, SQLException {
		int port = 5432;
		return newPostgreSQLConnection(host, port, db, charset);
	}

	public Connection newPostgreSQLConnection(String host, int port, String db,
			String charset) throws ClassNotFoundException, SQLException {
		String driver = ("org.postgresql.Driver");
		// jdbc:postgresql://hostname:port/dbname
		String s = "jdbc:postgresql://%s:%d/%s?charset=%s";
		String url = s(s, host, port, db, charset);
		Class.forName(driver);
		return DriverManager.getConnection(url);
	}

	// 开始事务
	public static final void openCommit(Connection conn) throws SQLException {
		conn.setAutoCommit(true);
	}

	// 关闭事务
	public static final void closeCommit(Connection conn) throws SQLException {
		conn.setAutoCommit(false);
	}

	// 提交事务
	public static final void commit(Connection conn) throws SQLException {
		conn.commit();
		closeCommit(conn);
	}

	// 回滚事务
	public static final void rollback(Connection conn) throws SQLException {
		conn.rollback();
		closeCommit(conn);
	}

	public static final void close(Connection conn) {
		try {
			if (conn == null)
				return;
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final List<Map> getDatabases(Connection conn)
			throws Exception {
		DatabaseMetaData dme = conn.getMetaData();
		ResultSet rs = dme.getCatalogs();
		return SqlEx.toMaps(rs);
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final List<Map> getPrimaryKeys(Connection conn, String table)
			throws Exception {
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getPrimaryKeys(null, null, table);

		List<Map> ret = toMaps(rs);
		return ret;
	}

	public static final List<Map> getImportedKeys(Connection conn, String table)
			throws Exception {
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getImportedKeys(null, null, table);

		List<Map> ret = toMaps(rs);
		return ret;
	}

	public static final List<Map> getExportedKeys(Connection conn, String table)
			throws Exception {
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getExportedKeys(null, null, table);

		List<Map> ret = toMaps(rs);
		return ret;
	}

	public static final List<String> indexColumns(Connection conn, String table)
			throws Exception {
		List<String> ret = newList();

		Map<String, List<Map<String, Object>>> map = getIndexs(conn, table);
		Set<Entry<String, List<Map<String, Object>>>> entrys = map.entrySet();

		Set<String> withIn = newSet();
		List<Map> pk = SqlEx.getPrimaryKeys(conn, table);
		for (Map map2 : pk) {
			String c = getString(map2, "COLUMN_NAME");
			ret.add(c);
			withIn.add(c);
		}
		for (Entry<String, List<Map<String, Object>>> entry : entrys) {
			List<Map<String, Object>> v = entry.getValue();
			for (Map<String, Object> map2 : v) {
				String c = getString(map2, "COLUMN_NAME");
				if (!withIn.contains(c))
					ret.add(c);
			}
		}
		return ret;
	}

	public static final Map<String, List<Map<String, Object>>> getIndexs(
			Connection conn, String table) throws Exception {
		Map ret = newMap();
		boolean unique = false;
		List<Map> indexs = getIndexInfo(conn, table, unique);
		for (Map<String, Object> m : indexs) {
			String INDEX_NAME = (String) m.get("INDEX_NAME");
			if (INDEX_NAME == null || INDEX_NAME.isEmpty())
				continue;

			List<Map<String, Object>> es = (List<Map<String, Object>>) ret
					.get(INDEX_NAME);
			if (es == null) {
				es = newList();
				ret.put(INDEX_NAME, es);
			}
			es.add(m);
		}
		return ret;
	}

	public static final List<Map> getIndexInfo(Connection conn, String table,
			boolean unique) throws Exception {
		boolean approximate = true;
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getIndexInfo(null, null, table, unique, approximate);
		return toMaps(rs);
	}

	// ///////////////////////////////////////////////////////////////////////

	public static final List<String> getTables(DataSource ds) throws Exception {
		List<String> result = newList();
		Connection conn = ds.getConnection();
		String db = conn.getCatalog();
		TableType type = TableType.TABLE;
		List<Map> tables = getTables(conn, db, type);
		for (Map map : tables) {
			String str = (String) map.get("TABLE_NAME");
			result.add(str);
		}
		return result;
	}

	public static final List<Map> getTables(Connection conn) throws Exception {
		String db = conn.getCatalog();
		TableType type = TableType.TABLE;
		return getTables(conn, db, type);
	}

	public static final List<Map> getTables(Connection conn, String db,
			TableType type) throws Exception {
		DatabaseMetaData dme = conn.getMetaData();
		ResultSet rs = dme.getTables(db, "", "",
				new String[] { type.toString() });
		return SqlEx.toMaps(rs);
	}

	private static final Map<String, Set<String>> TABLES = new HashMap<String, Set<String>>();

	public static final boolean isTableExist(Connection conn, String db,
			String table) {
		try {
			Set<String> tables = TABLES.get(db);
			if (tables == null) {
				tables = new NewSet<String>();
				TABLES.put(db, tables);
			}
			if (tables.contains(table))
				return true;

			DatabaseMetaData dme = conn.getMetaData();
			ResultSet rs = dme.getTables(db, "", table, null);
			boolean ret = false;
			while (rs.next()) {
				ret = true;
				tables.add(table);
				break;
			}
			rs.close();
			return ret;
		} catch (SQLException e) {
		}
		return false;
	}

	// ///////////////////////////////////////////////////////////////////////

	public static final List<Map> getColumns(Connection conn, String table)
			throws SQLException {
		String db = conn.getCatalog();
		return getColumns(conn, db, table);
	}

	public static final List<Map> getColumns(Connection conn, String db,
			String table) throws SQLException {
		DatabaseMetaData dme = conn.getMetaData();
		ResultSet rs = dme.getColumns(db, "", table, "");
		return SqlEx.toMaps(rs);
	}

	public static final Map<String, Map<String, Object>> mapColumns(
			Connection conn, String db, String table) throws Exception {
		Map<String, Map<String, Object>> ret = newMap();
		List<Map> m1 = getColumns(conn, db, table);
		for (Map<String, Object> map : m1) {
			String key = getString(map, "COLUMN_NAME");
			ret.put(key, map);
		}
		return ret;
	}

	public static final List<Map<String, Object>> getColumns(ResultSet rs)
			throws Exception {
		List<Map<String, Object>> ret = newList();
		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String columnName = rsmd.getColumnName(i);
			int columnType = rsmd.getColumnType(i);
			String columnLabel = rsmd.getColumnLabel(i);
			String columnTypeName = rsmd.getColumnTypeName(i);
			String catalogName = rsmd.getCatalogName(i);
			String columnClassName = rsmd.getColumnClassName(i);
			int precision = rsmd.getPrecision(i);
			int scale = rsmd.getScale(i);
			String schemaName = rsmd.getSchemaName(i);
			String tableName = rsmd.getTableName(i);
			int columnDisplaySize = rsmd.getColumnDisplaySize(i);
			boolean isAutoIncrement = rsmd.isAutoIncrement(i);
			boolean isCaseSensitive = rsmd.isCaseSensitive(i);
			boolean isCurrency = rsmd.isCurrency(i);
			boolean isDefinitelyWritable = rsmd.isDefinitelyWritable(i);
			int isNullable = rsmd.isNullable(i);
			boolean isReadOnly = rsmd.isReadOnly(i);
			boolean isSearchable = rsmd.isSearchable(i);
			boolean isSigned = rsmd.isSigned(i);
			boolean isWritable = rsmd.isWritable(i);

			Map e = newMap();
			e.put("i", i);
			e.put("columnName", columnName);
			e.put("columnType", columnType);
			e.put("columnLabel", columnLabel);
			e.put("columnTypeName", columnTypeName);
			e.put("catalogName", catalogName);
			e.put("columnClassName", columnClassName);
			e.put("precision", precision);
			e.put("scale", scale);
			e.put("schemaName", schemaName);
			e.put("tableName", tableName);
			e.put("columnDisplaySize", columnDisplaySize);
			e.put("isAutoIncrement", isAutoIncrement);
			e.put("isCaseSensitive", isCaseSensitive);
			e.put("isCurrency", isCurrency);
			e.put("isDefinitelyWritable", isDefinitelyWritable);
			e.put("isNullable", isNullable);
			e.put("isReadOnly", isReadOnly);
			e.put("isSearchable", isSearchable);
			e.put("isSigned", isSigned);
			e.put("isWritable", isWritable);
			ret.add(e);
		}
		return ret;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final PreparedStatement prepareStatement(Connection conn,
			String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	public static final boolean execute(Connection conn, String sql)
			throws SQLException {
		PreparedStatement ps = prepareStatement(conn, sql);
		boolean r2 = ps.execute();
		ps.close();
		return r2;
	}

	public static final boolean execute(Connection conn, File f)
			throws SQLException, IOException {
		String charset = "UTF-8";
		return execute(conn, f, charset);
	}

	public static final boolean execute(Connection conn, File f, String charset)
			throws SQLException, IOException {
		String sql = readFully(f, charset);
		return execute(conn, sql);
	}

	public static final ResultSet executeQuery(Connection conn, String sql)
			throws SQLException {
		PreparedStatement ps = prepareStatement(conn, sql);
		ResultSet rs = ps.executeQuery();
		return rs;
	}

	public static final int executeUpdate(Connection conn, String sql)
			throws SQLException {
		PreparedStatement ps = prepareStatement(conn, sql);
		int r2 = ps.executeUpdate();
		ps.close();
		return r2;
	}

	public static final CallableStatement callableStatement(Connection conn,
			String sql) throws SQLException {
		return conn.prepareCall(sql);
	}

	public static final boolean call(Connection conn, String sql)
			throws SQLException {
		CallableStatement stmt = callableStatement(conn, sql);
		boolean r2 = stmt.execute();
		stmt.close();
		return r2;
	}

	public static final ResultSet callQuery(Connection conn, String sql)
			throws SQLException {
		CallableStatement stmt = callableStatement(conn, sql);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	public static final int callUpdate(Connection conn, String sql)
			throws SQLException {
		CallableStatement stmt = callableStatement(conn, sql);
		int r2 = stmt.executeUpdate();
		stmt.close();
		return r2;
	}

	public static final boolean execute(PreparedStatement stmt)
			throws SQLException {
		return stmt.execute();
	}

	public static final boolean execute(CallableStatement stmt)
			throws SQLException {
		return stmt.execute();
	}

	public static final ResultSet executeQuery(PreparedStatement stmt)
			throws SQLException {
		return stmt.executeQuery();
	}

	public static final ResultSet executeQuery(CallableStatement stmt)
			throws SQLException {
		return stmt.executeQuery();
	}

	public static final int executeUpdate(PreparedStatement stmt)
			throws SQLException {
		return stmt.executeUpdate();
	}

	public static final int executeUpdate(CallableStatement stmt)
			throws SQLException {
		return stmt.executeUpdate();
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final int pageCount(int count, int pageSize) {
		return (int) pageCount((long) count, (long) pageSize);
	}

	public static final List getPage(List v, int page, int pageSize) {
		return getPage(v, (long) page, (long) pageSize);
	}

	// //////////////////////////////////
	public static final long pageCount(long count, long pageSize) {
		long page = count / pageSize;

		page = count == page * pageSize ? page : page + 1;
		return page;
	}

	public static final List getPage(List v, long page, long pageSize) {
		int count = v.size();
		int begin = (int) (page * pageSize);
		int end = (int) (begin + pageSize);
		if (begin > count || begin < 0 || end < 0)
			return newList();
		end = count < end ? count : end;
		if (end <= begin)
			return newList();
		return v.subList(begin, end);
	}

	public static final List<Map> toMaps(ResultSet rs) throws SQLException {
		List<Map> result = newList();
		while (rs.next()) {
			Map m = toMap(rs);
			result.add(m);
		}
		return result;
	}

	public static final Map toMap(ResultSet rs) throws SQLException {
		Map result = newMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			String c = rsmd.getColumnName(i);
			Object v = rs.getObject(i);
			result.put(c, v);
		}

		return result;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final String createMysqlTable(Connection conn, ResultSet rs,
			String tableName) throws Exception {
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("CREATE TABLE IF NOT EXISTS `${TABLENAME}` (\n");
			for (Map<String, Object> map : columns) {
				String columnName = MapEx.get(map, "columnName");
				// int columnType = MapEx.get(map, "columnType");
				// String columnLabel = MapEx.get(map, "columnLabel");
				String columnTypeName = MapEx.get(map, "columnTypeName");
				// String catalogName = MapEx.get(map, "catalogName");
				// String columnClassName = MapEx.get(map, "columnClassName");
				int precision = MapEx.get(map, "precision");
				int scale = MapEx.get(map, "scale");
				// String schemaName = MapEx.get(map, "schemaName");
				// String tableName = MapEx.get(map, "tableName");
				// int columnDisplaySize = MapEx.get(map, "columnDisplaySize");
				boolean isAutoIncrement = MapEx.get(map, "isAutoIncrement");
				// boolean isCaseSensitive = MapEx.get(map, "isCaseSensitive");
				// boolean isCurrency = MapEx.get(map, "isCurrency");
				// boolean isDefinitelyWritable =
				// MapEx.get(map,"isDefinitelyWritable");
				int isNullable = MapEx.get(map, "isNullable");
				// boolean isReadOnly = MapEx.get(map, "isReadOnly");
				// boolean isSearchable = MapEx.get(map, "isSearchable");
				// boolean isSigned = MapEx.get(map, "isSigned");
				// boolean isWritable = MapEx.get(map, "isWritable");

				// System.out.println(columnTypeName + "/" + precision + "/" +
				// scale);
				sb.append("\t");
				sb.append("`" + columnName + "`");
				sb.append("  ");
				if (columnTypeName.equals("VARCHAR") && precision >= 715827882) {
					sb.append("LONGTEXT");
				} else if (columnTypeName.equals("VARCHAR")
						&& precision >= 5592405) {
					sb.append("MEDIUMTEXT");
				} else if (columnTypeName.equals("VARCHAR")
						&& precision >= 21845) {
					sb.append("TEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 255) {
					sb.append("TINYTEXT");
				} else if (columnTypeName.equals("MEDIUMBLOB")
						|| columnTypeName.equals("LONGBLOB")
						|| columnTypeName.equals("BLOB")
						|| columnTypeName.equals("TINYBLOB")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DATETIME")
						|| columnTypeName.equals("DATE")
						|| columnTypeName.equals("TIME")
						|| columnTypeName.equals("TIMESTAMP")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DOUBLE")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DECIMAL")) {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(",");
					sb.append(scale);
					sb.append(")");
				} else {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(")");
				}
				if (isNullable == 0) {
					sb.append(" NOT NULL");
				}

				if (isAutoIncrement) {
					sb.append(" AUTO_INCREMENT");
				}
				sb.append(",\n");
				// System.out.println(map);
			}
			// List<Map<String, Object>> pks = SqlEx.getPrimaryKeys(conn, t);
			// for (Map<String, Object> map : pks) {
			// System.out.println(map);
			// }
			List<Map> nouniques = SqlEx.getIndexInfo(conn, tableName, false);
			// int nouniquesLength = nouniques.size();
			int i = 0;

			// 整理多键索引
			Map<String, List<Map<String, Object>>> _temp = newMap();
			for (Map<String, Object> map : nouniques) {
				String INDEX_NAME = MapEx.get(map, "INDEX_NAME");
				List<Map<String, Object>> _idx = _temp.get(INDEX_NAME);
				if (_idx == null) {
					_idx = newList();
					_temp.put(INDEX_NAME, _idx);
				}
				_idx.add(map);
			}

			Map _doit = newMap();
			for (Map<String, Object> mapx : nouniques) {
				String KEY = MapEx.get(mapx, "INDEX_NAME");
				String INDEX_NAME = MapEx.get(mapx, "INDEX_NAME");
				if (_doit.containsKey(KEY))
					continue;
				List<Map<String, Object>> v = (List<Map<String, Object>>) _temp
						.get(INDEX_NAME);
				if (v == null || v.size() <= 0)
					continue;
				String COLUMN_NAME = MapEx.get(v.get(0), "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(v.get(0).get("NON_UNIQUE"));

				sb.append("\t");
				if (INDEX_NAME.equals("PRIMARY")) {
					sb.append("PRIMARY KEY (`" + COLUMN_NAME + "`)");
				} else if (NON_UNIQUE.equals("false")) {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("UNIQUE KEY `" + INDEX_NAME + "` (");
					for (int x = 0; x < v.size(); x++) {
						Map<String, Object> map = v.get(x);
						COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						sb.append("`" + COLUMN_NAME + "`");
						if (x < v.size() - 1)
							sb.append(", ");
					}
					sb.append(")");

				} else {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("KEY `" + INDEX_NAME + "` (");
					for (int x = 0; x < v.size(); x++) {
						Map<String, Object> map = v.get(x);
						COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						sb.append("`" + COLUMN_NAME + "`");
						if (x < v.size() - 1)
							sb.append(", ");
					}
					sb.append(")");
				}
				i++;
				if (i < _temp.size()) {
					sb.append(",");
				}
				sb.append("\n");

				_doit.put(KEY, KEY);
			}

			sb.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;\n");
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final String createMysqlNoUniqueTable(Connection conn,
			ResultSet rs, String tableName) throws Exception {
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("CREATE TABLE IF NOT EXISTS `${TABLENAME}` (\n");
			for (Map<String, Object> map : columns) {
				String columnName = MapEx.get(map, "columnName");
				// int columnType = MapEx.get(map, "columnType");
				// String columnLabel = MapEx.get(map, "columnLabel");
				String columnTypeName = MapEx.get(map, "columnTypeName");
				// String catalogName = MapEx.get(map, "catalogName");
				// String columnClassName = MapEx.get(map, "columnClassName");
				int precision = MapEx.get(map, "precision");
				int scale = MapEx.get(map, "scale");
				// String schemaName = MapEx.get(map, "schemaName");
				// String tableName = MapEx.get(map, "tableName");
				// int columnDisplaySize = MapEx.get(map, "columnDisplaySize");
				boolean isAutoIncrement = MapEx.get(map, "isAutoIncrement");
				// boolean isCaseSensitive = MapEx.get(map, "isCaseSensitive");
				// boolean isCurrency = MapEx.get(map, "isCurrency");
				// boolean isDefinitelyWritable =
				// MapEx.get(map,"isDefinitelyWritable");
				int isNullable = MapEx.get(map, "isNullable");
				// boolean isReadOnly = MapEx.get(map, "isReadOnly");
				// boolean isSearchable = MapEx.get(map, "isSearchable");
				// boolean isSigned = MapEx.get(map, "isSigned");
				// boolean isWritable = MapEx.get(map, "isWritable");

				// System.out.println(columnTypeName + "/" + precision + "/" +
				// scale);
				sb.append("\t");
				sb.append("`" + columnName + "`");
				sb.append("  ");
				if (columnTypeName.equals("VARCHAR") && precision >= 715827882) {
					sb.append("LONGTEXT");
				} else if (columnTypeName.equals("VARCHAR")
						&& precision >= 5592405) {
					sb.append("MEDIUMTEXT");
				} else if (columnTypeName.equals("VARCHAR")
						&& precision >= 21845) {
					sb.append("TEXT");
				} else if (columnTypeName.equals("VARCHAR") && precision >= 255) {
					sb.append("TINYTEXT");
				} else if (columnTypeName.equals("MEDIUMBLOB")
						|| columnTypeName.equals("LONGBLOB")
						|| columnTypeName.equals("BLOB")
						|| columnTypeName.equals("TINYBLOB")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DATETIME")
						|| columnTypeName.equals("DATE")
						|| columnTypeName.equals("TIME")
						|| columnTypeName.equals("TIMESTAMP")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DOUBLE")) {
					sb.append(columnTypeName);
				} else if (columnTypeName.equals("DECIMAL")) {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(",");
					sb.append(scale);
					sb.append(")");
				} else {
					sb.append(columnTypeName);
					sb.append("(");
					sb.append(precision);
					sb.append(")");
				}
				if (isNullable == 0) {
					sb.append(" NOT NULL");
				}

				if (isAutoIncrement) {
					sb.append(" AUTO_INCREMENT");
				}
				sb.append(",\n");
				// System.out.println(map);
			}
			// List<Map<String, Object>> pks = SqlEx.getPrimaryKeys(conn, t);
			// for (Map<String, Object> map : pks) {
			// System.out.println(map);
			// }
			List<Map> nouniques = SqlEx.getIndexInfo(conn, tableName, false);
			// int nouniquesLength = nouniques.size();
			int i = 0;

			// 整理多键索引
			Map<String, List<Map<String, Object>>> _temp = newMap();
			for (Map<String, Object> map : nouniques) {
				String INDEX_NAME = MapEx.get(map, "INDEX_NAME");
				List<Map<String, Object>> _idx = _temp.get(INDEX_NAME);
				if (_idx == null) {
					_idx = newList();
					_temp.put(INDEX_NAME, _idx);
				}
				_idx.add(map);
			}

			Map _doit = newMap();
			for (Map<String, Object> mapx : nouniques) {
				String KEY = MapEx.get(mapx, "INDEX_NAME");
				String INDEX_NAME = MapEx.get(mapx, "INDEX_NAME");
				if (_doit.containsKey(KEY))
					continue;
				List<Map<String, Object>> v = (List<Map<String, Object>>) _temp
						.get(INDEX_NAME);
				if (v == null || v.size() <= 0)
					continue;
				String COLUMN_NAME = MapEx.get(v.get(0), "COLUMN_NAME");
				// String NON_UNIQUE =
				// String.valueOf(v.get(0).get("NON_UNIQUE"));

				sb.append("\t");
				if (INDEX_NAME.equals("PRIMARY")) {
					sb.append("PRIMARY KEY (`" + COLUMN_NAME + "`)");
					// } else if (NON_UNIQUE.equals("false")) {
					// INDEX_NAME = INDEX_NAME.replace(tableName,
					// "${TABLENAME}");
					// sb.append("UNIQUE KEY `" + INDEX_NAME + "` (");
					// for (int x = 0; x < v.size(); x++) {
					// Map<String, Object> map = v.get(x);
					// COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
					// sb.append("`" + COLUMN_NAME + "`");
					// if (x < v.size() - 1)
					// sb.append(", ");
					// }
					// sb.append(")");
					//
				} else {
					INDEX_NAME = INDEX_NAME.replace(tableName, "${TABLENAME}");
					sb.append("KEY `" + INDEX_NAME + "` (");
					for (int x = 0; x < v.size(); x++) {
						Map<String, Object> map = v.get(x);
						COLUMN_NAME = MapEx.get(map, "COLUMN_NAME");
						sb.append("`" + COLUMN_NAME + "`");
						if (x < v.size() - 1)
							sb.append(", ");
					}
					sb.append(")");
				}
				i++;
				if (i < _temp.size()) {
					sb.append(",");
				}
				sb.append("\n");

				_doit.put(KEY, KEY);
			}

			sb.append(") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;\n");
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final String getType(ResultSetMetaData rsmd, String columnName)
			throws SQLException {
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String key = rsmd.getColumnName(i);
			if (!key.equals(columnName))
				continue;

			return getType(rsmd, i);
		}
		return "";
	}

	public static final String getType(ResultSetMetaData rsmd, int i)
			throws SQLException {
		int count = rsmd.getColumnCount();
		if (i > count)
			return "";

		int columnType = rsmd.getColumnType(i);
		switch (columnType) {
		case java.sql.Types.ARRAY:
			return Array.class.getSimpleName();
		case java.sql.Types.BIGINT:
			return Long.class.getSimpleName();
		case java.sql.Types.BINARY:
			return "byte[]";
		case java.sql.Types.BIT:
			return Boolean.class.getSimpleName();
		case java.sql.Types.BLOB:
			return Blob.class.getName();
		case java.sql.Types.BOOLEAN:
			return Boolean.class.getSimpleName();
		case java.sql.Types.CHAR:
			return String.class.getSimpleName();
		case java.sql.Types.CLOB:
			return Clob.class.getName();
		case java.sql.Types.DATE:
			return java.util.Date.class.getName();
		case java.sql.Types.DECIMAL:
			return Integer.class.getName();
		case java.sql.Types.DISTINCT:
			break;
		case java.sql.Types.DOUBLE:
			return Double.class.getSimpleName();
		case java.sql.Types.FLOAT:
			return Float.class.getSimpleName();
		case java.sql.Types.INTEGER:
			return Integer.class.getSimpleName();
		case java.sql.Types.JAVA_OBJECT:
			return Object.class.getSimpleName();
		case java.sql.Types.LONGVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.LONGNVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.LONGVARBINARY:
			return "byte[]";
		case java.sql.Types.NCHAR:
			return String.class.getName();
		case java.sql.Types.NCLOB:
			return NClob.class.getName();
		case java.sql.Types.NULL:
			break;
		case java.sql.Types.NUMERIC:
			return Integer.class.getName();
		case java.sql.Types.NVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.OTHER:
			return Object.class.getSimpleName();
		case java.sql.Types.REAL:
			return Double.class.getSimpleName();
		case java.sql.Types.REF:
			break;
		case java.sql.Types.ROWID:
			return RowId.class.getName();
		case java.sql.Types.SMALLINT:
			return Short.class.getSimpleName();
		case java.sql.Types.SQLXML:
			return SQLXML.class.getName();
		case java.sql.Types.STRUCT:
			break;
		case java.sql.Types.TIME:
			return Time.class.getName();
		case java.sql.Types.TIMESTAMP:
			return java.util.Date.class.getName();
		case java.sql.Types.TINYINT:
			return Byte.class.getSimpleName();
		case java.sql.Types.VARBINARY:
			return "byte[]";
		case java.sql.Types.VARCHAR:
			return String.class.getSimpleName();
		default:
			break;
		}
		return "";
	}

	public static final String toJavaType(int columnType) throws SQLException {
		switch (columnType) {
		case java.sql.Types.BIT: // boolean
			return Boolean.class.getSimpleName();
		case java.sql.Types.TINYINT:
			return Byte.class.getSimpleName();
		case java.sql.Types.SMALLINT:
			return Short.class.getSimpleName();
		case java.sql.Types.INTEGER:
			return Integer.class.getSimpleName();
		case java.sql.Types.BIGINT:
			return Long.class.getSimpleName();
		case java.sql.Types.REAL:
			return Float.class.getSimpleName();
		case java.sql.Types.FLOAT:
		case java.sql.Types.DOUBLE:
			return Double.class.getSimpleName();
		case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
			return Integer.class.getSimpleName();
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			return String.class.getSimpleName();
		case java.sql.Types.DATE:
			return java.sql.Date.class.getName();
		case java.sql.Types.TIME:
			return java.sql.Time.class.getName();
		case java.sql.Types.TIMESTAMP:
			return java.sql.Timestamp.class.getName();
		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			return "byte[]";
		case java.sql.Types.BLOB:
			return java.sql.Blob.class.getName();
		case java.sql.Types.CLOB:
			return java.sql.Clob.class.getName();
		case java.sql.Types.ARRAY:
			return java.sql.Array.class.getName();
		case java.sql.Types.REF:
			return java.sql.Ref.class.getName();
		case java.sql.Types.STRUCT:
			return java.sql.Struct.class.getName();
		default:
			break;
		}
		return "";
	}

	public static final int toJdbcType(String type) {
		if (type.equals("boolean")
				|| type.equals(Boolean.class.getSimpleName())
				|| type.equals(Boolean.class.getName()))
			return java.sql.Types.BIT;
		else if (type.equals("byte") || type.equals(Byte.class.getSimpleName())
				|| type.equals(Byte.class.getName()))
			return java.sql.Types.TINYINT;
		else if (type.equals("short")
				|| type.equals(Short.class.getSimpleName())
				|| type.equals(Short.class.getName()))
			return java.sql.Types.SMALLINT;
		else if (type.equals("int")
				|| type.equals(Integer.class.getSimpleName())
				|| type.equals(Integer.class.getName()))
			return java.sql.Types.INTEGER;
		else if (type.equals("float")
				|| type.equals(Float.class.getSimpleName())
				|| type.equals(Float.class.getName()))
			return java.sql.Types.REAL;
		else if (type.equals("double")
				|| type.equals(Double.class.getSimpleName())
				|| type.equals(Double.class.getName()))
			return java.sql.Types.DOUBLE;
		else if (type.equals(BigDecimal.class.getSimpleName())
				|| type.equals(BigDecimal.class.getName()))
			return java.sql.Types.NUMERIC;
		else if (type.equals(String.class.getSimpleName())
				|| type.equals(String.class.getName()))
			return java.sql.Types.VARCHAR; // LONGVARCHAR
		else if (type.equals("byte[]"))
			return java.sql.Types.VARBINARY; // LONGVARBINARY
		else if (type.equals(java.util.Date.class.getSimpleName())
				|| type.equals(java.util.Date.class.getName())
				|| type.equals(java.sql.Date.class.getSimpleName())
				|| type.equals(java.sql.Date.class.getName()))
			return java.sql.Types.DATE;
		else if (type.equals(java.sql.Time.class.getSimpleName())
				|| type.equals(java.sql.Time.class.getName()))
			return java.sql.Types.TIME;
		else if (type.equals(java.sql.Timestamp.class.getSimpleName())
				|| type.equals(java.sql.Timestamp.class.getName()))
			return java.sql.Types.TIMESTAMP;
		else if (type.equals(java.sql.Blob.class.getSimpleName())
				|| type.equals(java.sql.Blob.class.getName()))
			return java.sql.Types.BLOB;
		else if (type.equals(java.sql.Clob.class.getSimpleName())
				|| type.equals(java.sql.Clob.class.getName()))
			return java.sql.Types.CLOB;
		else if (type.equals(java.sql.Array.class.getSimpleName())
				|| type.equals(java.sql.Array.class.getName()))
			return java.sql.Types.ARRAY;
		else if (type.equals(java.sql.Ref.class.getSimpleName())
				|| type.equals(java.sql.Ref.class.getName()))
			return java.sql.Types.REF;
		else if (type.equals(java.sql.Struct.class.getSimpleName())
				|| type.equals(java.sql.Struct.class.getName()))
			return java.sql.Types.STRUCT;

		return 0;
	}

	public static final String toJdbcType2(String type) {
		if (type.equals("boolean")
				|| type.equals(Boolean.class.getSimpleName())
				|| type.equals(Boolean.class.getName()))
			return "BIT";
		else if (type.equals("byte") || type.equals(Byte.class.getSimpleName())
				|| type.equals(Byte.class.getName()))
			return "TINYINT";
		else if (type.equals("short")
				|| type.equals(Short.class.getSimpleName())
				|| type.equals(Short.class.getName()))
			return "SMALLINT";
		else if (type.equals("int")
				|| type.equals(Integer.class.getSimpleName())
				|| type.equals(Integer.class.getName()))
			return "INTEGER";
		else if (type.equals("float")
				|| type.equals(Float.class.getSimpleName())
				|| type.equals(Float.class.getName()))
			return "REAL";
		else if (type.equals("double")
				|| type.equals(Double.class.getSimpleName())
				|| type.equals(Double.class.getName()))
			return "DOUBLE";
		else if (type.equals(BigDecimal.class.getSimpleName())
				|| type.equals(BigDecimal.class.getName()))
			return "NUMERIC";
		else if (type.equals(String.class.getSimpleName())
				|| type.equals(String.class.getName()))
			return "VARCHAR"; // LONGVARCHAR
		else if (type.equals("byte[]"))
			return "VARBINARY"; // LONGVARBINARY
		else if (type.equals(java.util.Date.class.getSimpleName())
				|| type.equals(java.util.Date.class.getName())
				|| type.equals(java.sql.Date.class.getSimpleName())
				|| type.equals(java.sql.Date.class.getName()))
			return "DATE";
		else if (type.equals(java.sql.Time.class.getSimpleName())
				|| type.equals(java.sql.Time.class.getName()))
			return "TIME";
		else if (type.equals(java.sql.Timestamp.class.getSimpleName())
				|| type.equals(java.sql.Timestamp.class.getName()))
			return "TIMESTAMP";
		else if (type.equals(java.sql.Blob.class.getSimpleName())
				|| type.equals(java.sql.Blob.class.getName()))
			return "BLOB";
		else if (type.equals(java.sql.Clob.class.getSimpleName())
				|| type.equals(java.sql.Clob.class.getName()))
			return "CLOB";
		else if (type.equals(java.sql.Array.class.getSimpleName())
				|| type.equals(java.sql.Array.class.getName()))
			return "ARRAY";
		else if (type.equals(java.sql.Ref.class.getSimpleName())
				|| type.equals(java.sql.Ref.class.getName()))
			return "REF";
		else if (type.equals(java.sql.Struct.class.getSimpleName())
				|| type.equals(java.sql.Struct.class.getName()))
			return "STRUCT";

		return "";
	}

	public static final String getDefaultValue(String type) throws SQLException {
		if (type.equals("boolean") || type.equals("Boolean")) {
			return "false";
		} else if (type.equals("byte") || type.equals("Byte")
				|| type.equals("java.lang.Byte") || type.equals("short")
				|| type.equals("Short") || type.equals("java.lang.Short")
				|| type.equals("int") || type.equals("Integer")
				|| type.equals("java.lang.Integer") || type.equals("long")) {
			return "0";
		} else if (type.equals("Long") || type.equals("java.lang.Long")) {
			return "0L";
		} else if (type.equals("float") || type.equals("Float")
				|| type.equals("java.lang.Float") || type.equals("double")
				|| type.equals("Double") || type.equals("java.lang.Double")) {
			return "0.0";
		} else if (type.equals("Date") || type.equals("java.util.Date")) {
			return "new java.util.Date()";
		} else if (type.equals("java.sql.Date")) {
			return "new java.sql.Date()";
		} else if (type.equals("String") || type.equals("java.lang.String")) {
			return "\"\"";
		} else if (type.equals("byte[]")) {
			return "new byte[0]";
		} else if (type.equals("Time") || type.equals("java.sql.Time")) {
			return "new java.sql.Time(System.currentTimeMillis())";
		} else if (type.equals("Timestamp")
				|| type.equals("java.sql.Timestamp")) {
			return "new java.sql.Timestamp(System.currentTimeMillis())";
		} else if (type.contains("oracle.sql.TIMESTAMP")) {
			return "new oracle.sql.TIMESTAMP()";
		} else if (type.contains("BigDecimal")) {
			return "new java.math.BigDecimal(0)";
		} else if (type.contains("Blob") || type.contains("BLOB")) {
			return "null";
		} else if (type.contains("Clob") || type.contains("CLOB")) {
			return "null";
		}
		return "";
	}

	// //
	// ///////////////////////////////////////////////////////////////////////
	// // ///////////////////////////////////////////////////
	// // 数据库处理单线程池
	// static Executor _singleExecutor = null;
	//
	// public static void executeSingle(Runnable r) {
	// if (_singleExecutor == null)
	// _singleExecutor = Executors.newSingleThreadExecutor();
	// _singleExecutor.execute(r);
	// }
	//
	// // 4线程并发线程池
	// static Executor _4FixedExecutor = null;
	//
	// public static void execute4Fixed(Runnable r) {
	// if (_4FixedExecutor == null)
	// _4FixedExecutor = Executors.newFixedThreadPool(4);
	// _4FixedExecutor.execute(r);
	// }

}
