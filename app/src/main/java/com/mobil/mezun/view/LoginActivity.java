package com.mobil.mezun.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobil.mezun.R;
import com.mobil.mezun.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginBtn;
    private Button signupBtn;

    TextView forgotPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signOut();

        emailEditText = (EditText) findViewById(R.id.editTextTextEmailAddress);
        passwordEditText = (EditText) findViewById(R.id.editTextTextPassword);
        forgotPassword = findViewById(R.id.forgotPassword);

        loginBtn = (Button) findViewById(R.id.login);
        signupBtn = (Button) findViewById(R.id.signup);

        loginBtn.setOnClickListener(view -> {

            try {
                loginWithEmailPassword(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }catch (IllegalArgumentException illegalArgumentException){
                Toast.makeText(LoginActivity.this, "Alanlar boş bırakılamaz",
                        Toast.LENGTH_SHORT).show();
            }


        });

        signupBtn.setOnClickListener(view -> {

            Intent openSignup = new Intent(this, SignupActivity.class);
            startActivity(openSignup);

        });

        forgotPassword.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Email adresinizi girin");

            final EditText mail = new EditText(LoginActivity.this);
            mail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mail.setHint("Sıfırlama linki gönderilecek mail adresiniz");

            builder.setView(mail);

            builder.setPositiveButton("Gönder",  new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firebaseAuth.sendPasswordResetEmail(mail.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Şifre sıfırlama linki gönderildi",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Bir hata oluştu",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });

            builder.setNegativeButton("İptal", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(LoginActivity.this, "Şifre sıfırlama iptal edildi",
                            Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();



        });


    }

    private void loginWithEmailPassword(@NonNull String email,@NonNull String password){

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Giriş Yaptınız",
                            Toast.LENGTH_SHORT).show();

                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    DocumentReference userDocRef = db.collection("users").document(firebaseUser.getUid());
                    userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User docUser = documentSnapshot.toObject(User.class);
                            if(docUser.getFirstname() == null || docUser.getSurname() == null || docUser.getEntry_year() == 0 || docUser.getGraduate_year() == 0){
                                Intent openUpdateProfile = new Intent(LoginActivity.this, UpdateProfileActivity.class);
                                startActivity(openUpdateProfile);
                            }
                            else{
                                Intent openMenu = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(openMenu);
                            }

                        }
                    });



                } else {
                    Toast.makeText(LoginActivity.this, "Giriş başarısız",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}