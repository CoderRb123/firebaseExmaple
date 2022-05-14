package com.rehan.firebasedemo.screen.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rehan.firebasedemo.R;
import com.rehan.firebasedemo.screen.AdminHome;
import com.rehan.firebasedemo.screen.student.StudentHome;

/**
 * Login Screen
 * View -> login_layout.xml
 * */

public class LoginScreen extends AppCompatActivity {
    TextInputEditText email, password;
    Button login;
    TextView createAccount;
    ProgressDialog pd;

    /**
     * Here we initialize Our Content View
     * And our Helper Methods
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        pd = new ProgressDialog(LoginScreen.this);
        checkUser();
        initUi();
        setClickListeners();
    }

    /**
     * Checking if user is logged in
     * if true then it will fetch user data
     * from database and check role
     * */

    private void checkUser() {
        pd.setTitle("Checking Login");
        pd.setMessage("Please Wait");
        pd.show();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                checkRole();
            } else {
                pd.dismiss();
                Toast.makeText(this, "Please Verify you Email", Toast.LENGTH_SHORT).show();
            }
        }else{
            pd.dismiss();
        }
    }

    /**
     * Check user Role and redirect according
     * to it on dashboard student for student
     * and admin from admin
     * */

    private void checkRole() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String role = documentSnapshot.getData().get("role").toString();
                redirectAccordingToRole(role);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Basic Helper method to Redirect the
     * Screen
     * */
    private void redirectAccordingToRole(String role) {
        pd.dismiss();
        if (role.equals("STUDENT")) {
            Intent intent = new Intent(LoginScreen.this, StudentHome.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(LoginScreen.this, AdminHome.class);
            startActivity(intent);
        }
    }

    /**
     * Initializing Ui
     * */
    private void initUi() {
        email = findViewById(R.id.signinEmailInput);
        password = findViewById(R.id.signinPasswordInput);
        login = findViewById(R.id.loginEmail);
        createAccount = findViewById(R.id.createNewAccount);
    }
    /**
     * Setting all click listeners
     * to one place.
     * */

    private void setClickListeners() {
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Todo SignupScreen Redirect
                Intent intent = new Intent(LoginScreen.this, SignUpScreen.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mauth = FirebaseAuth.getInstance();
                mauth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Todo Redirect Main
                        checkUser();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
