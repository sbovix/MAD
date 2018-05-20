package com.example.silviabova.mylogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    FloatingActionButton fab;
    String mChatUser;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    String mCurrentUserId;
    String UserName,UserPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRef = FirebaseDatabase.getInstance().getReference();
        RelativeLayout activity_chat = findViewById(R.id.activity_chat);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserName = dataSnapshot.child("Users").child(mCurrentUserId).child("name").getValue(String.class);
                //Log.d("name",UserName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser).push().setValue(new ChatMessage(input.getText().toString(),
                        UserName,1));
                FirebaseDatabase.getInstance().getReference("Users/"+mChatUser+"/Chats/"+mCurrentUserId).push().setValue(new ChatMessage(input.getText().toString(),
                        UserName,2));
                input.setText("");
            }
        });


        mChatUser = getIntent().getStringExtra("user_id");

        //Check if the user is already signed in
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mRef.child("/Chats/"+mChatUser);
        if( mAuth.getCurrentUser()== null){
            finish();
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
        }
        else{
            //Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show();
            //Load content
            displayChatMessage();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //back button in the navigation bar
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), AllChatsActivity.class);
        startActivityForResult(myIntent, 0);
        finish();

        return true;

    }

    private void displayChatMessage(){
        ListView listOfMessage = (ListView)findViewById(R.id.MessageList);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Get reference to the view of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));

                if(model.getType() == 1){ //MESSAGE SENT
                    messageText.setGravity(Gravity.LEFT);
                    messageTime.setGravity(Gravity.RIGHT);
                    messageUser.setText("You:");
                    messageText.setPadding(400,50,5,100);
                    messageUser.setPadding(400,5,5,50);
                    messageTime.setPadding(400,100,5,50);
                }
                else if(model.getType() == 2){ //MESSAGE RECEIVED
                    messageText.setGravity(Gravity.LEFT);
                    messageTime.setGravity(Gravity.RIGHT);
                    messageUser.setText(model.getMessageUser()+":");
                    messageText.setPadding(5,50,400,100);
                    messageUser.setPadding(5,5,400,50);
                    messageTime.setPadding(5,100,400,50);
                }

            }
        };
        listOfMessage.setAdapter(adapter);
    }



}
