package com.hsm.macs.campusguide.courseinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hsm.macs.campusguide.R;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    private int lastPosition = -1;

    private List<String> subjects;
    private Context context;

    // Constructor to initialize the adapter with data
    public SubjectAdapter(Context context, List<String> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    // Create ViewHolder objects to represent items in the RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.textViewSubject); // Replace with your TextView's ID
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to the views in each ViewHolder
        String subject = subjects.get(position);
        holder.subjectTextView.setText(subject);
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the data set
        return subjects.size();
    }
}