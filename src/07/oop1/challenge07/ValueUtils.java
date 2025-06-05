package oop1.challenge07;

import java.util.function.Function;

public class ValueUtils {
  public static <E> Value<E> of(E value) {
    return new Value<>(value);
  }

  public static <T, R> Value<R> map(Value<T> originalValue, Function<T, R> mapper) {
    if (originalValue.isNull()) {
      return ValueUtils.of(null);
    }

    var mappedValue = mapper.apply(originalValue.get());
    return ValueUtils.of(mappedValue);
  }
}

class ValueExample {
  static Integer map2x(Integer value) {
    return value * 2;
  }

  static String map2x(String value) {
    return value.repeat(2);
  }

  public static void main(String[] args) {
    var valueStr = ValueUtils.of("Hello, World!");
    System.out.println(valueStr);
    var valueStr2x = ValueUtils.map(valueStr, ValueExample::map2x);
    System.out.println(valueStr2x);
    System.out.println(

    );

    var valueInt = ValueUtils.of(42);
    System.out.println(valueInt);
    var valueInt2x = ValueUtils.map(valueInt, ValueExample::map2x);
    System.out.println(valueInt2x);

    var valueNull = ValueUtils.of(null);
    System.out.println(valueNull);
    var valueNullMapped = ValueUtils.map(valueNull, __ -> "このメッセージが 見れるのは おかしいよ");
    System.out.println(valueNullMapped);
  }
}
