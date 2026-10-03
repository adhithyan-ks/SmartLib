package com.college.library.dao.impl;

import com.college.library.dao.ReservationDAO;
import com.college.library.exception.LibraryException;
import com.college.library.model.Reservation;
import com.college.library.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOImpl implements ReservationDAO {

    @Override
    public void create(Reservation reservation) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createWithConnection(reservation, conn);
        } catch (SQLException e) {
            throw new LibraryException("Error creating reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void createWithConnection(Reservation reservation, Connection conn) throws SQLException {
        String sql = "INSERT INTO reservations (student_id, book_isbn, request_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, reservation.getStudentId());
            pstmt.setString(2, reservation.getBookIsbn());
            pstmt.setTimestamp(3, reservation.getRequestDate() != null ? Timestamp.valueOf(reservation.getRequestDate()) : new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, reservation.getStatus());
            
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Optional<Reservation> findById(Integer id) throws LibraryException {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding reservation: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findAll() throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new LibraryException("Error retrieving reservations: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void update(Reservation reservation) throws LibraryException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateWithConnection(reservation, conn);
        } catch (SQLException e) {
            throw new LibraryException("Error updating reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateWithConnection(Reservation reservation, Connection conn) throws SQLException {
        String sql = "UPDATE reservations SET student_id=?, book_isbn=?, request_date=?, status=? WHERE id=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getStudentId());
            pstmt.setString(2, reservation.getBookIsbn());
            pstmt.setTimestamp(3, reservation.getRequestDate() != null ? Timestamp.valueOf(reservation.getRequestDate()) : null);
            pstmt.setString(4, reservation.getStatus());
            pstmt.setInt(5, reservation.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Integer id) throws LibraryException {
        String sql = "DELETE FROM reservations WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new LibraryException("Error deleting reservation: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Reservation> findOldestPendingByIsbn(String isbn, Connection conn) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE book_isbn = ? AND status = 'PENDING' ORDER BY request_date ASC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReservation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> findPendingByStudentId(String studentId) throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE student_id = ? AND status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding pending reservations for student: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Reservation> findPendingByIsbn(String isbn) throws LibraryException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE book_isbn = ? AND status = 'PENDING' ORDER BY request_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new LibraryException("Error finding pending reservations by ISBN: " + e.getMessage(), e);
        }
        return list;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        res.setId(rs.getInt("id"));
        res.setStudentId(rs.getString("student_id"));
        res.setBookIsbn(rs.getString("book_isbn"));
        Timestamp ts = rs.getTimestamp("request_date");
        if (ts != null) {
            res.setRequestDate(ts.toLocalDateTime());
        }
        res.setStatus(rs.getString("status"));
        return res;
    }
}
