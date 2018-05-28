package com.example.silviabova.mylogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllChatsActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String CurrentUser = mAuth.getCurrentUser().getUid();
    private DatabaseReference UserDatabase, DB;
    private List<UserChat> userChats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        UserDatabase = FirebaseDatabase.getInstance().getReference("Users/").child(CurrentUser).child("Chats");
        DB = FirebaseDatabase.getInstance().getReference("Users/");

        userChats = new ArrayList<>();


        ////////////////////////////////////////////////////////////////////////////////////////////

        UserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    final String ChatUser = ds.getKey().toString().trim();
                    //Log.d("USERID", ChatUser);
                    DB.child(ChatUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataS) {
                            userChats.add(new UserChat(dataS.child("name").getValue(String.class),dataS.child("urlimage").getValue(String.class),dataS.child("bio").getValue(String.class),ChatUser));
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

        mUserList = (RecyclerView) findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        ViewHolder adapter = new ViewHolder(this,userChats);

        mUserList.setAdapter(adapter);


        ////////////////////////////////////////////////////////////////////////////////////////////
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

        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

}