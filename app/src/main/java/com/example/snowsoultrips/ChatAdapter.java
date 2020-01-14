package com.example.snowsoultrips;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.opencensus.internal.Utils;

import static com.example.snowsoultrips.MainActivity.mAuth;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<Chat> mData = new ArrayList<>();
    public static final String KEY = "key";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatAdapter(ArrayList<Chat> mData) {
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        Chat message = (Chat) mData.get(position);

        if (message.uid.equals(mAuth.getCurrentUser().getUid())) {
            System.out.println(message.uid + "" + mAuth.getUid());
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            System.out.println(message.uid + "" + mAuth.getUid());
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_sent_item, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_receive_item, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat message = (Chat) mData.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }



    @Override
    public int getItemCount() {
        return mData.size();
    }

private class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText;
    ImageView deleteBtn, sentImage;

    SentMessageHolder(View itemView) {
        super(itemView);
        sentImage = (ImageView) itemView.findViewById(R.id.sentImage);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        deleteBtn = (ImageView) itemView.findViewById(R.id.iv_deleteBtn);

    }

    void bind(final Chat message) {
        if(message.msgType.equals("text")) {
            messageText.setText(message.messsage);
        }
        else if(message.msgType.equals("image")){
            Picasso.get().load(message.messsage).resize(300, 300).centerCrop().into(sentImage);
            messageText.setVisibility(View.INVISIBLE);
        }
        timeText.setText(message.time);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatFragment.db.collection("Trips").document(ChatFragment.tripId).collection("Messages").document(message.messageId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Message Deleted");
                        ChatFragment.allmessages.remove(message);
                        ChatFragment.mrecyclerView.setAdapter(ChatFragment.mAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "Message Not Deleted");
                    }
                });
            }
        });
        // Format the stored timestamp into a readable String using method.
        //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
    }
}

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage, receivedImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            receivedImage = (ImageView) itemView.findViewById(R.id.receivedImage);
        }

        void bind(Chat message) {
            if(message.msgType.equals("text")) {
                messageText.setText(message.messsage);
            }
            else if(message.msgType.equals("image")){
                Picasso.get().load(message.messsage).resize(300, 300).centerCrop().into(receivedImage);
                messageText.setVisibility(View.INVISIBLE);
            }
            // Format the stored timestamp into a readable String using method.
            //timeText.setText(Utils.formatDateTime(message.getCreatedAt()));
            timeText.setText(message.time);
            nameText.setText(message.name);

            // Insert the profile image from the URL into the ImageView.
            Picasso.get().load(message.imageurl).resize(300, 300).centerCrop().into(profileImage);
        }
    }
}