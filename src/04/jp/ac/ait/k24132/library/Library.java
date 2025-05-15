package jp.ac.ait.k24132.library;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Library {
  private Book[] books;
  private LibraryMember[] members;

  public Library() {
    this.books = new Book[0];
    this.members = new LibraryMember[0];
  }

  public List<Book> getBooksAsList() {
    return List.of(this.books);
  }

  public List<LibraryMember> getMembersAsList() {
    return List.of(this.members);
  }

  public boolean hasRegistered(Book book) {
    var bookList = this.getBooksAsList();
    return bookList.stream().anyMatch(b -> b.getIsbn().equals(book.getIsbn()));
  }

  public boolean hasRegistered(LibraryMember member) {
    var memberList = this.getMembersAsList();
    return memberList.stream().anyMatch(m -> m.getMemberId().equals(member.getMemberId()));
  }

  private class Report {
    public static void bookNotFound(String isbn) {
      System.err.println("この本 (ISBN: " + isbn + ") は登録されていません");
    }

    public static void bookAlreadyExists(Book book) {
      System.err.println("この本 (" + book.toStringInline() + ") は既に登録されています");
    }

    public static void memberNotFound(String memberId) {
      System.err.println("この会員 (memberId: " + memberId + ") は登録されていません");
    }

    public static void memberAlreadyExists(LibraryMember member) {
      System.err.println("この会員 (" + member.toStringInline() + ") は既に登録されています");
    }

  }

  /// 蔵書管理メソッド ///

  public boolean addBook(Book book) {
    var bookList = this.getBooksAsList();

    if (hasRegistered(book)) {
      Report.bookAlreadyExists(book);
      return false;
    }

    var newBooks = new ArrayList<>(bookList);
    newBooks.add(book);
    this.books = newBooks.toArray(Book[]::new);

    return true;
  }

  private Optional<Book> findBookByIsbnOptional(String isbn) {
    var bookList = this.getBooksAsList();
    return bookList.stream()
        .filter(book -> book.getIsbn().equals(isbn))
        .findFirst();
  }

  public boolean removeBook(String isbn) {
    var bookList = this.getBooksAsList();

    var bookToRemoveOptional = this.findBookByIsbnOptional(isbn);
    if (bookToRemoveOptional.isEmpty()) {
      Report.bookNotFound(isbn);
      return false;
    }
    var bookToRemove = bookToRemoveOptional.get();
    if (bookToRemove.isBorrowed()) {
      System.err.println("この本 (" + bookToRemove.toStringInline() + ") は貸出中です");
      return false;
    }

    var newBooks = new ArrayList<>(bookList);
    newBooks.remove(bookToRemove);
    this.books = newBooks.toArray(Book[]::new);

    return true;
  }

  public Book findBookByIsbn(String isbn) {
    return this.findBookByIsbnOptional(isbn).orElse(null);
  }

  /// 会員管理メソッド ///

  private Optional<LibraryMember> findMemberByIdOptional(String memberId) {
    var memberList = this.getMembersAsList();
    return memberList.stream()
        .filter(member -> member.getMemberId().equals(memberId))
        .findFirst();
  }

  public boolean registerMember(LibraryMember member) {
    var memberList = this.getMembersAsList();

    if (hasRegistered(member)) {
      Report.memberAlreadyExists(member);
      return false;
    }

    var newMembers = new ArrayList<>(memberList);
    newMembers.add(member);
    this.members = newMembers.toArray(LibraryMember[]::new);

    return true;
  }

  public boolean unregisterMember(String memberId) {
    var memberList = this.getMembersAsList();

    var memberToRemoveOptional = this.findMemberByIdOptional(memberId);
    if (memberToRemoveOptional.isEmpty()) {
      Report.memberNotFound(memberId);
      return false;
    }
    var memberToRemove = memberToRemoveOptional.get();
    if (memberToRemove.getCurrentBorrowCount() > 0) {
      System.err.println("この会員 (" + memberToRemove.toStringInline() + ") は貸出中の本があります");
      return false;
    }

    var newMembers = new ArrayList<>(memberList);
    newMembers.remove(memberToRemove);
    this.members = newMembers.toArray(LibraryMember[]::new);

    return true;
  }

  public LibraryMember findMemberById(String memberId) {
    return this.findMemberByIdOptional(memberId).orElse(null);
  }

  /// 貸出・返却業務メソッド ///

  public boolean lendBookToMember(String memberId, String isbn) {
    var memberOptional = this.findMemberByIdOptional(memberId);
    if (memberOptional.isEmpty()) {
      Report.memberNotFound(memberId);
      return false;
    }
    var member = memberOptional.get();

    var bookOptional = this.findBookByIsbnOptional(isbn);
    if (bookOptional.isEmpty()) {
      Report.bookNotFound(isbn);
      return false;
    }
    var book = bookOptional.get();

    return member.borrowBook(book);
  }

  public boolean receiveBookFromMember(String memberId, String isbn) {
    var memberOptional = this.findMemberByIdOptional(memberId);
    if (memberOptional.isEmpty()) {
      Report.memberNotFound(memberId);
      return false;
    }
    var member = memberOptional.get();

    var bookOptional = this.findBookByIsbnOptional(isbn);
    if (bookOptional.isEmpty()) {
      Report.bookNotFound(isbn);
      return false;
    }
    var book = bookOptional.get();

    return member.returnBook(book);
  }

  /// 書籍検索メソッド ///

  public Book[] searchBook(String keyword) {
    var bookList = this.getBooksAsList();

    // 部分一致、大文字・小文字区別なし
    Predicate<Book> search = book -> {
      var title = book.getTitle().toLowerCase();
      var author = book.getAuthor().toLowerCase();
      var keywordLower = keyword.toLowerCase();

      return title.contains(keywordLower) || author.contains(keywordLower);
    };

    return bookList.stream().filter(search).toArray(Book[]::new);
  }

  public void displayAllBooks() {
    var bookList = this.getBooksAsList();

    System.out.println("/// 蔵書一覧 ///");
    System.out.println(Book.listToString(bookList, 0));
  }

  public void displayAvailableBooks() {
    var bookList = this.getBooksAsList();
    var availableBooks = bookList.stream().filter(book -> !book.isBorrowed()).toList();

    System.out.println("/// 貸出可能な本 ///");
    System.out.println(Book.listToString(availableBooks, 0));
  }

  public void displayAllMembersWithBorrowedBooks() {
    var memberList = this.getMembersAsList();

    System.out.println("/// 各会員の貸出状況 ///");
    int count = 1;
    for (var member : memberList) {
      var borrowedBooks = member.getBorrowedBooksAsList();
      System.out.println("#" + count + "/" + memberList.size() + "  " + member.toStringInline());
      System.out.println(Book.listToString(borrowedBooks, 2));
      count++;
    }
  }

  // public static void main(String[] args) {
  // var library = new Library();
  // var books = List.of(
  // new Book("978-4-7981-6340-5", "Javaポケットリファレンス", "柴田望洋"),
  // new Book("978-4-7981-6341-2", "JavaScriptポケットリファレンス", "山田祥寛"),
  // new Book("978-4-7981-6342-9", "Pythonポケットリファレンス", "黒川利明"));

  // var members = List.of(
  // new LibraryMember("k24132", "田中太郎", 3),
  // new LibraryMember("k24133", "佐藤花子", 5),
  // new LibraryMember("k24134", "鈴木次郎", 2));

  // members.get(0).borrowBook(books.get(0));
  // members.get(1).borrowBook(books.get(1));
  // members.get(2).borrowBook(books.get(2));

  // members.forEach(library::registerMember);
  // books.forEach(library::addBook);
  // library.displayAllMembersWithBorrowedBooks();
  // }
}
