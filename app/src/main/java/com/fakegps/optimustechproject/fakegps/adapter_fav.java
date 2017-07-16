package com.fakegps.optimustechproject.fakegps;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by satyam on 5/7/17.
 */

public class adapter_fav extends RecyclerView.Adapter<adapter_fav.view_holder> {


    History fav,fav2;
    Context context;
    Gson gson=new Gson();

    public adapter_fav(Context context, History fav) {

        this.fav=fav;
        this.context=context;
    }

    @Override
    public view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_fav,parent,false);
        return new view_holder(view);
    }

    @Override
    public void onBindViewHolder(final view_holder holder, int position) {
        holder.setIsRecyclable(false);

        holder.place.setText(fav.getCity().get(position)+" , "+fav.getCountry().get(position));
        holder.lati.setText(fav.getLatitude().get(position)+" , "+fav.getLongitude().get(position));

    }

    @Override
    public int getItemCount() {
        return fav.getLatitude().size();
    }

    public class view_holder extends RecyclerView.ViewHolder {

        TextView place,lati;
        Button locate;
        ImageView delete;
        public view_holder(View itemView) {

            super(itemView);
            place=(TextView)itemView.findViewById(R.id.place);
            lati=(TextView)itemView.findViewById(R.id.lati_longi);
            locate=(Button)itemView.findViewById(R.id.locate);
            delete=(ImageView)itemView.findViewById(R.id.delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fav.getLongitude().remove(fav.getLongitude().get(getAdapterPosition()));
                    fav.getLatitude().remove(fav.getLatitude().get(getAdapterPosition()));
                    fav.getCity().remove(fav.getCity().get(getAdapterPosition()));
                    fav.getCountry().remove(fav.getCountry().get(getAdapterPosition()));

                    fav2=new History(fav.getLatitude(),fav.getLongitude(),fav.getCity(),fav.getCountry());
                    DbHandler.putString(context,"favourites",gson.toJson(fav2));

                    Intent intent=new Intent(context,NavigationActivity.class);
                    Toast.makeText(context,context.getResources().getString(R.string.toast_item_removed_from_fav),Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);

//                    notifyItemRemoved(getAdapterPosition());
//                    notifyItemRangeChanged(getAdapterPosition(),fav.getLatitude().size());
                }
            });

            locate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(context,NavigationActivity.class);
                    if(DbHandler.contains(context,"go_to_specific_search")){
                        DbHandler.remove(context,"go_to_specific_search");
                    }
                    DbHandler.putString(context,"go_to_specific",String.valueOf(String.valueOf(fav.getLatitude().get(getAdapterPosition()))+"%"+String.valueOf(fav.getLongitude().get(getAdapterPosition()))));
                    context.startActivity(intent);


                }
            });

        }

    }
}