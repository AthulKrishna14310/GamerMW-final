package com.integrals.gamermw.Fragments.groupchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.integrals.gamermw.Helpers.ChatAdapter;
import com.integrals.gamermw.Helpers.Constants;
import com.integrals.gamermw.Helpers.CustomToast;
import com.integrals.gamermw.Models.ChatModel;
import com.integrals.gamermw.R;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class GroupChatFragment extends Fragment {

    private static final int CHAT_LOAD_COUNT = 10;
    private AppCompatImageButton sendImage;
    private EditText userMessage;
    private FirebaseAuth mAuth;
    private DatabaseReference mMessageData;
    private String username = "null";
    private RecyclerView recyclerOfMessages;
    private AppCompatImageButton sendImageButton;
    private Fragment fragment;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private LinearProgressIndicator progressBar;
    private static final ArrayList<ChatModel>chatArrayList=new ArrayList<>();
    private static final ArrayList<String>ids=new ArrayList<>();
    private ChatAdapter chatAdapter;
    private Query query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group_chat, container, false);
        mAuth = FirebaseAuth.getInstance();
        fragment=this;
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("messages");
        sendImage = root.findViewById(R.id.send_image);
        userMessage = root.findViewById(R.id.user_message);
        sendImageButton=root.findViewById(R.id.imageButton);
        progressBar=root.findViewById(R.id.messageProgress);
        mMessageData = FirebaseDatabase.getInstance().getReference().child("messages");
        query=mMessageData.limitToLast(CHAT_LOAD_COUNT);
        recyclerOfMessages = root.findViewById(R.id.list_message);
        recyclerOfMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerOfMessages.setHasFixedSize(true);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setVisibility(View.GONE);
        chatAdapter=new ChatAdapter(getContext(), getActivity(), chatArrayList);
        recyclerOfMessages.setAdapter(chatAdapter);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        sendImage.setOnClickListener(v -> {
            if (userMessage.getText() != null) {
                if (!userMessage.getText().toString().equals("") && username != null) {
                    mMessageData.push()
                            .setValue(new ChatModel(userMessage.getText().toString(),
                                    Objects.requireNonNull(mAuth.getCurrentUser()).getUid(),
                                    String.valueOf(System.currentTimeMillis())))
                            .addOnSuccessListener(aVoid -> userMessage.getText().clear());

                }
            }
        });
        sendImageButton.setOnClickListener(view -> CropImage.activity()
                .start(requireContext(), fragment));

        fetchChat();
    }


    private void fetchChat() {
        progressBar.setVisibility(View.VISIBLE);
            if((chatArrayList.isEmpty())&&(ids.isEmpty())) {
                query.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (Objects.requireNonNull(task.getResult()).hasChildren()) {
                                    chatArrayList.clear();
                                    ids.clear();
                                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                        String message = "", user = "";
                                        String timestamp ="";
                                        Constants.showLog("Snapshot size => " + task.getResult().getChildrenCount());
                                        if (dataSnapshot.hasChild("message")) {
                                            message = Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString();
                                        }
                                        if (dataSnapshot.hasChild("user")) {
                                            user = Objects.requireNonNull(dataSnapshot.child("user").getValue()).toString();
                                        }
                                        if (dataSnapshot.hasChild("timestamp")) {
                                            timestamp = Objects.requireNonNull(dataSnapshot.child("timestamp").getValue()).toString();
                                        }
                                        ChatModel _chatModel = new ChatModel(message, user, timestamp);
                                        chatArrayList.add(_chatModel);
                                        ids.add(dataSnapshot.getKey());
                                    }
                                    chatAdapter.notifyItemRangeInserted(0,chatArrayList.size());
                                    recyclerOfMessages.scrollToPosition(chatArrayList.size()-1);
                                }
                                progressBar.setVisibility(View.GONE);
                                initiateMessageUpdates();
                            }
                        });
            }else{
                progressBar.setVisibility(View.GONE);
                initiateMessageUpdates();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference riversRef = storageReference.child("images/"+resultUri.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(resultUri);
                progressDialog.setTitle("Upload");
                progressDialog.setMessage("Uploading your image , Please wait..");
                uploadTask.addOnFailureListener(exception -> {
                    progressDialog.dismiss();
                    Snackbar.make(requireView(),"Upload Failed", BaseTransientBottomBar.LENGTH_LONG).show();
                }).addOnSuccessListener(taskSnapshot -> riversRef.getDownloadUrl().addOnSuccessListener(uri -> mMessageData.push()
                        .setValue(new ChatModel("image-"+uri.toString()
                                , Objects.requireNonNull(mAuth.getCurrentUser()).getUid(),
                                String.valueOf(System.currentTimeMillis()))).
                                addOnSuccessListener(aVoid -> progressDialog.dismiss())))
                        .addOnProgressListener(snapshot -> {
                            double progress = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialog.setProgress((int)progress);
                            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.setMessage("Canceling.. , Please wait..");
                                    uploadTask.cancel();
                                }
                            });
                            progressDialog.show();
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                new CustomToast(getContext()).showErrorToast(error.getMessage());
            }
        }
    }
    private void initiateMessageUpdates(){
        mMessageData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(ids.contains(snapshot.getKey())){
                    recyclerOfMessages.scrollToPosition(chatArrayList.size()-1);
                }else{
                    ids.add(snapshot.getKey());
                    chatArrayList.add(new ChatModel(
                            Objects.requireNonNull(snapshot.child("message").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("user").getValue()).toString(),
                            Objects.requireNonNull(snapshot.child("timestamp").getValue()).toString()));
                   chatAdapter.notifyItemInserted(chatArrayList.size()-1);
                   new Handler(Looper.getMainLooper()).postDelayed(() ->
                           recyclerOfMessages.smoothScrollToPosition(chatArrayList.size()-1), 100);


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

            }
        });

    }

}