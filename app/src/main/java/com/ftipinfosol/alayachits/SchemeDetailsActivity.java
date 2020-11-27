package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.TextView;

import com.ftipinfosol.alayachits.Adapters.SchemeDetailsAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SchemeDetailsActivity extends AppCompatActivity {

    JSONObject scheme;
    String scid;
    TextView scheme_name, scheme_value, duration, subscription;

    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    RecyclerView recyclerView;
    private ProgressDialog dialog;
    private SchemeDetailsAdapter adapter;
    private List<JSONObject> scheme_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_details);
        Toolbar toolbar = findViewById(R.id.toolbar);

        scheme_name = findViewById(R.id.scheme_name);
        scheme_value = findViewById(R.id.scheme_value);
        duration = findViewById(R.id.duration);
        subscription = findViewById(R.id.subscription);

        try {
            scheme = new JSONObject(getIntent().getStringExtra("scheme"));
            toolbar.setTitle("SCHEME : " + scheme.getString("scheme_name"));
            scid = scheme.getString("scid");
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            scheme_name.setText(scheme.getString("scheme_name"));
            scheme_value.setText(scheme.getString("scheme_value"));
            subscription.setText(scheme.getString("subscription"));
            String duration_value = scheme.getString("duration")+((scheme.getInt("scheme_type")==1)?" Weeks":" Months");
            duration.setText(duration_value);

        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        adapter = new SchemeDetailsAdapter(scheme_list);
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        prepareData();
    }

    private void prepareData() {
        dialog.setMessage("Loading...");
        dialog.show();

        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);
        client.get(Config.SCHEME_DETAILS_URL+scid, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                process_data(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable err, JSONObject error) {
                err.printStackTrace();
                if(statusCode==401)
                {
                    Intent i = new Intent(SchemeDetailsActivity.this, MainActivity.class).putExtra("logout", "logout");
                    startActivity(i);
                }
            }
        });
    }

    public void process_data(JSONArray response)
    {
        try {
            JSONObject scheme_value = new JSONObject();
            scheme_value.put("month",((scheme.getInt("scheme_type")==1)?" Week":" Month"));
            scheme_value.put("bid_amount","Bid Amount");
            scheme_value.put("divident","Dividend");
            scheme_value.put("subscription","Subscription");
            scheme_list.add(scheme_value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject scheme_value = response.getJSONObject(i);
                    scheme_list.add(scheme_value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                JSONObject scheme_value = new JSONObject();
                scheme_value.put("month","");
                scheme_value.put("bid_amount","");
                scheme_value.put("divident",scheme.getString("total_divident"));
                scheme_value.put("subscription",scheme.getString("total_subscription"));
                scheme_list.add(scheme_value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
//            adapter.notifyItemRangeInserted(0, scheme_list.size());
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
