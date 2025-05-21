package BinarySearch;

import java.lang.reflect.Array;
import java.util.Arrays;

public class BSNearLeft {
    // arr有序，找>=num 最左
    public static int mostLeftNoMoreNum(int[] arr, int num) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int L = 0;
        int R = arr.length - 1;
        int ans = -1;
        while(L <= R) {
            int mid = (L + R )/ 2;
            if (arr[mid] >= num) {
                ans = mid;
                R = mid - 1;
            } else {
                L = mid + 1;
            }
        }
        return ans;
    }

    public static int test(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++){
            if (arr[i] >= value) {
                return i;
            }
        }
        return -1;
    }
    public static int[] generateRandomArray(int maxSize, int maxValue) {
        int[] arr = new int[(int) ((maxSize + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++){
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) (maxValue * Math.random());
        }
        return arr;
    }

    public static void main(String[] args) {
        int testTime = 50000;
        int maxValue = 100;
        int maxSize = 10;

        boolean succeed = true;
        for (int i = 0; i <  testTime; i++) {
            int[] arr = generateRandomArray(maxSize, maxValue);
            Arrays.sort(arr);
            int value = (int) ((maxValue + 1) * Math.random()) - (int)(maxValue * Math.random());
            if (test(arr, value) != mostLeftNoMoreNum(arr, value)) {
                System.out.println(value);
                System.out.println(test(arr, value));
                System.out.println(mostLeftNoMoreNum(arr, value));
                succeed = false;
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Bad!");
    }
}
