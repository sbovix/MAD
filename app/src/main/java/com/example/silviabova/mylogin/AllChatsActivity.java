package com.example.silviabova.mylogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    private DatabaseReference UserDatabase, DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        UserDatabase = FirebaseDatabase.getInstance().getReference("Users/").child(CurrentUser).child("Chats");
        DB = FirebaseDatabase.getInstance().getReference("Users/");

        mUserList = (RecyclerView) findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chats");

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
    }

    //back button in the navigation bar
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);

        return true;

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

                UserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            String ChatUser = ds.getKey().toString().trim();
                            //Log.d("USERID", ChatUser);
                            DB.child(ChatUser).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataS) {
                                    viewHolder.setDisplayName(dataS.child("name").getValue(String.class));
                                    viewHolder.setUserBio(dataS.child("bio").getValue(String.class));
                                    viewHolder.setUserImage(dataS.child("urlimage").getValue(String.class),getApplicationContext());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       final Intent intent = new Intent(AllChatsActivity.this, ChatActivity.class);
                       UserDatabase.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               for(DataSnapshot ds: dataSnapshot.getChildren()){
                                   intent.putExtra("user_id", ds.getKey().toString().trim());
                                   //Log.d("sdfghjk", ds.getKey().toString().trim());
                                   startActivity(intent);
                               }

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       }) ;

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
