package BinarySearch;

import java.util.Arrays;

public class BAwesome {
    //局部最小
    //arr 整体无序，但是相邻不相等
    public static int oneMinIndex(int[] arr){
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int n = arr.length;
        if (n == 1) {
            return 0;
        }

        if(arr[0] < arr[1]) {
            return 0;
        }
        if (arr[n -1] < arr[n - 2]) {
            return n-1;
        }

        int l = 0;
        int r = n - 1;

        while(l < r - 1) {
            int mid = (l + r)/ 2;
            if (arr[mid] < arr[mid - 1] && arr[mid] < arr[mid + 1]){
                return mid;
            } else {
                if (arr[mid] > arr[mid -1]) {
                    r = mid - 1;
                } else {
                    l = mid + 1;
                }
            }

        }
        return arr[l] < arr[r] ? l : r;
    }

    public static int[] generateRandomArray(int maxSize, int maxValue) {
        int len = (int) (Math.random() * maxSize);
        int[] arr = new int[len];
        if (len > 0) {
            arr[0] = (int)(Math.random() * maxValue);
            for (int i = 1; i< len; i++){
                do {
                    arr[i] = (int) (Math.random() * maxValue);
                } while (arr[i] == arr[i -1]);
            }
        }
        return arr;
    }

    public static boolean check(int[] arr, int minIndex){
        if (arr.length == 0){
            return minIndex==-1;
        }
        int left = minIndex - 1;
        int right = minIndex + 1;
        boolean leftBigger = left >= 0 ? arr[left] > arr[minIndex] : true;
        boolean rightBigger = right < arr.length ? arr[right] > arr[minIndex] : true;
        return leftBigger && rightBigger;
    }

    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int testTime = 1000;
        int maxValue = 20;
        int maxSize = 5;

        boolean succeed = true;
        for (int i = 0; i <  testTime; i++) {
            int[] arr = generateRandomArray(maxSize, maxValue);
            int ans = oneMinIndex(arr);

            if (!check(arr, ans)) {
                printArray(arr);
                System.out.println(ans);
                succeed = false;
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Bad!");
    }

}
