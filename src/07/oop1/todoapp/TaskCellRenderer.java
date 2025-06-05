package oop1.todoapp;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

public class TaskCellRenderer extends DefaultListCellRenderer {

  @Override
  public Component getListCellRendererComponent(
      JList<?> list, // 描画対象のJListインスタンス
      Object value, // 描画対象のリスト要素 (本課題ではTaskオブジェクト)
      int index, // リスト内での要素のインデックス
      boolean isSelected, // 当該要素が選択状態か否か
      boolean cellHasFocus) { // 当該要素がフォーカスを保持しているか否か

    // 1. 親クラスの実装を呼び出し、基本的なJLabelコンポーネントを取得
    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    // 2. valueをTask型にキャストして利用
    if (value instanceof Task) {
      Task task = (Task) value;

      // 3. TaskオブジェクトのtoString()メソッド等を利用して表示テキストを設定
      label.setText(task.toString());

      // 4. Taskオブジェクトの状態に基づき、ラベルの視覚的プロパティを変更
      if (task.isCompleted()) {
        label.setForeground(Color.GRAY); // 前景色をグレーに設定

        // フォント属性を操作して取り消し線を追加
        Map<TextAttribute, Object> attributes = new java.util.HashMap<>(label.getFont().getAttributes());
        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        label.setFont(label.getFont().deriveFont(attributes));
      } else {
        // デフォルトのスタイルに戻す
        label.setForeground(list.getForeground()); // JListの標準の前景色

        Map<TextAttribute, Object> attributes = new java.util.HashMap<>(label.getFont().getAttributes());
        attributes.put(TextAttribute.STRIKETHROUGH, false); // 取り消し線を解除
        label.setFont(label.getFont().deriveFont(attributes));
      }
    }
    // 5. 設定変更後のJLabelコンポーネントを返す
    return label;
  }
}
