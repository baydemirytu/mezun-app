package com.mobil.mezun.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobil.mezun.R;
import com.mobil.mezun.service.DBService;
import com.mobil.mezun.view.adapter.PostItemAdapter;
import com.mobil.mezun.view.adapter.UserListItemAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HomeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    DBService dbService;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    RecyclerView recyclerView;

    UserListItemAdapter userListItemAdapter;

    boolean alertDialogPostPhotoChosen = false;

    ImageView alertDialogImageView;
    FloatingActionButton floatingActionButton;
    PostItemAdapter postItemAdapter;

    ActivityResultLauncher<Intent> choosePhotoResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        if (result.getData() != null) {
                            Uri photoUri = result.getData().getData();
                            try {
                                alertDialogImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri));
                                alertDialogPostPhotoChosen = true;

                            } catch (IOException e) {
                                Toast.makeText(getContext(), "Seçilen fotoğraf bulunamadı",
                                        Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                }
            });

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        assignServiceFields();

        dbService.getPostListFromDB(postList -> {

            postItemAdapter = new PostItemAdapter(postList, getContext());
            recyclerView.setAdapter(postItemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        });




    }



    public void assignServiceFields(){

        db = FirebaseFirestore.getInstance();
        dbService = new DBService();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.postItemList);
        floatingActionButton = view.findViewById(R.id.postAddFloatingActionButton);

        floatingActionButton.setOnClickListener(tmpView -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Yeni Duyuru Oluştur");

            LinearLayout verticalLayout = new LinearLayout(getContext());
            verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            verticalLayout.setOrientation(LinearLayout.VERTICAL);


            final EditText message = new EditText(getContext());
            message.setInputType(InputType.TYPE_CLASS_TEXT);
            message.setHint("Duyuru mesajınızı girin");



            alertDialogImageView = new ImageView(getContext());
            alertDialogImageView.setMaxWidth(50);
            alertDialogImageView.setMaxHeight(50);
            alertDialogImageView.setImageBitmap(null);
            alertDialogImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final Button choosePhotoButton = new Button(getContext());
            choosePhotoButton.setText("Fotoğraf Seç");

            choosePhotoButton.setOnClickListener(view1 -> {


                Intent choosePhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                choosePhotoResultLauncher.launch(choosePhotoIntent);

            });

            verticalLayout.addView(message);
            verticalLayout.addView(alertDialogImageView);
            verticalLayout.addView(choosePhotoButton);

            builder.setView(verticalLayout);

            builder.setPositiveButton("OLUŞTUR", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String postId = UUID.randomUUID().toString();

                    alertDialogImageView.buildDrawingCache();
                    Bitmap bitmap = Bitmap.createBitmap(alertDialogImageView.getDrawingCache());
                    if(alertDialogPostPhotoChosen){
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                        byte[] bytes = baos.toByteArray();

                        StorageReference avatarRef = FirebaseStorage.getInstance().getReference();
                        avatarRef = avatarRef.child("posts/" + postId + ".png");
                        UploadTask uploadTask = avatarRef.putBytes(bytes);

                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if(task.isSuccessful()){
                                    HomeFragment.this.alertDialogImageView = null;
                                    HomeFragment.this.alertDialogPostPhotoChosen = false;
                                    Map<String, Object> postTemplate = new HashMap<>();

                                    postTemplate.put("postId", postId);
                                    postTemplate.put("userId", firebaseUser.getUid());
                                    postTemplate.put("message", message.getText().toString());
                                    postTemplate.put("imageId", postId);

                                    db.collection("posts").document(postId).set(postTemplate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getContext(), "Yeni duyuru eklendi",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        });

                    }else{

                        Map<String, Object> postTemplate = new HashMap<>();

                        postTemplate.put("postId", postId);
                        postTemplate.put("userId", firebaseUser.getUid());
                        postTemplate.put("message", message.getText().toString());
                        postTemplate.put("imageId", "");

                        db.collection("posts").document(postId).set(postTemplate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                HomeFragment.this.alertDialogPostPhotoChosen = false;
                                HomeFragment.this.alertDialogImageView = null;
                                Toast.makeText(getContext(), "Yeni duyuru eklendi",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }





                }
            });

            builder.setNegativeButton("İptal", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getContext(), "Duyuru iptal edildi",
                            Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();
        });


        return view;

    }
}