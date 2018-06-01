package com.example.silviabova.mylogin

/**
 * Created by silvia bova on 21/05/2018.
 */

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class RaitingUsers : AppCompatActivity() {


    internal lateinit var mChatUser: String
    private var dbReference: DatabaseReference? = null
    private var dbChat: DatabaseReference? = null
    private var n_rate: Int = 0
    private var rate : Float = 0.0f
    private var actual_rate : Float = 0.0f
    private var owner :String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raitingbar)

        mChatUser = intent.getStringExtra("chatUser_id")
        val authUser = FirebaseAuth.getInstance().currentUser!!.uid
        val UserImage = findViewById<ImageView>(R.id.imageView_user)
        val Text = findViewById<TextView>(R.id.textView2)
        FirebaseDatabase.getInstance().reference.child("Users/${FirebaseAuth.getInstance().currentUser!!.uid}/Chats/$mChatUser/book").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isbn = dataSnapshot.child("isbn").getValue(String::class.java)
                FirebaseDatabase.getInstance().reference.child("Books/$isbn/owner").addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(ds: DataSnapshot?) {
                        owner = ds!!.getValue().toString()
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(this@RaitingUsers, databaseError.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RaitingUsers, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })


        if(authUser.equals(owner)){
            Text.text = "Switch on the button when the book has been shared"
        }
        else{
            Text.text = "Switch on the button when the book has been given back"
        }



        dbReference = FirebaseDatabase.getInstance().reference
        dbChat = FirebaseDatabase.getInstance().getReference("Users/$authUser/Chats/$mChatUser/book")
        val simpleSwitch = findViewById<Switch>(R.id.simpleSwitch)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        //When the switch is on we have already lended tha book
        simpleSwitch?.setOnClickListener{
            if (simpleSwitch.isChecked){

                if(authUser.equals(owner)){
                    dbChat!!.child("prestato").setValue("1")
                }
                else{
                    dbChat!!.child("restituito").setValue("1")
                }

                simpleSwitch.visibility = View.INVISIBLE
                Text.visibility = View.INVISIBLE
                ratingBar.visibility = View.VISIBLE
                UserImage.visibility = View.VISIBLE
                val button = findViewById<Button>(R.id.button)
                button.visibility = View.VISIBLE
                if (ratingBar != null) {
                    button?.setOnClickListener {
                        val msg = ratingBar.rating.toString()
                        msg.evaluateRating()

                        if(authUser.equals(owner)){
                            dbChat!!.child("rated").setValue("1")
                        }
                        else{
                            dbChat!!.child("rated").setValue("1")
                        }

                        Toast.makeText(this@RaitingUsers, msg, Toast.LENGTH_SHORT).show()
                        val myIntent = Intent(applicationContext, ChatActivity::class.java)
                        myIntent.putExtra("user_id",mChatUser)
                        finish()
                        startActivityForResult(myIntent, 0)
                    }
                }
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

                val UserImage = findViewById<ImageView>(R.id.imageView_user)
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
        myIntent.putExtra("user_id", mChatUser)
        startActivityForResult(myIntent, 0)

        return true

    }

    private fun String.evaluateRating() {

        dbReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var mCurrentUserId = intent.getStringExtra("user_id")
                rate = dataSnapshot.child("Users").child(mChatUser).child("rate").getValue(String::class.java)!!.toFloat()
                //Log.d("rate",rate)
                n_rate = Integer.parseInt(dataSnapshot.child("Users").child(mChatUser).child("n_rate").getValue(String::class.java))

                actual_rate = rate + (this@evaluateRating).toFloat()
                n_rate ++
                Log.d("n rate", java.lang.String.valueOf(n_rate))
                Log.d("rate", java.lang.String.valueOf(actual_rate/n_rate))
                FirebaseDatabase.getInstance().reference.child("Users/$mChatUser/n_rate").setValue(n_rate.toString())
                FirebaseDatabase.getInstance().reference.child("Users/$mChatUser/rate").setValue((actual_rate/n_rate).toString())
                FirebaseDatabase.getInstance().getReference("Users/$mCurrentUserId/Chats/$mChatUser/book/rated").setValue("1")

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@RaitingUsers, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })

        }
}

