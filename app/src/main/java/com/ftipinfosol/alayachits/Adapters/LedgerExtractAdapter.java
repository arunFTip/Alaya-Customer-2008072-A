package com.ftipinfosol.alayachits.Adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftipinfosol.alayachits.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LedgerExtractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JSONObject> payment;

    public LedgerExtractAdapter(List<JSONObject> payment){
        this.payment=payment;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView month, date, amount,  dividend,  total, net_total;
        LinearLayout layout;

        MyViewHolder(View view) {
            super(view);
            month = view.findViewById(R.id.month);
            date = view.findViewById(R.id.date);
            amount = view.findViewById(R.id.amount);
            dividend = view.findViewById(R.id.dividend);
            total = view.findViewById(R.id.total);
            net_total=view.findViewById(R.id.net_total);
            layout = view.findViewById(R.id.layout);
        }

        void bind(final JSONObject pay, final int position) {
            if ((position % 2) == 0) {
                layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            else{
                layout.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }
            try {
                month.setText(pay.getString("month"));
                date.setText(pay.getString("commence"));
                amount.setText(pay.getString("amount"));
                dividend.setText(pay.getString("divident"));
                total.setText(pay.getString("total"));
                net_total.setText(pay.getString("net_total"));
                //total.setText(String.valueOf(pay.getInt("dividend")+pay.getInt("amount")));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ledger_extract_card, viewGroup, false);
        return new LedgerExtractAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        LedgerExtractAdapter.MyViewHolder holder = (LedgerExtractAdapter.MyViewHolder) viewHolder;
        holder.bind(payment.get(i),i);
    }

    @Override
    public int getItemCount() {
        return payment.size();
    }
}
