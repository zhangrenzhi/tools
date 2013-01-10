package com.bowlong.util;

import java.text.SimpleDateFormat;

public class FormatEx {

	static final NewMap<String, SimpleDateFormat> _SDF = new NewMap<String, SimpleDateFormat>();

	public static final SimpleDateFormat getFormat(String fmt) {
		SimpleDateFormat sdf = _SDF.get(fmt);
		if (sdf == null) {
			sdf = new SimpleDateFormat(fmt);
			_SDF.put(fmt, sdf);
		}

		return sdf;
	}

}
