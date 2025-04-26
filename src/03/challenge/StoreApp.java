// TODO: WARN: ↓ 提出前に必ず削除すること
package challenge;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
  public final String janCode;
  public final String productName;
  public final double unitPrice;
  public final int quantity;

  public ProductItem(String janCode, String productName, double unitPrice, int quantity) {
    this.janCode = janCode;
    this.productName = productName;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
  }

  public double getSubtotal() {
    return unitPrice * quantity;
  }

  public String toString() {
    return String.format(
        "JAN コード: %s, 商品名: %s, 単価: %.2f, 数量: %d, 小計: %.0f",
        janCode, productName, unitPrice, quantity, getSubtotal());
  }
}

class ProductItemDatabase {
  private final HashMap<String, ProductItem> items = new HashMap<>();
  private final List<ProductItem> ITEMS = List.of(
      new ProductItem("4900000000011", "クロワッサン", 180, 1),
      new ProductItem("4900000000028", "バタール", 250, 1),
      new ProductItem("4900000000035", "カレーパン", 200, 1),
      new ProductItem("4900000000042", "メロンパン", 210, 1),
      new ProductItem("4900000000059", "塩バターパン", 190, 1),
      new ProductItem("4900000000066", "チョココロネ", 220, 1),
      new ProductItem("4900000000073", "フランスパン", 260, 1),
      new ProductItem("4900000000080", "あんぱん", 180, 1),
      new ProductItem("4900000000097", "シナモンロール", 240, 1),
      new ProductItem("4900000000103", "クリームパン", 200, 1));

  public ProductItemDatabase() {
    ITEMS.forEach(item -> items.put(item.janCode, item));
  }

  public Optional<ProductItem> findFrom(String janCode) {
    return Optional.ofNullable(items.get(janCode));
  }
}

public class StoreApp extends JFrame {
  /**
   * JAN コードのフィールド
   */
  private JTextField janCodeField;
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

  /**
   * 商品データベース
   */
  private ProductItemDatabase productItemDatabase = new ProductItemDatabase();

  private JPanel createTopPanel() {
    JPanel topPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5); // 部品間の余白
    gbc.anchor = GridBagConstraints.WEST; // 左寄せを基本とする

    createJanCodeRow(topPanel, gbc);

    return topPanel;
  }

  /**
   * JAN コードの行
   */
  private void createJanCodeRow(JPanel panel, GridBagConstraints gbc) {
    // JAN コードラベル (gridx=0, gridy=0)
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.0; // ラベル列は伸縮させない
    gbc.fill = GridBagConstraints.NONE; // サイズ変更しない
    gbc.anchor = GridBagConstraints.EAST; // ラベルを右寄せにする
    gbc.insets = new Insets(5, 5, 5, 0);
    JLabel janCodeLabel = new JLabel("JAN コード：");
    panel.add(janCodeLabel, gbc);

    // JAN コードフィールド (gridx=1, gridy=0)
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0; // フィールド列は横方向に伸縮させる
    gbc.fill = GridBagConstraints.HORIZONTAL;
    janCodeField = new JTextField(20);
    panel.add(janCodeField, gbc);
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

    janCodeField.addActionListener(__ -> {
      var lineSeparator = System.lineSeparator();

      var janCode = janCodeField.getText();
      var itemOptional = this.productItemDatabase.findFrom(janCode);
      if (itemOptional.isEmpty()) {
        return;
      }
      var item = itemOptional.get();

      this.receipt.addProduct(item);
      outputArea.append(item.toString() + lineSeparator);

      this.janCodeField.setText("");
      this.janCodeField.requestFocus();
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
