package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;

import java.util.List;
import java.util.Optional;

public interface BorrowRequestDAO extends CrudDAO<BorrowRequest, Integer> {
    List<BorrowRequest> findByStudentId(String studentId) throws LibraryException;
    List<BorrowRequest> findByStatus(String status) throws LibraryException;
    Optional<BorrowRequest> findPendingByStudentAndBook(String studentId, String bookId) throws LibraryException;
}
