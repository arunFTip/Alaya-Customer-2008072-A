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

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JSONObject> payment;

    public ReportsAdapter(List<JSONObject> payment) { this.payment = payment;  }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView month, dividend, amount, total;
        LinearLayout layout;

        MyViewHolder(View view) {
            super(view);
            month = view.findViewById(R.id.month);
            dividend = view.findViewById(R.id.dividend);
            amount = view.findViewById(R.id.amount);
            total = view.findViewById(R.id.total);
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
                dividend.setText(pay.getString("dividend"));
                amount.setText(pay.getString("amount"));
                total.setText(String.valueOf(pay.getInt("dividend")+pay.getInt("amount")));
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reports_card, viewGroup, false);
        return new ReportsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ReportsAdapter.MyViewHolder holder = (ReportsAdapter.MyViewHolder) viewHolder;
        holder.bind(payment.get(i),i);
    }

    @Override
    public int getItemCount() {
        return payment.size();
    }
}
