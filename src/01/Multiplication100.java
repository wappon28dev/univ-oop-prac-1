import java.util.Scanner;

public class Multiplication100 {
  public static void main(String[] args) {
    var scanner = new Scanner(System.in);

    System.out.println("整数を入力してください");
    var inputLine = scanner.nextLine();

    var num = Integer.parseInt(inputLine);
    var ans = num * 100;

    System.out.println("計算結果：" + ans);

    scanner.close(); // まあ閉じたほうが良いかな……
  }
}
