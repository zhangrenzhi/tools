package com.bowlong.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class NewSet<T> extends CopyOnWriteArraySet<T> {

	public static NewSet create() {
		return new NewSet();
	}

	public static NewSet newly() {
		return new NewSet();
	}

	public static NewSet create(Collection c) {
		if (c instanceof NewSet)
			return (NewSet) c;
		NewSet ret = new NewSet();
		return ret.AddAll(c);
	}

	public static NewSet newly(Collection c) {
		return create(c);
	}

	public static NewSet create(Object... objects) {
		NewSet ret = new NewSet();
		for (Object obj : objects) {
			ret.add(obj);
		}
		return ret;
	}

	public static NewSet newly(Object... objects) {
		return create(objects);
	}

	public static <T> NewSet newly(T v) {
		return create().Add(v);
	}

	public NewSet() {
	}

	public NewSet(Collection<T> vars) {
		addAll(vars);
	}

	public NewSet<T> Add(T v) {
		this.add(v);
		return this;
	}

	public NewSet<T> add2(T v) {
		return Add(v);
	}

	public NewSet<T> AddAll(Collection<T> vars) {
		addAll(vars);
		return this;
	}

	public NewSet<T> add2(Collection<T> c) {
		return AddAdd(c);
	}

	public NewSet AddAdd(Object... objects) {
		for (Object obj : objects) {
			this.add((T) obj);
		}
		return this;
	}

	public NewSet add2(Object... objects) {
		return AddAdd(objects);
	}

	public Set<T> synchronizedSet() {
		return Collections.synchronizedSet(this);
	}

	public Set toSet() {
		return this;
	}
}
