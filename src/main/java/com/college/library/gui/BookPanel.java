package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
    private MainApplication app;
    private BookService bookService;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    private JButton deleteBtn;
    private JButton addBtn;

    public BookPanel(MainApplication app, BookService bookService) {
        this.app = app;
        this.bookService = bookService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Navigation & Action Header
        JButton backBtn = UITheme.createBackButton("Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));

        addBtn = UITheme.createPrimaryButton("Add New Book");
        deleteBtn = UITheme.createDangerButton("Delete Selected Book");

        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelectedBook());

        JPanel header = UITheme.createHeaderPanel(
                "Book Catalog & Inventory",
                "View, register, and manage book copies",
                backBtn,
                addBtn,
                deleteBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Table Container Card
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(new EmptyBorder(18, 24, 20, 24));

        UITheme.CardPanel tableCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        tableCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        String[] columns = {"Accession ID", "ISBN", "Title", "Author", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        UITheme.styleTable(bookTable);

        // Set optimal column proportions
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(280);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        contentWrapper.add(tableCard, BorderLayout.CENTER);
        add(contentWrapper, BorderLayout.CENTER);

        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(bookTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{bookTable}, deleteBtn, addBtn);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        try {
            List<Book> books = bookService.getAllBooks();
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                    b.getAccessionId(),
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getStatus()
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        JTextField idField = UITheme.createTextField(15);
        JTextField isbnField = UITheme.createTextField(15);
        JTextField titleField = UITheme.createTextField(15);
        JTextField authorField = UITheme.createTextField(15);
        
        JComboBox<com.college.library.model.Category> categoryBox = new JComboBox<>();
        UITheme.styleComboBox(categoryBox);
        try {
            List<com.college.library.model.Category> categories = bookService.getAllCategories();
            for (com.college.library.model.Category cat : categories) {
                categoryBox.addItem(cat);
            }
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(this, "Could not load categories.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 12, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Accession ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryBox);
        
        int option = JOptionPane.showConfirmDialog(this, formPanel, "Register New Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            Book b = new Book();
            b.setAccessionId(idField.getText().trim());
            b.setIsbn(isbnField.getText().trim());
            b.setTitle(titleField.getText().trim());
            b.setAuthor(authorField.getText().trim());
            b.setStatus("AVAILABLE");
            
            com.college.library.model.Category selectedCategory = (com.college.library.model.Category) categoryBox.getSelectedItem();
            if (selectedCategory != null) {
                b.setCategoryId(selectedCategory.getId());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid category.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                bookService.addBook(b);
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = (String) tableModel.getValueAt(selectedRow, 0);
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete book " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            try {
                bookService.deleteBook(id);
                refreshData();
                bookTable.clearSelection();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
