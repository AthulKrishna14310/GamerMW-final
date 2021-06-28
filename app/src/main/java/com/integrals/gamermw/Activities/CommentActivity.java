package com.integrals.gamermw.Activities;

import android.content.Intent;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.integrals.gamermw.Helpers.CommentAdapter;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.Models.ChatModel;
import com.integrals.gamermw.Models.CommentModel;
import com.integrals.gamermw.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private String title,cover,details,id;
    private AppCompatImageView appCompatImageView;
    private ShapeableImageView sendImage;
    private EditText userComment;
    private FirebaseAuth mAuth;
    private DatabaseReference mCommentData;
    private String userId = "null";
    private TextView titleTxt;
    private ProgressBar progressBar;
    private ArrayList<CommentModel> commentArrayList=new ArrayList<>();
    private ArrayList<String>commentIds=new ArrayList<>();
    private RecyclerView commentRecycler;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_comment);
        title=getIntent().getStringExtra("title");
        cover=getIntent().getStringExtra("cover");
        details=getIntent().getStringExtra("details");
        id=getIntent().getStringExtra("id");
        appCompatImageView=findViewById(R.id.imageView);
        titleTxt=findViewById(R.id.title);
        mAuth = FirebaseAuth.getInstance();
        sendImage = findViewById(R.id.send_image);
        userComment = findViewById(R.id.user_message);
        mCommentData = FirebaseDatabase.getInstance().getReference()
                .child("Posts")
                .child(id).child("Comments");
        userId=mAuth.getCurrentUser().getUid();
        progressBar=findViewById(R.id.progressComment);
        commentRecycler=findViewById(R.id.list_comment);
        commentRecycler.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        commentRecycler.setHasFixedSize(true);
        commentAdapter=new CommentAdapter(CommentActivity.this,
                CommentActivity.this,
                commentArrayList);
        progressBar.setVisibility(View.GONE);
        Picasso.get()
                .load(cover)
                .into(appCompatImageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchComment();
        titleTxt.setText(title);
        commentRecycler.setAdapter(commentAdapter);
        sendImage.setOnClickListener(v -> {
            if(userComment.getText() != null){
                if(!userComment.getText().toString().equals("") && userId != null){
                    mCommentData.push().setValue(new ChatModel(userComment.getText().toString(),userId,
                            String.valueOf(System.currentTimeMillis())));
                    userComment.getText().clear();
                }
            }
        });

    }

    private void fetchComment() {
        Constants.showLog("Fetch data called");
        progressBar.setVisibility(View.VISIBLE);
        if((commentArrayList.isEmpty())&&(commentIds.isEmpty())) {
            mCommentData.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().hasChildren()) {
                                commentIds.clear();
                                commentArrayList.clear();
                                Constants.showLog("Fetch data received");

                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                    String message = "", user = "";
                                    Long timestamp = Long.valueOf(0);

                                    if (dataSnapshot.hasChild("message")) {
                                        message = dataSnapshot.child("message").getValue().toString();
                                    }
                                    if (dataSnapshot.hasChild("user")) {
                                        user = dataSnapshot.child("user").getValue().toString();
                                    }
                                    if (dataSnapshot.hasChild("timestamp")) {
                                        timestamp = Long.parseLong(dataSnapshot.child("timestamp").getValue().toString());
                                    }
                                    commentIds.add(dataSnapshot.getKey());
                                    commentArrayList.add(new CommentModel(message,user,timestamp));
                                    Constants.showLog("Data =>"+message+":"+user+":"+timestamp);
                                }

                                commentAdapter.notifyDataSetChanged();
                                commentRecycler.scrollToPosition(commentArrayList.size() - 1);
                             }
                            progressBar.setVisibility(View.GONE);
                            initiateCommentUpdates();

                        }else{
                            new CustomToast(getApplicationContext()).showErrorToast(task.getException().getMessage());
                        }
                    });
        }else{
            progressBar.setVisibility(View.GONE);
            commentRecycler.scrollToPosition(commentArrayList.size()-1);
            initiateCommentUpdates();
        }

    }

    private void initiateCommentUpdates() {
        mCommentData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(commentIds.contains(snapshot.getKey())){
                }else{
                    commentIds.add(snapshot.getKey());
                    commentArrayList.add(new CommentModel(snapshot.child("message").getValue().toString(),
                            snapshot.child("user").getValue().toString(),
                            Long.parseLong(snapshot.child("timestamp").getValue().toString())));
                    commentAdapter.notifyItemInserted(commentArrayList.size()-1);
                    commentRecycler.smoothScrollToPosition(commentArrayList.size()-1);
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                new CustomToast(getApplicationContext()).showErrorToast(error.getMessage());
            }
        });

    }
}