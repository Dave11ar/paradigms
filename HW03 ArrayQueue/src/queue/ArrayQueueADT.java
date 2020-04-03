package queue;

public class ArrayQueueADT {
    private int head = 0; // last element of queue
    private int size = 0; // size of queue
    private Object[] elements = new Object[1];

    //Pre: queue isn't null
    public static void enqueue(ArrayQueueADT queue, Object value) {
        if (queue.size == queue.elements.length) {
            newList(queue);
        }

        queue.elements[(queue.head + queue.size) % queue.elements.length] = value;
        queue.size++;
    }
    //Post: add element x to back of queue

    //Pre: queue isn't null and queue isn't empty
    public static Object element(ArrayQueueADT queue) {
        return queue.elements[queue.head];
    }
    //Post: return first element of queue

    //Pre: queue isn't null and queue isn't empty
    public static Object dequeue(ArrayQueueADT queue) {
        Object tmp = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.size--;
        return tmp;
    }
    //Post: delete first element of queue and return it

    //Pre: queue isn't null
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }
    //Post: return size of queue

    //Pre: queue isn't null
    public static boolean isEmpty(ArrayQueueADT queue) {
        return size(queue) == 0;
    }
    //Post: return is queue empty

    //Pre: queue isn't null
    public static void clear(ArrayQueueADT queue) {
        queue.head = 0;
        queue.size = 0;
        queue.elements = new Object[1];
    }
    //Post: delete all elements from queue

    //Pre: queue isn't null
    public static void push(ArrayQueueADT queue, Object x) {
        if (queue.size == queue.elements.length) {
            newList(queue);
        }

        queue.head = (queue.head + queue.elements.length - 1) % queue.elements.length;
        queue.elements[queue.head] = x;
        queue.size++;
    }
    //Post: add element x to front

    //Pre: queue isn't null
    public static Object peek(ArrayQueueADT queue) {
        return queue.elements[(queue.head + queue.size - 1) % queue.elements.length];
    }
    //Post: return last element of queue

    //Pre: queue isn't null and queue isn't empty
    public static Object remove(ArrayQueueADT queue) {
        queue.size--;
        Object tmp = queue.elements[(queue.head + queue.size) % queue.elements.length];
        queue.elements[(queue.head + queue.size) % queue.elements.length] = null;
        return tmp;
    }
    //Post: delete last element of queue and return it

    //Pre: true
    private static void newList(ArrayQueueADT queue) {
        Object[] tmp = new Object[queue.elements.length * 2];

        System.arraycopy(queue.elements, queue.head, tmp, 0,
                Math.min(queue.size, queue.elements.length - queue.head));
        System.arraycopy(queue.elements, 0, tmp, Math.min(queue.size, queue.elements.length - queue.head),
                (queue.head + queue.size - 1)  < queue.elements.length ? 0 : (queue.head + queue.size /*- 1*/) % queue.elements.length);

        queue.head = 0;
        queue.elements = tmp;
    }
    //Post: increase capacity of queue by 2 times
}
