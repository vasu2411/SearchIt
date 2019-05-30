package com.example.vasus.searchthree;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Lost extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner whatLost;
    String documents[]={"Driving License","Passport","Pan card","Aadhar card","Election card"};

    EditText docNumber,issueDate,dateOfBirth;
    String docNum,issue,dob,document;

    Calendar myCalendar = Calendar.getInstance();

    TextView error;

    int t=0,k=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        //drop down menu setting
        whatLost = (Spinner)findViewById(R.id.whatlost);
        whatLost.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,documents);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        whatLost.setAdapter(aa);

        //wiaring
        docNumber = (EditText)findViewById(R.id.uniqueNumber);
        issueDate = (EditText)findViewById(R.id.issueDate);
        dateOfBirth = (EditText)findViewById(R.id.birthDate);
        error = (TextView)findViewById(R.id.txt_error);

        //date selection for issue date of document
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
                new DatePickerDialog(Lost.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //date selection for dob
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
                new DatePickerDialog(Lost.this, dateOne, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        //update label according to selection
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        issueDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateLabelBirth() {
        //update label according to selection
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

    int i=1,goAhead=0,goAheadOne=0;
    String[] values = new String[10];
    String[] contactDetails = new String[3];

    public void search(View view) {
        error.setText("");

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
//                            Toast.makeText(Lost.this,value,Toast.LENGTH_LONG).show();

                                if(i==1 && dob.equals(value))
                                {
                                    goAhead=1;
                                }
                                if(goAhead==0)
                                {
                                    Toast.makeText(Lost.this,"Same document with different date of birth found!!",Toast.LENGTH_LONG).show();
                                }
                                if(i==2 && issue.equals(value) && goAhead==1)
                                {
                                    goAheadOne=1;
                                }
                                if(i==2 && goAhead==1 && goAheadOne==0)
                                {
                                    Toast.makeText(Lost.this,"Same document with different issue date found!!",Toast.LENGTH_LONG).show();
                                }
                                if(i==3 || i==4 || i==5)
                                {
                                    if(goAhead==1 && goAheadOne==1)
                                    {
                                        contactDetails[k++]=value;
                                    }
                                }
                                i++;
                                if(i==6 && goAhead==1 && goAheadOne==1)
                                {
                                    showContactDetails();
                                }
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

    private void showContactDetails() {
           // t=1;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Name: "+contactDetails[2]+"\nEmail-id: "+contactDetails[0]+"\nMobile no: "+contactDetails[1]);
            alertDialogBuilder.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Toast.makeText(Lost.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
    }
}
