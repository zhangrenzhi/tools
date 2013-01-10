package com.bowlong.objpool;

public class StringBuilderPool extends AbstractQueueObjPool<StringBuilder> {
	public static final StringBuilderPool POOL = new StringBuilderPool();

	public StringBuilderPool() {
	}

	public StringBuilderPool(int num) {
		for (int i = 0; i < num; i++) {
			returnObj(createObj());
		}
	}

	@Override
	public StringBuilder createObj() {
		return new StringBuilder();
	}

	@Override
	public StringBuilder resetObj(StringBuilder obj) {
		obj.setLength(0);
		return obj;
	}

	@Override
	public StringBuilder destoryObj(StringBuilder obj) {
		obj.setLength(0);
		return obj;
	}

	public static StringBuilder borrowObject() {
		return POOL.borrow();
	}

	public static void returnObject(StringBuilder obj) {
		POOL.returnObj(obj);
	}

}
