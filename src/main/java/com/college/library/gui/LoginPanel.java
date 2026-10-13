package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import com.college.library.service.AuthService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainApplication app;
    private AuthService authService;
    
    // Librarian fields
    private JTextField libUserField;
    private JPasswordField libPassField;
    
    // Student fields
    private JTextField stuIdField;
    private JPasswordField stuPassField;

    private CardLayout formCardLayout;
    private JPanel formCardsPanel;
    private JButton studentTabBtn;
    private JButton librarianTabBtn;

    public LoginPanel(MainApplication app, AuthService authService) {
        this.app = app;
        this.authService = authService;
        
        setLayout(new GridBagLayout());
        setBackground(UITheme.BG_APP);

        // Main Login Card
        UITheme.CardPanel card = new UITheme.CardPanel(new BorderLayout(0, 20));
        card.setPreferredSize(new Dimension(440, 520));
        card.setCornerRadius(14);
        card.setBorder(new EmptyBorder(32, 32, 32, 32));

        // 1. Header (Brand, Logo, Title)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel logoBadge = new JLabel("SmartLib");
        logoBadge.setFont(UITheme.FONT_HERO);
        logoBadge.setForeground(UITheme.PRIMARY);
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLbl = new JLabel("College Library Management System");
        subtitleLbl.setFont(UITheme.FONT_SUBTITLE);
        subtitleLbl.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoBadge);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitleLbl);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // 2. Segmented Role Selector
        JPanel selectorWrapper = new JPanel(new GridLayout(1, 2, 6, 0));
        selectorWrapper.setBackground(UITheme.SECONDARY_BG);
        selectorWrapper.setBorder(new CompoundBorder(
                new UITheme.RoundedBorder(UITheme.BORDER, 8, 1),
                new EmptyBorder(4, 4, 4, 4)
        ));

        studentTabBtn = new JButton("Student Portal");
        librarianTabBtn = new JButton("Librarian / Staff");
        styleSegmentedButton(studentTabBtn, true);
        styleSegmentedButton(librarianTabBtn, false);

        studentTabBtn.addActionListener(e -> switchRole(true));
        librarianTabBtn.addActionListener(e -> switchRole(false));

        selectorWrapper.add(studentTabBtn);
        selectorWrapper.add(librarianTabBtn);
        headerPanel.add(selectorWrapper);

        card.add(headerPanel, BorderLayout.NORTH);

        // 3. Forms Container with CardLayout
        formCardLayout = new CardLayout();
        formCardsPanel = new JPanel(formCardLayout);
        formCardsPanel.setOpaque(false);

        // --- Student Form ---
        JPanel stuForm = createStudentForm();
        // --- Librarian Form ---
        JPanel libForm = createLibrarianForm();

        formCardsPanel.add(stuForm, "STUDENT");
        formCardsPanel.add(libForm, "LIBRARIAN");

        card.add(formCardsPanel, BorderLayout.CENTER);

        add(card);
    }

    private void styleSegmentedButton(JButton btn, boolean active) {
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 30));
        if (active) {
            btn.setBackground(UITheme.BG_CARD);
            btn.setForeground(UITheme.PRIMARY);
            btn.setOpaque(true);
        } else {
            btn.setBackground(UITheme.SECONDARY_BG);
            btn.setForeground(UITheme.TEXT_SECONDARY);
            btn.setOpaque(false);
        }
    }

    private void switchRole(boolean isStudent) {
        styleSegmentedButton(studentTabBtn, isStudent);
        styleSegmentedButton(librarianTabBtn, !isStudent);
        formCardLayout.show(formCardsPanel, isStudent ? "STUDENT" : "LIBRARIAN");
    }

    private JPanel createStudentForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // KTU ID
        JLabel idLbl = new JLabel("KTU ID");
        idLbl.setFont(UITheme.FONT_BODY_BOLD);
        idLbl.setForeground(UITheme.TEXT_PRIMARY);
        idLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        stuIdField = UITheme.createTextField(15);
        stuIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        stuIdField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel idHint = new JLabel("Format: TVE23CS001");
        idHint.setFont(UITheme.FONT_SMALL);
        idHint.setForeground(UITheme.TEXT_MUTED);
        idHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UITheme.FONT_BODY_BOLD);
        passLbl.setForeground(UITheme.TEXT_PRIMARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        stuPassField = UITheme.createPasswordField(15);
        stuPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        stuPassField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Action
        JButton stuLoginBtn = UITheme.createPrimaryButton("Sign In as Student");
        stuLoginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        stuLoginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        stuLoginBtn.addActionListener(e -> attemptStudentLogin());

        // Press Enter to login
        stuIdField.addActionListener(e -> attemptStudentLogin());
        stuPassField.addActionListener(e -> attemptStudentLogin());

        // Register Action
        JButton registerBtn = UITheme.createSecondaryButton("New Student? Create Account");
        registerBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.addActionListener(e -> showStudentRegistrationForm());

        panel.add(idLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(stuIdField);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(idHint);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));

        panel.add(passLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(stuPassField);
        panel.add(Box.createRigidArea(new Dimension(0, 22)));

        panel.add(stuLoginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(registerBtn);

        return panel;
    }

    private JPanel createLibrarianForm() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Username
        JLabel userLbl = new JLabel("Staff Username");
        userLbl.setFont(UITheme.FONT_BODY_BOLD);
        userLbl.setForeground(UITheme.TEXT_PRIMARY);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        libUserField = UITheme.createTextField(15);
        libUserField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        libUserField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(UITheme.FONT_BODY_BOLD);
        passLbl.setForeground(UITheme.TEXT_PRIMARY);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        libPassField = UITheme.createPasswordField(15);
        libPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        libPassField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Action
        JButton libLoginBtn = UITheme.createPrimaryButton("Sign In as Librarian");
        libLoginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        libLoginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        libLoginBtn.addActionListener(e -> attemptLibrarianLogin());

        // Press Enter to login
        libUserField.addActionListener(e -> attemptLibrarianLogin());
        libPassField.addActionListener(e -> attemptLibrarianLogin());

        panel.add(userLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(libUserField);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        panel.add(passLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(libPassField);
        panel.add(Box.createRigidArea(new Dimension(0, 26)));

        panel.add(libLoginBtn);

        return panel;
    }
    
    private void attemptLibrarianLogin() {
        String username = libUserField.getText().trim();
        String password = new String(libPassField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            libUserField.setText("");
            libPassField.setText("");
            return;
        }

        try {
            Librarian lib = authService.login(username, password);
            app.setLoggedInStudent(null); // Clear student session
            app.setLoggedInUser(lib);
            app.navigateTo("DASHBOARD");
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            libUserField.setText("");
            libPassField.setText("");
        }
    }

    private void attemptStudentLogin() {
        String ktuId = stuIdField.getText().trim();
        String password = new String(stuPassField.getPassword());
        
        if (ktuId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both KTU ID and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            stuIdField.setText("");
            stuPassField.setText("");
            return;
        }

        if (!ktuId.matches("^TVE(22|23|24|25|26)[A-Z]{2}[0-9]{3}$")) {
            JOptionPane.showMessageDialog(this, "Invalid KTU ID format.", "Login Error", JOptionPane.ERROR_MESSAGE);
            stuIdField.setText("");
            stuPassField.setText("");
            return;
        }

        if (!authService.isStudentRegistered(ktuId)) {
            JOptionPane.showMessageDialog(this, "Account not found or unregistered. Please create an account.", "Login Error", JOptionPane.ERROR_MESSAGE);
            stuIdField.setText("");
            stuPassField.setText("");
            return;
        }
        
        try {
            com.college.library.model.Student student = authService.loginStudent(ktuId, password);
            app.setLoggedInUser(null); // Clear librarian session
            app.setLoggedInStudent(student);
            app.navigateTo("STUDENT_DASHBOARD");
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            stuIdField.setText("");
            stuPassField.setText("");
        }
    }
    
    private void showStudentRegistrationForm() {
        JTextField nameField = UITheme.createTextField(15);
        JTextField ktuIdField = UITheme.createTextField(15);
        
        String[] branches = {"CSE", "ECE", "AEI", "EEE", "EL", "CE", "ME", "IE"};
        JComboBox<String> branchBox = new JComboBox<>(branches);
        UITheme.styleComboBox(branchBox);
        
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        JComboBox<String> semesterBox = new JComboBox<>(semesters);
        UITheme.styleComboBox(semesterBox);
        
        String[] batches = {"2020-2024", "2021-2025", "2022-2026", "2023-2027", "2024-2028", "2025-2029", "2026-2030"};
        JComboBox<String> batchBox = new JComboBox<>(batches);
        UITheme.styleComboBox(batchBox);
        batchBox.setSelectedItem("2023-2027");
        
        JTextField emailField = UITheme.createTextField(15);
        JTextField phoneField = UITheme.createTextField(15);
        
        // If they already typed a valid KTU ID, pre-fill it and auto-select branch/batch
        String currentKtuId = stuIdField.getText().trim();
        if (currentKtuId.matches("^TVE(22|23|24|25|26)[A-Z]{2}[0-9]{3}$")) {
            ktuIdField.setText(currentKtuId);
            
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

        JPasswordField passField = UITheme.createPasswordField(15);
        JPasswordField confirmPassField = UITheme.createPasswordField(15);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 12, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("KTU ID:"));
        panel.add(ktuIdField);

        panel.add(new JLabel("Branch:"));
        panel.add(branchBox);
        panel.add(new JLabel("Semester:"));
        panel.add(semesterBox);
        panel.add(new JLabel("Batch:"));
        panel.add(batchBox);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Student Account Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String ktuId = ktuIdField.getText().trim();
            String branch = branchBox.getSelectedItem() != null ? branchBox.getSelectedItem().toString().trim() : "";
            String semester = semesterBox.getSelectedItem() != null ? semesterBox.getSelectedItem().toString().trim() : "";
            String batch = batchBox.getSelectedItem() != null ? batchBox.getSelectedItem().toString().trim() : "";
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());
            
            if (name.isEmpty() || ktuId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!ktuId.matches("^TVE(22|23|24|25|26)[A-Z]{2}[0-9]{3}$")) {
                JOptionPane.showMessageDialog(this, "Invalid KTU ID format.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (branch.isEmpty() || semester.isEmpty() || batch.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the details.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                authService.registerStudent(ktuId, name, branch, semester, batch, email, phone, password);
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                stuIdField.setText(ktuId);
                stuPassField.setText("");
                switchRole(true);
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void resetFields() {
        libUserField.setText("");
        libPassField.setText("");
        stuIdField.setText("");
        stuPassField.setText("");
    }
}
