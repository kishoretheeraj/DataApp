package com.example.dataapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText ed1,ed2;
    Button bt1;
    ProgressBar progressBar;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ed1=findViewById(R.id.memail);
        ed2=findViewById(R.id.mpassword);
        bt1=findViewById(R.id.loginbtn);
        fauth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar1);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String email = ed1.getText().toString();
                String password = ed2.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    ed1.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    ed2.setError("Password is required");
                    return;
                }

                //delete notes first

                progressBar.setVisibility(View.VISIBLE);


                fauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(Login.this, Display.class));
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Login Successful", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            killActivity();
                            progressBar.setVisibility(View.VISIBLE);


                        } else {
                            Toast.makeText(Login.this, "Error!!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }


                    }
                });


            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        killActivity();

    }

    public void forgot(View view) {
        final EditText resetmail1=new EditText(view.getContext());
        final AlertDialog.Builder passworddialog=new AlertDialog.Builder(view.getContext());
        passworddialog.setTitle("Reset Password");
        passworddialog.setMessage("Enter your Email id to receive Reset link :-");
        passworddialog.setView(resetmail1);

        passworddialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get email and send link
                String mail=resetmail1.getText().toString();
                fauth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Reset link as been sent", Snackbar.LENGTH_LONG);
                        snackbar.show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "Error!!"+e.getMessage(), Toast.LENGTH_SHORT).show();


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
