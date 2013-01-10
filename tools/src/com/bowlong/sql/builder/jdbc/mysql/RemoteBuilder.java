package com.bowlong.sql.builder.jdbc.mysql;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bowlong.Abstract;
import com.bowlong.lang.StrEx;
import com.bowlong.pinyin.PinYin;
import com.bowlong.sql.SqlEx;
import com.bowlong.util.MapEx;

@SuppressWarnings("unused")
public class RemoteBuilder extends Abstract {
	public static void main(String[] args) throws Exception {
		String sql = "SELECT * FROM 用户 LIMIT 1";
		String host = "127.0.0.1";
		String db = "sea";
		String bpackage = "sea.db";
		String appContext = "xkx.context.AppContext";
		Connection conn = SqlEx.newMysqlConnection(host, db);
		boolean batch = true;
		ResultSet rs = SqlEx.executeQuery(conn, sql);

		String xml = build(conn, rs, bpackage, appContext, batch);
		System.out.println(xml);

	}

	public static String build(Connection conn, ResultSet rs, String pkg, String appContext, boolean batch) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		List<Map<String, Object>> columns = SqlEx.getColumns(rs);
		String catalogName = (String) columns.get(0).get("catalogName");
		String table = (String) columns.get(0).get("tableName");
		String tableEn = PinYin.getShortPinYin(table);
		String tableUEn = StrEx.upperFirst(tableEn);
		Map<String, List<Map<String, Object>>> indexs = SqlEx.getIndexs(conn, table);
		String primaryKey = BeanBuilder.primaryKey(rsmd, columns);
		String primaryKeyType = JavaType.getType(rsmd, primaryKey);
		String pkBasicType = JavaType.getBasicType(primaryKeyType);
//		String columns1 = BeanBuilder.columns1(rsmd, columns);
//		String columns2 = BeanBuilder.columns2(rsmd, columns);
//		String columns3 = BeanBuilder.columns3(rsmd, columns);
//		String columns4 = BeanBuilder.columns4(rsmd, columns);
//		String columns5 = BeanBuilder.columns5(rsmd, columns);
//		String columns6 = BeanBuilder.columns6(rsmd, columns);
//		String columns7 = BeanBuilder.columns7(rsmd, columns);
//		String columns8 = BeanBuilder.columns8(rsmd, columns);
//		String columns9 = BeanBuilder.columns9(rsmd, columns);
		
		sn(sb, "package %s.remote;", pkg);
		sn(sb, "");
		sn(sb, "import java.net.*;");
		sn(sb, "import java.rmi.*;");
		sn(sb, "import java.rmi.server.*;");
		sn(sb, "import java.util.*;");
		sn(sb, "import %s.bean.*;", pkg);
		sn(sb, "import %s.entity.*;", pkg);
		sn(sb, "import %s.rmi.*;", pkg);
		
		sn(sb, "");
		sn(sb, "//%s - %s", catalogName, table);
		sn(sb, "@SuppressWarnings(\"serial\")");
		sn(sb, "public class %sRemote implements %sRMI{", tableUEn, tableUEn);
		sn(sb, "");
		sn(sb, "    public %sRemote() throws RemoteException {", tableUEn);
		sn(sb, "        super();");
		sn(sb, "    }");
		sn(sb, "");
		
		///////////////////////
		sn(sb, "    public int count() throws RemoteException {");
		sn(sb, "        return %sEntity.count();", tableUEn);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public int count(String TABLENAME2) throws RemoteException {");
		sn(sb, "        return %sEntity.count(TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> sort(List<%s> %ss)  throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.sort(%ss);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> sortReverse(List<%s> %ss)  throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.sortReverse(%ss);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		{
		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
//				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				String basicType = JavaType.getBasicType(COLUMN_NAME_TYPE);
				
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				
				if (!basicType.equals("int") && !basicType.equals("java.util.Date"))
					continue;

				sn(sb, "    public List<%s> sort%s(List<%s> %ss) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, tableUEn, tableEn);
				sn(sb, "        return %sEntity.sort%s(%ss);", tableUEn, COLUMN_NAME_UEN, tableEn);
				sn(sb, "    }");
				sn(sb, "");
				sn(sb, "    public List<%s> sort%sRo(List<%s> %ss) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, tableUEn, tableEn);
				sn(sb, "        return %sEntity.sort%sRo(%ss);", tableUEn, COLUMN_NAME_UEN, tableEn);
				sn(sb, "    }");
				sn(sb, "");
			}else{ // 多索引
			}
		}
		}
		
		sn(sb, "    public %s insert(%s %s) throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.insert(%s);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s insert(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.insert(%s, TABLENAME2);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s insert2(%s %s) throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.insert2(%s);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s insert2(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.insert2(%s, TABLENAME2);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		if(batch){ // 批处理
		sn(sb, "    public int[] insert(List<%s> %ss) throws RemoteException {", tableUEn, tableEn);
		sn(sb, "        return %sEntity.insert(%ss);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int[] insert(List<%s> %ss, String TABLENAME2) throws RemoteException {", tableUEn, tableEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.insert(%ss, TABLENAME2);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");
		}

		sn(sb, "    public int delete(%s %s) throws RemoteException {", pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.delete(%s);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int delete(%s %s, String TABLENAME2) throws RemoteException {", pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.delete(%s, TABLENAME2);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		if(batch){ // 批处理
		sn(sb, "    public int[] delete(%s[] %ss) throws RemoteException {", pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.delete(%ss);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int[] delete(%s[] %ss,String TABLENAME2)throws RemoteException {", pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.delete(%ss, TABLENAME2);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		}
		
		if(batch){
		sn(sb, "    public int deleteIn(List<%s> keys) throws RemoteException {", primaryKeyType);
		sn(sb, "        return %sEntity.deleteIn(keys);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int deleteIn(List<%s> keys, String TABLENAME2) throws RemoteException {", primaryKeyType);
		sn(sb, "        return %sEntity.deleteIn(keys, TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		}
		
		sn(sb, "    public List<%s> getAll() throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getAll();", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getAll(String TABLENAME2) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getAll(TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getNoSortAll() throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getNoSortAll();", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getNoSortAll(String TABLENAME2) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getNoSortAll(TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");
		
		sn(sb, "    public List<%s> getPks() throws RemoteException {", primaryKeyType);
		sn(sb, "        return %sEntity.getPks();", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getPks(String TABLENAME2) throws RemoteException {", primaryKeyType);
		sn(sb, "        return %sEntity.getPks(TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getInIndex() throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getInIndex();", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getInIndex(String TABLENAME2) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getInIndex(TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		if(batch){
		sn(sb, "    public List<%s> getIn(List<%s> keys) throws RemoteException {", tableUEn, primaryKeyType);
		sn(sb, "        return %sEntity.getIn(keys);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getIn(List<%s> keys, String TABLENAME2) throws RemoteException {", tableUEn, primaryKeyType);
		sn(sb, "        return %sEntity.getIn(keys, TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getNoSortIn(List<%s> keys) throws RemoteException {", tableUEn, primaryKeyType);
		sn(sb, "        return %sEntity.getNoSortIn(keys);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getNoSortIn(List<%s> keys, String TABLENAME2) throws RemoteException {", tableUEn, primaryKeyType);
		sn(sb, "        return %sEntity.getNoSortIn(keys, TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");
		
		}
		
		sn(sb, "    public List<%s> getLast(int num) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getLast(num);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getLast(int num, String TABLENAME2) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getLast(num, TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s last() throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.last();", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s last(String TABLENAME2) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.last(TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getGtKey(%s %s) throws RemoteException {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.getGtKey(%s);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getGtKey(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.getGtKey(%s, TABLENAME2);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s getByKey(%s %s) throws RemoteException {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.getByKey(%s);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s getByKey(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, pkBasicType, primaryKey);
		sn(sb, "        return %sEntity.getByKey(%s, TABLENAME2);", tableUEn, primaryKey);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getByPage(int page, int size) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getByPage(page, size);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public List<%s> getByPage(int page, int size, String TABLENAME2) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.getByPage(page, size, TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		////
		sn(sb, "    public int pageCount(int size) throws RemoteException {", tableUEn);
		sn(sb, "        return %sEntity.pageCount(size);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public int pageCount(int size, String TABLENAME2) throws RemoteException {");
		sn(sb, "        return %sEntity.pageCount(size, TABLENAME2);", tableUEn);
		sn(sb, "    }");
		sn(sb, "");

		Iterator<String> it = indexs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<Map<String, Object>> idx = indexs.get(key);
			int idx_size = idx.size();
			if(idx_size == 1){ // 单索引
				Map<String, Object> index = idx.get(0);
				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
				String COLUMN_NAME_BTYPE = JavaType.getBasicType(COLUMN_NAME_TYPE);
				if(INDEX_NAME.equals("PRIMARY"))
					continue;
				if(COLUMN_NAME_TYPE.equals("java.util.Date"))
					continue;
				if(NON_UNIQUE.equals("false")){
					sn(sb, "    public %s getBy%s(%s %s) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return %sEntity.getBy%s(%s);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public %s getBy%s(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return %sEntity.getBy%s(%s, TABLENAME2);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

				}else{
					sn(sb, "    public int countBy%s(%s %s) throws RemoteException {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return %sEntity.countBy%s(%s);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int countBy%s(%s %s, String TABLENAME2) throws RemoteException {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return %sEntity.countBy%s(%s, TABLENAME2);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> getBy%s(%s %s) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return %sEntity.getBy%s(%s);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> getBy%s(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
					sn(sb, "        return %sEntity.getBy%s(%s, TABLENAME2);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
					sn(sb, "    }");
					sn(sb, "");
				} 
				
				// like 
				if(COLUMN_NAME_TYPE.equals("String")){
				sn(sb, "    public int countLike%s(%s %s) throws RemoteException {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return %sEntity.countLike%s(%s);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public int countLike%s(%s %s, String TABLENAME2) throws RemoteException {", COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return %sEntity.countLike%s(%s, TABLENAME2);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public List<%s> getLike%s(%s %s) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return %sEntity.getLike%s(%s);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				sn(sb, "    public List<%s> getLike%s(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_TYPE, COLUMN_NAME_EN);
				sn(sb, "        return %sEntity.getLike%s(%s, TABLENAME2);", tableUEn, COLUMN_NAME_UEN, COLUMN_NAME_EN);
				sn(sb, "    }");
				sn(sb, "");

				}
				////////////////
				
			}else { // 多键索引
				Map<String, Object> index = idx.get(0);
//				String INDEX_NAME = MapEx.get(index, "INDEX_NAME");
//				String COLUMN_NAME = MapEx.get(index, "COLUMN_NAME");
				String NON_UNIQUE = String.valueOf(index.get("NON_UNIQUE"));
//				String COLUMN_NAME_EN = PinYin.getShortPinYin(COLUMN_NAME);
//				String COLUMN_NAME_UEN = StrEx.upperFirst(COLUMN_NAME_EN);
//				String COLUMN_NAME_TYPE = JavaType.getType(rsmd, COLUMN_NAME);
//				String COLUMN_NAME_BTYPE = JavaType.getBasicType(COLUMN_NAME_TYPE);
				String index1 = BeanBuilder.index1(rsmd, idx);
				String index2 = BeanBuilder.index2(rsmd, idx);
				String index3 = BeanBuilder.index3(rsmd, idx);
//				String index4 = BeanBuilder.index4(rsmd, idx);
				String index5 = BeanBuilder.index5(rsmd, idx);
				if(NON_UNIQUE.equals("false")){ // 唯一数据
					sn(sb, "    public %s getBy%s(%s) throws RemoteException {", tableUEn, index1, index2);
					sn(sb, "        return %sEntity.getBy%s(%s);", tableUEn, index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public %s getBy%s(%s, String TABLENAME2) throws RemoteException {", tableUEn, index1, index2);
					sn(sb, "        return %sEntity.getBy%s(%s, TABLENAME2);", tableUEn, index1, index3);
					sn(sb, "    }");
					sn(sb, "");
				}else{ // 非唯一数据
					{
					sn(sb, "    public int countBy%s(%s) throws RemoteException {", index1, index2);
					sn(sb, "        return %sEntity.countBy%s(%s);", tableUEn, index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public int countBy%s(%s, String TABLENAME2) throws RemoteException {", index1, index2);
					sn(sb, "        return %sEntity.countBy%s(%s, TABLENAME2);", tableUEn, index1, index3);
					sn(sb, "    }");
					sn(sb, "");
					}
					{
					sn(sb, "    public List<%s> getBy%s(%s) throws RemoteException {", tableUEn, index1, index2);
					sn(sb, "        return %sEntity.getBy%s(%s);", tableUEn, index1, index3);
					sn(sb, "    }");
					sn(sb, "");

					sn(sb, "    public List<%s> getBy%s(%s, String TABLENAME2) throws RemoteException {", tableUEn, index1, index2);
					sn(sb, "        return %sEntity.getBy%s(%s, TABLENAME2);", tableUEn, index1, index3);
					sn(sb, "    }");
					sn(sb, "");
					}

				}
			}
			
		}
		
		sn(sb, "    public %s update(%s %s) throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.update(%s);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public %s update(%s %s, String TABLENAME2) throws RemoteException {", tableUEn, tableUEn, tableEn);
		sn(sb, "        return %sEntity.update(%s, TABLENAME2);", tableUEn, tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String bind(String host, int port) throws MalformedURLException, RemoteException, AlreadyBoundException{", tableUEn);
		sn(sb, "        return bind(host, port, \"%sI\");", tableEn);
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "    public static String bind(String host, int port, String name) throws MalformedURLException, RemoteException, AlreadyBoundException{", tableUEn);
		sn(sb, "        %sRMI remote = new %sRemote();", tableUEn, tableUEn);
		sn(sb, "        String url = String.format(\"rmi://%%s:%%d/%%s\", host, port, name);");
		sn(sb, "        UnicastRemoteObject.exportObject(remote, port);");
		sn(sb, "        Naming.bind(url, remote);");
		sn(sb, "        return url;");
		sn(sb, "    }");
		sn(sb, "");

		sn(sb, "}");
		
		return sb.toString();
	}
}
