package jp.ac.ait.k24132.library;

public class Book {
  private final String isbn;
  private String title;
  private String author;
  private boolean isBorrowed = false;

  public Book(String isbn, String title, String author) {
    if (isbn == null || isbn.isEmpty()) {
      System.err.println("ISBN が `null` または空文字列です");
    }
    if (title == null || title.isEmpty()) {
      System.err.println("タイトルが `null` または空文字列です");
    }
    if (author == null || author.isEmpty()) {
      System.err.println("著者が `null` または空文字列です");
    }

    this.isbn = isbn;
    this.title = title;
    this.author = author;
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
}
