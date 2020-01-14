package com.example.snowsoultrips;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TripFragment extends Fragment {
    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userList = db.collection("Trips");
    private static final String TRIPID = "tripid";
    private static final String NAME = "name";
    private static final String CREATERID = "createrid";
    private static final String MEMBERID = "memberid";
    private static final String IMAGEREF = "imageref";
    private static final String LOCATION = "location";
    final String user = mAuth.getCurrentUser().getUid();
    static RecyclerView mrecyclerView;
    static RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;
    static ArrayList<Trip> allTrips;
    static ArrayList<MyTrip> myTrips;
    ImageView addTrip;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.trips_navigation);
        tabLayout.getTabAt(0).select();
        addTrip = (ImageView) view.findViewById(R.id.btnAddTrip);
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.addTripFragment);
            }
        });
        getActivity().setTitle("All Trips");
        AllTrips();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition()==0 || tabLayout.getTabAt(0).isSelected()){
                    AllTrips();
                }
                else {
                    myTrips = new ArrayList<>();
                    userList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    final MyTrip mytripObj = new MyTrip();
                                    int flag = 0;
                                    ArrayList<String> allUsers = new ArrayList<>();
                                    mytripObj.tripid = document.getString(TRIPID);
                                    mytripObj.createrid = document.getString(CREATERID);
                                    mytripObj.location = document.getString(LOCATION);
                                    mytripObj.allUsers = (ArrayList<String>) document.get(MEMBERID);
                                    allUsers = (ArrayList<String>) document.get(MEMBERID);
                                    for (int i = 0; i < allUsers.size(); i++) {
                                        if (allUsers.get(i).equals(mAuth.getUid())) {
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    if(flag==1){
                                        myTrips.add(mytripObj);
                                    }
                                }
                                mrecyclerView = (RecyclerView) getView().findViewById(R.id.tripRecycler);
                                mrecyclerView.setHasFixedSize(true);

                                mlayoutManager = new LinearLayoutManager(getActivity());
                                mrecyclerView.setLayoutManager(mlayoutManager);

                                mAdapter = new MyTripsAdapter(myTrips);
                                mrecyclerView.setAdapter(mAdapter);
                            } else {
                                Log.d("TripsFragment", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void AllTrips(){
        allTrips = new ArrayList<>();
        userList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Trip trip = new Trip();
                        trip.tripid = document.getString(TRIPID);
                        trip.createrid = document.getString(CREATERID);
                        trip.location = document.getString(LOCATION);
                        trip.allUsers = (ArrayList<String>) document.get(MEMBERID);
                        allTrips.add(trip);

                        mrecyclerView = (RecyclerView) getView().findViewById(R.id.tripRecycler);
                        mrecyclerView.setHasFixedSize(true);

                        mlayoutManager = new LinearLayoutManager(getActivity());
                        mrecyclerView.setLayoutManager(mlayoutManager);

                        mAdapter = new TripsAdapter(allTrips);
                        mrecyclerView.setAdapter(mAdapter);

                    }
                } else {
                    Log.d("TripsFragment", "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
