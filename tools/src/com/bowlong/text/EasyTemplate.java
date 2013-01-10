package com.bowlong.text;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bowlong.objpool.StringBuilderPool;
import com.bowlong.util.MapEx;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EasyTemplate {
	private class Cache {
		public byte[] b;
		public long lastModified;
	}

	public static final Map<String, Cache> caches = new Hashtable<String, Cache>();

	public static EasyTemplate _template = new EasyTemplate();

	public Cache newCache() {
		return new Cache();
	}

	public static final String make(File file, Map<String, String> params,
			String encode) throws Exception {
		byte[] b = readFully(file);
		String s = new String(b, encode);
		return make(s, params);
	}

	public static final String make2(File file, Map<String, String> params,
			String encode) throws Exception {
		String fname = file.getPath();

		byte[] b;
		if (caches.containsKey(fname)) {
			Cache c = caches.get(fname);
			if (c == null || c.lastModified < file.lastModified()) {
				c = _template.newCache();
				b = readFully(file);
				c.b = b;
				c.lastModified = file.lastModified();
				caches.put(fname, c);
			} else {
				b = c.b;
			}
		} else {
			Cache c = _template.newCache();
			b = readFully(file);
			c.b = b;
			c.lastModified = file.lastModified();
			caches.put(fname, c);
		}

		String s = new String(b, encode);
		return make(s, params);
	}

	public static final String make(String s, Map params) throws Exception {
		if (s == null || s.isEmpty() || params == null || params.isEmpty())
			return s;
		if (s.indexOf("${") < 0 && s.indexOf("$[") < 0)
			return s;
		StringBuilder sb = StringBuilderPool.borrowObject();
		sb.append(s);
		// StringBuilder sb = new StringBuilder(s);
		try {
			Set<Entry> entrys = params.entrySet();
			for (Entry e : entrys) {
				Object key = e.getKey();
				Object v = e.getValue();
				String k = "${" + key + "}";
				String k2 = "$[" + key + "]";
				String var = String.valueOf(v);

				int i1 = sb.indexOf(k);
				while (i1 >= 0) {
					int end = i1 + k.length();
					sb.replace(i1, end, var);
					i1 = sb.indexOf(k);
				}

				int i2 = sb.indexOf(k2);
				while (i2 >= 0) {
					int end = i2 + k2.length();
					sb.replace(i2, end, "\"" + var + "\"");
					i2 = sb.indexOf(k2);
				}

				// while (s.contains(k))
				// s = s.replace(k, var);
				// while (s.contains(k2))
				// s = s.replace(k2, "\"" + var + "\"");
			}
			return sb.toString();
		} finally {
			StringBuilderPool.returnObject(sb);
		}
	}

	public static final byte[] readFully(File f) throws Exception {
		if (f == null || !f.exists()) {
			throw new IOException("file no exists");
		}
		int len = (int) f.length();
		byte[] b = new byte[len];
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		dis.readFully(b);
		fis.close();

		return b;
	}

	public static final Map<String, String> newMap() {
		return new HashMap<String, String>();
	}

	public static void main(String[] args) throws Exception {
		String str = EasyTemplate.make(
				"aaaabbbbccc1 ${abc.name} [ÄãºÃ] 1cccc $[a]", MapEx.builder()
						.put("abc.name", "xxxxxxxxx").put("a", "__%[x¹»x]__")
						.toMap());
		System.out.println(str);
	}
}
