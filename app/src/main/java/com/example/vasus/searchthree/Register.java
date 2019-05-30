package com.example.vasus.searchthree;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Locale;

public class Register extends AppCompatActivity {

    EditText email,password,confirmPassword,firstName,middleName,lastName,dateOfBirth,mobile,address;
    String emailId,pass,confPass,fName,mName,lName,dob,mob,add;
    Button register,cancel;
    TextView errorMessage;
    int temp = 0;

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Calendar myCalendar = Calendar.getInstance();

    private int PICK_IMAGE_REQUEST = 1;
    StorageReference storageReference;
    Intent intent;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //wiaring
        email = (EditText)findViewById(R.id.txt_emailId);
        password = (EditText)findViewById(R.id.txt_password);
        confirmPassword = (EditText)findViewById(R.id.txt_confpassword);
        firstName = (EditText)findViewById(R.id.txt_fname);
        middleName = (EditText)findViewById(R.id.txt_mname);
        lastName = (EditText)findViewById(R.id.txt_lname);
        dateOfBirth = (EditText)findViewById(R.id.txt_dob);
        address = (EditText)findViewById(R.id.txt_address);
        mobile = (EditText)findViewById(R.id.mobno);

        //wiaring
        register = (Button)findViewById(R.id.btn_reg);
        cancel = (Button)findViewById(R.id.btn_cancel);

        //wiaring
        errorMessage = (TextView)findViewById(R.id.txt_error);

        //datepicker related
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Register.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

        //to store imagge of user
        storageReference = FirebaseStorage.getInstance().getReference();

        errorMessage.setText("");
    }

    private void updateLabel() {
        //update the date accordingly
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }

    public void registeration(View view) {
        errorMessage.setText("");
        emailId = email.getText().toString();
        pass = password.getText().toString();
        confPass = confirmPassword.getText().toString();
        fName = firstName.getText().toString();
        mName = middleName.getText().toString();
        lName = lastName.getText().toString();
        dob = dateOfBirth.getText().toString();
        mob = mobile.getText().toString();
        add = address.getText().toString();

        //verify each field
        temp = verifyEmailId(emailId);
        if (temp == 1) {
            errorMessage.setText("Please enter valid Email-id");
        } else {
            temp = verfyPassAndconPass(pass, confPass);
            if (temp == 1) {
                errorMessage.setText("Password and Confirm Password must be same and length of both must be more than 5");
            } else {
                temp = verifyFirstName(fName);
                if (temp == 1) {
                    errorMessage.setText("First name is mandatory");
                } else {
                    temp = verifyMiddleName(mName);
                    if (temp == 1) {
                        errorMessage.setText("Middle name is mandatory");
                    } else {
                        temp = verifyLastName(lName);
                        if (temp == 1) {
                            errorMessage.setText("Last name is mandatory");
                        } else {
                            temp = verfiyDOB(dob);
                            if (temp == 1) {
                                errorMessage.setText("Date of birth is mandatory");
                            } else {
                                temp = verifyMobile(mob);
                                if (temp == 1) {
                                    errorMessage.setText("Mobile is mandatory");
                                } else {
                                    temp = verifyAddress(add);
                                    if (temp == 1) {
                                        errorMessage.setText("Address is mandatory");
                                    } else {
                                        //call register process
                                        registerProcess();
                                        Toast.makeText(this,"Registering...",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    }
                }
            }
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

    private int verfyPassAndconPass(String pass, String confPass) {
        //password and confirm password equal or not
        if(pass.equals(confPass) && pass.length()>=6 && confPass.length()>=6){
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private int verifyFirstName(String fName) {
        //first name must be not empty
        if(!fName.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private int verifyMiddleName(String mName) {
        //middle name must be not empty
        if(!mName.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private int verifyLastName(String lName) {
        //last name must be not empty
        if(!lName.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private int verfiyDOB(String dob) {
        //dob must be not empty
        if(!dob.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private int verifyMobile(String mob) {
        //mob must be not empty
        if(!mob.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    private int verifyAddress(String add) {
        //address must be not empty
        if(!add.isEmpty())
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    int registeredUser=0;
    private void registerProcess() {
        //register user to our app by firebase
        mAuth.createUserWithEmailAndPassword(emailId, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registeredUser=5;
                            //Toast.makeText(Register.this, "Success", Toast.LENGTH_SHORT).show();

                            //call insertion to insert data of user
                            insertion();
                        }
                        else if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(Register.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(Register.this, "failed. Please try again later...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void insertion() {
        //insert user data to firebase
        if(registeredUser==5)
        {
            //Toast.makeText(Register.this, "5", Toast.LENGTH_SHORT).show();
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
            //Toast.makeText(this, "" + currentFirebaseUser.getUid(), Toast.LENGTH_SHORT).show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(currentFirebaseUser.getUid()).child("emailld");
            myRef.setValue(emailId);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("password");
            myRef.setValue(pass);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("firstName");
            myRef.setValue(fName);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("middleName");
            myRef.setValue(mName);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("lastName");
            myRef.setValue(lName);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("dateOfBirth");
            myRef.setValue(dob);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("mobileNumber");
            myRef.setValue(mob);
            myRef = database.getReference(currentFirebaseUser.getUid()).child("address");
            myRef.setValue(add);
            //Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();

            //call upload id proof to upload the id proof of user
            uploadIdProof();
        }
    }

    public void selectPicFromGallery(View view) {
        //select image from gallery
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //take filepath
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }

    private void uploadIdProof() {

        if (filePath != null) {
            //getting the storage reference
            StorageReference sRef = storageReference.child(emailId+"/" + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying success toast
                            Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        else
        {
            //image must be selected
            errorMessage.setText("Image of id-proof is mandatory");
        }

    }

    public String getFileExtension(Uri uri) {
        //get extension of file
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    public void cancel(View view) {
        //redirect to login activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
