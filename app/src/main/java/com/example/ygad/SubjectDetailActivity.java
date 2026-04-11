package com.example.ygad;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubjectDetailActivity extends AppCompatActivity {

    private TextView tvSubjectName, tvAverage, tvTeacher;
    private RecyclerView rvGrades;
    private Button btnDeleteSubject;
    private AppDatabase db;
    private UserData userData;
    private int subjectId;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        tvSubjectName = findViewById(R.id.tvSubjectName);
        tvAverage = findViewById(R.id.tvAverage);
        tvTeacher = findViewById(R.id.tvTeacher);
        rvGrades = findViewById(R.id.rvGrades);
        btnDeleteSubject = findViewById(R.id.btnDeleteSubject);

        db = AppDatabase.getInstance(this);
        userData = new UserData(this);

        subjectId = getIntent().getIntExtra("subject_id", -1);
        subjectName = getIntent().getStringExtra("subject_name");

        tvSubjectName.setText(subjectName);

        // Кнопка удаления только для старосты
        if (userData.isElder()) {
            btnDeleteSubject.setVisibility(View.VISIBLE);
            btnDeleteSubject.setOnClickListener(v -> deleteSubject());
        }

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            // Загружаем информацию о предмете
            Subject subject = db.subjectDao().getById(subjectId);
            float avg = db.gradeDao().getAverageBySubject(subjectId);
            List<Grade> grades = db.gradeDao().getBySubject(subjectId);

            runOnUiThread(() -> {
                tvTeacher.setText("Преподаватель: " + (subject.teacher.isEmpty() ? "—" : subject.teacher));
                tvAverage.setText("Средний балл: " + String.format("%.2f", avg));

                // Показываем оценки в RecyclerView
                GradeAdapter adapter = new GradeAdapter(grades);
                rvGrades.setLayoutManager(new LinearLayoutManager(this));
                rvGrades.setAdapter(adapter);
            });
        }).start();
    }

    private void deleteSubject() {
        new AlertDialog.Builder(this)
                .setTitle("Удалить предмет")
                .setMessage("Вы уверены, что хотите удалить предмет \"" + subjectName + "\"? Все оценки по нему тоже будут удалены.")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    new Thread(() -> {
                        db.subjectDao().deleteById(subjectId);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Предмет удалён", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }).start();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}