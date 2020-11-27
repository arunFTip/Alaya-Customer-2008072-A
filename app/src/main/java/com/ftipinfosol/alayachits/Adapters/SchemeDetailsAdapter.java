package com.ftipinfosol.alayachits.Adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ftipinfosol.alayachits.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SchemeDetailsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JSONObject> scheme;

    public SchemeDetailsAdapter(List<JSONObject> scheme) {
        this.scheme = scheme;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView month, amount, dividend, subscription;
        LinearLayout layout;

        MyViewHolder(View view) {
            super(view);
            month = view.findViewById(R.id.month);
            amount = view.findViewById(R.id.amount);
            dividend = view.findViewById(R.id.dividend);
            subscription = view.findViewById(R.id.subscription);
            layout = view.findViewById(R.id.layout);
        }

        void bind(final JSONObject tic, final int position) {
            Log.d("AAAAA",""+tic);
            if ((position % 2) == 0) {
                layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            else{
                layout.setBackgroundColor(Color.parseColor("#f2f2f2"));
            }
            try {
                month.setText(tic.getString("month"));
                dividend.setText(tic.getString("divident"));
                amount.setText(tic.getString("bid_amount"));
                subscription.setText(tic.getString("subscription"));
            }catch (JSONException e) {
                e.printStackTrace();
            }
            if(((position+1)==scheme.size())||(position==0))
            {
                month.setTypeface(null, Typeface.BOLD);
                dividend.setTypeface(null, Typeface.BOLD);
                amount.setTypeface(null, Typeface.BOLD);
                subscription.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scheme_details_card, viewGroup, false);
        return new SchemeDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SchemeDetailsAdapter.MyViewHolder holder = (SchemeDetailsAdapter.MyViewHolder) viewHolder;
        holder.bind(scheme.get(i),i);
    }

    @Override
    public int getItemCount() {
        return scheme.size();
    }
}
