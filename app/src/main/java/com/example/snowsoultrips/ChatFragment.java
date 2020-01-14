package com.example.snowsoultrips;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;


public class ChatFragment extends Fragment {
    final static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference tripList = db.collection("Trips");
    final CollectionReference userList = db.collection("Users");
    private static final String UID = "uid";
    private static final String NAME = "name";
    private static final String TIME = "time";
    private static final String IMAGEREF = "imageref";
    private static final String MESSAGEID = "messageid";
    private static final String MESSAGE = "message";
    private static final String MESSAGETYPE = "messagetype";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    HashMap<String, String> hmap = new HashMap<>();
    final String user = mAuth.getCurrentUser().getUid();
    static RecyclerView mrecyclerView;
    static RecyclerView.Adapter mAdapter;
    static RecyclerView.LayoutManager mlayoutManager;
    static ArrayList<Chat> allmessages;
    static String tripId = null;
    Button btnSend;
    EditText etChatBox;
    ImageView addImageBtn, imageToSend;
    Bitmap bitmapUpload = null;
    int msgTypeFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        btnSend = (Button) view.findViewById(R.id.button_chatbox_send);
        etChatBox = (EditText) view.findViewById(R.id.edittext_chatbox);
        addImageBtn = (ImageView) view.findViewById(R.id.addImageBtn);
        imageToSend = (ImageView) view.findViewById(R.id.iv_imageToSend);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Chat Room");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tripId = bundle.getString(TripInfoFragment.TRIPKEY);
        }

        GetMessages(tripId);

        final String finalTripId = tripId;
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(msgTypeFlag==0) {
                    String message = etChatBox.getText().toString();
                    UUID uuid = UUID.randomUUID();
                    String msgId = uuid.toString();
                    DateFormat df = new SimpleDateFormat("d-MMM-yy HH:mm:ss");
                    String time = df.format(Calendar.getInstance().getTime());
                    HashMap<String, String> msg = new HashMap<>();
                    msg.put(UID, mAuth.getUid());
                    msg.put(MESSAGE, message);
                    msg.put(TIME, time);
                    msg.put(MESSAGEID, msgId);
                    msg.put(MESSAGETYPE, "text");
                    etChatBox.setText("");
                    hideKeyboard();
                    tripList.document(finalTripId).collection("Messages").document(msgId).set(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            GetMessages(finalTripId);
                            Log.d("TAG", "Message Saved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Error getting documents: " + e.toString());
                        }
                    });
                }
                else if(msgTypeFlag==1){
                    uploadImage(bitmapUpload);
                }
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public void GetMessages(String tripId){
        allmessages = new ArrayList<>();
        final Query query = tripList.document(tripId).collection("Messages").orderBy(TIME, Query.Direction.ASCENDING);
        query.get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final Chat chat = new Chat();
                        chat.uid = document.getString(UID);
                        final String idCheck = document.getString(UID);
                        chat.messsage = document.getString(MESSAGE);
                        chat.time = document.getString(TIME);
                        chat.messageId = document.getString(MESSAGEID);
                        chat.msgType = document.getString(MESSAGETYPE);
                        userList.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (final QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().equals(idCheck)) {
                                            //allUsers.add(document.getId());
                                            userList.document(document.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    //allUserNames.add(documentSnapshot.getString(NAME));
                                                    chat.name = (String) documentSnapshot.get(NAME);
                                                    chat.imageurl = (String) documentSnapshot.get(IMAGEREF);
                                                    allmessages.add(chat);

                                                    mrecyclerView = (RecyclerView) getView().findViewById(R.id.reyclerview_message_list);
                                                    mrecyclerView.setHasFixedSize(true);

                                                    mlayoutManager = new LinearLayoutManager(getActivity());
                                                    mrecyclerView.setLayoutManager(mlayoutManager);

                                                    mAdapter = new ChatAdapter(allmessages);
                                                    mrecyclerView.setAdapter(mAdapter);
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                            }
                        });

                    }
                } else {
                    Log.d("ChatFragment", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.back_button, menu);  // Use filter.xml from step 1
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.backButton){
            //Do whatever you want to do
            Navigation.findNavController(getView()).navigate(R.id.tripFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //    TAKE PHOTO USING CAMERA...
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Camera Callback........
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageToSend.setImageBitmap(imageBitmap);
            etChatBox.setVisibility(View.INVISIBLE);
            msgTypeFlag = 1;
            bitmapUpload = imageBitmap;
        }
    }

    //    Upload Camera Photo to Cloud Storage....
    private void uploadImage(Bitmap photoBitmap){
        UUID uuid = UUID.randomUUID();
        final String imageId = uuid.toString();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference imageRepo = storageReference.child("chatImages/"+imageId+".png");

//        Converting the Bitmap into a bytearrayOutputstream....
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRepo.putBytes(data);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                return null;
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return imageRepo.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Log.d("ChatFragment", "Image Download URL"+ task.getResult());
                    String imageURL = task.getResult().toString();
                    UUID uuid = UUID.randomUUID();
                    String msgId = uuid.toString();
                    DateFormat df = new SimpleDateFormat("d-MMM-yy hh:mm:ss a");
                    String time = df.format(Calendar.getInstance().getTime());
                    HashMap<String, String> msg = new HashMap<>();
                    msg.put(UID, mAuth.getUid());
                    msg.put(MESSAGE, imageURL);
                    msg.put(TIME, time);
                    msg.put(MESSAGEID, msgId);
                    msg.put(MESSAGETYPE, "image");
                    etChatBox.setText("");
                    hideKeyboard();
                    tripList.document(tripId).collection("Messages").document(msgId).set(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            GetMessages(tripId);
                            imageToSend.setImageDrawable(null);
                            etChatBox.setVisibility(View.VISIBLE);
                            msgTypeFlag=0;
                            Log.d("TAG", "Message Saved");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Error getting documents: " + e.toString());
                        }
                    });
                }
            }
        });
    }

}
