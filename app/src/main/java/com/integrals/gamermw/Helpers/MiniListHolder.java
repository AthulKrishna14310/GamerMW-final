package com.integrals.gamermw.Helpers;

import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.integrals.gamermw.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MiniListHolder extends RecyclerView.ViewHolder {

    private final TextView messageText;
    private final TextView messageUser;
    private final TextView messageTime;
    private final CircleImageView profilepic;
    private final AppCompatImageView imageView;
    private @NonNull final View view;

    public MiniListHolder(@NonNull View v) {
        super(v);
        imageView = v.findViewById(R.id.imageView);
        messageText = v.findViewById(R.id.message_text);
        messageUser = v.findViewById(R.id.message_user);
        messageTime = v.findViewById(R.id.message_time);
        profilepic = v.findViewById(R.id.imageViewAccount);
        view=v;
    }

    public void setInvisible(){
        imageView.setVisibility(View.GONE);
        messageText.setVisibility(View.GONE);
        messageUser.setVisibility(View.GONE);
        messageTime.setVisibility(View.GONE);
        profilepic.setVisibility(View.GONE);
    }
    public void setMessageText(String message){
        messageText.setVisibility(View.VISIBLE);
        messageText.setText(message);
    }
    public void setMessageUser(String user){
        messageUser.setVisibility(View.VISIBLE);
        messageUser.setText(user);
    }
    public void setMessageTime(String time) {
        messageTime.setVisibility(View.VISIBLE);
        messageTime.setText((DateFormat.format(Constants.TIME_FORMAT,Long.parseLong(time))));
    }
    public void setProfile(String url){
        profilepic.setVisibility(View.VISIBLE);
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .into(profilepic);
    }
    public void setImageView(String url){
        imageView.setVisibility(View.VISIBLE);
        Picasso.get().load(url)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(imageView);
    }

}
