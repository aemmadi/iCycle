package com.coppellcoders.icycle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {

    private List<Place> placesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, distance;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            distance = view.findViewById(R.id.distance);
        }
    }


    public PlaceAdapter(List<Place> moviesList) {
        this.placesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Place place = placesList.get(position);
        holder.name.setText(place.name);
        holder.distance.setText(place.dist+" miles");
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }
}