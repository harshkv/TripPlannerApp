package com.example.snowsoultrips;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;
import static com.example.snowsoultrips.MainActivity.mAuth;


public class MainFragment extends Fragment{
    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String GENDER = "gender";
    private static final String IMAGEREF = "imageref";
    EditText etName;
    Button btnNext;
    RadioGroup rgGender;
    ImageView addavatar;

    Boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Update Profile");
        final FirebaseUser user = mAuth.getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final NavController navController = Navigation.findNavController(view);
        etName = (EditText) view.findViewById(R.id.et_name);
        btnNext = (Button) view.findViewById(R.id.btn_Next);
        rgGender = (RadioGroup) view.findViewById(R.id.rgGender);
        addavatar = (ImageView) view.findViewById(R.id.img_avatar);


        Drawable myDrawable = getResources().getDrawable(R.drawable.addavatar);
        addavatar.setImageDrawable(myDrawable);

        final CollectionReference userList = db.collection("Users");
        DocumentReference userInfo = userList.document(user.getUid());

        userInfo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String gender = documentSnapshot.getString(GENDER);
                if(gender != null) {
                    if (gender.equals("Male")) {
                        rgGender.check(R.id.rb_male);
                    } else if (gender.equals("Female")) {
                        rgGender.check(R.id.rb_female);
                    }
                }
                etName.setText(documentSnapshot.getString(NAME));
                String imageurl = documentSnapshot.getString(IMAGEREF);
                if(imageurl == null){
                    Drawable myDrawable = getResources().getDrawable(R.drawable.addavatar);
                    addavatar.setImageDrawable(myDrawable);
                }
                else{
                    flag=true;
                    Picasso.get().load(imageurl).resize(300, 300).centerCrop().into(addavatar);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==false){
                    Toast.makeText(getContext(), "Please select Image Avatar!", Toast.LENGTH_SHORT).show();
                }
                else if(etName.getText().toString().matches("")){
                    Toast.makeText(getContext(), "Please enter Name!", Toast.LENGTH_SHORT).show();
                }
                else if(rgGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getContext(), "Please select Gender!", Toast.LENGTH_SHORT).show();
                }
                else {
                    String selection = null;
                    if (rgGender.getCheckedRadioButtonId() != -1) {
                        int id = rgGender.getCheckedRadioButtonId();
                        View radioButton = rgGender.findViewById(id);
                        int radioId = rgGender.indexOfChild(radioButton);
                        RadioButton btn = (RadioButton) rgGender.getChildAt(radioId);
                        selection = (String) btn.getText();
                    }
                    final CollectionReference userList = db.collection("Users");
                    DocumentReference userInfo = userList.document(user.getUid());

                    Map<String, Object> note = new HashMap<>();
                    note.put(UID, user.getUid());
                    note.put(NAME, etName.getText().toString());
                    note.put(GENDER, selection);

                    userInfo.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                            Toast.makeText(getContext(), "User Profile Updated!", Toast.LENGTH_SHORT).show();
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            navController.navigate(R.id.profileFragment);
                            NavigationUI.setupWithNavController(bottomNavigationView, navController);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error saving user details!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        addavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.avatarFragment);
            }
        });

    }

}

