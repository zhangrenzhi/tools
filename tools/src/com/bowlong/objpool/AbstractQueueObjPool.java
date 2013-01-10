package com.bowlong.objpool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractQueueObjPool<E> {
	protected final Queue<E> queues = new ConcurrentLinkedQueue<E>();

	protected abstract E createObj();

	protected abstract E resetObj(E obj);

	protected abstract E destoryObj(E obj);

	protected int MAX = Short.MAX_VALUE;

	protected final AtomicInteger num = new AtomicInteger();
	
	protected E borrow() {
		synchronized (queues) {
			if (num.intValue() > 0){
				num.decrementAndGet();
				return queues.poll();
			}
		}
		return createObj();
	}

	protected void returnObj(E obj) {
		if(obj == null)
			return ;
		
		synchronized (queues) {
			if (num.intValue() > MAX) {
				destoryObj(obj);
				return;
			}
			resetObj(obj);
			queues.add(obj);
			num.incrementAndGet();
		}
	}

	public void clear() {
		synchronized (queues) {
			queues.clear();
		}
	}

	public int size() {
		return num.intValue();
	}
}
