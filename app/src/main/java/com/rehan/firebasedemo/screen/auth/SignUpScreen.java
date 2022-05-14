package com.rehan.firebasedemo.screen.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rehan.firebasedemo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * SignUp Screen
 * View -> signup_layout.xml
 * */
public class SignUpScreen extends AppCompatActivity {

    TextInputEditText email, password, course, name;
    Button signUp;
    ImageView profileImage;
    Uri pickedImage;
    ProgressDialog pd;

    /**
     * Here we initialize Our Content View
     * And our Helper Methods
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        pd = new ProgressDialog(SignUpScreen.this);
        initUi();
        setOnListeners();
    }
    /**
     * Initializing Ui
     * */
    private void initUi() {
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInputNewAccount);
        course = findViewById(R.id.courseInput);
        name = findViewById(R.id.nameInput);
        signUp = findViewById(R.id.emailSignUp);
        profileImage = findViewById(R.id.imageView);
    }
    /**
     * Setting all click listeners
     * to one place.
     * */

    private void setOnListeners() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpWithEmail();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** here we create picker instance with intent and launch it with
                  activity result */
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultLauncher.launch(intent);
            }
        });
    }

    /**
     * Signup with email here firebase auth instance
     * with callbacks
     * */
    private void signUpWithEmail() {
        pd.setTitle("Verifying Credentials");
        pd.setMessage("Please Wait...");
        pd.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                uploadImage(authResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignUpScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Uploading image to Firebase Storage
     * with callbacks
     * */
    private void uploadImage(AuthResult authResult) {
        pd.setTitle("Uploading Image.");
        // Getting path
        String[] path = pickedImage.getPath().split("/");

        // Creating Storage Reference
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("profiles/" + authResult.getUser().getUid() + "/" + path[path.length - 1]);

        // putting file to Firebase Storage
        ref.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                pd.setMessage("Uploaded " + progress + " %");
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setUser(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignUpScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // After Uploading Setting User to Database
    private void setUser(String profileUrl) {
        pd.setTitle("Almost Done.");
        pd.setMessage("PLease Wait...");
        //creating instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Creating map
        Map<String, String> data = new HashMap<>();
        data.put("id", firebaseAuth.getCurrentUser().getUid());
        data.put("userEmail", firebaseAuth.getCurrentUser().getEmail());
        data.put("userName", name.getText().toString());
        data.put("userCourse", course.getText().toString());
        data.put("profileImage", profileUrl);
        data.put("role", "STUDENT");
        //set data to user Collection
        firebaseFirestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pd.dismiss();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(SignUpScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(SignUpScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Creating activity result launcher for picking image
     * as onActivityResult is Decrypted
     * */

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData().getData() != null) {
                            pickedImage = result.getData().getData();
                            profileImage.setImageURI(pickedImage);
                        }
                    }
                }
            });
}
