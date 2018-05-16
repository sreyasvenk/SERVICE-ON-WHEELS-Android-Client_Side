package com.example.mini.stupro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    FirebaseAuth fauth;
FirebaseAuth.AuthStateListener auths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         fauth= FirebaseAuth.getInstance();
         auths= new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user=fauth.getCurrentUser();
            if(user!=null)
            {
             startActivity(new Intent(login.this,MainPageActivity.class));
             finish();


            }


             }
         } ;

    }

    public void joinPress(View view) {
        Intent intent = new Intent(this, join.class);
        startActivity(intent);
    }

    public void registerPress(View view)
    {
        Intent intent=new Intent(this,register.class);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        fauth.addAuthStateListener(auths);

    }

    @Override
    protected void onStop() {
        super.onStop();
        fauth.removeAuthStateListener(auths);
    }
}
