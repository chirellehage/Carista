package com.carista.ui.main.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.carista.App;
import com.carista.MainActivity;
import com.carista.R;
import com.carista.api.RetrofitManager;
import com.carista.api.models.UpdateResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSettingsFragment extends Fragment {

    private SwitchCompat darkThemeSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_settings, container, false);
        return view ;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logoutButton = view.findViewById(R.id.btn_logout);
        darkThemeSwitch = view.findViewById(R.id.dark_theme_switch);
        Button checkForUpdate = view.findViewById(R.id.btn_check_update);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(App.PREF_DARK_THEME, Context.MODE_PRIVATE);
        darkThemeSwitch.setChecked(sharedPreferences.getBoolean(App.PREF_DARK_THEME, false));

        SharedPreferences.Editor editor = sharedPreferences.edit();

        darkThemeSwitch.setOnClickListener(view1 -> {
            if (darkThemeSwitch.isChecked()) {
                editor.putBoolean(App.PREF_DARK_THEME, true);
                editor.apply();
                ((MainActivity) getActivity()).switchTheme(true);
            } else {
                editor.putBoolean(App.PREF_DARK_THEME, false);
                editor.apply();
                ((MainActivity) getActivity()).switchTheme(false);
            }
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        });

        checkForUpdate.setOnClickListener(view1 -> {
            PackageInfo pInfo = null;
            try {
                pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);

                RetrofitManager.getInstance(getContext()).getUpdateApi().getUpdates(pInfo.versionName).enqueue(new Callback<UpdateResponse>() {
                    @Override
                    public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                        if (response.body() != null) {
                            if (response.body().isLatestUpdate)
                                Snackbar.make(getView(), R.string.already_on_latest_update, Snackbar.LENGTH_SHORT).show();
                            else
                                new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.update)
                                        .setMessage(getString(R.string.new_update_available, response.body().latestVersion))
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> Snackbar.make(getView(), R.string.an_error_has_occurred, Snackbar.LENGTH_SHORT).show())
                                        .setNegativeButton(android.R.string.cancel, null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UpdateResponse> call, Throwable t) {
                        Snackbar.make(getView(), R.string.an_error_has_occurred, Snackbar.LENGTH_SHORT).show();
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
