package com.example.ygad;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "students")
public class Student {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String lastName;   // фамилия
    public String firstName;  // имя
    public String group;      // группа

    public Student(String lastName, String firstName, String group) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.group = group;
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }
}