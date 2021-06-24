package com.integrals.gamermw.Fragments.questions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.integrals.gamermw.Models.QuestionsViewModel;
import com.integrals.gamermw.R;

public class QuestionsFragment extends Fragment {

    private QuestionsViewModel questionsViewModel;
    private ShapeableImageView sendImage;
    private EditText userQuestion;
    private FirebaseAuth mAuth;
    private DatabaseReference mQuestionData, mUserData;
    private String username = "null";
    // private FirebaseListAdapter<ChatModel> adapter;
    private ListView listOfQuestion;
    private TextView titleTxt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        questionsViewModel = new ViewModelProvider(this).get(QuestionsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_questions, container, false);
        mAuth = FirebaseAuth.getInstance();
        sendImage = root.findViewById(R.id.send_image);
        userQuestion = root.findViewById(R.id.user_message);
        mQuestionData = FirebaseDatabase.getInstance().getReference().child("questions");
        mUserData = FirebaseDatabase.getInstance().getReference().child("user").child(mAuth.getUid());
        listOfQuestion = root.findViewById(R.id.list_message);

        return root;
    }
    //to Do
    @Override
    public void onStart() {
        super.onStart();
//        adapter = new FirebaseListAdapter<ChatModel>(getActivity(), ChatModel.class, R.layout.list_question, mQuestionData) {
//            @Override
//            protected void populateView(View v, ChatModel model, int position) {
//                TextView messageText, messageUser, messageTime;
//                messageText = v.findViewById(R.id.message_text);
//                messageUser = v.findViewById(R.id.message_user);
//                messageTime = v.findViewById(R.id.message_time);
//                messageText.setText(model.getMessage());
//                messageTime.setText(DateFormat.format(Constants.TIME_FORMAT, model.getTimestamp()));
//
//                getRef(position).get().addOnSuccessListener(dataSnapshot -> {
//                    if(dataSnapshot.child("reply").hasChildren()){
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            MaterialButton t=v.findViewById(R.id.reply);
//                            int count= (int)dataSnapshot.child("reply").getChildrenCount();
//                            t.setTextColor(requireContext().getColor(R.color.colorGreen));
//                            t.setText(""+count+" Replies");
//                        }
//                    }else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            MaterialButton t=v.findViewById(R.id.reply);
//                             t.setTextColor(requireContext().getColor(R.color.colorPrimary));
//                             t.setText("Reply");
//                             t.setIcon(null);
//                        }
//                    }
//
//                });
//
//                v.findViewById(R.id.reply).setOnClickListener(view ->
//                        doReply(getRef(position).getKey(),getRef(position).child("reply"),position));
//            }
//
//
//        };
//
//
//        mUserData.get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                username=task.getResult().child("username").getValue().toString().trim();
//            }
//        });
//
//        sendImage.setOnClickListener(v -> {
//            if(userQuestion.getText() != null){
//                if(!userQuestion.getText().toString().equals("") && username != null){
//                    mQuestionData.push().setValue(new ChatModel(userQuestion.getText().toString(),username,System.currentTimeMillis())).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            userQuestion.getText().clear();
//                        }
//                    });
//
//                }
//            }
//        });
//        listOfQuestion.setAdapter(adapter);
//    }
//
//    private void doReply(String key, DatabaseReference reply,int position) {
//        TextInputLayout textInputLayout=getView().findViewById(R.id.txtInputLayout);
//        textInputLayout.setHint("Type your reply...");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            textInputLayout.setBoxStrokeColor(getContext().getColor(R.color.colorGreen));
//            textInputLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.colorGreen)));
//        }
//        userQuestion.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(userQuestion, InputMethodManager.SHOW_FORCED);
//
//        getView().findViewById(R.id.send_image).setOnClickListener(view ->
//                reply.push().setValue(userQuestion.getText().toString()).addOnSuccessListener(aVoid -> {
//            userQuestion.getText().clear();
//            Snackbar.make(getView(),"Successfully posted reply", BaseTransientBottomBar.LENGTH_LONG).show();
//            imm.hideSoftInputFromWindow(userQuestion.getWindowToken(), 0);
//            textInputLayout.setHint("Type your question...");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                textInputLayout.setBoxStrokeColor(getContext().getColor(R.color.colorPrimary));
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                textInputLayout.setHintTextColor(ColorStateList.valueOf(getContext().getColor(R.color.colorPrimary)));
//            }
//
//            onStart();
//
//        }).addOnFailureListener(e -> Snackbar.make(getView(),"Error", BaseTransientBottomBar.LENGTH_LONG).show()));
//
//    }
//
//}
    }
}