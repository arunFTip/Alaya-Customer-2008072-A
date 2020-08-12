package com.ftipinfosol.alayachits;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

public class ReturnRefundActivity extends AppCompatActivity {

    TextView txtReturnRefund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_refund);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("REFUND AND CANCELLATION POLICY");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtReturnRefund = findViewById(R.id.txt_return_refund);
        txtReturnRefund.setText(Html.fromHtml(getString(R.string.return_refund_large)));

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
