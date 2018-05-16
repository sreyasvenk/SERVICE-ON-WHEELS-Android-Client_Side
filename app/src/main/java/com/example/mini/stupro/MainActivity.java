package com.example.mini.stupro;



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    LinearLayout l1,l2;
    Animation animation,animation2;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l1=(LinearLayout)findViewById(R.id.top);
        l2=(LinearLayout)findViewById(R.id.bottom);
        t1=(TextView)findViewById(R.id.date);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm");
        String dateString = sdf.format(date);
        t1.setText(dateString.toString());


        animation= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        l1.setAnimation(animation);

        animation2= AnimationUtils.loadAnimation(this,R.anim.downtoup);
        l2.setAnimation(animation2);

        Thread thread=new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(4000);
                    Intent intent=new Intent(getApplicationContext(),login.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.goup, R.anim.godown);
                    finish();
                }catch(InterruptedException e)
                {e.printStackTrace();
                }
                super.run();
            }
        };
        thread.start();


    }
}

