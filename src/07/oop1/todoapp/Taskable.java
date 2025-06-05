package oop1.todoapp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * タスクアイテムの契約を定義します。
 * タスクの説明、期限日、完了状態を取得および設定するメソッドを含みます。
 */
public interface Taskable {

  /**
   * LocalDateオブジェクトの解析およびフォーマット用フォーマッタ (YYYY-MM-DD)。
   * 日付のフォーマット処理に使用してください。
   */
  DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  /**
   * タスクの説明を取得します。
   * 
   * @return タスクの説明文字列。
   */
  String getDescription();

  /**
   * タスクの説明を設定します。
   * 
   * @param description 新しいタスクの説明。nullや空文字列であってはなりません。
   */
  void setDescription(String description);

  /**
   * タスクの期限日を取得します。
   * 
   * @return 期限日。設定されていない場合はnull。
   */
  LocalDate getDueDate();

  /**
   * タスクの期限日を設定します。
   * 
   * @param dueDate 新しい期限日（null許容）。
   */
  void setDueDate(LocalDate dueDate);

  /**
   * タスクが完了したかどうかを確認します。
   * 
   * @return タスクが完了していればtrue、そうでなければfalse。
   */
  boolean isCompleted();

  /**
   * タスクの完了状態を設定します。
   * 
   * @param completed 新しい完了状態。
   */
  void setCompleted(boolean completed);

  /**
   * タスクの文字列表現を返します。通常、表示目的で使用されます。
   * この文字列表現には、完了状態、期限日、説明などの情報が含まれるべきです。
   * 
   * @return タスクの文字列表現。
   */
  @Override
  String toString();

  /**
   * "YYYY-MM-DD" 形式の日付文字列をLocalDateオブジェクトに解析します。
   * 入力文字列がnullまたは空または日付形式として認識できない場合はnullを返します。
   *
   * @param dateString 解析する日付文字列。
   * @return 解析されたLocalDate。入力がnull/空の場合/日付形式として認識できない場合null。
   */
  static LocalDate parseDueDate(String dateString) {
    if (dateString == null || dateString.trim().isEmpty()) {
      return null;
    }
    try {
      return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
    } catch (DateTimeParseException dtpe) {
      // 本来は例外処理として再入力させるなどの制御が必要だが、
      // まだ例外処理を講義で取り扱っていないので、日付形式の間違いは未設定扱いとする。
      return null;
    }
  }
}
