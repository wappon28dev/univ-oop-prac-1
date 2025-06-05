package oop1.todoapp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * TODOリストアプリケーションのメインクラスです。
 * GUIの構築とイベント処理を担当します。
 * Taskableインターフェースを実装したTaskクラスを利用します。
 */
public class TodoListApp {

  private JFrame frame; // メインウィンドウ
  private DefaultListModel<Task> listModel; // JListのモデル (Taskオブジェクトを格納)
  private JList<Task> taskList; // タスク表示用リスト (Taskオブジェクトを表示)
  private JTextField taskInput; // タスク内容入力用テキストフィールド
  private JTextField dueDateInput; // 期限日入力用テキストフィールド
  private List<Task> tasks; // タスクを格納するArrayList (Taskオブジェクトのリスト)

  private void updateListModel() {
    listModel.clear();
    tasks.forEach(listModel::addElement);
  }

  private void addTask() {
    var description = taskInput.getText().trim();
    var dueDateStr = dueDateInput.getText().trim();

    if (description.isEmpty() || dueDateStr.isEmpty()) {
      System.err.println("タスク内容または期限日が空です。");
      return;
    }

    var dueDate = Taskable.parseDueDate(dueDateStr);

    tasks.add(new Task(description, dueDate));
    updateListModel();

    taskInput.setText("");
    dueDateInput.setText("");
  }

  private void toggleTaskCompletion() {
    var selectedTaskOptional = Optional.ofNullable(taskList.getSelectedValue());

    if (selectedTaskOptional.isEmpty()) {
      System.err.println("タスクが選択されていません。");
      return;
    }

    var selectedTask = selectedTaskOptional.get();
    selectedTask.setCompleted(!selectedTask.isCompleted());
    taskList.repaint();
  }

  private void deleteTask() {
    var selectedTaskOptional = Optional.ofNullable(taskList.getSelectedValue());
    if (selectedTaskOptional.isEmpty()) {
      System.err.println("タスクが選択されていません。");
      return;
    }

    var selectedTask = selectedTaskOptional.get();
    tasks.remove(selectedTask);
    updateListModel();
  }

  private void sortTasksByDueDate() {
    var sortedTasks = new ArrayList<>(tasks);
    sortedTasks.sort(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));

    tasks.clear();
    tasks.addAll(sortedTasks);
    updateListModel();
  }

  /**
   * アプリケーションを初期化し、GUIを表示します。
   */
  public TodoListApp() {
    // データ構造の初期化
    tasks = new ArrayList<>();
    listModel = new DefaultListModel<>();

    // メインフレームの設定
    frame = new JFrame("TODO");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(750, 450);
    frame.setLayout(new BorderLayout(5, 5)); // コンポーネント間の隙間を設定

    // 入力パネルの作成
    JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // 左揃え、コンポーネント間隔5px

    // タスク内容入力フィールド
    inputPanel.add(new JLabel("タスク内容:"));
    taskInput = new JTextField(20); // 幅の目安として20文字分
    inputPanel.add(taskInput);

    // 期限日入力フィールド
    inputPanel.add(new JLabel("期限日 (YYYY-MM-DD):"));
    dueDateInput = new JTextField(10); // 幅の目安として10文字分 (YYYY-MM-DD)
    inputPanel.add(dueDateInput);

    // 追加ボタン
    JButton addButton = new JButton("追加");
    addButton.addActionListener(__ -> addTask());

    inputPanel.add(addButton);

    frame.add(inputPanel, BorderLayout.NORTH); // フレームの上部に入力パネルを追加

    // タスク表示リストの作成
    taskList = new JList<>(listModel); // listModelを使用してJListを初期化
    taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 単一選択モードに設定

    taskList.setCellRenderer(new TaskCellRenderer());

    JScrollPane scrollPane = new JScrollPane(taskList); // リストをスクロール可能にする
    frame.add(scrollPane, BorderLayout.CENTER); // フレームの中央にリストを追加

    // 操作ボタンパネルの作成
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // 中央揃え、コンポーネント間隔(左右10px, 上下5px)

    // 完了/未完了切り替えボタン
    JButton toggleCompleteButton = new JButton("完了/未完了");
    toggleCompleteButton.addActionListener(__ -> toggleTaskCompletion());

    buttonPanel.add(toggleCompleteButton);

    // 削除ボタン
    JButton deleteButton = new JButton("削除");
    deleteButton.addActionListener(__ -> deleteTask());

    buttonPanel.add(deleteButton);

    // 期限日ソートボタン
    JButton sortByDueDateButton = new JButton("期限日でソート");
    sortByDueDateButton.addActionListener(__ -> sortTasksByDueDate());

    buttonPanel.add(sortByDueDateButton);

    frame.add(buttonPanel, BorderLayout.SOUTH); // フレームの下部にボタンパネルを追加

    // フレームを画面中央に表示し、可視化
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  /**
   * アプリケーションのエントリーポイントです。
   * SwingUtilities.invokeLaterを使用して、イベントディスパッチスレッドでGUIを起動します。
   *
   * @param args コマンドライン引数 (このアプリケーションでは未使用)
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new TodoListApp());
  }
}
