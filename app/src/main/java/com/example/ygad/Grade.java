package com.example.ygad;

import androidx.room.Entity;
import androidx.room.ForeignKey;
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
    public int studentId;  // ← ДОБАВИТЬ!
    public int value;
    public String type;
    public Date date;
    public String source;

    public Grade(int subjectId, int gradeValue, String source) {
        this.subjectId = subjectId;
        this.value = gradeValue;
        this.type = "test";
        this.date = new Date();
        this.source = source;
    }
}