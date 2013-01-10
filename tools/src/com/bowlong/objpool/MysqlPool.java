package com.bowlong.objpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlPool extends AbstractQueueObjPool<Connection> {

	public String host = "127.0.0.1";
	public int port = 3306;
	public String db = "test";
	public String user = "root";
	public String password = "";
	public boolean reconnect = true;
	public String encoding = "utf-8";

	public MysqlPool() {
	}

	public MysqlPool(String db) {
		this.db = db;
	}

	public MysqlPool(String host, String db) {
		this.host = host;
		this.db = db;
	}

	public MysqlPool(String host, int port, String db) {
		this.host = host;
		this.port = port;
		this.db = db;
	}

	public MysqlPool(String db, String user, String password) {
		this.db = db;
		this.user = user;
		this.password = password;
	}

	public MysqlPool(String host, int port, String db, String user,
			String password) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.password = password;
	}

	public MysqlPool(String host, int port, String db, String user,
			String password, String encoding) {
		this.host = host;
		this.port = port;
		this.db = db;
		this.user = user;
		this.password = password;
		this.encoding = encoding;
	}

	private static final String s(String s, Object... args) {
		return String.format(s, args);
	}

	private Connection newMysqlConnection() throws ClassNotFoundException,
			SQLException {
		String driver = ("com.mysql.jdbc.Driver");
		String s = "jdbc:mysql://%s:%d/%s?autoReconnect=%s&characterEncoding=%s";
		String url = s(s, host, port, db, String.valueOf(reconnect), encoding);
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);
	}

	public void setMax(int num) {
		this.MAX = num;
	}

	@Override
	protected Connection createObj() {
		try {
			Connection conn = newMysqlConnection();
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected Connection resetObj(Connection obj) {
		return obj;
	}

	@Override
	protected Connection destoryObj(Connection obj) {
		try {
			obj.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public Connection getConnection() {
		return this.borrow();
	}

	public void release(Connection conn) {
		this.returnObj(conn);
	}

	// /////////////////////////////////////////////////
	public static MysqlPool POOL = new MysqlPool();

	public static void setHost(String host) {
		POOL.host = host;
	}

	public static void setPort(int port) {
		POOL.port = port;
	}

	public static void setDb(String db) {
		POOL.db = db;
	}

	public static void setUser(String user) {
		POOL.user = user;
	}

	public static void setPassword(String password) {
		POOL.password = password;
	}

	public static void setReconnect(boolean reconnect) {
		POOL.reconnect = reconnect;
	}

	public static void setEncoding(String encoding) {
		POOL.encoding = encoding;
	}

	public static Connection borrowObject() {
		return POOL.borrow();
	}

	public static void returnObject(Connection conn) {
		POOL.returnObj(conn);
	}

}
