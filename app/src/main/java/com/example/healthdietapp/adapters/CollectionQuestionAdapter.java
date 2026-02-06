package com.example.healthdietapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthdietapp.R;
import com.example.healthdietapp.models.HealthQuestion;

import java.util.ArrayList;
import java.util.List;

/**
 * CollectionQuestionAdapter - Adapter for displaying collected health questions
 */
public class CollectionQuestionAdapter extends RecyclerView.Adapter<CollectionQuestionAdapter.QuestionViewHolder> {

    private List<HealthQuestion> questions;

    public CollectionQuestionAdapter() {
        this.questions = new ArrayList<>();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        HealthQuestion question = questions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void updateQuestions(List<HealthQuestion> newQuestions) {
        this.questions = newQuestions;
        notifyDataSetChanged();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView questionText;
        private TextView answerText;
        private TextView categoryText;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.questionText);
            answerText = itemView.findViewById(R.id.answerText);
            categoryText = itemView.findViewById(R.id.categoryText);
        }

        void bind(HealthQuestion question) {
            questionText.setText(question.getQuestion());
            answerText.setText(question.getAnswer());
            categoryText.setText(question.getCategory());
        }
    }
}
