package com.college.library.model;

public class Librarian extends Person {
    private int id;
    private String username;
    private String passwordHash;

    public Librarian() {}

    public Librarian(int id, String username, String passwordHash, String name) {
        super(name, null, null);
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
