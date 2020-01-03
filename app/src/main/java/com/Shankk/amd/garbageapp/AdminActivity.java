package com.Shankk.amd.garbageapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminActivity extends AppCompatActivity {

    String fmail;
    Button fetch1;
    TextView messT, locnT;
    EditText fetchme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        fetch1 = (Button)findViewById(R.id.buttonfe);
        messT = (TextView)findViewById(R.id.textViewme);
        locnT = (TextView)findViewById(R.id.textViewlo);
        fetchme = (EditText)findViewById(R.id.fetchme);
        fmail = fetchme.getText().toString();
        fetch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchpostcode();
            }
        });
    }

    void fetchpostcode() {
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);//Dont put setPersistenceEnabled(true) during fetching..
        FirebaseDatabase firedb = FirebaseDatabase.getInstance();
        DatabaseReference rootref = firedb.getReference();//reference to database root node

        DatabaseReference rootby1 = rootref.child(fmail);//reference to database sub1 node

        rootby1.child("message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                messT.setText(value);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
