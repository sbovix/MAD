package com.example.silviabova.mylogin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText Et_password;
    private EditText Et_email;
    private Button login;
    private TextView registerLink;

    private FirebaseAuth MfirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MfirebaseAuth = FirebaseAuth.getInstance();

        Et_password = (EditText) findViewById(R.id.Et_password);
        Et_email = (EditText) findViewById(R.id.Et_email);
        login = (Button) findViewById(R.id.Bt_login);
        registerLink = (TextView) findViewById(R.id.Tv_register);

        login.setOnClickListener(this);
        registerLink.setOnClickListener(this);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void userLogin(){
        String email = Et_email.getText().toString().trim();
        String password = Et_password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //user must insert email
            Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            //user must insert password
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
            return;
        }

        // This is the Authentication via mail and password provided by firebase

        MfirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //start the profile activity
                    Toast.makeText(MainActivity.this,"Access",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    //FirebaseUser user = MfirebaseAuth.getCurrentUser();
                    //updateUI(user);
                }else{
                    Toast.makeText(MainActivity.this,"Fail",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    @Override
    public void onClick(View view){
        if(view == login){
            userLogin();
        }
        if(view == registerLink){
            //open login activity
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }


}
