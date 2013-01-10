package com.bowlong.netty.bio2;

import java.util.Map;

public interface TcpChannel {
	public void send(Map<Object, Object> params) throws Exception;
	public void send(byte[] buff) throws Exception;
}
