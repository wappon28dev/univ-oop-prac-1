// TODO: WARN: ↓ 提出前に必ず削除すること
package hw2;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;
import java.util.List;

class Receipt {
  public ProductItem[] items;

  public Receipt() {
    this.items = new ProductItem[0];
  }

  public List<ProductItem> getAsList() {
    return List.of(items);
  }

  public void addProduct(ProductItem item) {
    var newItems = new java.util.ArrayList<>(this.getAsList());
    newItems.add(item);
    // ref: https://zenn.dev/goriki/articles/038-list-to-array#stream().toarray()
    items = newItems.toArray(ProductItem[]::new);
  }

  public double getTotalPrice() {
    return this.getAsList().stream().mapToDouble(ProductItem::getSubtotal).sum();
  }

  public int getTotalQuantity() {
    return this.getAsList().stream().mapToInt(item -> item.quantity).sum();
  }
}

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
      INVALID_UNIT_OR_QUANTITY("単価と数量には正の数値を入力してください。");

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

    var unitPriceVal = Double.parseDouble(unitPrice);
    var quantityVal = Integer.parseInt(quantity);
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
   * 処理結果を表示するエリア
   */
  private JTextArea outputArea;

  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // 部品間の余白
    gbc.anchor = GridBagConstraints.WEST; // 左寄せを基本とする

    createProductNameRow(topPanel, gbc);
    createUnitPriceRow(topPanel, gbc);
    createQuantityRow(topPanel, gbc);
    createButtonRow(topPanel, gbc);

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
  private void createButtonRow(JPanel panel, GridBagConstraints gbc) {
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

    JPanel topPanel = createTopPanel();
    JScrollPane scrollPane = createOutputArea();

    add(topPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);

    processButton.addActionListener(__ -> {
      var productName = productNameField.getText();
      var unitPrice = unitPriceField.getText();
      var quantity = quantityField.getText();

      // NOTE: OS 依存の改行コード - Windows: CRLF, the others: LF
      var lineSeparator = System.lineSeparator();

      try {
        var item = ProductItem.fromString(productName, unitPrice, quantity);
        outputArea.append(item.toString() + lineSeparator);
        clearInputFields();
      } catch (ProductItem.FromStringError e) {
        System.err.println(e.getMessage());
      }
    });

    setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(StoreApp::new);
  }
}
