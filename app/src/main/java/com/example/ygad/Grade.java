package com.example.ygad;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "grades",
        foreignKeys = {
                @ForeignKey(entity = Subject.class, parentColumns = "id", childColumns = "subjectId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Student.class, parentColumns = "id", childColumns = "studentId", onDelete = ForeignKey.CASCADE)
        })
public class Grade {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int subjectId;
    public int studentId;
    public int value;
    public String type;
    public Date date;
    public String source;

    // Пустой конструктор (обязателен для Room)
    public Grade() {}

    // Конструктор с параметрами (игнорируем для Room)
    @Ignore
    public Grade(int subjectId, int studentId, int value, String source) {
        this.subjectId = subjectId;
        this.studentId = studentId;
        this.value = value;
        this.type = "test";
        this.date = new Date();
        this.source = source;
    }
}