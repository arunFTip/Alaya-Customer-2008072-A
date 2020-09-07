package com.ftipinfosol.alayachits;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

public class HowChitWorks extends AppCompatActivity {
    TextView txtPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_chit_works);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HOW CHIT WORKS");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtPrivacyPolicy = findViewById(R.id.txt_how_chit_works);
        txtPrivacyPolicy.setText(Html.fromHtml(getString(R.string.how_chit_string)));

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
