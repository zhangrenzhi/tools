package com.bowlong.concurrent.bio2;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.bowlong.lang.ByteEx;
import com.bowlong.util.NewList;
import com.bowlong.util.NewMap;

@SuppressWarnings("rawtypes")
public class B2Codec {
	public static final byte[] toBytes(Map o, int capacity) throws Exception {
		ByteBuffer os = ByteBuffer.allocate(capacity);
		toBytes(os, o);
		int pos = os.position();
		os.flip();
		byte[] result = new byte[pos];
		os.get(result, 0, pos);
		os.clear();
		return result;
	}

	public static final void toBytes(ByteBuffer os, Map o) throws Exception {
		B2OutEncoder.writeObject(os, o);
	}

	public static final NewMap toMap(byte[] b) throws Exception {
		ByteBuffer bais = ByteBuffer.wrap(b);
		return toMap(bais);
	}

	public static final NewMap toMap(ByteBuffer is) throws Exception {
		NewMap v = (NewMap) B2InDecoder.readObject(is);
		return v;
	}

	//////////////////////
	public static final byte[] toBytes(List o, int capacity) throws Exception {
		ByteBuffer os = ByteBuffer.allocate(capacity);
		toBytes(os, o);
		int pos = os.position();
		os.flip();
		byte[] result = new byte[pos];
		os.get(result, 0, pos);
		os.clear();
		return result;
	}

	public static final void toBytes(ByteBuffer os, List o) throws Exception {
		B2OutEncoder.writeObject(os, o);
	}

	public static final NewList toList(byte[] b) throws Exception {
		ByteBuffer bais = ByteBuffer.wrap(b);
		return toList(bais);
	}

	public static final NewList toList(ByteBuffer is) throws Exception {
		NewList v = (NewList) B2InDecoder.readObject(is);
		return v;
	}
	// public static boolean isBio2(byte[] b) {
	// if (B2.isEmpty(b))
	// return false;
	// if (b.length <= 5)
	// return false;
	// byte[] name = new byte[1];
	// name[0] = b[0];
	// byte ver = b[1];
	// byte begin = b[2];
	// byte end = b[b.length - 1];
	//
	// if (!B2.equals(B2Type.NAME, name))
	// return false;
	//
	// if (ver != (byte) B2Type.VER)
	// return false;
	//
	// if (begin != (byte) B2Type.BEGIN)
	// return false;
	//
	// if (end != (byte) B2Type.END)
	// return false;
	//
	// return true;
	// }

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		NewList l = new NewList();
		l.add(("Ò»ºÃ¹þ"));
		l.add((2L));
		l.add(("three"));
		l.add((4.0));
		l.add((5L));
		Hashtable m = new Hashtable();
		m.put(("key¼ü"), ("varÖµ"));
		m.put((1), "value2");
		m.put("list", l);
		byte[] b = B2Codec.toBytes(m, 10 * 1024);
		// boolean bio2 = isBio2(b);
		// System.out.println(bio2);
		System.out.println(b.length );
		System.out.println(ByteEx.bytesToString(b));
		NewMap m2 = B2Codec.toMap(b);
		System.out.println(m2);
		// System.out.println(isBio2(b));
	}

}
