package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import java.util.Optional;

public interface LibrarianDAO extends CrudDAO<Librarian, Integer> {
    Optional<Librarian> findByUsername(String username) throws LibraryException;
}
