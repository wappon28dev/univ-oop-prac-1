import java.util.Scanner;

public class Multiplication100 {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    System.out.println("整数を入力してください");

    String inputLine = in.nextLine();

    var num = Integer.parseInt(inputLine);
    var ans = num * 100;

    System.out.println("計算結果：" + ans);

    in.close(); // まあ閉じたほうが良いかな……
  }
}
