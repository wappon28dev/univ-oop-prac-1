package oop1.section08.kadai1;

import java.util.List;

public class DefaultSafeCollectionProcessor implements SafeCollectionProcessor {
  @Override
  public int sumPositiveNumbers(List<Integer> numbers) {
    if (numbers == null) {
      return 0;
    }

    return numbers.stream()
        .filter(num -> num != null && num > 0)
        .mapToInt(Integer::intValue)
        .sum();
  }

  @Override
  public int countLongStrings(List<String> texts, int minLength) {
    if (texts == null || texts.isEmpty()) {
      return 0;
    }

    return (int) texts.stream()
        .filter(text -> text != null && text.length() >= minLength)
        .count();
  }
}
