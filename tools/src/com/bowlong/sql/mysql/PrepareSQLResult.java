package com.bowlong.sql.mysql;

import java.util.List;

import com.bowlong.objpool.StringBufPool;

public class PrepareSQLResult {
	public String sql;
	public List<String> keys; // ×Ö¶Î

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public String toString() {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("[").append(sql).append(", ").append(keys).append("]");
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}
}
