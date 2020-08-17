package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ftipinfosol.alayachits.Adapters.HttpCache;
import com.ftipinfosol.alayachits.Adapters.ReportsAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class OptionActivity extends AppCompatActivity {
    JSONObject ticket;
    String tiid;
    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    private boolean isLoading = false;
    private boolean refresh = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    EditText newChitRequestAmount;
    private ProgressDialog dialog;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    startActivity(new Intent(getApplicationContext(), ChitActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
                case R.id.navigation_options:
                    return true;
                case R.id.navigation_ledger_extract:
                    startActivity(new Intent(getApplicationContext(), LedgerExtract.class).putExtra("ticket",String.valueOf(ticket)));
                    return true;
                case R.id.navigation_passbook:
                    startActivity(new Intent(getApplicationContext(), PassbookActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_options);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            ticket = new JSONObject(getIntent().getStringExtra("ticket"));
            toolbar.setTitle("Options : "+(ticket.getString("ticket_code").length()>0?ticket.getString("ticket_code"):ticket.getString("temp_id")));
            tiid = ticket.getString("tiid");
            Log.e("printticket", tiid + "print"+ ticket.toString());
            setSupportActionBar(toolbar);
//            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//            }
//        });

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        newChitRequestAmount = findViewById(R.id.chit_request_amount);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
    }



    public void payment_request(View view) {
        //Toast.makeText(getApplicationContext(), "TEst", Toast.LENGTH_SHORT).show();
        params.put("tiid", tiid);
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

        client.post(Config.PAYMENT_REQUEST, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("paymentRequestReturn", response.toString());
                HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
                Toast.makeText(getApplicationContext(), "Payment request sent successfully", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("paymentRequestReturnFai", response.toString());
                if(statusCode==422)
                {
                    Log.e("paymentRequestReturnFai", "in 422");
                    try {
                        JSONObject errors = response.getJSONObject("errors");
                        Log.e("paymentRequestReturnFai", "in error - "+ errors.toString());
                        //Toast.makeText(getApplicationContext(), errors.getString("tiid"), Toast.LENGTH_SHORT).show();
                        if(errors.has("tiid"))
                        {
                            Toast.makeText(getApplicationContext(), errors.getJSONArray("tiid").getString(0), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                else if(statusCode==401)
                {
                    Intent i = new Intent(OptionActivity.this, MainActivity.class).putExtra("logout", "logout");
                    startActivity(i);
                }
            }
        });

    }


    public void new_chit_request(View view) {
        try {

            if(newChitRequestAmount.getText().toString().matches("")){
                Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                newChitRequestAmount.findFocus();
                return;
            }

            params.put("cuid", ticket.getString("cuid"));
            params.put("date","15");
            params.put("amount",newChitRequestAmount.getText().toString());
            client.addHeader("Accept", "application/json");
            client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

            client.post(Config.NEW_CHIT_REQUEST, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.e("chitRequestReturn", response.toString());
                    HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
                    Toast.makeText(getApplicationContext(), "New Chit request sent successfully", Toast.LENGTH_SHORT).show();
                    newChitRequestAmount.setText("");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.e("chitRequestReturnFai", response.toString());
                    if(statusCode==422)
                    {
                        Log.e("chitRequestReturnFai", "in 422");
                        try {
                            JSONObject errors = response.getJSONObject("errors");
                            Log.e("chitRequestReturnFai", "in error - "+ errors.toString());
                            Toast.makeText(getApplicationContext(), errors.getString("tiid"), Toast.LENGTH_SHORT).show();
//                        if(errors.has("tiid"))
//                        {
//                            Toast.makeText(getApplicationContext(), errors.getString("tiid"), Toast.LENGTH_SHORT).show();
//                        }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else if(statusCode==401)
                    {
                        Intent i = new Intent(OptionActivity.this, MainActivity.class).putExtra("logout", "logout");
                        startActivity(i);
                    }
                }
            });


        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }


    public void download_statement(View view){
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

        dialog.setMessage("Loading...");
        dialog.show();

        client.get(Config.DOWNLOAD_STATEMENT+tiid, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                dialog.dismiss();
                Log.e("downloadRequestReturn", response.toString());
                String _filename="test";
                try {
                    JSONObject success = response.getJSONObject("success");
                    if(success.has("path")){
                        Log.e("downloadRequestReturn", success.getJSONArray("path").getString(0));
                        _filename = success.getJSONArray("path").getString(0);
                    }else {
                        Toast.makeText(getApplicationContext(), "No file downloaded", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                //String _url = "http://192.168.1.6:8000/ledger_view_statement/"+_filename;
                String _url = Config.DOWNLOAD_STATEMENT_URL+_filename;

                HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
                Toast.makeText(getApplicationContext(), "Download request sent successfully", Toast.LENGTH_SHORT).show();
                //new DownloadFile().execute("http://192.168.1.6:8000/ledger_view_statement/Statement_A1C-6-7.pdf", "Statement_A1C.pdf");
                new DownloadFile().execute(_url, _filename);
                //showPdf("AlayaChits","Statement_A1C.pdf" );
                showPdf("AlayaChits",_filename );

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                dialog.dismiss();
                Log.e("downloadRequestRetFai", response.toString());
            }

        });

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "AlayaChits");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    public void showPdf(String folder, String filename)
    {
        //File file = new File(Environment.getExternalStorageDirectory()+"/pdf/Read.pdf");
        File file = new File(Environment.getExternalStorageDirectory()+"//"+folder+"//"+filename);
        //File file = new File("/storage/emulated/0/AlayaChits/Statement_A1C.pdf");
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(OptionActivity.this,BuildConfig.APPLICATION_ID + ".provider", file);
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_schemes:
                startActivity(new Intent(this, SchemesActivity.class));
                return true;
            case R.id.action_contact:
                startActivity(new Intent(this, ContactAcivity.class));
                return true;
            case R.id.action_privacy_policy:
                startActivity(new Intent(this,PrivacyPolicyActivity.class));
                return true;
            case R.id.action_terms_conditions:
                startActivity(new Intent(this,TermsConditionsActivity.class));
                return true;
            case R.id.action_refund_cancellation:
                startActivity(new Intent(this,ReturnRefundActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
