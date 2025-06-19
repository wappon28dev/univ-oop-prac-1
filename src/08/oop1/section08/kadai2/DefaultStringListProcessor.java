package oop1.section08.kadai2;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultStringListProcessor implements StringListProcessor {

  @Override
  public String concatenateAndUppercase(List<String> texts)
      throws InvalidCollectionDataException, EmptyCollectionException, NullItemInCollectionException {
    if (texts == null) {
      throw new InvalidCollectionDataException("リストが `null` です。");
    }
    if (texts.isEmpty()) {
      throw new EmptyCollectionException("リストが空です。");
    }

    var containsNull = texts.stream().anyMatch(text -> text == null);
    if (containsNull) {
      var idx = texts.indexOf(null);
      throw new NullItemInCollectionException("リストに `null` 要素が含まれています。", idx);
    }

    return texts.stream().map(String::toUpperCase).collect(Collectors.joining());
  }
}
