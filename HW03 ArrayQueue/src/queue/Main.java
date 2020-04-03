package queue;

public class Main {
    public static void main(String[] args) {
        ArrayQueue queue1 = new ArrayQueue();

        for (int i = 0; i < 10; i++) {
            queue1.push(i);
            //System.out.println(queue1.element());
            //System.out.println(queue1.dequeue());
        }

        for (int i = 0; i < 10; i++) {
            //System.out.println(queue1.dequeue());
            //System.out.println(queue2.dequeue());
            //System.out.println();
        }
    }
}
