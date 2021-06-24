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
import com.integrals.gamermw.Models.ChatModel;
import com.integrals.gamermw.R;
import java.util.ArrayList;
import java.util.Objects;

public class ChatAdapter  extends RecyclerView.Adapter<MiniListHolder> {
    private final Context context;
    private final Activity activity;
    private final ArrayList<ChatModel> chatArrayList;
    private final DatabaseReference mUserData;

    public ChatAdapter(Context context, Activity activity, ArrayList<ChatModel> chatArrayList) {
        this.context = context;
        this.activity = activity;
        this.chatArrayList = chatArrayList;
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
        mUserData.child(chatArrayList.get(position).getUser())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if (chatArrayList.get(position).getMessage().length() > 6) {
                            if (chatArrayList.get(position).getMessage().subSequence(0, 6).toString()
                                    .contentEquals("image-")) {
                                holder.setImageView(String.valueOf(chatArrayList.get(position).getMessage().subSequence(6, chatArrayList.get(position).getMessage().length())));
                                String profileUrl = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child(context.getString(R.string.dbprofilepic)).getValue()).toString();
                                String username = Objects.requireNonNull(task.getResult().child(context.getString(R.string.dbusername)).getValue()).toString();
                                holder.setProfile(profileUrl);
                                holder.setMessageUser(username);
                                holder.setMessageTime(chatArrayList.get(position).getTimestamp().toString());
                                holder.itemView.findViewById(R.id.viewDivider).setVisibility(View.INVISIBLE);
                                holder.setMessageText("");

                            } else {
                                String profileUrl = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child(context.getString(R.string.dbprofilepic)).getValue()).toString();
                                String username = Objects.requireNonNull(task.getResult().child(context.getString(R.string.dbusername)).getValue()).toString();
                                holder.setProfile(profileUrl);
                                holder.setMessageUser(username);
                                holder.setMessageText(chatArrayList.get(position).getMessage());
                                holder.setMessageTime(chatArrayList.get(position).getTimestamp().toString());
                            }
                        }else{
                            String profileUrl = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child(context.getString(R.string.dbprofilepic)).getValue()).toString();
                            String username = Objects.requireNonNull(task.getResult().child(context.getString(R.string.dbusername)).getValue()).toString();
                            holder.setProfile(profileUrl);
                            holder.setMessageUser(username);
                            holder.setMessageText(chatArrayList.get(position).getMessage());
                            holder.setMessageTime(chatArrayList.get(position).getTimestamp().toString());

                        }
                    } else {
                        Constants.showLog("Chat load failed");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
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
