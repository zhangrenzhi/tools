package com.bowlong.netty.bio2g;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.bowlong.util.NewList;
import com.bowlong.util.NewMap;

public class B2G {
	public static final int METHOD = 0;
	public static final int BEAN = -1;
	public static final int RETURN_STAT = 1;

	public static final String SERVER = "SERVER";
	public static final String CLIENT = "CLIENT";
	public static final String DATA = "DATA";

	public static final boolean isServer(Class<?> c) {
		B2Class b2c = c.getAnnotation(B2Class.class);
		if (b2c == null)
			return false;
		boolean ret = (b2c.type().equals(SERVER));
		return ret;
	}

	public static final boolean isClient(Class<?> c) {
		B2Class b2c = c.getAnnotation(B2Class.class);
		if (b2c == null)
			return false;
		boolean ret = (b2c.type().equals(CLIENT));
		return ret;
	}

	public static final boolean isData(Class<?> c) {
		B2Class b2c = c.getAnnotation(B2Class.class);
		if (b2c == null)
			return false;
		boolean ret = (b2c.type().equals(DATA));
		return ret;
	}

	public static final boolean isConstant(Class<?> c) {
		B2Class b2c = c.getAnnotation(B2Class.class);
		if (b2c == null)
			return false;
		return b2c.constant();
	}

	public static final boolean isServer(Method m) {
		B2Method b2f = m.getAnnotation(B2Method.class);
		if (b2f == null)
			return false;
		boolean ret = (b2f.type().equals(SERVER));
		return ret;
	}

	public static final boolean isClient(Method m) {
		B2Method b2f = m.getAnnotation(B2Method.class);
		if (b2f == null)
			return false;
		boolean ret = (b2f.type().equals(CLIENT));
		return ret;
	}

	// ///////////////////////////////////////////////////////////////
	public static String getRemark(Method m) {
		B2Method b2m = m.getAnnotation(B2Method.class);
		return b2m.remark();
	}

	public static String getOType(Method m, String val) {
		B2Method b2m = m.getAnnotation(B2Method.class);
		String[] params = b2m.params();
		int i = 0;
		for (; i < params.length; i++) {
			if (params[i].equals(val)) {
				break;
			}
		}
		Annotation[][] annotations = m.getParameterAnnotations();
		B2Param b2p = (B2Param) annotations[i][0];
		return b2p.oType();
	}

	public static boolean isOut(Method m, String val) {
		B2Method b2m = m.getAnnotation(B2Method.class);
		String[] params = b2m.params();
		int i = 0;
		for (; i < params.length; i++) {
			if (params[i].equals(val)) {
				break;
			}
		}
		Annotation[][] annotations = m.getParameterAnnotations();
		if(annotations[i].length < 1)
			return false;
		B2Param b2p = (B2Param) annotations[i][0];
		return b2p.isOut();
	}

	
	public static final String getFieldRemark(Field f) {
		B2Field b2f = f.getAnnotation(B2Field.class);
		if (b2f == null)
			return null;
		return b2f.remark();
	}

	public static final String getFieldType(Field f) {
		if (f == null)
			return "";
		String r = f.getClass().getSimpleName();
		if (r.equals("void")) {
			return "";
		} else if (r.contains("boolean") || r.contains("Boolean")) {
			return "return true;";
		} else if (r.contains("byte") || r.contains("Byte")) {
			return "return 0;";
		} else if (r.contains("short") || r.contains("Short")) {
			return "return 0;";
		} else if (r.contains("int") || r.contains("Integer")) {
			return "return 0;";
		} else if (r.contains("long") || r.contains("Long")) {
			return "return 0L;";
		} else if (r.contains("float") || r.contains("Float")) {
			return "return 0.0;";
		} else if (r.contains("double") || r.contains("Double")) {
			return "return 0.0;";
		} else if (r.contains("String")) {
			return "return \"\";";
		} else if (r.contains("Map") || r.contains("Hashtable")) {
			return "return new NewMap();";
		} else if (r.contains("List") || r.contains("Vector")) {
			return "return new NewList();";
		}
		return r;
	}

	public static final String getReturnType(Method m) {
		if (m == null)
			return "";
		Class<?> rtype = m.getReturnType();
		String srtype = rtype.getSimpleName();
		return srtype;
	}

	public static final String getReturnVal(Method m) {
		if (m == null)
			return "";
		Class<?> rtype = m.getReturnType();
		String r = rtype.getSimpleName();
		if (r.equals("void")) {
			return "";
		} else if (r.contains("boolean") || r.contains("Boolean")) {
			return "return true;";
		} else if (r.contains("byte") || r.contains("Byte")) {
			return "return 0;";
		} else if (r.contains("short") || r.contains("Short")) {
			return "return 0;";
		} else if (r.contains("int") || r.contains("Integer")) {
			return "return 0;";
		} else if (r.contains("long") || r.contains("Long")) {
			return "return 0L;";
		} else if (r.contains("float") || r.contains("Float")) {
			return "return 0.0;";
		} else if (r.contains("double") || r.contains("Double")) {
			return "return 0.0;";
		} else if (r.contains("String")) {
			return "return \"\";";
		} else if (r.contains("Map") || r.contains("Hashtable")) {
			return "return new NewMap();";
		} else if (r.contains("List") || r.contains("Vector")) {
			return "return new NewList();";
		}
		return r;
	}

	public static final String getMapType(String r) {
		if (r.equals("void")) {
			return "";
		} else if (r.equals("boolean") || r.equals("Boolean")) {
			return "getBoolean";
		} else if (r.equals("byte") || r.equals("Byte")) {
			return "getByte";
		} else if (r.equals("short") || r.equals("Short")) {
			return "getShort";
		} else if (r.equals("int") || r.equals("Integer")) {
			return "getInt";
		} else if (r.equals("long") || r.equals("Long")) {
			return "getLong";
		} else if (r.equals("float") || r.equals("Float")) {
			return "getFloat";
		} else if (r.equals("double") || r.equals("Double")) {
			return "getDouble";
		} else if (r.equals("String")) {
			return "getString";
		} else if (r.equals("Map") || r.equals("Hashtable") || r.equals("HashMap") || r.equals("NewMap")) {
			return "getMap";
		} else if (r.equals("List") || r.equals("Vector") || r.equals("ArrayList") || r.equals("NewList")) {
			return "getList";
		}
		return "getObject";
	}

	public static final String getCsMapType(String r) {
		if (r.equals("void")) {
			return "";
		} else if (r.contains("bool") || r.contains("Boolean")) {
			return "getBool";
		} else if (r.contains("byte") || r.contains("Byte")) {
			return "getByte";
		} else if (r.contains("short") || r.contains("Short")) {
			return "getShort";
		} else if (r.contains("int") || r.contains("Integer")) {
			return "getInt";
		} else if (r.contains("long") || r.contains("Long")) {
			return "getLong";
		} else if (r.contains("float") || r.contains("Float")) {
			return "getFloat";
		} else if (r.contains("double") || r.contains("Double")) {
			return "getDouble";
		} else if (r.contains("string") || r.contains("String")) {
			return "getString";
		} else if (r.contains("Map") || r.contains("Hashtable")) {
			return "getMap";
		} else if (r.contains("List") || r.contains("Vector")) {
			return "getList";
		}
		return "getObject";
	}

	public static final String getNethodName(Method m) {
		if (m == null)
			return "";
		String ret = m.getName();
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static final NewList<NewMap<String, String>> getParameters(Method m) {
		if (m == null)
			return new NewList<NewMap<String, String>>();
		Class<?>[] types = m.getParameterTypes();
		B2Method b2m = m.getAnnotation(B2Method.class);
		String[] params = b2m.params();
		NewList<NewMap<String, String>> ret = new NewList<NewMap<String, String>>();
		for (int i = 0; i < types.length; i++) {
			String type = types[i].getSimpleName();
			String value = params[i];
			ret.add(NewMap.create().putPut(type, value));
		}

		return ret;
	}

	public static final String getRemark(Field f) {
		B2Field a = f.getAnnotation(B2Field.class);
		if (a == null)
			return "";
		String remark = a.remark();
		if (!remark.isEmpty())
			remark = "// " + remark;
		return remark;
	}

	public static final String getDef(Field f) {
		B2Field a = f.getAnnotation(B2Field.class);
		if (a == null)
			return "";
		return a.def();
	}

	public static final String getType(Field f) {
		if (f == null)
			return "";
		Class<?> rtype = f.getType();
		String r = rtype.getSimpleName();
		if (r.equals("void")) {
			return "";
		} else if (r.contains("boolean") || r.contains("Boolean")) {
			return "boolean";
		} else if (r.contains("byte") || r.contains("Byte")) {
			return "byte";
		} else if (r.contains("short") || r.contains("Short")) {
			return "short";
		} else if (r.contains("int") || r.contains("Integer")) {
			return "int";
		} else if (r.contains("long") || r.contains("Long")) {
			return "long";
		} else if (r.contains("float") || r.contains("Float")) {
			return "float";
		} else if (r.contains("double") || r.contains("Double")) {
			return "double";
		} else if (r.contains("String")) {
			return "String";
		} else if (r.contains("Map") || r.contains("Hashtable")) {
			return "Map";
		} else if (r.contains("List") || r.contains("Vector")) {
			return "List";
		}
		return r;
	}

	public static final String getJType(Field f) {
		if (f == null)
			return "";
		Class<?> rtype = f.getType();
		String r = rtype.getSimpleName();
		if (r.equals("void")) {
			return "";
		} else if (r.contains("boolean") || r.contains("Boolean")) {
			return "Boolean";
		} else if (r.contains("byte") || r.contains("Byte")) {
			return "Byte";
		} else if (r.contains("short") || r.contains("Short")) {
			return "Short";
		} else if (r.contains("int") || r.contains("Integer")) {
			return "Integer";
		} else if (r.contains("long") || r.contains("Long")) {
			return "Long";
		} else if (r.contains("float") || r.contains("Float")) {
			return "Float";
		} else if (r.contains("double") || r.contains("Double")) {
			return "Double";
		} else if (r.contains("String")) {
			return "String";
		} else if (r.contains("Map") || r.contains("Hashtable")) {
			return "Map";
		} else if (r.contains("List") || r.contains("Vector")) {
			return "List";
		}
		return r;
	}

	public static final String getCsType(Field f) {
		if (f == null)
			return "";
		Class<?> rtype = f.getType();
		String r = rtype.getSimpleName();
		if (r.equals("void")) {
			return "";
		} else if (r.contains("boolean") || r.contains("Boolean")) {
			return "bool";
		} else if (r.contains("byte") || r.contains("Byte")) {
			return "byte";
		} else if (r.contains("short") || r.contains("Short")) {
			return "short";
		} else if (r.contains("int") || r.contains("Integer")) {
			return "int";
		} else if (r.contains("long") || r.contains("Long")) {
			return "long";
		} else if (r.contains("float") || r.contains("Float")) {
			return "float";
		} else if (r.contains("double") || r.contains("Double")) {
			return "double";
		} else if (r.contains("String")) {
			return "string";
		} else if (r.contains("Map") || r.contains("Hashtable")) {
			return "Hashtable";
		} else if (r.contains("List") || r.contains("Vector")) {
			return "ArrayList";
		}
		return r;
	}

	public static final String getListType(Field f) {
		ParameterizedType pt = (ParameterizedType) f.getGenericType();
		if (pt.getActualTypeArguments().length <= 0)
			return "";
		Type type = pt.getActualTypeArguments()[0];
		String str = type.toString();
		if (str.contains("boolean") || str.contains(".Boolean"))
			return "Boolean";
		else if (str.contains("byte") || str.contains(".Byte"))
			return "Byte";
		else if (str.contains("short") || str.contains(".Short"))
			return "Short";
		else if (str.contains("int") || str.contains(".Integer"))
			return "Integer";
		else if (str.contains("long") || str.contains(".Long"))
			return "Long";
		else if (str.contains("float") || str.contains(".Float"))
			return "Float";
		else if (str.contains("double") || str.contains(".Double"))
			return "Double";
		else if (str.contains("String"))
			return "String";
		
		int pos = str.indexOf("$");
		return str.substring(pos + 1, str.length());
	}

	public static final boolean isBType(String str) {
		if(str.contains("void"))
			return true;
		else if (str.contains("boolean") || str.contains("Boolean")) {
			return true;
		} else if (str.contains("byte") || str.contains("Byte")) {
			return true;
		} else if (str.contains("short") || str.contains("Short")) {
			return true;
		} else if (str.contains("int") || str.contains("Integer")) {
			return true;
		} else if (str.contains("long") || str.contains("Long")) {
			return true;
		} else if (str.contains("float") || str.contains("Float")) {
			return true;
		} else if (str.contains("double") || str.contains("Double")) {
			return true;
		} else if (str.contains("String")) {
			return true;
		}
		return false;
	}
}
