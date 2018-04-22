package com.coppellcoders.icycle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyItem extends RecyclerView.Adapter<RecyItem.MyViewHolder> {

    private List<Recycle> users;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView rank, name, point;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.recy_name);

        }
    }


    public RecyItem(List<Recycle> us) {
        this.users = us;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.extra_recy, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(users.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}