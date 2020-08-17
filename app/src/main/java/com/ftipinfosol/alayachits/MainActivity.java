package com.ftipinfosol.alayachits;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static JSONObject AUTH_USER;
    public static String AUTH_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sp = getSharedPreferences("API", Context.MODE_PRIVATE);
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
