package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ftipinfosol.alayachits.Adapters.PaymentsAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PassbookActivity extends AppCompatActivity {

    JSONObject ticket;
    String tiid;
    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    private int count = 0;
    private boolean completed = false;
    private boolean isLoading = false;
    private boolean refresh = false;

    private ProgressDialog dialog;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView recyclerView;
    private PaymentsAdapter adapter;
    private List<JSONObject> payment_list = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    startActivity(new Intent(getApplicationContext(), ChitActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
                case R.id.navigation_options:
                    startActivity(new Intent(getApplicationContext(), OptionActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
                case R.id.navigation_ledger_extract:
                    startActivity(new Intent(getApplicationContext(), LedgerExtract.class).putExtra("ticket",String.valueOf(ticket)));
                    return true;
                case R.id.navigation_passbook:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledger);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_passbook);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            ticket = new JSONObject(getIntent().getStringExtra("ticket"));
            Log.e("passinglogP",ticket.toString());
            toolbar.setTitle("Passbook : "+(ticket.getString("ticket_code").length()>0?ticket.getString("ticket_code"):ticket.getString("temp_id")));
            tiid = ticket.getString("tiid");
            setSupportActionBar(toolbar);
//            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (JSONException e) {
            Log.e("passinglogPE",e.toString());
            e.printStackTrace();
            finish();
        }

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
//            }
//        });

        //mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        adapter = new PaymentsAdapter(payment_list);
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        prepareData();        //uncomment before live

//        recyclerView.addOnScrollListener(new EndlessScroll() {
//            @Override
//            public void onLoadMore() {
//                prepareData();
//            }
//        });

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh = true;
//                payment_list.clear();
//                count = 0;
//                isLoading = false;
//                completed = false;
//                prepareData();
//            }
//        });
    }



    private void prepareData() {
//        if(completed||isLoading){return;}
//        else
//        { isLoading=true;}

        Log.e("passBookPre", "in");
        dialog.setMessage("Loading...");
        dialog.show();
        count=payment_list.size();
        //params.put("skip", payment_list.size());
        params.put("tiid", tiid);
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

//        try {
//            if(!refresh)
//            {
//                JSONArray response = new JSONArray(HttpCache.read(getApplicationContext(), "ledger"+params));
//                process_data(response);
//                Log.e("passbooklog",response.toString());
//                return;
//            }
//        } catch (JSONException | IOException e) {
//            e.printStackTrace();
//        }

        client.get(Config.LEDGER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                process_data(response);
                //HttpCache.write(getApplicationContext(), "ledger"+params, String.valueOf(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                //Log.e("passBookFail", response.toString());
                e.printStackTrace();
                dialog.dismiss();
            }
        });
    }

    public void process_data(JSONArray response)
    {
//        if(refresh)
//        {
//            adapter.notifyDataSetChanged();
//        }

        Log.e("passBookProc", response.toString());


        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject payment_value = response.getJSONObject(i);
                    payment_list.add(payment_value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(response.length()==0)
            {
                completed = true;
            }
            isLoading=false;
            //mSwipeRefreshLayout.setRefreshing(false);
            adapter.notifyItemRangeInserted(count, payment_list.size());
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
