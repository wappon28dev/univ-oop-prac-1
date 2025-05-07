import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

class ProductItem {
  public final String productName;
  public final double unitPrice;
  public final int quantity;

  public ProductItem(String productName, double unitPrice, int quantity) {
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
  }

  public double getSubtotal() {
    return unitPrice * quantity;
  }

  public String toString() {
    return String.format(
        "商品名: %s, 単価: %.2f, 数量: %d, 小計: %.0f",
        productName, unitPrice, quantity, getSubtotal());
  }

  public static class FromStringError extends Exception {
    public enum Variant {
      CONTAINS_EMPTY("未入力項目があります。"),
      INVALID_UNIT_OR_QUANTITY("単価と数量には正の数値を入力してください。"),
      MALFORMED_NUMBER("数値形式が不正です。");

      public final String message;

      Variant(String message) {
        this.message = message;
      }
    }

    public final Variant variant;

    public FromStringError(Variant variant) {
      super(variant.message);
      this.variant = variant;
    }
  }

  public static ProductItem fromString(
      String productName,
      String unitPrice,
      String quantity) throws FromStringError {
    var containsEmpty = Stream.of(productName, unitPrice, quantity).anyMatch(String::isEmpty);
    if (containsEmpty) {
      throw new FromStringError(FromStringError.Variant.CONTAINS_EMPTY);
    }

    double unitPriceVal;
    int quantityVal;
    try {
      unitPriceVal = Double.parseDouble(unitPrice);
      quantityVal = Integer.parseInt(quantity);
    } catch (NumberFormatException e) {
      throw new FromStringError(FromStringError.Variant.MALFORMED_NUMBER);
    }

    if (unitPriceVal <= 0 || quantityVal <= 0) {
      throw new FromStringError(FromStringError.Variant.INVALID_UNIT_OR_QUANTITY);
    }

    return new ProductItem(productName, unitPriceVal, quantityVal);
  }
}

public class StoreApp extends JFrame {
  /**
   * 商品名を入力するフィールド
   */
  private JTextField productNameField;
  /**
   * 単価を入力するフィールド
   */
  private JTextField unitPriceField;
  /**
   * 数量を入力するフィールド
   */
  private JTextField quantityField;
  /**
   * 処理を実行するボタン
   */
  private JButton processButton;
  /**
   * 合計結果の算出を実行するボタン
   */
  private JButton totalButton;
  /**
   * 処理結果を表示するエリア
   */
  private JTextArea outputArea;

  /**
   * レシート
   */
  private Receipt receipt;

  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // 部品間の余白
    gbc.anchor = GridBagConstraints.WEST; // 左寄せを基本とする

    createProductNameRow(topPanel, gbc);
    createUnitPriceRow(topPanel, gbc);
    createQuantityRow(topPanel, gbc);
    createProcessButtonRow(topPanel, gbc);

    return topPanel;
  }

  /**
   * 商品名の行
   */
  private void createProductNameRow(JPanel panel, GridBagConstraints gbc) {
    // 商品名ラベル (gridx=0, gridy=0)
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.0; // ラベル列は伸縮させない
    gbc.fill = GridBagConstraints.NONE; // サイズ変更しない
    gbc.anchor = GridBagConstraints.EAST; // ラベルを右寄せにする
    JLabel productNameLabel = new JLabel("商品名:");
    panel.add(productNameLabel, gbc);

    // 商品名フィールド (gridx=1, gridy=0)
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0; // フィールド列は横方向に伸縮させる
    gbc.fill = GridBagConstraints.HORIZONTAL; // 横方向にいっぱいに広げる
    productNameField = new JTextField();
    panel.add(productNameField, gbc);
  }

  /**
   * 単価の行
   */
  private void createUnitPriceRow(JPanel panel, GridBagConstraints gbc) {
    // 単価ラベル (gridx=0, gridy=1)
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    JLabel unitPriceLabel = new JLabel("単価:");
    panel.add(unitPriceLabel, gbc);

    // 単価フィールド (gridx=1, gridy=1)
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    unitPriceField = new JTextField();
    panel.add(unitPriceField, gbc);
  }

  /**
   * 数量の行
   */
  private void createQuantityRow(JPanel panel, GridBagConstraints gbc) {
    // 数量ラベル (gridx=0, gridy=2)
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0.0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST;
    JLabel quantityLabel = new JLabel("数量:");
    panel.add(quantityLabel, gbc);

    // 数量フィールド (gridx=1, gridy=2)
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    quantityField = new JTextField();
    panel.add(quantityField, gbc);
  }

  /**
   * ボタンの行
   */
  private void createProcessButtonRow(JPanel panel, GridBagConstraints gbc) {
    // ボタン (gridx=1, gridy=3) 右寄せで配置
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.weightx = 0.0; // ボタン自体は伸縮させない
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST; // ボタンを右端に寄せる
    processButton = new JButton("追加");
    panel.add(processButton, gbc);
  }

  /**
   * 処理結果を表示するエリア
   */
  private JScrollPane createOutputArea() {
    outputArea = new JTextArea();
    return new JScrollPane(outputArea);
  }

  private JPanel createBottomPanel() {
    JPanel bottomPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // 部品間の余白
    gbc.anchor = GridBagConstraints.WEST; // 左寄せを基本とする

    createTotalButtonRow(bottomPanel, gbc);

    return bottomPanel;
  }

  /**
   * 合計ボタンの行
   */
  private void createTotalButtonRow(JPanel panel, GridBagConstraints gbc) {
    // 合計ボタン (gridx=1, gridy=4) 右寄せで配置
    gbc.gridx = 1;
    gbc.gridy = 4;
    // ref: https://chatgpt.com/share/680b07a8-1540-8003-bf93-dd0566fecce4
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.EAST; // ボタンを右端に寄せる
    totalButton = new JButton("合計計算");
    panel.add(totalButton, gbc);
  }

  private void clearInputFields() {
    productNameField.setText("");
    unitPriceField.setText("");
    quantityField.setText("");
  }

  private void initWindow() {
    setTitle("レジ");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(400, 500);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout(5, 5));
  }

  public StoreApp() {
    initWindow();

    this.receipt = new Receipt();
    JPanel topPanel = createTopPanel();
    JScrollPane scrollPane = createOutputArea();
    JPanel bottomPanel = createBottomPanel();

    add(topPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);

    processButton.addActionListener(__ -> {
      var productName = productNameField.getText();
      var unitPrice = unitPriceField.getText();
      var quantity = quantityField.getText();

      // NOTE: OS 依存の改行コード - Windows: CRLF, the others: LF
      var lineSeparator = System.lineSeparator();

      try {
        var item = ProductItem.fromString(productName, unitPrice, quantity);
        this.receipt.addProduct(item);
        outputArea.append(item.toString() + lineSeparator);

        clearInputFields();
        this.productNameField.requestFocus();
      } catch (ProductItem.FromStringError e) {
        System.err.println(e.getMessage());
      }
    });

    totalButton.addActionListener(__ -> {
      var lineSeparator = System.lineSeparator();
      var totalQuantity = this.receipt.getTotalQuantity();
      var totalPrice = this.receipt.getTotalPrice();

      var text = String
          .format(
              "<ls>--- 合計点数: %d 点 ---<ls>--- 合計金額: %.0f 円 ---<ls><ls>",
              totalQuantity, totalPrice)
          .replaceAll("<ls>", lineSeparator);
      outputArea.append(text);
    });

    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(StoreApp::new);
  }
}
