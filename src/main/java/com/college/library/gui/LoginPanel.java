package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import com.college.library.service.AuthService;

import javax.swing.*;
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

    public LoginPanel(MainApplication app, AuthService authService) {
        this.app = app;
        this.authService = authService;
        setLayout(new GridBagLayout());
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        
        // --- Librarian Login Panel ---
        JPanel libPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        libPanel.setBorder(BorderFactory.createTitledBorder("Librarian Login"));
        
        libPanel.add(new JLabel("Username:"));
        libUserField = new JTextField(15);
        libPanel.add(libUserField);
        
        libPanel.add(new JLabel("Password:"));
        libPassField = new JPasswordField(15);
        libPanel.add(libPassField);
        
        JButton libLoginBtn = new JButton("Login as Librarian");
        libLoginBtn.addActionListener(e -> attemptLibrarianLogin());
        
        libPanel.add(new JLabel("")); // spacer
        libPanel.add(libLoginBtn);
        
        // --- Student Login Panel ---
        JPanel stuPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        stuPanel.setBorder(BorderFactory.createTitledBorder("Student Login"));
        
        stuPanel.add(new JLabel("KTU ID:"));
        stuIdField = new JTextField(15);
        stuPanel.add(stuIdField);
        
        stuPanel.add(new JLabel("Password:"));
        stuPassField = new JPasswordField(15);
        stuPanel.add(stuPassField);
        
        JButton stuLoginBtn = new JButton("Login as Student");
        stuLoginBtn.addActionListener(e -> attemptStudentLogin());
        
        JButton registerBtn = new JButton("New Student? Create Account");
        registerBtn.addActionListener(e -> showStudentRegistrationForm());
        
        stuPanel.add(new JLabel("")); // spacer
        stuPanel.add(stuLoginBtn);
        
        stuPanel.add(new JLabel("")); // spacer
        stuPanel.add(registerBtn);
        
        container.add(libPanel);
        container.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing between panels
        container.add(stuPanel);
        
        add(container);
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
        JTextField nameField = new JTextField(15);
        JTextField ktuIdField = new JTextField(15);
        JTextField branchField = new JTextField(15);
        JTextField semesterField = new JTextField(15);
        JTextField batchField = new JTextField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        
        // If they already typed a valid KTU ID, pre-fill it
        String currentKtuId = stuIdField.getText().trim();
        if (currentKtuId.matches("^TVE(22|23|24|25|26)[A-Z]{2}[0-9]{3}$")) {
            ktuIdField.setText(currentKtuId);
        }

        JPasswordField passField = new JPasswordField(15);
        JPasswordField confirmPassField = new JPasswordField(15);
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("KTU ID:"));
        panel.add(ktuIdField);

        panel.add(new JLabel("Branch:"));
        panel.add(branchField);
        panel.add(new JLabel("Semester:"));
        panel.add(semesterField);
        panel.add(new JLabel("Batch:"));
        panel.add(batchField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPassField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Student Registration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String ktuId = ktuIdField.getText().trim();
            String branch = branchField.getText().trim();
            String semester = semesterField.getText().trim();
            String batch = batchField.getText().trim();
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
