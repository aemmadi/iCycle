package com.coppellcoders.icycle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editText = findViewById(R.id.emailForgot);
        imageButton = findViewById(R.id.forgot_password_submit);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText.getText().toString();
                if(!email.isEmpty()){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),"Email sent.",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                                        startActivity(i);
                                        finish();

                                      //  Log.d(TAG, "Email sent.");
                                    }else{

                                        Toast.makeText(getApplicationContext(),"Error cannot find email.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }else{

                    Toast.makeText(getApplicationContext(),"Error blank field",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}