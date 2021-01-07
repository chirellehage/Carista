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
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carista.MainActivity;
import com.carista.R;
import com.carista.utils.Data;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UserFragment extends Fragment {

    private static final int RESULT_LOAD_IMAGE = 100;
    private Button logoutButton, usernameChangeButton;
    private TextView userNickname;
    private EditText usernameEdit;
    private CircleImageView userAvatar;
    private Intent chooser;

    public UserFragment() {
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoutButton = view.findViewById(R.id.btn_logout);
        userNickname=view.findViewById(R.id.user_nickname);
        userAvatar=view.findViewById(R.id.user_avatar);
        usernameEdit=view.findViewById(R.id.username_change_edit);
        usernameChangeButton=view.findViewById(R.id.username_change_btn);

        UserInfo userInfo= FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("/users/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot avatar= dataSnapshot.child("avatar");
                DataSnapshot nickname=dataSnapshot.child("nickname");

                if(nickname.getValue()==null)
                    if(userInfo.getProviderId().equals("phone")){
                        userNickname.setText("Welcome, "+user.getPhoneNumber());
                    }
                    else{
                        userNickname.setText("Welcome, "+user.getEmail());
                    }
                else
                    userNickname.setText("Welcome, "+nickname.getValue());

                if(avatar.getValue()!=null)
                    Picasso.get().load(avatar.getValue().toString()).into(userAvatar);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ERROR", "Failed to read value.", error.toException());
            }
        });

        initChooser();



        userAvatar.setOnClickListener(view1 -> {
            startActivityForResult(chooser, RESULT_LOAD_IMAGE);
        });

        userNickname.setOnClickListener(view1 -> {
            usernameEdit.setVisibility(View.VISIBLE);
            usernameChangeButton.setVisibility(View.VISIBLE);
        });

        usernameChangeButton.setOnClickListener(view1 -> {
            String newNickname=usernameEdit.getText().toString();
            if(newNickname==null)
                return;
            usernameEdit.setText("");
            Data.uploadNickname(newNickname);
            Snackbar.make(getActivity().getCurrentFocus(), "Username changed!",Snackbar.LENGTH_SHORT).show();
            usernameEdit.setVisibility(View.GONE);
            usernameChangeButton.setVisibility(View.GONE);
        });



        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
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
                    bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(data.getData()));
                } catch (Exception e) {
                    Snackbar.make(getActivity().getCurrentFocus(), R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
            userAvatar.setImageBitmap(bitmap);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference("avatars");
            String imageName=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
            StorageReference imageRef = storageRef.child(imageName);

            userAvatar.setDrawingCacheEnabled(true);
            userAvatar.buildDrawingCache();
            Bitmap bitmapim = ((BitmapDrawable) userAvatar.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapim.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imdata = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(imdata);
            uploadTask.addOnFailureListener(exception -> Snackbar.make(getActivity().getCurrentFocus(), R.string.failed_to_upload, Snackbar.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> Data.uploadAvatarLink(uri.toString()));
                    });
        }
    }
}