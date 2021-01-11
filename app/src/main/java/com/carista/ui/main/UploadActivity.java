package com.carista.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.carista.R;
import com.carista.utils.Data;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class UploadActivity extends AppCompatActivity {


    private static final int RESULT_LOAD_IMAGE = 100;
    private Intent chooser;
    private ImageView imageView;
    private Button chooseButton, uploadButton;
    private EditText titleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        chooseButton = findViewById(R.id.new_post_choose);
        uploadButton = findViewById(R.id.new_post_upload);
        titleEditText = findViewById(R.id.new_post_title);
        imageView = findViewById(R.id.new_post_image);
        initChooser();

        chooseButton.setOnClickListener(view -> {
            startActivityForResult(chooser, RESULT_LOAD_IMAGE);
        });

        uploadButton.setOnClickListener(view -> {
            if (imageView.getDrawable() == null) {
                Snackbar.make(findViewById(R.id.upload_layout), R.string.select_image, Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (titleEditText.getText() == null || titleEditText.getText().toString().isEmpty()) {
                Snackbar.make(findViewById(R.id.upload_layout), R.string.insert_title, Snackbar.LENGTH_SHORT).show();
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
            uploadTask.addOnFailureListener(exception -> Snackbar.make(findViewById(R.id.upload_layout), R.string.failed_to_upload, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> Data.uploadPost(titleEditText.getText().toString(), id, uri.toString()));
                        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Bitmap bitmap;
            if (data.getExtras() != null && data.getExtras().get("data") instanceof Bitmap) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                try {
                    bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    Snackbar.make(getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            imageView.setImageBitmap(bitmap);
        }
    }
}
