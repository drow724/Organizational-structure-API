package com.batch.chunk;

import java.util.LinkedList;
import java.util.List;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;

public class Reader<T> implements ItemReader<T> {

	private List<T> list;

	public Reader(List<T> list) {
		// If it is a proxy we assume it knows how to deal with its own state.
		// (It's probably transaction aware.)
		if (AopUtils.isAopProxy(list)) {
			this.list = list;
		}
		else {
			this.list = new LinkedList<>(list);
		}
	}

    @Nullable
	@Override
	public T read() {
		if (!list.isEmpty()) {
			return list.remove(0);
		}
		return null;
	}

}
