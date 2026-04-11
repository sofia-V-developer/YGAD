package com.example.ygad;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM students ORDER BY lastName")
    List<Student> getAll();

    @Query("SELECT * FROM students WHERE lastName LIKE :query OR firstName LIKE :query")
    List<Student> search(String query);

    @Query("SELECT * FROM students WHERE lastName = :lastName")
    Student findByLastName(String lastName);

    @Insert
    void insert(Student student);
}