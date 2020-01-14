package com.example.snowsoultrips;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userList = db.collection("Users");
    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String IMAGEREF = "imageref";
    final String user = mAuth.getCurrentUser().getUid();
    private RecyclerView mrecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Discover People");
        final ArrayList<Friend> allUsers = new ArrayList<>();
        userList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Friend friend = new Friend();
                        String name = document.getString(NAME);
                        String id = document.getString(UID);
                        String imageDPUrl = document.getString(IMAGEREF);
                        if(!id.equals(user)){
                            friend.uid = id;
                            friend.name = name;
                            friend.imgUrl = imageDPUrl;
                            allUsers.add(friend);
                        }
                        mrecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
                        mrecyclerView.setHasFixedSize(true);

                        mlayoutManager = new LinearLayoutManager(getActivity());
                        mrecyclerView.setLayoutManager(mlayoutManager);

                        mAdapter = new FriendsAdapter(allUsers);
                        mrecyclerView.setAdapter(mAdapter);
                        Log.d("FriendsFragment", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("FriendsFragment", "Error getting documents: ", task.getException());
                }
            }
        });

    }
}
