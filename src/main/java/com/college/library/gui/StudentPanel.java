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

        String[] columns = {"KTU ID", "Name", "Branch", "Semester"};
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
                    s.getSemester()
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField branchField = new JTextField();
        JTextField semField = new JTextField();
        
        Object[] message = {
            "KTU ID:", idField,
            "Name:", nameField,
            "Branch:", branchField,
            "Semester:", semField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Student", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Student s = new Student();
            s.setKtuId(idField.getText().trim());
            s.setName(nameField.getText().trim());
            s.setBranch(branchField.getText().trim());
            try {
                s.setSemester(Integer.parseInt(semField.getText().trim()));
            } catch (NumberFormatException e) {
                s.setSemester(1);
            }
            
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
