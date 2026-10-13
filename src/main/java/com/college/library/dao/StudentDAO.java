package com.college.library.dao;

import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import java.util.Collections;
import java.util.List;

public interface StudentDAO extends CrudDAO<Student, String> {
    default boolean hasRelatedRecords(String ktuId) throws LibraryException {
        return false;
    }

    default List<String> getRelatedRecordsSummary(String ktuId) throws LibraryException {
        return Collections.emptyList();
    }

    default void updateStatus(String ktuId, String status) throws LibraryException {}
}
