package com.example.demo.common.collections;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Zav Deng/dengzf@asiainfo.com on 17-9-18.
 */
abstract class AbstractDataSet<E> extends AbstractCollection<E> {

    public static final int DEFAULT_INIT_CAPACITY = 5;

    protected DataSet.Type type;

    protected Collection<E> dataHolder;

    public AbstractDataSet() {
        this(DEFAULT_INIT_CAPACITY);
    }

    public AbstractDataSet(int initCapacity) {
        this(initCapacity, DataSet.Type.ARRAY);
    }

    public AbstractDataSet(int initCapacity, DataSet.Type type) {
        this.type = type;
        switch (type.toString()) {
            case "ARRAY": this.dataHolder = new ArrayList<E>(initCapacity); break;
            case "STACK": this.dataHolder = new Stack<>(); break;
            case "QUEUE": this.dataHolder = new LinkedBlockingDeque<>(initCapacity); break;
            case "LINKED": this.dataHolder = new LinkedList<>(); break;
            case "HASHSET": this.dataHolder = new HashSet<>(); break;
            case "TREESET": this.dataHolder = new TreeSet<>(); break;
            default: throw new UnsupportedOperationException(type + " type DataList not supported");
        }
    }

    public AbstractDataSet(List<E> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("Can't wrap null List object");
        }

        if (dataList instanceof AbstractDataSet) {
            AbstractDataSet<E> _data = ((AbstractDataSet)dataList);
            this.type = _data.type;
            this.dataHolder = _data.dataHolder;
            _data.dataHolder = null;
        } else if (dataList instanceof LinkedList) {
            this.type = DataSet.Type.LINKED;
            this.dataHolder = dataList;
        } else if (dataList instanceof Queue) {
            this.type = DataSet.Type.QUEUE;
            this.dataHolder = dataList;
        } else if (dataList instanceof Stack) {
            this.type = DataSet.Type.STACK;
            this.dataHolder = dataList;
        } else if (dataList instanceof HashSet) {
            this.type = DataSet.Type.HASHSET;
            this.dataHolder = dataList;
        } else if (dataList instanceof TreeSet) {
            this.type = DataSet.Type.TREESET;
            this.dataHolder = dataList;
        } else {
            throw new IllegalArgumentException("Don't known how wrap List: " + dataList.getClass());
        }
    };

    void addFirst(E v) {
        if (this.type != DataSet.Type.LINKED)
            throw new IllegalStateException("Only LINKED List supports addFirst");
        ((LinkedList)this.dataHolder).addFirst(v);
    }

    public E get(int index) {

        if (this.dataHolder instanceof List)
            return (E) ((List) this.dataHolder).get(index);
        else
            throw new UnsupportedOperationException("Only List supports index operation");
    }

    public Object remove(int index) {
        if (this.dataHolder instanceof List)
            return ((List) this.dataHolder).remove(index);
        else
            throw new UnsupportedOperationException("Only List supports remove by index operation");
    }

    public boolean add(E v) {
        return dataHolder.add(v);
    }

    @Override
    public boolean remove(Object o) {
        return dataHolder.remove(o);
    }

    @Override
    public boolean isEmpty() {
        return dataHolder.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return dataHolder.contains(o);
    }

    @Override
    public Object[] toArray() {
        return dataHolder.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return dataHolder.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return dataHolder.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return dataHolder.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return dataHolder.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return dataHolder.retainAll(c);
    }

    @Override
    public void clear() {
        dataHolder.clear();
    }

    @Override
    public String toString() {
        return dataHolder.toString();
    }

    @Override
    public Iterator<E> iterator() {
        return dataHolder.iterator();
    }

    @Override
    public int size() {
        return this.dataHolder.size();
    }

}
