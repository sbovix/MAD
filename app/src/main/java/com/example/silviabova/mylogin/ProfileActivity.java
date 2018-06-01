package com.example.silviabova.mylogin;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private TextView Tv_useremail;
    private Button logout;
    private ImageButton buttonImage;
    private EditText name;
    private EditText bio;
    private EditText age;
    private Button save;
    private ImageView ImageToSave;
    private Bitmap bitmap1, bitmap2;
    private static final String IMAGE_DIRECTORY = "/MyNewImages";
    private static final int GALLERY = 0, CAMERA = 1, WRITE=2;
    private ImageView image1, image2;
    private String simage;
    private Uri contentURI;
    private ProgressBar mProgressbar;
    private boolean permissioncamera, writingpermission;
    private boolean permissiongallery;
    //private final int PICK_IMAGE = 100;

    private DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        declareWritingPermission();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        Tv_useremail = (TextView) findViewById(R.id.Tv_useremail);
        Tv_useremail.setText("Welcome");


        databaseReference = FirebaseDatabase.getInstance().getReference();
        name = (EditText) findViewById(R.id.Name);
        bio = (EditText) findViewById(R.id.Bio);
        age = (EditText) findViewById(R.id.Age);
        save = (Button) findViewById(R.id.Bt_save);
        ImageToSave = (ImageView) findViewById(R.id.image);
        buttonImage = (ImageButton) findViewById(R.id.setImage);
        buttonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ChooseImage();
                showPictureDialog();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveuserInfo();
            }
        });
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    private void saveuserInfo(){
        final String uname = name.getText().toString().trim();
        final String uage = age.getText().toString().trim();
        final String ubio = bio.getText().toString().trim();
        final String uURI;

        if(contentURI==null){
            final StorageReference storageReference2 = storageReference;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(System.currentTimeMillis()+"image.jpeg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            storageReference.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("URIII",taskSnapshot.getDownloadUrl().toString() );
                    UserInfo userInfo = new UserInfo(uname,ubio,uage, taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(ProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    databaseReference.child("Users").child(user.getUid()).setValue(userInfo);
                }
            });

        }

        if(contentURI != null){
            StorageReference ref = storageReference.child( System.currentTimeMillis() + "." + getFileExtention(contentURI));
            ref.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UserInfo userInfo = new UserInfo(uname,ubio,uage, taskSnapshot.getDownloadUrl().toString());
                    Toast.makeText(ProfileActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    databaseReference.child("Users").child(user.getUid()).setValue(userInfo);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        Toast.makeText(this, "Information saved", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(this, UserActivity.class));
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        declareCameraPermission();
        if(permissioncamera){
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        }
    }
    private void declareWritingPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            writingpermission=true;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE);
        }
    }

    private void declareCameraPermission(){
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            permissioncamera=true;
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //mLocationPErmissionGranted=false;
        permissioncamera=false;
        writingpermission=false;
        switch (requestCode){
            case CAMERA: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissioncamera=true;
                }
            }
            case WRITE: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    writingpermission=true;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        image1 = findViewById(R.id.image);
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
                    Toast.makeText(ProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image1.setImageBitmap(bitmap1);
                    simage = savingImage(bitmap1);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == CAMERA) {

            image2 = (ImageView) findViewById(R.id.image);
            bitmap2 = (Bitmap) data.getExtras().get("data");
            image2.setImageBitmap(bitmap2);
            Toast.makeText(ProfileActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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