package com.mobil.mezun.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.mobil.mezun.R;
import com.mobil.mezun.service.DBService;

public class MenuActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    DBService dbService;

    private int selectedItemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assignServiceFields();

        setContentView(R.layout.activity_menu);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new HomeFragment());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.home:
                        replaceFragment(new HomeFragment());
                        selectedItemId =item.getItemId();
                        break;

                    case R.id.search:
                        replaceFragment(new PersonSearchFragment());
                        selectedItemId = item.getItemId();
                        break;

                    case R.id.profile:

                        Intent openUpdateProfile = new Intent(MenuActivity.this, UpdateProfileActivity.class);
                        startActivity(openUpdateProfile);
                        break;

                    case R.id.cikis:

                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                        builder.setTitle("ÇIKIŞ YAPILSIN MI?");


                        builder.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(MenuActivity.this, "Çıkış yaptınız",
                                        Toast.LENGTH_SHORT).show();
                                Intent openLogin = new Intent(MenuActivity.this, LoginActivity.class);
                                startActivity(openLogin);

                            }
                        });

                        builder.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Çıkış iptal edildi",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();

                        break;

                }

                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void assignServiceFields(){

        dbService = new DBService();

    }


    private void replaceFragment(Fragment fragment){

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}