package com.example.vasus.searchthree;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Forgot extends AppCompatActivity {

    private static final String TAG ="" ;
    EditText id;
    Button frgt;
    TextView error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        //wiaring
        id=(EditText)findViewById(R.id.txt_id);
        frgt=(Button)findViewById(R.id.btn_forgot);
        error=(TextView)findViewById(R.id.txt_error);
    }

    public void forgot(View view) {
        error.setText("");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = id.getText().toString();

        int temp = verifyEmailId(emailAddress);
        if(temp==1)
        {
            error.setText("Please enter valid Email-id");
        }
        else
        {
            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Forgot.this, "Check out your Mail-box", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                error.setText("Sorry, We can't send you a mail. Because, either you are not registered or it may " +
                                        "be system problem.");
                            }
                        }
                    });
        }
    }

    private int verifyEmailId(String emailId) {
        int at=0,dot=0;
        char[] splitted = emailId.toCharArray();
        for(int i=0;i<splitted.length;i++)
        {
            if(splitted[i]=='@')
            {
                at++;
            }
            else if(splitted[i]=='.')
            {
                dot++;
            }
        }

        //number of @ and .
        if(at==1 && dot>0)
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }
}
