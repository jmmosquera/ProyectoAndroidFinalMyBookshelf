package iezv.jmm.mybookshelfproject;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import iezv.jmm.mybookshelfproject.Firebase.firebase;

public class Login extends AppCompatActivity {

    EditText userInput;
    EditText passInput;
    Button btnlogin, btnregis;
    TextView txterror;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private firebase FB = new firebase();

    private String user, pass;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        userInput = findViewById(R.id.tvusername);
        passInput = findViewById(R.id.tvPass);
        btnlogin = findViewById(R.id.loginbtn);
        btnregis = findViewById(R.id.registerbtn);
        txterror = findViewById(R.id.errmsg);

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){
                    Intent maIntent = new Intent(Login.this, MainActivity.class);
                    startActivity(maIntent);
                }
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {

                user = userInput.getText().toString();
                pass = passInput.getText().toString();

                if (user != "" && pass != "") {

                    FB.signIn(user , pass);

                }else{
                    txterror.setText("You must fill in both fields");
                    return;
                }
            }
        });

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                user = userInput.getText().toString();
                pass = passInput.getText().toString();

                if (user != "" && pass != "") {

                    FB.createUser(user , pass);
                    Intent maIntent = new Intent(Login.this , Login.class);
                    startActivity(maIntent);
                }else{
                    txterror.setText("You must fill in both fields");
                    return;
                }
            }
        });
    }
}