package com.fakegps.optimustechproject.fakegps;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by satyam on 5/7/17.
 */

public class adapter_history extends RecyclerView.Adapter<adapter_history.view_holder> {


    History history;
    public adapter_history(History history) {

        this.history=history;

    }


    @Override
    public view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history,parent,false);
        return new view_holder(view);

    }


    @Override
    public void onBindViewHolder(final view_holder holder, int position) {
        holder.setIsRecyclable(false);
        Log.e("fav",String.valueOf(history.getCity()));
        holder.place.setText(history.getCity().get(position)+" ,"+history.getCountry().get(position));
        holder.lati.setText(history.getLatitude().get(position)+" ,"+history.getLongitude().get(position));

    }

    @Override
    public int getItemCount() {
        return history.getLatitude().size();
    }

    public class view_holder extends RecyclerView.ViewHolder {

        TextView place,lati;
        public view_holder(View itemView) {

            super(itemView);
            place=(TextView)itemView.findViewById(R.id.place);
            lati=(TextView)itemView.findViewById(R.id.lati_longi);

        }

    }
}