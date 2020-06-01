package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue, Iterable<Object> {
    int size = 0;

    public Object dequeue() {
        Object tmp = element();
        remove();
        return tmp;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Queue filter(Predicate<Object> predicate) {
        Queue queue = getEmptyQueue();

        for (Object cur: this){
            if (predicate.test(cur)) {
                queue.enqueue(cur);
            }
        }
        return queue;
    }

    public Queue map(Function<Object, Object> function) {
        Queue queue = getEmptyQueue();

        for (Object cur: this){
            queue.enqueue(function.apply(cur));
        }
        return queue;
    }

    protected abstract Queue getEmptyQueue();
    protected abstract void remove();
}
