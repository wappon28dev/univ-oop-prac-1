package oop1.challenge07;

import java.util.Objects;

public final class Value<T> {
  private final T value;

  public Value(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public boolean isNull() {
    return value == null;
  }

  @Override
  public String toString() {
    return "Value[value: " + value + "]";
  }

  @Override
  public boolean equals(Object obj) {
    // ref: https://stackoverflow.com/a/60640880
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Value<?>)) {
      return false;
    }

    var other = (Value<?>) obj;
    return Objects.equals(this.value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
