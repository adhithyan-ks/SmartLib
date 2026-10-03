package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.service.ReservationService;

import com.college.library.service.BorrowService;

import javax.swing.*;
import java.awt.*;

public class ReservationPanel extends JPanel {
    private MainApplication app;
    private ReservationService reservationService;
    private BorrowService borrowService;
    
    private JTextField studentIdField;
    private JTextField isbnField;
    private JTable reservationsTable;
    private javax.swing.table.DefaultTableModel tableModel;

    public ReservationPanel(MainApplication app, ReservationService reservationService, BorrowService borrowService) {
        this.app = app;
        this.reservationService = reservationService;
        this.borrowService = borrowService;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        
        // Form Panel
        JPanel formWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Reserve Book"),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        formPanel.add(new JLabel("Student KTU ID:"));
        studentIdField = new JTextField(15);
        formPanel.add(studentIdField);
        
        formPanel.add(new JLabel("Book ISBN:"));
        isbnField = new JTextField(15);
        formPanel.add(isbnField);
        
        formPanel.add(new JLabel("")); // spacer
        JButton reserveBtn = new JButton("Place Reservation");
        reserveBtn.addActionListener(e -> attemptReserve());
        formPanel.add(reserveBtn);
        
        formWrapper.add(formPanel);
        centerPanel.add(formWrapper, BorderLayout.NORTH);
        
        // Table Panel
        String[] columns = {"Reservation ID", "Student KTU ID", "Student Name", "Book ISBN", "Book Title", "Request Date", "Queue Position", "Status"};
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(tableModel);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Existing Reservations"));
        tablePanel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton fulfilBtn = new JButton("Fulfil Reservation");
        fulfilBtn.addActionListener(e -> attemptFulfil());
        buttonPanel.add(fulfilBtn);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);
        
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(reservationsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{reservationsTable}, fulfilBtn);
    }
    
    public void refreshData() {
        studentIdField.setText("");
        isbnField.setText("");
        tableModel.setRowCount(0);
        try {
            java.util.List<Object[]> rows = reservationService.getAllReservationsWithDetails();
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load reservations: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptReserve() {
        try {
            String message = reservationService.addReservation(studentIdField.getText().trim(), isbnField.getText().trim());
            JOptionPane.showMessageDialog(this, message, "Reservation Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptFulfil() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to fulfil.");
            return;
        }
        
        try {
            int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            
            String msg = borrowService.fulfilReservation(reservationId, librarianId);
            JOptionPane.showMessageDialog(this, msg, "Fulfilment Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            reservationsTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
