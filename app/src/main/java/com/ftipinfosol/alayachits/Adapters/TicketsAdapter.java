package com.ftipinfosol.alayachits.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ftipinfosol.alayachits.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JSONObject> ticket;
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(JSONObject tic);
    }

    public TicketsAdapter(List<JSONObject> ticket, OnItemClickListener listener) {
        this.ticket = ticket;
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView customer_name, scheme_value, ticket_code, close_status;

        MyViewHolder(View view) {
            super(view);
            customer_name = view.findViewById(R.id.customer_name);
            scheme_value = view.findViewById(R.id.scheme_value);
            ticket_code = view.findViewById(R.id.ticket_code);
            close_status = view.findViewById(R.id.close_status);
        }

        void bind(final JSONObject tic) {
            try {
                Log.e("printticket", tic.toString());
                String customer_detail = "%1$s (%2$s)";
                customer_name.setText(String.format(customer_detail, tic.getString("customer_name"),tic.getString("cust_code")));
                ticket_code.setText(tic.getString("ticket_code").length()>0?tic.getString("ticket_code"):tic.getString("temp_id"));
                scheme_value.setText(tic.getString("scheme_value"));

                if(tic.getString("closed_status").equals("0")){
                    close_status.setText("  Open  ");
                    close_status.setBackgroundResource(R.color.colorGreen);

                }else {
                    close_status.setText("  Closed  ");
                    close_status.setBackgroundResource(R.color.colorRed);
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(tic);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tickets_card, null);
        return new TicketsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        TicketsAdapter.MyViewHolder holder = (TicketsAdapter.MyViewHolder) viewHolder;
        holder.bind(ticket.get(i));
    }

    @Override
    public int getItemCount() {
        return ticket.size();
    }
}
