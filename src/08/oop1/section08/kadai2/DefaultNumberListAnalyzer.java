package oop1.section08.kadai2;

import java.util.List;

public class DefaultNumberListAnalyzer implements NumberListAnalyzer {

  @Override
  public int findMaximumValue(List<Integer> numbers)
      throws InvalidCollectionDataException, EmptyCollectionException, NullItemInCollectionException {
    if (numbers == null) {
      throw new InvalidCollectionDataException("リストが `null` です。");
    }
    if (numbers.isEmpty()) {
      throw new EmptyCollectionException("リストが空です。");
    }

    var containsNull = numbers.stream().anyMatch(num -> num == null);
    if (containsNull) {
      var idx = numbers.indexOf(null);
      throw new NullItemInCollectionException("リストに `null` 要素が含まれています。", idx);
    }

    return numbers.stream().max(Integer::compareTo).get();
  }
}
