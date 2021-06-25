package com.integrals.gamermw.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.integrals.gamermw.Helpers.TutorialGridAdapter;
import com.integrals.gamermw.MainActivity;
import com.integrals.gamermw.Models.TutorialModel;
import com.integrals.gamermw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tutorial extends AppCompatActivity {
    private GridView tutorialTitles;
    private final ArrayList<TutorialModel> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tutorialTitles=findViewById(R.id.tutorialGrid);
        arrayList.add(new TutorialModel("Query Attribute",getString(R.string.QueueAttributeUrl)));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
        arrayList.add(new TutorialModel("Title 1","https://google.co.in"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        TutorialGridAdapter tutorialGridAdapter=new TutorialGridAdapter(this,
                arrayList);
        tutorialTitles.setAdapter(tutorialGridAdapter);
    }



    private void mailAdmin() {
    }
}