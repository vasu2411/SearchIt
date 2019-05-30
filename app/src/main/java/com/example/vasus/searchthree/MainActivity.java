package com.example.vasus.searchthree;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    Button login,signup,forgot;
    TextView error;
    EditText email,password;
    String emailId,pass;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //wiaring
        login = (Button)findViewById(R.id.btn_login);
        signup = (Button)findViewById(R.id.btn_register);
        forgot = (Button)findViewById(R.id.btn_forgot);

        //wiaring
        error = (TextView)findViewById(R.id.txt_wrongIdPass);

        //wiaring
        email = (EditText)findViewById(R.id.txt_id);
        password = (EditText)findViewById(R.id.txt_password);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        error.setText("");
    }

    public void login(View view) {
        int temp=0;
        error.setText("");

        //getting text of textview
        emailId = email.getText().toString();
        pass = password.getText().toString();

        //email id or password must be not empty
        if(emailId.isEmpty() || pass.isEmpty())
        {
            error.setText("Id or password must be not NULL");
            temp=1;
        }

        //character array of email id
        char[] em = emailId.toCharArray();

        //if id or password is not empty
        if(temp!=1)
        {
            //first character must be not @
            if(em[0]=='@')
            {
                error.setText("Please enter valid Email-id");
                temp=2;
            }
            int countat=0,countdot=0;

            //if first character is not @
            if(temp!=2)
            {
                //count @ and .
                for(int i=0;i<em.length;i++)
                {
                    if(em[i]=='@')
                    {
                        countat++;
                    }
                    else if(em[i]=='.')
                    {
                        countdot++;
                    }
                }
            }

            //@!=1 and .<1
            if(countat!=1 || countdot<1)
            {
                error.setText("Please enter valid Email-id");
                temp=3;
            }
            if(temp!=3)
            {
                //password length must be greater than 6
                if(pass.length()<6)
                {
                    error.setText("Length of password atleast be 6");
                    temp=4;
                }
            }
        }

        //if all constraint satisfied, then go for login
        if(temp==0)
        {
            mAuth.signInWithEmailAndPassword(emailId, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                error.setText("Wrong id or password");
                                //error.setAlpha(1.0f);
                            } else if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Welcome back!!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                            }

                            // ...
                        }
                    });
        }

    }

    public void Register(View view) {
        email.setText("");
        password.setText("");
        error.setText("");

        //redirect to register activity
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public void forgot(View view) {
        email.setText("");
        password.setText("");
        error.setText("");

        //redirect to forgot password activity
        Intent intent = new Intent(this, Forgot.class);
        startActivity(intent);
    }
}
