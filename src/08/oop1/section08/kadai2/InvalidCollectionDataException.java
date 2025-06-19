package oop1.section08.kadai2;

/**
 * @apiNote リストが `null` であるなど、データそのものが処理に適さない根本的な問題を示す
 */
public class InvalidCollectionDataException extends Exception {
  public InvalidCollectionDataException(String message) {
    super(message);
  }
}
