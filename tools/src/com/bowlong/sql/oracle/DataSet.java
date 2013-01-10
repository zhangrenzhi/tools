package com.bowlong.sql.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.bowlong.objpool.StringBufPool;
import com.bowlong.sql.mysql.BeanSupport;
import com.bowlong.sql.mysql.JdbcTemplate;

@SuppressWarnings({ "rawtypes", "unchecked" })
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

	/*
	 * 从 begin 条 开始, num 条记录 SELECT * FROM ( SELECT ROWNUM r, a.* FROM 表名 a
	 * WHERE 条件 ORDER BY id ) b WHERE b.r >= begin AND b.r < begin + num ;
	 */
	public List<Map> queryForList(String c, String idKey, int begin, int num)
			throws SQLException {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			if (c != null && !c.isEmpty()) {
				sb.append("SELECT * FROM (SELECT ROWNUM r, a.* FROM ")
						.append(TABLENAME).append(" a WHERE ").append(c)
						.append(" ORDER BY ").append(idKey)
						.append(") b WHERE b.r >= m AND b.r < m + n ");
			} else {
				sb.append("SELECT * FROM (SELECT ROWNUM r, a.* FROM ")
						.append(TABLENAME).append(" a ").append(" ORDER BY ")
						.append(idKey)
						.append(") b WHERE b.r >= m AND b.r < m + n  ");
			}
			sb.append(" ORDER BY ").append(idKey);
			String sql = sb.toString();
			return super.queryForList(sql);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public <T> List<T> queryForList(String c, String idKey, Class c2,
			int begin, int num) throws Exception {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			if (c != null && !c.isEmpty()) {
				sb.append("SELECT * FROM (SELECT ROWNUM r, a.* FROM ")
						.append(TABLENAME).append(" a WHERE ").append(c)
						.append(" ORDER BY ").append(idKey)
						.append(") b WHERE b.r >= m AND b.r < m + n ");
			} else {
				sb.append("SELECT * FROM (SELECT ROWNUM r, a.* FROM ")
						.append(TABLENAME).append(" a ").append(" ORDER BY ")
						.append(idKey)
						.append(") b WHERE b.r >= m AND b.r < m + n  ");
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
