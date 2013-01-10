package com.bowlong.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpEx {
	public static final byte[] readUrl(String url) throws Exception {
		InputStream is = openUrl(url);
		try {
			return readStream(is);
		} finally {
			is.close();
		}
	}

	public static final byte[] readUrl(String url, byte[] post)
			throws Exception {
		InputStream is = openUrl(url, post);
		try {
			return readStream(is);
		} finally {
			is.close();
		}
	}

	public static final byte[] readUrl(URL url) throws Exception {
		InputStream is = openUrl(url);
		try {
			return readStream(is);
		} finally {
			is.close();
		}
	}

	public static final InputStream openUrl(String url) throws Exception {
		return openUrl(new URL(url));
	}

	public static final InputStream openUrl(String url, byte[] post)
			throws Exception {
		URL u = new URL(url);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setDoOutput(true);
		huc.setRequestMethod("POST");
		huc.setRequestProperty("Content-type", "text/html; charset=utf-8");
		huc.setRequestProperty("Connection", "close");
		huc.setRequestProperty("Content-Length", post.length + "");
		huc.getOutputStream().write(post);
		huc.getOutputStream().flush();
		huc.getOutputStream().close();
		return huc.getInputStream();
	}

	public static final InputStream openUrl(URL url) throws IOException {
		return url.openStream();
	}

	private static final byte[] readStream(InputStream is) {
		byte[] buf = new byte[1024];
		ByteArrayOutputStream out = newStream();
		int times = 100;
		while (true) {
			try {
				if (times-- <= 0)
					break;
				int len = is.read(buf);
				if (len <= 0)
					break;
				out.write(buf, 0, len);
			} catch (Exception e) {
			}
		}
		byte[] b = out.toByteArray();
		return b;
	}

	private static final ByteArrayOutputStream newStream() {
		return new ByteArrayOutputStream();
	}

}
