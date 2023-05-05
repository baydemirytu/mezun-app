package com.mobil.mezun.view.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobil.mezun.R;
import com.mobil.mezun.model.User;
import com.mobil.mezun.view.HomeFragment;
import com.mobil.mezun.view.SignupActivity;

import java.util.ArrayList;
import java.util.Objects;

public class UserListItemAdapter extends RecyclerView.Adapter<UserListItemAdapter.UserListItemViewHolder> {

    private ArrayList<User> userList;

    Context context;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    public UserListItemAdapter(ArrayList<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListItemAdapter.UserListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.user_list_item, parent, false);

        return new UserListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListItemAdapter.UserListItemViewHolder holder,
                                 int position) {
        User user = userList.get(position);
        String name = user.getFirstname() + " " + user.getSurname();
        holder.nameText.setText(name);

        String lisans = user.getEntry_year() + "-" + user.getGraduate_year() + " " + user.getLisans_name() + " mezunu";
        holder.lisansText.setText(lisans);

        String yuksekLisans = "Yüksek Lisans: " + user.getYuksek_name() ;
        holder.yuksekLisansText.setText(yuksekLisans);

        String doktora = "Doktora: " + user.getDoktora_name();
        holder.doktoraText.setText(doktora);

        String job = user.getJob().getCity() + "/" + user.getJob().getCountry() + "'de " + user.getJob().getCompany() + " şirketinde çalışıyor";
        holder.jobText.setText(job);

        String email = "İletişim emaili: " + user.getCommunication_email();
        holder.emailText.setText(email);


        String phone = "Telefon: " + user.getPhone_number();
        holder.phoneText.setText(phone);

        StorageReference avatarRef = storage.getReference();
        avatarRef = avatarRef.child("avatars/" + user.getId() + ".png");

        final long tenMB = 10 * 1024 *1024;
        avatarRef.getBytes(tenMB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.imageView.setImageBitmap(bitmap);
            }
        });



        if(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(user.getId())){
            String text = holder.nameText.getText() + " (siz)";
            holder.nameText.setText(text);
            holder.cardView.setCardBackgroundColor(0xFFC58745);
        }
        else{

            holder.emailText.setOnClickListener(view -> {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getCommunication_email()});
                intent.putExtra(Intent.EXTRA_SUBJECT, "YTÜ Mezun Uygulamasından");
                view.getContext().startActivity(Intent.createChooser(intent, "Send email"));

            });


        }

        holder.phoneText.setOnClickListener(view -> {

            if(user.getPhone_number()!=null && !user.getPhone_number().equals("")){
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", user.getPhone_number());

                clipboard.setPrimaryClip(clip);

                Toast.makeText(view.getContext(),  user.getPhone_number() + " panoya kopyalandı",
                        Toast.LENGTH_SHORT).show();

            }




        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserListItemViewHolder extends RecyclerView.ViewHolder {

        TextView nameText;
        TextView lisansText;
        TextView yuksekLisansText;
        TextView doktoraText;
        TextView jobText;
        TextView emailText;
        TextView phoneText;
        CardView cardView;
        ImageView imageView;

        public UserListItemViewHolder(@NonNull View itemView) {
            super(itemView);


            cardView = itemView.findViewById(R.id.userItemCardView);
            imageView = itemView.findViewById(R.id.imageViewUserListItem);
            nameText = itemView.findViewById(R.id.nameText);
            lisansText = itemView.findViewById(R.id.lisansText);
            yuksekLisansText = itemView.findViewById(R.id.yuksekLisansText);
            doktoraText = itemView.findViewById(R.id.doktoraText);
            jobText = itemView.findViewById(R.id.jobText);
            emailText = itemView.findViewById(R.id.emailtText);
            phoneText = itemView.findViewById(R.id.phoneText);

            cardView.setOnClickListener(view -> {
                int visibility = yuksekLisansText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;

                TransitionManager.beginDelayedTransition( cardView,new AutoTransition());

                yuksekLisansText.setVisibility(visibility);
                doktoraText.setVisibility(visibility);
                jobText.setVisibility(visibility);
                emailText.setVisibility(visibility);
                phoneText.setVisibility(visibility);

            });

        }
    }
}
