package com.example.silviabova.mylogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AllChatsActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String CurrentUser = mAuth.getCurrentUser().getUid();
    private DatabaseReference UserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        UserDatabase = FirebaseDatabase.getInstance().getReference("Users/");

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
            protected void populateViewHolder(final UserViewHolder viewHolder, final UserChat userChat, int position) {
                viewHolder.setDisplayName(userChat.getName());
                viewHolder.setUserBio(userChat.getBio());
                viewHolder.setUserImage(userChat.getURLimage(),getApplicationContext());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       final Intent intent = new Intent(AllChatsActivity.this, ChatActivity.class);
                        UserDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    String imagefound = ds.child("urlimage").getValue(String.class);
                                    if(imagefound.compareTo(userChat.getURLimage())==0){
                                        Log.d("ID", ds.getKey());
                                        intent.putExtra("user_id", ds.getKey().toString().trim());
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //intent.putExtra("user_id", viewHolder.getID(userChat.getURLimage()));


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
