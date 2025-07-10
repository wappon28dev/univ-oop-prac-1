package oop1.section11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DataAggregatorApp extends JFrame {
  private JTextField filePathField;
  private JButton selectFileButton;
  private JButton startButton;
  private JButton cancelButton;
  private JProgressBar progressBar;
  private JLabel statusLabel;
  private JTextArea resultArea;
  private File selectedFile;
  private AggregationWorker currentWorker;

  public DataAggregatorApp() {
    setTitle("大規模データ集計アプリケーション");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    initializeComponents();
    layoutComponents();
    pack();
    setLocationRelativeTo(null);
  }

  private void initializeComponents() {
    filePathField = new JTextField(40);
    filePathField.setEditable(false);

    selectFileButton = new JButton("ファイル選択");
    selectFileButton.addActionListener(new FileSelectListener());

    startButton = new JButton("集計開始");
    startButton.addActionListener(new StartListener());
    startButton.setEnabled(false);

    cancelButton = new JButton("キャンセル");
    cancelButton.addActionListener(new CancelListener());
    cancelButton.setEnabled(false);

    progressBar = new JProgressBar(0, 100);
    progressBar.setStringPainted(true);

    statusLabel = new JLabel("待機中");

    resultArea = new JTextArea(20, 60);
    resultArea.setEditable(false);
    resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
  }

  private void layoutComponents() {
    setLayout(new BorderLayout());

    // ファイル選択パネル
    JPanel filePanel = new JPanel(new FlowLayout());
    filePanel.add(new JLabel("ファイル:"));
    filePanel.add(filePathField);
    filePanel.add(selectFileButton);

    // 操作パネル
    JPanel controlPanel = new JPanel(new FlowLayout());
    controlPanel.add(startButton);
    controlPanel.add(cancelButton);

    // 進捗パネル
    JPanel progressPanel = new JPanel(new BorderLayout());
    progressPanel.add(progressBar, BorderLayout.NORTH);
    progressPanel.add(statusLabel, BorderLayout.SOUTH);

    // 上部パネル
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(filePanel, BorderLayout.NORTH);
    topPanel.add(controlPanel, BorderLayout.CENTER);
    topPanel.add(progressPanel, BorderLayout.SOUTH);

    add(topPanel, BorderLayout.NORTH);
    add(new JScrollPane(resultArea), BorderLayout.CENTER);
  }

  private class FileSelectListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));

      if (fileChooser.showOpenDialog(DataAggregatorApp.this) == JFileChooser.APPROVE_OPTION) {
        selectedFile = fileChooser.getSelectedFile();
        filePathField.setText(selectedFile.getAbsolutePath());
        startButton.setEnabled(true);
      }
    }
  }

  private class StartListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (selectedFile == null)
        return;

      startButton.setEnabled(false);
      cancelButton.setEnabled(true);
      selectFileButton.setEnabled(false);
      resultArea.setText("");
      progressBar.setValue(0);
      statusLabel.setText("集計を開始しています...");

      currentWorker = new AggregationWorker(selectedFile);
      currentWorker.execute();
    }
  }

  private class CancelListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (currentWorker != null && !currentWorker.isDone()) {
        currentWorker.cancel(true);
      }
    }
  }

  private class AggregationWorker extends SwingWorker<Map<String, SummaryData>, Integer> {
    private final File file;
    private long totalLines;

    public AggregationWorker(File file) {
      this.file = file;
    }

    @Override
    protected Map<String, SummaryData> doInBackground() throws Exception {
      Map<String, SummaryData> categoryData = new HashMap<>();

      // 総行数を取得
      totalLines = countLines();
      if (isCancelled())
        return null;

      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line = br.readLine(); // ヘッダー行をスキップ
        long processedLines = 0;

        while ((line = br.readLine()) != null && !isCancelled()) {
          processedLines++;

          String[] fields = line.split(",");
          if (fields.length >= 5) {
            try {
              String category = fields[2];
              int price = Integer.parseInt(fields[3]);
              int quantity = Integer.parseInt(fields[4]);
              long sales = (long) price * quantity;

              categoryData.computeIfAbsent(category, k -> new SummaryData())
                  .addTransaction(sales);

            } catch (NumberFormatException e) {
              // 不正なデータは無視
            }
          }

          // 進捗更新（1000行ごと）
          if (processedLines % 1000 == 0) {
            int progress = (int) ((processedLines * 100) / totalLines);
            publish(progress);
          }
        }
      }

      return categoryData;
    }

    private long countLines() throws IOException {
      long lines = 0;
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        while (br.readLine() != null && !isCancelled()) {
          lines++;
        }
      }
      return lines - 1; // ヘッダー行を除く
    }

    @Override
    protected void process(java.util.List<Integer> chunks) {
      int progress = chunks.get(chunks.size() - 1);
      progressBar.setValue(progress);
      statusLabel.setText(String.format("ファイルを読み込み中... %d%%", progress));
    }

    @Override
    protected void done() {
      startButton.setEnabled(true);
      cancelButton.setEnabled(false);
      selectFileButton.setEnabled(true);

      try {
        if (isCancelled()) {
          statusLabel.setText("キャンセルされました");
          progressBar.setValue(0);
        } else {
          Map<String, SummaryData> result = get();
          if (result != null) {
            displayResults(result);
            statusLabel.setText("完了");
            progressBar.setValue(100);
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        statusLabel.setText("エラーが発生しました");
        JOptionPane.showMessageDialog(DataAggregatorApp.this,
            "処理中にエラーが発生しました: " + e.getMessage(),
            "エラー", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private void displayResults(Map<String, SummaryData> categoryData) {
    StringBuilder sb = new StringBuilder();
    sb.append("商品カテゴリごとの集計結果:\n");
    sb.append("=====================================\n\n");

    for (Map.Entry<String, SummaryData> entry : categoryData.entrySet()) {
      String category = entry.getKey();
      SummaryData data = entry.getValue();

      sb.append(String.format("カテゴリ: %s\n", category));
      sb.append(String.format("  総売上高: %,d円\n", data.getTotalSales()));
      sb.append(String.format("  取引回数: %,d回\n", data.getTransactionCount()));
      sb.append(String.format("  平均取引単価: %,.2f円\n\n", data.getAverageTransaction()));
    }

    resultArea.setText(sb.toString());
  }

  private static class SummaryData {
    private long totalSales = 0;
    private long transactionCount = 0;

    public void addTransaction(long sales) {
      this.totalSales += sales;
      this.transactionCount++;
    }

    public long getTotalSales() {
      return totalSales;
    }

    public long getTransactionCount() {
      return transactionCount;
    }

    public double getAverageTransaction() {
      return transactionCount > 0 ? (double) totalSales / transactionCount : 0.0;
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new DataAggregatorApp().setVisible(true);
    });
  }
}
