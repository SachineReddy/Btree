package com.oop.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BTreeTest {

    Tree<Integer> integerBTree;

    @Before
    public void setUp() {
        integerBTree = new BTree<>();
        integerBTree.add(101);
        integerBTree.add(120);
        integerBTree.add(111);
        integerBTree.add(152);
        integerBTree.add(125);
        integerBTree.add(80);
        integerBTree.add(75);
        integerBTree.add(99);
        integerBTree.add(82);
        integerBTree.add(83);
    }

    @Test
    @DisplayName("Test toArray method of Btree")
    public void testToArray(){
       Object[] expectedArray = {75,80,82,83,99,101,111,120,125,152};
       Object[] btreeArray = integerBTree.toArray();
       Assert.assertArrayEquals(expectedArray, btreeArray);
    }

    @Test
    @DisplayName("toString for empty tree - to test NullNode")
    public void testToStringForEmptyTree(){
        Tree<Integer> emptyBTree = new BTree<>();
        String expectedOutput = "└── keys:() parent:() keySize=0 children=0\n";
        String actualOutput = emptyBTree.toString();
        Assert.assertEquals(expectedOutput, actualOutput);
    }

    @Test
    @DisplayName("External iteration nodes of Btree")
    public void testExternalIterationOfBtree() {
        List<Integer> iteratedList = new ArrayList<>();
        Integer[] ascendingArray = {75,80,82,83,99,101,111,120,125,152};
        List<Integer> expectedAscendingList = Arrays.asList(ascendingArray);

        Iterator<Integer> treeIterator = integerBTree.iterator();
        while (treeIterator.hasNext()) {
            iteratedList.add(treeIterator.next());
        }
        Assert.assertEquals(expectedAscendingList, iteratedList);
    }

    @Test
    @DisplayName("Internal iteration nodes of Btree")
    public void testInternalIterationOfBtree() {
        List<Integer> reverseIteratedList = new ArrayList<>();
        Integer[] descendingArray = {152,125,120,111,101,99,83,82,80,75};
        List<Integer> expectedDescendingList = Arrays.asList(descendingArray);

        integerBTree.forEach(reverseIteratedList::add);
        Assert.assertEquals(expectedDescendingList, reverseIteratedList);
    }
}
