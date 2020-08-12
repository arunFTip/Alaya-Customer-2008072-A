package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
                Intent i = new Intent(HomeActivity.this, PassbookActivity.class).putExtra("ticket", String.valueOf(ticket));
                startActivity(i);
            }
        });
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        prepareData();
    }

    private void prepareData() {
        dialog.setMessage("Loading...");
        dialog.show();

        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);
        client.get(Config.TICKETS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
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
