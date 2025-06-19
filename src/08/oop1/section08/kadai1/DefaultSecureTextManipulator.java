package oop1.section08.kadai1;

public class DefaultSecureTextManipulator implements SecureTextManipulator {
  @Override
  public String getFirstNCharsAsUpperCase(String text, int n) {
    if (text == null || text.isEmpty() || n <= 0) {
      return "";
    }

    var endIndex = Math.min(n, text.length());
    return text.substring(0, endIndex).toUpperCase();
  }
}
