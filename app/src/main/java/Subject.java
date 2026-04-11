package com.example.ygad;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects")
public class Subject {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String teacher;  // можно потом добавить
    public int semester;

    public Subject(String name) {
        this.name = name;
        this.teacher = "";
        this.semester = 1;
    }
}