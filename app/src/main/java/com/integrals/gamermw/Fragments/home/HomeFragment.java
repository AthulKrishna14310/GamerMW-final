package com.integrals.gamermw.Fragments.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.integrals.gamermw.Activities.LuckyWheel;

import com.integrals.gamermw.Activities.Tutorial;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.Helpers.FirebaseActions;
import com.integrals.gamermw.Models.HomeViewModel;
import com.integrals.gamermw.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class HomeFragment extends Fragment {
    private FirebaseActions firebaseActions;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReferenceAlbumList;
    private ProgressBar progressBar;
    private DatabaseReference giveAwayReference;
    private MaterialTextView giveAwayTitle;
    private MaterialTextView giveAwayTimer;
    private MaterialCardView giveAwayLayout;
    private AppCompatImageButton navigateToWheel;
    private MaterialCardView tutorialCard;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        firebaseActions=new FirebaseActions(getContext(),getActivity());
        recyclerView=root.findViewById(R.id.albumListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        databaseReferenceAlbumList=FirebaseDatabase.getInstance().getReference().child("Posts");
        giveAwayReference=FirebaseDatabase.getInstance().getReference().child("GiveAway");
        progressBar=root.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        giveAwayTitle=root.findViewById(R.id.giveAwaytitle);
        giveAwayTimer=root.findViewById(R.id.giveAwayCounter);
        giveAwayLayout=root.findViewById(R.id.giveAwayLayout);
        navigateToWheel=root.findViewById(R.id.navigateWheel);
        tutorialCard=root.findViewById(R.id.tutorial);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        giveAwayLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        giveAwayReference.get().addOnCompleteListener(task -> {
            try {
                if(task.isSuccessful()){
                    String when= Objects.requireNonNull(Objects.requireNonNull(task.getResult()).child("when").getValue()).toString();
                    if(isFinished(when)){
                        Constants.showLog("Give away over");
                        giveAwayLayout.setVisibility(View.GONE);
                        firebaseActions.loadRecyclerView(databaseReferenceAlbumList,recyclerView,progressBar);
                    }else{
                        giveAwayLayout.setVisibility(View.VISIBLE);
                        giveAwayTitle.setText("Give away on "+when);
                        initiateCounterTimer(when,giveAwayTimer);
                        firebaseActions.loadRecyclerView(databaseReferenceAlbumList,recyclerView,progressBar);
                    }

                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

        });
        tutorialCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(getContext(), Tutorial.class));
            }
        });
        navigateToWheel.setOnClickListener(v -> startActivity(new Intent(getContext(), LuckyWheel.class)));
    }

    private void initiateCounterTimer(String when, MaterialTextView textView) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT);
        try {
            Date date = sdf.parse(when);
            new CountDownTimer(Objects.requireNonNull(date).getTime(),1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    Calendar cal1 = Calendar.getInstance();
                    long diff = date.getTime() - cal1.getTimeInMillis();
                    long days = diff / (24 * 60 * 60 * 1000);
                    int weeks = (int) (days / 7);
                    long day = days - (weeks * 7);
                    diff = diff - (days * (24 * 60 * 60 * 1000));

                    long hours = diff / (60 * 60 * 1000);
                    diff = diff - (hours * (60 * 60 * 1000));

                    long minutes = diff / (60 * 1000);
                    diff = diff - (minutes * (60 * 1000));
                    long seconds = diff / 1000;
                    String count=days+" Days "+hours+" Hours "+minutes+" Minutes "+seconds+" Seconds left";
                    textView.setText(count);
                }


                @Override
                public void onFinish() {
                    Constants.showLog("Time over");
                }
            }.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private String getTimeStamp(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        String date = simpleDateFormat.format(calendar.getTime());
        return date;
    }
    private boolean isFinished(String when) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIME_FORMAT);
        try {
            Date date1 = sdf.parse(getTimeStamp());
            Date date2 = sdf.parse(when);
            Constants.showLog("Date 1 Today : "+date1.toString());
            Constants.showLog("Date 2 Entered : "+date2.toString());
            if(date1.after(date2)){
                return true;
            }else if(date1.before(date2)){
                return false;
            }else{
                return false;
            }
        } catch (ParseException e) {
            Constants.showLog("Parse exception");
            return true;
        }
    }


}
