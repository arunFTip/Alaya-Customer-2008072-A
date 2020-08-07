package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ftipinfosol.alayachits.Adapters.HttpCache;
import com.ftipinfosol.alayachits.Adapters.SchemesAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SchemesActivity extends AppCompatActivity {

    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    RecyclerView recyclerView;
    private ProgressDialog dialog;
    private SchemesAdapter adapter;
    private List<JSONObject> scheme_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("SCHEMES");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        adapter = new SchemesAdapter(scheme_list,  new SchemesAdapter.OnItemClickListener() {
            @Override public void onItemClick(JSONObject scheme) {
                Intent i = new Intent(SchemesActivity.this, SchemeDetailsActivity.class).putExtra("scheme", String.valueOf(scheme));
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
        client.get(Config.SCHEMES_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                process_data(response);
                HttpCache.write(getApplicationContext(), "scheme"+params, String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable err, JSONObject error) {
                err.printStackTrace();
                try {
                    JSONArray response = new JSONArray(HttpCache.read(getApplicationContext(), "scheme"+params));
                    process_data(response);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                if(statusCode==401)
                {
                    Intent i = new Intent(SchemesActivity.this, MainActivity.class).putExtra("logout", "logout");
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
                    JSONObject scheme_value = response.getJSONObject(i);
                    scheme_list.add(scheme_value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.notifyItemRangeInserted(0, scheme_list.size());
        }
        dialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
