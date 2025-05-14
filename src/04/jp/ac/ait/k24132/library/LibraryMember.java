package jp.ac.ait.k24132.library;

import java.util.ArrayList;
import java.util.List;

public class LibraryMember {
  private final String memberId;
  private String name;
  private Book[] borrowedBooks = new Book[0];
  private int maxBorrowLimit = 5;

  public LibraryMember(String memberId, String name) {
    this.memberId = memberId;
    this.name = name;
  }

  public LibraryMember(String memberId, String name, int maxBorrowLimit) {
    this(memberId, name);

    if (maxBorrowLimit <= 0) {
      System.err.println("`maxBorrowLimit` が `0` 以下です; `1` を使用します");
      this.maxBorrowLimit = 1;
    } else {
      this.maxBorrowLimit = maxBorrowLimit;
    }
  }

  public String getMemberId() {
    return this.memberId;
  }

  public String getName() {
    return this.name;
  }

  public int getMaxBorrowLimit() {
    return this.maxBorrowLimit;
  }

  public int getCurrentBorrowCount() {
    return this.borrowedBooks.length;
  }

  public boolean canBorrowMore() {
    return this.getCurrentBorrowCount() < this.maxBorrowLimit;
  }

  public List<Book> getBorrowedBooksAsList() {
    return List.of(this.borrowedBooks);
  }

  public boolean borrowBook(Book book) {
    var canBorrow = this.canBorrowMore() && !book.isBorrowed();

    if (!canBorrow) {
      var lineSeparator = System.lineSeparator();
      var msg = new StringBuilder();

      msg.append("貸出できません (" + book.toStringInline() + ")" + lineSeparator);

      if (!this.canBorrowMore()) {
        msg.append(" - 貸出上限に達しています" + lineSeparator);
      }

      if (book.isBorrowed()) {
        msg.append(" - この本は既に貸出中です" + lineSeparator);
      }

      System.err.print(msg);
      return false;
    }

    var newBorrowedBooks = new ArrayList<>(this.getBorrowedBooksAsList());
    newBorrowedBooks.add(book);
    this.borrowedBooks = newBorrowedBooks.toArray(Book[]::new);

    book.borrowBook();

    return true;
  }

  public int borrowBooks(Book[] booksToBorrow) {
    int borrowedCount = 0;

    for (Book book : booksToBorrow) {
      var isSuccess = this.borrowBook(book);
      if (isSuccess) {
        borrowedCount++;
      }
    }

    return borrowedCount;
  }

  public boolean returnBook(Book book) {
    var containsBookList = this.getBorrowedBooksAsList().contains(book);
    if (!containsBookList) {
      return false;
    }

    var newBorrowedBooks = new ArrayList<>(this.getBorrowedBooksAsList());
    newBorrowedBooks.remove(book);
    this.borrowedBooks = newBorrowedBooks.toArray(Book[]::new);

    book.returnBook();
    return true;
  }

  // 会員ID、名前、最大貸出冊数、現在の貸出冊数、貸出中の本の詳細（タイトルとISBN）リスト
  public void displayMemberInfo() {
    var msg = String.format(
        "会員ID: %s<ls>名前: %s<ls>最大貸出冊数: %d<ls>現在の貸出冊数: %d",
        this.memberId,
        this.name,
        this.maxBorrowLimit,
        this.getCurrentBorrowCount())
        .replaceAll("<ls>", System.lineSeparator());
    System.out.println(msg);
    System.out.println("貸出中の本:");
    System.out.println(Book.listToStringInline(this.getBorrowedBooksAsList(), 2));
  }

  public String toStringInline() {
    return String.format("%s: %s さん", this.name, this.memberId);
  }

  // public static void main(String[] args) {
  // // test displayMemberInfo

  // var member = new LibraryMember("12345", "山田太郎", 3);
  // var book1 = new Book("9784106028212", "君たちはどう生きるか", "吉野源三郎");
  // var book2 = new Book("9784106028229", "ノルウェイの森", "村上春樹");
  // var book3 = new Book("9784106028236", "コンビニ人間", "村田沙耶香");
  // member.borrowBook(book1);
  // member.borrowBook(book2);
  // member.borrowBook(book3);
  // member.displayMemberInfo();
  // }
}
