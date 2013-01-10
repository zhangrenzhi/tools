package com.bowlong.netty.objpool;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.bowlong.objpool.AbstractQueueObjPool;

public class ChannelBufferPool extends AbstractQueueObjPool<ChannelBuffer> {

	protected ChannelBuffer createObj() {
		return ChannelBuffers.dynamicBuffer();
	}

	protected ChannelBuffer resetObj(ChannelBuffer obj) {
		obj.clear();
		return obj;
	}

	protected ChannelBuffer destoryObj(ChannelBuffer obj) {
		return resetObj(obj);
	}

	private static ChannelBufferPool my = new ChannelBufferPool();

	public static ChannelBuffer borrowObject() {
		try {
			return my.borrow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final void returnObject(ChannelBuffer obj) {
		try {
			if (obj == null)
				return;

			my.resetObj(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
