package oop1.section13;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public class ZipPreviewFrame extends JFrame {
  private final File zipFile;

  public ZipPreviewFrame(File zipFile) throws IOException {
    this.zipFile = zipFile;
    initializeComponents();
    setupWindow();
  }

  private void initializeComponents() throws IOException {
    var root = new DefaultMutableTreeNode(zipFile.getName());
    var directoryNodes = new HashMap<String, DefaultMutableTreeNode>();

    try (var zip = new ZipFile(zipFile)) {
      var entries = zip.entries();

      while (entries.hasMoreElements()) {
        var entry = entries.nextElement();
        var path = entry.getName();

        if (entry.isDirectory()) {
          createDirectoryNode(root, path, directoryNodes);
        } else {
          createFileNode(root, path, directoryNodes);
        }
      }
    }

    var tree = new JTree(new DefaultTreeModel(root));
    tree.setRootVisible(true);
    tree.expandRow(0);

    var scrollPane = new JScrollPane(tree);
    scrollPane.setPreferredSize(new Dimension(600, 400));

    add(scrollPane);
  }

  private void createDirectoryNode(DefaultMutableTreeNode root, String path,
      Map<String, DefaultMutableTreeNode> directoryNodes) {
    var parts = path.split("/");
    var currentNode = root;
    var currentPath = new StringBuilder();

    for (var i = 0; i < parts.length - 1; i++) {
      if (i > 0)
        currentPath.append("/");
      currentPath.append(parts[i]);

      var pathStr = currentPath.toString();
      currentNode = directoryNodes.getOrDefault(pathStr, root);
    }
    if (currentNode == root || !currentNode.isLeaf()) {
      currentNode = new DefaultMutableTreeNode(parts[parts.length - 1]);
      directoryNodes.put(currentPath.toString(), currentNode);
      root.add(currentNode);
    }
  }

  private void createFileNode(DefaultMutableTreeNode root, String path,
      Map<String, DefaultMutableTreeNode> directoryNodes) {
    var parts = path.split("/");
    var currentNode = root;

    var currentPath = new StringBuilder();
    for (var i = 0; i < parts.length - 1; i++) {
      if (i > 0)
        currentPath.append("/");
      currentPath.append(parts[i]);

      var pathStr = currentPath.toString();
      currentNode = directoryNodes.getOrDefault(pathStr, root);
      if (currentNode == null) {
        currentNode = new DefaultMutableTreeNode(parts[i]);
        directoryNodes.put(pathStr, currentNode);
        root.add(currentNode);
      }
    }

    // ファイルノードを追加
    var fileNode = new DefaultMutableTreeNode(parts[parts.length - 1]);
    currentNode.add(fileNode);
  }

  private void setupWindow() {
    setTitle("ZIPプレビュー: " + zipFile.getName());
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
  }
}
