package com.bowlong.reflect;

import java.lang.reflect.Method;
import java.util.Vector;

@SuppressWarnings({ "rawtypes" })
public class Invoke {
	public static Object invoke(Object clazz, String method, Vector args)
			throws Exception {
		if (clazz == null)
			return new Exception("clazz is null");

		Class[] types = null;
		Object[] params = null;
		if (args != null) {
			int len = args.size();
			types = new Class[len];
			params = new Object[len];
			int index = 0;
			for (Object e : args) {
				params[index] = e;
				types[index++] = e.getClass();
			}
		}

		Method _method = BeanUtils.getMethod(clazz, method, types);
		// Method _method = clazz.getClass().getMethod(method, types);
		if (_method == null) {
			String s = String.format("class:%s, method:%s types:%s", clazz,
					method, types);
			System.out.println(s);
			return new Exception("method is null");
		}
		return _method.invoke(clazz, params);
	}

	public static Object invoke2(Object object, String method, Vector args)
			throws Exception {
		if (object == null)
			return new Exception("clazz is null");

		Object[] params = null;
		if (args != null) {
			int len = args.size();
			params = new Object[len];
			int index = 0;
			for (Object e : args) {
				params[index] = e;
				index++;
			}
		}

		Method _method = BeanUtils.getMethod(object, method);
		// Method _method = clazz.getClass().getMethod(method, types);
		if (_method == null) {
			String s = String.format("class:%s, method:%s", object,
					method);
			System.out.println(s);
			return new Exception("method is null");
		}
		return _method.invoke(object, params);
	}
}
