package com.example.snowsoultrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.snowsoultrips.MainActivity.mAuth;

public class LandingActivity extends AppCompatActivity implements AvatarFragment.OnFragmentCommunicationListener {
    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference userList = db.collection("Users");
    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String IMAGEREF = "imageref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);



        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        final NavController navController = navHost.getNavController();

        final NavInflater navInflater = navController.getNavInflater();
        final NavGraph graph = navInflater.inflate(R.navigation.navigation_graph);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.INVISIBLE);

        final FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userDR = userList.document(user.getUid());
        userDR.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                if (name == null) {
                    final CollectionReference userList = db.collection("Users");
                    DocumentReference userInfo = userList.document(user.getUid());

                    Map<String, Object> note = new HashMap<>();
                    note.put(UID, user.getUid());

                    userInfo.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LandingActivity.this, "User Profile Added!", Toast.LENGTH_SHORT).show();
                            bottomNavigationView.setVisibility(View.INVISIBLE);
                            System.out.println("User Collection is Empty!");
                            graph.setStartDestination(R.id.mainFragment);
                            navController.setGraph(graph);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LandingActivity.this, "Error adding user details!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (name != null) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    graph.setStartDestination(R.id.profileFragment);
                    navController.setGraph(graph);
                    NavigationUI.setupWithNavController(bottomNavigationView, navController);
                }
            }
        });
    }


        @Override
        public void OnImageSelected (String name){
            Map<String, Object> updateImage = new HashMap<>();
            updateImage.put(IMAGEREF, name);
            Task<Void> user = db.collection("Users").document(mAuth.getUid()).update(updateImage);

        }
    }