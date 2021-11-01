package com.oop.util;

import java.util.*;
import java.util.function.Consumer;

public class BTree<T extends Comparable<T>> extends AbstractCollection<T> implements Tree<T> {
    // Defaulted to 2-3(order 3) B-Tree
    private int maxChildrenSize = 3;
    private int maxKeySize = maxChildrenSize - 1;

    private Node<T> root;
    private int size = 0;
    private final Comparator<T> comparator;

    public BTree() {
        this.root = new NullNode<>();
        this.comparator = null;
    }

    public BTree(Comparator<T> comparator) {
        this.root = new NullNode<>();
        this.comparator = comparator;
    }

    public BTree(int order) {
        this.maxKeySize = order - 1;
        this.maxChildrenSize = order;
        this.root = new NullNode<>();
        this.comparator = null;
    }

    public BTree(int order, Comparator<T> comparator) {
        this.maxKeySize = order - 1;
        this.maxChildrenSize = order;
        this.root = new NullNode<>();
        this.comparator = comparator;
    }

    @Override
    public boolean add(T value) {
        if (root.isNull()) {
            root = new NodeImpl<>(NullNode.getInstance());
            root.addKey(value, comparator);
        } else {
            Node<T> node = root;
            while (!node.isNull()) {
                if (node.isLeaf()) {
                    node.addKey(value, comparator);
                    if (node.keys.size() <= maxKeySize) {
                        break;
                    }
          /* condition : no. of keys exceed the maxKeySize, add the element to the
          node and split it */
                    node.split(this);
                    break;
                }

        /* Traversing through children of a node to place the new node
        at the correct position */
                // case 1: When new key is less than/equal to the leftmost key of the node
                T leftKey = node.keys.get(0);
                if (compare(value, leftKey, comparator) <= 0) {
                    node = node.getChild(0);
                    continue;
                }

                // case 2: When new key is greater than the rightmost key of the node
                int numberOfKeys = node.keys.size();
                int last = numberOfKeys - 1;
                T rightKey = node.keys.get(last);
                if (compare(value, rightKey, comparator) > 0) {
                    node = node.getChild(numberOfKeys);
                    continue;
                }

                // case 3: Check internal nodes to fix the position of the new key
                for (int keyIndex = 1; keyIndex < node.keys.size(); keyIndex++) {
                    T prev = node.keys.get(keyIndex - 1);
                    T next = node.keys.get(keyIndex);
                    if (compare(value, prev, comparator) > 0 && compare(value, next, comparator) <= 0) {
                        node = node.getChild(keyIndex);
                        break;
                    }
                }
            }
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object value) {
        return false; // TODO: logic will be implemented in the future.
    }

    @Override
    public void clear() {
        root = NullNode.getInstance();
        size = 0;
    }

    @Override
    public boolean contains(Object value) {
        Node<T> node = root.getNode((T) value, this);
        return !node.isNull();
    }

    @Override
    public int size() {
        return size;
    }

    static int compare(Object k1, Object k2, Comparator comparator) {
        return comparator == null ? ((Comparable) k1).compareTo(k2) : comparator.compare(k1, k2);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public T elementAt(int index) {
        return (T) this.toArray()[index];
    }

    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }

    @Override
    public Iterator<T> iterator() {
        return (new InorderIterator());
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Stack<Node<T>> nodeStack = new Stack<>();
        Stack<Integer> keyIndexStack = new Stack<>();
        if (!root.keys.isEmpty())
            pushRightPath(root, nodeStack, keyIndexStack);

        while (!nodeStack.isEmpty()) {
            Node<T> node = nodeStack.peek();
            int keyIndex = keyIndexStack.pop();
            T resultKey = node.keys.get(keyIndex - 1);
            keyIndex--;

            if (keyIndex > 0)
                keyIndexStack.push(keyIndex);
            else
                nodeStack.pop();
            if (!node.isLeaf())
                pushRightPath(node.children.get(keyIndex), nodeStack, keyIndexStack);
            action.accept(resultKey);
        }
    }

    private void pushRightPath(Node<T> node, Stack<Node<T>> nodeStack, Stack<Integer> keyIndexStack) {
        while (true) {
            nodeStack.push(node);
            int lastKeyIndex = node.keys.size();
            int lastChildIndex = node.children.size() - 1;
            keyIndexStack.push(lastKeyIndex);
            if (node.isLeaf())
                break;
            node = node.children.get(lastChildIndex);
        }
    }

    private final class InorderIterator implements Iterator<T> {
        private Stack<Node<T>> nodeStack;
        private Stack<Integer> keyIndexStack;

        public InorderIterator() {
            nodeStack = new Stack<>();
            keyIndexStack = new Stack<>();
            if (!root.keys.isEmpty())
                pushLeftPath(root);
        }

        public boolean hasNext() {
            return !nodeStack.isEmpty();
        }

        public T next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Node<T> node = nodeStack.peek();
            int keyIndex = keyIndexStack.pop();
            T resultKey = node.keys.get(keyIndex);
            keyIndex++;

            if (keyIndex < node.keys.size())
                keyIndexStack.push(keyIndex);
            else
                nodeStack.pop();
            if (!node.isLeaf())
                pushLeftPath(node.children.get(keyIndex));
            return resultKey;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void pushLeftPath(Node<T> node) {
            while (true) {
                nodeStack.push(node);
                keyIndexStack.push(0);
                if (node.isLeaf())
                    break;
                node = node.children.get(0);
            }
        }
    }

    private abstract static class Node<T extends Comparable<T>> {
        Node<T> parent;
        List<T> keys;
        List<Node<T>> children;

        protected abstract void addKey(T t, Comparator<T> comparator);

        protected abstract Node<T> getChild(int i);

        protected abstract boolean addChild(Node<T> leftChild, BTree<T> tree);

        protected abstract boolean removeChild(Node<T> node);

        protected abstract Node<T> getNode(T value, BTree<T> tree);

        protected abstract boolean isNull();

        protected abstract void split(BTree<T> tree);

        protected abstract boolean isLeaf();

        public abstract String toString();
    }

    private static class NullNode<T extends Comparable<T>> extends Node<T> {

        private static final NullNode instance = new NullNode();

        private NullNode() {
            this.parent = instance;
            this.keys = Collections.EMPTY_LIST;
            this.children = Collections.EMPTY_LIST;
        }

        public static NullNode getInstance() {
            return instance;
        }

        @Override
        protected Node<T> getChild(int i) {
            return getInstance();
        }

        @Override
        protected void addKey(T t, Comparator<T> comparator) {
        }

        @Override
        protected boolean addChild(Node<T> leftChild, BTree<T> tree) {
            return false;
        }

        @Override
        protected void split(BTree<T> tree) {
        }

        @Override
        protected boolean removeChild(Node<T> node) {
            return false;
        }

        @Override
        protected boolean isNull() {
            return true;
        }

        @Override
        protected Node<T> getNode(T value, BTree<T> tree) {
            return getInstance();
        }

        @Override
        protected boolean isLeaf() {
            return true;
        }

        @Override
        public String toString() {
            return "keys:() parent:() keySize=0 children=0";
        }
    }

    private static class NodeImpl<T extends Comparable<T>> extends Node<T> {

        private NodeImpl(Node<T> parent) {
            this.parent = parent;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        protected void addKey(T value, Comparator<T> comparator) {
            keys.add(value);
            keys.sort(comparator);
        }

        protected Node<T> getChild(int index) {
            if (index >= children.size()) {
                return NullNode.getInstance();
            }
            return children.get(index);
        }

        protected boolean addChild(Node<T> child, BTree<T> tree) {

            Comparator<Node<T>> nodeComparator =
                    (node1, node2) ->
                            tree.comparator == null
                                    ? node1.keys.get(0).compareTo(node2.keys.get(0))
                                    : tree.comparator.compare(node1.keys.get(0), node2.keys.get(0));
            child.parent = this;
            children.add(child);
            children.sort(nodeComparator);
            return true;
        }

        protected boolean removeChild(Node<T> child) {
            boolean found = false;
            if (children.contains(child)) {
                children.remove(child);
                found = true;
            }
            return found;
        }

        @Override
        protected boolean isNull() {
            return false;
        }

        @Override
        protected boolean isLeaf() {
            return children.size() == 0;
        }

        protected Node<T> getNode(T value, BTree<T> tree) {
            Node<T> node = this;
            while (!node.isNull()) {
                T leftKey = node.keys.get(0);
        /* comparing input value with left-most key in the node,
        if 'value' is less, traverse left child to get the node */
                if (compare(value, leftKey, tree.comparator) < 0) {
                    node = node.getChild(0);
                    continue;
                }

                int numberOfKeys = node.keys.size();
                int last = numberOfKeys - 1;
                T rightKey = node.keys.get(last);
        /* comparing input value with right-most key in the node,
        if 'value' is more, traverse right child to get the node*/
                if (compare(value, rightKey, tree.comparator) > 0) {
                    node = node.getChild(numberOfKeys);
                    continue;
                }

                for (int keyIndex = 0; keyIndex < numberOfKeys; keyIndex++) {
                    T currentValue = node.keys.get(keyIndex);
                    if (compare(currentValue, value, tree.comparator) == 0) {
                        return node;
                    }
                    int next = keyIndex + 1;
                    if (next <= last) {
                        T nextValue = node.keys.get(next);
                        // To traverse child subtree when 'value' lies between 2 keys in a node
                        if (compare(currentValue, value, tree.comparator) < 0
                                && compare(nextValue, value, tree.comparator) > 0) {
                            if (next < node.children.size()) {
                                node = node.getChild(next);
                                break;
                            }
                            return NullNode.getInstance();
                        }
                    }
                }
            }
            return NullNode.getInstance();
        }

        protected void split(BTree<T> tree) {
            Node<T> node = this;
            int numberOfKeys = node.keys.size();
            int medianIndex = numberOfKeys / 2;
            T medianValue = node.keys.get(medianIndex);

            Node<T> left = new NodeImpl<>(null);
            // To create left node and add keys before median index position
            for (int keyIndex = 0; keyIndex < medianIndex; keyIndex++) {
                left.addKey(node.keys.get(keyIndex), tree.comparator);
            }
            // To attach the child nodes before median index position to the left node
            // if (node.children.size() > 0) {
            if (!node.isLeaf()) {
                for (int childIndex = 0; childIndex <= medianIndex; childIndex++) {
                    Node<T> leftChild = node.getChild(childIndex);
                    left.addChild(leftChild, tree);
                }
            }

            Node<T> right = new NodeImpl<>(null);
            // To create right node and add keys after median index position
            for (int keyIndex = medianIndex + 1; keyIndex < numberOfKeys; keyIndex++) {
                right.addKey(node.keys.get(keyIndex), tree.comparator);
            }
            // To attach the child nodes after median index position to the right node
            // if (node.children.size() > 0) {
            if (!node.isLeaf()) {
                for (int childIndex = medianIndex + 1; childIndex < node.children.size(); childIndex++) {
                    Node<T> rightChild = node.getChild(childIndex);
                    right.addChild(rightChild, tree);
                }
            }

            if (node.parent.isNull()) {
                // To create new root node, in case there is no parent node
                Node<T> newRoot = new NodeImpl<>(NullNode.getInstance());
                newRoot.addKey(medianValue, tree.comparator);
                node.parent = newRoot;
                tree.root = newRoot;
                node = tree.root;
                node.addChild(left, tree);
                node.addChild(right, tree);
            } else {
                // To make the split node parent, move node to the parent level
                Node<T> parent = node.parent;
                parent.addKey(medianValue, tree.comparator);
                parent.removeChild(node);
                parent.addChild(left, tree);
                parent.addChild(right, tree);

                if (parent.keys.size() > tree.maxKeySize) {
                    parent.split(tree);
                }
            }
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("keys: (");
            keys.forEach(key -> builder.append(key).append(","));
            builder.append(")\t");

            builder.append("parent: (");
            parent.keys.forEach(key -> builder.append(key).append(","));
            builder.append(")\t");

            builder
                    .append("keySize=")
                    .append(keys.size())
                    .append(" children=")
                    .append(children.size())
                    .append("\t");

            return builder.toString();
        }

    }

    private static class TreePrinter {

        public static <T extends Comparable<T>> String getString(BTree<T> tree) {
            return getString(tree.root, "");
        }

        private static <T extends Comparable<T>> String getString(
                Node<T> node, String prefix) {
            StringBuilder builder = new StringBuilder();
            builder.append(prefix).append("└── ");

            builder.append(node.toString());
            builder.append("\n");

            node.children
                    .forEach(child -> builder.append(getString(child, prefix + "    ")));

            return builder.toString();
        }
    }
}
