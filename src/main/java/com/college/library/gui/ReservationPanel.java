package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.service.ReservationService;
import com.college.library.service.BorrowService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReservationPanel extends JPanel {
    private MainApplication app;
    private ReservationService reservationService;
    private BorrowService borrowService;
    
    private JTextField studentIdField;
    private JTextField isbnField;
    private JTable reservationsTable;
    private DefaultTableModel tableModel;
    private JButton fulfilBtn;
    private JButton reserveBtn;

    public ReservationPanel(MainApplication app, ReservationService reservationService, BorrowService borrowService) {
        this.app = app;
        this.reservationService = reservationService;
        this.borrowService = borrowService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Header Navbar
        JButton backBtn = UITheme.createBackButton("Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));

        JPanel header = UITheme.createHeaderPanel(
                "Reservation Desk",
                "Place student book reservations and fulfil queued waitlists",
                backBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Main Content Container
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        
        // --- Form Card: Place Reservation ---
        UITheme.CardPanel formCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        JLabel formTitle = UITheme.createSectionHeader("Place New Reservation");
        formCard.add(formTitle, BorderLayout.NORTH);

        JPanel fieldsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        fieldsRow.setOpaque(false);

        fieldsRow.add(new JLabel("Student KTU ID:"));
        studentIdField = UITheme.createTextField(12);
        fieldsRow.add(studentIdField);

        fieldsRow.add(new JLabel("Book ISBN:"));
        isbnField = UITheme.createTextField(12);
        fieldsRow.add(isbnField);

        reserveBtn = UITheme.createPrimaryButton("Place Reservation");
        reserveBtn.addActionListener(e -> attemptReserve());
        fieldsRow.add(reserveBtn);

        formCard.add(fieldsRow, BorderLayout.CENTER);
        mainContentPanel.add(formCard);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Table Card: Existing Reservations ---
        UITheme.CardPanel tableCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel tableTitle = UITheme.createSectionHeader("Active Reservations Queue");
        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);

        fulfilBtn = UITheme.createSuccessButton("Fulfil Selected Reservation");
        fulfilBtn.addActionListener(e -> attemptFulfil());
        tableHeaderPanel.add(fulfilBtn, BorderLayout.EAST);
        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);

        String[] columns = {"Reservation ID", "Student KTU ID", "Student Name", "Book ISBN", "Book Title", "Request Date", "Queue Position", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationsTable = new JTable(tableModel);
        UITheme.styleTable(reservationsTable);

        reservationsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        reservationsTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        reservationsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        reservationsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        reservationsTable.getColumnModel().getColumn(5).setPreferredWidth(140);
        reservationsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        reservationsTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(tableCard);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(reservationsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{reservationsTable}, fulfilBtn, reserveBtn);
    }
    
    public void refreshData() {
        studentIdField.setText("");
        isbnField.setText("");
        tableModel.setRowCount(0);
        try {
            List<Object[]> rows = reservationService.getAllReservationsWithDetails();
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
            JOptionPane.showMessageDialog(this, "Please select a reservation to fulfil.", "Warning", JOptionPane.WARNING_MESSAGE);
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
