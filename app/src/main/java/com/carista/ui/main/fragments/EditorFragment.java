package com.carista.ui.main.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.carista.R;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class EditorFragment extends Fragment {

    public EditorFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PhotoEditorView mPhotoEditorView = view.findViewById(R.id.photoEditorView);

        mPhotoEditorView.getSource().setImageResource(R.drawable.car_placeholder);

        //Use custom font using latest support library
//        Typeface mTextRobotoTf = ResourcesCompat.getFont(getContext(), R.font.roboto_medium);

        //loading font from assest
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getContext().getAssets(), "emojione-android.ttf");


        PhotoEditor mPhotoEditor = new PhotoEditor.Builder(getContext(), mPhotoEditorView)
                .setDefaultTextTypeface(Typeface.DEFAULT)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .setPinchTextScalable(true)
                .build();
    }
}
