package queue;

import java.util.function.Function;
import java.util.function.Predicate;

//E = {e1, e2,..., en}
public interface Queue {
    //Pre: true
    void enqueue(Object value);
    //Post: insert value at back of queue (E' = {value, e1,..., en})

    //Pre: |E| > 0
    Object element();
    //Post: return first element of queue (en in E = {e1,..., en})

    //Pre: |E| > 0
    Object dequeue();
    //Post: return first element of queue and remove it (en in E = {e1,..., en}
    // and E' = {e1,..., en-1})

    //Pre: true
    int size();
    //Post return size of queue (|E|)

    //Pre: true
    boolean isEmpty();
    //Post: return true if |E| = 0, else false

    //Pre: true
    void clear();
    //Post: remove all elements of queue (E' = {})

    //Pre: true
    Queue filter(Predicate<Object> predicate);
    //Post: return queue of elements which satisfy predicate in same order as in a
    //source queue

    //Pre: true
    Queue map(Function<Object, Object> function);
    //Post: return queue of elements in same order as in a source queue and apply
    //function to every element
}

