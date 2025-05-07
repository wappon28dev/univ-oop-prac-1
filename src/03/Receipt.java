import java.util.List;

public class Receipt {
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
