package BinarySearch;

public class BSExist {
    public static boolean find(int[] arr, int num) {
        if (arr == null || arr.length == 0) {
            return false;
        }
        int l = 0;
        int r = arr.length - 1;
        while (l < r) {
            int mid = (l + r) / 2;
            if (arr[mid] == num) {
                return true;
            } else if (arr[mid] < num) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return false;
    }


    public static int findMostLeft(int[] arr, int num) {
        if (arr == null || arr.length == 0){
            return -1;
        }
        int l = 0;
        int r = arr.length - 1;
        int t = -1;
        while (l <= r){
            int mid = (l + r) / 2;
            if (arr[mid] >= num) {
                t = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return t;
    }
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
        int ans = -1;

        while(l <= r) {
            int mid = (l + r)/ 2;
            if (arr[mid] < arr[mid - 1] && arr[mid] > arr[mid + 1]){
                ans = mid;
                break;
            }
            if (arr[mid] > arr[mid -1]) {
                r = mid - 1;
                continue;
            }
            if (arr[mid] > arr[mid + 1]) {
                l = mid + 1;
            }
        }
        return ans;
    }
}
