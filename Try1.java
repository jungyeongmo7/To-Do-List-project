
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Try1 extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JButton addButton, editButton, deleteButton;
    private final String FILE_NAME = "todo_table.txt";

    public Try1() {
        setTitle("To-Do List");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        addButton = new JButton("추가");
        editButton = new JButton("수정");
        deleteButton = new JButton("삭제");

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);
        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"완료", "할 일", "마감기한", "중요도"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new PriorityTableCellRenderer());
        
        //폭 설정
        table.getColumnModel().getColumn(0).setPreferredWidth(1);  // 완료 체크박스
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // 할 일
        table.getColumnModel().getColumn(2).setPreferredWidth(20);  // 마감기한
        table.getColumnModel().getColumn(3).setPreferredWidth(5);  // 중요도
        

        // 중요도 콤보박스 적용
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"높음", "중간", "낮음"});
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));

        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTableFromFile();

        addButton.addActionListener(e -> new AddTask(tableModel));

        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Object[] existing = new Object[]{
                    tableModel.getValueAt(row, 0), // 완료
                    tableModel.getValueAt(row, 1), // 할 일
                    tableModel.getValueAt(row, 2), // 마감기한
                    tableModel.getValueAt(row, 3)  // 중요도
                };
                new EditTask(tableModel, row, existing);
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
                writer.write(
                    tableModel.getValueAt(i, 0) + "::" +  // 완료
                    tableModel.getValueAt(i, 1) + "::" +  // 할 일
                    tableModel.getValueAt(i, 2) + "::" +  // 마감기한
                    tableModel.getValueAt(i, 3)           // 중요도
                );
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
                    if (parts.length == 4) {
                        Boolean done = Boolean.parseBoolean(parts[0]);
                        tableModel.addRow(new Object[]{done, parts[1], parts[2], parts[3]});
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
            if (column == 3) {
                String priority = value.toString();
                switch (priority) {
                    case "높음": c.setForeground(Color.RED); break;
                    case "중간": c.setForeground(new Color(255, 165, 0)); break;
                    case "낮음": c.setForeground(new Color(0, 128, 0)); break;
                    default: c.setForeground(Color.BLACK);
                }
            } else {
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }

    //AddTask 클래스 추가 팝업창
    class AddTask extends JFrame {
        protected JTextField taskField, yearField;
        protected JComboBox<String> monthBox, dayBox, importanceBox;
        protected JButton submitButton;

        private final String[] months = {
                " ", "1", "2", "3", "4", "5", "6",
                "7", "8", "9", "10", "11", "12"
            };
        private final String[] days = {
        		" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
        		};
        private final String[] priorities = {"높음", "중간", "낮음"};

        public AddTask(DefaultTableModel model) {
            setTitle("할 일 추가");
            setSize(350, 200);
            setLayout(new GridLayout(4, 1));

            // 1열 할 일
            JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row1.add(new JLabel("할 일:"));
            taskField = new JTextField(25);
            row1.add(taskField);
            add(row1);

            // 2열 마감기한 (연/월/일)
            JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row2.add(new JLabel("마감기한:"));
            yearField = new JTextField(4);
            row2.add(yearField);
            row2.add(new JLabel("년"));
            monthBox = new JComboBox<>(months);
            row2.add(monthBox);
            row2.add(new JLabel("월"));
            dayBox = new JComboBox<>(days);
            row2.add(dayBox);
            row2.add(new JLabel("일"));
            add(row2);

            // 3열 중요도
            JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row3.add(new JLabel("중요도:"));
            importanceBox = new JComboBox<>(priorities);
            row3.add(importanceBox);
            add(row3);

            // 4열 추가 버튼
            JPanel row4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            submitButton = new JButton("추가");
            row4.add(submitButton);
            add(row4);

            submitButton.addActionListener(e -> {
                String task = taskField.getText().trim();
                String year = yearField.getText().trim();
                String month = (String) monthBox.getSelectedItem();
                String day = (String) dayBox.getSelectedItem();
                String priority = (String) importanceBox.getSelectedItem();
                
                String deadline;

                if (task.isEmpty() && year.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "할 일과 마감기한을 입력하세요.");
                    return;
                }
                
                if (task.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "할 일을 입력하세요.");
                    return;
                }
                
                if (year.isEmpty()) {
                    deadline = " "; // 연도 없이 추가
                } else {
                    deadline = year + "/" + month + "/" + day;
                }

                model.addRow(new Object[]{false, task, deadline, priority});
                dispose();
            });

            setVisible(true);
        }
    }
    
    //EditTask 클래스 수정 팝업창
    class EditTask extends AddTask {
        public EditTask(DefaultTableModel model, int editRow, Object[] existingValues) {
            super(model);
            setTitle("할 일 수정");
            submitButton.setText("수정");

            // 기존 데이터 삽입
            taskField.setText((String) existingValues[1]);

            String[] dateParts = ((String) existingValues[2]).split("/");
            if (dateParts.length == 3) {
                yearField.setText(dateParts[0]);
                monthBox.setSelectedItem(dateParts[1]);
                dayBox.setSelectedItem(dateParts[2]);
            }

            importanceBox.setSelectedItem((String) existingValues[3]);

            // 버튼 액션 교체
            for (ActionListener al : submitButton.getActionListeners()) {
                submitButton.removeActionListener(al);
            }

            submitButton.addActionListener(e -> {
                String task = taskField.getText().trim();
                String year = yearField.getText().trim();
                String month = (String) monthBox.getSelectedItem();
                String day = (String) dayBox.getSelectedItem();
                String priority = (String) importanceBox.getSelectedItem();

                if (task.isEmpty() || year.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "할 일과 연도를 입력하세요.");
                    return;
                }

                String deadline = year + "/" + month + "/" + day;

                model.setValueAt(task, editRow, 1);
                model.setValueAt(deadline, editRow, 2);
                model.setValueAt(priority, editRow, 3);
                dispose();
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Try1::new);
    }
}