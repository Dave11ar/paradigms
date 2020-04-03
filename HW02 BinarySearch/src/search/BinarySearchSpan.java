package search;

import java.util.ArrayList;
import java.util.List;

public class BinarySearchSpan {

    //pre for all i < j: array[i] >= array[j]
    public static int firstLesserOrEqual(List<Integer> array, int x) {
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
        return r;
    }
    //post: return min(i) : array[i] <= x



    //pre: for all i < j: array[i] >= array[j]
    public static int firstLesser(List<Integer> array, int x) {
        int l = -1;
        int r = array.size();

        // l' = l, r' = r

        // pre: for all i < j: array[i] >= array[j] &&
        // array[l] > x && array[r] <= x && l <= r - 1

        // inv l' < r' - 1 && array[l'] >= x && array[r'] < x
        while (l < r - 1) {
            int mid = (l + r) / 2;
            // l' < mid < r'

            if (array.get(mid) < x) {
                // inv && array[mid] < x
                r = mid;
                // r'' < r' && array[r''] < x
            } else {
                // inv && array[mid] >= x
                l = mid;
                // l'' > l' && array[l''] >= x
            }
            // r'' - l'' == 1/2(r' - l')
        }
        //post: array[l'] >= x && array[r'] < x && l' == r' - 1
        //--> r' = min(i): array[r'] < x
        return r;
    }
    //post: return min(i) : array[i] < x


    // pre: input 1 or more ints, for input[1...last]: input[i] >= input[j] for all i < j
    public static void main(String[] args) {
        int x;
        List<Integer> array = new ArrayList<>();
        x = Integer.parseInt(args[0]);

        for (int i = 1; i < args.length; i++) {
            array.add(Integer.parseInt(args[i]));
        }
        // set array[-1] = inf, array[array,size()] = -inf


        int t1 = firstLesserOrEqual(array, x);
        // t1 = min(i): array[i] >= x
        int t2 = firstLesser(array, x);
        // t2 = min(i): array[i] > x


        System.out.println(t1 + " " + (t2 - t1));
    }
    // post: Sout min(i) : input[i + 1] <= x + " " + number of elements == x)
}
