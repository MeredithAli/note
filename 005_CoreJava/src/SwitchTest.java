import java.util.Scanner;

public class SwitchTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String test = scanner.next();
        int num = switch(test){
            case "Y" -> 1;
            case "N" -> 2;
            default -> 3;
        };
        System.out.println(num);
    }
}
