package search;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchMissing {

    //pre for all i < j: array[i] >= array[j]
    public static int iterate(List<Integer> array, int x) {
        int l = -1;
        int r = array.size();

        // l' = l, r' = r

        // pre: for all i < j: array[i] >= array[j] && l <= r - 1 &&
        // array[l] > x && array[r] <= x

        // inv l' < r' - 1 && array[l'] > x && array[r'] <= x
        while (l < r - 1) {
            int mid = (l + r) / 2;
            // l' < mid < r'

            if (array.get(mid) <= x) {
                // inv && array[mid] <= x
                r = mid;
                // r'' < r' && array[r''] <= x
            } else {
                // inv && array[mid] > x
                l = mid;
                // l'' > l' && array[l''] > x
            }
        }
        //post: array[l'] > x && array[r'] <= x && l' == r' - 1
        //--> r' = min(i): array[r'] <= x

        if (r == array.size() || array.get(r) != x) {
            // x isn't exists in array && r' = min(i): array[i] <= x
            // --> r' = pos of x in sorted mas
            return -r - 1;
        } else {
            // x exists in array && r' = min(i) : array[i] = x
            // --> r' = min(i): array[r'] = x
            return r;
        }
    }
    //post: return min(i): array[i] = x if exists || -pos - 1 (pos = place of x in sorted mas)


    // pre: for all i < j: array[i] >= array[j] && array[l'] > x && array[r'] <= x
    public static int recursion(List<Integer> array, int x, int l, int r) {
        if  (l < r - 1) {
            // l' < r' - 1
            int mid = (l + r) / 2;
            // l' < mid < r'

            if (array.get(mid) <= x) {
                // array[mid] <= x


                // pre of "recursion" satisfied:
                // for all i < j(l'...r'): array[i] >= array[j] && array[l'] > x && array[r' = mid] <= x
                return recursion(array, x, l, mid);
                // --> post: return min(i): array[i] = x if exists || -pos - 1 (pos = place of x in sorted mas)
            } else {
                // array[mid] > x

                // pre of "recursion" satisfied:
                // for all i < j(l'...r'): array[i] >= array[j] && array[l' = mid] > x && array[r'] <= x
                return recursion(array, x, mid, r);
                // --> post: return min(i): array[i] = x if exists || -pos - 1 (pos = place of x in sorted mas)
            }
        } else {
            // l' == r' - 1 && array[l'] > x && array[r'] <= x
            // --> r' = min(i) array[i] <= x

            if (r == array.size() || array.get(r) != x) {
                // x isn't exists in array && r' = min(i): array[i] <= x
                // --> r' = pos of x in sorted mas
                return -r - 1;
            } else {
                // x exists in array && r' = min(i) : array[i] = x
                // --> r' = min(i): array[r'] = x
                return r;
            }
        }
    }
    //post: return min(i): array[i] = x if exists || -pos - 1 (pos = place of x in sorted mas)


    // pre: input 1 or more ints, for input[1...last]: input[i] >= input[j] for all i < j
    public static void main(String[] args) {
        int x;
        List<Integer> array = new ArrayList<>();
        x = Integer.parseInt(args[0]);

        for (int i = 1; i < args.length; i++) {
            array.add(Integer.parseInt(args[i]));
        }

        // set array[-1] = inf, array[array,size()] == -inf

        System.out.println(recursion(array, x, -1, array.size()));
    }
    // post: Sout return min(i): array[i] = x if exists || -pos - 1 (pos = place of x in sorted mas)
}
