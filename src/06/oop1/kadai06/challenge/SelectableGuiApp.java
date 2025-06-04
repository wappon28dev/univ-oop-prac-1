package oop1.kadai06.challenge;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * {@link Executable} インターフェイスを実装したタスクを選択し、実行するためのシンプルなGUIアプリケーション。
 * ラジオボタンで処理を選択し、実行ボタンで結果を表示します。
 */
public class SelectableGuiApp extends JFrame {
  private JLabel resultLabel; // 実行結果を表示するラベル
  private JRadioButton radioTaskA; // TaskA選択用ラジオボタン
  private JRadioButton radioTaskB; // TaskB選択用ラジオボタン
  private ButtonGroup taskButtonGroup; // ラジオボタンをグループ化

  /**
   * {@code SelectableGuiApp} のコンストラクタ。
   * GUIの初期化とコンポーネントの配置を行います。
   */
  public SelectableGuiApp() {
    // 1. フレームの基本設定
    setTitle("処理選択デモ"); // ウィンドウのタイトル
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 閉じるボタンでプログラム終了
    setSize(550, 200); // ウィンドウサイズ
    setLocationRelativeTo(null); // ウィンドウを画面中央に表示

    // 2. メインパネルの作成とレイアウト設定
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // コンポーネントを中央揃えで配置

    // 3. ラジオボタンの作成と設定
    var taskA = new TaskA();
    var taskB = new TaskB();

    var tasks = List.of(taskA, taskB);
    var taskPool = new TaskPool<JRadioButton>(tasks);

    radioTaskA = taskA.getButton();
    radioTaskA.setSelected(true); // 初期状態でTaskAを選択

    radioTaskB = taskB.getButton();

    // 4. ラジオボタンをグループ化 (いずれか一つだけ選択可能にする)
    taskButtonGroup = new ButtonGroup();
    taskButtonGroup.add(radioTaskA);
    taskButtonGroup.add(radioTaskB);

    // 5. 実行ボタンの作成
    JButton executeButton = new JButton("実行");

    // 6. 結果表示ラベルの作成
    resultLabel = new JLabel("ここに結果が表示されます", SwingConstants.CENTER);
    resultLabel.setPreferredSize(new Dimension(300, 30)); // ラベルの推奨サイズを設定

    // 7. 実行ボタンにActionListenerを追加
    executeButton.addActionListener(__ -> {
      var task = taskPool.getSelectedTasks().getFirst();
      var result = task.execute();
      resultLabel.setText("結果: " + result);
    });

    // 8. パネルにコンポーネントを追加
    panel.add(new JLabel("実行するタスクを選択:"));
    panel.add(radioTaskA);
    panel.add(radioTaskB);
    panel.add(executeButton);
    panel.add(resultLabel);

    // 9. フレームにパネルを追加
    add(panel);
  }

  /**
   * アプリケーションのメインエントリポイント。
   * GUIをイベントディスパッチスレッドで安全に起動します。
   *
   * @param args コマンドライン引数 (未使用)
   */
  public static void main(String[] args) {
    // SwingのGUI操作はイベントディスパッチスレッド(EDT)で行うことが推奨される
    SwingUtilities.invokeLater(() -> {
      new SelectableGuiApp().setVisible(true); // GUIアプリケーションのインスタンスを作成し、表示
    });
  }
}
