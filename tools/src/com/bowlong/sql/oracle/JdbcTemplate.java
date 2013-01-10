package com.bowlong.sql.oracle;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import org.apache.commons.dbcp.DelegatingConnection;

import com.bowlong.objpool.StringBufPool;
import com.bowlong.util.FormatEx;
import com.sun.rowset.CachedRowSetImpl;

@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class JdbcTemplate {
	// /////////////////////////
	private static final Map<String, PrepareSQLResult> SQLCHCHE = newMap();
	private static final Map<Class, ResultSetHandler> RSHCHCHE = newMap();

	private static final ResultSetHandler getRsh(Class c) throws Exception {
		ResultSetHandler rsh = RSHCHCHE.get(c);
		if (rsh == null) {
			rsh = (ResultSetHandler) c.newInstance();
			RSHCHCHE.put(c, rsh);
		}
		return rsh;
	}

	// /////////////////////////
	public static final SimpleDateFormat sdfYy = FormatEx.getFormat("yyyy");
	public static final SimpleDateFormat sdfMm = FormatEx.getFormat("yyyyMM");
	public static final SimpleDateFormat sdfDd = FormatEx.getFormat("yyyyMMdd");

	// /////////////////////////
	public static final int getInt(Map m, Object key) {
		Object obj = m.get(key);
		if (obj instanceof Integer) {
			return ((Integer) obj).intValue();
		} else if (obj instanceof Long) {
			return ((Long) obj).intValue();
		} else if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).intValue();
		} else if (obj instanceof String) {
			try {
				return Integer.parseInt((String) obj);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public static final long getLong(Map m, Object key) {
		Object obj = m.get(key);
		if (obj instanceof Integer) {
			return ((Integer) obj).longValue();
		} else if (obj instanceof Long) {
			return ((Long) obj).longValue();
		} else if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).intValue();
		} else if (obj instanceof String) {
			try {
				return Long.parseLong((String) obj);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	// /////////////////////////
	Connection conn; // 单链接模式

	DataSource ds_r; // 读写分离模式
	DataSource ds_w;

	// /////////////////////////
	public JdbcTemplate(Connection conn) {
		this.conn = conn;
	}

	public JdbcTemplate(DataSource ds) {
		this.ds_r = ds;
		this.ds_w = ds;
	}

	public JdbcTemplate(DataSource ds_r, DataSource ds_w) {
		this.ds_r = ds_r;
		this.ds_w = ds_w;
	}

	public DataSource ds_r() {
		return ds_r;
	}

	public DataSource ds_w() {
		return ds_w;
	}

	// /////////////////////////
	public void close() {
		try {
			if (this.conn != null && !this.conn.isClosed()) {
				this.conn.close();
			}
		} catch (Exception e) {
		} finally {
			this.conn = null;
			this.ds_r = null;
			this.ds_w = null;
		}
	}

	private void close(Connection conn) throws SQLException {
		if (this.conn != null) // 如果是单链接模式则不关闭链接
			return;

		conn.close();
	}

	// /////////////////////////
	public Connection conn_r() throws SQLException {
		if (this.conn != null)
			return this.conn;

		// Connection conn = ds_r.getConnection();
		// return ((DelegatingConnection) conn).getInnermostDelegate();
		return ds_r.getConnection();
	}

	public Connection conn_w() throws SQLException {
		if (this.conn != null)
			return this.conn;

		// Connection conn = ds_w.getConnection();
		// return ((DelegatingConnection) conn).getInnermostDelegate();
		return ds_w.getConnection();
	}

	// /////////////////////////

	public void execute(String sql) throws SQLException {
		Connection conn = conn_w();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public CachedRowSet query(String sql) throws SQLException {
		Connection conn = conn_r();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			CachedRowSet crs = new CachedRowSetImpl();
			crs.populate(rs);
			rs.close();
			stmt.close();
			return crs;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	// private <T> T query(String sql, Class c) throws Exception {
	// ResultSetHandler rsh = getRsh(c);
	// return query(sql, rsh);
	// }

	private <T> T query(String sql, ResultSetHandler rsh) throws SQLException {
		Connection conn = conn_r();
		try {
			T r2 = null;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = (T) rsh.handle(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> T queryForObject(String sql, Class c) throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return queryForObject(sql, rsh);
	}

	public <T> T queryForObject(String sql, ResultSetHandler rsh)
			throws SQLException {
		return query(sql, rsh);
	}

	public Map queryForMap(String sql) throws SQLException {
		Connection conn = conn_r();
		try {
			Map r2 = null;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = toMap(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public List<Map> queryForList(String sql) throws SQLException {
		Connection conn = conn_r();
		try {
			List<Map> r2 = null;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			r2 = toMaps(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> List<T> queryForKeys(String sql) throws SQLException {
		Connection conn = conn_r();
		try {
			List<T> r2 = null;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			r2 = toKeys(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> List<T> queryForList(String sql, Class c) throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return queryForList(sql, rsh);
	}

	public <T> List<T> queryForList(String sql, ResultSetHandler rsh)
			throws SQLException {
		Connection conn = conn_r();
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			List<T> result = newList();
			while (rs.next()) {
				T v = (T) rsh.handle(rs);
				result.add(v);
			}
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public long queryForLong(String sql) throws SQLException {
		Connection conn = conn_r();
		try {
			long r2 = 0;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = rs.getLong(1);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int queryForInt(String sql) throws SQLException {
		Connection conn = conn_r();
		try {
			int r2 = 0;
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = rs.getInt(1);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public RowSet queryForRowSet(String sql) throws SQLException {
		return query(sql);
	}

	public int update(String sql) throws SQLException {
		Connection conn = conn_w();
		try {
			int r2 = 0;
			PreparedStatement stmt = conn.prepareStatement(sql);
			r2 = stmt.executeUpdate();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int[] batchUpdate(String as[]) throws SQLException {
		Connection conn = conn_w();
		try {
			int r2[] = null;
			Statement stmt = conn.createStatement();
			for (String sql : as) {
				stmt.addBatch(sql);
			}
			r2 = stmt.executeBatch();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public void call(String sql) throws SQLException {
		Connection conn = conn_w();
		try {
			CallableStatement stmt = conn.prepareCall(sql);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public List<Map> queryByCall(String sql) throws SQLException {
		Connection conn = conn_w();
		try {
			List<Map> r2 = null;
			CallableStatement stmt = conn.prepareCall(sql);
			ResultSet rs = stmt.executeQuery();
			r2 = toMaps(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	// /////////////////////////

	private static final PrepareSQLResult prepareKeys(String sql) {
		if (SQLCHCHE.containsKey(sql)) {
			// 从缓存中读取
			return SQLCHCHE.get(sql);
		}

		PrepareSQLResult result = new PrepareSQLResult();

		List<String> keys = new Vector<String>();
		// 没有缓存,则从头获取
		String sql2 = sql;
		int index = 0;
		while (true) {
			index++;
			int p1 = sql2.indexOf(":");
			if (p1 < 0)
				break;
			p1 = p1 + ":".length();
			int p2 = sql2.indexOf(",", p1);
			int p3 = sql2.indexOf(" ", p1);
			int p4 = sql2.indexOf(")", p1);
			int p5 = sql2.length();
			if (p3 > 0)
				p2 = (p2 >= 0 && p2 < p3) ? p2 : p3;
			if (p4 > 0)
				p2 = (p2 >= 0 && p2 < p4) ? p2 : p4;
			if (p5 > 0)
				p2 = (p2 >= 0 && p2 < p5) ? p2 : p5;

			String key = sql2.substring(p1, p2).trim();
			String okey = String.format(":%s", key);
			sql2 = sql2.replaceFirst(okey, "?");
			keys.add(key);
		}

		result.setSql(sql2);
		result.setKeys(keys);

		// 写入缓存
		SQLCHCHE.put(sql, result);

		return result;
	}

	private static final PreparedStatement prepareMap(PreparedStatement stmt,
			List<String> keys, Map m) throws SQLException {
		int index = 0;
		for (String key : keys) {
			index++;
			Object var = m.get(key);
			stmt.setObject(index, var);
		}
		return stmt;
	}

	// /////////////////////////
	public void execute(String sql, Map params) throws SQLException {
		Connection conn = conn_w();
		try {
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public Map insert(String sql, String sqlId, Map params) throws SQLException {
		Connection conn = conn_w();
		try {
			Map r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepareMap(stmt, sr.keys, params);
			int r = stmt.executeUpdate();
			if (r < 0)
				throw new SQLException(" r = 0");

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				RowId rowid = rs.getRowId(1);
				PreparedStatement stmt2 = conn.prepareStatement(sqlId);
				stmt2.setRowId(1, rowid);
				ResultSet rs2 = stmt2.executeQuery();
				if (rs2.next())
					r2 = toMap(rs2);
			}
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int[] insert(String sql, List<Map> list) throws SQLException {
		Connection conn = conn_w();
		try {
			int r2[] = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			for (Map map : list) {
				prepareMap(stmt, sr.keys, map);
				stmt.addBatch();
			}
			r2 = stmt.executeBatch();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public CachedRowSet query(String sql, Map params) throws SQLException {
		Connection conn = conn_r();
		try {
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			CachedRowSet crs = new CachedRowSetImpl();
			crs.populate(rs);
			rs.close();
			stmt.close();
			return crs;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> T query(String sql, Map params, Class c) throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return query(sql, params, rsh);
	}

	public <T> T query(String sql, Map params, ResultSetHandler rsh)
			throws SQLException {
		Connection conn = conn_r();
		try {
			T r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = (T) rsh.handle(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> T queryForObject(String sql, Map params, Class c)
			throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return queryForObject(sql, params, rsh);
	}

	public <T> T queryForObject(String sql, Map params, ResultSetHandler rsh)
			throws SQLException {
		return query(sql, params, rsh);
	}

	public Map queryForMap(String sql, Map params) throws SQLException {
		Connection conn = conn_r();
		try {
			Map r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = toMap(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public List<Map> queryForList(String sql, Map params) throws SQLException {
		Connection conn = conn_r();
		try {
			List<Map> r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			r2 = toMaps(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> List<T> queryForKeys(String sql, Map params) throws SQLException {
		Connection conn = conn_r();
		try {
			List<T> r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			r2 = toKeys(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> List<T> queryForList(String sql, Map params, Class c)
			throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return queryForList(sql, params, rsh);
	}

	public <T> List<T> queryForList(String sql, Map params, ResultSetHandler rsh)
			throws SQLException {
		Connection conn = conn_r();
		try {
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			List<T> result = newList();
			while (rs.next()) {
				T v = (T) rsh.handle(rs);
				result.add(v);
			}
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public long queryForLong(String sql, Map params) throws SQLException {
		Connection conn = conn_r();
		try {
			long r2 = 0;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = rs.getLong(1);
			rs.close();
			stmt.close();
			return 0;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int queryForInt(String sql, Map params) throws SQLException {
		Connection conn = conn_r();
		try {
			int r2 = 0;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = rs.getInt(1);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public RowSet queryForRowSet(String sql, Map params) throws SQLException {
		return query(sql, params);
	}

	public int update(String sql, Map params) throws SQLException {
		Connection conn = conn_w();
		try {
			int r2 = 0;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			r2 = stmt.executeUpdate();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int[] batchUpdate(String sql, List<Map> list) throws SQLException {
		Connection conn = conn_w();
		try {
			int r2[] = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			for (Map map : list) {
				prepareMap(stmt, sr.keys, map);
				stmt.addBatch();
			}
			r2 = stmt.executeBatch();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public void call(String sql, Map params) throws SQLException {
		Connection conn = conn_w();
		try {
			PrepareSQLResult sr = prepareKeys(sql);
			CallableStatement stmt = conn.prepareCall(sr.sql);
			prepareMap(stmt, sr.keys, params);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public List<Map> queryBycall(String sql, Map params) throws SQLException {
		Connection conn = conn_w();
		try {
			List<Map> r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			CallableStatement stmt = conn.prepareCall(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			r2 = toMaps(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	// /////////////////////////
	public void execute(String sql, BeanSupport x) throws SQLException {
		Connection conn = conn_w();
		try {
			Map params = x.toBasicMap();
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public Map insert(String sql, String sqlId, BeanSupport x)
			throws SQLException {
		Connection conn = conn_w();
		try {
			Map r2 = null;
			Map params = x.toBasicMap();
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql,
					PreparedStatement.RETURN_GENERATED_KEYS);
			prepareMap(stmt, sr.keys, params);
			int r = stmt.executeUpdate();
			if (r < 0)
				throw new SQLException(" r = 0");

			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				RowId rowid = rs.getRowId(1);
				PreparedStatement stmt2 = conn.prepareStatement(sqlId);
				stmt2.setRowId(1, rowid);
				ResultSet rs2 = stmt2.executeQuery();
				if (rs2.next())
					r2 = toMap(rs2);
			}
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int[] batchInsert(String sql, List list) throws SQLException {
		Connection conn = conn_w();
		try {
			int[] r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			for (Object x : list) {
				Map params = ((BeanSupport) x).toBasicMap();
				prepareMap(stmt, sr.keys, params);
				stmt.addBatch();
			}
			r2 = stmt.executeBatch();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public CachedRowSet query(String sql, BeanSupport x) throws SQLException {
		Connection conn = conn_r();
		try {
			Map params = x.toBasicMap();
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			CachedRowSet crs = new CachedRowSetImpl();
			crs.populate(rs);
			rs.close();
			stmt.close();
			return crs;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> T query(String sql, BeanSupport x, Class c) throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return query(sql, x, rsh);
	}

	public <T> T query(String sql, BeanSupport x, ResultSetHandler rsh)
			throws SQLException {
		Map params = x.toBasicMap();
		Connection conn = conn_r();
		try {
			T r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = (T) rsh.handle(rs);
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}

		return null;
	}

	public <T> T queryForObject(String sql, BeanSupport x, Class c)
			throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return query(sql, x, rsh);
	}

	public <T> T queryForObject(String sql, BeanSupport x, ResultSetHandler rsh)
			throws SQLException {
		Map params = x.toBasicMap();
		return query(sql, params, rsh);
	}

	public Map queryForMap(String sql, BeanSupport x) throws SQLException {
		Map params = x.toBasicMap();
		Connection conn = conn_r();
		try {
			Map r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = toMap(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public List<Map> queryForList(String sql, BeanSupport x)
			throws SQLException {
		Map params = x.toBasicMap();
		Connection conn = conn_r();
		try {
			List<Map> r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			r2 = toMaps(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public <T> List<T> queryForList(String sql, BeanSupport x, Class c)
			throws Exception {
		ResultSetHandler rsh = getRsh(c);
		return queryForList(sql, x, rsh);
	}

	public <T> List<T> queryForList(String sql, BeanSupport x,
			ResultSetHandler rsh) throws SQLException {
		Map params = x.toBasicMap();
		Connection conn = conn_r();
		try {
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			List<T> result = newList();
			while (rs.next()) {
				T v = (T) rsh.handle(rs);
				result.add(v);
			}
			rs.close();
			stmt.close();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public long queryForLong(String sql, BeanSupport x) throws SQLException {
		Map params = x.toBasicMap();
		Connection conn = conn_r();
		try {
			long r2 = 0;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = rs.getLong(1);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int queryForInt(String sql, BeanSupport x) throws SQLException {
		Map params = x.toBasicMap();
		Connection conn = conn_r();
		try {
			int r2 = 0;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			if (rs.next())
				r2 = rs.getInt(1);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public RowSet queryForRowSet(String sql, BeanSupport x) throws SQLException {
		Map params = x.toBasicMap();
		return query(sql, params);
	}

	public int update(String sql, BeanSupport x) throws SQLException {
		Connection conn = conn_w();
		try {
			int r2 = 0;
			Map params = x.toBasicMap();
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			prepareMap(stmt, sr.keys, params);
			r2 = stmt.executeUpdate();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public int[] batchUpdate2(String sql, List list) throws SQLException {
		Connection conn = conn_w();
		try {
			int[] r2 = null;
			PrepareSQLResult sr = prepareKeys(sql);
			PreparedStatement stmt = conn.prepareStatement(sr.sql);
			for (Object x : list) {
				Map map = ((BeanSupport) x).toBasicMap();
				prepareMap(stmt, sr.keys, map);
				stmt.addBatch();
			}
			r2 = stmt.executeBatch();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public void call(String sql, BeanSupport x) throws SQLException {
		Connection conn = conn_w();
		try {
			Map params = x.toBasicMap();
			PrepareSQLResult sr = prepareKeys(sql);
			CallableStatement stmt = conn.prepareCall(sr.sql);
			prepareMap(stmt, sr.keys, params);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	public List<Map> queryByCall(String sql, BeanSupport x) throws SQLException {
		Connection conn = conn_w();
		try {
			List<Map> r2 = null;
			Map params = x.toBasicMap();
			PrepareSQLResult sr = prepareKeys(sql);
			CallableStatement stmt = conn.prepareCall(sr.sql);
			prepareMap(stmt, sr.keys, params);
			ResultSet rs = stmt.executeQuery();
			r2 = toMaps(rs);
			rs.close();
			stmt.close();
			return r2;
		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn);
		}
	}

	// /////////////////////////

	public static final List newList() {
		return new Vector();
	}

	public static final Map newMap() {
		return new Hashtable();
	}

	// /////////////////////////
	// 错误堆栈的内容
	public static final String e2s(Exception e) {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append(e);
			sb.append("\r\n");
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append("at ");
				sb.append(ste);
				sb.append("\r\n");
			}
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	// ///////////////////////////////////////////////////
	public static final boolean isTimeout(long LASTTIME, long TIMEOUT) {
		long l2 = System.currentTimeMillis();
		return isTimeout(l2, LASTTIME, TIMEOUT);
	}

	public static final boolean isTimeout(long TIME1, long TIME2, long TIMEOUT) {
		if (TIMEOUT <= 0)
			return false;
		long l2 = TIME1;
		long t = l2 - TIME2;
		return (t > TIMEOUT);
	}

	public static final boolean isTimeout(Date DLASTTIME, long TIMEOUT) {
		if (DLASTTIME == null)
			return false;
		long LASTTIME = DLASTTIME.getTime();
		return isTimeout(TIMEOUT, LASTTIME);
	}

	// ///////////////////////////////////////////////////
	public static final List<Map> toMaps(ResultSet rs) throws SQLException {
		List<Map> result = new Vector();
		while (rs.next()) {
			Map m = toMap(rs);
			result.add(m);
		}
		return result;
	}

	public static final <T> List<T> toKeys(ResultSet rs) throws SQLException {
		List<T> result = new Vector();
		while (rs.next()) {
			Object o = rs.getObject(1);
			result.add((T) o);
		}
		return result;
	}

	public static final Map toMap(ResultSet rs) throws SQLException {
		Map result = newMap();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		for (int i = 1; i <= cols; i++)
			result.put(rsmd.getColumnName(i), rs.getObject(i));

		return result;
	}

	// ///////////////////////////////////////////////////////////////////////
	public static final int pageCount(int count, int pageSize) {
		int page = count / pageSize;

		page = count == page * pageSize ? page : page + 1;
		return page;
	}

	public static final List getPage(List v, int page, int pageSize) {
		int count = v.size();
		int begin = page * pageSize;
		int end = begin + pageSize;
		if (begin > count || begin < 0 || end < 0)
			return new Vector();
		end = count < end ? count : end;
		if (end <= begin)
			new Vector();
		return v.subList(begin, end);
	}

	// ///////////////////////////////////////////////////////////////////////
	// public void truncate(String TABLENAME2) throws SQLException {
	// String sql = "TRUNCATE TABLE " + TABLENAME2 + "";
	// this.update(sql);
	// }
	//
	// public void repair(String TABLENAME2) throws SQLException {
	// String sql = "REPAIR TABLE " + TABLENAME2 + "";
	// this.update(sql);
	// }
	//
	// public void optimize(String TABLENAME2) throws SQLException {
	// String sql = "OPTIMIZE TABLE " + TABLENAME2 + "";
	// this.update(sql);
	// }

	public static void main(String[] args) throws Exception {
		// DataSource ds = Dbcp.newMysql("fych").dataSource();
		// JdbcTemplate jt = new JdbcTemplate(ds);
		// String TABLENAME2 = "Copyright";
		// String sql =
		// "INSERT INTO Copyright (name, version) VALUES (:name, :version)";
		// Copyright c = Copyright.newCopyright(0L, "name -- 0", "version");
		// ResultSet rs = jt.insert(sql, c);
		// System.out.println(SqlEx.toMaps(rs));
		// String sql = "SELECT id, name, version FROM " + TABLENAME2
		// + " WHERE id = :id";
		//
		// Copyright x = Copyright.newCopyright(200L, "name -- 0", "version");
		// Copyright c2 = jt.queryForObject(sql, x,
		// new ResultSetHandler<Copyright>() {
		// public Copyright handle(ResultSet rs) throws SQLException {
		// Map e = SqlEx.toMap(rs);
		// return Copyright.mapTo(e);
		// }
		// });
		// System.out.println(c2);
	}
}
