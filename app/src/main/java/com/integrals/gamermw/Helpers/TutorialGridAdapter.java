package com.integrals.gamermw.Helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textview.MaterialTextView;
import com.integrals.gamermw.Activities.WebViewActivity;
import com.integrals.gamermw.Models.TutorialModel;
import com.integrals.gamermw.R;
import java.util.ArrayList;

public class TutorialGridAdapter extends ArrayAdapter<TutorialModel> {

    public TutorialGridAdapter(@NonNull Context context, ArrayList<TutorialModel> courseModelArrayList) {
        super(context, 0, courseModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.tutorial_grid_item,
                    parent, false);
        }
        MaterialTextView title= listitemView.
                findViewById(R.id.tutorial_grid_item_title);
        title.setText(getItem(position).getName());
        listitemView.setOnClickListener(v -> getContext().
                startActivity(new Intent(getContext(), WebViewActivity.class)
                .putExtra("url",getItem(position).getUrl())));
        return listitemView;
    }
}
