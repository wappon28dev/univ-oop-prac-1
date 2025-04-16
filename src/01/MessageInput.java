import java.util.Scanner;

public class MessageInput {
  public static void main(String[] args) {
    var scanner = new Scanner(System.in);

    System.out.println("こんにちは、メッセージをどうぞ");

    var inputLine = scanner.nextLine();

    System.out.println("メッセージを受信しました");
    System.out.println("----");
    System.out.println(inputLine);
    System.out.println("----");

    scanner.close(); // まあ閉じたほうが良いかな……
  }
}
