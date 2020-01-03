package com.Shankk.amd.garbageapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements LocationListener, NavigationView.OnNavigationItemSelectedListener {

    LocationManager locationManager;

    String locna;

    DatabaseReference rootRef,demoRef;
    EditText editText;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    private Button btnChoose, btnUpload;
    private ImageView imageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseAuth auth;

    TextView namevw, emailvw;
    String temail;
    int index = 0;

    TextView uuname,  lldata;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //---------------------------------loc
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        getLocation();

        //-------------------------------loc

        //editText = (EditText) findViewById(R.id.etValue);

        uuname = (TextView)findViewById(R.id.nametextView);
        lldata = (TextView)findViewById(R.id.locntextView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //Initialize Views
        //btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        //imageView = (ImageView) findViewById(R.id.imgView);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendmessage();
                //uploadImage();
                startActivity(new Intent(MainActivity.this, ForceActivity.class).putExtra("key", temail));
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.nameView);
        TextView nav_user1 = (TextView)hView.findViewById(R.id.emailView);
        // namevw = (TextView)findViewById(R.id.nameView);
        emailvw = (TextView)findViewById(R.id.emailView);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Toast.makeText(getApplicationContext(),"Logged in: "+user.getEmail(),Toast.LENGTH_SHORT).show();
        temail = user.getEmail();
        index = temail.indexOf('@');
        temail = temail.substring(0,index);
        nav_user.setText(temail);
        nav_user1.setText(user.getEmail());
        uuname.setText("Hi "+temail);

        //database reference pointing to root of database
        rootRef = FirebaseDatabase.getInstance().getReference();
        //database reference pointing to demo node
        demoRef = rootRef.child(temail);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_admin) {
            if ((user.getEmail()) != null && (user.getEmail()).equals("shashankbm28@gmail.com")||
            (user.getEmail()) != null && (user.getEmail()).equals("akshaybharadwajkalgundi@gmail.com")) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                Toast.makeText(getApplicationContext(),"Admin Portal",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Access Denied! You are not Admin",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.locn) {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        } else if (id == R.id.sn_message) {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
        } else if (id == R.id.qk_contact) {
            startActivity(new Intent(MainActivity.this, ContactActivity.class));
        } else if (id == R.id.u_settings) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        } else if (id == R.id.si_out) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(getApplicationContext(),"Signed Out",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+temail);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public void sendmessage() {
        String value1 = editText.getText().toString();
        //push creates a unique id in database
        //demoRef.push().setValue(value1);
        //demoRef.push().setValue(locna);

        DatabaseReference rootby1phone= demoRef.child("message");//reference to child sub2 node
        rootby1phone.setValue(value1);//set value to child node
        DatabaseReference rootby2phone= demoRef.child("location");//reference to child sub2 node
        rootby2phone.setValue(locna);//set value to child node
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
            }
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(Location location) {
        /*Toast.makeText(getApplicationContext(),"Current location:\n " +
                        "Latitude = " +String.valueOf(location.getLatitude())+"\n " +
                        "Longitude = " +String.valueOf(location.getLongitude())+"\n ",
                        Toast.LENGTH_SHORT).show();*/

        locna = "https://www.google.com/maps?q="+location.getLatitude()+","+location.getLongitude();

        //locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            /*addressText.setText(addresses.get(0).getAddressLine(0) +"\n"+
                    addresses.get(0).getLocality() +"\n"+
                    addresses.get(0).getAdminArea() +"\n"+
                    addresses.get(0).getCountryName() +"\n"+
                    addresses.get(0).getPostalCode() +"\n"+
                    addresses.get(0).getFeatureName());*/
            lldata.setText("Your current place is:\n"+addresses.get(0).getLocality()+",\t"+addresses.get(0).getAdminArea()+",\t"+addresses.get(0).getCountryName());

        }catch(Exception e)
        {

        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
