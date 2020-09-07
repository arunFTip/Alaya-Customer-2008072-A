package com.ftipinfosol.alayachits;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ftipinfosol.alayachits.Adapters.HttpCache;
import com.ftipinfosol.alayachits.Adapters.TicketsAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity {

    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    RecyclerView recyclerView;
    private ProgressDialog dialog;
    private TicketsAdapter adapter;
    private List<JSONObject> ticket_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("SREE ALAYA CHITS");
        setSupportActionBar(toolbar);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        adapter = new TicketsAdapter(ticket_list,  new TicketsAdapter.OnItemClickListener() {
            @Override public void onItemClick(JSONObject ticket) {
                Log.e("passinglogH",ticket.toString());
                Intent i = new Intent(HomeActivity.this, PassbookActivity.class).putExtra("ticket", String.valueOf(ticket));
                startActivity(i);
            }
        });
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        prepareData();

        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE},
                1);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(HomeActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void prepareData() {
        dialog.setMessage("Loading...");
        dialog.show();

        Log.e("homeactivitylog", MainActivity.AUTH_TOKEN);

        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);
        client.get(Config.TICKETS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("homeactivitylog", response.toString());
                process_data(response);
                HttpCache.write(getApplicationContext(), "ticket"+params, String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable err, JSONObject error) {
                err.printStackTrace();
                try {
                    Log.e("getTicketFailure", error.toString());
                    //dialog.dismiss();
                    JSONArray response = new JSONArray(HttpCache.read(getApplicationContext(), "ticket"+params));
                    process_data(response);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                if(statusCode==401)
                {
                    Intent i = new Intent(HomeActivity.this, MainActivity.class).putExtra("logout", "logout");
                    startActivity(i);
                }
            }
        });
    }

    public void process_data(JSONArray response)
    {
        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject ticket_value = response.getJSONObject(i);
                    ticket_list.add(ticket_value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.notifyItemRangeInserted(0, ticket_list.size());
        }
        dialog.dismiss();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_schemes:
                startActivity(new Intent(this, SchemesActivity.class));
                return true;
            case R.id.action_how_chit_works:
                startActivity(new Intent(this, HowChitWorks.class));
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
