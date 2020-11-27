package com.ftipinfosol.alayachits;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Objects;

public class TermsConditionsActivity extends AppCompatActivity {
    TextView txtTermsConditons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TERMS AND CONDITIONS");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtTermsConditons = findViewById(R.id.txt_terms_conditions);
        txtTermsConditons.setText(Html.fromHtml(getString(R.string.terms_conditions_large)));

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
