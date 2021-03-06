package com.example.silviabova.mylogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class UserShowActivity extends AppCompatActivity {
    private TextView name;
    private TextView age;
    private TextView bio;
    private ImageView UserImage;
    private RatingBar starViews;
    private ImageButton message;

    private String userid,isbn;

    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        getSupportActionBar().setTitle("Owner Profile");

        userid = getIntent().getStringExtra("UID");

        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        bio = (TextView) findViewById(R.id.bio);
        UserImage = (ImageView) findViewById(R.id.Tv_UserImage);
        message=(ImageButton) findViewById(R.id.contactbutton);
        starViews = (RatingBar)findViewById(R.id.ratingBar);


        dbReference = FirebaseDatabase.getInstance().getReference("Users/");

        showUserProfile(userid);

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(UserShowActivity.this,ChatActivity.class);
                intent.putExtra("user_id",userid);
                isbn = intent.getStringExtra("book");
                intent.putExtra("book", isbn);
                startActivity(intent);
            }
        });


    }

    private void showUserProfile(final String userid){

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child(userid).child("name").getValue(String.class);
                String userage= dataSnapshot.child(userid).child("age").getValue(String.class);
                String userbio= dataSnapshot.child(userid).child("bio").getValue(String.class);
                String url = dataSnapshot.child(userid).child("urlimage").getValue(String.class);
                float rate = Float.parseFloat(dataSnapshot.child(userid).child("rate").getValue(String.class));

                Log.d("Stars", String.valueOf(rate));

                name.setText("Name: " + username);
                age.setText("Birthday: " + userage);
                bio.setText("Description: " + userbio);
                Picasso.with(UserShowActivity.this).load(url).transform((Transformation) new PicassoCircleTransformation()).into(UserImage);

                starViews.setRating(rate);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserShowActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    //back button in the navigation bar
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), SearchActivity.class);
        myIntent.putExtra("book",isbn);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }
}
