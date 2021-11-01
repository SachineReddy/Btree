package com.oop.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TreeUtils {

    // private constructor to prevent instantiation
    private TreeUtils() {
    }

    public static <T> List<T> toList(Tree<T> tree) {
        T[] elementArray = (T[]) tree.toArray();
        return Arrays.asList(elementArray);
    }

    public static <T> List<T> toList(Tree<T> tree, Comparator<T> comparator) {
        return toList(tree).stream().sorted(comparator).collect(Collectors.toList());
    }

    /* @param comparator - specify sort order,
      @param predicate  - filter criteria to restrict elements
      @param dataMapper - function to show specific data from returned list of objects */
    public static <T, R> List<?> toList(Tree<T> tree, Comparator<T> comparator,
                                        Predicate<T> predicate, Function<T, R> dataMapper) {
        List<T> sortedList = toList(tree, comparator);
        return sortedList.stream()
                .filter(predicate)
                .map(dataMapper)
                .collect(Collectors.toList());
    }
}
