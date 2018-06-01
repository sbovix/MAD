package com.example.silviabova.mylogin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by silvia bova on 27/05/2018.
 */

public class ViewHolder extends RecyclerView.Adapter<ViewHolder.UserViewHolder> {

    private Context context;
    private List<UserChat> user;

    public ViewHolder(Context context, List<UserChat> user) {
        this.context = context;
        this.user = user;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.single_chat,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.name.setText(user.get(position).getName());
        String image = user.get(position).getURLimage();
        final String userID = user.get(position).getUserid();
        holder.bio.setText(user.get(position).getBio());
        Picasso.with(context).load(image).placeholder(R.drawable.user1).transform((Transformation) new PicassoCircleTransformation()).into(holder.profile);
        holder.rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_id", userID.toString().trim());
                FirebaseDatabase.getInstance().getReference().child("Users/"+userID+"/Chats/book").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String isbn = dataSnapshot.child("isbn").getValue(String.class);
                        intent.putExtra("book",isbn);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return user.size();
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        TextView name, bio;
        RelativeLayout rv;

        public UserViewHolder(View itemView){
            super(itemView);

            profile = (ImageView) itemView.findViewById(R.id.userimage);
            name = (TextView) itemView.findViewById(R.id.username);
            bio = (TextView) itemView.findViewById(R.id.user_name);
            rv = (RelativeLayout)itemView.findViewById(R.id.rv);

        }

    }
}
