package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookPanel extends JPanel {
    private MainApplication app;
    private BookService bookService;
    private DefaultTableModel tableModel;
    private JTable bookTable;

    public BookPanel(MainApplication app, BookService bookService) {
        this.app = app;
        this.bookService = bookService;
        setLayout(new BorderLayout());

        // Top Navigation
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center Table
        String[] columns = {"Accession ID", "ISBN", "Title", "Author", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Book");
        JButton deleteBtn = new JButton("Delete Book");
        
        addBtn.addActionListener(e -> showAddDialog());
        deleteBtn.addActionListener(e -> deleteSelectedBook());
        
        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(bookTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{bookTable}, deleteBtn);
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
        JTextField idField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        
        JComboBox<com.college.library.model.Category> categoryBox = new JComboBox<>();
        try {
            List<com.college.library.model.Category> categories = bookService.getAllCategories();
            for (com.college.library.model.Category cat : categories) {
                categoryBox.addItem(cat);
            }
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(this, "Could not load categories.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        Object[] message = {
            "Accession ID:", idField,
            "ISBN:", isbnField,
            "Title:", titleField,
            "Author:", authorField,
            "Category:", categoryBox
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Book", JOptionPane.OK_CANCEL_OPTION);
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
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                refreshData();
            } catch (LibraryException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
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
