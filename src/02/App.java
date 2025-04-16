
import java.util.*;

public class App {
  public static void main(String[] args) {

    var list = new ArrayList<Integer>();
    list.add(1);
    list.add(2);
    list.add(3);
    list.add(null);

    list.forEach(System.out::println);
  }
}
