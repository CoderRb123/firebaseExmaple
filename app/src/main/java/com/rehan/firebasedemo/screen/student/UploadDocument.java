package com.rehan.firebasedemo.screen.student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rehan.firebasedemo.R;
import com.rehan.firebasedemo.screen.auth.SignUpScreen;

import java.util.HashMap;
import java.util.Map;
/**
 * Upload  Document
 * View -> upload_document.xml
 * */
public class UploadDocument extends AppCompatActivity {

    Button documentPicker, upload;
    TextInputEditText courseName;
    Uri pickedPdf;
    ProgressDialog pd;

    /**
     * Here we initialize Our Content View
     * And our Helper Methods
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_document);
        pd = new ProgressDialog(UploadDocument.this);
        initUi();
        setClickListner();

    }

    /**
     * Setting all click listeners
     * to one place.
     * */
    private void setClickListner() {
        documentPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                activityResultContract.launch(intent);
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pickedPdf==null){
                    Toast.makeText(UploadDocument.this, "Pdf Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(courseName.getText().toString().isEmpty()){
                    Toast.makeText(UploadDocument.this, "Course name Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadDocument();
            }
        });
    }
    /**
     * Upload Document to Firebase Storage
     * and Then Taking action Accordingly
     * */
    private void uploadDocument() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        pd.setTitle("Uploading Image.");
        // getting path
        String[] path = pickedPdf.getPath().split("/");
        // Storage Reference
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("assigments/" + firebaseAuth.getCurrentUser().getUid() + "/" + path[path.length - 1]);
        // Uploading PDF
        ref.putFile(pickedPdf).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) ((taskSnapshot.getBytesTransferred() * 100) / taskSnapshot.getTotalByteCount());
                pd.setMessage("Uploaded " + progress + " %");
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setAssigment(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(UploadDocument.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadDocument.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Set Assignment data to Firestore
     * */
    private void setAssigment(String url) {
        pd.setTitle("Almost Done.");
        pd.setMessage("PLease Wait...");
        // Firebase Auth and Firestore init
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        // Creating data variable for our table data
        Map<String, String> data = new HashMap<>();
        data.put("id", firebaseAuth.getCurrentUser().getUid());
        data.put("userEmail", firebaseAuth.getCurrentUser().getEmail());
        data.put("subjectName", courseName.getText().toString());
        data.put("assigmentUrl", url);
        data.put("status","UNSEEN");
        firebaseFirestore.collection("assigments").document().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                pd.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadDocument.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Activity Result Contract for Picking
     * Pdf file.
     * */
    ActivityResultLauncher<Intent> activityResultContract = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData().getData() != null) {
                    pickedPdf = result.getData().getData();
                    String[] path = pickedPdf.getPath().split("/");
                    documentPicker.setText(path[path.length - 1]);
                }
            }
        }
    });

    /**
     * Initializing Ui
     * */
    private void initUi() {
        documentPicker = findViewById(R.id.documentPicker);
        courseName = findViewById(R.id.documentTitleInput);
        upload = findViewById(R.id.uploadDoc);
    }
}
