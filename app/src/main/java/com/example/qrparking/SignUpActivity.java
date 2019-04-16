package com.example.qrparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class SignUpActivity extends AppCompatActivity {
    Button button;
    EditText et;
    EditText et2;
    EditText et3;
    EditText et4;
    String username;
    String name;
    String password;
    String repassword;
    String id;
    DatabaseReference dataUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FirebaseApp.initializeApp(this);
        button= (Button)findViewById(R.id.button);
        et =(EditText)findViewById(R.id.editText);
        et2 =(EditText)findViewById(R.id.editText2);
        et3 =(EditText)findViewById(R.id.editText3);
        et4 =(EditText)findViewById(R.id.editText4);

        dataUsers = FirebaseDatabase.getInstance().getReference("users");
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //dataUsers.addListenerForSingleValueEvent(valueEventListener);
                username = et.getText().toString();
                password=et2.getText().toString();
                repassword=et3.getText().toString();
                name=et4.getText().toString();

                if(!password.equals(repassword)){
                    Toast("Passwords don't match");
                }
                else{
                    Query query=dataUsers.orderByChild("username").equalTo(username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange( DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                Toast("user exists");
                            else
                                id = dataUsers.push().getKey();
                                User user = new User(name, username, password);
                                dataUsers.child(id).setValue(user);
                                Toast("User added");
                        }

                        @Override
                        public void onCancelled( DatabaseError databaseError) {

                        }
                    });

                }
            }
        });





    }

     public void Toast(String msg){
        Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
     }

}
