package com.example.silviabova.mylogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class UserActivity extends AppCompatActivity {
    private TextView name;
    private TextView age;
    private TextView bio;
    private ImageView UserImage;

    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        name = (TextView) findViewById(R.id.name);
        age = (TextView) findViewById(R.id.age);
        bio = (TextView) findViewById(R.id.bio);
        UserImage = (ImageView) findViewById(R.id.Tv_UserImage);

        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        showUserProfile();

    }



    private void showUserProfile(){

        final FirebaseUser user = mAuth.getCurrentUser();

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sname = dataSnapshot.child("Users").child(user.getUid()).child("name").getValue(String.class);
                String sage = dataSnapshot.child("Users").child(user.getUid()).child("age").getValue(String.class);
                String sbio = dataSnapshot.child("Users").child(user.getUid()).child("bio").getValue(String.class);
                String sURL = dataSnapshot.child("Users").child(user.getUid()).child("urlimage").getValue(String.class);

                name.setText("Name: " + sname);
                age.setText("Birthday: " + sage);
                bio.setText("Description: " + sbio);
                Picasso.with(UserActivity.this).load(sURL).transform((Transformation) new PicassoCircleTransformation()).into(UserImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);

        if (id == R.id.mybutton) {
            // do something here
            mAuth.signOut();
            finish();
            startActivity(new Intent(UserActivity.this, MainActivity.class));
        }
        if (id == R.id.buttonSet) {
            // do something here
            finish();
            startActivity(new Intent(UserActivity.this, SettingActivity.class));
        }
        if (id == R.id.buttonLibrary) {
            // do something here
            finish();
            startActivity(new Intent(UserActivity.this, HomeActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


}



