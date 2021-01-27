package com.carista.ui.main.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.carista.R;
import com.carista.utils.Data;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;


public class UploadFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 100;
    private Intent chooser;
    private ImageView imageView;
    private Button chooseButton, uploadButton;
    private EditText titleEditText;

    public UploadFragment() {
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
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chooseButton = view.findViewById(R.id.new_post_choose);
        uploadButton = view.findViewById(R.id.new_post_upload);
        titleEditText = view.findViewById(R.id.new_post_title);
        imageView = view.findViewById(R.id.new_post_image);
        initChooser();

        chooseButton.setOnClickListener(view1 -> {
            startActivityForResult(chooser, RESULT_LOAD_IMAGE);
        });

        uploadButton.setOnClickListener(view1 -> {
            if (imageView.getDrawable() == null) {
                Snackbar.make(getActivity().getCurrentFocus(), R.string.select_image, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (titleEditText.getText() == null || titleEditText.getText().toString().isEmpty()) {
                Snackbar.make(getActivity().getCurrentFocus(), R.string.insert_title, Snackbar.LENGTH_SHORT).show();
                return;
            }

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference("posts");
            // Create a reference to "mountains.jpg"
            long id = new Date().getTime();
            String name = id + ".jpg";
            StorageReference imageRef = storageRef.child(name);

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> Snackbar.make(getActivity().getCurrentFocus(), R.string.failed_to_upload, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                            Data.uploadPost(titleEditText.getText().toString().trim(), id, uri.toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                            imageView.setImageBitmap(null);
                            titleEditText.setText("");
                            Snackbar.make(getView().findViewById(R.id.upload_layout),R.string.success_upload,Snackbar.LENGTH_SHORT).show();
                        });

                    });
        });
    }

    private void initChooser() {
        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent gallIntent = new Intent(Intent.ACTION_PICK);
        gallIntent.setType("image/*");
        chooser = Intent.createChooser(gallIntent, getResources().getString(R.string.select_image));
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{camIntent});
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Bitmap bitmap;
            if (data.getExtras() != null && data.getExtras().get("data") instanceof Bitmap) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            imageView.setImageBitmap(bitmap);
        }
    }
}