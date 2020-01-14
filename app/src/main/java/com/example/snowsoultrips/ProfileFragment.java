package com.example.snowsoultrips;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import static com.example.snowsoultrips.MainActivity.mAuth;


public class ProfileFragment extends Fragment {
    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String IMAGEREF = "imageref";
    TextView mpName,mpGender;
    Button mpEdit;
    ImageView profileImage;
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button signOut = (Button) view.findViewById(R.id.btn_logout);
        getActivity().setTitle("My Profile");
        mpName = (TextView) view.findViewById(R.id.mpName);
        mpGender = (TextView) view.findViewById(R.id.mpGender);
        mpEdit = (Button) view.findViewById(R.id.btnEdit);
        profileImage = (ImageView) view.findViewById(R.id.imgMyProfile);


        final FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference userList = db.collection("Users");
        DocumentReference userInfo = userList.document(user.getUid());

        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = "Name: "+documentSnapshot.getString(NAME);
                String gender = "Gender: "+documentSnapshot.getString(GENDER);
                String imageUrl = documentSnapshot.getString(IMAGEREF);

                mpName .setText(name);
                mpGender.setText(gender);
                Picasso.get().load(imageUrl).resize(300, 300).centerCrop().into(profileImage);
            }
        });

        mpEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
                System.out.println("success");;
            }
        });


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });



    }
}
