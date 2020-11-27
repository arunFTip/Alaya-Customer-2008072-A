package com.ftipinfosol.alayachits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.ftipinfosol.alayachits.Adapters.HttpCache;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class OptionActivity extends AppCompatActivity{
    JSONObject ticket;
    String tiid, cuid;
    private RequestParams params = new RequestParams();
    private AsyncHttpClient client = new AsyncHttpClient();

    private boolean isLoading = false;
    private boolean refresh = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    EditText newChitRequestAmount;
    private ProgressDialog dialog;
    private ProgressDialog dialogPayTM;

    private String  TAG ="OptionActivity";
    private Integer ActivityRequestCode = 2;
    private String midString ="eWrKgz59911424619072", txnAmountString="", orderIdString="", txnTokenString="";
    EditText etPaymentAmount;

    TextView tvPaytmResponse, tvPaytmResponseAddPaymentInSoftware;
    String mPaytmResponse, mPaytmResponseAddPaymentInSoftware;


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
            cuid = ticket.getString("cuid");
            Log.e("printticket", tiid + "print"+ ticket.toString());
            setSupportActionBar(toolbar);
//            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (JSONException e) {
            e.printStackTrace();
            finish();
        }

        etPaymentAmount = findViewById(R.id.payment_amount);
        tvPaytmResponse = findViewById(R.id.tvPaytmResponse);
        tvPaytmResponseAddPaymentInSoftware = findViewById(R.id.tvPaytmResponseAddPaymentInSoftware);

        mPaytmResponse = "PayTM Response : ";
        mPaytmResponseAddPaymentInSoftware = "Software Response : ";

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

        dialogPayTM = new ProgressDialog(this);
        dialogPayTM.setCancelable(false);
    }



    public void payment_request(View view) throws Exception {
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
                try {
                    Log.e("paymentRequestReturnFai", response.toString());
                }catch (Exception er){
                    er.printStackTrace();
                }

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

    public void make_payment(View view) {

        //Toast.makeText(getApplicationContext(), )
        tvPaytmResponse.setText("");
        tvPaytmResponseAddPaymentInSoftware.setText("");
        tvPaytmResponseAddPaymentInSoftware.setVisibility(View.VISIBLE);

        Log.e(TAG, " get token start");
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String date = df.format(c.getTime());
        Random rand = new Random();
        int min =1000, max= 9999;
        // nextInt as provided by Random is exclusive of the top value so you need to add 1
        int randomNum = rand.nextInt((max - min) + 1) + min;
        orderIdString =  date+String.valueOf(randomNum);
        //progressBar.setVisibility(View.VISIBLE);

        txnAmountString = etPaymentAmount.getText().toString();
        if(txnAmountString.matches((""))){
            //Toast.makeText(getApplicationContext(),"Please Enter Amount", Toast.LENGTH_LONG).show();
            etPaymentAmount.setError("Please Enter Amount");
            etPaymentAmount.findFocus();
            return;
        }

        params.put("orderID", orderIdString);
        params.put("cusID", cuid);
        params.put("amount", txnAmountString);
        //client.addHeader("Accept", "application/json");
        //client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

        client.post(Config.GET_PAYTM_CHECKSM, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                try {
                    JSONObject errors = response.getJSONObject("body");
                    Log.e("paymentRequestReturn", errors.getString("txnToken"));
                    startPaytmPayment(errors.getString("txnToken"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
                Toast.makeText(getApplicationContext(), "Payment initiated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject response) {
                //Log.e("paymentRequestReturnFai", response.toString());
                if (statusCode == 422) {
                    Log.e("paymentRequestReturnFai", "in 422");
                    try {
                        JSONObject errors = response.getJSONObject("errors");
                        Log.e("paymentRequestReturnFai", "in error - " + errors.toString());
                        //Toast.makeText(getApplicationContext(), errors.getString("tiid"), Toast.LENGTH_SHORT).show();
//                        if (errors.has("tiid")) {
//                            Toast.makeText(getApplicationContext(), errors.getJSONArray("tiid").getString(0), Toast.LENGTH_SHORT).show();
//                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } else if (statusCode == 401) {
//                    Intent i = new Intent(OptionActivity.this, MainActivity.class).putExtra("logout", "logout");
//                    startActivity(i);
                }
            }
        });
    }

    public void startPaytmPayment (String token){
        txnTokenString = token;
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";//commented
        // for production mode use it
        //String host = "https://securegw.paytm.in/";
        String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString
                + ", Amount: " + txnAmountString;
        //Log.e(TAG, "order details "+ orderDetails);
        String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdString;
        Log.e(TAG, " callback URL "+callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdString, midString, txnTokenString, txnAmountString, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());

                //Toast.makeText(getApplicationContext(), "Payment has been completed successfully", Toast.LENGTH_LONG).show();

                String respMsg =bundle.getString("RESPMSG");
                Log.e("OptionActivity", "RESPMSG : "+ respMsg);
                tvPaytmResponse.setText(mPaytmResponse+ respMsg);
                tvPaytmResponseAddPaymentInSoftware.setText(mPaytmResponseAddPaymentInSoftware+"Updating...");

                if(respMsg != null) {
                    if (respMsg.equals("Txn Success")) {
                        Toast.makeText(getApplicationContext(), "Payment has been completed successfully", Toast.LENGTH_LONG).show();
                        tvPaytmResponse.setTextColor(getResources().getColor(R.color.colorGreen));
                        params.put("tiid", tiid);
                        params.put("cuid", cuid);
                        params.put("amount", txnAmountString);
                        client.addHeader("Accept", "application/json");
                        client.addHeader("Authorization", MainActivity.AUTH_TOKEN);

                        dialogPayTM.setMessage("Payment success. Now Updating software..");
                        dialogPayTM.show();

                        client.post(Config.PAYTM_COLLECTION, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                try {
                                    dialogPayTM.dismiss();
                                    if (response.getString("message").equals("success")) {
                                        tvPaytmResponseAddPaymentInSoftware.setText(mPaytmResponseAddPaymentInSoftware + "Updated Successfully");
                                        tvPaytmResponseAddPaymentInSoftware.setTextColor(getResources().getColor(R.color.colorGreen));
                                        etPaymentAmount.setText("");
                                    } else {
                                        tvPaytmResponseAddPaymentInSoftware.setText(mPaytmResponseAddPaymentInSoftware + "Updated failed. Contact Office");
                                        tvPaytmResponseAddPaymentInSoftware.setTextColor(getResources().getColor(R.color.colorRed));
                                        //Add appCare bugLog
                                        params.put("log_code","C3001");
                                        params.put("description", "OrderID : "+ orderIdString + " <br> "+" CustomerID : "+ cuid +" <br> " + "Ticket ID : "+ tiid + " <br> "+ " Amount : "+ txnAmountString);
                                        params.put("debug", getResources().getString(R.string.app_care_bug_log_debug));

                                        client.post( "http://appcare.sf3.in/api/V1/saveBugRaiseV1", params, new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                                try {
                                                    //Log.e("bugRaise",response.toString());

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                //HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
                                                //Toast.makeText(getApplicationContext(), "Payment initiated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                            @Override
                                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject response) {
                                                //Log.e("paymentRequestReturnFai", response.toString());

                                                if (statusCode == 422) {
                                                    Log.e("paymentRequestReturnFai", "in 422");
                                                    try {
                                                        JSONObject errors = response.getJSONObject("errors");
                                                        Log.e("paymentRequestReturnFai", "in error - " + errors.toString());

                                                    } catch (JSONException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                } else if (statusCode == 401) {
                                                }
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject response) {
                                dialogPayTM.dismiss();
                                tvPaytmResponseAddPaymentInSoftware.setText(mPaytmResponseAddPaymentInSoftware + "Updated failed. Contact Office");
                                tvPaytmResponseAddPaymentInSoftware.setTextColor(getResources().getColor(R.color.colorRed));
                                //Add appCare bugLog
                                params.put("log_code","C3001");
                                params.put("description", "OrderID : "+ orderIdString + " <br> "+" CustomerID : "+ cuid +" <br> " + "Ticket ID : "+ tiid + " <br> "+ " Amount : "+ txnAmountString);
                                params.put("debug", R.string.app_care_bug_log_debug);

                                client.post( "http://appcare.sf3.in/api/V1/saveBugRaiseV1", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                                        try {
                                            //Log.e("bugRaise",response.toString());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //HttpCache.write(getApplicationContext(), "report"+params, String.valueOf(response));
                                        //Toast.makeText(getApplicationContext(), "Payment initiated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable e, JSONObject response) {
                                        //Log.e("paymentRequestReturnFai", response.toString());

                                        //gotoHome();
                                        if (statusCode == 422) {
                                            Log.e("paymentRequestReturnFai", "in 422");
                                            try {
                                                JSONObject errors = response.getJSONObject("errors");
                                                Log.e("paymentRequestReturnFai", "in error - " + errors.toString());

                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else if (statusCode == 401) {
                                        }
                                    }
                                });
                                //Log.e("paymentRequestReturnFai", response.toString());
                                if (statusCode == 422) {
                                    Log.e("paymentRequestReturnFai", "in 422");
                                    try {
                                        JSONObject errors = response.getJSONObject("errors");
                                        Log.e("paymentRequestReturnFai", "in error - " + errors.toString());

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (statusCode == 401) {

                                }
                            }
                        });


                    } else {
                        tvPaytmResponse.setTextColor(getResources().getColor(R.color.colorRed));
                        tvPaytmResponseAddPaymentInSoftware.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Payment failed. Something went wrong", Toast.LENGTH_LONG).show();
                    }
//                }
//
//                String respMsg2 = bundle.getString("STATUS");
//                Log.e(TAG, "Response (respMsg2) : "+respMsg2);
//                if(respMsg2.equals("PENDING")){
//                    new SweetAlertDialog(OptionActivity.this, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Failed")
//                            .setContentText("Something went wrong. Please try again. If amount debited, will credit automatically to your account.")
//                            .show();
//                    //gotoHome();
//                }
//                if(respMsg2 != null){
//                    new SweetAlertDialog(OptionActivity.this, SweetAlertDialog.ERROR_TYPE)
//                            .setTitleText("Failed")
//                            .setContentText("Something went wrong. Please try again. If amount debited, will credit automatically to your account.")
//                            .show();
//                    //gotoHome();
                }
                //Toast.makeText(getApplicationContext(), respMsg, Toast.LENGTH_LONG).show();
                //gotoHome();
            }
            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }
            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+s.toString());
            }
            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }
            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }
            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }
            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }
            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });
        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, ActivityRequestCode);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG ," result code "+resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));
/*
 data response - {"BANKNAME":"WALLET","BANKTXNID":"1394221115",
 "CHECKSUMHASH":"7jRCFIk6eRmrep+IhnmQrlrL43KSCSXrmM+VHP5pH0ekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
 "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAcP3138556","ORDERID":"100620202152",
 "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS",
 "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"2020061011121280011018328631290118"}
  */
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }else{
            Log.e(TAG, " payment failed");
        }
    }

    private void gotoHome(){
        startActivity(new Intent(OptionActivity.this, HomeActivity.class));
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
