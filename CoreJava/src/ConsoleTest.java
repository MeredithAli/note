import java.io.Console;

public class ConsoleTest {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        Console cons = System.console();
        String username = cons.readLine("User name: ");
        char[] passwd = cons.readPassword("Password: ");
    }
}