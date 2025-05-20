
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Try1 extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField inputField;
    private JComboBox<String> priorityBox;
    private JButton addButton, editButton, deleteButton;
    private final String FILE_NAME = "todo_table.txt";

    public Try1() {
        setTitle("To-Do List with Priority and Completion");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField(12);
        priorityBox = new JComboBox<>(new String[]{"높음", "중간", "낮음"});
        addButton = new JButton("추가");
        editButton = new JButton("수정");
        deleteButton = new JButton("삭제");

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("할 일:"));
        inputPanel.add(inputField);
        inputPanel.add(new JLabel("우선순위:"));
        inputPanel.add(priorityBox);
        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);
        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"완료", "할 일", "중요도"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // 모든 셀 직접 수정 가능
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // 완료 체크박스
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new PriorityTableCellRenderer());
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // ✅ 중요도 열만 콤보박스로 제한
        String[] priorities = {"높음", "중간", "낮음"};
        JComboBox<String> comboBox = new JComboBox<>(priorities);
        TableColumn priorityColumn = table.getColumnModel().getColumn(2);
        priorityColumn.setCellEditor(new DefaultCellEditor(comboBox));

        loadTableFromFile();

        addButton.addActionListener(e -> {
            String task = inputField.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            if (!task.isEmpty()) {
                tableModel.addRow(new Object[]{false, task, priority});
                inputField.setText("");
                saveTableToFile();
            }
        });

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String task = inputField.getText().trim();
                String priority = (String) priorityBox.getSelectedItem();
                if (!task.isEmpty()) {
                    tableModel.setValueAt(task, row, 1);
                    tableModel.setValueAt(priority, row, 2);
                    inputField.setText("");
                    saveTableToFile();
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                tableModel.removeRow(row);
                saveTableToFile();
            }
        });

        tableModel.addTableModelListener(e -> saveTableToFile());

        setVisible(true);
    }

    private void saveTableToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Boolean done = (Boolean) tableModel.getValueAt(i, 0);
                String task = tableModel.getValueAt(i, 1).toString();
                String priority = tableModel.getValueAt(i, 2).toString();
                writer.write(done + "::" + task + "::" + priority);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTableFromFile() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("::");
                    if (parts.length == 3) {
                        Boolean done = Boolean.parseBoolean(parts[0]);
                        tableModel.addRow(new Object[]{done, parts[1], parts[2]});
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class PriorityTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 2) {
                String priority = value.toString();
                if (priority.equals("높음")) {
                    c.setForeground(Color.RED);
                } else if (priority.equals("중간")) {
                    c.setForeground(new Color(255, 165, 0));
                } else if (priority.equals("낮음")) {
                    c.setForeground(new Color(0, 128, 0));
                } else {
                    c.setForeground(Color.BLACK);
                }
            } else {
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Try1::new);
    }
}
