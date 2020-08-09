package com.example.dataapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Display extends AppCompatActivity {
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    String userid;
    FirebaseUser user;
    TextView t1,t2,t3,t4,t5,t6,t7,t8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        t1=findViewById(R.id.nname);
        t2=findViewById(R.id.nemail);
        t3=findViewById(R.id.nmobile);
        t4=findViewById(R.id.ndob);
        t5=findViewById(R.id.date);
        t6=findViewById(R.id.mdate);
        t7=findViewById(R.id.mtime);
        t8=findViewById(R.id.ntime);
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        user = fauth.getCurrentUser();
        userid = user.getUid();


        DocumentReference documentReference = fstore.collection("users").document(userid);
        documentReference.addSnapshotListener(Display.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                t1.setText(documentSnapshot.getString("Name"));
                t2.setText(documentSnapshot.getString("Email"));
                t3.setText(documentSnapshot.getString("Mobile"));
                t4.setText(documentSnapshot.getString("DOB"));
                t5.setText(documentSnapshot.getString("DateCreated"));
                t6.setText(documentSnapshot.getString("DateModified"));
                t7.setText(documentSnapshot.getString("TimeModified"));
                t8.setText(documentSnapshot.getString("TimeCreated"));

            }
        });


    }

    public void edit(View view) {
       Intent i=new Intent(view.getContext(),EditProfile.class);
       i.putExtra("name",t1.getText().toString());
       i.putExtra("email",t2.getText().toString());
       i.putExtra("mobile",t3.getText().toString());
       i.putExtra("dob",t4.getText().toString());
       startActivity(i);




    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        killActivity();
       // Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
    }

    public void reset(View view) {
        final EditText resetpassword = new EditText(view.getContext());
        final AlertDialog.Builder passworddialog = new AlertDialog.Builder(view.getContext());
        passworddialog.setTitle("Reset Password");
        passworddialog.setMessage("Enter your New Password :-");
        passworddialog.setView(resetpassword);

        passworddialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get email and send link
                String newpassword = resetpassword.getText().toString();
                user.updatePassword(newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Display.this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Display.this, "Error in Resetting password", Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });
        passworddialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passworddialog.show();
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