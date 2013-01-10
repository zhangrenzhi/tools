package com.bowlong.sql.sqlserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.bowlong.objpool.StringBufPool;
import com.bowlong.sql.mysql.BeanSupport;
import com.bowlong.sql.mysql.JdbcTemplate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataSet extends JdbcTemplate {

	private String TABLENAME;

	public DataSet(Connection conn, String TABLENAME) {
		super(conn);
		this.TABLENAME = TABLENAME;
	}

	public DataSet(DataSource ds, String TABLENAME) {
		super(ds);
		this.TABLENAME = TABLENAME;
	}

	public DataSet(DataSource ds_r, DataSource ds_w, String TABLENAME) {
		super(ds_r, ds_w);
		this.TABLENAME = TABLENAME;
	}

	public int count(String c) throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("SELECT COUNT(*) FROM ").append(TABLENAME);
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE ").append(c);
			}
			String sql = sb.toString();
			return super.queryForInt(sql);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public int count() throws SQLException {
		return count("");
	}

	public int pageCount(String c, int size) throws SQLException {
		int count = count(c);
		return super.pageCount(count, size);
	}

	public int pageCount(int size) throws SQLException {
		return pageCount("", size);
	}

	public List<Map> queryAll() throws SQLException {
		return queryForList("");
	}

	public <T> List<T> queryAll(Class c2) throws Exception {
		return queryForList("", c2);
	}

	public <T> T queryForObject(String c, Class c2) throws Exception {
		List<T> dataset = queryForList(c, c2);
		if (dataset == null || dataset.isEmpty())
			return null;
		return dataset.get(0);
	}

	public Map queryForMap(String c) throws SQLException {
		List<Map> dataset = queryForList(c);
		if (dataset == null || dataset.isEmpty())
			return null;
		return dataset.get(0);
	}

	public List<Map> queryForList(String c) throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("SELECT * FROM ").append(TABLENAME);
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE ").append(c);
			}
			String sql = sb.toString();
			return super.queryForList(sql);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public <T> List<T> queryForList(String c, Class c2) throws Exception {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("SELECT * FROM ").append(TABLENAME);
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE ").append(c);
			}
			String sql = sb.toString();
			return super.queryForList(sql, c2);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	// public List<Map> queryForList(String c, String idKey, int begin, int num)
	// throws SQLException {
	// StringBuffer sb = new StringBuffer();
	// sb.append("SELECT TOP ").append(num).append(" * FROM ").append(TABLENAME);
	// if (c != null && !c.isEmpty()) {
	// sb.append(" WHERE (").append(c).append(") AND ( ")
	// .append(idKey).append(" NOT IN (SELECT TOP ").append(begin).append(" ").append(idKey).append(" FROM ")
	// .append(TABLENAME).append(" WHERE (").append(c).append(") ORDER BY ").append(idKey).append("))");
	// } else {
	// sb.append(" WHERE ( ")
	// .append(idKey).append(" NOT IN (SELECT TOP ").append(begin).append(" ").append(idKey).append(" FROM ")
	// .append(TABLENAME).append(" ORDER BY ").append(idKey).append("))");
	// }
	// sb.append(" ORDER BY ").append(idKey);
	// String sql = sb.toString();
	// return super.queryForList(sql);
	// }

	public List<Map> queryForList(String c, String idKey, int begin, int num)
			throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("SELECT TOP ").append(num).append(" * FROM ")
					.append(TABLENAME);
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE (").append(c).append(") AND (").append(idKey)
						.append(" > SELECT MAX(").append(idKey)
						.append(") FROM (SELECT TOP ").append(begin)
						.append(" ").append(idKey).append(" FROM ")
						.append(TABLENAME).append(" WHERE (").append(c)
						.append(") ORDER BY ").append(idKey).append(") AS T)");
			} else {
				sb.append(" WHERE (").append(idKey).append(" > SELECT MAX(")
						.append(idKey).append(") FROM (SELECT TOP ")
						.append(begin).append(" ").append(idKey)
						.append(" FROM ").append(TABLENAME)
						.append(" ORDER BY ").append(idKey).append(") AS T)");
			}
			sb.append(" ORDER BY ").append(idKey);
			String sql = sb.toString();
			return super.queryForList(sql);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	// public <T> List<T> queryForList(String c, String idKey, Class c2, int
	// begin, int num) throws Exception {
	// StringBuffer sb = new StringBuffer();
	// sb.append("SELECT TOP ").append(num).append(" * FROM ").append(TABLENAME);
	// if (c != null && !c.isEmpty()) {
	// sb.append(" WHERE (").append(c).append(") AND ( ")
	// .append(idKey).append(" NOT IN (SELECT TOP ").append(begin).append(" ").append(idKey).append(" FROM ")
	// .append(TABLENAME).append(" WHERE (").append(c).append(") ORDER BY ").append(idKey).append("))");
	// } else {
	// sb.append(" WHERE ( ")
	// .append(idKey).append(" NOT IN (SELECT TOP ").append(begin).append(" ").append(idKey).append(" FROM ")
	// .append(TABLENAME).append(" ORDER BY ").append(idKey).append("))");
	// }
	// sb.append(" ORDER BY ").append(idKey);
	// String sql = sb.toString();
	// return super.queryForList(sql, c2);
	// }

	public <T> List<T> queryForList(String c, String idKey, Class c2,
			int begin, int num) throws Exception {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("SELECT TOP ").append(num).append(" * FROM ")
					.append(TABLENAME);
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE (").append(c).append(") AND ( ")
						.append(idKey).append(" > SELECT MAX(").append(idKey)
						.append(") FROM (SELECT TOP ").append(begin)
						.append(" ").append(idKey).append(" FROM ")
						.append(TABLENAME).append(" WHERE (").append(c)
						.append(") ORDER BY ").append(idKey).append(") AS T)");
			} else {
				sb.append(" WHERE (").append(idKey).append(" > SELECT MAX(")
						.append(idKey).append(") FROM (SELECT TOP ")
						.append(begin).append(" ").append(idKey)
						.append(" FROM ").append(TABLENAME)
						.append(" ORDER BY ").append(idKey).append(") AS T)");
			}
			sb.append(" ORDER BY ").append(idKey);
			String sql = sb.toString();
			return super.queryForList(sql, c2);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public List<Map> queryForList(String idKey, int begin, int num)
			throws SQLException {
		return queryForList("", begin, num);
	}

	public <T> List<T> queryForList(String idKey, Class c2, int begin, int num)
			throws Exception {
		return queryForList("", idKey, c2, begin, num);
	}

	public int insert(BeanSupport x) throws SQLException {
		return insert(x.toBasicMap());
	}

	public int insert(Map<String, Object> m) throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			List<String> keys = newList();
			keys.addAll(m.keySet());
			int num = keys.size();
			sb.append("INSERT INTO ").append(TABLENAME).append(" (");
			for (int i = 0; i < num; i++) {
				sb.append(keys.get(i));
				if (i < num - 1) {
					sb.append(", ");
				}
			}
			sb.append(") VALUES (");
			for (int i = 0; i < num; i++) {
				sb.append(":").append(keys.get(i));
				if (i < num - 1) {
					sb.append(", ");
				}
			}
			sb.append(")");
			String sql = sb.toString();
			Map r = super.insert(sql, m);
			if (r == null)
				return 0;
			return getInt(r, "GENERATED_KEY");
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public int update(BeanSupport x, String c) throws SQLException {
		return update(x.toBasicMap(), c);
	}

	public int update(Map<String, Object> m, String c) throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			List<String> keys = newList();
			keys.addAll(m.keySet());
			int num = keys.size();
			sb.append("UPDATE ").append(TABLENAME).append(" SET ");
			for (int i = 0; i < num; i++) {
				String key = keys.get(i);
				sb.append(key).append("=:").append(key);
				if (i < num - 1) {
					sb.append(", ");
				}
			}
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE ").append(c);
			}
			String sql = sb.toString();
			return super.update(sql, m);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public int delete(Map<String, Object> m, String c) throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("DELETE FROM ");
			sb.append(TABLENAME);
			if (c != null && !c.isEmpty()) {
				sb.append(" WHERE ");
				sb.append(c);
			}
			String sql = sb.toString();
			return super.update(sql, m);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public int delete(BeanSupport x, String c) throws SQLException {
		return delete(x.toBasicMap(), c);
	}

	public int delete(String c) throws SQLException {
		return delete(newMap(), c);
	}

}
