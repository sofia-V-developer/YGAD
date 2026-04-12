package com.example.ygad;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface GradeDao {
    @Query("SELECT * FROM grades WHERE subjectId = :subjectId ORDER BY date DESC")
    List<Grade> getBySubject(int subjectId);

    @Query("SELECT grades.id as gradeId, grades.value as gradeValue, grades.date as gradeDate, grades.source as gradeSource, " +
            "students.id as studentId, students.lastName as studentLastName, students.firstName as studentFirstName, students.studentGroup as studentGroup " +
            "FROM grades " +
            "JOIN students ON grades.studentId = students.id " +
            "WHERE grades.subjectId = :subjectId " +
            "ORDER BY grades.date DESC")
    List<GradeWithStudent> getGradesWithStudents(int subjectId);

    @Insert
    long insert(Grade grade);

    @Query("SELECT AVG(value) FROM grades WHERE subjectId = :subjectId")
    float getAverageBySubject(int subjectId);

    @Query("SELECT COUNT(*) FROM grades WHERE value < 3 AND subjectId = :subjectId")
    int getFailCountBySubject(int subjectId);
}