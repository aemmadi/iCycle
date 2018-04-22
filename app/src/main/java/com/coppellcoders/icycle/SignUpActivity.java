package com.coppellcoders.icycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends Activity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText cpassword;
    private ImageButton submit;
private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.sname);
        email = findViewById(R.id.semail);
        password = findViewById(R.id.spassword);
        cpassword = findViewById(R.id.scpassword);
        submit = findViewById(R.id.ssubmit);
        submit.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ssubmit:
                if(checkPassword()){
                    createUser();
                }else{
                    Toast.makeText(getApplicationContext(), "Password Don't Match", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    public boolean checkPassword(){
        return (password.getText().toString()).equals(cpassword.getText().toString());
    }
    public void createUser(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Signing up...");
        dialog.show();
        final String emaill =email.getText().toString();
        final String namee = name.getText().toString();
        if(!emaill.isEmpty()&&!namee.isEmpty()) {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                                Log.d("Sign Up", "Created User Successfully");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Users");
                                DatabaseReference childRef = myRef.child(mAuth.getCurrentUser().getUid());
                                childRef.child("Email").setValue(emaill);
                                childRef.child("Name").setValue(namee);
                                childRef.child("Points").setValue(0);
                                goToHome();



                        }else{
                            Log.w("Sign Up", task.getException());
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                }else{
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error: blank fields", Toast.LENGTH_LONG).show();
        }
    }
    public void goToHome(){
        Intent i = new Intent(this, Main.class);
        startActivity(i);
        finish();
    }

}
