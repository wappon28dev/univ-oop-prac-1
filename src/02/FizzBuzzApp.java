import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.List;

class FizzBuzz {
  public static String fizzBuzz(int number) {
    if (number % 3 == 0 && number % 5 == 0) {
      return "FizzBuzz";
    } else if (number % 3 == 0) {
      return "Fizz";
    } else if (number % 5 == 0) {
      return "Buzz";
    } else {
      return String.valueOf(number);
    }
  }

  public static List<String> fizzBuzzUntil(int max) {
    return IntStream.rangeClosed(1, max)
        .mapToObj(FizzBuzz::fizzBuzz)
        .collect(Collectors.toList());
  }
}

public class FizzBuzzApp extends JFrame {
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
    setTitle("K24132:FizzBuzz");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  private void process(String inputText) {
    // NOTE: OS 依存の改行コード - Windows: CRLF, the others: LF
    var lineSeparator = System.lineSeparator();
    var max = Integer.parseInt(inputText);

    var fizzBuzzList = FizzBuzz.fizzBuzzUntil(max);
    var text = String.join(lineSeparator, fizzBuzzList);

    outputArea.setText("");
    outputArea.append(text + lineSeparator);
  }

  public FizzBuzzApp() {
    init();

    var topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    var inputLabel = new JLabel("最大値：");
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
    SwingUtilities.invokeLater(FizzBuzzApp::new);
  }
}
