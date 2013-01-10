package com.bowlong.sql.builder.jdbc.mysql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class BatchOP {
	public static String setOP(ResultSetMetaData rsmd, String columnName)
			throws SQLException {
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String key = rsmd.getColumnName(i);
			if (!key.equals(columnName))
				continue;

			return setOP(rsmd, i);
		}
		return "";
	}

	public static String setOP(ResultSetMetaData rsmd, int i)
			throws SQLException {
		int count = rsmd.getColumnCount();
		if (i > count)
			return "";
		int columnType = rsmd.getColumnType(i);
		switch (columnType) {
		case java.sql.Types.ARRAY:
			return "setArray";
		case java.sql.Types.BIGINT:
			return "setLong";
		case java.sql.Types.BINARY:
			return "setBytes";
		case java.sql.Types.BIT:
			return "setBoolean";
		case java.sql.Types.BLOB:
			return "setBlob";
		case java.sql.Types.BOOLEAN:
			return "setBoolean";
		case java.sql.Types.CHAR:
			return "setString";
		case java.sql.Types.CLOB:
			return "setClob";
		case java.sql.Types.DATE:
			return "setDate";
		case java.sql.Types.DECIMAL:
			return "setBigDecimal";
		case java.sql.Types.DISTINCT:
			break;
		case java.sql.Types.DOUBLE:
			return "setDouble";
		case java.sql.Types.FLOAT:
			return "setFloat";
		case java.sql.Types.INTEGER:
			return "setInt";
		case java.sql.Types.JAVA_OBJECT:
			return "setObject";
		case java.sql.Types.LONGVARCHAR:
			return "setString";
		case java.sql.Types.LONGNVARCHAR:
			return "setString";
		case java.sql.Types.LONGVARBINARY:
			return "setBytes";
		case java.sql.Types.NCHAR:
			return "setString";
		case java.sql.Types.NCLOB:
			return "setNClob";
		case java.sql.Types.NULL:
			break;
		case java.sql.Types.NUMERIC:
			return "setBigDecimal";
		case java.sql.Types.NVARCHAR:
			return "setString";
		case java.sql.Types.OTHER:
			return "setObject";
		case java.sql.Types.REAL:
			return "setDouble";
		case java.sql.Types.REF:
			break;
		case java.sql.Types.ROWID:
			return "setRowId";
		case java.sql.Types.SMALLINT:
			return "setShort";
		case java.sql.Types.SQLXML:
			return "setSQLXML";
		case java.sql.Types.STRUCT:
			break;
		case java.sql.Types.TIME:
			return "setTime";
		case java.sql.Types.TIMESTAMP:
			return "setTimestamp";
		case java.sql.Types.TINYINT:
			return "setByte";
		case java.sql.Types.VARBINARY:
			return "setBytes";
		case java.sql.Types.VARCHAR:
			return "setString";
		default:
			break;
		}
		return "";
	}

	public static String getOP(ResultSetMetaData rsmd, String columnName)
			throws SQLException {
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) {
			String key = rsmd.getColumnName(i);
			if (!key.equals(columnName))
				continue;

			return getOP(rsmd, i);
		}
		return "";
	}

	public static String getOP(ResultSetMetaData rsmd, int i)
			throws SQLException {
		int count = rsmd.getColumnCount();
		if (i > count)
			return "";
		int columnType = rsmd.getColumnType(i);
		switch (columnType) {
		case java.sql.Types.ARRAY:
			return "getArray";
		case java.sql.Types.BIGINT:
			return "getLong";
		case java.sql.Types.BINARY:
			return "getBytes";
		case java.sql.Types.BIT:
			return "getBoolean";
		case java.sql.Types.BLOB:
			return "getBlob";
		case java.sql.Types.BOOLEAN:
			return "getBoolean";
		case java.sql.Types.CHAR:
			return "getString";
		case java.sql.Types.CLOB:
			return "getClob";
		case java.sql.Types.DATE:
			return "getDate";
		case java.sql.Types.DECIMAL:
			return "getBigDecimal";
		case java.sql.Types.DISTINCT:
			break;
		case java.sql.Types.DOUBLE:
			return "getDouble";
		case java.sql.Types.FLOAT:
			return "getFloat";
		case java.sql.Types.INTEGER:
			return "getInt";
		case java.sql.Types.JAVA_OBJECT:
			return "getObject";
		case java.sql.Types.LONGVARCHAR:
			return "getString";
		case java.sql.Types.LONGNVARCHAR:
			return "getString";
		case java.sql.Types.LONGVARBINARY:
			return "getBytes";
		case java.sql.Types.NCHAR:
			return "getString";
		case java.sql.Types.NCLOB:
			return "getNClob";
		case java.sql.Types.NULL:
			break;
		case java.sql.Types.NUMERIC:
			return "getBigDecimal";
		case java.sql.Types.NVARCHAR:
			return "getString";
		case java.sql.Types.OTHER:
			return "getObject";
		case java.sql.Types.REAL:
			return "getDouble";
		case java.sql.Types.REF:
			break;
		case java.sql.Types.ROWID:
			return "getRowId";
		case java.sql.Types.SMALLINT:
			return "getShort";
		case java.sql.Types.SQLXML:
			return "getSQLXML";
		case java.sql.Types.STRUCT:
			break;
		case java.sql.Types.TIME:
			return "getTime";
		case java.sql.Types.TIMESTAMP:
			return "getTimestamp";
		case java.sql.Types.TINYINT:
			return "getByte";
		case java.sql.Types.VARBINARY:
			return "getBytes";
		case java.sql.Types.VARCHAR:
			return "getString";
		default:
			break;
		}
		return "";
	}

}
