package com.example.ygad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText etSubjectName, etTeacher;
    private Button btnSaveSubject;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        db = AppDatabase.getInstance(this);

        etSubjectName = findViewById(R.id.etSubjectName);
        etTeacher = findViewById(R.id.etTeacher);
        btnSaveSubject = findViewById(R.id.btnSaveSubject);

        btnSaveSubject.setOnClickListener(v -> {
            String name = etSubjectName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название предмета", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                Subject subject = new Subject(name);
                subject.teacher = etTeacher.getText().toString().trim();
                long id = db.subjectDao().insert(subject);
                runOnUiThread(() -> {
                    if (id > 0) {
                        Toast.makeText(this, "Предмет добавлен", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}