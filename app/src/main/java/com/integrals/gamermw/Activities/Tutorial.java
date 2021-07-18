package com.integrals.gamermw.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.widget.GridView;
import com.integrals.gamermw.Helpers.TutorialGridAdapter;
import com.integrals.gamermw.Models.TutorialModel;
import com.integrals.gamermw.R;
import java.util.ArrayList;

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