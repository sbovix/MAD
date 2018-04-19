package com.example.silviabova.mylogin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddBook extends AppCompatActivity {
    private EditText isbn, title, author, publisher, edition_year, extra;
    private Button register;
    private ImageButton buttonLoadImage;
    private ImageView image1, image2;
    private static final int CAMERA=1;
    private static final int GALLERY=0;
    private Bitmap bitmap1, bitmap2;
    private String simage;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        register = findViewById(R.id.Bt_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerBook(view);
            }
        });

        buttonLoadImage = (ImageButton) findViewById(R.id.Ib_set_image);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        storage = FirebaseStorage.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //elimina la barra sopra
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //permette di mostrare il logo
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo);
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        image1 = findViewById(R.id.Iv_image);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                uri = data.getData();
                try {
                    bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image1.setImageBitmap(bitmap1);
                    simage=savingImage(bitmap1);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == CAMERA) {

            bitmap2 = (Bitmap) data.getExtras().get("data");
            image1.setImageBitmap(bitmap2);
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
            simage=savingImage(bitmap2);
        }
    }

    public static String savingImage(Bitmap bitmap){
        Bitmap image = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public void registerBook(View view){
        isbn = findViewById(R.id.isbn);
        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        publisher = findViewById(R.id.publisher);
        edition_year= findViewById(R.id.ey);
        extra= findViewById(R.id.extra);

        String s_isbn = isbn.getText().toString();
        String s_title = title.getText().toString();
        String s_author = author.getText().toString();
        String s_publisher = publisher.getText().toString();
        String s_edyear = edition_year.getText().toString();
        String s_extra = extra.getText().toString();
        int edyear = Integer.parseInt(s_edyear);

        Book book = new Book();
        book.setIsbn(s_isbn);
        book.setTitle(s_title);
        book.setAuthor(s_author);
        book.setPublisher(s_publisher);
        book.setEdition_year(edyear);
        book.setExtra(s_extra);
        uploadImage();
        book.setImagestring(simage);
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        book.saveBookInformation(db);


    }

    private void uploadImage(){
        if(uri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            simage="image/"+ UUID.randomUUID().toString();

            StorageReference ref = storage.getReference(simage);
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(AddBook.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddBook.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
        }
    }

    //back button in the navigation bar
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }
}
