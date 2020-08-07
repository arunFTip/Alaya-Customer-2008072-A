package com.ftipinfosol.alayachits.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ftipinfosol.alayachits.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SchemesAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JSONObject> scheme;
    private final SchemesAdapter.OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(JSONObject tic);
    }

    public SchemesAdapter(List<JSONObject> scheme, SchemesAdapter.OnItemClickListener listener) {
        this.scheme = scheme;
        this.listener = listener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView scheme_name, scheme_value, duration;

        MyViewHolder(View view) {
            super(view);
            scheme_name = view.findViewById(R.id.scheme_name);
            scheme_value = view.findViewById(R.id.scheme_value);
            duration = view.findViewById(R.id.duration);
        }

        void bind(final JSONObject tic) {
            try {
                scheme_name.setText(tic.getString("scheme_name"));
                String duration_value = tic.getString("duration")+((tic.getInt("scheme_type")==1)?" Weeks":" Months");
                duration.setText(duration_value);
                scheme_value.setText(tic.getString("scheme_value"));
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
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schemes_card, null);
        return new SchemesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SchemesAdapter.MyViewHolder holder = (SchemesAdapter.MyViewHolder) viewHolder;
        holder.bind(scheme.get(i));
    }

    @Override
    public int getItemCount() {
        return scheme.size();
    }
}
