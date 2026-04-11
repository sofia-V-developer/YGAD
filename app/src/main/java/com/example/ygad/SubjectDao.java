package com.example.ygad;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name")
    List<Subject> getAll();

    @Query("SELECT * FROM subjects WHERE name LIKE :query OR teacher LIKE :query")
    List<Subject> search(String query);

    @Insert
    long insert(Subject subject);
    @Query("SELECT * FROM subjects WHERE id = :id")
    Subject getById(int id);

    @Query("DELETE FROM subjects WHERE id = :id")
    void deleteById(int id);
}