package com.example.snowsoultrips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> {
    ArrayList<Trip> mData = new ArrayList<>();
    public static final String KEY = "key";
    public static final String TRIPIDKEY = "tripidkey";

    public TripsAdapter(ArrayList<Trip> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public TripsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        TripsAdapter.ViewHolder viewHolder = new TripsAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TripsAdapter.ViewHolder holder, int position) {
        final Trip trips = mData.get(position);
        holder.tripName.setText(trips.location);
        if (trips.createrid.equals(TripFragment.mAuth.getUid())) {
            holder.createdBy.setText("Created by You");
            holder.tripDeleteBtn.setVisibility(View.VISIBLE);
            holder.tripDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TripFragment.db.collection("Trips").document(trips.tripid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            TripFragment.allTrips.remove(trips);
                            TripFragment.mrecyclerView.setAdapter(TripFragment.mAdapter);
//                            TripFragment.mAdapter.notifyDataSetChanged();
                            Log.d("TAG", "Trip Deleted");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Trip Not Deleted");
                        }
                    });
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(TRIPIDKEY, trips.tripid);
                bundle.putString(KEY, trips.location);
                if (trips.createrid.equals(TripFragment.mAuth.getUid())) {
                    TripInfoFragment infoFragment = new TripInfoFragment();
                    infoFragment.setArguments(bundle);
                    Navigation.findNavController(view).navigate(R.id.tripInfoFragment, bundle);
                } else if (!trips.createrid.equals(TripFragment.mAuth.getUid())) {
                    FriendTripInfoFragment friendTripInfoFragment = new FriendTripInfoFragment();
                    friendTripInfoFragment.setArguments(bundle);
                    Navigation.findNavController(view).navigate(R.id.friendTripInfoFragment, bundle);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tripName, createdBy;
        ImageView tripDeleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripName = (TextView) itemView.findViewById(R.id.tripName);
            createdBy = (TextView) itemView.findViewById(R.id.tv_createdBy);
            tripDeleteBtn = (ImageView) itemView.findViewById(R.id.iv_tripDeleteBtn);
        }
    }
}