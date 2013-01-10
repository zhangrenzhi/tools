package com.bowlong.bio2;

import java.nio.charset.Charset;

public class B2Type {
	//null
	public static final byte NULL = 0;
	//bool
	public static final byte BOOLEAN_TRUE = 1;
	public static final byte BOOLEAN_FALSE = 2;
	// byte
	public static final byte BYTE_0 = 3;
	public static final byte BYTE = 4;
	// short
	public static final byte SHORT_0 = 5;
	public static final byte SHORT_8B = 6;
	public static final byte SHORT_16B = 7;
	//int b32 b24 b16 b8
	public static final byte INT_0 = 8;
	public static final byte INT_8B = 9;
	public static final byte INT_16B = 10;
	public static final byte INT_32B = 11;
	public static final byte INT_N1 = 12;
	public static final byte INT_1 = 13;
	public static final byte INT_2 = 14;
	public static final byte INT_3 = 15;
	public static final byte INT_4 = 16;
	public static final byte INT_5 = 17;
	public static final byte INT_6 = 18;
	public static final byte INT_7 = 19;
	public static final byte INT_8 = 20;
	public static final byte INT_9 = 21;
	public static final byte INT_10 = 22;
	public static final byte INT_11 = 23;
	public static final byte INT_12 = 24;
	public static final byte INT_13 = 25;
	public static final byte INT_14 = 26;
	public static final byte INT_15 = 27;
	public static final byte INT_16 = 28;
	public static final byte INT_17 = 29;
	public static final byte INT_18 = 30;
	public static final byte INT_19 = 31;
	public static final byte INT_20 = 32;
	public static final byte INT_21 = 33;
	public static final byte INT_22 = 34;
	public static final byte INT_23 = 35;
	public static final byte INT_24 = 36;
	public static final byte INT_25 = 37;
	public static final byte INT_26 = 38;
	public static final byte INT_27 = 39;
	public static final byte INT_28 = 40;
	public static final byte INT_29 = 41;
	public static final byte INT_30 = 42;
	public static final byte INT_31 = 43;
	public static final byte INT_32 = 44;
	//long b64 b56 b48 b40 b32 b24 b16 b8
	public static final byte LONG_0 = 45;
	public static final byte LONG_8B = 46;
	public static final byte LONG_16B = 47;
	public static final byte LONG_32B = 48;
	public static final byte LONG_64B = 49;
	//double b64 b56 b48 b40 b32 b24 b16 b8
	public static final byte DOUBLE_0 = 50;
//	public static final byte DOUBLE_8B = 51;
//	public static final byte DOUBLE_16B = 52;
//	public static final byte DOUBLE_32B = 53;
	public static final byte DOUBLE_64B = 54;
	//STR [bytes]
	public static final byte STR_0 = 55;
	public static final byte STR = 56;
	public static final byte STR_1 = 57;
	public static final byte STR_2 = 58;
	public static final byte STR_3 = 59;
	public static final byte STR_4 = 60;
	public static final byte STR_5 = 61;
	public static final byte STR_6 = 62;
	public static final byte STR_7 = 63;
	public static final byte STR_8 = 64;
	public static final byte STR_9 = 65;
	public static final byte STR_10 = 66;
	public static final byte STR_11 = 67;
	public static final byte STR_12 = 68;
	public static final byte STR_13 = 69;
	public static final byte STR_14 = 70;
	public static final byte STR_15 = 71;
	public static final byte STR_16 = 72;
	public static final byte STR_17 = 73;
	public static final byte STR_18 = 74;
	public static final byte STR_19 = 75;
	public static final byte STR_20 = 76;
	public static final byte STR_21 = 77;
	public static final byte STR_22 = 78;
	public static final byte STR_23 = 79;
	public static final byte STR_24 = 80;
	public static final byte STR_25 = 81;
	public static final byte STR_26 = 82;
	//Bytes [int len, byte[]]
	public static final byte BYTES_0 = 83;
	public static final byte BYTES = 84;
	//VECTOR [int len, v...]
	public static final byte VECTOR_0 = 85;
	public static final byte VECTOR = 86;
	public static final byte VECTOR_1 = 87;
	public static final byte VECTOR_2 = 88;
	public static final byte VECTOR_3 = 89;
	public static final byte VECTOR_4 = 90;
	public static final byte VECTOR_5 = 91;
	public static final byte VECTOR_6 = 92;
	public static final byte VECTOR_7 = 93;
	public static final byte VECTOR_8 = 94;
	public static final byte VECTOR_9 = 95;
	public static final byte VECTOR_10 = 96;
	public static final byte VECTOR_11 = 97;
	public static final byte VECTOR_12 = 98;
	public static final byte VECTOR_13 = 99;
	public static final byte VECTOR_14 = 100;
	public static final byte VECTOR_15 = 101;
	public static final byte VECTOR_16 = 102;
	public static final byte VECTOR_17 = 103;
	public static final byte VECTOR_18 = 104;
	public static final byte VECTOR_19 = 105;
	public static final byte VECTOR_20 = 106;
	public static final byte VECTOR_21 = 107;
	public static final byte VECTOR_22 = 108;
	public static final byte VECTOR_23 = 109;
	public static final byte VECTOR_24 = 110;
	//HASHTABLE [int len, k,v...]
	public static final byte HASHTABLE_0 = 111;
	public static final byte HASHTABLE = 112;
	public static final byte HASHTABLE_1 = 113;
	public static final byte HASHTABLE_2 = 114;
	public static final byte HASHTABLE_3 = 115;
	public static final byte HASHTABLE_4 = 116;
	public static final byte HASHTABLE_5 = 117;
	public static final byte HASHTABLE_6 = 118;
	public static final byte HASHTABLE_7 = 119;
	public static final byte HASHTABLE_8 = 120;
	public static final byte HASHTABLE_9 = 121;
	public static final byte HASHTABLE_10 = 122;
	public static final byte HASHTABLE_11 = 123;
	public static final byte HASHTABLE_12 = 124;
	public static final byte HASHTABLE_13 = 125;
	public static final byte HASHTABLE_14 = 126;
	public static final byte HASHTABLE_15 = 127;
	// int[]
	public static final byte INT_ARRAY = -9;
	public static final byte INT_ARRAY_0 = -10;
	public static final byte INT_ARRAY_1 = -11;
	public static final byte INT_ARRAY_2 = -12;
	public static final byte INT_ARRAY_3 = -13;
	public static final byte INT_ARRAY_4 = -14;
	public static final byte INT_ARRAY_5 = -15;
	public static final byte INT_ARRAY_6 = -16;
	public static final byte INT_ARRAY_7 = -17;
	public static final byte INT_ARRAY_8 = -18;
	public static final byte INT_ARRAY_9 = -19;
	public static final byte INT_ARRAY_10 = -20;
	public static final byte INT_ARRAY_11 = -21;
	public static final byte INT_ARRAY_12 = -22;
	public static final byte INT_ARRAY_13 = -23;
	public static final byte INT_ARRAY_14 = -24;
	public static final byte INT_ARRAY_15 = -25;
	public static final byte INT_ARRAY_16 = -26;

	// int[][]
	public static final byte INT_2D_ARRAY = -29;
	public static final byte INT_2D_ARRAY_0 = -30;
	
	// java.util.Date
	public static final byte JAVA_DATE = -31;

	public static final Charset UTF8 = Charset.forName("UTF-8");
}

