package com.example.snowsoultrips;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.sql.Time;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.example.snowsoultrips.MainActivity.mAuth;


public class AddTripFragment extends Fragment {
TextView placeLat, placeLong, placeName;
ImageView placeCover;
String location,latitude,longitude,coverFromPhotoID;
Button createTrip;
    private static final String TRIPID = "tripid";
    private static final String CREATERID = "createrid";
    private static final String MEMBERID = "memberid";
    private static final String COVERPHOTOID = "coverphotoid";
    private static final String LOCATION = "location";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        placeName = view.findViewById(R.id.tv_addplaceName);
        placeLat = (TextView) view.findViewById(R.id.tv_addplaceLat);
        placeLong = (TextView) view.findViewById(R.id.tv_addplaceLong);
        placeCover = (ImageView) view.findViewById(R.id.addPlaceCover);
        createTrip = (Button) view.findViewById(R.id.btnCreateTrip);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String apiKey = getResources().getString(R.string.api_key);

        // Initialize Places.
        Places.initialize(getContext(), apiKey);
        // Create a new Places client instance.
        final PlacesClient placesClient = Places.createClient(getContext());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.PHOTO_METADATAS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: Get info about the selected place.
//              LatLng queriedLocation = place.getLatLng();
                placeName.setText("Place: "+place.getName());
                location = place.getName();
                placeLat.setText("Latitude: "+place.getLatLng().latitude);
                latitude = String.valueOf(place.getLatLng().latitude);
                placeLong.setText("Longitude: "+place.getLatLng().longitude);
                longitude = String.valueOf(place.getLatLng().longitude);

                //--------------------------------------------------------------------------------------------------------------------------------------------

                // Define a Place ID.
                final String placeId = place.getId();
                coverFromPhotoID = placeId;

                // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
                List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

                // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
                FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

                placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        //Place place = response.getPlace();

                        // Get the photo metadata.
                        final PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                        // Get the attribution text.
                        String attributions = photoMetadata.getAttributions();


                        // Create a FetchPhotoRequest.
                        final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                .setMaxWidth(1000) // Optional.
                                .setMaxHeight(300) // Optional.
                                .build();
                        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                            @Override
                            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                placeCover.setImageBitmap(bitmap);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Place not found: ");
                                    }
                                });
                    }
                });


                //--------------------------------------------------------------------------------------------------------------------------------------------
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }

        });


    createTrip.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final FirebaseUser user = mAuth.getCurrentUser();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference userList = db.collection("Trips");
            Date currentTime = Calendar.getInstance().getTime();
            UUID uuid = UUID.randomUUID();
            String newTripId = uuid.toString();
            DocumentReference userInfo = userList.document(newTripId);
            Map<String, Object> note = new HashMap<>();
            note.put(CREATERID, user.getUid());
            note.put(LOCATION, location);
            note.put(TRIPID, newTripId);
            note.put(COVERPHOTOID, coverFromPhotoID);
            note.put(MEMBERID, Arrays.asList(user.getUid()));

            userInfo.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                    Toast.makeText(getContext(), "Trip Added", Toast.LENGTH_SHORT).show();
                    final NavController navController = Navigation.findNavController(getView());
                    navController.navigate(R.id.tripFragment);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error adding Trip!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    });

    }
}
