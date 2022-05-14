package com.rehan.firebasedemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.rehan.firebasedemo.R;

import java.util.List;
/**
 * Student Assignment  Adapter
 * This is Normal Adapter to View All Assignment
 * From Firebase According to email. it Take
 * Document Snapshot List Context and
 * StudentActions "Interface" as Argument.
 * */

public class StudentAssignmentAdapter extends RecyclerView.Adapter<StudentAssignmentAdapter.MyViewHolder> {

    List<DocumentSnapshot> assigments;
    Activity context;
    StudentActions studentActions;
    /*Constructor */
    public StudentAssignmentAdapter(List<DocumentSnapshot> assigments, Activity context, StudentActions studentActions) {
        this.assigments = assigments;
        this.context = context;
        this.studentActions = studentActions;
    }

    /**
     * On Created Overriding from Adapter Class on
     * This Method we assign Our View on Our ViewHolder
     * */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_assignment_card, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * On onBindViewHolder Also Overriding from
     * Adapter Class Here we bind our data from
     * List By Extracting single item from list
     * and accessing view from MyViewHolder
     *  **/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DocumentSnapshot item = assigments.get(position);
        holder.studentSubjectName.setText(item.get("subjectName").toString());
        holder.status.setText(item.get("status").toString());
        /**
         * OnDelete Initialization Call from interface
         * */
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentActions.onDelete(item.getId());
            }
        });
        /**
         * Here in View Button Listener we are Passing our link
         * as data and then start as intent to view in default
         * pdf app
         * */
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(item.get("assigmentUrl").toString()),"application/pdf");

                Intent chooser = Intent.createChooser(intent,"PDF");
                context.startActivity(chooser);
            }
        });
    }

    /**
     * GetItemCount Overriding from Adapter Class
     * Here we need to tell adapter size of our items
     * like item count how much we have to show
     * */
    @Override
    public int getItemCount() {
        return assigments.size();
    }


    /**
     * View Holder Which hold over view for Adapter
     * Here we assign our view item id to be access from
     * onBindView which is override method from Adapter
     * */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView studentSubjectName, status;
        Button view;
        ImageButton delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            studentSubjectName = itemView.findViewById(R.id.studentSubjectName);
            status = itemView.findViewById(R.id.status);
            view = itemView.findViewById(R.id.btnView);
            delete = itemView.findViewById(R.id.btnDeleteAssignment);
        }
    }
}
