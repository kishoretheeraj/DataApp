package com.example.dataapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
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

public class EditProfile extends AppCompatActivity {
    EditText ed1,ed2,ed3,ed4;
    Button save,delete;
    FirebaseAuth fauth;
    DatePickerDialog datePickerDialog;
    FirebaseFirestore fstore;
    FirebaseUser user;
    String thisDate;
    String localTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ed1=findViewById(R.id.fname);
        ed2=findViewById(R.id.femail);
        ed3=findViewById(R.id.fmobile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ed4=findViewById(R.id.fdob);
        save=findViewById(R.id.save);
        delete=findViewById(R.id.delete);
        fauth=FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        user=fauth.getCurrentUser();
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
       // you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        localTime = date.format(currentLocalTime);

        Intent data=getIntent();
        final String name=data.getStringExtra("name");
        String email=data.getStringExtra("email");
        String mobile=data.getStringExtra("mobile");
        String dob=data.getStringExtra("dob");

        ed1.setText(name);
        ed2.setText(email);
        ed3.setText(mobile);
        ed4.setText(dob);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ed1.getText().toString().isEmpty()||ed2.getText().toString().isEmpty()||ed3.getText().toString().isEmpty()||ed4.getText().toString().isEmpty())
                {
                    Toast.makeText(EditProfile.this, "Fields Should Not Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email=ed2.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docref=fstore.collection("users").document(user.getUid());
                        Map<String,Object>edited=new HashMap<>();
                        edited.put("Name",ed1.getText().toString());
                        edited.put("Email",email);
                        edited.put("Mobile",ed3.getText().toString());
                        edited.put("DOB",ed4.getText().toString());
                        edited.put("DateModified",thisDate);
                        edited.put("TimeModified",localTime);
                        docref.update(edited);
                        startActivity(new Intent(getApplicationContext(), Display.class));
                        killActivity();
                       Toast.makeText(EditProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle("Delete Account?");
                dialog.setMessage("Your Account will be deleted Permanently:-");

                  dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fstore.collection("users").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditProfile.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                killActivity();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfile.this, "Error in Deleting Account", Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                });
              dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
           dialog.show();
            }
        });

    }

    public void date(View view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(EditProfile.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        ed4.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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