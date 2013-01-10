package com.bowlong.lang;

public class Offset {
	public int reader;
	public int writer;

	public String toString() {
		return PStr.begin("[reader:").a(reader).a(",writer:").a(writer).end("]");
	}
}
