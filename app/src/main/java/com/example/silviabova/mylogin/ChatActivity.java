package com.example.silviabova.mylogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    String UserName,UserPhoto,isbn,rated;
    private RelativeLayout whole;
    private int preLast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRef = FirebaseDatabase.getInstance().getReference();

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


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.input);
                FirebaseDatabase.getInstance().getReference("Users/" + mCurrentUserId + "/Chats/" + mChatUser + "/messages").push().setValue(new ChatMessage(input.getText().toString(),
                        UserName, 1));
                FirebaseDatabase.getInstance().getReference("Users/" + mChatUser + "/Chats/" + mCurrentUserId + "/messages").push().setValue(new ChatMessage(input.getText().toString(),
                        UserName, 2));

                FirebaseDatabase.getInstance().getReference("Users/" + mCurrentUserId + "/Chats/" + mChatUser + "/read").setValue(true);
                FirebaseDatabase.getInstance().getReference("Users/" + mChatUser + "/Chats/" + mCurrentUserId + "/read").setValue(false);

                input.setText("");
            }
        });


        mChatUser = getIntent().getStringExtra("user_id");
        isbn = getIntent().getStringExtra("book");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setTitle(dataSnapshot.child("Users").child(mChatUser).child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Check if the user is already signed in
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mRef.child("/Chats/" + mChatUser);
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
        } else {
            //Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show();
            //Load content
            displayChatMessage();
        }


        FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild("book")||snapshot.child("book/isbn").getValue().equals(isbn)) {
                    FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser+"/book/isbn").setValue(isbn);
                    FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser+"/book/restituito").setValue("0");
                    FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser+"/book/rated").setValue("0");

                    FirebaseDatabase.getInstance().getReference("Users/"+mChatUser+"/Chats/"+mCurrentUserId+"/book/isbn").setValue(isbn);
                    FirebaseDatabase.getInstance().getReference("Users/"+mChatUser+"/Chats/"+mCurrentUserId+"/book/prestato").setValue("0");
                    FirebaseDatabase.getInstance().getReference("Users/"+mChatUser+"/Chats/"+mCurrentUserId+"/book/rated").setValue("0");
                    }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser+"/book").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rated = dataSnapshot.child("rated").getValue(String.class);
                //Log.d("rate",rated);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }


    private void displayChatMessage(){
        final ListView listOfMessage = (ListView)findViewById(R.id.MessageList);
        //Visualizza la chat dall'ultimo messaggio ricevuto
        listOfMessage.post(new Runnable(){
            public void run() {
                listOfMessage.setSelection(listOfMessage.getCount() - 1);
            }});

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference("Users/"+mCurrentUserId+"/Chats/"+mChatUser+"/messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Get reference to the view of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);
                whole = (RelativeLayout) v.findViewById(R.id.whole_msg);

                messageText.setText(model.getMessageText());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getMessageTime()));
                db.child("read").setValue(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

//        final Intent myIntent = new Intent(getApplicationContext(), AllChatsActivity.class);
//        startActivityForResult(myIntent, 0);
//        finish();

        if(rated.equals("0")){
            if (id == R.id.rating_bar){
                //open RatingBar Activity
                finish();
                Intent intent = new Intent(ChatActivity.this, RaitingUsers.class);
                intent.putExtra("user_id", mCurrentUserId);
                intent.putExtra("chatUser_id", mChatUser);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(ChatActivity.this,"User already rated",Toast.LENGTH_LONG);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rating, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
