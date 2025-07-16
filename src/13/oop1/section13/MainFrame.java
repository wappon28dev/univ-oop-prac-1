package oop1.section13;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

public class MainFrame extends JFrame {
  private enum FileType {
    IMAGE, TEXT, ZIP, UNSUPPORTED
  }

  private JButton openDirectoryButton;
  private JList<File> fileList;
  private DefaultListModel<File> listModel;
  private JFrame currentPreviewFrame;

  public MainFrame() {
    initializeComponents();
    setupLayout();
    setupEventHandlers();
    setupWindow();
  }

  private void initializeComponents() {
    openDirectoryButton = new JButton("ディレクトリを開く");
    listModel = new DefaultListModel<>();
    fileList = new JList<>(listModel);

    // ファイルリストにアイコンとファイル名を表示するためのセルレンダラーを設定
    fileList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof File file) {
          setText(file.getName());
          setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
        }
        return this;
      }
    });
  }

  private void setupLayout() {
    setLayout(new BorderLayout());

    // 上部にボタンを配置
    var topPanel = new JPanel(new FlowLayout());
    topPanel.add(openDirectoryButton);
    add(topPanel, BorderLayout.NORTH);

    // 中央にファイルリストを配置
    var scrollPane = new JScrollPane(fileList);
    add(scrollPane, BorderLayout.CENTER);
  }

  private void setupEventHandlers() {
    openDirectoryButton.addActionListener(__ -> openDirectory());

    fileList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting() && fileList.getSelectedValue() != null) {
        openPreviewWindow(fileList.getSelectedValue());
      }
    });
  }

  private void setupWindow() {
    setTitle("ファイルプレビューア");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(600, 400);
    setLocationRelativeTo(null);
  }

  private void openDirectory() {
    var fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    var result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      var directory = fileChooser.getSelectedFile();
      loadFilesFromDirectory(directory);
    }
  }

  private void loadFilesFromDirectory(File directory) {
    listModel.clear();
    var files = directory.listFiles();

    if (files != null) {
      for (var file : files) {
        if (file.isFile()) {
          listModel.addElement(file);
        }
      }
    }
  }

  private void openPreviewWindow(File file) {
    // 既存のプレビューウィンドウを閉じる
    if (currentPreviewFrame != null) {
      currentPreviewFrame.dispose();
      currentPreviewFrame = null;
    }

    var fileName = file.getName().toLowerCase();
    var extension = getFileExtension(fileName);

    try {
      currentPreviewFrame = switch (getFileType(extension)) {
        case IMAGE -> new ImagePreviewFrame(file);
        case TEXT -> new TextPreviewFrame(file);
        case ZIP -> new ZipPreviewFrame(file);
        case UNSUPPORTED -> null;
      };

      if (currentPreviewFrame != null) {
        currentPreviewFrame.setVisible(true);
      }
    } catch (Exception e) {
      var errorMessage = """
          ファイルが破損しているか、サポートされていない形式です。

          エラーの詳細: %s
          """.formatted(e.getMessage());

      showErrorMessage(errorMessage);
      if (currentPreviewFrame != null) {
        currentPreviewFrame.dispose();
        currentPreviewFrame = null;
      }
    }
  }

  private String getFileExtension(String fileName) {
    var lastDotIndex = fileName.lastIndexOf('.');
    return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
  }

  private FileType getFileType(String extension) {
    return switch (extension) {
      case "png", "jpg", "jpeg", "gif", "bmp" -> FileType.IMAGE;
      case "txt", "csv", "xml", "java", "md" -> FileType.TEXT;
      case "zip" -> FileType.ZIP;
      default -> FileType.UNSUPPORTED;
    };
  }

  private void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "エラー", JOptionPane.ERROR_MESSAGE);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      var mainFrame = new MainFrame();
      mainFrame.setVisible(true);
    });
  }
}
