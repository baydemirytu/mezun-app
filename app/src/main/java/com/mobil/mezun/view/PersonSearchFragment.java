package com.mobil.mezun.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobil.mezun.R;
import com.mobil.mezun.model.User;
import com.mobil.mezun.service.DBService;
import com.mobil.mezun.view.adapter.UserListItemAdapter;

import java.util.ArrayList;


public class PersonSearchFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    FirebaseFirestore db;
    DBService dbService;

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    UserListItemAdapter userListItemAdapter;


    public PersonSearchFragment() {
    }

    public static PersonSearchFragment newInstance(String param1, String param2) {
        PersonSearchFragment fragment = new PersonSearchFragment();
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



        dbService.getUserListFromDB(userList -> {
            userListItemAdapter = new UserListItemAdapter(userList, getContext());

            recyclerView.setAdapter(userListItemAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        });







    }

    public void assignServiceFields(){

        db = FirebaseFirestore.getInstance();
        dbService = new DBService();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_search, container, false);

        recyclerView = view.findViewById(R.id.userItemList);

        floatingActionButton = view.findViewById(R.id.personSearchFloatingActionButton);

        floatingActionButton.setOnClickListener(tmpView -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Mezunları Filtrele");

            LinearLayout verticalLayout = new LinearLayout(getContext());
            verticalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            verticalLayout.setOrientation(LinearLayout.VERTICAL);



            final EditText graduate_year = new EditText(getContext());
            graduate_year.setInputType(InputType.TYPE_CLASS_NUMBER);
            graduate_year.setHint("Şu yıldan önceki mezunlar");


            final EditText country = new EditText(getContext());
            country.setInputType(InputType.TYPE_CLASS_TEXT);
            country.setHint("Şu ülkedeki mezunlar");


            verticalLayout.addView(graduate_year);
            verticalLayout.addView(country);

            builder.setView(verticalLayout);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.out.println(graduate_year.getText().toString());

                    if(!graduate_year.getText().toString().equals("")){

                        if(!country.getText().toString().equals("")){

                            FieldPath fieldPath = FieldPath.of("job", "country");

                            db.collection("users")
                                    .whereLessThanOrEqualTo("graduate_year", Integer.parseInt(graduate_year.getText().toString()))
                                    .whereEqualTo(fieldPath, country.getText().toString())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){

                                                ArrayList<User> userList = new ArrayList<>();

                                                for(QueryDocumentSnapshot tmpUserDoc: task.getResult()){
                                                    userList.add(tmpUserDoc.toObject(User.class));
                                                }
                                                userListItemAdapter = new UserListItemAdapter(userList, getContext());
                                                recyclerView.setAdapter(userListItemAdapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                Toast.makeText(getContext(), userList.size() + " kişi bulundu",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                        else{
                            db.collection("users")
                                    .whereLessThanOrEqualTo("graduate_year", Integer.parseInt(graduate_year.getText().toString()))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){

                                                ArrayList<User> userList = new ArrayList<>();

                                                for(QueryDocumentSnapshot tmpUserDoc: task.getResult()){
                                                    userList.add(tmpUserDoc.toObject(User.class));
                                                }
                                                userListItemAdapter = new UserListItemAdapter(userList, getContext());
                                                recyclerView.setAdapter(userListItemAdapter);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                Toast.makeText(getContext(), userList.size() + " kişi bulundu",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }




                    }
                    else if (!country.getText().toString().equals("")){
                        FieldPath fieldPath = FieldPath.of("job", "country");

                        db.collection("users")
                                .whereEqualTo(fieldPath, country.getText().toString())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){

                                            ArrayList<User> userList = new ArrayList<>();

                                            for(QueryDocumentSnapshot tmpUserDoc: task.getResult()){
                                                userList.add(tmpUserDoc.toObject(User.class));
                                            }


                                            userListItemAdapter = new UserListItemAdapter(userList, getContext());
                                            recyclerView.setAdapter(userListItemAdapter);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                            Toast.makeText(getContext(), userList.size() + " kişi bulundu",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }


                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();


        });



        return view;
    }
}