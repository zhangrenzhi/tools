package com.bowlong.bio2;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bowlong.lang.Offset;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class B2Size {

	public static final void sizeNull(Offset os) throws IOException {
		os.writer += 1;
	}

	public static final void sizeBoolean(Offset os, boolean v)
			throws IOException {
		os.writer += 1;
	}

	public static final void sizeByte(Offset os, int v) throws IOException {
		if (v == 0)
			os.writer += 1;
		else {
			os.writer += 2;
		}
	}

	public static final void sizeShort(Offset os, int v) throws IOException {
		if (v == 0)
			os.writer += 1;
		else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {
			os.writer += 2;
		} else {
			os.writer += 3;
		}
	}

	public static final void sizeInt(Offset os, int v) throws IOException {
		switch (v) {
		case -1:
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
			os.writer += 1;
			break;
		default:
			if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {
				os.writer += 2;
			} else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) {
				os.writer += 3;
			} else {
				os.writer += 5;
			}
			break;
		}
	}

	public static final void sizeIntArray(Offset os, int[] v) throws IOException {
		int len = v.length;
		switch (len) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
			os.writer += 1;
			break;
		default:
			os.writer += 1;
			sizeInt(os, len);
			break;
		}
		for (int i = 0; i < len; i++) {
			sizeInt(os, v[i]);
		}
	}
	
	public static final void sizeInt2DArray(Offset os, int[][] v) throws IOException {
		int len = v.length;
		if(len <= 0){
			os.writer += 1;
			return ;
		}
		os.writer += 1;
		sizeInt(os, len);
		for (int i = 0; i < len; i++) {
			sizeIntArray(os, v[i]);
		}
	}
	
	public static final void sizeLong(Offset os, long v) throws IOException {
		if (v == 0) {
			os.writer += 1;
		} else if (v >= Byte.MIN_VALUE && v <= Byte.MAX_VALUE) {
			os.writer += 2;
		} else if (v >= Short.MIN_VALUE && v <= Short.MAX_VALUE) {
			os.writer += 3;
		} else if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
			os.writer += 5;
		} else {
			os.writer += 9;
		}
	}

	public static final void sizeDouble(Offset os, double var)
			throws IOException {
		long v = Double.doubleToLongBits(var);
		if (v == 0) {
			os.writer += 1;
		} else {
			os.writer += 9;
		}
	}

	public static final void sizeString(Offset os, String v)
			throws IOException {
		if (v == null) {
			sizeNull(os);
		} else {
			int len = v.length();
			switch (len) {
			case 0:
				os.writer += 1;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
				os.writer += 1;
				printString(os, v);
				break;
			default:
				os.writer += 1;
				sizeInt(os, len);
				printString(os, v);
				break;
			}
		}
	}

	public static final void writeBytes(Offset os, byte[] v) throws IOException {
		if (v == null) {
			sizeNull(os);
		} else {
			int len = v.length;
			if (len == 0) {
				os.writer += 1;
			} else {
				os.writer += 1;
				sizeInt(os, len);
				os.writer += v.length;
			}
		}
	}

	public static final void sizeVector(Offset os, List v) throws Exception {
		if (v == null) {
			sizeNull(os);
		} else {
			int len = v.size();
			switch (len) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
				os.writer += 1;
				break;
			default:
				os.writer += 1;
				sizeInt(os, len);
				break;
			}

			for (int i = 0; i < len; i++) {
				//Object object = v.elementAt(i);
				Object object = v.get(i);
				sizeObject(os, object);
			}
		}
	}

	public static final void sizeMap(Offset os, Map v) throws Exception {
		if (v == null) {
			sizeNull(os);
		} else {
			int len = v.size();
			switch (len) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				os.writer += 1;
				break;
			default:
				os.writer += 1;
				sizeInt(os, len);
				break;
			}
			
			Set<Entry> entrys = v.entrySet();
			for (Entry e : entrys) {
				Object key = e.getKey();
				Object var = e.getValue();
				sizeObject(os, key);
				sizeObject(os, var);
			}
			
//			Enumeration keys = v.keys();
//			Iterator keys = v.keySet().iterator();
//			while (keys.hasNext()) {
//				Object key = keys.next();
//				Object var = v.get(key);
//				writeObject(os, key);
//				writeObject(os, var);
//			}
		}
	}

	public static final void sizeObject(Offset os, Object object)
			throws Exception {
		if (object == null) {
			sizeNull(os);
		} else if (object instanceof Map) {
			Map v = (Map) object;
			sizeMap(os, v);
		} else if (object instanceof Integer) {
			int v = ((Integer) object).intValue();
			sizeInt(os, v);
		} else if (object instanceof String) {
			String v = (String) object;
			sizeString(os, v);
		} else if (object instanceof Boolean) {
			boolean v = ((Boolean) object).booleanValue();
			sizeBoolean(os, v);
		} else if (object instanceof Byte) {
			int v = ((Byte) object).byteValue();
			sizeByte(os, v);
		} else if (object instanceof byte[]) {
			byte[] v = (byte[]) object;
			writeBytes(os, v);
		} else if (object instanceof List) {
			List v = (List) object;
			sizeVector(os, v);
		} else if (object instanceof Short) {
			int v = ((Short) object).shortValue();
			sizeShort(os, v);
		} else if (object instanceof Long) {
			long v = ((Long) object).longValue();
			sizeLong(os, v);
		} else if (object instanceof Double) {
			double v = ((Double) object).doubleValue();
			sizeDouble(os, v);
		} else if (object instanceof int[]) {
			int[] v = (int[]) object;
			sizeIntArray(os, v);
		} else if(object instanceof int[][]){
			int[][] v = (int[][]) object;
			sizeInt2DArray(os, v);
		} else {
			throw new IOException("unsupported object:" + object);
		}
	}

	// ////////////////////////////////
	protected static final void printString(Offset os, String v)
			throws IOException {
		printString(os, v, 0, v.length());
	}

	protected static final void printString(Offset os, String v, int offset,
			int length) throws IOException {
		for (int i = 0; i < length; i++) {
			char ch = v.charAt(i + offset);

			if (ch < 0x80)
				os.writer += 1;
			else if (ch < 0x800) {
				os.writer += 2;
			} else {
				os.writer += 3;
			}
		}
	}
}