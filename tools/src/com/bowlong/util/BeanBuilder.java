package com.bowlong.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bowlong.lang.StrEx;
import com.bowlong.reflect.TypeEx;

public class BeanBuilder {

	public static final String fromMap(String objectName,
			Map<String, Object> map) {

		StrBuilder sb = StrEx.builder();
		
		sb.pn("import java.util.*;");
		sb.pn("");
		
		sb.pn("@SuppressWarnings({ \"unchecked\", \"rawtypes\" })");
		sb.pn("public class ${1} {", objectName);
		Set<Entry<String, Object>> entrys = map.entrySet();
		for (Entry<String, Object> entry : entrys) {
			String name = entry.getKey();
			Object obj = entry.getValue();
			String type = TypeEx.getBasicType(obj.getClass().getName());
			sb.pn("    public ${1} ${2};", type, name);
		}
		
		sb.pn("");
		sb.pn("    public String toString() {");
		sb.pn("        return toMap().toString();");
		sb.pn("    }");
		
		sb.pn("");
		sb.pn("    public Map toMap() {");
		sb.pn("        Map result = new HashMap();");
		for (Entry<String, Object> entry : entrys) {
			String name = entry.getKey();
			sb.pn("        result.put($[1], this.${2});", name, name);
		}
		sb.pn("        return result;");
		sb.pn("    }");
		sb.pn("");
		
		sb.pn("    public static ${1} fromMap(Map map) {", objectName);
		sb.pn("        ${1} result = new ${1}();", objectName);

		for (Entry<String, Object> entry : entrys) {
			String name = entry.getKey();
			Object obj = entry.getValue();
			String type = obj.getClass().getName();
			String value = TypeEx.typeValue(type);
			sb.pn("        { // ${1}", name);
			sb.pn("            Object obj = map.get($[1]);", name);
			sb.pn("            result.${1} = obj == null ? ${2} : (${3}) obj;", name, value, type);
			sb.pn("        }");
		}
		sb.pn("        return result;");
		sb.pn("    }");
		sb.pn("}");
		return sb.toString();
	}

	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", 1);
		map.put("username", "ÄãºÃ");
		map.put("pwd", "1234");
		map.put("age", 1.1);
		map.put("sex", true);
		map.put("big", new BigDecimal(1));
		String s = fromMap("Userinfo", map);
		System.out.println(s);
	}

}
