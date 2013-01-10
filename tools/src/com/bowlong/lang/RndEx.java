package com.bowlong.lang;

import java.util.List;
import java.util.Random;
import java.util.Vector;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class RndEx {
	private static Random rnd = new Random(System.currentTimeMillis());

	public static final boolean nextBoolean() {
		return rnd.nextBoolean();
	}

	public static final byte[] nextBytes(int len) {
		byte[] bytes = new byte[len];
		rnd.nextBytes(bytes);
		return bytes;
	}

	public static final double nextDouble() {
		return rnd.nextDouble();
	}

	public static final double nextGaussian() {
		return rnd.nextGaussian();
	}

	public static final double nextFloat() {
		return rnd.nextFloat();
	}

	public static final int nextInt() {
		return rnd.nextInt();
	}

	public static final int nextInt(int n) {
		return rnd.nextInt(n);
	}

	public static final int nextInt(int f, int t) {
		return rnd.nextInt(t - f) + f;
	}

	public static final long nextLong() {
		return rnd.nextLong();
	}

	public static final int nextInt(int[] args){
		int i = nextInt(args.length);
		return args[i];
	}

	public static final int nextInt(List<Integer> args){
		int i = nextInt(args.size());
		return args.get(i);
	}

	public static final int nextInt2(int... args){
		int i = nextInt(args.length);
		return args[i];
	}
	
	public static final List random(List srcList) {
		List ret = new Vector();
		List list = new Vector();
		list.addAll(srcList);
		
		int num = list.size();
		for (int n = num ; n > 0; n--) {
			int p = nextInt(n);
			Object e = list.remove(p);
			ret.add(e);
		}
		return ret;
	}

}
