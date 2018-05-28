package com.example.silviabova.mylogin

/**
 * Created by silvia bova on 21/05/2018.
 */

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class RaitingUsers : AppCompatActivity() {

    private var UserImage: ImageView? = null
    internal lateinit var mChatUser: String
    private var dbReference: DatabaseReference? = null
    private var n_rate: Int = 0
    private var rate : Float = 0.0f
    private var actual_rate : Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raitingbar)

        UserImage = findViewById<ImageView>(R.id.imageView_user)
        dbReference = FirebaseDatabase.getInstance().reference

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        if (ratingBar != null) {
            val button = findViewById<Button>(R.id.button)
            button?.setOnClickListener {
                val msg = ratingBar.rating.toString()
                msg.evaluateRating()
                Toast.makeText(this@RaitingUsers, msg, Toast.LENGTH_SHORT).show()
                val myIntent = Intent(applicationContext, ChatActivity::class.java)
                myIntent.putExtra("user_id",mChatUser);
                startActivityForResult(myIntent, 0)
                finish()
            }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        //elimina la barra sopra
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        showUserImage()

    }

    private fun showUserImage() {

        dbReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mChatUser = intent.getStringExtra("chatUser_id")

                val sURL = dataSnapshot.child("Users").child(mChatUser).child("urlimage").getValue(String::class.java)

                Picasso.with(this@RaitingUsers).load(sURL).transform(PicassoCircleTransformation() as Transformation).into(UserImage)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RaitingUsers, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //elimina la barra sopra
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, ChatActivity::class.java)
        startActivityForResult(myIntent, 0)
        finish()

        return true

    }

    private fun String.evaluateRating() {

        dbReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                rate = dataSnapshot.child("Users").child(mChatUser).child("rate").getValue(String::class.java)!!.toFloat()
                //Log.d("rate",rate)
                n_rate = Integer.parseInt(dataSnapshot.child("Users").child(mChatUser).child("n_rate").getValue(String::class.java))

                actual_rate = rate + (this@evaluateRating).toFloat()
                n_rate ++
                Log.d("n rate", java.lang.String.valueOf(n_rate))
                Log.d("rate", java.lang.String.valueOf(actual_rate/n_rate))
                FirebaseDatabase.getInstance().reference.child("Users/$mChatUser/n_rate").setValue(n_rate.toString())
                FirebaseDatabase.getInstance().reference.child("Users/$mChatUser/rate").setValue((actual_rate/n_rate).toString())


            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RaitingUsers, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        }
}

