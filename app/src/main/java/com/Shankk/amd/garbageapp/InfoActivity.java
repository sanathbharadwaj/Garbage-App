package com.Shankk.amd.garbageapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoActivity extends AppCompatActivity {

    EditText msg, name;
    Button b1;

    DatabaseReference rootRef,demoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        msg = (EditText)findViewById(R.id.editText2);
        name = (EditText)findViewById(R.id.textView3);

        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();

        b1 = (Button)findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String demm = name.getText().toString();
                //database reference pointing to demo node
                demoRef = rootRef.child(demm);
                String value = msg.getText().toString();
                demoRef.push().setValue(value);
                Toast.makeText(getApplicationContext(),"Message sent by name of: "+demm,Toast.LENGTH_SHORT).show();
                msg.setText("");
                name.setText("");
            }
        });
    }
}
