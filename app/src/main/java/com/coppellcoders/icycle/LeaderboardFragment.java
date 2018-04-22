package com.coppellcoders.icycle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class LeaderboardFragment extends Fragment {
    int count =0;
    ArrayList<User> user = new ArrayList<>();
    String username;
    TextView currentRank;
    Button button;


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DatabaseReference ref = database.getReference("Users");
//mAuth.getCurrentUser().getUid()
        if(mAuth.getCurrentUser()!=null) {
            ref.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot
                            username = (String) dataSnapshot.child("Name").getValue();
                            System.out.println(" " + username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });


            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of users in datasnapshot

                            collectWinner((Map<String, Object>) dataSnapshot.getValue());


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }

        // user.add(new User ("eeee", 69));



        return inflater.inflate(R.layout.activity_leaderboard, container, false);
    }


    private void collectWinner(Map<String,Object> users) {




        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();

            long holder = (long)(singleUser.get("Points"));
            int points = (int) holder;
            String name = (String) singleUser.get("Name");
            user.add(new User(name,points));
            // System.out.println(user);
            Collections.sort(user);
            System.out.println(user);
            RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.leader_recy);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            LeaderData adapter = new LeaderData(user);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new SpacingItemDecoration(getActivity(), 1));
            adapter.notifyDataSetChanged();
            currentRank  = getActivity().findViewById(R.id.currentRank);
            for(int x =0; x<user.size();x++){
if(user.get(x).getName().equals(username)){

    currentRank.setText("Your Rank: " + (x+1));


}



            }
        }

        //  System.out.println(points.toString());
        //  System.out.println(name.toString());
    }

}

