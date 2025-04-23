import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CircularArray {
  public String[] buffer;

  public CircularArray(int len) {
    buffer = new String[len];
  }

  public void add(String value) {
    /**
     * 末尾から順に, 前の要素を持ってくる
     * 
     * @formatter:off
     * +-----+-----+-----+-----+-----+
     * |  A  |  B  |  C  |  D  |  E  |
     * +-----+-----+-----+-----+-----+
     *          ↘     ↘     ↘
     * +-----+-----+-----+-----+-----+
     * |  A  |  A  |  B  |  C  |  D  |
     * +-----+-----+-----+-----+-----+
     *   ↓
     * +-----+-----+-----+-----+-----
     * |  E  |  A  |  B  |  C  |  D  |
     * +-----+-----+-----+-----+-----+
     * @formatter:on
     */
    for (int i = buffer.length - 1; i > 0; i--) {
      buffer[i] = buffer[i - 1];
    }

    buffer[0] = value;
  }

  public List<String> getBuffAsList() {
    return Arrays.asList(buffer);
  }
}

public class CircularBufferApp extends JFrame {
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

  private final CircularArray circularArray = new CircularArray(10);

  private void init() {
    setTitle("K24132:サーキュラーバッファ");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  private void process(String inputText) {
    // NOTE: OS 依存の改行コード - Windows: CRLF, the others: LF
    var lineSeparator = System.lineSeparator();

    circularArray.add(inputText);
    var text = circularArray.getBuffAsList()
        .stream()
        .filter(s -> s != null)
        .collect(Collectors.joining(lineSeparator));

    outputArea.setText("");
    outputArea.append(text + lineSeparator);
  }

  public CircularBufferApp() {
    init();

    var topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    var inputLabel = new JLabel("データを入力：");
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
    SwingUtilities.invokeLater(CircularBufferApp::new);
  }
}
