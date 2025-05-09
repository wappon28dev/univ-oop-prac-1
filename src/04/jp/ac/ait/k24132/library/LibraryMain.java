package jp.ac.ait.k24132.library;

public class LibraryMain {
  private Book book0 = new Book("9784101010014", "ノルウェイの森", "村上春樹");
  private Book book1 = new Book("9784344030036", "火花", "又吉直樹");
  private Book book2 = new Book("9784101201719", "海辺のカフカ", "村上春樹");
  private Book book3 = new Book("9784163907956", "コンビニ人間", "村田沙耶香");
  private Book book4 = new Book("9784087717660", "蜜蜂と遠雷", "恩田陸");
  private Book book5 = new Book("9784087443279", "夜は短し歩けよ乙女", "森見登美彦");
  private Book book6 = new Book("9784101200521", "1Q84 BOOK 1", "村上春樹");
  private Book book7 = new Book("9784062778290", "博士の愛した数式", "小川洋子");
  private Book book8 = new Book("9784106028212", "君たちはどう生きるか", "吉野源三郎");
  private Book book9 = new Book("9784004301302", "こころ", "夏目漱石");

  private void tryAssert(boolean condition, String message) {
    System.out.println("🧪 test: " + message);
    if (!condition) {
      System.err.println(" → ❌");
      throw new AssertionError(message);
    }
  }

  private void testSingleBorrow() {
    var member = new LibraryMember("M001", "太郎");

    tryAssert(member.getCurrentBorrowCount() == 0, "初期貸出冊数は0");

    var ok = member.borrowBook(book0);
    tryAssert(ok, "1冊目の貸出に成功するはず");
    tryAssert(book0.isBorrowed(), "本が貸出中フラグになる");
    tryAssert(member.getCurrentBorrowCount() == 1, "貸出冊数が1になる");
  }

  private void testMultipleBorrow() {
    var member = new LibraryMember("M002", "花子");

    var ok1 = member.borrowBook(book1);
    tryAssert(ok1, "1冊目OK");
    var ok2 = member.borrowBook(book2);
    tryAssert(ok2, "2冊目OK");
    tryAssert(member.getCurrentBorrowCount() == 2, "貸出冊数が2");
  }

  private void testBorrowLimit() {
    var member = new LibraryMember("M003", "次郎", 3);

    tryAssert(member.borrowBook(book3), "1冊目OK");
    tryAssert(member.borrowBook(book4), "2冊目OK");
    tryAssert(member.borrowBook(book5), "3冊目OK");

    boolean ok = member.borrowBook(book6);
    tryAssert(!ok, "貸出上限超過でfalse");
    tryAssert(member.getCurrentBorrowCount() == 3, "貸出冊数はまだ3");
  }

  private void testReturnBook() {
    var member = new LibraryMember("M004", "三郎");

    member.borrowBook(book7);
    tryAssert(member.getCurrentBorrowCount() == 1, "借りた状態");

    var retOk = member.returnBook(book7);

    tryAssert(retOk, "返却成功");
    tryAssert(!book7.isBorrowed(), "本の貸出フラグが戻る");
    tryAssert(member.getCurrentBorrowCount() == 0, "貸出冊数が0に戻る");
    tryAssert(!member.returnBook(book8), "借りてない本は返却できない");
  }

  private void testBorrowBooksArray() {
    var member = new LibraryMember("M005", "四郎", 10);
    var arr = new Book[] { book8, book9 };
    var borrowCount = member.borrowBooks(arr);

    tryAssert(borrowCount == 3, "配列で2冊借りられる");
    tryAssert(member.getCurrentBorrowCount() == 2, "貸出冊数が2");
  }

  private void runAllTests() {
    testSingleBorrow();
    testMultipleBorrow();
    testBorrowLimit();
    testReturnBook();
    testBorrowBooksArray();
    System.out.println("done!");
  }

  public static void main(String[] args) {
    new LibraryMain().runAllTests();
  }
}
