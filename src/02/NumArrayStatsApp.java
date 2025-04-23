import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ListStats {
  private List<Integer> data;
  private List<Integer> dataSorted;

  public ListStats(List<Integer> data) {
    this.data = data;

    // ref: https://tech-blog.rakus.co.jp/entry/20220928/sort
    this.dataSorted = data.stream().sorted().collect(Collectors.toList());
  }

  public Integer getSum() {
    return data.stream().mapToInt(Integer::intValue).sum();
  }

  public Integer getMax() {
    return data.stream().max(Integer::compareTo).orElseThrow();
  }

  public Integer getMin() {
    return data.stream().min(Integer::compareTo).orElseThrow();
  }

  public Double getAverage() {
    return data.stream().mapToInt(Integer::intValue).average().orElseThrow();
  }

  public Double getMedian() {
    var size = dataSorted.size();
    var mid = size / 2;

    var isEven = size % 2 == 0;

    // ref:
    // https://qiita.com/pompomJ/items/62c958839588a1751799#%E4%B8%AD%E5%A4%AE%E5%80%A4%E3%82%92%E6%B1%82%E3%82%81%E3%82%8B%E3%81%9D%E3%81%AE%EF%BC%91
    var midData = isEven
        ? (dataSorted.get(mid - 1) + dataSorted.get(mid)) / 2.0
        : dataSorted.get(mid);

    return midData;
  }

  /**
   * 方針:
   * [1, 1, 2, 3, 3, 4, 5]
   * -> [1, 2, 3, 4, 5] 重複をなくす (A)
   * -> [2, 1, 2, 1, 1] 数を数える (B)
   * A がキーで B が値のハッシュマップを作成する.
   * 
   * @return HashMap<Integer, Integer>: <数, 出現回数>
   */
  private HashMap<Integer, Integer> getCountMap() {
    var countMap = new HashMap<Integer, Integer>();
    data.forEach(i -> {
      var current = countMap.getOrDefault(i, 0);
      countMap.put(i, current + 1);
    });

    return countMap;
  }

  public List<Integer> getMode() {
    // [<1, 2>, <2, 1>, <3, 2>, <4, 1>, <5, 1>]
    var countMap = getCountMap();

    // 2
    var maxValue = countMap.values().stream().max(Integer::compareTo).orElse(0);
    // [<1, 2>, <3, 2>]
    var modeEntries = countMap
        .entrySet()
        .stream()
        .filter(entry -> entry.getValue() == maxValue)
        .collect(Collectors.toList());

    // [1, 3]
    return modeEntries.stream()
        .map(e -> e.getKey())
        .sorted()
        .collect(Collectors.toList());
  }
}

public class NumArrayStatsApp extends JFrame {
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
    setTitle("K24132 統計情報算出");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 350);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  private void process(String inputText) {
    // NOTE: OS 依存の改行コード - Windows: CRLF, the others: LF
    var lineSeparator = System.lineSeparator();

    var arr = inputText.split(",");
    var arr_int = Stream.of(arr)
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    var stats = new ListStats(arr_int);

    var sum = stats.getSum();
    var average = stats.getAverage();
    var min = stats.getMin();
    var max = stats.getMax();
    var median = stats.getMedian();
    var mode = stats.getMode().stream()
        .map(String::valueOf)
        .collect(Collectors.joining(", "));

    var text = String.format(
        "合計：%d<ls>"
            + "平均：%f<ls>"
            + "最小値：%d<ls>"
            + "最大値：%d<ls>"
            + "中央値：%f<ls>"
            + "最頻値：[%s]<ls>",
        sum,
        average,
        min,
        max,
        median,
        mode)
        .replaceAll("<ls>", lineSeparator);

    outputArea.setText("");
    outputArea.append(text + lineSeparator);
  }

  public NumArrayStatsApp() {
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
    SwingUtilities.invokeLater(NumArrayStatsApp::new);
  }
}
