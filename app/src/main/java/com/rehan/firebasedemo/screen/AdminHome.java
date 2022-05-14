package com.rehan.firebasedemo.screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rehan.firebasedemo.R;
import com.rehan.firebasedemo.adapter.AdminAction;
import com.rehan.firebasedemo.adapter.AdminAssigmentAdapter;
import com.rehan.firebasedemo.screen.auth.LoginScreen;
import com.rehan.firebasedemo.screen.student.StudentHome;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Admin  Home
 * View -> admin_layout.xml
 * */
public class AdminHome extends AppCompatActivity {
    RecyclerView adminRecyclerView;
    Toolbar adminToolbar;
    AdminAssigmentAdapter adminAssigmentAdapter;

    /**
     * Here we initialize Our Content View
     * And our Helper Methods
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);
        initUi();
        setUpToolbar();
        fetchData();
    }
   /**
    * Fetch Data from Firestore and
    * assign adapter
    * */
    private void fetchData() {
        FirebaseFirestore.getInstance().collection("assigments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                setUpAdapter(value.getDocuments());
            }
        });
    }

    /**
     * Setting Adapter
     * */
    private void setUpAdapter(List<DocumentSnapshot> documents) {
        adminAssigmentAdapter = new AdminAssigmentAdapter(documents, AdminHome.this, new AdminAction() {
            @Override
            public void onAccept(String id) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "ACCEPTED");
                FirebaseFirestore.getInstance().collection("assigments").document(id).update(data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onReject(String id) {
                Map<String, Object> data = new HashMap<>();
                data.put("status", "REJECT");
                FirebaseFirestore.getInstance().collection("assigments").document(id).update(data).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        adminRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adminRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adminRecyclerView.setAdapter(adminAssigmentAdapter);
    }

    // basic Toolbar Adapter
    private void setUpToolbar() {
        adminToolbar.setTitle("Admin Portal");
        adminToolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
        adminToolbar.inflateMenu(R.menu.mainmenu);
        adminToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    Intent intent = new Intent(AdminHome.this, LoginScreen.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }


    /**
     * Initializing Ui
     * */
    private void initUi() {
        adminRecyclerView = findViewById(R.id.adminRecyclerView);
        adminToolbar = findViewById(R.id.adminToolbar);

    }
}
