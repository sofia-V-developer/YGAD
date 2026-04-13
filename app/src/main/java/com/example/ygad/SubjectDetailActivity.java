package com.example.ygad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SubjectDetailActivity extends AppCompatActivity {

    private TextView tvSubjectName, tvAverage, tvTeacher;
    private RecyclerView rvGrades;
    private Button btnDeleteSubject, btnAddGradePhoto;
    private AppDatabase db;
    private UserData userData;
    private int subjectId;
    private String subjectName;
    private GradeAdapter gradeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        tvSubjectName = findViewById(R.id.tvSubjectName);
        tvAverage = findViewById(R.id.tvAverage);
        tvTeacher = findViewById(R.id.tvTeacher);
        rvGrades = findViewById(R.id.rvGrades);
        btnDeleteSubject = findViewById(R.id.btnDeleteSubject);
        btnAddGradePhoto = findViewById(R.id.btnAddGradePhoto);

        db = AppDatabase.getInstance(this);
        userData = new UserData(this);

        subjectId = getIntent().getIntExtra("subject_id", -1);
        subjectName = getIntent().getStringExtra("subject_name");

        if (subjectId == -1) {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvSubjectName.setText(subjectName);

        if (userData.isElder()) {
            btnDeleteSubject.setVisibility(View.VISIBLE);
            btnDeleteSubject.setOnClickListener(v -> deleteSubject());
            btnAddGradePhoto.setVisibility(View.VISIBLE);
            btnAddGradePhoto.setOnClickListener(v -> openCamera());
        } else {
            btnDeleteSubject.setVisibility(View.GONE);
            btnAddGradePhoto.setVisibility(View.GONE);
        }

        gradeAdapter = new GradeAdapter();
        rvGrades.setLayoutManager(new LinearLayoutManager(this));
        rvGrades.setAdapter(gradeAdapter);

        loadData();
    }

    private void openCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        cameraLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String recognizedText = result.getData().getStringExtra("recognized_text");
                    if (recognizedText != null && !recognizedText.isEmpty()) {
                        processRecognizedText(recognizedText);
                    }
                }
            });

    private void processRecognizedText(String text) {
        String[] lines = text.split("\n");
        Pattern pattern = Pattern.compile("([А-Яа-яёЁ]+)\\s+.*?([2-5])");

        new Thread(() -> {
            int addedCount = 0;

            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String lastName = matcher.group(1);
                    int gradeValue = Integer.parseInt(matcher.group(2));

                    Student student = db.studentDao().findByLastName(lastName);
                    if (student != null) {
                        Grade grade = new Grade(subjectId, student.id, gradeValue, "photo");
                        db.gradeDao().insert(grade);
                        addedCount++;
                    }
                }
            }

            final int finalAdded = addedCount;
            runOnUiThread(() -> {
                Toast.makeText(this, "Добавлено оценок: " + finalAdded, Toast.LENGTH_LONG).show();
                loadData();
            });
        }).start();
    }

    private void loadData() {
        new Thread(() -> {
            Subject subject = db.subjectDao().getById(subjectId);
            float avg = db.gradeDao().getAverageBySubject(subjectId);

            List<GradeWithStudent> grades;
            if (userData.isElder()) {
                // Староста видит всех
                grades = db.gradeDao().getGradesWithStudents(subjectId);
            } else {
                // Обычный студент видит только свои оценки
                Student currentStudent = db.studentDao().findByLastName(userData.getLastName());
                if (currentStudent != null) {
                    grades = db.gradeDao().getGradesByStudentAndSubject(currentStudent.id, subjectId);
                } else {
                    grades = new ArrayList<>();
                }
            }

            final List<GradeWithStudent> finalGrades = grades;
            runOnUiThread(() -> {
                if (subject != null) {
                    tvTeacher.setText("Преподаватель: " + (subject.teacher.isEmpty() ? "—" : subject.teacher));
                }
                tvAverage.setText("Средний балл: " + String.format("%.2f", avg));
                gradeAdapter.setGrades(finalGrades);
            });
        }).start();
    }

    private void deleteSubject() {
        new AlertDialog.Builder(this)
                .setTitle("Удалить предмет")
                .setMessage("Вы уверены, что хотите удалить предмет \"" + subjectName + "\"?")
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

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}