import java.util.stream.Stream;

public class ProductItem {
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
