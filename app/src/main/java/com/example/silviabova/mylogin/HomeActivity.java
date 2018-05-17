package com.example.silviabova.mylogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;

    private ImageView UserImage;
    private TextView Name;
    private ImageButton add;

    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    private GridView gridView;
    private ArrayList<String> imageStrings = new ArrayList<>();
    private ArrayList<String> isbn= new ArrayList<>();
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Bitmap bitmap;
    ArrayList<Bitmap> books;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        drawerLayout = (DrawerLayout)findViewById(R.id.activityHome);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.slider);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        UserImage = (ImageView) header.findViewById(R.id.HUserImage);
        Name = (TextView) header.findViewById(R.id.Name);
        add = (ImageButton) findViewById(R.id.imageButton);

        showUserImageName();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

        books = new ArrayList<>();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("image/");

        findImage();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        if (id == R.id.search_bar){
            //aprire Activity di ricerca
            finish();
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void showUserImageName() {

        final FirebaseUser user = mAuth.getCurrentUser();


        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sname = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                String sURL = dataSnapshot.child(user.getUid()).child("URLimage").getValue(String.class);

                Name.setText(sname);

                Picasso.with(HomeActivity.this).load(sURL).transform((Transformation) new PicassoCircleTransformation()).into(UserImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.buttonProfile){
            finish();
            startActivity(new Intent(HomeActivity.this,UserActivity.class));
        }
        if(id==R.id.mybutton){
            mAuth.signOut();
            finish();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        }
        return false;
    }

    private void findImage(){
        imageStrings.clear();
        isbn.clear();
        final FirebaseUser user = mAuth.getCurrentUser();

        dbReference=FirebaseDatabase.getInstance().getReference(""+user.getUid()+"/Books");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    String simage = ds.child("/image").getValue(String.class);
                    String sisbn = ds.getKey();
                    if(simage!=null){
                        simage=simage.replace("image/", "");
                        simage=simage.trim();
                        imageStrings.add(simage);
                    }
                    if(sisbn!=null){
                        isbn.add(sisbn);
                    }


                }
                findImageInStorage(imageStrings, isbn);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findImageInStorage(List<String> strings, List<String> isbn) {
        books.clear();
        for (String s : strings) {
            final long ONE_MEGABYTE = 1024 * 1024*1024;
            final List<String> isbn2 = isbn.subList(0, isbn.size());
            storageReference.child(s).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    books.add(bitmap);
                    gridView = (GridView) findViewById(R.id.gridview);
                    gridView.setAdapter(new ImageAdapterGridView(HomeActivity.this, books));

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getBaseContext(), "Grid item " + (i + 1) + " Selected", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(HomeActivity.this, BookDetails.class);
                            intent.putExtra("ISBN", isbn2.get(i));
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Insert book",
                "Scan ISBN"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //chiama registra manualmente
                                finish();
                                startActivity(new Intent(HomeActivity.this,AddBook.class));
                                break;
                            case 1:
                                //scanning
                                finish();
                                startActivity(new Intent(HomeActivity.this, Barcode_Scanner.class));
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
