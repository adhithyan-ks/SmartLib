package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BorrowTransactionDAO extends CrudDAO<BorrowTransaction, Integer> {
    void createWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException;
    void updateWithConnection(BorrowTransaction transaction, Connection conn) throws SQLException;
    Optional<BorrowTransaction> findActiveByBookId(String bookId) throws LibraryException;
    List<BorrowTransaction> findByStudentId(String studentId) throws LibraryException;
    int countActiveTransactions() throws LibraryException;
}
