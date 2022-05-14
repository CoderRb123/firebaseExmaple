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
* Admin Assignment  Adapter
* This is Normal Adapter to View All Assignment
* From Firebase it Take Document Snapshot List
* Context and AdminAction "Interface" as Argument.
* */

public class AdminAssigmentAdapter extends RecyclerView
        .Adapter<AdminAssigmentAdapter.MyViewHolder> {

    List<DocumentSnapshot> assigments;
    Activity context;
    AdminAction adminAction;

    /*Constructor */

    public AdminAssigmentAdapter(List<DocumentSnapshot> assigments, Activity context, AdminAction adminAction) {
        this.assigments = assigments;
        this.context = context;
        this.adminAction = adminAction;
    }

    /**
     * On Created Overriding from Adapter Class on
    * This Method we assign Our View on Our ViewHolder
    * */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_assignment_card, parent, false);
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
     holder.name.setText(item.get("userEmail").toString());
     holder.subject.setText(item.get("subjectName").toString());
     /**
      *  Here we are comparing if status is UNSEEN
      *  then then we ser visibility for accepted
      *  button and reject button VISIBLE else
      *  we will Hide Them Also here we set setClick
      *  Listener
      * */
     if(item.get("status").toString().equals("UNSEEN")){
         holder.accept.setVisibility(View.VISIBLE);
         holder.accept.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 adminAction.onAccept(item.getId());
             }
         });
         holder.reject.setVisibility(View.VISIBLE);
         holder.reject.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 adminAction.onReject(item.getId());
             }
         });
     }else{
         holder.accept.setVisibility(View.GONE);
         holder.reject.setVisibility(View.GONE);
     }
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
    static  class  MyViewHolder extends  RecyclerView.ViewHolder{
       TextView name,subject;
       Button view;
       ImageButton accept,reject;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.adminStudentName);
            subject = itemView.findViewById(R.id.adminSubjectName);
            view = itemView.findViewById(R.id.adminViewButton);
            accept = itemView.findViewById(R.id.adminApproveAssignment);
            reject = itemView.findViewById(R.id.adminCancelAssignment);
        }
    }
}
