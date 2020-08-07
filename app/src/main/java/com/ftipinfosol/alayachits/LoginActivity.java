package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    private EditText input_mobile, input_otp;
    private LinearLayout otp_form, verify_form;
    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();
    private ProgressDialog dialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        input_mobile = findViewById(R.id.input_mobile);
        input_otp = findViewById(R.id.input_otp);
        otp_form = findViewById(R.id.otp_form);
        verify_form = findViewById(R.id.verify_form);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sp = getSharedPreferences("API", Context.MODE_PRIVATE);
    }

    public void get_otp(View view) {
        if(input_mobile.getText().toString().trim().isEmpty()||input_mobile.getText().toString().length()<10)
        {
            input_mobile.setError("Please fill valid Mobile");
            input_mobile.requestFocus();
            return;
        }
        else {
            input_mobile.setError(null);
        }

        params.put("mobile", input_mobile.getText().toString());
        dialog.setMessage("Loading..");
        dialog.show();
        client.addHeader("Accept", "application/json");
        client.post(Config.OTP_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                otp_form.setVisibility(View.GONE);
                verify_form.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                if(statusCode==422)
                {
                    try {
                        JSONObject errors = response.getJSONObject("errors");
                        if(errors.has("mobile"))
                        {
                            input_mobile.setError(errors.getJSONArray("mobile").getString(0));
                            input_mobile.requestFocus();
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                }
                else if(statusCode==401)
                {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class).putExtra("logout", "logout");
                    startActivity(i);
                }
                dialog.dismiss();
            }

        });
    }

    public void verify_otp(View view) {
        if(input_otp.getText().toString().trim().isEmpty()||input_otp.getText().toString().length()<6)
        {
            input_otp.setError("Please fill valid OTP");
            input_otp.requestFocus();
            return;
        }
        else {
            input_otp.setError(null);
        }


        params.put("mobile", input_mobile.getText().toString());
        params.put("otp", input_otp.getText().toString());
        dialog.setMessage("Loading..");
        dialog.show();
        client.addHeader("Accept", "application/json");
        client.post(Config.VERIFY_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                sp.edit().putString("TOKEN", String.valueOf(response)).apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                try {
                    JSONObject errors = response.getJSONObject("errors");
                    if(errors.has("otp"))
                    {
                        input_otp.setError(errors.getJSONArray("otp").getString(0));
                        input_otp.requestFocus();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                dialog.dismiss();
            }

        });
    }


}
