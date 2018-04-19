package com.example.silviabova.mylogin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class BookDetails extends AppCompatActivity {
    FirebaseAuth mAuth;
    //FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Book book;
    String isbn;

    TextView title;
    TextView author;
    TextView publisher;
    TextView edyear;
    TextView condition;
    TextView tisbn;
    ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        isbn= getIntent().getStringExtra("ISBN");

        title = (TextView) findViewById(R.id.title);
        author = (TextView) findViewById(R.id.author);
        publisher = (TextView) findViewById(R.id.publisher);
        edyear = (TextView) findViewById(R.id.edyear);
        condition = (TextView) findViewById(R.id.bookcondition);
        image = (ImageView) findViewById(R.id.imageView);
        tisbn = (TextView) findViewById(R.id.isbn);
        tisbn.setText(isbn);
        storageReference = FirebaseStorage.getInstance().getReference("image/");

        getBookInformation(isbn);
    }

    private void getBookInformation(String isbn){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("/"+user.getUid()+"/Books/"+isbn);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stitle = dataSnapshot.child("/title").getValue(String.class);
                String sauthor = dataSnapshot.child("/author").getValue(String.class);
                String spublisher = dataSnapshot.child("/publisher").getValue(String.class);
                int sedyear =  dataSnapshot.child("/edition_year").getValue(Integer.class);
                String scond = dataSnapshot.child("/extra").getValue(String.class);
                String simage = dataSnapshot.child("/image").getValue(String.class);
                simage=simage.replace("image/", "");
                simage= simage.trim();

                title.setText(stitle);
                author.setText(sauthor);
                publisher.setText(spublisher);
                edyear.setText(""+sedyear);
                condition.setText(scond);

                findImageInStorage(simage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void findImageInStorage(String simage){
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.child(simage).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        });
    }
}
