package com.example.silviabova.mylogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class BookUserDetails extends AppCompatActivity {
    String isbn;
    DatabaseReference databaseReference, userdatabase;
    StorageReference storageReference;
    String title;
    String username;
    String publisher;
    String author;
    String sedyear;
    String condition;
    String userid;
    String sprofileurl;
    String owner;
    String simage;

    TextView publisherview;
    TextView authorview;
    TextView sedyearview;
    TextView condview;
    TextView nameview;
    ImageView profileview;
    ImageView imageView;
    ImageButton message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_user_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.logo);

        isbn=getIntent().getStringExtra("ISBN");
        databaseReference= FirebaseDatabase.getInstance().getReference("Books/");
        storageReference = FirebaseStorage.getInstance().getReference("/image/");

        publisherview = (TextView) findViewById(R.id.publisher);
        authorview = (TextView) findViewById(R.id.author);
        sedyearview= (TextView) findViewById(R.id.edyear);
        condview=(TextView) findViewById(R.id.condition);
        nameview=(TextView) findViewById(R.id.username);
        profileview=(ImageView)findViewById(R.id.profileImage);
        imageView=(ImageView) findViewById(R.id.bookimage);
        message=(ImageButton) findViewById(R.id.chat);

        findUserAndBook(isbn.toString());

        nameview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(BookUserDetails.this, UserShowActivity.class);
                intent.putExtra("UID", owner);
                startActivity(intent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookUserDetails.this,ChatActivity.class);
                intent.putExtra("user_id",owner);
                startActivity(intent);
                //Toast.makeText(BookUserDetails.this, "Chat aperta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }

    private void findUserAndBook(String isbn){
        final String Isbn=isbn;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                    if(ds.child(Isbn).exists()){
                        title=ds.child(Isbn).child("title").getValue(String.class);
                        publisher=ds.child(Isbn).child("publisher").getValue(String.class);
                        author=ds.child(Isbn).child("author").getValue(String.class);
                        sedyear=ds.child(Isbn).child("edition_year").getValue(Integer.class).toString();
                        condition=ds.child(Isbn).child("extra").getValue(String.class);
                        owner = ds.child(Isbn).child("owner").getValue(String.class);
                        simage=ds.child(Isbn).child("image").getValue(String.class);
                        simage=simage.replace("image/", "");
                        simage= simage.trim();

                        getSupportActionBar().setTitle(title);

                        publisherview.setText(publisher);
                        authorview.setText(author);
                        sedyearview.setText(sedyear);
                        condview.setText(condition);
                        if(simage!=null){
                            findImageInStorage(simage);
                        }

                        if((owner==null) ||(owner.compareTo("")==0)){
                            Toast.makeText(BookUserDetails.this, "USERID nulla", Toast.LENGTH_SHORT).show();
                            //Log.d("USERID", "gggggggggggggggggggggggggg userid nulla");
                        }else{
                            //Log.d("USERID", "gggggggggggggggggggggggggg userid NON nulla");
                        }

                        userdatabase = FirebaseDatabase.getInstance().getReference("Users/" + owner);
                        userdatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                username = dataSnapshot.child("name").getValue(String.class);
                                //Log.d("USERID", username);
                                if((username==null)||(username.compareTo("")==0)){
                                    Toast.makeText(BookUserDetails.this, "USERNAME null", Toast.LENGTH_SHORT).show();
                                }
                                nameview.setText("Contact "+username+"to share the book");
                                sprofileurl=dataSnapshot.child("urlimage").getValue(String.class);
                                Picasso.with(BookUserDetails.this).load(sprofileurl).transform((Transformation) new PicassoCircleTransformation()).into(profileview);
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

    }

    public void findImageInStorage(String simage){
        final long ONE_MEGABYTE = 10* 1024 * 1024;
        storageReference.child(simage).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }
}
