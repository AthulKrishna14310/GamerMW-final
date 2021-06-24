package com.integrals.gamermw.Helpers;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.integrals.gamermw.Models.AlbumModel;
import com.integrals.gamermw.R;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class FirebaseActions {
    private Context context;
    private ArrayList<AlbumModel> albumModels;
    private FirebaseActions thisClass;
    private Activity activity;
    private ArrayList<String> keys;

    public FirebaseActions(Context context,Activity activity) {
        this.thisClass=this;
        this.context = context;
        this.activity=activity;

    }



    public void shareAlbumLink(String s) {
        Intent SharingIntent = new Intent(Intent.ACTION_SEND);
        SharingIntent.setType("text/plain");
        SharingIntent.putExtra(Intent.EXTRA_TEXT, "Chordlines Music Youtube Link" +"\n\n" + s);
        SharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(SharingIntent);

        }

        public void shareImage(){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.buisness_card_hd);
                String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,"title", null);
                Uri bitmapUri = Uri.parse(bitmapPath);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                else
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                shareIntent.setType("image/*");

                // For a file in shared storage.  For data in private storage, use a ContentProvider.
                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                context.startActivity(shareIntent);

                }



    public void shareAppLink(String s) {

        Intent SharingIntent = new Intent(Intent.ACTION_SEND);
        SharingIntent.setType("text/plain");
        SharingIntent.putExtra(Intent.EXTRA_TEXT, "Chordlines Music App Link" +"\n\n" + s);
        SharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(SharingIntent);

    }

    public void loadRecyclerView(DatabaseReference databaseReference,
                                 RecyclerView recyclerView,
                                 ProgressBar progressBar) {
        albumModels=new ArrayList<>();
        albumModels.clear();
        keys=new ArrayList<>();
        keys.clear();

        AlbumAdapter albumAdapter=new AlbumAdapter(context,albumModels,thisClass,activity);
        recyclerView.setAdapter(albumAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                if(snapshot.hasChildren()) {
                    Constants.showLog("Child Count ="+snapshot.getChildrenCount());
                    Constants.showLog("Child ID ="+snapshot.getKey());
                        String coverPic = null, youTubeLink = null, publishedON = null, albumName = null, details = null, likes = "0";
                        if (snapshot.hasChild("CoverPic")) {
                            coverPic = snapshot.child("CoverPic").getValue().toString();
                        }
                        if (snapshot.hasChild("YoutubeLink")) {
                            youTubeLink = snapshot.child("YoutubeLink").getValue().toString();
                        }
                        if (snapshot.hasChild("PublishedON")) {
                            publishedON = snapshot.child("PublishedON").getValue().toString();
                        }
                        if (snapshot.hasChild("AlbumName")) {
                            albumName = snapshot.child("AlbumName").getValue().toString();

                        }
                        if (snapshot.hasChild("Details")) {
                            details = snapshot.child("Details").getValue().toString();

                        }
                        if(snapshot.hasChild("Likes")){
                            likes=String.valueOf(snapshot.child("Likes").getChildrenCount());
                        }
                        keys.add(snapshot.getKey());
                        albumModels.add(new AlbumModel(
                                coverPic,
                                youTubeLink,
                                publishedON,
                                albumName,
                                details,
                                snapshot.getKey(),
                                likes+""
                        ));
                    albumAdapter.notifyDataSetChanged();

                }else{
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                String coverPic = null, youTubeLink = null, publishedON = null, albumName = null, details = null, likes = "0";
                if (snapshot.hasChild("CoverPic")) {
                    coverPic = snapshot.child("CoverPic").getValue().toString();
                }
                if (snapshot.hasChild("YoutubeLink")) {
                    youTubeLink = snapshot.child("YoutubeLink").getValue().toString();
                }
                if (snapshot.hasChild("PublishedON")) {
                    publishedON = snapshot.child("PublishedON").getValue().toString();
                }
                if (snapshot.hasChild("AlbumName")) {
                    albumName = snapshot.child("AlbumName").getValue().toString();

                }
                if (snapshot.hasChild("Details")) {
                    details = snapshot.child("Details").getValue().toString();

                }
                if(snapshot.hasChild("Likes")){
                    likes=String.valueOf(snapshot.child("Likes").getChildrenCount());
                }
                albumModels.set(keys.indexOf(snapshot.getKey()),
                        new AlbumModel(
                                coverPic,
                                youTubeLink,
                                publishedON,
                                albumName,
                                details,
                                snapshot.getKey(),
                                likes+""
                        ));
                albumAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });
    }

}