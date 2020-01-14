package com.example.snowsoultrips;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>{
        ArrayList<Friend> mData= new ArrayList<>();

public FriendsAdapter(ArrayList<Friend> mData) {
        this.mData = mData;
        }

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
        }

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend person = mData.get(position);
        holder.personName.setText(person.name);
        Picasso.get().load(person.imgUrl).resize(300, 300).centerCrop().into(holder.personDP);
        }

@Override
public int getItemCount() {
        return mData.size();
        }

public class ViewHolder extends RecyclerView.ViewHolder{
    TextView personName;
    ImageView personDP;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        personName = (TextView) itemView.findViewById(R.id.personName);
        personDP = (ImageView) itemView.findViewById(R.id.personDP);
    }
}
}