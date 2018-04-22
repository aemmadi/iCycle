package com.coppellcoders.icycle;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchFragment extends Fragment {

    ArrayList<Recycle> all = new ArrayList<>();
    ArrayList<Recycle> user = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.recyitemss, container, false);
        try {
            DataInputStream textFileStream = new DataInputStream(getActivity().getAssets().open(String.format("terms.txt")));
            DataInputStream textFileStream2 = new DataInputStream(getActivity().getAssets().open(String.format("defs.txt")));
            Scanner scan = new Scanner(textFileStream);
            Scanner sc = new Scanner(textFileStream2);
            while(scan.hasNext()){
                all.add(new Recycle(scan.nextLine(), sc.nextLine()));
            }
            user = new ArrayList<>(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final EditText search = mView.findViewById(R.id.search);



        final RecyclerView recyclerView = mView.findViewById(R.id.recyItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final Dialog myDia = new Dialog(getActivity());


                myDia.setContentView(R.layout.decpopup);
                TextView tv = myDia.findViewById(R.id.def);
                Button close = myDia.findViewById(R.id.closedef);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDia.dismiss();
                    }
                });
                tv.setText(user.get(position).getDecs());
                myDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDia.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        RecyItem adapter = new RecyItem(user);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacingItemDecoration(getActivity(), 2));
        adapter.notifyDataSetChanged();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user.clear();
                for(int a = 0; a < all.size(); a++){
                    if(all.get(a).name.toLowerCase().contains(search.getText().toString().toLowerCase())){
                        user.add(all.get(a));
                    }
                }
                System.out.println(all);
                System.out.println(search.getText().toString());
                RecyItem adapter = new RecyItem(user);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return mView;


    }
}