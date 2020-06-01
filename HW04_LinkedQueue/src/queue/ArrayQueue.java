package queue;

import java.util.Iterator;

public class ArrayQueue extends AbstractQueue {
    private int head = 0;
    private Object[] elements = new Object[1];

    public void enqueue(Object value) {
        if (size == elements.length) {
            newList();
        }

        elements[(head + size++) % elements.length] = value;
    }

    public Object element() {
        return elements[head];
    }

    public void clear() {
        head = 0;
        size = 0;
        elements = new Object[1];
    }

    protected void remove() {
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
    }

    @Override
    public Iterator<Object> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<Object> {
        int cur = 0;

        public boolean hasNext() {
            return cur < size;
        }

        public Object next() {
            return elements[(head + cur++) % elements.length];
        }
    }

    protected Queue getEmptyQueue() {
        return new ArrayQueue();
    }

    private Object[] makeArray(int newSize) {
        Object[] tmp = new Object[newSize];

        System.arraycopy(elements, head, tmp, 0, elements.length - head);
        System.arraycopy(elements, 0, tmp, elements.length - head, head);
        return tmp;
    }

    private void newList() {
        elements = makeArray(elements.length * 2);
        head = 0;
    }
}

