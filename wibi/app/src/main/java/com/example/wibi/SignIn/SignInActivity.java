package com.example.wibi.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wibi.ChatActivity;
import com.example.wibi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import static android.text.TextUtils.isEmpty;

public class SignInActivity extends AppCompatActivity {

    EditText edtEmail, edtPass;

    Button btnSignIn;

    Toolbar toolbar;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtEmail = findViewById(R.id.signin_email);
        edtPass = findViewById(R.id.signin_password);
        btnSignIn = findViewById(R.id.btnSignIn);
        auth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignIn();
            }
        });
    }

    private void doSignIn() {
        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();

        if(isEmpty(email)) {
            edtEmail.setError("Enter your Email");
            return;
        }
        else if(isEmpty(pass)){
            edtPass.setError("Enter your Password");
            return;
        }
        else {
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent(SignInActivity.this, ChatActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK|
                                overridePendingTransition(0,0);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignInActivity.this, "Authentication failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}