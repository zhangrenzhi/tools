package com.bowlong.security;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64 {

	private static BASE64Encoder base64encoder = new BASE64Encoder();

	private static BASE64Decoder base64decoder = new BASE64Decoder();

	public static String encode(byte[] buf) {
//		return Base64.encode(buf);
		return base64encoder.encode(buf);
	}

	public static byte[] decode(String enc) throws IOException {
//		return Base64.decode(enc);
		return base64decoder.decodeBuffer(enc);
	}

	final static char[] b32 = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6' };

	final static char endChar = '-';

	static byte[] b32_valid;
	static {
		
		b32_valid = new byte[127];
		for (int i = 0; i < b32_valid.length; i++)
			b32_valid[i] = 33;
		for (int i = 0; i < b32.length; i++)
			b32_valid[b32[i]] = (byte) i;
	}

	public static String encode32(byte[] buf) {
		int size = buf.length / 5;
		int rest = buf.length % 5;

		char[] enc = new char[rest > 0 ? (size * 8 + 8) : size * 8];
		size *= 5;
		int enc_index = 0, i = 0;
		for (; i < size; i += 5) {
			enc[enc_index++] = b32[(buf[i] & 0xf8) >> 3];
			enc[enc_index++] = b32[((buf[i] & 7) << 2)
					| ((buf[i + 1] & 0xc0) >> 6)];
			enc[enc_index++] = b32[(buf[i + 1] & 0x3e) >> 1];
			enc[enc_index++] = b32[((buf[i + 1] & 1) << 4)
					| ((buf[i + 2] & 0xf0) >> 4)];
			enc[enc_index++] = b32[((buf[i + 2] & 0xf) << 1)
					| ((buf[i + 3] & 0x80) >> 7)];
			enc[enc_index++] = b32[(buf[i + 3] & 0x7c) >> 2];
			enc[enc_index++] = b32[((buf[i + 3] & 3) << 3)
					| ((buf[i + 4] & 0xe0) >> 5)];
			enc[enc_index++] = b32[buf[i + 4] & 0x1f];
		}

		switch (rest) {
		case 1:
			enc[enc_index++] = b32[(buf[i] & 0xf8) >> 3];
			enc[enc_index++] = b32[((buf[i] & 7) << 2)];
			for (int j = 0; j < 6; j++)
				enc[enc_index++] = endChar;
			break;
		case 2:
			enc[enc_index++] = b32[(buf[i] & 0xf8) >> 3];
			enc[enc_index++] = b32[((buf[i] & 7) << 2)
					| ((buf[i + 1] & 0xc0) >> 6)];
			enc[enc_index++] = b32[(buf[i + 1] & 0x3e) >> 1];
			enc[enc_index++] = b32[((buf[i + 1] & 1) << 4)];
			for (int j = 0; j < 4; j++)
				enc[enc_index++] = endChar;
			break;
		case 3:
			enc[enc_index++] = b32[(buf[i] & 0xf8) >> 3];
			enc[enc_index++] = b32[((buf[i] & 7) << 2)
					| ((buf[i + 1] & 0xc0) >> 6)];
			enc[enc_index++] = b32[(buf[i + 1] & 0x3e) >> 1];
			enc[enc_index++] = b32[((buf[i + 1] & 1) << 4)
					| ((buf[i + 2] & 0xf0) >> 4)];
			enc[enc_index++] = b32[((buf[i + 2] & 0xf) << 1)];
			for (int j = 0; j < 3; j++)
				enc[enc_index++] = endChar;
			break;
		case 4:
			enc[enc_index++] = b32[(buf[i] & 0xf8) >> 3];
			enc[enc_index++] = b32[((buf[i] & 7) << 2)
					| ((buf[i + 1] & 0xc0) >> 6)];
			enc[enc_index++] = b32[(buf[i + 1] & 0x3e) >> 1];
			enc[enc_index++] = b32[((buf[i + 1] & 1) << 4)
					| ((buf[i + 2] & 0xf0) >> 4)];
			enc[enc_index++] = b32[((buf[i + 2] & 0xf) << 1)
					| ((buf[i + 3] & 0x80) >> 7)];
			enc[enc_index++] = b32[(buf[i + 3] & 0x7c) >> 2];
			enc[enc_index++] = b32[((buf[i + 3] & 3) << 3)];
			enc[enc_index++] = endChar;
			break;
		}
		return new String(enc);
	}

	public static byte[] decode32(String str) throws IOException {
		char[] chs = str.toLowerCase().toCharArray();
		if ((chs.length & 0x7) != 0)
			throw new IOException("B32 encode need to have 8 bytes");
		int count;
		for (count = chs.length - 1; count >= 0; count--) {
			if (chs[count] != endChar)
				break;
		}
		count++;
		byte[] buf = new byte[count * 5 / 8];
		int buf_index = 0;
		int restBits = 0;
		int rest = 0;
		for (int i = 0; i < count; i++) {
			char c = chs[i];
			if (c > 126)
				throw new IOException("B32 encode need to have 8 bytes");
			byte b = b32_valid[c];
			if (b == 33)
				throw new IOException("B32 encode need to have 8 bytes");

			if (restBits < 0) {
				buf[buf_index] |= (byte) (b >> (5 + restBits));
				restBits += 5;
				rest = b & ((2 << restBits) - 1);
			} else if (3 > restBits) {
				buf[buf_index] |= (byte) ((rest << (8 - restBits)) | (b << (3 - restBits)));
				restBits -= 3;
				rest = 0;
			} else {
				buf[buf_index] |= (byte) ((rest << (8 - restBits)) | (b >> (restBits - 3)));
				restBits -= 3;
				rest = b & ((2 << restBits) - 1);
			}
			if (restBits >= 0)
				buf_index++;
		}

		return buf;
	}
}