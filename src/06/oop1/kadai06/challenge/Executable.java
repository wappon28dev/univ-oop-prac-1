package oop1.kadai06.challenge;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;

class TaskPool<T extends AbstractButton> {
  private final List<Executable<T>> tasks = new ArrayList<>();

  public TaskPool(List<Executable<T>> tasks) {
    this.tasks.addAll(tasks);
  }

  public List<Executable<T>> getSelectedTasks() {
    return tasks.stream()
        .filter(task -> task.getButton().isSelected())
        .toList();
  }
}

/**
 * 実行可能な処理の契約を定義するインターフェイス。
 * このインターフェイスを実装するクラスは、具体的な実行処理を持つことになります。
 */
public interface Executable<T extends AbstractButton> {
  T getButton();

  /**
   * 実行可能な処理を行い、結果メッセージを返却します。
   *
   * @return 処理結果を示す文字列
   */
  String execute();
}
