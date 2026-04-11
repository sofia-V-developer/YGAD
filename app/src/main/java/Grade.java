package com.example.ygad;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "grades",
        foreignKeys = @ForeignKey(
                entity = Subject.class,
                parentColumns = "id",
                childColumns = "subjectId",
                onDelete = ForeignKey.CASCADE
        ))
public class Grade {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int subjectId;
    public int value;
    public String type;  // "test", "exam", "homework"
    public Date date;
    public String source;  // "manual" или "photo"

    public Grade(int subjectId, int value, String source) {
        this.subjectId = subjectId;
        this.value = value;
        this.type = "test";
        this.date = new Date();
        this.source = source;
    }
}