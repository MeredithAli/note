import java.util.HashMap;

public class HashMapTreeMap {
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("10001", "Alice");
        System.out.println(map.containsKey("10001"));
        System.out.println(map.containsKey("1"));
        System.out.println(map.get("10001"));

        map.put("10001", "Bob");
        System.out.println(map.get("10001"));

        map.remove("10001");
        System.out.println(map.get("10001"));
    }
}
