package com.ftipinfosol.alayachits;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ftipinfosol.alayachits.Adapters.HttpCache;
import com.ftipinfosol.alayachits.Adapters.LedgerExtractAdapter;
import com.ftipinfosol.alayachits.Adapters.ReportsAdapter;
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

public class LedgerExtract extends AppCompatActivity {
    JSONObject ticket;
    String tiid;
    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    private boolean isLoading = false;
    private boolean refresh = false;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    private LedgerExtractAdapter adapter;
    private List<JSONObject> report_list = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_passbook:
                    startActivity(new Intent(getApplicationContext(), PassbookActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
//                case R.id.navigation_report:
//                    startActivity(new Intent(getApplicationContext(), ReportsActivity.class).putExtra("ticket", String.valueOf(ticket)));
//                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(getApplicationContext(), ChitActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;

                case R.id.navigation_ledger_extract:
                    startActivity(new Intent(getApplicationContext(), LedgerExtract.class).putExtra("ticket",String.valueOf(ticket)));
                    return true;
                case R.id.navigation_options:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger_extract);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_ledger_extract);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            ticket = new JSONObject(getIntent().getStringExtra("ticket"));
            toolbar.setTitle("Ledger : "+(ticket.getString("ticket_code").length()>0?ticket.getString("ticket_code"):ticket.getString("temp_id")));
            tiid = ticket.getString("tiid");
            Log.d("ledgerExtracttiid", tiid);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

        //mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        adapter = new LedgerExtractAdapter(report_list);
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        prepareData();

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh = true;
//                report_list.clear();
//                isLoading = false;
//                prepareData();
//            }
//        });
    }

    private void prepareData() {
        if(isLoading){return;}
        else
        { isLoading=true;}

        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

//        try {
//            if(!refresh)
//            {
//                JSONObject response = new JSONObject(HttpCache.read(getApplicationContext(), "report"+params));
//                process_data(response);
//                return;
//            }
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }

        client.get(Config.LEDGER_EXTRACT+tiid, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(getApplicationContext(), "on success", Toast.LENGTH_SHORT).show();
                Log.e("ledgerExtracttiid",response.toString());
                process_data(response);
                HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Toast.makeText(getApplicationContext(), "on failure", Toast.LENGTH_SHORT).show();
                Log.e("ledgerExtracttiidfail",response.toString());
                e.printStackTrace();
            }
        });
    }

    public void process_data(JSONObject response) {
        if (refresh) {
            adapter.notifyDataSetChanged();
        }
        try {
            JSONArray data = response.getJSONArray("report");
            if (data != null) {
                for (int i = 0; i < data.length(); i++) {
                    try {
                        JSONObject report_value = data.getJSONObject(i);
                        report_list.add(report_value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                report_list.add(response.getJSONObject("total"));
                Log.e("totaljson",response.getJSONObject("total").toString());
                isLoading=false;
                //mSwipeRefreshLayout.setRefreshing(false);
                adapter.notifyItemRangeInserted(0, report_list.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_schemes:
                startActivity(new Intent(this, SchemesActivity.class));
                return true;
            case R.id.action_contact:
                startActivity(new Intent(this, ContactAcivity.class));
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }
}
