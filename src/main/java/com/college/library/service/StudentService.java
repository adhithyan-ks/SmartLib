package com.college.library.service;

import com.college.library.dao.StudentDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Student;

import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
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
        studentDAO.delete(ktuId);
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
