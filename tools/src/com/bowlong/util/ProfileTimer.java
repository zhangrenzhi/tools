package com.bowlong.util;

public class ProfileTimer {
	public void start() {
		start_ = System.currentTimeMillis();
	}

	public void stop() {
		stop_ = System.currentTimeMillis();
	}

	public long elapsed() {
		return stop_ - start_;
	}

	public long stopElapsed() {
		stop();
		return stop_ - start_;
	}

	public long stopShow() {
		stop();
		long e = stop_ - start_;
		System.out.println(e);
		return e;
	}

	private long start_;
	private long stop_;
}
