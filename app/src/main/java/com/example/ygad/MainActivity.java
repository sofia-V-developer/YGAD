package com.example.ygad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.stream.Collectors;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private TextView tvOverallAverage, tvFailsCount, tvUserName;
    private Button btnRefresh, btnAddSubject;
    private AutoCompleteTextView actvSubjectSearch;
    private RecyclerView rvSubjects;
    private SubjectAdapter subjectAdapter;
    private UserData userData;
    private AppDatabase db;
    private List<Subject> allSubjects = new ArrayList<>();
    private ImageView ivAvatarMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация базы данных студентов
        DatabaseInitializer.initializeStudents(this);

        // Инициализация элементов
        tvUserName = findViewById(R.id.tvUserName);
        tvOverallAverage = findViewById(R.id.tvOverallAverage);
        tvFailsCount = findViewById(R.id.tvFailsCount);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        actvSubjectSearch = findViewById(R.id.actvSubjectSearch);
        rvSubjects = findViewById(R.id.rvSubjects);
        ivAvatarMenu = findViewById(R.id.ivAvatarMenu);

        // База данных
        db = AppDatabase.getInstance(this);
        userData = new UserData(this);

        // Загрузка аватарки
        loadAvatar();

        // Аватарка с меню выхода
        ivAvatarMenu.setOnClickListener(v -> showLogoutDialog());

        // Показать имя пользователя
        String fullName = userData.getFirstName() + " " + userData.getLastName();
        if (fullName.trim().isEmpty()) {
            fullName = "Гость";
        }
        tvUserName.setText(fullName);

        // Настройка RecyclerView
        subjectAdapter = new SubjectAdapter();
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        rvSubjects.setAdapter(subjectAdapter);

        // Поиск предмета
        actvSubjectSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSubjects(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        actvSubjectSearch.setThreshold(1);

        // Кнопка добавления предмета (только для старосты)
        if (userData.isElder()) {
            btnAddSubject.setVisibility(View.VISIBLE);
            btnAddSubject.setOnClickListener(v -> {
                startActivity(new Intent(this, AddSubjectActivity.class));
            });
        }

        // Кнопка обновления
        btnRefresh.setOnClickListener(v -> {
            loadSubjects();
            Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show();
        });

        // Загрузка предметов
        loadSubjects();

        // Обработчик клика по предмету
        subjectAdapter.setOnSubjectClickListener(subject -> {
            Intent intent = new Intent(this, SubjectDetailActivity.class);
            intent.putExtra("subject_id", subject.id);
            intent.putExtra("subject_name", subject.name);
            startActivity(intent);
        });
    }

    private void loadAvatar() {
        String avatarPath = userData.getAvatarPath();
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                ivAvatarMenu.setImageURI(Uri.parse(avatarPath));
            } catch (Exception e) {
                ivAvatarMenu.setImageResource(R.drawable.ic_avatar_default);
            }
        } else {
            ivAvatarMenu.setImageResource(R.drawable.ic_avatar_default);
        }
        // Долгое нажатие на аватарку — смена темы
        ivAvatarMenu.setOnLongClickListener(v -> {
            showThemeDialog();
            return true;
        });
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> logout())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void logout() {
        userData.clear();
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadSubjects() {
        new Thread(() -> {
            List<Subject> subjects = db.subjectDao().getAll();
            runOnUiThread(() -> {
                allSubjects = subjects;
                subjectAdapter.setSubjects(subjects);
                updateSubjectSearchAdapter(subjects);
            });
        }).start();
    }

    private void searchSubjects(String query) {
        new Thread(() -> {
            List<Subject> results;
            if (query.isEmpty()) {
                results = db.subjectDao().getAll();
            } else {
                results = db.subjectDao().search("%" + query + "%");
            }
            runOnUiThread(() -> {
                subjectAdapter.setSubjects(results);
            });
        }).start();
    }

    private void updateSubjectSearchAdapter(List<Subject> subjects) {
        List<String> names = subjects.stream().map(s -> s.name).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
        actvSubjectSearch.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubjects();
        loadAvatar();
    }
    private void showThemeDialog() {
        String[] themes = {"Светлая тема", "Тёмная тема", "Системная (по умолчанию)"};

        new AlertDialog.Builder(this)
                .setTitle("Выберите тему")
                .setItems(themes, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            setThemeMode(AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        case 1:
                            setThemeMode(AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case 2:
                            setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                    }
                })
                .show();
    }

    private void setThemeMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
        recreate(); // Перезапускаем активность для применения темы
    }
}