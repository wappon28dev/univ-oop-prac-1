package oop1.kadai06.challenge;

import javax.swing.JRadioButton;

/**
 * {@link Executable} インターフェイスを実装するクラス。
 * タスクBの具体的な処理を定義します。
 */
public class TaskB implements Executable<JRadioButton> {
  JRadioButton radioButton = new JRadioButton("タスクBを実行");

  @Override
  public JRadioButton getButton() {
    return radioButton;
  }

  /**
   * タスクBの処理を実行し、その結果を説明する文字列を返します。
   *
   * @return タスクBの実行結果メッセージ
   */
  @Override
  public String execute() {
    // コンソールにも実行中であることを示すメッセージを出力（デバッグや確認用）
    System.out.println("コンソール: TaskBを実行中...");
    return "タスクBが完了しました。";
  }
}
