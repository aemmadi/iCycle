package com.coppellcoders.icycle;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coppellcoders.icycle.User;

import java.util.List;

public class LeaderData extends RecyclerView.Adapter<LeaderData.MyViewHolder> {

    private List<User> users;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rank, name, point;

        public MyViewHolder(View view) {
            super(view);
            rank = (TextView) view.findViewById(R.id.rankk);
            name = (TextView) view.findViewById(R.id.namee);
            point = (TextView) view.findViewById(R.id.pointss);
        }
    }


    public LeaderData(List<User> us) {
        this.users = us;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leader_recy, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User us = users.get(position);
        holder.rank.setText((position+1)+ "");
        holder.name.setText(us.getName());
        holder.point.setText(us.getPoints()+"");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}