package com.example.vasus.searchthree;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class Found extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{

    Spinner whatFound;
    String documents[]={"Driving License","Passport","Pan card","Aadhar card","Election card"};

    EditText docNumber,issueDate,dateOfBirth;
    String docNum,issue,dob,document;
    TextView error;

    int temp = 0;

    Calendar myCalendar = Calendar.getInstance();

    private ArrayList<String> details = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        whatFound = (Spinner)findViewById(R.id.whatfound);

        whatFound.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,documents);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        whatFound.setAdapter(aa);

        docNumber = (EditText)findViewById(R.id.uniqueNumber);
        issueDate = (EditText)findViewById(R.id.issueDate);
        dateOfBirth = (EditText)findViewById(R.id.birthDate);
        error = (TextView)findViewById(R.id.txt_error);

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

        issueDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Found.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateOne = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelBirth();
            }

        };

        dateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Found.this, dateOne, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        issueDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelBirth() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        if(position==0)
        {
            document = "Driving License";
            //Toast.makeText(getApplicationContext(),"Driving License" , Toast.LENGTH_LONG).show();
        }
        else if(position==1)
        {
            document = "Passport";
            //Toast.makeText(getApplicationContext(),"Passport" , Toast.LENGTH_LONG).show();
        }
        else if(position==2)
        {
            document = "Pan card";
            //Toast.makeText(getApplicationContext(),"Pan card" , Toast.LENGTH_LONG).show();
        }
        else if(position==3)
        {
            document = "Aadhar card";
            //Toast.makeText(getApplicationContext(),"Aadhar card" , Toast.LENGTH_LONG).show();
        }
        else if(position==4)
        {
            document = "Election card";
            //Toast.makeText(getApplicationContext(),"Election card" , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    int empty=1;
    int i=1;
    String x="";
    public void post(View view) {
        docNum = docNumber.getText().toString();
        issue = issueDate.getText().toString();
        dob = dateOfBirth.getText().toString();

        if(!docNum.isEmpty())
        {
            if(!issue.isEmpty())
            {
                if(!dob.isEmpty())
                {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    final DatabaseReference[] myRef = {database.getReference(docNum)};
                    myRef[0].addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String value = dataSnapshot.getValue(String.class);
                            if(!value.isEmpty())
                            {
                                empty=0;
                            }
                            details.add(value);
                            //Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    if(empty==1)
                    {
                        myRef[0] = database.getReference(docNum).child("dateOfIssue");
                        myRef[0].setValue(issue);
                        myRef[0] = database.getReference(docNum).child("dateOfBirth");
                        myRef[0].setValue(dob);
                        //insertUserData();

                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        myRef[0] = database.getReference(currentFirebaseUser.getUid());
                        myRef[0].addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                String value = dataSnapshot.getValue(String.class);

                                if(i==3) {
                                    //Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
                                    myRef[0] = database.getReference(docNum).child("emailId");
                                    myRef[0].setValue(value);
                                }
                                else if(i==4)
                                {
                                    x=x+value+" ";
                                }
                                else if(i==5)
                                {
                                    x=x+value;
                                    myRef[0] = database.getReference(docNum).child("name");
                                    myRef[0].setValue(x);
                                }
                                else if(i==7)
                                {
                                    myRef[0] = database.getReference(docNum).child("mobilNumber");
                                    myRef[0].setValue(value);
                                }
                                i++;
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //empty=0;
                        Toast.makeText(Found.this, "Posted Successfully. Stay Active!!", Toast.LENGTH_LONG).show();
                    }

                    else if(empty==0)
                    {
                        Toast.makeText(Found.this,"Already posted",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    error.setText("Date of birth must be non-empty");
                }
            }
            else
            {
                error.setText("Issue date must be non-empty");
            }
        }
        else
        {
            error.setText("Document number must be non-empty");
        }
    }
}