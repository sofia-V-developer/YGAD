package com.example.ygad;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "students")
public class Student {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String lastName;
    public String firstName;
    public String studentGroup;

    // Пустой конструктор (обязателен для Room)
    public Student() {}

    // Конструктор с параметрами
    public Student(String lastName, String firstName, String studentGroup) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.studentGroup = studentGroup;
    }
}