package com.bowlong.bio2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.bowlong.lang.ByteEx;
import com.bowlong.lang.Offset;
import com.bowlong.objpool.OutputArrayPool;
import com.bowlong.util.NewList;
import com.bowlong.util.NewMap;

@SuppressWarnings("rawtypes")
public class B2Helper {
	public static final byte[] toBytes(Map o) throws Exception {
		ByteArrayOutputStream os = OutputArrayPool.borrowObject();
		try {
			toBytes(os, o);
			return os.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			OutputArrayPool.returnObject(os);
		}
	}

	public static final int length(Map o) throws Exception {
		Offset offset = new Offset();
		length(offset, o);
		return offset.writer;
	}
	
	public static final void toBytes(OutputStream os, Map o) throws Exception {
		B2OutputStream.writeObject(os, o);
	}

	public static final void length(Offset offset, Map o) throws Exception {
		B2Size.sizeMap(offset, o);
	}

	public static final NewMap toMap(byte[] b) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return toMap(bais);
	}

	public static final NewMap toMap(InputStream is) throws Exception {
		NewMap v = (NewMap) B2InputStream.readObject(is);
		return v;
	}

	//////////////////////
	public static final byte[] toBytes(List o) throws Exception {
		ByteArrayOutputStream os = OutputArrayPool.borrowObject();
		try {
			toBytes(os, o);
			return os.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			OutputArrayPool.returnObject(os);
		}
	}

	public static final int length(List o) throws Exception {
		Offset offset = new Offset();
		length(offset, o);
		return offset.writer;
	}

	public static final void toBytes(OutputStream os, List o) throws Exception {
		B2OutputStream.writeObject(os, o);
	}

	public static final void length(Offset offset, List o) throws Exception {
		B2Size.sizeVector(offset, o);
	}

	public static final NewList toList(byte[] b) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return toList(bais);
	}

	public static final NewList toList(InputStream is) throws Exception {
		NewList v = (NewList) B2InputStream.readObject(is);
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
		NewMap m = new NewMap();
		m.put(("key¼ü"), ("varÖµ"));
		m.put((1), "value2");
		m.put("list", l);
		byte[] b = B2Helper.toBytes(m);
		// boolean bio2 = isBio2(b);
		// System.out.println(bio2);
		System.out.println(b.length );
		System.out.println(ByteEx.bytesToString(b));
		NewMap m2 = B2Helper.toMap(b);
		System.out.println(m2);
		// System.out.println(isBio2(b));
	}

}
