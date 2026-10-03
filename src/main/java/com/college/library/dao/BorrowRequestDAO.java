package com.college.library.dao;

import com.college.library.model.BorrowRequest;
import java.util.List;

public interface BorrowRequestDAO extends CrudDAO<BorrowRequest, Integer> {
    List<BorrowRequest> findByStudentId(String studentId) throws com.college.library.exception.LibraryException;
    List<BorrowRequest> findByStatus(String status) throws com.college.library.exception.LibraryException;
    java.util.Optional<BorrowRequest> findPendingByStudentAndBook(String studentId, String bookId) throws com.college.library.exception.LibraryException;
}
