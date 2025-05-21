public class PreSum {
    private int[] preSum;

    public PreSum(int[] array) {
        int n = array.length;
        preSum = new int[n];
        for (int i = 1; i < n; i++) {
            preSum[i] = preSum[i - 1] + array[i];
        }
    }

    public int rangeSum(int l, int r){
        return l == 0 ? preSum[r] : preSum[r] - preSum[l -1];
    }
}
