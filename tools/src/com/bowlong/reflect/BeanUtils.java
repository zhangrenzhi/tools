package com.bowlong.reflect;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.bowlong.lang.StrEx;
import com.bowlong.sql.mysql.JdbcTemplate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class BeanUtils {
	public static Field[] getFields(Object o) {
		return getFields(o.getClass());
	}

	public static Field getField(Class c, String f) {
		try {
			return c.getField(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getField(Object c, String f) {
		return getField(c.getClass(), f);
	}

	public static Field[] getFields(Class c) {
		return c.getDeclaredFields();
	}

	public static Method[] getMethods(Object o) {
		return getMethods(o.getClass());
	}

	public static Method[] getMethods(Class c) {
		return c.getDeclaredMethods();
	}

	public static Method getMethod(Class c, String method, Class[] types) {
		try {
			return c.getMethod(method, types);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Object c, String method) {
		return getMethod(c.getClass(), method);
	}

	public static Method getMethod(Class c, String method) {
		try {
			Method[] ms = c.getMethods();
			for (Method m : ms) {
				if (m.getName().equals(method)) {
					return m;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getMethod(Object c, String method, Class[] types) {
		return getMethod(c.getClass(), method, types);
	}

	// public static final BeanUtils me = new BeanUtils();

	public static Map<String, Field> getPublicGetFields(Object o) {
		Map<String, Field> ret = new Hashtable<String, Field>();
		Field[] fs = getFields(o);
		Method[] ms = getMethods(o);
		Map<String, Method> m = new Hashtable();
		for (Method method : ms) {
			m.put(method.getName(), method);
		}
		for (Field f : fs) {
			String mname = "get" + StrEx.upperFirst(f.getName());
			String fname = f.getName();
			Method method = m.get(mname);
			if (method == null)
				continue;
			ret.put(fname, f);
		}
		return ret;
	}

	public static List<Field> getPublicGetFields2(Object o) {
		List<Field> ret = new Vector<Field>();
		Field[] fs = getFields(o);
		Method[] ms = getMethods(o);
		Map<String, Method> m = new Hashtable();
		for (Method method : ms) {
			m.put(method.getName(), method);
		}
		for (Field f : fs) {
			String mname = "get" + StrEx.upperFirst(f.getName());
			Method method = m.get(mname);
			if (method == null)
				continue;
			ret.add(f);
		}
		return ret;
	}

	public static Map<String, Field> getPublicSetFields(Object o) {
		Map<String, Field> ret = new Hashtable<String, Field>();
		Field[] fs = getFields(o);
		Method[] ms = getMethods(o);
		Map<String, Method> m = new Hashtable();
		for (Method method : ms) {
			m.put(method.getName(), method);
		}
		for (Field f : fs) {
			String mname = "set" + StrEx.upperFirst(f.getName());
			String fname = f.getName();
			Method method = m.get(mname);
			if (method == null)
				continue;
			ret.put(fname, f);
		}
		return ret;
	}

	public static List<Field> getPublicSetFields2(Object o) {
		List<Field> ret = new Vector<Field>();
		Field[] fs = getFields(o);
		Method[] ms = getMethods(o);
		Map<String, Method> m = new Hashtable();
		for (Method method : ms) {
			m.put(method.getName(), method);
		}
		for (Field f : fs) {
			String mname = "set" + StrEx.upperFirst(f.getName());
			Method method = m.get(mname);
			if (method == null)
				continue;
			ret.add(f);
		}
		return ret;
	}

	public static Method getPublicGetFieldMethod(Object o, Field f) {
		Method[] ms = getMethods(o);
		Map<String, Method> m = new Hashtable();
		for (Method method : ms) {
			m.put(method.getName(), method);
		}
		String fname = f.getName();
		String mname = "get" + StrEx.upperFirst(fname);
		Method method = m.get(mname);
		if (method == null)
			return null;
		return method;
	}

	public static Method getPublicSetFieldMethod(Object o, Field f) {
		Method[] ms = getMethods(o);
		Map<String, Method> m = new Hashtable();
		for (Method method : ms) {
			m.put(method.getName(), method);
		}
		String fname = f.getName();
		String mname = "set" + StrEx.upperFirst(fname);
		Method method = m.get(mname);
		if (method == null)
			return null;
		return method;
	}

	public static Object getValue(Object o, Method m) throws Exception {
		if (o == null || m == null)
			return null;
		return m.invoke(o);
	}

	public static Object getValue(Object o, String f) throws Exception {
		Field field = getField(o, f);
		return getValue(o, field);
	}

	public static Object getValue(Object o, Field f) throws Exception {
		if (o == null || f == null)
			return null;
		Method m = getPublicGetFieldMethod(o, f);
		return getValue(o, m);
	}

	public static Object setValue(Object o, String f, Object v) throws Exception {
		Field field = getField(o, f);
		return setValue(o, field, v);
	}

	public static Object setValue(Object o, Method m, Object v) throws Exception {
		if (o == null || m == null)
			return null;
		return m.invoke(o, v);
	}

	public static Object setValue(Object o, Field f, Object v) throws Exception {
		if (o == null || f == null)
			return null;
		Method m = getPublicSetFieldMethod(o, f);
		return setValue(o, m, v);
	}

	public static <T> T getTValue(Object o, Method m) throws Exception {
		return (T) getValue(o, m);
	}

	public static <T> T getTValue(Object o, Field f) throws Exception {
		return (T) getValue(o, f);
	}

	public static Map toMap(Object o) throws Exception {
		Map ret = new HashMap();
		List<Field> fields = getPublicGetFields2(o);
		for (Field field : fields) {
			String key = field.getName();
			Object var = getValue(o, field);
			ret.put(key, var);
		}
		return ret;
	}

	public static String toString(Object object) throws Exception {
		Map map = toMap(object);
		return map.toString();
	}

	public static Object toBean(Map m, Object o) throws Exception {
		List<Field> fields = getPublicSetFields2(o);
		for (Field field : fields) {
			String key = field.getName();
			Object v = m.get(key);
			setValue(o, field, v);
		}
		return o;
	}

	public static Object toBean(Map m, Class c) throws Exception {
		Object o = c.newInstance();
		return toBean(m, o);
	}

	// ///////////////
	public static BeanDescriptor getBeanDescriptor(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getBeanDescriptor();
	}

	public static PropertyDescriptor[] getPropertyDescriptors(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getPropertyDescriptors();
	}

	public static Method[] getReadMethods(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		Method[] methods = new Method[pds.length];
		int p = 0;
		for (PropertyDescriptor pd : pds) {
			methods[p] = pd.getReadMethod();
			p++;
		}
		return methods;
	}

	public static Method[] getWriteMethod(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		Method[] methods = new Method[pds.length];
		int p = 0;
		for (PropertyDescriptor pd : pds) {
			methods[p] = pd.getWriteMethod();
		}
		return methods;
	}

	public static BeanInfo[] getAdditionalBeanInfo(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getAdditionalBeanInfo();
	}

	public static EventSetDescriptor[] getEventSetDescriptors(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getEventSetDescriptors();
	}

	public static MethodDescriptor[] getMethodDescriptors(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		return beanInfo.getMethodDescriptors();
	}

	public static Method[] getMethods2(Class c) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(c);
		MethodDescriptor[] mds = beanInfo.getMethodDescriptors();
		Method[] methods = new Method[mds.length];
		int p = 0;
		for (MethodDescriptor md : mds) {
			methods[p] = md.getMethod();
			p++;
		}
		return methods;
	}

	public static Annotation[][] getParameterAnnotations(Method method) {
		return method.getParameterAnnotations();
	}

	public static Class<?>[] getParameterTypes(Method method) {
		return method.getParameterTypes();
	}

	public static TypeVariable<Method>[] getTypeParameters(Method method) {
		return method.getTypeParameters();
	}

	public static Class<?> getReturnType(Method method) {
		return method.getReturnType();
	}

	public static Annotation getAnnotation(Method method, Class c) {
		return method.getAnnotation(c);
	}

	public static final List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
		boolean iterative = false;
		return getClasses(packageName, iterative);
	}

	public static final List<Class> getClasses(String packageName, boolean iterative) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new Vector<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		List<Class> classes = new Vector<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName, iterative));
		}
		return classes;
	}

	private static final List<Class> findClasses(File directory, String packageName, boolean iterative) throws ClassNotFoundException {
		List<Class> classes = new Vector<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				if (iterative) {
					// µü´úËÑË÷×ÓÄ¿Â¼
					assert !file.getName().contains(".");
					classes.addAll(findClasses(file, packageName + "." + file.getName(), iterative));
				}
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		List<Class> l = getClasses("com.bowlong.sql.mysql");
		int i = 0;
		for (Class o : l) {
			System.out.println(i++ + ":" + o.getName() + " " + o.equals(JdbcTemplate.class));
		}
	}
}
