package com.college.library.model;

public class Student extends Person {
    private String ktuId;
    private String branch;
    private int semester;
    private String batch;

    public Student() {}

    public Student(String ktuId, String name, String branch, int semester, String batch, String email, String phone) {
        super(name, email, phone);
        this.ktuId = ktuId;
        this.branch = branch;
        this.semester = semester;
        this.batch = batch;
    }

    public String getKtuId() { return ktuId; }
    public void setKtuId(String ktuId) { this.ktuId = ktuId; }
    
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
}
