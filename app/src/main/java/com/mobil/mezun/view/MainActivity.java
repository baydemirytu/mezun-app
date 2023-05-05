package com.mobil.mezun.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobil.mezun.R;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseAuth.getInstance().signOut();
        FirebaseUser user = firebaseAuth.getCurrentUser();



        final Handler handler= new Handler();
        handler.postDelayed(() -> {
            if(user == null){
                Intent openLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(openLogin);
            }
            else {
                Intent openMenu = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(openMenu);
            }
        }, 3000);


    }
}