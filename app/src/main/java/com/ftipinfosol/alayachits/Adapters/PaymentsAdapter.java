package com.ftipinfosol.alayachits.Adapters;

import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftipinfosol.alayachits.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JSONObject> payment;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public PaymentsAdapter(List<JSONObject> payment) { this.payment = payment;  }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date, receipt, amount, total;
        LinearLayout layout;

        MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            receipt = view.findViewById(R.id.receipt);
            amount = view.findViewById(R.id.amount);
            total = view.findViewById(R.id.total);
            layout = view.findViewById(R.id.layout);
        }

        void bind(final JSONObject pay, final int position) {
            SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date_val;
            if ((position % 2) == 0) {
                layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            else{
                layout.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }
            try {
                date_val = format.parse(pay.getString("created_at"));
                date.setText(date_format.format(date_val));
                receipt.setText(pay.getString("ccid"));
                amount.setText(pay.getString("amount"));
                total.setText(pay.getString("total"));
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ledger_card, viewGroup, false);
        return new PaymentsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        PaymentsAdapter.MyViewHolder holder = (PaymentsAdapter.MyViewHolder) viewHolder;
        holder.bind(payment.get(i),i);
    }

    @Override
    public int getItemCount() {
        return payment.size();
    }
}
