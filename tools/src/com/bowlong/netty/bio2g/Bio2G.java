package com.bowlong.netty.bio2g;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

import com.bowlong.bio2.B2Helper;
import com.bowlong.lang.PStr;
import com.bowlong.lang.StrEx;
import com.bowlong.netty.bio2.TcpChannel;
import com.bowlong.util.NewList;
import com.bowlong.util.NewMap;
import com.bowlong.util.StrBuilder;

@SuppressWarnings({ "unused" })
public class Bio2G {
	public static void b2g(Class<?> c) throws Exception {
		B2Class B2C = c.getAnnotation(B2Class.class);
		String namespace = "";
		if (B2C != null) {
			namespace = B2C.namespace();
		}
		Class<?>[] classes = c.getDeclaredClasses();
		String p = "gen_b2g";
		if (namespace != null && !namespace.isEmpty()) {
			p = p + "/" + namespace;
		}

		for (Class<?> class1 : classes) {
			String sname = class1.getSimpleName();
			if (B2G.isServer(class1)) {
				File path = new File(p);
				if (!path.exists())
					path.mkdirs();

				{
					StrBuilder sb = new StrBuilder();
					g2s_service(class1, namespace, sb);
					writeFile(p + "/" + sname + ".java", sb.toString());
					System.out.println(sb);
				}
				{
					StrBuilder sb2 = new StrBuilder();
					g2s_call(class1, namespace, sb2);
					writeFile(p + "/" + "Call" + sname + ".java",
							sb2.toString());
					System.out.println(sb2);
				}
			} else if (B2G.isData(class1)) {
				String p2 = p + "/bean";
				File path = new File(p2);
				if (!path.exists())
					path.mkdirs();

				String f = class1.getSimpleName();
				StrBuilder sb = new StrBuilder();
				if (B2G.isConstant(class1)) {
					g2beanConstant(class1, namespace, sb);
				} else {
					g2bean(class1, namespace, sb);
				}
				writeFile(p2 + "/" + f + ".java", sb.toString());
				System.out.println(sb);
			}
		}
	}

	public static void g2bean(Class<?> c, String namespace, StrBuilder sb) {
		Field[] fs = c.getDeclaredFields();
		String cname = c.getSimpleName();
		int hcname = cname.hashCode();
		sb.pn("package gen_b2g${1}.bean;", StrEx.isEmpty(namespace) ? "" : "."
				+ namespace);
		sb.pn("");
//		sb.pn("import java.io.*;");
		sb.pn("import java.util.*;");
		sb.pn("");
//		sb.pn("import com.bowlong.bio2.*;");
		sb.pn("import com.bowlong.util.*;");
		sb.pn("");
		// sb.pn("@SuppressWarnings({ \"rawtypes\", \"unchecked\", \"serial\", \"unused\" })");
		sb.pn("@SuppressWarnings({ \"rawtypes\", \"unchecked\" })");
		sb.pn("public class ${1} {", cname);
		sb.pn("    public static final int _CID = ${1};", hcname);
		sb.pn("");
		for (Field field : fs) {
			B2Field a = field.getAnnotation(B2Field.class);
			String t = B2G.getType(field);
			String s = field.getName();
			if (s.contains("$"))
				continue;

			String remark = B2G.getRemark(field);
			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				if (gtype != null && !gtype.isEmpty()) {
					sb.pn("    public ${1}<${4}> ${2} = new NewList();; ${3}",
							t, s, remark, gtype);
				}
			} else {
				if (t.contains("String")) {
					sb.pn("    public ${1} ${2} = \"\"; ${3}", t, s, remark);
				} else if (t.contains("Map")) {
					sb.pn("    public ${1} ${2} = new NewMap(); ${3}", t, s,
							remark);
				} else {
					sb.pn("    public ${1} ${2}; ${3}", t, s, remark);
				}
			}
		}

		// ///////
		sb.pn("");
		for (Field field : fs) {
			String s = field.getName();
			if (s.contains("$"))
				continue;
			String s1 = StrEx.upperFirst(s);
			String t = B2G.getType(field);
			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				boolean isBtype = B2G.isBType(gtype);
				if (gtype != null && !gtype.isEmpty() && !isBtype) {
					sb.pn("    public List<Map> ${1}_maps() {", s);
					sb.pn("        List<Map> r = new NewList<Map>();");
					sb.pn("        if(${1} == null) return r;", s);
					sb.pn("        for(${1} _e : ${2}) {", gtype, s);
					sb.pn("            Map e = _e.toMap();");
					sb.pn("            if(e == null) continue;");
					sb.pn("            r.add(e);");
					sb.pn("        }");
					sb.pn("        return r;");
					sb.pn("    }");
					sb.pn("");

					sb.pn("    public static List<${2}> maps_${1}(List<Map> maps) {",
							s, gtype);
					sb.pn("        List r = new NewList();");
					sb.pn("        for(Map _e : maps) {", gtype, s);
					sb.pn("            ${1} e = ${1}.parse(_e);", gtype);
					sb.pn("            if(e == null) continue;");
					sb.pn("            r.add(e);");
					sb.pn("        }");
					sb.pn("        return r;");
					sb.pn("    }");
					sb.pn("");

//					sb.pn("    public void write${1}(OutputStream _out) throws Exception {",
//							s1);
//					sb.pn("        int size = ${1}.size();", s);
//					sb.pn("        B2OutputStream.writeObject(_out, size);");
//					sb.pn("        for(${1} _e : ${2}) {", gtype, s);
//					sb.pn("            _e.writeObject(_out);");
//					sb.pn("        }");
//					sb.pn("    }");
//					sb.pn("");
//
//					sb.pn("    public static List<${2}> read${1}(InputStream _in) throws Exception {",
//							s1, gtype);
//					sb.pn("        List r = new NewList();");
//					sb.pn("        int size = (Integer) B2InputStream.readObject(_in);");
//					sb.pn("        for (int i = 0; i < size; i++) {");
//					sb.pn("            ${1} e = ${1}.readObject(_in);", gtype);
//					sb.pn("            r.add(e);");
//					sb.pn("        }");
//					sb.pn("        return r;");
//					sb.pn("    }");
//					sb.pn("");
				}
			}
		}

		sb.pn("");
		sb.pn("    public Map toMap() {");
		sb.pn("        Map r = new NewMap();");
		sb.pn("        r.put(${1}, _CID);", B2G.BEAN);
		for (Field field : fs) {
			String t = B2G.getType(field);
			String gm = B2G.getMapType(t);
			String s = field.getName();
			int hs = s.hashCode();
			if (s.contains("$"))
				continue;

			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				boolean isBType = B2G.isBType(gtype);
				if (gtype != null && !gtype.isEmpty() && !isBType) {
					sb.pn("        r.put(${1}, ${2}_maps());", hs, s);
				} else {
					sb.pn("        r.put(${1}, ${2});", hs, s);
				}
			} else {
				if (gm.equals("getObject")) {
					sb.pn("        r.put(${1}, ${2}.toMap());", hs, s);
				} else {
					sb.pn("        r.put(${1}, ${2});", hs, s);
				}
			}
		}
		sb.pn("        return r;");
		sb.pn("    }");
		sb.pn("");

		sb.pn("");
		sb.pn("    public static ${1} parse(Map map) {", cname);
		sb.pn("        if(map == null) return null;");
		sb.pn("");
		sb.pn("        NewMap map2 = NewMap.create(map);");
		sb.pn("        ${1} r = new ${1}();", cname);
		for (Field field : fs) {
			String t = B2G.getType(field);
			String gm = B2G.getMapType(t);
			String s = field.getName();
			int hs = s.hashCode();
			if (s.contains("$"))
				continue;

			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				boolean isBType = B2G.isBType(gtype);
				if (gtype != null && !gtype.isEmpty() && !isBType) {
					sb.pn("        r.${1} = maps_${1}( map2.getList(${3}) );",
							s, gm, hs, gtype);
				} else {
					sb.pn("        r.${1} = map2.${2}(${3});", s, gm, hs);
				}
			} else {
				if (gm.equals("getObject")) {
					sb.pn("        r.${1} = ${2}.parse(map2.getNewMap(${3}));",
							s, t, hs);
				} else {
					sb.pn("        r.${1} = map2.${2}(${3});", s, gm, hs);
				}
			}
		}
		sb.pn("        return r;");
		sb.pn("    }");
		sb.pn("");

//		sb.pn("");
//		sb.pn("    public void writeObject(OutputStream _out) throws Exception {");
//		sb.pn("        B2OutputStream.writeObject(_out, _CID);");
//		for (Field field : fs) {
//			String t = B2G.getType(field);
//			String gm = B2G.getMapType(t);
//			String s = field.getName();
//			String s1 = StrEx.upperFirst(s);
//			int hs = s.hashCode();
//			if (s.contains("$"))
//				continue;
//
//			if (field.getType().equals(List.class)) {
//				String gtype = B2G.getListType(field);
//				boolean isBType = B2G.isBType(gtype);
//				if (gtype != null && !gtype.isEmpty() && !isBType) {
//					sb.pn("        write${1}(_out);", s1);
//				} else {
//					sb.pn("        B2OutputStream.writeObject(_out, ${1});", s);
//				}
//			} else {
//				if (gm.equals("getObject")) {
//					sb.pn("        ${1}.writeObject(_out);", s);
//				} else {
//					sb.pn("        B2OutputStream.writeObject(_out, ${1});", s);
//				}
//			}
//		}
//		sb.pn("    }");
//		sb.pn("");
//
//		sb.pn("");
//		sb.pn("    public static ${1} readObject(InputStream _in) throws Exception {",
//				cname);
//		sb.pn("        ${1} r = new ${1}();", cname);
//		for (Field field : fs) {
//			String jtype = B2G.getJType(field);
//			String t = B2G.getType(field);
//			String gm = B2G.getMapType(t);
//			String s = field.getName();
//			String s1 = StrEx.upperFirst(s);
//			int hs = s.hashCode();
//			if (s.contains("$"))
//				continue;
//
//			if (field.getType().equals(List.class)) {
//				String gtype = B2G.getListType(field);
//				boolean isBType = B2G.isBType(gtype);
//				if (gtype != null && !gtype.isEmpty() && !isBType) {
//					sb.pn("        r.${1} = read${2}(_in);", s, s1);
//				} else {
//					sb.pn("        r.${1} = (${2}) B2InputStream.readObject(_in);",
//							s, jtype);
//				}
//			} else {
//				if (gm.equals("getObject")) {
//					sb.pn("        r.${1} = ${2}.readObject(_in);", s, t);
//				} else {
//					sb.pn("        r.${1} = (${2}) B2InputStream.readObject(_in);",
//							s, jtype);
//				}
//			}
//		}
//		sb.pn("        return r;");
//		sb.pn("    }");
//		sb.pn("");

		StrBuilder sbts = new StrBuilder();
		sbts.a("\"").a(cname).a("[");
		for (Field field : fs) {
			String t = B2G.getType(field);
			String gm = B2G.getMapType(t);
			String s = field.getName();
			if (s.contains("$"))
				continue;
			sbts.a("").a(s).a("=\" + ").a(s).a(" + \", ");
		}
		sbts.removeRight(2);
		sbts.a("]\"");
		sb.pn("    public String toString() {");
		sb.pn("        return ${1};", sbts);
		sb.pn("    }");
		sb.pn("");

		sb.pn("}");
	}

	public static void g2beanConstant(Class<?> c, String namespace,
			StrBuilder sb) {
		Field[] fs = c.getDeclaredFields();
		String cname = c.getSimpleName();
		sb.pn("package gen_b2g${1}.bean;", StrEx.isEmpty(namespace) ? "" : "."
				+ namespace);
		sb.pn("");
		sb.pn("public class ${1} {", cname);
		sb.pn("");
		for (Field field : fs) {
			B2Field a = field.getAnnotation(B2Field.class);
			String t = B2G.getType(field);
			String s = field.getName();
			if (s.contains("$"))
				continue;

			String remark = B2G.getRemark(field);
			String def = B2G.getDef(field);
			if ("".equals(def)) {
				continue;
			}
			if (field.getType().equals(List.class)) {
				continue;
			} else {
				if (t.contains("String")) {
					sb.pn("    public static final ${1} ${2} = \"${4}\"; ${3}",
							t, s, remark, def);

				} else {
					sb.pn("    public static final ${1} ${2} = ${4}; ${3}", t,
							s, remark, def);
				}
			}
		}
		sb.pn("}");
	}

	// 生成服务器接口
	public static void g2s_service(Class<?> c, String namespace, StrBuilder sb) {
		Method[] methods = c.getMethods();
		String cname = c.getSimpleName();
		sb.pn("package gen_b2g${1};", StrEx.isEmpty(namespace) ? "" : "."
				+ namespace);
		// sb.pn("package gen_b2g;");
		sb.pn("");
		// sb.pn("import java.io.*;");
		sb.pn("import java.util.*;");
		sb.pn("");
		sb.pn("import com.bowlong.util.*;");
		sb.pn("import com.bowlong.netty.bio2.*;");
		sb.pn("");
		sb.pn("import gen_b2g${1}.bean.*;", StrEx.isEmpty(namespace) ? "" : "."
				+ namespace);
		sb.pn("");
		sb.pn("@SuppressWarnings({ \"rawtypes\", \"unchecked\", \"unused\" })");
		sb.pn("public abstract class ${1} {", cname);
		sb.pn("");
		sb.pn("public abstract TcpChannel chn(int XID) throws Exception;");
		sb.pn("");
		for (Method m : methods) { // 向客户端主动发送
			if (!B2G.isClient(m))
				continue;
			String remark = B2G.getRemark(m);
			// String oType = B2G.getOType(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			int hmname = mname.hashCode();
			NewList<NewMap<String, String>> params = B2G.getParameters(m);

			StrBuilder sb1 = new StrBuilder();
			StrBuilder sb2 = new StrBuilder();
			for (NewMap<String, String> m1 : params) {
				String key = (String) m1.getKey();
				String val = (String) m1.getValue();
				// StrBuilder sb0 = new StrBuilder();
				if (B2G.getMapType(key).equals("getList")) {
					key = PStr.str("${1}<${2}>", key, B2G.getOType(m, val));
					// sb0.ap("${1}<${2}>", key, B2G.getOType(m, val));
					// key = sb0.toString();
				}
				sb1.ap(", ${1} ${2}", key, val);
				sb2.ap(", ${1}", val);
			}

			sb.pn("    // //////////////////////////////////////////////");
			sb.pn("    // 逻辑调用");
			sb.pn("    // //////////////////////////////////////////////");
			sb.pn("");
			sb.pn("    // ${1}", remark);
			sb.pn("    public void ${1}(int XID ${2}) throws Exception {",
					mname, sb1);
			sb.pn("        TcpChannel chn = chn(XID);");
			sb.pn("        ${1}(chn${2});", mname, sb2);
			sb.pn("    }");
			sb.pn("    public void ${1}(int XID, List<Integer> xids2 ${2}) throws Exception {",
					mname, sb1);
			sb.pn("        TcpChannel chn = chn(XID);");
			sb.pn("        List<TcpChannel> chn2 = new NewList<TcpChannel>();");
			sb.pn("        if (xids2 != null) {");
			sb.pn("        	for (Integer _xid2 : xids2) {");
			sb.pn("        		TcpChannel _chn = chn(XID);");
			sb.pn("        		if(_chn != null) chn2.add(_chn);");
			sb.pn("        	}");
			sb.pn("        }");
			sb.pn("        ${1}(chn,chn2${2});", mname, sb2);
			sb.pn("    }");
			sb.pn("    public void ${1}(TcpChannel chn ${2}) throws Exception {",
					mname, sb1);
			sb.pn("        ${1}(chn, null ${2});", mname, sb2);
			sb.pn("    }");
			sb.pn("    // ${1}", remark);
			sb.pn("    public void ${1}(TcpChannel chn, List<TcpChannel> chn2 ${2}) throws Exception {",
					mname, sb1);
			sb.pn("        if(chn == null) return;");
			sb.pn("        NewMap _params = new NewMap();");
			sb.pn("        _params.put(${1}, ${2}); // cmd:${3}", B2G.METHOD,
					hmname, mname);
			int i = 0;
			for (NewMap<String, String> m1 : params) {
				String key = (String) m1.getKey();
				String val = (String) m1.getValue();
				int hval = val.hashCode();
				if (B2G.getMapType(key).equals("getObject")) {
					sb.pn("        _params.put(${1}, ${2}.toMap());", hval, val);
				} else {
					if (B2G.getMapType(key).equals("getList")) {
						sb.pn("		{");
						sb.pn("			// Lsit对象(${1})", val);
						sb.pn("			List _list = new NewList();");
						String oType = B2G.getOType(m, val);
						String mType = B2G.getMapType(oType);
						sb.pn("			for (${1} object : ${2}) {", oType, val);
						if (mType.equals("getObject")) {
							sb.pn("            _list.add(object.toMap());");
						} else {
							sb.pn("            _list.add(object);");
						}
						sb.pn("			}");
						sb.pn("			_params.put(${1}, _list);", hval);
						sb.pn("		}");
					} else {
						sb.pn("        _params.put(${1}, ${2});", hval, val);
					}
				}
				i++;
			}
			// sb.pn("        chn.send(_params);");
			sb.pn("        byte[] buff = B2Helper.toBytes(_params);");
			sb.pn("        chn.send(buff);");
			sb.pn("        if( chn2 != null) {");
			sb.pn("            for(TcpChannel _chn : chn2) ");
			sb.pn("               _chn.send(buff);");
			sb.pn("        }");
			sb.pn("    }");
			sb.pn("");
		}

		StrBuilder sb2 = new StrBuilder();
		for (Method m : methods) {
			String rtype = B2G.getReturnType(m);
			if (B2G.isClient(m) && rtype.equals("void"))
				continue;
			String mname = B2G.getNethodName(m);
			int hmname = mname.hashCode();
			sb2.ap(".Add(${1})", hmname);
		}
		String s = sb2.toString();

		sb.pn("");
		sb.pn("    public static final Set<Integer> CMD = NewSet.create()${1};",
				s);
		sb.pn("");
		sb.pn("    public static boolean in(Map map) throws Exception {");
		sb.pn("        int cmd = MapEx.getInt(map, ${1});", B2G.METHOD);
		sb.pn("        return CMD.contains(cmd);");
		sb.pn("    }");
		sb.pn("");

		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 逻辑分发");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		sb.pn("    public String disp(TcpChannel chn, Map map) throws Exception {");
		sb.pn("        if(chn == null) return \"\";");
		sb.pn("        int cmd = MapEx.getInt(map, ${1});", B2G.METHOD);
		sb.pn("        return disp(chn, cmd, map);");
		sb.pn("    }");
		sb.pn("    public String disp(TcpChannel chn, int cmd, Map map) throws Exception {");
		sb.pn("        if(chn == null) return \"\";");
		sb.pn("        switch (cmd) {");
		for (Method m : methods) {
			String remark = B2G.getRemark(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			int hmname = mname.hashCode();
			if (B2G.isServer(m)) {
				sb.pn("            case ${1}: { //  ${2}", hmname, remark);
				sb.pn("                __${1}(chn, map);", mname);
				sb.pn("                return \"${1}\";", mname);
				sb.pn("            }");
			} else {
				if (!srtype.equals("void")) {
					sb.pn("            case ${1}: { //  ${2}", hmname, remark);
					sb.pn("                __onCallback_${1}(chn, map);", mname);
					sb.pn("                return \"${1}\";", mname);
					sb.pn("            }");
				}
			}
		}
		sb.pn("        }");
		sb.pn("        throw new Exception(\" cmd: \" + cmd + \":\" + map + \" not found processor.\");");
		sb.pn("    }");
		sb.pn("");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 解析参数");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		for (Method m : methods) {
			String remark = B2G.getRemark(m);
			// String oType = B2G.getOType(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			String hmname = mname.hashCode() + "";
			NewList<NewMap<String, String>> params = B2G.getParameters(m);

			StrBuilder sb1 = new StrBuilder();

			// 解析参数函数
			sb.pn("    // ${1}", remark);
			if (B2G.isClient(m)) {
				if (!srtype.equals("void")) {
					String mx = B2G.getMapType(srtype);

					sb.pn("    private void __onCallback_${1}(TcpChannel chn, Map map) throws Exception {",
							mname);
					sb.pn("        if(chn == null) return;");
					sb.pn("        NewMap map2 = NewMap.create(map);");
					sb.pn("        Map retVal = map2.getMap(1);", srtype, mx);
					sb.pn("");
					sb.pn("        ReturnStatus rst = ReturnStatus.parse(retVal);");
					sb.pn("");
					sb.pn("        on${1}(chn, rst);", upper1(mname));
					sb.pn("    }");
				}
			} else {
				sb.pn("    private void __${1}(TcpChannel chn, Map map) throws Exception {",
						mname);
				sb.pn("        if(chn == null) return;");
				if (!params.isEmpty()) {
					sb.pn("        NewMap map2 = NewMap.create(map);");
					sb.pn("");
				}
				for (NewMap<String, String> m1 : params) {
					String key = (String) m1.getKey();
					String val = (String) m1.getValue();
					String hval = val.hashCode() + "";
					String p = B2G.getMapType(key);
					boolean isOut = B2G.isOut(m, val);
					if (isOut) {
						sb.pn("        ${1} ${2} = new ${1}();", key, val);
					} else {
						if (p.equals("getObject")) {
							sb.pn("        ${1} ${2} = ${1}.parse(map2.getNewMap(${3}));",
									key, val, hval);
						} else {

							if (p.equals("getList")) {
								String oType = B2G.getOType(m, val);
								String mType = B2G.getMapType(oType);
								if (mType.equals("getObject")) {
									sb.pn("        ${1}<${2}> ${3} = new NewList<${2}>();",
											key, oType, val);
									sb.pn("        {");
									sb.pn("            List<Map> maps = map2.${1}(${2});",
											p, hval);
									sb.pn("            for(Map m1 : maps) {");
									sb.pn("                ${1}.add(${2}.parse(m1));",
											val, oType);
									sb.pn("            }");
									sb.pn("        }");
									key = PStr.str("${1}<${2}>", key, oType);
								} else {
									sb.pn("        ${1} ${2} = map2.${3}(${4});",
											key, val, p, hval);
								}
							} else {
								sb.pn("        ${1} ${2} = map2.${3}(${4});",
										key, val, p, hval);
							}
						}
					}
					sb1.ap(", ${1}", val);
				}
				sb.pn("");
				if (srtype.equals("void")) {
					sb.pn("        on${1}(chn${2});", upper1(mname), sb1);
				} else {
					sb.pn("        ${1} rst = new ${1}();", srtype);
					sb.pn("        rst = on${1}(chn${2}, rst);", upper1(mname),
							sb1);
					sb.pn("        Map result = new NewMap();");
					sb.pn("        result.put(${1}, ${2});", B2G.METHOD, hmname);
					sb.pn("        result.put(${1}, rst.toMap());",
							B2G.RETURN_STAT);
					for (NewMap<String, String> m1 : params) {
						String key = (String) m1.getKey();
						String val = (String) m1.getValue();
						String hval = val.hashCode() + "";
						String p = B2G.getMapType(key);
						boolean isOut = B2G.isOut(m, val);
						if (isOut) {
							sb.pn("        result.put(${1}, ${2}.toMap());",
									hval, val);
						}
					}

					sb.pn("        chn.send(result);");
				}
				sb.pn("    }");

				if (B2G.isServer(m)) {
					if (!srtype.equals("void")) {
						StrBuilder msb = new StrBuilder();
						for (NewMap<String, String> m1 : params) {
							String key = (String) m1.getKey();
							String val = (String) m1.getValue();
							String hval = val.hashCode() + "";
							String p = B2G.getMapType(key);
							boolean isOut = B2G.isOut(m, val);
							if (isOut) {
								if (p.equals("getObject")) {
									msb.ap(", ${1} ${2}", key, val);
								}
							}
						}

						// sb.pn("    public void on${1}(${2}${3} val) throws Exception {};",
						// upper1(mname), msb, srtype);
						// sb.pn("    public void snd${1}(TcpChannel chn${2}) {",
						// upper1(mname), msb);
						// if (srtype.equals("void")) {
						// sb.pn("        on${1}(chn${2});", upper1(mname),
						// sb1);
						// } else {
						// sb.pn("        ${1} rst = new ${1}();", srtype);
						// sb.pn("        rst = on${1}(chn${2}, rst);",
						// upper1(mname),
						// sb1);
						// sb.pn("        Map result = new NewMap();");
						// sb.pn("        result.put(${1}, ${2});", B2G.METHOD,
						// hmname);
						// sb.pn("        result.put(${1}, rst.toMap());",
						// B2G.RETURN_STAT);
						// for (NewMap<String, String> m1 : params) {
						// String key = (String) m1.getKey();
						// String val = (String) m1.getValue();
						// String hval = val.hashCode() + "";
						// String p = B2G.getMapType(key);
						// boolean isOut = B2G.isOut(m, val);
						// if (isOut) {
						// sb.pn("        result.put(${1}, ${2}.toMap());",
						// hval, val);
						// }
						// }
						//
						// sb.pn("        chn.send(result);");
						//
						// sb.pn("    }");
					}
					sb.pn("");
				}
			}
		}

		sb.pn("");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 需要实现的接口");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		for (Method m : methods) {
			String remark = B2G.getRemark(m);
			// String oType = B2G.getOType(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			NewList<NewMap<String, String>> params = B2G.getParameters(m);

			StrBuilder sb1 = new StrBuilder();
			for (NewMap<String, String> m1 : params) {
				String key = (String) m1.getKey();
				String val = (String) m1.getValue();
				String p = B2G.getMapType(key);
				if (p.equals("getList") && !"".equals(B2G.getOType(m, val))) {
					key = PStr.str("${1}<${2}>", key, B2G.getOType(m, val));
				}
				sb1.ap(", ${1} ${2}", key, val);
			}

			// 需要实现的逻辑函数
			if (B2G.isServer(m)) {
				sb.pn("    // ${1}", remark);
				if (!srtype.equals("void")) {
					sb.pn("    public abstract ${1} on${2}(TcpChannel chn ${3}, ReturnStatus ret) throws Exception;",
							srtype, upper1(mname), sb1);
				} else {
					sb.pn("    public abstract ${1} on${2}(TcpChannel chn ${3}) throws Exception;",
							srtype, upper1(mname), sb1);
				}
			} else {
				if (!srtype.equals("void")) {
					sb.pn("    // ${1}", remark);
					sb.pn("    public void on${1}(TcpChannel chn, ${2} val) throws Exception { };",
							upper1(mname), srtype);
				}
			}
		}
		sb.pn("}");
	}

	// 生成客户端接口
	public static void g2s_call(Class<?> c, String namespace, StrBuilder sb) {
		String sname = c.getSimpleName();
		Method[] methods = c.getMethods();
		String cname = c.getSimpleName();
		sb.pn("package gen_b2g${1};", StrEx.isEmpty(namespace) ? "" : "."
				+ namespace);
		// sb.pn("package gen_b2g;");
		sb.pn("");
		// sb.pn("import java.io.*;");
		sb.pn("import java.util.*;");
		sb.pn("");
		sb.pn("import com.bowlong.util.*;");
		sb.pn("import com.bowlong.netty.bio2.*;");
		sb.pn("");
		sb.pn("import gen_b2g${1}.bean.*;", StrEx.isEmpty(namespace) ? "" : "."
				+ namespace);
		sb.pn("");
		sb.pn("@SuppressWarnings({ \"rawtypes\", \"unchecked\" })");
		sb.pn("public abstract class Call${1} {", cname);

		sb.pn("");
		sb.pn("    public int __pid;");
		sb.pn("    public TcpChannel chn;");
		sb.pn("    public Call${1}(TcpChannel chn) {", sname);
		sb.pn("        this.chn = chn;");
		sb.pn("    }");
		sb.pn("");
		for (Method m : methods) {
			if (!B2G.isServer(m))
				continue;

			String remark = B2G.getRemark(m);
			// String oType = B2G.getOType(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			String hhmname = mname.hashCode() + "";
			NewList<NewMap<String, String>> params = B2G.getParameters(m);

			StrBuilder sb1 = new StrBuilder();
			for (NewMap<String, String> m1 : params) {
				String mykey = (String) m1.getKey();
				String myvar = (String) m1.getValue();
				String hval = myvar.hashCode() + "";
				boolean isOut = B2G.isOut(m, myvar);
				if (isOut) {

				} else {
					if (mykey.equals("List")
							&& !"".equals(B2G.getOType(m, myvar))) {
						mykey = PStr.str("${1}<${2}>", mykey,
								B2G.getOType(m, myvar));
					}
					sb1.ap("${1} ${2}, ", mykey, myvar);
				}
			}
			if (sb1.length() > 2) {
				sb1.removeRight(2);
			}

			// 需要实现的逻辑函数
			sb.pn("    // ${1}", remark);
			sb.pn("    public void ${1}(${2}) throws Exception {", mname, sb1);
			sb.pn("        NewMap _map = new NewMap();");
			sb.pn("        _map.put(-1, (__pid++));  // _pid");
			sb.pn("        _map.put(${1}, ${2});  // cmd:${3}", B2G.METHOD,
					hhmname, mname);
			for (NewMap<String, String> m1 : params) {
				String key = (String) m1.getKey();
				String val = (String) m1.getValue();
				String p = B2G.getMapType(key);
				String hval = val.hashCode() + "";
				boolean isOut = B2G.isOut(m, val);
				if (isOut) {

				} else {
					if (p.equals("getList")) {
						String oType = B2G.getOType(m, val);
						String mType = B2G.getMapType(oType);
						if (mType.equals("getObject")) {
							sb.pn("		{");
							sb.pn("			// Lsit对象(${1})", val);
							sb.pn("		    List<Map> ${1}_list = new NewList<Map>();",
									val);
							sb.pn("        _map.put(${1}, ${2});", hval, val);
							sb.pn("        _map.put(${1}, ${2}_list);", hval,
									val);
							sb.pn("			for(${1} obj : ${2}) {", oType, val);
							sb.pn("				${1}_list.add(obj.toMap());", val, oType);
							sb.pn("			}");
							sb.pn("		}");
							val += "_list";
						} else {
						}
					} else if (B2G.getMapType(key).equals("getObject")) {
						sb.pn("        _map.put(${1}, ${2}.toMap());", hval,
								val);
					} else {
						sb.pn("        _map.put(${1}, ${2});", hval, val);
					}
				}
			}
			sb.pn("        chn.send(_map);");
			sb.pn("    }");
			sb.pn("");
		}

		StrBuilder sb2 = new StrBuilder();
		for (Method m : methods) {
			String rtype = B2G.getReturnType(m);
			if (B2G.isServer(m) && rtype.equals("void"))
				continue;
			String mname = B2G.getNethodName(m);
			String hmane = mname.hashCode() + "";
			sb2.ap(".Add(${1})", hmane);
		}
		String s = sb2.toString();

		sb.pn("");
		sb.pn("    public static final Set<Integer> CMD = NewSet.create()${1};",
				s);
		sb.pn("");
		sb.pn("    public static boolean in(Map map) throws Exception {");
		sb.pn("        int cmd = MapEx.getInt(map, ${1});", B2G.METHOD);
		sb.pn("        return CMD.contains(cmd);");
		sb.pn("    }");
		sb.pn("");

		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 逻辑分发");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		sb.pn("    public void disp(Map map) throws Exception {");
		sb.pn("        int cmd = MapEx.getInt(map, ${1});", B2G.METHOD);
		sb.pn("        disp(cmd, map);");
		sb.pn("    }");
		sb.pn("    public void disp(int cmd, Map map) throws Exception {");
		sb.pn("        switch (cmd) {");
		for (Method m : methods) {
			String remark = B2G.getRemark(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			int hmname = mname.hashCode();
			if (B2G.isServer(m)) {
				if (!srtype.equals("void")) {
					sb.pn("            case ${1}: { //  ${2}", hmname, remark);
					sb.pn("                __onCallback_${1}(map);", mname);
					sb.pn("                return;");
					sb.pn("            }");
				}
			} else {
				sb.pn("            case ${1}: { //  ${2}", hmname, remark);
				sb.pn("                __onCall_${1}(map);", mname);
				sb.pn("                return;");
				sb.pn("            }");
			}
		}
		sb.pn("        }");
		sb.pn("        throw new Exception(\" cmd: \" + cmd + \":\" + map + \" not found processor.\");");
		sb.pn("    }");
		sb.pn("");
		sb.pn("");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 参数解析");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		for (Method m : methods) {
			String remark = B2G.getRemark(m);
			// String oType = B2G.getOType(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			String hmname = mname.hashCode() + "";
			NewList<NewMap<String, String>> params = B2G.getParameters(m);

			// 解析参数函数
			if (B2G.isServer(m)) {
				if (!srtype.equals("void")) {
					sb.pn("    // ${1}", remark);
					sb.pn("    private void __onCallback_${1}(Map map) throws Exception {",
							mname);
					String mx = B2G.getMapType(srtype);
					sb.pn("        NewMap map2 = NewMap.create(map);");
					sb.pn("        Map retVal = map2.getMap(${1});",
							B2G.RETURN_STAT);
					sb.pn("        ReturnStatus rst = ReturnStatus.parse(retVal);");

					StrBuilder msb = new StrBuilder();
					for (NewMap<String, String> m1 : params) {
						String key = (String) m1.getKey();
						String val = (String) m1.getValue();
						String hval = val.hashCode() + "";
						String p = B2G.getMapType(key);
						boolean isOut = B2G.isOut(m, val);
						if (isOut) {
							if (p.equals("getObject")) {
								sb.pn("        ${1} ${2} = ${1}.parse(map2.getNewMap(${3}));",
										key, val, hval);
								msb.ap("${1}, ", val);
							}
						}
					}

					sb.pn("");
					sb.pn("        on${1}(${2}rst);", upper1(mname), msb);
					sb.pn("    }");
				}
			} else {
				sb.pn("    // ${1}", remark);
				sb.pn("    private void __onCall_${1}(Map map) throws Exception {",
						mname);
				sb.pn("        NewMap map2 = NewMap.create(map);");
				sb.pn("");
				StrBuilder sb1 = new StrBuilder();
				for (NewMap<String, String> m1 : params) {
					String key = (String) m1.getKey();
					String val = (String) m1.getValue();
					String hval = val.hashCode() + "";
					String p = B2G.getMapType(key);
					boolean isOut = B2G.isOut(m, val);

					if (p.equals("getObject")) {
						sb.pn("        ${1} ${2} = ${1}.parse(map2.getNewMap(${3}));",
								key, val, hval);
					} else {
						sb.pn("        ${1} ${2} = map2.${3}(${4});", key, val,
								p, hval);
						if (p.equals("getList")) {
							String oType = B2G.getOType(m, val);
							String mType = B2G.getMapType(oType);
							sb.pn("		List<${1}> ${2}_list = new NewList<${3}>();",
									oType, val, oType);
							sb.pn("		{");
							sb.pn("			// Lsit对象(${1})", val);
							sb.pn("			for(Object obj : ${1}) {", val);
							if (mType.equals("getObject"))
								sb.pn("				${1}_list.add(${2}.parse((Map)obj));",
										val, oType);
							else
								sb.pn("				${1}_list.add((${2})obj);", val,
										oType);

							sb.pn("			}");
							sb.pn("		}");
							val += "_list";
						}
					}
					sb1.ap("${1}, ", val);
				}
				if (sb1.length() > 2)
					sb1.removeRight(2);

				sb.pn("");
				if (srtype.equals("void")) {
					sb.pn("        on${1}(${2});", upper1(mname), sb1);
				} else {

					sb.pn("        ReturnStatus rst = on${1}(${2});",
							upper1(mname), sb1, srtype);

					sb.pn("        Map result = new NewMap();");
					sb.pn("        result.put(${1}, ${2});", B2G.METHOD, hmname);
					sb.pn("        result.put(${1}, rst.toMap());",
							B2G.RETURN_STAT);
					sb.pn("        chn.send(result);");
				}
				sb.pn("    }");
			}
			sb.pn("");
		}

		sb.pn("");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 需要实现的接口");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		for (Method m : methods) {
			String remark = B2G.getRemark(m);
			// String oType = B2G.getOType(m);
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			NewList<NewMap<String, String>> params = B2G.getParameters(m);
			// 解析参数函数
			sb.pn("    // ${1}", remark);
			if (B2G.isServer(m)) {
				if (!srtype.equals("void")) {
					StrBuilder msb = new StrBuilder();
					for (NewMap<String, String> m1 : params) {
						String key = (String) m1.getKey();
						String val = (String) m1.getValue();
						String hval = val.hashCode() + "";
						String p = B2G.getMapType(key);
						boolean isOut = B2G.isOut(m, val);
						if (isOut) {
							if (p.equals("getObject")) {
								msb.ap("${1} ${2}, ", key, val);
							}
						}
					}

					sb.pn("    public void on${1}(${2}${3} val) throws Exception {};",
							upper1(mname), msb, srtype);
				}
			} else {

				StrBuilder sb1 = new StrBuilder();
				for (NewMap<String, String> m1 : params) {
					String key = (String) m1.getKey();
					String val = (String) m1.getValue();
					if (B2G.getMapType(key).equals("getList")) {
						key = PStr.str("${1}<${2}>", key, B2G.getOType(m, val));
					}
					sb1.ap("${1} ${2}, ", key, val);
				}
				if (sb1.length() > 2) {
					sb1.removeRight(2);
				}

				// 需要实现的逻辑函数
				sb.pn("    public abstract ${1} on${2}(${3}) throws Exception;",
						srtype, upper1(mname), sb1);

			}
			sb.pn("");
		}
		sb.pn("}");
	}

	public static void writeFile(String f, String str) {
		try {
			File fn = new File(f);
			FileOutputStream out = new FileOutputStream(fn);
			OutputStreamWriter osw = new OutputStreamWriter(out,
					Charset.forName("GBK"));
			osw.write(str, 0, str.length());
			osw.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String upper1(String s) {
		if (s == null || s.isEmpty())
			return s;
		int len = s.length();
		return s.substring(0, 1).toUpperCase() + s.substring(1, len);
	}
}
