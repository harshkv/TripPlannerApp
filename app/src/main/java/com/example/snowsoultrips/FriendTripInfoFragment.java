package com.example.snowsoultrips;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class FriendTripInfoFragment extends Fragment {
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
    Button chatRoom, joinGroup;
    ImageView locationImage;
    int groupJoinedFlag=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_trip_info, container, false);
        locationHead = (TextView) view.findViewById(R.id.tv_locationHeadF);
        joinGroup = (Button) view.findViewById(R.id.btnJoinGroupF);
        chatRoom = (Button) view.findViewById(R.id.btnChatRoomsF);
        tripmembers = (TextView) view.findViewById(R.id.tv_tripmembersF);
        locationImage = (ImageView) view.findViewById(R.id.locationimageF);
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
                            userList.document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    //allUserNames.add(documentSnapshot.getString(NAME));
                                    hmap.put(document.getId(),documentSnapshot.getString(NAME));
                                }
                            });
                    }
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        UpdateJoinLeaveButton(tripId);

        Handler handler = new Handler();
        final String finalTripId = tripId;
        handler.postDelayed(new Runnable() {
            public void run() {
                UpdateTextView(finalTripId);
                UpdateJoinLeaveButton(finalTripId);
                joinGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(groupJoinedFlag==0){
                            DocumentReference userInfo = tripList.document(finalTripId);
                            Map<String, Object> note = new HashMap<>();
                            note.put(MEMBERID, FieldValue.arrayUnion(mAuth.getUid()));
                            userInfo.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    groupJoinedFlag=1;
                                    UpdateTextView(finalTripId);
                                    UpdateJoinLeaveButton(finalTripId);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Group Joined!!")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Log.d("msg","You Clicked OK");
                                                }
                                            })
                                            .setCancelable(false);
                                    final AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e);
                                    Toast.makeText(getContext(), "Error joining Group!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else if(groupJoinedFlag==1){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Leave the group?!")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DocumentReference userInfo = tripList.document(finalTripId);
                                    Map<String, Object> note = new HashMap<>();
                                    note.put(MEMBERID, FieldValue.arrayRemove(mAuth.getUid()));
                                    userInfo.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            groupJoinedFlag=0;
                                            UpdateTextView(finalTripId);
                                            UpdateJoinLeaveButton(finalTripId);
                                            Toast.makeText(getContext(), "Group Left!", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            System.out.println(e);
                                            Toast.makeText(getContext(), "Error leaving Group!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                                    .setCancelable(false);
                            final AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                });
            }
        }, 1000);   //1 seconds



        chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(groupJoinedFlag==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Please join the group to Chat!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("msg","You Clicked OK");
                                }
                            })
                            .setCancelable(false);
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
                else if(groupJoinedFlag==1){
                    Bundle bundle = new Bundle();
                    bundle.putString(TRIPKEY,finalTripId);
                    ChatFragment chatFragment = new ChatFragment();
                    chatFragment.setArguments(bundle);
                    Navigation.findNavController(view).navigate(R.id.chatFragment, bundle);
                }
            }
        });
    }
    public void UpdateTextView(String finalTripId){
        DocumentReference userInfo = tripList.document(finalTripId);
        System.out.println(finalTripId);
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
                tripmembers.setText("Members: "+allUserNames.toString().replaceAll("[\\[\\](){}]",""));
            }
        });
    }

    public void UpdateJoinLeaveButton(String finalTripId){
        DocumentReference userInfo = tripList.document(finalTripId);
        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> allUserIds = (ArrayList<String>) documentSnapshot.get(MEMBERID);
                for(int k=0;k<allUserIds.size();k++){
                    if(allUserIds.get(k).equals(mAuth.getUid())){
                        groupJoinedFlag=1;
                    }
                }
                if(groupJoinedFlag==0){
                    joinGroup.setText("Join Group");
                    joinGroup.setVisibility(View.VISIBLE);
                    chatRoom.setVisibility(View.VISIBLE);
                }
                else if(groupJoinedFlag==1){
                    joinGroup.setText("Leave Group");
                    joinGroup.setVisibility(View.VISIBLE);
                    chatRoom.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
