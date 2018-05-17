package com.example.silviabova.mylogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AllChatsActivity extends AppCompatActivity {

    private RecyclerView mUserList;

    private DatabaseReference UserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        UserDatabase = FirebaseDatabase.getInstance().getReference();

        mUserList = (RecyclerView) findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter <UserChat,UserViewHolder>firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserChat, UserViewHolder>(
                UserChat.class,
                R.layout.single_chat,
                UserViewHolder.class,
                UserDatabase
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, UserChat userChat, int position) {
                viewHolder.setDisplayName(userChat.getName());
                viewHolder.setUserBio(userChat.getBio());
                viewHolder.setUserImage(userChat.getURLimage(),getApplicationContext());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(AllChatsActivity.this, ChatActivity.class));
                    }
                });
            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public UserViewHolder(View itemView){
            super(itemView);

            mView = itemView;
        }

        public void setDisplayName(String name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.username);
            userNameView.setText(name);
        }
        public void setUserBio(String bio) {
            TextView userBioView = (TextView) mView.findViewById(R.id.user_name);
            userBioView.setText(bio);
        }
        public void setUserImage(String sURL, Context ctx) {
            ImageView userImageView = (ImageView) mView.findViewById(R.id.userimage);
            Picasso.with(ctx).load(sURL).placeholder(R.drawable.user1).transform((Transformation) new PicassoCircleTransformation()).into(userImageView);
        }
    }
}
