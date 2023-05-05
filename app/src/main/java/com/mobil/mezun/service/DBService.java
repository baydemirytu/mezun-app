package com.mobil.mezun.service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobil.mezun.model.Post;
import com.mobil.mezun.model.User;
import com.mobil.mezun.view.UpdateProfileActivity;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class DBService {

    private User user;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    public DBService() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public void getUserFromDB(UserCallback userCallback){

        DocumentReference userDocRef = db.collection("users").document(firebaseUser.getUid());

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User tmpUser = documentSnapshot.toObject(User.class);
                userCallback.onCallbackUser(tmpUser);

            }
        });

    }

    public void getUserFromDB(String userId, UserCallback userCallback){

        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User tmpUser = documentSnapshot.toObject(User.class);
                userCallback.onCallbackUser(tmpUser);

            }
        });

    }

    public interface UserCallback{
        void onCallbackUser(User user);

    }


    public void getUserListFromDB(UserListCallback userListCallback){

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    ArrayList<User> userList = new ArrayList<>();

                    for(QueryDocumentSnapshot tmpUserDoc: task.getResult()){

                        userList.add(tmpUserDoc.toObject(User.class));

                    }

                    userListCallback.onCallbackUserList(userList);

                }

            }
        });

    }

    public interface UserListCallback{
        void onCallbackUserList(ArrayList<User> userList);

    }

    public void getPostListFromDB(PostListCallback postListCallback){


            ArrayList<Post> postList = new ArrayList<>();

            db.collection("posts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    for(QueryDocumentSnapshot tmpPostDoc: queryDocumentSnapshots){

                        postList.add(tmpPostDoc.toObject(Post.class));

                    }

                    postListCallback.onCallbackPostList(postList);

                }
            });


    }

    public interface PostListCallback{
        void onCallbackPostList(ArrayList<Post> postList);

    }



}
