package com.integrals.gamermw.Helpers;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Activities.CommentActivity;
import com.integrals.gamermw.Models.AlbumModel;
import com.integrals.gamermw.R;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter
        extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
private Context context;
private List<AlbumModel> albumModels;
private FirebaseActions firebaseActions;
private Activity activity;
    public AlbumAdapter(Context context, List<AlbumModel> albumModels, FirebaseActions firebaseActions,
                        Activity activity) {
        this.context = context;
        this.firebaseActions=firebaseActions;
        this.albumModels = albumModels;
        this.activity=activity;
        ///
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.album_card, parent, false);
        return new AlbumViewHolder(view);

        }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        RequestOptions requestOptions=new RequestOptions().centerCrop();
        Glide.with(context)
                .load(albumModels.get(position).getCoverPic())
                .apply(requestOptions)
                .into(holder.CoverPic);
        holder.albumTitle.setText(albumModels.get(position).getAlbumName());
        holder.likeBtn.setText(albumModels.get(position).getLikes());
        holder.share_link.setOnClickListener(view ->
                firebaseActions.shareAlbumLink(albumModels.get(position).getYouTubeLink()));

        holder.likeBtn.setOnClickListener(view -> {
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts")
//                    .child(albumModels.get(position).getId())
//                    .child("Likes");
//            databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    Toast.makeText(context,"Likes Posted",Toast.LENGTH_LONG).show();
//                }
//            });
            ArrayList<String> arrayList=new ArrayList<>();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts")
                    .child(albumModels.get(position).getId())
                    .child("Likes");
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().hasChildren()) {
                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            arrayList.add(dataSnapshot.getValue(true).toString());
                        }
                        if (arrayList.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Constants.showToast("Removed Like", context);
                                        }
                                    });
                        } else {
                            databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   Constants.showToast("Like Posted",context);
                                }
                            });
                        }

                    }else{
                        databaseReference.push().setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Constants.showToast("Like Posted",context);
                            }
                        });
                    }
                }
            });
        });

        holder.postComments.setOnClickListener(view -> context.startActivity(new Intent(context, CommentActivity.class)
        .putExtra("cover",albumModels.get(position).getCoverPic())
        .putExtra("title",albumModels.get(position).getAlbumName())
        .putExtra("details",albumModels.get(position).getDetails())
        .putExtra("id",albumModels.get(position).getId())));

        holder.CoverPic.setOnClickListener(view -> {
        });


    }

    @Override
    public int getItemCount() {
        return albumModels.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        public ImageView CoverPic;
        public TextView  albumTitle;
        public TextView   likeBtn;
        public ImageButton share_link;
        public ImageButton postComments;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            CoverPic=itemView.findViewById(R.id.cover_pic);
            postComments =itemView.findViewById(R.id.post_comments);
            albumTitle=itemView.findViewById(R.id.albumTitle);
            share_link=itemView.findViewById(R.id.share_link);
            likeBtn=itemView.findViewById(R.id.post_like);

        }
    }
}
