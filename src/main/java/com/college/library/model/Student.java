package com.college.library.model;

public class Student extends Person {
    private String ktuId;
    private String passwordHash;
    private String branch;
    private int semester;
    private String batch;
    private String status = "ACTIVE";

    public Student() {}

    public Student(String ktuId, String passwordHash, String name, String branch, int semester, String batch, String email, String phone, String status) {
        super(name, email, phone);
        this.ktuId = ktuId;
        this.passwordHash = passwordHash;
        this.branch = branch;
        this.semester = semester;
        this.batch = batch;
        this.status = status != null ? status : "ACTIVE";
    }

    public Student(String ktuId, String passwordHash, String name, String branch, int semester, String batch, String email, String phone) {
        this(ktuId, passwordHash, name, branch, semester, batch, email, phone, "ACTIVE");
    }

    public Student(String ktuId, String name, String branch, int semester, String batch, String email, String phone) {
        this(ktuId, null, name, branch, semester, batch, email, phone, "ACTIVE");
    }

    public String getKtuId() { return ktuId; }
    public void setKtuId(String ktuId) { this.ktuId = ktuId; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status != null ? status : "ACTIVE"; }

    public boolean isActive() {
        return status == null || "ACTIVE".equalsIgnoreCase(status);
    }
}
