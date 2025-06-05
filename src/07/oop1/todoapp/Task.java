package oop1.todoapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Taskable {

  private String description;
  private LocalDate dueDate;
  private boolean completed;

  /**
   * 新しいTaskオブジェクトを生成します。
   *
   * @param description タスクの説明。nullや空文字列であってはなりません。
   * @param dueDate     タスクの期限日。null許容。
   */
  public Task(String description, LocalDate dueDate) {
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be null or empty.");
    }
    this.description = description;
    this.dueDate = dueDate;
    this.completed = false; // 初期状態は未完了
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be null or empty.");
    }
    this.description = description;
  }

  @Override
  public LocalDate getDueDate() {
    return dueDate;
  }

  @Override
  public void setDueDate(LocalDate dueDate) {
    this.dueDate = dueDate;
  }

  @Override
  public boolean isCompleted() {
    return completed;
  }

  @Override
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /**
   * タスクの文字列表現を返します。
   * 形式: [状態] 説明 (期限日: YYYY-MM-DD または 期限日未設定)
   * 例: [未完了] レポート作成 (期限日: 2023-10-26)
   * 例: [完了] ゴミ出し (期限日未設定)
   *
   * @return タスクの文字列表現。
   */
  @Override
  public String toString() {
    String status = completed ? "[完了]" : "[未完了]";
    String dueDateStr = (dueDate != null) ? dueDate.format(DATE_FORMATTER) : "期限日未設定";
    return String.format("%s %s (期限: %s)", status, description, dueDateStr);
  }
}
