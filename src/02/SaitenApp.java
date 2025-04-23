import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

class InvalidPointException extends Exception {
  public InvalidPointException() {
    super();
  }

  public String message = "入力された値は点数として正しくありません。";
}

enum SaitenStatus {
  PASS("合格です。おめでとう！"),
  FAIL_RETRY("不合格です。再テストを行いましょう！"),
  FAIL_NEXT_YEAR("不合格です。来年もう一年頑張りましょう。");

  public final String message;

  private SaitenStatus(String message) {
    this.message = message;
  }

  public static SaitenStatus fromPoint(int point) throws InvalidPointException {
    if (point < 0 || point > 100) {
      throw new InvalidPointException();
    }

    if (point >= 60) {
      return PASS;
    } else if (point >= 20) {
      return FAIL_RETRY;
    } else {
      return FAIL_NEXT_YEAR;
    }
  }
}

public class SaitenApp extends JFrame {
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
    setTitle("K24132:採点");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  private void process(String inputText) {
    // NOTE: OS 依存の改行コード - Windows: \r\n, the others: \n
    var lineSeparator = System.lineSeparator();
    var point = Integer.parseInt(inputText);

    outputArea.setText("");
    try {
      var status = SaitenStatus.fromPoint(point);
      outputArea.append(status.message + lineSeparator);
    } catch (InvalidPointException e) {
      outputArea.append(e.message + lineSeparator);
    }
  }

  public SaitenApp() {
    init();

    var topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    var inputLabel = new JLabel("テストの点数：");
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
    SwingUtilities.invokeLater(SaitenApp::new);
  }
}
