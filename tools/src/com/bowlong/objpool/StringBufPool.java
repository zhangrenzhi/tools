package com.bowlong.objpool;

public class StringBufPool extends AbstractQueueObjPool<StringBuffer> {

	public static final StringBufPool POOL = new StringBufPool();

	public StringBufPool() {
	}

	public StringBufPool(int num) {
		for (int i = 0; i < num; i++) {
			returnObj(createObj());
		}
	}

	@Override
	public StringBuffer createObj() {
		return new StringBuffer();
	}

	@Override
	public StringBuffer resetObj(StringBuffer obj) {
		obj.setLength(0);
		return obj;
	}

	@Override
	public StringBuffer destoryObj(StringBuffer obj) {
		obj.setLength(0);
		return obj;
	}

	public static StringBuffer borrowObject() {
		return POOL.borrow();
	}

	public static void returnObject(StringBuffer obj) {
		POOL.returnObj(obj);
	}

}
