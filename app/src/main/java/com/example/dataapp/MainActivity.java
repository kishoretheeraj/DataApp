package com.example.dataapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    EditText ed1, ed2, ed3, ed4,ed5;
    Button bt;
    DatePickerDialog datePickerDialog;
    ProgressBar progressBar;
    FirebaseAuth fauth;
    FirebaseFirestore mFireStore;
    String userid;
    String thisDate,localTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed1 = findViewById(R.id.name);
        ed2 = findViewById(R.id.email);
        ed3 = findViewById(R.id.mobile);
        ed4 = findViewById(R.id.password);
        ed5 = findViewById(R.id.dob);
        bt = findViewById(R.id.createAccount);
        progressBar = findViewById(R.id.progressBar);
        fauth = FirebaseAuth.getInstance();
        mFireStore=FirebaseFirestore.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
        // you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        localTime = date.format(currentLocalTime);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = ed1.getText().toString().trim();
                final String email = ed2.getText().toString().trim();
                final String mobile = ed3.getText().toString().trim();
                final String password = ed4.getText().toString().trim();
                final String dob = ed5.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    ed1.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    ed2.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {
                    ed3.setError("Mobile Number is required");
                    return;
                }
                if (mobile.length() < 6) {
                    ed3.setError("Mobile Number Should be less than 10 Digits");
                    return;
                }
                if (password.length() < 6) {
                    ed4.setError("password should be greater than six letters");
                    return;
                }
                if (TextUtils.isEmpty(dob)) {
                    ed5.setError("Date of Birth is Required");
                    return;
                }



                fauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userid=fauth.getCurrentUser().getUid();
                            String name = ed1.getText().toString().trim();
                            String email = ed2.getText().toString().trim();
                            String mobile = ed3.getText().toString().trim();
                            String password = ed4.getText().toString().trim();
                            String dob = ed5.getText().toString().trim();

                            Map<String,String> usermap=new HashMap<>();
                            usermap.put("Id",userid);
                            usermap.put("Name",name);
                            usermap.put("Email",email);
                            usermap.put("Mobile",mobile);
                            usermap.put("Password",password);
                            usermap.put("DOB",dob);
                            usermap.put("DateCreated",thisDate);
                            usermap.put("TimeCreated",localTime);


                            DocumentReference documentReference=mFireStore.collection("users").document(userid);

                            documentReference.set(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(MainActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Display.class));
                            killActivity();
                            progressBar.setVisibility(View.VISIBLE);


                            //send verification link
                            FirebaseUser user=fauth.getCurrentUser();
                            user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this,"Verification Email has been sent successfully", Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {
                            Toast.makeText(MainActivity.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void loginpage(View view) {
        startActivity(new Intent(getApplicationContext(),Login.class));
        killActivity();
    }

    public void view(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        ed5.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void killActivity() {
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}