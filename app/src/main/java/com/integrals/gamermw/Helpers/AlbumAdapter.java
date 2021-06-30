package com.integrals.gamermw.Helpers;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Activities.CommentActivity;
import com.integrals.gamermw.Models.AlbumModel;
import com.integrals.gamermw.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
}

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.album_card, parent, false);
        return new AlbumViewHolder(view);

        }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Picasso.get()
                .load(albumModels.get(position).getCoverPic())
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(holder.CoverPic);
        holder.albumTitle.setText(albumModels.get(position).getAlbumName());
        holder.likeBtn.setText(albumModels.get(position).getLikes());
        holder.share_link.setOnClickListener(view ->
                firebaseActions.shareAlbumLink(albumModels.get(position).getYouTubeLink()));
        DatabaseReference lR=FirebaseDatabase.getInstance().getReference().child("Posts")
                .child(albumModels.get(position).getId())
                .child("Likes");
        lR.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(task.isSuccessful()) {
                    ArrayList<String>likedUsers=new ArrayList<>();
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        likedUsers.add(dataSnapshot.getValue().toString());
                    }
                    if(likedUsers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
                    }else{
                        holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_not_like, 0, 0, 0);
                    }
                }
            }
        });

        holder.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,holder.optionButton);
                MenuInflater inflater=popupMenu.getMenuInflater();
                inflater.inflate(R.menu.card_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.giveAwayAdd){
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR);
                            int mMonth = c.get(Calendar.MONTH);
                            int mDay = c.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                    new DatePickerDialog.OnDateSetListener() {

                                        @Override
                                        public void onDateSet(DatePicker view, int year,
                                                              int monthOfYear, int dayOfMonth) {
                                            timeSelector(dayOfMonth,(monthOfYear + 1),year,albumModels.get(position).getId());

                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.show();


                        } else if(item.getItemId()==R.id.deletePost){
                            FirebaseDatabase.getInstance().getReference().child("Posts").child(albumModels.get(position)
                            .getId()) .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        new CustomToast(context)
                                                .showSuccessToast("Post Deleted.");
                                    }
                                }
                            });
                        }
                        return false;
                    }
                });
            }
        });

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> data=new HashMap<>();
                HashMap<String,String> reverseData=new HashMap<>();
                DatabaseReference likeReference=FirebaseDatabase.getInstance().getReference().child("Posts")
                        .child(albumModels.get(position).getId())
                        .child("Likes");

                likeReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()) {
                            if (task.getResult().hasChildren()) {
                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                    data.put(dataSnapshot.getKey(),dataSnapshot.getValue().toString());
                                    reverseData.put(dataSnapshot.getValue().toString(),dataSnapshot.getKey());
                                }
                                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                if (data.containsValue(currentUser)) {
                                    removeLike(likeReference,data,reverseData,currentUser);
                                } else {
                                    putLike(likeReference);
                                }
                            }else{
                                putLike(likeReference);
                            }
                        }

                    }
                });

            }
        });


        holder.postComments.setOnClickListener(view -> context.startActivity(new Intent(context, CommentActivity.class)
        .putExtra("cover",albumModels.get(position).getCoverPic())
        .putExtra("title",albumModels.get(position).getAlbumName())
        .putExtra("details",albumModels.get(position).getDetails())
        .putExtra("id",albumModels.get(position).getId())));

        holder.CoverPic.setOnClickListener(view -> {
        });


    }

    private void timeSelector(int dayOfMonth, int i, int year,String postkey) {
        Calendar mCurrentTime = Calendar.getInstance();
        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mCurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hour,minute;
                if(selectedHour==0){
                    hour="00";
                }else{
                    hour=String.valueOf(selectedHour);
                }

                if(selectedMinute==0){
                    minute="00";
                }else{
                    minute=String.valueOf(selectedMinute);
                }
                String pickedData = dayOfMonth + "-" + i + "-" + year + " " + hour +":"+minute+":"+"00";
                Map<String,Object> data=new HashMap<>();
                data.put("when", pickedData);
                data.put("post",postkey);
                data.put("winner","");
                data.put("initiate","no");
                FirebaseDatabase.
                        getInstance().getReference().child("GiveAway")
                        .updateChildren(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            new CustomToast(context).showSuccessToast("Give away set successfully");
                        }else{
                            new CustomToast(context).showErrorToast(task.getException().getMessage());
                        }
                    }
                });

            }
        }, hour, minute, true);

        mTimePicker.setTitle("Select Time for give away");
        mTimePicker.show();
    }

    private void removeLike(DatabaseReference likereference, HashMap<String, String> data,HashMap<String, String> reverseData, String currentUser) {
        likereference.child(reverseData.get(currentUser))
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                 if(task.isSuccessful()) {
                     Constants.showLog("Like Removed");
                 }

            }
        });
    }

    private void putLike(DatabaseReference likereference) {
        likereference
        .push()
        .setValue(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Constants.showLog("Liked");
            }
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
        public AppCompatImageButton optionButton;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            CoverPic=itemView.findViewById(R.id.cover_pic);
            postComments =itemView.findViewById(R.id.post_comments);
            albumTitle=itemView.findViewById(R.id.albumTitle);
            share_link=itemView.findViewById(R.id.share_link);
            likeBtn=itemView.findViewById(R.id.post_like);
            optionButton=itemView.findViewById(R.id.cardOptions);

        }
    }
}
