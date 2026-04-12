package com.example.ygad;

import android.content.Context;

public class DatabaseInitializer {
    public static void initializeStudents(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        new Thread(() -> {
            // Проверяем, есть ли уже студенты
            if (db.studentDao().getAll().isEmpty()) {
                // Список твоей группы (22 человека)
                String[][] students = {
                        {"Балакин", "Сергей"},
                        {"Батурин", "Антон"},
                        {"Башев", "Лев"},
                        {"Булатова", "Диана"},
                        {"Волков", "Вячеслав"},
                        {"Воропаева", "София"},
                        {"Горлова", "Валерия"},
                        {"Дыдыкин", "Матвей"},
                        {"Елшанкина", "Виктория"},
                        {"Еремичев", "Данила"},
                        {"Комлякова", "Анастасия"},
                        {"Кузнецов", "Максим"},
                        {"Ларионова", "Ангелина"},
                        {"Лебедев", "Денис"},
                        {"Макаров", "Дмитрий"},
                        {"Руссакова", "Анна"},
                        {"Рызаева", "Полина"},
                        {"Староверов", "Ярослав"},
                        {"Тихомирова", "Ульяна"},
                        {"Федосеев", "Иван"},
                        {"Чилибанова", "Вероника"}
                };

                for (String[] s : students) {
                    db.studentDao().insert(new Student(s[0], s[1], "Группа 1")); }
            }
        }).start();
    }
}