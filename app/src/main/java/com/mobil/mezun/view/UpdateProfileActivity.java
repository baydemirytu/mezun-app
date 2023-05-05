package com.mobil.mezun.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobil.mezun.R;
import com.mobil.mezun.model.Job;
import com.mobil.mezun.model.User;
import com.mobil.mezun.service.DBService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UpdateProfileActivity extends AppCompatActivity {

    private Button takePhotoBtn;
    private Button choosePhotoBtn;
    private Button saveButton;
    private ImageView imageView;
    private EditText firstnameEditText;
    private EditText surnameEditText;
    private EditText entryYearEditText;
    private EditText graduateYearEditText;
    private EditText lisansEditText;
    private EditText yuksekLisansEditText;
    private EditText doktoraEditText;
    private EditText countryEditText;
    private EditText cityEditText;
    private EditText companyEditText;
    private EditText emailEditText;
    private EditText phoneEditText;

    User user = new User();

    FirebaseUser firebaseUser;

    FirebaseStorage storage;

    FirebaseFirestore db;

    DBService dbService;

    private final static int CAMERA_REQUEST_CODE = 1;

    ActivityResultLauncher<Intent> takePhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bundle extras = null;
                        if (result.getData() != null) {
                            extras = result.getData().getExtras();
                            Bitmap photoBitmap = (Bitmap) extras.get("data");

                            imageView.setImageBitmap(photoBitmap);
                        }

                    }
                }
            });

    ActivityResultLauncher<Intent> choosePhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        if (result.getData() != null) {
                            Uri photoUri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                                imageView.setImageBitmap(bitmap);
                            } catch (IOException e) {
                                Toast.makeText(UpdateProfileActivity.this, "Seçilen fotoğraf bulunamadı",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        assignServiceFields();
        assignViewFields();

        dbService.getUserFromDB(this::updateUiFromUser);

        assignOnClickListeners();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(UpdateProfileActivity.this, "Kameranıza erişim izni alınamadı",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void assignViewFields(){

        firstnameEditText = (EditText) findViewById(R.id.firstnameEditText);
        surnameEditText = (EditText) findViewById(R.id.surnameEditText);
        entryYearEditText = (EditText) findViewById(R.id.entryYearEditText);
        graduateYearEditText = (EditText) findViewById(R.id.graduateYearEditText);
        lisansEditText = (EditText) findViewById(R.id.lisansEditText);
        yuksekLisansEditText = (EditText) findViewById(R.id.yuksekLisansEditText);
        doktoraEditText = (EditText) findViewById(R.id.doktoraEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);
        companyEditText = (EditText) findViewById(R.id.companyEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);



        takePhotoBtn = (Button) findViewById(R.id.takePhotoBtn);
        choosePhotoBtn = (Button) findViewById(R.id.choosePhotoBtn);
        saveButton = (Button) findViewById(R.id.saveButton);
        imageView = (ImageView) findViewById(R.id.takePhotoView);

    }

    public void assignServiceFields(){

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        dbService = new DBService();

    }

    public void assignOnClickListeners(){

        takePhotoBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(UpdateProfileActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UpdateProfileActivity.this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            } else {
                takePhoto();
            }
        });

        choosePhotoBtn.setOnClickListener(view -> {

            Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            choosePhotoResultLauncher.launch(choosePhotoIntent);
        });


        saveButton.setOnClickListener(view -> {

            saveUserToDB(userFromUi());

        });

    }

    public void saveUserToDB(User tmpUser){

        if(!Objects.equals(tmpUser.getFirstname(), "") && !Objects.equals(tmpUser.getSurname(), "") && tmpUser.getEntry_year() != 0 && tmpUser.getGraduate_year() != 0){
            db.collection("users").document(firebaseUser.getUid()).set(tmpUser).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(UpdateProfileActivity.this, "Bilgileriniz güncellendi",
                            Toast.LENGTH_SHORT).show();


                }
            });

            imageView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] bytes = baos.toByteArray();

            StorageReference avatarRef = storage.getReference();
            avatarRef = avatarRef.child("avatars/" + firebaseUser.getUid() +".png");
            UploadTask uploadTask = avatarRef.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UpdateProfileActivity.this, "Fotoğraf yüklendi",
                            Toast.LENGTH_SHORT).show();

                    Intent openMenu = new Intent(UpdateProfileActivity.this, MenuActivity.class);
                    startActivity(openMenu);

                }
            });
        }
        else{
            Toast.makeText(UpdateProfileActivity.this, "Lütfen kırmızı alanları doldurun",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void takePhoto() {

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {

            takePhotoResultLauncher.launch(takePhotoIntent);

        } else {
            Toast.makeText(UpdateProfileActivity.this, "Kamera uygulaması bulunamadı",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public User userFromUi(){

        User tmpUser = new User();
        Job tmpJob = new Job();

        tmpJob.setCountry(String.valueOf(countryEditText.getText()));
        tmpJob.setCity(String.valueOf(cityEditText.getText()));
        tmpJob.setCompany(String.valueOf(companyEditText.getText()));

        tmpUser.setId(firebaseUser.getUid());
        tmpUser.setFirstname(String.valueOf(firstnameEditText.getText()));
        tmpUser.setSurname(String.valueOf(surnameEditText.getText()));
        tmpUser.setEntry_year(Integer.parseInt(String.valueOf(entryYearEditText.getText())));
        tmpUser.setGraduate_year(Integer.parseInt(String.valueOf(graduateYearEditText.getText())));
        tmpUser.setLisans_name(String.valueOf(lisansEditText.getText()));
        tmpUser.setYuksek_name(String.valueOf(yuksekLisansEditText.getText()));
        tmpUser.setDoktora_name(String.valueOf(doktoraEditText.getText()));
        tmpUser.setCommunication_email(String.valueOf(emailEditText.getText()));
        tmpUser.setPhone_number(String.valueOf(phoneEditText.getText()));
        tmpUser.setJob(tmpJob);

        return tmpUser;
    }

    public void updateUiFromUser(User user){

        firstnameEditText.setText(user.getFirstname());
        surnameEditText.setText(user.getSurname());
        entryYearEditText.setText(String.valueOf(user.getEntry_year()));
        graduateYearEditText.setText(String.valueOf(user.getGraduate_year()));
        lisansEditText.setText(user.getLisans_name());
        yuksekLisansEditText.setText(user.getYuksek_name());
        doktoraEditText.setText(user.getDoktora_name());
        countryEditText.setText(user.getJob().getCountry());
        cityEditText.setText(user.getJob().getCity());
        companyEditText.setText(user.getJob().getCompany());
        emailEditText.setText(user.getCommunication_email());
        phoneEditText.setText(user.getPhone_number());


        try {

            StorageReference avatarRef = storage.getReference();
            avatarRef = avatarRef.child("avatars/" + firebaseUser.getUid() + ".png");

            final long tenMB = 10 * 1024 *1024;
            avatarRef.getBytes(tenMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            });


        }catch (IllegalArgumentException ex){
            Toast.makeText(UpdateProfileActivity.this, "Fotoğraf bilgisi alınamadı",
                    Toast.LENGTH_SHORT).show();
        }

    }




}