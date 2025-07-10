package oop1.section11;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class GenerateSalesData {
  public static void main(String[] args) {
    final String fileName = "sales_data.csv";
    final int numberOfRows = 1_000_0000; // 1億行
    final String[] categories = { "Electronics", "Books", "Clothing", "Groceries", "Home Goods", "Sports" };
    final Random random = new Random();
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    System.out.println("Generating " + numberOfRows + " rows of sales data...");

    // try-with-resources構文で、ファイル書き込み後に自動でリソースをクローズする
    try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
      // ヘッダー行を書き込む
      pw.println("OrderID,Timestamp,Category,Price,Quantity");

      for (int i = 0; i < numberOfRows; i++) {
        String orderId = UUID.randomUUID().toString();
        LocalDateTime timestamp = LocalDateTime.now().minusSeconds(random.nextInt(365 * 24 * 3600));
        String category = categories[random.nextInt(categories.length)];
        int price = 100 + random.nextInt(10000); // 100円から10099円
        int quantity = 1 + random.nextInt(10); // 1個から10個

        pw.println(String.join(",", orderId, timestamp.format(formatter), category, String.valueOf(price),
            String.valueOf(quantity)));

        // 10万行ごとに進捗を表示
        if ((i + 1) % 100000 == 0) {
          System.out.println((i + 1) + " rows generated...");
        }
      }
    } catch (IOException e) {
      System.err.println("An error occurred while writing to the file: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("Data generation complete. File '" + fileName + "' has been created.");
  }
}
