package com.bowlong.objpool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OutputArrayPool extends
		AbstractQueueObjPool<ByteArrayOutputStream> {
	public static final OutputArrayPool POOL = new OutputArrayPool();

	public static ByteArrayOutputStream borrowObject() {
		return POOL.borrow();
	}

	public OutputArrayPool() {
	}

	public OutputArrayPool(int num) {
		for (int i = 0; i < num; i++) {
			returnObj(createObj());
		}
	}

	public static final void returnObject(ByteArrayOutputStream obj) {
		POOL.returnObj(obj);
	}

	@Override
	public ByteArrayOutputStream createObj() {
		return new ByteArrayOutputStream();
	}

	@Override
	public ByteArrayOutputStream resetObj(ByteArrayOutputStream obj) {
		obj.reset();
		return obj;
	}

	@Override
	public ByteArrayOutputStream destoryObj(ByteArrayOutputStream obj) {
		try {
			obj.close();
		} catch (IOException e) {
		}
		return obj;
	}

}
