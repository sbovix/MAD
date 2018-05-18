package com.example.silviabova.mylogin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by cupia on 18/04/2018.
 */

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int edition_year;
    private String book_condition;
    private String extra;
    private int libri=1;
    private String imagestring;


    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getEdition_year() {
        return edition_year;
    }

    public void setEdition_year(int edition_year) {
        this.edition_year = edition_year;
    }

    public String getBook_condition() {
        return book_condition;
    }

    public void setBook_condition(String book_condition) {
        this.book_condition = book_condition;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getLibri() {
        return libri;
    }

    public void setLibri(int libri) {
        this.libri = libri;
    }

    public String getImagestring() {
        return imagestring;
    }

    public void setImagestring(String imagestring) {
        this.imagestring = imagestring;
    }

    public void saveBookInformation(FirebaseDatabase fbUser,FirebaseDatabase fbBook){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference myIsbn = fbUser.getReference("Users/"+user.getUid()+"/Books/"+isbn);
        DatabaseReference myReftitle = fbBook.getReference("Books/"+isbn+"/title");
        DatabaseReference myRefauthor = fbBook.getReference("Books/"+isbn+"/author");
        DatabaseReference myRefpublisher = fbBook.getReference("Books/"+isbn+"/publisher");
        DatabaseReference myRefeditionyear= fbBook.getReference("Books/"+isbn+"/edition_year");
        DatabaseReference myRefbookcondition = fbBook.getReference("Books/"+isbn+"/book_condition");
        DatabaseReference myRefextra = fbBook.getReference("Books/"+isbn+"/extra");
        DatabaseReference myRefimage = fbBook.getReference("Books/"+isbn+"/image");

        myReftitle.setValue(title);
        myRefauthor.setValue(author);
        myRefpublisher.setValue(publisher);
        myRefeditionyear.setValue(edition_year);
        myRefbookcondition.setValue(book_condition);
        myRefextra.setValue(extra);
        myRefimage.setValue(imagestring);
        myIsbn.setValue(isbn);
    }
}
