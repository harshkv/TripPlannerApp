package com.example.snowsoultrips;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TripInfoFragment extends Fragment {
    public static final String TRIPKEY ="tripkey" ;
    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userList = db.collection("Users");
    final CollectionReference tripList = db.collection("Trips");
    private static final String TRIPID = "tripid";
    private static final String NAME = "name";
    private static final String CREATERID = "createrid";
    private static final String MEMBERID = "memberid";
    private static final String IMAGEREF = "imageref";
    private static final String LOCATION = "location";
    private static final String COVERPHOTOID = "coverphotoid";
    HashMap<String,String> hmap = new HashMap<>();
    final String user = mAuth.getCurrentUser().getUid();
    TextView locationHead, tripmembers;
    Button chatRoom, addFriends;
    ImageView locationImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_info, container, false);
        locationHead = (TextView) view.findViewById(R.id.tv_locationHead);
        addFriends = (Button) view.findViewById(R.id.btnAddFriends);
        chatRoom = (Button) view.findViewById(R.id.btnChatRooms);
        tripmembers = (TextView) view.findViewById(R.id.tv_tripmembers);
        locationImage = (ImageView) view.findViewById(R.id.locationimage);
        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        String tripId = null;
        String location = null;
        if (bundle != null) {
            location = bundle.getString(TripsAdapter.KEY);
            tripId = bundle.getString(TripsAdapter.TRIPIDKEY);
            locationHead.setText(location);
//            allUsers = bundle.getStringArrayList(TripsAdapter.MEMBERKEY);
        }

        final NavController navController = Navigation.findNavController(view);
        userList.orderBy(NAME, Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if(!document.getId().equals(user)){
                            //allUsers.add(document.getId());
                            userList.document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    //allUserNames.add(documentSnapshot.getString(NAME));
                                    hmap.put(document.getId(),documentSnapshot.getString(NAME));
                                }
                            });
                        }
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        Handler handler = new Handler();
        final String finalTripId = tripId;
        handler.postDelayed(new Runnable() {
            public void run() {
                final ArrayList<String> allMembers = new ArrayList<>();
                final ArrayList<String> allMemberIds = new ArrayList<>();
                // yourMethod();
                final CharSequence[] items = hmap.values().toArray(new CharSequence[hmap.size()]);
                final CharSequence[] itemids = hmap.keySet().toArray(new CharSequence[hmap.size()]);
                if(getActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select User!")
                            .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if(b==true){
                                        allMembers.add((String) items[i]);
                                        allMemberIds.add((String) itemids[i]);
                                    }
                                    else{
                                        allMembers.remove((String) items[i]);
                                        allMemberIds.remove((String) itemids[i]);
                                    }
                                }
                            })        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DocumentReference userInfo = tripList.document(finalTripId);
                            Map<String, Object> note = new HashMap<>();
                            //allMemberIds.add(user);
                            for(int j=0;j<allMemberIds.size();j++){
                                note.put(MEMBERID, FieldValue.arrayUnion(allMemberIds.get(j)));
                                userInfo.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        UpdateTextView(finalTripId);
                                        Toast.makeText(getContext(), "Member Added", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e);
                                        Toast.makeText(getContext(), "Error adding Trip!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            Log.d("msg","You Clicked OK");
                        }
                    })
                            .setCancelable(false);

                    final AlertDialog alert = builder.create();
                    UpdateTextView(finalTripId);
                    addFriends.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.show();
                        }
                    });
                }

            }
        }, 1000);   //1 seconds



        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(TRIPKEY,finalTripId);
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                Navigation.findNavController(view).navigate(R.id.chatFragment, bundle);
            }
        });
    }
    public void UpdateTextView(String finalTripId){
        DocumentReference userInfo = tripList.document(finalTripId);
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> allUserNames = new ArrayList<>();
                ArrayList<String> allUserIds = (ArrayList<String>) documentSnapshot.get(MEMBERID);
                for(int k=0;k<allUserIds.size();k++){
                    String nameFromId = hmap.get(allUserIds.get(k));
                    allUserNames.add(nameFromId);
                }
                allUserNames.removeAll(Collections.singleton(null));
                if(allUserNames.size()>1) {
                    tripmembers.setText("Members: " + allUserNames.toString().replaceAll("[\\[\\](){}]", ""));
                }
                else{
                    tripmembers.setText("No Members");
                }
            }
        });
    }
}
