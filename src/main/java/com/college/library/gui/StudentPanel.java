package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import com.college.library.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private MainApplication app;
    private StudentService studentService;
    private DefaultTableModel tableModel;
    private JTable studentTable;

    public StudentPanel(MainApplication app, StudentService studentService) {
        this.app = app;
        this.studentService = studentService;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"KTU ID", "Name", "Branch", "Semester", "Batch", "Email", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Student");
        JButton deleteBtn = new JButton("Delete Student");
        
        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        
        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(studentTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{studentTable}, deleteBtn);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Student> students = studentService.getAllStudents();
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                    s.getKtuId(),
                    s.getName(),
                    s.getBranch(),
                    s.getSemester(),
                    s.getBatch(),
                    s.getEmail(),
                    s.getPhone()
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        
        String[] branches = {"CSE", "ECE", "AEI", "EEE", "EL", "CE", "ME", "IE"};
        JComboBox<String> branchBox = new JComboBox<>(branches);
        
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        JComboBox<String> semBox = new JComboBox<>(semesters);
        
        String[] batches = {"2020-2024", "2021-2025", "2022-2026", "2023-2027", "2024-2028", "2025-2029", "2026-2030"};
        JComboBox<String> batchBox = new JComboBox<>(batches);
        batchBox.setSelectedItem("2023-2027");
        
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();

        // Auto-select batch and branch when valid KTU ID is typed
        Runnable updateAutoSelect = () -> {
            String currentKtuId = idField.getText().trim();
            if (currentKtuId.matches("^TVE(22|23|24|25|26)[A-Z]{2}[0-9]{3}$")) {
                String yearCode = currentKtuId.substring(3, 5);
                int startYear = 2000 + Integer.parseInt(yearCode);
                String batch = startYear + "-" + (startYear + 4);
                batchBox.setSelectedItem(batch);

                String branchCode = currentKtuId.substring(5, 7);
                switch (branchCode) {
                    case "CS": branchBox.setSelectedItem("CSE"); break;
                    case "EC": branchBox.setSelectedItem("ECE"); break;
                    case "AE": branchBox.setSelectedItem("AEI"); break;
                    case "EE": branchBox.setSelectedItem("EEE"); break;
                    case "EL": branchBox.setSelectedItem("EL"); break;
                    case "CE": branchBox.setSelectedItem("CE"); break;
                    case "ME": branchBox.setSelectedItem("ME"); break;
                    case "IE": branchBox.setSelectedItem("IE"); break;
                }
            }
        };

        idField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateAutoSelect.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateAutoSelect.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateAutoSelect.run(); }
        });
        
        Object[] message = {
            "KTU ID:", idField,
            "Name:", nameField,
            "Branch:", branchBox,
            "Semester:", semBox,
            "Batch:", batchBox,
            "Email:", emailField,
            "Phone:", phoneField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String ktuId = idField.getText().trim();
            String name = nameField.getText().trim();
            String branch = branchBox.getSelectedItem() != null ? branchBox.getSelectedItem().toString().trim() : "";
            String semesterStr = semBox.getSelectedItem() != null ? semBox.getSelectedItem().toString().trim() : "1";
            String batch = batchBox.getSelectedItem() != null ? batchBox.getSelectedItem().toString().trim() : "";
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (name.isEmpty() || ktuId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "KTU ID and Name are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!ktuId.matches("^TVE(22|23|24|25|26)[A-Z]{2}[0-9]{3}$")) {
                JOptionPane.showMessageDialog(this, "Invalid KTU ID format.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (branch.isEmpty() || semesterStr.isEmpty() || batch.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the details.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int semester = 1;
            try {
                semester = Integer.parseInt(semesterStr);
            } catch (NumberFormatException ignored) {
            }

            Student s = new Student(ktuId, name, branch, semester, batch, email, phone);
            
            try {
                studentService.addStudent(s);
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                refreshData();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }
        
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                studentService.deleteStudent(id);
                refreshData();
                studentTable.clearSelection();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
