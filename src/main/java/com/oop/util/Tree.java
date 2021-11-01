package com.oop.util;

import java.util.Collection;
import java.util.Iterator;

public interface Tree<T> extends Collection<T> {

    boolean add(T value);

    boolean remove(Object value);

    void clear();

    boolean contains(Object value);

    int size();

    boolean isEmpty();

    Iterator<T> iterator();

    T elementAt(int index);
}
