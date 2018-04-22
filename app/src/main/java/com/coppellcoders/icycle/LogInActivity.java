package com.coppellcoders.icycle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

/**
 * Created by rkark on 4/21/2018.
 */

public class LogInActivity extends Activity implements View.OnClickListener{

private ProgressDialog dialog;
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;
    private ImageButton submit;
    private ImageButton goToSignup;
    private ImageButton forgotPass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.lemail);
        password = findViewById(R.id.lpassword);
        submit = findViewById(R.id.lsubmit);
        goToSignup = findViewById(R.id.goToSignup);
        forgotPass = findViewById(R.id.forgotpass);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(i);
            }
        });
        submit.setOnClickListener(this);
        goToSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.lsubmit:
                signIn();
                break;
            case R.id.goToSignup:
                Intent i = new Intent(this, SignUpActivity.class);
                startActivity(i);
        }
    }
    public void signIn(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging in...");
        dialog.show();
        String emaill = email.getText().toString();
        String passwordd = password.getText().toString();
        if(!emaill.isEmpty()&&!passwordd.isEmpty()) {
            mAuth.signInWithEmailAndPassword(emaill, passwordd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Log In", "signInWithEmail:success");
                                Intent i = new Intent(getApplicationContext(), Main.class);
                                startActivity(i);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                dialog.dismiss();
                                Log.w("Log In", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Log In Failed", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }else{
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error: blank fields", Toast.LENGTH_SHORT).show();
        }
    }
}
