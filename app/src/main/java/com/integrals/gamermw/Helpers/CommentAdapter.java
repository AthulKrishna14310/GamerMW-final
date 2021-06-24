package com.integrals.gamermw.Helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Models.CommentModel;
import com.integrals.gamermw.R;

import java.util.ArrayList;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<MiniListHolder> {
    private final Context context;
    private final Activity activity;
    private final ArrayList<CommentModel> commentArrayList;
    private final DatabaseReference mUserData;

    public CommentAdapter(Context context, Activity activity, ArrayList<CommentModel> commentArrayList) {
        this.context = context;
        this.activity = activity;
        this.commentArrayList = commentArrayList;
        mUserData= FirebaseDatabase.getInstance().getReference().child("user");
    }

    @NonNull
    @Override
    public MiniListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity)
                .inflate(R.layout.mini_list,
                        parent, false);
        return new MiniListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MiniListHolder holder, int position) {
        mUserData.child(commentArrayList.get(position).getUser())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String profileUrl = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child(context.getString(R.string.dbprofilepic)).getValue()).toString();
                        String username = Objects.requireNonNull(task.getResult().child(context.getString(R.string.dbusername)).getValue()).toString();
                        holder.setProfile(profileUrl);
                        holder.setMessageUser(username);
                        holder.setMessageText(commentArrayList.get(position).getMessage());
                        holder.setMessageTime(commentArrayList.get(position).getTimestamp().toString());
                    }else {
                        Constants.showLog("Chat load failed");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



}

