package com.college.library.model;

public class Book {
    private String accessionId;
    private String isbn;
    private String title;
    private String author;
    private int categoryId;
    private String status; // 'AVAILABLE', 'ISSUED', 'LOST', 'DAMAGED'

    public Book() {}

    public Book(String accessionId, String isbn, String title, String author, int categoryId, String status) {
        this.accessionId = accessionId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.categoryId = categoryId;
        this.status = status;
    }

    @Deprecated
    public Book(String accessionId, String isbn, String title, String author, String publisher, String edition, int categoryId, String status) {
        this(accessionId, isbn, title, author, categoryId, status);
    }

    public String getAccessionId() { return accessionId; }
    public void setAccessionId(String accessionId) { this.accessionId = accessionId; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
