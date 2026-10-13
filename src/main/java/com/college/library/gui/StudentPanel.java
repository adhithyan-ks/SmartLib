package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import com.college.library.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private MainApplication app;
    private StudentService studentService;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JButton toggleStatusBtn;
    private JButton addBtn;
    private JButton deleteBtn;

    public StudentPanel(MainApplication app, StudentService studentService) {
        this.app = app;
        this.studentService = studentService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Header & Action Controls
        JButton backBtn = UITheme.createBackButton("Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));

        addBtn = UITheme.createPrimaryButton("Add New Student");
        toggleStatusBtn = UITheme.createWarningButton("Deactivate Student");
        deleteBtn = UITheme.createDangerButton("Delete Student");

        addBtn.addActionListener(e -> showAddDialog());
        toggleStatusBtn.addActionListener(e -> toggleSelectedStudentStatus());
        deleteBtn.addActionListener(e -> deleteSelectedStudent());

        JPanel header = UITheme.createHeaderPanel(
                "Student Directory & Records",
                "Manage student accounts, registration, and circulation eligibility",
                backBtn,
                addBtn,
                toggleStatusBtn,
                deleteBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Table Container Card
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(18, 24, 20, 24));

        UITheme.CardPanel tableCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] columns = {"KTU ID", "Name", "Branch", "Semester", "Batch", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        UITheme.styleTable(studentTable);

        // Proportional column sizing
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        studentTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateToggleStatusButton();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        contentWrapper.add(tableCard, BorderLayout.CENTER);
        add(contentWrapper, BorderLayout.CENTER);

        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(studentTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{studentTable}, deleteBtn, toggleStatusBtn, addBtn);
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
                    s.getPhone(),
                    s.getStatus() != null ? s.getStatus() : "ACTIVE"
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateToggleStatusButton();
    }

    private void showAddDialog() {
        JTextField idField = UITheme.createTextField(15);
        JTextField nameField = UITheme.createTextField(15);
        
        String[] branches = {"CSE", "ECE", "AEI", "EEE", "EL", "CE", "ME", "IE"};
        JComboBox<String> branchBox = new JComboBox<>(branches);
        UITheme.styleComboBox(branchBox);
        
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        JComboBox<String> semBox = new JComboBox<>(semesters);
        UITheme.styleComboBox(semBox);
        
        String[] batches = {"2020-2024", "2021-2025", "2022-2026", "2023-2027", "2024-2028", "2025-2029", "2026-2030"};
        JComboBox<String> batchBox = new JComboBox<>(batches);
        UITheme.styleComboBox(batchBox);
        batchBox.setSelectedItem("2023-2027");
        
        JTextField emailField = UITheme.createTextField(15);
        JTextField phoneField = UITheme.createTextField(15);

        // Auto-select batch and branch when valid KTU ID is typed
        Runnable updateAutoSelect = () -> {
            String currentKtuId = idField.getText().trim();
            if (currentKtuId.matches("^[A-Z]{3}[0-9]{2}[A-Z]{2}[0-9]{3}$")) {
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
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("KTU ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Branch:"));
        formPanel.add(branchBox);
        formPanel.add(new JLabel("Semester:"));
        formPanel.add(semBox);
        formPanel.add(new JLabel("Batch:"));
        formPanel.add(batchBox);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);
        
        int option = JOptionPane.showConfirmDialog(this, formPanel, "Register New Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
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
            if (!ktuId.matches("^[A-Z]{3}[0-9]{2}[A-Z]{2}[0-9]{3}$")) {
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
                JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleSelectedStudentStatus() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        try {
            if ("INACTIVE".equalsIgnoreCase(status)) {
                studentService.activateStudent(id);
                JOptionPane.showMessageDialog(this, "Student " + id + " activated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                studentService.deactivateStudent(id);
                JOptionPane.showMessageDialog(this, "Student " + id + " deactivated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshData();
            studentTable.clearSelection();
            updateToggleStatusButton();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateToggleStatusButton() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1 && selectedRow < tableModel.getRowCount()) {
            String status = (String) tableModel.getValueAt(selectedRow, 7);
            if ("INACTIVE".equalsIgnoreCase(status)) {
                toggleStatusBtn.setText("Activate Student");
            } else {
                toggleStatusBtn.setText("Deactivate Student");
            }
        } else {
            toggleStatusBtn.setText("Deactivate Student");
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                studentService.deleteStudent(id);
                JOptionPane.showMessageDialog(this, "Student " + id + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
                studentTable.clearSelection();
                updateToggleStatusButton();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete Student", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
