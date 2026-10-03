package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Reservation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO extends CrudDAO<Reservation, Integer> {
    void createWithConnection(Reservation reservation, Connection conn) throws SQLException;
    void updateWithConnection(Reservation reservation, Connection conn) throws SQLException;
    Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection conn) throws SQLException;
    List<Reservation> findPendingByStudentId(String studentId) throws LibraryException;
    List<Reservation> findPendingByIsbn(String isbn) throws LibraryException;
}
