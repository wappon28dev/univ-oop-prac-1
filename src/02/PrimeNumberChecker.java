import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

class PrimeNumber {
  // ref: https://qiita.com/ppza53893/items/e0f464340d6f97760cd5
  public static boolean isPrime(int num) {
    if (num < 2) {
      return false;
    }
    for (int i = 2; i < num; i++) {
      if (num % i == 0) {
        return false;
      }
    }
    return true;
  }
}

public class PrimeNumberChecker extends JFrame {
  /**
   * 文字を入力するフィールド
   */
  private final JTextField inputField;
  /**
   * 処理を実行するボタン
   */
  private final JButton processButton;
  /**
   * 処理結果を表示するエリア
   */
  private final JTextArea outputArea;

  private void init() {
    setTitle("K24132:素数判定");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  private void process(String inputText) {
    // NOTE: OS 依存の改行コード - Windows: CRLF, the others: LF
    var lineSeparator = System.lineSeparator();
    var num = Integer.parseInt(inputText);

    var text = PrimeNumber.isPrime(num) ? "素数です" : "素数ではありません";

    outputArea.setText("");
    outputArea.append(text + lineSeparator);
  }

  public PrimeNumberChecker() {
    init();

    var topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    var inputLabel = new JLabel("整数値を入力：");
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
    SwingUtilities.invokeLater(PrimeNumberChecker::new);
  }
}
