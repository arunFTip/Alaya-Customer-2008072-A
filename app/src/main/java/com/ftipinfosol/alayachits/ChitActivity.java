package com.ftipinfosol.alayachits;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ChitActivity extends AppCompatActivity {

    JSONObject ticket;
    String tiid;
    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

    private boolean isLoading = false;
    //private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView customer_name, cust_code, ticket_code, scheme_value, duration, month, amount, cheque_no, cheque_date, commence_date, end_date, auction_no, running_balance;
    CardView card;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_passbook:
                    startActivity(new Intent(getApplicationContext(), PassbookActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
                case R.id.navigation_options:
                    startActivity(new Intent(getApplicationContext(), OptionActivity.class).putExtra("ticket", String.valueOf(ticket)));
                    return true;
                case R.id.navigation_ledger_extract:
                    startActivity(new Intent(getApplicationContext(), LedgerExtract.class).putExtra("ticket",String.valueOf(ticket)));
                    return true;
                case R.id.navigation_profile:
                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chit);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_profile);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            ticket = new JSONObject(getIntent().getStringExtra("ticket"));
            toolbar.setTitle("Ticket : "+(ticket.getString("ticket_code").length()>0?ticket.getString("ticket_code"):ticket.getString("temp_id")));
            tiid = ticket.getString("tiid");
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

        customer_name = findViewById(R.id.customer_name);
        cust_code = findViewById(R.id.cust_code);
        ticket_code = findViewById(R.id.ticket_code);
        scheme_value = findViewById(R.id.scheme_value);
        commence_date = findViewById(R.id.commence_date);
        end_date = findViewById(R.id.end_date);
        auction_no = findViewById(R.id.auction_no);
        running_balance =  findViewById(R.id.running_balance);
        duration = findViewById(R.id.duration);
        month = findViewById(R.id.month);
        amount = findViewById(R.id.amount);
        cheque_no = findViewById(R.id.cheque_no);
        cheque_date = findViewById(R.id.cheque_date);
        card = findViewById(R.id.card);

        //mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        prepareData();

//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                isLoading = false;
//                prepareData();
//            }
//        });

    }




    private void prepareData() {
//        if(isLoading){return;}
//        else
//        { isLoading=true;}
        params.put("tiid", tiid);
        client.addHeader("Accept", "application/json");
        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);
        client.get(Config.CHIT_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject ticket = response.getJSONObject("ticket");

                    customer_name.setText(ticket.getString("customer_name"));
                    cust_code.setText(ticket.getString("cust_code"));
                    ticket_code.setText(ticket.getString("ticket_code").length()>0?ticket.getString("ticket_code"):ticket.getString("temp_id"));
                    scheme_value.setText(ticket.getString("scheme_value"));
                    duration.setText(ticket.getString("duration"));
                    String auc = ticket.optString("auction_no","")+"/"+ticket.optString("month_balance","");
                    auction_no.setText(auc);
                    running_balance.setText(ticket.getString("running_balance"));
                    Date date_val = format.parse(ticket.getString("start_date"));
                    commence_date.setText(date_format.format(date_val));
                    Date end_date_val = format.parse(ticket.getString("end_date"));
                    end_date.setText(date_format.format(end_date_val));

                    if(response.isNull("payment"))
                    {
                        card.setVisibility(View.GONE);
                    }
                    else{
                        card.setVisibility(View.VISIBLE);
                        JSONObject payment = response.getJSONObject("payment");
                        month.setText(payment.getString("month"));
                        amount.setText(payment.getString("amount"));
                        cheque_no.setText(payment.getString("cheque_no"));
                        Date cheque_val = format.parse(payment.getString("cheque_date"));
                        cheque_date.setText(date_format.format(cheque_val));
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
                //mSwipeRefreshLayout.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                e.printStackTrace();
            }
        });
    }


    public void logout(View view) {
        Intent i = new Intent(ChitActivity.this, MainActivity.class).putExtra("logout", "logout");
        startActivity(i);
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
