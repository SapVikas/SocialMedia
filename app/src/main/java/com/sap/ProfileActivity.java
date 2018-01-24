package com.sap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 101;
    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail , name,dob,mob;
    private Button buttonLogout;

    private FirebaseUser user1;
    private DatabaseReference db;
     CircleImageView profpic;
     Uri filePath;
    String profileImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        name=findViewById(R.id.textViewUserName);
        dob=findViewById(R.id.textViewUserDob);
        mob=findViewById(R.id.textViewUserMob);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        profpic = findViewById(R.id.iv8);
        profpic.setOnClickListener(this);

        name.setText(user.getDisplayName());
        textViewUserEmail.setText(user.getEmail());
        buttonLogout.setOnClickListener(this);
        loadUserDob();
        loadUserMob();
        loadUserProfPic();
    }

    private void loadUserProfPic() {

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user!=null){
                if(user.getPhotoUrl()!=null){
                    Glide.with(this).load(user.getPhotoUrl().toString()).into(profpic);
                }


            }

    }

    private void loadUserDob() {
        user1=FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseDatabase.getInstance().getReference().child("users").child(user1.getUid()).child("dob");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dob1= dataSnapshot.getValue().toString();
                dob.setText(dob1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadUserMob() {
        user1=FirebaseAuth.getInstance().getCurrentUser();
        db= FirebaseDatabase.getInstance().getReference().child("users").child(user1.getUid()).child("phn");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mob1= dataSnapshot.getValue().toString();
                mob.setText(mob1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view==profpic){
            showImageChooser();
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                profpic.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase() {
        StorageReference profileImageReference = FirebaseStorage.getInstance().getReference("profilepics/ " + System.currentTimeMillis() + ".jpg");

        if(filePath!=null){
            profileImageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                    saveUserInfo();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this ,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void saveUserInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile Picture Please Wait...");
        progressDialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null) {

            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Profile Pic Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}