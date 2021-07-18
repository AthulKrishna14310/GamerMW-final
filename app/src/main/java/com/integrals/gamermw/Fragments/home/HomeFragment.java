package com.integrals.gamermw.Fragments.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.integrals.gamermw.Activities.LuckyWheel;

import com.integrals.gamermw.Activities.Tutorial;
import com.integrals.gamermw.Fragments.groupchat.GroupChatFragment;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.Helpers.FirebaseActions;
import com.integrals.gamermw.Models.AlbumModel;
import com.integrals.gamermw.Models.HomeViewModel;
import com.integrals.gamermw.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


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
    private MaterialButton   giveAwayResult;
    private Uri imageUri;
    private AlertDialog dialog;
    private StorageReference storageReference;
    private FloatingActionButton addPost;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
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
        giveAwayResult=root.findViewById(R.id.giveAwayResult);
        giveAwayResult.setVisibility(View.GONE);
        firebaseActions=new FirebaseActions(getContext(),getActivity(),recyclerView);
        giveAwayLayout.setVisibility(View.GONE);
        firebaseActions.loadRecyclerView(databaseReferenceAlbumList,progressBar);
        storageReference=FirebaseStorage.getInstance().getReference();
        addPost=root.findViewById(R.id.addPost);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        giveAwayReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    String when= Objects.requireNonNull(Objects.requireNonNull(snapshot.child("when").getValue()).toString());
                    if(isFinished(when)){
                    giveAwayLayout.setVisibility(View.GONE);
                    giveAwayResult.setVisibility(View.VISIBLE);
                    String winner=Objects.requireNonNull(Objects.requireNonNull(snapshot.child("winner").getValue()).toString());
                    if(!winner.contentEquals("")) {
                        giveAwayResult.setText("Last Give Away winner "+winner);
                        giveAwayResult.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }else{
                        giveAwayResult.setText("Give Away on progress...");
                        giveAwayResult.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getContext(), LuckyWheel.class));
                            }
                        });
                    }
                }else{
                    giveAwayLayout.setVisibility(View.VISIBLE);
                    giveAwayResult.setVisibility(View.GONE);
                    giveAwayTitle.setText("Give away on "+when);
                    initiateCounterTimer(when,giveAwayTimer);
                }

                }catch (NullPointerException e){
                    e.printStackTrace();
                }


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        tutorialCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(getContext(), Tutorial.class));
            }
        });
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
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
                    if(days==0&&hours==0&&minutes==0&&seconds==-1){
                        onStart();
                    }
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

    private void showAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = getLayoutInflater().inflate(R.layout.add_post, null);
        builder.setView(v);
        builder.setMessage(" Add New Dish ");
        EditText postName = v.findViewById(R.id.newPostTitle);
        EditText postDetails = v.findViewById(R.id.newDescription);
        EditText postedON = v.findViewById(R.id.postedON);
        EditText youTubeLink=v.findViewById(R.id.newYouTubeLink);
        Button addPost = v.findViewById(R.id.addNewPost);
        Button addImage = v.findViewById(R.id.addNewPostImage);
        LinearProgressIndicator linearProgressIndicator=v.findViewById(R.id.uploadProgress);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .start(HomeFragment.this.requireContext(), HomeFragment.this);
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(postName.getText())){
                    postName.setError("Please set Post Title");
                }else if (TextUtils.isEmpty(postDetails.getText())){
                    postDetails.setError("Please set Post Details");
                }else if(TextUtils.isEmpty(postedON.getText())){
                    postedON.setError("Please set posted On");
                }else if(imageUri==null){
                    new CustomToast(getContext()).showErrorToast("Please add a image for post");
                }else if(TextUtils.isEmpty(youTubeLink.getText()))
                { youTubeLink.setError("Please set a youtube link");
                }else{
                   AlbumModel albumModel= new AlbumModel(
                            null,
                            youTubeLink.getText().toString(),
                            postedON.getText().toString(),
                            postName.getText().toString(),
                            postDetails.getText().toString(),
                            null,
                            0+""
                    );
                addNewPost(albumModel,imageUri,linearProgressIndicator);
                }

            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    private void addNewPost(AlbumModel albumModel, Uri imageUri,LinearProgressIndicator linearProgressIndicator) {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        FirebaseStorage.getInstance().getReference().child("posts")
                .putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    StorageReference riversRef = storageReference.child("images/"+imageUri.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(imageUri);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){
                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url=uri.toString();
                                        albumModel.setCoverPic(url);
                                        uploadToDatabase(albumModel);
                                        linearProgressIndicator.setVisibility(View.INVISIBLE);

                                    }
                                });

                            }
                        }
                    });
                }else{

                }
            }
        });
    }

    private void uploadToDatabase(AlbumModel albumModel) {
        Map<String,Object> data=new HashMap<>();
        data.put("CoverPic",albumModel.getCoverPic());
        data.put("AlbumName",albumModel.getAlbumName());
        data.put("Details",albumModel.getDetails());
        data.put("PublishedON",albumModel.getPublishedON());
        data.put("YouTubeLink",albumModel.getYouTubeLink());
        databaseReferenceAlbumList.push().setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    new CustomToast(getContext()).showSuccessToast("Post Posted");
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
            }
        }
    }

}
