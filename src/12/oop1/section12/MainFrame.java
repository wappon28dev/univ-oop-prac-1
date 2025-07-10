package oop1.section12;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class StudentTableModel extends AbstractTableModel {
    private final String[] columnNames = { "学生ID", "名前", "学年" };
    private List<University.Student> students = new ArrayList<>();

    public void setStudents(List<University.Student> students) {
        this.students = students;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        var student = students.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> student.id();
            case 1 -> student.name();
            case 2 -> student.grade();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

}

public class MainFrame extends JFrame {
    private JPanel panel;
    private JTree tree;
    private JTable table;
    private University university;
    private StudentTableModel tableModel;

    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        initializeComponents();
        loadUniversityData();

        setContentPane(panel);
        setVisible(true);
    }

    private void initializeComponents() {
        panel = new JPanel(new BorderLayout());
        tree = new JTree();
        tableModel = new StudentTableModel();
        table = new JTable(tableModel);

        tree.addTreeSelectionListener(e -> {
            var selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.getUserObject() instanceof University.Department dept) {
                tableModel.setStudents(dept.students());
            }
        });

        var treeScrollPane = new JScrollPane(tree);
        var tableScrollPane = new JScrollPane(table);

        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, tableScrollPane);
        splitPane.setDividerLocation(300);

        panel.add(splitPane, BorderLayout.CENTER);
    }

    private void loadUniversityData() {
        try {
            university = University.parse("university_data.xml");
            buildTree();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "XMLファイルの読み込みに失敗しました: " + e.getMessage(),
                    "エラー",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildTree() {
        var root = new DefaultMutableTreeNode(university.name());

        university.faculties().forEach(faculty -> {
            var facultyNode = new DefaultMutableTreeNode(faculty.name());

            faculty.departments().forEach(department -> {
                var departmentNode = new DefaultMutableTreeNode(department) {
                    @Override
                    public String toString() {
                        return ((University.Department) getUserObject()).name();
                    }
                };
                facultyNode.add(departmentNode);
            });

            root.add(facultyNode);
        });

        var treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
