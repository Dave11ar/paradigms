package queue;

public class ArrayQueueModule {
    private static int head = 0; // last element of queue
    private static int size = 0; // size of queue
    private static Object[] elements = new Object[1];

    //Pre: true
    public static void enqueue(Object value) {
        if (size == elements.length) {
            newList();
        }

        elements[(head + size) % elements.length] = value;
        size++;
    }
    //Post: add element x to back of queue

    //Pre: queue isn't empty
    public static Object element() {
        return elements[head];
    }
    //Post: return first element of queue

    //Pre: queue isn't empty
    public static Object dequeue() {
        Object tmp = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return tmp;
    }
    //Post: delete first element of queue and return it

    //Pre: true
    public static int size() {
        return size;
    }
    //Post: return size of queue

    //Pre: true
    public static boolean isEmpty() {
        return size() == 0;
    }
    //Post: return is queue empty

    //Pre: true
    public static void clear() {
        head = 0;
        size = 0;
        elements = new Object[1];
    }
    //Post: delete all elements from queue

    //Pre: true
    public static void push(Object x) {
        if (size() == elements.length) {
            newList();
        }

        head = (head + elements.length - 1) % elements.length;
        elements[head] = x;
        size++;
    }
    //Post: add element x to front of queue

    //Pre: queue isn't empty
    public static Object peek() {
        return elements[(head + size - 1) % elements.length];
    }
    //Post: return last element of queue

    //Pre: queue isn't empty
    public static Object remove() {
        size--;
        Object tmp = elements[(head + size) % elements.length];
        elements[(head + size) % elements.length] = null;
        return tmp;
    }
    //Post: delete last element of queue and return it

    //Pre: true
    private static void newList() {
        Object[] tmp = new Object[elements.length * 2];

        System.arraycopy(elements, head, tmp, 0, Math.min(size, elements.length - head));
        System.arraycopy(elements, 0, tmp, Math.min(size, elements.length - head),
                (head + size - 1)  < elements.length ? 0 : (head + size /*- 1*/) % elements.length);

        head = 0;
        elements = tmp;
    }
    //Post: increase capacity of queue by 2 times
}
