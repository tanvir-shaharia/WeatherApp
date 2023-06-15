package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Weather_Adapter extends RecyclerView.Adapter<Weather_Adapter.Weather_Viewholder>{

    Context context;
    ArrayList<WeatherModel> weatherModelArrayList;

    public Weather_Adapter(Context context, ArrayList<WeatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @NonNull
    @Override
    public Weather_Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Weather_Viewholder(LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Weather_Viewholder holder, int position) {

        WeatherModel model=weatherModelArrayList.get(position);
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.itemicon);
        holder.itemtemp.setText(model.getTempa()+"°C");
        holder.itemwinspeed.setText(model.getWinapeed()+"km/h");

        SimpleDateFormat input=new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm aa");
        try {
            Date t=input.parse(model.getTime());
            holder.itemtime.setText(output.format(t));

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    public class Weather_Viewholder extends RecyclerView.ViewHolder {

        public TextView itemtime,itemtemp,itemwinspeed;
        public ImageView itemicon;

        public Weather_Viewholder(@NonNull View itemView) {
            super(itemView);
            itemtime=itemView.findViewById(R.id.item_time);
            itemtemp=itemView.findViewById(R.id.item_temp);
            itemwinspeed=itemView.findViewById(R.id.item_winspeed);
            itemicon=itemView.findViewById(R.id.item_icon);
        }
    }
}
