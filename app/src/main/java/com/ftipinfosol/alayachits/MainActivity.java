package com.ftipinfosol.alayachits;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import eu.dkaratzas.android.inapp.update.InAppUpdateStatus;

public class MainActivity extends AppCompatActivity implements InAppUpdateManager.InAppUpdateHandler{

    public static JSONObject AUTH_USER;
    public static String AUTH_TOKEN;
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final int REQ_CODE_VERSION_UPDATE = 530;
    private InAppUpdateManager inAppUpdateManager;

    @Override
    public void onInAppUpdateError(int code, Throwable error) {

    }

    @Override
    public void onInAppUpdateStatus(InAppUpdateStatus status) {
        /*
         * If the update downloaded, ask user confirmation and complete the update
         */

        if (status.isDownloaded()) {

            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(rootView,
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("RESTART", view -> {

                // Triggers the completion of the update of the app for the flexible flow.
                inAppUpdateManager.completeUpdate();

            });

            snackbar.show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sp = getSharedPreferences("API", Context.MODE_PRIVATE);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        Button crashButton = new Button(this);
//        crashButton.setText("Crash!");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });
//
//        addContentView(crashButton, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));

        inAppUpdateManager = InAppUpdateManager.Builder(this, REQ_CODE_VERSION_UPDATE)
                .resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                .mode(eu.dkaratzas.android.inapp.update.Constants.UpdateMode.IMMEDIATE)
                // default is false. If is set to true you,
                // have to manage the user confirmation when
                // you detect the InstallStatus.DOWNLOADED status,
                .useCustomNotification(true)
                .handler(this);

        inAppUpdateManager.checkForAppUpdate();


        Intent intent = getIntent();
        if (intent.hasExtra("logout")) {
            logout(sp);
        }
        if (sp.contains("TOKEN")) {
            try {
                AUTH_USER = new JSONObject(sp.getString("TOKEN", ""));
                AUTH_TOKEN = "Bearer "+AUTH_USER.getString("token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
    }

    public void logout(SharedPreferences sp) {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
