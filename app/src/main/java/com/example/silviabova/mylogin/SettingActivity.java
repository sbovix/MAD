package com.example.silviabova.mylogin;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by silvia bova on 22/05/2018.
 */

public class SettingActivity extends AppCompatActivity {

    private TextView name,age,bio;
    private ImageView imageProfile;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private EditText age_edit,name_edit, bio_edit;
    private int GALLERY = 0, CAMERA = 1;
    private Bitmap bitmap1, bitmap2;
    private ImageButton imageButton;
    private Uri contentURI;
    private static final String IMAGE_DIRECTORY = "/MyNewImages";
    private String ubio,uname,uage,sname,sage,sbio,simage;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        name = (TextView)findViewById(R.id.name);
        age = (TextView)findViewById(R.id.age);
        bio = (TextView)findViewById(R.id.bio);
        imageProfile = (ImageView)findViewById(R.id.Tv_UserImage);
        name_edit = (EditText)findViewById(R.id.name_edit);
        age_edit = (EditText)findViewById(R.id.age_edit);
        bio_edit = (EditText)findViewById(R.id.bio_edit);
        imageButton = (ImageButton)findViewById(R.id.setImage);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        showCurrentUserProfile();

        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                age.setVisibility(View.INVISIBLE);
                age_edit.setVisibility(View.VISIBLE);
                uage = age_edit.getText().toString().trim();
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setVisibility(View.INVISIBLE);
                name_edit.setVisibility(View.VISIBLE);
                uname = name_edit.getText().toString().trim();
            }
        });

        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bio.setVisibility(View.INVISIBLE);
                bio_edit.setVisibility(View.VISIBLE);
                ubio = bio_edit.getText().toString().trim();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);

    }

    private void showCurrentUserProfile(){

        final FirebaseUser user = auth.getCurrentUser();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sname = dataSnapshot.child("Users").child(user.getUid()).child("name").getValue(String.class);
                sage = dataSnapshot.child("Users").child(user.getUid()).child("age").getValue(String.class);
                sbio = dataSnapshot.child("Users").child(user.getUid()).child("bio").getValue(String.class);
                String sURL = dataSnapshot.child("Users").child(user.getUid()).child("urlimage").getValue(String.class);

                name.setText("Name: " + sname);
                age.setText("Birthday: " + sage);
                bio.setText("Description: " + sbio);
                Picasso.with(SettingActivity.this).load(sURL).placeholder(R.drawable.user1).transform((Transformation) new PicassoCircleTransformation()).into(imageProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingActivity.this,databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);

        if (id == R.id.ic_save){
            //aprire Activity di ricerca
            final FirebaseUser user = auth.getCurrentUser();

            if(uname!=null){
                final String uname = name_edit.getText().toString().trim();
                database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("Users/"+user.getUid()+"/name").setValue(uname);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            }

            if(uage!=null){
                final String uage = age_edit.getText().toString().trim();
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FirebaseDatabase.getInstance().getReference().child("Users/"+user.getUid()+"/age").setValue(uage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            }

            if(ubio!=null){
                final String ubio = bio_edit.getText().toString().trim();
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("Users/"+user.getUid()+"/bio").setValue(ubio);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if(contentURI != null){
                StorageReference ref = storageReference.child( System.currentTimeMillis() + "." + getFileExtention(contentURI));
                ref.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UserInfo userInfo = new UserInfo(uname,ubio,uage, taskSnapshot.getDownloadUrl().toString());
                        Toast.makeText(SettingActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();

                        database.child("Users").child(user.getUid()).setValue(userInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        imageProfile = findViewById(R.id.Tv_UserImage);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                contentURI = data.getData();
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap1);
                    Toast.makeText(SettingActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageProfile.setImageBitmap(bitmap1);
                    Picasso.with(SettingActivity.this).load(contentURI).placeholder(R.drawable.user1).transform(new PicassoCircleTransformation()).into(imageProfile);
                    simage = savingImage(bitmap1);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SettingActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == CAMERA) {

            imageProfile = (ImageView) findViewById(R.id.Tv_UserImage);
            bitmap2 = (Bitmap) data.getExtras().get("data");
            imageProfile.setImageBitmap(bitmap2);
            Picasso.with(SettingActivity.this).load(contentURI).placeholder(R.drawable.user1).transform(new PicassoCircleTransformation()).into(imageProfile);
            Toast.makeText(SettingActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            simage = savingImage(bitmap2);
        }
    }

    public static String savingImage(Bitmap bitmap) {
        Bitmap image = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private String getFileExtention(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

}

