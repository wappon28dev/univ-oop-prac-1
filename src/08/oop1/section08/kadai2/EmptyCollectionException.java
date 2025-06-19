package oop1.section08.kadai2;

/**
 * @apiNote リストが空であり、処理を続行できない場合
 */
public class EmptyCollectionException extends InvalidCollectionDataException {
  public EmptyCollectionException(String message) {
    super(message);
  }
}
