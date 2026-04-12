package com.example.ygad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.ViewHolder> {

    private List<GradeWithStudent> grades = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public void setGrades(List<GradeWithStudent> grades) {
        this.grades = grades;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GradeWithStudent grade = grades.get(position);

        String displayText = grade.studentLastName + " " + grade.studentFirstName + " — " + grade.gradeValue;
        holder.text1.setText(displayText);
        holder.text2.setText(sdf.format(new java.util.Date(Long.parseLong(grade.gradeDate))));

        if (grade.gradeValue < 3) {
            holder.text1.setTextColor(0xFFFF0000);
        } else {
            holder.text1.setTextColor(0xFF000000);
        }
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;
        ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}