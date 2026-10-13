# BookDAO.java
**Path:** `src/main/java/com/college/library/dao/BookDAO.java`

```java
package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BookDAO extends CrudDAO<Book, String> {
    void updateStatus(String accessionId, String status, Connection conn) throws SQLException;
    int updateStatusIfCondition(String accessionId, String newStatus, String expectedOldStatus, Connection conn) throws SQLException;
    List<Category> getAllCategories() throws LibraryException;
    List<Book> findByIsbn(String isbn) throws LibraryException;
}
```

# BookDAOImpl.java
**Path:** `src/main/java/com/college/library/dao/impl/BookDAOImpl.java`

```java
package com.college.library.dao.impl;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Category;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {

    @Override
    public void create(Book book) throws LibraryException {
        String sql = "INSERT INTO books (accession_id, isbn, title, author, publisher, edition, category_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getAccessionId());
            pstmt.setString(2, book.getIsbn());
            pstmt.setString(3, book.getTitle());
            pstmt.setString(4, book.getAuthor());
            pstmt.setString(5, book.getPublisher());
            pstmt.setString(6, book.getEdition());
            pstmt.setInt(7, book.getCategoryId());
            pstmt.setString(8, book.getStatus());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error creating book: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Book> findById(String id) throws LibraryException {
        String sql = "SELECT * FROM books WHERE accession_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding book by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() throws LibraryException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving all books: " + e.getMessage(), e);
        }
        return books;
    }

    @Override
    public void update(Book book) throws LibraryException {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, publisher=?, edition=?, category_id=?, status=? WHERE accession_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setString(5, book.getEdition());
            pstmt.setInt(6, book.getCategoryId());
            pstmt.setString(7, book.getStatus());
            pstmt.setString(8, book.getAccessionId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating book: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) throws LibraryException {
        String sql = "DELETE FROM books WHERE accession_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting book: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateStatus(String accessionId, String status, Connection conn) throws SQLException {
        String sql = "UPDATE books SET status=? WHERE accession_id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, accessionId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public int updateStatusIfCondition(String accessionId, String newStatus, String expectedOldStatus, Connection conn) throws SQLException {
        String sql = "UPDATE books SET status=? WHERE accession_id=? AND status=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, accessionId);
            pstmt.setString(3, expectedOldStatus);
            return pstmt.executeUpdate();
        }
    }
    
    @Override
    public List<Category> getAllCategories() throws LibraryException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving categories: " + e.getMessage(), e);
        }
        return categories;
    }
    
    @Override
    public List<Book> findByIsbn(String isbn) throws LibraryException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving books by ISBN: " + e.getMessage(), e);
        }
        return books;
    }
    
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
            rs.getString("accession_id"),
            rs.getString("isbn"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("publisher"),
            rs.getString("edition"),
            rs.getInt("category_id"),
            rs.getString("status")
        );
    }
}
```

# BookService.java
**Path:** `src/main/java/com/college/library/service/BookService.java`

```java
package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Category;

import java.util.*;

public class BookService {
    private static final Set<String> VALID_STATUSES = Set.of("AVAILABLE", "ISSUED", "LOST", "DAMAGED");
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void addBook(Book book) throws LibraryException {
        validateBook(book);
        if (bookDAO.findById(book.getAccessionId()).isPresent()) {
            throw new LibraryException("Book with accession ID " + book.getAccessionId() + " already exists.");
        }
        bookDAO.create(book);
    }

    public void updateBook(Book book) throws LibraryException {
        validateBook(book);
        if (bookDAO.findById(book.getAccessionId()).isEmpty()) {
            throw new LibraryException("Book not found for update.");
        }
        bookDAO.update(book);
    }

    public void deleteBook(String accessionId) throws LibraryException {
        if (accessionId == null || accessionId.trim().isEmpty()) {
            throw new LibraryException("Accession ID cannot be empty.");
        }
        bookDAO.delete(accessionId);
    }

    public Book getBook(String accessionId) throws LibraryException {
        return bookDAO.findById(accessionId)
                .orElseThrow(() -> new LibraryException("Book not found."));
    }

    public List<Book> getAllBooks() throws LibraryException {
        return bookDAO.findAll();
    }

    public List<Category> getAllCategories() throws LibraryException {
        return bookDAO.getAllCategories();
    }

    private void validateBook(Book book) throws LibraryException {
        if (book == null) throw new LibraryException("Book cannot be null.");
        if (book.getAccessionId() == null || book.getAccessionId().trim().isEmpty()) {
            throw new LibraryException("Book accession ID is required.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new LibraryException("Book title is required.");
        }
        if (book.getStatus() == null || !VALID_STATUSES.contains(book.getStatus())) {
            throw new LibraryException("Invalid book status.");
        }
    }

    public List<Object[]> searchAvailableBooks(String query) throws LibraryException {
        List<Book> allBooks = bookDAO.findAll();
        List<Category> categories = bookDAO.getAllCategories();
        
        Map<Integer, String> catMap = new HashMap<>();
        for (Category c : categories) {
            catMap.put(c.getId(), c.getName());
        }
        
        Map<String, Object[]> grouped = new LinkedHashMap<>();
        String lowerQuery = query == null ? "" : query.toLowerCase().trim();
        
        for (Book b : allBooks) {
            boolean matches = lowerQuery.isEmpty() || 
                              b.getTitle().toLowerCase().contains(lowerQuery) ||
                              b.getAuthor().toLowerCase().contains(lowerQuery) ||
                              b.getIsbn().toLowerCase().contains(lowerQuery);
            if (!matches) continue;
            
            Object[] row = grouped.computeIfAbsent(b.getIsbn(), isbn -> new Object[]{
                isbn,
                b.getTitle(),
                b.getAuthor(),
                catMap.getOrDefault(b.getCategoryId(), "Unknown"),
                0
            });
            if ("AVAILABLE".equals(b.getStatus())) {
                row[4] = (Integer) row[4] + 1;
            }
        }
        return new ArrayList<>(grouped.values());
    }
    
    public String getFirstAvailableBookId(String isbn) throws LibraryException {
        return bookDAO.findByIsbn(isbn).stream()
                .filter(b -> "AVAILABLE".equals(b.getStatus()))
                .map(Book::getAccessionId)
                .findFirst()
                .orElse(null);
    }
}
```

# BookPanel.java
**Path:** `src/main/java/com/college/library/gui/BookPanel.java`

```java
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
        deleteBtn = UITheme.createDangerButton("Delete Book");

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
```

# StudentDAO.java
**Path:** `src/main/java/com/college/library/dao/StudentDAO.java`

```java
package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import java.util.Collections;
import java.util.List;

public interface StudentDAO extends CrudDAO<Student, String> {
    default boolean hasRelatedRecords(String ktuId) throws LibraryException {
        return false;
    }

    default List<String> getRelatedRecordsSummary(String ktuId) throws LibraryException {
        return Collections.emptyList();
    }

    default void updateStatus(String ktuId, String status) throws LibraryException {}
}
```

# StudentDAOImpl.java
**Path:** `src/main/java/com/college/library/dao/impl/StudentDAOImpl.java`

```java
package com.college.library.dao.impl;

import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public void create(Student student) throws LibraryException {
        String status = student.getStatus() != null ? student.getStatus() : "ACTIVE";
        String sql = "INSERT INTO students (ktu_id, password_hash, name, branch, semester, batch, email, phone, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getKtuId());
            pstmt.setString(2, student.getPasswordHash());
            pstmt.setString(3, student.getName());
            pstmt.setString(4, student.getBranch());
            pstmt.setInt(5, student.getSemester());
            pstmt.setString(6, student.getBatch());
            pstmt.setString(7, student.getEmail());
            pstmt.setString(8, student.getPhone());
            pstmt.setString(9, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error creating student: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findById(String ktuId) throws LibraryException {
        String sql = "SELECT * FROM students WHERE ktu_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ktuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding student by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() throws LibraryException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving all students: " + e.getMessage(), e);
        }
        return students;
    }

    @Override
    public void update(Student student) throws LibraryException {
        String status = student.getStatus() != null ? student.getStatus() : "ACTIVE";
        String sqlWithStatus = "UPDATE students SET password_hash=?, name=?, branch=?, semester=?, batch=?, email=?, phone=?, status=? WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlWithStatus)) {
            
            pstmt.setString(1, student.getPasswordHash());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getBranch());
            pstmt.setInt(4, student.getSemester());
            pstmt.setString(5, student.getBatch());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhone());
            pstmt.setString(8, status);
            pstmt.setString(9, student.getKtuId());
            
            pstmt.executeUpdate();
            return;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("status")) {
                String sqlWithout = "UPDATE students SET password_hash=?, name=?, branch=?, semester=?, batch=?, email=?, phone=? WHERE ktu_id=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sqlWithout)) {
                    pstmt.setString(1, student.getPasswordHash());
                    pstmt.setString(2, student.getName());
                    pstmt.setString(3, student.getBranch());
                    pstmt.setInt(4, student.getSemester());
                    pstmt.setString(5, student.getBatch());
                    pstmt.setString(6, student.getEmail());
                    pstmt.setString(7, student.getPhone());
                    pstmt.setString(8, student.getKtuId());
                    pstmt.executeUpdate();
                    return;
                } catch (SQLException ex) {
                    throw new LibraryException("Error updating student: " + ex.getMessage(), ex);
                }
            }
            throw new LibraryException("Error updating student: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(String ktuId, String status) throws LibraryException {
        String sql = "UPDATE students SET status=? WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, ktuId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error updating student status: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String ktuId) throws LibraryException {
        String sql = "DELETE FROM students WHERE ktu_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ktuId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451 || "23000".equals(e.getSQLState()) || 
                (e.getMessage() != null && e.getMessage().toLowerCase().contains("foreign key"))) {
                throw new LibraryException("Cannot delete student: Related records (borrow transactions, borrow requests, or reservations) exist in the system. Deletion is blocked to preserve data integrity.", e);
            }
            throw new LibraryException("Error deleting student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasRelatedRecords(String ktuId) throws LibraryException {
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM borrow_transactions WHERE student_id = ?) + " +
                     "(SELECT COUNT(*) FROM borrow_requests WHERE student_id = ?) + " +
                     "(SELECT COUNT(*) FROM reservations WHERE student_id = ?) AS total_count";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ktuId);
            pstmt.setString(2, ktuId);
            pstmt.setString(3, ktuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking related records for student: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<String> getRelatedRecordsSummary(String ktuId) throws LibraryException {
        List<String> summaries = new ArrayList<>();
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM borrow_transactions WHERE student_id = ?) AS total_tx, " +
                     "(SELECT COUNT(*) FROM borrow_transactions WHERE student_id = ? AND status = 'ACTIVE') AS active_tx, " +
                     "(SELECT COUNT(*) FROM borrow_requests WHERE student_id = ?) AS total_req, " +
                     "(SELECT COUNT(*) FROM borrow_requests WHERE student_id = ? AND status = 'PENDING') AS pending_req, " +
                     "(SELECT COUNT(*) FROM reservations WHERE student_id = ?) AS total_res, " +
                     "(SELECT COUNT(*) FROM reservations WHERE student_id = ? AND status = 'PENDING') AS pending_res";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
                pstmt.setString(i, ktuId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int totalTx = rs.getInt("total_tx");
                    int activeTx = rs.getInt("active_tx");
                    int completedTx = totalTx - activeTx;
                    if (activeTx > 0 && completedTx > 0) {
                        summaries.add(activeTx + " active borrowed book(s) and " + completedTx + " borrowing history record(s)");
                    } else if (activeTx > 0) {
                        summaries.add(activeTx + " active borrowed book(s)");
                    } else if (completedTx > 0) {
                        summaries.add(completedTx + " borrowing history record(s)");
                    }

                    int totalReq = rs.getInt("total_req");
                    int pendingReq = rs.getInt("pending_req");
                    int pastReq = totalReq - pendingReq;
                    if (pendingReq > 0 && pastReq > 0) {
                        summaries.add(pendingReq + " pending borrow request(s) and " + pastReq + " past request(s)");
                    } else if (pendingReq > 0) {
                        summaries.add(pendingReq + " pending borrow request(s)");
                    } else if (totalReq > 0) {
                        summaries.add(totalReq + " borrow request(s)");
                    }

                    int totalRes = rs.getInt("total_res");
                    int pendingRes = rs.getInt("pending_res");
                    int pastRes = totalRes - pendingRes;
                    if (pendingRes > 0 && pastRes > 0) {
                        summaries.add(pendingRes + " pending reservation(s) and " + pastRes + " past reservation(s)");
                    } else if (pendingRes > 0) {
                        summaries.add(pendingRes + " pending reservation(s)");
                    } else if (totalRes > 0) {
                        summaries.add(totalRes + " reservation(s)");
                    }
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error checking related records summary for student: " + e.getMessage(), e);
        }
        return summaries;
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        // Handle password_hash column safely in case it doesn't exist yet or is null
        String passwordHash = null;
        try {
            passwordHash = rs.getString("password_hash");
        } catch (SQLException ignored) {
            // Column might not exist if migration hasn't run yet
        }
        
        String status = "ACTIVE";
        try {
            String dbStatus = rs.getString("status");
            if (dbStatus != null && !dbStatus.trim().isEmpty()) {
                status = dbStatus;
            }
        } catch (SQLException ignored) {
            // Column might not exist if migration hasn't run yet
        }
        
        return new Student(
            rs.getString("ktu_id"),
            passwordHash,
            rs.getString("name"),
            rs.getString("branch"),
            rs.getInt("semester"),
            rs.getString("batch"),
            rs.getString("email"),
            rs.getString("phone"),
            status
        );
    }
}
```

# StudentService.java
**Path:** `src/main/java/com/college/library/service/StudentService.java`

```java
package com.college.library.service;

import com.college.library.dao.BorrowRequestDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;
    private final BorrowTransactionDAO transactionDAO;
    private final ReservationDAO reservationDAO;
    private final BorrowRequestDAO borrowRequestDAO;

    public StudentService(StudentDAO studentDAO) {
        this(studentDAO, null, null, null);
    }

    public StudentService(StudentDAO studentDAO,
                          BorrowTransactionDAO transactionDAO,
                          ReservationDAO reservationDAO,
                          BorrowRequestDAO borrowRequestDAO) {
        this.studentDAO = studentDAO;
        this.transactionDAO = transactionDAO;
        this.reservationDAO = reservationDAO;
        this.borrowRequestDAO = borrowRequestDAO;
    }

    public void addStudent(Student student) throws LibraryException {
        validateStudent(student);
        if (studentDAO.findById(student.getKtuId()).isPresent()) {
            throw new LibraryException("Student with KTU ID " + student.getKtuId() + " already exists.");
        }
        studentDAO.create(student);
    }

    public void updateStudent(Student student) throws LibraryException {
        validateStudent(student);
        if (studentDAO.findById(student.getKtuId()).isEmpty()) {
            throw new LibraryException("Student not found for update.");
        }
        studentDAO.update(student);
    }

    public void deleteStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID cannot be empty.");
        }

        if (studentDAO.findById(ktuId).isEmpty()) {
            throw new LibraryException("Student not found for deletion.");
        }

        List<String> blockingReasons = new ArrayList<>();

        // 1. Check borrow transactions
        if (transactionDAO != null) {
            List<BorrowTransaction> transactions = transactionDAO.findByStudentId(ktuId);
            if (transactions != null && !transactions.isEmpty()) {
                long active = transactions.stream().filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus())).count();
                long completed = transactions.size() - active;
                if (active > 0 && completed > 0) {
                    blockingReasons.add(active + " active borrowed book(s) and " + completed + " borrowing history record(s)");
                } else if (active > 0) {
                    blockingReasons.add(active + " active borrowed book(s)");
                } else {
                    blockingReasons.add(completed + " borrowing history record(s)");
                }
            }
        }

        // 2. Check borrow requests
        if (borrowRequestDAO != null) {
            List<BorrowRequest> requests = borrowRequestDAO.findByStudentId(ktuId);
            if (requests != null && !requests.isEmpty()) {
                long pending = requests.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
                long past = requests.size() - pending;
                if (pending > 0 && past > 0) {
                    blockingReasons.add(pending + " pending borrow request(s) and " + past + " past request(s)");
                } else if (pending > 0) {
                    blockingReasons.add(pending + " pending borrow request(s)");
                } else {
                    blockingReasons.add(requests.size() + " borrow request(s)");
                }
            }
        }

        // 3. Check reservations
        if (reservationDAO != null) {
            List<Reservation> reservations = reservationDAO.findByStudentId(ktuId);
            if (reservations != null && !reservations.isEmpty()) {
                long pending = reservations.stream().filter(r -> "PENDING".equalsIgnoreCase(r.getStatus())).count();
                long past = reservations.size() - pending;
                if (pending > 0 && past > 0) {
                    blockingReasons.add(pending + " pending reservation(s) and " + past + " past reservation(s)");
                } else if (pending > 0) {
                    blockingReasons.add(pending + " pending reservation(s)");
                } else {
                    blockingReasons.add(reservations.size() + " reservation(s)");
                }
            }
        }

        // Fallback to studentDAO related records check if specific DAOs were not provided
        if (blockingReasons.isEmpty() && transactionDAO == null && reservationDAO == null && borrowRequestDAO == null) {
            List<String> daoReasons = studentDAO.getRelatedRecordsSummary(ktuId);
            if (daoReasons != null && !daoReasons.isEmpty()) {
                blockingReasons.addAll(daoReasons);
            }
        }

        if (!blockingReasons.isEmpty()) {
            throw new LibraryException(String.format(
                    "Cannot permanently delete student %s: Related records exist (%s). " +
                    "To preserve borrowing history and system records, please deactivate the student instead.",
                    ktuId, String.join(", ", blockingReasons)
            ));
        }

        studentDAO.delete(ktuId);
    }

    public void deactivateStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID cannot be empty.");
        }
        Student student = studentDAO.findById(ktuId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        if (!student.isActive()) {
            throw new LibraryException("Student " + ktuId + " is already deactivated.");
        }
        student.setStatus("INACTIVE");
        studentDAO.update(student);
    }

    public void activateStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID cannot be empty.");
        }
        Student student = studentDAO.findById(ktuId)
                .orElseThrow(() -> new LibraryException("Student not found."));
        if (student.isActive()) {
            throw new LibraryException("Student " + ktuId + " is already active.");
        }
        student.setStatus("ACTIVE");
        studentDAO.update(student);
    }

    public boolean hasRelatedRecords(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            return false;
        }
        if (transactionDAO != null) {
            List<BorrowTransaction> tx = transactionDAO.findByStudentId(ktuId);
            if (tx != null && !tx.isEmpty()) return true;
        }
        if (borrowRequestDAO != null) {
            List<BorrowRequest> req = borrowRequestDAO.findByStudentId(ktuId);
            if (req != null && !req.isEmpty()) return true;
        }
        if (reservationDAO != null) {
            List<Reservation> res = reservationDAO.findByStudentId(ktuId);
            if (res != null && !res.isEmpty()) return true;
        }
        return studentDAO.hasRelatedRecords(ktuId);
    }

    public Student getStudent(String ktuId) throws LibraryException {
        return studentDAO.findById(ktuId)
                .orElseThrow(() -> new LibraryException("Student not found."));
    }

    public List<Student> getAllStudents() throws LibraryException {
        return studentDAO.findAll();
    }

    private void validateStudent(Student student) throws LibraryException {
        if (student == null) throw new LibraryException("Student cannot be null.");
        if (student.getKtuId() == null || student.getKtuId().trim().isEmpty()) {
            throw new LibraryException("Student KTU ID is required.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new LibraryException("Student name is required.");
        }
    }
}
```

# StudentPanel.java
**Path:** `src/main/java/com/college/library/gui/StudentPanel.java`

```java
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
```

# BookServiceTest.java
**Path:** `src/test/java/com/college/library/service/BookServiceTest.java`

```java
package com.college.library.service;

import com.college.library.dao.BookDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    
    private BookService bookService;
    private boolean createCalled = false;

    @BeforeEach
    void setUp() {
        createCalled = false;
        BookDAO mockDAO = new BookDAO() {
            @Override public void create(Book entity) { createCalled = true; }
            @Override public Optional<Book> findById(String id) {
                if ("EXISTS".equals(id)) return Optional.of(new Book());
                return Optional.empty();
            }
            @Override public java.util.List<Book> findAll() { return null; }
            @Override public void update(Book entity) {}
            @Override public void delete(String id) {}
            @Override public void updateStatus(String id, String s, Connection c) {}
            @Override public int updateStatusIfCondition(String i, String n, String o, Connection c) { return 1; }
            @Override public java.util.List<com.college.library.model.Category> getAllCategories() { return new java.util.ArrayList<>(); }
            @Override public java.util.List<Book> findByIsbn(String isbn) { return new java.util.ArrayList<>(); }
        };
        bookService = new BookService(mockDAO);
    }

    @Test
    void testAddBookSuccess() throws Exception {
        Book b = new Book("NEW", "123", "Title", "Author", "Pub", "1", 1, "AVAILABLE");
        bookService.addBook(b);
        assertTrue(createCalled);
    }

    @Test
    void testAddBookDuplicate() {
        Book b = new Book("EXISTS", "123", "Title", "Author", "Pub", "1", 1, "AVAILABLE");
        assertThrows(LibraryException.class, () -> bookService.addBook(b));
    }
    
    @Test
    void testValidateBookMissingTitle() {
        Book b = new Book("NEW2", "123", "", "Author", "Pub", "1", 1, "AVAILABLE");
        assertThrows(LibraryException.class, () -> bookService.addBook(b));
    }
}
```

# StudentServiceTest.java
**Path:** `src/test/java/com/college/library/service/StudentServiceTest.java`

```java
package com.college.library.service;

import com.college.library.dao.BorrowRequestDAO;
import com.college.library.dao.BorrowTransactionDAO;
import com.college.library.dao.ReservationDAO;
import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Reservation;
import com.college.library.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentDAO mockStudentDAO;
    private BorrowTransactionDAO mockTransactionDAO;
    private ReservationDAO mockReservationDAO;
    private BorrowRequestDAO mockBorrowRequestDAO;
    private StudentService studentService;

    private boolean deleteCalled = false;
    private Map<String, List<BorrowTransaction>> studentTransactions;
    private Map<String, List<BorrowRequest>> studentRequests;
    private Map<String, List<Reservation>> studentReservations;
    private Set<String> existingStudents;
    private Map<String, Student> studentStore;

    @BeforeEach
    void setUp() {
        deleteCalled = false;
        studentTransactions = new HashMap<>();
        studentRequests = new HashMap<>();
        studentReservations = new HashMap<>();
        existingStudents = new HashSet<>();
        studentStore = new HashMap<>();

        Student validStudent = new Student();
        validStudent.setKtuId("VALID_STU");
        validStudent.setName("Test Student");
        validStudent.setStatus("ACTIVE");
        existingStudents.add("VALID_STU");
        studentStore.put("VALID_STU", validStudent);

        mockStudentDAO = new StudentDAO() {
            @Override
            public void create(Student entity) throws LibraryException {
                existingStudents.add(entity.getKtuId());
                studentStore.put(entity.getKtuId(), entity);
            }

            @Override
            public Optional<Student> findById(String id) throws LibraryException {
                if (studentStore.containsKey(id)) {
                    return Optional.of(studentStore.get(id));
                }
                return Optional.empty();
            }

            @Override
            public List<Student> findAll() throws LibraryException {
                return new ArrayList<>(studentStore.values());
            }

            @Override
            public void update(Student entity) throws LibraryException {
                studentStore.put(entity.getKtuId(), entity);
            }

            @Override
            public void delete(String id) throws LibraryException {
                deleteCalled = true;
                existingStudents.remove(id);
                studentStore.remove(id);
            }
        };

        mockTransactionDAO = new BorrowTransactionDAO() {
            @Override
            public void createWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException {}

            @Override
            public void updateWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException {}

            @Override
            public Optional<BorrowTransaction> findActiveByBookId(String bookId) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<BorrowTransaction> findByStudentId(String studentId) throws LibraryException {
                return studentTransactions.getOrDefault(studentId, Collections.emptyList());
            }

            @Override
            public int countActiveTransactions() throws LibraryException {
                return 0;
            }

            @Override
            public void create(BorrowTransaction entity) throws LibraryException {}

            @Override
            public Optional<BorrowTransaction> findById(Integer id) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<BorrowTransaction> findAll() throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public void update(BorrowTransaction entity) throws LibraryException {}

            @Override
            public void delete(Integer id) throws LibraryException {}
        };

        mockReservationDAO = new ReservationDAO() {
            @Override
            public void createWithConnection(Reservation reservation, Connection conn) throws SQLException {}

            @Override
            public void updateWithConnection(Reservation reservation, Connection conn) throws SQLException {}

            @Override
            public Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection conn) throws SQLException {
                return Optional.empty();
            }

            @Override
            public List<Reservation> findPendingByStudentId(String studentId) throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public List<Reservation> findPendingByIsbn(String isbn) throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public List<Reservation> findByStudentId(String studentId) throws LibraryException {
                return studentReservations.getOrDefault(studentId, Collections.emptyList());
            }

            @Override
            public void create(Reservation entity) throws LibraryException {}

            @Override
            public Optional<Reservation> findById(Integer id) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<Reservation> findAll() throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public void update(Reservation entity) throws LibraryException {}

            @Override
            public void delete(Integer id) throws LibraryException {}
        };

        mockBorrowRequestDAO = new BorrowRequestDAO() {
            @Override
            public List<BorrowRequest> findByStudentId(String studentId) throws LibraryException {
                return studentRequests.getOrDefault(studentId, Collections.emptyList());
            }

            @Override
            public List<BorrowRequest> findByStatus(String status) throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public Optional<BorrowRequest> findPendingByStudentAndBook(String studentId, String bookId) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public void create(BorrowRequest entity) throws LibraryException {}

            @Override
            public Optional<BorrowRequest> findById(Integer id) throws LibraryException {
                return Optional.empty();
            }

            @Override
            public List<BorrowRequest> findAll() throws LibraryException {
                return Collections.emptyList();
            }

            @Override
            public void update(BorrowRequest entity) throws LibraryException {}

            @Override
            public void delete(Integer id) throws LibraryException {}
        };

        studentService = new StudentService(mockStudentDAO, mockTransactionDAO, mockReservationDAO, mockBorrowRequestDAO);
    }

    @Test
    void testDeleteStudentSuccessWhenNoRelatedRecords() throws Exception {
        studentService.deleteStudent("VALID_STU");
        assertTrue(deleteCalled, "DAO delete should have been invoked when no related records exist.");
    }

    @Test
    void testDeleteStudentBlockedByActiveBorrowTransaction() {
        BorrowTransaction activeTx = new BorrowTransaction();
        activeTx.setStudentId("VALID_STU");
        activeTx.setStatus("ACTIVE");
        activeTx.setIssueDate(LocalDate.now());
        studentTransactions.put("VALID_STU", List.of(activeTx));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("active borrowed book(s)"));
        assertTrue(ex.getMessage().contains("preserve borrowing history"));
        assertFalse(deleteCalled, "Student must not be deleted when active borrow transactions exist.");
    }

    @Test
    void testDeleteStudentBlockedByCompletedBorrowTransactionPreservingHistory() {
        BorrowTransaction completedTx = new BorrowTransaction();
        completedTx.setStudentId("VALID_STU");
        completedTx.setStatus("COMPLETED");
        completedTx.setIssueDate(LocalDate.now().minusDays(30));
        completedTx.setReturnDate(LocalDate.now().minusDays(16));
        studentTransactions.put("VALID_STU", List.of(completedTx));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("borrowing history record(s)"));
        assertTrue(ex.getMessage().contains("preserve borrowing history"));
        assertFalse(deleteCalled, "Student must not be deleted when borrowing history exists.");
    }

    @Test
    void testDeleteStudentBlockedByBorrowRequest() {
        BorrowRequest req = new BorrowRequest();
        req.setStudentId("VALID_STU");
        req.setBookId("B1001");
        req.setStatus("PENDING");
        req.setRequestDate(LocalDateTime.now());
        studentRequests.put("VALID_STU", List.of(req));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("borrow request(s)"));
        assertFalse(deleteCalled, "Student must not be deleted when borrow requests exist.");
    }

    @Test
    void testDeleteStudentBlockedByReservation() {
        Reservation res = new Reservation();
        res.setStudentId("VALID_STU");
        res.setBookIsbn("978-0134685991");
        res.setStatus("PENDING");
        studentReservations.put("VALID_STU", List.of(res));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("Related records exist"));
        assertTrue(ex.getMessage().contains("reservation(s)"));
        assertFalse(deleteCalled, "Student must not be deleted when reservations exist.");
    }

    @Test
    void testDeleteStudentBlockedByMultipleRelatedRecords() {
        BorrowTransaction activeTx = new BorrowTransaction();
        activeTx.setStatus("ACTIVE");
        BorrowTransaction pastTx = new BorrowTransaction();
        pastTx.setStatus("COMPLETED");
        studentTransactions.put("VALID_STU", List.of(activeTx, pastTx));

        BorrowRequest req = new BorrowRequest();
        req.setStatus("PENDING");
        studentRequests.put("VALID_STU", List.of(req));

        Reservation res = new Reservation();
        res.setStatus("PENDING");
        studentReservations.put("VALID_STU", List.of(res));

        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("VALID_STU"));
        String msg = ex.getMessage();
        assertTrue(msg.contains("active borrowed book(s)"));
        assertTrue(msg.contains("borrowing history"));
        assertTrue(msg.contains("borrow request(s)"));
        assertTrue(msg.contains("reservation(s)"));
        assertFalse(deleteCalled);
    }

    @Test
    void testDeleteStudentFallbackToStudentDAOSummary() {
        StudentDAO customDAO = new StudentDAO() {
            @Override
            public Optional<Student> findById(String id) {
                return Optional.of(new Student());
            }
            @Override
            public void create(Student s) {}
            @Override
            public List<Student> findAll() { return Collections.emptyList(); }
            @Override
            public void update(Student s) {}
            @Override
            public void delete(String id) {}
            @Override
            public List<String> getRelatedRecordsSummary(String id) {
                return List.of("1 active transaction(s) via DAO fallback");
            }
        };

        StudentService singleDaoService = new StudentService(customDAO);
        LibraryException ex = assertThrows(LibraryException.class, () -> singleDaoService.deleteStudent("ANY_ID"));
        assertTrue(ex.getMessage().contains("active transaction(s) via DAO fallback"));
    }

    @Test
    void testDeleteStudentNotFound() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deleteStudent("NON_EXISTENT"));
        assertTrue(ex.getMessage().contains("Student not found"));
        assertFalse(deleteCalled);
    }

    @Test
    void testDeleteStudentEmptyOrNullId() {
        assertThrows(LibraryException.class, () -> studentService.deleteStudent(null));
        assertThrows(LibraryException.class, () -> studentService.deleteStudent("   "));
        assertFalse(deleteCalled);
    }

    @Test
    void testAddStudentValidation() throws Exception {
        Student newStu = new Student("NEW_KTU", "hash", "New Student", "CSE", 1, "2024", "new@test.com", "1234");
        studentService.addStudent(newStu);
        assertTrue(existingStudents.contains("NEW_KTU"));

        // Duplicate
        assertThrows(LibraryException.class, () -> studentService.addStudent(newStu));
    }

    @Test
    void testDeactivateStudentSuccess() throws Exception {
        studentService.deactivateStudent("VALID_STU");
        Student s = studentStore.get("VALID_STU");
        assertNotNull(s);
        assertEquals("INACTIVE", s.getStatus());
        assertFalse(s.isActive());
    }

    @Test
    void testDeactivateStudentAlreadyInactive() throws Exception {
        studentService.deactivateStudent("VALID_STU");
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deactivateStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("already deactivated"));
    }

    @Test
    void testActivateStudentSuccess() throws Exception {
        studentService.deactivateStudent("VALID_STU");
        studentService.activateStudent("VALID_STU");
        Student s = studentStore.get("VALID_STU");
        assertNotNull(s);
        assertEquals("ACTIVE", s.getStatus());
        assertTrue(s.isActive());
    }

    @Test
    void testActivateStudentAlreadyActive() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.activateStudent("VALID_STU"));
        assertTrue(ex.getMessage().contains("already active"));
    }

    @Test
    void testDeactivateStudentNotFound() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.deactivateStudent("UNKNOWN_STU"));
        assertTrue(ex.getMessage().contains("Student not found"));
    }

    @Test
    void testActivateStudentNotFound() {
        LibraryException ex = assertThrows(LibraryException.class, () -> studentService.activateStudent("UNKNOWN_STU"));
        assertTrue(ex.getMessage().contains("Student not found"));
    }

    @Test
    void testDeactivateStudentEmptyOrNullId() {
        assertThrows(LibraryException.class, () -> studentService.deactivateStudent(null));
        assertThrows(LibraryException.class, () -> studentService.deactivateStudent("   "));
    }

    @Test
    void testActivateStudentEmptyOrNullId() {
        assertThrows(LibraryException.class, () -> studentService.activateStudent(null));
        assertThrows(LibraryException.class, () -> studentService.activateStudent("   "));
    }
}
```
