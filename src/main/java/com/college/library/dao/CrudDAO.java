package com.college.library.dao;

import com.college.library.exception.LibraryException;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T, ID> {
    void create(T entity) throws LibraryException;
    Optional<T> findById(ID id) throws LibraryException;
    List<T> findAll() throws LibraryException;
    void update(T entity) throws LibraryException;
    void delete(ID id) throws LibraryException;
}
