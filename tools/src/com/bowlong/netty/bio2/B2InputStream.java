package com.bowlong.netty.bio2;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;

import com.bowlong.bio2.B2Type;
import com.bowlong.util.NewList;
import com.bowlong.util.NewMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class B2InputStream {
	public static final int readInt(ChannelBuffer is) throws IOException {
		byte tag = (byte) is.readByte();
		switch (tag) {
		case B2Type.INT_N1:
			return -1;
		case B2Type.INT_0:
			return 0;
		case B2Type.INT_1:
			return 1;
		case B2Type.INT_2:
			return 2;
		case B2Type.INT_3:
			return 3;
		case B2Type.INT_4:
			return 4;
		case B2Type.INT_5:
			return 5;
		case B2Type.INT_6:
			return 6;
		case B2Type.INT_7:
			return 7;
		case B2Type.INT_8:
			return 8;
		case B2Type.INT_9:
			return 9;
		case B2Type.INT_10:
			return 10;
		case B2Type.INT_11:
			return 11;
		case B2Type.INT_12:
			return 12;
		case B2Type.INT_13:
			return 13;
		case B2Type.INT_14:
			return 14;
		case B2Type.INT_15:
			return 15;
		case B2Type.INT_16:
			return 16;
		case B2Type.INT_17:
			return 17;
		case B2Type.INT_18:
			return 18;
		case B2Type.INT_19:
			return 19;
		case B2Type.INT_20:
			return 20;
		case B2Type.INT_21:
			return 21;
		case B2Type.INT_22:
			return 22;
		case B2Type.INT_23:
			return 23;
		case B2Type.INT_24:
			return 24;
		case B2Type.INT_25:
			return 25;
		case B2Type.INT_26:
			return 26;
		case B2Type.INT_27:
			return 27;
		case B2Type.INT_28:
			return 28;
		case B2Type.INT_29:
			return 29;
		case B2Type.INT_30:
			return 30;
		case B2Type.INT_31:
			return 31;
		case B2Type.INT_32:
			return 32;
		case B2Type.INT_8B: {
			byte v = (byte) is.readByte();
			return v;
		}
		case B2Type.INT_16B: {
			short v = (short) (((is.readByte() & 0xff) << 8) + ((is.readByte() & 0xff) << 0));
			return v;
		}
		case B2Type.INT_32B: {
			int value1 = is.readByte();
			int value2 = is.readByte();
			int value3 = is.readByte();
			int value4 = is.readByte();

			int v = ((value1 & 0xff) << 24) + ((value2 & 0xff) << 16)
					+ ((value3 & 0xff) << 8) + ((value4 & 0xff) << 0);
			return v;
		}
		default:
			throw new IOException("read int tag error:" + tag);
		}
	}

	private static final int[] readIntArray(ChannelBuffer is, int len)
			throws Exception {
		int[] ret = new int[len];
		for (int i = 0; i < len; i++) {
			int v = readInt(is);
			ret[i] = v;
		}
		return ret;
	}

	private static final int[][] readInt2DArray(ChannelBuffer is, int len)
			throws Exception {
		int[][] ret = new int[len][];
		for (int i = 0; i < len; i++) {
			Object o = readObject(is);
			int[] v = (int[]) o;
			ret[i] = v;
		}
		return ret;
	}

	private static final NewList readList(ChannelBuffer is, int len)
			throws Exception {
		NewList ret = new NewList();
		for (int i = 0; i < len; i++) {
			Object o = readObject(is);
			// ret.addElement(o);
			ret.add(o);
		}
		return ret;
	}

	private static final NewMap readMap(ChannelBuffer is, int len)
			throws Exception {
		NewMap ret = new NewMap();
		for (int i = 0; i < len; i++) {
			Object key = readObject(is);
			Object var = readObject(is);
			ret.put(key, var);
		}
		return ret;
	}

	public static final Object readObject(ChannelBuffer is) throws Exception {
		byte tag = (byte) is.readByte();
		switch (tag) {
		case B2Type.NULL: {
			return null;
		}
		case B2Type.HASHTABLE_0: {
			return new NewMap();
		}
		case B2Type.HASHTABLE_1: {
			return readMap(is, 1);
		}
		case B2Type.HASHTABLE_2: {
			return readMap(is, 2);
		}
		case B2Type.HASHTABLE_3: {
			return readMap(is, 3);
		}
		case B2Type.HASHTABLE_4: {
			return readMap(is, 4);
		}
		case B2Type.HASHTABLE_5: {
			return readMap(is, 5);
		}
		case B2Type.HASHTABLE_6: {
			return readMap(is, 6);
		}
		case B2Type.HASHTABLE_7: {
			return readMap(is, 7);
		}
		case B2Type.HASHTABLE_8: {
			return readMap(is, 8);
		}
		case B2Type.HASHTABLE_9: {
			return readMap(is, 9);
		}
		case B2Type.HASHTABLE_10: {
			return readMap(is, 10);
		}
		case B2Type.HASHTABLE_11: {
			return readMap(is, 11);
		}
		case B2Type.HASHTABLE_12: {
			return readMap(is, 12);
		}
		case B2Type.HASHTABLE_13: {
			return readMap(is, 13);
		}
		case B2Type.HASHTABLE_14: {
			return readMap(is, 14);
		}
		case B2Type.HASHTABLE_15: {
			return readMap(is, 15);
		}
		case B2Type.HASHTABLE: {
			int len = readInt(is);
			return readMap(is, len);
		}
		case B2Type.INT_N1: {
			return new Integer(-1);
		}
		case B2Type.INT_0: {
			return new Integer(0);
		}
		case B2Type.INT_1: {
			return new Integer(1);
		}
		case B2Type.INT_2: {
			return new Integer(2);
		}
		case B2Type.INT_3: {
			return new Integer(3);
		}
		case B2Type.INT_4: {
			return new Integer(4);
		}
		case B2Type.INT_5: {
			return new Integer(5);
		}
		case B2Type.INT_6: {
			return new Integer(6);
		}
		case B2Type.INT_7: {
			return new Integer(7);
		}
		case B2Type.INT_8: {
			return new Integer(8);
		}
		case B2Type.INT_9: {
			return new Integer(9);
		}
		case B2Type.INT_10: {
			return new Integer(10);
		}
		case B2Type.INT_11: {
			return new Integer(11);
		}
		case B2Type.INT_12: {
			return new Integer(12);
		}
		case B2Type.INT_13: {
			return new Integer(13);
		}
		case B2Type.INT_14: {
			return new Integer(14);
		}
		case B2Type.INT_15: {
			return new Integer(15);
		}
		case B2Type.INT_16: {
			return new Integer(16);
		}
		case B2Type.INT_17: {
			return new Integer(17);
		}
		case B2Type.INT_18: {
			return new Integer(18);
		}
		case B2Type.INT_19: {
			return new Integer(19);
		}
		case B2Type.INT_20: {
			return new Integer(20);
		}
		case B2Type.INT_21: {
			return new Integer(21);
		}
		case B2Type.INT_22: {
			return new Integer(22);
		}
		case B2Type.INT_23: {
			return new Integer(23);
		}
		case B2Type.INT_24: {
			return new Integer(24);
		}
		case B2Type.INT_25: {
			return new Integer(25);
		}
		case B2Type.INT_26: {
			return new Integer(26);
		}
		case B2Type.INT_27: {
			return new Integer(27);
		}
		case B2Type.INT_28: {
			return new Integer(28);
		}
		case B2Type.INT_29: {
			return new Integer(29);
		}
		case B2Type.INT_30: {
			return new Integer(30);
		}
		case B2Type.INT_31: {
			return new Integer(31);
		}
		case B2Type.INT_32: {
			return new Integer(32);
		}
		case B2Type.INT_8B: {
			byte v = (byte) is.readByte();
			return new Integer(v);
		}
		case B2Type.INT_16B: {
			short v = (short) (((is.readByte() & 0xff) << 8) + ((is.readByte() & 0xff) << 0));
			return new Integer(v);
		}
		case B2Type.INT_32B: {
			int v1 = is.readByte();
			int v2 = is.readByte();
			int v3 = is.readByte();
			int v4 = is.readByte();
			int v = ((v1 & 0xff) << 24) + ((v2 & 0xff) << 16)
					+ ((v3 & 0xff) << 8) + ((v4 & 0xff) << 0);
			return new Integer(v);
		}
		case B2Type.STR_0: {
			return "";
		}
		case B2Type.STR_1: {
			return readStringImpl(is, 1);
		}
		case B2Type.STR_2: {
			return readStringImpl(is, 2);
		}
		case B2Type.STR_3: {
			return readStringImpl(is, 3);
		}
		case B2Type.STR_4: {
			return readStringImpl(is, 4);
		}
		case B2Type.STR_5: {
			return readStringImpl(is, 5);
		}
		case B2Type.STR_6: {
			return readStringImpl(is, 6);
		}
		case B2Type.STR_7: {
			return readStringImpl(is, 7);
		}
		case B2Type.STR_8: {
			return readStringImpl(is, 8);
		}
		case B2Type.STR_9: {
			return readStringImpl(is, 9);
		}
		case B2Type.STR_10: {
			return readStringImpl(is, 10);
		}
		case B2Type.STR_11: {
			return readStringImpl(is, 11);
		}
		case B2Type.STR_12: {
			return readStringImpl(is, 12);
		}
		case B2Type.STR_13: {
			return readStringImpl(is, 13);
		}
		case B2Type.STR_14: {
			return readStringImpl(is, 14);
		}
		case B2Type.STR_15: {
			return readStringImpl(is, 15);
		}
		case B2Type.STR_16: {
			return readStringImpl(is, 16);
		}
		case B2Type.STR_17: {
			return readStringImpl(is, 17);
		}
		case B2Type.STR_18: {
			return readStringImpl(is, 18);
		}
		case B2Type.STR_19: {
			return readStringImpl(is, 19);
		}
		case B2Type.STR_20: {
			return readStringImpl(is, 20);
		}
		case B2Type.STR_21: {
			return readStringImpl(is, 21);
		}
		case B2Type.STR_22: {
			return readStringImpl(is, 22);
		}
		case B2Type.STR_23: {
			return readStringImpl(is, 23);
		}
		case B2Type.STR_24: {
			return readStringImpl(is, 24);
		}
		case B2Type.STR_25: {
			return readStringImpl(is, 25);
		}
		case B2Type.STR_26: {
			return readStringImpl(is, 26);
		}
		case B2Type.STR: {
			int len = readInt(is);
			return readStringImpl(is, len);
		}
		case B2Type.BOOLEAN_TRUE: {
			return new Boolean(true);
		}
		case B2Type.BOOLEAN_FALSE: {
			return new Boolean(false);
		}
		case B2Type.BYTE_0: {
			byte v = 0;
			return new Byte(v);
		}
		case B2Type.BYTE: {
			byte v = (byte) is.readByte();
			return new Byte(v);
		}
		case B2Type.BYTES_0: {
			return new byte[0];
		}
		case B2Type.BYTES: {
			int len = readInt(is);
			return is.readBytes(len);
		}
		case B2Type.VECTOR_0: {
			return new NewList();
		}
		case B2Type.VECTOR_1: {
			return readList(is, 1);
		}
		case B2Type.VECTOR_2: {
			return readList(is, 2);
		}
		case B2Type.VECTOR_3: {
			return readList(is, 3);
		}
		case B2Type.VECTOR_4: {
			return readList(is, 4);
		}
		case B2Type.VECTOR_5: {
			return readList(is, 5);
		}
		case B2Type.VECTOR_6: {
			return readList(is, 6);
		}
		case B2Type.VECTOR_7: {
			return readList(is, 7);
		}
		case B2Type.VECTOR_8: {
			return readList(is, 8);
		}
		case B2Type.VECTOR_9: {
			return readList(is, 9);
		}
		case B2Type.VECTOR_10: {
			return readList(is, 10);
		}
		case B2Type.VECTOR_11: {
			return readList(is, 11);
		}
		case B2Type.VECTOR_12: {
			return readList(is, 12);
		}
		case B2Type.VECTOR_13: {
			return readList(is, 13);
		}
		case B2Type.VECTOR_14: {
			return readList(is, 14);
		}
		case B2Type.VECTOR_15: {
			return readList(is, 15);
		}
		case B2Type.VECTOR_16: {
			return readList(is, 16);
		}
		case B2Type.VECTOR_17: {
			return readList(is, 17);
		}
		case B2Type.VECTOR_18: {
			return readList(is, 18);
		}
		case B2Type.VECTOR_19: {
			return readList(is, 19);
		}
		case B2Type.VECTOR_20: {
			return readList(is, 20);
		}
		case B2Type.VECTOR_21: {
			return readList(is, 21);
		}
		case B2Type.VECTOR_22: {
			return readList(is, 22);
		}
		case B2Type.VECTOR_23: {
			return readList(is, 23);
		}
		case B2Type.VECTOR_24: {
			return readList(is, 24);
		}
		case B2Type.VECTOR: {
			int len = readInt(is);
			return readList(is, len);
		}
		case B2Type.SHORT_0: {
			short v = 0;
			return new Short(v);
		}
		case B2Type.SHORT_8B: {
			short v = (short) is.readByte();
			return new Short(v);
		}
		case B2Type.SHORT_16B: {
			short v = (short) (((is.readByte() & 0xff) << 8) + ((is.readByte() & 0xff) << 0));
			return new Short(v);
		}
		case B2Type.LONG_0: {
			int v = 0;
			return new Long(v);
		}
		case B2Type.LONG_8B: {
			int v = is.readByte();
			return new Long(v);
		}
		case B2Type.LONG_16B: {
			int v = (((is.readByte() & 0xff) << 8) + ((is.readByte() & 0xff) << 0));
			return new Long(v);
		}
		case B2Type.LONG_32B: {
			int v1 = is.readByte();
			int v2 = is.readByte();
			int v3 = is.readByte();
			int v4 = is.readByte();
			int v = ((v1 & 0xff) << 24) + ((v2 & 0xff) << 16)
					+ ((v3 & 0xff) << 8) + ((v4 & 0xff) << 0);
			return new Long(v);
		}
		case B2Type.LONG_64B: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) is.readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			return new Long(v);
		}
		case B2Type.JAVA_DATE: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) is.readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			return new java.util.Date(v);
		}
		case B2Type.DOUBLE_0: {
			// int v = 0;
			// double ret = Double.longBitsToDouble(v);
			return new Double(0);
			// }case B2Type.DOUBLE_8B: {
			// int v = is.readByte();
			// double ret = Double.longBitsToDouble(v);
			// return new Double(ret);
			// }case B2Type.DOUBLE_16B: {
			// int v = (((is.readByte() & 0xff) << 8) + ((is.readByte() & 0xff)
			// << 0));
			// double ret = Double.longBitsToDouble(v);
			// return new Double(ret);
			// }case B2Type.DOUBLE_32B: {
			// int v1 = is.readByte();
			// int v2 = is.readByte();
			// int v3 = is.readByte();
			// int v4 = is.readByte();
			//
			// int v = ((v1 & 0xff) << 24) + ((v2 & 0xff) << 16)
			// + ((v3 & 0xff) << 8) + ((v4 & 0xff) << 0);
			// double ret = Double.longBitsToDouble(v);
			// return new Double(ret);
		}
		case B2Type.DOUBLE_64B: {
			byte[] b = new byte[8];
			for (int i = 0; i < 8; i++) {
				b[i] = (byte) is.readByte();
			}
			long high = ((b[0] & 0xff) << 24) + ((b[1] & 0xff) << 16)
					+ ((b[2] & 0xff) << 8) + ((b[3] & 0xff) << 0);
			long low = ((b[4] & 0xff) << 24) + ((b[5] & 0xff) << 16)
					+ ((b[6] & 0xff) << 8) + ((b[7] & 0xff) << 0);
			long v = (high << 32) + (0xffffffffL & low);
			double ret = Double.longBitsToDouble(v);
			return new Double(ret);
		}
		case B2Type.INT_ARRAY_0: {
			return new int[0];
		}
		case B2Type.INT_ARRAY_1: {
			return readIntArray(is, 1);
		}
		case B2Type.INT_ARRAY_2: {
			return readIntArray(is, 2);
		}
		case B2Type.INT_ARRAY_3: {
			return readIntArray(is, 3);
		}
		case B2Type.INT_ARRAY_4: {
			return readIntArray(is, 4);
		}
		case B2Type.INT_ARRAY_5: {
			return readIntArray(is, 5);
		}
		case B2Type.INT_ARRAY_6: {
			return readIntArray(is, 6);
		}
		case B2Type.INT_ARRAY_7: {
			return readIntArray(is, 7);
		}
		case B2Type.INT_ARRAY_8: {
			return readIntArray(is, 8);
		}
		case B2Type.INT_ARRAY_9: {
			return readIntArray(is, 9);
		}
		case B2Type.INT_ARRAY_10: {
			return readIntArray(is, 10);
		}
		case B2Type.INT_ARRAY_11: {
			return readIntArray(is, 11);
		}
		case B2Type.INT_ARRAY_12: {
			return readIntArray(is, 12);
		}
		case B2Type.INT_ARRAY_13: {
			return readIntArray(is, 13);
		}
		case B2Type.INT_ARRAY_14: {
			return readIntArray(is, 14);
		}
		case B2Type.INT_ARRAY_15: {
			return readIntArray(is, 15);
		}
		case B2Type.INT_ARRAY_16: {
			return readIntArray(is, 16);
		}
		case B2Type.INT_ARRAY: {
			int len = readInt(is);
			return readIntArray(is, len);
		}
		case B2Type.INT_2D_ARRAY_0: {
			return new int[0][0];
		}
		case B2Type.INT_2D_ARRAY: {
			int len = readInt(is);
			return readInt2DArray(is, len);
		}
		default:
			throw new IOException("unknow tag error:" + tag);
		}
	}

	// //////////////////////////////////
	private static final String readStringImpl(ChannelBuffer is, int length)
			throws IOException {
		if (length <= 0)
			return "";

		byte[] b = new byte[length];
		is.readBytes(b);
		return new String(b, B2Type.UTF8);
	}

	// private static final String readStringImpl(ChannelBuffer is, int length)
	// throws IOException {
	// StringBuffer sb = new StringBuffer();
	//
	// for (int i = 0; i < length; i++) {
	// int ch = is.readByte();
	//
	// if (ch < 0x80)
	// sb.append((char) ch);
	// else if ((ch & 0xe0) == 0xc0) {
	// int ch1 = is.readByte();
	// int v = ((ch & 0x1f) << 6) + (ch1 & 0x3f);
	//
	// sb.append((char) v);
	// } else if ((ch & 0xf0) == 0xe0) {
	// int ch1 = is.readByte();
	// int ch2 = is.readByte();
	// int v = ((ch & 0x0f) << 12) + ((ch1 & 0x3f) << 6)
	// + (ch2 & 0x3f);
	//
	// sb.append((char) v);
	// } else
	// throw new IOException("bad utf-8 encoding");
	// }
	//
	// return sb.toString();
	// }

	public static NewMap readNewMap(ChannelBuffer is) throws Exception {
		byte tag = (byte) is.readByte();
		switch (tag) {
		case B2Type.NULL: {
			return null;
		}
		case B2Type.HASHTABLE_0: {
			return new NewMap();
		}
		case B2Type.HASHTABLE_1: {
			return readNewMap(is, 1);
		}
		case B2Type.HASHTABLE_2: {
			return readNewMap(is, 2);
		}
		case B2Type.HASHTABLE_3: {
			return readNewMap(is, 3);
		}
		case B2Type.HASHTABLE_4: {
			return readNewMap(is, 4);
		}
		case B2Type.HASHTABLE_5: {
			return readNewMap(is, 5);
		}
		case B2Type.HASHTABLE_6: {
			return readNewMap(is, 6);
		}
		case B2Type.HASHTABLE_7: {
			return readNewMap(is, 7);
		}
		case B2Type.HASHTABLE_8: {
			return readNewMap(is, 8);
		}
		case B2Type.HASHTABLE_9: {
			return readNewMap(is, 9);
		}
		case B2Type.HASHTABLE_10: {
			return readNewMap(is, 10);
		}
		case B2Type.HASHTABLE_11: {
			return readNewMap(is, 11);
		}
		case B2Type.HASHTABLE_12: {
			return readNewMap(is, 12);
		}
		case B2Type.HASHTABLE_13: {
			return readNewMap(is, 13);
		}
		case B2Type.HASHTABLE_14: {
			return readNewMap(is, 14);
		}
		case B2Type.HASHTABLE_15: {
			return readNewMap(is, 15);
		}
		case B2Type.HASHTABLE: {
			int len = readInt(is);
			return readNewMap(is, len);
		}
		default:
			throw new IOException("unknow tag error:" + tag + " readerIndex:" + is.readerIndex());
		}
	}

	private static NewMap readNewMap(ChannelBuffer is, int len) throws Exception {
		NewMap ret = new NewMap();
		for (int i = 0; i < len; i++) {
			Object key = readObject(is);
			Object var = readObject(is);
			ret.put(key, var);
		}
		return ret;
	}
}
