public class OddTimes {
    public static void printOddTimesNum2(int[] arr) {
        int eor = 0;
        for (int i = 0; i < arr.length; i++){
            eor ^= arr[i];
        }
        //eor = a ^ b
        // eor != 0
        //eor必然有一个位置上是1
        int rightOne = eor & (~eor + 1);

        int onlyOne = 0;
        for (int cur: arr){
            if ((cur & rightOne) == 0) {
                onlyOne ^= cur;
            }
        }

        System.out.println(onlyOne + " " + (eor ^ onlyOne));
    }
}
