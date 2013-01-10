package com.bowlong.netty.bio2g;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

import com.bowlong.lang.PStr;
import com.bowlong.lang.StrEx;
import com.bowlong.util.NewList;
import com.bowlong.util.NewMap;
import com.bowlong.util.StrBuilder;

@SuppressWarnings({ "unused" })
public class Bio2GCSharp {

	/**
	 * @param args
	 * @return
	 */
	public static String b2g(Class<?> c) {
		B2Class B2C = c.getAnnotation(B2Class.class);
		String namespace = "";
		if (B2C != null) {
			namespace = B2C.namespace();
		}

		String p = "gen_b2g";
		if (namespace != null && !namespace.isEmpty()) {
			p = p + "/" + namespace;
		}
		File path = new File(p);
		if (!path.exists())
			path.mkdirs();

		Class<?>[] classes = c.getDeclaredClasses();
		StrBuilder sb = new StrBuilder();

		sb.pn("using System;");
		sb.pn("using System.Collections;");
		sb.pn("");

		sb.pn("namespace ${1} {", c.getSimpleName());
		sb.pn("");
		sb.pn("public interface TcpChannel {");
		sb.pn("    void send(Hashtable map);");
		sb.pn("}");
		sb.pn("");
		for (Class<?> class1 : classes) {
			String sname = class1.getSimpleName();
			if (B2G.isData(class1)) {
				String f = class1.getSimpleName();
				if (B2G.isConstant(class1)) {
					g2beanConstant(class1, namespace, sb);
				} else {
					g2bean(class1, namespace, sb);
				}
			}
		}

		for (Class<?> class1 : classes) {
			String sname = class1.getSimpleName();
			if (B2G.isServer(class1)) {
				g2s_call(class1, namespace, sb);
			}
		}

		String sname = c.getSimpleName();
		writeFile(p + "/" + sname + ".cs", sb.toString());

		System.out.println(sb);
		return sb.toString();
	}

	public static void g2bean(Class<?> c, String namespace, StrBuilder sb) {
		Field[] fs = c.getDeclaredFields();
		String cname = c.getSimpleName();
		int hcname = cname.hashCode();
		// sb.pn("package gen_b2g${1}.bean;", StrEx.isEmpty(namespace) ? "" :
		// "."
		// + namespace);
		// sb.pn("");
		// sb.pn("import java.io.*;");
		// sb.pn("import java.util.*;");
		// sb.pn("");
		// sb.pn("import com.bowlong.util.*;");
		sb.pn("public class ${1} {", cname);
		sb.pn("    public const int _CID = ${1};", hcname);
		sb.pn("");
		for (Field field : fs) {
			String t = B2G.getCsType(field);
			String s = field.getName();

			if (s.contains("$"))
				continue;

			String remark = B2G.getRemark(field);
			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				if (gtype != null && !gtype.isEmpty()) {
					sb.pn("    public ${1} ${2}; ${3}", t, s, remark);
				}
			} else {
				if (t.contains("string")) {
					sb.pn("    public ${1} ${2} = \"\"; ${3}", t, s, remark);

				} else {
					sb.pn("    public ${1} ${2}; ${3}", t, s, remark);
				}
			}
		}

		// ///////
		sb.pn("");
		for (Field field : fs) {
			B2Field a = field.getAnnotation(B2Field.class);
			String s = field.getName();
			String t = B2G.getType(field);
			if (s.contains("$"))
				continue;

			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				boolean isBtype = B2G.isBType(gtype);
				if (gtype != null && !gtype.isEmpty() && !isBtype) {
					sb.pn("    public ArrayList ${1}_maps() {", s);
					sb.pn("        ArrayList r = new ArrayList();");
					sb.pn("        if(${1} == null) return r;", s);
					sb.pn("        foreach(${1} _e in ${2}) {", gtype, s);
					sb.pn("            Hashtable e = _e.toMap();");
					sb.pn("            if(e == null) continue;");
					sb.pn("            r.Add(e);");
					sb.pn("        }");
					sb.pn("        return r;");
					sb.pn("    }");
					sb.pn("");

					sb.pn("    public static ArrayList maps_${1}(ArrayList maps) {",
							s);
					sb.pn("        ArrayList r = new ArrayList();");
					sb.pn("        foreach(Hashtable _e in maps) {", gtype, s);
					sb.pn("            ${1} e = ${1}.parse(_e);", gtype);
					sb.pn("            if(e == null) continue;");
					sb.pn("            r.Add(e);");
					sb.pn("        }");
					sb.pn("        return r;");
					sb.pn("    }");
					sb.pn("");

				}
			}
		}
		// ///////

		sb.pn("");
		sb.pn("    public Hashtable toMap() {");
		sb.pn("        Hashtable r = new Hashtable();");
		sb.pn("        r.Add(${1}, _CID);", B2G.BEAN);
		for (Field field : fs) {
			String t = B2G.getType(field);
			String gm = B2G.getMapType(t);
			String s = field.getName();
			int hs = s.hashCode();
			if (s.contains("$"))
				continue;

			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				boolean isBtype = B2G.isBType(gtype);
				if (gtype != null && !gtype.isEmpty() && !isBtype) {
					sb.pn("        r.Add(${1}, ${2}_maps());", hs, s);
				} else {
					sb.pn("        r.Add(${1}, ${2});", hs, s);
				}
			} else {
				if (gm.equals("getObject")) {
					sb.pn("        r.Add(${1}, ${2}.toMap());", hs, s);
				} else {
					sb.pn("        r.Add(${1}, ${2});", hs, s);
				}
			}
		}
		sb.pn("        return r;");
		sb.pn("    }");
		sb.pn("");

		sb.pn("");
		sb.pn("    public static ${1} parse(Hashtable map) {", cname);
		sb.pn("        if(map == null) return null;");
		sb.pn("");
		sb.pn("        NewMap map2 = NewMap.create(map);");
		sb.pn("        ${1} r = new ${1}();", cname);
		for (Field field : fs) {
			String t = B2G.getCsType(field);
			String gm = B2G.getCsMapType(t);
			String s = field.getName();
			int hs = s.hashCode();
			if (s.contains("$"))
				continue;

			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				boolean isBtype = B2G.isBType(gtype);
				if (gtype != null && !gtype.isEmpty() && !isBtype) {
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

		sb.pn("}");
	}

	public static void g2beanConstant(Class<?> c, String namespace,
			StrBuilder sb) {
		Field[] fs = c.getDeclaredFields();
		String cname = c.getSimpleName();
		sb.pn("public class ${1} {", cname);
		for (Field field : fs) {
			String t = B2G.getCsType(field);
			String s = field.getName();

			if (s.contains("$"))
				continue;

			String remark = B2G.getRemark(field);
			String def = B2G.getDef(field);
			if (field.getType().equals(List.class)) {
				String gtype = B2G.getListType(field);
				if (gtype != null && !gtype.isEmpty()) {
					continue;
				}
			} else {
				if (t.contains("string")) {
					sb.pn("    public const ${1} ${2} = \"${4}\"; ${3}", t, s,
							remark, def);

				} else {
					sb.pn("    public const ${1} ${2} = ${4}; ${3}", t, s,
							remark, def);
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
			String srtype = B2G.getReturnType(m);
			String mname = B2G.getNethodName(m);
			int hmname = mname.hashCode();
			NewList<NewMap<String, String>> params = B2G.getParameters(m);
			StrBuilder sb1 = new StrBuilder();
			for (NewMap<String, String> m1 : params) {
				String mykey = (String) (m1.getKey().equals("boolean") ? "bool"
						: m1.getKey());
				mykey = (String) (mykey.equals("List") ? "ArrayList" : mykey);
				mykey = (String) (mykey.equals("Map") ? "Hashtable" : mykey);
				String myvar = (String) m1.getValue();
				boolean isOut = B2G.isOut(m, myvar);
				if (isOut) {

				} else {
					sb1.ap("${1} ${2}, ", mykey, myvar);
				}
			}
			if (sb1.length() > 2) {
				sb1.removeRight(2);
			}

			// 需要实现的逻辑函数
			sb.pn("    // ${1}", remark);
			sb.pn("    public void ${1}(${2}) {", mname, sb1);
			sb.pn("        Hashtable _map = new Hashtable();");
			sb.pn("        _map.Add(-1, (__pid++));  // _pid");
			sb.pn("        _map.Add(${1}, ${2});  // cmd:${3}", B2G.METHOD,
					hmname, mname);
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
							sb.pn("        { // Lsit对象(${1})", val);
							sb.pn("            NewList ${1}_list = new NewList();", val);
							sb.pn("            _map.Add(${1}, ${2}_list);", hval, val);
							sb.pn("            foreach(${1} obj in ${2}) {", oType, val);
							sb.pn("                ${1}_list.add(obj.toMap());", val, oType);
							sb.pn("            }");
							sb.pn("        }");
							val += "_list";
						} else {
							sb.pn("        _map.Add(${1}, ${2});", hval, val);
						}
					} else if (B2G.getMapType(key).equals("getObject")) {
						sb.pn("        _map.Add(${1}, ${2}.toMap());", hval,
								val);
					} else {
						sb.pn("        _map.Add(${1}, ${2});", hval, val);
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
			int hmname = mname.hashCode();
			sb2.ap(".add(${1})", hmname);
		}
		String s = sb2.toString();

		sb.pn("");
		sb.pn("    public static NewSet CMD = NewSet.create()${1};", s);
		sb.pn("");
		sb.pn("    public static bool withIn(Hashtable map) {");
		sb.pn("        int cmd = MapEx.getInt(map, ${1});", B2G.METHOD);
		sb.pn("        return CMD.Contains(cmd);");
		sb.pn("    }");
		sb.pn("");

		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("    // 逻辑分发");
		sb.pn("    // //////////////////////////////////////////////");
		sb.pn("");
		sb.pn("    public void disp(Hashtable map) {");
		sb.pn("        int cmd = MapEx.getInt(map, ${1});", B2G.METHOD);
		sb.pn("        disp(cmd, map);");
		sb.pn("    }");
		sb.pn("    public void disp(int cmd, Hashtable map) {");
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
			int hmname = mname.hashCode();
			NewList<NewMap<String, String>> params = B2G.getParameters(m);

			// 解析参数函数
			if (B2G.isServer(m)) {
				if (!srtype.equals("void")) {
					sb.pn("    // ${1}", remark);
					sb.pn("    private void __onCallback_${1}(Hashtable map) {",
							mname);
					String mx = B2G.getCsMapType(srtype);
					sb.pn("        NewMap map2 = NewMap.create(map);");
					sb.pn("        Hashtable retVal = map2.getMap(${1});",
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
				sb.pn("    private void __onCall_${1}(Hashtable map) {", mname);
				sb.pn("        NewMap map2 = NewMap.create(map);");
				sb.pn("");
				StrBuilder sb1 = new StrBuilder();
				for (NewMap<String, String> m1 : params) {
					String key = (String) m1.getKey();
					String val = (String) m1.getValue();
					String hval = val.hashCode() + "";
					String p = B2G.getCsMapType(key);
					if (p.equals("getObject")) {
						sb.pn("        ${1} ${2} = ${1}.parse(map2.getNewMap(${3}));",
								key, val, hval);
					} else {
						if (B2G.getMapType(key).equals("getList")) {
							key = "ArrayList";
						}
						key = key.toLowerCase().equals("boolean") ? "bool"
								: key;
						key = key.equals("String") ? "string" : key;
						sb.pn("        ${1} ${2} = map2.${3}(${4});", key, val,
								p, hval);
						if (B2G.getMapType(key).equals("getList")) {
							String oType = B2G.getOType(m, val);
							String mType = B2G.getCsMapType(oType);

							sb.pn("		ArrayList ${1}_list = new NewList();", val);
							sb.pn("		{");
							sb.pn("			// Lsit对象(${1})", val);
							sb.pn("			foreach (object obj in ${1}) {", val);
							if (mType.equals("getObject")) {
								sb.pn("				${1}_list.Add(${2}.parse((Hashtable)obj));",
										val, oType);
							} else {
								sb.pn("				${1}_list.Add(obj);", val);
							}
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
					sb.pn("        Hashtable result = new NewMap();");
					sb.pn("        result.Add(${1}, ${2});", B2G.METHOD, hmname);
					sb.pn("        result.Add(${1}, rst.toMap());",
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

					sb.pn("    public abstract void on${1}(${2}${3} val);",
							upper1(mname), msb, srtype);
				}
			} else {

				StrBuilder sb1 = new StrBuilder();
				for (NewMap<String, String> m1 : params) {
					String key = (String) m1.getKey();
					String val = (String) m1.getValue();
					if (B2G.getMapType(key).equals("getList")) {
						key = PStr.str("${1}", "ArrayList",
								B2G.getOType(m, m1.getValue().toString()));
					}
					key = key.toLowerCase().equals("boolean") ? "bool" : key;
					key = key.equals("String") ? "string" : key;

					sb1.ap("${1} ${2}, ", key, val);
				}
				if (sb1.length() > 2) {
					sb1.removeRight(2);
				}

				// 需要实现的逻辑函数
				sb.pn("    public abstract ${1} on${2}(${3}) ;", srtype,
						upper1(mname), sb1);

			}
			sb.pn("");
		}
		sb.pn("    }");
		sb.pn("}");
	}

	public static String upper1(String s) {
		if (s == null || s.isEmpty())
			return s;
		int len = s.length();
		return s.substring(0, 1).toUpperCase() + s.substring(1, len);
	}

	public static void writeFile(String f, String str) {
		try {
			File fn = new File(f);
			FileOutputStream out = new FileOutputStream(fn);
			OutputStreamWriter osw = new OutputStreamWriter(out,
					Charset.forName("UTF8"));
			osw.write(str, 0, str.length());
			osw.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
