package com.example.mini.stupro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class join extends AppCompatActivity {
EditText t1,t2;
    private FirebaseAuth fauth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
Button bt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        t1=(EditText)findViewById(R.id.user);
        t2=(EditText)findViewById(R.id.pass);
        bt1=(Button)findViewById(R.id.logbtn);
        fauth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(join.this, "PROCESSING….", Toast.LENGTH_LONG).show();
                String email = t1.getText().toString().trim();
                String password = t2.getText().toString().trim();
                if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)){
                    fauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                checkUserExistence();
                            }else {
                                Toast.makeText(join.this, "Username/Password is incorrecct", Toast.LENGTH_SHORT).show();
                            } } });
                }else {
                    Toast.makeText(join.this, "Complete all fields", Toast.LENGTH_SHORT).show();
                } }

        });






    }

    public void checkUserExistence(){
        final String user_id = fauth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)){
                    startActivity(new Intent(join.this, MainPageActivity.class));
                }else {
                    Toast.makeText(join.this, "User not registered!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }




}


