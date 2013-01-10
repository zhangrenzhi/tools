package com.bowlong.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListBuilder {

	public List list = new ArrayList();

	public static final ListBuilder builder() {
		return new ListBuilder();
	}

	public static final ListBuilder builder(List list) {
		ListBuilder result = new ListBuilder();
		result.list = list;
		return result;
	}

	// public ListBuilder add(Object var) {
	// list.add(var);
	// return this;
	// }

	public ListBuilder addAll(List l) {
		list.addAll(l);
		return this;
	}

	public ListBuilder add(Object... vars) {
		for (Object v : vars) {
			list.add(v);
		}
		return this;
	}

	public ListBuilder retainAll(List l) {
		list.retainAll(l);
		return this;
	}

	public ListBuilder remove(int index) {
		list.remove(index);
		return this;
	}

	public ListBuilder remove(Object o) {
		list.remove(o);
		return this;
	}

	public ListBuilder removeAll(List l) {
		list.removeAll(l);
		return this;
	}

	public List toList() {
		return list;
	}
	
	public NewList toNewList(){
		return NewList.create(list);
	}

	public NewMap toNewMap() {
		NewMap result = new NewMap();
		for (Object o : list) {
			result.put(o, o);
		}
		return result;
	}

	public Map toMap() {
		return toNewMap();
	}

	public ListBuilder sort(Comparator c) {
		Collections.sort(list, c);
		return this;
	}

	public String toString() {
		return list.toString();
	}

	public static void main(String[] args) {
		List l1 = ListBuilder.builder().add(1).add(2).add("3").toList();

		List m = ListBuilder.builder().add(00).add("111", "111").add(222, 222).add(333, 333).addAll(l1)

		.toList();
		System.out.println(m);
	}

}
