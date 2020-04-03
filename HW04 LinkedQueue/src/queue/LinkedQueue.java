package queue;

import java.util.Iterator;

public class LinkedQueue extends AbstractQueue {
    private Node tail;
    private Node head;

    public void enqueue(Object value) {
        if (size == 0) {
            tail = new Node(value, null);
            head = tail;
        } else {
            tail.next = new Node(value, null);
            tail = tail.next;
        }
        size++;
    }

    public Object element() {
        return head.value;
    }

    protected void remove() {
        head = head.next;
        size--;
    }

    public void clear() {
        tail = new Node(null, null);
        head = tail;
        size = 0;
    }

    @Override
    public Iterator<Object> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<Object> {
        Node cur = head;

        public boolean hasNext() {
            return cur != null;
        }

        public Object next() {
            Node ret = cur;
            cur = cur.next;
            return ret.value;
        }
    }

    protected Queue getEmptyQueue() {
        return new LinkedQueue();
    }

    private static class Node {
        private Object value;
        private Node next;

        public Node(Object value, Node next) {
            this.value = value;
            this.next = next;
        }
    }
}
