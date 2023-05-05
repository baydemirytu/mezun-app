package com.mobil.mezun.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobil.mezun.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signupBtn;
    private FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = (EditText) findViewById(R.id.editTextTextPassword);

        signupBtn = (Button) findViewById(R.id.signup);

        signupBtn.setOnClickListener(view -> {
            try {
                signupWithEmailPassword(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }catch (IllegalArgumentException illegalArgumentException){
                Toast.makeText(SignupActivity.this, "Alanlar boş bırakılamaz.",
                        Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void signupWithEmailPassword(@NonNull String email, @NonNull String password){

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth = FirebaseAuth.getInstance();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    System.out.println(firebaseUser.getUid());

                    if(firebaseUser!=null ){
                        createUserDocument(firebaseUser.getUid());

                        Intent openMenu = new Intent(SignupActivity.this, UpdateProfileActivity.class);
                        startActivity(openMenu);
                    } else{

                        Toast.makeText(SignupActivity.this, "Ortak bilgiler girilirken hata oluştu",
                                Toast.LENGTH_SHORT).show();


                    }

                } else {
                    Toast.makeText(SignupActivity.this, "Kayıt başarısız",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void createUserDocument(String userId){

        Map<String, Object> userDocumentTemplate = new HashMap<>();
        Map<String, Object> jobTemplate = new HashMap<>();

        jobTemplate.put("country", "");
        jobTemplate.put("city", "");
        jobTemplate.put("company", "");

        userDocumentTemplate.put("id", userId);
        userDocumentTemplate.put("firstname", "");
        userDocumentTemplate.put("surname", "");
        userDocumentTemplate.put("entry_year", 0);
        userDocumentTemplate.put("graduate_year", 0);
        userDocumentTemplate.put("communication_email", "");
        userDocumentTemplate.put("phone_number", "");
        userDocumentTemplate.put("lisans_name", "");
        userDocumentTemplate.put("yuksek_name", "");
        userDocumentTemplate.put("doktora_name", "");
        userDocumentTemplate.put("job", jobTemplate);

        db.collection("users").document(userId).set(userDocumentTemplate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(SignupActivity.this, "Kayıt Oldunuz",
                        Toast.LENGTH_SHORT).show();
            }
        });



    }

}