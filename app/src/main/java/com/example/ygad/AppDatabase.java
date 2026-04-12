package com.example.ygad;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Subject.class, Grade.class, Student.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract SubjectDao subjectDao();
    public abstract GradeDao gradeDao();
    public abstract StudentDao studentDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "ygad_database"
                    ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()  // ← ДОБАВЬ ЭТУ СТРОКУ
                    .build();
        }
        return instance;

    }
}