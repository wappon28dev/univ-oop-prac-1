package jp.ac.ait.k24132.library;

public class LibrarySystemMain {
  private Book book0 = new Book("9784101010014", "ノルウェイの森", "村上春樹");
  private Book book1 = new Book("9784344030036", "火花", "又吉直樹");
  private Book book2 = new Book("9784101201719", "海辺のカフカ", "村上春樹");
  private Book book3 = new Book("9784163907956", "コンビニ人間", "村田沙耶香");
  private Book book4 = new Book("9784087717660", "蜜蜂と遠雷", "恩田陸");
  private Book book5 = new Book("9784087443279", "夜は短し歩けよ乙女", "森見登美彦");
  private Book book6 = new Book("9784101200521", "1Q84 BOOK 1", "村上春樹");
  private Book book7 = new Book("9784062778290", "博士の愛した数式", "小川洋子");
  private Book book8 = new Book("9784106028212", "君たちはどう生きるか", "吉野源三郎");

  private LibraryMember member0 = new LibraryMember("M001", "太郎");
  private LibraryMember member1 = new LibraryMember("M002", "花子");
  private LibraryMember member2 = new LibraryMember("M003", "次郎");
  private LibraryMember memberWithLimit = new LibraryMember("M004", "三郎", 3);

  private void tryAssert(boolean condition, String message) {
    System.out.println("🧪 test: " + message);
    if (!condition) {
      System.err.println(" → ❌");
      throw new AssertionError(message);
    }
  }

  private void testAddAndRemoveBook() {
    var library = new Library();

    tryAssert(library.addBook(book0), "新規蔵書の追加に成功するはず");
    tryAssert(library.findBookByIsbn(book0.getIsbn()) != null, "追加した本が見つかるはず");

    tryAssert(!library.addBook(book0), "重複蔵書の追加は失敗するはず");

    tryAssert(library.removeBook(book0.getIsbn()), "既存蔵書の削除に成功するはず");
    tryAssert(library.findBookByIsbn(book0.getIsbn()) == null, "削除した本は見つからないはず");

    tryAssert(!library.removeBook("non-existent"), "存在しないISBNの削除は失敗するはず");
  }

  private void testRemoveBorrowedBookFailure() {
    var library = new Library();
    library.addBook(book1);
    library.registerMember(member1);
    library.lendBookToMember(member1.getMemberId(), book1.getIsbn());

    tryAssert(!library.removeBook(book1.getIsbn()), "貸出中の本の削除は失敗するはず");
    library.receiveBookFromMember(member1.getMemberId(), book1.getIsbn());
  }

  private void testRegisterAndUnregisterMember() {
    var library = new Library();

    tryAssert(library.registerMember(member0), "新規会員登録に成功するはず");
    tryAssert(library.findMemberById(member0.getMemberId()) != null, "登録した会員が見つかるはず");

    tryAssert(!library.registerMember(member0), "重複会員登録は失敗するはず");

    tryAssert(library.unregisterMember(member0.getMemberId()), "既存会員の退会に成功するはず");
    tryAssert(library.findMemberById(member0.getMemberId()) == null, "退会した会員は見つからないはず");

    tryAssert(!library.unregisterMember("M999"), "存在しない会員IDの退会は失敗するはず");
  }

  private void testUnregisterMemberWhileBorrowedFailure() {
    var library = new Library();
    library.addBook(book2);
    library.registerMember(memberWithLimit);
    library.lendBookToMember(memberWithLimit.getMemberId(), book2.getIsbn());

    tryAssert(!library.unregisterMember(memberWithLimit.getMemberId()), "貸出中の会員の退会は失敗するはず");
    library.receiveBookFromMember(memberWithLimit.getMemberId(), book2.getIsbn());
  }

  private void testLendAndReturn() {
    var library = new Library();
    library.addBook(book1);
    library.registerMember(member1);

    tryAssert(library.lendBookToMember(member1.getMemberId(), book1.getIsbn()),
        "貸出に成功するはず");
    tryAssert(book1.isBorrowed(), "本が貸出中フラグになるはず");

    tryAssert(library.receiveBookFromMember(member1.getMemberId(), book1.getIsbn()),
        "返却に成功するはず");
    tryAssert(!book1.isBorrowed(), "本の貸出フラグが戻るはず");

    tryAssert(!library.receiveBookFromMember(member1.getMemberId(), book1.getIsbn()),
        "借りていない本の返却は失敗するはず");
  }

  private void testBorrowLimit() {
    var library = new Library();
    library.addBook(book2);
    library.addBook(book3);
    library.addBook(book4);
    library.addBook(book5);
    library.registerMember(memberWithLimit);

    tryAssert(library.lendBookToMember(memberWithLimit.getMemberId(), book2.getIsbn()), "1冊目OK");
    tryAssert(library.lendBookToMember(memberWithLimit.getMemberId(), book3.getIsbn()), "2冊目OK");
    tryAssert(library.lendBookToMember(memberWithLimit.getMemberId(), book4.getIsbn()), "3冊目OK");

    tryAssert(!library.lendBookToMember(memberWithLimit.getMemberId(), book5.getIsbn()),
        "貸出上限超過で失敗するはず");
  }

  private void testSearchBook() {
    var library = new Library();
    library.addBook(book6);
    library.addBook(book7);

    var result1 = library.searchBook("村上");
    tryAssert(result1.length == 1 && result1[0].equals(book6),
        "キーワード検索でタイトルまたは著者にマッチするはず");

    var result2 = library.searchBook("数式");
    tryAssert(result2.length == 1 && result2[0].equals(book7),
        "キーワード検索で別の本にもマッチするはず");
  }

  private void testDisplayMethods() {
    var library = new Library();
    library.addBook(book4);
    library.addBook(book8);
    library.registerMember(member2);
    library.lendBookToMember(member2.getMemberId(), book8.getIsbn());

    library.displayAllBooks();
    library.displayAvailableBooks();
    library.displayAllMembersWithBorrowedBooks();
  }

  private void runAllTests() {
    testAddAndRemoveBook();
    testRemoveBorrowedBookFailure();
    testRegisterAndUnregisterMember();
    testUnregisterMemberWhileBorrowedFailure();
    testLendAndReturn();
    testBorrowLimit();
    testSearchBook();
    testDisplayMethods();
    System.out.println("🎉 どね！");
  }

  public static void main(String[] args) {
    new LibrarySystemMain().runAllTests();
  }
}
