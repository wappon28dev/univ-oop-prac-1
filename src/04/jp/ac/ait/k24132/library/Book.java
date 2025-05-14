package jp.ac.ait.k24132.library;

import java.util.List;
import java.util.function.Function;

public class Book {
  private final String isbn;
  private String title;
  private String author;
  private boolean isBorrowed = false;

  public Book(String isbn, String title, String author) {
    if (isbn == null || isbn.isEmpty()) {
      this.isbn = "unknown";
      System.err.println("ISBN が `null` または空文字列です");
    } else {
      this.isbn = isbn;
    }

    if (title == null || title.isEmpty()) {
      this.title = "unknown";
      System.err.println("タイトルが `null` または空文字列です");
    } else {
      this.title = title;
    }

    if (author == null || author.isEmpty()) {
      this.author = "unknown";
      System.err.println("著者が `null` または空文字列です");
    } else {
      this.author = author;
    }

  }

  public String getIsbn() {
    return this.isbn;
  }

  public String getTitle() {
    return this.title;
  }

  public String getAuthor() {
    return this.author;
  }

  public boolean isBorrowed() {
    return this.isBorrowed;
  }

  public boolean borrowBook() {
    if (this.isBorrowed) {
      System.err.println("この本 (" + this.title + ") は既に貸出中です");
      return false;
    }

    this.isBorrowed = true;
    return true;
  }

  public boolean returnBook() {
    if (!this.isBorrowed) {
      System.err.println("この本 (" + this.title + ") は貸出中ではありません");
      return false;
    }

    this.isBorrowed = false;
    return true;
  }

  public String getBookDetails() {
    var lineSeparator = System.lineSeparator();
    return String.format(
        "ISBN: %s<ls>タイトル: %s<ls>著者: %s<ls>貸出中: %s",
        this.isbn,
        this.title,
        this.author,
        this.isBorrowed ? "はい" : "いいえ")
        .replaceAll("<ls>", lineSeparator);
  }

  public String toStringInline() {
    return String.format("%s: 『%s』", this.isbn, this.title);
  }

  private static String formatBookList(List<Book> books, int indent, Function<Book, String> formatter) {
    var sep = System.lineSeparator();
    var spaces = " ".repeat(indent);
    var indentSep = sep + spaces;
    var sb = new StringBuilder();
    int size = books.size();
    for (int i = 0; i < size; i++) {
      var detail = formatter.apply(books.get(i));
      sb.append(spaces)
          .append("#").append(i + 1).append("/").append(size)
          .append(indentSep).append(" | ")
          .append(detail.replace(sep, indentSep + " | "))
          .append(sep);
    }
    return sb.toString();
  }

  public static String listToString(List<Book> books, int indent) {
    return formatBookList(books, indent, Book::getBookDetails);
  }

  public static String listToStringInline(List<Book> books, int indent) {
    return formatBookList(books, indent, Book::toStringInline);
  }

}
