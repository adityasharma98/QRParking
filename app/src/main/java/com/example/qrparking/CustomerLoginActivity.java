package com.example.qrparking;

import android.content.Intent;
import android.os.Bundle;
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

public class CustomerLoginActivity extends AppCompatActivity {
    private Button b1;
    private EditText et1,et2;
    DatabaseReference dataUsers;
    String username;
    String password;
    String name;
    String keys;
    int Flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        FirebaseApp.initializeApp(this);
        dataUsers = FirebaseDatabase.getInstance().getReference("users");
        b1=(Button)findViewById(R.id.button);
        et1=(EditText)findViewById(R.id.editText);
        et2=(EditText)findViewById(R.id.editText2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et1.getText().toString();
                password=et2.getText().toString();

                final Query userQuery = dataUsers.orderByChild("username").equalTo(username);

                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            keys=datas.getKey();
                            String dpass=datas.child("password").getValue().toString();
                            name=datas.child("name").getValue().toString();
                            if(dpass.equals(password)) {
                                Intent myIntent = new Intent(CustomerLoginActivity.this, AfterLoginCustomerActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", keys);
                                bundle.putString("username", username);
                                bundle.putString("password", password);
                                bundle.putString("name", name);
                                myIntent.putExtras(bundle);
                                CustomerLoginActivity.this.startActivity(myIntent);
                                finish();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            }
        });
    }
    public void Toast(String msg){
        Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
    }

}
