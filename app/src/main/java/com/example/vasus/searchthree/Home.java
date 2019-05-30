package com.example.vasus.searchthree;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button found,lost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //wiaring
        found = (Button)findViewById(R.id.btn_found);
        lost = (Button)findViewById(R.id.btn_lost);
    }

    public void lost(View view) {
        //redirect to lost activity
        Intent intent = new Intent(this, Lost.class);
        startActivity(intent);
    }

    public void found(View view) {
        //redirect to found activity
        Intent intent = new Intent(this, Found.class);
        startActivity(intent);
    }

    public void logout(View view) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Home.this,"Successfully logged out",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Home.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
