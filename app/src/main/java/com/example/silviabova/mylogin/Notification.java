package com.example.silviabova.mylogin;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by silvia bova on 28/05/2018.
 */

public class Notification extends IntentService {

    private NotificationManager manager;
    private DatabaseReference db;
    private FirebaseAuth auth;
    ArrayList<String> titles;
    private PendingIntent pendingIntent;
    private int indice=0;
    private final String channel_name= "dfghj",
            channel_description= "dfghjk",
            CHANNEL_ID= "dfghjkl";
    private String isbn,id;

    public Notification() {
        super("Notification");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getApplicationContext();
        manager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        final Intent mintent = new Intent(context, ChatActivity.class);
//        pendingIntent = PendingIntent.getActivity(context,0,mintent,0);

        db = FirebaseDatabase.getInstance().getReference("Users/"+user+"/Chats");
        if(!db.getKey().isEmpty()){
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds :dataSnapshot.getChildren()){
                        id = ds.getKey();
                        //mintent.putExtra("user_id",id);
                        Log.d("current",user);
                        Log.d("chat",id);
                        FirebaseDatabase.getInstance().getReference("Users/"+user+"/Chats/"+id+"/book/isbn").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                isbn = dataSnapshot.getValue(String.class);
                                //mintent.putExtra("book",isbn);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        if(ds.child("read").exists()&&!ds.child("read").getValue(Boolean.class)){
                            FirebaseDatabase.getInstance().getReference("Users/"+id+"/name").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    sendNotification(dataSnapshot.getValue(String.class));
                                    //Log.d("Ci entroooo",dataSnapshot.getValue(String.class));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


    }

    private void sendNotification(String value) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.libri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setContentTitle(value)
                .setContentText("New messages")
                .setWhen(System.currentTimeMillis());

                manager.notify(indice,builder.build());
                indice++;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager.createNotificationChannel(channel);
        }
    }

}
