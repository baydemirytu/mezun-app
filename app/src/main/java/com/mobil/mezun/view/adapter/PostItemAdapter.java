package com.mobil.mezun.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobil.mezun.R;
import com.mobil.mezun.model.Post;
import com.mobil.mezun.model.User;
import com.mobil.mezun.service.DBService;

import java.util.ArrayList;
import java.util.Objects;

public class PostItemAdapter extends RecyclerView.Adapter<PostItemAdapter.PostItemViewHolder>{

    ArrayList<Post> postList;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    DBService dbService = new DBService();

    Context context;

    public PostItemAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostItemAdapter.PostItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.post_item, parent, false);

        return new PostItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostItemAdapter.PostItemViewHolder holder,
                                 int position) {

        Post post = postList.get(position);

        StorageReference postsRef = storage.getReference();

        if(post.getImageId().equals("")){
            postsRef = postsRef.child("avatars/" + post.getUserId() + ".png");
        }else{

            postsRef = postsRef.child("posts/" + post.getImageId() + ".png");


        }


        final long tenMB = 10 * 1024 *1024;
        postsRef.getBytes(tenMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(bitmap);
            }
        });

        dbService.getUserFromDB(post.getUserId(), tmpUser -> {
            String usernameOfPostText = tmpUser.getFirstname() + " " + tmpUser.getSurname();
            holder.usernameOfPostText.setText(usernameOfPostText);



            if(post.getUserId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){

                holder.postItemCardView.setCardBackgroundColor(0xFFC58745);
                holder.communicationButton = null;
                holder.messageText.setText("(Sizin) " + post.getMessage());


            }else{
                holder.messageText.setText(post.getMessage());
                if(holder.communicationButton!=null){
                    holder.communicationButton.setOnClickListener(view -> {

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{tmpUser.getCommunication_email()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "YTÜ Mezun Uygulamasından");
                        view.getContext().startActivity(Intent.createChooser(intent, "Send email"));

                    });
                }

            }

        });





    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostItemViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView messageText;
        TextView usernameOfPostText;
        Button communicationButton;
        CardView postItemCardView;

        public PostItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewPostItem);
            messageText = itemView.findViewById(R.id.messageText);
            usernameOfPostText = itemView.findViewById(R.id.usernameOfPostText);
            communicationButton = itemView.findViewById(R.id.postCommunicationButton);
            postItemCardView = itemView.findViewById(R.id.postItemCardView);

            postItemCardView.setOnClickListener(view -> {

                int visibility = usernameOfPostText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;

                TransitionManager.beginDelayedTransition( postItemCardView,new AutoTransition());
                if(communicationButton != null){
                    communicationButton.setVisibility(visibility);
                }
                usernameOfPostText.setVisibility(visibility);


            });

        }



    }


}
