package com.example.snowsoultrips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class AvatarFragment extends Fragment{
    ImageView image1, image2, image3, image4, image5, image6;
    private static final String KEY = "key";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_avatar, container, false);
    }

    private OnFragmentCommunicationListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentCommunicationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image1 = view.findViewById(R.id.imageView1);
        image2 = view.findViewById(R.id.imageView2);
        image3 = view.findViewById(R.id.imageView3);
        image4 = view.findViewById(R.id.imageView4);
        image5 = view.findViewById(R.id.imageView5);
        image6 = view.findViewById(R.id.imageView6);

        String urlf1 = "https://firebasestorage.googleapis.com/v0/b/snowsoultrips-f09a0.appspot.com/o/images%2Favatar_f_1.png?alt=media&token=62d28c94-a582-466e-a209-3e993522a564";
        String urlf2 = "https://firebasestorage.googleapis.com/v0/b/snowsoultrips-f09a0.appspot.com/o/images%2Favatar_f_2.png?alt=media&token=922045c3-1f6f-44ed-ac9f-cfc90c2e7e7a";
        String urlf3 = "https://firebasestorage.googleapis.com/v0/b/snowsoultrips-f09a0.appspot.com/o/images%2Favatar_f_3.png?alt=media&token=936525a4-f4b0-4243-8a10-96abbdac0f9d";
        String urlf4 = "https://firebasestorage.googleapis.com/v0/b/snowsoultrips-f09a0.appspot.com/o/images%2Favatar_m_1.png?alt=media&token=9be53150-82fc-413a-8dfa-f21a64f035d6";
        String urlf5 = "https://firebasestorage.googleapis.com/v0/b/snowsoultrips-f09a0.appspot.com/o/images%2Favatar_m_2.png?alt=media&token=3ec87e28-073c-4ef6-be4c-1c17076128ae";
        String urlf6 = "https://firebasestorage.googleapis.com/v0/b/snowsoultrips-f09a0.appspot.com/o/images%2Favatar_m_3.png?alt=media&token=066ce22f-1c5b-4298-bc8b-f901e80737fa";

        Picasso.get().load(urlf1).into(image1);
        image1.setTag(urlf1);
        Picasso.get().load(urlf2).into(image3);
        image3.setTag(urlf2);
        Picasso.get().load(urlf3).into(image5);
        image5.setTag(urlf3);
        Picasso.get().load(urlf4).into(image2);
        image2.setTag(urlf4);
        Picasso.get().load(urlf5).into(image4);
        image4.setTag(urlf5);
        Picasso.get().load(urlf6).into(image6);
        image6.setTag(urlf6);

//        Fragment fragment = new Fragment();
//        Bundle bundle = new Bundle();
//        bundle.putString(KEY, urlf1);
//        fragment.setArguments(bundle);



        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnImageSelected(image1.getTag().toString());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnImageSelected(image3.getTag().toString());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });
        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnImageSelected(image5.getTag().toString());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnImageSelected(image2.getTag().toString());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnImageSelected(image4.getTag().toString());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });
        image6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnImageSelected(image6.getTag().toString());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.mainFragment);
            }
        });



    }


    public interface OnFragmentCommunicationListener {
        void OnImageSelected(String name);
    }
}


