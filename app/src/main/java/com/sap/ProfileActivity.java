package com.sap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail , name,dob,mob;
    private Button buttonLogout;

    private FirebaseUser user1;
    private DatabaseReference db;
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

        name.setText(user.getDisplayName());
        textViewUserEmail.setText(user.getEmail());
        buttonLogout.setOnClickListener(this);
        loadUserDob();
        loadUserMob();
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
    }
}