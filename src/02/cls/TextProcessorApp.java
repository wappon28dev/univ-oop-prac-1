package cls;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class TextProcessorApp extends JFrame {
  /**
   * 文字を入力するフィールド
   */
  private JTextField inputField;
  /**
   * 処理を実行するボタン
   */
  private JButton processButton;
  /**
   * 処理結果を表示するエリア
   */
  private JTextArea outputArea;

  private void init() {
    setTitle("課題用サンプル");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  private void process(String inputText) {
    // NOTE: OS 依存の改行コード - Windows: \r\n, the others: \n
    var lineSeparator = System.lineSeparator();

    outputArea.append(inputText + lineSeparator);
  }

  public TextProcessorApp() {
    init();

    var topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    var inputLabel = new JLabel("入力:");
    inputField = new JTextField(15);
    processButton = new JButton("処理実行");

    Stream
        .of(inputLabel, inputField, processButton)
        .forEach(topPanel::add);

    outputArea = new JTextArea();
    outputArea.setEditable(false);

    var scrollPane = new JScrollPane(outputArea);

    add(topPanel,
        // NOTE: `NORTH` は上部 (北側) を表す
        BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);

    processButton.addActionListener(__ -> {
      var inputText = inputField.getText();
      process(inputText);
    });

    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(TextProcessorApp::new);
  }
}
