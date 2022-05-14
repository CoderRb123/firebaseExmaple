package com.rehan.firebasedemo.screen.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rehan.firebasedemo.R;
import com.rehan.firebasedemo.adapter.StudentActions;
import com.rehan.firebasedemo.adapter.StudentAssignmentAdapter;
import com.rehan.firebasedemo.screen.auth.LoginScreen;

import java.util.ArrayList;
import java.util.List;


/**
 * Student home
 * View -> student_home.xml
 * */
public class StudentHome extends AppCompatActivity {
    RecyclerView studentRecyclerView;
    Toolbar studentHomeToolbar;
    FloatingActionButton upload;
    ProgressDialog pd;
    List<DocumentSnapshot> assigments = new ArrayList<>();
    StudentAssignmentAdapter assignmentAdapter;


    /**
     * Here we initialize Our Content View
     * And our Helper Methods
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_home);
        pd = new ProgressDialog(StudentHome.this);
        initUi();
        setUpToolbar();
        setClickListener();
        fetchData();
    }

    /**
     * Fetching all assignment
     * from Firebase Database
     * */
    private void fetchData() {
        pd.setTitle("Fetching Assigments");
        pd.setMessage("Please Wait...");
        pd.show();
        FirebaseFirestore.getInstance().collection("assigments").whereEqualTo("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                pd.dismiss();
                // Setting adapter
                setUpAdapter(value.getDocuments());
            }
        });
    }

    /**
     * Adapter initialization
     * After document is being fetch from
     * FireStore
     * */

    private void setUpAdapter(List<DocumentSnapshot> docs) {
        assignmentAdapter = new StudentAssignmentAdapter(
                docs,
                StudentHome.this,
                new StudentActions() {
                    @Override
                    public void onDelete(String id) {
                        FirebaseFirestore.getInstance().collection("assigments").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(StudentHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
        );
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        studentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        studentRecyclerView.setAdapter(assignmentAdapter);
    }

    // Basic toolbar menu setup
    private void setUpToolbar() {
        studentHomeToolbar.setTitle("Student Portal");
        studentHomeToolbar.inflateMenu(R.menu.mainmenu);
        studentHomeToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent intent = new Intent(StudentHome.this, LoginScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
    /**
     * Setting all click listeners
     * to one place.
     * */
    private void setClickListener() {
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentHome.this, UploadDocument.class);
                startActivity(intent);
            }
        });
    }
    /**
     * Initializing Ui
     * */
    private void initUi() {
        studentRecyclerView = findViewById(R.id.studentRecyclerView);
        studentHomeToolbar = findViewById(R.id.studentHomeToolbar);
        upload = findViewById(R.id.uploadDocument);
    }
}
