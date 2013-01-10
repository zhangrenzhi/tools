package com.bowlong.objpool;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjPool {

	private static final Map<Class<?>, ObjectFactory> pools = new HashMap<Class<?>, ObjectFactory>();

	private static final ObjPool m = new ObjPool();

	class ObjectFactory extends AbstractQueueObjPool {
		final Class clazz;

		public ObjectFactory(Class c) {
			this.clazz = c;
		}

		protected Object createObj() {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected Object resetObj(Object obj) {
			return obj;
		}

		protected Object destoryObj(Object obj) {
			return obj;
		}
	}

	private ObjectFactory factory(final Class<?> c) {
		return new ObjectFactory(c);
	}

	private static final ObjectFactory objectPool(Class<?> c) {
		ObjectFactory objectPool = pools.get(c);
		if (objectPool == null) {
			objectPool = m.factory(c);
			pools.put(c, objectPool);
		}
		return objectPool;
	}

	public static <T> T borrowObject(Class<?> c) {
		try {
			ObjectFactory pool = objectPool(c);
			if (pool == null)
				return null;

			return (T) pool.borrow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final void returnObject(Object obj) {
		if (obj == null)
			return;
		Class<?> c = obj.getClass();
		returnObject(obj, c);
	}

	public static final void returnObject(Object obj, Class<?> c) {
		try {
			if (obj == null)
				return;
			ObjectFactory pool = objectPool(c);
			if (pool == null)
				return;

			pool.returnObj(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
