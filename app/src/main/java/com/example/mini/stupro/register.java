package com.example.mini.stupro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    private ImageView imgbtn;
    EditText t1, t2, t3, t4;

    Button btn;

FirebaseAuth fauth;
DatabaseReference mdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        imgbtn = (ImageView) findViewById(R.id.closebtn);
        t1 = (EditText) findViewById(R.id.username);
        t2 = (EditText) findViewById(R.id.password);
        t3 = (EditText) findViewById(R.id.phonenumber);
        t4 = (EditText) findViewById(R.id.name);
        fauth=FirebaseAuth.getInstance();
        mdata=FirebaseDatabase.getInstance().getReference().child("Users");

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register.super.onBackPressed();
            }
        });


        btn = (Button) findViewById(R.id.regbtn);

          btn.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
              final String username,password,name,phonenumber;

              username=t1.getText().toString();
              password=t2.getText().toString();
              name=t4.getText().toString();
              phonenumber=t3.getText().toString();

              fauth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                  if(task.isSuccessful())
                  {
                     String user_id=fauth.getCurrentUser().getUid();
                      Toast.makeText(register.this, "Registration Successful....", Toast.LENGTH_SHORT).show();

                      DatabaseReference data= mdata.child(user_id);
                      data.setValue(true);
                      data.child("Name").setValue(name);
                      data.child("Phone Number").setValue(phonenumber);
                      data.child("Username").setValue(username);
                      startActivity(new Intent(register.this,join.class));

                  }
                  else
                  {
                      FirebaseAuthException e = (FirebaseAuthException)task.getException();
                      Toast.makeText(register.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();

                      return;
                  }


                  }
              });





              }
          });

    }
}










