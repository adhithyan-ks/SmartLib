package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import com.college.library.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private MainApplication app;
    private AuthService authService;
    
    private JTextField userField;
    private JPasswordField passField;

    public LoginPanel(MainApplication app, AuthService authService) {
        this.app = app;
        this.authService = authService;
        setLayout(new GridBagLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Librarian Login"));
        
        formPanel.add(new JLabel("Username:"));
        userField = new JTextField(15);
        formPanel.add(userField);
        
        formPanel.add(new JLabel("Password:"));
        passField = new JPasswordField(15);
        formPanel.add(passField);
        
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> attemptLogin());
        
        formPanel.add(new JLabel("")); // spacer
        formPanel.add(loginBtn);
        
        add(formPanel);
    }
    
    private void attemptLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        
        try {
            Librarian lib = authService.login(username, password);
            app.setLoggedInUser(lib);
            userField.setText("");
            passField.setText("");
            app.navigateTo("DASHBOARD");
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
