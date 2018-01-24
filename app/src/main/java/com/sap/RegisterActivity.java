package com.sap;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail,editTextPassword,editTextName,editTextPassword2,editTextMob,editTextDob;
    private Button buttonSignup;

    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;

    FirebaseStorage  storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://social-media-d93f6.appspot.com");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();

            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword2= findViewById(R.id.editTextPassword2);
        editTextMob=findViewById(R.id.editMob);
        editTextDob=findViewById(R.id.editDob);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){
        String name= editTextName.getText().toString();
        String email = editTextEmail.getText().toString().trim();
        String password  = editTextPassword.getText().toString().trim();
        String password2  = editTextPassword2.getText().toString().trim();
        String pas1=editTextPassword.getText().toString();
        String pas2=editTextPassword2.getText().toString();
       final String dob= editTextDob.getText().toString();
        final String mob= editTextMob.getText().toString();

        if(name.isEmpty()){
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return;
        }

        if(dob.isEmpty()){
            editTextDob.setError("DOB required");
            editTextDob.requestFocus();
            return;
        }
        if(mob.isEmpty()){
            editTextMob.setError("Mobile No. required");
            editTextMob.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password2)){
            Toast.makeText(this,"Please enter confirm password", Toast.LENGTH_LONG).show();
            return;
        }



        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        if (pas1.equals(pas2)){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userInfo();
                            veryfyEmail();
                            displayNameUser();
                            Toast.makeText(RegisterActivity.this,"Verification Mail Sent Successfully",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this,"Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });}else{
            Toast.makeText(RegisterActivity.this,"Psswords dont match", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

    }

    private void userInfo() {
      String dob2 = editTextDob.getText().toString();
      String mob2 = editTextMob.getText().toString();
      Users sap = new Users(dob2,mob2);
      db = FirebaseDatabase.getInstance().getReference("users");
      FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      String uid = user.getUid();
      db.child(uid).setValue(sap);

    }

    private void displayNameUser() {
        String displayName= editTextName.getText().toString();
        user = firebaseAuth.getCurrentUser();
        if (displayName!=null){
            UserProfileChangeRequest profile= new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
            user.updateProfile(profile);
        }
    }

    private void veryfyEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            registerUser();
        }

        if(view == textViewSignin){
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}